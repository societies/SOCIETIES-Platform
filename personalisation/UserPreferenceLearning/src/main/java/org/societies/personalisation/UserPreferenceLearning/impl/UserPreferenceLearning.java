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

package org.societies.personalisation.UserPreferenceLearning.impl;

import java.util.Date;

import org.societies.api.identity.IIdentity;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.personalisation.UserPreferenceLearning.impl.threads.AA_AI;
import org.societies.personalisation.UserPreferenceLearning.impl.threads.AA_SI;
import org.societies.personalisation.UserPreferenceLearning.impl.threads.SA_AI;
import org.societies.personalisation.UserPreferenceLearning.impl.threads.SA_SI;
import org.societies.personalisation.preference.api.UserPreferenceLearning.IC45Learning;
import org.societies.personalisation.preference.api.model.IC45Consumer;

public class UserPreferenceLearning implements IC45Learning{
	
	private AA_AI aa_ai;
	private AA_SI aa_si;
	private SA_AI sa_ai;
	private SA_SI sa_si;
	private HistoryRetriever historyRetriever;
	private ICtxBroker ctxBroker;

	@Override
	//run preference learning on all actions for all identities
	public void runC45Learning(IC45Consumer requestor, Date startDate) {
		aa_ai = new AA_AI(requestor, startDate, historyRetriever);
        aa_ai.start();
	}

	@Override
	//run preference learning on specific service action parameterName for all identities
	public void runC45Learning(IC45Consumer requestor, Date startDate,
			ServiceResourceIdentifier serviceId, String parameterName) {
		sa_ai = new SA_AI(requestor, startDate, serviceId, parameterName, historyRetriever);
        sa_ai.start();
	}
	
	@Override
    //run C45 on all actions for specific identity
    public void runC45Learning(IC45Consumer requestor, Date startDate, IIdentity historyOwner){
        aa_si = new AA_SI(requestor, startDate, historyOwner, historyRetriever);
        aa_si.start();
    }

	@Override
    //run C45 learning on specific service action for specific identity
    public void runC45Learning(IC45Consumer requestor, Date startDate, IIdentity historyOwner,
    		ServiceResourceIdentifier serviceId, String parameterName){
        sa_si = new SA_SI(requestor, startDate, historyOwner, serviceId, parameterName, historyRetriever);
        sa_si.start();
    }
	
	public void initialiseUserPreferenceLearning(){
		if(this.ctxBroker == null){
			System.out.println("ctxBroker is null :(");
		}else{
			System.out.println("ctxBroker is not null :)");
			historyRetriever = new HistoryRetriever(ctxBroker);
		}
	}
	
	public void setCtxBroker(ICtxBroker broker){
		this.ctxBroker = broker;
	}
	
}
