package org.societies.integration.test.bit.feedback.privacy;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.pubsub.PubsubClient;
import org.societies.api.comm.xmpp.pubsub.Subscriber;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.internal.schema.useragent.feedback.NegotiationDetailsBean;
import org.societies.api.internal.schema.useragent.feedback.UserFeedbackPrivacyNegotiationEvent;
import org.societies.api.osgi.event.EventTypes;
import org.societies.api.schema.identity.DataIdentifierScheme;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.identity.RequestorServiceBean;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ActionConstants;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Condition;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ConditionConstants;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Decision;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.NegotiationStatus;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestItem;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Resource;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponseItem;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponsePolicy;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;

public class Tester implements Subscriber{

	private PubsubClient pubsub;
	private IIdentityManager idMgr;
	private IIdentity userID;
	private Logger logging = LoggerFactory.getLogger(this.getClass());

	private static final List<String> EVENT_SCHEMA_CLASSES = 
			Collections.unmodifiableList(Arrays.asList(
					"org.societies.api.internal.schema.useragent.feedback.UserFeedbackPrivacyNegotiationEvent"));
	private boolean receivedResult = false;
	
	private ResponsePolicy receivedPolicy;
	public Tester() {
		logging.debug("Tester constructor");
	}
	@Before
	public void setUp(){
		logging.debug("Setting up Tester");
		this.pubsub = TestFeedback.getPubsub();
		this.idMgr = TestFeedback.getIdMgr();
		this.userID = this.idMgr.getThisNetworkNode();
		logging.debug("Finished setting up tester");
	}
	
	@Test
	public void TestPubSubEvent() throws XMPPError, CommunicationException, ClassNotFoundException{
		logging.debug("starting testPubSubEvent");
		
		this.pubsub.addSimpleClasses(EVENT_SCHEMA_CLASSES);
		this.pubsub.ownerCreate(this.userID, EventTypes.UF_PRIVACY_NEGOTIATION);
		this.pubsub.ownerCreate(this.userID, EventTypes.UF_PRIVACY_NEGOTIATION_RESPONSE);
		this.pubsub.subscriberSubscribe(this.userID, EventTypes.UF_PRIVACY_NEGOTIATION, this);
		this.pubsub.subscriberSubscribe(this.userID, EventTypes.UF_PRIVACY_NEGOTIATION_RESPONSE, this);
		UserFeedbackPrivacyNegotiationEvent event = new UserFeedbackPrivacyNegotiationEvent();
		
		NegotiationDetailsBean detailsBean = new NegotiationDetailsBean();
		
		detailsBean.setNegotiationID(1223);
		detailsBean.setRequestor(getRequestorBean());
		event.setNegotiationDetails(detailsBean);
		event.setResponsePolicy(getResponsePolicy());
		this.pubsub.publisherPublish(this.userID, EventTypes.UF_PRIVACY_NEGOTIATION, null, event);
		logging.debug("Finished testPubSubEvent");
		
		while (!receivedResult){
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		Assert.assertNotNull(this.receivedPolicy);
	}
	
	private ResponsePolicy getResponsePolicy() {
		ResponsePolicy responsePolicy = new ResponsePolicy();
		
		
		responsePolicy.setNegotiationStatus(NegotiationStatus.ONGOING);
		responsePolicy.setRequestor(getRequestorBean());
		List<ResponseItem> responseItems = new ArrayList<ResponseItem>();
		
		responseItems.add(this.getResponseItem(CtxAttributeTypes.LOCATION_SYMBOLIC));
		responseItems.add(this.getResponseItem(CtxAttributeTypes.NAME));
		responseItems.add(this.getResponseItem("SomeDataType"));
		responseItems.add(this.getResponseItem("AnotherDataType"));
		responsePolicy.setResponseItems(responseItems);
		
		return responsePolicy;
	}

	private ResponseItem getResponseItem(String dataType) {
		ResponseItem item = new ResponseItem();
		item.setDecision(Decision.PERMIT);
		item.setRequestItem(getRequestItem(dataType));
		
		return item;
	}

	private RequestItem getRequestItem(String dataType) {
		RequestItem item = new RequestItem();
		
		//create some actions
		List<Action> actions = new ArrayList<Action>();
		Action read = new Action();
		read.setActionConstant(ActionConstants.READ);
		Action write = new Action();
		write.setActionConstant(ActionConstants.WRITE);
		Action create = new Action();
		create.setActionConstant(ActionConstants.CREATE);
		
		Action delete = new Action();
		delete.setActionConstant(ActionConstants.DELETE);
		
		//create some conditions
		List<Condition> conditions = new ArrayList<Condition>();
		Condition dataRetentionHours = new Condition();
		dataRetentionHours.setConditionConstant(ConditionConstants.DATA_RETENTION_IN_HOURS);
		dataRetentionHours.setOptional(true);
		dataRetentionHours.setValue("24");
		
		
		Condition rightToOptOut = new Condition();
		rightToOptOut.setConditionConstant(ConditionConstants.RIGHT_TO_OPTOUT);
		rightToOptOut.setOptional(false);
		rightToOptOut.setValue("YES");
		
		Condition shareWith3p = new Condition();
		shareWith3p.setConditionConstant(ConditionConstants.SHARE_WITH_3RD_PARTIES);
		shareWith3p.setOptional(true);
		shareWith3p.setValue("YES");
		
		Condition shareWithCISAdmin = new Condition();
		shareWithCISAdmin.setConditionConstant(ConditionConstants.SHARE_WITH_CIS_OWNER_ONLY);
		shareWithCISAdmin.setOptional(false);
		shareWithCISAdmin.setValue("YES");
		
		Condition shareWithCISmembers = new Condition();
		shareWithCISmembers.setConditionConstant(ConditionConstants.SHARE_WITH_CIS_MEMBERS_ONLY);
		shareWithCISmembers.setOptional(true);
		shareWithCISmembers.setValue("YES");
		
		//create the resource
		
		Resource resource = new Resource();
		resource.setScheme(DataIdentifierScheme.CONTEXT);
		resource.setDataType(dataType);
		
		if(dataType.equalsIgnoreCase(CtxAttributeTypes.LOCATION_SYMBOLIC) || dataType.equalsIgnoreCase(CtxAttributeTypes.NAME)){
			actions.add(read);
			conditions.add(shareWithCISmembers);
			conditions.add(shareWith3p);
			conditions.add(rightToOptOut);
			conditions.add(dataRetentionHours);
		}
		else{
			actions.add(read);
			actions.add(write);
			actions.add(create);
			actions.add(delete);
			conditions.add(shareWithCISmembers);
			conditions.add(shareWith3p);
			conditions.add(rightToOptOut);
		}
		
		item.setActions(actions);

		item.setConditions(conditions);
				
		item.setResource(resource);
		
		
		return item;
	}

	@After
	public void tearDown(){
		
	}
	
	
	private RequestorBean getRequestorBean(){
		ServiceResourceIdentifier myServiceID = new ServiceResourceIdentifier();
		myServiceID.setServiceInstanceIdentifier("css://eliza@societies.org/HelloEarth");
		try {
			myServiceID.setIdentifier(new URI("css://eliza@societies.org/HelloEarth"));
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		RequestorServiceBean bean = new RequestorServiceBean();
		bean.setRequestorServiceId(myServiceID);
		bean.setRequestorId(this.userID.getJid());
		return bean;
		
	}

	@Override
	public void pubsubEvent(IIdentity pubsubService, String node,
			String itemId, Object item) {
		logging.debug("Received pubsub event");
		this.receivedResult = true;
		
		Assert.assertTrue(item instanceof UserFeedbackPrivacyNegotiationEvent);
		UserFeedbackPrivacyNegotiationEvent event = (UserFeedbackPrivacyNegotiationEvent) item;
		this.receivedPolicy = event.getResponsePolicy();
		
	}
}
