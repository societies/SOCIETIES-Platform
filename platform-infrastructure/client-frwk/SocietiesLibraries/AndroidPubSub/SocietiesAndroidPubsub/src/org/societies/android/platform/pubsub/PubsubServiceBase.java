package org.societies.android.platform.pubsub;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.jabber.protocol.pubsub.Create;
import org.jabber.protocol.pubsub.Item;
import org.jabber.protocol.pubsub.Publish;
import org.jabber.protocol.pubsub.Pubsub;
import org.jabber.protocol.pubsub.Retract;
import org.jabber.protocol.pubsub.Subscribe;
import org.jabber.protocol.pubsub.Unsubscribe;
import org.jabber.protocol.pubsub.owner.Delete;
import org.jabber.protocol.pubsub.owner.Purge;
import org.jivesoftware.smack.packet.IQ;

import org.societies.api.identity.IIdentity;
import org.societies.api.identity.InvalidFormatException;
import org.societies.android.api.comms.IMethodCallback;
import org.societies.android.api.comms.xmpp.CommunicationException;
import org.societies.android.api.comms.xmpp.ICommCallback;
import org.societies.android.api.comms.xmpp.Stanza;
import org.societies.android.api.comms.xmpp.StanzaError;
import org.societies.android.api.comms.xmpp.XMPPError;
import org.societies.android.api.comms.xmpp.XMPPInfo;
import org.societies.android.api.pubsub.IPubsubService;
import org.societies.android.api.pubsub.ISubscriber;
import org.societies.android.api.pubsub.ISubscriberInternal;
import org.societies.android.api.pubsub.Subscription;
import org.societies.android.platform.androidutils.MarshallUtils;
import org.societies.utilities.DBC.Dbc;
import org.xml.sax.SAXException;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class PubsubServiceBase implements IPubsubService {
	private static final String LOG_TAG = PubsubServiceBase.class.getName();
	
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
	
	private PubsubCommsMgr ccm;
	private Context androidContext;
	private boolean restrictBroadcast;
	private Map<Subscription,List<ISubscriber>> subscribers;	
	
	public PubsubServiceBase (Context androidContext, PubsubCommsMgr ccm, boolean restrictBroadcast) {
		this.ccm = ccm;
		this.androidContext = androidContext;
		this.restrictBroadcast = restrictBroadcast;
		Log.d(LOG_TAG, "Broadcast restricted : " + this.restrictBroadcast);
		Log.d(LOG_TAG, "PubsubServiceBase object constructed");
	}

	@Override
	public boolean bindToAndroidComms(final String client, final long remoteCallID) {
		this.ccm.bindCommsService(new IMethodCallback() {
			
			@Override
			public void returnAction(String result) {
			}
			
			@Override
			public void returnAction(boolean resultFlag) {
				//Send intent
				Intent intent = new Intent();
				if (PubsubServiceBase.this.restrictBroadcast) {
					intent.setPackage(client);
				}
				intent.putExtra(INTENT_RETURN_CALL_ID_KEY, remoteCallID);
				intent.setAction(IPubsubService.BIND_TO_ANDROID_COMMS);
				intent.putExtra(IPubsubService.INTENT_RETURN_VALUE_KEY, resultFlag);
				PubsubServiceBase.this.androidContext.sendBroadcast(intent);
			}
		});
		return false;
	}

	@Override
	public boolean unBindFromAndroidComms(String client, long remoteCallID) {
		return this.ccm.unbindCommsService();
	}


	public String[] discoItems(final String client, String pubsubService, String node, final long remoteCallID) {
		Dbc.require("Client must be supplied", null != client && client.length() > 0);
		Dbc.require("Pubsub node must be supplied", null != node && node.length() > 0);
		Dbc.require("Pubsub service must be specified", null != pubsubService && pubsubService.length() > 0);
		
		Log.d(LOG_TAG, "discoItems called with domain authority: " + pubsubService + " and node: " + node);
		
		try {
			this.ccm.getItems(convertStringToIdentity(pubsubService), node, new ICommCallback() {
				
				public void receiveResult(Stanza arg0, Object arg1) {
				}
				
				public void receiveMessage(Stanza arg0, Object result) {
				}
				
				public void receiveItems(Stanza stanza, String mapKey, List<String> mapValue) {
					
					String returnValue [] = new String [mapValue.size()];
					for (int i = 0; i < mapValue.size(); i++) {
						returnValue[i] = mapValue.get(i);
					}
					//Send intent
					Intent intent = new Intent();
					if (PubsubServiceBase.this.restrictBroadcast) {
						intent.setPackage(client);
					}
					intent.putExtra(INTENT_RETURN_CALL_ID_KEY, remoteCallID);
					intent.setAction(IPubsubService.DISCO_ITEMS);
					intent.putExtra(IPubsubService.INTENT_RETURN_VALUE_KEY, returnValue);
					PubsubServiceBase.this.androidContext.sendBroadcast(intent);
				}
				
				public void receiveInfo(Stanza arg0, String arg1, XMPPInfo arg2) {
				}
				
				public void receiveError(Stanza arg0, XMPPError arg1) {
				}
				
				public List<String> getXMLNamespaces() {
					return NAMESPACES;
				}
				
				public List<String> getJavaPackages() {
					return PACKAGES;
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
		Dbc.require("Client must be supplied", null != client && client.length() > 0);
		Dbc.require("Pubsub node must be supplied", null != node && node.length() > 0);
		Dbc.require("Pubsub service must be specified", null != pubsubService && pubsubService.length() > 0);
		
		Log.d(LOG_TAG, "ownerCreate called with domain authority: " + pubsubService + " and node: " + node);

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
				
				public void receiveResult(Stanza stanza, Object payload) {
//					if (payload instanceof org.jabber.protocol.pubsub.event.Event) {
//						org.jabber.protocol.pubsub.event.Items items = ((org.jabber.protocol.pubsub.event.Event)payload).getItems();
//						String node = items.getNode();
//						Subscription sub = new Subscription(stanza.getFrom(), stanza.getTo(), node, null); // TODO may break due to mismatch between "to" and local IIdentity
//						org.jabber.protocol.pubsub.event.Item i = items.getItem().get(0); // TODO assume only one item per notification
//						try {
//							List<ISubscriber> subscriberList = subscribers.get(sub);
//							for (ISubscriber subscriber : subscriberList)
//								subscriber.pubsubEvent(stanza.getFrom().getJid(), node, i.getId(), MarshallUtils.nodeToString((Element)i.getAny()));
//
//						} catch (TransformerException e) {
//							Log.e(LOG_TAG, "Error while unmarshalling pubsub event payload", e);
//						}
//					}

					//Send intent
					Intent intent = new Intent();
					if (PubsubServiceBase.this.restrictBroadcast) {
						intent.setPackage(client);
					}
					intent.putExtra(INTENT_RETURN_CALL_ID_KEY, remoteCallID);
					intent.setAction(IPubsubService.OWNER_CREATE);
					intent.putExtra(IPubsubService.INTENT_RETURN_VALUE_KEY, (String) payload);
					PubsubServiceBase.this.androidContext.sendBroadcast(intent);
				}
				
				public void receiveMessage(Stanza arg0, Object arg1) {
				}
				
				public void receiveItems(Stanza arg0, String arg1, List<String> arg2) {
				}
				
				public void receiveInfo(Stanza arg0, String arg1, XMPPInfo arg2) {
				}
				
				public void receiveError(Stanza arg0, XMPPError arg1) {
				}
				
				public List<String> getXMLNamespaces() {
					return NAMESPACES;
				}
				
				public List<String> getJavaPackages() {
					return PACKAGES;
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
		Dbc.require("Client must be supplied", null != client && client.length() > 0);
		Dbc.require("Pubsub node must be supplied", null != node && node.length() > 0);
		Dbc.require("Pubsub service must be specified", null != pubsubService && pubsubService.length() > 0);
		
		Log.d(LOG_TAG, "ownerDelete called with domain authority: " + pubsubService + " and node: " + node);

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
					intent.putExtra(IPubsubService.INTENT_RETURN_VALUE_KEY, (String) object);
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
					return NAMESPACES;
				}
				
				public List<String> getJavaPackages() {
					return PACKAGES;
				}
			});
		} catch (CommunicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}



	public boolean ownerPurgeItems(final String client, String pubsubServiceJid, String node, final long remoteCallID) {
		Dbc.require("Client must be supplied", null != client && client.length() > 0);
		Dbc.require("Pubsub node must be supplied", null != node && node.length() > 0);
		Dbc.require("Pubsub service must be specified", null != pubsubServiceJid && pubsubServiceJid.length() > 0);
		
		Log.d(LOG_TAG, "ownerPurgeItems called with domain authority: " + pubsubServiceJid + " and node: " + node);
		
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
					intent.putExtra(IPubsubService.INTENT_RETURN_VALUE_KEY, (String) object);
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
					return NAMESPACES;
				}
				
				public List<String> getJavaPackages() {
					return PACKAGES;
				}
			});
		} catch (CommunicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}



	public String publisherPublish(final String client, String pubsubService, String node, String itemId, String item, final long remoteCallID) {
		Dbc.require("Client must be supplied", null != client && client.length() > 0);
		Dbc.require("Pubsub node must be supplied", null != node && node.length() > 0);
		Dbc.require("Pubsub service must be specified", null != pubsubService && pubsubService.length() > 0);
		
		Log.d(LOG_TAG, "publisherPublish called with domain authority: " + pubsubService + " and node: " + node);

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
					intent.putExtra(IPubsubService.INTENT_RETURN_VALUE_KEY, (String) object);
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
					return NAMESPACES;
				}
				
				public List<String> getJavaPackages() {
					return PACKAGES;
				}
			});
		} catch (CommunicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}



	public boolean publisherDelete(final String client, String pubsubServiceJid, String node, String itemId, final long remoteCallID) {
		Dbc.require("Client must be supplied", null != client && client.length() > 0);
		Dbc.require("Pubsub node must be supplied", null != node && node.length() > 0);
		Dbc.require("Pubsub service must be specified", null != pubsubServiceJid && pubsubServiceJid.length() > 0);
		
		Log.d(LOG_TAG, "publisherDelete called with domain authority: " + pubsubServiceJid + " and node: " + node);

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
					intent.putExtra(IPubsubService.INTENT_RETURN_VALUE_KEY, (String) object);
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
					return NAMESPACES;
				}
				
				public List<String> getJavaPackages() {
					return PACKAGES;
				}
			});
		} catch (CommunicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}



	public boolean subscriberSubscribe(final String client, String pubsubService, final String node, final ISubscriberInternal subscriber, final long remoteCallID) {
		Dbc.require("Client must be supplied", null != client && client.length() > 0);
		Dbc.require("Pubsub node must be supplied", null != node && node.length() > 0);
		Dbc.require("Pubsub service must be specified", null != pubsubService && pubsubService.length() > 0);
		
		Log.d(LOG_TAG, "subscriberSubscribe called with domain authority: " + pubsubService + " and node: " + node);

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
//							String subId = ((Pubsub)response).getSubscription().getSubid();
//							Subscription newSubscription = new Subscription(pubsubServiceIdentity, localIdentity(), node, subId);
//							subscribers.put(newSubscription, subscriberList);
							//Send intent
							Intent intent = new Intent();
							if (PubsubServiceBase.this.restrictBroadcast) {
								intent.setPackage(client);
							}
							intent.putExtra(INTENT_RETURN_CALL_ID_KEY, remoteCallID);
							intent.setAction(IPubsubService.SUBSCRIBER_SUBSCRIBE);
							intent.putExtra(IPubsubService.INTENT_RETURN_VALUE_KEY, (String) response);
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
							return NAMESPACES;
						}
						
						public List<String> getJavaPackages() {
							return PACKAGES;
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
		return false;
	}



	public boolean subscriberUnsubscribe(final String client, String pubsubService, String node, ISubscriberInternal subscriber, final long remoteCallID) {
		Dbc.require("Client must be supplied", null != client && client.length() > 0);
		Dbc.require("Pubsub node must be supplied", null != node && node.length() > 0);
		Dbc.require("Pubsub service must be specified", null != pubsubService && pubsubService.length() > 0);
		
		Log.d(LOG_TAG, "subscriberUnsubscribe called with domain authority: " + pubsubService + " and node: " + node);

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
						intent.putExtra(IPubsubService.INTENT_RETURN_VALUE_KEY, (String) object);
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
						return NAMESPACES;
					}
					
					public List<String> getJavaPackages() {
						return PACKAGES;
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
