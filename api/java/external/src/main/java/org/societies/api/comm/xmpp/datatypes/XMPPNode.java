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

// TODO improve distinction between nodes that are returned by this endpoint and nodes that were returned by other endpoints? 
/**
 * The Class XMPPNode.
 */
public class XMPPNode {
	
	/** The Constant ITEM_NAMESPACE. */
	public static final String ITEM_NAMESPACE = "http://jabber.org/protocol/disco#items";
	
	/** The Constant ITEM_QUERY_RESPONSE_OPEN. */
	public static final String ITEM_QUERY_RESPONSE_OPEN = "<query xmlns='"+ITEM_NAMESPACE+"'>";
	
	/** The Constant ITEM_QUERY_RESPONSE_CLOSE. */
	public static final String ITEM_QUERY_RESPONSE_CLOSE = "</query>";
	
	/** The Constant ITEM_QUERY_RESPONSE_OPEN_BYTES. */
	public static final byte[] ITEM_QUERY_RESPONSE_OPEN_BYTES = ITEM_QUERY_RESPONSE_OPEN.getBytes();
	
	/** The Constant ITEM_QUERY_RESPONSE_CLOSE_BYTES. */
	public static final byte[] ITEM_QUERY_RESPONSE_CLOSE_BYTES = ITEM_QUERY_RESPONSE_CLOSE.getBytes();
 
	/** The jid. */
	private String jid;
	
	/** The node. */
	private String node;
	
	/** The name. */
	private String name;
	
	/** The item xml. */
	private String itemXml;
	
	/** The item xml bytes. */
	private byte[] itemXmlBytes;
	
	/** The query xml. */
	private String queryXml;
	
	/** The query xml bytes. */
	private byte[] queryXmlBytes;
	
	/** The parent node. */
	private XMPPNode parentNode;
	// TODO disco#info on nodes?
	// TODO verify that remote node is not created with local jid
	
	/**
	 * New root remote node.
	 *
	 * @param jid the jid
	 * @return the new XMPP node
	 */
	public XMPPNode newRootRemoteNode(String jid) {
		return new XMPPNode(null, jid, null);
	}
	
	/**
	 * New root remote node.
	 *
	 * @param jid the jid
	 * @param node the node
	 * @return the new XMPP node
	 */
	public XMPPNode newRootRemoteNode(String jid, String node) {
		return new XMPPNode(node, jid, null);
	}
	
	/**
	 * Instantiates a new xMPP node.
	 *
	 * @param node the node
	 * @param jid the jid
	 * @param parentNode the parent node
	 */
	protected XMPPNode(String node, String jid, XMPPNode parentNode) {
		this.jid = jid;
		this.node = node;
		this.parentNode = parentNode;
		updateItemXml();
		updateQueryXml();
	}
	
	/**
	 * Update query xml.
	 */
	private void updateQueryXml() {
		queryXml = "<query xmlns='"+ITEM_NAMESPACE+"' node='"+node+"' >";
		queryXmlBytes = queryXml.getBytes();
	}

	/**
	 * Update item xml.
	 */
	private void updateItemXml() {
		String jidPart = "";
		String nodePart = "";
		String namePart = "";
		if (jid!=null && jid.length()>0)
			jidPart = " jid='"+jid+"'";
		if (node!=null && node.length()>0)
			nodePart = " node='"+node+"'";
		if (name!=null && name.length()>0)
			namePart = " name='"+name+"'";
		itemXml = "<item"+jidPart+nodePart+namePart+"/>\n";
		itemXmlBytes = itemXml.getBytes();
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name.
	 *
	 * @param name the new name
	 */
	public void setName(String name) {
		this.name = name;
		updateItemXml();
	}

	/**
	 * Gets the jid.
	 *
	 * @return the jid
	 */
	public String getJid() {
		return jid;
	}

	/**
	 * Gets the node.
	 *
	 * @return the node
	 */
	public String getNode() {
		return node;
	}

	/**
	 * Gets the parent node.
	 *
	 * @return the parent node
	 */
	public XMPPNode getParentNode() {
		return parentNode;
	}
	
	/**
	 * Gets the item xml bytes.
	 *
	 * @return the item xml bytes
	 */
	public byte[] getItemXmlBytes(){
		return itemXmlBytes; // TODO copyOf?
	}
	
	/**
	 * Gets the query xml bytes.
	 *
	 * @return the query xml bytes
	 */
	public byte[] getQueryXmlBytes() {
		return queryXmlBytes; // TODO copyOf?
	}

	/**
	 * Hash code.
	 *
	 * @return the a hash code representation of the XMPP node
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((jid == null) ? 0 : jid.hashCode());
		result = prime * result + ((node == null) ? 0 : node.hashCode());
		result = prime * result
				+ ((parentNode == null) ? 0 : parentNode.hashCode());
		return result;
	}

	/**
	 * Equals.
	 *
	 * @param obj the XMPPNode/Object to be compared
	 * @return true, if successful
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		XMPPNode other = (XMPPNode) obj;
		if (jid == null) {
			if (other.jid != null)
				return false;
		} else if (!jid.equals(other.jid))
			return false;
		if (node == null) {
			if (other.node != null)
				return false;
		} else if (!node.equals(other.node))
			return false;
		if (parentNode == null) {
			if (other.parentNode != null)
				return false;
		} else if (!parentNode.equals(other.parentNode))
			return false;
		return true;
	}
}
