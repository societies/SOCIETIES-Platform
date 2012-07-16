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
package org.societies.personalisation.PersonalisationGUI.impl.preferences;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.servicelifecycle.IServiceDiscovery;
import org.societies.api.osgi.event.IEventMgr;
import org.societies.personalisation.PersonalisationGUI.impl.preferences.personalisation.PreferenceSelectionGUI;
import org.societies.personalisation.preference.api.IUserPreferenceManagement;
import org.societies.personalisation.preference.api.UserPreferenceConditionMonitor.IUserPreferenceConditionMonitor;

public class GUI {

	private IIdentityManager idMgr;
	private ICommManager commManager;
	private ICtxBroker ctxBroker;
	private IUserPreferenceManagement prefMgr;
	private IEventMgr eventMgr;
	private IUserPreferenceConditionMonitor pcm;
	private IIdentity userIdentity;
	private IServiceDiscovery serviceDiscovery;
	
	private Logger logging = LoggerFactory.getLogger(this.getClass());

	

	public void setCtxBroker(ICtxBroker broker){
		this.ctxBroker = broker;
	}

	public ICtxBroker getCtxBroker(){
		return this.ctxBroker;
	}


	

	public void setPrefMgr(IUserPreferenceManagement prefMgr){
		this.prefMgr = prefMgr;
	}

	public IUserPreferenceManagement getPrefMgr(){
		return this.prefMgr;
	}

	


	public void setEventMgr(IEventMgr evMgr){
		this.eventMgr = evMgr;
	}

	public IEventMgr getEventMgr(){
		return this.eventMgr;
	}
	
	/**
	 * @return the idMgr
	 */
	public IIdentityManager getIdMgr() {
		return idMgr;
	}

	public void initialiseGUI(){
		PreferenceSelectionGUI preferenceGUI = new PreferenceSelectionGUI(this, this.userIdentity);
	}


	/**
	 * @return the commManager
	 */
	public ICommManager getCommManager() {
		return commManager;
	}

	/**
	 * @param commManager the commManager to set
	 */
	public void setCommManager(ICommManager commManager) {
		this.commManager = commManager;
		this.idMgr = commManager.getIdManager();
		this.userIdentity = this.idMgr.getThisNetworkNode();
	}

	/**
	 * @return the pcm
	 */
	public IUserPreferenceConditionMonitor getPcm() {
		return pcm;
	}

	/**
	 * @param pcm the pcm to set
	 */
	public void setPcm(IUserPreferenceConditionMonitor pcm) {
		this.pcm = pcm;
		this.prefMgr = pcm.getPreferenceManager();
	}

	/**
	 * @return the serviceDiscovery
	 */
	public IServiceDiscovery getServiceDiscovery() {
		return serviceDiscovery;
	}

	/**
	 * @param serviceDiscovery the serviceDiscovery to set
	 */
	public void setServiceDiscovery(IServiceDiscovery serviceDiscovery) {
		this.serviceDiscovery = serviceDiscovery;
	}


}
