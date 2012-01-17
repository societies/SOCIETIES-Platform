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
import java.io.StringReader;
import java.lang.reflect.Method;
import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.stream.XMLStreamException;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.comm.xmpp.datatypes.Stanza;
import org.societies.comm.xmpp.datatypes.XMPPError;
import org.societies.comm.xmpp.datatypes.XMPPError.StanzaError;
import org.societies.comm.xmpp.exceptions.CommunicationException;
import org.societies.comm.xmpp.interfaces.CommCallback;
import org.societies.comm.xmpp.interfaces.FeatureServer;
import org.xml.sax.InputSource;
import org.xmpp.packet.IQ;
import org.xmpp.packet.IQ.Type;
import org.xmpp.packet.JID;
import org.xmpp.packet.Message;
import org.xmpp.packet.Packet;
import org.xmpp.packet.PacketError;

/**
 * @author Joao M. Goncalves (PTIN), Miquel Martin (NEC)
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

public class CommManagerHelper {
	private static final String JABBER_CLIENT = "jabber:client";
	private static final String JABBER_SERVER = "jabber:server";

	private static Logger LOG = LoggerFactory
			.getLogger(CommManagerHelper.class);
	private SAXReader reader = new SAXReader();

	private final Map<String, FeatureServer> featureServers = new HashMap<String, FeatureServer>();
	private final Map<String, CommCallback> commCallbacks = new HashMap<String, CommCallback>();
	private final Map<String, Unmarshaller> nsToUnmarshaller = new HashMap<String, Unmarshaller>();
	private final Map<String, Marshaller> pkgToMarshaller = new HashMap<String, Marshaller>();

	public String[] getSupportedNamespaces() {
		String[] returnArray = new String[featureServers.size()];
		return featureServers.keySet().toArray(returnArray);
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

	private FeatureServer getFeatureServer(String namespace)
			throws UnavailableException {
		return (FeatureServer) ifNotNull(featureServers.get(namespace),
				"namespace", namespace);
	}

	private CommCallback getCommCallback(String namespace)
			throws UnavailableException {
		return (CommCallback) ifNotNull(commCallbacks.get(namespace),
				"namespace", namespace);
	}

	private Unmarshaller getUnmarshaller(String namespace)
			throws UnavailableException {
		return (Unmarshaller) ifNotNull(nsToUnmarshaller.get(namespace),
				"namespace", namespace);
	}

	private Marshaller getMarshaller(Package pkg) throws UnavailableException {
		return (Marshaller) ifNotNull(pkgToMarshaller.get(pkg.getName()),
				"package", pkg.getName());
	}

	public void dispatchIQResult(IQ iq) {
		Element element = getElementAny(iq);
		try {
			CommCallback callback = getCommCallback(iq.getID());
			Unmarshaller u = getUnmarshaller(element.getNamespace().toString());
			Object bean = u.unmarshal(new InputSource(new StringReader(element
					.asXML())));
			callback.receiveResult(Stanza.fromPacket(iq), bean);
		} catch (JAXBException e) {
			LOG.info("JAXB error unmarshalling an IQ result", e);
		} catch (UnavailableException e) {
			LOG.info(e.getMessage());
		}
	}

	public void dispatchIQError(IQ iq) {
		try {
			CommCallback callback = getCommCallback(iq.getID());
			callback.receiveError(Stanza.fromPacket(iq));
		} catch (UnavailableException e) {
			LOG.info(e.getMessage());
		}
	}

	public IQ dispatchIQ(IQ iq) {
		Element element = getElementAny(iq);
		String namespace = element.getNamespace().getURI();
		JID originalFrom = iq.getFrom();
		String id = iq.getID();

		try {
			FeatureServer fs = getFeatureServer(namespace);
			Unmarshaller u = getUnmarshaller(namespace);
			Object bean = u.unmarshal(new InputSource(new StringReader(element
					.asXML())));
			Object responseBean = fs.receiveQuery(Stanza.fromPacket(iq), bean);
			if (responseBean!=null && responseBean instanceof XMPPError)
				return buildApplicationErrorResponse(originalFrom, id, (XMPPError)responseBean);
			else
				return buildResponseIQ(originalFrom, id, responseBean);
		} catch (UnavailableException e) {
			LOG.info(e.getMessage());
			return buildErrorResponse(originalFrom, id, e.getMessage());
		} catch (JAXBException e) {
			String message = e.getClass().getName()
					+ "Error (un)marshalling the message:" + e.getMessage();
			LOG.info(message);
			return buildErrorResponse(originalFrom, id, message);
		} catch (XMLStreamException e) {
			String message = e.getClass().getName()
					+ "Error (un)marshalling the message:" + e.getMessage();
			LOG.info(message);
			return buildErrorResponse(originalFrom, id, message);
		} catch (DocumentException e) {
			String message = e.getClass().getName()
					+ "Error (un)marshalling the message:" + e.getMessage();
			LOG.info(message);
			return buildErrorResponse(originalFrom, id, message);
		}
	}

	public void dispatchMessage(Message message) {
		Element element = getElementAny(message);
		try {
			FeatureServer fs = getFeatureServer(element.getNamespace()
					.toString());
			Unmarshaller u = getUnmarshaller(element.getNamespace().toString());
			Object bean = u.unmarshal(new InputSource(new StringReader(element
					.asXML())));
			fs.receiveMessage(Stanza.fromPacket(message), bean);
		} catch (JAXBException e) {
			String m = e.getClass().getName()
					+ "Error unmarshalling the message:" + e.getMessage();
			LOG.info(m);
		} catch (UnavailableException e) {
			LOG.info(e.getMessage());
		}
	}

	public void sendIQ(Stanza stanza, IQ.Type type, Object payload,
			CommCallback callback) throws CommunicationException {
		// Usual disclaimer about how this needs to be optimized ;)
		try {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			Marshaller m = getMarshaller(payload.getClass().getPackage());
			m.marshal(payload, os);

			ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
			Document document = reader.read(is);
			IQ iq = stanza.createIQ(type);
			iq.getElement().add(document.getRootElement());
			commCallbacks.put(iq.getID(), callback);
		} catch (Exception e) {
			throw new CommunicationException("Error sending IQ message", e);
		}
	}

	public void sendMessage(Stanza stanza, Message.Type type, Object payload)
			throws CommunicationException {
		if (payload == null) {
			throw new InvalidParameterException("Payload can not be null");
		}
		try {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			Marshaller m = getMarshaller(payload.getClass().getPackage());
			m.marshal(payload, os);

			ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
			Message message = stanza.createMessage(type);
			Document document = reader.read(is);
			message.getElement().add(document.getRootElement());
		} catch (Exception e) {
			throw new CommunicationException("Error sending Message message", e);
		}
	}

	public void register(FeatureServer fs) throws CommunicationException,
			ClassNotFoundException {
		// TODO latest namespace register sticks! no multiple namespace support atm
		StringBuilder contextPath = new StringBuilder(fs.getJavaPackages().get(0));
		for (int i = 1; i < fs.getJavaPackages().size(); i++)
			contextPath.append(":" + fs.getJavaPackages().get(i));

		try {
			JAXBContext jc = JAXBContext.newInstance(contextPath.toString(),
					this.getClass().getClassLoader());
			Unmarshaller u = jc.createUnmarshaller();
			Marshaller m = jc.createMarshaller();
			
			for (String ns : fs.getXMLNamespaces()) {
				LOG.info("registering " + ns);
				featureServers.put(ns, fs);
				nsToUnmarshaller.put(ns, u);
			}

			for (String packageStr : fs.getJavaPackages())
				pkgToMarshaller.put(packageStr, m);
			
		} catch (JAXBException e) {
			throw new CommunicationException(
					"Could not register NamespaceExtension... caused by JAXBException: ",
					e);
		}
	}

	/** Get the element with the payload out of the XMPP packet. */
	private Element getElementAny(Packet p) {
		if (p instanceof IQ) {
			// According to the schema in RCF6121 IQs only have one
			// element, unless they have an error
			return (Element) p.getElement().elements().get(0);
		} else if (p instanceof Message) {
			// according to the schema in RCF6121 messages have an unbounded
			// number
			// of "subject", "body" or "thread" elements before the any element
			// part
			Message message = (Message) p;
			for (Object o : message.getElement().elements()) {
				Namespace ns = ((Element) o).getNamespace();
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
	
	private IQ buildApplicationErrorResponse(JID originalFrom, String id, XMPPError error) throws XMLStreamException, JAXBException, UnavailableException, DocumentException {
		IQ errorResponse = new IQ(Type.error, id);
		errorResponse.setTo(originalFrom);
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		os.write(error.getStanzaErrorBytes(), 0, error.getStanzaErrorBytes().length);
		if (error.getApplicationError()!=null) {
			InlineNamespaceXMLStreamWriter inxsw = new InlineNamespaceXMLStreamWriter(os);
			getMarshaller(error.getApplicationError().getClass().getPackage()).marshal(error.getApplicationError(), inxsw);
		}
		os.write(XMPPError.CLOSE_ERROR_BYTES,0,XMPPError.CLOSE_ERROR_BYTES.length);
		ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
		Document dom4jError = reader.read(is);
		errorResponse.getElement().add(dom4jError.getRootElement());
		return errorResponse;
	}

	private IQ buildErrorResponse(JID originalFrom, String id, String message) {
		LOG.info("Error occurred:" + message);
		IQ errorResponse = new IQ(Type.error, id);
		errorResponse.setTo(originalFrom);
		PacketError error = new PacketError(
				PacketError.Condition.internal_server_error,
				PacketError.Type.cancel, message);
		errorResponse.getElement().add(error.getElement());
		return errorResponse;
	}

	private IQ buildResponseIQ(JID originalFrom, String id, Object responseBean)
			throws JAXBException, DocumentException, UnavailableException, XMLStreamException {
		IQ responseIq = new IQ(Type.result, id);
		responseIq.setTo(originalFrom);
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		InlineNamespaceXMLStreamWriter inxsw = new InlineNamespaceXMLStreamWriter(os);
		if (responseBean!=null) {
			getMarshaller(responseBean.getClass().getPackage()).marshal(responseBean, inxsw);
			ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
			Document document = reader.read(is);
			responseIq.getElement().add(document.getRootElement());
		}
		return responseIq;
	}

	class UnavailableException extends Exception {
		private static final long serialVersionUID = -7976036541747605416L;

		public UnavailableException(String message) {
			super(message);
		}
	}
}
