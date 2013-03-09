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

package org.societies.useragent.monitoring;

import java.util.List;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.identity.IIdentity;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.css.ICSSInternalManager;
import org.societies.api.internal.useragent.monitoring.UIMEvent;
import org.societies.api.osgi.event.EMSException;
import org.societies.api.osgi.event.EventTypes;
import org.societies.api.osgi.event.IEventMgr;
import org.societies.api.osgi.event.InternalEvent;
import org.societies.api.personalisation.model.IAction;
import org.societies.api.schema.cssmanagement.CssInterfaceResult;
import org.societies.api.schema.cssmanagement.CssNode;
import org.societies.api.schema.cssmanagement.CssRecord;
import org.societies.api.useragent.monitoring.IUserActionMonitor;
import org.societies.useragent.api.monitoring.IInternalUserActionMonitor;

public class UserActionMonitor implements IUserActionMonitor, IInternalUserActionMonitor{

	private static Logger LOG = LoggerFactory.getLogger(UserActionMonitor.class);
	private boolean interactable;
	private boolean interactableSet;
	private ICtxBroker ctxBroker;
	private IEventMgr eventMgr;
	private ICommManager commsMgr;
	private ICSSInternalManager cssMgr;
	private ContextCommunicator ctxComm;
	String myDeviceID;
	IIdentity myCssID;

	public void initialiseUserActionMonitor(){
		System.out.println("Initialising user action monitor!");

		//get myDeviceID from comms Mgr
		myDeviceID = commsMgr.getIdManager().getThisNetworkNode().getJid();  //with resource
		myCssID = commsMgr.getIdManager().getThisNetworkNode();
		
		LOG.debug("My device ID is: "+myDeviceID);
		LOG.debug("My CSS ID is: "+myCssID);

		ctxComm = new ContextCommunicator(ctxBroker, myCssID);

		interactableSet = false;
	}

	@Override
	public void monitor(IIdentity owner, IAction action) {
		LOG.debug("UAM - Received local user action!");
		LOG.debug("action ServiceId: "+action.getServiceID().toString());
		LOG.debug("action serviceType: "+action.getServiceType());
		LOG.debug("action parameterName: "+action.getparameterName());
		LOG.debug("action value: "+action.getvalue());
		
		if(!interactableSet){
			//interactable not yet set - set now
			setInteractable();
			interactableSet = true;
		}

		//update UID if this device is interactable
		if(interactable){  
			ctxComm.updateUID(owner, myDeviceID);
		}

		
		if (action.isContextDependent()){
		//save action in context - IIdentity (Person) > ServiceId > paramName
		//create new entities and attributes if necessary
			ctxComm.updateHistory(owner, action);
			//send local event
			UIMEvent payload = new UIMEvent(owner, action);
			InternalEvent event = new InternalEvent(EventTypes.UIM_EVENT, "newaction", "org/societies/useragent/monitoring", payload);
			try {
				eventMgr.publishInternalEvent(event);
			} catch (EMSException e) {
				e.printStackTrace();
			}			
		}else{
			UIMEvent payload = new UIMEvent(owner, action);
			InternalEvent event = new InternalEvent(EventTypes.UIM_STATIC_ACTION, "newaction", "org/societies/useragent/monitoring", payload);
		}


	}


	/*
	 * Called by UACommsServer - msg from light node to rich or cloud along with node ID for UID purposes
	 * 
	 * (non-Javadoc)
	 * @see org.societies.useragent.api.monitoring.IInternalUserActionMonitor#monitorFromRemoteNode(java.lang.String, org.societies.api.identity.IIdentity, org.societies.api.personalisation.model.IAction)
	 */
	@Override
	public void monitorFromRemoteNode(String remoteNodeId, IIdentity owner, IAction action) {
		LOG.debug("UAM - Received remote user action from light node with node ID: "+remoteNodeId);
		LOG.debug("action ServiceId: "+action.getServiceID().toString());
		LOG.debug("action serviceType: "+action.getServiceType());
		LOG.debug("action parameterName: "+action.getparameterName());
		LOG.debug("action value: "+action.getvalue());
		
		if(!interactableSet){
			//interactable not yet set - set now
			setInteractable();
			interactableSet = true;
		}

		//save action in context - IIdentity (Person) > ServiceId > paramName
		//create new entities and attributes if necessary
		ctxComm.updateHistory(owner, action);
		
		//update UID with remoteNodeId
		ctxComm.updateUID(owner, remoteNodeId);

		//send local event
		UIMEvent payload = new UIMEvent(owner, action);
		InternalEvent event = new InternalEvent(EventTypes.UIM_EVENT, "newaction", "org/societies/useragent/monitoring", payload);
		try {
			eventMgr.publishInternalEvent(event);
		} catch (EMSException e) {
			e.printStackTrace();
		}
	}


	
	private void setInteractable(){
		LOG.debug("Setting interactable variable in UAM for this device with ID: "+myDeviceID);
		interactable = true;
		
		try {
			CssInterfaceResult cssInterface = cssMgr.getCssRecord().get();
			CssRecord record = cssInterface.getProfile();
			if(record != null){
				LOG.debug("Got CssRecord, checking nodes...");
				List<CssNode> cssNodes = record.getCssNodes();
				if(cssNodes.size() > 0){
					boolean found = false;
					for(CssNode nextNode: cssNodes){  //find this node by myDeviceId
						if(nextNode.getIdentity().equals(myDeviceID)){
							LOG.debug("Comparing nextNode with ID: "+nextNode.getIdentity()+" against this node with ID: "+myDeviceID);
							LOG.debug("Found this device in CSS record");
							found = true;
							
							 /* 
							  * Temporary use of String
							  */
							String tmp = nextNode.getInteractable();
							if(tmp == null){
								LOG.error("getInteractable returns null for node -> "+nextNode.getIdentity()+", assuming default: not interactable");
								interactable = false;
							}else if(tmp.equalsIgnoreCase("true")){
								LOG.debug("This device is interactable");
								interactable = true;
							}else if(tmp.equalsIgnoreCase("false")){
								LOG.debug("This device is not interactable");
								interactable = false;
							}else{
								LOG.error("Interactable variable is not defined for this node, assuming default: not interactable");
								interactable = false;
							}
							/*
							 * end
							 */
							
							break;
						}
					}
					
					if(!found){
						LOG.error("Could not find this device in CssRecord with ID: "+myDeviceID);
					}
				}else{
					LOG.error("There are no CSS Nodes listed in this CssRecord with CSS ID: " +record.getCssIdentity());
				}
			}else{
				LOG.error("The CssRecord is null for this CSS");
			}
						
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}

	public void setCtxBroker(ICtxBroker broker){
		this.ctxBroker = broker;
	}

	public void setEventMgr(IEventMgr eventMgr){
		this.eventMgr = eventMgr;
	}

	public void setCommsMgr(ICommManager commsMgr){
		this.commsMgr = commsMgr;
	}
	
	public void setCssMgr(ICSSInternalManager cssMgr){
		this.cssMgr = cssMgr;
	}

}
