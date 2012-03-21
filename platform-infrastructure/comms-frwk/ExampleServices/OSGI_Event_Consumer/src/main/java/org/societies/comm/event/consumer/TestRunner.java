package org.societies.comm.event.consumer;

import org.societies.api.osgi.event.CSSEventConstants;

public class TestRunner {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		 String eventFilter = "(&" + "(" + CSSEventConstants.EVENT_NAME + "="
	                + "attributeId" + ")" + "(" + CSSEventConstants.EVENT_SOURCE
	                + "=" + "EVENT_SOURCE" + ")" + ")";
	        System.out.println("Registering context EventListener for attribute "
	                + "attributeId" + " using event filter " + eventFilter);

	}

}
