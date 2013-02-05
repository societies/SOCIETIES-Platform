package org.societies.api.identity;

/**
 * Returns the identity of this node
 */
public interface INetworkNode extends IIdentity {
	/**
	 * Returns the Node Identifier
	 */
	String getNodeIdentifier();
}
