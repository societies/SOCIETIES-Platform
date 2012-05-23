package org.societies.integration.test.bit.caui_prediction;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IdentityType;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.personalisation.IPersonalisationManager;
import org.societies.api.personalisation.model.Action;
import org.societies.api.personalisation.model.IAction;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.api.useragent.monitoring.IUserActionMonitor;


public class UserIntentLearningTest {

	public ICtxBroker ctxBroker;

	public IUserActionMonitor uam;

	//CtxAttribute location = null;
	//CtxAttribute symLoc = null;
	//CtxAttribute status = null;

	UserIntentLearningTest(ICtxBroker ctxBroker,IUserActionMonitor uam){
		this.ctxBroker = ctxBroker;
		this.uam = uam;
}

	void createHistorySet(){

		IndividualCtxEntity operator = null;

		IIdentity identity = new MockIdentity(IdentityType.CSS, "user", "societies.org");

		ServiceResourceIdentifier serviceId = new ServiceResourceIdentifier();
		try {
			serviceId.setIdentifier(new URI("testServiceId"));
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		try {
			operator = ctxBroker.retrieveCssOperator().get();

			// primary attribute
			CtxAttribute actAttr = ctxBroker.createAttribute(operator.getId(), CtxAttributeTypes.ACTION).get();
			actAttr.setHistoryRecorded(true);
			actAttr = (CtxAttribute) ctxBroker.update(actAttr).get();

			//escorting attributes
			CtxAttribute statusAttr = ctxBroker.createAttribute(operator.getId(), CtxAttributeTypes.STATUS).get();
			statusAttr.setHistoryRecorded(true);
			statusAttr =  ctxBroker.updateAttribute(statusAttr.getId(),(Serializable)"free").get();

			CtxAttribute tempAttr = ctxBroker.createAttribute(operator.getId(), CtxAttributeTypes.TEMPERATURE).get();
			tempAttr.setHistoryRecorded(true);
			tempAttr =  ctxBroker.updateAttribute(tempAttr.getId(),(Serializable)12).get();

			CtxAttribute symLocAttr = ctxBroker.createAttribute(operator.getId(), CtxAttributeTypes.LOCATION_SYMBOLIC).get();
			symLocAttr.setHistoryRecorded(true);
			symLocAttr =  ctxBroker.updateAttribute(symLocAttr.getId(),(Serializable)"home").get();
			/*
			// set history tuples
			List<CtxAttributeIdentifier> listOfEscortingAttributeIds = new ArrayList<CtxAttributeIdentifier>();
			listOfEscortingAttributeIds.add(statusAttr.getId());
			listOfEscortingAttributeIds.add(tempAttr.getId());
			listOfEscortingAttributeIds.add(symLocAttr.getId());
			ctxBroker.setHistoryTuples(actAttr.getId(), listOfEscortingAttributeIds).get();	
			 */
			for(int i=0; i<3; i++){
				//primary attribute value
				IAction action1 = new Action(serviceId, "testService", "volume", "high");
				symLocAttr =  ctxBroker.updateAttribute(symLocAttr.getId(),(Serializable)"office").get();
				uam.monitor(identity, action1);
				/*
			byte[] binaryAction1  = SerialisationHelper.serialise(action1);
			actAttr.setBinaryValue(binaryAction1);
			actAttr = (CtxAttribute) ctxBroker.update(actAttr).get();
				 */
				IAction action2 = new Action(serviceId, "testService", "volume", "low");
				symLocAttr =  ctxBroker.updateAttribute(symLocAttr.getId(),(Serializable)"restaurant").get();
				uam.monitor(identity, action2);
				/*
			byte[] binaryAction2 = SerialisationHelper.serialise(action2);
			actAttr.setBinaryValue(binaryAction2);
			actAttr = (CtxAttribute) ctxBroker.update(actAttr).get();
				 */
				IAction action3 = new Action(serviceId, "testService", "volume", "mute");
				symLocAttr =  ctxBroker.updateAttribute(symLocAttr.getId(),(Serializable)"home").get();
				uam.monitor(identity, action2);
				/*
			byte[] binaryAction3 = SerialisationHelper.serialise(action3);
			actAttr.setBinaryValue(binaryAction3);
			actAttr = (CtxAttribute) ctxBroker.update(actAttr).get();
				 */
			}

			//	Map<CtxHistoryAttribute, List<CtxHistoryAttribute>> tupleResults = ctxBroker.retrieveHistoryTuples(actAttr.getId(), listOfEscortingAttributeIds, null, null).get();

			//	System.out.println("hoc tuple results size "+tupleResults.size());
			//	System.out.println("hoc tuple results "+tupleResults);

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
	}



/*
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

/*
	public void setContextValues(String symLocValue, String statusValue, Double tempValue){
		try {
			this.ctxBroker.updateAttribute(location.getId(),symLocValue);
			this.ctxBroker.updateAttribute(status.getId(), statusValue);
			this.ctxBroker.updateAttribute(status.getId(), tempValue);
		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	void setUpContextAttributes(){

	}
*/
}