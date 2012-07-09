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
package org.societies.activity.model;

import java.util.HashMap;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.societies.activity.ActivityFeed;
import org.societies.api.activity.IActivity;

@Entity
@Table(name = "org_societies_cis_activity_model_Activity")
public class Activity implements IActivity {
	/**
	 * Serializable .. 
	 */
	private static final long serialVersionUID = 1L;
	@Transient
	private HashMap<String,ActivityString> data = null;
	private long time;
	private String verb;
	private String actor;
	private String object;
	private String target;
	private String published;
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	private Long ownerId;
	
	public Long getOwnerId() {
		return ownerId;
	}
	public void setOwnerId(Long ownerId) {
		this.ownerId = ownerId;
	}
	public Activity()
	{
		data = new HashMap<String,ActivityString>();
		this.setActor("");
		this.setObject("");
		this.setTarget("");
		this.setVerb("");
		this.setPublished("");
	}
	public Activity(IActivity iact)
	{
		data = new HashMap<String,ActivityString>();
		this.setActor(iact.getActor());
		this.setObject(iact.getObject());
		this.setTarget(iact.getTarget());
		this.setVerb(iact.getVerb());
		this.setPublished(iact.getPublished());
	}
	public void repopHash(){
		this.setActor(this.getActor());
		this.setObject(this.getObject());
		this.setTarget(this.getTarget());
		this.setVerb(this.getVerb());
		this.setPublished(this.getPublished());
	}
	
	@Column(name="verb")
	@Override
	public String getVerb() {
		return verb;
	}

	@Column(name="verb")
	@Override
	public void setVerb(String verb) {
		this.verb = verb;
		data.put("verb", new ActivityString(verb));
		
	}
	@Column(name="actor")
	@Override
	public String getActor() {
		return actor;
	}
	@Column(name="actor")
	@Override
	public void setActor(String actor) {
		this.actor = actor;
		data.put("actor", new ActivityString(actor));
	}
	@Column(name="object")
	@Override
	public String getObject() {
		return object;
	}
	@Column(name="object")
	@Override
	public void setObject(String object) {
		this.object = object;
		data.put("object", new ActivityString(object));
		
	}
	@Column(name="target")
	@Override
	public String getTarget() {
		return target;
	}
	@Column(name="target")
	@Override
	public void setTarget(String target) {
		this.target = target;
		data.put("target", new ActivityString(target));
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	@Override
	public long getTime() {
		return time;
	}
	@Override
	public void setTime(long time) {
		this.time = time;
	}
	@Override
	public String getPublished() {
		return published;
	}
	@Override
	public void setPublished(String published) {
		this.published = published;
		data.put("published", new ActivityString(published));
		
	}
	public ActivityString getValue(String key){
		return data.get(key);
	}
}
