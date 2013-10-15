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
package org.societies.webapp.controller.services;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.cis.management.ICis;
import org.societies.api.identity.IIdentity;
import org.societies.api.internal.servicelifecycle.ServiceModelUtils;
import org.societies.api.schema.css.directory.CssAdvertisementRecord;
import org.societies.api.schema.servicelifecycle.model.Service;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.api.schema.servicelifecycle.model.ServiceStatus;
import org.societies.api.schema.servicelifecycle.model.ServiceType;
/**
 * Describe your class here...
 *
 * @author 10036469
 *
 */
public class ServiceWrapper {

	private Service service;
	private ServicesController controller;
	private List<ICis> mySharedCis;
	private String ownerName;
	private String sharedBy;
	private List<String> sharedCisList;
	private boolean mine;

	private static final Logger log = LoggerFactory.getLogger(ServiceWrapper.class);
	
	public ServiceWrapper(Service service, ServicesController controller) {
		log.debug("ServiceWrapper created for Service: {}", service.getServiceName());
		this.service = service;
		this.controller = controller;
		this.mySharedCis = new ArrayList<ICis>();
		this.ownerName = null;
		this.sharedBy = null;
		this.sharedCisList = null;
		this.mine = ServiceModelUtils.isServiceOurs(service, controller.getCommManager());
		
	}
	
	public Service getService(){
		return service;
	}
	
	public void setService(Service service){
		this.service = service;
		if(log.isDebugEnabled())
			log.debug("ServiceWrapper changed for for Service: " + service.getServiceName());
	}
	public String getName(){
		return service.getServiceName();
	}
	
	public String getDescription(){
		return service.getServiceDescription();
	}
	
	public String getCategory(){
		return service.getServiceCategory();
	}
	
	public ServiceStatus getStatus(){
		return service.getServiceStatus();
	}
	
	public void setServiceStatus(ServiceStatus serviceStatus){
		this.service.setServiceStatus(serviceStatus);
	}
	
	public boolean isStarted(){
		return service.getServiceStatus().equals(ServiceStatus.STARTED);
	}
	
	public boolean isStopped(){
		return service.getServiceStatus().equals(ServiceStatus.STOPPED);
	}
	
	public String getType(){
		switch(service.getServiceType()){
		case DEVICE: return "Device";
		case THIRD_PARTY_ANDROID: return "Android";
		case THIRD_PARTY_CLIENT: return "Service Client";
		default: return "Service";
		}
	}
	
	public String getCreator(){
		return service.getAuthorSignature();
	}
	
	public String getOwnerJid(){
		return ServiceModelUtils.getJidFromServiceIdentifier(service.getServiceIdentifier());
	}
	
	public String getEndpoint(){
		return service.getServiceEndpoint();
	}
	
	public String getOwnerName(){
		if(ownerName == null){
			/*List<String> cssIdList = new ArrayList();
			cssIdList.add(getOwnerJid());
			try{
				List<CssAdvertisementRecord> recordList = controller.getCssDirectory().searchByID(cssIdList).get();
				if(!recordList.isEmpty()){
					ownerName = recordList.get(0).getName();
				}
			} catch(Exception ex){
				log.error("Exception occured {}", ex.getMessage());
				ex.printStackTrace();
			}*/
			if(ownerName == null || "".equals(ownerName)){
				try{
					controller.getCommManager().getIdManager().fromJid(getOwnerJid()).getIdentifier();
				} catch(Exception ex){
					log.error("Exception occured {}", ex.getMessage());
					ex.printStackTrace();
					ownerName = getOwnerJid();
				}
			}		
			
		}
		return ownerName;
	}
	
	public String getSharedByJid(){
		return service.getServiceInstance().getParentJid();
	}
	
	public String getSharedBy(){
		if(sharedBy == null || "".equals(sharedBy)){
			try{
				sharedBy = controller.getCommManager().getIdManager().fromJid(getSharedByJid()).getIdentifier();
			} catch(Exception ex){
				log.error("Exception occured {}", ex.getMessage());
				ex.printStackTrace();
				sharedBy = getSharedByJid();
			}
		}		
			
		return sharedBy;
		
	}
	
	public boolean isShared(){
		if(!isMine())
			return true;
		else
			if(getSharedCisId().size() > 0)
				return true;
			else
				return false;
		
	}
	
	public boolean isSharedWithCis(String node){
		return getSharedCisId().contains(node);
	}
	
	public boolean isInstalled(){
		
		if(isMine())
			return true;
		else{
			return controller.getThirdClients().containsValue(getId());
		}
	}
	
	public boolean isCanShare(){
		if(service.getServiceType().equals(ServiceType.THIRD_PARTY_CLIENT) || !isMine())
			return false;
		
		if(service.getServiceType().equals(ServiceType.THIRD_PARTY_SERVER) && service.getServiceInstance().getServiceImpl().getServiceClient() != null )
			return true;

		if(service.getServiceType().equals(ServiceType.DEVICE))
			return true;
		
		return false;
			
	}
	
	public List<ICis> getSharedCis(){
		try {
			//if(mySharedCis == null)
				mySharedCis =  controller.getServiceControl().getCisServiceIsSharedWith(service.getServiceIdentifier()).get();
			//else
				return mySharedCis;
		} catch (InterruptedException e) {
			log.error("Exception!");
			e.printStackTrace();
		} catch (ExecutionException e) {
			log.error("Exception!");
			e.printStackTrace();
		}
		
		return new ArrayList<ICis>();
	}
	
	public void setSharedCis(List<ICis> cisList){
		mySharedCis = cisList;
	}
	
	public List<String> getSharedCisId(){
		if(sharedCisList == null){
			sharedCisList = new ArrayList<String>();
			List<ICis> cisList = getSharedCis();
			for(ICis myCis : cisList){
				sharedCisList.add(myCis.getCisId());
			}
		}
		
		return sharedCisList;
	}
	
	public void setSharedCisId(List<String> cisListId){
		
		if(cisListId == null){
			sharedCisList = null;
			return;
		}
		
		if(sharedCisList.size() ==  cisListId.size())
			return;
		
		//log.debug("Set Shared CIS, previous {}, now {}", sharedCisList.size(), cisListId.size());

		if(sharedCisList.size() > cisListId.size()){
			log.debug("{}: We've stopped sharing with a CIS!",getName());
			String removedCis = null;
			for(String sharedCis : sharedCisList){
				if(!cisListId.contains(sharedCis))
					removedCis = sharedCis;
			}
			if(removedCis != null){
				log.debug("{}: The CIS we need to unshare is: {}",getName(), removedCis);
				controller.unshareService(getId(),removedCis);
			} else{
				log.warn("{}: Couldn't find the CIS to remove?!",getName());
			}
		} else{
			log.debug("{}: We've added sharing to another CIS!",getName());
			String newCis = null;
			for(String sharedCis : cisListId){
				if(!sharedCisList.contains(sharedCis))
					newCis = sharedCis;
			}
			if(newCis != null){
				log.debug("{}: The CIS we need to share is: {}",getName(), newCis);
				controller.shareService(getId(),newCis);
			} else{
				log.warn("{}: Couldn't find the CIS to share?!",getName());
			}
		}
		
		this.sharedCisList = cisListId;
	}
		
	public boolean isMine(){
		
		return mine;
	}
	
	public boolean isDevice(){
		return service.getServiceType().equals(ServiceType.DEVICE);
	}
	
	public boolean isClient(){
		return service.getServiceType().equals(ServiceType.THIRD_PARTY_CLIENT);
	}
	
	public boolean isAndroid(){
		return service.getServiceType().equals(ServiceType.THIRD_PARTY_ANDROID);
	}
	
	@Override
	public boolean equals(Object obj){
	
		if(obj.getClass().equals(ServiceWrapper.class)){
			//Now we compare the SRI
			ServiceResourceIdentifier objSri = ((ServiceWrapper) obj).getService().getServiceIdentifier();
			return ServiceModelUtils.compare(service.getServiceIdentifier(), objSri);
		} else
			return false;
		
	}
	
	@Override
	public int hashCode(){
		return ServiceModelUtils.serviceResourceIdentifierToString(service.getServiceIdentifier()).hashCode();
	}
	
	public String getId(){
		return ServiceModelUtils.serviceResourceIdentifierToString(service.getServiceIdentifier());
	}
	

}
