package org.societies.comm.xmpp.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlRootElement;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.io.SAXReader;
import org.jivesoftware.whack.ExternalComponentManager;
import org.societies.comm.xmpp.CommunicationException;
import org.societies.comm.xmpp.CommunicationManager;
import org.societies.comm.xmpp.NamespaceExtension;
import org.societies.comm.xmpp.Stanza;
import org.societies.comm.xmpp.util.SocietiesTinderUtil;
import org.xml.sax.InputSource;
import org.xmpp.component.AbstractComponent;
import org.xmpp.component.ComponentException;
import org.xmpp.packet.IQ;
import org.xmpp.packet.IQ.Type;
import org.xmpp.packet.Message;
import org.xmpp.packet.PacketError;

import com.sun.xml.bind.v2.ContextFactory;

public class ExternalComponentCommunication extends AbstractComponent implements CommunicationManager {

	private static final String JABBER_CLIENT = "jabber:client";
	private static final String JABBER_SERVER = "jabber:server";
	
	private String host;
	private String subDomain;
	private String secretKey;
	private ExternalComponentManager manager;
	//private Map<String,Collection<NamespaceExtension>> extensions;
	private Map<String,NamespaceExtension> extensions;
	private Map<String,NamespaceExtension> queryIdExtension;
	private Map<String,Unmarshaller> unmarshallers;
	private Map<Class<?>,Marshaller> marshallers;
	
	private SAXReader reader;
	
	public ExternalComponentCommunication(String host, String subDomain, String secretKey) {
		this.host = host;
		this.subDomain = subDomain;
		this.secretKey = secretKey;
		
		//extensions = new HashMap<String, Collection<NamespaceExtension>>();
		extensions = new HashMap<String, NamespaceExtension>();
		queryIdExtension = new HashMap<String, NamespaceExtension>();
		unmarshallers = new HashMap<String, Unmarshaller>();
		marshallers = new HashMap<Class<?>, Marshaller>();
		
		reader = new SAXReader();
		
		manager = new ExternalComponentManager(host);
		manager.setSecretKey(subDomain, secretKey);
		
		log.info("Connected!");
		log.info("###"+this.getClass().getClassLoader().toString());
		try {
			manager.addComponent(subDomain,this);
		} catch (ComponentException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Implementation of AbstractComponent methods
	 */

	@Override
	public String getDescription() {
		return "This component is just amazing";
	}

	@Override
	public String getName() {
		return "Extensible External Component";
	}
	
	@Override
	protected String[] discoInfoFeatureNamespaces() {
		String[] returnArray = new String[extensions.size()];
		return extensions.keySet().toArray(returnArray);
	}

	@Override
	protected IQ handleIQGet(IQ iq) throws Exception {
		log.info("handleIQGet");
		Element any = (Element) iq.getElement().elements().get(0); // according to the schema in RCF3921 IQs only have one element, unless they have an error
		NamespaceExtension nsExtension = extensions.get(any.getNamespace().getURI());
		Unmarshaller u = unmarshallers.get(any.getNamespace().getURI());
		
//		log.info("extensions.size()="+extensions.size()+"; unmarshallers.size()="+unmarshallers.size());
//		log.info("extensionsKey:"+extensions.keySet().iterator().next()+"; unmarshallersKey:"+unmarshallers.keySet().iterator().next());
//		log.info("extensionsContains:"+extensions.containsKey(any.getNamespace().getURI())+"; unmarshallersContains:"+unmarshallers.containsKey(any.getNamespace().getURI()));
//		log.info("handleIQGet xmlns="+any.getNamespace().getURI()+" ext="+nsExtension+" u="+u);
		org.xmpp.packet.IQ returnIq = null;
		if (extensions!=null && u!=null) {
			// TODO DISCLAIMER: this jaxb-dom4j conversion code is VERY BAD but i have to rush this; the propper solution would be to rewrite whack
			Object pojo = u.unmarshal(new InputSource(new StringReader(any.asXML())));
			Object result = nsExtension.receiveQuery(SocietiesTinderUtil.toStanzaInfo(iq), pojo);
			returnIq = new IQ(Type.result, iq.getID());
			returnIq.setTo(iq.getFrom());
			ByteArrayOutputStream os = new ByteArrayOutputStream(); 
			marshallers.get(result.getClass()).marshal(result, os);
			ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
	        Document document = reader.read(is);
			returnIq.getElement().add(document.getRootElement());
		}
		else {
			returnIq = new IQ(Type.error, iq.getID());
			returnIq.setTo(iq.getFrom());
			PacketError error = new PacketError(PacketError.Condition.service_unavailable, PacketError.Type.cancel);
			returnIq.getElement().add(error.getElement());
		}
		return returnIq;
	}

	@Override
	protected IQ handleIQSet(IQ iq) throws Exception {
		return handleIQGet(iq);
	}
	
	@Override
	protected void handleIQResult(IQ iq) {
		Element any = (Element) iq.getElement().elements().get(0); // according to the schema in RCF3921 IQs only have one element, unless they have an error
		NamespaceExtension callback = queryIdExtension.get(iq.getID());
		Unmarshaller u = unmarshallers.get(any.getNamespace());
		try {
			if (callback!=null && u!=null) {
				// TODO DISCLAIMER: this jaxb-dom4j conversion code is VERY BAD but i have to rush this; the propper solution would be to rewrite whack
				Object pojo = u.unmarshal(new InputSource(new StringReader(any.asXML())));
				callback.receiveResult(SocietiesTinderUtil.toStanzaInfo(iq), pojo);
			}
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	protected void handleIQError(IQ iq) {
		NamespaceExtension callback = queryIdExtension.get(iq.getID());
		if (callback!=null) {
			callback.receiveError(SocietiesTinderUtil.toStanzaInfo(iq));
		}
	}
	
	@Override
	protected void handleMessage(Message message) {
		// according to the schema in RCF3921 messages have an unbounded number of "subject", "body" or "thread" elements before the any element part
		Element any = null;
		for (Object o : message.getElement().elements()) {
			Namespace ns = ((Element)o).getNamespace();
			if (!(ns.equals(JABBER_CLIENT) || ns.equals(JABBER_SERVER))) {
				any = (Element) o;
				break;
			}
		}
		NamespaceExtension nsExtension = extensions.get(any.getNamespace().toString());
		Unmarshaller u = unmarshallers.get(any.getNamespace());
		try {
			if (extensions!=null && u!=null) {
				// TODO DISCLAIMER: this jaxb-dom4j conversion code is VERY BAD but i have to rush this; the propper solution would be to rewrite whack
				Object pojo = u.unmarshal(new InputSource(new StringReader(any.asXML())));
				nsExtension.receiveMessage(SocietiesTinderUtil.toStanzaInfo(message), pojo);
			}
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Implementation of CommunicationManager methods
	 */
	
	// TODO test thread.getclassloader and Async
	public void register(NamespaceExtension nsExt) throws CommunicationException, ClassNotFoundException {
//		Collection<NamespaceExtension> currentExtensions = extensions.get(nsExt.getNamespace());
//		if (currentExtensions==null) {
//			currentExtensions = new ArrayList<NamespaceExtension>();
//			extensions.put(nsExt.getNamespace(), currentExtensions);
//		}
//		currentExtensions.add(nsExt);
		// latest sticks!
		log.info("register "+nsExt.getNamespace());
		log.info("this.getClass().getClassLoader().toString()="+this.getClass().getClassLoader().toString());
		log.info("Thread.currentThread().getContextClassLoader()="+Thread.currentThread().getContextClassLoader());
		ContextFactory cf;
		try {
			extensions.put(nsExt.getNamespace(),nsExt);
			JAXBContext jc = JAXBContext.newInstance(nsExt.getPackage(),this.getClass().getClassLoader());
			unmarshallers.put(nsExt.getNamespace(), jc.createUnmarshaller());
			Marshaller mr = jc.createMarshaller();
			
			// is not loading join and leave because they are not complextypes
			Class<?> objFactory = Class.forName(nsExt.getPackage()+".ObjectFactory");
			for (Method m : objFactory.getMethods()) {
				if (m.getName().startsWith("create")) {
					XmlRootElement re = m.getReturnType().getAnnotation(XmlRootElement.class);
					if (re!=null) {
						marshallers.put(m.getReturnType(),mr);
					}
				}
			}
		} catch (JAXBException e) {
			throw new CommunicationException("Could not register NamespaceExtension... caused by JAXBException: ", e);
		}
	}
	
	public void sendQuery(Stanza info, Object payload, NamespaceExtension callback) throws Exception {
		// TODO DISCLAIMER: this jaxb-dom4j conversion code is VERY BAD but i have to rush this; the propper solution would be to rewrite whack
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		marshallers.get(payload.getClass()).marshal(payload, os);
		ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
        Document document = reader.read(is);
        IQ iq = (IQ) SocietiesTinderUtil.toPacket(info);
		iq.getElement().add(document.getRootElement());
		queryIdExtension.put(iq.getID(), callback);
	}

	public void sendMessage(Stanza info, Object payload)
			throws Exception {
		if (payload!=null) {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			marshallers.get(payload.getClass()).marshal(payload, os);
			ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
	        Message message = (Message) SocietiesTinderUtil.toPacket(info);
	        Document document = reader.read(is);
			message.getElement().add(document.getRootElement());
		}
	}
}