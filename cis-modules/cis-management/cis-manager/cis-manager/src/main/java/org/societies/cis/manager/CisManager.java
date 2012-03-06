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


package org.societies.cis.manager;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IdentityType;
import org.societies.api.internal.cis.management.CisActivityFeed;
import org.societies.api.internal.cis.management.CisRecord;
import org.societies.api.internal.cis.management.ICisManager;
import org.societies.api.internal.comm.ICISCommunicationMgrFactory;
import org.societies.cis.manager.CisEditor;
import org.springframework.beans.factory.annotation.Autowired;

import org.societies.identity.IdentityImpl;

// this is the class which manages all the CIS from a CSS
// for the class responsible for editing and managing each CIS instance, consult the CISEditor

/**
 * @author Thomas Vilarinho (Sintef)
*/

public class CisManager implements ICisManager {

	public Set<CisEditor> CISs; 
	private ICISCommunicationMgrFactory ccmFactory;
	private IIdentity cisManagerId;
	private ICommManager CSSendpoint;
	private ICommManager CISMgmtendpoint;

	private static Logger LOG = LoggerFactory
			.getLogger(CisManager.class);

	@Autowired
	public CisManager(ICISCommunicationMgrFactory ccmFactory,ICommManager CSSendpoint) {
		this.ccmFactory = ccmFactory;
		this.CSSendpoint = CSSendpoint;
		this.ccmFactory = ccmFactory;
		String host= "thomas.local";
		String subDomain= "CISCommManager";
		String secretKey= "password.thomas.local";
		
		LOG.info("factory bundled");
		
		cisManagerId = new IdentityImpl(IdentityType.CIS, subDomain, host); 
		
		CISMgmtendpoint = ccmFactory.getNewCommManager(cisManagerId, secretKey);
		
		LOG.info("endpoint created");
		
		
		
		
		
		CISs = new HashSet<CisEditor>();
		
		CisEditor cEditor1 = new CisEditor(CSSendpoint.getIdManager().getThisNetworkNode().getJid(),
				"cis1","thomas.local","","","cis1.password.thomas.local",ccmFactory);
		
		CISs.add(cEditor1);
		
	}



	/**
	 * @deprecated  Replaced by constructor which inherits the ComManager Factory and the IcommManager of the CSS
	 */
	
	@Deprecated
	public CisManager() {
		CISs = new HashSet<CisEditor>();
	}
	
	// TODO: review this constructor in the future
	@Override
	public CisRecord createCis(String cssId, String cisId) {
		// check if ccs already exist
		CisEditor cis = new CisEditor(cssId, cisId);
		if (CISs.add(cis))
			return cis.getCisRecord();
		else
			return null;
	}

	@Override
	public Boolean deleteCis(String cssId, String cisId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean updateCis(String cssId, CisRecord newCis, String oldCisId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CisRecord getCis(String cssId, String cisId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CisRecord[] getCisList(CisRecord query) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CisActivityFeed getActivityFeed(String cssId, String cisId) {
		// TODO Auto-generated method stub
		return null;
	}



}
