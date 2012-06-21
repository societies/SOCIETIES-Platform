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
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.cis.management.ICisManager;
import org.societies.api.cis.management.ICisOwned;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.INetworkNode;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.internal.servicelifecycle.IServiceDiscovery;
import org.societies.api.internal.servicelifecycle.IServiceDiscoveryCallback;
import org.societies.api.internal.servicelifecycle.IServiceDiscoveryRemote;
import org.societies.api.internal.servicelifecycle.ServiceDiscoveryException;
import org.societies.api.internal.servicelifecycle.ServiceModelUtils;
import org.societies.api.internal.servicelifecycle.serviceRegistry.IServiceRegistry;
import org.societies.api.internal.servicelifecycle.serviceRegistry.exception.ServiceRetrieveException;
import org.societies.api.schema.servicelifecycle.model.Service;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;

/**
 * The implementation of Service Discovery
 *
 * @author mmanniox
 *
 */
public class ServiceDiscovery implements IServiceDiscovery {

	static final Logger logger = LoggerFactory.getLogger(ServiceDiscovery.class);

	private IServiceRegistry serviceReg;
	private ICommManager commMngr;
	private IServiceDiscoveryRemote serviceDiscoveryRemote;
	private ICisManager cisManager;
	
	public ICisManager getCisManager(){
		return cisManager;
	}
	
	public void setCisManager(ICisManager cisManager){
		this.cisManager = cisManager;
	}
	
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
	 * ()
	 */
	@Override
	@Async
	public Future<List<Service>> getServices(String jid)
			throws ServiceDiscoveryException {
		
		Future<List<Service>> asyncResult = null;
		List<Service> result = null;

		try {
			
			asyncResult = this.getServices(commMngr.getIdManager().fromJid(jid));

			result = asyncResult.get();
						
			} catch (InterruptedException e) {
					// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
					// TODO Auto-generated catch block
				e.printStackTrace();
		} catch (InvalidFormatException e) {
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

		if(logger.isDebugEnabled())
			logger.debug("getServices(Identity node) for node: " + node.getJid());
		
		boolean myNode;
		boolean myCIS = false;
		INetworkNode currentNode = commMngr.getIdManager().getThisNetworkNode();
		if (!currentNode.getJid().contentEquals(node.getJid()))
			myNode = false;
		else
			myNode = true;
		
		try
		{
			// Is it our node? If so, local search
			if(myNode){
				if(logger.isDebugEnabled())
					logger.debug("We're dealing with our own node!");
				serviceList = getServiceReg().retrieveServicesSharedByCSS(node.getJid());
			} else{
				//Is it one of my CIS? If so, local search
				ICisOwned localCis = getCisManager().getOwnedCis(node.getJid());
				if(localCis != null){
					if(logger.isDebugEnabled()) logger.debug("We're dealing with our CIS! Local search!");
					serviceList = getServiceReg().retrieveServicesSharedByCIS(node.getJid());
					myCIS = true;
				}
			}
			/*
			switch (node.getType())
			{
			case CSS:
			case CSS_RICH:
			case CSS_LIGHT:
				serviceList = getServiceReg().retrieveServicesSharedByCSS(node.getJid());
				break;
			case CIS:
				if(logger.isDebugEnabled()) logger.debug("Retrieving services of a CIS");
				ICisOwned myCis = getCisManager().getOwnedCis(node.getJid());
				if(myCis != null){
					if(logger.isDebugEnabled()) logger.debug("We're dealing with our CIS! Local search!");
					serviceList = getServiceReg().retrieveServicesSharedByCIS(node.getJid());			
				}
				break;
			default: 
				logger.warn("Unknown node!");
				break;
			}
			*/
		}catch (ServiceRetrieveException e)	{

			//TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {

			// TODO Auto-generated catch block
			e.printStackTrace();
		} 


		if (serviceList == null || serviceList.isEmpty())
		{
			if(logger.isDebugEnabled())
				logger.debug("No services retrieved from local node...");
			
			//IIdentity currentNode = commMngr.getIdManager().getThisNetworkNode();
			
			if (!myNode && !myCIS)
			{
				
				if(logger.isDebugEnabled())
					logger.debug("Attempting to retrieve services from remote node: " + node.getJid());
				
				ServiceDiscoveryRemoteClient callback = new ServiceDiscoveryRemoteClient();
				getServiceDiscoveryRemote().getServices(node, callback); 
				serviceList = callback.getResultList();
								
			}
		}
		
		// Quick log message
		if(logger.isDebugEnabled()){
						
			if(serviceList.isEmpty())	
				logger.debug("getServices: no services found!");
			else{				
				Iterator<Service> it = serviceList.iterator();
				String logStuff = "getServices: ";
		
				while(it.hasNext()){
					logStuff += it.next().getServiceName() + "; \n";
				}
							
				logger.debug(logStuff);
			}
		}
				
		return new AsyncResult<List<Service>>(serviceList);

	}

	@Override
	public Future<Service> getService(ServiceResourceIdentifier serviceId)
			throws ServiceDiscoveryException {

		if(logger.isDebugEnabled())
			logger.debug("Service Discovery::getService()");
			
		Service result = null;
		try{
			// First we check the local repository
			result = getServiceReg().retrieveService(serviceId);

			// Did we find it there?
			if(result == null){
				if(logger.isDebugEnabled()) 
					logger.debug("Didn't find service on local repository, now checking if it's a remote service!");
				
				String myLocalJid = getCommMngr().getIdManager().getThisNetworkNode().getJid();
				String serviceJid = ServiceModelUtils.getJidFromServiceIdentifier(serviceId);
				
				// Is it supposed to be local?
				if(!myLocalJid.equals(serviceJid)){
					
					if(logger.isDebugEnabled())
						logger.debug("It's a remote service from node: " + serviceJid);
					
					INetworkNode node = getCommMngr().getIdManager().fromFullJid(serviceJid);

					// We call the other network node to get the information on the service
					ServiceDiscoveryRemoteClient callback = new ServiceDiscoveryRemoteClient();
					getServiceDiscoveryRemote().getService(serviceId, node, callback);
					List<Service> resultList = callback.getResultList();
					
					// Only one service should be returned. If not, we're dealing with some sort of problem
					if(resultList.size() == 1){
						result = resultList.get(0);
						if(logger.isDebugEnabled()) logger.debug("Found service remotely!");
					}
					
				}
				
			}
			
		} catch(Exception ex){
			ex.printStackTrace();
			logger.error("getService():: Exception getting Service: " + ex);
			throw new ServiceDiscoveryException("getService():: Exception getting Service",ex);
		}
			
		if(result == null)
			return null;
		else
			return new AsyncResult<Service>(result);

	}

	@Override
	public Future<List<Service>> searchServices(Service filter)
			throws ServiceDiscoveryException {
		
		if(logger.isDebugEnabled()) logger.debug("Searching local repository for a given service");
		
		List<Service> result;
		
		try{
		
			result = getServiceReg().findServices(filter);
			
			if(logger.isDebugEnabled())
				logger.debug("Found "+ result.size() + " services that fulfill the criteria"); 
			
		} catch(Exception ex){
			ex.printStackTrace();
			logger.error("Searching for services: Exception! : " + ex);
			throw new ServiceDiscoveryException("Exception while searching for services",ex);
		}
		
		return new AsyncResult<List<Service>>(result);
	}

	@Override
	public Future<List<Service>> searchServices(Service filter, IIdentity node)
			throws ServiceDiscoveryException {

		if(logger.isDebugEnabled()) logger.debug("Searching repository for a given service, on node: " + node.getJid());
		List<Service> result = new ArrayList<Service>();
		
		try{
				
			String myLocalJid = getCommMngr().getIdManager().getThisNetworkNode().getJid();
			
			if(myLocalJid.equals(node.getJid())){
				if(logger.isDebugEnabled()) logger.debug("It's the local node, so we do a local call");
				return searchServices(filter);
			}
			
			if(logger.isDebugEnabled()) logger.debug("Trying to query the remote node...");
			
			ServiceDiscoveryRemoteClient callback = new ServiceDiscoveryRemoteClient();
			getServiceDiscoveryRemote().searchService(filter, node, callback);
			result = callback.getResultList();
		
		} catch(Exception ex){
			ex.printStackTrace();
			logger.error("Exception while searching for services!");
			throw new ServiceDiscoveryException("Exception while searching for services!",ex);
		}
		
		return new AsyncResult<List<Service>>(result);
	}

	@Override
	public Future<List<Service>> searchServices(Service filter, String jid)
			throws ServiceDiscoveryException {
		
		if(logger.isDebugEnabled()) logger.debug("Searching repository for a given service, on node: " + jid);

		try {
			
			INetworkNode node = getCommMngr().getIdManager().fromFullJid(jid);
			return searchServices(filter,node);

		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("Searching for services: Exception! : " + ex);
			throw new ServiceDiscoveryException("Exception while searching for services!",ex);
		}
			
	}


}
