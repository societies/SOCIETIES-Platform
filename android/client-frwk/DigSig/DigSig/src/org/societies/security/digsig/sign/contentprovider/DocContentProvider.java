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

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;

/**
 * {@link ContentProvider} for reading and writing files from/to internal storage of this app.
 *
 * @author Mitja Vardjan
 *
 */
public class DocContentProvider extends ContentProvider {

	private static final String tag = DocContentProvider.class.getSimpleName();

	private static final String AUTHORITY = "org.societies.security.digsig.provider";

	public static String localPath2UriString(String path) {
		return "content://" + AUTHORITY + "/" + path;
	}

	public static Uri localPath2Uri(String path) {
		return Uri.parse(localPath2UriString(path));
	}

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#delete(android.net.Uri, java.lang.String, java.lang.String[])
	 */
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		
		Log.d(tag, "delete");

		File root = getContext().getFilesDir();

		String fileName = uri.getEncodedPath();
		if (fileName.startsWith("/")) {
			fileName = fileName.replaceFirst("/", "");
		}
		Log.d(tag, "File name = " + fileName);
		
		File file = new File(root, fileName);

		if (file.exists() && file.delete()) {
			return 1;
		}
		else {
			return 0;
		}
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
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#onCreate()
	 */
	@Override
	public boolean onCreate() {
		Log.d(tag, "onCreate");
		return false;
	}

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#query(android.net.Uri, java.lang.String[], java.lang.String, java.lang.String[], java.lang.String)
	 */
	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {

		Log.d(tag, "query");
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#update(android.net.Uri, android.content.ContentValues, java.lang.String, java.lang.String[])
	 */
	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {

		Log.d(tag, "update");
		throw new UnsupportedOperationException();
	}

	@Override
	public ParcelFileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException {

		Log.d(tag, "openFile(" + uri + ", " + mode + ")");

//		File root = Environment.getExternalStorageDirectory();
		File root = getContext().getFilesDir();

		String fileName = uri.getEncodedPath();
		if (fileName.startsWith("/")) {
			fileName = fileName.replaceFirst("/", "");
		}
		Log.d(tag, "File name = " + fileName);
		
//		String pass = uri.getQueryParameter(Sign.ContentUrl.PARAM_PASSWORD);
//		Log.d(tag, "Password = " + pass);
		
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
}
