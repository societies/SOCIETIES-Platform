package org.societies.identity;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityContextMapper;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.INetworkNode;
import org.societies.api.identity.IdentityType;
import org.societies.api.identity.InvalidFormatException;

public class IdentityManagerImpl implements IIdentityManager {
	
	public static final String CIS_PREFIX = "cis-";

	private static Logger LOG = LoggerFactory
			.getLogger(IdentityManagerImpl.class);
	
	private INetworkNode thisNode;
	private INetworkNode cloudNode;
	private INetworkNode domainAuthorityNode;
	private Set<IIdentity> publicIdentities;
	private IIdentityContextMapper ctxMapper;
	// TODO cache known identities
	
	public IdentityManagerImpl(String thisNode) throws InvalidFormatException {
		init(thisNode);
		this.domainAuthorityNode = null;
	}
	
	private void init(String thisNode) throws InvalidFormatException {
		this.thisNode = fromFullJid(thisNode);
		if (this.thisNode.getType().equals(IdentityType.CSS_RICH))
			cloudNode = this.thisNode;
		else
			cloudNode = fromFullJid(this.thisNode.getIdentifier()+"."+this.thisNode.getDomain());
		publicIdentities = new HashSet<IIdentity>();
		publicIdentities.add(this.thisNode); // TODO pseudonyms
		ctxMapper = new IdentityContextMapperImpl();
	}

	public IdentityManagerImpl(String thisNode, String domainAuthortyNode) throws InvalidFormatException {
		init(thisNode);
		this.domainAuthorityNode = fromFullJid(domainAuthortyNode);
	}
	
	
	// TODO good domain check 
	// http://commons.apache.org/validator/apidocs/org/apache/commons/validator/routines/DomainValidator.html
	private static boolean checkDomainNameFormat(String domain) {
		String[] domainParts = domain.split("\\.");
		if (domainParts.length>1)
			return true;
		return false;
	}
	
	public static IIdentity staticfromJid(String jid) throws InvalidFormatException {
		String[] parts = jid.split("@|/"); // TODO regexp
		switch (parts.length) {
			case 1:
//				String[] richParts = jid.split("\\."); // TODO regexp
//				LOG.info("JID="+jid+";richParts.length="+richParts.length);
				int firstDotIndex = jid.indexOf(".");
				if (firstDotIndex>0) {
					String domain = jid.substring(firstDotIndex+1);
//				if (checkDomainNameFormat(domain)) {
					String identifier = jid.substring(0,firstDotIndex);
					if (identifier.startsWith(CIS_PREFIX))
						return new NetworkNodeImpl(IdentityType.CIS, identifier, domain, "cis");
					else
						return new NetworkNodeImpl(IdentityType.CSS_RICH, identifier, domain, "rich");
				}
				break;
			case 2:
				return new IdentityImpl(IdentityType.CSS, parts[0], parts[1]);
			case 3:
				return new NetworkNodeImpl(IdentityType.CSS_LIGHT, parts[0], parts[1], parts[2]);
		}
		throw new InvalidFormatException("Unable to parse JID into IIdentity: "+jid);
	}

	public IIdentity fromJid(String jid) throws InvalidFormatException {
		return staticfromJid(jid);
	}

	public INetworkNode fromFullJid(String jid) throws InvalidFormatException {
		IIdentity id = fromJid(jid);
		if (id instanceof INetworkNode)
			return (INetworkNode)id;
		throw new InvalidFormatException("Unable to parse JID into INetworkNode: "+jid);
	}

	public INetworkNode getThisNetworkNode() {
		return thisNode; // TODO clone?
	}
	
	public INetworkNode getCloudNode() {
		return cloudNode; // TODO clone?
	}

	public INetworkNode getDomainAuthorityNode() {
		if (this.domainAuthorityNode == null)
			return thisNode;
		return this.domainAuthorityNode;
	}
	
	public Set<IIdentity> getPublicIdentities() {
		return publicIdentities; // TODO clone?
	}

	public boolean isMine(IIdentity identity) {
		return (((IdentityImpl)thisNode).equals(identity));
	}

	public IIdentityContextMapper getIdentityContextMapper() {
		return ctxMapper;
	}

	public IIdentity newMemorableIdentity(String memorableIdentifier) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean releaseMemorableIdentity(IIdentity memorableIdentity) {
		// TODO Auto-generated method stub
		return false;
	}

	public IIdentity newTransientIdentity() {
		// TODO Auto-generated method stub
		return null;
	}

	
}
