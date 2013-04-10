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
package org.societies.context.location.management.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.comm.xmpp.pubsub.PubsubClient;
import org.societies.api.context.source.ICtxSourceMgr;
import org.societies.api.identity.INetworkNode;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.internal.comm.ICommManagerController;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.context.location.management.PZWrapper;
import org.societies.context.location.management.PzPropertiesReader;
import org.societies.context.location.management.api.ILocationManagementAdapter;
import org.societies.context.location.management.api.IUserLocation;
import org.societies.context.location.management.api.IZone;
import org.societies.context.location.management.api.IZoneId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class LMAdapterImpl implements ILocationManagementAdapter {
	
	/** The logging facility. */
	private static final Logger log = LoggerFactory.getLogger(LMAdapterImpl.class);
	
	
	private final Timer timer = new Timer();
	private AtomicInteger updateTaskFailures = new AtomicInteger(0);
	
	private LocationManagementContextAccessor locationInference;
	private ICtxSourceMgr contextSourceManagement;
	private ICtxBroker contextBroker;
	private ICommManager commManager;
	private ICommManagerController commMngrController;
	private PubsubClient pubSubManager; 
	//private IDeviceRegistry deviceRegistry;
	//private ICSSInternalManager cssInternalManager;
	
	@Autowired
	private PZWrapper pzWrapper; 
	
	HashSet<String> registeredDevices = new HashSet<String>();
	
	private int contextUpdateInterval;
	private int updateTaskMaxFailures;
	
	@SuppressWarnings("unused")
	private void init(){
		
		try{
			locationInference = new LocationManagementContextAccessor();
			locationInference.init(contextSourceManagement, contextBroker, commManager);
			
			LMConfiguratorImpl lmConfiguratorImpl = new LMConfiguratorImpl();
			lmConfiguratorImpl.init(pubSubManager, commManager,commMngrController, this);
			
			contextUpdateInterval = PzPropertiesReader.instance().getUpdateCycle();
			updateTaskMaxFailures = PzPropertiesReader.instance().getPzUpdateTaskMaxFailures();
			
			
			if (pzWrapper.isLocationSystemActive()){
				timer.scheduleAtFixedRate(new UpdateTask(),contextUpdateInterval, contextUpdateInterval);
			}else{
				log.warn("Notice --> Location system isn't active and therfore location update task is disabled. To activate it, please refer to the Societies platform properties file");
			}
			
		}catch (Exception e) {
			log.error("Exception msg: "+e.getMessage()+" ; Cause: "+e.getCause(),e);
		}
	}
	
	@SuppressWarnings("unused")
	private void cleanup(){
		try{
			timer.cancel();
		}catch (Exception e) {
			log.error("Exception in bean'cleanup' method ; msg: "+e.getMessage()+" ; Cause: "+e.getCause(),e);
		}
	}
	
	@Override
	public Set<String> getActiveEntitiesIdsInZone(IZoneId arg0) {
		return pzWrapper.getActiveEntitiesIdsInZone(arg0);
	}
	

	@Override
	public Collection<IZone> getActiveZones() {
		return pzWrapper.getActiveZones();
	}

	@Override
	public IUserLocation getEntityFullLocation(String entityId) {
		return pzWrapper.getEntityFullLocation(entityId);
		
	}
	
	private Collection<INetworkNode> getCSSnodesFromCssManager(){
		/*
		Future<CssInterfaceResult> futureCssRecord = cssInternalManager.getCssRecord();
		CssInterfaceResult cssInterfaceResult= null;
		List<CssNode> cssNodes;
		
		List<INetworkNode> networkNodes = new ArrayList<INetworkNode>();
		
		try {
			cssInterfaceResult = (CssInterfaceResult)futureCssRecord.get();
			CssRecord cssRecord = cssInterfaceResult.getProfile();
			cssNodes = cssRecord.getCssNodes();
			for (CssNode cssNode: cssNodes){
				try {
					INetworkNode networkNode = commManager.getIdManager().fromFullJid(cssNode.getIdentity());
					networkNodes.add(networkNode);
					
				} catch (InvalidFormatException e) {
					log.error("Couldn't convert '"+cssNode.getIdentity()+"' to INetworkNode");
				}
			}
		} catch (Exception e) {
			log.error("Error from cssLocalManager ",e);
		}
		*/
		//TODO: TEMP !!! adding this network node
		List<INetworkNode> networkNodes = new ArrayList<INetworkNode>();
		networkNodes.add(commManager.getIdManager().getThisNetworkNode());
		
		return networkNodes;
	}
	
	
	@Override
	public void registerCSSdevice(String entityId,String deviceId,String macAddress) {
		INetworkNode networkNode;
		try {
			networkNode = commManager.getIdManager().fromFullJid(entityId);
			locationInference.addDevice(networkNode, macAddress);
			
		} catch (InvalidFormatException e) {
			log.error("Exception msg: "+e.getMessage()+" ; Cause: "+e.getCause(),e);
		}
		
	}

	@Override
	public void removeCSSdevice(String entityId,String deviceId,String macAddress) {
		locationInference.removeDevice(macAddress);
		
	}
	
	private class UpdateTask extends TimerTask{
		@Override
		public void run() {
			log.debug("---------------- Update task started");
			
			IUserLocation userLocation=null;
			try{
				
				Collection<INetworkNode> registeredEntities =  getCSSnodesFromCssManager();	//locationInference.getAllRegisteredEntites();
				for (INetworkNode networkNode : registeredEntities){
					String jid = networkNode.getJid();
					if (!registeredDevices.contains(jid)){
						registerCSSdevice(jid, "", "00:00:00:00:00");
						registeredDevices.add(jid);
					}
				}
				
				for (INetworkNode networkNode : registeredEntities){
					userLocation = getEntityFullLocation(networkNode.getJid());
					if (userLocation != null){
						log.debug("update CSM node - "+networkNode.getJid()+" \t location: "+userLocation.toString());
						locationInference.updateCSM(userLocation, networkNode,contextUpdateInterval);
						
						//reset failures counters
						updateTaskFailures.set(0);
					}else{
						/*
						//increment failures counters
						updateTaskFailures.incrementAndGet();
						log.error("update CSM failed! - This is the '"+updateTaskFailures.get() +"'th attempt ;  entity '"+networkNode.getJid());
						
						//cancel the timer task if too many failures
						if (updateTaskFailures.get() > updateTaskMaxFailures){
							log.error("Location Management - Update CSM failed in the '"+updateTaskFailures.get() +"' attempt ; stopping task !");
							timer.cancel();
						}
						*/
					}
				}
			}catch (IllegalStateException e) {
				log.error("IllegalStateException; Probably because the bundle was unistalled - canceling timer task);  Msg: "
						   +e.getMessage()+" \t; cause:  "+e.getCause(),e);
				timer.cancel();
			}catch (Exception e) {
				log.error("Error in update task; Msg: "+e.getMessage()+" \t; cause:  "+e.getCause(),e);
			}
			log.debug("--------------------- Update task finished");
		}
		
	}
	
		
	/*
	 * Getters / Setters
	 * 
	 */
	public ICtxSourceMgr getContextSourceManagement() {
		return contextSourceManagement;
	}
	
	public void setContextSourceManagement(ICtxSourceMgr contextSourceManagement) {
		this.contextSourceManagement = contextSourceManagement;
	}
	
	public ICtxBroker getContextBroker() {
		return contextBroker;
	}


	public void setContextBroker(ICtxBroker contextBroker) {
		this.contextBroker = contextBroker;
	}
	
	public ICommManager getCommManager() {
		return commManager;
	}

	public void setCommManager(ICommManager commManager) {
		this.commManager = commManager;
	}

	public PubsubClient getPubSubManager() {
		return pubSubManager;
	}

	public void setPubSubManager(PubsubClient pubSubManager) {
		this.pubSubManager = pubSubManager;
	}
/*
	public IDeviceRegistry getDeviceRegistry() {
		return deviceRegistry;
	}

	public void setDeviceRegistry(IDeviceRegistry deviceRegistry) {
		this.deviceRegistry = deviceRegistry;
	}
*/
	public PZWrapper getPzWrapper() {
		return pzWrapper;
	}

	public ICommManagerController getCommMngrController() {
		return commMngrController;
	}

	public void setCommMngrController(ICommManagerController commMngrController) {
		this.commMngrController = commMngrController;
	}
/*
	public ICSSInternalManager getCssInternalManager() {
		return cssInternalManager;
	}

	public void setCssInternalManager(ICSSInternalManager cssLocalManager) {
		this.cssInternalManager = cssInternalManager;
	}*/

	@Autowired
	public void setPzWrapper(PZWrapper pzWrapper) {
		this.pzWrapper = pzWrapper;
	}
}
