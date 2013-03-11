package org.societies.comm.xmpp.pubsub.impl;

import java.io.ByteArrayInputStream;
import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.UnavailableException;

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
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.convert.AnnotationStrategy;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.strategy.Strategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.datatypes.StanzaError;
import org.societies.api.comm.xmpp.datatypes.XMPPInfo;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommCallback;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.comm.xmpp.pubsub.Affiliation;
import org.societies.api.comm.xmpp.pubsub.PubsubClient;
import org.societies.api.comm.xmpp.pubsub.Subscriber;
import org.societies.api.comm.xmpp.pubsub.Subscription;
import org.societies.api.comm.xmpp.pubsub.SubscriptionState;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.InvalidFormatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Element;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;

import com.sun.org.apache.xerces.internal.dom.ElementNSImpl;

@Component
public class PubsubClientImpl implements PubsubClient, ICommCallback {

	public static final int WAIT_TIMEOUT = 10000;
	public static final long REQUEST_TIMEOUT = 20000;
	
	private final static List<String> NAMESPACES = Collections
			.unmodifiableList(Arrays.asList("http://jabber.org/protocol/pubsub#event",
					"http://jabber.org/protocol/pubsub",
   					"http://jabber.org/protocol/pubsub#errors",
   					"http://jabber.org/protocol/pubsub#owner",
   					"jabber:x:data"));
	private static final List<String> PACKAGES = Collections
			.unmodifiableList(Arrays.asList("org.jabber.protocol.pubsub.event",
					"org.jabber.protocol.pubsub",
					"org.jabber.protocol.pubsub.errors",
					"org.jabber.protocol.pubsub.owner",
					"jabber.x.data"));
	
	private static Logger LOG = LoggerFactory
			.getLogger(PubsubClientImpl.class);
	
	private ICommManager endpoint;
	private Map<String,Object> responses;
	private Map<String,Long> responseTimeout;
	private Map<Subscription,List<Subscriber>> subscribers;
	private IIdentityManager idm;
	
	private final Map<String, String> nsToPackage = new HashMap<String, String>();
	private String packagesContextPath;
	private IIdentity localIdentity;
	private Map<String,Class<?>> elementToClass;
	private Serializer serializer;
	
	@Autowired
	public PubsubClientImpl(ICommManager endpoint) {
		responses = new HashMap<String, Object>();
		responseTimeout = new HashMap<String, Long>();
		subscribers = new HashMap<Subscription, List<Subscriber>>();
		elementToClass = new HashMap<String, Class<?>>();
		
		Strategy strategy = new AnnotationStrategy();
		serializer = new Persister(strategy);
		
		this.endpoint = endpoint;
		idm = endpoint.getIdManager();
		try {
			if (endpoint.isConnected())
				localIdentity = idm.getThisNetworkNode();
			else
				throw new CommunicationException("Injected endpoint is not connected!");
			packagesContextPath = "";
			endpoint.register(this);
			
		} catch (CommunicationException e) {
			LOG.error(e.getMessage(),e);
		}
	}
	
	public ICommManager getICommManager() {
		return endpoint;
	}
	
	/*
	 * CommCallback Impl
	 */
	
	@Override
	public List<String> getXMLNamespaces() {
		return NAMESPACES;
	}

	@Override
	public List<String> getJavaPackages() {
		return PACKAGES;
	}

	/** Retrieves a package from a namespace mapping
	 * @param namespace
	 * @return
	 * @throws UnavailableException
	 */
	private String getPackage(String namespace) {
		return nsToPackage.get(namespace);
	}
	
	@Override
	public void receiveMessage(Stanza stanza, Object payload) {
		if (payload instanceof org.jabber.protocol.pubsub.event.Event) {
			org.jabber.protocol.pubsub.event.Items items = ((org.jabber.protocol.pubsub.event.Event)payload).getItems();
			String node = items.getNode();
			Subscription sub = new Subscription(stanza.getFrom(), stanza.getTo(), node, null); // TODO may break due to mismatch between "to" and local IIdentity
			org.jabber.protocol.pubsub.event.Item i = items.getItem().get(0); // TODO assume only one item per notification
			
			Object bean = unmarshallBean((ElementNSImpl) i.getAny());
			
			//POST EVENT
			List<Subscriber> subscriberList = subscribers.get(sub);
			for (Subscriber subscriber : subscriberList)
				subscriber.pubsubEvent(stanza.getFrom(), node, i.getId(), bean);
			
			// TODO multiple subscribers and classloaders
			// TODO CLASSLOADING MAGIC DISABLED!
//			Thread.currentThread().setContextClassLoader(oldCl);
		}
	}
	// TODO subId
//	<message from='pubsub.shakespeare.lit' to='francisco@denmark.lit' id='foo'>
//	  <event xmlns='http://jabber.org/protocol/pubsub#event'>
//	    <items node='princely_musings'>
//	      <item id='ae890ac52d0df67ed7cfdf51b644e901'/>
//	    </items>
//	  </event>
//	  <headers xmlns='http://jabber.org/protocol/shim'>
//	    <header name='SubID'>123-abc</header>
//	    <header name='SubID'>004-yyy</header>
//	  </headers>
//	</message>

	
	private Object unmarshallBean(ElementNSImpl eventBean) {
		Object bean = null;
		
		//CONVERT THE .getAny() OBJECT TO XML
		DOMImplementationLS domImplLS = (DOMImplementationLS) eventBean.getOwnerDocument().getImplementation(); 
		LSSerializer domSerializer = domImplLS.createLSSerializer(); 
		String eventBeanXML = domSerializer.writeToString(eventBean); 
		
		//SERIALISE OBJECT
		String elementID = "{" + eventBean.getNamespaceURI() + "}" + eventBean.getLocalName();
		Class<?> c = elementToClass.get(elementID);
		// TODO CLASSLOADING MAGIC DISABLED!
//		ClassLoader oldCl = Thread.currentThread().getContextClassLoader();
//		Thread.currentThread().setContextClassLoader(c.getClassLoader());
		
		if (c==null) {
			// when received xml was has not been binded print a warn and notify with raw XML
			String[] keyArray = new String[elementToClass.keySet().size()];
			keyArray = elementToClass.keySet().toArray(keyArray);
			LOG.warn("No class found for namespace{element} '"+elementID+"', passing unmarshalled XML element... Registered entries are: "+Arrays.toString(keyArray));
			bean = eventBean;
		}
		else {
			// unmarshall item content
			try {
				bean = serializer.read(c, eventBeanXML);
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
			}
		}
		
		return bean;
	}

	@Override
	public void receiveResult(Stanza stanza, Object payload) {
		synchronized (responses) {
//			LOG.info("receiveResult 4 id "+stanza.getId());
			responses.put(stanza.getId(), payload);
			responses.notifyAll();
		}
	}

	@Override
	public void receiveError(Stanza stanza, XMPPError error) {
		synchronized (responses) {
//			LOG.info("receiveError 4 id "+stanza.getId());
			responses.put(stanza.getId(), error);
			responses.notifyAll();
		}
	}

	@Override
	public void receiveInfo(Stanza stanza, String node, XMPPInfo info) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void receiveItems(Stanza stanza, String node, List<String> items) {
		SimpleEntry<String, List<String>> mapSimpleEntry = new AbstractMap.SimpleEntry<String, List<String>>(node, items);
		synchronized (responses) {
//			LOG.info("receiveItems 4 id "+stanza.getId());
			responses.put(stanza.getId(), mapSimpleEntry);
			responses.notifyAll();
		}
	}

	/*
	 * PubsubClient Impl - emulates synchronous
	 */
	
	private Object blockingIQ(Stanza stanza, Object payload) throws CommunicationException, XMPPError  {
		endpoint.sendIQSet(stanza, payload, this);
		return waitForResponse(stanza.getId());
	}
	
	private Object waitForResponse(String id) throws XMPPError {
		
		synchronized (responseTimeout) {
			responseTimeout.put(id, REQUEST_TIMEOUT);
			
			Object response = null;
			synchronized (responses) {
				while (!responses.containsKey(id)) {
					long cycleStart = System.currentTimeMillis();
					try {
//						LOG.info("waiting response 4 id "+id);
						responses.wait(WAIT_TIMEOUT);
					} catch (InterruptedException e) {
						LOG.info(e.getMessage(),e);
					}
//					LOG.info("checking response 4 id "+id+" in "+Arrays.toString(responses.keySet().toArray()));
					long timeLeft = responseTimeout.get(id)-System.currentTimeMillis()+cycleStart;
					if (timeLeft>0)
						responseTimeout.put(id,timeLeft);
					else {
						LOG.warn("Result for request with id='"+id+"' timed out...");
						throw new XMPPError(StanzaError.remote_server_timeout);
					}
				}
				response = responses.remove(id);
//				LOG.info("got response 4 id "+id);
			}
			if (response instanceof XMPPError)
				throw (XMPPError)response;
			return response;
		}
	}

	@Override
	public List<String> discoItems(IIdentity pubsubService, String node)
			throws XMPPError, CommunicationException {
		String id = endpoint.getItems(pubsubService, node, this);
		Object response = waitForResponse(id);
		
		// TODO node check
//		String returnedNode = ((SimpleEntry<String, List<XMPPNode>>)response).getKey();
//		if (returnedNode != node)
//			throw new CommunicationException("");
		return ((SimpleEntry<String, List<String>>)response).getValue();
	}

	@Override
	public Subscription subscriberSubscribe(IIdentity pubsubService, String node,
			Subscriber subscriber) throws XMPPError, CommunicationException {
		Subscription subscription = new Subscription(pubsubService, localIdentity, node, null);
		List<Subscriber> subscriberList = subscribers.get(subscription);
		
		if (subscriberList==null) {
			subscriberList = new ArrayList<Subscriber>();
			
			Stanza stanza = new Stanza(pubsubService);
			Pubsub payload = new Pubsub();
			Subscribe sub = new Subscribe();
			sub.setJid(localIdentity.getBareJid());
			sub.setNode(node);
			payload.setSubscribe(sub);
	
			Object response = blockingIQ(stanza, payload);
			
			String subId = ((Pubsub)response).getSubscription().getSubid();
			subscription = new Subscription(pubsubService, localIdentity, node, subId);
			subscribers.put(subscription, subscriberList);
		}
		
		subscriberList.add(subscriber);
		
		return subscription;
	}

	@Override
	public void subscriberUnsubscribe(IIdentity pubsubService, String node,
			Subscriber subscriber) throws XMPPError,
			CommunicationException {
		
		Subscription subscription = new Subscription(pubsubService, localIdentity, node, null);
		List<Subscriber> subscriberList = subscribers.get(subscription);
		subscriberList.remove(subscriber);
		
		if (subscriberList.size()==0) {
			Stanza stanza = new Stanza(pubsubService);
			Pubsub payload = new Pubsub();
			Unsubscribe unsub = new Unsubscribe();
			unsub.setJid(localIdentity.getJid());
			unsub.setNode(node);
			payload.setUnsubscribe(unsub);
	
			Object response = blockingIQ(stanza, payload);
		}
	}

	

	@Override
	public List<Object> subscriberRetrieveLast(IIdentity pubsubService,
			String node, String subId) throws XMPPError, CommunicationException {
		Stanza stanza = new Stanza(pubsubService);
		Pubsub payload = new Pubsub();
		Items items = new Items();
		items.setNode(node);
		if (subId!=null)
			items.setSubid(subId);
		// TODO max items... in the server also!
		payload.setItems(items);
		
		Object response = blockingIQ(stanza, payload);
		
		List<Item> itemList = ((Pubsub)response).getItems().getItem();
		List<Object> returnList = new ArrayList<Object>();
		for (Item i : itemList)
			returnList.add(unmarshallBean((ElementNSImpl) i.getAny()));
		
		return returnList;
	}

	@Override
	public List<Object> subscriberRetrieveSpecific(IIdentity pubsubService,
			String node, String subId, List<String> itemIdList)
			throws XMPPError, CommunicationException {
		Stanza stanza = new Stanza(pubsubService);
		Pubsub payload = new Pubsub();
		Items items = new Items();
		items.setNode(node);
		if (subId!=null)
			items.setSubid(subId);
		
		for(String itemId : itemIdList) {
			Item item = new Item();
			item.setId(itemId);
			items.getItem().add(item);
		}
		
		payload.setItems(items);
		
		Object response = blockingIQ(stanza, payload);
		
		List<Item> itemList = ((Pubsub)response).getItems().getItem();
		List<Object> returnList = new ArrayList<Object>();
		for (Item i : itemList)
			returnList.add(unmarshallBean((ElementNSImpl) i.getAny()));
		
		return returnList;
	}

	@Override
	public String publisherPublish(IIdentity pubsubService, String node,
			String itemId, Object item) throws XMPPError, CommunicationException {
		Stanza stanza = new Stanza(pubsubService);
		Pubsub payload = new Pubsub();
		Publish p = new Publish();
		p.setNode(node);
		Item i = new Item();
		if (itemId!=null)
			i.setId(itemId);
		
		i.setAny(item);
		p.setItem(i);
		payload.setPublish(p);
		
		Object response = blockingIQ(stanza, payload);
		
		if (response!=null)
			return ((Pubsub)response).getPublish().getItem().getId();
		else
			return itemId;
	}

	@Override
	public void publisherDelete(IIdentity pubsubService, String node,
			String itemId) throws XMPPError, CommunicationException {
		Stanza stanza = new Stanza(pubsubService);
		Pubsub payload = new Pubsub();
		
		Retract retract = new Retract();
		retract.setNode(node);
		Item i = new Item();
		i.setId(itemId);
		retract.getItem().add(i);
		payload.setRetract(retract);
		
		Object response = blockingIQ(stanza, payload);
	}

	@Override
	public void ownerCreate(IIdentity pubsubService, String node)
			throws XMPPError, CommunicationException {
		Stanza stanza = new Stanza(pubsubService);
		Pubsub payload = new Pubsub();
		Create c = new Create();
		c.setNode(node);
		payload.setCreate(c);
		
		blockingIQ(stanza, payload);
	}

	@Override
	public void ownerDelete(IIdentity pubsubService, String node)
			throws XMPPError, CommunicationException {
		Stanza stanza = new Stanza(pubsubService);
		org.jabber.protocol.pubsub.owner.Pubsub payload = new org.jabber.protocol.pubsub.owner.Pubsub();
		Delete delete = new Delete();
		delete.setNode(node);
		payload.setDelete(delete);
		
		blockingIQ(stanza, payload);
	}

	@Override
	public void ownerPurgeItems(IIdentity pubsubService, String node)
			throws XMPPError, CommunicationException {
		Stanza stanza = new Stanza(pubsubService);
		org.jabber.protocol.pubsub.owner.Pubsub payload = new org.jabber.protocol.pubsub.owner.Pubsub();
		Purge purge = new Purge();
		purge.setNode(node);
		payload.setPurge(purge);
		
		blockingIQ(stanza, payload);
	}

	@Override
	public Map<IIdentity, SubscriptionState> ownerGetSubscriptions(
			IIdentity pubsubService, String node) throws XMPPError,
			CommunicationException {
		Stanza stanza = new Stanza(pubsubService);
		org.jabber.protocol.pubsub.owner.Pubsub payload = new org.jabber.protocol.pubsub.owner.Pubsub();
		Subscriptions subs = new Subscriptions();
		subs.setNode(node);
		payload.setSubscriptions(subs);
		
		blockingIQ(stanza, payload);
		
		List<org.jabber.protocol.pubsub.owner.Subscription> subList = ((org.jabber.protocol.pubsub.owner.Pubsub)payload).getSubscriptions().getSubscription();
		Map<IIdentity, SubscriptionState> returnMap = new HashMap<IIdentity, SubscriptionState>();
		for (org.jabber.protocol.pubsub.owner.Subscription s : subList)
			try {
				returnMap.put(idm.fromJid(s.getJid()), SubscriptionState.valueOf(s.getSubscription()));
			} catch (InvalidFormatException e) {
				LOG.warn("Unable to parse subscription JIDs",e);
			}
		
		return returnMap;
	}

	@Override
	public Map<IIdentity, Affiliation> ownerGetAffiliations(
			IIdentity pubsubService, String node) throws XMPPError,
			CommunicationException {
		Stanza stanza = new Stanza(pubsubService);
		org.jabber.protocol.pubsub.owner.Pubsub payload = new org.jabber.protocol.pubsub.owner.Pubsub();
		Affiliations affs = new Affiliations();
		affs.setNode(node);
		payload.setAffiliations(affs);
		
		blockingIQ(stanza, payload);
		
		List<org.jabber.protocol.pubsub.owner.Affiliation> affList = ((org.jabber.protocol.pubsub.owner.Pubsub)payload).getAffiliations().getAffiliation();
		Map<IIdentity, Affiliation> returnMap = new HashMap<IIdentity, Affiliation>();
		for (org.jabber.protocol.pubsub.owner.Affiliation a : affList)
			try {
				returnMap.put(idm.fromJid(a.getJid()), Affiliation.valueOf(a.getAffiliation()));
			} catch (InvalidFormatException e) {
				LOG.warn("Unable to parse affiliation JIDs",e);
			}
		
		return returnMap;
	}

	@Override
	public void ownerSetSubscriptions(IIdentity pubsubService, String node,
			Map<IIdentity, SubscriptionState> subscriptions) throws XMPPError,
			CommunicationException {
		Stanza stanza = new Stanza(pubsubService);
		org.jabber.protocol.pubsub.owner.Pubsub payload = new org.jabber.protocol.pubsub.owner.Pubsub();
		Subscriptions subs = new Subscriptions();
		subs.setNode(node);
		payload.setSubscriptions(subs);
		
		for (IIdentity subscriber : subscriptions.keySet()) {
			org.jabber.protocol.pubsub.owner.Subscription s = new org.jabber.protocol.pubsub.owner.Subscription();
			s.setJid(subscriber.getJid());
			s.setSubscription(subscriptions.get(subscriber).toString());
			subs.getSubscription().add(s);
		}
		
		blockingIQ(stanza, payload);
		
		// TODO error handling on multiple subscription changes
	}

	@Override
	public void ownerSetAffiliations(IIdentity pubsubService, String node,
			Map<IIdentity, Affiliation> affiliations) throws XMPPError,
			CommunicationException {
		Stanza stanza = new Stanza(pubsubService);
		org.jabber.protocol.pubsub.owner.Pubsub payload = new org.jabber.protocol.pubsub.owner.Pubsub();
		Affiliations affs = new Affiliations();
		affs.setNode(node);
		payload.setAffiliations(affs);
		
		for (IIdentity subscriber : affiliations.keySet()) {
			org.jabber.protocol.pubsub.owner.Affiliation a = new org.jabber.protocol.pubsub.owner.Affiliation();
			a.setJid(subscriber.getJid());
			a.setAffiliation(affiliations.get(subscriber).toString());
			affs.getAffiliation().add(a);
		}
		
		blockingIQ(stanza, payload);
		
		// TODO error handling on multiple affiliation changes
		
	}

	@Override
	public synchronized void addJaxbPackages(List<String> packageList) { //throws JAXBException {
		if (packagesContextPath.length()==0) {
			// TODO first run!
		}
		
		StringBuilder contextPath = new StringBuilder(packagesContextPath);
		for (String pack : packageList)
			contextPath.append(":" + pack);

		/*
		JAXBContext jc = JAXBContext.newInstance(contextPath.toString(),
				this.getClass().getClassLoader());
		contentUnmarshaller = jc.createUnmarshaller();
		contentMarshaller = jc.createMarshaller();
		*/
		//TODO: SIMPLE
		try {
			for (int i=0; i<packageList.size(); i++) {
				String packageStr = packageList.get(i);
				String nsStr = getNSfromPackage(packageStr);
				nsToPackage.put(nsStr, packageStr);
			}	
		}
		catch (Exception ex) {
			LOG.error("Error in JAXBMapping adding: " + ex.getMessage());
		}
		packagesContextPath = contextPath.toString();
	}

	/** Returns the Namespace for a Package string
	 * @param packageString
	 * @return
	 */
	private String getNSfromPackage(String packageString) {
		String ns = "";
		String[] packArr = packageString.split("\\.");
		ns = "http://" + packArr[1] + "." + packArr[0];
		for(int i=2; i<packArr.length; i++)
			ns+="/" + packArr[i]; 
		return ns;
	}

	@Override
	public void addSimpleClasses(List<String> classList) throws ClassNotFoundException {
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		try {
			for (String c : classList) {
				Class<?> clazz = cl.loadClass(c);
				Root rootAnnotation = clazz.getAnnotation(Root.class);
				Namespace namespaceAnnotation = clazz.getAnnotation(Namespace.class);
				if (rootAnnotation!=null && namespaceAnnotation!=null) {
					elementToClass.put("{"+namespaceAnnotation.reference()+"}"+rootAnnotation.name(),clazz);
				}
			}
		} catch (ClassNotFoundException e) {
			throw new ClassNotFoundException(e.getMessage()+" from classloader "+cl.toString()+": class being registered from a context that doesn't have access to it.", e);
		}
	}
}
