package org.societies.comm.xmpp.client.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.xml.sax.InputSource;
import org.xmpp.packet.IQ;
import org.xmpp.packet.Message;
import org.xmpp.packet.Message.Type;
import org.xmpp.packet.Packet;

public class PacketMarshaller {	
	
	private SAXReader reader = new SAXReader();
	
	private Map<String, Marshaller> pkgToMarshaller = new HashMap<String, Marshaller>();
	private Map<String, Unmarshaller> nsToUnmarshaller = new HashMap<String, Unmarshaller>();
	
	public void register(List<String> namespaces, List<String> packages) {
		try {
			StringBuilder contextPath = new StringBuilder(packages.get(0));
			for (int i = 1; i < packages.size(); i++)
				contextPath.append(":" + packages.get(i));
			
			JAXBContext jc = JAXBContext.newInstance(contextPath.toString(),
					this.getClass().getClassLoader());
			Unmarshaller u = jc.createUnmarshaller();
			Marshaller m = jc.createMarshaller();
			
			for (String ns : namespaces) {
				nsToUnmarshaller.put(ns, u);
			}

			for (String packageStr : packages) {
				pkgToMarshaller.put(packageStr, m);
			}
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		}
	}
	
	public String marshallMessage(Stanza stanza, Type type, Object payload) throws Exception {
		Marshaller m = getMarshaller(payload);
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		m.marshal(payload, os);					
		ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());		
		Document document = reader.read(is);
		Message message = new Message();
		if(type != null)                        
			message.setType(type);
		message.setTo(stanza.getTo().getJid());
		message.getElement().add(document.getRootElement());
		return message.toXML();
	}
	
	public String marshallIQ(Stanza stanza, IQ.Type type, Object payload) throws Exception {				
		Marshaller m = getMarshaller(payload);
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		m.marshal(payload, os);
		ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());			
		Document document = reader.read(is);
		IQ iq = new IQ();
		iq.setTo(stanza.getTo().getJid());
		iq.setType(type);
		iq.getElement().add(document.getRootElement());
		return iq.toXML();
	}
	
	public IQ unmarshallIq(String xml) throws Exception {	
		return new IQ(DocumentHelper.parseText(xml).getRootElement());
	}
	
	public Message unmarshallMessage(String xml) throws Exception {	
		return new Message(DocumentHelper.parseText(xml).getRootElement());
	}
	
	public Object unmarshallPayload(Packet packet) throws Exception {
		Element element = getElementAny(packet);
		String namespace = element.getNamespace().getURI();
		Unmarshaller u = getUnmarshaller(namespace);

		String xml = element.asXML();
		
		Object payload = u.unmarshal(new InputSource(new StringReader(xml)));

		return payload;
	}
	
	private Marshaller getMarshaller(Object payload) {
		return pkgToMarshaller.get(payload.getClass().getPackage().getName());	
	}
	
	private Unmarshaller getUnmarshaller(String namespace) {
		return nsToUnmarshaller.get(namespace);
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
				String ns = ((Element)o).getNamespace().getURI();
				if (!(ns.equals("jabber:client") || ns.equals("jabber:server"))) {
					return (Element) o;
				}
			}
			throw new RuntimeException("Got a Message with no payload element.");
		} else {
			throw new RuntimeException("Got Packet type that I could not handle: "
					+ p.getClass().getName());
		}
	}
	
}
