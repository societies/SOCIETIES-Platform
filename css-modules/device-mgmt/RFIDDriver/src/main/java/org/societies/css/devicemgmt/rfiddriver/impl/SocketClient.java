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
package  org.societies.css.devicemgmt.rfiddriver.impl;
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
	private IEventMgr eventMgr;
	
	
	private Pattern pattern;
	private Matcher matcher;
	private static final String IPADDRESS_PATTERN = 
			"^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
			+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
			+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
			+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";

	private final String rfidAddress;

	private String deviceId;

	public SocketClient(String rfidAddress){
		this.rfidAddress = rfidAddress;
		pattern = Pattern.compile(IPADDRESS_PATTERN);
	}
	
	public boolean checkIp(String sip) {
		matcher = pattern.matcher(sip);
		return matcher.matches();
	}

	public void run() {
/*		boolean hasAddress = true;
		if (this.rfidAddress==null){
			hasAddress = false;
		}
		String addrStr = "";
		
		while (!hasAddress){
			addrStr = (String) JOptionPane.showInputDialog(null, "Please enter the IP address of the RFID reader", "RFID configuration", JOptionPane.PLAIN_MESSAGE, null, null, "");
			if (checkIp(addrStr)){
				hasAddress = true;
				this.rfidAddress = addrStr;
			}else{
				JOptionPane.showMessageDialog(null, "Invalid IP address entered. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
				hasAddress = false;
			}
		}*/
		
		//String addrStr = "137.195.14.2";
		//addrStr="137.195.27.198";
		//addrStr="192.168.0.200";
		try {
			
			InetAddress address = InetAddress.getByName(this.rfidAddress);
			
			echoSocket = new Socket(address, 10001);
			out = new PrintWriter(echoSocket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
		} catch (UnknownHostException e) {
			if(logging.isDebugEnabled()) logging.debug("Don't know about host: "+rfidAddress);
			return;
			//System.exit(1);
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for "
					+ "the connection to: "+rfidAddress);
			if(logging.isDebugEnabled()) logging.debug("Couldn't get I/O for "
					+ "the connection to: "+rfidAddress);
			//return;
			
			try {
				InetAddress address = InetAddress.getByName("localhost");
				echoSocket = new Socket(address, 10001);
				out = new PrintWriter(echoSocket.getOutputStream(), true);
				in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));			
			} catch (UnknownHostException e1) {
				// TODO Auto-generated catch block
				if(logging.isDebugEnabled()) logging.debug("Don't know about host: localhost");
				e1.printStackTrace();
				return;
			}
			//System.exit(1);
			catch (IOException ioe) {
				// TODO Auto-generated catch block
				ioe.printStackTrace();
				if(logging.isDebugEnabled()) logging.debug("Couldn't get I/O for the connection to localhost");
				return;
			}
		}

		if(logging.isDebugEnabled()) logging.debug("Ready to receive data from RFID Reader");
		//BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
		String userInput = "GI";

		try {
			out.println(userInput);
			String serverInput;

			serverInput = in.readLine();

			while (serverInput!=null){

				if (serverInput.trim().equalsIgnoreCase(userInput)){
					logging.debug("Server acknowledged: "+serverInput.trim() + " of length:"+serverInput.length());
				}else if (serverInput.length()>20){
					logging.debug("Server said: "+serverInput.trim() + " of length:"+serverInput.length());
				}
				else {
					logging.debug("echo: " + serverInput.trim()+ "\n");
					String tag = serverInput.substring(6,10);
					logging.debug("Tag: "+tag);
					String wakeUpUnit = serverInput.substring(11,15);
					logging.debug("WUnit: "+wakeUpUnit);
					
					//this.rfidServer.sendUpdate(wakeUpUnit, tag);
					//send an event to notify new data arrived
					//RFIDUpdateEvent rfidEvent = new RFIDUpdateEvent(wakeUpUnit, tag);
					
					HashMap<String, Object> payload = new HashMap<String, Object>();
					payload.put("wakeupUnit", wakeUpUnit);
					payload.put("tagNumber", tag);
					
					
					InternalEvent event = new InternalEvent(EventTypes.RFID_UPDATE_EVENT, DeviceMgmtEventConstants.RFID_READER_EVENT, deviceId, payload);
					try {
						this.eventMgr.publishInternalEvent(event);
						if(logging.isDebugEnabled()) logging.debug("Published internal event: "+wakeUpUnit+"-"+tag);
					} catch (EMSException e) {
						e.printStackTrace();
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

	/**
	 * @return the eventMgr
	 */
	public IEventMgr getEventMgr() {
		return eventMgr;
	}


	/**
	 * @param eventMgr the eventMgr to set
	 */
	public void setEventMgr(IEventMgr eventMgr) {
		this.eventMgr = eventMgr;
	}
	
	
	/**
	 * 
	 * @param deviceId
	 */
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	
	/**
	 * @return the deviceId
	 */
	public String getDeviceId() {
		return deviceId;
	}


	public static void main(String[] args){
		SocketClient c = new SocketClient("137.195.27.198");
		c.start();
	}
}

