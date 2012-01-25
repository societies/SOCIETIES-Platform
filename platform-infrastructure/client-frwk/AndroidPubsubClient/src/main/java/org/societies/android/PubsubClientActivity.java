package org.societies.android;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.jabber.protocol.pubsub.Item;
import org.jabber.protocol.pubsub.Publish;
import org.jabber.protocol.pubsub.Pubsub;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.comm.xmpp.client.impl.ClientCommunicationMgr;
import org.societies.comm.xmpp.datatypes.Identity;
import org.societies.comm.xmpp.datatypes.Stanza;
import org.societies.comm.xmpp.interfaces.CommCallback;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.xmpp.packet.IQ;

import android.app.Activity;
import android.os.Bundle;
import java.io.Serializable;

public class PubsubClientActivity extends Activity {

	private static final Logger log = LoggerFactory.getLogger(PubsubClientActivity.class);

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
             
        Identity to = Identity.fromJid("user@host");
		Stanza stanza = new Stanza(to);		
        try {
			Object payload = createPayload();
			ClientCommunicationMgr ccm = new ClientCommunicationMgr(this);
			ccm.sendMessage(stanza, payload);
			ccm.sendIQ(stanza, IQ.Type.set, payload, createCallback());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
    }
    
    @Override
    public void onDestroy() {
    	super.onDestroy();
    	// TODO unbound service?
    }
    
    private Object createPayload() throws DOMException, ParserConfigurationException {
    	Element content = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument().createElement("test");   	
		
		Item item = new Item();
		item.setAny(content);
		Publish publish = new Publish();
		publish.setNode("testNode");
		publish.setItem(item);
		Pubsub pubsub = new Pubsub();
		pubsub.setPublish(publish);
		
		return pubsub;
    }
    
    private CommCallback createCallback() {
    	return new CommCallback() {
			
			@Override
			public void receiveError(Stanza stanza, Object payload) {
				log.debug("receiveError");
			}

			@Override
			public void receiveResult(Stanza stanza, Object payload) {
				log.debug("receiveResult");
			}
		};
    }
}

