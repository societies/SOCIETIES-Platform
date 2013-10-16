/* Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske druzbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVACAO, SA (PTIN), IBM Corp., 
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

/* @Author: Haoyi XIONG, haoyi.xiong@it-sudparis.eu
 * @Dependency:
 * org.societies.api.internal.personalisation.model.IOutcome;
 * org.societies.api.internal.useragent.model.AbstractDecisionMaker;
 * org.societies.api.internal.useragent.model.ConflictType;
 * org.societies.api.personalisation.model.IAction;
 */
package org.societies.useragent.decisionmaking;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.*;

import org.osgi.util.tracker.ServiceTracker;
import org.osgi.framework.BundleContext;
import org.springframework.osgi.context.BundleContextAware;
import org.societies.api.identity.IIdentity;
import org.societies.api.internal.personalisation.model.IOutcome;
import org.societies.useragent.conflict.*;
import org.societies.api.personalisation.model.IAction;
import org.societies.api.personalisation.model.IActionConsumer;
import org.societies.useragent.conflict.ConfidenceTradeoffRule;
import org.societies.useragent.conflict.ConflictResolutionManager;
import org.societies.useragent.conflict.IntentPriorRule;
import org.societies.api.identity.IIdentity;
import org.societies.api.schema.servicelifecycle.model.Service;
import org.societies.api.services.ServiceUtils;
import org.societies.api.internal.personalisation.model.*;
import org.societies.api.internal.servicelifecycle.IServiceControl;
import org.societies.api.internal.servicelifecycle.ServiceModelUtils;
import org.societies.api.internal.servicelifecycle.serviceRegistry.IServiceRegistry;
import org.societies.api.internal.servicelifecycle.serviceRegistry.exception.ServiceRetrieveException;
import org.societies.api.osgi.event.EMSException;
import org.societies.api.osgi.event.EventTypes;
import org.societies.api.osgi.event.IEventMgr;
import org.societies.api.osgi.event.InternalEvent;


public class DecisionMaker extends AbstractDecisionMaker implements
BundleContextAware {

	private BundleContext myContext;
	
	
	
	private List<IActionConsumer> temporal = null;

	// private IServiceDiscovery SerDiscovery;
	private Logger logging = LoggerFactory.getLogger(this.getClass());

	public void setBundleContext(BundleContext bundleContext) {
		this.myContext = bundleContext;
	}

	// public IServiceDiscovery getSerDiscovery() {
	// return SerDiscovery;
	// }
	// public void setSerDiscovery(IServiceDiscovery serDiscovery) {
	// SerDiscovery = serDiscovery;
	// }

	private void refreshServiceLookup() {
		List<IActionConsumer> lst = new ArrayList<IActionConsumer>();
		ServiceTracker servTracker = new ServiceTracker(this.myContext,
				IActionConsumer.class.getName(), null);
		logging.debug("query for all IActionConsumer");
		servTracker.open();
		Object[] ls = servTracker.getServices();
		// List<Service> ls=this.SerDiscovery.getLocalServices().get();
		if (ls == null)
			return;
		for (Object ser : ls) {
			if (ser instanceof IActionConsumer) {
				lst.add((IActionConsumer) ser);
			}
			logging.debug("fetch service:\t" + ser);
		}
		this.temporal = lst;
	}

	public BundleContext getMyContext() {
		return myContext;
	}

	public void setMyContext(BundleContext myContext) {
		logging.debug("BundleContext injected:\t" + myContext);
		this.myContext = myContext;
	}



	public DecisionMaker() {
		ConflictResolutionManager man = new ConflictResolutionManager();
		man.addRule(new ConfidenceTradeoffRule());
		//	man.addRule(new IntentPriorRule());
		super.setManager(man);
		logging.debug("Intialized DM");
	}

	@Override
	protected ConflictType detectConflict(IOutcome intent, IOutcome prefernce) {
		// TODO Auto-generated method stub
		try {
			if (ServiceUtils.
					compare(intent.getServiceID(), prefernce.getServiceID())) {
				if (intent.getparameterName().equals(
						prefernce.getparameterName())) {
					if (!intent.getvalue().equalsIgnoreCase(
							prefernce.getvalue())) {
						logging.debug("conflict detected!");
						return ConflictType.PREFERNCE_INTENT_NOT_MATCH;
					}
				}
			}
			return ConflictType.NO_CONFLICT;
		} catch (Exception e) {
			return ConflictType.UNKNOWN_CONFLICT;
		}

	}


	@Override
	public void makeDecision(List<IOutcome> intents, List<IOutcome> preferences) {
		makeDecision(intents,preferences,"");
	}


	@Override
	public void makeDecision(List<IOutcome> intents, List<IOutcome> preferences, String uuid) {
		logging.debug("make decision with\t" + preferences.size()
				+ " preferences" + "\t" + intents.size() + " intents");
		this.refreshServiceLookup();
		logging.debug("refresh the list of services and doing decision making...");
		super.makeDecision(intents, preferences,uuid);
		logging.debug("decision making has been finished");
	}

	@Override
	protected void implementIAction(IAction action, String uuid) {

		try{
			// TODO Auto-generated method stub
			// @temporal solution depends on the 3rd party-services
			if (logging.isDebugEnabled()){
				logging.debug("****************************************");
				logging.debug("implement the Action for Service ID:\t"
						+ action.getServiceID());
				logging.debug("Service Type:\t" + action.getServiceType());
				logging.debug("Parameter Name of IAction:\t"
						+ action.getparameterName());
				logging.debug("Parameter Value of IAction:\t" + action.getvalue());
				logging.debug("****************************************");
				logging.debug("implementing IAction DM");
			}
			boolean found = false;
			if (this.temporal != null) {
				for (IActionConsumer consumer : this.temporal) {
					if (this.logging.isDebugEnabled()){
						this.logging.debug("comparing: "
								+ consumer.getServiceIdentifier()
								.getServiceInstanceIdentifier() + " with: "
								+ action.getServiceID().getServiceInstanceIdentifier());
					}
					if (ServiceUtils.compare(consumer.getServiceIdentifier(),
							action.getServiceID())) {

						String cImp = "Do you want to implement the Service:" + consumer.getServiceIdentifier()
								+ "\n with the Parameter:" + action;
						if (getUserFeedback(cImp, action,uuid)) {
							boolean service_decision=consumer.setIAction(super.getEntityID(), action);

							InternalEvent event = null;
							if (service_decision){

								this.logging.info("Service "+ServiceModelUtils.serviceResourceIdentifierToString(action.getServiceID())+" was personalised proactively. Action: "+action.getparameterName()+" - value: "+action.getvalue());

								FeedbackEvent fedb = new FeedbackEvent(super.getEntityID(),
										action, true, FeedbackTypes.IMPLEMENTED);
								fedb.setUuid(uuid);
								event = new InternalEvent(
										EventTypes.UI_EVENT, "feedback",
										"org/societies/useragent/decisionmaker", fedb);
							}else{
								this.logging.info("Service "+ServiceModelUtils.serviceResourceIdentifierToString(action.getServiceID())+" was NOT personalised. (Action: "+action.getparameterName()+" - value: "+action.getvalue()+" was not implemented by service.");
								FeedbackEvent fedb = new FeedbackEvent(
										super.getEntityID(), action, true,
										FeedbackTypes.SERVICE_DECISION);
								fedb.setUuid(uuid);
								event = new InternalEvent(
										EventTypes.UI_EVENT,
										"feedback","org/societies/useragent/decisionmaker",fedb);
							}
							try {
								this.getEventMgr().publishInternalEvent(event);
							} catch (EMSException e) {
								e.printStackTrace();
							}
						} else {	

							logging.info("User aborted proactively personalising the service: "+ServiceModelUtils.serviceResourceIdentifierToString(action.getServiceID())+" with action: "+action.getparameterName()+" - value: "+action.getvalue());


						}
						found = true;
					}
				}
			}
			if (!found) {
				FeedbackEvent fedb = new FeedbackEvent(super.getEntityID(),
						action, true, FeedbackTypes.SERVICE_UNREACHABLE);
				fedb.setUuid(uuid);
				InternalEvent event = new InternalEvent(EventTypes.UI_EVENT,
						"feedback", "org/societies/useragent/decisionmaker",
						fedb);
				logging.info("Service Unreachable. Could not implement action: "+action.getparameterName()+" with value: "+action.getvalue()+" for service: "+ServiceModelUtils.serviceResourceIdentifierToString(action.getServiceID()));
				try {
					this.getEventMgr().publishInternalEvent(event);
				} catch (EMSException e) {
					e.printStackTrace();
				}
				logging.debug("No services have been founded to implement the IAction");
			}

		}catch(Exception e2){
			FeedbackEvent fedb = new FeedbackEvent(super.getEntityID(),
					action, true, FeedbackTypes.SYSTEM_ERROR);
			fedb.setUuid(uuid);
			InternalEvent event = new InternalEvent(EventTypes.UI_EVENT,
					"feedback", "org/societies/useragent/decisionmaker",
					fedb);
			try {
				this.getEventMgr().publishInternalEvent(event);
			} catch (EMSException e) {
				e.printStackTrace();
			}
			if (logging.isDebugEnabled()){
			logging.debug("System fails");
			}
			logging.info("System Error. Could not implement action: "+action.getparameterName()+" with value: "+action.getvalue()+" for service: "+ServiceModelUtils.serviceResourceIdentifierToString(action.getServiceID()));

		}

	}

}
