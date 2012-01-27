package org.societies.comm.xmpp.xc.impl;

import java.util.List;
import java.util.Map;

import org.societies.api.comm.xmpp.datatypes.XMPPInfo;
import org.societies.api.comm.xmpp.datatypes.XMPPNode;
import org.xml.sax.InputSource;

public class ParsingUtils {
	
	public static Map<String, XMPPInfo> parseInfoResult(InputSource inputSource) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public static byte[] getInfoQueryRequestBytes(String node) {
		String nodePart = "";
		if (node!=null) 
			nodePart = "node='"+node+"' ";
		return ("<query xmlns='"+XMPPInfo.INFO_NAMESPACE+"' "+nodePart+"/>\n").getBytes();
	}
	
	public static Map<String, List<XMPPNode>> parseItemsResult(InputSource inputSource) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public static byte[] getItemsQueryRequestBytes(String node) {
		String nodePart = "";
		if (node!=null) 
			nodePart = "node='"+node+"' ";
		return ("<query xmlns='"+XMPPNode.ITEM_NAMESPACE+"' "+nodePart+"/>\n").getBytes();
	}
}
