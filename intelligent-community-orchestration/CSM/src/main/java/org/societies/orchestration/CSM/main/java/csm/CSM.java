/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske držbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOAÇÃO, SA (PTIN), IBM Corp., 
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
package org.societies.orchestration.CSM.main.java.csm;

//import local.test.dummy.interfaces.ICisDirectory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.cis.directory.ICisDirectory;
import org.societies.api.context.broker.ICtxBroker;
import org.societies.api.identity.IIdentity;
import org.societies.api.osgi.event.CSSEvent;
import org.societies.api.osgi.event.EventListener;
import org.societies.api.osgi.event.InternalEvent;
//
import org.societies.orchestration.CSM.main.java.GroupIdentfier.GroupManager;
import org.societies.orchestration.CSM.main.java.Models.ModelManager;

/**
 * CIO
 */
public class CSM {
	
//private Models models;
private ModelManager modelMang;
private GroupManager groupMang;
private Logger LOG = LoggerFactory.getLogger(CSM.class);

	public CSM()  
	{
		LOG.info("CSM Begin");
		modelMang = new ModelManager();		
		groupMang = new GroupManager(modelMang);
	}

	public void setUP(){
		LOG.info("CSM  :  Set up");
		modelMang.populateModels();
	}
	
	public void addUser(IIdentity user){
		groupMang.addUser(user);
	}
	
	public void attributeUpdate(IIdentity user,String att){
		groupMang.AttributeUpdate(user,att);
	}
	
	/**************************************************************
	*
	*   getter
	*
	***************************************************************/	
	public ICisDirectory getCisDirectory(){
		return (ICisDirectory) modelMang.getCisDirectory();
	}
	
	public ICtxBroker getCtxBroker(){
		return modelMang.getCtxBroker();
	}
	/**************************************************************
	*
	*   setter
	*
	***************************************************************/		
	public void setCisDirectory(ICisDirectory icd){
		modelMang.setCisDirectory((org.societies.api.cis.directory.ICisDirectory) icd);
	}	
	
	public void setCtxBroker(ICtxBroker ctxBroker){
		modelMang.setCtxBroker(ctxBroker);
	}

}