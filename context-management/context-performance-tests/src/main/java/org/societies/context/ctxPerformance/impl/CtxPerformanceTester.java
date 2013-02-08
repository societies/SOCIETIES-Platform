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
package org.societies.context.ctxPerformance.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxHistoryAttribute;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.INetworkNode;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.context.model.CtxAttributeTypes;
import org.societies.api.internal.logging.IPerformanceMessage;
import org.societies.api.internal.logging.PerformanceMessage;
import org.societies.api.osgi.event.CSSEvent;
import org.societies.api.osgi.event.EventListener;
import org.societies.api.osgi.event.EventTypes;
import org.societies.api.osgi.event.IEventMgr;
import org.societies.api.osgi.event.InternalEvent;
import org.societies.api.schema.cssmanagement.CssRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

/**
 * This class is used to add initial context data.
 * 
 * @author <a href="mailto:nikosk@cn.ntua.gr">Nikos Kalatzis</a> (ICCS)
 * @since 0.4
 */
@Service
@Lazy(false)
public class CtxPerformanceTester{

	/** The logging facility. */
	private static final Logger LOG = LoggerFactory.getLogger(CtxPerformanceTester.class);
	
	/** to define a dedicated Logger for Performance Testing */
	private static Logger PERF_LOG = LoggerFactory.getLogger("PerformanceMessage");
	
	
	/** The internal BaseUser Broker service. */
	private ICtxBroker ctxBroker;

	/** The Comm Mgr service. */
	private ICommManager commMgr;

	/** The Identity Mgr service. */
	private IIdentityManager idMgr; 

	private INetworkNode cssNodeId;
	private IIdentity cssOwnerId;
	private static CtxEntityIdentifier ownerCtxId;
	private IndividualCtxEntity indiEntity;

	@Autowired(required=true)
	CtxPerformanceTester(ICtxBroker ctxBroker,ICommManager commMgr) {

		if (LOG.isInfoEnabled())
			LOG.info(this.getClass() + " instantiated");

		this.commMgr = commMgr;
		this.ctxBroker = ctxBroker;

		this.idMgr = commMgr.getIdManager();
		this.cssOwnerId = this.getLocalIdentity();

		startMonitoring();

	}


	public void startMonitoring() {

		Timer timer1 = new Timer();             // Get timer 1
		long delay1 = 60*1000;                   // 60 seconds delay

		System.out.println("startMonitoring");
		timer1.schedule(new GetVolume(this.ctxBroker, this.cssOwnerId), 0, delay1);	
	}



	private IIdentity getLocalIdentity()  {

		IIdentity cssOwnerId = null;
		INetworkNode cssNodeId = this.idMgr.getThisNetworkNode();
		try {
			cssOwnerId = this.idMgr.fromJid(cssNodeId.getBareJid());
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return cssOwnerId;	
	}


	private class GetVolume extends TimerTask{


		ICtxBroker internalCtxBroker;
		IIdentity internalOwnerId;


		public GetVolume(ICtxBroker ctxBroker,IIdentity cssOwnerId ){

			this.internalCtxBroker = ctxBroker;
			this.internalOwnerId = cssOwnerId;

			System.out.println("inside GetVolume broker reference "+this.internalCtxBroker);
			System.out.println("inside GetVolume broker internalOwnerCtxId "+this.internalOwnerId);
		}



		public byte[] toByteArray (Object obj)
		{
			byte[] bytes = null;
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			try {
				ObjectOutputStream oos = new ObjectOutputStream(bos); 
				oos.writeObject(obj);
				oos.flush(); 
				oos.close(); 
				bos.close();
				bytes = bos.toByteArray ();
			}
			catch (IOException ex) {
				//TODO: Handle the exception
			}
			return bytes;
		}



		@Override
		public void run() {

			long  attrSize = 0;
			long  assocSize = 0;
			long  entSize = 0;

			try {
				indiEntity = this.internalCtxBroker.retrieveIndividualEntity(this.internalOwnerId).get();

				//current context data
				
				byte entBytes [] = toByteArray(indiEntity);
				entSize = entBytes.length;
				System.out.println("entity volume "+ entSize);
			
				if(indiEntity.getAttributes() != null){
					LOG.info("Attrs volume "+ indiEntity.getAttributes());					
					byte attrBytes [] = toByteArray(indiEntity.getAttributes());
					attrSize = attrBytes.length;
					System.out.println("attribute volume length "+ attrSize);
				}

				if(indiEntity.getAssociations() != null){
					LOG.info("Assoc volume "+ indiEntity.getAssociations());					
					System.out.println("association volume "+ indiEntity.getAssociations());
					byte assocBytes [] = toByteArray(indiEntity.getAssociations());
					assocSize = assocBytes.length; 
					System.out.println("association volume length "+ assocSize);
				}


				IPerformanceMessage m1 = new PerformanceMessage();
				m1.setTestContext("ContextBroker_Volume_UserContextData");
				m1.setSourceComponent(this.getClass()+"");
				m1.setPerformanceType(IPerformanceMessage.Memory);
				m1.setOperationType("CSS_ContextData");
				m1.setD82TestTableName("S56");
				long volumeCtxDB = attrSize + assocSize + entSize;
				m1.setPerformanceNameValue("User Ctx Volume="+(volumeCtxDB));

				PERF_LOG.trace(m1.toString());

				//history context data
				
				long  attrHocSize = 0;
				
				if(indiEntity.getAttributes() != null){
					Set<CtxAttribute> attrSet = indiEntity.getAttributes();
					
					for(CtxAttribute attr : attrSet){
					
						if(attr.isHistoryRecorded()){
							List<CtxHistoryAttribute> attrList = this.internalCtxBroker.retrieveHistory(attr.getId(), null, null).get();
							byte attrHistoryBytes [] = toByteArray(attrList);
							long attrTempHocSize = attrHistoryBytes.length;
							attrHocSize = attrHocSize + attrTempHocSize; 
						}
					}
					System.out.println("individual hoc attribute volume length "+ attrSize);
						
				}
								
				IPerformanceMessage m2 = new PerformanceMessage();
				m2.setTestContext("ContextBroker_Volume_UserContextHistoryData");
				m2.setSourceComponent(this.getClass()+"");
				m2.setPerformanceType(IPerformanceMessage.Memory);
				m2.setOperationType("CSS_HistoryContextData");
				m2.setD82TestTableName("S56");
				long volumeHoCCtxDB = attrHocSize; 
				m2.setPerformanceNameValue("User Ctx History Volume="+(volumeHoCCtxDB));
									
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (CtxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}