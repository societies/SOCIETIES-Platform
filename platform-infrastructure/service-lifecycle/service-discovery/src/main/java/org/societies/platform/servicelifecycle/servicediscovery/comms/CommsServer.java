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
package org.societies.platform.servicelifecycle.servicediscovery.comms;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.comm.xmpp.interfaces.IFeatureServer;
import org.societies.api.osgi.event.CSSEvent;
import org.societies.api.osgi.event.CSSEventConstants;
import org.societies.api.osgi.event.EventListener;
import org.societies.api.osgi.event.EventTypes;
import org.societies.api.osgi.event.IEventMgr;
import org.societies.api.osgi.event.InternalEvent;
import org.societies.api.schema.cis.community.Community;
import org.societies.api.schema.servicelifecycle.servicediscovery.ServiceDiscoveryMsgBean;
import org.societies.api.schema.servicelifecycle.servicediscovery.ServiceDiscoveryResultBean;
import org.societies.api.schema.servicelifecycle.model.Service;
import org.societies.api.identity.IIdentity;
import org.societies.api.internal.comm.ICISCommunicationMgrFactory;
import org.societies.api.internal.servicelifecycle.IServiceDiscovery;
import org.societies.api.internal.servicelifecycle.ServiceDiscoveryException;


public class CommsServer extends EventListener implements IFeatureServer {

	private static final List<String> NAMESPACES = Collections.unmodifiableList(
			  Arrays.asList("http://societies.org/api/schema/servicelifecycle/model",
				  		"http://societies.org/api/schema/servicelifecycle/servicediscovery"));
	private static final List<String> PACKAGES = Collections.unmodifiableList(
			  Arrays.asList("org.societies.api.schema.servicelifecycle.model",
						"org.societies.api.schema.servicelifecycle.servicediscovery"));
	
	//PRIVATE VARIABLES
	private static Logger LOG = LoggerFactory.getLogger(CommsServer.class);

	private ICommManager commManager;
	private IServiceDiscovery serviceDiscovery;
	private IEventMgr eventMgr;
	private ICISCommunicationMgrFactory cisCommMgrFactory;
	private String eventFilter;
	private String[] eventTypes;

	
	public ICISCommunicationMgrFactory getCisCommMgrFactory(){
		return cisCommMgrFactory;
	}
	
	public void setCisCommMgrFactory(ICISCommunicationMgrFactory cisCommMgrFactory){
		this.cisCommMgrFactory = cisCommMgrFactory;
	}
	
	public IEventMgr getEventMgr(){
		return eventMgr;
	}
	
	public void setEventMgr(IEventMgr eventMgr){
		this.eventMgr=eventMgr;
	}
	
	public void setCommMngr(ICommManager commMngr) {
		this.commManager = commMngr;
	}
	
	public ICommManager getCommMngr() {
		return commManager;
	}
	
	public void setServiceDiscovery(IServiceDiscovery serviceDiscovery) {
		this.serviceDiscovery = serviceDiscovery;
		
	}

	public IServiceDiscovery getServiceDiscovery() {
		return serviceDiscovery;
		
	}
	
	//METHODS
	public CommsServer() {
		if(LOG.isDebugEnabled())
			LOG.debug("Constructor: CommsService of SLM (Discovery)");
		
	}
	
	public void InitService() {
		//REGISTER OUR ServiceManager WITH THE XMPP Communication Manager
		try {
			if(LOG.isDebugEnabled())
				LOG.debug("InitService of SLM: CommsServer (Discovery)");
			getCommMngr().register(this); 
			
			if(LOG.isDebugEnabled())
				LOG.debug("Now registering for CIS Creation events");
			String eventSource = getCommMngr().getIdManager().getThisNetworkNode().getBareJid();
			eventFilter = "(&" +
			"(|(" + CSSEventConstants.EVENT_NAME + "=creation of CIS)("+ CSSEventConstants.EVENT_NAME + "=SLM_START))" + 
			"(|(" + CSSEventConstants.EVENT_SOURCE + "="+eventSource+")(" + CSSEventConstants.EVENT_SOURCE + "=org/societies/servicelifecycle))" +  
			")";
			eventTypes = new String[] {EventTypes.CIS_CREATION, EventTypes.SERVICE_LIFECYCLE_EVENT};
			getEventMgr().subscribeInternalEvent(this, eventTypes, eventFilter);
			
		} catch (CommunicationException e) {
			e.printStackTrace();
		}
	}
	
	public void killService(){
		// UnregisterStuff
		try{
			if(LOG.isDebugEnabled())
				LOG.debug("Unregistering from Comm Manager... HOW?");
			
			if(LOG.isDebugEnabled())
				LOG.debug("Unregistering from CIS Events...");
			getEventMgr().unSubscribeInternalEvent(this, eventTypes, eventFilter);
			
		} catch(Exception e){
			LOG.error("Exception removing Bean! :" + e.getMessage());
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
		if(LOG.isDebugEnabled())
			LOG.debug("receiveMessage in org.societies.platform.servicelifecycle.servicediscovery.comms");
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
						
						returnList = serviceDiscovery.getServices(stanza.getTo());
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
						returnList = serviceDiscovery.searchServices(serviceMessage.getService(), stanza.getTo());
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
		
		return null;
	}

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


	@Override
	public void handleInternalEvent(InternalEvent event) {
		if(LOG.isDebugEnabled())
			LOG.debug("Received an Internal Event: "+event.geteventName()+ ","+event.geteventSource()+","+event.geteventType()+ ": Time to check it!");
		
		if(event.geteventType().equals(EventTypes.CIS_CREATION)){
			Community eventInfo = (Community) event.geteventInfo();
			if(LOG.isDebugEnabled())
				LOG.debug("The created CIS has the JID: " + eventInfo.getCommunityJid());
			
			try {
				IIdentity cisIdentity = getCommMngr().getIdManager().fromJid(eventInfo.getCommunityJid());
				Map<IIdentity, ICommManager> allCis = getCisCommMgrFactory().getAllCISCommMgrs();
				
				ICommManager cisEndpoint = allCis.get(cisIdentity);
				
				if(LOG.isDebugEnabled())
					LOG.debug("Got the cisEndpoint of this CIS, now registering!");
				
				cisEndpoint.register(this);
				
			} catch (Exception e) {
				LOG.error("Exception while registering us in a new endpoint!");
				e.printStackTrace();
			}
		} else{
			
			if(event.geteventType().equals(EventTypes.SERVICE_LIFECYCLE_EVENT)){

				if(LOG.isDebugEnabled())
					LOG.debug("SLM Started, now registering our current CIS endpoints...");
				
				try {
					Map<IIdentity, ICommManager> allCis = getCisCommMgrFactory().getAllCISCommMgrs();
					
					for( IIdentity cisJid : allCis.keySet()){
						if(LOG.isDebugEnabled())
							LOG.debug("Registering endpoint for CIS: " + cisJid.getJid());
						allCis.get(cisJid).register(this);
					}
					
				} catch (Exception e) {
					LOG.error("Exception while registering existing CIS endpoints!");
					e.printStackTrace();
				}
				
			}
		}
			
	}

	/* (non-Javadoc)
	 * @see org.societies.api.osgi.event.EventListener#handleExternalEvent(org.societies.api.osgi.event.CSSEvent)
	 */
	@Override
	public void handleExternalEvent(CSSEvent event) {
		if(LOG.isDebugEnabled())
			LOG.debug("Received an External Event! It's a problem!");
		
	}
	
}
