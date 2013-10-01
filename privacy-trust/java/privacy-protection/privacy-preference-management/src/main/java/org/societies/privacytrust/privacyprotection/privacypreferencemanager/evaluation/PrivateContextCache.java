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
package org.societies.privacytrust.privacyprotection.privacypreferencemanager.evaluation;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;

public class PrivateContextCache {
	
	/* cache:
	 * CtxIdentifier: the identifier of the context attribute
	 * String: the value of that identifier currently
	 */
	private Hashtable<CtxIdentifier, String> cache;
	/* mapper:
	 * String: the type of the context attribute (i.e. symloc)
	 * CtxIdentifier: the context attribute identifier 
	 */
	private Hashtable<String, CtxIdentifier> mapping;
	private Logger logging = LoggerFactory.getLogger(this.getClass());
	private ICtxBroker ctxBroker;
	private ContextCacheUpdater updater;
	
	
	public PrivateContextCache(ICtxBroker broker){
		this.ctxBroker = broker;
		this.mapping = new Hashtable<String, CtxIdentifier>();
		this.cache = new Hashtable<CtxIdentifier, String>();
		this.updater = new ContextCacheUpdater(broker,this);
	}
	
	public ContextCacheUpdater getContextCacheUpdater(){
		return updater;
	}
	
	public String getContextValue(CtxIdentifier id){
		
		this.logging.debug("looking for value of context id: "+id.toUriString());
		this.printCache();
		if (this.cache.containsKey(id)){
			this.logging.debug("cache contains context value of id: "+id.toUriString());
			return this.cache.get(id);
		}
		this.logging.debug("cache doesn't have id:"+id.toUriString());
		//cache doesn't contain the context identifier so we're going to get it from context, add it to the cache and return the value
		this.retrieveContext(id);
		if (this.cache.containsKey(id)){
			return this.cache.get(id);
		}
		
		return "";
	
	}

	public void printCache(){
		this.logging.debug("********* CONTEXT CACHE CONTENTS START ********************");
		Enumeration<String> e = mapping.keys();
		
		while (e.hasMoreElements()){
			String type = e.nextElement();
			CtxIdentifier id = mapping.get(type);
			this.logging.debug("Type: "+type+" :: ID: "+mapping.get(type).toUriString()+"  VALUE: "+this.cache.get(id));
		}
		this.logging.debug("********* CONTEXT CACHE CONTENTS END ********************");
	}
	
	private void retrieveContext(String type){
		this.printCache();
		
		try {
			Future<List<CtxIdentifier>> futureAttrList =ctxBroker.lookup(CtxModelType.ATTRIBUTE, type); 
			List<CtxIdentifier> attrList = futureAttrList.get();
			if (attrList.size()>0){
				CtxIdentifier id = (CtxIdentifier) attrList.get(0);
				CtxAttribute attr =  (CtxAttribute) ctxBroker.retrieve(id);
				String val = attr.getStringValue();
				this.mapping.put(type, id);
				this.cache.put(id,val);
			}
		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void retrieveContext(CtxIdentifier id){
		this.updater.registerForContextEvent((CtxAttributeIdentifier) id);
		this.printCache();
		//JOptionPane.showMessageDialog(null, "contacting context DB for retrieving id"+id.toUriString());
		this.logging.debug("contacting context DB for retrieving id"+id.toUriString());
		try {
			CtxAttribute attr = (CtxAttribute) ctxBroker.retrieve(id).get();
			if (null!=attr){
				this.logging.debug ("found id: "+id.toUriString()+" in context DB");
				String val = attr.getStringValue();
				String type = attr.getType();
				if (type==null){
					this.logging.debug("context attribute type is null!");
				}
				if (id==null){
					this.logging.debug("Context ID is null!");
				}
				
				if (val==null){
					this.logging.debug("String value of attribute is null!");
				}
				this.mapping.put(type, id);
				this.cache.put(id,val);
			
				this.logging.debug("updated Context Cache for context type: "+type+" with id: "+id.toUriString()+" with value: "+this.cache.get(id));
			}else{
				this.logging.debug("id :"+id.toUriString()+" not found in context DB");
			}
		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public void updateCache(CtxAttribute ctxAttr){

		if (ctxAttr==null){
			this.logging.debug("Attempt to update Policy Manager context cache with null CtxAttribute, ignoring ");
			return;
		}
		String type = ctxAttr.getType();
		String value = ctxAttr.getStringValue();
		CtxIdentifier id = ctxAttr.getId();
		this.cache.put(id, value);
		this.mapping.put(type, id);
		this.logging.debug("updated Context Cache for context type: "+type+" with id: "+id.toUriString()+" with value: "+this.cache.get(id));
		this.printCache();
	}
	

	private class Pair{
		private String type;
		private ServiceResourceIdentifier id;
		Pair(String serviceType, ServiceResourceIdentifier serviceID){
			this.setServiceType(serviceType);
			this.setServiceId(serviceID);
		}
		public void setServiceType(String type) {
			this.type = type;
		}
		public String getServiceType() {
			return type;
		}
		public void setServiceId(ServiceResourceIdentifier id) {
			this.id = id;
		}
		public ServiceResourceIdentifier getServiceId() {
			return this.id;
		}
		
		public boolean equals(Pair p){
			if (!(this.type.equalsIgnoreCase(p.getServiceType()))){
				return false;
			}
			if (!(this.id.toString().equalsIgnoreCase(p.getServiceId().toString()))){
				return false;
			}
			
			return true;
		}
	}

	
}

