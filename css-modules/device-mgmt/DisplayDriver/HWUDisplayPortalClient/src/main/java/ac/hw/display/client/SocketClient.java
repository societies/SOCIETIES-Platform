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
package  ac.hw.display.client;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SocketClient {
	private Logger logging = LoggerFactory.getLogger(this.getClass());

	private Socket echoSocket = null;
	private PrintWriter out = null;
	private BufferedReader in = null;

	private boolean connected = false;

	private final String displayAddress;



	public SocketClient(String displayAddress){
		this.displayAddress = displayAddress;
	}

	private boolean connect(){

		try{
			InetAddress address = InetAddress.getByName(this.displayAddress);
			echoSocket = new Socket(address, 2112);
			out = new PrintWriter(echoSocket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
			connected = true;
			return  true;
		} catch (UnknownHostException e) {
			this.logging.debug("Don't know about host: "+displayAddress);
			//System.exit(1);
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for "
					+ "the connection to: "+displayAddress);
			this.logging.debug("Couldn't get I/O for "
					+ "the connection to: "+displayAddress);
		} 
		return false;
	}

	private void disconnect(){
		if (echoSocket!=null){
			try {
				out.close();
				in.close();
				echoSocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//stdIn.close();
		}
		connected = false;
	}
	public boolean sendMessage(String message) {


		if (connected){
		try{
			
			out.println(message);
			out.flush();
			System.out.println("Sent message: "+message);
			String input = in.readLine();
			disconnect();
			System.out.println("received: "+input);
			if (input.contains("OK")){
				return true;
			}else{
				return false;
			}
			
			
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
		}else{
			if (connect()){
				sendMessage(message);
				
				return true;
			}
		}
		return false;


	}



	public void startSession(UserSession userSession){
		String message = "LOGIN\n" +
				"USER:"+userSession.getUserIdentity()+"\n"+
				"PORTAL_PORT:"+userSession.getServiceRuntimeSocketPort()+"\n"+
				"NUM_SERVICES:"+userSession.getServices().size()+"\n"+
				"START_SERVICES\n";
		
		int count = 0;
		for (ServiceInfo sInfo : userSession.getServices()){
			
			String serviceMsg = 
					"START_SERVICE_INFO:"+count+"\n"+
					"SERVICE_NAME_"+count+":"+sInfo.getServiceName()+"\n"+
					"SERVICE_EXE_"+count+":"+sInfo.getExe()+"\n"+
					"SERVICE_PORT_"+count+":"+sInfo.getServicePortNumber()+"\n"+
					"RequiresKinect_"+count+":"+sInfo.isKinectRequired()+"\n"+
					"END_SERVICE_INFO:"+count+"\n";
			message = message.concat(serviceMsg);
			count++;
		}
		
		message = message.concat("END_SERVICES\n");
		this.sendMessage(message);
	}

	
	public void endSession(String userIdentity){
		String message = "LOGOUT\n"+
				userIdentity+"\n";
		this.sendMessage(message);
	}
	
	public void startService(UserSession userSession, String serviceName){
		String message = "START_SERVICE\n"+
				userSession.getUserIdentity()+"\n"+
				serviceName+"\n";
		this.sendMessage(message);
	}
	
	public void sendImage(UserSession userSession, URL remoteImageLocation) {
		String message = "SHOW_IMAGE\n"+
				userSession.getUserIdentity()+"\n"+
				remoteImageLocation+"\n";
		this.sendMessage(message);
		
	}
	
	public void sendText(String serviceName, UserSession userSession, String text) {
		String message = "SHOW_TEXT\n"+
				userSession.getUserIdentity()+"\n"+
				serviceName+"\n"+
				text+"\n"+
				"END_TEXT";
		this.sendMessage(message);	
		
	}
	
	public void logOut(UserSession userSession){
		String message = "LOGOUT\n"+userSession.getUserIdentity()+"\n";
		this.sendMessage(message);
		this.logging.debug("Sent LOGOUT message");
	}
	
	public void startService(ServiceInfo sInfo){
		String message = "START_SERVICE\n "+sInfo.getServiceName()+"\n";
		this.sendMessage(message);
	}
	private static int countLines(String str){
		   String[] lines = str.split("\r\n|\r|\n");
		   return  lines.length;
		}

	public static void main(String[] args){
		
		try {
			URL url = new URL("http://www.macs.hw.ac.uk/~ceeep1/societies/services/MockWindowsExecutable.exe");
			System.out.println(url.toString());
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		SocketClient c = new SocketClient("127.0.0.1");
		
		UserSession userSession = new UserSession("emma.societies.local.macs.hw.ac.uk", 10001);
		
		//c.sendText("Eliza's service", userSession, "<strong>Hello Emma!!! </strong>");
		/*ServiceInfo sInfo = new ServiceInfo(null, "Social Learning", "http://www.macs.hw.ac.uk/~ceeep1/societies/services/SocialLearningGame.exe", 0 , true);
		
		ServiceInfo sInfo2 = new ServiceInfo (null, "PolicyEditor", "http://www.macs.hw.ac.uk/~ceeep1/societies/services/POLICY~1.JAR", 0, false);
		ServiceInfo sInfo3 = new ServiceInfo(null, "Youtube", "http://www.youtube.com/watch?v=3OnnDqH6Wj8", 0, false);*/
/*		ServiceInfo sInfo4 = new ServiceInfo(null, "MyTV", "http://www.macs.hw.ac.uk/~ceesmm1/societies/mytv/MyTvUI.exe", 0, true);*/
		ServiceInfo policyEditor = new ServiceInfo (null, "MyTV", "http://www.macs.hw.ac.uk/~ceesmm1/societies/mytv/MyTvUI.exe", 4984, true);
		
		userSession.addService(policyEditor);
		
		c.startSession(userSession);
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		c.startService(userSession, policyEditor.getServiceName());
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		c.logOut(userSession);
		
		//userSession.addService(sInfo2);
		//userSession.addService(sInfo);
	//	userSession.addService(sInfo4);
		//userSession.addService(sInfo2);
		//userSession.addService(sInfo2);
		//userSession.addService(sInfo3);
/*		c.startSession(userSession);
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		
		//c.startService(userSession, sInfo2.getServiceName());
		/*
		URL url;
		try {
			url = new URL("http://images.google.com/intl/en_ALL/images/logos/images_logo_lg.gif");
			c.sendImage(userSession, url);
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		//c.sendText("HWUService", userSession, getSampleText(0));
		//userSession.setUserIdentity("other@societies.local");
/*		for (int i = 0; i<5; i++){
			c.sendText("HWUService", userSession, getSampleText(i));
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		for (int i = 0; i<5; i++){
			c.sendText("HWUService", userSession, getSampleText(i));
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}*/
/*		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		c.endSession(userSession.getUserIdentity());*/
/*		String message = "LOGIN\n" +
				"eliza@societies.local\n" +
				"MockService\n"+
				"http://www.macs.hw.ac.uk/~ceeep1/societies/services/MockWindowsExecutable.exe\n" +
				"MockService2\n"+
				"http://www.macs.hw.ac.uk/~ceeep1/societies/services/MockWindowsExecutable.exe\n" +
				"MockService3\n"+
				"http://www.macs.hw.ac.uk/~ceeep1/societies/services/MockWindowsExecutable.exe\n" +
				"END_SERVICES\n";
		c.sendMessage(message);
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/

	
	
		/*message = "START SERVICE\n" +
				"MockService2\n";
		c.sendMessage(message);
		*/
/*		try {
			Thread.sleep(8000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String message = "LOGOUT\n" +
				"eliza@societies.local\n";*/
		//c.sendMessage(message);

	}



	private static String getSampleText(int n){
		if (n==0){
			return "The lab was started in 2001 with research in the area of personalisation in the context of pervasive and ubiquitous systems. Personalisation is the adaptation of systems to provide different views to different users according to their needs and preferences. For instance, in searching for information on the web, different users have different needs (e.g. within any particular domain some users can deal with detailed technical information, others cannot). If an information provider wants the provided information to be effective, the type of information selected and the way in which it is presented must be adapted to meet the preferences and requirements of the individual user. Early work on this was done in the EU FP5 project Youngster.";
		}
		if (n==1){
			return "In the context of pervasive systems, personalisation can be very effective as user preferences can be applied in areas such as service ranking, service selection and adaptation of services and resources. One important characteristic of pervasive systems is the amount of information that becomes available about the environment of their users through the use of sensors. These can range from conventional indoor and outdoor temperature sensors to GPS sensors that indicate location information or even activity sensors that suggest walking, sitting and standing positions of the user. This information is generally referred to as context information. Context aware personalisation takes advantage of the context information available and uses it to adapt a system accordingly. For instance, as the user moves from one location to another, user preferences can be used to select what type of communication network should be used. Early work on context-aware personalisation was done in the context of the EU FP6 projects Daidalos I and II."; 
		}
		if (n==2){
			return "Personalisation requires user preferences (rules) to be entered into the system by the user. This task can become a burden for the user as the number of services and sources of context information grows in the system. More importantly, one must not expect all the users of a pervasive system to have the necessary knowledge to perform this task. User behaviour can be monitored, logged and mined upon to learn behavioural patterns when the user interacts with the system. This information can be translated into user preferences using appropriate algorithms and stored in the system in order to relieve the user from manually entering user preferences. Early work on user behaviour learning and user preference merging was done in the context of Daidalos II and continues in all later and current projects."; 
		}
		if (n==3){
			return "There are clearly potential threats to the privacy of users when so much personal information is collected and disseminated and it is the responsibility of these systems to provide appropriate mechanisms to protect privacy. However, privacy in such systems cannot be handled in a static way. In every day life, people choose to reveal the type and detail of information about them, when to do so and to whom. Therefore, it is possible to personalise privacy protection using context-aware user preferences. Early work on this area was done in the context of project Daidalos II with research and implementation of this work continued in the EU FP7 project PERSIST as well as in the ongoing EU FP7 project SOCIETIES.";
		}
		return "Current trends in the design of pervasive systems have concentrated on the problem of isolated smart spaces (such as smart homes) via a fixed infrastructure. This is likely to lead to the evolution of islands of pervasiveness separated by voids in which there is no support for pervasiveness. The PERSIST project introduced the Personal Smart Space paradigm which allows user devices to communicate with each other in an ad hoc manner to provide a bridge between these isolated smart spaces. A Personal Smart Space (PSS) is a collection of devices that belong to a single user.. It is a network of devices that communicate with each other and are aware that they are part of a group of devices aiming to aid their owner in his/her daily tasks. A PSS facilitates the interaction with other PSSs and promotes the provision and consumption of services and exchange of information. Services installed in any of the devices of a PSS can be advertised on the network so that other PSSs can consume them when needed.";
		
	}



}

