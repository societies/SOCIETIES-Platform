package org.societies.impl;

import java.util.ArrayList;
import java.util.List;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.provider.IQProvider;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class RawXmlProvider implements IQProvider {

	public IQ parseIQ(XmlPullParser parser) throws Exception {
		List<String> prefixes = new ArrayList<String>();
		String rootElementName = parser.getName();
		final StringBuilder sb = new StringBuilder(getText(parser,prefixes));
		String text = null;	
		do {
			parser.next();
			text = getText(parser,prefixes);
			sb.append(text);			
		} while (!parser.getName().equals(rootElementName));
		
		return new IQ() {
			@Override
			public String getChildElementXML() {
				return sb.toString();
			}			
		};
	}	

	private String getText(XmlPullParser parser, List<String> prefixes) throws XmlPullParserException {
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
}
