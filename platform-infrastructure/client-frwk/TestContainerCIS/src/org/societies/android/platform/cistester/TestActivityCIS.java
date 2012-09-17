package org.societies.android.platform.cistester;

import java.util.ArrayList;
import java.util.List;

import org.societies.android.api.cis.directory.ICisDirectory;
import org.societies.android.api.cis.management.AActivity;
import org.societies.android.api.cis.management.ACommunity;
import org.societies.android.api.cis.management.ACriteria;
import org.societies.android.api.cis.management.AJoinResponse;
import org.societies.android.api.cis.management.AParticipant;
import org.societies.android.api.cis.management.ICisManager;
import org.societies.android.api.cis.management.ICisSubscribed;
import org.societies.android.platform.cis.CisDirectoryRemote;
import org.societies.android.platform.cis.CommunityManagement;
import org.societies.android.platform.cis.CommunityManagement.LocalBinder;
import org.societies.api.schema.cis.manager.ListCrit;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;

public class TestActivityCIS extends Activity {

	private final String CLIENT_PACKAGE = "org.societies.android.platform.cistester";
	
    private ICisManager serviceCISManager;
    private boolean serviceCISManagerConnected = false;
    
    private ICisSubscribed serviceCISsubscribe;
    private boolean serviceCISsubscribeConnected = false;
    
    private ICisDirectory serviceCISdir;
    private boolean serviceCISdirConnected = false;
    
    private static final String LOG_TAG = TestActivityCIS.class.getName();
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        //CREATE INTENT FOR CIS MANAGER SERVICE AND BIND
        Intent intentCisManager = new Intent(this.getApplicationContext(), CommunityManagement.class);
        this.getApplicationContext().bindService(intentCisManager, cisManagerConnection, Context.BIND_AUTO_CREATE);
        
        //CREATE INTENT FOR CIS SUBSCRIBE AND BIND
        Intent intentCisSubscribe = new Intent(this.getApplicationContext(), CommunityManagement.class);
        this.getApplicationContext().bindService(intentCisSubscribe, cisSubscribeConnection, Context.BIND_AUTO_CREATE);
                
        //CREATE INTENT FOR CIS DIRECTORY AND BIND
        Intent intentCisDir = new Intent(this.getApplicationContext(), CisDirectoryRemote.class);
        this.getApplicationContext().bindService(intentCisDir, cisDirConnection, Context.BIND_AUTO_CREATE);
        
        //REGISTER BROADCAST
        //CIS Manager
        IntentFilter intentFilter = new IntentFilter() ;
        intentFilter.addAction(CommunityManagement.CREATE_CIS);
        intentFilter.addAction(CommunityManagement.DELETE_CIS);
        intentFilter.addAction(CommunityManagement.GET_CIS_LIST);
        //CIS Subscriber
        intentFilter.addAction(CommunityManagement.JOIN_CIS);
        intentFilter.addAction(CommunityManagement.GET_MEMBERS);
        intentFilter.addAction(CommunityManagement.GET_ACTIVITY_FEED);
        intentFilter.addAction(CommunityManagement.ADD_ACTIVITY);
        //CIS DIRECTORY
        intentFilter.addAction(CisDirectoryRemote.FIND_ALL_CIS);
        intentFilter.addAction(CisDirectoryRemote.FILTER_CIS);
        intentFilter.addAction(CisDirectoryRemote.FIND_CIS_ID);
        
        this.getApplicationContext().registerReceiver(new bReceiver(), intentFilter);
        
        //TEST THE SLM COMPONENT
        TestCIS task = new TestCIS(this);
        task.execute();
    }
    
    /**
     * ICisManager service connection
     */
    private ServiceConnection cisManagerConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName name, IBinder service) {
        	Log.d(LOG_TAG, "Connecting to ICisManager service");
        	try {
	        	//GET LOCAL BINDER
	            LocalBinder binder = (LocalBinder) service;
	
	            //OBTAIN SERVICE DISCOVERY API
	            serviceCISManager = (ICisManager) binder.getService();
	            serviceCISManagerConnected = true;
	            Log.d(LOG_TAG, "Successfully connected to ICisManager service");
	            
        	} catch (Exception ex) {
        		Log.d(LOG_TAG, "Error binding to service: " + ex.getMessage());
        	}
        }
        
        public void onServiceDisconnected(ComponentName name) {
        	Log.d(LOG_TAG, "Disconnecting from ServiceDiscovery service");
        	serviceCISManagerConnected = false;
        }
    };
    
    /**
     * ICisSubscribed service connection
     */
    private ServiceConnection cisSubscribeConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName name, IBinder service) {
        	Log.d(LOG_TAG, "Connecting to ICisSubsribed service");
        	//get a local binder
        	LocalBinder binder = (LocalBinder) service;
            //obtain the service's API
        	serviceCISsubscribe = (ICisSubscribed) binder.getService();
        	serviceCISsubscribeConnected = true;
            Log.d(LOG_TAG, "Successfully connected to ICisSubsribed service");
        }
        
        public void onServiceDisconnected(ComponentName name) {
        	Log.d(LOG_TAG, "Disconnecting from ICisSubsribed service");
        	serviceCISsubscribeConnected = false;
        }
    };

    /**
     * ICisDirectory service connection
     */
    private ServiceConnection cisDirConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName name, IBinder service) {
        	Log.d(LOG_TAG, "Connecting to ICisDirectory service");
        	//get a local binder
        	org.societies.android.platform.cis.CisDirectoryRemote.LocalBinder binder = (org.societies.android.platform.cis.CisDirectoryRemote.LocalBinder) service;
            //obtain the service's API
        	serviceCISdir = (ICisDirectory) binder.getService();
        	serviceCISdirConnected = true;
            Log.d(LOG_TAG, "Successfully connected to ICisDirectory service");
        }
        
        public void onServiceDisconnected(ComponentName name) {
        	Log.d(LOG_TAG, "Disconnecting from ICisDirectory service");
        	serviceCISsubscribeConnected = false;
        }
    };

    
    public void btnSendMessage_onClick(View view) {
    	Log.d(LOG_TAG, ">>>>>>>>btnSendMessage_onClick - serviceCISsubscribeConnected: " + serviceCISsubscribeConnected);
		//if (serviceCISsubscribeConnected)
		//
    }
    

    private class TestCIS extends AsyncTask<Void, Void, Void> {
    	
    	private Context context;
    	
    	public TestCIS(Context context) {
    		this.context = context;
    	}
    	
    	protected Void doInBackground(Void... args) {

    		try {//	WAIT TILL ALL THE SERVICES ARE CONNECTED
				Thread.currentThread();
				Thread.sleep(5 * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    		
    		//TEST CONNECTIONS
    		Log.d(LOG_TAG, ">>>>>>>>serviceCISManagerConnected: " + serviceCISManagerConnected);
    		Log.d(LOG_TAG, ">>>>>>>>serviceCISsubscribeConnected: " + serviceCISsubscribeConnected);
    		Log.d(LOG_TAG, ">>>>>>>>serviceCISdirConnected: " + serviceCISdirConnected);

    		//TEST: CREATE CIS
			List<ACriteria> rules = new ArrayList<ACriteria>();
			ACriteria crit1 = new ACriteria();
			ACriteria crit2 = new ACriteria();
			crit1.setAttrib("Location"); crit1.setOperator("="); crit1.setRank(1); crit1.setValue1("Dublin");
			crit2.setAttrib("Age"); crit2.setOperator(">"); crit2.setRank(2); crit2.setValue1("18");
			rules.add(crit1);
			rules.add(crit2);
			serviceCISManager.createCis(CLIENT_PACKAGE, "Tester CIS", "Test Type", "Test Description", rules, "privacy policy stuff");
    		
			//TEST: GET ALL CIS ADVERTISEMENTS FROM DIRECTORY
			serviceCISdir.findAllCisAdvertisementRecords(CLIENT_PACKAGE);
			
			return null;
    	}
    }
    
    private void continueTests(String cis_id, String cis_name) {
    	
    	//TEST: GET ADVERT RECORD
    	serviceCISdir.searchByID(CLIENT_PACKAGE, cis_id);
    	
    	//TEST: JOIN
    	serviceCISsubscribe.Join(CLIENT_PACKAGE, cis_id);
    	serviceCISManager.subscribeToCommunity(CLIENT_PACKAGE, cis_name, cis_id);
    	
    	//TEST: LIST CIS'S (OWNED + SUBSCRIBED)
    	serviceCISManager.getCisList(CLIENT_PACKAGE, ListCrit.ALL.toString());
    			
    	//TEST: GET MEMBERS OF CIS
		serviceCISsubscribe.getMembers(CLIENT_PACKAGE, cis_id);
		
		//TEST: ADD ACTIVITY
		AActivity activity = new AActivity();
		activity.setActor("Alec"); activity.setVerb("went"); activity.setTarget("mad"); activity.setTarget("late");
		serviceCISsubscribe.addActivity(CLIENT_PACKAGE, cis_id, activity); 
		
		//TEST: GET ACTIVITIES
		serviceCISsubscribe.getActivityFeed(CLIENT_PACKAGE, cis_id);
    }

    private class bReceiver extends BroadcastReceiver  {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(LOG_TAG, intent.getAction());
			
			if (intent.getAction().equals(CommunityManagement.CREATE_CIS)) {
				//UNMARSHALL THE COMMUNITY FROM Parcel BACK TO COMMUNITY
				Parcelable parcel =  intent.getParcelableExtra(CommunityManagement.INTENT_RETURN_VALUE);
				ACommunity cis =  (ACommunity) parcel;
				Log.d(LOG_TAG, ">>>>>CREATE COMMUNITY  RESULT:\nCIS ID: " + cis.getCommunityJid());
			}
			
			if (intent.getAction().equals(CisDirectoryRemote.FIND_ALL_CIS)) {
				//UNMARSHALL THE ADVERTS FROM THE RETURNED PARCELS
				Parcelable parcels[] =  intent.getParcelableArrayExtra(CisDirectoryRemote.INTENT_RETURN_VALUE);
				for (int i = 0; i < parcels.length; i++) {
					ACommunity cis = (ACommunity) parcels[i];
					Log.d(LOG_TAG, ">>>>>CIS DIRECTORY RESULTS:\nCIS ID: " + cis.getCommunityJid());
					continueTests(cis.getCommunityJid(), cis.getCommunityName());
				}
			}
			
			if (intent.getAction().equals(CommunityManagement.GET_CIS_LIST)) {
				//UNMARSHALL THE ID FROM THE RETURNED PARCEL
				Parcelable parcels[] =  intent.getParcelableArrayExtra(CommunityManagement.INTENT_RETURN_VALUE);
				for (int i = 0; i < parcels.length; i++) {
					ACommunity cis = (ACommunity) parcels[i];
					Log.d(LOG_TAG, ">>>>>GET CIS RESULTS:\nCIS ID: " + cis.getCommunityJid());
				}
			}
			
			if (intent.getAction().equals(CommunityManagement.JOIN_CIS)) {
				//UNMARSHALL THE community FROM Parcel
				Parcelable parcel =  intent.getParcelableExtra(CommunityManagement.INTENT_RETURN_VALUE);
				AJoinResponse resp = (AJoinResponse) parcel;
				Log.d(LOG_TAG, ">>>>>CIS JOIN RESULT:\n>>>>>Allowed to join: " + resp.isResult());
				Log.d(LOG_TAG, ">>>>>Community Joined: " + resp.getCommunity().getCommunityName() + "\n" + resp.getCommunity().getDescription());
			}
			
			if (intent.getAction().equals(CommunityManagement.GET_MEMBERS)) {
				//UNMARSHALL THE PARTICIPANTS FROM THE RETURNED PARCELS
				Parcelable parcels[] =  intent.getParcelableArrayExtra(CommunityManagement.INTENT_RETURN_VALUE);
				for (int i = 0; i < parcels.length; i++) {
					AParticipant member = (AParticipant) parcels[i];
					Log.d(LOG_TAG, ">>>>>CIS Member Listing RESULTS:\nMEMBER ID: " + member.getJid());
					Log.d(LOG_TAG, ">>>>>MEMBER ROLE: " + member.getRole().toString());
				}
			}
			
			if (intent.getAction().equals(CommunityManagement.ADD_ACTIVITY)) {
				//UNMARSHALL THE RESULT FROM Parcel 
				Parcelable parcel =  intent.getParcelableExtra(CommunityManagement.INTENT_RETURN_VALUE);
				Log.d(LOG_TAG, ">>>>>ADD ACTIVIY RESULTS:\npublished: " + parcel.toString());
			}
			
			if (intent.getAction().equals(CommunityManagement.GET_ACTIVITY_FEED)) {
				//UNMARSHALL THE ACTIVITIES FROM Parcels 
				Parcelable parcels[] =  intent.getParcelableArrayExtra(CommunityManagement.INTENT_RETURN_VALUE);
				for (int i = 0; i < parcels.length; i++) {
					AActivity activity = (AActivity) parcels[i];
					Log.d(LOG_TAG, ">>>>>GET ACTIVIY FEED RESULTS:\npublish: " + activity.getPublished());
				}
			}
		}
	};
}