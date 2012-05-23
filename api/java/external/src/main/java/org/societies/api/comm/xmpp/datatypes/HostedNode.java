/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVA��O, SA (PTIN), IBM ISRAEL
 * SCIENCE AND TECHNOLOGY LTD (IBM), INSTITUT TELECOM (ITSUD), AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA
 * PERIORISMENIS EFTHINIS (AMITEC), TELECOM ITALIA S.p.a.(TI),  TRIALOG (TRIALOG), Stiftelsen SINTEF (SINTEF), NEC EUROPE LTD
 * (NEC))
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
package org.societies.api.comm.xmpp.datatypes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * The Class HostedNode.
 */
public class HostedNode extends XMPPNode {
 
	/** The local child nodes. */
	private final Map<String,HostedNode> localChildNodes = new HashMap<String, HostedNode>();
	
	/** The list of all remote child nodes. */
	private final List<XMPPNode> allRemoteChildNodes = new ArrayList<XMPPNode>();
	
	/**
	 * Instantiates a new hosted node.
	 *
	 * @param node the node
	 * @param parentNode the parent node
	 */
	public HostedNode(String node, HostedNode parentNode) {
		super(node, null, parentNode); // TODO put local jid?
	}
	
	/**
	 * Adds the child local node.
	 *
	 * @param newNode the new node
	 */
	public void addChildLocalNode(HostedNode newNode) {
		localChildNodes.put(newNode.getNode(), newNode);
		allRemoteChildNodes.add(newNode);
	}
	
	/**
	 * New child remote node.
	 *
	 * @param jid the jid
	 * @return the xMPP node
	 */
	public XMPPNode newChildRemoteNode(String jid) {
		XMPPNode newNode = new XMPPNode(null, jid, this);
		allRemoteChildNodes.add(newNode);
		return newNode;
	}
	
	/**
	 * Gets the children of the HostedNode.
	 *
	 * @return the children
	 */
	public Collection<XMPPNode> getChildren() {
		return Collections.unmodifiableCollection(allRemoteChildNodes);
	}
	
	/**
	 * Gets the local child of the specified node
	 *
	 * @param node specifes the node which we want to get the child of/
	 * @return the local child
	 */
	public HostedNode getLocalChild(String node) {
		return localChildNodes.get(node);
	}
}
