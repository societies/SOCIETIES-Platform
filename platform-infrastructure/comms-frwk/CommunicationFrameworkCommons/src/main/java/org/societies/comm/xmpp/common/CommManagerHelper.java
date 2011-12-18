package org.societies.comm.xmpp.common;

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

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.comm.xmpp.datatypes.Stanza;
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
 * TODO this jaxb-dom4j conversion code is VERY BAD // but i have to rush this;
 * the propper solution would be to // rewrite whack
 * 
 * @author miquel
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
	private final Map<String, Unmarshaller> unmarshallers = new HashMap<String, Unmarshaller>();
	private final Map<Class<?>, Marshaller> marshallers = new HashMap<Class<?>, Marshaller>();

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
		return (Unmarshaller) ifNotNull(unmarshallers.get(namespace),
				"namespace", namespace);
	}

	private Marshaller getMarshaller(Class<?> clazz)
			throws UnavailableException {
		return (Marshaller) ifNotNull(marshallers.get(clazz), "class",
				clazz.getName());
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
			return buildResponseIQ(originalFrom, id, responseBean);
		} catch (UnavailableException e) {
			LOG.info(e.getMessage());
			return buildErrorResponse(originalFrom, id, e.getMessage());
		} catch (Exception e) {
			String message = e.getClass().getName()
					+ "Error unmarshalling the message:" + e.getMessage();
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
			Marshaller m = getMarshaller(payload.getClass());
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
			Marshaller m = getMarshaller(payload.getClass());
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
		LOG.info("Registering " + fs.getXMLNamespace());
		try {
			featureServers.put(fs.getXMLNamespace(), fs);
			JAXBContext jc = JAXBContext.newInstance(fs.getJavaPackage(), this
					.getClass().getClassLoader());
			unmarshallers.put(fs.getXMLNamespace(), jc.createUnmarshaller());

			Marshaller marshaller = jc.createMarshaller();
			Class<?> objFactory = Class.forName(fs.getJavaPackage()
					+ ".ObjectFactory");
			for (Method m : objFactory.getMethods()) {
				if (m.getName().startsWith("create")) {
					XmlRootElement re = m.getReturnType().getAnnotation(
							XmlRootElement.class);
					if (re != null) {
						marshallers.put(m.getReturnType(), marshaller);
					}
				}
			}
		} catch (JAXBException e) {
			throw new CommunicationException(
					"Could not register FeatureServer", e);
		}
	}

	/** Get the element with the payload out of the XMPP packet. */
	private Element getElementAny(Packet p) {
		if (p instanceof IQ) {
			// According to the schema in RCF3921 IQs only have one
			// element, unless they have an error
			return (Element) p.getElement().elements().get(0);
		} else if (p instanceof Message) {
			// according to the schema in RCF3921 messages have an unbounded
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

	private IQ buildErrorResponse(JID originalFrom, String id, String message) {
		LOG.info("Error occurred:" + message);
		IQ errorResponse = new IQ(Type.error, id);
		errorResponse.setTo(originalFrom);
		PacketError error = new PacketError(
				PacketError.Condition.service_unavailable,
				PacketError.Type.cancel, message);
		errorResponse.getElement().add(error.getElement());
		return errorResponse;
	}

	private IQ buildResponseIQ(JID originalFrom, String id, Object responseBean)
			throws JAXBException, DocumentException, UnavailableException {
		IQ responseIq = new IQ(Type.result, id);
		responseIq.setTo(originalFrom);
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		getMarshaller(responseBean.getClass()).marshal(responseBean, os);
		ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
		Document document = reader.read(is);
		responseIq.getElement().add(document.getRootElement());
		return responseIq;
	}

	class UnavailableException extends Exception {
		private static final long serialVersionUID = -7976036541747605416L;

		public UnavailableException(String message) {
			super(message);
		}
	}
}
