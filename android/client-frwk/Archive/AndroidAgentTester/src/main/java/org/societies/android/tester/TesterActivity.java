package org.societies.android.tester;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.jabber.protocol.pubsub.Create;
import org.jabber.protocol.pubsub.Item;
import org.jabber.protocol.pubsub.Publish;
import org.jabber.protocol.pubsub.Pubsub;
import org.jabber.protocol.pubsub.Subscription;
import org.jabber.protocol.pubsub.Subscriptions;
import org.jabber.protocol.pubsub.owner.Delete;
import org.jivesoftware.smack.packet.IQ;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.comm.xmpp.client.impl.ClientCommunicationMgr;
import org.societies.identity.IdentityManagerImpl;
import org.societies.utilities.DBC.Dbc;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.datatypes.XMPPNode;
import org.societies.api.comm.xmpp.datatypes.XMPPInfo;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommCallback;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.InvalidFormatException;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TesterActivity extends Activity {

	private static final Logger log = LoggerFactory.getLogger(TesterActivity.class);
	
    private final List<String> elementNames = Arrays.asList("pubsub", "event", "query");
    private final List<String> namespaces = Arrays.asList(
					"http://jabber.org/protocol/pubsub",
			        "http://jabber.org/protocol/pubsub#errors",
			        "http://jabber.org/protocol/pubsub#event",
			        "http://jabber.org/protocol/pubsub#owner",
			        "http://jabber.org/protocol/disco#items");
    private final List<String> packages = Arrays.asList(
					"org.jabber.protocol.pubsub",
					"org.jabber.protocol.pubsub.errors",
					"org.jabber.protocol.pubsub.owner",
					"org.jabber.protocol.pubsub.event");
    private ClientCommunicationMgr ccm;
    private final IIdentity toXCManager;
    private final ICommCallback callback = createCallback();

    public TesterActivity() {
    	try {
			toXCManager = IdentityManagerImpl.staticfromJid("xcmanager.societies.local");
		} catch (InvalidFormatException e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}     
    }
    
    /**
     * Called when the activity is first created.
     * @param savedInstanceState If the activity is being re-initialized after 
     * previously being shut down then this Bundle contains the data it most 
     * recently supplied in onSaveInstanceState(Bundle). <b>Note: Otherwise it is null.</b>
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		log.debug("onCreate");
        setContentView(R.layout.main);
        
        ExampleTask task = new ExampleTask(this);
        task.execute();
    }
    
    @Override
    public void onDestroy() {
    	super.onDestroy();
    	ccm.unregister(elementNames, callback);
    }
    
    private class ExampleTask extends AsyncTask<Void, Void, Void> {
    	
    	private Context context;
    	
    	public ExampleTask(Context context) {
    		this.context = context;
    	}
    	
    	protected Void doInBackground(Void... args) {
        	ccm = new ClientCommunicationMgr(context);
            try {            	
    			Object payload = createPayload();		
    			ccm.getIdManager();
    			ccm.register(elementNames, callback);
    			ccm.sendMessage(new Stanza(IdentityManagerImpl.staticfromJid("psi@societies.local")), payload);
    			String nodeName = "test3";
    			ccm.sendIQ(new Stanza(toXCManager), IQ.Type.SET, createNodePayload(nodeName), callback);
    			ccm.sendIQ(new Stanza(toXCManager), IQ.Type.SET, deleteNodePayload(nodeName), callback);
    			ccm.sendIQ(new Stanza(toXCManager), IQ.Type.SET, deleteNodePayload(nodeName), callback);
    			Dbc.assertion("android@societies.local/default".equals(ccm.getIdentity().getJid()));
    			testGetItems();
    		} catch (Exception e) {
    			log.error(e.getMessage(), e);
    		}
            return null;
    	}
    }
    
    private Object createPayload() throws DOMException, ParserConfigurationException {
    	Subscriptions subscriptions = new Subscriptions();
		Pubsub pubsub = new Pubsub();
		pubsub.setSubscriptions(subscriptions);
		
		return pubsub;
    }
    
    private Object createNodePayload(String nodeName) {
    	Create create = new Create();
    	create.setNode(nodeName);
    	Pubsub pubsub = new Pubsub();
    	pubsub.setCreate(create);
    	return pubsub;
    }
    
    private Object deleteNodePayload(String nodeName) {
    	Delete delete = new Delete();
    	delete.setNode(nodeName);
    	org.jabber.protocol.pubsub.owner.Pubsub pubsub = new org.jabber.protocol.pubsub.owner.Pubsub();
    	pubsub.setDelete(delete);
    	return pubsub;
    }
    
    private ICommCallback createCallback() {
    	return new ICommCallback() {

			public List<String> getXMLNamespaces() {
				return namespaces;
			}

			public List<String> getJavaPackages() {
				return packages;
			}

			public void receiveResult(Stanza stanza, Object payload) {
				log.debug("receiveResult");
				debugStanza(stanza);				
			}

			public void receiveError(Stanza stanza, XMPPError error) {
				log.debug("receiveError: "+error.getStanzaErrorString());
				debugStanza(stanza);				
			}

			public void receiveInfo(Stanza stanza, String node, XMPPInfo info) {
				log.debug("receiveInfo");
			}

			public void receiveMessage(Stanza stanza, Object payload) {
				log.debug("receiveMessage");
				debugStanza(stanza);
			}
			
			private void debugStanza(Stanza stanza) {
				log.debug("id="+stanza.getId());
				log.debug("from="+stanza.getFrom());
				log.debug("to="+stanza.getTo());
			}

			public void receiveItems(Stanza stanza, String node, List<String> items) {
				log.debug("receiveItems");
				debugStanza(stanza);
				log.debug("node: "+node);
				log.debug("items:");
				for(String  item:items)
					log.debug(item);
			}
		};
    }
    
    private void testGetItems() throws Exception {
    	log.debug("getItems");
    	String id = ccm.getItems(toXCManager, "", callback);
    	log.debug("id: "+id);    	    	
    }
}

