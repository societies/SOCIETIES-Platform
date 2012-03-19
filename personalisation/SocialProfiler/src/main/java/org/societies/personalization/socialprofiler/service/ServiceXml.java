package org.societies.personalization.socialprofiler.service;


public interface ServiceXml {
	
	
//	/**
//	 * function which enables the properties file
//	 */
//	public void enableProperties();
//	
//	/**
//	 * returns a Properties including the info the config.properties file
//	 * @return
//	 */
//	public Properties getProperties();
//	
//	/**returns the list of users from CA platform using a particular service . this list of users 
//	 * will then be used to determine their credentials in oder to connect to their web account ,
//	 * such as facebook ,flickr , twitter 
//	 *  
//	 * @param service
//	 * @return  ArrayList<String>
//	 * 				the list of users
//	 */
//	public ArrayList<String> getListOfUsersFromCAPlatform(String service);
//	
//	/**initialises the facebook credentials hashtable , which is a pair facebook 
//	 * user id and array list(session key, session secret , CA user name)
//	 * 
//	 */
//	public void initialiseCredentialTable(Hashtable<String,ArrayList<String> > credentials,
//			String Service ,ArrayList<String> list_users );
//	
//	/**
//	 * initialises the blog credentials hashtable , which is actually a pair of user and password for
//	 * the blog service from aup
//	 * @param credentials
//	 * @param service
//	 * @param list_users
//	 */
//	public void initialiseBlogTable(
//			Hashtable<String, String> credentials, String service,
//			ArrayList<String> list_users);
//	
//	/**
//	 * returns an user id using the user CA name and looking on the beta.teamlife.it in order to recover it
//	 * @param userName
//	 * 		  CA user Name
//	 * @param service
//	 * 			type of service , e.g facebooks
//	 * @return userId (= facebook user id)
//	 */
//	public String getIdForCAUser(String userName,String service);
//	
//	/**
//	 * returns the CA user name from the credentials using the user id
//	 * @param credentials
//	 * @param userId
//	 * 			user id from facebook
//	 * @return
//	 */
//	public String getCANameFromCredentials(Hashtable<String, ArrayList<String>> credentials,String userId);
//	
//	/**
//	 * returns a particular piece of information from one of the blog credentials
//	 * @param credentials
//	 * @param userCA
//	 * 			user id from CA platform
//	 * @return
//	 */
//	public String getBlogInfoFromCredentials(Hashtable<String, String> credentials,String userCA);
//	
//	/**
//	 * check if an user identified by its id exists in the credentials
//	 * @param credentials
//	 * 			HashTable String(user id) arrayList<String> other info reagarding the user
//	 * @param service
//	 * 			type of service(e.g facebook)
//	 * @param id
//	 * 			id of the user
//	 * @return boolean , true if found , else if not found
//	 */
//	public boolean checkIfUserExists(Hashtable<String, ArrayList<String>> credentials, String service,String id);
//	
//	/**
//	 * returns a facebook client for an user using its id and the credentials hashtable
//	 * @param credentials
//	 * 			HashTable String(user id) arrayList<String> other info reagarding the user
//	 * @param user_id
//	 * 			id of the user
//	 * @return FacebookXmlRestClient
//	 * 			facebook client type xml
//	 */
//	public FacebookXmlRestClient getFacebookClient(Hashtable<String, ArrayList<String>> credentials,
//			String user_id);
//		
//	/**
//	 * add to interface - returns the list of friends for a certain user in ArrayList format
//	 * @param client
//	 * 			FacebookXmlRestClient
//	 * @return ArrayList <String>
//	 */
//	public ArrayList<String> friendsGetFacebook(FacebookXmlRestClient client);
//		
//	/**
//	 * - this method fills the arraylist given as paremeter with a list of all pages
//	 *existent for a user and returns as a hash all the infromation needed to fill in the tree information
//	 *ADVANTAGE : this method uses only one request for the user
//	 * @param client
//	 * 			FacebookXmlRestClient 
//	 * @param userId
//	 * 			the id of the user
//	 * @param pages
//	 * 			ArrayList <String> list of all the pages ids for the user
//	 * @return HashTable
//	 */
//	public Hashtable<String,ArrayList<String>> getFanPagesDataFacebook(FacebookXmlRestClient client, String userId,
//			ArrayList <String> pages);
//		
//	/**
//	 * this method fills the arraylist given as paremeter with a list of all groups
//	 * existent for a user and returns as a hash all the infromation needed to fill in the tree information
//	 * ADVANTAGE : this method uses only one request for the user
//	 * @param client
//	 * 			FacebookXmlRestClient
//	 * @param userId
//	 * 			id of the user
//	 * @param groups
//	 * 			ArrayList <String> list of groups ids
//	 * @return HashTable
//	 */
//	public Hashtable<String,ArrayList<String>> getGroupsDataFacebook(FacebookXmlRestClient client,
//			String userId,ArrayList <String> groups);
//		
//	/**
//	 * function which allows to read the frequency (frequency is actually the the average 
//	 * difference between 2 last time ,which are in UNIX timestamp)
//	 * @param frequency
//	 */
//	public void readFrequency(int frequency);
//	
//	/**
//	 * prints an XML file with tabulations to log4j
//	 * @param el
//	 *			element of the Document , normally it is the root
//	 */
//	public void printXMLElements(Element el);
//	
//	/**
//	 * prints all the content of the Document into a String which then returns
//	 * @param doc
//	 * 			Document to be read
//	 * @return String
//	 * 			all the document in String format
//	 */
//	public String getStringFromDocument(Document doc);
//		
//	/**
//	 * method retrieving the fan pages ids list as an array list which will be then analysed one by one with other
//	 * method. NOTE .this method is not used for the moment because facebook imposes a limit of request...
//	 * @param client
//	 * 			FacebookXmlRestClient
//	 * @param userId
//	 * 			id of the user
//	 * @return ArrayList <String>
//	 */
//	public ArrayList<String> getFanPagesFacebook(FacebookXmlRestClient client, String userId);
//		
//	/**
//	 * recover data for a particular fanpage
//	 * NOTE .this method is not used for the moment because facebook imposes a limit of request...
//	 * @param client
//	 * 			FacebookXmlRestClient
//	 * @param fanPageId
//	 * 			id of a fanpage
//	 * @return ArrayList <String>
//	 */
//	public ArrayList<String> getFanPageData(FacebookXmlRestClient client,String fanPageId);
//	
//	/**method retrieving the groups ids list as an array list which will be then analysed one by one with other
//	 * method. NOTE .this method is not used for the moment because facebook imposes a limit of request...
//	 * 
//	 * @param client
//	 * 			FacebookXmlRestClient
//	 * @param userId
//	 * 			id of the user
//	 * @return ArrayList <String>
//	 */
//	public ArrayList<String> getGroupsFacebook(FacebookXmlRestClient client, String userId);
//		
//	/**recover data for a particular group
//	 * NOTE .this method is not used for the moment because facebook imposes a limit of request...ZZ
//	 * 
//	 * @param client
//	 * 			FacebookXmlRestClient
//	 * @param userId
//	 * 			id of the user
//	 * @param groupId
//	 * 			id of the group
//	 * @return
//	 */
//	public ArrayList<String> getGroupData(FacebookXmlRestClient client,String userId,String groupId);
//		
//	/**
//	 * create a Collection of ProfileFields needed to retrieve User Info for Interests and GeneralInfo
//	 * @return Collection <ProfileField>
//	 */
//	public Collection <ProfileField> createFieldsForUserInfo();
//	
//	/**
//	 * create a Collection of ProfileFields needed to to check if an update of Interests and GeneralInfo is necessary
//	 * @return Collection <ProfileField>
//	 */
//	public Collection <ProfileField> createUpdateFieldForUserInfo();
//	
//	/******************************************************/
//	/*********DEBUG,ANALYSIS *******************/
//	/********************************************************/
//	
//	/**
//	 * allows to extract one particular type of post from a full stream
//	 * @param client
//	 * @param userId
//	 */
//	public void getStreamFacebook(FacebookXmlRestClient client,String userId);
//	
//	
	
}
