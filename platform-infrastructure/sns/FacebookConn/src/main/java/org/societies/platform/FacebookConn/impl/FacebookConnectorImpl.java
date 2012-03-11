package org.societies.platform.FacebookConn.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.societies.platform.FacebookConn.Connector;
import org.societies.platform.FacebookConn.FacebookConnector;
import org.societies.platform.FacebookConn.exeptions.MissingTokenExeptions;
import org.springframework.web.context.request.WebRequest;

import com.restfb.Connection;
import com.restfb.DefaultFacebookClient;
import com.restfb.DefaultWebRequestor;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.WebRequestor;
import com.restfb.WebRequestor.Response;
import com.restfb.types.Post;



public class FacebookConnectorImpl implements FacebookConnector {

	
	private String 				access_token = null;
	private String 				identity	 = null;
	private String 				name;
	
	private List<Object>		parameters;
	private FacebookClient 		facebookClient;
	private int					maxPostLimit = 50;
	
	
	public FacebookConnectorImpl (String access_token, String identity){
		
		this.identity		= identity;
		this.access_token	= access_token;
		this.name 			= Connector.FACEBOOK_CONN;
		
		
	}
	
	
	public void setToken(String access_token) {
		this.access_token = access_token;
		
	}
	public String getToken() {
		return access_token;
	}
	public void setConnectorName(String name) {
		this.name= name;
		
	}
	public String getConnectorName() {
		return name;
	}
	
	
	public String getSocialData(String path) throws MissingTokenExeptions {
		
		if (access_token==null) throw new MissingTokenExeptions();
		
		
		WebRequestor requestor = new DefaultWebRequestor();
		try {
		
			Response response = requestor.executeGet(genURL(path));
			return response.getBody();
		}
		catch (IOException e) {}
		return null;
		
		
		// TODO Auto-generated method stub
		//return myFeedConnectionPage.toString();
	}
	public Map<String, String> requireAccessToken() {
		HashMap<String, String > credential = new HashMap<String, String>();
		try {
		
			WebRequestor doGet = new DefaultWebRequestor();
			Response response = doGet.executeGet("http://wd.teamlife.it/test.php");
			System.out.println("Auth: " +response.getBody()+ " code:" + response.getStatusCode());
			
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		
		
		// this should be provided by an external component
		credential.put(AUTH_TOKEN, "");
		
		return credential;
	}
	public void disconnect() {
		access_token="";
	}

	public void setMaxPostLimit(int postLimit) {
		 this.maxPostLimit = postLimit;
	}
	
	public void setParameter(Object p){
		if (parameters == null){
			parameters= new ArrayList<Object>();
		}
		
		parameters.add(p);
	}
	
	
	
	public String genURL(String path){
		String opt= "";
		if (parameters!=null){
			Iterator it = parameters.iterator();
			while (it.hasNext()){
				Parameter p = (Parameter) it.next();
				opt += "&" +p.name +"=" + p.value;
			}
		}
		return "https://graph.facebook.com/"+path+"?access_token="+access_token+opt ;
	}


	public void resetParameters() {
		parameters = null;
	}
	
	
	
	

}
