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
package org.societies.css.devicemgmt.controller.model;

import java.io.Serializable;
import java.util.ArrayList;

import org.societies.api.context.model.CtxEntityIdentifier;

/**
 * Describe your class here...
 *
 * @author Eliza
 *
 */
public class Controller implements Serializable{

	
	private ArrayList<IPluggableResource> resources;
	private final String location;
	private final String controllerId; 
	private final CtxEntityIdentifier ctxId;
	private final String ipAddress;
	
	public Controller(String ipAddr, String controllerId, String location, CtxEntityIdentifier ctxId){
		this.ipAddress = ipAddr;
		this.controllerId = controllerId;
		this.location = location;
		this.resources = new ArrayList<IPluggableResource>();
		this.ctxId = ctxId;
		
		
	}

	/**
	 * @return the locationName
	 */
	public String getLocationName() {
		return location;
	}



	public void addPluggableResource(IPluggableResource resource){
		if (resource instanceof PressureMat){
			this.resources.add((PressureMat) resource);
		}
	}

	
	public ArrayList<IPluggableResource> getPluggableResources(){
		return this.resources;
	}
	
	public void clearResourcesList(){
		this.resources.clear();
	}
	
	public boolean removeResource(String resourceId){
		for (IPluggableResource resource: this.resources){
			if (resource.getPortId().equalsIgnoreCase(resourceId)){
				this.resources.remove(resource);
				return true;
			}
		}
		return false;
	}
	
	public void setResourceStatus(String resourceId, boolean enabled){
		for (IPluggableResource resource: this.resources){
			if (resource.getPortId().equalsIgnoreCase(resourceId)){
				if (resource instanceof PressureMat)
				((PressureMat) resource).setEnabled(enabled);
			}
		}
	}
	
	public IPluggableResource getPressureMat(String resourceId){
		for (IPluggableResource mat: this.resources){
			if (mat.getPortId().equalsIgnoreCase(resourceId)){
				return mat;
			}
		}
		return null;
	}


	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((controllerId == null) ? 0 : controllerId.hashCode());
		result = prime * result + ((ctxId == null) ? 0 : ctxId.hashCode());
		result = prime * result
				+ ((location == null) ? 0 : location.hashCode());
		result = prime * result
				+ ((resources == null) ? 0 : resources.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Controller other = (Controller) obj;
		if (controllerId == null) {
			if (other.controllerId != null)
				return false;
		} else if (!controllerId.equals(other.controllerId))
			return false;
		if (ctxId == null) {
			if (other.ctxId != null)
				return false;
		} else if (!ctxId.equals(other.ctxId))
			return false;
		if (location == null) {
			if (other.location != null)
				return false;
		} else if (!location.equals(other.location))
			return false;
		if (resources == null) {
			if (other.resources != null)
				return false;
		} else if (!resources.equals(other.resources))
			return false;
		return true;
	}

	/**
	 * @return the controllerId
	 */
	public String getControllerId() {
		return controllerId;
	}

	/**
	 * @return the ctxId
	 */
	public CtxEntityIdentifier getCtxId() {
		return ctxId;
	}

	/**
	 * @return the ipAddress
	 */
	public String getIpAddress() {
		return ipAddress;
	}
}
