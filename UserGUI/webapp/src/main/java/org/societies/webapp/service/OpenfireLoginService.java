package org.societies.webapp.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.internal.comm.ICISCommunicationMgrFactory;
import org.societies.api.internal.comm.ICommManagerController;
import org.societies.domainauthority.registry.DaRegistry;
import org.societies.domainauthority.registry.DaUserRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

/**
 * 
 * @author Maria Mannion
 * 
 */
@Service
public class OpenfireLoginService {

	private static final String OPENFIRE_PLUGIN = "http://%s:9090/plugins/societies/societies";
	
	@Autowired
	private ICommManager commManager;
	@Autowired
	private ICommManagerController commManagerControl;
	@Autowired
	ICISCommunicationMgrFactory ccmFactory;
	@Autowired
	DaRegistry daregistry;
	
	private String xmppDomain;
	
	public ICommManager getCommManager() {
		return commManager;
	}

	public void setCommManager(ICommManager commManager) {
		this.commManager = commManager;
	}
	
	/**
	 * @return the commManagerControl
	 */
	public ICommManagerController getCommManagerControl() {
		return commManagerControl;
	}

	/**
	 * @param commManagerControl the commManagerControl to set
	 */
	public void setCommManagerControl(ICommManagerController commManagerControl) {
		this.commManagerControl = commManagerControl;
	}

	
	public ICISCommunicationMgrFactory getCcmFactory() {
		return ccmFactory;
	}

	public void setCcmFactory(ICISCommunicationMgrFactory ccmFactory) {
		this.ccmFactory = ccmFactory;
	}


	public DaRegistry getDaregistry() {
		return daregistry;
	}

	public void setDaregistry(DaRegistry daregistry) {
		this.daregistry = daregistry;
	}


	private static Logger log = LoggerFactory.getLogger(OpenfireLoginService.class);
	
	public OpenfireLoginService() {
		log.info("OpenfireLoginService constructor");
		
	}
	
	
	public String doLogin(String username, String password)
	{
		
			if (xmppDomain == null)
				xmppDomain = new String(getCommManager().getIdManager().getDomainAuthorityNode().getDomain());
			
			log.info("Loging on to upfire account with details : Username :" + username + "passsword" + password);
			
			////////////////LOGIN ////////////////
			// 	AUTHENTICATE JABBER ID
			Map<String, String> params = new LinkedHashMap<String, String>();
			params.put("username", username);
			params.put("password", password);
			params.put("secret", "defaultSecret");
	
			String xmppUrl = new String();
			xmppUrl = String.format(OPENFIRE_PLUGIN, xmppDomain);
			
			String resp = postData(MethodType.LOGIN, xmppUrl, params);
			try {
				if (resp.isEmpty())
				{
					//model.put("loginError", "Error logging onto openfire Account. Please try again");
					//return new ModelAndView("pilot", model);
					log.info("Error logging onto openfire Account : Empty Response: Url was " + xmppUrl);
					return null;
				}
				//CHECK RESPONSE - DOES ACCOUNT ALREADY EXIST
				Document respDoc = loadXMLFromString(resp);
				if (respDoc.getDocumentElement().getNodeName().equals("error")) {
				//	model.put("loginError", "Username/password incorrect. Please try again");
				//	return new ModelAndView("pilot", model);
					log.info("Username/password incorrect");
					return null;
				}
			} catch (Exception e) {
				e.printStackTrace();
				//model.put("loginError", "Error logging onto openfire Account. Please try again");
				//return new ModelAndView("pilot", model);
				log.info("Error logging onto openfire Account");
				return null;
			}

			
			log.info("Logged on succesffuly");
			
			//GET CONTAINER INFO
			DaUserRecord userRecord = new DaUserRecord();
			userRecord = getDaregistry().getXmppIdentityDetails(username);
	
			if ((userRecord != null) && (userRecord.getStatus() != null))
			{
				if (!userRecord.getStatus().contentEquals("active") 
						|| userRecord.getHost().isEmpty() 
						|| userRecord.getPort().isEmpty()) 
				{
					//CLOUD NODE NOT SETUP YET
					//model.put("loginError", "Cloud node creation not completed");
					//return new ModelAndView("pilot", model);
					log.info("Cloud node creation not completed");
					return null;
				}
			} else { // account doesn't exist
					//CLOUD NODE NOT SETUP YET
					//model.put("loginError", "User Acount Not Found");
				log.info("User Acount Not Found");
					//return new ModelAndView("pilot", model);
				return null;
			}
			
	// 		GET SERVER/PORT NUMBER FROM REGISTRY
			//String redirectUrl = new String();
			//redirectUrl = String.format("http://%s:%s/societies/%s/loginviada.html", userRecord.getHost(), userRecord.getPort(), userRecord.getId());
			//model.put("webappurl", redirectUrl);
			//model.put("name", userName);	
			//return new ModelAndView("loginsuccess", model);
			log.info("Got userid"+ userRecord.getId());
			return userRecord.getId();
		
	}
	
	private static String postData(MethodType method, String openfireUrl, Map<String, String> params) {
		try { 
			StringBuffer data = new StringBuffer();
			for(String s : params.keySet()) {
				String tmp = URLEncoder.encode(s, "UTF-8") + "=" + URLEncoder.encode((String)params.get(s), "UTF-8") +  "&";
				data.append(tmp);
			}
			//ADD METHOD
			String methodStr = URLEncoder.encode("type", "UTF-8") + "=" + URLEncoder.encode(method.toString().toLowerCase(), "UTF-8");
			data.append(methodStr);
			
	        // Send data 
	        URL url = new URL(openfireUrl); 
	        URLConnection conn = url.openConnection(); 
	        conn.setDoOutput(true); 
	        OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream()); 
	        wr.write(data.toString()); 
	        wr.flush(); 
	  
	        // Get the response 
	        BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	        StringBuffer sb = new StringBuffer();
	        String line; 
	        while ((line = rd.readLine()) != null) {  
	        	sb.append(line);
	        } 
	        wr.close(); 
	        rd.close();
	        
	        //RESPONSE CODE
	        return sb.toString();
	        
	    } catch (Exception e) { 
	    	
	    	e.printStackTrace();

	    }
		return ""; 
	}

	private Document loadXMLFromString(String xml) throws Exception{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		InputSource is = new InputSource(new StringReader(xml));
		return builder.parse(is);
	}
	
	private enum MethodType {

        ADD,
        DELETE,
        ENABLE,
        DISABLE,
        UPDATE,
        LOGIN;
	}
	
}
