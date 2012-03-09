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

package org.societies.useragent.comms;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.comm.xmpp.interfaces.IFeatureServer;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.api.schema.useragent.monitoring.UserActionMonitorBean;
import org.societies.api.useragent.monitoring.IUserActionMonitor;

public class UACommsServer implements IFeatureServer{

	private static final List<String> NAMESPACES = Collections.unmodifiableList(
			Arrays.asList("http://societies.org/api/schema/useragent/monitoring"));
	private static final List<String> PACKAGES = Collections.unmodifiableList(
			Arrays.asList("org.societies.api.schema.useragent.monitoring"));

	//PRIVATE VARIABLES
	private ICommManager commManager;
	private IUserActionMonitor uam;

	//PROPERTIES
	public ICommManager getCommManager() {
		return commManager;
	}

	public void setCommManager(ICommManager commManager) {
		this.commManager = commManager;
	}

	public IUserActionMonitor getUAM() {
		return uam;
	}

	public void setUAM(IUserActionMonitor uam) {
		this.uam = uam;
	}

	//METHODS
	public UACommsServer(){
	}

	public void InitService() {
		//REGISTER OUR CommsManager WITH THE XMPP Communication Manager
		try {
			getCommManager().register(this); 
		} catch (CommunicationException e) {
			e.printStackTrace();
		}
	}

	public List<String> getJavaPackages() {
		return PACKAGES;
	}

	public List<String> getXMLNamespaces() {
		return NAMESPACES;
	}

	public void receiveMessage(Stanza stanza, Object payload) {
		//CHECK WHICH END BUNDLE TO BE CALLED THAT I MANAGE
		if (payload instanceof UserActionMonitorBean){ this.receiveMessage(stanza, (UserActionMonitorBean)payload);}	
	}
	
	public void receiveMessage(Stanza stanza, UserActionMonitorBean payload){
		//---- UAM Bundle ---
		UserActionMonitorBean monitorBean = (UserActionMonitorBean) payload;
		
		switch(monitorBean.getMethod()){
		case MONITOR:
			ServiceResourceIdentifier serviceId = monitorBean.getServiceResourceIdentifier();
			String owner = monitorBean.getIdentity();
			String action = monitorBean.getAction();
			uam.monitor(serviceId, owner, action);
			break;
		}
	}
	
	
	
	public Object getQuery(Stanza arg0, Object arg1) throws XMPPError {
		//PUT FUNCTIONALITY HERE FOR IF THERE IS A RETURN TYPE
		return null;
	}

	public Object setQuery(Stanza arg0, Object arg1) throws XMPPError {
		return null;
	}





}
