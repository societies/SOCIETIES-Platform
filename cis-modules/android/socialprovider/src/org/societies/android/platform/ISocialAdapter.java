/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske dru�be in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVA��O, SA (PTIN), IBM Corp., 
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

import android.content.ContentValues;
import android.database.Cursor;

/**
 * All adapters have to implement this interface. It follows a logic very
 * similar to Android content provider. The authorities and URLs are documented
 * in {@link SocialContract2}. Currently it supports CRUD on people, groups and 
 * apps.
 * 
 * for the first four methods below see Content Provider interfaces in Android.
 * 
 * @author Babak.Farshchian@sintef.no
 *
 */
public interface ISocialAdapter {
	
	//CRUD for people:
	public long insertPeople(ContentValues values);
	public Cursor queryPeople(String[] projection, String selection,
			String[] selectionArgs, String sortOrder);
	public int updatePeople(ContentValues values, String selection,
			String[] selectionArgs);
	public int deletePeople(String _selection, String[] _selectionArgs);
	
	//CRUD for communities:
	public long insertCommunities(ContentValues values);
	public Cursor queryCommunities(String[] projection, String selection,
			String[] selectionArgs, String sortOrder);
	public int updateCommunities(ContentValues values, String selection,
			String[] selectionArgs);
	public int deleteCommunities(String _selection, String[] _selectionArgs);
	
	//CRUD for services:
	public long insertServices(ContentValues values);
	public Cursor queryServices(String[] projection, String selection,
			String[] selectionArgs, String sortOrder);
	public int updateServices(ContentValues values, String selection,
			String[] selectionArgs);
	public int deleteServices(String _selection, String[] _selectionArgs);
	
	//CRUD for relationships:
	public long insertRelationship(ContentValues values);
	public Cursor queryRelationship(String[] projection, String selection,
			String[] selectionArgs, String sortOrder);
	public int updateRelationship(ContentValues values, String selection,
			String[] selectionArgs);
	public int deleteRelationship(String _selection, String[] _selectionArgs);
	
	
	//CRUD for membership:
	public long insertMembership(ContentValues values);
	public Cursor queryMembership(String[] projection, String selection,
			String[] selectionArgs, String sortOrder);
	public int updateMembership(ContentValues values, String selection,
			String[] selectionArgs);
	public int deleteMembership(String _selection, String[] _selectionArgs);
	//CRUD for sharing:
	public long insertSharing(ContentValues values);
	public Cursor querySharing(String[] projection, String selection,
			String[] selectionArgs, String sortOrder);
	public int updateSharing(ContentValues values, String selection,
			String[] selectionArgs);
	public int deleteSharing(String _selection, String[] _selectionArgs);

	//CRUD for people activities:
	public long insertPeopleActivity(ContentValues values);
	public Cursor queryPeopleActivity(String[] projection, String selection,
			String[] selectionArgs, String sortOrder);
	public int updatePeopleActivity(ContentValues values, String selection,
			String[] selectionArgs);
	public int deletePeopleActivity(String _selection, String[] _selectionArgs);
	
	//CRUD for community activities:
	public long insertCommunityActivity(ContentValues values);
	public Cursor queryCommunityActivity(String[] projection, String selection,
			String[] selectionArgs, String sortOrder);
	public int updateCommunityActivity(ContentValues values, String selection,
			String[] selectionArgs);
	public int deleteCommunityActivity(String _selection, String[] _selectionArgs);

	//CRUD for service activities:
	public long insertServiceActivity(ContentValues values);
	public Cursor queryServiceActivity(String[] projection, String selection,
			String[] selectionArgs, String sortOrder);
	public int updateServiceActivity(ContentValues values, String selection,
			String[] selectionArgs);
	public int deleteServiceActivity(String _selection, String[] _selectionArgs);

	//CRUD for me:
	public long insertMe(ContentValues values);
	public Cursor queryMe(String[] projection, String selection,
	String[] selectionArgs, String sortOrder);
	public int updateMe(ContentValues values, String selection,
			String[] selectionArgs);
	public int deleteMe(String _selection, String[] _selectionArgs);
	 
    /**
     * A method that can be used to check whether this adapter is usable.
     * @return true if connection is on.
     */
    public boolean isConnected();
    
    /**
     * Called to make this adapter usable for storing and querying data. 
     * @return 1 if connection was set up correctly
     * TODO: Need to define other values for error reporting.
     */
    public int connect();
    /**
     * Called to make this adapter usable for storing and querying data in a location that requires credential.
     * 
     * @param username
     * @param password
     * @return 1 if connection was set up correctly. 
     * 
     */
    public int connect(String username, String password);
    public int disconnect();
    
    public boolean firstRun();
	/**
	 * Retrieves the list of CISs owned by CIS Manager
	 * 
	 * @return list of ICisOwned in the callback object*/
	public void getListOfOwnedCis(ISocialAdapterCallback callback);
    }
