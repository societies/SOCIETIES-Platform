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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.CtxException;
import org.societies.api.context.event.CtxChangeEvent;
import org.societies.api.context.event.CtxChangeEventListener;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.context.model.CtxAttributeValueType;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.CtxOriginType;
import org.societies.api.context.source.ICtxSourceMgr;
import org.societies.api.context.source.CtxSourceNames;
import org.societies.api.identity.INetworkNode;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.context.location.management.api.IUserLocation;

/**
 * 
 * ToDO 
 * 1) The CSM API has to be changed to be able to update atrr on a specific CSS node
 * 2) Who creates the GPS attributes in the CSM ? 
 * Describe your class here...
 *
 * @author Guy Feigenblat (guyf@il.ibm.com)
 * 
 */
public class LocationManagementContextAccessor {

	/** The logging facility. */
	private static final Logger log = LoggerFactory.getLogger(LocationManagementContextAccessor.class);
	
	private final static String CSM_PZ_SOURCE = CtxSourceNames.PZ;
	private final static String CSM_GPS_SOURCE = CtxSourceNames.GPS;
	
	private final static String LOCATION_TYPE_FUSED = "location_fused";
	
	private ICtxSourceMgr contextSourceManagement;
	private ICtxBroker contextBroker;
	private ICommManager commManager;
	
	
	private final static Map<String,DeviceInternalObject> deviceMapping = new HashMap<String,DeviceInternalObject>(); 
	
	public void init(ICtxSourceMgr contextSourceManagement, ICtxBroker contextBroker, ICommManager commManager){
		if (contextSourceManagement == null || contextBroker == null || commManager == null){
			throw new NullPointerException("contextSourceManagement or contextBroker or commManager is NULL" );
		}
		this.contextSourceManagement = contextSourceManagement;
		this.contextBroker = contextBroker; 
		this.commManager = commManager;
	}
	
	public void addDevice(INetworkNode cssNodeId,String macAddress){
		try {
			CtxEntity ctxEntity = getCtxEntity(cssNodeId);
			
			Future<String> id = contextSourceManagement.register(cssNodeId,CSM_PZ_SOURCE, CtxAttributeTypes.LOCATION_COORDINATES);
			String csmLocationTypeGlobal_internalId = id.get();
			id = contextSourceManagement.register(cssNodeId,CSM_PZ_SOURCE, CtxAttributeTypes.LOCATION_SYMBOLIC);
			String csmLocationTypeSymbolic_internalId = id.get();
			
			id = contextSourceManagement.register(cssNodeId,CSM_PZ_SOURCE, CtxAttributeTypes.LOCATION_PUBLIC_TAGS);
			String csmLocationTypePublicTags_internalId = id.get();
			
			id = contextSourceManagement.register(cssNodeId,CSM_PZ_SOURCE, CtxAttributeTypes.LOCATION_PERSONAL_TAGS);
			String csmLocationTypePersonalTag_internalId = id.get();
			
			id = contextSourceManagement.register(cssNodeId,CSM_PZ_SOURCE, CtxAttributeTypes.LOCATION_ID);
			String csmLocationTypeZoneId_internalId = id.get();
			
			id = contextSourceManagement.register(cssNodeId,CSM_PZ_SOURCE, CtxAttributeTypes.LOCATION_TYPE);
			String csmLocationTypeZoneType_internalId = id.get();
			
			id = contextSourceManagement.register(cssNodeId,CSM_PZ_SOURCE, CtxAttributeTypes.LOCATION_PARENT_ID);
			String csmLocationTypeParent_internalId = id.get();
			
			
			addToDeviceMapping(macAddress,cssNodeId,
							   csmLocationTypeGlobal_internalId,csmLocationTypeSymbolic_internalId, 
							   csmLocationTypePublicTags_internalId,csmLocationTypePersonalTag_internalId,
							   csmLocationTypeZoneId_internalId,csmLocationTypeZoneType_internalId,
							   csmLocationTypeParent_internalId);
			
			createInferredLocationAttribute(ctxEntity);
			
			//currently only the Location coordinates can be changed by another source 
			contextBroker.registerForChanges(new MyCtxChangeEventListener(),ctxEntity.getId(),CtxAttributeTypes.LOCATION_COORDINATES);
			
			
		} catch (InterruptedException e) {
			log.error("Exception msg: "+e.getMessage()+" \t exception cause: "+e.getCause(),e);
		} catch (ExecutionException e) {
			log.error("Exception msg: "+e.getMessage()+" \t exception cause: "+e.getCause(),e);
		} catch (CtxException e) {
			log.error("Exception msg: "+e.getMessage()+" \t exception cause: "+e.getCause(),e);
		}
	}
	
	public void removeDevice(String macAddress){
		removeFromDeviceMapping(macAddress);
	}
	
	
	
	private void updatedFusedLocationAttr(CtxAttribute fusedAtrr,CtxAttribute gpsAttr,CtxAttribute pzAttr){
		CtxAttribute attrToBeUpdated;
		try{
			//gpsAttr = getAttribute(networkNode,CtxAttributeTypes.LOCATION_COORDINATES,CSM_GPS_SOURCE, CtxOriginType.SENSED);
			//pzAttr = getAttribute(networkNode,CtxAttributeTypes.LOCATION_COORDINATES, CSM_PZ_SOURCE, CtxOriginType.SENSED);
			
			attrToBeUpdated = pzAttr;
			
			if (gpsAttr != null && gpsAttr.getLastModified().after(pzAttr.getLastModified())){
				attrToBeUpdated = gpsAttr;
			}else{
				attrToBeUpdated = pzAttr;
			}
			
			fusedAtrr.setStringValue(attrToBeUpdated.getStringValue());
			try {
				contextBroker.update(fusedAtrr);
			} catch (CtxException e) {
				log.error("Exception msg: "+e.getMessage()+" \t exception cause: "+e.getCause(),e);
			}
		}catch (Exception e) {
			log.error("Exception msg: "+e.getMessage()+" \t exception cause: "+e.getCause(),e);
		}
	}
	
	public void updateCSM(IUserLocation userLocation,INetworkNode networkNode){
		try{
			String locationString = LMDataEncoding.encodeCoordinates(userLocation);
			String symbolicLocationString = LMDataEncoding.encodeLocationSymbolic(userLocation);
			DeviceInternalObject deviceInternalObject = getNodeObject(networkNode);
			CtxEntity ctxEntity = getCtxEntity(deviceInternalObject.getCssNodeId());
			
			String csmLocationTypeGlobal = deviceInternalObject.csmLocationTypeGlobal_internalId;
			String csmLocationTypeSymbolic = deviceInternalObject.csmLocationTypeSymbolic_internalId;
			contextSourceManagement.sendUpdate(csmLocationTypeGlobal,locationString, ctxEntity,false , 0, 0);
			contextSourceManagement.sendUpdate(csmLocationTypeSymbolic,symbolicLocationString, ctxEntity,false , 0, 0);
			
			
			String presonalTagValue = LMDataEncoding.encodePersonalTags(userLocation);
			String tagsValue = LMDataEncoding.encodePublicTags(userLocation);
			String zonesValue = LMDataEncoding.encodeZones(userLocation);
			String zoneTypeValue = LMDataEncoding.encodeZoneType(userLocation);
			String parentZoneValue = LMDataEncoding.encodeParentZones(userLocation);
			
			String csmLocationType_personalTag = deviceInternalObject.csmLocationTypePersonalTag_internalId;
			String csmLocationType_tags =   deviceInternalObject.csmLocationTypePublicTags_internalId;
			String csmLocationType_zonesId = 	deviceInternalObject.csmLocationTypeZoneId_internalId;
			String csmLocationType_zoneType = 	deviceInternalObject.csmLocationTypeZoneType_internalId;
			String csmLocationType_parentId = 	deviceInternalObject.csmLocationTypeParent_internalId;
			
			contextSourceManagement.sendUpdate(csmLocationType_personalTag,presonalTagValue, ctxEntity,false , 0, 0);
			contextSourceManagement.sendUpdate(csmLocationType_tags,tagsValue, ctxEntity,false , 0, 0);
			contextSourceManagement.sendUpdate(csmLocationType_zonesId,zonesValue, ctxEntity,false , 0, 0);
			contextSourceManagement.sendUpdate(csmLocationType_zoneType,zoneTypeValue, ctxEntity,false , 0, 0);
			contextSourceManagement.sendUpdate(csmLocationType_parentId,parentZoneValue, ctxEntity,false , 0, 0);
			
		}catch (Exception e) {
			log.error("Exception msg: "+e.getMessage()+" \t exception cause: "+e.getCause(),e);
		}
	}
	
	
	private void createInferredLocationAttribute(CtxEntity ctxEntity){
		try {
			/*
			CtxAttribute ctxAttribute=null;
			CtxIdentifier ctxIdentifier = ctxEntity.getId();
			Future<CtxModelObject> futureCtxModelObject;
			futureCtxModelObject = contextBroker.retrieve(ctxIdentifier);
			ctxAttribute = (CtxAttribute)futureCtxModelObject.get();
			CtxEntityIdentifier ctxEntityIdentifier = ctxAttribute.getScope();
			*/
			Future<List<CtxIdentifier>> futureAttributeIds = contextBroker.lookup(ctxEntity.getId(), CtxModelType.ATTRIBUTE, LOCATION_TYPE_FUSED);
			List<CtxIdentifier> attributeIds = futureAttributeIds.get();
			
			if (attributeIds.size() > 0){
				log.info("no need to create LOCATION_TYPE_FUSED attribute as it already exists");
				return;
			}
			
			CtxAttribute deviceTempAttr = contextBroker.createAttribute(ctxEntity.getId(),LOCATION_TYPE_FUSED).get();
			deviceTempAttr.setValueType(CtxAttributeValueType.STRING);
			deviceTempAttr.getQuality().setOriginType(CtxOriginType.INFERRED);
			deviceTempAttr.setStringValue("");
			deviceTempAttr.setSourceId("");
			contextBroker.update(deviceTempAttr);
		} catch (CtxException e) {
			log.error("Exception msg: "+e.getMessage()+" \t exception cause: "+e.getCause(),e);
		} catch (InterruptedException e) {
			log.error("Exception msg: "+e.getMessage()+" \t exception cause: "+e.getCause(),e);
		} catch (ExecutionException e) {
			log.error("Exception msg: "+e.getMessage()+" \t exception cause: "+e.getCause(),e);
		}
		
		
	}
	
	private class MyCtxChangeEventListener implements CtxChangeEventListener{

		@Override
		public void onCreation(CtxChangeEvent arg0) {
			//updateFusedAtrr(arg0);
			
		}

		@Override
		public void onModification(CtxChangeEvent arg0) {
			//updateFusedAtrr(arg0);
			
		}

		@Override
		public void onRemoval(CtxChangeEvent arg0) {
			//updateFusedAtrr(arg0);
		}

		@Override
		public void onUpdate(CtxChangeEvent arg0) {
			updateFusedAtrr(arg0);
		}
		
		private void updateFusedAtrr(CtxChangeEvent arg0){
			CtxAttribute ctxAttribute=null;
			CtxIdentifier ctxIdentifier = arg0.getId();
			Future<CtxModelObject> futureCtxModelObject;
			try {
				futureCtxModelObject = contextBroker.retrieve(ctxIdentifier);
				ctxAttribute = (CtxAttribute)futureCtxModelObject.get();
				CtxEntityIdentifier ctxEntityIdentifier = ctxAttribute.getScope();
				
				Future<List<CtxIdentifier>> futureAttributeIds = contextBroker.lookup(ctxEntityIdentifier, CtxModelType.ATTRIBUTE, LOCATION_TYPE_FUSED);
				List<CtxIdentifier> attributeIds = futureAttributeIds.get();
				
				if (attributeIds.size() > 1){
					log.error("ERROR !!! more than one location type fused attribute");
				}else if (attributeIds.size() == 0){
					log.error("ERROR !!!  LOCATION_TYPE_FUSED wasn't found");
					return;
				}
				
				futureCtxModelObject = contextBroker.retrieve (attributeIds.get(0));
				CtxAttribute locationTypeFusedAttribute = (CtxAttribute)futureCtxModelObject.get();
				
				
				futureAttributeIds = contextBroker.lookup(ctxEntityIdentifier, CtxModelType.ATTRIBUTE, CtxAttributeTypes.LOCATION_COORDINATES);
				attributeIds = futureAttributeIds.get();
				
				CtxAttribute gpsAttribute=null,pzAttribute=null,buffer;
				for (final CtxIdentifier attributeId : attributeIds){
					futureCtxModelObject = contextBroker.retrieve (attributeId);
					buffer = (CtxAttribute)futureCtxModelObject.get();
					
					if (ctxAttribute.getQuality().getOriginType() == CtxOriginType.SENSED){
						if (ctxAttribute.getSourceId().startsWith(CSM_GPS_SOURCE)) {
							gpsAttribute = buffer;
						}else if (ctxAttribute.getSourceId().startsWith(CSM_PZ_SOURCE)) {
							pzAttribute = buffer;
						}
					}
				}
				
				updatedFusedLocationAttr(locationTypeFusedAttribute,gpsAttribute,pzAttribute);
				
			} catch (CtxException e) {
				log.error("Exception msg: "+e.getMessage()+" \t exception cause: "+e.getCause(),e);
			} catch (InterruptedException e) {
				log.error("Exception msg: "+e.getMessage()+" \t exception cause: "+e.getCause(),e);
			} catch (ExecutionException e) {
				log.error("Exception msg: "+e.getMessage()+" \t exception cause: "+e.getCause(),e);
			}
		}
	}		

	/***** Helper methods *****/
	
	private INetworkNode getNetworkNodeByDevice(String macAddress){
		synchronized (deviceMapping) {
			return deviceMapping.get(macAddress).getCssNodeId();
		}
	}
	
	private DeviceInternalObject getNodeObject(INetworkNode networkNodeId){
		synchronized (deviceMapping) {
			for (Map.Entry<String, DeviceInternalObject> entry : deviceMapping.entrySet()){
				if (entry.getValue().getCssNodeId().equals(networkNodeId)){
					return entry.getValue();
				}
			}
		}
		
		log.error("Couldn't find  DeviceInternalObject for node - "+ networkNodeId);
		return null;
	}
	
	private void addToDeviceMapping(String macAddress, INetworkNode networkNodeId, 
									String csmLocationTypeGlobal_internalId, String csmLocationTypeSymbolic_internalId, 
									String csmLocationTypePublicTags_internalId, String csmLocationTypePersonalTag_internalId, 
									String csmLocationTypeZoneId_internalId, String csmLocationTypeZoneType_internalId, 
									String csmLocationTypeParent_internalId){
		
		synchronized (deviceMapping) {
			
			DeviceInternalObject deviceObject = new DeviceInternalObject();
			deviceObject.setCsmLocationTypeGlobal_internalId(csmLocationTypeGlobal_internalId);
			deviceObject.setCsmLocationTypeSymbolic_internalId(csmLocationTypeSymbolic_internalId);
			deviceObject.setCsmLocationTypePublicTags_internalId(csmLocationTypePublicTags_internalId);
			deviceObject.setCsmLocationTypePersonalTag_internalId(csmLocationTypePersonalTag_internalId);
			deviceObject.setCsmLocationTypeZoneId_internalId(csmLocationTypeZoneId_internalId);
			deviceObject.setCsmLocationTypeZoneType_internalId(csmLocationTypeZoneType_internalId);
			deviceObject.setCsmLocationTypeParent_internalId(csmLocationTypeParent_internalId);
			
			deviceObject.setCssNodeId(networkNodeId);
			deviceObject.setMacAddress(macAddress);
			
			deviceMapping.put(networkNodeId.getJid(),deviceObject);
		}
	}
	
	private void removeFromDeviceMapping(String macAddress){
		synchronized (deviceMapping) {
			deviceMapping.remove(macAddress);
		}
	}
	
	public  Collection<INetworkNode> getAllRegisteredEntites(){
		List<INetworkNode> networkNodes = new ArrayList<INetworkNode>();
		synchronized (deviceMapping) {
			for (Map.Entry<String, DeviceInternalObject> entries: deviceMapping.entrySet()){
				networkNodes.add(entries.getValue().getCssNodeId());
			}
		}
		return networkNodes;
	}
	
	
	/**
	 * Describe your class here...
	 *
	 * @author guyf
	 *
	 */
	private class DeviceInternalObject{
		private INetworkNode cssNodeId;
		private String macAddress;
		private String csmLocationTypeGlobal_internalId;
		private String csmLocationTypeSymbolic_internalId;
		private String csmLocationTypePublicTags_internalId;
		private String csmLocationTypePersonalTag_internalId;
		private String csmLocationTypeZoneType_internalId;
		private String csmLocationTypeZoneId_internalId;
		private String csmLocationTypeParent_internalId;
		
		public void setCsmLocationTypeZoneType_internalId(String csmLocationTypeZoneType_internalId) {
			this.csmLocationTypeZoneType_internalId = csmLocationTypeZoneType_internalId;
		}
		public INetworkNode getCssNodeId() {
			return cssNodeId;
		}
		public void setCssNodeId(INetworkNode cssNodeId) {
			this.cssNodeId = cssNodeId;
		}
		public void setMacAddress(String macAddress) {
			this.macAddress = macAddress;
		}
		public void setCsmLocationTypeGlobal_internalId(String csmLocationTypeGlobal_internalId) {
			this.csmLocationTypeGlobal_internalId = csmLocationTypeGlobal_internalId;
		}
		public void setCsmLocationTypeSymbolic_internalId(String csmLocationTypeSymbolic_internalId) {
			this.csmLocationTypeSymbolic_internalId = csmLocationTypeSymbolic_internalId;
		}
		public void setCsmLocationTypePublicTags_internalId(
				String csmLocationTypePublicTags_internalId) {
			this.csmLocationTypePublicTags_internalId = csmLocationTypePublicTags_internalId;
		}
		public void setCsmLocationTypePersonalTag_internalId(
				String csmLocationTypePersonalTag_internalId) {
			this.csmLocationTypePersonalTag_internalId = csmLocationTypePersonalTag_internalId;
		}
		public void setCsmLocationTypeZoneId_internalId(
				String csmLocationTypeZoneId_internalId) {
			this.csmLocationTypeZoneId_internalId = csmLocationTypeZoneId_internalId;
		}
		public void setCsmLocationTypeParent_internalId(String csmLocationTypeParent_internalId) {
			this.csmLocationTypeParent_internalId = csmLocationTypeParent_internalId;
		}
		
	}
	
	
	private CtxEntity getCtxEntity(INetworkNode cssNodeId) throws CtxException, InterruptedException, ExecutionException{
		Future<CtxEntity> futureCtxEntity = contextBroker.retrieveCssNode(cssNodeId);
		CtxEntity ctxEntity = futureCtxEntity.get();
		return ctxEntity;
	}

	/*
	private Requestor getRequestor(){
		IIdentity identity = getIIdentity();
		return new Requestor(identity);
	}
	
	private IIdentity getIIdentity(){
		IIdentityManager iIdentityManager;
		iIdentityManager = commManager.getIdManager();
		return iIdentityManager.getThisNetworkNode();
	}

	
	*/
	
	
}
