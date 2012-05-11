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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.societies.personalisation.DIANNE.api.DianneNetwork.IDIANNE;
import org.societies.personalisation.DIANNE.api.model.IDIANNEOutcome;
import org.societies.personalisation.common.api.management.IInternalPersonalisationManager;
import org.societies.personalisation.common.api.model.PersonalisationTypes;
import org.societies.personalisation.dianne.model.Network;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAssociation;
import org.societies.api.context.model.CtxAssociationIdentifier;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.context.model.util.SerialisationHelper;
import org.societies.api.identity.IIdentity;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.context.model.CtxAssociationTypes;
import org.societies.api.internal.context.model.CtxAttributeTypes;
import org.societies.api.personalisation.model.IAction;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.springframework.scheduling.annotation.AsyncResult;

public class DIANNE implements IDIANNE{

	private HashMap<IIdentity, Network> d_nets;
	private HashMap<IIdentity, NetworkRunner> runnerMappings;
	private String[] defaultContext = {
			CtxAttributeTypes.LOCATION_SYMBOLIC, 
			CtxAttributeTypes.STATUS,
			CtxAttributeTypes.TEMPERATURE
	};

	private IInternalPersonalisationManager persoMgr;
	private ICtxBroker ctxBroker;
	private ICommManager commsMgr;
	private IIdentity personID;

	public DIANNE(){
		d_nets = new HashMap<IIdentity, Network>();
		runnerMappings = new HashMap<IIdentity, NetworkRunner>();
	}

	@Override
	public Future<List<IDIANNEOutcome>> getOutcome(IIdentity ownerId, ServiceResourceIdentifier serviceId, String preferenceName) {
		return new AsyncResult<List<IDIANNEOutcome>>(new ArrayList<IDIANNEOutcome>());
	}

	@Override
	public Future<List<IDIANNEOutcome>> getOutcome(IIdentity ownerId, CtxAttribute attribute) {
		// Context update received!!!
		if(runnerMappings.containsKey(ownerId)){

		}else{
			//NetworkRunner newNetwork = new NetworkRunner();
		}
		return new AsyncResult<List<IDIANNEOutcome>>(new ArrayList<IDIANNEOutcome>());
	}

	@Override
	public Future<List<IDIANNEOutcome>> getOutcome(IIdentity ownerId,
			IAction action){
		// Action update received!!!
		return new AsyncResult<List<IDIANNEOutcome>>(new ArrayList<IDIANNEOutcome>());
	}

	@Override
	public void enableDIANNELearning(IIdentity ownerId) {
		System.out.println("Enabling incremental learning for identity: "+ ownerId);
		if(runnerMappings.containsKey(ownerId)){
			NetworkRunner network = runnerMappings.get(ownerId);
			network.play();
		}else{
			System.out.println("No networks exist for this identity");
		}
	}

	@Override
	public void disableDIANNELearning(IIdentity ownerId) {
		System.out.println("Disabling incremental learning for identity: "+ ownerId);	
		if(runnerMappings.containsKey(ownerId)){
			NetworkRunner network = runnerMappings.get(ownerId);
			network.pause();
		}else{
			System.out.println("No networks exist for this identity");
		}
	}


	public void initialiseDIANNELearning(){
		personID = commsMgr.getIdManager().getThisNetworkNode();
		retrieveNetworks();  //get Networks from context
		initialiseNetworks();  //start runners for each network
		registerForContext();  //register for default context updates from PersonalisationMgr
		//start DIANNE storage thread - store DIANNEs every 1?/5? minute(s)
	}
	
	private void retrieveNetworks(){
		try {
			IndividualCtxEntity person = ctxBroker.retrieveCssOperator().get();
			Set<CtxAssociationIdentifier> hasDianneAssocIDs = person.getAssociations(CtxAssociationTypes.HAS_DIANNE);
			
			if(hasDianneAssocIDs.size() > 0){// HAS_DIANNE association found in context
				CtxAssociation hasDianneAssoc = (CtxAssociation)ctxBroker.retrieve(hasDianneAssocIDs.iterator().next()).get();
				Set<CtxEntityIdentifier> dianneEntityIDs = hasDianneAssoc.getChildEntities();
				
				if(dianneEntityIDs.size() > 0){// DIANNEs found in context
					for(CtxEntityIdentifier nextDianneID: dianneEntityIDs){
						CtxEntity nextDianne = (CtxEntity)ctxBroker.retrieve(nextDianneID).get();
						Set<CtxAttribute> attributes = nextDianne.getAttributes();
						
						if(attributes.size() > 0){// Network and ID found for this DIANNE entity
							IIdentity dNetID = null;
							Network dNet = null;
							for(CtxAttribute nextAttr: attributes){
								if(nextAttr.getType().equals("dnet"/*CtxAttributeTypes.D_NET*/)){
									dNet = (Network)SerialisationHelper.deserialise(nextAttr.getBinaryValue(), this.getClass().getClassLoader());
								}else if(nextAttr.getType().equals(CtxAttributeTypes.ID)){
									dNetID = (IIdentity)SerialisationHelper.deserialise(nextAttr.getBinaryValue(), this.getClass().getClassLoader());
								}
							}
							d_nets.put(dNetID, dNet);
						}
					}
				}
			}
		} catch (CtxException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private void initialiseNetworks(){
		Iterator<IIdentity> e = d_nets.keySet().iterator();
		while(e.hasNext()){
			IIdentity nextIdentity = e.next();
			Network nextNetwork = d_nets.get(nextIdentity);
			NetworkRunner nextRunner = new NetworkRunner(nextNetwork);
			runnerMappings.put(nextIdentity, nextRunner);
		}
	}

	private void registerForContext(){
		
		for(int i=0; i<defaultContext.length; i++){
			try {
				String nextType = defaultContext[i];
				List<CtxIdentifier> attrIDs = ctxBroker.lookup(CtxModelType.ATTRIBUTE, nextType).get();
				if(attrIDs.size() > 0){
					persoMgr.registerForContextUpdate(personID, PersonalisationTypes.DIANNE, (CtxAttributeIdentifier)attrIDs.get(0));
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			} catch (CtxException e) {
				e.printStackTrace();
			}
			
		}
	}


	/*
	 * getter methods. Do not rename. The name of these methods are referenced in the spring osgi files
	 */
	public IInternalPersonalisationManager getPersoMgr() {
		return persoMgr;
	}
	
	public ICtxBroker getCtxBroker(){
		return ctxBroker;
	}
	
	public ICommManager getCommsMgr(){
		return commsMgr;
	}

	/*
	 * setter methods. Do not rename. The name of these methods are referenced in the spring osgi files
	 */
	public void setPersoMgr(IInternalPersonalisationManager persoMgr) {		
		this.persoMgr = persoMgr;
	}
	
	public void setCtxBroker(ICtxBroker ctxBroker){
		this.ctxBroker = ctxBroker;
	}
	
	public void setCommsMgr(ICommManager commsMgr){
		this.commsMgr = commsMgr;
	}
}