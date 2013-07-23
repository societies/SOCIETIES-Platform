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
package ac.hw.display.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Describe your class here...
 *
 * @author Eliza
 *
 */
public class ServiceRuntimeSocketServer extends Thread{


	private DisplayPortalClient displayService;
	private ServerSocket server;
	private Socket client;
	private BufferedReader in;
	private PrintWriter out;
	public static final String started_Service = "STARTED_SERVICE";
	public static final String stopped_Service = "STOPPED_SERVICE";
	private static final String logged_Out = "LOGGED_OUT";
	public static final String START_MSG = "START_MSG";
	public static final String END_MSG = "END_MSG";
	private  Logger logging = LoggerFactory.getLogger(this.getClass());
	private boolean listening = true;
	private int serverPort;
	//private int[] ports = new int[]{2121,2122,2123,2124,2125,2126,2127,2128,2129,2130,2131,2132,2133,2134,2135,2136,2137,2138,2139,2140,2141,2142, 2143,2144,2145,2146,2147,2148,2149,2150,2151};

	public ServiceRuntimeSocketServer(DisplayPortalClient displayService){
		this.displayService = displayService;
		listening = true;

	}

	public int setListenPort(){
		try {
			ServerSocket portLocator = new ServerSocket(0);
			serverPort = portLocator.getLocalPort();
			portLocator.close();
			this.logging.debug("Found available port: "+serverPort);
			return serverPort;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return -1;
	}

	@Override
	public void run(){
		while (listening){
			this.logging.debug("Starting a new serverSocket on: "+this.serverPort);
			this.listenSocket();
		}
	}


	public void listenSocket(){
		try{

			server = new ServerSocket(serverPort); 

		} catch (IOException e) {
			System.out.println("Could not listen on port "+serverPort);

		}


		try{
			client = server.accept();
		} catch (IOException e) {
			System.out.println("Accept failed: "+serverPort);

		}

		try{
			in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			out = new PrintWriter(client.getOutputStream(), true);
		} catch (IOException e) {
			System.out.println("Accept failed: "+serverPort);
			
		}


		
		try{
			String line = in.readLine();
			while (line!=null){


				this.logging.debug("Received from portal: "+line);
				if (line!=null){
					if (line.contains(started_Service)){
						String serviceName = line.substring(started_Service.length()+1);
						this.displayService.notifyServiceStarted(serviceName.trim());
						this.logging.debug("Called serviceStarted method on"+serviceName);
					}else if (line.contains(stopped_Service)){
						String serviceName = line.substring(stopped_Service.length()+1);
						this.displayService.notifyServiceStopped(serviceName.trim());
						this.logging.debug("Called serviceStopped method on"+serviceName);
					}else if (line.contains(logged_Out)){
						this.displayService.notifyLogOutEvent();

						this.logging.debug("Called notifyLogOutEvent");
					}


				}
				line = in.readLine();

				//Send data back to client
				//out.println(line);
			} 
			this.finalize();
		}catch (IOException e) {
			this.logging.debug(e.getMessage());
			e.printStackTrace();
			finalize();
			return;
		}

	}








	@Override
	protected void finalize(){

		this.logging.debug("finalise");
		//Clean up 
		try{
			in.close();
			out.close();
			server.close();
			this.logging.debug("SocketServer closed.");
		} catch (IOException e) {
			this.logging.debug("Could not close.");
		}
	}
}
