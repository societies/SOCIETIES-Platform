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

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.CtxException;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.context.event.CtxChangeEvent;
import org.societies.api.context.event.CtxChangeEventListener;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.context.model.CtxAttributeValueType;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.CtxOriginType;
import org.societies.api.context.source.ICtxSourceMgr;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.Requestor;
import org.societies.context.api.user.location.IUserLocation;
import org.societies.context.api.user.location.IZone;


/**
 * 
 * ToDO 
 * 1) The CSM API has to be changed to be able to update atrr on a specific CSS node
 * 2) Find a way to maintain the JID corresponds to a mac address
 * 3) Who creates the GPS attributes in the CSM ? 
 * 4) Waiting for Nikollas to create a method that gets a JID and returns the CtxEntityIdentifier
 * Describe your class here...
 *
 * @author Guy Feigenblat (guyf@il.ibm.com)
 *
 */
public class LocationManagementContextAccessor {

	private final static String CSM_PZ_SOURCE = "PZ";
	private final static String CSM_GPS_SOURCE = "GPS";
	
	private final static String LOCATION_TYPE_FUSED = "location_fused";
	
	private ICtxSourceMgr contextSourceManagement;
	private ICtxBroker contextBroker;
	private ICommManager commManager;
	
	
	String csmLocationTypeGlobal_internalId = null;
	String csmLocationTypeSymbolic_internalId = null;
	
	
	public void init(ICtxSourceMgr contextSourceManagement, ICtxBroker contextBroker, ICommManager commManager){
		if (contextSourceManagement == null || contextBroker == null || commManager == null){
			throw new NullPointerException("contextSourceManagement or contextBroker or commManager is NULL" );
		}
		this.contextSourceManagement = contextSourceManagement;
		this.contextBroker = contextBroker; 
		this.commManager = commManager;
		
		try {
			Future<String> id = contextSourceManagement.register(CSM_PZ_SOURCE, CtxAttributeTypes.LOCATION_COORDINATES);
			csmLocationTypeGlobal_internalId = id.get();
			id = contextSourceManagement.register(CSM_PZ_SOURCE, CtxAttributeTypes.LOCATION_SYMBOLIC);
			csmLocationTypeSymbolic_internalId = id.get();
					
			createInferredLocationAttribute();
			
			try {
				//TODO example
				//contextBroker.registerForChanges(new MyCtxChangeEventListener(),"Blue method",CtxAttributeTypes.LOCATION_COORDINATES);
				contextBroker.registerForChanges(new MyCtxChangeEventListener(),null,CtxAttributeTypes.LOCATION_COORDINATES);
			} catch (CtxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	private CtxAttribute getAttribute(String attrName, String attrSourceId,CtxOriginType ctxOriginType){
		Future<CtxModelObject> futureCtxModelObject;
		CtxAttribute ctxAttribute=null;
		try{
			Future<List<CtxIdentifier>> futureList = contextBroker.lookup(CtxModelType.ATTRIBUTE,attrName);
			List<CtxIdentifier> ctxIdentifiers= futureList.get();
			
			for (CtxIdentifier ctxIdentifier : ctxIdentifiers){
				futureCtxModelObject = contextBroker.retrieve (ctxIdentifier);
				ctxAttribute = (CtxAttribute)futureCtxModelObject.get();
				
				if (ctxAttribute.getSourceId().startsWith(attrSourceId) &&
					ctxAttribute.getQuality().getOriginType() == ctxOriginType ){
						return ctxAttribute;
				}
			}
		}catch (CtxException e) {
			e.printStackTrace();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	private void updatedFusedLocationAttr(){
		CtxAttribute gpsAttr,pzAttr, fusedAttrTobeUpdated, fusedAtrr;
		
		gpsAttr = getAttribute(CtxAttributeTypes.LOCATION_COORDINATES,CSM_GPS_SOURCE, CtxOriginType.SENSED);
		pzAttr = getAttribute(CtxAttributeTypes.LOCATION_COORDINATES, CSM_PZ_SOURCE, CtxOriginType.SENSED);
		fusedAtrr = getAttribute(LOCATION_TYPE_FUSED,"",CtxOriginType.INFERRED);
		
				
		if (gpsAttr.getLastModified().after(pzAttr.getLastModified())){
			fusedAttrTobeUpdated = gpsAttr;
		}else{
			fusedAttrTobeUpdated = pzAttr;
		}
		
		fusedAtrr.setStringValue(fusedAttrTobeUpdated.getStringValue());
		try {
			contextBroker.update(fusedAtrr);
		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public void updateCSM(IUserLocation userLocation){
		String locationString= "";
		String symbolicLocationString= "";
		
		locationString = gpsToString(userLocation);
		//CtxAttributeTypes.LOCATION_COORDINATES
		contextSourceManagement.sendUpdate(csmLocationTypeGlobal_internalId,locationString, null,false , 0, 0);
		
		symbolicLocationString = zonesToStringEncoding(userLocation);
		contextSourceManagement.sendUpdate(csmLocationTypeSymbolic_internalId,symbolicLocationString, null,false , 0, 0);
	}
	

	private void createInferredLocationAttribute(){
		try {
			//TODO sending the ID instead of null -the ID should be retrieved using a method that will generate it from the JID 
			//CtxAttribute deviceTempAttr = contextBroker.createAttribute("blue method",LOCATION_TYPE_FUSED).get();
			CtxAttribute deviceTempAttr = contextBroker.createAttribute(null,LOCATION_TYPE_FUSED).get();
			deviceTempAttr.setValueType(CtxAttributeValueType.STRING);
			deviceTempAttr.getQuality().setOriginType(CtxOriginType.INFERRED);
			deviceTempAttr.setStringValue("");
			deviceTempAttr.setSourceId("");
			
			contextBroker.update(deviceTempAttr);
			
		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	private class MyCtxChangeEventListener implements CtxChangeEventListener{

		@Override
		public void onCreation(CtxChangeEvent arg0) {
			updateFusedAtrr(arg0);
			
		}

		@Override
		public void onModification(CtxChangeEvent arg0) {
			updateFusedAtrr(arg0);
			
		}

		@Override
		public void onRemoval(CtxChangeEvent arg0) {
			updateFusedAtrr(arg0);
		}

		@Override
		public void onUpdate(CtxChangeEvent arg0) {
			updateFusedAtrr(arg0);
		}
		
		private void updateFusedAtrr(CtxChangeEvent arg0){
			
			CtxAttribute ctxAttribute=null;
			CtxIdentifier ctxIdentifier = arg0.getId();
			try {
				Future<CtxModelObject> futureCtxModelObject;
				futureCtxModelObject = contextBroker.retrieve(ctxIdentifier);
				ctxAttribute = (CtxAttribute)futureCtxModelObject.get();
				
				//update only if the SENSED changed 
				if (ctxAttribute.getQuality().getOriginType() == CtxOriginType.SENSED){
					updatedFusedLocationAttr();
				}
				
			} catch (CtxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

	/***** Helper methods *****/
	private String zonesToStringEncoding(IUserLocation userLocation){
		String symbolicLocationString = "";
		for (IZone zone : userLocation.getZones()){
			symbolicLocationString+= symbolicLocationString += zone.getId() + ",";
		}
		if (symbolicLocationString.length() > 0){
			symbolicLocationString = symbolicLocationString.substring(0, symbolicLocationString.length()-1);
		}
		return symbolicLocationString;
	}
	
	private String gpsToString(IUserLocation userLocation){
		return userLocation.getXCoordinate().toString()+","+userLocation.getYCoordinate().toString();
		
	}
	/*
	private Requestor getRequestor(){
		IIdentity identity = getIIdentity();
		return new Requestor(identity);
	}*/
	
	
	private IIdentity getIIdentity(){
		IIdentityManager iIdentityManager;
		iIdentityManager = commManager.getIdManager();
		return iIdentityManager.getThisNetworkNode();
	}

}
