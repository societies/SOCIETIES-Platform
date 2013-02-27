/**
Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 

(SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAÇÃO, SA (PTIN), IBM Corp (IBM),
INSTITUT TELECOM (ITSUD), AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA PERIORISMENIS EFTHINIS (AMITEC), TELECOM 
ITALIA S.p.a.(TI), TRIALOG (TRIALOG), Stiftelsen SINTEF (SINTEF), NEC EUROPE LTD (NEC))
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
conditions are met:

1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
   disclaimer in the documentation and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.societies.android.platform.content;

import org.societies.android.api.contentproviders.CSSContentProvider;
import org.societies.android.api.contentproviders.CSSContentProvider.CssNodes;
import org.societies.android.platform.androidutils.AppPreferences;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

/**
 * Implements the Content Provider API to allow other external and internal components to access 
 * basic CSS data (CSSRecord)
 *
 */
public class ProviderImplementation extends ContentProvider {
	private final static String LOG_TAG = ProviderImplementation.class.getName();
	
	private final static int CSS_NODES_MATCHER = 1;
	private final static int CSS_ARCHIVED_NODES_MATCHER = 2;
	private final static int CSS_RECORD_MATCHER = 3;
	private final static int CSS_PREFERENCES_MATCHER = 4;
	
	
    private UriMatcher uriMatcher;
	private DBHelper dbHelper;
	private final String preferenceColumnNames [] = {CSSContentProvider.CssPreferences.CSS_USER_PREFERENCE,
											CSSContentProvider.CssPreferences.CSS_XMPP_SERVER,
											CSSContentProvider.CssPreferences.CSS_DOMAIN_AUTHORITY,
											CSSContentProvider.CssPreferences.CSS_CURRENT_NODE_JID};

	
    public ProviderImplementation() {
    	super();
    	this.uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    	this.buildUriMatcher(this.uriMatcher);
    }
	
	@Override
	public int delete(Uri arg0, String arg1, String[] arg2) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getType(Uri arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri arg0, ContentValues arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean onCreate() {
		Log.d(LOG_TAG, "CSS Content Provider created");
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] columns, String selectionCriteria, String[] selectionArgs, String sortOrder) {
		if (null == this.dbHelper) {
			this.dbHelper = new DBHelper(this.getContext(), CssRecordDAO.SOCIETIES_DATABASE_NAME, null, CssRecordDAO.SOCIETIES_DATABASE_VERSION);
		}
		
		Log.d(LOG_TAG, "Content provider query");
		Log.d(LOG_TAG,"Selection criteria: " + selectionCriteria);
		if (null != columns) {
			for (String column: columns) {
				Log.d(LOG_TAG, "Query column: " + column);
			}
		}
		
		Cursor cursor = null;
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
	    // TODO: required API level 14
//	    queryBuilder.setStrict(true);
	    
	    switch(this.uriMatcher.match(uri)) {
		    case CSS_ARCHIVED_NODES_MATCHER: 
		    	Log.d(LOG_TAG, "Archived CSS Nodes query matched" );
		    	queryBuilder.setTables(DBHelper.ARCHIVED_NODE_TABLE);
		    	break;
		    case CSS_NODES_MATCHER: 
		    	Log.d(LOG_TAG, "Current CSS Nodes query matched" );
		    	queryBuilder.setTables(DBHelper.CURRENT_NODE_TABLE);
		    	break;
		    case CSS_PREFERENCES_MATCHER:
		    	Log.d(LOG_TAG, "CSS Preferences query matched" );
		    	break;
		    case CSS_RECORD_MATCHER:
		    	Log.d(LOG_TAG, "CSS record query matched" );
		    	queryBuilder.setTables(DBHelper.CSS_RECORD_TABLE);
		    	break;
	    	default:
		    	Log.d(LOG_TAG, "Unknown URI" );
	    		throw new IllegalArgumentException("Unknown URI: " + uri);
	    }
	    
	    if (CSS_PREFERENCES_MATCHER != this.uriMatcher.match(uri)) {
	    	Log.d(LOG_TAG, "Executing database query");
	    	cursor = queryBuilder.query(this.dbHelper.getReadableDatabase(), columns, selectionCriteria, selectionArgs, null, null, sortOrder);

	    } else {
	    	Log.d(LOG_TAG, "Getting app preferences for query");
	    	AppPreferences preferences = new AppPreferences(getContext());
	    	
	    	String columnValues [] = new String[this.preferenceColumnNames.length];
	    	for (int i = 0; i < this.preferenceColumnNames.length; i++) {
	    		columnValues[i] = preferences.getStringPrefValue(this.preferenceColumnNames[i]);
	    	}
	    	
	    	MatrixCursor mCursor  = new MatrixCursor(this.preferenceColumnNames);
	    	mCursor.addRow(columnValues);
	    	cursor = mCursor;
	    }
	    
	    // Make sure that potential listeners are notified
	    cursor.setNotificationUri(getContext().getContentResolver(), uri);

		return cursor;
	}

	@Override
	public int update(Uri arg0, ContentValues arg1, String arg2, String[] arg3) {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * Build the URI matcher with
	 */
	private void buildUriMatcher(UriMatcher matcher) {
		matcher.addURI(CSSContentProvider.AUTHORITY, CSSContentProvider.CssNodes.PATH, CSS_NODES_MATCHER);
		matcher.addURI(CSSContentProvider.AUTHORITY, CSSContentProvider.CssArchivedNodes.PATH, CSS_ARCHIVED_NODES_MATCHER);		
		matcher.addURI(CSSContentProvider.AUTHORITY, CSSContentProvider.CssRecord.PATH, CSS_RECORD_MATCHER);
		matcher.addURI(CSSContentProvider.AUTHORITY, CSSContentProvider.CssPreferences.PATH, CSS_PREFERENCES_MATCHER);
	}
}
