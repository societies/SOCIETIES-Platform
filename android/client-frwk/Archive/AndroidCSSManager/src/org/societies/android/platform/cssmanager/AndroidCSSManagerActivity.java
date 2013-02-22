package org.societies.android.platform.cssmanager;

import java.util.Arrays;
import java.util.List;

import org.jivesoftware.smack.packet.IQ;
import org.societies.android.api.internal.cssmanager.AndroidCSSNode;
import org.societies.android.api.internal.cssmanager.AndroidCSSRecord;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.datatypes.XMPPInfo;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommCallback;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.internal.css.management.CSSManagerEnums;
import org.societies.api.schema.cssmanagement.CssInterfaceResult;
import org.societies.api.schema.cssmanagement.CssManagerMessageBean;
import org.societies.api.schema.cssmanagement.CssManagerResultBean;
import org.societies.api.schema.cssmanagement.CssNode;
import org.societies.api.schema.cssmanagement.CssRecord;
import org.societies.api.schema.cssmanagement.MethodType;
import org.societies.comm.xmpp.client.impl.ClientCommunicationMgr;
import org.societies.identity.IdentityManagerImpl;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

public class AndroidCSSManagerActivity extends Activity {
	private static final String LOG_TAG = AndroidCSSManagerActivity.class.getName();
    private static final List<String> ELEMENT_NAMES = Arrays.asList("cssManagerMessageBean", "cssManagerResultBean");
    private static final List<String> NAME_SPACES = Arrays.asList(
    		"http://societies.org/api/schema/cssmanagement");
    private static final List<String> PACKAGES = Arrays.asList(
		"org.societies.api.schema.cssmanagement");
    
    
	public static final String TEST_IDENTITY_1 = "node11";
	public static final String TEST_IDENTITY_2 = "node22";

	public static final String TEST_IDENTITY = "android";
	public static final String TEST_INACTIVE_DATE = "20121029";
	public static final String TEST_REGISTERED_DATE = "20120229";
	public static final int TEST_UPTIME = 7799;
	public static final String TEST_EMAIL = "somebody@tssg.org";
	public static final String TEST_FORENAME = "4Name";
	public static final String TEST_HOME_LOCATION = "The Hearth";
	public static final String TEST_IDENTITY_NAME = "Id Name";
	public static final String TEST_IM_ID = "somebody.tssg.org";
	public static final String TEST_NAME = "The CSS";
	public static final String TEST_PASSWORD = "androidpass";
	public static final String TEST_SOCIAL_URI = "sombody@fb.com";



    private static final String DESTINATION = "xcmanager.societies.local";

    private final IIdentity toXCManager;
    private final ICommCallback callback = createCallback();
    private ClientCommunicationMgr ccm;


    public AndroidCSSManagerActivity() {
    	try {
			toXCManager = IdentityManagerImpl.staticfromJid(DESTINATION);
		} catch (InvalidFormatException e) {
			Log.e(LOG_TAG, e.getMessage(), e);
			throw new RuntimeException(e);
		}     
    }

    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "Activity created");
        setContentView(R.layout.main);

        ExampleTask task = new ExampleTask(this); 
        task.execute();
    }
    
    @Override
    public void onDestroy() {
    	super.onDestroy();
    	Log.d(LOG_TAG, "Activity destroyed");
    	ccm.unregister(ELEMENT_NAMES, callback);
    }
    
    private class ExampleTask extends AsyncTask<Void, Void, Void> {

    	private Context context;
    	
    	public ExampleTask(Context context) {
    		this.context = context;
    	}

    	protected Void doInBackground(Void... args) {
    		ccm = new ClientCommunicationMgr(context);
    		
    		CssManagerMessageBean messageBean = new CssManagerMessageBean();
    		messageBean.setProfile(AndroidCSSManagerActivity.this.createAndroidRecord());
    		messageBean.setMethod(MethodType.LOGIN_CSS);

    		Stanza stanza = new Stanza(toXCManager);
    		

            try {
    			ccm.register(ELEMENT_NAMES, callback);
    			ccm.sendIQ(stanza, IQ.Type.GET, messageBean, callback);
    			Log.d(LOG_TAG, "Send stanza");
    		} catch (Exception e) {
    			Log.e(this.getClass().getName(), e.getMessage());
	        }
            return null;
    	}
    }
 
    private AndroidCSSRecord createAndroidRecord() {
    	
    	AndroidCSSNode aCSSNode_1, aCSSNode_2;
    	AndroidCSSNode aCSSNodes [];
    	AndroidCSSNode aCSSArchivedNodes [];

    	aCSSNode_1 = new AndroidCSSNode();
    	aCSSNode_1.setIdentity(TEST_IDENTITY_1);
    	aCSSNode_1.setStatus(CSSManagerEnums.nodeStatus.Available.ordinal());
    	aCSSNode_1.setType(CSSManagerEnums.nodeType.Rich.ordinal());

    	aCSSNode_2 = new AndroidCSSNode();
    	aCSSNode_2.setIdentity(TEST_IDENTITY_2);
    	aCSSNode_2.setStatus(CSSManagerEnums.nodeStatus.Hibernating.ordinal());
    	aCSSNode_2.setType(CSSManagerEnums.nodeType.Android.ordinal());
		
    	aCSSNodes = new AndroidCSSNode[2];
    	aCSSNodes[0] = aCSSNode_1;
    	aCSSNodes[1] = aCSSNode_2;
		
    	aCSSArchivedNodes = new AndroidCSSNode[2];
    	aCSSArchivedNodes[0] = aCSSNode_1;
    	aCSSArchivedNodes[1] = aCSSNode_1;

		AndroidCSSRecord cssProfile = new AndroidCSSRecord();
		
		cssProfile.setCSSNodes(aCSSNodes);
		cssProfile.setArchiveCSSNodes(aCSSArchivedNodes);
		cssProfile.setCssIdentity(TEST_IDENTITY);
		cssProfile.setCssInactivation(TEST_INACTIVE_DATE);
		cssProfile.setCssRegistration(TEST_REGISTERED_DATE);
		cssProfile.setStatus(CSSManagerEnums.cssStatus.Active.ordinal());
		cssProfile.setCssUpTime(TEST_UPTIME);
		cssProfile.setEmailID(TEST_EMAIL);
		cssProfile.setEntity(CSSManagerEnums.entityType.Organisation.ordinal());
		cssProfile.setForeName(TEST_FORENAME);
		cssProfile.setHomeLocation(TEST_HOME_LOCATION);
		cssProfile.setIdentityName(TEST_IDENTITY_NAME);
		cssProfile.setImID(TEST_IM_ID);
		cssProfile.setName(TEST_NAME);
		cssProfile.setPassword(TEST_PASSWORD);
		cssProfile.setPresence(CSSManagerEnums.presenceType.Available.ordinal());
		cssProfile.setSex(CSSManagerEnums.genderType.Unspecified.ordinal());
		cssProfile.setSocialURI(TEST_SOCIAL_URI);
		
		return cssProfile;
		
    }
    
    private CssRecord createRecord() {
    	CssNode cssNode_1, cssNode_2;

		cssNode_1 = new CssNode();
		cssNode_1.setIdentity(TEST_IDENTITY_1);
		cssNode_1.setStatus(CSSManagerEnums.nodeStatus.Available.ordinal());
		cssNode_1.setType(CSSManagerEnums.nodeType.Rich.ordinal());

		cssNode_2 = new CssNode();
		cssNode_2.setIdentity(TEST_IDENTITY_2);
		cssNode_2.setStatus(CSSManagerEnums.nodeStatus.Hibernating.ordinal());
		cssNode_2.setType(CSSManagerEnums.nodeType.Android.ordinal());
		

		CssRecord cssProfile = new CssRecord();
		cssProfile.getCssNodes().add(cssNode_1);
		cssProfile.getCssNodes().add(cssNode_2);
		cssProfile.getArchiveCSSNodes().add(cssNode_1);
		cssProfile.getArchiveCSSNodes().add(cssNode_2);
		
		cssProfile.setCssIdentity(TEST_IDENTITY);
		cssProfile.setCssInactivation(TEST_INACTIVE_DATE);
		cssProfile.setCssRegistration(TEST_REGISTERED_DATE);
		cssProfile.setStatus(CSSManagerEnums.cssStatus.Active.ordinal());
		cssProfile.setCssUpTime(TEST_UPTIME);
		cssProfile.setEmailID(TEST_EMAIL);
		cssProfile.setEntity(CSSManagerEnums.entityType.Organisation.ordinal());
		cssProfile.setForeName(TEST_FORENAME);
		cssProfile.setHomeLocation(TEST_HOME_LOCATION);
		cssProfile.setIdentityName(TEST_IDENTITY_NAME);
		cssProfile.setImID(TEST_IM_ID);
		cssProfile.setName(TEST_NAME);
		cssProfile.setPassword(TEST_PASSWORD);
		cssProfile.setPresence(CSSManagerEnums.presenceType.Available.ordinal());
		cssProfile.setSex(CSSManagerEnums.genderType.Unspecified.ordinal());
		cssProfile.setSocialURI(TEST_SOCIAL_URI);
		return cssProfile;
    }
    
    private ICommCallback createCallback() {
    	return new ICommCallback() {

			public List<String> getXMLNamespaces() {
				return NAME_SPACES;
			}

			public List<String> getJavaPackages() {
				return PACKAGES;
			}

			public void receiveResult(Stanza stanza, Object payload) {
				Log.d(LOG_TAG, "receiveResult");
				Log.d(LOG_TAG, "Payload class of type: " + payload.getClass().getName());
				if (payload instanceof CssManagerResultBean) {
					CssManagerResultBean resultBean = (CssManagerResultBean) payload;
					CssInterfaceResult cssResult = (CssInterfaceResult) resultBean.getResult();
					Log.d(LOG_TAG, "bean result: " + cssResult.isResultStatus());
					Log.d(LOG_TAG, "bean CssRecord: " + cssResult.getProfile().getCssIdentity());
					
				}
				debugStanza(stanza);				
			}

			public void receiveError(Stanza stanza, XMPPError error) {
				Log.d(LOG_TAG, "receiveError");
			}

			public void receiveInfo(Stanza stanza, String node, XMPPInfo info) {
				Log.d(LOG_TAG, "receiveInfo");
			}

			public void receiveMessage(Stanza stanza, Object payload) {
				Log.d(LOG_TAG, "receiveMessage");
				debugStanza(stanza);
				
			}
			
			private void debugStanza(Stanza stanza) {
				Log.d(LOG_TAG, "id="+stanza.getId());
				Log.d(LOG_TAG, "from="+stanza.getFrom());
				Log.d(LOG_TAG, "to="+stanza.getTo());
			}

			public void receiveItems(Stanza stanza, String node, List<String> items) {
				Log.d(LOG_TAG, "receiveItems");
				debugStanza(stanza);
				Log.d(LOG_TAG, "node: "+node);
				Log.d(LOG_TAG, "items:");
				for(String  item:items)
					Log.d(LOG_TAG, item);
			}
		};
    }
     
}
