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
package org.societies.display.client;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.CtxException;
import org.societies.api.context.event.CtxChangeEvent;
import org.societies.api.context.event.CtxChangeEventListener;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.Requestor;
import org.societies.api.internal.context.broker.ICtxBroker;


/**
 * Describe your class here...
 *
 * @author Eliza
 *
 */
public class ContextEventListener implements CtxChangeEventListener{

	private final DisplayPortalClient client;
	private static Logger LOG = LoggerFactory.getLogger(ContextEventListener.class);

	private ICtxBroker ctxBroker;

	private final IIdentity userIdentity;

	private final Requestor requestor;

	public ContextEventListener(DisplayPortalClient client, ICtxBroker ctxBroker, IIdentity userIdentity, Requestor requestor){
		this.client = client;
		this.ctxBroker = ctxBroker;
		this.userIdentity = userIdentity;
		this.requestor = requestor;
		this.registerForLocationEvents();
	}

	public void registerForLocationEvents(){



		try {
			final IndividualCtxEntity individualCtxEntity = this.ctxBroker.retrieveIndividualEntity(userIdentity).get();
			
			this.ctxBroker.registerForChanges(this, individualCtxEntity.getId(), CtxAttributeTypes.LOCATION_SYMBOLIC);
			if(LOG.isDebugEnabled()) LOG.debug("Registered for symloc events");
			//}
		} catch (CtxException e) {
			// TODO Auto-generated catch block
            if(LOG.isDebugEnabled()) LOG.debug("1 " +e.toString());
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
            if(LOG.isDebugEnabled()) LOG.debug("2 " +e.toString());
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
            if(LOG.isDebugEnabled()) LOG.debug("3 " + e.toString());
			e.printStackTrace();
		}



	}
	@Override
	public void onCreation(CtxChangeEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onModification(final CtxChangeEvent event) {

		
		if(LOG.isDebugEnabled()) LOG.debug("Received context event: "+event.getId().toUriString());

		new Thread(){
			public void run(){
				CtxIdentifier ctxIdentifier = event.getId();
				Future<CtxModelObject> futureAttribute;
				try {
					Thread.sleep(10);
					futureAttribute = ctxBroker.retrieve(requestor,ctxIdentifier);

					try {
						CtxAttribute ctxAttribute = (CtxAttribute) futureAttribute.get();
						if(LOG.isDebugEnabled()) LOG.debug("Received context event for "+ctxAttribute.getType()+" with value: "+ctxAttribute.getStringValue());
						client.updateUserLocation(ctxAttribute.getStringValue().trim());

					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ExecutionException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} catch (CtxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				if(LOG.isDebugEnabled()) LOG.debug("thread of handleInternalEvent method finished executing");
			}
		}.start();

	}

	@Override
	public void onRemoval(CtxChangeEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUpdate(CtxChangeEvent arg0) {
		// TODO Auto-generated method stub

	}
}
