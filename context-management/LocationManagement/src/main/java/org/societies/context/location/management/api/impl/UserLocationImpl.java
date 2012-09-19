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
package org.societies.context.location.management.api.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.societies.context.location.management.api.ICoordinate;
import org.societies.context.location.management.api.ITag;
import org.societies.context.location.management.api.IUserLocation;
import org.societies.context.location.management.api.IZone;


/**
*
* @author guyf@il.ibm.com
* 
*/
public class UserLocationImpl implements IUserLocation{
	private ICoordinate xCoordinate;
	private ICoordinate yCoordinate;
	private Collection<IZone> zones;
	private String id;
	
	@Override
	public ICoordinate getXCoordinate() {
		return xCoordinate;
	}

	@Override
	public ICoordinate getYCoordinate() {
		return yCoordinate;
	}

	@Override
	public void setXCoordinate(ICoordinate coordinate) {
		this.xCoordinate = coordinate;
	}

	@Override
	public void setYCoordinate(ICoordinate coordinate) {
		this.yCoordinate = coordinate;
	}

	@Override
	public Collection<IZone> getZones() {
		List<IZone> tempZones = new ArrayList<IZone>();
		tempZones.addAll(zones);
		return tempZones;
	}

	@Override
	public void setZones(Collection<IZone> zones) {
		this.zones = new ArrayList<IZone>();
		this.zones.addAll(zones);
	}

	@Override
	public String getId() {
		return this.id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}
	
	@Override
	public String toString(){
		String str = "";
		
		str += "x= "+this.getXCoordinate().getCoordinate() + "  y= "+this.getYCoordinate().getCoordinate() +" zones = [";
		int count = 0;
		for (IZone zone: this.getZones()){
			str+= zone.getId().getId()+",name="+zone.getName()+",type="+zone.getType()+", Personal Tag="+zone.getPersonalTag().getTag();
			str+= ", Tags=[";
			
			int count2=0;
			for (ITag tag: zone.getTags()){
				str+= tag.getTag();
				
				count2++;
				if (count2 != zone.getTags().size()){
					str+= ",";
				}
			}
			str+= "]";
			
			count ++;
			if (count != this.getZones().size()){
				str+= " ;\t";
			}
		}
		str+= "]";
		
		return str;
	}
}
