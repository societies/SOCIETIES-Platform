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
package org.societies.personalisation.management.impl;

import org.societies.api.comm.xmpp.datatypes.Identity;
import org.societies.personalisation.CAUI.api.model.IUserIntentAction;
import org.societies.personalisation.CRIST.api.model.ICRISTUserAction;
import org.societies.personalisation.DIANNE.api.model.IDIANNEOutcome;
import org.societies.personalisation.common.api.management.IPersonalisationInternalCallback;
import org.societies.personalisation.preference.api.model.IPreferenceOutcome;

/**
 * Describe your class here...
 *
 * @author Eliza
 *
 */
public class InternalPersonalisationCallback implements IPersonalisationInternalCallback{

	
	private final PersonalisationManager persoMgr;
	private boolean askPreference = false;
	private boolean askDianne = false;
	private boolean askCAUI = false;
	private boolean askCRIST = false;
	
	public InternalPersonalisationCallback(PersonalisationManager persoMgr){
		this.persoMgr = persoMgr;
		
	}
	
	public boolean isAskPreference() {
		return askPreference;
	}


	public void setAskPreference(boolean askPreference) {
		this.askPreference = askPreference;
	}


	public boolean isAskDianne() {
		return askDianne;
	}


	public void setAskDianne(boolean askDianne) {
		this.askDianne = askDianne;
	}


	public boolean isAskCAUI() {
		return askCAUI;
	}


	public void setAskCAUI(boolean askCAUI) {
		this.askCAUI = askCAUI;
	}


	public boolean isAskCRIST() {
		return askCRIST;
	}


	public void setAskCRIST(boolean askCRIST) {
		this.askCRIST = askCRIST;
	}


	/*
	 * (non-Javadoc)
	 * @see org.societies.personalisation.common.api.management.IPersonalisationInternalCallback#sendCAUIOutcome(org.societies.api.mock.Identity, org.societies.personalisation.CAUI.api.model.IUserIntentAction)
	 */
	@Override
	public void sendCAUIOutcome(Identity arg0, IUserIntentAction arg1) {
		// TODO Auto-generated method stub
		
	}

	/*
	 * (non-Javadoc)
	 * @see org.societies.personalisation.common.api.management.IPersonalisationInternalCallback#sendCRISTOutcome(org.societies.api.mock.Identity, org.societies.personalisation.CRIST.api.model.ICRISTUserAction)
	 */
	@Override
	public void sendCRISTOutcome(Identity arg0, ICRISTUserAction arg1) {
		// TODO Auto-generated method stub
		
	}

	/*
	 * (non-Javadoc)
	 * @see org.societies.personalisation.common.api.management.IPersonalisationInternalCallback#sendDIANNEOutcome(org.societies.api.mock.Identity, org.societies.personalisation.DIANNE.api.model.IDIANNEOutcome)
	 */
	@Override
	public void sendDIANNEOutcome(Identity arg0, IDIANNEOutcome arg1) {
		// TODO Auto-generated method stub
		
	}

	/*
	 * (non-Javadoc)
	 * @see org.societies.personalisation.common.api.management.IPersonalisationInternalCallback#sendPrefOutcome(org.societies.api.mock.Identity, org.societies.personalisation.preference.api.model.IPreferenceOutcome)
	 */
	@Override
	public void sendPrefOutcome(Identity arg0, IPreferenceOutcome arg1) {
		// TODO Auto-generated method stub
		
	}

}
