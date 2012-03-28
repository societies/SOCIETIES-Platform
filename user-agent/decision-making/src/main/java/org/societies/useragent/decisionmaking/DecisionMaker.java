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

import org.societies.api.internal.personalisation.model.IOutcome;
import org.societies.useragent.conflict.*;
import org.societies.api.personalisation.model.IAction;
import org.societies.useragent.conflict.ConfidenceTradeoffRule;
import org.societies.useragent.conflict.ConflictResolutionManager;
import org.societies.useragent.conflict.IntentPriorRule;

public class DecisionMaker extends AbstractDecisionMaker {

	public DecisionMaker(){
		ConflictResolutionManager man=new ConflictResolutionManager();
		man.addRule(new ConfidenceTradeoffRule());
		man.addRule(new IntentPriorRule());
		this.manager=man;
		
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
			return ConflictType.NO_CONFLICT;
		} catch (Exception e) {
			return ConflictType.UNKNOWN_CONFLICT;
		}
	}

	@Override
	protected void implementIAction(IAction action) {
		// TODO Auto-generated method stub
		//@temporal solution depends on the 3rd party-services
		System.out.println("****************************************");
		System.out.println("implement the Action:\t"+action);
		System.out.println("of Type:\t"+action.getServiceType());
		System.out.println("with Parameter:\t"+action.getparameterName());
		System.out.println("with Parameter:\t"+action.getvalue());
		System.out.println("****************************************");
		
	}

}
