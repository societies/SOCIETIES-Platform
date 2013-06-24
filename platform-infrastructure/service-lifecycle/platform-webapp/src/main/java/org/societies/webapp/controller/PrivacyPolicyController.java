package org.societies.webapp.controller;
/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAÇÃO, SA (PTIN), IBM Corp., 
 * INSTITUT TELECOM (ITSUD), AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA PERIORISMENIS EFTHINIS (AMITEC), TELECOM 
 * ITALIA S.p.a.(TI),  TRIALOG (TRIALOG), Stiftelsen SINTEF (SINTEF), NEC EUROPE LTD (NEC))
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
 * conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
 *    disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.cis.management.ICis;
import org.societies.api.cis.management.ICisManager;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.context.model.MalformedCtxIdentifierException;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.identity.RequestorCis;
import org.societies.api.identity.RequestorService;
import org.societies.api.internal.privacytrust.privacyprotection.IPrivacyPolicyManager;
import org.societies.api.internal.privacytrust.privacyprotection.model.listener.IPrivacyPolicyManagerListener;
import org.societies.api.internal.privacytrust.privacyprotection.remote.IPrivacyPolicyManagerRemote;
import org.societies.api.internal.servicelifecycle.IServiceDiscovery;
import org.societies.api.internal.servicelifecycle.ServiceDiscoveryException;
import org.societies.api.internal.servicelifecycle.ServiceModelUtils;
import org.societies.api.privacytrust.privacy.model.PrivacyException;
import org.societies.api.privacytrust.privacy.model.privacypolicy.Action;
import org.societies.api.privacytrust.privacy.model.privacypolicy.Condition;
import org.societies.api.privacytrust.privacy.model.privacypolicy.RequestItem;
import org.societies.api.privacytrust.privacy.model.privacypolicy.RequestPolicy;
import org.societies.api.privacytrust.privacy.model.privacypolicy.Resource;
import org.societies.api.privacytrust.privacy.model.privacypolicy.constants.ActionConstants;
import org.societies.api.privacytrust.privacy.model.privacypolicy.constants.ConditionConstants;
import org.societies.api.privacytrust.privacy.util.privacypolicy.RequestPolicyUtils;
import org.societies.api.schema.identity.DataIdentifierScheme;
import org.societies.api.schema.servicelifecycle.model.Service;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.webapp.models.PrivacyPolicyForm;
import org.societies.webapp.models.privacy.PrivacyPolicyCriteriaForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;


@Controller
public class PrivacyPolicyController {

	private static Logger LOG = LoggerFactory.getLogger(PrivacyPolicyController.class);

	private static String[] resourceList;
	private static String[] resourceHumanList;
	private static String[] resourceSchemeList;

	/**
	 * OSGI service get auto injected
	 */
	@Autowired(required=false)
	private IPrivacyPolicyManager privacyPolicyManager;
	@Autowired(required=false)
	private IPrivacyPolicyManagerRemote privacyPolicyManagerRemote;
	@Autowired(required=false)
	private ICommManager commMngrRef;
	@Autowired(required=false)
	private ICisManager cisManager;
	@Autowired(required=false)
	private IServiceDiscovery serviceDiscovery;

	@RequestMapping(value = "/privacy-policies.html", method = RequestMethod.GET)
	public ModelAndView indexAction() {
		LOG.debug("privacy policy index HTTP GET");

		Map<String, Object> model = new HashMap<String, Object>();
		model.put("privacyPolicyCriteria", new PrivacyPolicyCriteriaForm());
		return new ModelAndView("privacy/privacy-policy/index", model);
	}

	@RequestMapping(value = "/privacy-policies.html", method = RequestMethod.POST)
	public ModelAndView indexAction(@Valid PrivacyPolicyCriteriaForm privacyPolicyCriteria, BindingResult result, Map model) {
		LOG.debug("privacy policy update HTTP POST");

		// -- Verification
		if (result.hasErrors()) {
			LOG.warn("BindingResult has errors");
			model.put("errormsg", "Privacy policy criteria form error<br />"+result.toString()+"<br />"+result.getFieldErrors().get(0).getObjectName());
			return new ModelAndView("error", model);
		}
		LOG.info("[indexAction] "+privacyPolicyCriteria.toString());

		// -- Retrieve the CIS owner Id
		boolean allowRemoteCall = true;
		String ownerId = privacyPolicyCriteria.getOwnerId();
		if ("local".equals(privacyPolicyCriteria.getCisLocation())) {
			allowRemoteCall = true;
			ownerId = commMngrRef.getIdManager().getThisNetworkNode().getJid();
			privacyPolicyCriteria.setOwnerId(ownerId);
			LOG.info("[indexAction local] "+privacyPolicyCriteria.toString());
		}
		return showCisAction(privacyPolicyCriteria.getOwnerId(), privacyPolicyCriteria.getCisId(), false, allowRemoteCall);
	}


	@RequestMapping(value = "/cis-privacy-policy-show.html", method = RequestMethod.GET)
	public ModelAndView showCisAction(@RequestParam(value="cisOwnerId", required=false) String cisOwnerId,
			@RequestParam(value="cisId", required=true) String cisId,
			@RequestParam(value="test", required=false, defaultValue="false") boolean test,
			@RequestParam(value="allowRemoteCall", required=false, defaultValue="true") boolean allowRemoteCall) {
		LOG.debug("Show CIS privacy policy: "+cisId+" "+cisOwnerId);
		StringBuffer infoMsg = new StringBuffer();
		StringBuffer errorMsg = new StringBuffer();

		// -- Retrieve the privacy policy
		RequestorCis provider = null;
		RequestPolicy privacyPolicy = null;
		IIdentity cisOwnerIdentity = null;
		ICis cis = null;
		try {
			cisOwnerIdentity = commMngrRef.getIdManager().fromJid(cisOwnerId);
			IIdentity cisIdentity = commMngrRef.getIdManager().fromJid(cisId);
			provider = new RequestorCis(cisOwnerIdentity, cisIdentity);
			privacyPolicy = privacyPolicyManager.getPrivacyPolicy(provider);
			cis = cisManager.getCis(cisId);
		} catch (PrivacyException e) {
			LOG.error("Can't retrieve the privacy policy of this CIS", e);
		} catch (InvalidFormatException e) {
			LOG.error("Can't retrieve parameters of this CIS", e);
		}

		// -- Display the privacy policy
		// - No privacy policy
		if (null == privacyPolicy) {
			LOG.error("The CIS privacy policy of "+provider+" can not be retrieved. It doesn't exist on the local node, or identifiers are incorrect.");
			// - Try remote call
			if (allowRemoteCall) {
				PrivacyPolicyManagerRemoteListener listener = new PrivacyPolicyManagerRemoteListener();
				try {
					if (null == privacyPolicyManagerRemote) {
						LOG.error("privacyPolicyManagerRemote not ready!");
						throw new PrivacyException("privacyPolicyManagerRemote not ready!");
					}
					privacyPolicyManagerRemote.getPrivacyPolicy(provider, cisOwnerIdentity, listener);
					synchronized(listener) {
						listener.wait(10000);
						if (listener.ack) {
							privacyPolicy = listener.privacyPolicy;
						}
					}
				} catch (PrivacyException e) {
					LOG.error("Can't retrieve the remote CIS privacy policy", e);
					errorMsg.append("Can't retrieve the remote CIS privacy policy");
				} catch (InterruptedException e) {
					LOG.error("Timeout: Can't retrieve the remote CIS privacy policy", e);
					errorMsg.append("Timeout: Can't retrieve the remote CIS privacy policy");
				}
			}
			if (null == privacyPolicy) {
				LOG.error("The CIS privacy policy of "+provider+" can not be retrieved. It doesn't exist on local and remote node, or identifiers are incorrect.");
				errorMsg.append("Can't retrieve the privacy policy of this CIS.");
			}
			// - Create an example one for testing purpose
			if (null == privacyPolicy && test) {
				LOG.error("Let's create one");
				errorMsg.append("\nFor testing purpose: lets create one.");
				// -- Create a privacy policy
				List<RequestItem> requests = createTestPrivacyPolicy();
				privacyPolicy = new RequestPolicy(provider, requests);
			}
		}
		else {
			LOG.debug(privacyPolicy.toXMLString());
		}

		LOG.error(errorMsg.toString());
		LOG.info(infoMsg.toString());

		Map<String, Object> model = new HashMap<String, Object>();
		model.put("error", errorMsg.toString());
		model.put("info", infoMsg.toString());
		model.put("PrivacyPolicy", privacyPolicy);
		model.put("Cis", cis);
		model.put("Element", "CIS");
		return new ModelAndView("privacy/privacy-policy/show-cis", model);
	}

	@RequestMapping(value = "/service-privacy-policy-show.html", method = RequestMethod.GET)
	public ModelAndView showServiceAction(@RequestParam(value="serviceOwnerId", required=false) String serviceOwnerId,
			@RequestParam(value="serviceId", required=true) String serviceId,
			@RequestParam(value="test", required=false, defaultValue="false") boolean test) {
		LOG.debug("Show 3P service privacy policy: "+serviceId+" "+serviceOwnerId);
		StringBuffer infoMsg = new StringBuffer();
		StringBuffer errorMsg = new StringBuffer();

		// -- Retrieve the privacy policy
		RequestorService provider = null;
		RequestPolicy privacyPolicy = null;
		Service service = null;
		//Service service = ServiceModelUtils.getServiceFromServiceInstance(serviceId, serviceDiscovery);
		//service.setServiceName(serviceId);
		try {
			ServiceResourceIdentifier serviceIdentity = ServiceModelUtils.getServiceId64Decode(serviceId);
			IIdentity serviceOwnerIdentity  = commMngrRef.getIdManager().fromJid(ServiceModelUtils.getJidFromServiceIdentifier(serviceIdentity));

			provider = new RequestorService(serviceOwnerIdentity, serviceIdentity);
			privacyPolicy = privacyPolicyManager.getPrivacyPolicy(provider);
			Future<Service> serviceFuture = serviceDiscovery.getService(serviceIdentity);
			service = serviceFuture.get();
		} catch (PrivacyException e) {
			LOG.error("Can't retrieve the privacy policy of this Service", e);
			errorMsg.append("Can't retrieve the privacy policy of this Service\n");
		} catch (InvalidFormatException e) {
			LOG.error("Can't retrieve parameters of this Service", e);
			errorMsg.append("Can't retrieve parameters of this Service\n");
		} catch (ServiceDiscoveryException e) {
			LOG.error("Can't retrieve this Service", e);
			errorMsg.append("Can't retrieve this Service\n");
		} catch (InterruptedException e) {
			LOG.error("Can't retrieve this Service: interruption", e);
			errorMsg.append("Can't retrieve this Service: interruption\n");
		} catch (ExecutionException e) {
			LOG.error("Can't retrieve this Service: execution", e);
			errorMsg.append("Can't retrieve this Service: execution\n");
		}

		// -- Display the privacy policy
		// - No privacy policy
		if (null == privacyPolicy) {
			//			errorMsg.append("The CIS privacy policy of "+provider+" can not be retrieved. It doesn't exist on this node, or identifiers are incorrect.");
			LOG.error("The Service privacy policy of "+provider+" can not be retrieved. It doesn't exist on this node, or identifiers are incorrect.");
			errorMsg.append("Can't retrieve the privacy policy of this Service.");
			if (test) {
				LOG.error("Let's create one");
				errorMsg.append("\nFor testing purpose: lets create one.");
				// -- Create a privacy policy
				List<RequestItem> requests = createTestPrivacyPolicy();
				privacyPolicy = new RequestPolicy(provider, requests);
			}
		}
		else {
			LOG.debug(privacyPolicy.toXMLString());
		}

		LOG.error(errorMsg.toString());
		LOG.info(infoMsg.toString());

		Map<String, Object> model = new HashMap<String, Object>();
		model.put("error", errorMsg.toString());
		model.put("info", infoMsg.toString());
		model.put("PrivacyPolicy", privacyPolicy);
		model.put("Service", service);
		model.put("Element", "service");
		return new ModelAndView("privacy/privacy-policy/show-3p-service", model);
	}



	private List<RequestItem> createTestPrivacyPolicy() {
		List<Action> actionsRw = new ArrayList<Action>();
		actionsRw.add(new Action(ActionConstants.READ));
		actionsRw.add(new Action(ActionConstants.WRITE, true));
		List<Action> actionsR = new ArrayList<Action>();
		actionsR.add(new Action(ActionConstants.READ));
		List<Condition> conditionsMembersOnly = new ArrayList<Condition>();
		conditionsMembersOnly.add(new Condition(ConditionConstants.SHARE_WITH_CIS_MEMBERS_ONLY, "1"));
		conditionsMembersOnly.add(new Condition(ConditionConstants.STORE_IN_SECURE_STORAGE, "1"));
		List<Condition> conditionsPublic = new ArrayList<Condition>();
		conditionsPublic.add(new Condition(ConditionConstants.SHARE_WITH_3RD_PARTIES, "1"));
		conditionsPublic.add(new Condition(ConditionConstants.STORE_IN_SECURE_STORAGE, "1"));
		List<Condition> conditionsPrivate = new ArrayList<Condition>();
		conditionsPrivate.add(new Condition(ConditionConstants.SHARE_WITH_CIS_OWNER_ONLY, "1"));
		conditionsPrivate.add(new Condition(ConditionConstants.STORE_IN_SECURE_STORAGE, "1"));
		conditionsPrivate.add(new Condition(ConditionConstants.MAY_BE_INFERRED, "1"));
		List<RequestItem> requests = new ArrayList<RequestItem>();
		requests.add(new RequestItem(new Resource(DataIdentifierScheme.CONTEXT, CtxAttributeTypes.LOCATION_SYMBOLIC), actionsRw, conditionsMembersOnly));
		requests.add(new RequestItem(new Resource(DataIdentifierScheme.CONTEXT, CtxAttributeTypes.BIRTHDAY), actionsR, conditionsPublic));
		requests.add(new RequestItem(new Resource(DataIdentifierScheme.CONTEXT, CtxAttributeTypes.LAST_ACTION), actionsR, conditionsPrivate));
		requests.add(new RequestItem(new Resource(DataIdentifierScheme.CIS, "cis-member-list"), actionsRw, conditionsMembersOnly));
		return requests;
	}


	@RequestMapping(value = "/privacy-policy.html", method = RequestMethod.GET)
	public ModelAndView updateAction() {
		LOG.debug("privacy policy update HTTP GET");

		Map<String, Object> model = new HashMap<String, Object>();
		PrivacyPolicyForm privacyPolicyFrom = new PrivacyPolicyForm();
		privacyPolicyFrom.createEmptyPrivacyPolicyFrom();
		StringBuffer resultMsg = new StringBuffer();
		try {
			generateResourceLists();
			model.put("privacyPolicy", privacyPolicyFrom);
			model.put("ActionList", ActionConstants.values());
			model.put("ConditionList", ConditionConstants.values());
			model.put("ResourceList", resourceList);
			model.put("ResourceHumanList", resourceHumanList);
			model.put("ResourceSchemeList", DataIdentifierScheme.values());
		}
		catch(IllegalArgumentException e) {
			resultMsg.append("Error during the generation of the privacy policy form: can't retrieve data type (scheme) "+e.getMessage());
			LOG.error("Error during the generation of the privacy policy form: can't retrieve data type (scheme)", e);
		} catch (IllegalAccessException e) {
			resultMsg.append("Error during the generation of the privacy policy form: error when retrievint data type (scheme) "+e.getMessage());
			LOG.error("Error during the generation of the privacy policy form: error when retrievint data type (scheme)", e);
		}
		model.put("ResultMsg", resultMsg.toString());
		return new ModelAndView("privacy/privacy-policy/update", model);
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/privacy-policy.html", method = RequestMethod.POST)
	public ModelAndView updateAction(@Valid PrivacyPolicyForm privacyPolicyFrom, BindingResult result, Map model) {
		LOG.debug("privacy policy update HTTP POST");

		// -- Verification
		if (result.hasErrors()) {
			LOG.warn("BindingResult has errors");
			model.put("errormsg", "privacy policy form error<br />"+result.toString()+"<br />"+result.getFieldErrors().get(0).getObjectName());
			return new ModelAndView("error", model);
		}
		LOG.info(privacyPolicyFrom.toString());

		// -- Storage
		RequestPolicy privacyPolicy;
		StringBuffer resultMsg = new StringBuffer();
		if (isDepencyInjectionDone()) {
			try {
				privacyPolicy = privacyPolicyFrom.toRequestPolicy(commMngrRef.getIdManager());
				LOG.info(privacyPolicy.toXMLString());
				//				privacyPolicyManager.updatePrivacyPolicy(privacyPolicy);
				resultMsg.append("\nPrivacy policy successfully created.");
				resultMsg.append("\n"+privacyPolicyFrom.toString());
			} catch (InvalidFormatException e) {
				resultMsg.append("Error during privacy policy saving: "+e.getLocalizedMessage());
				LOG.error("Error during privacy policy saving", e);
			} catch (MalformedCtxIdentifierException e) {
				resultMsg.append("Error during privacy policy saving: can't retrieve data type (scheme) "+e.getMessage());
				LOG.error("Error during privacy policy saving: can't retrieve data type (scheme)", e);
			}
		}
		else {
			resultMsg.append("Error with dependency injection");
			LOG.error("Error with dependency injection");
		}



		// -- Display the privacy policy
		try {
			generateResourceLists();
			model.put("privacyPolicy", privacyPolicyFrom);
			model.put("ActionList", ActionConstants.values());
			model.put("ConditionList", ConditionConstants.values());
			model.put("ResourceList", resourceList);
			model.put("ResourceHumanList", resourceHumanList);
			model.put("ResourceSchemeList", DataIdentifierScheme.values());
		}
		catch(IllegalArgumentException e) {
			resultMsg.append("Error during the generation of the privacy policy form: can't retrieve data type (scheme) "+e.getMessage());
			LOG.error("Error during the generation of the privacy policy form: can't retrieve data type (scheme)", e);
		} catch (IllegalAccessException e) {
			resultMsg.append("Error during the generation of the privacy policy form: error when retrievint data type (scheme) "+e.getMessage());
			LOG.error("Error during the generation of the privacy policy form: error when retrievint data type (scheme)", e);
		}
		model.put("ResultMsg", resultMsg.toString());
		return new ModelAndView("privacy/privacy-policy/update", model);
	}

	public static void generateResourceLists() throws IllegalArgumentException, IllegalAccessException {
		Field[] resourceTypeList = CtxAttributeTypes.class.getDeclaredFields();
		resourceList = new String[resourceTypeList.length];
		resourceHumanList = new String[resourceTypeList.length];
		for(int i=0; i<resourceTypeList.length; i++) {
			resourceList[i] = DataIdentifierScheme.CONTEXT+":///"+((String)resourceTypeList[i].get(null));
			resourceHumanList[i] = DataIdentifierScheme.CONTEXT+": "+((String)resourceTypeList[i].get(null));
		}

		DataIdentifierScheme[] schemes = DataIdentifierScheme.values();
		resourceSchemeList = new String[schemes.length];
		for(int j=0; j<schemes.length; j++) {
			resourceSchemeList[j] = schemes[j].value();
		}
	}

	private boolean isDepencyInjectionDone() {
		return isDepencyInjectionDone(0);
	}
	private boolean isDepencyInjectionDone(int level) {
		if (null == commMngrRef) {
			LOG.info("[Dependency Injection] Missing ICommManager");
			return false;
		}
		if (null == commMngrRef.getIdManager()) {
			LOG.info("[Dependency Injection] Missing IIdentityManager");
			return false;
		}
		return true;
	}

	// -- Dependency Injection
	public void setPrivacyPolicyManager(IPrivacyPolicyManager privacyPolicyManager) {
		this.privacyPolicyManager = privacyPolicyManager;
		LOG.info("[DepencyInjection] IPrivacyPolicyManager injected");
	}
	public void setPrivacyPolicyManagerRemote(IPrivacyPolicyManagerRemote privacyPolicyManagerRemote) {
		this.privacyPolicyManagerRemote = privacyPolicyManagerRemote;
		LOG.info("[DepencyInjection] IPrivacyPolicyManagerRemote injected");
	}
	public void setCommMngrRef(ICommManager commMngrRef) {
		this.commMngrRef = commMngrRef;
		LOG.info("[DepencyInjection] ICommManager injected");
	}
	public void setCisManager(ICisManager cisManager) {
		this.cisManager = cisManager;
		LOG.info("[DepencyInjection] ICisManager injected");
	}
	public void setServiceDiscovery(IServiceDiscovery serviceDiscovery) {
		this.serviceDiscovery = serviceDiscovery;
		LOG.info("[DepencyInjection] IServiceDiscovery injected");
	}

	public class PrivacyPolicyManagerRemoteListener implements IPrivacyPolicyManagerListener {
		public RequestPolicy privacyPolicy;
		public boolean ack;
		public String ackMessage;

		@Override
		public void onPrivacyPolicyRetrieved(org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestPolicy privacyPolicy) {
			LOG.error("onPrivacyPolicyRetrieved");
			ack = true;
			try {
				this.privacyPolicy = RequestPolicyUtils.toRequestPolicy(privacyPolicy, commMngrRef.getIdManager());
			} catch (InvalidFormatException e) {
				onOperationAborted("Privacy policy retrieved, but it is ununderstandable", e);
			}
			notifyAll();
		}
		
		@Override
		public void onPrivacyPolicyRetrieved(RequestPolicy privacyPolicy) {
			LOG.error("onPrivacyPolicyRetrieved");
			ack = true;
			this.privacyPolicy = privacyPolicy;
			notifyAll();
		}

		@Override
		public void onOperationSucceed(String msg) {
			LOG.error("onOperationSucceed");
			ack = true;
			ackMessage = msg;
			notifyAll();
		}

		@Override
		public void onOperationCancelled(String msg) {
			LOG.error("onOperationCancelled");
			ack = false;
			ackMessage = msg;
			notifyAll();
		}

		@Override
		public void onOperationAborted(String msg, Exception e) {
			LOG.error("onOperationAborted: "+e.getMessage(), e);
			ack = false;
			ackMessage = msg;
			notifyAll();
		}
	}
}