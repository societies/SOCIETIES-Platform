package org.societies.android.platform;

import org.societies.android.api.cis.SocialContract;

import android.content.ContentValues;

public class SocialDataSet {

	private ISocialAdapter adapter = null;
	public SocialDataSet(ISocialAdapter _adapter){
		adapter = _adapter;
	}
	public boolean initialize(){
    	if (adapter.firstRun()){
    		populateMe();
    		populateCommunities();
    		//populateMyServices();
    		//populateMyPeople();
    		return true;
    	}
    	else return false;
	}
    private void populateMe(){
		ContentValues initialValues = new ContentValues();
		initialValues.put(SocialContract.Me.GLOBAL_ID , "babak@societies.org");
		initialValues.put(SocialContract.Me.NAME , "Babak Farshchian");
		initialValues.put(SocialContract.Me.DISPLAY_NAME , "Babak@SOCIETIES");
		adapter.insertMe(initialValues);
		initialValues.clear();
		initialValues.put(SocialContract.Me.GLOBAL_ID , "babak@facebook.com");
		initialValues.put(SocialContract.Me.NAME , "Babak F");
		initialValues.put(SocialContract.Me.DISPLAY_NAME , "Babak@FB");
		adapter.insertMe(initialValues);
	}
	private void populateCommunities(){
		ContentValues initialValues = new ContentValues();
		
		initialValues.put(SocialContract.Communities.GLOBAL_ID , "community1.societies.org");
		initialValues.put(SocialContract.Communities.TYPE , "sports");
		initialValues.put(SocialContract.Communities.NAME , "Community 1");
		//initialValues.put(SocialContract.Community.DISPLAY_NAME , "Football");
		initialValues.put(SocialContract.Communities.OWNER_ID, "babak@societies.org");
		//initialValues.put(SocialContract.Community.CREATION_DATE , "Today");
		//initialValues.put(SocialContract.Community.MEMBERSHIP_TYPE, "Open");
		adapter.connect();
		adapter.insertCommunities(initialValues);
		adapter.disconnect();
		initialValues.clear();

		initialValues.put(SocialContract.Community.GLOBAL_ID , "community2.societies.org");
		initialValues.put(SocialContract.Community.TYPE , "sports");
		initialValues.put(SocialContract.Community.NAME , "Community 2");
		//initialValues.put(SocialContract.Community.DISPLAY_NAME , "Baseball");
		initialValues.put(SocialContract.Community.OWNER_ID, "jacqueline@societies.org");
		//initialValues.put(SocialContract.Community.CREATION_DATE , "Today");
		//initialValues.put(SocialContract.Community.MEMBERSHIP_TYPE, "Open");
		initialValues.put(SocialContract.Community.DIRTY , "yes");
		adapter.connect();
		adapter.insertCommunities(initialValues);
		adapter.disconnect();
		initialValues.clear();

		initialValues.put(SocialContract.Community.GLOBAL_ID , "community3.societies.org");
		initialValues.put(SocialContract.Community.TYPE , "sports");
		initialValues.put(SocialContract.Community.NAME , "Community 3");
		//initialValues.put(SocialContract.Community.DISPLAY_NAME , "Basketball");
		initialValues.put(SocialContract.Community.OWNER_ID, "babak@societies.org");
		//initialValues.put(SocialContract.Community.CREATION_DATE , "Today");
		//initialValues.put(SocialContract.Community.MEMBERSHIP_TYPE, "Open");
		initialValues.put(SocialContract.Community.DIRTY , "yes");
		adapter.connect();
		adapter.insertCommunities(initialValues);
		adapter.disconnect();
		initialValues.clear();

		initialValues.put(SocialContract.Community.GLOBAL_ID , "community4.societies.org");
		initialValues.put(SocialContract.Community.TYPE , "sports");
		initialValues.put(SocialContract.Community.NAME , "Community 4");
		//initialValues.put(SocialContract.Community.DISPLAY_NAME , "Handball");
		initialValues.put(SocialContract.Community.OWNER_ID, "thomas@societies.org");
		//initialValues.put(SocialContract.Community.CREATION_DATE , "Today");
		//initialValues.put(SocialContract.Community.MEMBERSHIP_TYPE, "Closed");
		initialValues.put(SocialContract.Community.DIRTY , "yes");
		adapter.connect();
		adapter.insertCommunities(initialValues);
		adapter.disconnect();
    	
    }
}
