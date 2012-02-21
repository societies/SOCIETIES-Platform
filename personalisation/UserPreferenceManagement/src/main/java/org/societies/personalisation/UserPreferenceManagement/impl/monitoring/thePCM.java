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
package org.societies.personalisation.UserPreferenceManagement.impl.monitoring;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.datatypes.Identity;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.personalisation.model.IOutcome;
import org.societies.api.internal.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.api.internal.useragent.decisionmaking.IDecisionMaker;
import org.societies.api.servicelifecycle.model.IServiceResourceIdentifier;
import org.societies.personalisation.preference.api.UserPreferenceConditionMonitor.IUserPreferenceConditionMonitor;

import sun.awt.PeerEvent;

import com.sun.jndi.toolkit.ctx.ComponentContext;

public class thePCM /*extends EventListener*/ implements IUserPreferenceConditionMonitor{

	private Logger logging = LoggerFactory.getLogger(this.getClass());
	private Hashtable<Identity, PCM> pcmInstances;
	private String myPeerID;
	private boolean isPeerUID;
	/*
	 * this value is user controlled from the GUI. it is not related to the 
	 * Current User Interaction device. 
	 * It is a utility for turning off the PCM in order to allow 
	 * other input providers to be used by Proactivity
	 */
	private boolean enabled = true;

	private ICtxBroker broker;	


	//EVENTMANAGER


	private IEventMgr eventMgr;	



	//DECISIONMAKER

	private IDecisionMaker decisionMaker;



	//IDM

	private IIdentityManagement IDM;	


	//PREFERENCE MANAGER (HANDLER)

	private IPreferenceHandler prefImpl;



	private IServiceResourceIdentifier	my_FW_SERVICE_ID;
	//PSS MANAGER


	/*
	 * END DS DEPENDENCIES ***************************************************************************************
	 */


	protected synchronized void activate(ComponentContext cc){
		this.logging.debug("Activating PCM");
		try{
			if (this.broker==null){
				this.logging.debug("activating PCM with null reference of broker");
			}
			if (this.decisionMaker==null){
				this.logging.debug("activating PCM with null reference of DM");
			}
			this.cContext = cc;
			this.pcmInstances = new Hashtable<Identity, PCM>();
			String myLocalID = (String) cc.getProperties().get(PssConstants._FW_SERVICE_ID);
			this.my_FW_SERVICE_ID = new ServiceResourceIdentifier(myLocalID, this.IDM.getPublicDigitalPersonalIdentifier());
			this.decisionMaker.registerInputProvider(my_FW_SERVICE_ID, this);
			try {
				this.myPeerID = this.pssMgr.getPeerIdentifier();
			} catch (PssManagerException e) {
				this.logging.debug("Could not get my PEERID!!!!");
				this.myPeerID = "";
				e.printStackTrace();
			}
			this.registerForUIDChange();
			this.registerForSessionEvent();
			this.registerForPreferenceChangedEvents();

			/*
			 * see if this is the CUID
			 */

			try {
				List<ICtxIdentifier> attrs = this.broker.lookup(CtxModelType.ATTRIBUTE, CtxAttributeTypes.USER_INTERACTION_DEVICE);
				if (attrs.size()==0){
					this.logging.debug("No USER_INTERACTION_DEVICE attribute found in context. Not setting the CUID");

				}else{
					ICtxIdentifier attrID = attrs.get(0);
					CtxAttribute attr = (CtxAttribute) this.broker.retrieve(attrID);
					String uid = attr.getStringValue();
					if (uid==null){
						this.logging.debug("value of UID context attribute is null");
					}else if (uid.equalsIgnoreCase(myPeerID)){
						this.isPeerUID = true;
					}

				}

			} catch (CtxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		catch (Exception e){
			this.logging.debug("CATASTROPHE!");
			this.logging.debug(e.getMessage());
			e.printStackTrace();
		}
	}



	private void registerForSessionEvent(){
		this.eventMgr.registerListener(this, new String[]{PSSEventTypes.SERVICE_SESSION_EVENT}, null);
		this.logging.debug("Registered for events: "+PSSEventTypes.SERVICE_SESSION_EVENT);

	}

	private void registerForUIDChange(){
		try {
			CtxEntity operator = this.broker.retrieveOperator();
			this.broker.registerUpdateNotification(this, operator.getCtxIdentifier(),org.personalsmartspace.cm.model.api.pss3p.CtxAttributeTypes.USER_INTERACTION_DEVICE);
		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private  void registerForPreferenceChangedEvents(){
		this.eventMgr.registerListener(this, new String[]{PSSEventTypes.PREFERENCES_CHANGED}, null);
		this.logging.debug("Registered for events: "+PSSEventTypes.PREFERENCES_CHANGED);
	}
	

	/* (non-Javadoc)
	 * @see org.personalsmartspace.sre.ems.api.pss3p.EventListener#handlePSSEvent(org.personalsmartspace.sre.ems.api.pss3p.PSSEvent)
	 */
	@Override
	public void handlePSSEvent(PSSEvent event) {
		this.logging.debug("PSS Event Received " + event);
		String value = (String) event.geteventInfoAsXML();

		if (event.geteventType().equals(PSSEventTypes.SERVICE_SESSION_EVENT)){
			SessionEvent sessionEvt = (SessionEvent) XMLConverter.xmlToObject(event.geteventInfoAsXML(), SessionEvent.class);

			SessionPlan sp = sessionEvt.getSessionPlan();
			SessionPlanNode[] nodes = sp.getServices();
			for (SessionPlanNode node : nodes){

				Identity dpi = node.getUserDPI();

				if (sessionEvt.getSessionType().equals(SessionEventType.SessionCreated)){
					this.getPCMperDPI(dpi).processServiceStarted(node.getService());
				}else if (sessionEvt.getSessionType().equals(SessionEventType.SessionTerminated)){
					this.getPCMperDPI(dpi).processServiceStopped(node.getService());
				}
			}

		}else if (event.geteventType().equals(PSSEventTypes.CONTEXT_UPDATE_EVENT)){
			this.logging.debug(this.getClass().getName()+" RECEIVED CONTEXT UPDATE PSS EVENT");
			CtxAttribute ctxAttr = (CtxAttribute) XMLConverter.xmlToObject(event.geteventInfoAsXML(),CtxAttribute.class);
			ICtxIdentifier id;
			try {
				id = broker.parseIdentifier((String) event.geteventName());
				String type = id.getType();
				if (type.equals(org.personalsmartspace.cm.model.api.pss3p.CtxAttributeTypes.USER_INTERACTION_DEVICE)){

					String newUID = ctxAttr.getStringValue();
					//JOptionPaneshowMessageDialog(null, "NEW CUID: "+newUID+" my Peer ID: "+this.myPeerID);

					this.isPeerUID = newUID.equalsIgnoreCase(this.myPeerID);
					Enumeration<Identity> dpis = this.pcmInstances.keys();
					while (dpis.hasMoreElements()){
						Identity dpi = dpis.nextElement();
						PCM pcm = this.getPCMperDPI(dpi);
						pcm.setCUID(isPeerUID);
					}

				}
			} catch (CtxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}else if (event.geteventType().equals(PSSEventTypes.PREFERENCES_CHANGED)){
			//JOptionPaneshowMessageDialog(null, "thePCM received "+PSSEventTypes.PREFERENCES_CHANGED+" event");
			PreferenceChangedEvent pEvent;
			try {
				pEvent = (PreferenceChangedEvent) XMLConverter.xmlToObject(event.geteventInfoAsXML(), PreferenceChangedEvent.class);

				Identity dpi = DigitalPersonalIdentifier.fromString(pEvent.getDpi());
				PCM pcm = this.getPCMperDPI(dpi);

				pcm.processNewPreferences(pEvent);
			} catch (MalformedDigitialPersonalIdentifierException e) {
				this.logging.debug("Unable to convert String to DPI (String acquired from the PreferenceChangedEvent");
				e.printStackTrace();
			} catch (Exception ex){
				this.logging.debug("Unable to process preferenceChangedEvent");
				ex.printStackTrace();
			}






		}

	}

	/* (non-Javadoc)
	 * @see org.personalsmartspace.sre.ems.api.pss3p.EventListener#handlePeerEvent(org.personalsmartspace.sre.ems.api.pss3p.PeerEvent)
	 */
	@Override
	public void handlePeerEvent(PeerEvent event) {
		this.logging.debug(this.getClass().getName()+" RECEIVED "+event.geteventType()+" as a PeerEvent");
		if (event.geteventType().equals(PSSEventTypes.CONTEXT_UPDATE_EVENT)){
			this.logging.debug(this.getClass().getName()+" RECEIVED CONTEXT UPDATE PEER EVENT");
			CtxAttribute ctxAttr = (CtxAttribute) event.geteventInfo();

			try {

				String type = ctxAttr.getType();
				if (type.equals(CtxAttributeTypes.USER_INTERACTION_DEVICE)){
					String newUID = ctxAttr.getStringValue();

					if (newUID==null){
						this.logging.debug("String value of type: "+ctxAttr.getType()+" is null");
						newUID = (String) ctxAttr.getBlobValue(this.getClass().getClassLoader());
					}else{
						this.logging.debug("String value of type: "+ctxAttr.getType()+" is "+newUID);
					}
					if (this.myPeerID ==null){
						this.logging.debug("variable myPeerID is null");
					}else{
						this.logging.debug("variable myPeerID is not null");
					}
					if (newUID.compareToIgnoreCase(this.myPeerID)==0){
						isPeerUID = true;
						this.logging.debug("This node is the UID - Setting the instances of PCM of this node to ACTIVE");
						//JOptionPane.showMessageDialog(null, "PCM: Setting this PEER as the Current User Interaction Device");
					}
					Enumeration<Identity> dpis = this.pcmInstances.keys();
					while (dpis.hasMoreElements()){
						Identity dpi = dpis.nextElement();
						PCM pcm = this.getPCMperDPI(dpi);
						pcm.setCUID(isPeerUID);
					}

				}
			} catch (CtxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}else if (event.geteventType().equals(PSSEventTypes.SERVICE_SESSION_EVENT)){
			SessionEvent sessionEvt = (SessionEvent) event.geteventInfo();

			SessionPlan sp = sessionEvt.getSessionPlan();
			SessionPlanNode[] nodes = sp.getServices();
			for (SessionPlanNode node : nodes){

				Identity dpi = node.getUserDPI();
				//JOptionPaneshowMessageDialog(null, "Session started for DPI "+dpi.toUriString()+" for service "+node.getService().getServiceType());

				this.logging.debug("Processing: "+sessionEvt.getSessionType());
				if (sessionEvt.getSessionType().equals(SessionEventType.SessionCreated)){
					
					this.getPCMperDPI(dpi).processServiceStarted(node.getService());
				}else if (sessionEvt.getSessionType().equals(SessionEventType.SessionTerminated)){
					this.getPCMperDPI(dpi).processServiceStopped(node.getService());
				}else{
					this.logging.debug("not called anything");
				}
			}
		}else{
			this.logging.debug(this.getClass().getName()+"IGNORED RECEIVED "+event.geteventType()+" as a PeerEvent");
		}

	}

	private PCM getPCMperDPI(Identity dpi){
		if (this.pcmInstances.containsKey(dpi)){
			//JOptionPaneshowMessageDialog(null, "PCM exists for dpi "+dpi.toUriString());
			return this.pcmInstances.get(dpi);
		}
		//JOptionPaneshowMessageDialog(null, "PCM doesn't exist for DPI: "+dpi.toUriString());
		//JOptionPaneshowMessageDialog(null, "Instantiating PCM with DPI: "+dpi.toUriString());
		PCM pcm = new PCM(this.decisionMaker,this.broker,this.prefImpl, my_FW_SERVICE_ID, dpi);
		this.pcmInstances.put(dpi, pcm);
		pcm.setCUID(isPeerUID);
		pcm.setEnabled(this.enabled);
		return pcm;

	}
	/* (non-Javadoc)
	 * @see org.personalsmartspace.pm.pcm.api.platform.IPCM#requestOutcomeWithCurrentContext(org.personalsmartspace.sre.api.pss3p.Identity, java.lang.String, org.personalsmartspace.sre.api.pss3p.IServiceResourceIdentifier, java.lang.String)
	 */
	@Override
	public IOutcome requestOutcomeWithCurrentContext(
			Identity dpi, String serviceType,
			IServiceResourceIdentifier serviceID, String parameterName) {
		return this.getPCMperDPI(dpi).requestOutcomeWithCurrentContext(dpi, serviceType, serviceID, parameterName);
	}

	/* (non-Javadoc)
	 * @see org.personalsmartspace.pm.pcm.api.platform.IPCM#requestOutcomeWithFutureContext(org.personalsmartspace.sre.api.pss3p.Identity, java.lang.String, org.personalsmartspace.sre.api.pss3p.IServiceResourceIdentifier, java.lang.String)
	 */
	@Override
	public IOutcome requestOutcomeWithFutureContext(
			Identity dpi, String serviceType,
			IServiceResourceIdentifier serviceID, String parameterName) {
		return this.getPCMperDPI(dpi).requestOutcomeWithFutureContext(dpi, serviceType, serviceID, parameterName);
	}

	/* (non-Javadoc)
	 * @see org.personalsmartspace.pro.common.api.platform.IIOutcomeProvider#getFWServiceID()
	 */
	@Override
	public IServiceResourceIdentifier getFWServiceID() {
		return this.my_FW_SERVICE_ID;
	}

	/* (non-Javadoc)
	 * @see org.personalsmartspace.pro.common.api.platform.IIOutcomeProvider#getOutcome(org.personalsmartspace.sre.api.pss3p.Identity, org.personalsmartspace.sre.api.pss3p.IServiceResourceIdentifier, java.lang.String)
	 */
	@Override
	public IOutcome getOutcome(Identity dpi,
			IServiceResourceIdentifier serviceID, String parameterName) {
		return this.getPCMperDPI(dpi).getOutcome(dpi, serviceID, parameterName);
	}

	/* (non-Javadoc)
	 * @see org.personalsmartspace.pro.common.api.platform.IIOutcomeProvider#getConditions(org.personalsmartspace.sre.api.pss3p.Identity, org.personalsmartspace.pm.prefmodel.api.platform.IOutcome)
	 */
	@Override
	public List<ICtxIdentifier> getConditions(
			Identity ownerID, IOutcome outcome) {
		return this.prefImpl.getConditions(ownerID, outcome);
	}

	public ICtxBroker getBroker(){

		return this.broker;
	}

	public IDecisionMaker getDecisionMaker(){

		return this.decisionMaker;
	}

	public IPreferenceHandler getPrefMgr(){
		return this.prefImpl;
	}

	//@Override
	public long getStartMilliSeconds(Identity dpi) {
		return this.getPCMperDPI(dpi).getStartMilliseconds();
	}

	//@Override
	public long getStopMilliSeconds(Identity dpi) {
		return this.getPCMperDPI(dpi).getStopMilliseconds();
	}
	
	public Enumeration<Identity> getDPIs(){
		return this.pcmInstances.keys();
	}
	
	public String getTableDataAsString(Identity dpi){
		PCM pcm = this.getPCMperDPI(dpi);
		if (pcm!=null){
			return pcm.getTableAsString();
		}
		return "PCM for :"+dpi.toUriString()+" not initialised yet";
	}
	
	public Class getInterfaceClass() {
		return IPCM.class;
	}

	@Override
	public void disableAllPCM() {
		Enumeration<PCM> pcmInstances = this.pcmInstances.elements();
		while (pcmInstances.hasMoreElements()){
			pcmInstances.nextElement().setEnabled(false);
		}
		this.enabled = false;
	}

	@Override
	public void disablePCM(Identity dpi) {
		if (this.pcmInstances.containsKey(dpi)){
			this.pcmInstances.get(dpi).setEnabled(false);
		}
		
	}

	@Override
	public void enableAllPCM() {
		Enumeration<PCM> pcmInstances = this.pcmInstances.elements();
		while (pcmInstances.hasMoreElements()){
			pcmInstances.nextElement().setEnabled(true);
		}
		this.enabled = true;
	}

	@Override
	public void enablePCM(Identity dpi) {
		if (this.pcmInstances.containsKey(dpi)){
			this.pcmInstances.get(dpi).setEnabled(true);
		}
	}
}
