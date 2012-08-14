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

/**
 * @author Thomas Vilarinho (Sintef)
*/


import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.cis.management.ICis;
import org.societies.api.cis.management.ICisManagerCallback;
import org.societies.api.cis.management.ICisOwned;
import org.societies.api.cis.management.IcisManagerClient;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.datatypes.XMPPInfo;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommCallback;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.internal.comm.ICISCommunicationMgrFactory;
import org.societies.api.schema.cis.community.Community;
import org.societies.api.schema.cis.community.Join;
import org.societies.api.schema.cis.community.Leave;

//import org.societies.api.schema.cis.community.Leave;

import org.societies.api.schema.cis.community.Participant;


// THIS CLASS IS DEPRECATED AND SHOULD BE IGNORED
// IT IS BEING KEPT ONLY BECAUSE WE MAY DECIDE TO BRING IT BACK
// IN THE FUTURE

@Deprecated
public class CisManagerClient implements IcisManagerClient {

	IIdentity cssManagerId;
	ICommManager CSSendpoint;
	
	CisManager cisManag;
	private final static List<String> NAMESPACES = Collections
			.unmodifiableList( Arrays.asList("http://societies.org/api/schema/cis/manager",
						  		"http://societies.org/api/schema/cis/community"));
			//.singletonList("http://societies.org/api/schema/cis/manager");
	private final static List<String> PACKAGES = Collections
			//.singletonList("org.societies.api.schema.cis.manager");
			.unmodifiableList( Arrays.asList("org.societies.api.schema.cis.manager",
					"org.societies.api.schema.cis.community"));

	private static Logger LOG = LoggerFactory
			.getLogger(CisManagerClient.class);
	
	
	public CisManagerClient(CisManager cisManag){
		this.cisManag = cisManag;
		this.CSSendpoint = cisManag.iCommMgr;
		
		cssManagerId = CSSendpoint.getIdManager().getThisNetworkNode();
		LOG.info("Jid = " + cssManagerId.getBareJid() + ", domain = " + cssManagerId.getDomain() );
	}
	

	
	@Override
	public void joinRemoteCIS(String cisId, ICisManagerCallback callback) {

	}

	@Override
	public void leaveRemoteCIS(String cisId, ICisManagerCallback callback){
	
	}



	
}
