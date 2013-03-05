package org.societies.da.openfire.plugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collection;

public class BatchOpenfirePluginCall {
	
	private String baseUrl;
	private PrintStream out;
	
	public BatchOpenfirePluginCall(String baseUrl, PrintStream out, OperationType o) {
		this.baseUrl = baseUrl+"&type="+o.toString();
		this.out = out;
	}
	
	public void process(Collection<AccountDetails> accountDetails) {
		for (AccountDetails ad : accountDetails) {
			sendRequest(ad);
		}
	}
	
	private void sendRequest(AccountDetails ad) {
		out.print("Processing user '"+ad.getUsername()+"': ");
		try {
			URL u = new URL(baseUrl+"&"+ad.getUrlParameters());
	        URLConnection yc = u.openConnection();
	        BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
	        String inputLine;
	        while ((inputLine = in.readLine()) != null) 
	            out.println(inputLine);
	        in.close();
		} catch (MalformedURLException e) {
			out.println(e.getMessage());
		} catch (IOException e) {
			out.println(e.getMessage());
		}
	}
}
