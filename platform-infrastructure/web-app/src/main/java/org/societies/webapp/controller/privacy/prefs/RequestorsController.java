/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske druzbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVACAO, SA (PTIN), IBM Corp., 
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

package org.societies.webapp.controller.privacy.prefs;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.sql.rowset.spi.SyncResolver;
import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.cis.directory.ICisDirectoryCallback;
import org.societies.api.cis.directory.ICisDirectoryRemote;
import org.societies.api.cis.management.ICis;
import org.societies.api.cis.management.ICisManager;
import org.societies.api.cis.management.ICisOwned;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.internal.css.ICSSInternalManager;
import org.societies.api.internal.servicelifecycle.IServiceDiscovery;
import org.societies.api.internal.servicelifecycle.ServiceDiscoveryException;
import org.societies.api.internal.servicelifecycle.ServiceModelUtils;
import org.societies.api.schema.cis.directory.CisAdvertisementRecord;
import org.societies.api.schema.css.directory.CssAdvertisementRecord;
import org.societies.api.schema.cssmanagement.CssAdvertisementRecordDetailed;
import org.societies.api.schema.servicelifecycle.model.Service;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;

/**
 * @author Eliza
 *
 */
@ViewScoped
@ManagedBean(name="RequestorsController")
public class RequestorsController implements Serializable{
	private final Logger logging = LoggerFactory.getLogger(getClass());


	@ManagedProperty(value= "#{cssLocalManager}")
	private ICSSInternalManager cssManager; 

	@ManagedProperty(value="#{commMngrRef}")
	private ICommManager commsManager;

	@ManagedProperty(value="#{cisDirectoryRemote}")
	private ICisDirectoryRemote cisDirectory;

    @ManagedProperty(value = "#{serviceDiscovery}")
    private IServiceDiscovery serviceDiscovery; // NB: MUST include public getter/setter

    
	private List<CssAdvertisementRecord> cssAdvertisements;

	private List<CisAdvertisementRecord> cisAdvertisements;
	
	private List<Service> localServices;

	private Hashtable<String, List<CisAdvertisementRecord>> resultTable = new Hashtable<String, List<CisAdvertisementRecord>>();


	private List<Service> services;

	

	@PostConstruct
	public void setupController(){
		this.cssAdvertisements = new ArrayList<CssAdvertisementRecord>();
		this.cisAdvertisements = new ArrayList<CisAdvertisementRecord>();
		this.services = new ArrayList<Service>();
	}

	public List<CisAdvertisementRecord> getCisListByOwner(String ownerID){

		if (ownerID==null){
			return new ArrayList<CisAdvertisementRecord>();
		}
		String uuid = UUID.randomUUID().toString();
		//this.cisDirectory.searchByID(ownerID, new CisDirectoryCallback(uuid, ownerID));
		this.cisDirectory.findAllCisAdvertisementRecords(new CisDirectoryCallback(uuid, ownerID));
		if (logging.isDebugEnabled()){
			this.logging.debug("Asked cisDirectory CISs of: "+ownerID);
		}
		while (!this.resultTable.containsKey(uuid)){
			synchronized (this.resultTable) {
				try {
					if (logging.isDebugEnabled()){
						this.logging.debug("Waiting for result");
					}
					this.resultTable.wait();
					
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
			}
		}
		
		
		if (logging.isDebugEnabled()){
			this.logging.debug("Retrieved "+this.cisAdvertisements.size()+ "CISs of "+ownerID);
		}
		return cisAdvertisements;
	}
	
	
	public List<Service> getServiceListByOwner(String ownerID){
		if (logging.isDebugEnabled()){
			this.logging.debug("getServiceListByOwner("+ownerID+")");
		}
		if (ownerID==null){
			return new ArrayList<Service>();
		}
		
		try {
			IIdentity fromJid = this.commsManager.getIdManager().fromJid(ownerID);
			if (logging.isDebugEnabled()){
				this.logging.debug("serviceDiscovery.getServices("+fromJid.getBareJid()+")");
			}
			services = this.serviceDiscovery.getServices(fromJid).get();
			if (services==null){
				this.services = new ArrayList<Service>();
			}
/*			services.get(0).getServiceIdentifier();
			services.get(0).getServiceName();
			*/
			if (logging.isDebugEnabled()){
				this.logging.debug("Found :"+services.size()+" services shared by: "+ownerID);
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ServiceDiscoveryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidFormatException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return new ArrayList<Service>();
		}catch (Exception e){
			e.printStackTrace();
		}
		
		return this.services;
	}

	public String getServiceIDAsString(ServiceResourceIdentifier serviceID){
		return ServiceModelUtils.serviceResourceIdentifierToString(serviceID);
	}
	public ICSSInternalManager getCssManager() {
		return cssManager;
	}

	public void setCssManager(ICSSInternalManager cssManager) {
		this.cssManager = cssManager;
	}


	public ICommManager getCommsManager() {
		return commsManager;
	}


	public void setCommsManager(ICommManager commsManager) {
		this.commsManager = commsManager;
	}

	public ICisDirectoryRemote getCisDirectory() {
		return cisDirectory;
	}


	public void setCisDirectory(ICisDirectoryRemote cisDirectory) {
		this.cisDirectory = cisDirectory;
	}
	
	public List<CssAdvertisementRecord> getCssAdvertisements() {
		if (logging.isDebugEnabled()){
			this.logging.debug("getCssAdvertisements() called");
		}
		this.cssAdvertisements.clear();
		try {
			Future<List<CssAdvertisementRecordDetailed>> cssAdvertisementRecordsFull = this.cssManager.getCssAdvertisementRecordsFull();
			if (cssAdvertisementRecordsFull!=null){

				List<CssAdvertisementRecordDetailed> list = cssAdvertisementRecordsFull.get();
				if (logging.isDebugEnabled()){
					this.logging.debug("CssRecords found: "+list.size());
				}
				for (CssAdvertisementRecordDetailed cssRecord : list){
					this.cssAdvertisements.add(cssRecord.getResultCssAdvertisementRecord());
				}
				if (logging.isDebugEnabled()){
					this.logging.debug("Added to cssList: "+cssAdvertisements.size()+" records ");
				}
			}else{
				if (logging.isDebugEnabled()){
					this.logging.debug("returned cssAdvertisementrecords is null");
				}
			}

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (logging.isDebugEnabled()){
			this.logging.debug("Returning true");
		}
		return this.cssAdvertisements;
	}



	public void setCssAdvertisements(List<CssAdvertisementRecord> cssAdvertisements) {
		this.cssAdvertisements = cssAdvertisements;
	}


	public IServiceDiscovery getServiceDiscovery() {
		return serviceDiscovery;
	}

	public void setServiceDiscovery(IServiceDiscovery serviceDiscovery) {
		this.serviceDiscovery = serviceDiscovery;
	}


	
	public List<Service> getLocalServices() {
		try {
			//localServices.get(0).
			
			return this.serviceDiscovery.getLocalServices().get();
			
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ServiceDiscoveryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return new ArrayList<Service>();
	}

	public void setLocalServices(List<Service> localServices) {
		this.localServices = localServices;
	}



	private class CisDirectoryCallback implements ICisDirectoryCallback{

		
		private String uuid;
		private String ownerID;

		public CisDirectoryCallback(String uuid, String ownerID){
			this.uuid = uuid;
			this.ownerID = ownerID;
		}
		@Override
		public void getResult(List<CisAdvertisementRecord> records) {
			
			cisAdvertisements.clear();
			if (records!=null){
				if (logging.isDebugEnabled()){
					logging.debug("Received result from remote cisDirectory: "+records.size()+" CISs");
				}
			
				for (CisAdvertisementRecord cisRecord : records){
					if (cisRecord.getCssownerid().equalsIgnoreCase(ownerID)){
						cisAdvertisements.add(cisRecord);
					}
					
				}

				if (logging.isDebugEnabled()){
					logging.debug("From these, "+cisAdvertisements.size()+" are owned by "+ownerID);
				}
				synchronized (resultTable) {
					resultTable.put(uuid, cisAdvertisements);
					resultTable.notifyAll();
				}
			}else{
				if (logging.isDebugEnabled()){
					logging.debug("Received null result from remote cisDirectory");
				}

				synchronized (resultTable) {
					resultTable.put(this.uuid, new ArrayList<CisAdvertisementRecord>());
					resultTable.notifyAll();
				}
			}
			
		}
		
	}


}
