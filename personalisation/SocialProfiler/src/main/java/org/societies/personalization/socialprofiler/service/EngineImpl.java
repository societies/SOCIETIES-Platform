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
package org.societies.personalization.socialprofiler.service;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.societies.api.internal.sns.ISocialConnector;
import org.societies.personalization.socialprofiler.datamodel.Person;
import org.societies.personalization.socialprofiler.datamodel.PersonImpl;
import org.societies.personalization.socialprofiler.datamodel.behaviour.RelTypes;
import org.societies.personalization.socialprofiler.exception.NeoException;
import org.societies.personalization.socialprofiler.impl.Variables;
import org.societies.platform.socialdata.impl.SocialDataImpl;

import com.sun.corba.se.spi.legacy.connection.GetEndPointInfoAgainException;


public class EngineImpl implements Engine, Variables{

	private static final Logger 			logger 							= Logger.getLogger(EngineImpl.class);
	private ServiceImpl 					service;
	private DatabaseConnectionImpl 			databaseConnection;

	
	private Hashtable<String, ArrayList<String>>  credentials_sn			= new Hashtable<String, ArrayList<String>> ();
	private Hashtable<String, ArrayList<String>>  credentials_sn_auxiliary	= new Hashtable<String, ArrayList<String>> ();
	private SocialDataImpl	socialdata;
	
	public EngineImpl(ServiceImpl service, DatabaseConnectionImpl databaseConnection){
		this.service = service;
		this.socialdata =new SocialDataImpl();
		this.databaseConnection = databaseConnection;
		
	}
	
	/**
	 * returns the service given as parameter to the constructor
	 * @return ServiceImpl
	 */
	 public ServiceImpl getService() {
		return service;
	}

	
	/**
	 * returns the databaseConnection given as parameter to the constructor
	 * @return
	 */
	public final DatabaseConnectionImpl getDatabaseConnection() {
		return databaseConnection;
	}
	
	public void setDatabaseConnection(DatabaseConnectionImpl databaseConnection) {
		this.databaseConnection = databaseConnection;
	}
	
	
	/**
	 * 
	 * @param option can be 1:FIRST TIME or 2:UPDATE ONLY if option is 1 
	 * then this function also generates info but no updates are 
	 * done if option 2 , the function generates if necessary but 
	 * also updates
	 */
	public void UpdateNetwork(int option){
	

		logger.debug("UPDATING NETWORK , all new users will be added to network");
		logger.debug("updating or removing if necessary the existing users"); 
		
		ArrayList<String> list_usersIds =null;
		
		try {
			list_usersIds = service.getGraphNodesIds(service.getAllGraphNodes());
		} 
		catch (NeoException e) {
			logger.error("Cannot get graph nodes IDs: " + e.getMessage());
			return;
		}
		
		if (list_usersIds.size()>0){
			
			for (int i=0;i<list_usersIds.size();i++){
				if (list_usersIds.get(i)!=null){
					generate_tree(list_usersIds.get(i),null,option);
				}
			}
		}
		
		logger.info("UPDATE -> adding new users to neo network");
		Enumeration <ArrayList <String>> enumeration=credentials_sn.elements();
		
		while (enumeration.hasMoreElements()){
			ArrayList <String> element=enumeration.nextElement();
			String userId=element.get(3);
			logger.info("add a new user "+userId);
			generate_tree(userId,null, FIRST_TIME);
		}	
		
		databaseConnection.addInfoForCommunityProfile();
	}
	
	
	
	/**
	 * 
	 * @param p
	 */
	public void linkToRoot(Person p){
		Transaction tx = service.getNeoService().beginTx();
		try{
			Node startPersonNode	=  ((PersonImpl) p).getUnderlyingNode();
			Node rootNode			=  ((PersonImpl)service.getPerson("ROOT")).getUnderlyingNode();
			
			startPersonNode.createRelationshipTo(rootNode, RelTypes.TRAVERSER);
			tx.success();
		}
		finally{
			tx.finish();
		}	
	}
	
	
	public void generate_tree(String current_id, String previous_id,int option) {
		
		
		
		logger.debug("**********************************************************************");
		logger.debug("GENERATING TREE ->> current_id : "+current_id+" previous_id: "+previous_id+" opt:"+option);
		logger.debug("**********************************************************************");
		
		logger.debug("----checking if current user "+current_id+" exists on neo network");
		
		Person currentPerson=service.getPerson(current_id);
		
		if (currentPerson==null){
			
			logger.debug("----the current user "+current_id+" doesn't exist on Neo network");
			logger.debug("--checking if current user "+current_id+" exists on CA platform");
			
			boolean answer = true;//serviceXml.checkIfUserExists(credentials_sn, "facebook", current_id);
			
			if (answer==true)
			{
				
				//


				List<Person> list=  new ArrayList<Person>();//socialdata.getSocialPeople();   //serviceXml.friendsGetFacebook(client);
				
			
				if (list.size()==0) {
					logger.debug("the credentials of user "+current_id+" don't function : Reason : possible invalid session keys");
					logger.debug("REMOVING USER "+current_id);
					credentials_sn.remove(current_id);
				}
				else{
					logger.debug("the credentials of user "+current_id+" function properly");
					logger.debug("-->creating user "+current_id+" on Neo network");
					Person startPerson=service.createPerson(current_id);
					//String ca_Name=serviceXml.getCANameFromCredentials(credentials_sn_auxiliary, current_id);
					
//					service.setPersonCAName(current_id,ca_Name );
//					databaseConnection.addUserToDatabase(current_id, ca_Name);
					
					logger.debug("REMOVING USER "+current_id);
					credentials_sn.remove(current_id);
					
					linkToRoot(startPerson);
					
					logger.debug("---# initialising user"+current_id+" profile percentages");
					
					//service.updatePersonPercentages(current_id,"0", "0", "0", "0","0","0");
					
					logger.debug("---*checking previous user id in order to create relationship");
					
					if (previous_id==null){
						logger.debug("previous user id is null -> no relationship will be created");
					}
					else{
						String nameDescription=current_id+previous_id;
						Person endPerson=service.getPerson(previous_id);
						logger.debug("---# Trying to create relationship between "+current_id+" and "+previous_id+" with name "+nameDescription);
						service.createDescription(startPerson, endPerson, current_id,previous_id);
					}
					
					
					
					/**
					createGroupsAndCategories(current_id, startPerson, client);			 	
					createFanPagesAndCategories(current_id, startPerson, client);    
					initialiseUserInformation(current_id, startPerson);
					generateUserInformation(current_id, client);
					initialiseUserProfiles(current_id, startPerson);
					**/					
					
					
					
					
					//current time- 1 week				
					java.util.TimeZone.setDefault(TimeZone.getTimeZone("GMT")); 
					java.util.Date today = new java.util.Date();
					java.sql.Timestamp timestamp=new java.sql.Timestamp(today.getTime());
					long current_time = (timestamp.getTime())/1000;
					//1 week=7 x 24 x 60 x 60=604800
					long week_time=604800;
					long end_date=current_time-week_time;
					long end_date1=end_date*1000;
					Date d_end = new Date(end_date1);
					
					//generateInitialProfileContent(current_id, client, d_end); 		//till one week before , then update
										
					for(int i=0;i<list.size();i++){
						String friend="";//list.get(i);
						if (friend==null){  
							logger.warn("retrieved a null friends");
						}else{
							logger.debug("friend id "+ friend);
						}
						generate_tree(friend, current_id,option);
					}
				}	
			}
			else{
				logger.debug("--current user"+current_id+" not found on CA platform-->nothing is to be done--end of this sub-branch");
			}
		}
		else{
		
			logger.debug("---current user "+current_id+" exists on Neo network");
			if (option==FIRST_TIME){
				Person startPerson=service.getPerson(current_id);
//				FacebookXmlRestClient client=serviceXml.getFacebookClient(credentials_sn_auxiliary,current_id);
				
//				if (client ==null){
//					logger.error("Tree generation aborted. User " + current_id + " Facebook client is null.");
//					return;
//				}
				if (previous_id==null){
					logger.debug("previous user is null=> nothing to check - end of this sub-branch");
				}else{
					logger.debug("####checking if there is a relationship between current "+current_id+" and previous"+previous_id);
					Person endPerson=service.getPerson(previous_id);
					
					boolean exists= false;  //service.existsRelationship(startPerson, endPerson);
					
					if (exists==false){
						logger.debug("#### the relationship doesn't exist");
						logger.debug("####looking through user friends to determine if a relationship is necessary");
						
						boolean necessary=false;
						ArrayList<String> list= null; //serviceXml.friendsGetFacebook(client);
						for(int i=0;i<list.size();i++){
							String friend=list.get(i);
							if (friend.equals(previous_id)){
								necessary=true;
							}
						}
						if (necessary==true){
							logger.debug("a relationship is necessary and will be created");
							String nameDescription=current_id+previous_id;
							logger.debug("---Creating relationship between "+current_id+" and "+previous_id+" with name "+nameDescription);
//							service.createDescription(startPerson, endPerson, current_id,previous_id);	
							
						}else{
							logger.debug("NO relationship is necessary - end of check");
						}
					}else{
						logger.debug("####a relationship was found between the 2 nodes- end of check");
					}
				}
			}
			
			else if((option==UPDATE_EVERYTHING)		||
					(option==UPDATE_ONLY_STREAM)	||
					(option==UPDATE_STREAM_AND_FANPAGES_AND_GROUPS)	||
					(option==UPDATE_STREAM_AND_USER_INFORMATION)){
			
					logger.debug("---->checking if current user "+current_id+" still exists on the CA platform with valid credentials");
					boolean answer=true;//serviceXml.checkIfUserExists(credentials_sn, "facebook", current_id);
					
					if (answer==true){
							logger.debug("--current user "+current_id+" found on CA platfrom");
					
					
							// WE SHOULD USE SOCIAL DATA !!!!!!!
														
							//					FacebookXmlRestClient client=serviceXml.getFacebookClient(credentials_sn_auxiliary,current_id);
							//					
							//					
							//					if (client ==null){
							//						logger.error("Tree generation aborted. User " + current_id + " Facebook client is null.");
							//						return;
							//					}
							
							
							ArrayList<String> list1=null;//serviceXml.friendsGetFacebook(client);
							logger.debug("@@@verifying that user "+current_id+" credentials are valid and that basic permissions function properly@@@");
							
							if (list1.size()==0) {
									logger.debug("the credentials of user "+current_id+" don't function : Reason : possible invalid session keys");
									logger.debug("removing current id "+current_id+" from neo netowrk , index and from credentials database");
									logger.debug("REMOVING USER + DELETING FROM NEO"+current_id);
									credentials_sn.remove(current_id);
									service.deletePerson(current_id);
						
							}
							else{
								
									logger.debug("the credentials of user "+current_id+" function properly");
									credentials_sn.remove(current_id);
									Person startPerson=service.getPerson(current_id);
									if (previous_id==null){
										logger.debug("previous user is null=> nothing to check - end of this sub-branch");
									}else{
										logger.debug("####checking if there is a relationship between current "+current_id+" and previous"+previous_id);
										Person endPerson=service.getPerson(previous_id);
										boolean exists= false;//service.existsRelationship(startPerson, endPerson);
							
										if (exists==false){
											logger.debug("#### the relationship doesn't exist");
											logger.debug("####looking through user friends to determine if a relationship is necessary");
											boolean necessary=false;
											ArrayList<String> list=null;//serviceXml.friendsGetFacebook(client);
											for(int i=0;i<list.size();i++){
												String friend=list.get(i);
												if (friend.equals(previous_id)){
													necessary=true;
												}
											}
											if (necessary==true){
												logger.debug("a relationship is necessary and will be created");
												String nameDescription=current_id+previous_id;
												logger.debug("---Creating relationship between "+current_id+" and "+previous_id+" with name "+nameDescription);
												service.createDescription(startPerson, endPerson, current_id,previous_id);	
											}else{
												logger.debug("NO relationship is necessary - end of check");
											}
										}else{
											logger.debug("####a relationship was found between the 2 nodes- end of check");
										}
									}
						
						/**
						switch (option){
							case UPDATE_EVERYTHING :{
								createFanPagesAndCategories(current_id, startPerson, client); //adding additional fan pages if necessary
								createGroupsAndCategories(current_id, startPerson, client);	// adding additional groups if necessary
								updateUserInformation(current_id, startPerson, client);//modifying general info if necessary
								updateProfileContent(current_id, startPerson, client);//updating profile content information if necessary
								break;
							}
							case UPDATE_ONLY_STREAM :{
								updateProfileContent(current_id, startPerson, client);//updating profile content information if necessary
								break;
							}
							case UPDATE_STREAM_AND_FANPAGES_AND_GROUPS :{
								createFanPagesAndCategories(current_id, startPerson, client); //adding additional fan pages if necessary
								createGroupsAndCategories(current_id, startPerson, client);	// adding additional groups if necessary
								updateProfileContent(current_id, startPerson, client);//updating profile content information if necessary
								break;
							}
							case UPDATE_STREAM_AND_USER_INFORMATION :{
								updateUserInformation(current_id, startPerson, client);//modifying general info if necessary
								updateProfileContent(current_id, startPerson, client);//updating profile content information if necessary
								break;
							}
							default :{
								logger.debug("ERROR , nothing will be updated , the update option introduced doesn't exist");
							}
						}
						**/
						
						logger.debug("REMOVING USER "+current_id);
						credentials_sn.remove(current_id);
					}
				}else{ 
					logger.debug("removing current id"+current_id+" from neo netowrk , index ");
					service.deletePerson(current_id);
					databaseConnection.deleteUserFromDatabase(current_id);
				}
			}
		}	
	}

	@Override
	public void linkSocialNetwork(ISocialConnector connector) {
		try {
			this.socialdata.addSocialConnector(connector);
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
	}

	@Override
	public void unlinkSocialNetwork(ISocialConnector connector) {
		try {
			this.socialdata.removeSocialConnector(connector.getID());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public List<ISocialConnector> getSNConnectors() {
		return this.socialdata.getSocialConnectors();
	}
}
