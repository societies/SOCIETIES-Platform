/*********************************************
  Context Awareness Platform
 ********************************************
  Copyright (c) 2006-2010 Telecom Italia S.p.A
  International patents pending: 2007, 2008
 ******************************************** 
  $Id: HTTPTimedoutConnection.java 7883 2010-09-15 08:57:17Z rolando $
  $HeadURL: http://163.162.93.163:90/svn/ca/platform/cb/branches/rel-1_4-ev/src/main/java/com/tilab/ca/platform/cb/utils/HTTPTimedoutConnection.java $
 *********************************************
 */
package com.tilab.ca.platform.SSO.social.facebook.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.SocketTimeoutException;
import java.net.ConnectException;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import java.util.Hashtable;

import java.io.UTFDataFormatException;
import java.io.BufferedInputStream;

import org.apache.log4j.Logger;


public class HTTPUtilsTimedoutConnection {
	
	
	// private static LogMan LogManager =
	// LogMan.getInstance(ConfigParameters.PROVIDER_NAME);
	private static Logger LogManager =  Logger.getLogger(HTTPUtilsTimedoutConnection.class);

	// Hashtable usata per il metodo queuedSendWithPost
	private static Hashtable activeRequests = new Hashtable();

	private String url;
	private String query;
	private int connTimeout_ms = 0;
	private int readTimeout_ms = -1; // non attivo (default)

	public HTTPUtilsTimedoutConnection(String url, String query) {
		this.url = url;
		this.query = query;
	}

	public HTTPUtilsTimedoutConnection(String url, String query,
			int connTimeout_ms) {
		this.url = url;
		this.query = query;
		this.connTimeout_ms = connTimeout_ms;
	}

	public HTTPUtilsTimedoutConnection(String url, String query,
			int connTimeout_ms, int readTimeout_ms) {
		this.url = url;
		this.query = query;
		this.connTimeout_ms = connTimeout_ms;
		this.readTimeout_ms = readTimeout_ms;
	}

	public static String sendWithPost(String url, String query,
			int connTimeout_ms, int readTimeout_ms)
			throws SocketTimeoutException, ConnectException,
			UTFDataFormatException {
		return sendRequest("POST", url, query, connTimeout_ms, readTimeout_ms);
	}

	public static String sendWithPost(String url, String query,
			int connTimeout_ms) throws SocketTimeoutException,
			ConnectException, UTFDataFormatException {
		return sendRequest("POST", url, query, connTimeout_ms, -1);
	}

	public static String sendGet(String url, String query, int connTimeout_ms,
			int readTimeout_ms) throws SocketTimeoutException,
			ConnectException, UTFDataFormatException {
		return sendRequest("GET", url, query, connTimeout_ms, readTimeout_ms);
	}

	public static String sendGet(String url, String query, int connTimeout_ms)
			throws SocketTimeoutException, ConnectException,
			UTFDataFormatException {
		return sendRequest("GET", url, query, connTimeout_ms, -1);
	}
	
	public static String sendWithDelete(String url, String query,
			int connTimeout_ms, int readTimeout_ms)
			throws SocketTimeoutException, ConnectException,
			UTFDataFormatException {
		return sendRequest("DELETE", url, query, connTimeout_ms, readTimeout_ms);
	}

	private static String sendRequest(String req_type, String url,
			String query, int connTimeout_ms, int readTimeout_ms)
			throws SocketTimeoutException, ConnectException,
			UTFDataFormatException {

		URLConnection aConnection;
		HttpURLConnection aHttpConnection;
		URL localUrl;
		String postResponse = "";

		LogManager.debug("Enter Send request:" + url + " - query:" + query);

		long invokeTime = System.currentTimeMillis();
		try {
			if (connTimeout_ms < 0)
				connTimeout_ms = 0;
			// create connection
			try {
				// creo la URL
				localUrl = new URL(url);
				// Preparo la connessione
				aConnection = localUrl.openConnection();
				aHttpConnection = (HttpURLConnection) aConnection;

				// Configuro la connessione per la POST
				aHttpConnection.setConnectTimeout(connTimeout_ms);
				if (readTimeout_ms != -1)
					aHttpConnection.setReadTimeout(readTimeout_ms);
				aHttpConnection.setRequestMethod(req_type);
				
				//LogManager.debug("Using proxy="+aHttpConnection.usingProxy());
				
				//aConnection.setRequestProperty ( "User-agent", "my agent name");

				// aHttpConnection.setRequestProperty ("Content-Type",
				// "text/xml");
				// MODIFICA BY SERGIO
				if (req_type.equals("POST"))
					aHttpConnection.setRequestProperty("Content-Type",
							"application/x-www-form-urlencoded");
				
				if (req_type.equals("DELETE"))
					aHttpConnection.setRequestProperty("Content-Length", "0");

				aHttpConnection.setDoOutput(true);
				aHttpConnection.setDoInput(true);

				LogManager.debug("aHttpConnection.connect()");
				aHttpConnection.connect();

			} catch (SocketTimeoutException e) {
				LogManager.warn("Exception in sendRequest(): " + e.toString());
				e.printStackTrace();
				throw e;
			} catch (ConnectException e) {
				LogManager.warn("Exception in sendRequest(): " + e.toString());
				e.printStackTrace();
				throw e;
			} catch (Exception e) {
				LogManager.warn("Exception in sendRequest(): " + e.toString());
				e.printStackTrace();
				return null;
			}

			// invio i dati al server
			PrintWriter myOut;
			if (req_type.equals("POST") && query!=null) {
				try {
					// creo lo stream per inviare i dati
					myOut = new PrintWriter(aHttpConnection.getOutputStream());
					// MODIFICA BY SERGIO
					
					query = query.replaceAll("\\+", "%2b");
					// query = changeAllPlusToCode(query);
					// invio i dati

					LogManager.debug("Send data");
					myOut.print(query);
					// System.out.println(xmlRequest);
					myOut.flush();
					myOut.close();
				} catch (IOException exIO) {
					LogManager.warn("IOException in sendRequest()");
					return null;
				}
			}

			// leggo la risposta dal server
			try {

				LogManager.debug("Response from server .."+aHttpConnection.getResponseCode()+
						" "+aHttpConnection.getResponseMessage());
				int respCode = aHttpConnection.getResponseCode();
				
				// stampo i dati letti
				if (respCode == HttpURLConnection.HTTP_OK) {
					// leggo i dati
					InputStream myIn = (InputStream) aHttpConnection
							.getContent();
					LogManager.debug("ResponseMessage: "
							+ aHttpConnection.getResponseMessage());

					try {
						DataInputStream myBuf = new DataInputStream(
								new BufferedInputStream(myIn));

						/*
						 * MODIFICATO PERCHE' AUP NON SETTA HEADER
						 * CONTENT-LENGTH if
						 * (aHttpConnection.getContentLength()<1) throw new
						 * IOException
						 * ("No body received, or content-length missing");
						 * byte[] byteArrOrig = new
						 * byte[aHttpConnection.getContentLength()];
						 */
						byte[] byteArrOrig = null;
						if (aHttpConnection.getContentLength() >= 1)
							byteArrOrig = new byte[aHttpConnection
									.getContentLength()];
						else
							// Content-length mancante, o messaggio vuoto
							byteArrOrig = new byte[10000];
						// int readNum = myBuf.read(byteArrOrig);
						int readNum = 0;
						int parzRead = 0;
						while ((readNum < byteArrOrig.length)
								&& (parzRead = myBuf.read(byteArrOrig, readNum,
										byteArrOrig.length - readNum)) >= 0) {
							readNum += parzRead;
						}
						myBuf.close();
						if (readNum < 1)
							throw new IOException("No body received");

						// if (ConfigParameters.CHECK_UTF8)
						// XMLParser.checkUTF8(byteArrOrig,0, readNum);
						postResponse = new String(byteArrOrig, 0, readNum,
								"UTF-8");
						
						LogManager.debug("postResponse BODY: "
								+ postResponse);

					} catch (UTFDataFormatException e) {
						// LogManager.warn("Data charset is not UTF-8");
						throw e;
					} catch (Exception e) {
						LogManager
								.warn("Error during reading file in responseHandle");
						return null;
					}
					return postResponse;
				} else {
					return null;
				}
			} catch (UTFDataFormatException e) {
				throw e;
			} catch (IOException exIO) {
				LogManager.warn("IOException while reading data");
				return null;
			}
		} finally {
			// counters.provTime += System.currentTimeMillis() - invokeTime;
		}
	}

	

}
