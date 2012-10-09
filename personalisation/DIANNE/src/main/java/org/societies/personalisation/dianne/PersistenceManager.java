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

package org.societies.personalisation.dianne;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAssociation;
import org.societies.api.context.model.CtxAssociationIdentifier;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.identity.IIdentity;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.context.model.CtxAssociationTypes;
import org.societies.api.internal.context.model.CtxAttributeTypes;
import org.societies.api.internal.context.model.CtxEntityTypes;
import org.societies.personalisation.dianne.model.Network;

public class PersistenceManager implements Runnable{

	private DIANNE dianne;
	private ICtxBroker ctxBroker;

	public PersistenceManager(DIANNE dianne, ICtxBroker ctxBroker){
		this.dianne = dianne;
		this.ctxBroker = ctxBroker;
	}

	@Override
	public void run() {		
		//store diannes to context
		HashMap<IIdentity, Network> d_nets = dianne.getDNets();
		Iterator<IIdentity> d_nets_it = d_nets.keySet().iterator();
		while(d_nets_it.hasNext()){
			IIdentity nextId = d_nets_it.next();
			Network nextNet = d_nets.get(nextId);
			storeToContext(nextId, nextNet);
		}

		//sleep for 5 minutes
		try {
			Thread.sleep(50000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void storeToContext(IIdentity identity, Network network){
		try {
			IndividualCtxEntity person = ctxBroker.retrieveCssOperator().get();
			Set<CtxAssociationIdentifier> hasDianneAssocIds = person.getAssociations(CtxAssociationTypes.HAS_DIANNE);
			if(hasDianneAssocIds.size()>0){  //found association in context
				CtxAssociation hasDianneAssoc = (CtxAssociation)ctxBroker.retrieve(hasDianneAssocIds.iterator().next()).get();
				Set<CtxEntityIdentifier> dianneEntities = hasDianneAssoc.getChildEntities();
				List<CtxEntityIdentifier> dianneIds = 
						ctxBroker.lookupEntities(
								new ArrayList<CtxEntityIdentifier>(dianneEntities), CtxAttributeTypes.ID, identity.getBareJid()
								).get();
				if(dianneIds.size()>0){//dianne entity found for this identity
					CtxEntity dianne = (CtxEntity)ctxBroker.retrieve(dianneIds.get(0)).get();
					Set<CtxAttribute> dNetAttrs = dianne.getAttributes(CtxAttributeTypes.D_NET);
					ctxBroker.updateAttribute(dNetAttrs.iterator().next().getId(), network);
				}else{//no dianne entity found for this identity - create
					createDianneEntity(hasDianneAssoc, identity, network);
				}
			}else{ //create association in context
				CtxAssociation newHasDianneAssoc = createHasDianneAssoc(person);
				createDianneEntity(newHasDianneAssoc, identity, network);
			}
		} catch (CtxException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}

	private CtxAssociation createHasDianneAssoc(CtxEntity parent){
		CtxAssociation newHasDianneAssoc = null;
		try {
			newHasDianneAssoc = ctxBroker.createAssociation(CtxAssociationTypes.HAS_DIANNE).get();
			newHasDianneAssoc.setParentEntity(parent.getId());
			ctxBroker.update(newHasDianneAssoc);
		} catch (CtxException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return newHasDianneAssoc;
	}

	private CtxEntity createDianneEntity(CtxAssociation hasDianneAssoc, IIdentity identity, Network network){
		CtxEntity newDianneEntity = null;
		try {
			newDianneEntity = ctxBroker.createEntity(CtxEntityTypes.DIANNE).get();
			hasDianneAssoc.addChildEntity(newDianneEntity.getId());
			ctxBroker.update(hasDianneAssoc);
			CtxAttribute newIdAttr = ctxBroker.createAttribute(newDianneEntity.getId(), CtxAttributeTypes.ID).get();
			CtxAttribute newNetworkAttr = ctxBroker.createAttribute(newDianneEntity.getId(), CtxAttributeTypes.D_NET).get();
			ctxBroker.updateAttribute(newIdAttr.getId(), identity.getBareJid());
			ctxBroker.updateAttribute(newNetworkAttr.getId(), network);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (CtxException e) {
			e.printStackTrace();
		}

		return newDianneEntity;
	}
}
