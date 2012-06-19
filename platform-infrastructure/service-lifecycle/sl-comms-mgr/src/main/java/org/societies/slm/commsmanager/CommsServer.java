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
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.comm.xmpp.interfaces.IFeatureServer;
import org.societies.api.schema.servicelifecycle.servicediscovery.ServiceDiscoveryMsgBean;
import org.societies.api.schema.servicelifecycle.servicediscovery.ServiceDiscoveryResultBean;
import org.societies.api.schema.servicelifecycle.servicecontrol.ServiceControlMsgBean;
import org.societies.api.schema.servicelifecycle.servicecontrol.ServiceControlResult;
import org.societies.api.schema.servicelifecycle.servicecontrol.ResultMessage;
import org.societies.api.schema.servicelifecycle.servicecontrol.ServiceControlResultBean;
import org.societies.api.schema.servicelifecycle.model.Service;
import org.societies.api.internal.servicelifecycle.IServiceControl;
import org.societies.api.internal.servicelifecycle.IServiceDiscovery;
import org.societies.api.internal.servicelifecycle.ServiceDiscoveryException;


public class CommsServer implements IFeatureServer {

	private static final List<String> NAMESPACES = Collections.unmodifiableList(
			  Arrays.asList("http://societies.org/api/schema/servicelifecycle/model",
				  		"http://societies.org/api/schema/servicelifecycle/servicediscovery",
				  		"http://societies.org/api/schema/servicelifecycle/servicecontrol"));
	private static final List<String> PACKAGES = Collections.unmodifiableList(
			  Arrays.asList("org.societies.api.schema.servicelifecycle.model",
						"org.societies.api.schema.servicelifecycle.servicediscovery",
						"org.societies.api.schema.servicelifecycle.servicecontrol"));
	
	//PRIVATE VARIABLES
	private ICommManager commManager;
	private IServiceDiscovery serviceDiscovery;
	private IServiceControl serviceControl;
	private static Logger LOG = LoggerFactory.getLogger(CommsServer.class);

	
	public ICommManager getCommManager() {
		return commManager;
	}

	public void setCommManager(ICommManager commManager) {
		this.commManager = commManager;
	}
	
	public IServiceDiscovery getServiceDiscovery() {
		return serviceDiscovery;
		
	}
	
	public void setServiceDiscovery(IServiceDiscovery serviceDiscovery) {
		this.serviceDiscovery = serviceDiscovery;
		
	}
	
	public IServiceControl getServiceControl(){
		return serviceControl;
	}
	
	public void setServiceControl(IServiceControl serviceControl){
		this.serviceControl = serviceControl;
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
	

	@Override
	public List<String> getJavaPackages() {
		return PACKAGES;
	}
	
	@Override
	public List<String> getXMLNamespaces() {
		return NAMESPACES;
	}
	
	
	
	/* Put your functionality here if there is NO return object, ie, VOID  */
	@Override
	public void receiveMessage(Stanza stanza, Object payload) {
		
	}

	/* Put your functionality here if there IS a return object */
	@Override
	public Object getQuery(Stanza stanza, Object payload) throws XMPPError {

		if(LOG.isDebugEnabled()) LOG.debug("getQuery: Received a message!");
		
		if (payload.getClass().equals(ServiceDiscoveryMsgBean.class)) {
			
			if(LOG.isDebugEnabled()) LOG.debug("Remote call to Service Discovery");
			
			ServiceDiscoveryMsgBean serviceMessage = (ServiceDiscoveryMsgBean) payload;
			ServiceDiscoveryResultBean serviceResult = new ServiceDiscoveryResultBean(); 
			
				
			Future<List<Service>> returnList = null;
			List<Service> resultBeanList = serviceResult.getServices();
			List<Service> resultList = null;
			
			try
			{
				switch (serviceMessage.getMethod()) {
					case GET_LOCAL_SERVICES :
					{
						
						returnList = serviceDiscovery.getLocalServices();
						resultList =  returnList.get();
						
						
						if (resultList != null)
						{
							for (int i = 0; i < resultList.size(); i++)
							{
								resultBeanList.add(resultList.get(i));
							}
						}
						break;
					}
					
					case GET_SERVICE:
					{
						Future<Service> futureFoundService = serviceDiscovery.getService(serviceMessage.getServiceId());
						Service foundService = futureFoundService.get();
						if(foundService != null)
							resultBeanList.add(foundService);
						
						break;
					}
					
					case SEARCH_SERVICE:
					{
						returnList = serviceDiscovery.getLocalServices();
						resultList =  returnList.get();
							
						if (resultList != null)
						{
							for (int i = 0; i < resultList.size(); i++)
							{
								resultBeanList.add(resultList.get(i));
							}
						}
						break;
					}
				}
			}catch (ServiceDiscoveryException e) {
					e.printStackTrace();
			} catch (Exception e) {
					e.printStackTrace();
			};
									
			//RETURN MESSAGEBEAN RESULT
			return serviceResult;
			
		}
		
		
		// Is it Service Control?
		
		if (payload.getClass().equals(ServiceControlMsgBean.class)) {
			
			if(LOG.isDebugEnabled()) LOG.debug("Remote call to Service Control");

			
			ServiceControlMsgBean serviceMessage = (ServiceControlMsgBean) payload;
			ServiceControlResultBean serviceResult = new ServiceControlResultBean(); 
			
				
			Future<ServiceControlResult> controlResult = null;
	
			try
			{

				switch (serviceMessage.getMethod()) {
					case START_SERVICE :
					{											
						if(LOG.isDebugEnabled()) LOG.debug("Remote call to Service Control: START SERVICE");
														
						controlResult = getServiceControl().startService(serviceMessage.getServiceId());
						ServiceControlResult result = controlResult.get();
						
						if(LOG.isDebugEnabled()) LOG.debug("Result was: " + result);
						
						serviceResult.setControlResult(result);
						break;
			
					}
					case STOP_SERVICE:
					{
						if(LOG.isDebugEnabled()) LOG.debug("Remote call to Service Control: STOP SERVICE");
								
						controlResult = getServiceControl().stopService(serviceMessage.getServiceId());
						ServiceControlResult result = controlResult.get();
						
						if(LOG.isDebugEnabled()) LOG.debug("Result was: " + result);
						
						serviceResult.setControlResult(result);
						break;
					}
					case INSTALL_SERVICE:
					{
						if(LOG.isDebugEnabled()) LOG.debug("Remote call to Service Control: INSTALL SERVICE");
								
						controlResult = getServiceControl().installService(serviceMessage.getURL().toURL());
						ServiceControlResult result = controlResult.get();
						
						if(LOG.isDebugEnabled()) LOG.debug("Result was: " + result);
						
						serviceResult.setControlResult(result);
						break;
					}
					case UNINSTALL_SERVICE:
					{
						if(LOG.isDebugEnabled()) LOG.debug("Remote call to Service Control: UNINSTALL SERVICE");
								
						controlResult = getServiceControl().uninstallService(serviceMessage.getServiceId());
						ServiceControlResult result = controlResult.get();
						
						if(LOG.isDebugEnabled()) LOG.debug("Result was: " + result);
						
						serviceResult.setControlResult(result);
						break;
					}
					case SHARE_SERVICE:
					{
						if(LOG.isDebugEnabled()) LOG.debug("Remote call to Service Control: SHARE SERVICE");
								
						controlResult = getServiceControl().shareService(serviceMessage.getService(), serviceMessage.getShareJid());
						ServiceControlResult result = controlResult.get();
						
						if(LOG.isDebugEnabled()) LOG.debug("Result was: " + result);
						
						serviceResult.setControlResult(result);
						break;
					}
					case UNSHARE_SERVICE:
					{
						if(LOG.isDebugEnabled()) LOG.debug("Remote call to Service Control: UNSHARE SERVICE");
								
						controlResult = getServiceControl().unshareService(serviceMessage.getService(), serviceMessage.getShareJid());
						ServiceControlResult result = controlResult.get();
						
						if(LOG.isDebugEnabled()) LOG.debug("Result was: " + result);
						
						serviceResult.setControlResult(result);
						break;
					}
					default:
						ServiceControlResult result = new ServiceControlResult();
						result.setServiceId(serviceMessage.getServiceId());
						result.setMessage(ResultMessage.COMMUNICATION_ERROR);
						serviceResult.setControlResult(result);
				}
			} catch (Exception e) {
				LOG.error("Exception: " + e);
				e.printStackTrace();
				ServiceControlResult result = new ServiceControlResult();
				result.setServiceId(serviceMessage.getServiceId());
				result.setMessage(ResultMessage.EXCEPTION_ON_REMOTE);
				serviceResult.setControlResult(result);
			};
				
			//RETURN MESSAGEBEAN RESULT
			return serviceResult;
			
		}
		
		
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
	
	protected void registerCISendpoint(ICommManager endpoint){
		if(LOG.isDebugEnabled())
			LOG.debug("New CIS created, so we need to register to its endpoint!");
		try{
			endpoint.register(this);
		} catch(Exception ex){
			ex.printStackTrace();
			LOG.error("Error / Exception when registering new endpoint!");
		}
	}
	
}
