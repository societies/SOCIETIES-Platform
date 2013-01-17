package org.societies.android.platform.pubsub;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.societies.api.comm.xmpp.pubsub.Affiliation;
import org.societies.api.comm.xmpp.pubsub.Subscription;
import org.societies.api.comm.xmpp.pubsub.SubscriptionState;

import org.jabber.protocol.pubsub.Create;
import org.jabber.protocol.pubsub.Item;
import org.jabber.protocol.pubsub.Items;
import org.jabber.protocol.pubsub.Publish;
import org.jabber.protocol.pubsub.Pubsub;
import org.jabber.protocol.pubsub.Retract;
import org.jabber.protocol.pubsub.Subscribe;
import org.jabber.protocol.pubsub.Unsubscribe;
import org.jabber.protocol.pubsub.owner.Affiliations;
import org.jabber.protocol.pubsub.owner.Delete;
import org.jabber.protocol.pubsub.owner.Purge;
import org.jabber.protocol.pubsub.owner.Subscriptions;
import org.jivesoftware.smack.packet.IQ;

import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.datatypes.StanzaError;
import org.societies.api.comm.xmpp.datatypes.XMPPInfo;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommCallback;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.InvalidFormatException;
import org.societies.android.platform.comms.helper.ClientCommunicationMgr;
import org.societies.android.platform.comms.helper.MarshallUtils;
import org.societies.android.api.comms.XMPPAgent;
import org.societies.android.api.pubsub.IPubsubService;
import org.societies.android.api.pubsub.ISubscriber;
import org.societies.android.api.pubsub.SubscriptionParcelable;
import org.societies.utilities.DBC.Dbc;
import org.xml.sax.SAXException;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class PubsubServiceBase implements IPubsubService {
	private static final String LOG_TAG = PubsubServiceBase.class.getName();
	
	public static final int TIMEOUT = 10000;
	
	private final static List<String> NAMESPACES = Collections
			.unmodifiableList(Arrays.asList("http://jabber.org/protocol/pubsub",
						   					"http://jabber.org/protocol/pubsub#errors",
						   					"http://jabber.org/protocol/pubsub#event",
						   					"http://jabber.org/protocol/pubsub#owner",
						   					"http://jabber.org/protocol/disco#items"));
	private static final List<String> PACKAGES = Collections
			.unmodifiableList(Arrays.asList("org.jabber.protocol.pubsub",
					"org.jabber.protocol.pubsub.errors",
					"org.jabber.protocol.pubsub.event",
					"org.jabber.protocol.pubsub.owner"));
	
	private static final List<String> ELEMENTS = Collections.unmodifiableList(
			Arrays.asList("pubsub", 
					      "event", 
					      "query"));
	
	private ClientCommunicationMgr ccm;
	private Context androidContext;
	private boolean restrictBroadcast;
	private Map<Subscription,List<ISubscriber>> subscribers;	

	
	public PubsubServiceBase (Context androidContext, ClientCommunicationMgr ccm, boolean restrictBroadcast) {
		this.ccm = ccm;
		this.androidContext = androidContext;
		this.restrictBroadcast = restrictBroadcast;
	}



	public String[] discoItems(final String client, String pubsubService, String node, final long remoteCallID) {
		Dbc.require("Pubsub domain authority must be specified", null != pubsubService && pubsubService.length() > 0);
		Log.d(LOG_TAG, "discoItems called with domain authority: " + pubsubService + " and node: " + node);
		
		try {
			this.ccm.getItems(convertStringToIdentity(pubsubService), node, new ICommCallback() {
				
				public void receiveResult(Stanza arg0, Object arg1) {
				}
				
				public void receiveMessage(Stanza arg0, Object result) {
				}
				
				public void receiveItems(Stanza stanza, String mapKey, List<String> mapValue) {
					//Send intent
					Intent intent = new Intent();
					if (PubsubServiceBase.this.restrictBroadcast) {
						intent.setPackage(client);
					}
					intent.putExtra(INTENT_RETURN_CALL_ID_KEY, remoteCallID);
					intent.setAction(IPubsubService.DISCO_ITEMS);
					intent.putExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY, "");
					PubsubServiceBase.this.androidContext.sendBroadcast(intent);
				}
				
				public void receiveInfo(Stanza arg0, String arg1, XMPPInfo arg2) {
				}
				
				public void receiveError(Stanza arg0, XMPPError arg1) {
				}
				
				public List<String> getXMLNamespaces() {
					return null;
				}
				
				public List<String> getJavaPackages() {
					return null;
				}
			});
		} catch (CommunicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XMPPError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}



	public boolean ownerCreate(final String client, String pubsubService, String node, final long remoteCallID) {
		Stanza stanza = null;
		try {
			stanza = new Stanza(convertStringToIdentity(pubsubService));
		} catch (XMPPError e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Pubsub payload = new Pubsub();
		Create c = new Create();
		c.setNode(node);
		payload.setCreate(c);
				
		try {
			this.ccm.sendIQ(stanza, IQ.Type.SET, payload, new ICommCallback() {
				
				public void receiveResult(Stanza arg0, Object arg1) {
					//Send intent
					Intent intent = new Intent();
					if (PubsubServiceBase.this.restrictBroadcast) {
						intent.setPackage(client);
					}
					intent.putExtra(INTENT_RETURN_CALL_ID_KEY, remoteCallID);
					intent.setAction(IPubsubService.OWNER_CREATE);
					intent.putExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY, "");
					PubsubServiceBase.this.androidContext.sendBroadcast(intent);
				}
				
				public void receiveMessage(Stanza arg0, Object arg1) {
					// TODO Auto-generated method stub
					
				}
				
				public void receiveItems(Stanza arg0, String arg1, List<String> arg2) {
					// TODO Auto-generated method stub
					
				}
				
				public void receiveInfo(Stanza arg0, String arg1, XMPPInfo arg2) {
					// TODO Auto-generated method stub
					
				}
				
				public void receiveError(Stanza arg0, XMPPError arg1) {
					// TODO Auto-generated method stub
					
				}
				
				public List<String> getXMLNamespaces() {
					// TODO Auto-generated method stub
					return null;
				}
				
				public List<String> getJavaPackages() {
					// TODO Auto-generated method stub
					return null;
				}
			});
		} catch (CommunicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

//		return waitForResponse(stanza.getId());
		return false;
	}



	public boolean ownerDelete(final String client, String pubsubService, String node, final long remoteCallID) {
		Stanza stanza = null;
		try {
			stanza = new Stanza(convertStringToIdentity(pubsubService));
		} catch (XMPPError e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		org.jabber.protocol.pubsub.owner.Pubsub payload = new org.jabber.protocol.pubsub.owner.Pubsub();
		Delete delete = new Delete();
		delete.setNode(node);
		payload.setDelete(delete);
		
		try {
			this.ccm.sendIQ(stanza, IQ.Type.SET, payload, new ICommCallback() {
				
				public void receiveResult(Stanza stanza, Object object) {
					//Send intent
					Intent intent = new Intent();
					if (PubsubServiceBase.this.restrictBroadcast) {
						intent.setPackage(client);
					}
					intent.putExtra(INTENT_RETURN_CALL_ID_KEY, remoteCallID);
					intent.setAction(IPubsubService.OWNER_DELETE);
					intent.putExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY, "");
					PubsubServiceBase.this.androidContext.sendBroadcast(intent);
				}
				
				public void receiveMessage(Stanza arg0, Object arg1) {
					// TODO Auto-generated method stub
					
				}
				
				public void receiveItems(Stanza arg0, String arg1, List<String> arg2) {
					// TODO Auto-generated method stub
					
				}
				
				public void receiveInfo(Stanza arg0, String arg1, XMPPInfo arg2) {
					// TODO Auto-generated method stub
					
				}
				
				public void receiveError(Stanza arg0, XMPPError arg1) {
					// TODO Auto-generated method stub
					
				}
				
				public List<String> getXMLNamespaces() {
					// TODO Auto-generated method stub
					return null;
				}
				
				public List<String> getJavaPackages() {
					// TODO Auto-generated method stub
					return null;
				}
			});
		} catch (CommunicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}



	public boolean ownerPurgeItems(final String client, String pubsubServiceJid, String node, final long remoteCallID) {
		IIdentity pubsubService = null;
		try {
			pubsubService = convertStringToIdentity(pubsubServiceJid);
		} catch (XMPPError e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Stanza stanza = new Stanza(pubsubService);
		org.jabber.protocol.pubsub.owner.Pubsub payload = new org.jabber.protocol.pubsub.owner.Pubsub();
		Purge purge = new Purge();
		purge.setNode(node);
		payload.setPurge(purge);
		
		try {
			this.ccm.sendIQ(stanza, IQ.Type.SET, payload, new ICommCallback() {
				
				public void receiveResult(Stanza stanza, Object object) {
					//Send intent
					Intent intent = new Intent();
					if (PubsubServiceBase.this.restrictBroadcast) {
						intent.setPackage(client);
					}
					intent.putExtra(INTENT_RETURN_CALL_ID_KEY, remoteCallID);
					intent.setAction(IPubsubService.OWNER_PURGE_ITEMS);
					intent.putExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY, "");
					PubsubServiceBase.this.androidContext.sendBroadcast(intent);
				}
				
				public void receiveMessage(Stanza arg0, Object arg1) {
					// TODO Auto-generated method stub
					
				}
				
				public void receiveItems(Stanza arg0, String arg1, List<String> arg2) {
					// TODO Auto-generated method stub
					
				}
				
				public void receiveInfo(Stanza arg0, String arg1, XMPPInfo arg2) {
					// TODO Auto-generated method stub
					
				}
				
				public void receiveError(Stanza arg0, XMPPError arg1) {
					// TODO Auto-generated method stub
					
				}
				
				public List<String> getXMLNamespaces() {
					// TODO Auto-generated method stub
					return null;
				}
				
				public List<String> getJavaPackages() {
					// TODO Auto-generated method stub
					return null;
				}
			});
		} catch (CommunicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}



	public String publisherPublish(final String client, String pubsubService, String node, String itemId, String item, final long remoteCallID) {
		Stanza stanza = null;
		try {
			stanza = new Stanza(convertStringToIdentity(pubsubService));
		} catch (XMPPError e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Pubsub payload = new Pubsub();
		Publish publish = new Publish();
		publish.setNode(node);
		Item i = new Item();
		if (itemId!=null)
			i.setId(itemId);

		try {
			i.setAny(MarshallUtils.stringToElement(item));
		} catch (SAXException e) {
			Log.e(LOG_TAG, "SAXException when parsing string to XML Element", e);
		} catch (IOException e) {
			Log.e(LOG_TAG, "IOException when parsing string to XML Element", e);
		} catch (ParserConfigurationException e) {
			Log.e(LOG_TAG, "ParserConfigurationException when parsing string to XML Element", e);
		}
		
		publish.setItem(i);
		payload.setPublish(publish);
		
		try {
			this.ccm.sendIQ(stanza, IQ.Type.SET, payload, new ICommCallback() {
				
				public void receiveResult(Stanza stanza, Object object) {
					//Send intent
					Intent intent = new Intent();
					if (PubsubServiceBase.this.restrictBroadcast) {
						intent.setPackage(client);
					}
					intent.putExtra(INTENT_RETURN_CALL_ID_KEY, remoteCallID);
					intent.setAction(IPubsubService.PUBLISHER_PUBLISH);
					intent.putExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY, "");
					PubsubServiceBase.this.androidContext.sendBroadcast(intent);
				}
				
				public void receiveMessage(Stanza arg0, Object arg1) {
					// TODO Auto-generated method stub
					
				}
				
				public void receiveItems(Stanza arg0, String arg1, List<String> arg2) {
					// TODO Auto-generated method stub
					
				}
				
				public void receiveInfo(Stanza arg0, String arg1, XMPPInfo arg2) {
					// TODO Auto-generated method stub
					
				}
				
				public void receiveError(Stanza arg0, XMPPError arg1) {
					// TODO Auto-generated method stub
					
				}
				
				public List<String> getXMLNamespaces() {
					// TODO Auto-generated method stub
					return null;
				}
				
				public List<String> getJavaPackages() {
					// TODO Auto-generated method stub
					return null;
				}
			});
		} catch (CommunicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}



	public boolean publisherDelete(final String client, String pubsubServiceJid, String node, String itemId, final long remoteCallID) {
		IIdentity pubsubService = null;
		try {
			pubsubService = convertStringToIdentity(pubsubServiceJid);
		} catch (XMPPError e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Stanza stanza = new Stanza(pubsubService);
		Pubsub payload = new Pubsub();
		
		Retract retract = new Retract();
		retract.setNode(node);
		Item i = new Item();
		i.setId(itemId);
		retract.getItem().add(i);
		payload.setRetract(retract);
		
		try {
			this.ccm.sendIQ(stanza, IQ.Type.SET, payload, new ICommCallback() {
				
				public void receiveResult(Stanza stanza, Object object) {
					//Send intent
					Intent intent = new Intent();
					if (PubsubServiceBase.this.restrictBroadcast) {
						intent.setPackage(client);
					}
					intent.putExtra(INTENT_RETURN_CALL_ID_KEY, remoteCallID);
					intent.setAction(IPubsubService.PUBLISHER_DELETE);
					intent.putExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY, "");
					PubsubServiceBase.this.androidContext.sendBroadcast(intent);
				}
				
				public void receiveMessage(Stanza arg0, Object arg1) {
					// TODO Auto-generated method stub
					
				}
				
				public void receiveItems(Stanza arg0, String arg1, List<String> arg2) {
					// TODO Auto-generated method stub
					
				}
				
				public void receiveInfo(Stanza arg0, String arg1, XMPPInfo arg2) {
					// TODO Auto-generated method stub
					
				}
				
				public void receiveError(Stanza arg0, XMPPError arg1) {
					// TODO Auto-generated method stub
					
				}
				
				public List<String> getXMLNamespaces() {
					// TODO Auto-generated method stub
					return null;
				}
				
				public List<String> getJavaPackages() {
					// TODO Auto-generated method stub
					return null;
				}
			});
		} catch (CommunicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}



	public SubscriptionParcelable subscriberSubscribe(final String client, String pubsubService, final String node, final ISubscriber subscriber, final long remoteCallID) {
		try {
			final IIdentity pubsubServiceIdentity;
			pubsubServiceIdentity = convertStringToIdentity(pubsubService);
			
			final Subscription subscription = new Subscription(pubsubServiceIdentity, localIdentity(), node, null);
			final List<ISubscriber> subscriberList = subscribers.get(subscription);
			
			if (subscriberList==null) {
				final List<ISubscriber> newSubscriberList = new ArrayList<ISubscriber>();
				
				Stanza stanza = new Stanza(pubsubServiceIdentity);
				Pubsub payload = new Pubsub();
				Subscribe sub = new Subscribe();
				sub.setJid(localIdentity().getBareJid());
				sub.setNode(node);
				payload.setSubscribe(sub);
		
				try {
					this.ccm.sendIQ(stanza, IQ.Type.SET, payload, new ICommCallback() {
						
						public void receiveResult(Stanza arg0, Object response) {
							String subId = ((Pubsub)response).getSubscription().getSubid();
							Subscription newSubscription = new Subscription(pubsubServiceIdentity, localIdentity(), node, subId);
							subscribers.put(newSubscription, subscriberList);
							//Send intent
							Intent intent = new Intent();
							if (PubsubServiceBase.this.restrictBroadcast) {
								intent.setPackage(client);
							}
							intent.putExtra(INTENT_RETURN_CALL_ID_KEY, remoteCallID);
							intent.setAction(IPubsubService.SUBSCRIBER_SUBSCRIBE);
							intent.putExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY, "");
							PubsubServiceBase.this.androidContext.sendBroadcast(intent);
							
//							newSubscriberList.add(subscriber);
//							try {
//								return new SubscriptionParcelable(subscription, this.ccm.getIdManager());
//							} catch (InvalidFormatException e) {
//								// TODO Auto-generated catch block
//								e.printStackTrace();
//							}

						}
						
						public void receiveMessage(Stanza arg0, Object arg1) {
							// TODO Auto-generated method stub
							
						}
						
						public void receiveItems(Stanza arg0, String arg1, List<String> arg2) {
							// TODO Auto-generated method stub
							
						}
						
						public void receiveInfo(Stanza arg0, String arg1, XMPPInfo arg2) {
							// TODO Auto-generated method stub
							
						}
						
						public void receiveError(Stanza arg0, XMPPError arg1) {
							// TODO Auto-generated method stub
							
						}
						
						public List<String> getXMLNamespaces() {
							// TODO Auto-generated method stub
							return null;
						}
						
						public List<String> getJavaPackages() {
							// TODO Auto-generated method stub
							return null;
						}
					});
				} catch (CommunicationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (XMPPError e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return null;
	}



	public boolean subscriberUnsubscribe(final String client, String pubsubService, String node, ISubscriber subscriber, final long remoteCallID) {
		IIdentity pubsubServiceIdentity = null;
		try {
			pubsubServiceIdentity = convertStringToIdentity(pubsubService);
		} catch (XMPPError e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Subscription subscription = new Subscription(pubsubServiceIdentity, localIdentity(), node, null);
		List<ISubscriber> subscriberList = subscribers.get(subscription);
//		subscriberList.remove(subscriber);  // TODO Unsubscribes all subcribers. Change 
		subscriberList.clear();             //  to allow single unsubscription.
		
		if (subscriberList.size()==0) {
			Stanza stanza = new Stanza(pubsubServiceIdentity);
			Pubsub payload = new Pubsub();
			Unsubscribe unsub = new Unsubscribe();
			unsub.setJid(localIdentity().getJid());
			unsub.setNode(node);
			payload.setUnsubscribe(unsub);
	
			try {
				this.ccm.sendIQ(stanza, IQ.Type.SET, payload, new ICommCallback() {
					
					public void receiveResult(Stanza stanza, Object object) {
						//Send intent
						Intent intent = new Intent();
						if (PubsubServiceBase.this.restrictBroadcast) {
							intent.setPackage(client);
						}
						intent.putExtra(INTENT_RETURN_CALL_ID_KEY, remoteCallID);
						intent.setAction(IPubsubService.SUBSCRIBER_UNSUBSCRIBE);
						intent.putExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY, "");
						PubsubServiceBase.this.androidContext.sendBroadcast(intent);

					}
					
					public void receiveMessage(Stanza arg0, Object arg1) {
						// TODO Auto-generated method stub
						
					}
					
					public void receiveItems(Stanza arg0, String arg1, List<String> arg2) {
						// TODO Auto-generated method stub
						
					}
					
					public void receiveInfo(Stanza arg0, String arg1, XMPPInfo arg2) {
						// TODO Auto-generated method stub
						
					}
					
					public void receiveError(Stanza arg0, XMPPError arg1) {
						// TODO Auto-generated method stub
						
					}
					
					public List<String> getXMLNamespaces() {
						// TODO Auto-generated method stub
						return NAMESPACES;
					}
					
					public List<String> getJavaPackages() {
						// TODO Auto-generated method stub
						return null;
					}
				});
			} catch (CommunicationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return false;
	}

	private IIdentity convertStringToIdentity(String jid) throws XMPPError {
		IIdentity returnIdentity = null;
		try {
			this.ccm.getIdManager().fromJid(jid);
		} catch (InvalidFormatException e) {
			throw new XMPPError(StanzaError.jid_malformed, "Invalid JID: "+jid);
		}
		return returnIdentity;
	}
	
	private IIdentity localIdentity() {
		IIdentity returnIdentity = null;

		try {
			this.ccm.getIdManager().getThisNetworkNode();
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return returnIdentity;
	}
}