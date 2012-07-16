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
package org.societies.privacytrust.privacyprotection.privacypreferencemanager.gui;




import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.INetworkNode;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.osgi.event.IEventMgr;
import org.societies.privacytrust.privacyprotection.api.IPrivacyPreferenceManager;
import org.societies.privacytrust.privacyprotection.privacypreferencemanager.gui.privacy.PPN.PPNPreferenceSelectionGUI;

public class GUI {

	
	private Logger logging = LoggerFactory.getLogger(this.getClass());


	private ICtxBroker ctxBroker;

	public void setCtxBroker(ICtxBroker broker){
		this.ctxBroker = broker;
	}

	public ICtxBroker getCtxBroker(){
		return ctxBroker;
	}
	
	private IPrivacyPreferenceManager privPrefMgr;

	public void setPrivPrefMgr(IPrivacyPreferenceManager privacyPreferenceManager){
		this.privPrefMgr = privacyPreferenceManager;
	}

	

	public IPrivacyPreferenceManager getPrivPrefMgr(){
		return this.privPrefMgr;
	}
	

	private IIdentityManager idMgr;

	private ICommManager commMgr;
	private IIdentity userIdentity;
	

	public ICommManager getCommMgr() {
		return commMgr;
	}


	public void setCommMgr(ICommManager commMgr) {
		this.commMgr = commMgr;
		this.idMgr = commMgr.getIdManager();
		this.userIdentity = this.idMgr.getThisNetworkNode();
	}
	
	public IIdentityManager getIdMgr(){
		return this.idMgr;
	}


	public IIdentity getUserIdentity() {
		return userIdentity;
	}

	private IEventMgr eventMgr;

	public void setEventMgr(IEventMgr evMgr){
		this.eventMgr = evMgr;
	}

	public IEventMgr getEventMgr(){
		return this.eventMgr;
	}
	
	public void initialiseGUIs(){
		PPNPreferenceSelectionGUI prefGUI = new PPNPreferenceSelectionGUI(this); 
	}




	public static void main(String[] args) {


	}


}
