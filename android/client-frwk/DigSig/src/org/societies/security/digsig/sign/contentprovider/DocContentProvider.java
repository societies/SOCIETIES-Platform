/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAÇÃO, SA (PTIN), IBM Corp., 
 * INSTITUT TELECOM (ITSUD), AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA PERIORISMENIS EFTHINIS (AMITEC), TELECOM 
 * ITALIA S.p.a.(TI),  TRIALOG (TRIALOG), Stiftelsen SINTEF (SINTEF), NEC EUROPE LTD (NEC))
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
 * conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
 *    disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.societies.security.digsig.sign.contentprovider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;

import org.societies.security.digsig.api.Sign;
import org.societies.security.digsig.sign.db.DocDatabaseHelper;
import org.societies.security.digsig.sign.db.DocTable;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.text.TextUtils;
import android.util.Log;

/**
 * Describe your class here...
 *
 * @author mitjav
 *
 */
public class DocContentProvider extends ContentProvider {

	private static final String tag = DocContentProvider.class.getSimpleName();

	// database
	private DocDatabaseHelper database;

	// Used for the UriMacher
	private static final int DOCS = 10;
	private static final int DOC_ID = 20;

	private static final String AUTHORITY = "org.societies.security.digsig.provider";

	private static final String BASE_PATH = "docs";
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);

	public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/docs";
	public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/doc";

	private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	static {
		sURIMatcher.addURI(AUTHORITY, BASE_PATH, DOCS);
		sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/#", DOC_ID);
	}

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#delete(android.net.Uri, java.lang.String, java.lang.String[])
	 */
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		Log.d(tag, "delete");
		int uriType = sURIMatcher.match(uri);
		SQLiteDatabase sqlDB = database.getWritableDatabase();
		int rowsDeleted = 0;
		switch (uriType) {
		case DOCS:
			rowsDeleted = sqlDB.delete(DocTable.TABLE_NAME, selection,
					selectionArgs);
			break;
		case DOC_ID:
			String id = uri.getLastPathSegment();
			if (TextUtils.isEmpty(selection)) {
				rowsDeleted = sqlDB.delete(DocTable.TABLE_NAME,
						DocTable.COLUMN_ID + "=" + id, 
						null);
			} else {
				rowsDeleted = sqlDB.delete(DocTable.TABLE_NAME,
						DocTable.COLUMN_ID + "=" + id 
						+ " and " + selection,
						selectionArgs);
			}
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return rowsDeleted;
	}

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#getType(android.net.Uri)
	 */
	@Override
	public String getType(Uri uri) {
		Log.d(tag, "getType");
		return null;
	}

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#insert(android.net.Uri, android.content.ContentValues)
	 */
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		Log.d(tag, "insert");
		int uriType = sURIMatcher.match(uri);
		SQLiteDatabase sqlDB = database.getWritableDatabase();
		long id = 0;
		switch (uriType) {
		case DOCS:
			id = sqlDB.insert(DocTable.TABLE_NAME, null, values);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return Uri.parse(BASE_PATH + "/" + id);
	}

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#onCreate()
	 */
	@Override
	public boolean onCreate() {
		Log.d(tag, "onCreate");
		database = new DocDatabaseHelper(getContext());
		return false;
	}

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#query(android.net.Uri, java.lang.String[], java.lang.String, java.lang.String[], java.lang.String)
	 */
	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {

		Log.d(tag, "query");
		// Uisng SQLiteQueryBuilder instead of query() method
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

		// Check if the caller has requested a column which does not exists
		checkColumns(projection);

		// Set the table
		queryBuilder.setTables(DocTable.TABLE_NAME);

		int uriType = sURIMatcher.match(uri);
		switch (uriType) {
		case DOCS:
			break;
		case DOC_ID:
			// Adding the ID to the original query
			queryBuilder.appendWhere(DocTable.COLUMN_ID + "=" + uri.getLastPathSegment());
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}

		SQLiteDatabase db = database.getWritableDatabase();
		Cursor cursor = queryBuilder.query(db, projection, selection,
				selectionArgs, null, null, sortOrder);
		// Make sure that potential listeners are getting notified
		cursor.setNotificationUri(getContext().getContentResolver(), uri);

		return cursor;
	}

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#update(android.net.Uri, android.content.ContentValues, java.lang.String, java.lang.String[])
	 */
	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {

		Log.d(tag, "update");
		int uriType = sURIMatcher.match(uri);
		SQLiteDatabase sqlDB = database.getWritableDatabase();
		int rowsUpdated = 0;
		switch (uriType) {
		case DOCS:
			rowsUpdated = sqlDB.update(DocTable.TABLE_NAME, 
					values, 
					selection,
					selectionArgs);
			break;
		case DOC_ID:
			String id = uri.getLastPathSegment();
			if (TextUtils.isEmpty(selection)) {
				rowsUpdated = sqlDB.update(DocTable.TABLE_NAME, 
						values,
						DocTable.COLUMN_ID + "=" + id, 
						null);
			} else {
				rowsUpdated = sqlDB.update(DocTable.TABLE_NAME, 
						values,
						DocTable.COLUMN_ID + "=" + id 
						+ " and " 
						+ selection,
						selectionArgs);
			}
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return rowsUpdated;
	}

	@Override
	public ParcelFileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException {

		Log.d(tag, "openFile(" + uri + ", " + mode + ")");

		File root = new File(Environment.getExternalStorageDirectory(), 
				"/Android/data/org.societies.security.digsig/cache");

		String fileName = uri.getEncodedPath();
		if (fileName.startsWith("/")) {
			fileName = fileName.replaceFirst("/", "");
		}
		Log.d(tag, "File name = " + fileName);
		
		String pass = uri.getQueryParameter(Sign.ContentUrl.PARAM_PASSWORD);
//		Log.d(tag, "Password = " + pass);  // TODO: remove
		
		File path = new File(root, fileName);

		int imode = 0;
		if (mode.contains("w")) {
			imode |= ParcelFileDescriptor.MODE_WRITE_ONLY;
			root.mkdirs();
			if (!path.exists()) {
				try {
					path.createNewFile();
				} catch (IOException e) {
					Log.w(tag, e);
					throw new FileNotFoundException("Cannot create file");
				}
			}
		}
		if (mode.contains("r")) {
			imode |= ParcelFileDescriptor.MODE_READ_ONLY;
		}
		if (mode.contains("+")) {
			imode |= ParcelFileDescriptor.MODE_APPEND;        
		}

		return ParcelFileDescriptor.open(path, imode);
	}

	private void checkColumns(String[] projection) {
		String[] available = {
				DocTable.COLUMN_PATH_TO_UNSIGNED_DOC,
				DocTable.COLUMN_PATH_TO_SIGNED_DOC,
				DocTable.COLUMN_FINISHED,
				DocTable.COLUMN_ID};
		if (projection != null) {
			HashSet<String> requestedColumns = new HashSet<String>(Arrays.asList(projection));
			HashSet<String> availableColumns = new HashSet<String>(Arrays.asList(available));
			// Check if all columns which are requested are available
			if (!availableColumns.containsAll(requestedColumns)) {
				throw new IllegalArgumentException("Unknown columns in projection");
			}
		}
	}
}
