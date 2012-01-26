package org.societies.comm.xmpp.datatypes;

// TODO improve distinction between nodes that are returned by this endpoint and nodes that were returned by other endpoints? 
public class XMPPNode {
	
	public static final String ITEM_NAMESPACE = "http://jabber.org/protocol/disco#items";
	public static final String ITEM_QUERY_RESPONSE_OPEN = "<query xmlns='"+ITEM_NAMESPACE+"'>";
	public static final String ITEM_QUERY_RESPONSE_CLOSE = "</query>";
	public static final byte[] ITEM_QUERY_RESPONSE_OPEN_BYTES = ITEM_QUERY_RESPONSE_OPEN.getBytes();
	public static final byte[] ITEM_QUERY_RESPONSE_CLOSE_BYTES = ITEM_QUERY_RESPONSE_CLOSE.getBytes();
 
	private String jid;
	private String node;
	private String name;
	private String itemXml;
	private byte[] itemXmlBytes;
	private String queryXml;
	private byte[] queryXmlBytes;
	private XMPPNode parentNode;
	// TODO disco#info on nodes?
	// TODO verify that remote node is not created with local jid
	
	public XMPPNode newRootRemoteNode(String jid) {
		return new XMPPNode(null, jid, null);
	}
	
	public XMPPNode newRootRemoteNode(String jid, String node) {
		return new XMPPNode(node, jid, null);
	}
	
	protected XMPPNode(String node, String jid, XMPPNode parentNode) {
		this.jid = jid;
		this.node = node;
		this.parentNode = parentNode;
		updateItemXml();
		updateQueryXml();
	}
	
	private void updateQueryXml() {
		queryXml = "<query xmlns='"+ITEM_NAMESPACE+"' node='"+node+"' >";
		queryXmlBytes = queryXml.getBytes();
	}

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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
		updateItemXml();
	}

	public String getJid() {
		return jid;
	}

	public String getNode() {
		return node;
	}

	public XMPPNode getParentNode() {
		return parentNode;
	}
	
	public byte[] getItemXmlBytes(){
		return itemXmlBytes; // TODO copyOf?
	}
	
	public byte[] getQueryXmlBytes() {
		return queryXmlBytes; // TODO copyOf?
	}

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
