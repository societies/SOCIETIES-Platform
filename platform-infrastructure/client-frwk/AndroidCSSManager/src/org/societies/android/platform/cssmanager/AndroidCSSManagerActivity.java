package org.societies.android.platform.cssmanager;

import java.util.Arrays;
import java.util.List;

import org.jivesoftware.smack.packet.IQ;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.datatypes.XMPPInfo;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommCallback;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.schema.cssmanagement.CssManagerMessageBean;
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
    		messageBean.setProfile(new CssRecord());
    		messageBean.setMethod(MethodType.REGISTER_XMPP_SERVER);

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
