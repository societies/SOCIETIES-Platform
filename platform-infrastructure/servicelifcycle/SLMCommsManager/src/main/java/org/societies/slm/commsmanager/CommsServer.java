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

/**
 * Describe your class here...
 *
 * @author aleckey
 *
 */
package org.societies.slm.commsmanager;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.comm.xmpp.interfaces.IFeatureServer;
import org.societies.api.internal.servicelifecycle.model.Service;
import org.societies.api.internal.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.api.internal.servicelifecycle.serviceMgmt.IServiceManagement;
import org.societies.api.internal.servicelifecycle.serviceMgmt.ServiceStatus;
import org.societies.slm.servicemanagement.schema.ServiceMgmtMsgBean;
import org.societies.slm.servicemanagement.schema.ServiceMgmtResultBean;

public class CommsServer implements IFeatureServer {

	private static final List<String> NAMESPACES = Collections.unmodifiableList(
							  Arrays.asList("http://societies.org/slm/servicemanagement/schema",
									  		"http://societies.org/slm/serviceregistry/schema"));
	private static final List<String> PACKAGES = Collections.unmodifiableList(
							  Arrays.asList("org.societies.slm.servicemanagement.schema",
											"org.societies.slm.serviceregistry.schema"));
	
	//PRIVATE VARIABLES
	private ICommManager commManager;
	private IServiceManagement serviceManagement;
	private static Logger LOG = LoggerFactory.getLogger(CommsServer.class);

	//PROPERTIES
	public IServiceManagement getServiceManagement() {
		return serviceManagement;
	}

	public void setServiceManagement(IServiceManagement serviceManagement) {
		this.serviceManagement = serviceManagement;
	}
	
	public ICommManager getCommManager() {
		return commManager;
	}

	public void setCommManager(ICommManager commManager) {
		this.commManager = commManager;
	}
	
	//METHODS
	public CommsServer() {
	}
	
	public void InitService() {
		//REGISTER OUR ServiceManager WITH THE XMPP Communication Manager
		try {
			getCommManager().register(this); 
		} catch (CommunicationException e) {
			e.printStackTrace();
		}
	}
	
	/* (non-Javadoc)
	 * @see org.societies.comm.xmpp.interfaces.FeatureServer#getJavaPackages() */
	@Override
	public List<String> getJavaPackages() {
		return PACKAGES;
	}
	
	/* (non-Javadoc)
	 * @see org.societies.comm.xmpp.interfaces.FeatureServer#getXMLNamespaces() */
	@Override
	public List<String> getXMLNamespaces() {
		return NAMESPACES;
	}
	
	/* Put your functionality here if there is NO return object, ie, VOID  */
	@Override
	public void receiveMessage(Stanza stanza, Object payload) {
		//CHECK WHICH END BUNDLE TO BE CALLED THAT I MANAGE
		// --------- Service Management BUNDLE ---------
		if (payload.getClass().equals(ServiceMgmtMsgBean.class)) {
			
		}
	}

	/* Put your functionality here if there IS a return object */
	@Override
	public Object getQuery(Stanza stanza, Object payload) throws XMPPError {
		//CHECK WHICH END BUNDLE TO BE CALLED THAT I MANAGE
		
		// --------- Service Management BUNDLE ---------
		if (payload.getClass().equals(ServiceMgmtMsgBean.class)) {
			
			ServiceMgmtMsgBean serviceMessage = (ServiceMgmtMsgBean) payload;
			ServiceMgmtResultBean serviceResult;
			
			switch (serviceMessage.getMethod()) {
				case IS_SERVICE_REGISTRY_ACTIVE :
					boolean IsServiceRegistryActive = serviceManagement.IsServiceRegistryActive();
					serviceResult.setServiceRegistryActiveResult(IsServiceRegistryActive);
					break;
					
				case GET_SERVICE_STATUS :
					org.societies.slm.servicemanagement.schema.ServiceResourceIdentifier serviceId = serviceMessage.getServiceID();
					org.societies.slm.servicemanagement.schema.ServiceStatus status = serviceManagement.getServiceStatus(serviceId);
					serviceResult.setServiceStatusResult(status);
					break;
					
				case FIND_ALL_SERVICES :
					Collection<org.societies.slm.servicemanagement.schema.Service> serviceCollection = serviceResult.getFindAllServicesResult();
					serviceCollection = serviceManagement.findAllServices();
					break;
			
			}				
			
			//RETURN MESSAGEBEAN RESULT
			return serviceResult;
		}
		
		//TODO: Better error handling, ie, if there is no match on the received Message Bean
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.comm.xmpp.interfaces.FeatureServer#setQuery(org.societies.comm.xmpp.datatypes.Stanza, java.lang.Object)
	 */
	@Override
	public Object setQuery(Stanza arg0, Object arg1) throws XMPPError {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}
