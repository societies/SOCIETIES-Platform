/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAÇÃO, SA (PTIN), IBM Corp., 
 * INSTITUT TELECOM (ITSUD), AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA PERIORISMENIS EFTHINIS (AMITEC), TELECOM 
 * ITALIA S.p.a.(TI),  TRIALOG (TRIALOG), Stiftelsen SINTEF (SINTEF), NEC EUROPE LTD (NEC))
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
package  org.societies.css.devicemgmt.controller;
import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.css.devicemgmt.model.DeviceMgmtEventConstants;
import org.societies.api.osgi.event.EMSException;
import org.societies.api.osgi.event.EventTypes;
import org.societies.api.osgi.event.IEventMgr;
import org.societies.api.osgi.event.InternalEvent;


public class SocketClient extends Thread{
	private Logger logging = LoggerFactory.getLogger(this.getClass());

	Socket echoSocket = null;
	PrintWriter out = null;
	BufferedReader in = null;


	private Pattern pattern;
	private Matcher matcher;
	private static final String IPADDRESS_PATTERN = 
			"^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
					+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
					+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
					+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";

	private final String controllerIPAddress;

	private String deviceId;

	private ContextDataManager ctxDataManager;
	public SocketClient(String controllerIPAddress, ContextDataManager ctxDataManager){
		this.controllerIPAddress = controllerIPAddress;
		pattern = Pattern.compile(IPADDRESS_PATTERN);
		this.ctxDataManager = ctxDataManager;
	}

	public boolean checkIp(String sip) {
		matcher = pattern.matcher(sip);
		return matcher.matches();
	}

	public void run() {

		try {

			InetAddress address = InetAddress.getByName(this.controllerIPAddress);

			echoSocket = new Socket(address, 10002);
			out = new PrintWriter(echoSocket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
		} catch (UnknownHostException e) {
			this.logging.debug("Don't know about host: "+controllerIPAddress);
			return;
			//System.exit(1);
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for "
					+ "the connection to: "+controllerIPAddress);
			this.logging.debug("Couldn't get I/O for "
					+ "the connection to: "+controllerIPAddress);
			//return;

			try {
				InetAddress address = InetAddress.getByName("localhost");
				echoSocket = new Socket(address, 10001);
				out = new PrintWriter(echoSocket.getOutputStream(), true);
				in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));			
			} catch (UnknownHostException e1) {
				// TODO Auto-generated catch block
				this.logging.debug("Don't know about host: localhost");
				e1.printStackTrace();
				return;
			}
			//System.exit(1);
			catch (IOException ioe) {
				// TODO Auto-generated catch block
				ioe.printStackTrace();
				this.logging.debug("Couldn't get I/O for the connection to localhost");
				return;
			}
		}

		this.logging.debug("Ready to receive data from Controller");
		//BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
		String userInput = "";

		try {
			//out.println(userInput);
			String serverInput;

			serverInput = in.readLine();

			while (serverInput!=null){

				logging.debug("Received input from controller: "+serverInput);
				System.out.println("Received input from controller: "+serverInput);
				if (serverInput.startsWith("$")){
					String[] fields = serverInput.split("#");

					if (fields.length>=3){
						String ctrlField = fields[0];

						String portField = fields[1];

						String valueField = fields[2];

						String controllerId = "";
						String portId = "";
						String value = "";
						int delim = 0;
						if (ctrlField.startsWith("$CTRID")){
							delim = ctrlField.indexOf(':');
							controllerId = ctrlField.substring(delim+1, ctrlField.length());
						}
						if (portField.startsWith("PRTID")){
							delim = portField.indexOf(':');
							portId = portField.substring(delim+1, portField.length());
						}

						if (valueField.startsWith("VALUE")){
							delim = valueField.indexOf(':');
							value = valueField.substring(delim+1, valueField.length());
						}

						logging.debug("Controller: "+controllerId);
						logging.debug("PortId: "+portId);
						logging.debug("Value: "+value);
						System.out.println("Controller: "+controllerId);
						System.out.println("PortId: "+portId);
						System.out.println("Value: "+value);
						
						Integer intValue = Integer.parseInt(value);
						
						this.ctxDataManager.updateContext(controllerId, portId, intValue);
					}

				}
					serverInput = in.readLine();
				}

			out.close();
			in.close();
			//stdIn.close();
			echoSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	public static void main(String[] args){
		SocketClient c = new SocketClient("192.168.0.30", null);
		c.start();
	}
}

