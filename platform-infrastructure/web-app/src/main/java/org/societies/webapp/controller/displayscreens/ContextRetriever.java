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
package org.societies.webapp.controller.displayscreens;

import java.io.IOException;
import java.security.Identity;
import java.util.ArrayList;
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
import org.societies.api.css.devicemgmt.display.Screen;

/**
 * @author Eliza
 *
 */
public class ContextRetriever {


	private static final String SCREEN_LIST = "screenList";
	private static final String OTHER_LOC = "otherLocations";
	private static final String DISPLAY_SCREEN_ENTITY = "DISPLAY_SCREEN_ENTITY";
	private ArrayList<Screen> screenList;
	private ArrayList<String> otherLocations;
	private CtxEntity ctxEntity;
	private ICtxBroker ctxBroker;
	private IIdentity serverIdentity;

	protected final Logger log = LoggerFactory.getLogger(getClass()); 


	public ContextRetriever(ICtxBroker ctxBroker, IIdentity serverIdentity){
		this.ctxBroker = ctxBroker;
		this.serverIdentity = serverIdentity;
		this.screenList = new ArrayList<Screen>();
		this.otherLocations = new ArrayList<String>();

		try {
			List<CtxIdentifier> list = ctxBroker.lookup(serverIdentity, CtxModelType.ENTITY, DISPLAY_SCREEN_ENTITY).get();
			if (list.size()>0){
				CtxIdentifier ctxEntityId = list.get(0);
				ctxEntity = (CtxEntity) ctxBroker.retrieve(ctxEntityId).get();
				Set<CtxAttribute> screenToIPAttributes = ctxEntity.getAttributes(SCREEN_LIST);
				if (screenToIPAttributes.size()>0){

					byte[] binaryValue = screenToIPAttributes.iterator().next().getBinaryValue();
					if (binaryValue!=null){
						this.screenList = (ArrayList<Screen>) SerialisationHelper.deserialise(binaryValue, this.getClass().getClassLoader());
						log.debug("Amount in hashtable: " + this.screenList.size());
					}else{
						this.screenList = new ArrayList<Screen>();
					}
				}
				Set<CtxAttribute> otherLocationAttributes = ctxEntity.getAttributes(OTHER_LOC);
				if (otherLocationAttributes.size()>0){

					byte[] binaryValue = otherLocationAttributes.iterator().next().getBinaryValue();
					if (binaryValue!=null){
						this.otherLocations = (ArrayList<String>) SerialisationHelper.deserialise(binaryValue, this.getClass().getClassLoader());
						log.debug("Amount in hashtable: " + this.screenList.size());
					}else{
						this.screenList = new ArrayList<Screen>();
					}
				}
			}else{
				this.screenList = new ArrayList<Screen>();
			}
		} catch (InterruptedException e) {
			this.screenList = new ArrayList<Screen>();
			e.printStackTrace();
		} catch (ExecutionException e) {
			this.screenList = new ArrayList<Screen>();
			e.printStackTrace();
		} catch (CtxException e) {
			this.screenList = new ArrayList<Screen>();
			e.printStackTrace();
		} catch (IOException e) {
			this.screenList = new ArrayList<Screen>();
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			this.screenList = new ArrayList<Screen>();
			e.printStackTrace();
		}

	}

	public ArrayList<Screen> getScreens() {
		return this.screenList;
	}
	
	public ArrayList<String> getOtherLocations() {
		return this.otherLocations;
	}
	
	public void updateContext(ArrayList<Screen> screenList, ArrayList<String> otherLocations) {	
		this.screenList = screenList;
		this.otherLocations = otherLocations;
		try {

			List<CtxIdentifier> list = ctxBroker.lookup(serverIdentity, CtxModelType.ENTITY, DISPLAY_SCREEN_ENTITY).get();
			CtxEntity ctxEntity;
			if (list.size()>0){
				CtxIdentifier ctxEntityId = list.get(0);
				ctxEntity = (CtxEntity) ctxBroker.retrieve(ctxEntityId).get();
			}else{
				ctxEntity = this.ctxBroker.createEntity(serverIdentity, DISPLAY_SCREEN_ENTITY).get();
			}
			Set<CtxAttribute> screenListAttributes = ctxEntity.getAttributes(SCREEN_LIST);
			if (screenListAttributes.size()==0){
				CtxAttribute screenListAttribute = this.ctxBroker.createAttribute(ctxEntity.getId(), SCREEN_LIST).get();
				this.ctxBroker.updateAttribute(screenListAttribute.getId(), SerialisationHelper.serialise(this.screenList)).get();

			}else{
				CtxAttribute screenToIPAttribute = screenListAttributes.iterator().next();
				this.ctxBroker.updateAttribute(screenToIPAttribute.getId(), SerialisationHelper.serialise(this.screenList)).get();


			}
			Set<CtxAttribute> otherLocationsAttributes = ctxEntity.getAttributes(OTHER_LOC);
			if (otherLocationsAttributes.size()==0){
				CtxAttribute otherLocationsAttribute = this.ctxBroker.createAttribute(ctxEntity.getId(), OTHER_LOC).get();
				this.ctxBroker.updateAttribute(otherLocationsAttribute.getId(), SerialisationHelper.serialise(this.otherLocations)).get();

			}else{
				CtxAttribute otherLocationsAttribute = otherLocationsAttributes.iterator().next();
				this.ctxBroker.updateAttribute(otherLocationsAttribute.getId(), SerialisationHelper.serialise(this.otherLocations)).get();


			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


}
