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
package org.societies.platform.servicelifecycle.servicecontrol;

import java.net.URI;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.internal.security.policynegotiator.INegotiationCallback;
import org.societies.api.internal.security.policynegotiator.INegotiationProviderSLMCallback;

/**
 * The callback object for the Negotiation procedure
 *
 * @author <a href="mailto:sanchocsa@gmail.com">Sancho Rêgo</a> (PTIN)
 *
 */
public class ServiceNegotiationCallback implements INegotiationCallback, INegotiationProviderSLMCallback {

	static final Logger logger = LoggerFactory.getLogger(ServiceNegotiationCallback.class);

	private final long TIMEOUT = 150;
	private BlockingQueue<ServiceNegotiationResult> resultList;
	
	/**
	 * 
	 */
	public ServiceNegotiationCallback() {
		
		if(logger.isDebugEnabled())
			logger.debug("ServiceNegotiationCallback created!");
		
		resultList = new ArrayBlockingQueue<ServiceNegotiationResult>(1);
	}


	@Override
	public void onNegotiationError(String msg) {
		if(logger.isDebugEnabled())
			logger.debug("Service negotiation complete: Error: " + msg);
		
		try {
			resultList.put(new ServiceNegotiationResult(false, null, msg));
		} catch (InterruptedException e) {
			logger.error("Error putting result in List");
			e.printStackTrace();
		}
	}


	@Override
	public void onNegotiationComplete(String agreementKey, List<URI> jars) {
		if(logger.isDebugEnabled())
			logger.debug("Service negotiation complete: agreementKey: " + agreementKey);
		
		if(agreementKey == null){
			if(logger.isDebugEnabled())
				logger.debug("AgreementKey came back null! This means that negotiation failed");
			
			try {
				resultList.put(new ServiceNegotiationResult(false, jars, agreementKey));
			} catch (InterruptedException e) {
				logger.error("Error putting result in List");
				e.printStackTrace();
			}
				
		} else{
			
			if(logger.isDebugEnabled())
				logger.debug("AgreementKey came back normal, so we proceed!");
			
			try {
				resultList.put(new ServiceNegotiationResult(true, jars, agreementKey));
			} catch (InterruptedException e) {
				logger.error("Error putting result in List");
				e.printStackTrace();
			}
		}
		
	}
	

	public ServiceNegotiationResult getResult() {
		try {
			//return resultList.poll(TIMEOUT, TimeUnit.SECONDS);
			ServiceNegotiationResult result = resultList.take();
			
			if(logger.isDebugEnabled())
				logger.debug("getResult: " + result);
			
			return result;
			
		} catch (InterruptedException e) {
			logger.error("Error getting result in List");
			e.printStackTrace();
			return null;
		}
	}
	
	public ServiceNegotiationResult getResult(long timeout) {
		try {
			ServiceNegotiationResult result = resultList.poll(timeout, TimeUnit.SECONDS);
			
			if(logger.isDebugEnabled())
				logger.debug("getResult: " + result);
			
			return result;
			
		} catch (InterruptedException e) {
			logger.error("Error getting result in List");
			e.printStackTrace();
			return null;
		}
	}

	public class ServiceNegotiationResult{
		
		boolean success;
		List<URI> serviceUri;
		String agreementKey;
		
		public ServiceNegotiationResult(){}
		
		public ServiceNegotiationResult(boolean success, List<URI> serviceUri, String agreementKey){
			this.success = success;
			this.serviceUri = serviceUri;
			this.agreementKey = agreementKey;
		}
		
		public void setServiceUri(List<URI> serviceUri){
			this.serviceUri = serviceUri;
		}
		
		public void setSuccess(boolean success){
			this.success = success;
		}
		
		public void setAgreementKey(String agreementKey){
			this.agreementKey = agreementKey;
		}
		
		public List<URI> getServiceUri(){
			return serviceUri;
		}
		
		public boolean getSuccess(){
			return success;
		}
		
		public String getAgreementKey(){
			return agreementKey;
		}
		
		@Override
		public String toString(){
			return "" + success + serviceUri + agreementKey;
		}
		
	}

	
	@Override
	public void notifySuccess() {
		if(logger.isDebugEnabled())
			logger.debug("Registering Service on Policy Negotiator successful.");
		try {
			resultList.put(new ServiceNegotiationResult(true, null, null));
		} catch (InterruptedException e) {
			logger.error("Error putting result in List");
			e.printStackTrace();
		}
	}


	@Override
	public void notifyError(String msg, Throwable e) {
		logger.warn("Registering Service on Policy Negotiator error: " + msg);
		try {
			resultList.put(new ServiceNegotiationResult(false, null, msg));
		} catch (InterruptedException ex) {
			logger.error("Error putting result in List");
			ex.printStackTrace();
		}
	}
}
