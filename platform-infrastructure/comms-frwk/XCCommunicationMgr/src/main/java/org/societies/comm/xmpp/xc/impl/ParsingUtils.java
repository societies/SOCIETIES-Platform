package org.societies.comm.xmpp.xc.impl;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Element;
import org.societies.api.comm.xmpp.datatypes.XMPPInfo;
import org.societies.api.comm.xmpp.datatypes.XMPPNode;

public class ParsingUtils {
	
	public static SimpleEntry<String, XMPPInfo> parseInfoResult(Element element) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public static byte[] getInfoQueryRequestBytes(String node) {
		String nodePart = "";
		if (node!=null) 
			nodePart = "node='"+node+"' ";
		return ("<query xmlns='"+XMPPInfo.INFO_NAMESPACE+"' "+nodePart+"/>\n").getBytes();
	}
	
	public static SimpleEntry<String, List<String>> parseItemsResult(Element element) {
		String node = element.attributeValue("node");
		List<String> list = new ArrayList<String>();
		for (Object o : element.elements()) {
			Element e = (Element) o; 
			list.add(e.attributeValue("node"));
		}
		return new SimpleEntry<String, List<String>>(node, list);
	}
	
	public static byte[] getItemsQueryRequestBytes(String node) {
		String nodePart = "";
		if (node!=null) 
			nodePart = "node='"+node+"' ";
		return ("<query xmlns='"+XMPPNode.ITEM_NAMESPACE+"' "+nodePart+"/>\n").getBytes();
	}
}
