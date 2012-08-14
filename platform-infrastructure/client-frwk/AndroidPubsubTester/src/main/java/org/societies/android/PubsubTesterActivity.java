package org.societies.android;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

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
        setContentView(R.layout.main);
        
        PubsubClientAndroid pubsubClient = new PubsubClientAndroid(this);
		pubsubClient.addJaxbPackages(packageList);
		
		task = new ExampleTask(); 
		task.execute(pubsubClient);
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
				log.debug("**************pubsubEvent: "+pubsubService.getJid()+" "+node+" "+itemId+" "+item.getClass().getCanonicalName());
				CalcBean calcBean = (CalcBean)item;
				log.debug("A: "+calcBean.getA());
				log.debug("B: "+calcBean.getB());
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}    	
    };
    
    private static class ExampleTask extends AsyncTask<PubsubClientAndroid, Void, Void> {
    	
    	protected Void doInBackground(PubsubClientAndroid... args) {
    		PubsubClientAndroid pubsubClient = args[0];	    	
	        try {
	        	IIdentity pubsubService = IdentityManagerImpl.staticfromJid("xcmanager.societies.local");
	        	CalcBean item = new CalcBean();
	        	item.setA(1);
	        	item.setB(2);
	        	item.setMessage("testBean");
	        	
	        	final String nodeName = "test3"; 
				pubsubClient.ownerCreate(pubsubService, nodeName);
				List<String> items = pubsubClient.discoItems(pubsubService, null);
				for(String i:items)
					log.debug("DiscoItem: "+i);
				pubsubClient.subscriberSubscribe(pubsubService, nodeName, subscriber);
	        	String itemId = pubsubClient.publisherPublish(pubsubService, nodeName, UUID.randomUUID().toString(), item);
	        	log.debug("ID: "+itemId);
	        	try {
	        		Thread.sleep(1000);
	        	} catch(InterruptedException e) {}
	        	pubsubClient.publisherDelete(pubsubService, nodeName, itemId);
	        	pubsubClient.ownerPurgeItems(pubsubService, nodeName);
	        	pubsubClient.subscriberUnsubscribe(pubsubService, nodeName, subscriber);
				pubsubClient.ownerDelete(pubsubService, nodeName);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
	        return null;
    	}
    }
}

