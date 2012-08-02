package org.societies.comm.xmpp.client.impl;

import static android.content.Context.BIND_AUTO_CREATE;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.convert.AnnotationStrategy;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.strategy.Strategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.pubsub.Affiliation;
import org.societies.api.comm.xmpp.pubsub.PubsubClient;
import org.societies.api.comm.xmpp.pubsub.Subscriber;
import org.societies.api.comm.xmpp.pubsub.Subscription;
import org.societies.api.comm.xmpp.pubsub.SubscriptionState;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.InvalidFormatException;
import org.societies.comm.android.ipc.IMethodInvocation;
import org.societies.comm.android.ipc.MethodInvocationServiceConnection;
import org.societies.comm.android.ipc.utils.MarshallUtils;
import org.societies.identity.IdentityManagerImpl;
import org.societies.pubsub.interfaces.ISubscriber;
import org.societies.pubsub.interfaces.Pubsub;
import org.societies.pubsub.interfaces.SubscriptionParcelable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

public class PubsubClientAndroid implements PubsubClient { 
	
	private static final Logger LOG = LoggerFactory.getLogger(PubsubClientAndroid.class);
	
	private static final ComponentName serviceCN = new ComponentName("org.societies.pubsub", "org.societies.pubsub.PubsubService"); // TODO	

	private MethodInvocationServiceConnection<Pubsub> miServiceConnection;
	//private Marshaller contentMarshaller;
	//private Unmarshaller contentUnmarshaller;
	private final Map<String, String> nsToPackage = new HashMap<String, String>();
	private String packagesContextPath;
	private Map<Subscriber, SubscriberAdapter> subscribersMap = new HashMap<Subscriber, SubscriberAdapter>();
	
	public PubsubClientAndroid(Context androidContext) {
		Intent intent = new Intent();
		intent.setComponent(serviceCN);
		miServiceConnection = new MethodInvocationServiceConnection<Pubsub>(intent, androidContext, BIND_AUTO_CREATE, Pubsub.class);
		packagesContextPath = "";
		/*
		try {
			JAXBContext jc = JAXBContext.newInstance();
			contentUnmarshaller = jc.createUnmarshaller();
			contentMarshaller = jc.createMarshaller();
		} catch (JAXBException e) {			
			throw new RuntimeException(e.getMessage(), e);
		}
		*/
	}
	
	public void ownerCreate(IIdentity pubsubService, final String node) throws XMPPError, CommunicationException {		
		final String pubsubServiceJid = pubsubService.getJid();
		invokeRemoteMethod(new IMethodInvocation<Pubsub>() {
			public Object invoke(Pubsub pubsub) throws Throwable {				
				pubsub.ownerCreate(pubsubServiceJid, node);				
				return null;
			}
		});		
	}

	public List<String> discoItems(final IIdentity pubsubService, final String node)
			throws XMPPError, CommunicationException {
		return (List<String>)invokeRemoteMethod(new IMethodInvocation<Pubsub>() {
			public Object invoke(Pubsub pubsub) throws Throwable {
				return pubsub.discoItems(pubsubService.getJid(), node);				
			}
		});
	}

	public Subscription subscriberSubscribe(IIdentity pubsubService,
			final String node, final Subscriber subscriber) throws XMPPError,
			CommunicationException {
		final String pubsubServiceJid = pubsubService.getJid();
		final SubscriberAdapter subscriberAdapter = new SubscriberAdapter(subscriber);
		Subscription subscription = ((SubscriptionParcelable)invokeRemoteMethodAndKeepBound(new IMethodInvocation<Pubsub>() {
			public Object invoke(Pubsub pubsub) throws Throwable {
				return pubsub.subscriberSubscribe(pubsubServiceJid, node, subscriberAdapter);
			}
		})).subscription();	
		subscribersMap.put(subscriber, subscriberAdapter);
		return subscription;
	}

	public void subscriberUnsubscribe(final IIdentity pubsubService, final String node,
			Subscriber subscriber) throws XMPPError, CommunicationException {
		final String pubsubServiceJid = pubsubService.getJid();
		final SubscriberAdapter subscriberAdapter = subscribersMap.get(subscriber);		
		invokeRemoteMethod(new IMethodInvocation<Pubsub>() {
			public Object invoke(Pubsub pubsub) throws Throwable {
				pubsub.subscriberUnsubscribe(pubsubServiceJid, node, subscriberAdapter);
				return null;
			}
		});
		miServiceConnection.unbind();
	}

	public List<Element> subscriberRetrieveLast(IIdentity pubsubService,
			String node, String subId) throws XMPPError, CommunicationException {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Element> subscriberRetrieveSpecific(IIdentity pubsubService,
			String node, String subId, List<String> itemIdList)
			throws XMPPError, CommunicationException {
		// TODO Auto-generated method stub
		return null;
	}

	public String publisherPublish(IIdentity pubsubService, final String node,
			final String itemId, Object item) throws XMPPError, CommunicationException {
		final String pubsubServiceJid = pubsubService.getJid();
		final String itemXml;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {	
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.newDocument();
			//synchronized (contentMarshaller) {
			//	contentMarshaller.marshal(item, doc);
			//}
			//itemXml = MarshallUtils.nodeToString(doc.getDocumentElement());
			itemXml = marshallPayload(item);
		} catch (ParserConfigurationException e) {
			throw new CommunicationException("ParserConfigurationException while marshalling item to publish", e);
		} catch (Exception e) {
			throw new CommunicationException("Exception while marshalling item to publish", e);
		}
		
		return (String)invokeRemoteMethod(new IMethodInvocation<Pubsub>() {
			public Object invoke(Pubsub pubsub) throws Throwable {
				return pubsub.publisherPublish(pubsubServiceJid, node, itemId, itemXml);
			}
		});
	}
	
	public void publisherDelete(final IIdentity pubsubService, final String node, final String itemId) throws XMPPError, CommunicationException {
		invokeRemoteMethod(new IMethodInvocation<Pubsub>() {
			public Object invoke(Pubsub pubsub) throws Throwable {
				pubsub.publisherDelete(pubsubService.getJid(), node, itemId);
				return null;
			}
		});	
	}

	public void ownerDelete(IIdentity pubsubService, final String node)
			throws XMPPError, CommunicationException {
		final String pubsubServiceJid = pubsubService.getJid();
		invokeRemoteMethod(new IMethodInvocation<Pubsub>() {
			public Object invoke(Pubsub pubsub) throws Throwable {
				pubsub.ownerDelete(pubsubServiceJid, node);
				return null;
			}
		});
	}

	public void ownerPurgeItems(final IIdentity pubsubService, final String node)
			throws XMPPError, CommunicationException {
		invokeRemoteMethod(new IMethodInvocation<Pubsub>() {
			public Object invoke(Pubsub pubsub) throws Throwable {
				pubsub.ownerPurgeItems(pubsubService.getJid(), node);
				return null;
			}
		});	
	}

	public Map<IIdentity, SubscriptionState> ownerGetSubscriptions(
			IIdentity pubsubService, String node) throws XMPPError,
			CommunicationException {
		// TODO Auto-generated method stub
		return null;
	}

	public Map<IIdentity, Affiliation> ownerGetAffiliations(
			IIdentity pubsubService, String node) throws XMPPError,
			CommunicationException {
		// TODO Auto-generated method stub
		return null;
	}

	public void ownerSetSubscriptions(IIdentity pubsubService, String node,
			Map<IIdentity, SubscriptionState> subscriptions) throws XMPPError,
			CommunicationException {
		// TODO Auto-generated method stub
		
	}

	public void ownerSetAffiliations(IIdentity pubsubService, String node,
			Map<IIdentity, Affiliation> affiliations) throws XMPPError,
			CommunicationException {
		// TODO Auto-generated method stub
		
	}
	
	public synchronized void addJaxbPackages(List<String> packageList) { //throws JAXBException {		
		StringBuilder contextPath = new StringBuilder(packagesContextPath);
		for (String pack : packageList)
			contextPath.append(":" + pack);

		/*
		JAXBContext jc = JAXBContext.newInstance(contextPath.toString(), this.getClass().getClassLoader());
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
	
	private String marshallPayload(Object payload) {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		
		//GET SIMPLE SERIALISER 
		Strategy strategy = new AnnotationStrategy();
		Serializer s = new Persister(strategy);
		try {
			s.write(payload, os);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return os.toString();
	}
	
	public Object unmarshallPayload(Element element) throws Exception {
		if(element == null) // Empty stanza
			return null;
		
		String namespace = element.lookupNamespaceURI(element.getPrefix());
		String xml = MarshallUtils.nodeToString(element);
		
		//GET CLASS FIRST
		String packageStr = nsToPackage.get(namespace);  
		String beanName = element.getLocalName().substring(0,1).toUpperCase() + element.getLocalName().substring(1); //NEEDS TO BE "CalcBean", not "calcBean"
		Class<?> c = Class.forName(packageStr + "." + beanName);
		
		//GET SIMPLE SERIALISER 
		Strategy strategy = new AnnotationStrategy();
		Serializer s = new Persister(strategy);
		Object payload = s.read(c, xml);
		
		return payload;
	}
	
	private Object invokeRemoteMethod(IMethodInvocation<Pubsub> methodInvocation) throws XMPPError, CommunicationException {		
		try {
			return miServiceConnection.invoke(methodInvocation);
		} catch (Throwable e) {
			if(e instanceof XMPPError)
				throw (XMPPError)e;
			if(e instanceof CommunicationException)
				throw (CommunicationException)e;
			throw new CommunicationException(e.getMessage(), e);
		}
	}
	
	private Object invokeRemoteMethodAndKeepBound(IMethodInvocation<Pubsub> methodInvocation) throws XMPPError, CommunicationException {		
		try {
			return miServiceConnection.invokeAndKeepBound(methodInvocation);
		} catch (Throwable e) {
			if(e instanceof XMPPError)
				throw (XMPPError)e;
			if(e instanceof CommunicationException)
				throw (CommunicationException)e;
			throw new CommunicationException(e.getMessage(), e);
		}
	}
	
	private class SubscriberAdapter implements ISubscriber {
		private Subscriber subscriber;
		
		public SubscriberAdapter(Subscriber subscriber) {
			this.subscriber = subscriber;
		}

		public void pubsubEvent(String pubsubService, String node, String itemId, String item) {			
			try {
				IIdentity pubsubServiceIdentity = IdentityManagerImpl.staticfromJid(pubsubService);
			
				Object bean = null;
				//synchronized (contentUnmarshaller) {
				//	Element element = MarshallUtils.stringToElement(item);
				//	bean = contentUnmarshaller.unmarshal(element);
				//}
				Element element = MarshallUtils.stringToElement(item);
				bean = unmarshallPayload(element);
				
				subscriber.pubsubEvent(pubsubServiceIdentity, node, itemId, bean);
			} catch(InvalidFormatException e) {
				LOG.error("InvalidFormatException parsing pubsub service JID.", e);
			} catch (Exception e) {
				LOG.error("Exception while unmarshalling pubsub event payload", e);
			}
		}
	}
}
