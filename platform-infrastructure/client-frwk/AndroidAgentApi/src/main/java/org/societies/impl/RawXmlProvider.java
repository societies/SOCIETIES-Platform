package org.societies.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.provider.IQProvider;
import org.jivesoftware.smack.provider.PacketExtensionProvider;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import android.util.Log;

public class RawXmlProvider implements IQProvider, PacketExtensionProvider {

	private static final String LOG_TAG = RawXmlProvider.class.getName();

	public IQ parseIQ(XmlPullParser parser) throws Exception {
		Log.d(LOG_TAG, "parseIQ");
		final XmlElement element = parseRootElement(parser);
				
		return new IQ() {
			@Override
			public String getChildElementXML() {
				return element.childElementXml;
			}			
		};
	}	

	public PacketExtension parseExtension(XmlPullParser parser) throws Exception {
		Log.d(LOG_TAG, "parseExtension");
		final XmlElement element = parseRootElement(parser);
				
		return new PacketExtension() {			
			public String toXML() {
				return element.childElementXml;
			}			
			public String getNamespace() {
				return element.nameSpace;
			}			
			public String getElementName() {
				return element.name;
			}
		};
	}
	
	private XmlElement parseRootElement(XmlPullParser parser) throws XmlPullParserException, IOException {
		Log.d(LOG_TAG, "parseRootElement");
		List<String> prefixes = new ArrayList<String>();
		String rootElementName = parser.getName();
		String rootNS = parser.getNamespace();
		StringBuilder sb = new StringBuilder(getText(parser,prefixes));
		String text = null;	
		do {
			parser.next();
			text = getText(parser,prefixes);
			sb.append(text);			
		} while (!atRootEndTag(parser, rootElementName));
		
		return new XmlElement(rootElementName, rootNS, sb.toString());
	}

	private boolean atRootEndTag(XmlPullParser parser,String rootElementName) throws XmlPullParserException {		
		Log.d(LOG_TAG, "atRootEndTag");
		return parser.getEventType() == XmlPullParser.END_TAG && 
				parser.getName() != null &&
				parser.getName().equals(rootElementName);
	}	

	private String getText(XmlPullParser parser, List<String> prefixes) throws XmlPullParserException {
		Log.d(LOG_TAG, "getText");
		String returnValue = parser.getText();
		if (returnValue!=null)
			return returnValue;
		
		int event = parser.getEventType();

		String prefix = parser.getPrefix();
		if (prefix==null)
			prefix = "";
		else
			prefix = prefix+":";		
		
		String prefixDeclaration = "";
		if (!prefixes.contains(prefix)) {
			prefixDeclaration = " xmlns:"+parser.getPrefix()+"=\""+parser.getNamespace()+"\"";
			prefixes.add(prefix);
		}
		
		if (event==XmlPullParser.START_TAG) {
			StringBuilder attributes = new StringBuilder();
			for (int i=0; i<parser.getAttributeCount(); i++) {
				attributes.append(" "+parser.getAttributeName(i)+"=\""+parser.getAttributeValue(i)+"\"");
			}			
			if(parser.getNamespace()!="")
				returnValue = "<"+prefix+parser.getName()+prefixDeclaration+" xmlns=\""+parser.getNamespace()+"\" "+attributes.toString()+">";
			else
				returnValue = "<"+prefix+parser.getName()+prefixDeclaration+attributes.toString()+">";
		}
		if (event==XmlPullParser.END_TAG) {
			returnValue = "</"+prefix+parser.getName()+">";
		}

		return returnValue;
	}
	
	private static class XmlElement {
		public final String childElementXml;
		public final String name;
		public final String nameSpace;


		public XmlElement(String name, String nameSpace, String childElementXml) {
			this.childElementXml = childElementXml;
			this.name = name;
			this.nameSpace = nameSpace;
		}
	}
}
