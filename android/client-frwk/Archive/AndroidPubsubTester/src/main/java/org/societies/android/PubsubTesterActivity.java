package org.societies.android;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.UUID;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.comm.android.ipc.utils.MarshallUtils;
import org.societies.comm.xmpp.client.impl.PubsubClientAndroid;
import org.societies.identity.IdentityManagerImpl;
import org.societies.test.model.AddDirectEvidenceRequestBean;
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

	private static final String LOG_TAG = PubsubTesterActivity.class.getName();
    
	private static final List<String> classList = Collections.singletonList("org.societies.api.schema.examples.calculatorbean.CalcBean");
	
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
		Log.d(LOG_TAG,"onCreate");
        setContentView(R.layout.main);
        
        PubsubClientAndroid pubsubClient = new PubsubClientAndroid(this);
        try {
        	pubsubClient.addSimpleClasses(classList);
        } catch (ClassNotFoundException e) {
        	Log.e(LOG_TAG,"ClassNotFoundException loading "+Arrays.toString(classList.toArray()), e);
        }
		
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
				Log.d(LOG_TAG,"**************pubsubEvent: "+pubsubService.getJid()+" "+node+" "+itemId+" "+item.getClass().getCanonicalName());
				if (item instanceof CalcBean) {
					CalcBean calcBean = (CalcBean)item;
					Log.d(LOG_TAG,"A: "+calcBean.getA());
					Log.d(LOG_TAG,"B: "+calcBean.getB());
				} else if (item instanceof AddDirectEvidenceRequestBean) {
					AddDirectEvidenceRequestBean b = (AddDirectEvidenceRequestBean) item;
					Log.d(LOG_TAG,b.getTimestamp().toXMLFormat());
				} else {
					Log.w(LOG_TAG,"item is not an instance of CalcBean nor AddDirectEvidenceRequestBean");
				}
			} catch (Exception e) {
				Log.e(LOG_TAG,e.getMessage(), e);
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
					Log.d(LOG_TAG,"DiscoItem: "+i);
				pubsubClient.subscriberSubscribe(pubsubService, nodeName, subscriber);
	        	
				// publish calcBean
				String itemId = pubsubClient.publisherPublish(pubsubService, nodeName, UUID.randomUUID().toString(), item);
				Log.d(LOG_TAG,"Calcbean ID: "+itemId);
	        	try {
	        		Thread.sleep(1000);
	        	} catch(InterruptedException e) {}
	        	
	        	// publish AddDirectEvidenceRequestBean
//	        	AddDirectEvidenceRequestBean item2 = new AddDirectEvidenceRequestBean();
//	        	DatatypeFactory df = DatatypeFactory.newInstance();
//				XMLGregorianCalendar cal = df.newXMLGregorianCalendar();
//				Log.d(LOG_TAG,"cal.getClass()="+cal.getClass());
//	        	GregorianCalendar gc = new GregorianCalendar();
//	        	gc.setTime(new Date());
//				cal = df.newXMLGregorianCalendar(gc);
//	        	item2.setTimestamp(cal);
//				String itemId2 = pubsubClient.publisherPublish(pubsubService, nodeName, UUID.randomUUID().toString(), item2);
//				Log.d(LOG_TAG,"AddDirectEvidenceRequestBean ID: "+itemId2);
//	        	try {
//	        		Thread.sleep(1000);
//	        	} catch(InterruptedException e) {}
	        	
	        	pubsubClient.publisherDelete(pubsubService, nodeName, itemId);
//	        	pubsubClient.publisherDelete(pubsubService, nodeName, itemId2);
	        	pubsubClient.ownerPurgeItems(pubsubService, nodeName);
	        	pubsubClient.subscriberUnsubscribe(pubsubService, nodeName, subscriber);
				pubsubClient.ownerDelete(pubsubService, nodeName);
			} catch (Exception e) {
				Log.e(LOG_TAG,e.getMessage(), e);
			}
	        return null;
    	}
    }
}

