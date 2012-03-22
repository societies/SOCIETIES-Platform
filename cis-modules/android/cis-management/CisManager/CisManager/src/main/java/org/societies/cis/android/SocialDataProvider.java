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
package org.societies.cis.android;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

/**
 * This is the Android-based SocialDataProvider. It provides a content provider interface to access CIS and related data.
 * 
 * 
 * @author Babak.Farshchian@sintef.no
 *
 */
public class SocialDataProvider extends ContentProvider {

    public static final Uri CONTENT_URI = 
            Uri.parse("content://org.societies.cis.android.SocialDataProvider");
    
    private boolean online = false; // True when we are online.
    private DatabaseAdapter dbAdapter = null;
    private CommunicationAdapter comAdapter = null;
    /* 
     * Here I should do the following:
     * - Create a CommunicationAdapter and try to get connection with cloud CisManager
     * - Initiate local database if any
     * 
     * (non-Javadoc)
     * @see android.content.ContentProvider#onCreate()
     */
    @Override
    public boolean onCreate() {
	// TODO Auto-generated method stub
	Context context = getContext();
	dbAdapter = new DatabaseAdapter(context);
	comAdapter = new CommunicationAdapter(context);
	comAdapter.goOnline();
	if (comAdapter.isOnline()){
	    online = true;
	}
	return false;
    }

    /* (non-Javadoc)
     * @see android.content.ContentProvider#delete(android.net.Uri, java.lang.String, java.lang.String[])
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
	// TODO Auto-generated method stub
	return 0;
    }

    /* (non-Javadoc)
     * @see android.content.ContentProvider#getType(android.net.Uri)
     */
    @Override
    public String getType(Uri uri) {
	// TODO Auto-generated method stub
	return null;
    }

    /* 
     * (non-Javadoc)
     * @see android.content.ContentProvider#insert(android.net.Uri, android.content.ContentValues)
     */
    @Override
    public Uri insert(Uri uri, ContentValues values) {
	// TODO Auto-generated method stub
	return null;
    }

    /* (non-Javadoc)
     * @see android.content.ContentProvider#onCreate()
     */
    /* (non-Javadoc)
     * @see android.content.ContentProvider#query(android.net.Uri, java.lang.String[], java.lang.String, java.lang.String[], java.lang.String)
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
	    String[] selectionArgs, String sortOrder) {
	// TODO Auto-generated method stub
	SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

	return null;
    }

    /* (non-Javadoc)
     * @see android.content.ContentProvider#update(android.net.Uri, android.content.ContentValues, java.lang.String, java.lang.String[])
     */
    @Override
    public int update(Uri uri, ContentValues values, String selection,
	    String[] selectionArgs) {
	// TODO Auto-generated method stub
	return 0;
    }
    /**
     * Constants and helpers for the CIS metadata table
     * 
     * @author Babak.Farshchian@sintef.no
     *
     */
    public boolean isOnline(){
	return online;
    }
    
    public static final class Groups {
	String _ID = "_id"; //Key column in the table
	String NAME = "name"; //Name column in the group
	String JID = "jid"; //Unique JID of the group
	String OWNER = "owner"; //Owner CSS jid of the group
	String CREATION_DATE = "creation_date";	
    };
    

}
