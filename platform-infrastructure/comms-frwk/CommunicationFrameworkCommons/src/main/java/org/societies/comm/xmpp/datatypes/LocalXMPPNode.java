package org.societies.comm.xmpp.datatypes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LocalXMPPNode extends XMPPNode {
 
	private final Map<String,LocalXMPPNode> localChildNodes = new HashMap<String, LocalXMPPNode>();
	private final List<XMPPNode> allRemoteChildNodes = new ArrayList<XMPPNode>();
	
	public LocalXMPPNode(String node, LocalXMPPNode parentNode) {
		super(node, null, parentNode); // TODO put local jid?
	}
	
	public void addChildLocalNode(LocalXMPPNode newNode) {
		localChildNodes.put(newNode.getNode(), newNode);
		allRemoteChildNodes.add(newNode);
	}
	
	public XMPPNode newChildRemoteNode(String jid) {
		XMPPNode newNode = new XMPPNode(null, jid, this);
		allRemoteChildNodes.add(newNode);
		return newNode;
	}
	
	public Collection<XMPPNode> getChildren() {
		return Collections.unmodifiableCollection(allRemoteChildNodes);
	}
	
	public LocalXMPPNode getLocalChild(String node) {
		return localChildNodes.get(node);
	}
}
