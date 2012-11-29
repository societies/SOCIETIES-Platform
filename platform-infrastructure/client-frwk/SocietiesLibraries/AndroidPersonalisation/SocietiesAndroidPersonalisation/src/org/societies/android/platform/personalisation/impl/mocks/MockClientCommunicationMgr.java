package org.societies.android.platform.personalisation.impl.mocks;

import java.util.HashSet;
import java.util.Set;

import org.jivesoftware.smack.packet.IQ.Type;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.interfaces.ICommCallback;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityContextMapper;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.INetworkNode;
import org.societies.api.identity.IdentityType;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.personalisation.model.Action;
import org.societies.api.schema.personalisation.mgmt.PersonalisationManagerBean;
import org.societies.api.schema.personalisation.model.ActionBean;
import org.societies.comm.xmpp.client.impl.ClientCommunicationMgr;

import android.content.Context;
import android.util.Log;

/**
 * Mock version of {@link MockClientCommunicationMgr} class for use in testing
 */
public class MockClientCommunicationMgr extends ClientCommunicationMgr {

	private static final String TAG = MockClientCommunicationMgr.class.getName();
	private String jid;
	private INetworkNode identity;
	private String identifier;
	private String domain;
	
	public MockClientCommunicationMgr(Context androidContext, final String identifier, final String domain) {
		
		super(androidContext);
		
		this.jid = identifier+"@"+domain;
		this.identifier = identifier;
		this.domain = domain;
		this.identity = new INetworkNode() {

			public IdentityType getType() {
				// TODO Auto-generated method stub
				return IdentityType.CSS_LIGHT;
			}

			public String getJid() {
				// TODO Auto-generated method stub
				return jid;
			}

			public String getIdentifier() {
				// TODO Auto-generated method stub
				return identifier;
			}

			public String getDomain() {
				// TODO Auto-generated method stub
				return domain;
			}

			public String getBareJid() {
				// TODO Auto-generated method stub
				return jid;
			}

			public String getNodeIdentifier() {
				// TODO Auto-generated method stub
				return jid;
			}
		};
	}

	public IIdentityManager getIdManager() {
		return new IIdentityManager() {

			public boolean releaseMemorableIdentity(IIdentity arg0) {
				// TODO Auto-generated method stub
				return false;
			}

			public IIdentity newTransientIdentity() {
				// TODO Auto-generated method stub
				return identity;
			}

			public IIdentity newMemorableIdentity(String strId) {
				// TODO Auto-generated method stub
				return identity;
			}

			public boolean isMine(IIdentity id) {
				// TODO Auto-generated method stub
				return jid.equalsIgnoreCase(id.getBareJid());
			}

			public INetworkNode getThisNetworkNode() {
				// TODO Auto-generated method stub
				return identity;
			}

			public Set<IIdentity> getPublicIdentities() {
				Set<IIdentity> ids = new HashSet<IIdentity>();
				ids.add(identity);
				return ids;
			}

			public IIdentityContextMapper getIdentityContextMapper() {
				// TODO Auto-generated method stub
				return null;
			}

			public INetworkNode getDomainAuthorityNode() {
				return new INetworkNode() {

					public IdentityType getType() {
						// TODO Auto-generated method stub
						return IdentityType.CSS;
					}

					public String getJid() {
						return "da.societies.test";
					}

					public String getIdentifier() {
						// TODO Auto-generated method stub
						return "da.societies.test";
					}

					public String getDomain() {
						// TODO Auto-generated method stub
						return domain;
					}

					public String getBareJid() {
						// TODO Auto-generated method stub
						return "da.societies.test";
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
						return IdentityType.CSS;
					}

					public String getJid() {
						return identifier+"."+domain;
					}

					public String getIdentifier() {
						// TODO Auto-generated method stub
						return identifier;
					}

					public String getDomain() {
						// TODO Auto-generated method stub
						return domain;
					}

					public String getBareJid() {
						// TODO Auto-generated method stub
						return identifier+"."+domain;
					}

					public String getNodeIdentifier() {
						// TODO Auto-generated method stub
						return null;
					}
				};
			}

			public IIdentity fromJid(final String strId) throws InvalidFormatException {
				// TODO Auto-generated method stub
				return new INetworkNode() {

					public IdentityType getType() {
						// TODO Auto-generated method stub
						return IdentityType.CSS;
					}

					public String getJid() {
						// TODO Auto-generated method stub
						return strId;
					}

					public String getIdentifier() {
						// TODO Auto-generated method stub
						return strId;
					}

					public String getDomain() {
						// TODO Auto-generated method stub
						String[] str = strId.split(".");
						return str[str.length-1]==null ? "" : str[str.length-1];
					}

					public String getBareJid() {
						// TODO Auto-generated method stub
						return strId;
					}

					public String getNodeIdentifier() {
						// TODO Auto-generated method stub
						String[] str = strId.split("/");
						return str[str.length-1]==null ? "" : str[str.length-1];
					}
				};
			}

			public INetworkNode fromFullJid(final String strId) throws InvalidFormatException {
				// TODO Auto-generated method stub
				return new INetworkNode() {

					public IdentityType getType() {
						// TODO Auto-generated method stub
						return IdentityType.CSS;
					}

					public String getJid() {
						// TODO Auto-generated method stub
						return strId;
					}

					public String getIdentifier() {
						// TODO Auto-generated method stub
						return strId;
					}

					public String getDomain() {
						// TODO Auto-generated method stub
						String[] str = strId.split(".");
						return str[str.length-1]==null ? "" : str[str.length-1];
					}

					public String getBareJid() {
						// TODO Auto-generated method stub
						return strId;
					}

					public String getNodeIdentifier() {
						// TODO Auto-generated method stub
						String[] str = strId.split("/");
						return str[str.length-1]==null ? "" : str[str.length-1];
					}
				};
			}
		};
	}
	
	@Override
	public void sendIQ(Stanza stanza, Type type, Object bean, ICommCallback callback){
		if (bean instanceof PersonalisationManagerBean){
		ActionBean actionBean = new ActionBean();
		actionBean.setParameterName(((PersonalisationManagerBean) bean).getParameterName());
		actionBean.setServiceID(((PersonalisationManagerBean) bean).getServiceId());
		actionBean.setServiceType(((PersonalisationManagerBean) bean).getServiceType());
		actionBean.setValue("mockValue");
		callback.receiveResult(stanza, actionBean);
		Log.d(TAG, "Sent actionBean mocking a return from the cloud node");
		}else{
			callback.receiveResult(stanza, null);
			Log.d(TAG, "Sent null bean mocking a return from the cloud node as the bean received in sendIQ was not of type: "+PersonalisationManagerBean.class.getName());
		}
	}
}
