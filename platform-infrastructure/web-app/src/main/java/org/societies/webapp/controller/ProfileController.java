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
package org.societies.webapp.controller;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;

import org.societies.api.internal.css.ICSSInternalManager;
import org.societies.api.schema.cssmanagement.CssInterfaceResult;
import org.societies.api.schema.cssmanagement.CssRecord;
import org.springframework.stereotype.Controller;


@Controller
@ManagedBean(name = "profile")
@SessionScoped
public class ProfileController extends BasePageController{
	
	@ManagedProperty(value = "#{cssLocalManager}")
	private ICSSInternalManager cssLocalManager;
	
	
	public ICSSInternalManager getCssLocalManager() {
		return cssLocalManager;
	}
	
	public void setCssLocalManager(ICSSInternalManager cssLocalManager) {
		this.cssLocalManager = cssLocalManager;
	}
	
	public ProfileController() {
        log.info("ProfileController constructor");
	}
	

	public String getId() {
		return Id;
	}

	public void setId(String id) {
		Id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getSex() {
		return sex;
	}

	public void setSex(int sex) {
		this.sex = sex;
	}

	public String getHomelocation() {
		return homelocation;
	}

	public void setHomelocation(String homelocation) {
		this.homelocation = homelocation;
	}

	public String getWorkplace() {
		return workplace;
	}

	public void setWorkplace(String workplace) {
		this.workplace = workplace;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public int getEntity() {
		return entity;
	}

	public void setEntity(int entity) {
		this.entity = entity;
	}


	private String Firstname;
	public String getFirstname() {
		return Firstname;
	}

	public void setFirstname(String firstname) {
		Firstname = firstname;
	}

	public String getSurname() {
		return Surname;
	}

	public void setSurname(String surname) {
		Surname = surname;
	}


	private String Surname;
	private String Id;
	private String email;
	private int sex;
	private String homelocation;
	private String workplace;
	private String position;
	private int entity;
        
        public CssRecord getrecord(){
        	
        	log.info("ProfileController getrecord called");
        	
        	CssRecord record = new CssRecord();
        	
        	Future<CssInterfaceResult> futurerecord = cssLocalManager.getCssRecord();
        	try {
    			record = futurerecord.get().getProfile();
    		} catch (InterruptedException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		} catch (ExecutionException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    		//
        	
        	Firstname = record.getForeName();
        	Surname = record.getName();
        	email = record.getEmailID();
        	sex = record.getSex();
        	homelocation = record.getHomeLocation();
        	workplace = record.getWorkplace();
        	position = record.getPosition();
        	entity = record.getEntity();
        	
    		log.info("ProfileController getName returns :" +Surname);
    		return record;
        	
        }
     
     public void modifyrecord(){
     	
     	log.info("ProfileController modifyRecord called");
     	
     	CssRecord record = new CssRecord();
     	record.setCssIdentity(this.getId());
     	log.info("Setting Record SurName with : " +Surname);
     	record.setForeName(Firstname);
     	record.setName(Surname);
     	
     	log.info("Record SurName contains : " +record.getName());
     	record.setEmailID(this.getEmail());
     	record.setPosition(this.getPosition());
     	record.setHomeLocation(this.getHomelocation());
     	record.setWorkplace(this.getWorkplace());
     	record.setSex(sex);
     	record.setEntity(entity);
     	log.info("entity contains : " +entity);
     	log.info("Record contains : " +record);
     	
     	cssLocalManager.modifyCssRecord(record);
     	
     }
	
	

}
