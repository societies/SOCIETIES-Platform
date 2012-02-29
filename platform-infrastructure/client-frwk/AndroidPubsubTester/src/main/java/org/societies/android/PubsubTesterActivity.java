package org.societies.android;

import java.util.UUID;

import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.datatypes.Identity;
import org.societies.comm.android.ipc.utils.MarshallUtils;
import org.societies.comm.xmpp.client.impl.PubsubClientAndroid;
import org.societies.comm.xmpp.interfaces.IdentityManager;
import org.societies.comm.xmpp.pubsub.Subscriber;
import org.w3c.dom.Element;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;

public class PubsubTesterActivity extends Activity {

	private static final Logger log = LoggerFactory.getLogger(PubsubTesterActivity.class);
    ExampleTask task;

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
        
        PubsubClientAndroid pubsubClient = new PubsubClientAndroid(this);
        
        task = new ExampleTask(); 
        task.execute(pubsubClient);
    }
    
    @Override
    public void onDestroy() {
    	super.onDestroy();
    	task.cancel(true);
    }
     
    private static Subscriber subscriber = new Subscriber() {
		public void pubsubEvent(Identity pubsubService, String node,
				String itemId, Element item) {
			try {
				log.debug("**************pubsubEvent: "+pubsubService.getJid()+" "+node+" "+itemId+" "+MarshallUtils.nodeToString(item));
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}    	
    };
    
    private static class ExampleTask extends AsyncTask<PubsubClientAndroid, Void, Void> {
    	
    	protected Void doInBackground(PubsubClientAndroid... args) {
    		PubsubClientAndroid pubsubClient = args[0];
	    	Identity pubsubService = (new IdentityManager()).fromJid("xcmanager.societies.local");
	        try {
				Element item = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument().createElement("test");				
	        	final String nodeName = "test3"; 
				pubsubClient.ownerCreate(pubsubService, nodeName);
				pubsubClient.subscriberSubscribe(pubsubService, nodeName, subscriber);
	        	String id = pubsubClient.publisherPublish(pubsubService, nodeName, UUID.randomUUID().toString(), item);
	        	log.debug("ID: "+id);
				pubsubClient.ownerDelete(pubsubService, nodeName);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
	        return null;
    	}
    }
}

