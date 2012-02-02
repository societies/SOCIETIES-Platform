package org.societies.android;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.jabber.protocol.pubsub.Item;
import org.jabber.protocol.pubsub.Publish;
import org.jabber.protocol.pubsub.Pubsub;
import org.jabber.protocol.pubsub.Subscription;
import org.jabber.protocol.pubsub.Subscriptions;
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
import org.xmpp.packet.IQ;

import android.app.Activity;
import android.os.Bundle;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class PubsubClientActivity extends Activity {

	private static final Logger log = LoggerFactory.getLogger(PubsubClientActivity.class);
	
    private final List<String> elementNames = Arrays.asList("pubsub");
    private final List<String> namespaces = Arrays.asList("http://jabber.org/protocol/pubsub");
    private final List<String> packages = Arrays.asList("jabber.x.data",
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
                  
		Stanza stanza = new Stanza((new IdentityManager()).fromJid("user@host"));	// TODO 	
		Stanza stanza2 = new Stanza((new IdentityManager()).fromJid("pubsub.host"));
        try {
			Object payload = createPayload();			
			ccm.register(elementNames, namespaces, packages);
			ccm.sendMessage(stanza, payload);
			ccm.sendIQ(stanza2, IQ.Type.get, payload, createCallback());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
    }
    
    @Override
    public void onDestroy() {
    	super.onDestroy();
    	ccm.unregister(elementNames, namespaces, packages);
    }
    
    private Object createPayload() throws DOMException, ParserConfigurationException {
    	Subscriptions subscriptions = new Subscriptions();
		Pubsub pubsub = new Pubsub();
		pubsub.setSubscriptions(subscriptions);
		
		return pubsub;
    }
    
    private ICommCallback createCallback() {
    	return new ICommCallback() {

			public List<String> getXMLNamespaces() {
				log.debug("getXMLNamespaces");
				return null;
			}

			public List<String> getJavaPackages() {
				log.debug("getJavaPackages");
				return null;
			}

			public void receiveResult(Stanza stanza, Object payload) {
				log.debug("receiveResult");
				log.debug("id="+stanza.getId());
				log.debug("from="+stanza.getFrom());
				log.debug("to="+stanza.getTo());
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
					log.debug("pubsub == null");
			}

			public void receiveError(Stanza stanza, XMPPError error) {
				log.debug("receiveError");
			}

			public void receiveInfo(Stanza stanza, String node, XMPPInfo info) {
				log.debug("receiveInfo");
			}

			public void receiveItems(Stanza stanza, String node,
					List<XMPPNode> items) {
				log.debug("receiveItems");
			}

			public void receiveMessage(Stanza stanza, Object payload) {
				log.debug("receiveMessage");
			}
		};
    }
}

