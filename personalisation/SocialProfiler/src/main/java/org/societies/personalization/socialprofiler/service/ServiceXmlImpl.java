/**
 * 
 */
package org.societies.personalization.socialprofiler.service;


import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.SocketAddress;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Hashtable;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;



public class ServiceXmlImpl implements ServiceXml{

//	private static final Logger logger = Logger.getLogger(ServiceXml.class);
//	private Properties props = new Properties();  
//	private static int indent=0;
//	
//	public ServiceXmlImpl() {
//		super();
//		
//	}
//
//	
//	public void enableProperties(){
//		try{
//			InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("config.properties");
//            props.load(inputStream);
//            inputStream.close();
//
//		}catch(IOException e){
//			logger.fatal("ERROR : impossible to read the properties file");
//		}
//	}
//	
//	
//	public Properties getProperties(){
//		return props;
//	}
//	
//	
//	public ArrayList<String> getListOfUsersFromCAPlatform(String service) {
//		ArrayList<String> listOfUsers=new ArrayList<String>();
//		           
//		try{
//			
//			URLConnection conn = this.setURLConnection(props.getProperty("Url.getUsers")+"?service="+service);
//			
//			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//			factory.setValidating( false );
//			Document xml_teamlife = factory.newDocumentBuilder().parse(conn.getInputStream());
//			NodeList list_users=xml_teamlife.getElementsByTagName("parS");
//			logger.debug("extracting users for "+service+" service from CA platform");
//					
//			for (int s = 0; s < list_users.getLength(); s++) {
//				org.w3c.dom.Node user = list_users.item(s);
//				if (user.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE){
//					NodeList list=user.getChildNodes();
//					org.w3c.dom.Node name=list.item(3); //<par n=userName> user_id   
////					FIXME: limitato a licciardi per il debug
////					if (name.getTextContent().equals("licciardi") || name.getTextContent().equals("lukostaz")){
//						listOfUsers.add(name.getTextContent());
//						logger.info("User " +name.getTextContent() + " added to list.");
////					}
//				}  
//			}
//		
//		}catch (IOException e){
//			logger.error("IOException : cannot retrieve usernames from CA platform. "+ e.getMessage());
//		}catch (Exception e){
//			logger.error("Error while retrieving users from CA platform: "+ e.getMessage() );
//		}
//		return listOfUsers;
//	}
//
//		
//	
//	private URLConnection setURLConnection(String urlStr) throws Exception{
//		
//		 boolean isDirectCAPConnection = new Boolean(props.getProperty("http.directConnectionToCAP").toLowerCase().trim()).booleanValue();
////		 logger.info("Direct connection to cap: " + props.getProperty("http.directConnectionToCAP") + "||" + isDirectCAPConnection);
//		
//		URL url = new URL(urlStr);
//		logger.debug("url is "+ url.toString());
//		URLConnection conn;
//		
//		// proxy settings
//		if (props.getProperty("http.proxySet").trim().equalsIgnoreCase("true") && !isDirectCAPConnection){
//			Authenticator.setDefault(
//					new Authenticator() {
//						public PasswordAuthentication getPasswordAuthentication() {
//							return new PasswordAuthentication(
//									props.getProperty("http.proxy.usr"), props.getProperty("http.proxy.pwd").toCharArray());
//						}
//					}
//				
//			);
//			SocketAddress address = new InetSocketAddress(props.getProperty("http.proxyHost"), new Integer(props.getProperty("http.proxyPort")).intValue());
//			
//			Proxy proxy;
//			proxy = new Proxy(Proxy.Type.HTTP, address);
//			conn = url.openConnection(proxy);
//		}else{
//			conn = url.openConnection();
//		}
//		return conn;
//	}
//
//
//	public void initialiseCredentialTable(
//												Hashtable<String, ArrayList<String>> credentials, 
//												String service,
//												ArrayList<String> list_users) {
//											
//		
//		//emptying cache in order to avoid overriding
//		for (int j=credentials.size()-1;j>=0;j--){
//			credentials.remove(j);
//		}
//		
//		for (int s=0;s<list_users.size();s++){
//			try{
//				
//				String Url_getServiceProfile=props.getProperty("Url.getServiceProfile");
//				URLConnection conn = this.setURLConnection(Url_getServiceProfile+"?username="+list_users.get(s)+"&service="+service);
//				
//				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//				factory.setValidating( false );
//				Document user_credentials= factory.newDocumentBuilder().
//					parse(conn.getInputStream());
//				logger.debug("---******---extracting credentials for user "+list_users.get(s)+" for service = "+service);
//				
//				
//				NodeList list_credentials=user_credentials.getElementsByTagName("par");
//				String session_key="",session_secret="",user_id="";
//				for (int i=0;i<list_credentials.getLength();i++){
//					org.w3c.dom.Node param=list_credentials.item(i);
//					String attribute=param.getAttributes().getNamedItem("n").getNodeValue();
//					if (attribute.equals("session_secr")){
//						//logger.debug("session_secret " +param.getTextContent());
//						session_secret=param.getTextContent();
//					}else if (attribute.equals("session_key")){
//						//logger.debug("session_key "+param.getTextContent());
//						session_key=param.getTextContent();
//					}else if (attribute.equals("user")){
//						//logger.debug("user id "+param.getTextContent());
//						user_id=param.getTextContent();
//					}
//				}
//				logger.debug("adding information to Arraylist , before wrapping it into the HashTable");
//				ArrayList<String> temp= new ArrayList<String>();
//				temp.add(session_secret);
//				temp.add(session_key);
//				temp.add(list_users.get(s));
//				temp.add(user_id);
//				logger.debug("adding information to hashtable");
//				if (user_id!=null)
//					credentials.put(user_id,temp);
//			}catch (SAXException e){
//				logger.error("SAXException " + e.getMessage());
//			}catch (IOException e){
//				logger.error("IOException : ERROR while tryong to connect to CA Platform to retrieve credentials: " + e.getMessage());
//			}catch (ParserConfigurationException e){
//				logger.error("ParserConfigurationException " + e.getMessage());
//			} catch (Exception e) {
//				logger.error("Error while retrieving Facebook credentials from CA platform: "+ e.getMessage() );
//			}
//		}
//	}
//
//	
////	public void initialiseBlogTable(
////			Hashtable<String, String> credentials, String service,
////			ArrayList<String> list_users) {
////		//emptying cache in order to avoid overriding
////		for (int j=credentials.size()-1;j>=0;j--){
////			credentials.remove(j);
////		}
////		
////		for (int s=0;s<list_users.size();s++){
////			try{
////				String userName=list_users.get(s);
////				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
////				factory.setValidating( false );
////				String Url_getServiceProfile=props.getProperty("Url.getServiceProfile");
////				Document user_credentials= factory.newDocumentBuilder().
////					parse(Url_getServiceProfile+"?username="+list_users.get(s)+"&service="+service);
////				logger.debug("---******---extracting credentials for user "+userName+" for service = "+service);
////				NodeList list_credentials=user_credentials.getElementsByTagName("par");
////				org.w3c.dom.Node param=list_credentials.item(0);
////				String password=param.getFirstChild().getNodeValue();
////				//logger.debug("password "+password);
////				logger.debug("adding information to hashtable");
////				credentials.put(userName,password);
////			}catch (SAXException e){
////				logger.error("SAXException "+e);
////			}catch (IOException e){
////				logger.error("IOException : ERROR while tryong to connect to beta.teamlife.it to retrieve credentials"+e);
////			}catch (ParserConfigurationException e){
////				logger.error("ParserConfigurationException "+e);
////			}
////		}
////	}
//	
//	
//	public String getIdForCAUser(String userName,String service) {
//		String Url_getServiceProfile=props.getProperty("Url.getServiceProfile");
//		URLConnection conn =null;
//		try {
//			conn = this.setURLConnection(Url_getServiceProfile+"?username="+ userName +"&service="+service);
//		} catch (Exception e1) {
//			logger.error("Cannot set connection: " + e1.getMessage());
//			return "user_not_found_on_CA";
//		}
//		
//		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//		factory.setValidating( false );
//		
//		
//		Document doc = null;
//		try {
//			doc = factory.newDocumentBuilder().
//				parse(conn.getInputStream());
//		} catch (Exception e) {
//			logger.error("unable to retrieve credentials from beta for user "+userName+" and service "+service+" reason: "+e.getMessage());
//			return "user_not_found_on_CA";
//		}
//		String userId = null;
//		NodeList list=doc.getElementsByTagName("par");
//		for (int i=0;i<list.getLength();i++){
//			org.w3c.dom.Node param=list.item(i);
//			String attribute=param.getAttributes().getNamedItem("n").getNodeValue();
//			if (attribute.equals("user")){
//				userId=param.getTextContent();
//			}
//		}
//		if (userId==null) userId="user_not_found_on_CA";
//		return userId;
//	}
//	
//	
//	public String getCANameFromCredentials(Hashtable<String, ArrayList<String>> credentials,String userId){
//		ArrayList<String> user_keys=credentials.get(userId);
//		if (user_keys!=null){
//			return user_keys.get(2);
//		}
//		return "null CA Name-"+userId;
//	}
//	
//	
//	public String getBlogInfoFromCredentials(Hashtable<String, String> credentials,String userCA){
//		String info=credentials.get(userCA);
//		if (info!=null){
//			return info;
//		}
//		return "null_info";
//	}
//	
//	
//	public boolean checkIfUserExists(Hashtable<String, ArrayList<String>> credentials, String service,String id){
//		
//		ArrayList<String> user_keys=credentials.get(id);
//		logger.debug("checking if user "+id+" exists on CA platform for service "+service);
//		if (user_keys!=null){
//			 logger.debug("user id:"+id+" CA_name:"+user_keys.get(2)+" was found on AUP , CA platform"); 	
//			 //logger.debug("user "+id+" was found" );
//			 //logger.debug("session_secret of arraylist for user is "+user_keys.get(0));
//	         //logger.debug("session_key of arraylist for user is "+user_keys.get(1));
//	         //logger.debug("CA username of arraylist for user is "+user_keys.get(2));
//	         return true;
//		}else{
//			logger.debug("user "+id+" was not found on AUP, CA platform");
//			return false;
//		}
//	}
//
//	
//	public FacebookXmlRestClient getFacebookClient(Hashtable<String, ArrayList<String>> credentials,
//			String user_id){
//		logger.debug("Generating FACEBOOK Client for user "+user_id);
//		logger.debug("retrieving user keys....");
//		ArrayList<String> user_keys=credentials.get(user_id);
//		if (user_keys==null){
//			logger.error("unable to retrieve user keys - impossible to create facebook client");
//		}else{
//			logger.debug("user keys retrieved successfully");
//		}
//		String session_secret=user_keys.get(0);
//		String session_key=user_keys.get(1);
//		String api_key=props.getProperty("api_key");
//		
//		FacebookXmlRestClient client = new FacebookXmlRestClient (api_key,session_secret,session_key);
//		
//		if (props.getProperty("http.proxySet").trim().equalsIgnoreCase("true")){
//			Authenticator.setDefault(
//					new Authenticator() {
//						public PasswordAuthentication getPasswordAuthentication() {
//							return new PasswordAuthentication(
//									props.getProperty("http.proxy.usr"), props.getProperty("http.proxy.pwd").toCharArray());
//						}
//					}
//				
//			);
//			
//			
//			SocketAddress address = new InetSocketAddress(props.getProperty("http.proxyHost"), new Integer(props.getProperty("http.proxyPort")).intValue());
//			Proxy proxy = new Proxy(Proxy.Type.HTTP, address);
////			client.setProxy(proxy);
//
//		}
//		
//		if (client==null){
//			logger.error("impossible to create facebook client ");
//		}else{
//			logger.info("facebook client created successfully for user " + user_id);
//		}
//		return client;
//	}
//	
//	 
//	public ArrayList<String> friendsGetFacebook(FacebookXmlRestClient client){
//		ArrayList<String> list_friends=new ArrayList<String>();
//		try{
//			logger.debug("extracting friends list of current user from facebook");
//			Document friends=client.friends_get();
//			if (friends==null){
//				logger.warn("friends list is null");
//			}else{
//				logger.debug("friends list extracted succesfully");
//			}
//			logger.debug("extracting friends from friends list");
//			NodeList list=friends.getElementsByTagName("uid");
//					
//			for (int s = 0; s < list.getLength(); s++) {
//				org.w3c.dom.Node friend = list.item(s);
//				if (friend.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE){
//					logger.debug("friend name : "+friend.getTextContent());
//					list_friends.add(friend.getTextContent());
//				}
//			}
//		}catch (FacebookException e){
//			logger.error("impossible to retrieve the friends of an user: " + e.getMessage());
//		}catch (Exception e){
//			logger.error("unpredicted exception : "+e.getMessage());
//		}
//		
//		return list_friends;
//	}
//	
//	 
//	public Hashtable<String,ArrayList<String>> getFanPagesDataFacebook(FacebookXmlRestClient client, String userId,
//			ArrayList <String> pages){
//		  
//		Hashtable <String,ArrayList<String>> pages_data= new Hashtable <String,ArrayList<String>>();
//		logger.debug("retrieving the list of fanapges for user "+userId);
//		try{
//			long user=Long.parseLong(userId);
//			EnumSet <PageProfileField> fields=EnumSet.of(PageProfileField.PAGE_ID,
//					PageProfileField.NAME,PageProfileField.TYPE);
//			if (fields==null){
//				logger.error("ERROR fields set seem empty");
//			}
//			Document pages_list=client.pages_getInfo(user, fields);
////			printXMLElements(pages_list.getDocumentElement()); // FIXME: da commentare
//			NodeList list=pages_list.getElementsByTagName("page");
//			for (int s = 0; s < list.getLength(); s++) {
//				org.w3c.dom.Node page = list.item(s);
//				if (page.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE){
//					NodeList list1=page.getChildNodes();
//					logger.debug("page id  "+list1.item(1).getTextContent());
//					logger.debug("page name  "+list1.item(2).getTextContent());
//					logger.debug("page actor "+list1.item(3).getTextContent());
//					logger.debug("adding information to Arraylist , before wrapping it into the HashTable");
//					ArrayList<String> temp= new ArrayList<String>();
//					temp.add(list1.item(2).getTextContent());
//					temp.add(list1.item(3).getTextContent());
//					logger.debug("adding information to hashtable pages_data");
//					pages_data.put(list1.item(1).getTextContent(),temp);
//					logger.debug("adding also information to the arraylist given as parameter");
//					pages.add(list1.item(1).getTextContent());
//				}
//			}
//		}catch(FacebookException e){
//			logger.error("ERROR while retrieving the list of groups os user "+userId+" "+e);
//		}catch (Exception e) {
//			logger.error("Communication Error with Facebook Platform while fetching user "+userId+" pages_getInfo(): "+e);
//		}
//		return pages_data;
//	}
//	
//	 
//	public Hashtable<String,ArrayList<String>> getGroupsDataFacebook(FacebookXmlRestClient client,
//			String userId,ArrayList <String> groups){
//		  
//		Hashtable <String,ArrayList<String>> groups_data= new Hashtable <String,ArrayList<String>>();
//		logger.debug("retrieving the list of groups for user "+userId);
//		try{
//			long user=Long.parseLong(userId);
//			Document group_data=client.groups_get(user, null);
////			printXMLElements(group_data.getDocumentElement());
//			NodeList list=group_data.getElementsByTagName("group");
//			for (int s = 0; s < list.getLength(); s++) {
//				org.w3c.dom.Node page = list.item(s);
//				if (page.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE){
//					NodeList list1=page.getChildNodes();
//					logger.debug("group id         "+list1.item(0).getTextContent());
//					logger.debug("group name       "+list1.item(1).getTextContent());
//					logger.debug("page description "+list1.item(6).getTextContent());
//					logger.debug("page type        "+list1.item(7).getTextContent());
//					logger.debug("page subtype     "+list1.item(8).getTextContent());
//					logger.debug("page creator     "+list1.item(10).getTextContent());
//					logger.debug("page update time "+list1.item(11).getTextContent());
//					logger.debug("adding information to Arraylist , before wrapping it into the HashTable");
//					ArrayList<String> temp= new ArrayList<String>();
//					temp.add(list1.item(1).getTextContent());
//					temp.add(list1.item(6).getTextContent());
//					temp.add(list1.item(7).getTextContent());
//					temp.add(list1.item(8).getTextContent());
//					temp.add(list1.item(10).getTextContent());
//					temp.add(list1.item(11).getTextContent());
//					logger.debug("adding information to hashtable groups_data");
//					groups_data.put(list1.item(0).getTextContent(),temp);
//					logger.debug("adding also information to the arraylist given as parameter");
//					groups.add(list1.item(0).getTextContent());
//					
//				}
//			}
//			
//		}catch(FacebookException e){
//			logger.error("ERROR while retrieving the list of groups os user "+userId+" "+e);
//		}
//		return groups_data;
//	}
//		
//	 
//	public void readFrequency(int frequency){
//		int days=frequency / (24*3600);
//		int hours=(frequency % (24*36000))/3600;
//		int minutes=((frequency%(24*36000))%3600)/60;
//		int seconds=(((frequency%(24*36000))%3600)%60);
//		// this method doesn't take into consideration the present , it only shows the frequency based on posts
//		logger.debug("frequency is "+days+" days, "+hours+" hours, "+minutes+" minutes, "+seconds+" seconds" );
//	}
//		
//	
//	public void printXMLElements(Element el) {
//		String a="";
//		for (int j=1;j<=indent;j++){
//			  a=a+" ";
//		}  
//		logger.debug(a+el.getTagName());
//		NamedNodeMap nnm = el.getAttributes();
//		for (int i = 0 ; i < nnm.getLength() ; i++) {
//		 	logger.debug(" [ " + nnm.item(i).getNodeName() + " : "+ nnm.item(i).getNodeValue() + " ] ");
//		}
//		NodeList nl = el.getChildNodes();
//		for (int i = 0 ; i < nl.getLength() ; i++) {
//				 
//			 if (nl.item(i).getNodeType() == org.w3c.dom.Node.ELEMENT_NODE){
//				 indent++;
//				 printXMLElements((Element)nl.item(i));
//			 } else if (nl.item(i).getNodeType() == org.w3c.dom.Node.TEXT_NODE){
//				 logger.debug("   "+nl.item(i).getNodeValue());
//			 }
//		}
//		indent--;
//	}
//
//	
//	@Deprecated
//	public String getStringFromDocument(Document doc)
//	{
//	    try
//	    {
//	       DOMSource domSource = new DOMSource(doc);
//	       StringWriter writer = new StringWriter();
//	       StreamResult result = new StreamResult(writer);
//	       TransformerFactory tf = TransformerFactory.newInstance();
//	       Transformer transformer = tf.newTransformer();
//	       transformer.transform(domSource, result);
//	       return writer.toString();
//	    }
//	    catch(TransformerException ex)
//	    {
//	       logger.error("ERROR while printing Document to String "+ex);
//	       ex.printStackTrace();
//	       return null;
//	    }
//	}
//	
//	
//	@Deprecated	
//	public ArrayList<String> getFanPagesFacebook(FacebookXmlRestClient client, String userId){
//		ArrayList <String> list_pages=new ArrayList<String> ();
//		logger.debug("retrieving the list of fanapges for user "+userId);
//		try{
//			long user=Long.parseLong(userId);
//			EnumSet <PageProfileField> fields=EnumSet.of(PageProfileField.PAGE_ID);
//			if (fields==null){
//				logger.error("ERROR fields set seem empty");
//			}
//			Document pages_list=client.pages_getInfo(user, fields);
//			NodeList list=pages_list.getElementsByTagName("page_id");
//			
//			for (int s = 0; s < list.getLength(); s++) {
//				org.w3c.dom.Node page = list.item(s);
//				if (page.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE){
//					logger.debug("page id : "+page.getTextContent());
//					list_pages.add(page.getTextContent());
//				}
//			}
//		}catch(FacebookException e){
//			logger.error("ERROR while retrieving the list of groups os user "+userId+" "+e);
//		}
//		return list_pages;
//	}
//	
//	
//	@Deprecated
//	public ArrayList<String> getFanPageData(FacebookXmlRestClient client,String fanPageId){
//		ArrayList <String> fanPage_data=new ArrayList<String> ();
//		logger.debug("retrieving data for fanPage "+fanPageId);
//		try{
//			
//			long page=Long.parseLong(fanPageId);
//			Collection <Long> fanPage=new ArrayList<Long>();
//			fanPage.add(page);
//			EnumSet <PageProfileField> fields_page=EnumSet.of(PageProfileField.PAGE_ID
//					,PageProfileField.NAME,PageProfileField.TYPE
//			);
//			Document data=client.pages_getInfo(fanPage, fields_page);
//			NodeList list1=data.getFirstChild().getFirstChild().getChildNodes();
//			
//			logger.debug("page name  "+list1.item(2).getTextContent());
//			fanPage_data.add(list1.item(2).getTextContent());
//			logger.debug("page actor "+list1.item(3).getTextContent());
//			fanPage_data.add(list1.item(3).getTextContent());
//		
//		}catch(FacebookException e){
//			logger.error("error while retrieving the data from page "+fanPageId);
//		}
//		return fanPage_data;
//	}
//	
//	
//	@Deprecated	
//	public ArrayList<String> getGroupsFacebook(FacebookXmlRestClient client, String userId){
//		ArrayList <String> list_groups=new ArrayList<String> ();
//		logger.debug("retrieving the list of groups for user "+userId);
//		try{
//			long user=Long.parseLong(userId);
//			Document groups=client.groups_get(user, null);
//			NodeList list=groups.getElementsByTagName("gid");
//			for (int s = 0; s < list.getLength(); s++) {
//				org.w3c.dom.Node friend = list.item(s);
//				if (friend.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE){
//					logger.debug("group id : "+friend.getTextContent());
//					list_groups.add(friend.getTextContent());
//				}
//			}
//		}catch(FacebookException e){
//			logger.error("ERROR while retrieving the list of groups os user "+userId+" "+e);
//		}
//		return list_groups;
//	}
//	
//	
//	@Deprecated
//	public ArrayList<String> getGroupData(FacebookXmlRestClient client,String userId,String groupId){
//		ArrayList <String> group_data=new ArrayList<String> ();
//		logger.debug("retrieving data for group "+groupId+" using user "+userId);
//		try{
//			long user=Long.parseLong(userId);
//			long group=Long.parseLong(groupId);
//			Collection <Long> gr=new ArrayList<Long>();
//			gr.add(group);
//			Document data=client.groups_get(user, gr);
//			NodeList list=data.getElementsByTagName("name");
//			org.w3c.dom.Node realName = list.item(0);
//			logger.debug("group name : "+realName.getTextContent());
//			group_data.add(realName.getTextContent());
//			
//			NodeList list1=data.getElementsByTagName("description");
//			org.w3c.dom.Node description = list1.item(0);
//			logger.debug("group description : "+description.getTextContent());
//			group_data.add(description.getTextContent());
//			
//			NodeList list2=data.getElementsByTagName("group_type");
//			org.w3c.dom.Node type = list2.item(0);
//			logger.debug("group type : "+type.getTextContent());
//			group_data.add(type.getTextContent());
//			
//			NodeList list3=data.getElementsByTagName("group_subtype");
//			org.w3c.dom.Node subtype = list3.item(0);
//			logger.debug("group subtype : "+subtype.getTextContent());
//			group_data.add(subtype.getTextContent());
//			
//			NodeList list4=data.getElementsByTagName("creator");
//			org.w3c.dom.Node creator = list4.item(0);
//			logger.debug("group cretor : "+creator.getTextContent());
//			group_data.add(creator.getTextContent());
//			
//			NodeList list5=data.getElementsByTagName("update_time");
//			org.w3c.dom.Node time = list5.item(0);
//			logger.debug("group update time : "+time.getTextContent());
//			group_data.add(time.getTextContent());
//			
//			
//			
//			
//			
//		}catch(FacebookException e){
//			logger.error("error while retrieving the data from group "+groupId+" from user "+userId);
//		}
//		return group_data;
//	}
//	
//		
//	public Collection <ProfileField> createFieldsForUserInfo(){
//		logger.debug("creating parameters for the retrieving GeneralInfo and Interests");
//		Collection <ProfileField> fields = new ArrayList<ProfileField>();
//		//Interests fields
//		fields.add(ProfileField.ACTIVITIES);
//		fields.add(ProfileField.INTERESTS);
//		fields.add(ProfileField.MUSIC);
//		fields.add(ProfileField.MOVIES);
//		fields.add(ProfileField.BOOKS);
//		fields.add(ProfileField.QUOTES);
//		fields.add(ProfileField.ABOUT_ME);
//		fields.add(ProfileField.PROFILE_UPDATE_TIME);
//		//GeneralInfo fields
//		fields.add(ProfileField.FIRST_NAME);
//		fields.add(ProfileField.LAST_NAME);
//		fields.add(ProfileField.BIRTHDAY);
//		fields.add(ProfileField.SEX);
//		fields.add(ProfileField.HOMETOWN_LOCATION);
//		fields.add(ProfileField.CURRENT_LOCATION);
//		fields.add(ProfileField.POLITICAL);
//		fields.add(ProfileField.RELIGION);
//		return fields;
//	}
//	
//	
//	public Collection <ProfileField> createUpdateFieldForUserInfo(){
//		Collection <ProfileField> fields = new ArrayList<ProfileField>();
//		fields.add(ProfileField.PROFILE_UPDATE_TIME);
//		return fields;
//	}
//	
//	
//	//******************************************************//
//	//*********DEBUG,ANALYSIS *******************//
//	//********************************************************//
//	
//	
//	public void getStreamFacebook(FacebookXmlRestClient client,String userId){
//		try{
//			long current_user=Long.parseLong(userId);
//			ArrayList <Long> list_source=new ArrayList<Long>();
//			list_source.add(current_user);
//			//Document doc= client.stream_get(current_user,list_source, null, null,20, null, null);
//			Document doc= client.stream_get(null,null, null, null,2000, null, null);
//			NodeList posts=doc.getElementsByTagName("stream_post");
//			int number_posts=posts.getLength();
//			@SuppressWarnings("unused")
//			String viewer="",source="",type="";
//			logger.debug("number of posts is "+number_posts);
//			logger.debug("start");
//			
//			for(int i=0;i<posts.getLength();i++){
//				NodeList list_items=posts.item(i).getChildNodes();
//				viewer=list_items.item(1).getTextContent();
//				source=list_items.item(2).getTextContent();
//				type=list_items.item(3).getTextContent();
//				
//				if (list_items.item(3).getNodeName().equals("type")){
//					//condition to have a stream post is that is has a type tag in its xml definition
//					if  (type.equals("56")){
//						logger.debug("###########################################################");
////						printXMLElements((Element)posts.item(i));
//					}
//				}else{
//					logger.debug("!!!!!!!!!!!!!!!!!!!!post type"+list_items.item(3).getNodeName());
//				}
//			}
//			
//		}catch(FacebookException e){
//			logger.debug("ERROR facebook exception while retrieving the stream from facebook for user "+userId);
//		}
//	}
//	
//	
//	//******************************************************//
//	//*********TESTING , UNDER CONSTRUCTION *******************//
//	//********************************************************//
//	
//		
//
//	public ArrayList<String>  analyseStreamFacebook(FacebookXmlRestClient client,String userId){
//		ArrayList <String> result=new ArrayList<String> ();
//		try{
//			logger.debug("recovering all the stream for the user");
//			Document doc= client.stream_get(null, null, null, null,10000, null, null);
//			int number_narcissism=0;
//			int number_superActive=0;
//			int number_photoManiac=0;
//			int frequency=0;
//			String lastTime="";
//			String createTime="";
//			String viewer="",source="",type="";
//			
//			NodeList posts=doc.getElementsByTagName("stream_post");
//			for(int i=0;i<posts.getLength();i++){
//				NodeList list_items=posts.item(i).getChildNodes();
//				
//				viewer=list_items.item(1).getTextContent();
//				source=list_items.item(2).getTextContent();
//				type=list_items.item(3).getTextContent();
//				if (list_items.item(3).getNodeName().equals("type")){
//					
//					if  (type.equals("46")){
//						
//						org.w3c.dom.Element comments=(Element)list_items.item(12);
//						NodeList comment_list=comments.getElementsByTagName("comment");
//						for (int k=0;k<comment_list.getLength();k++){
//							org.w3c.dom.Node comment=comment_list.item(k).getFirstChild();
//							if(comment.getNodeName().equals("fromid")){
//								if (comment.getTextContent().equals(viewer)){
//									logger.debug("Super Active Altruist Maniac interaction - comment reply");
//									number_superActive++;
//								}else{
//									logger.debug("other user reply - has to be actualised");
//								}
//							}
//						}
//													
//						if (viewer.equals(source)) {
//							logger.debug("Narcissism Profile interaction");
//							createTime=list_items.item(16).getTextContent();
//							logger.debug("createtime"+createTime+" "+list_items.item(16).getNodeName());
//							//TODO a second condition has to be added when using this algo with temporizatr
//							if (number_narcissism==0){
//								logger.debug("first interaction");
//								lastTime=createTime;
//								logger.debug("createTime="+createTime+" lastTime="+lastTime);
//								number_narcissism++;
//							}else{
//								logger.debug("updating frequency");
//								logger.debug("old frequency = "+frequency+" number= "+number_narcissism+
//									" lastTime="+lastTime+" createTime="+createTime );
//								frequency=(Integer.parseInt(lastTime)-(frequency*(number_narcissism-1))-Integer.parseInt(createTime))/(number_narcissism);
//								number_narcissism++;
//								logger.debug("new frequency = "+frequency);
//							}
//						}	
//					}else if (type.equals("247")){
//						if (viewer.equals(source)){
//							logger.debug("Photo Maniac interaction");
//							number_photoManiac++;
//						}
//					}
//					
//				}
//			}	
//			logger.debug("user "+viewer+" had "+number_photoManiac+" interactions as Photo Maniac");
//			logger.debug("user "+viewer+" had "+number_superActive+" interactions as Super-Active Altruist Maniac");
//			logger.debug("user "+viewer+" had "+number_narcissism+" interactions as Narcissism Maniac");
//			readFrequency(frequency);
//			long unixTime = Long.parseLong(lastTime) ;  
//			long timestamp = unixTime * 1000;  // msec  
//			java.util.Date d = new java.util.Date(timestamp);  
//			logger.debug( "last time = " + d.toString()  );  
//			int prevision=Integer.parseInt(lastTime)+frequency;
//			long unixTime1 = prevision ;
//			long timestamp1 = unixTime1 * 1000;  // msec  
//			java.util.Date d1 = new java.util.Date(timestamp1);  
//			logger.debug("user is supposed to change his status again on "+d1.toString());
//			//String number=(String)number_narcissism;
//			result.add(String.valueOf(frequency));
//			result.add(lastTime);
//			result.add(String.valueOf(number_narcissism));
//		}catch(FacebookException e){
//			logger.error("facebook exception while analysing the stream of the user "+userId+" "+e);
//		}
//		return result;
//	}
//	
//	
//	public ArrayList<String>  initialStreamFacebook(FacebookXmlRestClient client,String userId){
//		ArrayList <String> result=new ArrayList<String> ();
//		try{
//			logger.debug("recovering the initial stream of the user");
//			Document doc= client.stream_get(null, null, null, null,5, null, null);
//			int number_narcissism=0;
//			int number_superActive=0;
//			int number_photoManiac=0;
//			int frequency=0;
//			String lastTime="";
//			String createTime="";
//			String viewer="",source="",type="";
//			
//			NodeList posts=doc.getElementsByTagName("stream_post");
//			for(int i=0;i<posts.getLength();i++){
//				NodeList list_items=posts.item(i).getChildNodes();
//				viewer=list_items.item(1).getTextContent();
//				source=list_items.item(2).getTextContent();
//				type=list_items.item(3).getTextContent();
//				if (list_items.item(3).getNodeName().equals("type")){
//					//condition to have a stream post is that is has a type tag in its xml definition
//					if  (type.equals("46")){
//						logger.debug("analysing stream post of type 46 a.k.a Status ");
//						
//						
//						org.w3c.dom.Element comments=(Element)list_items.item(12);
//						NodeList comment_list=comments.getElementsByTagName("comment");
//						for (int k=0;k<comment_list.getLength();k++){
//							org.w3c.dom.Node comment=comment_list.item(k).getFirstChild();
//							if(comment.getNodeName().equals("fromid")){
//								//comments of a particular stream post
//								if (comment.getTextContent().equals(viewer)){
//									logger.debug("Super Active Altruist Maniac interaction - comment reply");
//									number_superActive++;
//								}else{
//									logger.debug("other user reply - has to be actualised");
//								}
//							}
//						}
//													
//						if (viewer.equals(source)) {
//							logger.debug("Narcissism Profile interaction");
//							createTime=list_items.item(16).getTextContent();
//							logger.debug("createtime"+createTime+" "+list_items.item(16).getNodeName());
//							//TODO a second condition has to be added when using this algo with temporizatr
//							if (number_narcissism==0){
//								logger.debug("first interaction");
//								lastTime=createTime;
//								logger.debug("createTime="+createTime+" lastTime="+lastTime);
//								number_narcissism++;
//							}else{
//								logger.debug("updating frequency");
//								logger.debug("old frequency = "+frequency+" number= "+number_narcissism+
//									" lastTime="+lastTime+" createTime="+createTime );
//								frequency=(Integer.parseInt(lastTime)-(frequency*(number_narcissism-1))-Integer.parseInt(createTime))/(number_narcissism);
//								number_narcissism++;
//								logger.debug("new frequency = "+frequency);
//							}
//						}	
//					}else if (type.equals("247")){
//						if (viewer.equals(source)){
//							logger.debug("Photo Maniac interaction");
//							number_photoManiac++;
//						}
//					}
//					
//				}
//			}	
//			logger.debug("user "+viewer+" had "+number_photoManiac+" interactions as Photo Maniac");
//			logger.debug("user "+viewer+" had "+number_superActive+" interactions as Super-Active Altruist Maniac");
//			logger.debug("user "+viewer+" had "+number_narcissism+" interactions as Narcissism Maniac");
//			readFrequency(frequency);
//			long unixTime = Long.parseLong(lastTime) ;  
//			long timestamp = unixTime * 1000;  // msec  
//			java.util.Date d = new java.util.Date(timestamp);  
//			logger.debug( "last time = " + d.toString()  );  
//			int prevision=Integer.parseInt(lastTime)+frequency;
//			long unixTime1 = prevision ;
//			long timestamp1 = unixTime1 * 1000;  // msec  
//			java.util.Date d1 = new java.util.Date(timestamp1);  
//			logger.debug("user is supposed to change his status again on "+d1.toString());
//			//String number=(String)number_narcissism;
//			result.add(String.valueOf(frequency));
//			result.add(lastTime);
//			result.add(String.valueOf(number_narcissism));
//		}catch(FacebookException e){
//			logger.error("facebook exception while analysing the stream of the user "+userId+" "+e);
//		}
//		return result;
//	}	
//	
//			
//	public void userGetInfoFacebook(FacebookXmlRestClient client){
//		try{
//			Long userId=642547991L;
//			//Document info=client.groups_get(userId, null);
//			//long group=15104990418L;
//			//Collection <Long> gr=new ArrayList<Long>();
//			//gr.add(group);
//			//Document doc1=client.groups_get(userId, gr);
//			//printXMLElements(doc1.getDocumentElement());
//			
//			//printXMLElements(info.getDocumentElement());
//			logger.debug("extracting FAN pages");
//		
//			EnumSet <PageProfileField> fields=EnumSet.of(PageProfileField.PAGE_ID
//					//PageProfileField.GENRE,PageProfileField.NAME,PageProfileField.GENERAL_INFO,
//					//PageProfileField.TYPE,PageProfileField.WEBSITE,PageProfileField.PERSONAL_INFO,
//					//PageProfileField.PERSONAL_INTERESTS
//			);
//			//logger.debug("adding fields to fields set");
//			//fields.add(PageProfileField.PAGE_ID);
//			//fields.add(PageProfileField.GENRE);
//			//fields.add(PageProfileField.NAME);
//			if (fields==null){
//				logger.error("ERROR fields set seems empty");
//			}
//			Document info1=client.pages_getInfo(userId, fields);
////			printXMLElements(info1.getDocumentElement());
//		
//			long page=16453049322L;
//			Collection <Long> pages=new ArrayList<Long>();
//			pages.add(page);
//			EnumSet <PageProfileField> fields_page=EnumSet.of(PageProfileField.PAGE_ID
//					,PageProfileField.NAME,PageProfileField.TYPE
//			);
//			Document info2=client.pages_getInfo(pages, fields_page);
////			printXMLElements(info2.getDocumentElement());
//		
//			//NodeList list=info2.getElementsByTagName("page");
//			//org.w3c.dom.Node node = list.item(0);
//			//NodeList list1=node.getChildNodes();
//			NodeList list1=info2.getFirstChild().getFirstChild().getChildNodes();
//			
//			//logger.debug("##"+node.getFirstChild().getTextContent());
//			logger.debug("##"+list1.item(0).getTextContent());
//			logger.debug("##"+list1.item(1).getTextContent());
//			logger.debug("##"+list1.item(2).getTextContent());
//			logger.debug("##"+list1.item(3).getTextContent());
//			//logger.debug("##"+list.item(1).getTextContent());
//			//logger.debug("##"+list.item(2).getTextContent());
//			//logger.debug("##"+list.item(3).getTextContent());
//			
//		}catch(FacebookException e){
//			logger.error("facebook exception "+e);
//		}catch (Exception e){
//			logger.error("Exception "+e);
//		}
//	}
//	
	

}






