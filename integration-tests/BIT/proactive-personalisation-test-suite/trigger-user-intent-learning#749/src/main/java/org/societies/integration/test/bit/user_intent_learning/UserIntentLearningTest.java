package org.societies.integration.test.bit.user_intent_learning;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IdentityType;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.personalisation.IPersonalisationManager;
import org.societies.api.personalisation.model.Action;
import org.societies.api.personalisation.model.IAction;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.api.useragent.monitoring.IUserActionMonitor;


public class UserIntentLearningTest {

	public ICtxBroker internalCtxBroker;

	public IPersonalisationManager persManager;

	public IUserActionMonitor uamMonitor;

	CtxAttribute location = null;
	CtxAttribute symLoc = null;
	CtxAttribute status = null;

	UserIntentLearningTest(ICtxBroker internalCtxBroker,IPersonalisationManager persManager,IUserActionMonitor uamMonitor){
		this.internalCtxBroker = internalCtxBroker;
		this.persManager = persManager;
		this.uamMonitor = uamMonitor;

		setUpContextAttributes();
		
		if(lookupRetrieveAttrHelp("uiModel") == null);	

		mockActionsProducer();

	}

	public void mockActionsProducer(){
		IIdentity identity = new MockIdentity(IdentityType.CSS, "sarah", "societies.org");
		ServiceResourceIdentifier serviceId = new ServiceResourceIdentifier();
		try {
			serviceId.setIdentifier(new URI("testServiceId"));
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		if(lookupRetrieveAttrHelp("CAUI_model") == null) System.out.println("No model created yet");
		if(lookupRetrieveAttrHelp("CRIST_model") == null) System.out.println("No model created yet");
		
		setContextValues("Home", "Free", 25.0);
		IAction action1 = new Action(serviceId, "testService", "volume", "high");
		uamMonitor.monitor(identity, action1);

		setContextValues("Home", "Free", 25.0);
		IAction action2 = new Action(serviceId, "testService", "volume", "high");
		uamMonitor.monitor(identity, action2);

		setContextValues("Home", "Free", 25.0);
		IAction action3 = new Action(serviceId, "testService", "volume", "high");
		uamMonitor.monitor(identity, action3);
		
		//.....
		// after 10 actions user intent models are automatically learned
		
		if(lookupRetrieveAttrHelp("CAUI_model") != null) System.out.println("Model created - test successfull");
		if(lookupRetrieveAttrHelp("CRIST_model") != null) System.out.println("Model created - test successfull");
	}


	



	protected CtxAttribute lookupRetrieveAttrHelp(String type){
		CtxAttribute ctxAttr = null;
		
		try {
			List<CtxIdentifier> tupleAttrList = internalCtxBroker.lookup(CtxModelType.ATTRIBUTE,type).get();
			if(tupleAttrList.size() >0 ){
				CtxIdentifier ctxId = tupleAttrList.get(0);
				ctxAttr =  (CtxAttribute) this.internalCtxBroker.retrieve(ctxId).get();	
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ctxAttr;
	}


	public void setContextValues(String symLocValue, String statusValue, Double tempValue){
		try {
			this.internalCtxBroker.updateAttribute(location.getId(),symLocValue);
			this.internalCtxBroker.updateAttribute(status.getId(), statusValue);
			this.internalCtxBroker.updateAttribute(status.getId(), tempValue);
		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	void setUpContextAttributes(){

	}
}