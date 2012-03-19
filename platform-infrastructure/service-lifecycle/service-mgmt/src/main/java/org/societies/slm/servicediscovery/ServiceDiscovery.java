/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVA��O, SA (PTIN), IBM ISRAEL
 * SCIENCE AND TECHNOLOGY LTD (IBM), INSTITUT TELECOM (ITSUD), AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA
 * PERIORISMENIS EFTHINIS (AMITEC), TELECOM ITALIA S.p.a.(TI),  TRIALOG (TRIALOG), Stiftelsen SINTEF (SINTEF), NEC EUROPE LTD
 * (NEC))
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
package org.societies.slm.servicediscovery;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.INetworkNode;
import org.societies.api.internal.servicelifecycle.IServiceDiscovery;
import org.societies.api.internal.servicelifecycle.IServiceDiscoveryCallback;
import org.societies.api.internal.servicelifecycle.IServiceDiscoveryRemote;
import org.societies.api.internal.servicelifecycle.ServiceDiscoveryException;
import org.societies.api.internal.servicelifecycle.serviceRegistry.IServiceRegistry;
import org.societies.api.internal.servicelifecycle.serviceRegistry.exception.ServiceRetrieveException;
import org.societies.api.schema.servicelifecycle.model.Service;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;

public class ServiceDiscovery implements IServiceDiscovery {

	static final Logger logger = LoggerFactory.getLogger(ServiceDiscovery.class);

	private IServiceRegistry serviceReg;
	private ICommManager commMngr;
	private IServiceDiscoveryRemote serviceDiscoveryRemote;

	/**
	 * @return the commMngr
	 */
	public ICommManager getCommMngr() {
		return commMngr;
	}

	/**
	 * @return the serviceDiscoveryRemote
	 */
	public IServiceDiscoveryRemote getServiceDiscoveryRemote() {
		return serviceDiscoveryRemote;
	}

	/**
	 * @param serviceDiscoveryRemote the serviceDiscoveryRemote to set
	 */
	public void setServiceDiscoveryRemote(
			IServiceDiscoveryRemote serviceDiscoveryRemote) {
		this.serviceDiscoveryRemote = serviceDiscoveryRemote;
	}

	/**
	 * @param commMngr the commMngr to set
	 */
	public void setCommMngr(ICommManager commMngr) {
		this.commMngr = commMngr;
	}

	public IServiceRegistry getServiceReg() {
		return serviceReg;
	}

	public void setServiceReg(IServiceRegistry serviceReg) {
		this.serviceReg = serviceReg;
	}

	/* (non-Javadoc)
	 * @see org.societies.api.internal.servicelifecycle.IServiceDiscovery#getServices()
	 */
	@Override
	@Async
	public Future<List<Service>> getLocalServices() throws ServiceDiscoveryException {

		// TODO : Fix this up!
		INetworkNode currentNode = commMngr.getIdManager().getThisNetworkNode();

		Future<List<Service>> asyncResult = null;
		List<Service> result = null;

		asyncResult = this.getServices(currentNode);

		try {
			result = asyncResult.get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return new AsyncResult<List<Service>>(result);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.societies.api.internal.servicelifecycle.IServiceDiscovery#getServices
	 * (org.societies.api.comm.xmpp.datatypes.Identity)
	 */
	@Override
	@Async
	public Future<List<Service>> getServices(IIdentity node)
			throws ServiceDiscoveryException {
		List<Service>  serviceList = new ArrayList<Service>();

		try
		{
			//Object filter = "*.*"; //placeholder for a filter to all
			Service filter=new Service();
			
			switch (node.getType())
			{
			case CSS:
			case CSS_RICH:
			case CSS_LIGHT:
				//serviceList = getServiceReg().retrieveServicesSharedByCSS(node.getJid());
				//TODO : Temporary measure until retrieveServicesSharedByCSS implemented
				
				 serviceList= getServiceReg().findServices(filter);
				
				break;
			case CIS:

				//serviceList = getServiceReg().retrieveServicesSharedByCIS(node.getJid());
				//TODO : Temporary measure until retrieveServicesSharedByCIS implemented

				serviceList= getServiceReg().findServices(filter);
				
				break;

			default: 
				// No nothing, we will handle it below

			} 
		}catch (ServiceRetrieveException e)	{

			//TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {

			// TODO Auto-generated catch block
			e.printStackTrace();
		} 



		if (serviceList == null || serviceList.isEmpty())
		{
			IIdentity currentNode = commMngr.getIdManager().getThisNetworkNode();
			
			if (!currentNode.getJid().contentEquals(node.getJid()))
			{
				
				ServiceDiscoveryRemoteClient callback = new ServiceDiscoveryRemoteClient();
				
				getServiceDiscoveryRemote().getServices(node, callback); 
				serviceList = callback.getResultList();
				
			}
		}
		return new AsyncResult<List<Service>>(serviceList);




	}


}
