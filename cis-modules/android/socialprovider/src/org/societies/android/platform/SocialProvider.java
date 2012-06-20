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
package org.societies.android.platform;

import org.societies.android.platform.SocialContract;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

/**
 * This is the Android-based SocialProvider. It provides a content provider interface
 * to access CSS/CIS and related data. The design is documented in CSS/CIS Redmine wiki.
 * This provider will have a number of adapters where CSS/CIS data can be stored.
 * Currently it works with a local DB Adapter and an XMPP adapter is under
 * development which will use cloud data. The local DB adapter will gradually 
 * function as a local cache. The logic to operate remote data and local cache
 * will reside in this class.
 * 
 * @author Babak.Farshchian@sintef.no
 *
 */
public class SocialProvider extends ContentProvider implements ISocialAdapterListener {

    //will contain all the legal URIs:
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    private boolean online = false; // True when we are online.
    private LocalDBAdapter dbAdapter = null;
    /* 
     * Here I should do the following:
     * - Create a {@link CommunicationAdapter} and try to get connection with cloud CisManager (currently not here 
     *   due to problems with communication manager.
     * - Initiate local database using {@link SocialDatabaseAdapter}
     * 
     * (non-Javadoc)
     * @see android.content.ContentProvider#onCreate()
     */
    @Override
    public boolean onCreate() {
	Context context = getContext();
	//Used for local testing that ContentProvider works:
	//TODO: to be used in later versions with local caching.
	dbAdapter = new LocalDBAdapter(context);
	dbAdapter.connect();
	populateMe();
	populateMyCommunities();
	populateMyServices();
	populateMyPeople();
	
	//TODO: Add Edgar's CommunicationAdapter initialization here.
	
	//Construct all the legal query URIs:
	//TODO replace with constants or move to SocialContract.
	sUriMatcher.addURI(SocialContract.AUTHORITY.getAuthority(), "me", 0);
	sUriMatcher.addURI(SocialContract.AUTHORITY.getAuthority(), "people", 1);
	sUriMatcher.addURI(SocialContract.AUTHORITY.getAuthority(), "people/#", 2);
	sUriMatcher.addURI(SocialContract.AUTHORITY.getAuthority(), "communities", 3);
	sUriMatcher.addURI(SocialContract.AUTHORITY.getAuthority(), "communities/#", 4);
	sUriMatcher.addURI(SocialContract.AUTHORITY.getAuthority(), "services", 5);
	sUriMatcher.addURI(SocialContract.AUTHORITY.getAuthority(), "services/#", 6);

	return dbAdapter.isConnected();
    }

    private void populateMe(){
		ContentValues initialValues = new ContentValues();
		initialValues.put(SocialContract.Me.GLOBAL_ID , "babak@societies.org");
		initialValues.put(SocialContract.Me.NAME , "Babak Farshchian");
		initialValues.put(SocialContract.Me.DISPLAY_NAME , "Babak F");

		//2- Call insert in SocialProvider to initiate insertion
		dbAdapter.insertMe(initialValues);
    	
    }
    private void populateMyCommunities(){
		ContentValues initialValues = new ContentValues();
		initialValues.put(SocialContract.Community.GLOBAL_ID , "community1.societies.org");
		initialValues.put(SocialContract.Community.NAME , "Community 1");
		initialValues.put(SocialContract.Community.DISPLAY_NAME , "Football");
		initialValues.put(SocialContract.Community.MEMBERSHIP_TYPE, "Open");
		initialValues.put(SocialContract.Community.OWNER_ID, "babak@societies.org");
		dbAdapter.insertCommunity(initialValues);
		initialValues.clear();
		initialValues.put(SocialContract.Community.GLOBAL_ID , "community2.societies.org");
		initialValues.put(SocialContract.Community.NAME , "Community 2");
		initialValues.put(SocialContract.Community.DISPLAY_NAME , "Baseball");
		initialValues.put(SocialContract.Community.MEMBERSHIP_TYPE, "Closed");
		initialValues.put(SocialContract.Community.OWNER_ID, "babak@societies.org");
		dbAdapter.insertCommunity(initialValues);
		initialValues.clear();
		initialValues.put(SocialContract.Community.GLOBAL_ID , "community3.societies.org");
		initialValues.put(SocialContract.Community.NAME , "Community 3");
		initialValues.put(SocialContract.Community.DISPLAY_NAME , "Basketball");
		initialValues.put(SocialContract.Community.MEMBERSHIP_TYPE, "Closed");
		initialValues.put(SocialContract.Community.OWNER_ID, "babak@societies.org");
		dbAdapter.insertCommunity(initialValues);
		initialValues.clear();
		initialValues.put(SocialContract.Community.GLOBAL_ID , "community4.societies.org");
		initialValues.put(SocialContract.Community.NAME , "Community 4");
		initialValues.put(SocialContract.Community.DISPLAY_NAME , "Handball");
		initialValues.put(SocialContract.Community.MEMBERSHIP_TYPE, "Open");
		initialValues.put(SocialContract.Community.OWNER_ID, "babak@societies.org");
		dbAdapter.insertCommunity(initialValues);


		

		//2- Call insert in SocialProvider to initiate insertion
		dbAdapter.insertMe(initialValues);
    	
    }
    private void populateMyServices(){
		ContentValues initialValues = new ContentValues();
		initialValues.put(SocialContract.Service.GLOBAL_ID , "service1@babak@societies.org");
		initialValues.put(SocialContract.Service.NAME , "Service 1");
		initialValues.put(SocialContract.Service.DISPLAY_NAME , "Some service");
		dbAdapter.insertService(initialValues);
		initialValues.clear();
		initialValues.put(SocialContract.Service.GLOBAL_ID , "service2@babak@societies.org");
		initialValues.put(SocialContract.Service.NAME , "Service 2");
		initialValues.put(SocialContract.Service.DISPLAY_NAME , "Some other service");
		dbAdapter.insertService(initialValues);
		initialValues.clear();
		initialValues.put(SocialContract.Service.GLOBAL_ID , "service3@babak@societies.org");
		initialValues.put(SocialContract.Service.NAME , "Service 3");
		initialValues.put(SocialContract.Service.DISPLAY_NAME , "Another service");
		dbAdapter.insertService(initialValues);

		//2- Call insert in SocialProvider to initiate insertion
    	
    }
    private void populateMyPeople(){
		ContentValues initialValues = new ContentValues();
		initialValues.put(SocialContract.Person.GLOBAL_ID , "jacqueline@societies.org");
		initialValues.put(SocialContract.Person.NAME , "Jacqueline Floch");
		initialValues.put(SocialContract.Me.DISPLAY_NAME , "Jacqueline F");
		dbAdapter.insertPerson(initialValues);
		initialValues.clear();
		initialValues.put(SocialContract.Person.GLOBAL_ID , "thomas@societies.org");
		initialValues.put(SocialContract.Person.NAME , "ThomasVilarinho");
		initialValues.put(SocialContract.Me.DISPLAY_NAME , "Thomas V");
		dbAdapter.insertPerson(initialValues);
		initialValues.clear();
		initialValues.put(SocialContract.Person.GLOBAL_ID , "bjorn-magnus@societies.org");
		initialValues.put(SocialContract.Person.NAME , "Bjørn Magnus Mathisen");
		initialValues.put(SocialContract.Me.DISPLAY_NAME , " Bjørn Magnus M");
		dbAdapter.insertPerson(initialValues);
		initialValues.clear();


		//2- Call insert in SocialProvider to initiate insertion
		dbAdapter.insertMe(initialValues);
    	
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
    public Uri insert(Uri _uri, ContentValues _values) {

    	//Switch on the name of the path used in the query:
    	switch (sUriMatcher.match(_uri)){
    	case 0: //Me
    		return dbAdapter.insertMe(_values);
    	case 1:
    		break;
    		
    	}
    	return dbAdapter.insert(_uri, _values);
    }

    /* 
     * Return a cursor that contains the contents of a query
     * 
     * (non-Javadoc)
     * @see android.content.ContentProvider#query(android.net.Uri, java.lang.String[], java.lang.String, java.lang.String[], java.lang.String)
     */
    @Override
    public Cursor query(Uri _uri, String[] _projection, String _selection,
	    String[] _selectionArgs, String _sortOrder) {
    	//Switch on the name of the path used in the query:
    	switch (sUriMatcher.match(_uri)){
    	case 0: //Me
    		return dbAdapter.queryMe(_projection, _selection, _selectionArgs, _sortOrder);
    	case 1:
    		break;	
    	}
    	return dbAdapter.query(_uri, _projection, _selection, _selectionArgs, _sortOrder);
    }

    /* (non-Javadoc)
     * @see android.content.ContentProvider#update(android.net.Uri, android.content.ContentValues, java.lang.String, java.lang.String[])
     */
    @Override
    public int update(Uri _uri, ContentValues _values, String _selection,
	    String[] _selectionArgs) {
    	switch (sUriMatcher.match(_uri)){
    	case 0: //Me
    		return dbAdapter.updateMe(_values, _selection, _selectionArgs);
    	case 1:
    		break;	
    	}
    	return dbAdapter.update(_uri, _values, _selection, _selectionArgs);
    }
    /**
     * 
     * 
     * @author Babak.Farshchian@sintef.no
     *
     */
    //TODO: probably delete this:
    
    public boolean isOnline(){
	return online;
    };

}
