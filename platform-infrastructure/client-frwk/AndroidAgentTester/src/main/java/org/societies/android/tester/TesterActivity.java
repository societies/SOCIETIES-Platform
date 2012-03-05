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
import org.societies.api.comm.xmpp.datatypes.Identity;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.datatypes.XMPPNode;
import org.societies.api.comm.xmpp.datatypes.XMPPInfo;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommCallback;
import org.societies.comm.xmpp.interfaces.IdentityManager;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TesterActivity extends Activity {

	private static final Logger log = LoggerFactory.getLogger(TesterActivity.class);
	
    private final List<String> elementNames = Arrays.asList("pubsub");
    private final List<String> namespaces = Arrays.asList(
					"http://jabber.org/protocol/pubsub",
			        "http://jabber.org/protocol/pubsub#errors",
			        "http://jabber.org/protocol/pubsub#event",
			        "http://jabber.org/protocol/pubsub#owner");
    private final List<String> packages = Arrays.asList(
					"org.jabber.protocol.pubsub",
					"org.jabber.protocol.pubsub.errors",
					"org.jabber.protocol.pubsub.owner",
					"org.jabber.protocol.pubsub.event");
    private ClientCommunicationMgr ccm = new ClientCommunicationMgr(this);

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
                 
        Identity toXCManager = (new IdentityManager()).fromJid("xcmanager.societies.local");     // TODO
        final ICommCallback callback = createCallback();
        final Stanza stanza = new Stanza((new IdentityManager()).fromJid("psi@societies.local"));	 // TODO	
        final Stanza stanza2 = new Stanza(toXCManager); 
        final Stanza stanza3 = new Stanza(toXCManager); 
        final Stanza stanza4 = new Stanza(toXCManager);
        try {
			Object payload = createPayload();			
			ccm.register(elementNames, callback);
			ccm.sendMessage(stanza, payload);
//			ccm.sendIQ(stanza2, IQ.Type.GET, payload, callback);
			String nodeName = "test3";
			ccm.sendIQ(stanza2, IQ.Type.SET, createNodePayload(nodeName), callback);
			ccm.sendIQ(stanza3, IQ.Type.SET, deleteNodePayload(nodeName), callback);
			ccm.sendIQ(stanza4, IQ.Type.SET, deleteNodePayload(nodeName), callback);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
    }
    
    @Override
    public void onDestroy() {
    	super.onDestroy();
    	ccm.unregister(elementNames, namespaces, packages);
    }
    
    private class ExampleTask extends AsyncTask<Void, Void, Void> {
    	
    	protected Void doInBackground(Void... args) {
    		Identity toXCManager = (new IdentityManager()).fromJid("xcmanager.societies.local");     // TODO
            final ICommCallback callback = createCallback();
            final Stanza stanza = new Stanza((new IdentityManager()).fromJid("psi@societies.local"));	 // TODO	
            final Stanza stanza2 = new Stanza(toXCManager); 
            final Stanza stanza3 = new Stanza(toXCManager); 
            final Stanza stanza4 = new Stanza(toXCManager);
            try {
    			Object payload = createPayload();			
    			ccm.register(elementNames, callback);
    			ccm.sendMessage(stanza, payload);
//    			ccm.sendIQ(stanza2, IQ.Type.GET, payload, callback);
    			String nodeName = "test3";
    			ccm.sendIQ(stanza2, IQ.Type.SET, createNodePayload(nodeName), callback);
    			ccm.sendIQ(stanza3, IQ.Type.SET, deleteNodePayload(nodeName), callback);
    			ccm.sendIQ(stanza4, IQ.Type.SET, deleteNodePayload(nodeName), callback);
    			Dbc.assertion("android@societies.local/default".equals(ccm.getIdentity().getJid()));
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
				if(payload.getClass().equals(Pubsub.class)) {
					Pubsub pubsub = (Pubsub)payload;
					if(pubsub.getSubscriptions() != null) {
						List<Subscription> subscriptions = pubsub.getSubscriptions().getSubscription();
						log.debug("subcriptions=" + Arrays.toString(subscriptions.toArray()));
						for(Subscription sub:subscriptions) {
							log.debug("jid=" + sub.getJid());
							log.debug("node=" + sub.getNode());
							log.debug("subid=" + sub.getSubid());
							log.debug("subscription=" + sub.getSubscription());							
						}
					}
					else
						log.debug("getSubscriptions == null");
				}
				else
					log.debug("not pubsub");
			}

			public void receiveError(Stanza stanza, XMPPError error) {
				log.debug("receiveError");
			}

			public void receiveInfo(Stanza stanza, String node, XMPPInfo info) {
				log.debug("receiveInfo");
			}

			public void receiveMessage(Stanza stanza, Object payload) {
				log.debug("receiveMessage");
				debugStanza(stanza);
				if(payload.getClass().equals(Pubsub.class)) {
					Pubsub pubsub = (Pubsub)payload;
					if(pubsub.getSubscriptions() != null) {
						List<Subscription> subscriptions = pubsub.getSubscriptions().getSubscription();
						log.debug("subcriptions=" + Arrays.toString(subscriptions.toArray()));
						for(Subscription sub:subscriptions) {
							log.debug("jid=" + sub.getJid());
							log.debug("node=" + sub.getNode());
							log.debug("subid=" + sub.getSubid());
							log.debug("subscription=" + sub.getSubscription());							
						}
					}
					else
						log.debug("getSubscriptions == null");
				}
				else
					log.debug("not pubsub");
			}
			
			private void debugStanza(Stanza stanza) {
				log.debug("id="+stanza.getId());
				log.debug("from="+stanza.getFrom());
				log.debug("to="+stanza.getTo());
			}

			public void receiveItems(Stanza arg0, String arg1, List<String> arg2) {
				log.debug("receiveItems");
			}
		};
    }
}

