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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.personalisation.DIANNE.api.DianneNetwork.IDIANNE;
import org.societies.personalisation.DIANNE.api.model.IDIANNEOutcome;
import org.societies.personalisation.common.api.management.IInternalPersonalisationManager;
import org.societies.personalisation.common.api.model.PersonalisationTypes;
import org.societies.personalisation.dianne.model.IOutcomeListener;
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

public class DIANNE implements IDIANNE, IOutcomeListener{

	private Logger LOG = LoggerFactory.getLogger(DIANNE.class);
	private HashMap<IIdentity, Network> d_nets;  //IIdentity - Network mappings
	private HashMap<String, NetworkRunner> runnerMappings;  //IIdentity(bareJid) - runner mappings
	private List<IDIANNEOutcome> outcomes;
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
		runnerMappings = new HashMap<String, NetworkRunner>();
		outcomes = null;
	}

	@Override
	public Future<List<IDIANNEOutcome>> getOutcome(IIdentity ownerId, ServiceResourceIdentifier serviceId, String preferenceName) {
		//no updates received - just return current outcome
		List<IDIANNEOutcome> results = new ArrayList<IDIANNEOutcome>();
		if(runnerMappings.containsKey(ownerId.getBareJid())){
			LOG.info("Network Runner already exists for this ownerId: "+ownerId.getBareJid());
			NetworkRunner runner = runnerMappings.get(ownerId.getBareJid());
			IDIANNEOutcome outcome = runner.getPrefOutcome(serviceId, preferenceName);
			if(outcome!=null){
				results.add(outcome);
			}
		}else{
			LOG.info("No DIANNE exists for this identity: "+ownerId.getBareJid());
		}
		return new AsyncResult<List<IDIANNEOutcome>>(results);
	}

	@Override
	public Future<List<IDIANNEOutcome>> getOutcome(IIdentity ownerId, CtxAttribute attribute) {
		LOG.info("Received request for outcome with new context update");		
		List<IDIANNEOutcome> results = new ArrayList<IDIANNEOutcome>();
		// Context update received!!!
		if(runnerMappings.containsKey(ownerId.getBareJid())){
			LOG.info("Network runner already exists for this ownerId: "+ownerId.getBareJid());
			runnerMappings.get(ownerId.getBareJid()).contextUpdate(attribute);
		}else{
			LOG.info("Network runner does not exist for this ownerId: "+ownerId.getBareJid());
			Network newD_net = new Network();
			NetworkRunner newRunner = new NetworkRunner(ownerId, newD_net, this);
			d_nets.put(ownerId, newD_net);
			runnerMappings.put(ownerId.getBareJid(), newRunner);
			newRunner.contextUpdate(attribute);
		}
		
		//wait for new outcomes
		while(outcomes == null){
			try {
				LOG.info("waiting for output response...");
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		results = outcomes;
		outcomes = null;
		
		return new AsyncResult<List<IDIANNEOutcome>>(results);
	}

	@Override
	public Future<List<IDIANNEOutcome>> getOutcome(IIdentity ownerId, IAction action){
		LOG.info("Received request for outcome with new action update");		
		// Action update received!!!
		if(runnerMappings.containsKey(ownerId.getBareJid())){
			runnerMappings.get(ownerId.getBareJid()).actionUpdate(action);
		}else{
			Network newD_net = new Network();
			NetworkRunner newRunner = new NetworkRunner(ownerId, newD_net, this);
			d_nets.put(ownerId, newD_net);
			runnerMappings.put(ownerId.getBareJid(), newRunner);
			newRunner.actionUpdate(action);
		}		
		//No new outcomes will be provided after action updates - return empty list
		return new AsyncResult<List<IDIANNEOutcome>>(new ArrayList<IDIANNEOutcome>());
	}

	
	@Override
	public void enableDIANNELearning(IIdentity ownerId) {
		LOG.info("Enabling incremental learning for identity: "+ ownerId.getBareJid());
		if(runnerMappings.containsKey(ownerId.getBareJid())){
			NetworkRunner network = runnerMappings.get(ownerId.getBareJid());
			network.play();
		}else{
			LOG.info("No networks exist for this identity");
		}
	}

	@Override
	public void disableDIANNELearning(IIdentity ownerId) {
		LOG.info("Disabling incremental learning for identity: "+ ownerId.getBareJid());	
		if(runnerMappings.containsKey(ownerId.getBareJid())){
			NetworkRunner network = runnerMappings.get(ownerId.getBareJid());
			network.pause();
		}else{
			LOG.info("No networks exist for this identity");
		}
	}
	
	@Override
	/*
	 * Called by PersonalisationManager when initialised
	 * (non-Javadoc)
	 * @see org.societies.personalisation.DIANNE.api.DianneNetwork.IDIANNE#registerContext()
	 */
	public void registerContext(){
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

	public void initialiseDIANNELearning(){
		personID = commsMgr.getIdManager().getThisNetworkNode();
		retrieveNetworks();  //get Networks from context
		initialiseNetworks();  //start runners for each network
		//start DIANNE storage thread - store DIANNEs every 1?/5? minute(s)
		Thread persistThread = new Thread(new PersistenceManager(this, ctxBroker));
		persistThread.setName("DIANNE Persistence Thread");
		persistThread.start();
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
								if(nextAttr.getType().equals(CtxAttributeTypes.D_NET)){
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
			NetworkRunner nextRunner = new NetworkRunner(nextIdentity, nextNetwork, this);
			runnerMappings.put(nextIdentity.getBareJid(), nextRunner);
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
	
	public HashMap<IIdentity, Network> getDNets(){
		return this.d_nets;
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

	@Override
	public void handleOutcomes(List<IDIANNEOutcome> outcomes) {
		LOG.info("Received output response");
		for(IDIANNEOutcome nextOutcome : outcomes){
			LOG.info(nextOutcome.getServiceID().getServiceInstanceIdentifier()+": "+nextOutcome.getparameterName()+"="+nextOutcome.getvalue());
		}
		this.outcomes = outcomes;
	}
}