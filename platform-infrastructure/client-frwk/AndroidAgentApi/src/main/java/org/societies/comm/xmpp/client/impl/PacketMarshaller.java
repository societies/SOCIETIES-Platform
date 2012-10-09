package org.societies.comm.xmpp.client.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.util.PacketParserUtils;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.convert.Registry;
import org.simpleframework.xml.convert.RegistryStrategy;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.strategy.Strategy;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.datatypes.StanzaError;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.comm.android.ipc.utils.MarshallUtils;
import org.societies.impl.RawXmlProvider;
import org.societies.maven.converters.URIConverter;
import org.societies.simple.converters.EventItemsConverter;
import org.societies.simple.converters.PubsubItemConverter;
import org.societies.simple.converters.PubsubItemsConverter;
import org.societies.simple.converters.XMLGregorianCalendarConverter;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.util.Log;

public class PacketMarshaller {	
	
	private static final String LOG_TAG = PacketMarshaller.class.getName();
	private final Map<String, String> nsToPackage = new HashMap<String, String>();
	private Serializer s;
	
	public PacketMarshaller() {
//		Log.d(LOG_TAG, "Constructor");
		Registry registry = new Registry();
		Strategy strategy = new RegistryStrategy(registry);
		try {
			Element exampleElementImpl = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument().createElement("dummy");
			registry.bind(exampleElementImpl.getClass(), ElementConverter.class);
			Log.i(LOG_TAG, "Registered class '"+exampleElementImpl.getClass()+"' as ElementImpl");
			
			// http://stackoverflow.com/questions/7918466/cant-use-xmlgregoriancalendar-in-android-even-if-it-is-documented
//			XMLGregorianCalendar gregorianCalendarImpl = DatatypeFactory.newInstance().newXMLGregorianCalendar();
//			registry.bind(gregorianCalendarImpl.getClass(), XMLGregorianCalendarConverter.class);
//			Log.i(LOG_TAG, "Registered class '"+gregorianCalendarImpl.getClass()+"' as XMLGregorianCalendarImpl");
			
			registry.bind(java.net.URI.class,URIConverter.class);
			registry.bind(org.jabber.protocol.pubsub.event.Items.class, new EventItemsConverter(s));
			registry.bind(org.jabber.protocol.pubsub.Items.class, new PubsubItemsConverter(s));
			registry.bind(org.jabber.protocol.pubsub.Item.class, new PubsubItemConverter(s));
		} catch (DOMException e) {
			Log.e(LOG_TAG, "DOMException trying to get runtime ElementImpl");
		} catch (ParserConfigurationException e) {
			Log.e(LOG_TAG, "ParserConfigurationException trying to get runtime ElementImpl");
		} catch (Exception e) {
			Log.e(LOG_TAG, "Exception trying to register runtime ElementImpl or XMLGregorianCalendarImpl in SimpleXML",e);
		}
		
		s = new Persister(strategy);
	}
	
	public void register(List<String> elementNames, List<String> namespaces, List<String> packages) {
//		Log.d(LOG_TAG, "register");
		for (String element : elementNames) {
//			Log.d(LOG_TAG, "register element: " + element);
		}
		
		try {
			//TODO: SIMPLE XML
			for (int i=0; i<packages.size(); i++) {
				String packageStr = packages.get(i);
				String nsStr = namespaces.get(i); 
				nsToPackage.put(nsStr, packageStr);
			}
				
			RawXmlProvider rawXmlProvider = new RawXmlProvider();
			for(String elementName:elementNames)
				for(String namespace:namespaces)
					ProviderManager.getInstance().addExtensionProvider(elementName, namespace, rawXmlProvider);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public String marshallMessage(Stanza stanza, Message.Type type, Object payload) throws Exception {
//		Log.d(LOG_TAG, "marshallMessage type: " + type.name() + "stanza from : " + stanza.getFrom() + " to: " + stanza.getTo());
		
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
//		Log.d(LOG_TAG, "marshallIQ type: " + type + "stanza from : " + stanza.getFrom() + " to: " + stanza.getTo());
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
//		Log.d(LOG_TAG, "unmarshallIq xml: " + xml);
		
		return parseIq(createXmlPullParser(xml));
	}
	
	public Message unmarshallMessage(String xml) throws Exception {			
//		Log.d(LOG_TAG, "unmarshallMessage + xml: " + xml);
		
	    return (Message)PacketParserUtils.parseMessage(createXmlPullParser(xml));
	}
	
	public Object unmarshallPayload(Packet packet) throws Exception {
//		Log.d(LOG_TAG, "unmarshallPayload packet: " + packet.getPacketID());
		Element element = getElementAny(packet);
		
		if(element == null) // Empty stanza
			return null;
		
		String namespace = element.lookupNamespaceURI(element.getPrefix());
		String xml = MarshallUtils.nodeToString(element);
//		Log.d(PacketMarshaller.class.getName() + " ### ", xml);
		
		//GET CLASS FIRST
		String packageStr = nsToPackage.get(namespace);  
		String beanName = element.getLocalName().substring(0,1).toUpperCase() + element.getLocalName().substring(1); //NEEDS TO BE "CalcBean", not "calcBean"
//		Log.d(PacketMarshaller.class.getName(), "Trying to unmarshall: " + packageStr + "." + beanName);
		Class<?> c = Class.forName(packageStr + "." + beanName);
		
		Object payload = s.read(c, xml);
		
		return payload;
	}
	
	public Entry<String, List<String>> parseItemsResult(Packet packet) throws SAXException, IOException, ParserConfigurationException {
//		Log.d(LOG_TAG, "Entry");
		Element element = getElementAny(packet);
		final String node = element.getAttribute("node");
		final List<String> list = new ArrayList<String>();
		NodeList childs = element.getChildNodes();
		for(int i=0; i<childs.getLength(); i++) {
			Node child = childs.item(i);
			if(child instanceof Element) {
				Element childElement = (Element)child;
				list.add(childElement.getAttribute("node"));
			}
			
		}
		return new Entry<String, List<String>>() {
			public String getKey() {
				return node;
			}
			public List<String> getValue() {
				return list;
			}
			public List<String> setValue(List<String> value) {
				throw new UnsupportedOperationException("Immutable object.");
			}			
		};
	}
	
	public XMPPError unmarshallError(Packet packet) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		Element element = (Element) factory.newDocumentBuilder().parse(new InputSource(new StringReader(packet.toXML()))).getDocumentElement().getFirstChild();
		
		Element errorElement = ((Element)element.getElementsByTagName("error").item(0));
		String errorElementName = firstElement(errorElement.getChildNodes()).getTagName(); // TODO assumes the stanza error comes first
	
		StanzaError stanzaError = StanzaError.valueOf(errorElementName.replaceAll("-", "_"));
		return new XMPPError(stanzaError, null); // TODO parse application error
	}
	
	private String marshallPayload(Object payload) {
//		Log.d(LOG_TAG, "marshallPayload payload: " + payload.getClass().getName());
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		
		try {
			s.write(payload, os);
		} catch (Exception e) {
			e.printStackTrace();
		}
//		Log.d(PacketMarshaller.class.getName() + " ### ", os.toString());
		return os.toString();
	}
	
	/** Get the element with the payload out of the XMPP packet. 
	 * @throws ParserConfigurationException */
	private Element getElementAny(Packet packet) throws SAXException, IOException, ParserConfigurationException {
//		Log.d(LOG_TAG, "getElementAny packet: " + packet.getPacketID());
		
		if (packet instanceof IQ) {
			// According to the schema in RCF6121 IQs only have one
			// element, unless they have an error
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
			return (Element) factory.newDocumentBuilder().parse(new InputSource(new StringReader(packet.toXML()))).getDocumentElement().getFirstChild();
		} 
		else if (packet instanceof Message) {
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
//		Log.d(LOG_TAG, "createXmlPullParser xml: " + xml);
		
		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
	    factory.setNamespaceAware(true);
	    XmlPullParser parser = factory.newPullParser();
	    parser.setInput(new StringReader(xml));
	    parser.next();
	    return parser;
	}
	
	private IQ parseIq(XmlPullParser parser) throws Exception {
//		Log.d(LOG_TAG, "parseIq");
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
	
	private Element firstElement(NodeList nodes) {
		for(int i=0; i<nodes.getLength(); i++)
			if(nodes.item(i) instanceof Element)
				return (Element)nodes.item(i);
		throw new IllegalArgumentException("There is no Element in the given node list.");
	}
	
}
