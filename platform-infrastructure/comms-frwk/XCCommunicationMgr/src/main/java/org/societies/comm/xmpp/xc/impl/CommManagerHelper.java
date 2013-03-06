/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVA��O, SA (PTIN), IBM ISRAEL
 * SCIENCE AND TECHNOLOGY LTD (IBM), INSTITUT TELECOM (ITSUD), AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA
 * PERIORISMENIS EFTHINIS (AMITEC), TELECOM ITALIA S.p.a.(TI),  TRIALOG (TRIALOG), Stiftelsen SINTEF (SINTEF), NEC EUROPE LTD
 * (NEC))
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
 * conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
 *    disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.societies.comm.xmpp.xc.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.convert.Registry;
import org.simpleframework.xml.convert.RegistryStrategy;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.strategy.Strategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.datatypes.HostedNode;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.datatypes.StanzaError;
import org.societies.api.comm.xmpp.datatypes.XMPPInfo;
import org.societies.api.comm.xmpp.datatypes.XMPPNode;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommCallback;
import org.societies.api.comm.xmpp.interfaces.IFeatureServer;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.InvalidFormatException;
import org.societies.simple.basic.URIConverter;
import org.societies.simple.converters.EventItemsConverter;
import org.societies.simple.converters.PubsubItemConverter;
import org.societies.simple.converters.PubsubItemsConverter;
import org.societies.simple.converters.XMLGregorianCalendarConverter;
import org.xmpp.packet.IQ;
import org.xmpp.packet.IQ.Type;
import org.xmpp.packet.JID;
import org.xmpp.packet.Message;
import org.xmpp.packet.Packet;
import org.xmpp.packet.PacketError;

/**
 * @author Joao M. Goncalves (PTIN), Miquel Martin (NEC), Alec Leckey (Intel)
 * 
 *         TODO list 
 *         
 *         - this jaxb-dom4j conversion code is VERY BAD but i have to
 *         rush this; the propper solution would be to rewrite whack
 * 
 *         - it is single threaded and can be stuck by the synchronous calls to
 *         the extensions - should support "send and forget"
 * 
 *         - only supports one extension per namespace - the last to register
 *         sticks
 * 
 *         - dom4j parsing just sucks to use with jaxb - should cut dom4j out of
 *         it and have the packed handled in a lighter way
 * 
 *         - only supports one pojo per stanza - according to rfc6120: ok for IQ
 *         request/result processing; not ok for errors, messages and presence
 * 
 */

// TODO review this class
// TODO had to place synchronous blocks because marshallers are not threadsafe
public class CommManagerHelper {
	private static final String JABBER_CLIENT = "jabber:client";
	private static final String JABBER_SERVER = "jabber:server";

	private static Logger LOG = LoggerFactory
			.getLogger(CommManagerHelper.class);
	private SAXReader reader = new SAXReader(); // TODO the sax reader is not threadsafe either so I turned every method where it is used to synchronized :(

	private final Map<String, IFeatureServer> featureServers = new HashMap<String, IFeatureServer>();
	private final Map<String, ICommCallback> iqCommCallbacks = new HashMap<String, ICommCallback>();
	private final Map<String, ICommCallback> nsCommCallbacks = new HashMap<String, ICommCallback>();
	private final Map<String, String> nsToPackage = new HashMap<String, String>();
	
	private final Map<String, HostedNode> localToplevelNodes = new HashMap<String, HostedNode>();
	private final List<XMPPNode> allToplevelNodes = new ArrayList<XMPPNode>();

	private Serializer s;
	private ClassLoaderManager clm;
	
	public CommManagerHelper () {
		this.clm = new ClassLoaderManager();
		Registry registry = new Registry();
		Strategy strategy = new RegistryStrategy(registry);
		s = new Persister(strategy);
		try {
			registry.bind(com.sun.org.apache.xerces.internal.jaxp.datatype.XMLGregorianCalendarImpl.class, XMLGregorianCalendarConverter.class);
			registry.bind(java.net.URI.class,URIConverter.class);
			registry.bind(org.jabber.protocol.pubsub.event.Items.class, new EventItemsConverter(s));
			registry.bind(org.jabber.protocol.pubsub.Items.class, new PubsubItemsConverter(s));
			registry.bind(org.jabber.protocol.pubsub.Item.class, new PubsubItemConverter(s));
		} catch (Exception e) {
			LOG.error(e.getMessage(),e);
		}
	}
	
	public String[] getSupportedNamespaces() {
		String[] returnArray = new String[featureServers.size()];
		return featureServers.keySet().toArray(returnArray);
	}
	
	public synchronized IQ handleDiscoItems(IQ iq) {
		String node = null;
		Attribute nodeAttr = iq.getElement().attribute("node");
		if (nodeAttr!=null)
			node = nodeAttr.getText();
		
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			if (node==null) {
				// return top level nodes
				os.write(XMPPNode.ITEM_QUERY_RESPONSE_OPEN_BYTES);
				for (XMPPNode n : allToplevelNodes)
					os.write(n.getItemXmlBytes());
				os.write(XMPPNode.ITEM_QUERY_RESPONSE_CLOSE_BYTES);
			}
			else {
				// return specific nodes
				// check if some root-level node matches specified node
				HostedNode localNode = localToplevelNodes.get(node);
				// if not try to use node hierarchy to find speficied node
				if (localNode==null) {
					String[] nodePath = node.split("/");
					for (int i=0; i<nodePath.length; i++) {
						if (i==0)
							localNode = localToplevelNodes.get(nodePath[i]);
						else
							localNode = localNode.getLocalChild(nodePath[i]);
						if (localNode==null)
							break;
					}
				}
				
				os.write(localNode.getQueryXmlBytes());
				if (localNode!=null) {
					for (XMPPNode n : localNode.getChildren())
						os.write(n.getItemXmlBytes());
				}
				os.write(XMPPNode.ITEM_QUERY_RESPONSE_CLOSE_BYTES);
			}
		} catch (IOException e) {
			LOG.error(e.getMessage());
		}
		
		try {
			if (os.size()>0) {
				ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
				
				// return items
				IQ response = new IQ(Type.result, iq.getID());
				response.setTo(iq.getFrom());

				Document dom4jItems = reader.read(is);
				response.getElement().add(dom4jItems.getRootElement());
				return response;
			}
		} catch (DocumentException e) {
			LOG.error(e.getMessage());
			return buildErrorResponse(iq.getFrom(), iq.getID(), e.getMessage(), e);
		}
		
		// return empty answer
		iq.setTo(iq.getFrom());
		iq.setType(Type.result);
		iq.setFrom("");
		return iq;
	}
	
	public void addRootNode(XMPPNode newNode) {
		if (newNode instanceof HostedNode)
			localToplevelNodes.put(newNode.getNode(), (HostedNode)newNode);
		allToplevelNodes.add(newNode);
	}
	
	public void removeRootNode(XMPPNode node) {
		if (node instanceof HostedNode)
			localToplevelNodes.remove(((HostedNode)node).getNode());
		allToplevelNodes.remove(node);
	}

	private Object ifNotNull(Object o, String type, String which)
			throws UnavailableException {
		if (o == null) {
			throw new UnavailableException("Can not process " + type + ": "
					+ which);
		} else {
			return o;
		}
	}
	
	private String removeFragment(String namespace) {
		int cardinalIndex = namespace.indexOf("#");
		if (cardinalIndex>0)
			return namespace.substring(0,cardinalIndex);
		else
			return namespace;
	}

	private IFeatureServer getFeatureServer(String namespace)
			throws UnavailableException {
		return (IFeatureServer) ifNotNull(featureServers.get(removeFragment(namespace)),
				"namespace", namespace);
	}

	private ICommCallback getCommCallback(String iqId)
			throws UnavailableException {
		return (ICommCallback) ifNotNull(iqCommCallbacks.get(iqId),
				"IQ id", iqId);
	}
	
	private ICommCallback getMessageCommCallback(String namespace)
			throws UnavailableException {
		return (ICommCallback) ifNotNull(nsCommCallbacks.get(namespace),
				"namespace", namespace);
	}


	/** Retrieves a package from a namespace mapping
	 * @param namespace
	 * @return
	 * @throws UnavailableException
	 */
	private String getPackage(String namespace) throws UnavailableException {
		String p = nsToPackage.get(namespace);
		if (p==null)
			throw new UnavailableException("No namespace registered: "+namespace);
		return p;
	}

	public void dispatchIQResult(IQ iq) {
		Element element = getElementAny(iq);
		ClassLoader oldCl = null;
		try {
			ICommCallback callback = getCommCallback(iq.getID());
			oldCl = clm.classLoaderMagicTemp(iq.getID());
			
			// payloadless (confirmation) iqs
			if (element==null) {
				callback.receiveResult(TinderUtils.stanzaFromPacket(iq), null);
				return;
			}
			
			String ns = element.getNamespace().getURI();
			if (ns.equals(XMPPInfo.INFO_NAMESPACE)) {
				SimpleEntry<String, XMPPInfo> infoMap = ParsingUtils.parseInfoResult(element);
				callback.receiveInfo(TinderUtils.stanzaFromPacket(iq), infoMap.getKey(), infoMap.getValue());
				return;
			}
			if (ns.equals(XMPPNode.ITEM_NAMESPACE)) {
				SimpleEntry<String, List<String>> nodeMap = ParsingUtils.parseItemsResult(element);
				callback.receiveItems(TinderUtils.stanzaFromPacket(iq), nodeMap.getKey(), nodeMap.getValue());
				return;
			}
			
			Class<?> c = getClass(ns,element.getName());
			
			Object bean = s.read(c, element.asXML() );
			
			callback.receiveResult(TinderUtils.stanzaFromPacket(iq), bean);
		} catch (UnavailableException e) {
			LOG.error("Unable to find package for namespace",e);
		} catch (InvalidFormatException e) {
			LOG.error("Unable to convert Tinder Packet into Stanza", e);
		} catch (ClassNotFoundException e) {
			LOG.error("Unable to create class", e);
		} catch (Exception e) {
			LOG.error("Unable to serialise Simple element", e);
		}
		
		if (oldCl!=null)
			Thread.currentThread().setContextClassLoader(oldCl);
	}

	public void dispatchIQError(IQ iq) {
		ClassLoader oldCl = null;
		try {
			ICommCallback callback = getCommCallback(iq.getID());
			
			// http://xmpp.org/rfcs/rfc6120.html#stanzas-error-syntax
			Element errorElement = (Element)iq.getElement().elements().get(0);
			if (iq.getElement().elements().size()>1) {
				// handle [OPTIONAL to include sender XML here]
				// TODO currently ignoring
				errorElement = (Element)iq.getElement().elements().get(1);
			}
			// assumes the stanza error comes first in the error element as described the syntax
			String errorElementStr = ((Element)errorElement.elements().get(0)).getName(); 
			StanzaError se = StanzaError.valueOf(errorElementStr.replaceAll("-", "_").toLowerCase());
			XMPPError error = new XMPPError(se, null);
			
			// handle optional error fields: text and application error
//			[<text xmlns='urn:ietf:params:xml:ns:xmpp-stanzas'
//		           xml:lang='langcode'>
//		      OPTIONAL descriptive text
//		    </text>]
//		    [OPTIONAL application-specific condition element]
			oldCl = clm.classLoaderMagicTemp(iq.getID());
			if (errorElement.elements().size()>1)
				error = parseApplicationError(se, errorElement.elements());
			callback.receiveError(TinderUtils.stanzaFromPacket(iq),error);
		} catch (UnavailableException e) {
			LOG.error("Unable to find package for namespace",e);
		} catch (InvalidFormatException e) {
			LOG.error("Unable to convert Tinder Packet into Stanza", e);
		} catch (ClassNotFoundException e) {
			LOG.error("Unable to find class during Simple serialisation prep", e);
		}
		
		if (oldCl!=null)
			Thread.currentThread().setContextClassLoader(oldCl);
	}

	private XMPPError parseApplicationError(StanzaError error, List list) throws UnavailableException, ClassNotFoundException {
		Object appError = null;
		String text = "";
		
		for (Object o: list) {
			if (o instanceof Element) {
				Element e = (Element) o;
				if (e.getNamespaceURI().equals(XMPPError.STANZA_ERROR_NAMESPACE_DECL)) {
					if (e.getName().equals("text"))
						text = e.getText();
				} else {
					Class<?> c = getClass(e.getNamespaceURI(),e.getName());
					
					try {
						appError = s.read(c, e.asXML());
					} catch (Exception e1) {
						throw new UnavailableException(e1.getMessage());
					}
				}
			}
		}
		
		if (appError==null)
			return new XMPPError(error, text);
		else
			return new XMPPError(error, text, appError);
	}

	public IQ dispatchIQ(IQ iq) {
		Element element = getElementAny(iq);
		String namespace = element.getNamespace().getURI();
		JID originalFrom = iq.getFrom();
		String id = iq.getID();

		try {
			IFeatureServer fs = getFeatureServer(namespace);
			ClassLoader oldClassloader = clm.classLoaderMagic(fs);
			
			Class<?> c = getClass(namespace,element.getName());
			
			Object bean = s.read(c, element.asXML());
			
			Object responseBean = null;
			if (iq.getType().equals(IQ.Type.get))
				responseBean = fs.getQuery(TinderUtils.stanzaFromPacket(iq), bean);
			if (iq.getType().equals(IQ.Type.set))
				responseBean = fs.setQuery(TinderUtils.stanzaFromPacket(iq), bean);
			
			return buildResponseIQ(originalFrom, id, responseBean, oldClassloader);
		} catch (XMPPError e) {
			return buildApplicationErrorResponse(originalFrom, id, e);
		} catch (UnavailableException e) {
			return buildErrorResponse(originalFrom, id, e.getMessage(), e);
		} catch (DocumentException e) {
			String message = e.getClass().getName()
					+ "Error (un)marshalling the message:" + e.getMessage();
			return buildErrorResponse(originalFrom, id, message, e);
		} catch (InvalidFormatException e) {
			String message = e.getClass().getName()
					+ "Error (un)marshalling the message:" + e.getMessage();
			return buildErrorResponse(originalFrom, id, message, e);
		} catch (ClassNotFoundException e) {
			String message = e.getClass().getName() + ": Unable to create class for serialisation - " + e.getMessage();
			return buildErrorResponse(originalFrom, id, message, e);
		} catch (Exception e) {
			String message = e.getClass().getName() + "Unable to serialise Simple element"; 
			return buildErrorResponse(originalFrom, id, message, e);
		}
	}

	private Class<?> getClass(String namespace, String name) throws ClassNotFoundException, UnavailableException {
		String packageStr = getPackage(namespace);  
		String beanName = name.substring(0,1).toUpperCase() + name.substring(1);
		
		// TODO issue rasing
		try {
			clm.currentNewClassloader.loadClass(packageStr + "." + beanName);
		} catch (ClassNotFoundException e) {
			LOG.warn("### ClassLoader '"+clm.currentNewClassloader.toString()+"' could not load Class '"+packageStr + "." + beanName+"' ###",e);
		}
		
		return Thread.currentThread().getContextClassLoader().loadClass(packageStr + "." + beanName);
	}

	public void dispatchMessage(Message message) {
		Element element = getElementAny(message);
		String namespace = element.getNamespace().getURI();
		ClassLoader oldCl = null;
		try {
			try {
				ICommCallback cb = getMessageCommCallback(namespace);
				oldCl = clm.classLoaderMagic(cb);
			} catch (UnavailableException e) {
				IFeatureServer fs = getFeatureServer(namespace);
				oldCl = clm.classLoaderMagic(fs);
			}
			
			Class<?> c = getClass(namespace,element.getName());
			
			Object bean = s.read(c, element.asXML());
			
			try {
				ICommCallback cb = getMessageCommCallback(namespace);
				cb.receiveMessage(TinderUtils.stanzaFromPacket(message), bean);
			} catch (UnavailableException e) {
				IFeatureServer fs = getFeatureServer(namespace);
				fs.receiveMessage(TinderUtils.stanzaFromPacket(message), bean);
			}
		} catch (UnavailableException e) {
			LOG.error("Unable to find package or feature server for namespace",e);
		} catch (InvalidFormatException e) {
			LOG.error("Unable to convert Tinder Packet into Stanza", e);
		} catch (ClassNotFoundException e) {
			String m = e.getClass().getName() + "Error finding class:" + e.getMessage();
			LOG.error(m, e);
		} catch (Exception e) {
			String m = e.getClass().getName() + "Error de-serializing the message:" + e.getMessage();
			LOG.error(m, e);
		}
		
		if (oldCl!=null)
			Thread.currentThread().setContextClassLoader(oldCl);
	}

	public synchronized IQ sendIQ(Stanza stanza, IQ.Type type, Object payload, ICommCallback callback) 
			throws CommunicationException {
		try {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			
			s.write(payload, os);

			ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
			Document document = reader.read(is);
			IQ iq = TinderUtils.createIQ(stanza, type); // ???
			iq.getElement().add(document.getRootElement());
			iqCommCallbacks.put(iq.getID(), callback);
			clm.addTempClassloader(iq.getID(),payload); // or use the callback?
			return iq;
		} catch (ClassNotFoundException e) {
			throw new CommunicationException("ClassNotFoundException for class "+payload.getClass().toString(), e);
		} catch (Exception e) {
			throw new CommunicationException("Error sending IQ message", e);
		}
	}

	public synchronized Message sendMessage(Stanza stanza, Message.Type type, Object payload)
			throws CommunicationException {
		if (payload == null) {
			throw new InvalidParameterException("Payload can not be null");
		}
		try {
			ByteArrayOutputStream os = new ByteArrayOutputStream();

			s.write(payload, os);
			
			ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
			Document document = reader.read(is);
			Message message = TinderUtils.createMessage(stanza, type);
			message.getElement().add(document.getRootElement());
			return message;
		} catch (Exception e) {
			throw new CommunicationException("Error sending Message message", e);
		}
	}

	public void register(IFeatureServer fs) throws CommunicationException {
		jaxbMapping(fs.getXMLNamespaces(),fs.getJavaPackages());
		clm.classloaderRegistry(fs);
		for (String ns : fs.getXMLNamespaces()) {
			LOG.info("registering FeatureServer for namespace " + ns);
			featureServers.put(ns, fs);
		}
	}
	
	public void register(ICommCallback messageCallback) throws CommunicationException {
		jaxbMapping(messageCallback.getXMLNamespaces(), messageCallback.getJavaPackages());
		clm.classloaderRegistry(messageCallback);
		if (messageCallback.getXMLNamespaces().size()>0) {
			String mainNs = messageCallback.getXMLNamespaces().get(0);
			if (mainNs.indexOf("#")>-1) { // ICommCallback has priority only for the mainNs in case it has a fragment
				LOG.info("registering Callback for namespace " + mainNs);
				nsCommCallbacks.put(mainNs, messageCallback);
			}
		}
	}
	
	private void jaxbMapping(List<String> namespaces, List<String> packages) throws CommunicationException {
		// TODO latest namespace register sticks! no multiple namespace support atm
		//assumes a 1:1 mapping between NS and package (for prototyping)
		try {
			for (int i=0; i<packages.size(); i++) {
				String packageStr = packages.get(i);
				String nsStr = namespaces.get(i);
				LOG.info("registering namespace-package mapping: "+nsStr+" <-> "+packageStr);
				nsToPackage.put(nsStr, packageStr);
			}	
		}
		catch (Exception ex) {
			LOG.error("Error in JAXBMapping adding: " + ex.getMessage());
		}
	}

	/** Get the element with the payload out of the XMPP packet. */
	private Element getElementAny(Packet p) {
		if (p instanceof IQ) {
			// According to the schema in RCF6121 IQs only have one
			// element, unless they have an error
			switch (p.getElement().elements().size()) {
				case 0:
					return null;
				default:
					return (Element) p.getElement().elements().get(0);
				// TODO handle errors
			}
				
		} else if (p instanceof Message) {
			// according to the schema in RCF6121 messages have an unbounded
			// number
			// of "subject", "body" or "thread" elements before the any element
			// part
			Message message = (Message) p;
			for (Object o : message.getElement().elements()) {
				String ns = ((Element)o).getNamespace().getURI();
				if (!(ns.equals(JABBER_CLIENT) || ns.equals(JABBER_SERVER))) {
					return (Element) o;
				}
			}
			LOG.warn("Got a Message with no payload element.");
			return null;
		} else {
			LOG.warn("Got Packet type that I could not handle: "
					+ p.getClass().getName());
			return null;
		}
	}
	
	private synchronized IQ buildApplicationErrorResponse(JID originalFrom, String id, XMPPError error) {
		try {
			IQ errorResponse = new IQ(Type.error, id);
			errorResponse.setTo(originalFrom);
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			os.write(error.getStanzaErrorBytes(), 0, error.getStanzaErrorBytes().length);
			if (error.getApplicationError()!=null) {
				s.write(error.getApplicationError(), os);
			}
			os.write(XMPPError.CLOSE_ERROR_BYTES,0,XMPPError.CLOSE_ERROR_BYTES.length);
			ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
			
			Document dom4jError = reader.read(is);
			errorResponse.getElement().add(dom4jError.getRootElement());
			
			return errorResponse;
		} catch (DocumentException e) {
			return buildErrorResponse(originalFrom, id, "DocumentException while building application error", e);
		} catch (Exception e) {
			return buildErrorResponse(originalFrom, id, "Serializing Exception while building application error", e);
		}
	}

	private IQ buildErrorResponse(JID originalFrom, String id, String message, Exception e) {
		LOG.warn("Error occurred:" + message);
		IQ errorResponse = new IQ(Type.error, id);
		errorResponse.setTo(originalFrom);
		// default error
		PacketError error = new PacketError(PacketError.Condition.internal_server_error,PacketError.Type.cancel, message);
		if (e instanceof UnavailableException) // unrecognized namespace
			error = new PacketError(PacketError.Condition.service_unavailable, PacketError.Type.cancel);
		errorResponse.getElement().add(error.getElement());
		return errorResponse;
	}

	private synchronized IQ buildResponseIQ(JID originalFrom, String id, Object responseBean, ClassLoader oldClassloader)
			throws DocumentException {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		Document document = null;
		if (responseBean!=null) {
			try {
				s.write(responseBean, os);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
			document = reader.read(is);
		}

		if (oldClassloader!=null)
			Thread.currentThread().setContextClassLoader(oldClassloader);
		
		IQ responseIq = new IQ(Type.result, id);
		responseIq.setTo(originalFrom);
		if (document!=null)
			responseIq.getElement().add(document.getRootElement());
		
		return responseIq;
	}

	class UnavailableException extends Exception {
		private static final long serialVersionUID = -7976036541747605416L;

		public UnavailableException(String message) {
			super(message);
		}
	}

	public synchronized IQ buildInfoIq(IIdentity entity, String node, ICommCallback callback) throws CommunicationException {
		IQ infoIq = new IQ(Type.get);
		infoIq.setTo(entity.getJid());
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			os.write(ParsingUtils.getInfoQueryRequestBytes(node));
			ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
			Document document = reader.read(is);
			infoIq.getElement().add(document.getRootElement());
		} catch (IOException e) {
			throw new CommunicationException("Error building disco#info request", e);
		} catch (DocumentException e) {
			throw new CommunicationException("Error building disco#info request", e);
		}
		iqCommCallbacks.put(infoIq.getID(), callback);
		return infoIq;
	}

	public synchronized IQ buildItemsIq(IIdentity entity, String node, ICommCallback callback) throws CommunicationException {
		IQ itemsIq = new IQ(Type.get);
		itemsIq.setTo(entity.getJid());
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			os.write(ParsingUtils.getItemsQueryRequestBytes(node));
			ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
			Document document = reader.read(is);
			itemsIq.getElement().add(document.getRootElement());
		} catch (IOException e) {
			throw new CommunicationException("Error building disco#items request", e);
		} catch (DocumentException e) {
			throw new CommunicationException("Error building disco#items request", e);
		}
		iqCommCallbacks.put(itemsIq.getID(), callback);
		return itemsIq;
	}	
}
