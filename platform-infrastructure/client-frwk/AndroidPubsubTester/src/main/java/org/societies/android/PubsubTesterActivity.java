package org.societies.android;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.comm.android.ipc.utils.MarshallUtils;
import org.societies.comm.xmpp.client.impl.PubsubClientAndroid;
import org.societies.identity.IdentityManagerImpl;
import org.societies.api.comm.xmpp.pubsub.Subscriber;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.w3c.dom.Element;
import org.societies.api.schema.examples.calculatorbean.CalcBean;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

public class PubsubTesterActivity extends Activity {

	private static final Logger log = LoggerFactory.getLogger(PubsubTesterActivity.class);
    
	private static final List<String> packageList = Collections.singletonList("org.societies.api.schema.examples.calculatorbean");
	
	private ExampleTask task;

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
		Log.d(this.getClass().getName(), "onCreate");
        setContentView(R.layout.main);
        
        PubsubClientAndroid pubsubClient = new PubsubClientAndroid(this);
        try {
			pubsubClient.addJaxbPackages(packageList);
			
			task = new ExampleTask(); 
			task.execute(pubsubClient);
			
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
    }
    
    @Override
    public void onDestroy() {
    	super.onDestroy();
    	task.cancel(true);
    }
     
    private static Subscriber subscriber = new Subscriber() {
		public void pubsubEvent(IIdentity pubsubService, String node,
				String itemId, Object item) {
			try {
				Log.d(this.getClass().getName(), "**************pubsubEvent: "+pubsubService.getJid()+" "+node+" "+itemId+" "+item.getClass().getCanonicalName());
				CalcBean calcBean = (CalcBean)item;
				Log.d(this.getClass().getName(), "A: "+calcBean.getA());
				Log.d(this.getClass().getName(), "B: "+calcBean.getB());
			} catch (Exception e) {
				Log.d(this.getClass().getName(), e.getMessage(), e);
			}
		}    	
    };
    
    private static class ExampleTask extends AsyncTask<PubsubClientAndroid, Void, Void> {
    	
    	protected Void doInBackground(PubsubClientAndroid... args) {
    		PubsubClientAndroid pubsubClient = args[0];	    	
	        try {
	        	IIdentity pubsubService = IdentityManagerImpl.staticfromJid("john.societies.local");
	        	CalcBean item = new CalcBean();
	        	item.setA(1);
	        	item.setB(2);
	        	item.setMessage("Testing");
	        	
	        	final String nodeName = "test3"; 
	        	Log.d(this.getClass().getName(), "### Creating pubsub node");
	        	try {
	        		pubsubClient.ownerCreate(pubsubService, nodeName);
	        	} catch (Exception ex) {
	        		Log.d(this.getClass().getName(), "Node already exists");
	        	}
	        	Log.d(this.getClass().getName(), "### Listing pubsub nodes");
	        	List<String> items = pubsubClient.discoItems(pubsubService, null);
				for(String i:items)
					Log.d(this.getClass().getName(), "DiscoItem: "+i);
				Log.d(this.getClass().getName(), "### Subscribing to pubsub node");
				pubsubClient.subscriberSubscribe(pubsubService, nodeName, subscriber);
				Log.d(this.getClass().getName(), "### Publishing to pubsub node");
	        	String itemId = pubsubClient.publisherPublish(pubsubService, nodeName, UUID.randomUUID().toString(), item);
	        	Log.d(this.getClass().getName(), "### Published ID: "+itemId);
	        	try {
	        		Thread.sleep(10 * 1000);
	        	} catch(InterruptedException e) {}
	        	Log.d(this.getClass().getName(), "### Deleting event");
	        	pubsubClient.publisherDelete(pubsubService, nodeName, itemId);
	        	Log.d(this.getClass().getName(), "### Purging node");
	        	pubsubClient.ownerPurgeItems(pubsubService, nodeName);
	        	Log.d(this.getClass().getName(), "### Unsubscribing from pubsub node");
	        	pubsubClient.subscriberUnsubscribe(pubsubService, nodeName, subscriber);
	        	Log.d(this.getClass().getName(), "### Deleting pubsub node");
				pubsubClient.ownerDelete(pubsubService, nodeName);
				Log.d(this.getClass().getName(), "### Listing pubsub nodes again");
	        	items = pubsubClient.discoItems(pubsubService, null);
	        	Log.d(this.getClass().getName(), "Pubsub nodes count: " + items.size());
				for(String i:items)
					Log.d(this.getClass().getName(), "DiscoItem: "+i);
			} catch (Exception e) {
				Log.d(this.getClass().getName(), e.getMessage(), e);
			}
	        return null;
    	}
    }
}

