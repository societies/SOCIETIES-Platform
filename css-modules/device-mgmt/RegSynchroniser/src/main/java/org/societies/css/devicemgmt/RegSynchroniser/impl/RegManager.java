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

package org.societies.css.devicemgmt.RegSynchroniser.impl;


import java.util.Collection;
import java.util.HashMap;

import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.osgi.context.BundleContextAware;
import org.societies.api.identity.IIdentity;
import org.societies.api.internal.css.devicemgmt.IDeviceRegistry;
import org.societies.css.devicemgmt.deviceregistry.DeviceRegistry;
import org.societies.api.internal.css.devicemgmt.ILocalDevice;
import org.societies.api.internal.css.devicemgmt.model.DeviceCommonInfo;
import org.societies.api.comm.xmpp.pubsub.PubsubClient;
import org.societies.api.comm.xmpp.pubsub.Subscriber;
import org.societies.comm.xmpp.event.InternalEvent;
import org.societies.comm.xmpp.event.PubsubEvent;
import org.societies.comm.xmpp.event.PubsubEventFactory;
import org.societies.api.schema.css.devicemanagment.DmEvent;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.identity.INetworkNode;

public class RegManager implements ILocalDevice, Subscriber, BundleContextAware{

	
	private static Logger LOG = LoggerFactory.getLogger(RegManager.class);
    private IDeviceRegistry deviceRegistry;
    private BundleContext bundleContext;
    private PubsubClient pubSubManager;  
    private IIdentityManager idManager;
	private ICommManager commManager;
	IIdentity pubsubID = null;
    
    //private HashMap<String, String> eventResult;
    
    /**
     * Default Constructor
     */
    public RegManager() {
    }

    /**
     * Constructor
     * 
     * @param context
     */
    public RegManager(BundleContext bundlecontext) {
                
        
    	LOG.info("+++ RegManager has been created ");
    	//IIdentity pubsubID = null;
    	//idManager = commManager.getIdManager();
        
    	this.bundleContext = bundlecontext;
        
        this.deviceRegistry = DeviceRegistry.getInstance();
        
        //if((idManager = commManager.getIdManager()!= null) != null){
//        	try {
//    			pubsubID = idManager.fromJid("XCManager.societies.local");
//    		} catch (InvalidFormatException e) {
 //   			// TODO Auto-generated catch block
 //   			e.printStackTrace();
 //   		}
        	
 //       	try {
//    			pubSubManager.subscriberSubscribe(pubsubID, "DEVICE_REGISTERED", this);
//    		} catch (XMPPError e) {
//    			// TODO Auto-generated catch block
 //   			e.printStackTrace();
//    		} catch (CommunicationException e) {
//    			// TODO Auto-generated catch block
//    			e.printStackTrace();
//    		}
       // }
    	

    }

   // public void initiateSearch() {
   // }

    /**
     * Register an events listener with the container
     * 
     * @param listener
     * @param filterOption
     */

////////////////////////////////////////////////////////////////////////////////////////////////////////
// need to register event listeners and call commsMgr eventing system to fire new events
////////////////////////////////////////////////////////////////////////////////////////////////////////
    

    /**
     * Add a device to the device Registry
     * 
     * @param device
     */
    public boolean addDevice(DeviceCommonInfo device, String CSSNodeID) throws Exception {

    	LOG.info("+++ RegManager addDevice called to add device: " +device.getDeviceID());
        boolean retValue = true;
        
        retValue = LocalDevices.addDevice(device, CSSNodeID);
        
        return retValue;
    }

    /**
     * Convenience method to add a collection of devices
     */

    public boolean addDevices(Collection<DeviceCommonInfo> deviceCollection, String CSSNodeID)
            throws Exception {
    	
    	LOG.info("+++ RegManager addDevices called to add devices: " +deviceCollection);
        boolean retValue = true;

        for (DeviceCommonInfo device : deviceCollection) {
            if (!this.addDevice(device, CSSNodeID)) {
                retValue = false;
                break;
            }
        }
        return retValue;
    }

    /**
     * Remove a device
     * 
     * @param device
     */
    public boolean removeDevice(DeviceCommonInfo device, String CSSNodeID)
            throws Exception {
    	LOG.info("+++ RegManager removeDevice called to remove device: " +device.getDeviceID());

        return LocalDevices.removeDevice(device, CSSNodeID);
    }

    /**
     * Convenience method to remove a collection of devices
     */
    public boolean removeDevices(
            Collection<DeviceCommonInfo> deviceCollection, String CSSNodeID)
            throws Exception {

    	LOG.info("+++ RegManager removeDevices called to add devices: " +deviceCollection);
        boolean retValue = true;

        for (DeviceCommonInfo device : deviceCollection) {
            if (!this.removeDevice(device, CSSNodeID)) {
                retValue = false;
                break;
            }
        }
        return retValue;
    }


    /**
     * Clear the registry
     */
    public boolean clearRegistry() throws Exception {
        boolean retValue = false;

        LOG.info("+++ RegManager Clear Registry called: ");
        this.deviceRegistry.clearRegistry();

        if (0 == this.deviceRegistry.registrySize()) {
            retValue = true;
        }
        return retValue;
    }

	@Override
	public void setBundleContext(BundleContext arg0) {
		// TODO Auto-generated method stub
		
	}

	public boolean removedevice(String deviceID, String CSSNodeID) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	
	public PubsubClient getPubSubManager() {
		return pubSubManager;
	}

	public void setPubSubManager(PubsubClient pubSubManager) {
		this.pubSubManager = pubSubManager;
		LOG.info("+++ RegManager setPubSubManager called: ");
		
	}
	
	public ICommManager getCommManager() {
		return commManager;
	}


	public void setCommManager(ICommManager commManager) {
		this.commManager = commManager;
		LOG.info("+++ RegManager setCommManager called: ");
	}
	
	private void init(){
		LOG.info("+++ RegManager init called: ");
		idManager = commManager.getIdManager();
		try {
			pubsubID = idManager.getThisNetworkNode();
			pubSubManager.subscriberSubscribe(pubsubID, "DEVICE_REGISTERED", this);
			pubSubManager.subscriberSubscribe(pubsubID, "DEVICE_DISCONNECTED", this);
		} catch (XMPPError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CommunicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	@Override
	public void pubsubEvent(IIdentity pubsubService, String node, String itemId, Object payload) {
		
		LOG.info("+++ RegManager pubsubEvent called with the following event: " +node);
		DmEvent dmEvent = null;
		DeviceCommonInfo device = new DeviceCommonInfo();
		dmEvent = (DmEvent)payload;
		device.setDeviceConnectionType(dmEvent.getConnectionType());
		device.setContextSource(dmEvent.isContextSource());
		device.setDeviceDescription(dmEvent.getDescription());
		//device.setDeviceFamilyIdentity(dmEvent.getType());
		device.setDeviceID(dmEvent.getDeviceId());
		device.setDeviceLocation(dmEvent.getLocation());
		device.setDeviceName(dmEvent.getName());
		device.setDeviceProvider(dmEvent.getProvider());
		device.setDeviceType(dmEvent.getType());
	
		String CSSNodeID = "liam.societies.org";	
		
		if(node.equals("DEVICE_REGISTERED")){
			try {
				LocalDevices.addDevice(device, CSSNodeID);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(node.equals("DEVICE_DISCONNECTED")){
			try {
				LocalDevices.removeDevice(device, CSSNodeID);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
}
