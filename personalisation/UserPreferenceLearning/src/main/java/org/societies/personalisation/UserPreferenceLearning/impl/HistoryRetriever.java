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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.context.model.CtxHistoryAttribute;
import org.societies.api.context.model.util.SerialisationHelper;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.personalisation.model.Action;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;

public class HistoryRetriever {

	private ICtxBroker ctxBroker;
	Logger LOG = LoggerFactory.getLogger(this.getClass());

	public HistoryRetriever(ICtxBroker ctxBroker){
		this.ctxBroker = ctxBroker;
	}

	public Map<CtxHistoryAttribute, List<CtxHistoryAttribute>> getFullHistory(Date startDate){
		Map<CtxHistoryAttribute, List<CtxHistoryAttribute>> history = null;
		try {
			history = ctxBroker.retrieveHistoryTuples(CtxAttributeTypes.LAST_ACTION, new ArrayList<CtxAttributeIdentifier>(), startDate, null).get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}

		printHistory(history);

		return history;
	}

	public Map<CtxHistoryAttribute, List<CtxHistoryAttribute>> getActionHistory(Date startDate, ServiceResourceIdentifier serviceID, String parameterName){
		Map<CtxHistoryAttribute, List<CtxHistoryAttribute>> history = null;
		//use?
		return history;
	}

	private void printHistory(Map<CtxHistoryAttribute, List<CtxHistoryAttribute>> history){
		//printout full dataset test
		for(Iterator<CtxHistoryAttribute> dataset_it = history.keySet().iterator(); dataset_it.hasNext();){
			CtxHistoryAttribute printAction = dataset_it.next();
			try {
				Action next_action = (Action) SerialisationHelper.deserialise(printAction.getBinaryValue(), this.getClass().getClassLoader());
				if (LOG.isDebugEnabled()){
					LOG.debug("------------------------------------------");
				}
				if (LOG.isDebugEnabled()){
					LOG.debug("Action: "+next_action.getparameterName()+" => "+next_action.getvalue());
				}
				List<CtxHistoryAttribute> printContext = history.get(printAction);
				for(CtxHistoryAttribute nextParam : printContext){
					if (LOG.isDebugEnabled()){
						LOG.debug(nextParam.getType()+" = "+nextParam.getStringValue());
					}
				}
				if (LOG.isDebugEnabled()){
					LOG.debug("------------------------------------------");
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
}
