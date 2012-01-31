package org.societies.api.comm.xmpp.datatypes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HostedNode extends XMPPNode {
 
	private final Map<String,HostedNode> localChildNodes = new HashMap<String, HostedNode>();
	private final List<XMPPNode> allRemoteChildNodes = new ArrayList<XMPPNode>();
	
	public HostedNode(String node, HostedNode parentNode) {
		super(node, null, parentNode); // TODO put local jid?
	}
	
	public void addChildLocalNode(HostedNode newNode) {
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
	
	public HostedNode getLocalChild(String node) {
		return localChildNodes.get(node);
	}
}
