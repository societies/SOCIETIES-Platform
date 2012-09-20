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

import org.societies.android.api.cis.SocialContract;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;

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
public class SocialProvider extends ContentProvider{
    
	//For logging:
    private static final String TAG = "SocialProvider";

    //will contain all the legal URIs:
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    private ISocialAdapter adapter = null;
	//Construct all the legal query URIs. The URIs that are added here are the
    //ones that are supported in calls to SocialProvider. For all others there
    //will be some exception being thrown.

    //The method addURI() maps an authority and path to an integer value.
    //The method match() returns the integer value for a URI.
    // Later on in methods called from a ContentResolver, a switch statement 
    // chooses between the different legal queries.
    static{
    	sUriMatcher.addURI(SocialContract.AUTHORITY.getAuthority(),
    			SocialContract.UriPathIndex.ME, SocialContract.UriMatcherIndex.ME);
    	sUriMatcher.addURI(SocialContract.AUTHORITY.getAuthority(), 
    			SocialContract.UriPathIndex.ME_SHARP, SocialContract.UriMatcherIndex.ME_SHARP);
    	sUriMatcher.addURI(SocialContract.AUTHORITY.getAuthority(), 
    			SocialContract.UriPathIndex.PEOPLE, SocialContract.UriMatcherIndex.PEOPLE);
    	sUriMatcher.addURI(SocialContract.AUTHORITY.getAuthority(),
    			SocialContract.UriPathIndex.PEOPLE_SHARP, SocialContract.UriMatcherIndex.PEOPLE_SHARP);
    	sUriMatcher.addURI(SocialContract.AUTHORITY.getAuthority(),
    			SocialContract.UriPathIndex.COMMINITIES, SocialContract.UriMatcherIndex.COMMUNITIES);
    	sUriMatcher.addURI(SocialContract.AUTHORITY.getAuthority(),
    			SocialContract.UriPathIndex.COMMINITIES_SHARP, SocialContract.UriMatcherIndex.COMMUNITIES_SHARP);
    	sUriMatcher.addURI(SocialContract.AUTHORITY.getAuthority(),
    			SocialContract.UriPathIndex.SERVICES, SocialContract.UriMatcherIndex.SERVICES);
    	sUriMatcher.addURI(SocialContract.AUTHORITY.getAuthority(),
    			SocialContract.UriPathIndex.SERVICES_SHARP, SocialContract.UriMatcherIndex.SERVICES_SHARP);
    	sUriMatcher.addURI(SocialContract.AUTHORITY.getAuthority(),
    			SocialContract.UriPathIndex.RELATIONSHIP, SocialContract.UriMatcherIndex.RELATIONSHIP);
    	sUriMatcher.addURI(SocialContract.AUTHORITY.getAuthority(),
    			SocialContract.UriPathIndex.RELATIONSHIP_SHARP, SocialContract.UriMatcherIndex.RELATIONSHIP_SHARP);
    	sUriMatcher.addURI(SocialContract.AUTHORITY.getAuthority(),
    			SocialContract.UriPathIndex.MEMBERSHIP, SocialContract.UriMatcherIndex.MEMBERSHIP);
    	sUriMatcher.addURI(SocialContract.AUTHORITY.getAuthority(),
    			SocialContract.UriPathIndex.MEMBERSHIP_SHARP, SocialContract.UriMatcherIndex.MEMBERSHIP_SHARP);
    	sUriMatcher.addURI(SocialContract.AUTHORITY.getAuthority(),
    			SocialContract.UriPathIndex.SHARING, SocialContract.UriMatcherIndex.SHARING);
    	sUriMatcher.addURI(SocialContract.AUTHORITY.getAuthority(),
    			SocialContract.UriPathIndex.SHARING_SHARP, SocialContract.UriMatcherIndex.SHARING_SHARP);
    	sUriMatcher.addURI(SocialContract.AUTHORITY.getAuthority(),
    			SocialContract.UriPathIndex.PEOPLE_ACTIVITIY, SocialContract.UriMatcherIndex.PEOPLE_ACTIVITY);
    	sUriMatcher.addURI(SocialContract.AUTHORITY.getAuthority(),
    			SocialContract.UriPathIndex.PEOPLE_ACTIVITIY_SHARP, SocialContract.UriMatcherIndex.PEOPLE_ACTIVITY_SHARP);
    	sUriMatcher.addURI(SocialContract.AUTHORITY.getAuthority(),
    			SocialContract.UriPathIndex.COMMUNITY_ACTIVITIY, SocialContract.UriMatcherIndex.COMMUNITY_ACTIVITIY);
    	sUriMatcher.addURI(SocialContract.AUTHORITY.getAuthority(),
    			SocialContract.UriPathIndex.COMMUNITY_ACTIVITIY_SHARP, SocialContract.UriMatcherIndex.COMMUNITY_ACTIVITIY_SHARP);
    	sUriMatcher.addURI(SocialContract.AUTHORITY.getAuthority(),
    			SocialContract.UriPathIndex.SERVICE_ACTIVITY, SocialContract.UriMatcherIndex.SERVICE_ACTIVITY);
    	sUriMatcher.addURI(SocialContract.AUTHORITY.getAuthority(),
    			SocialContract.UriPathIndex.SERVICE_ACTIVITY_SHARP, SocialContract.UriMatcherIndex.SERVICE_ACTIVITY_SHARP);

    	}
   	/* 
     * 
     * (non-Javadoc)
     * @see android.content.ContentProvider#onCreate()
     */
    @Override
    public boolean onCreate() {
    	android.util.Log.d(TAG, ": In onCreate()");
    	Context context = getContext();
    	adapter = new LocalDBAdapter(context);
    	android.util.Log.d(TAG, ": dbAdapter created.");
    	//Outsourcing initial data set to a separate class:
    	//If this is the first time, populate the DB:
    	if(adapter.firstRun()){
    		SocialDataSet dataSet = new SocialDataSet(adapter);
        	dataSet.populate();
        	android.util.Log.d(TAG, ": Data set created.");
    	}
     	return true;	
    }
    
    /* 
	 * (non-Javadoc)
	 * @see android.content.ContentProvider#insert(android.net.Uri, android.content.ContentValues)
	 */
	
	@Override
	public Uri insert(Uri _uri, ContentValues _values) {
	
		//Switch on the name of the path used in the query:
		Uri returnUri = null;
		int index = sUriMatcher.match(_uri);
		switch (index){
		case SocialContract.UriMatcherIndex.ME:
			
			//TODO: For all these, need to check for missing values in _values and add them.
			returnUri = Uri.withAppendedPath(_uri, Long.toString(adapter.insertMe(_values)));
			break;
			
		// Cannot insert in specific index in the table. SQLite decides index.
		//case SocialContract.UriMatcherIndex.ME_SHARP:
		//	break;
			
		case SocialContract.UriMatcherIndex.PEOPLE:
			
			returnUri = Uri.withAppendedPath(_uri, Long.toString(adapter.insertPeople(_values)));
			break;
			
	   	// Cannot insert in specific index in the table. SQLite decides index.
		//case SocialContract.UriMatcherIndex.PEOPLE_SHARP:
		//	break;
			
		case SocialContract.UriMatcherIndex.COMMUNITIES:
			returnUri = Uri.withAppendedPath(_uri, Long.toString(adapter.insertCommunities(_values)));
			break;
	
		// Cannot insert in specific index in the table. SQLite decides index.
		//case SocialContract.UriMatcherIndex.COMMUNITIES_SHARP:
		//	break;
			
		case SocialContract.UriMatcherIndex.SERVICES:
			returnUri = Uri.withAppendedPath(_uri, Long.toString(adapter.insertServices(_values)));
			break;
			
	   	// Cannot insert in specific index in the table. SQLite decides index.
		//case SocialContract.UriMatcherIndex.SERVICES_SHARP:
		//	break;
			
		case SocialContract.UriMatcherIndex.RELATIONSHIP:
			returnUri = Uri.withAppendedPath(_uri, Long.toString(adapter.insertRelationship(_values)));
			break;
			
	   	// Cannot insert in specific index in the table. SQLite decides index.
		//case SocialContract.UriMatcherIndex.RELATIONSHIP_SHARP:
		//	break;
			
		case SocialContract.UriMatcherIndex.MEMBERSHIP:
			returnUri = Uri.withAppendedPath(_uri, Long.toString(adapter.insertMembership(_values)));
			break;
			
	   	// Cannot insert in specific index in the table. SQLite decides index.	
		//case SocialContract.UriMatcherIndex.MEMBERSHIP_SHARP:
		//	break;
			
		case SocialContract.UriMatcherIndex.SHARING:
			returnUri = Uri.withAppendedPath(_uri, Long.toString(adapter.insertSharing(_values)));
			break;
			
	   	// Cannot insert in specific index in the table. SQLite decides index.
		//case SocialContract.UriMatcherIndex.SHARING_SHARP:
		//	break;
			
		case SocialContract.UriMatcherIndex.PEOPLE_ACTIVITY:
			returnUri = Uri.withAppendedPath(_uri, Long.toString(adapter.insertPeopleActivity(_values)));
			break;
			
	   	// Cannot insert in specific index in the table. SQLite decides index.
		//case SocialContract.UriMatcherIndex.PEOPLE_ACTIVITY_SHARP:
		//	break;
			
		case SocialContract.UriMatcherIndex.COMMUNITY_ACTIVITIY:
			returnUri = Uri.withAppendedPath(_uri, Long.toString(adapter.insertCommunityActivity(_values)));
			break;
			
	   	// Cannot insert in specific index in the table. SQLite decides index.
		//case SocialContract.UriMatcherIndex.COMMUNITY_ACTIVITIY_SHARP:
		//	break;
			
		case SocialContract.UriMatcherIndex.SERVICE_ACTIVITY:
			returnUri = Uri.withAppendedPath(_uri, Long.toString(adapter.insertServiceActivity(_values)));
			break;
			
	   	// Cannot insert in specific index in the table. SQLite decides index.
		//case SocialContract.UriMatcherIndex.SERVICE_ACTIVITY_SHARP:
		//	break;
			
		default:
	        throw new IllegalArgumentException("Unsupported URI sent to SocialProvider insert:" + _uri);    	
			
		}
		
		//Inform content resolvers about changes:
		getContext().getContentResolver().notifyChange(returnUri, null);
		
		return returnUri;
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
		
		String rowID = null;
		
		//Use a query builder to build the query in the switch:
		//SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		
		//Switch on the name of the path used in the query:
		switch (sUriMatcher.match(_uri)){
	
		case SocialContract.UriMatcherIndex.ME:
			return adapter.queryMe(_projection, _selection, _selectionArgs, _sortOrder);
		case SocialContract.UriMatcherIndex.ME_SHARP:
			//I have to set selection to exact row ID:
			rowID = _uri.getPathSegments().get(1);
			return adapter.queryMe(_projection, SocialContract.Me._ID+" = "+rowID,
					_selectionArgs, _sortOrder);
			
		case SocialContract.UriMatcherIndex.PEOPLE:
			return adapter.queryPeople(_projection, _selection, _selectionArgs, _sortOrder);
		case SocialContract.UriMatcherIndex.PEOPLE_SHARP:
			//I have to set selection to exact row ID:
			rowID = _uri.getPathSegments().get(1);
			return adapter.queryMe(_projection, SocialContract.People._ID+" = "+rowID,
					_selectionArgs, _sortOrder);
			
		case SocialContract.UriMatcherIndex.COMMUNITIES:
			return adapter.queryCommunities(_projection, _selection, _selectionArgs, _sortOrder);
			
		case SocialContract.UriMatcherIndex.COMMUNITIES_SHARP:
			//I have to set selection to exact row ID:
			rowID = _uri.getPathSegments().get(1);
			return adapter.queryCommunities(_projection, SocialContract.Communities._ID+" = "+rowID,
					_selectionArgs, _sortOrder);
			
		case SocialContract.UriMatcherIndex.SERVICES:
			return adapter.queryServices(_projection, _selection, _selectionArgs, _sortOrder);
			
		case SocialContract.UriMatcherIndex.SERVICES_SHARP:
			//I have to set selection to exact row ID:
			rowID = _uri.getPathSegments().get(1);
			return adapter.queryServices(_projection, SocialContract.Services._ID+" = "+rowID,
					_selectionArgs, _sortOrder);
			
		case SocialContract.UriMatcherIndex.RELATIONSHIP:
			return adapter.queryRelationship(_projection, _selection, _selectionArgs, _sortOrder);
			
		case SocialContract.UriMatcherIndex.RELATIONSHIP_SHARP:
			//I have to set selection to exact row ID:
			rowID = _uri.getPathSegments().get(1);
			return adapter.queryRelationship(_projection, SocialContract.Relationship._ID+" = "+rowID,
					_selectionArgs, _sortOrder);
			
		case SocialContract.UriMatcherIndex.MEMBERSHIP:
			return adapter.queryMembership(_projection, _selection, _selectionArgs, _sortOrder);
			
		case SocialContract.UriMatcherIndex.MEMBERSHIP_SHARP:
			//I have to set selection to exact row ID:
			rowID = _uri.getPathSegments().get(1);
			return adapter.queryMembership(_projection, SocialContract.Membership._ID+" = "+rowID,
					_selectionArgs, _sortOrder);

		case SocialContract.UriMatcherIndex.SHARING:
			return adapter.querySharing(_projection, _selection, _selectionArgs, _sortOrder);
			
		case SocialContract.UriMatcherIndex.SHARING_SHARP:
			//I have to set selection to exact row ID:
			rowID = _uri.getPathSegments().get(1);
			return adapter.querySharing(_projection, SocialContract.Sharing._ID+" = "+rowID,
					_selectionArgs, _sortOrder);

		case SocialContract.UriMatcherIndex.PEOPLE_ACTIVITY:
			return adapter.queryPeopleActivity(_projection, _selection, _selectionArgs, _sortOrder);
			
		case SocialContract.UriMatcherIndex.PEOPLE_ACTIVITY_SHARP:
			//I have to set selection to exact row ID:
			rowID = _uri.getPathSegments().get(1);
			return adapter.queryPeopleActivity(_projection, SocialContract.PeopleActivity._ID+" = "+rowID,
					_selectionArgs, _sortOrder);

		case SocialContract.UriMatcherIndex.COMMUNITY_ACTIVITIY:
			return adapter.queryCommunityActivity(_projection, _selection, _selectionArgs, _sortOrder);
			
		case SocialContract.UriMatcherIndex.COMMUNITY_ACTIVITIY_SHARP:
			//I have to set selection to exact row ID:
			rowID = _uri.getPathSegments().get(1);
			return adapter.queryCommunityActivity(_projection, SocialContract.CommunityActivity._ID+" = "+rowID,
					_selectionArgs, _sortOrder);

		case SocialContract.UriMatcherIndex.SERVICE_ACTIVITY:
			return adapter.queryServiceActivity(_projection, _selection, _selectionArgs, _sortOrder);
			
		case SocialContract.UriMatcherIndex.SERVICE_ACTIVITY_SHARP:
			//I have to set selection to exact row ID:
			rowID = _uri.getPathSegments().get(1);
			return adapter.queryServiceActivity(_projection, SocialContract.ServiceActivity._ID+" = "+rowID,
					_selectionArgs, _sortOrder);
		default:
	        throw new IllegalArgumentException("Unsupported URI in SocialProvider query method:" + _uri);   			
		}
	
	
	}

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#update(android.net.Uri, android.content.ContentValues, java.lang.String, java.lang.String[])
	 */
	@Override
	public int update(Uri _uri, ContentValues _values, String _selection,
	    String[] _selectionArgs) {
		int updateCount = 0;
		String rowID = null;
		String selection = null;
		switch (sUriMatcher.match(_uri)){
		case SocialContract.UriMatcherIndex.ME:
			//Call the right method with original parameters:
			updateCount = adapter.updateMe(_values, _selection, _selectionArgs);
			//Inform resolvers about change:
			getContext().getContentResolver().notifyChange(_uri, null);
			//Return number of rows updated:
			return updateCount;

		case SocialContract.UriMatcherIndex.ME_SHARP:
			//If this is a # query, add row ID to the selection:
			rowID = _uri.getPathSegments().get(1);
			selection = SocialContract.Me._ID+" = "+ rowID
					+ (!TextUtils.isEmpty(_selection) ?
							" AND (" + _selection + ")" : "");
			updateCount = adapter.updateMe(_values, selection, _selectionArgs);
			//Inform resolvers about change:
			getContext().getContentResolver().notifyChange(_uri, null);
			//Return number of rows updated:
			return updateCount;
			
		case SocialContract.UriMatcherIndex.PEOPLE:
			//Call the right method with original parameters:
			updateCount = adapter.updatePeople(_values, _selection, _selectionArgs);
			//Inform resolvers about change:
			getContext().getContentResolver().notifyChange(_uri, null);
			//Return number of rows updated:
			return updateCount;
			
		case SocialContract.UriMatcherIndex.PEOPLE_SHARP:
			rowID = _uri.getPathSegments().get(1);
			selection = SocialContract.People._ID+" = "+ rowID
					+ (!TextUtils.isEmpty(_selection) ?
							" AND (" + _selection + ")" : "");			
			updateCount = adapter.updatePeople(_values, selection, _selectionArgs);
			//Inform resolvers about change:
			getContext().getContentResolver().notifyChange(_uri, null);
			//Return number of rows updated:
			return updateCount;
			
		case SocialContract.UriMatcherIndex.COMMUNITIES:
			//Call the right method with original parameters:
			updateCount = adapter.updateCommunities(_values, _selection, _selectionArgs);
			//Inform resolvers about change:
			getContext().getContentResolver().notifyChange(_uri, null);
			//Return number of rows updated:
			return updateCount;
			
		case SocialContract.UriMatcherIndex.COMMUNITIES_SHARP:
			rowID = _uri.getPathSegments().get(1);
			selection = SocialContract.Communities._ID+" = "+ rowID
					+ (!TextUtils.isEmpty(_selection) ?
							" AND (" + _selection + ")" : "");
			updateCount = adapter.updateCommunities(_values, selection, _selectionArgs);
			//Inform resolvers about change:
			getContext().getContentResolver().notifyChange(_uri, null);
			//Return number of rows updated:
			return updateCount;
			
		case SocialContract.UriMatcherIndex.SERVICES:
			//Call the right method with original parameters:
			updateCount = adapter.updateServices(_values, _selection, _selectionArgs);
			//Inform resolvers about change:
			getContext().getContentResolver().notifyChange(_uri, null);
			//Return number of rows updated:
			return updateCount;
			
		case SocialContract.UriMatcherIndex.SERVICES_SHARP:
			rowID = _uri.getPathSegments().get(1);
			selection = SocialContract.Services._ID+" = "+ rowID
					+ (!TextUtils.isEmpty(_selection) ?
							" AND (" + _selection + ")" : "");
			updateCount = adapter.updateServices(_values, selection, _selectionArgs);
			//Inform resolvers about change:
			getContext().getContentResolver().notifyChange(_uri, null);
			//Return number of rows updated:
			return updateCount;
			
		case SocialContract.UriMatcherIndex.RELATIONSHIP:
			//Call the right method with original parameters:
			updateCount = adapter.updateRelationship(_values, _selection, _selectionArgs);
			//Inform resolvers about change:
			getContext().getContentResolver().notifyChange(_uri, null);
			//Return number of rows updated:
			return updateCount;
			
		case SocialContract.UriMatcherIndex.RELATIONSHIP_SHARP:
			rowID = _uri.getPathSegments().get(1);
			selection = SocialContract.Relationship._ID+" = "+ rowID
					+ (!TextUtils.isEmpty(_selection) ?
							" AND (" + _selection + ")" : "");
			updateCount = adapter.updateRelationship(_values, selection, _selectionArgs);
			//Inform resolvers about change:
			getContext().getContentResolver().notifyChange(_uri, null);
			//Return number of rows updated:
			return updateCount;
			
		case SocialContract.UriMatcherIndex.MEMBERSHIP:
			//Call the right method with original parameters:
			updateCount = adapter.updateMembership(_values, _selection, _selectionArgs);
			//Inform resolvers about change:
			getContext().getContentResolver().notifyChange(_uri, null);
			//Return number of rows updated:
			return updateCount;
			
		case SocialContract.UriMatcherIndex.MEMBERSHIP_SHARP:
			rowID = _uri.getPathSegments().get(1);
			selection = SocialContract.Membership._ID+" = "+ rowID
					+ (!TextUtils.isEmpty(_selection) ?
							" AND (" + _selection + ")" : "");
			updateCount = adapter.updateMembership(_values, selection, _selectionArgs);
			//Inform resolvers about change:
			getContext().getContentResolver().notifyChange(_uri, null);
			//Return number of rows updated:
			return updateCount;
			
		case SocialContract.UriMatcherIndex.SHARING:
			//Call the right method with original parameters:
			updateCount = adapter.updateSharing(_values, _selection, _selectionArgs);
			//Inform resolvers about change:
			getContext().getContentResolver().notifyChange(_uri, null);
			//Return number of rows updated:
			return updateCount;
			
		case SocialContract.UriMatcherIndex.SHARING_SHARP:
			rowID = _uri.getPathSegments().get(1);
			selection = SocialContract.Sharing._ID+" = "+ rowID
					+ (!TextUtils.isEmpty(_selection) ?
							" AND (" + _selection + ")" : "");
			updateCount = adapter.updateSharing(_values, selection, _selectionArgs);
			//Inform resolvers about change:
			getContext().getContentResolver().notifyChange(_uri, null);
			//Return number of rows updated:
			return updateCount;
			
		case SocialContract.UriMatcherIndex.PEOPLE_ACTIVITY:
			//Call the right method with original parameters:
			updateCount = adapter.updatePeopleActivity(_values, _selection, _selectionArgs);
			//Inform resolvers about change:
			getContext().getContentResolver().notifyChange(_uri, null);
			//Return number of rows updated:
			return updateCount;
			
		case SocialContract.UriMatcherIndex.PEOPLE_ACTIVITY_SHARP:
			rowID = _uri.getPathSegments().get(1);
			selection = SocialContract.PeopleActivity._ID+" = "+ rowID
					+ (!TextUtils.isEmpty(_selection) ?
							" AND (" + _selection + ")" : "");
			updateCount = adapter.updatePeopleActivity(_values, selection, _selectionArgs);
			//Inform resolvers about change:
			getContext().getContentResolver().notifyChange(_uri, null);
			//Return number of rows updated:
			return updateCount;
			
		case SocialContract.UriMatcherIndex.COMMUNITY_ACTIVITIY:
			//Call the right method with original parameters:
			updateCount = adapter.updateCommunityActivity(_values, _selection, _selectionArgs);
			//Inform resolvers about change:
			getContext().getContentResolver().notifyChange(_uri, null);
			//Return number of rows updated:
			return updateCount;
			
		case SocialContract.UriMatcherIndex.COMMUNITY_ACTIVITIY_SHARP:
			rowID = _uri.getPathSegments().get(1);
			selection = SocialContract.CommunityActivity._ID+" = "+ rowID
					+ (!TextUtils.isEmpty(_selection) ?
							" AND (" + _selection + ")" : "");
			updateCount = adapter.updateCommunityActivity(_values, selection, _selectionArgs);
			//Inform resolvers about change:
			getContext().getContentResolver().notifyChange(_uri, null);
			//Return number of rows updated:
			return updateCount;
			
		case SocialContract.UriMatcherIndex.SERVICE_ACTIVITY:
			//Call the right method with original parameters:
			updateCount = adapter.updateServiceActivity(_values, _selection, _selectionArgs);
			//Inform resolvers about change:
			getContext().getContentResolver().notifyChange(_uri, null);
			//Return number of rows updated:
			return updateCount;
			
		case SocialContract.UriMatcherIndex.SERVICE_ACTIVITY_SHARP:
			rowID = _uri.getPathSegments().get(1);
			selection = SocialContract.ServiceActivity._ID+" = "+ rowID
					+ (!TextUtils.isEmpty(_selection) ?
							" AND (" + _selection + ")" : "");
			updateCount = adapter.updateServiceActivity(_values, selection, _selectionArgs);
			//Inform resolvers about change:
			getContext().getContentResolver().notifyChange(_uri, null);
			//Return number of rows updated:
			return updateCount;
		default:
	        throw new IllegalArgumentException("Unsupported URI in SocialProvider update method:" + _uri);    				
		}
	}
		
		/* (non-Javadoc)
	     * @see android.content.ContentProvider#delete(android.net.Uri, java.lang.String, java.lang.String[])
	     */
	    @Override
	    public int delete(Uri _uri, String _selection, String[] _selectionArgs) {
		// TODO Auto-generated method stub
	    	String rowID = null;
	    	String selection = null;
	    	int deleteCount = 0;
	    	
	    	switch (sUriMatcher.match(_uri)){
	    	case SocialContract.UriMatcherIndex.ME:
	    		//To return the number of deleted items, you must
	    		//specify a where clause. To delete all rows and
	    		//return a value pass in "1":
	    		if (_selection == null)
	    			_selection = "1";
				deleteCount = adapter.deleteMe(_selection, _selectionArgs);
				//Inform resolvers about change:
				getContext().getContentResolver().notifyChange(_uri, null);
				//Return number of rows updated:
				return deleteCount;
	    		
	    	case SocialContract.UriMatcherIndex.ME_SHARP:
				//If this is a # query, add row ID to the selection:
				rowID = _uri.getPathSegments().get(1);
				selection = SocialContract.Me._ID+" = "+ rowID
						+ (!TextUtils.isEmpty(_selection) ?
								" AND (" + _selection + ")" : "");
				deleteCount = adapter.deleteMe(selection, _selectionArgs);
				//Inform resolvers about change:
				getContext().getContentResolver().notifyChange(_uri, null);
				//Return number of rows updated:
				return deleteCount;
	    		
	    	case SocialContract.UriMatcherIndex.PEOPLE:
	    		//To return the number of deleted items, you must
	    		//specify a where clause. To delete all rows and
	    		//return a value pass in "1":
	    		if (_selection == null)
	    			_selection = "1";
				deleteCount = adapter.deletePeople(_selection, _selectionArgs);
				//Inform resolvers about change:
				getContext().getContentResolver().notifyChange(_uri, null);
				//Return number of rows updated:
				return deleteCount;
	    		
	    	case SocialContract.UriMatcherIndex.PEOPLE_SHARP:
				//If this is a # query, add row ID to the selection:
				rowID = _uri.getPathSegments().get(1);
				selection = SocialContract.People._ID+" = "+ rowID
						+ (!TextUtils.isEmpty(_selection) ?
								" AND (" + _selection + ")" : "");
				deleteCount = adapter.deletePeople(selection, _selectionArgs);
				//Inform resolvers about change:
				getContext().getContentResolver().notifyChange(_uri, null);
				//Return number of rows updated:
				return deleteCount;
	    		
	    	case SocialContract.UriMatcherIndex.COMMUNITIES:
	    		//To return the number of deleted items, you must
	    		//specify a where clause. To delete all rows and
	    		//return a value pass in "1":
	    		if (_selection == null)
	    			_selection = "1";
				deleteCount = adapter.deleteCommunities(_selection, _selectionArgs);
				//Inform resolvers about change:
				getContext().getContentResolver().notifyChange(_uri, null);
				//Return number of rows updated:
				return deleteCount;
	    		
	    	case SocialContract.UriMatcherIndex.COMMUNITIES_SHARP:
				//If this is a # query, add row ID to the selection:
				rowID = _uri.getPathSegments().get(1);
				selection = SocialContract.Communities._ID+" = "+ rowID
						+ (!TextUtils.isEmpty(_selection) ?
								" AND (" + _selection + ")" : "");
				deleteCount = adapter.deleteCommunities(selection, _selectionArgs);
				//Inform resolvers about change:
				getContext().getContentResolver().notifyChange(_uri, null);
				//Return number of rows updated:
				return deleteCount;
	    		
	    	case SocialContract.UriMatcherIndex.SERVICES:
	    		//To return the number of deleted items, you must
	    		//specify a where clause. To delete all rows and
	    		//return a value pass in "1":
	    		if (_selection == null)
	    			_selection = "1";
				deleteCount = adapter.deleteServices(_selection, _selectionArgs);
				//Inform resolvers about change:
				getContext().getContentResolver().notifyChange(_uri, null);
				//Return number of rows updated:
				return deleteCount;
	    		
	    	case SocialContract.UriMatcherIndex.SERVICES_SHARP:
				//If this is a # query, add row ID to the selection:
				rowID = _uri.getPathSegments().get(1);
				selection = SocialContract.Services._ID+" = "+ rowID
						+ (!TextUtils.isEmpty(_selection) ?
								" AND (" + _selection + ")" : "");
				deleteCount = adapter.deleteServices(selection, _selectionArgs);
				//Inform resolvers about change:
				getContext().getContentResolver().notifyChange(_uri, null);
				//Return number of rows updated:
				return deleteCount;
	    		
	    	case SocialContract.UriMatcherIndex.RELATIONSHIP:
	    		//To return the number of deleted items, you must
	    		//specify a where clause. To delete all rows and
	    		//return a value pass in "1":
	    		if (_selection == null)
	    			_selection = "1";
				deleteCount = adapter.deleteRelationship(_selection, _selectionArgs);
				//Inform resolvers about change:
				getContext().getContentResolver().notifyChange(_uri, null);
				//Return number of rows updated:
				return deleteCount;
	    		
	    	case SocialContract.UriMatcherIndex.RELATIONSHIP_SHARP:
				//If this is a # query, add row ID to the selection:
				rowID = _uri.getPathSegments().get(1);
				selection = SocialContract.Relationship._ID+" = "+ rowID
						+ (!TextUtils.isEmpty(_selection) ?
								" AND (" + _selection + ")" : "");
				deleteCount = adapter.deleteRelationship(selection, _selectionArgs);
				//Inform resolvers about change:
				getContext().getContentResolver().notifyChange(_uri, null);
				//Return number of rows updated:
				return deleteCount;
	    		
	    	case SocialContract.UriMatcherIndex.MEMBERSHIP:
	    		//To return the number of deleted items, you must
	    		//specify a where clause. To delete all rows and
	    		//return a value pass in "1":
	    		if (_selection == null)
	    			_selection = "1";
				deleteCount = adapter.deleteMembership(_selection, _selectionArgs);
				//Inform resolvers about change:
				getContext().getContentResolver().notifyChange(_uri, null);
				//Return number of rows updated:
				return deleteCount;
	    		
	    	case SocialContract.UriMatcherIndex.MEMBERSHIP_SHARP:
				//If this is a # query, add row ID to the selection:
				rowID = _uri.getPathSegments().get(1);
				selection = SocialContract.Membership._ID+" = "+ rowID
						+ (!TextUtils.isEmpty(_selection) ?
								" AND (" + _selection + ")" : "");
				deleteCount = adapter.deleteMembership(selection, _selectionArgs);
				//Inform resolvers about change:
				getContext().getContentResolver().notifyChange(_uri, null);
				//Return number of rows updated:
				return deleteCount;
	    		
	    	case SocialContract.UriMatcherIndex.SHARING:
	    		//To return the number of deleted items, you must
	    		//specify a where clause. To delete all rows and
	    		//return a value pass in "1":
	    		if (_selection == null)
	    			_selection = "1";
				deleteCount = adapter.deleteSharing(_selection, _selectionArgs);
				//Inform resolvers about change:
				getContext().getContentResolver().notifyChange(_uri, null);
				//Return number of rows updated:
				return deleteCount;
	    		
	    	case SocialContract.UriMatcherIndex.SHARING_SHARP:
				//If this is a # query, add row ID to the selection:
				rowID = _uri.getPathSegments().get(1);
				selection = SocialContract.Sharing._ID+" = "+ rowID
						+ (!TextUtils.isEmpty(_selection) ?
								" AND (" + _selection + ")" : "");
				deleteCount = adapter.deleteSharing(selection, _selectionArgs);
				//Inform resolvers about change:
				getContext().getContentResolver().notifyChange(_uri, null);
				//Return number of rows updated:
				return deleteCount;
	    		
	    	case SocialContract.UriMatcherIndex.PEOPLE_ACTIVITY:
	    		//To return the number of deleted items, you must
	    		//specify a where clause. To delete all rows and
	    		//return a value pass in "1":
	    		if (_selection == null)
	    			_selection = "1";
				deleteCount = adapter.deletePeopleActivity(_selection, _selectionArgs);
				//Inform resolvers about change:
				getContext().getContentResolver().notifyChange(_uri, null);
				//Return number of rows updated:
				return deleteCount;
	    		
	    	case SocialContract.UriMatcherIndex.PEOPLE_ACTIVITY_SHARP:
				//If this is a # query, add row ID to the selection:
				rowID = _uri.getPathSegments().get(1);
				selection = SocialContract.PeopleActivity._ID+" = "+ rowID
						+ (!TextUtils.isEmpty(_selection) ?
								" AND (" + _selection + ")" : "");
				deleteCount = adapter.deletePeopleActivity(selection, _selectionArgs);
				//Inform resolvers about change:
				getContext().getContentResolver().notifyChange(_uri, null);
				//Return number of rows updated:
				return deleteCount;
	    		
	    	case SocialContract.UriMatcherIndex.COMMUNITY_ACTIVITIY:
	    		//To return the number of deleted items, you must
	    		//specify a where clause. To delete all rows and
	    		//return a value pass in "1":
	    		if (_selection == null)
	    			_selection = "1";
				deleteCount = adapter.deleteCommunityActivity(_selection, _selectionArgs);
				//Inform resolvers about change:
				getContext().getContentResolver().notifyChange(_uri, null);
				//Return number of rows updated:
				return deleteCount;
	    		
	    	case SocialContract.UriMatcherIndex.COMMUNITY_ACTIVITIY_SHARP:
				//If this is a # query, add row ID to the selection:
				rowID = _uri.getPathSegments().get(1);
				selection = SocialContract.CommunityActivity._ID+" = "+ rowID
						+ (!TextUtils.isEmpty(_selection) ?
								" AND (" + _selection + ")" : "");
				deleteCount = adapter.deleteCommunityActivity(selection, _selectionArgs);
				//Inform resolvers about change:
				getContext().getContentResolver().notifyChange(_uri, null);
				//Return number of rows updated:
				return deleteCount;
	    		
	    	case SocialContract.UriMatcherIndex.SERVICE_ACTIVITY:
	    		//To return the number of deleted items, you must
	    		//specify a where clause. To delete all rows and
	    		//return a value pass in "1":
	    		if (_selection == null)
	    			_selection = "1";
				deleteCount = adapter.deleteServiceActivity(_selection, _selectionArgs);
				//Inform resolvers about change:
				getContext().getContentResolver().notifyChange(_uri, null);
				//Return number of rows updated:
				return deleteCount;
	    		
	    	case SocialContract.UriMatcherIndex.SERVICE_ACTIVITY_SHARP:
				//If this is a # query, add row ID to the selection:
				rowID = _uri.getPathSegments().get(1);
				selection = SocialContract.ServiceActivity._ID+" = "+ rowID
						+ (!TextUtils.isEmpty(_selection) ?
								" AND (" + _selection + ")" : "");
				deleteCount = adapter.deleteServiceActivity(selection, _selectionArgs);
				//Inform resolvers about change:
				getContext().getContentResolver().notifyChange(_uri, null);
				//Return number of rows updated:
				return deleteCount;
	    	default:
	            throw new IllegalArgumentException("Unsupported URI in SocialProvider delete method:" + _uri);
	    	}
	
	   }
		
		/* (non-Javadoc)
		 * @see android.content.ContentProvider#getType(android.net.Uri)
		 */
		@Override
		public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return null;
		}
}


