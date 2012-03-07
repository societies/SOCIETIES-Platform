package org.societies.comm.xmpp.client.impl;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.AbstractMap.SimpleEntry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import jaxb.JAXBContext;
import jaxb.JAXBException;
import jaxb.Marshaller;
import jaxb.Unmarshaller;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.util.PacketParserUtils;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.comm.android.ipc.utils.MarshallUtils;
import org.societies.impl.RawXmlProvider;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public class PacketMarshaller {	
	
	private Map<String, Marshaller> pkgToMarshaller = new HashMap<String, Marshaller>();
	private Map<String, Unmarshaller> nsToUnmarshaller = new HashMap<String, Unmarshaller>();
	
	public void register(List<String> elementNames, List<String> namespaces, List<String> packages) {
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
						
			RawXmlProvider rawXmlProvider = new RawXmlProvider();
			for(String elementName:elementNames)
				for(String namespace:namespaces)
					ProviderManager.getInstance().addExtensionProvider(elementName, namespace, rawXmlProvider);
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		}
	}
	
	public String marshallMessage(Stanza stanza, Message.Type type, Object payload) throws Exception {
		final String xml = marshallPayload(payload);
		Message message = new Message();
		if(stanza.getId() != null)
			message.setPacketID(stanza.getId());
		if(stanza.getFrom() != null)
			message.setFrom(stanza.getFrom().getJid());
		if(type != null)                        
			message.setType(type);
		message.setTo(stanza.getTo().getJid());
		message.addExtension(new PacketExtension() {
			public String getElementName() {				
				return null;
			}
			public String getNamespace() {			
				return null;
			}
			public String toXML() {
				return xml;
			}			
		});
		return message.toXML();
	}
	
	public String marshallIQ(Stanza stanza, IQ.Type type, Object payload) throws Exception {				
		final String xml = marshallPayload(payload);
		IQ iq = new IQ() {
			@Override
			public String getChildElementXML() {
				return xml;
			}			
		};
		if(stanza.getId() != null)
			iq.setPacketID(stanza.getId());
		if(stanza.getFrom() != null)
			iq.setFrom(stanza.getFrom().getJid());
		iq.setTo(stanza.getTo().getJid());
		iq.setType(type);	
		return iq.toXML();
	}
	
	public IQ unmarshallIq(String xml) throws Exception {
		return parseIq(createXmlPullParser(xml));
	}
	
	public Message unmarshallMessage(String xml) throws Exception {			
	    return (Message)PacketParserUtils.parseMessage(createXmlPullParser(xml));
	}
	
	public Object unmarshallPayload(Packet packet) throws Exception {
		Element element = getElementAny(packet);
		
		if(element == null) // Empty stanza
			return null;
		
		String namespace = element.lookupNamespaceURI(element.getPrefix());
		Unmarshaller u = getUnmarshaller(namespace);

		String xml = MarshallUtils.nodeToString(element);
		
		Object payload = u.unmarshal(new InputSource(new StringReader(xml)));

		return payload;
	}
	
	public SimpleEntry<String, List<String>> parseItemsResult(Packet packet) throws SAXException, IOException, ParserConfigurationException {
		Element element = getElementAny(packet);
		String node = element.getAttribute("node");
		List<String> list = new ArrayList<String>();
		NodeList childs = element.getChildNodes();
		for(int i=0; i<childs.getLength(); i++) {
			Node child = childs.item(i);
			if(child instanceof Element) {
				Element childElement = (Element)child;
				list.add(childElement.getAttribute("node"));
			}
			
		}
		return new SimpleEntry<String, List<String>>(node, list);
	}
	
	private String marshallPayload(Object payload) throws JAXBException, ParserConfigurationException, TransformerException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.newDocument();
		Marshaller m = getMarshaller(payload);
		m.marshal(payload, doc);
		return MarshallUtils.nodeToString(doc);
	}
	
	private Marshaller getMarshaller(Object payload) {
		return pkgToMarshaller.get(payload.getClass().getPackage().getName());	
	}
	
	private Unmarshaller getUnmarshaller(String namespace) {
		return nsToUnmarshaller.get(namespace);
	}
	
	/** Get the element with the payload out of the XMPP packet. */
	private Element getElementAny(Packet packet) throws SAXException, IOException, ParserConfigurationException {
		if (packet instanceof IQ) {
			// According to the schema in RCF6121 IQs only have one
			// element, unless they have an error
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
			return (Element) factory.newDocumentBuilder().parse(new InputSource(new StringReader(packet.toXML()))).getDocumentElement().getFirstChild();
		} else if (packet instanceof Message) {
			// according to the schema in RCF6121 messages have an unbounded
			// number
			// of "subject", "body" or "thread" elements before the any element
			// part
			
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
			Element element = factory.newDocumentBuilder().parse(new InputSource(new StringReader(packet.toXML()))).getDocumentElement();
			
			NodeList childs = element.getChildNodes();
			for(int i=0; i<childs.getLength(); i++) {
				Node child = childs.item(i);
				if(child instanceof Element) {
					Element childElem = (Element)child;						
					String namespace = childElem.lookupNamespaceURI(childElem.getPrefix());
					if(!namespace.equals("jabber:client") && !namespace.equals("jabber:server"))
						return childElem;
				}
			}
			throw new RuntimeException("Got a Message with no payload element.");
		} else {
			throw new RuntimeException("Got Packet type that I could not handle: "
					+ packet.getClass().getName());
		}
	}
	
	private XmlPullParser createXmlPullParser(String xml) throws XmlPullParserException, IOException {
		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
	    factory.setNamespaceAware(true);
	    XmlPullParser parser = factory.newPullParser();
	    parser.setInput(new StringReader(xml));
	    parser.next();
	    return parser;
	}
	
	private IQ parseIq(XmlPullParser parser) throws Exception {
		IQ iqPacket = null;
		String id = parser.getAttributeValue("", "id");
		String to = parser.getAttributeValue("", "to");
		String from = parser.getAttributeValue("", "from");
		IQ.Type type = IQ.Type.fromString(parser.getAttributeValue("", "type"));

		boolean done = false;
		while (!done) {
			int eventType = parser.next();
			if (eventType == XmlPullParser.START_TAG) {
				iqPacket = new RawXmlProvider().parseIQ(parser);
			}
			else if (eventType == XmlPullParser.END_TAG) {
				if (parser.getName().equals("iq")) {
					done = true;
				}
			}
		}
		
		if(iqPacket == null) {
			iqPacket = new IQ() {
				public String getChildElementXML() {
					return "";
				}
			};
		}

        // Set basic values on the iq packet.
        iqPacket.setPacketID(id);
        iqPacket.setTo(to);
        iqPacket.setFrom(from);
        iqPacket.setType(type);

        return iqPacket;    
	}
	
}
