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
package org.societies.context.source.time.impl;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxAttributeValueType;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.CtxOriginType;
import org.societies.api.identity.IIdentity;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.context.model.CtxAttributeTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

/**
 * This class is used to update time related context attributes 
 *  HOUR_OF_DAY, TIME_OF_DAY, DAY_OF_WEEK
 * 
 * @author <a href="mailto:nikosk@cn.ntua.gr">Nikos Kalatzis</a> (ICCS)
 * @since 1.0
 */
@Service
@Lazy(false)
public class TimeCtxMonitor  {

	/** The logging facility. */
	private static final Logger LOG = LoggerFactory.getLogger(TimeCtxMonitor.class);

	/** The internal Context Broker service. */
	
	private ICtxBroker ctxBroker;

	/** The Comm Mgr service. */
	private ICommManager commMgr;

	/** The executor service. */
	final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

	CtxAttributeIdentifier todAttrId = null;
	CtxAttributeIdentifier hodAttrId = null;
	CtxAttributeIdentifier dowAttrId = null;

	@Autowired(required=true)
	TimeCtxMonitor(ICommManager commMgr, ICtxBroker ctxBroker) throws Exception {

		LOG.info("{} instantiated", this.getClass());

		this.commMgr = commMgr;
		this.ctxBroker = ctxBroker;
		
		LOG.info("{} ctxBroker service instantiated" , this.ctxBroker);
		
		//LOG.info("instantiateCtxAttributes");
		this.instantiateCtxAttributes();
		
		//LOG.info("updateTimeCtxValues");
		DateTime dateTime = new DateTime();
		// perform initial time update 
		this.updateCtxAttributes(dateTime);
	
		// 
		this.scheduledUpdateTimeCtxValues();
	}


	private void instantiateCtxAttributes(){

		IIdentity cssId;
		try {
			cssId = this.commMgr.getIdManager().fromJid(
					this.commMgr.getIdManager().getThisNetworkNode().getBareJid());

			final CtxEntityIdentifier userCtxEntId = this.ctxBroker.retrieveIndividualEntityId(null, cssId).get();

			if (cssId == null) {
				LOG.error("Failed to retrieve cssID: " );
				return;
			}

			if (userCtxEntId == null) {
				LOG.error("Failed to retrieve userCtxEntId: " );
				return;
			}

			//DAY_OF_WEEK
			List<CtxIdentifier> dayOfWeekList = this.ctxBroker.lookup(cssId, CtxModelType.ATTRIBUTE, CtxAttributeTypes.DAY_OF_WEEK).get();
			CtxAttribute dowAttr = null;
			
			if(dayOfWeekList.isEmpty()){
				 dowAttr = this.createCtxAttribute(userCtxEntId,CtxAttributeTypes.DAY_OF_WEEK, null );
				
			} else {
				dowAttr = (CtxAttribute) this.ctxBroker.retrieve(dayOfWeekList.get(0)).get();
				
			}
			this.dowAttrId = dowAttr.getId();
						
			// TIME_OF_DAY
			List<CtxIdentifier> timeOfDayList = this.ctxBroker.lookup(cssId, CtxModelType.ATTRIBUTE, CtxAttributeTypes.TIME_OF_DAY).get();
			CtxAttribute todAttr = null;
			
			if(timeOfDayList.isEmpty()){
				 todAttr = this.createCtxAttribute(userCtxEntId,CtxAttributeTypes.TIME_OF_DAY, null );
			
			} else {
				 todAttr = (CtxAttribute) this.ctxBroker.retrieve(timeOfDayList.get(0)).get();
			}
			this.todAttrId = todAttr.getId();
						
			//HOUR_OF_DAY
			List<CtxIdentifier> hourOfDayList = this.ctxBroker.lookup(cssId, CtxModelType.ATTRIBUTE, CtxAttributeTypes.HOUR_OF_DAY).get();
			CtxAttribute hodAttr = null;
			
			if(hourOfDayList.isEmpty()){
				hodAttr = this.createCtxAttribute(userCtxEntId,CtxAttributeTypes.HOUR_OF_DAY, null );
			} else {
				hodAttr = (CtxAttribute) this.ctxBroker.retrieve(hourOfDayList.get(0)).get();
			}
			this.hodAttrId = hodAttr.getId();
		
			
		} catch (Exception e) {
			LOG.error("Exception while trying to retrieve time related ctxAttribute "+e.getLocalizedMessage());
			e.printStackTrace();
		}

		if(dowAttrId == null || todAttrId == null || hodAttrId == null){
			LOG.error("Failed to retrieve or create time related ctxAttributes " );
			return;
		}
	}

	
	private void scheduledUpdateTimeCtxValues() {
		
		final Runnable updateValues = new Runnable() {
				
			public void run() { 
				DateTime dateTime = new DateTime();	
				updateCtxAttributes(dateTime);
			}
		};

		final DateTime now = new DateTime();
		long startAt = 60l - now.getMinuteOfHour();
		
		scheduler.scheduleAtFixedRate(updateValues, startAt, 60, TimeUnit.MINUTES);
	}
	
	/*
	 * 4 hours step
		earlyMorning (6 - 9) 
		lateMorning (9 - 12)  
		
		earlyAfternoon (12 - 15)
		lateAfternoon (15 - 18)
		
		earlyEvening (18 - 21) 
		lateEvening (21 - 24)
		
		earlyNight (0 - 3)  
		lateNight (3 - 6)
	 */
	private String convertTimeOfDay(DateTime date){
		
		//DateTime dt = new DateTime();
		String tod = "";
		int i = date.getHourOfDay();
		
		if(i >= 6 && i < 9 )  tod="earlyMorning";
		if(i >= 9 && i < 12 )  tod="lateMorning";
			
		if(i >= 12 && i < 15 ) tod="earlyAfternoon";
		if(i >= 15 && i < 18 ) tod="lateAfternoon";
		
		if(i >= 18 && i < 21 ) tod="earlyEvening";
		if(i >= 21 && i < 24 ) tod="lateEvening";
		
		if(i >= 0 && i < 3 )  tod="earlyNight";
		if(i >= 3 && i < 6 )  tod="lateNight";
	
		return tod;
	}

	
	private String convertDayOfWeek(DateTime date){
		
		String dow = "";
		int i = date.getDayOfWeek();
		if(i == 1) dow = "Monday";
		if(i == 2) dow = "Tuesday";
		if(i == 3) dow = "Wednesday";
		if(i == 4) dow = "Thursday";
		if(i == 5) dow = "Friday";
		if(i == 6) dow = "Saturday";
		if(i == 7) dow = "Sunday";		
		
		return dow; 
	}
	
	
	private void updateCtxAttributes(DateTime date){
	
		try {
			
			if(this.todAttrId != null){
				CtxAttribute todAttr = (CtxAttribute) this.ctxBroker.retrieve(this.todAttrId).get();
				String timeOfDayValue = convertTimeOfDay(date);
				todAttr.setStringValue(timeOfDayValue);
				todAttr.setValueType(CtxAttributeValueType.STRING);
				todAttr.getQuality().setOriginType(CtxOriginType.INFERRED);
				this.ctxBroker.update(todAttr);
				LOG.debug("updating tod: timeOfDayValue " +timeOfDayValue);
			}

			if(this.hodAttrId != null){
				CtxAttribute hodAttr = (CtxAttribute) this.ctxBroker.retrieve(this.hodAttrId).get();
				hodAttr.setIntegerValue(date.getHourOfDay());
				hodAttr.setValueType(CtxAttributeValueType.INTEGER);
				hodAttr.getQuality().setOriginType(CtxOriginType.INFERRED);
				this.ctxBroker.update(hodAttr);
				LOG.debug("updating hod:  " +hodAttr);
			}
			
			if(this.dowAttrId != null){
				CtxAttribute dowAttr = (CtxAttribute) this.ctxBroker.retrieve(this.dowAttrId).get();
				String dow = convertDayOfWeek(date);
				dowAttr.setStringValue(dow);
				dowAttr.setValueType(CtxAttributeValueType.STRING);
				dowAttr.getQuality().setOriginType(CtxOriginType.INFERRED);
				this.ctxBroker.update(dowAttr);
				LOG.debug("updating  dow:  " +dow);
			}
		
		} catch (Exception e) {
			LOG.error("Exception while updating time based ctx attribute values "
					+ e.getLocalizedMessage(), e);
		}	
	}

	private CtxAttribute createCtxAttribute(CtxEntityIdentifier ownerCtxId, 
			String type, String value) throws Exception {

		LOG.debug("Creating '{}' attribute under entity '{}' with value '{}'",
				new Object[] { type, ownerCtxId, value });

		CtxAttribute attr = this.ctxBroker.createAttribute(ownerCtxId, type).get();
		if (value != null) {
			attr.setStringValue(value);
			attr = (CtxAttribute) this.ctxBroker.update(attr).get();
		}
		return attr;
	}	
}