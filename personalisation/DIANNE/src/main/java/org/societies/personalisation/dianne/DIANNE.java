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
	private IIdentity cssID;
	private boolean activated;

	public DIANNE(){
		
		d_nets = new HashMap<IIdentity, Network>();
		runnerMappings = new HashMap<String, NetworkRunner>();
		outcomes = null;
		activated = true;
	}

	@Override
	public Future<List<IDIANNEOutcome>> getOutcome(IIdentity ownerId, ServiceResourceIdentifier serviceId, String preferenceName) {
		if (LOG.isDebugEnabled()){
			LOG.debug("Request - getOutcome with values: "+ownerId.getBareJid()+", "+serviceId.getServiceInstanceIdentifier()+", "+preferenceName);
		}
		//no updates received - just return current outcome
		List<IDIANNEOutcome> results = new ArrayList<IDIANNEOutcome>();

		if(activated){
			if(runnerMappings.containsKey(ownerId.getBareJid())){
				if (LOG.isDebugEnabled()){
					LOG.debug("DIANNE already exists for this ownerId: "+ownerId.getBareJid());
				}
				NetworkRunner runner = runnerMappings.get(ownerId.getBareJid());
				IDIANNEOutcome outcome = runner.getPrefOutcome(serviceId, preferenceName);
				if(outcome!=null){
					results.add(outcome);
				}else{
					if (LOG.isDebugEnabled()){
						LOG.debug("No outcomes to return from DIANNE");
					}
				}
			}else{
				if (LOG.isDebugEnabled()){
					LOG.debug("No DIANNE exists for this ownerId: "+ownerId.getBareJid()+"...cannot return result");
				}
			}
		}else{
			if (LOG.isDebugEnabled()){
				LOG.debug("DIANNE learning is not enabled - ignoring input and returning empty results list");
			}
		}

		return new AsyncResult<List<IDIANNEOutcome>>(results);
	}

	@Override
	public Future<List<IDIANNEOutcome>> getOutcome(IIdentity ownerId, CtxAttribute attribute) {
		if (LOG.isDebugEnabled()){
			LOG.debug("Context update - getOutcome with values: "+ownerId.getBareJid()+", "+attribute.getType()+"="+attribute.getStringValue());	
		}
		List<IDIANNEOutcome> results = new ArrayList<IDIANNEOutcome>();

		if(activated){
			//process if context update is not null
			if(attribute.getType() != null && attribute.getStringValue()!=null){
				// Context update received!!!
				if(runnerMappings.containsKey(ownerId.getBareJid())){
					if (LOG.isDebugEnabled()){
						LOG.debug("DIANNE already exists for this ownerId: "+ownerId.getBareJid());
					}
					runnerMappings.get(ownerId.getBareJid()).contextUpdate(attribute);
				}else{
					if (LOG.isDebugEnabled()){
						LOG.debug("DIANNE does not exist for this ownerId: "+ownerId.getBareJid()+"...creating");
					}
					Network newD_net = new Network();
					NetworkRunner newRunner = new NetworkRunner(ownerId, newD_net, this);
					d_nets.put(ownerId, newD_net);
					runnerMappings.put(ownerId.getBareJid(), newRunner);
					newRunner.contextUpdate(attribute);
				}

				//wait for new outcomes
				int loopCount = 0;
				while(outcomes == null && loopCount < 10){
					try {
						if (LOG.isDebugEnabled()){
							LOG.debug("waiting for output response..."+loopCount);
						}
						Thread.sleep(500);
						loopCount++;
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				if(outcomes != null){
					results = outcomes;
					outcomes = null;
				}else{
					if (LOG.isDebugEnabled()){
						LOG.debug("Wait cycle exited - DIANNE did not return any new outcomes for this context update");
					}
				}

			}else{
				if (LOG.isDebugEnabled()){
					LOG.debug("Not performing context update - context update contained null element: "+attribute.getType()+"="+attribute.getStringValue());
				}
			}
		}else{
			if (LOG.isDebugEnabled()){
				LOG.debug("DIANNE learning is not enabled - ignoring input and returning empty results list");
			}
		}

		return new AsyncResult<List<IDIANNEOutcome>>(results);
	}

	@Override
	public Future<List<IDIANNEOutcome>> getOutcome(IIdentity ownerId, IAction action){
		LOG.info("Action update with value: "+ownerId.getBareJid()+", "+action.getparameterName()+"="+action.getvalue());		
		// Action update received!!!

		if(activated){
			if(runnerMappings.containsKey(ownerId.getBareJid())){
				LOG.info("DIANNE already exists for this ownerId: "+ownerId.getBareJid());
				runnerMappings.get(ownerId.getBareJid()).actionUpdate(action);
			}else{
				LOG.info("DIANNE does not exist for this ownerId: "+ownerId.getBareJid()+"...creating");
				Network newD_net = new Network();
				NetworkRunner newRunner = new NetworkRunner(ownerId, newD_net, this);
				d_nets.put(ownerId, newD_net);
				runnerMappings.put(ownerId.getBareJid(), newRunner);
				newRunner.actionUpdate(action);
			}	
		}else{
			if (LOG.isDebugEnabled()){
				LOG.debug("DIANNE learning is not enabled - ignoring input and returning empty results list");
			}
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
		activated = true;
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
		activated = false;
	}

	@Override
	/*
	 * Called by PersonalisationManager when initialised
	 * (non-Javadoc)
	 * @see org.societies.personalisation.DIANNE.api.DianneNetwork.IDIANNE#registerContext()
	 */
	public void registerContext(){
		if (LOG.isDebugEnabled()){
			LOG.debug("DIANNE is registering for default context updates: symLoc, status and temperature");
		}
		for(int i=0; i<defaultContext.length; i++){
			try {
				String nextType = defaultContext[i];
				IndividualCtxEntity individualCtxEntity = this.ctxBroker.retrieveIndividualEntity(cssID).get();
				
				Set<CtxAttribute> attributes = individualCtxEntity.getAttributes(nextType);
				
				if(attributes.iterator().hasNext()){
					if (LOG.isDebugEnabled()){
						LOG.debug("Registering for context update: "+defaultContext[i]);
					}
					persoMgr.registerForContextUpdate(cssID, PersonalisationTypes.DIANNE, (CtxAttributeIdentifier)attributes.iterator().next().getId());
				}else{
					if (LOG.isDebugEnabled()){
						LOG.debug("Ctx Attribute: "+defaultContext[i]+" does not yet exist - could not register for context updates");
					}
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

	@Override
	public void receiveDIANNEFeedback(IIdentity ownerId, IAction action){
		this.getOutcome(ownerId, action);
	}

	public void initialiseDIANNELearning(){
		cssID = commsMgr.getIdManager().getThisNetworkNode();
		retrieveNetworks();  //get Networks from context
		initialiseNetworks();  //start runners for each network
		//start DIANNE storage thread - store DIANNEs every 1?/5? minute(s)
		Thread persistThread = new Thread(new PersistenceManager(this, ctxBroker));
		persistThread.setName("DIANNE Persistence Thread");
		persistThread.start();
		
		//TEMPORARY FIX FOR #751 -
		this.disableDIANNELearning(cssID);
	
	}

	private void retrieveNetworks(){
		try {
			IndividualCtxEntity person = ctxBroker.retrieveIndividualEntity(cssID).get();
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