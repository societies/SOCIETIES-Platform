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
package org.societies.webapp.controller.rfid;

import java.io.IOException;
import java.security.Identity;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.util.SerialisationHelper;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.internal.context.broker.ICtxBroker;

/**
 * @author Eliza
 *
 */
public class ContextRetriever {

	private static final String TAG_TO_IDENTITY = "tagToIdentity";
	private static final String TAG_TO_PASSWORD = "tagToPassword";
	private static final String TAG_TO_SYMLOC = "tagToSymloc";
	private static final String RFID_SERVER_ENTITY = "RFID_SERVER_ENTITY";
	private static final String RFID_INFO = "RFID_INFO";
	private static final String RFID_LAST_LOCATION = "RFID_LAST_LOCATION";
	private Hashtable<String, String> tagToPassword;
	private Hashtable<String, String> tagToIdentity;
	private Hashtable<String, String> tagToSymloc;
	private CtxEntity ctxEntity;
	private ICtxBroker ctxBroker;
	private IIdentity serverIdentity;

	protected final Logger log = LoggerFactory.getLogger(getClass()); 


	public ContextRetriever(ICtxBroker ctxBroker, IIdentity serverIdentity){
		this.ctxBroker = ctxBroker;
		this.serverIdentity = serverIdentity;
		this.tagToIdentity = new Hashtable<String, String>();
		this.tagToPassword = new Hashtable<String, String>();
		this.tagToSymloc= new Hashtable<String, String>();

		try {
			List<CtxIdentifier> list = ctxBroker.lookup(serverIdentity, CtxModelType.ENTITY, RFID_SERVER_ENTITY).get();
			if (list.size()>0){
				CtxIdentifier ctxEntityId = list.get(0);
				ctxEntity = (CtxEntity) ctxBroker.retrieve(ctxEntityId).get();
				Set<CtxAttribute> tagToPassAttributes = ctxEntity.getAttributes(TAG_TO_PASSWORD);
				if (tagToPassAttributes.size()>0){

					byte[] binaryValue = tagToPassAttributes.iterator().next().getBinaryValue();
					if (binaryValue!=null){
						this.tagToPassword = (Hashtable<String, String>) SerialisationHelper.deserialise(binaryValue, this.getClass().getClassLoader());
					}else{
						this.tagToPassword = new Hashtable<String, String>();
					}
				}
				Set<CtxAttribute> tagToIdAttributes = ctxEntity.getAttributes(TAG_TO_IDENTITY);
				if (tagToIdAttributes.size()>0){			
					byte[] binaryValue = tagToIdAttributes.iterator().next().getBinaryValue();
					if (binaryValue!=null){
						this.tagToIdentity = (Hashtable<String, String>) SerialisationHelper.deserialise(binaryValue, this.getClass().getClassLoader());
					}else{
						this.tagToIdentity = new Hashtable<String, String>();
					}
				}
				
				Set<CtxAttribute> tagToSymlocAttributes = ctxEntity.getAttributes(TAG_TO_SYMLOC);
				if (tagToSymlocAttributes.size()>0){			
					byte[] binaryValue = tagToSymlocAttributes.iterator().next().getBinaryValue();
					if (binaryValue!=null){
						this.tagToSymloc = (Hashtable<String, String>) SerialisationHelper.deserialise(binaryValue, this.getClass().getClassLoader());
					}else{
						this.tagToSymloc = new Hashtable<String, String>();
					}
				}


			}else{
				this.tagToIdentity = new Hashtable<String, String>();
				this.tagToPassword = new Hashtable<String, String>();
				this.tagToSymloc = new Hashtable<String, String>();
			}
		} catch (InterruptedException e) {
			this.tagToIdentity = new Hashtable<String, String>();
			this.tagToPassword = new Hashtable<String, String>();
			this.tagToSymloc = new Hashtable<String, String>();
			e.printStackTrace();
		} catch (ExecutionException e) {
			this.tagToIdentity = new Hashtable<String, String>();
			this.tagToPassword = new Hashtable<String, String>();
			this.tagToSymloc = new Hashtable<String, String>();
			e.printStackTrace();
		} catch (CtxException e) {
			this.tagToIdentity = new Hashtable<String, String>();
			this.tagToPassword = new Hashtable<String, String>();
			this.tagToSymloc = new Hashtable<String, String>();
			e.printStackTrace();
		} catch (IOException e) {
			this.tagToIdentity = new Hashtable<String, String>();
			this.tagToPassword = new Hashtable<String, String>();
			this.tagToSymloc = new Hashtable<String, String>();
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			this.tagToIdentity = new Hashtable<String, String>();
			this.tagToPassword = new Hashtable<String, String>();
			this.tagToSymloc = new Hashtable<String, String>();
			e.printStackTrace();
		}

	}

	public Hashtable<String, String> getTagToIdentity() {
		return tagToIdentity;
	}


	public Hashtable<String, String> getTagToPassword() {
		return tagToPassword;
	}

	public Hashtable<String, String> getTagToSymloc() {
		return tagToSymloc;
	}

}
