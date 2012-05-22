/* Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske druzbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVACAO, SA (PTIN), IBM Corp., 
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

/* @Author: Haoyi XIONG, haoyi.xiong@it-sudparis.eu
 * @Dependency:
 * org.societies.api.internal.personalisation.model.IOutcome;
 * org.societies.api.internal.useragent.model.AbstractDecisionMaker;
 * org.societies.api.internal.useragent.model.ConflictType;
 * org.societies.api.personalisation.model.IAction;
 */
package org.societies.useragent.decisionmaking;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import org.slf4j.*;

import org.societies.api.identity.IIdentity;
import org.societies.api.internal.personalisation.model.IOutcome;
import org.societies.api.internal.servicelifecycle.IServiceDiscovery;
import org.societies.api.internal.servicelifecycle.ServiceDiscoveryException;
import org.societies.useragent.conflict.*;
import org.societies.api.personalisation.model.IAction;
import org.societies.api.personalisation.model.IActionConsumer;
import org.societies.api.schema.servicelifecycle.model.Service;
import org.societies.useragent.conflict.ConfidenceTradeoffRule;
import org.societies.useragent.conflict.ConflictResolutionManager;
import org.societies.useragent.conflict.IntentPriorRule;

public class DecisionMaker extends AbstractDecisionMaker {

	private IIdentity entityID;
	
	private List<IActionConsumer> temporal=null;
	
	private IServiceDiscovery SerDiscovery;
	private Logger logging = LoggerFactory.getLogger(this.getClass());
	
	public IServiceDiscovery getSerDiscovery() {
		return SerDiscovery;
	}
	public void setSerDiscovery(IServiceDiscovery serDiscovery) {
		SerDiscovery = serDiscovery;
	}
	private void refreshServiceLookup(){
		List<IActionConsumer> lst=new ArrayList<IActionConsumer>();
		try {
			List<Service> ls=this.SerDiscovery.getLocalServices().get();
			for(Service ser:ls){
				if(ser instanceof IActionConsumer){
					lst.add((IActionConsumer)ser);
				}
			}
		} catch (ServiceDiscoveryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		this.temporal=lst;
	}
	
	public IIdentity getEntityID() {
		return entityID;
	}
	public void setEntityID(IIdentity entityID) {
		this.entityID = entityID;
	}

	public DecisionMaker(){
		ConflictResolutionManager man=new ConflictResolutionManager();
		man.addRule(new ConfidenceTradeoffRule());
		man.addRule(new IntentPriorRule());
		this.manager=man;
		logging.debug("Intialized DM");
	}
	@Override
	protected ConflictType detectConflict(IOutcome intent, IOutcome prefernce) {
		// TODO Auto-generated  method stub
		try {
			if (intent.getServiceID().equals(prefernce.getServiceID())) {
				if (intent.getparameterName().equals(
						prefernce.getparameterName())) {
					if (!intent.getvalue().
							equalsIgnoreCase(prefernce.getvalue())) {
						return ConflictType.PREFERNCE_INTENT_NOT_MATCH;
					}
				}
			}
			logging.debug("detecting conflict DM");
			return ConflictType.NO_CONFLICT;
		} catch (Exception e) {
			return ConflictType.UNKNOWN_CONFLICT;
		}
		
	}
	@Override
	public void makeDecision(List<IOutcome> intents, List<IOutcome> preferences) {
		this.refreshServiceLookup();
		super.makeDecision(intents, preferences);
	}

	@Override
	protected void implementIAction(IAction action) {
		// TODO Auto-generated method stub
		//@temporal solution depends on the 3rd party-services
		//System.out.println("****************************************");
		//System.out.println("implement the Action:\t"+action);
		//System.out.println("of Type:\t"+action.getServiceType());
		//System.out.println("with Parameter:\t"+action.getparameterName());
		//System.out.println("with Parameter:\t"+action.getvalue());
		//System.out.println("****************************************");
		logging.debug( "implement IAction DM");
		for(IActionConsumer consumer:this.temporal){
			if(consumer.getServiceIdentifier().equals(action.getServiceID())){
				consumer.setIAction(this.entityID, action);
				logging.debug("set IAction DM");
			}
		}
		
	}

}
