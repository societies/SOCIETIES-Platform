package org.societies.android.platform.useragent.uam.mocks;

import java.util.Set;

import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityContextMapper;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.INetworkNode;
import org.societies.api.identity.IdentityType;
import org.societies.api.identity.InvalidFormatException;
import org.societies.comm.xmpp.client.impl.ClientCommunicationMgr;

import android.content.Context;

/**
 * Mock version of {@link MockClientCommunicationMgr} class for use in testing
 */
public class MockClientCommunicationMgr extends ClientCommunicationMgr {

	public MockClientCommunicationMgr(Context androidContext) {
		super(androidContext);
	}

	public IIdentityManager getIdManager() {
		return new IIdentityManager() {
			
			public boolean releaseMemorableIdentity(IIdentity arg0) {
				// TODO Auto-generated method stub
				return false;
			}
			
			public IIdentity newTransientIdentity() {
				// TODO Auto-generated method stub
				return null;
			}
			
			public IIdentity newMemorableIdentity(String arg0) {
				// TODO Auto-generated method stub
				return null;
			}
			
			public boolean isMine(IIdentity arg0) {
				// TODO Auto-generated method stub
				return false;
			}
			
			public INetworkNode getThisNetworkNode() {
				// TODO Auto-generated method stub
				return null;
			}
			
			public Set<IIdentity> getPublicIdentities() {
				// TODO Auto-generated method stub
				return null;
			}
			
			public IIdentityContextMapper getIdentityContextMapper() {
				// TODO Auto-generated method stub
				return null;
			}
			
			public INetworkNode getDomainAuthorityNode() {
				return new INetworkNode() {
					
					public IdentityType getType() {
						// TODO Auto-generated method stub
						return null;
					}
					
					public String getJid() {
						return "daNode.societies.test";
					}
					
					public String getIdentifier() {
						// TODO Auto-generated method stub
						return null;
					}
					
					public String getDomain() {
						// TODO Auto-generated method stub
						return null;
					}
					
					public String getBareJid() {
						// TODO Auto-generated method stub
						return null;
					}
					
					public String getNodeIdentifier() {
						// TODO Auto-generated method stub
						return null;
					}
				};
			}
			
			public INetworkNode getCloudNode() {
				return new INetworkNode() {
					
					public IdentityType getType() {
						// TODO Auto-generated method stub
						return null;
					}
					
					public String getJid() {
						return "sample.societies.test";
					}
					
					public String getIdentifier() {
						// TODO Auto-generated method stub
						return null;
					}
					
					public String getDomain() {
						// TODO Auto-generated method stub
						return null;
					}
					
					public String getBareJid() {
						// TODO Auto-generated method stub
						return null;
					}
					
					public String getNodeIdentifier() {
						// TODO Auto-generated method stub
						return null;
					}
				};
			}
			
			public IIdentity fromJid(String arg0) throws InvalidFormatException {
				// TODO Auto-generated method stub
				return null;
			}
			
			public INetworkNode fromFullJid(String arg0) throws InvalidFormatException {
				// TODO Auto-generated method stub
				return null;
			}
		};
	}
}
