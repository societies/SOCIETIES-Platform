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
package org.societies.cis.android.client;

import java.util.ArrayList;
import java.util.List;

import org.societies.api.cis.management.ICisManager;
import org.societies.api.cis.management.ICisOwned;
import org.societies.api.cis.management.ICisRecord;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

/**
 * Android implementation of ICisManager.
 * 
 * @author Babak.Farshchian@sintef.no
 *
 */
public class CisManager implements ICisManager {

//    private List<ICisOwned> ownedDisasterList = new ArrayList<ICisOwned>();
    //List to hold disasters this users is only a member of:
//    private List<ICisSubscribed> subscribedDisasterList = new ArrayList<ICisSubscribed>();
    Context context;
    
    public CisManager(Context _context){
	context = _context;
    }

    /* 
     * This method adds a CisOwned to CIS manager. It returns the added CIS 
     * with the cisId set.
     * 
     * 
     * @see org.societies.api.cis.management.ICisManager#createCis(java.lang.String, java.lang.String, java.lang.String, java.lang.String, int)
     */
    //TODO _cisId should be removed from the signature:
    public ICisOwned createCis(String _cisId, String _cisName, String _ownerId,
	    String _cisType, int _membershipType) {
	Uri newUri;
	//Create a new CIS in the content provider:
	ContentValues cv = new ContentValues();
	cv.put(SocialContract.Groups.NAME , _cisName);
	cv.put(SocialContract.Groups.OWNER_ID , _ownerId);
	cv.put(SocialContract.Groups.TYPE , _cisType);
	cv.put(SocialContract.Groups.MEMBERSHIP_TYPE , Integer.toString(_membershipType));
	//Send the CIS to the content provider:
	newUri = context.getContentResolver().insert(
		    SocialContract.Groups.CONTENT_URI,   // the user dictionary content URI
		    cv                          // the values to insert
		);
	//Create a new CIS with the returned ID from the content provider:
	CisOwned newCis = new CisOwned(
		newUri.getLastPathSegment() , 
		_cisName, _ownerId, _cisType, _membershipType);
	return newCis;
    }

    /* (non-Javadoc)
     * @see org.societies.api.cis.management.ICisManager#deleteCis(java.lang.String, java.lang.String, java.lang.String)
     */
    public Boolean deleteCis(String cssId, String cssPassword, String cisId) {
	// TODO Auto-generated method stub
	return null;
    }

    /* (non-Javadoc)
     * @see org.societies.api.cis.management.ICisManager#getCis(java.lang.String, java.lang.String)
     */
    public ICisRecord getCis(String cssId, String cisId) {
	// TODO Auto-generated method stub
	return null;
    }

    /* (non-Javadoc)
     * @see org.societies.api.cis.management.ICisManager#getCisList(org.societies.api.cis.management.ICisRecord)
     */
    public ICisRecord[] getCisList(ICisRecord query) {
	// TODO Auto-generated method stub
	return null;
    }

    /* (non-Javadoc)
     * @see org.societies.api.cis.management.ICisManager#getCisList()
     */
    public List<ICisRecord> getOwnedCisList(String _cssId, 
	    String _cssPassword, String _cisType) {

	// TODO Auto-generated method stub
	List<ICisRecord> cisList = new ArrayList<ICisRecord>();
	String[] mProjection =
	    {
	        SocialContract.Groups._ID,
	        SocialContract.Groups.NAME,
	        SocialContract.Groups.OWNER_ID,
	        SocialContract.Groups.TYPE	        
	    };

	// Defines a string to select all rows with type _cisType:
	String mSelectionClause = SocialContract.Groups.TYPE + " = " + _cisType;
	//Get the cursor from the content provider:
	Cursor cursor= context.getContentResolver().query(
		SocialContract.Groups.CONTENT_URI,
		mProjection,
		mSelectionClause,
		null, null);
	if (cursor == null){
	    Log.d("CisManager", "CisManager: Got null after query from content provider");
	}
	else if (cursor.getCount() <1){
	    Log.d("CisManager", "CisManager: Got minus after query from content provider");
	}
	else {
	    cursor.moveToFirst();
	    for (int i = 0; i< cursor.getCount();i++){
	    String id = cursor.getString(0);
	    String name = cursor.getString(1);
	    String ownerId = cursor.getString(2);
	    String type = cursor.getString(3);
	    CisOwned record = new CisOwned(id, name, ownerId, type, 1);
	    cisList.add(record);
	    cursor.moveToNext();
	    }
	    return cisList;
	}
	return null;
    }

    /* (non-Javadoc)
     * @see org.societies.api.cis.management.ICisManager#requestNewCisOwner(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    public Boolean requestNewCisOwner(String currentOwnerCssId,
	    String currentOwnerCssPassword, String newOwnerCssId, String cisId) {
	// TODO Auto-generated method stub
	return null;
    }

}
