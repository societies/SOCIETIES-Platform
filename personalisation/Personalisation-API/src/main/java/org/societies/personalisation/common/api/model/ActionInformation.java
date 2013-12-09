/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
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

package org.societies.personalisation.common.api.model;

import java.util.ArrayList;
import java.util.List;

import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.internal.personalisation.model.IOutcome;
import org.societies.api.personalisation.model.IAction;
import org.societies.personalisation.CAUI.api.model.IUserIntentAction;
import org.societies.personalisation.CRIST.api.model.CRISTUserAction;
import org.societies.personalisation.CRIST.api.model.ICRISTUserAction;
import org.societies.personalisation.DIANNE.api.model.IDIANNEOutcome;
import org.societies.personalisation.preference.api.model.IPreferenceOutcome;

/**
 * @author Eliza
 *
 */
public class ActionInformation {

	private List<IPreferenceOutcome> sentPreferenceOutcomes;
	private List<IDIANNEOutcome> sentDianneOutcomes;
	private List<IUserIntentAction> sentCAUIIntentOutcomes;
	private List<CRISTUserAction> sentCRISTIntentOutcomes;
	private final IAction actionTrigger;
	private final CtxAttribute contextTrigger;
	private final String uuid;

	public ActionInformation(String uuid, IAction actionTrigger){
		this.uuid = uuid;

		this.actionTrigger = actionTrigger;

		this.contextTrigger = null;

	}

	public ActionInformation(String uuid, CtxAttribute contextTrigger){
		this.uuid = uuid;
		this.contextTrigger = contextTrigger;
		this.actionTrigger = null;
	}

	public ActionInformation(String uuid, IAction actionTrigger, List<IPreferenceOutcome> preferenceOutcomes, List<IDIANNEOutcome> dianneOutcomes, List<IUserIntentAction> cauiIntentOutcomes, List<CRISTUserAction> cristIntentOutcomes){
		this.uuid = uuid;
		this.actionTrigger = actionTrigger;
		this.sentPreferenceOutcomes = preferenceOutcomes;
		this.sentDianneOutcomes = dianneOutcomes;
		this.sentCAUIIntentOutcomes = cauiIntentOutcomes;
		this.sentCRISTIntentOutcomes = cristIntentOutcomes;

		this.contextTrigger = null;
	}

	public ActionInformation(String uuid, CtxAttribute contextTrigger, List<IPreferenceOutcome> preferenceOutcomes, List<IDIANNEOutcome> dianneOutcomes, List<IUserIntentAction> cauiIntentOutcomes, List<CRISTUserAction> cristIntentOutcomes){
		this.uuid = uuid;
		this.contextTrigger = contextTrigger;
		this.sentPreferenceOutcomes = preferenceOutcomes;
		this.sentDianneOutcomes = dianneOutcomes;
		this.sentCAUIIntentOutcomes = cauiIntentOutcomes;
		this.sentCRISTIntentOutcomes = cristIntentOutcomes;
		this.actionTrigger = null;
	}


	public IAction getActionTrigger() {
		return actionTrigger;
	}


	public CtxAttribute getContextTrigger() {
		return contextTrigger;
	}

	public String getUuid() {
		return uuid;
	}

	public List<IPreferenceOutcome> getSentPreferenceOutcomes() {
		if (null == this.sentPreferenceOutcomes){
			this.sentPreferenceOutcomes = new ArrayList<IPreferenceOutcome>();
		}
		return sentPreferenceOutcomes;
	}

	public List<IDIANNEOutcome> getSentDianneOutcomes() {
		if (null == this.sentDianneOutcomes){
			this.sentDianneOutcomes = new ArrayList<IDIANNEOutcome>();
		}
		return sentDianneOutcomes;
	}

	public List<IUserIntentAction> getSentCAUIIntentOutcomes() {
		if (null==this.sentCAUIIntentOutcomes){
			this.sentCAUIIntentOutcomes = new ArrayList<IUserIntentAction>();
		}
		return sentCAUIIntentOutcomes;
	}

	public List<CRISTUserAction> getSentCRISTIntentOutcomes() {
		if (null == this.sentCRISTIntentOutcomes){
			this.sentCRISTIntentOutcomes = new ArrayList<CRISTUserAction>();
		}
		return sentCRISTIntentOutcomes;
	}

	public void setSentPreferenceOutcomes(List<IPreferenceOutcome> sentPreferenceOutcomes) {
		this.sentPreferenceOutcomes = sentPreferenceOutcomes;
	}

	public void setSentDianneOutcomes(List<IDIANNEOutcome> sentDianneOutcomes) {
		this.sentDianneOutcomes = sentDianneOutcomes;
	}

	public void setSentCAUIIntentOutcomes(List<IUserIntentAction> sentCAUIIntentOutcomes) {
		this.sentCAUIIntentOutcomes = sentCAUIIntentOutcomes;
	}

	public void setSentCRISTIntentOutcomes(List<CRISTUserAction> sentCRISTIntentOutcomes) {
		this.sentCRISTIntentOutcomes = sentCRISTIntentOutcomes;
	}
}
