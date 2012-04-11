/**
z * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
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
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.apache.shindig.social.opensocial.model.ActivityEntry;
import org.apache.shindig.social.opensocial.model.Group;
import org.apache.shindig.social.opensocial.model.Person;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.societies.api.internal.sns.ISocialData;
import org.societies.personalization.socialprofiler.Variables;
import org.societies.personalization.socialprofiler.datamodel.Interests;
import org.societies.personalization.socialprofiler.datamodel.SocialGroup;
import org.societies.personalization.socialprofiler.datamodel.SocialPerson;
import org.societies.personalization.socialprofiler.datamodel.impl.RelTypes;
import org.societies.personalization.socialprofiler.datamodel.impl.SocialPersonImpl;
import org.societies.personalization.socialprofiler.exception.NeoException;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class ProfilerEngine implements Variables{

	private static final Logger 			logger 							= Logger.getLogger(ProfilerEngine.class);
	private GraphManager					graph;
	private DatabaseConnection 				databaseConnection;
	private ISocialData						socialData;
	
	private List<?> 			friends 	= new ArrayList<Person>();
	private List<?> 			profiles 	= new ArrayList<Person>();
	private List<?>	 			groups 		= new ArrayList<Group>();
	private List<?> 			activities = new ArrayList<ActivityEntry>();
	
	private boolean 			firstTime   = true;
	
	
	private Hashtable<String, ArrayList<String>>  credentials_sn			= new Hashtable<String, ArrayList<String>> ();
	private Hashtable<String, ArrayList<String>>  credentials_sn_auxiliary	= new Hashtable<String, ArrayList<String>> ();
	
	
	public ProfilerEngine(GraphManager graph, DatabaseConnection databaseConnection, ISocialData socialData){
	
		this.graph 					= graph;
		this.databaseConnection 	= databaseConnection;
		this.socialData				= socialData;
		generateCompleteNetwork();
		
	}
	
	
	public ISocialData getSocialData(){
		return this.socialData;
	}

	public void setSocialData(ISocialData socialData){
		this.socialData = socialData;
		
	}
	
	
	/**
	 * returns the service given as parameter to the constructor
	 * @return ServiceImpl
	 */
	 public GraphManager getService() {
		return graph;
	}

	
	/**
	 * returns the databaseConnection given as parameter to the constructor
	 * @return
	 */
	public final DatabaseConnection getDatabaseConnection() {
		return databaseConnection;
	}
	
	public void setDatabaseConnection(DatabaseConnection databaseConnection) {
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
	
		logger.info("=============================================================");
		logger.info("====           SOCIAL PROFILER UPDATE                   =====");
		logger.info("=============================================================");
		logger.info("=== UPDATING NETWORK , all new users will be added to network");
		logger.info("=== updating or removing if necessary the existing users     "); 
		logger.info("=============================================================");
		
		socialData.updateSocialData();
		// Update data source
		profiles 	= socialData.getSocialProfiles();
		friends  	= socialData.getSocialPeople();
		groups 	 	= socialData.getSocialGroups();
		activities	= socialData.getSocialActivity();
		
		try {
			Thread.sleep(8000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		if (!databaseConnection.connectMysql()){
		   logger.error("Cannot proceed with request due to database connection problems.");
		   return;
	   }
		
	
		
		
		logger.debug("=============================");
		logger.debug("=== Traversing NEO GRAPH  ==="); 
		logger.debug("=============================");
		
		// ANALIZZO ESISTENTI
		ArrayList<String> list_usersIds = new ArrayList<String>();  // empty LIST
		
		try {
			list_usersIds = graph.getGraphNodesIds(graph.getAllGraphNodes());
		} 
		catch (NeoException e) {
			logger.error("Cannot get graph nodes IDs: " + e.getMessage());
			return;
		}
		
		// If the Graph Node contains at least one node ....
		if (list_usersIds.size()>0){
			
			for (int i=0;i<list_usersIds.size();i++){
				if (list_usersIds.get(i)!=null){
					generateTree(list_usersIds.get(i), null, option);
				}
			}
		}
		
		
		
		databaseConnection.addInfoForCommunityProfile();

		databaseConnection.closeMysql();
		logger.debug("=============================================================");
		logger.debug("====      SOCIAL PROFILER COMPLETED UPDATE              =====");
		logger.debug("=============================================================");
	}
	
	private void generateCompleteNetwork(){
		logger.debug("GENERATING the whole network including isolated clusters and/or nodes");
		graph.createPerson("ROOT");
		
		// creating base user (needs to be a number)
		String userId = "0";
		logger.debug("### adding new cluster using user "+userId); 
		
		if (!databaseConnection.connectMysql()){
		   logger.error("Cannot proceed with request due to database connection problems.");
		   return;
	    }
		generateTree(userId,null,FIRST_TIME); 
		
		databaseConnection.addInfoForCommunityProfile();
		databaseConnection.closeMysql();
	}
	
	
	/**
	 * Add a new User
	 * @param p
	 */
	public void linkToRoot(SocialPerson p){
		Transaction tx = graph.getNeoService().beginTx();
		try{
			Node startPersonNode	=  ((SocialPersonImpl) p).getUnderlyingNode();
			Node rootNode			=  ((SocialPersonImpl) graph.getPerson("ROOT")).getUnderlyingNode();
			
			startPersonNode.createRelationshipTo(rootNode, RelTypes.TRAVERSER);
			tx.success();
		}
		finally{
			tx.finish();
		}	
	}
	
	
	
	
	public void generateTree(String current_id, String previous_id, int option) {
		
		
		
		logger.info("=============================================================");
		logger.info("==== 					GENERATING TREE                     ===");
		logger.info("=============================================================");
		logger.info("->> current_id : "+current_id+" previous_id: "+previous_id+" opt:"+option);
		logger.info("=============================================================");

		
		logger.info("--- checking if current user "+current_id+" exists on neo network");
		
		SocialPerson currentPerson=graph.getPerson(current_id);
		
		if (currentPerson==null){
			
			logger.info("----the current user "+current_id+" doesn't exist on Neo network");
							    
			// friends List but not used any more!
			//List<SocialPerson> list= new ArrayList(getSocialData().getSocialPeople());   //serviceXml.friendsGetFacebook(client);
			logger.debug("-->creating user "+current_id+" on Neo network");
			SocialPerson startPerson=graph.createPerson(current_id);			
			linkToRoot(startPerson);
			
			databaseConnection.addUserToDatabase(current_id, startPerson.getName());
			credentials_sn.remove(current_id);
			
			logger.debug("---# initialising user"+current_id+" profile percentages");
			
			// TODO - initialize percentages
			//graph.updatePersonPercentages(current_id,"0", "0", "0", "0","0","0");
			
			logger.debug("---*checking previous user id in order to create relationship");
			
			if (previous_id==null){
				logger.info("previous user id is null -> no relationship will be created");
			}
			else{
				String nameDescription=current_id+previous_id;
				SocialPerson endPerson=graph.getPerson(previous_id);
				logger.info("---# Trying to create relationship between "+current_id+" and "+previous_id+" with name "+nameDescription);
				graph.createDescription(startPerson, endPerson, current_id, previous_id);
			}
			
			
			
			// ADD GROUP for the USER	
			createGroupsAndCategories(current_id, startPerson, (List<Group>)groups);			 	
			
			// TODO: actually is not used
			//     createFanPagesAndCategories(current_id, startPerson, client);    
			
			// Update USER INTERESTs
			initialiseUserInformation(current_id, startPerson);
			
			generateUserInformation(current_id, (List<Person>)profiles);
			
			initialiseUserProfiles(current_id, (List<Person>)profiles);
			
			
			//// SET WINDOW TIME to get the last Activities
			
			
			//current time- 1 week				
			java.util.TimeZone.setDefault(TimeZone.getTimeZone("GMT")); 
			java.util.Date today = new java.util.Date();
			java.sql.Timestamp timestamp=new java.sql.Timestamp(today.getTime());
			long current_time = (timestamp.getTime())/1000;
			//1 week=7 x 24 x 60 x 60=604800
			long week_time=604800;
			long end_date=current_time-week_time;
			long end_date1=end_date*1000;
			
			///// ANALIZZARE LE ACTIVITIES
			generateProfileContent(current_id, activities, end_date1); 		//till one week before , then update
								
			for(int i=0;i<friends.size();i++){
				
				String friend= ((Person)friends.get(i)).getId();
				if (friend==null){  
					logger.warn("retrieved a null friends");
				}else{
					logger.debug("friend id "+ friend);
				}
				generateTree(friend, current_id, option);
			}
					
		}
		else{
		
			logger.info("---current user "+current_id+" exists on Neo network");
			if (option==FIRST_TIME){
				SocialPerson startPerson=graph.getPerson(current_id);				
				
				if (previous_id==null){
					logger.debug("previous user is null => nothing to check - end of this sub-branch");
				}else{
					logger.debug("####checking if there is a relationship between current "+current_id+" and previous"+previous_id);
					SocialPerson endPerson=graph.getPerson(previous_id);
					
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
							graph.createDescription(startPerson, endPerson, current_id, previous_id);	
							
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
					// checking if current user still exists. always true for now
					boolean exists=true;
					if (exists){
							logger.debug("--current user "+current_id+" found");	
							credentials_sn.remove(current_id);
							// checking if current user has valid credentials (eg token). always true for now
							boolean valid=true;
							if (!valid){		
									logger.debug("the credentials of user "+current_id+" don't function : Reason : possible invalid token");
									logger.debug("removing current id "+current_id+" from neo network, index and database");
									logger.info("REMOVING USER + DELETING FROM NEO: "+current_id);
									graph.deletePerson(current_id);						
							}else{								
									SocialPerson startPerson=graph.getPerson(current_id);
									if (previous_id==null){
										logger.debug("previous user is null=> nothing to check - end of this sub-branch");
									}else{
										logger.debug("####checking if there is a relationship between current "+current_id+" and previous"+previous_id);
										SocialPerson endPerson=graph.getPerson(previous_id);
										boolean existsRel=graph.existsRelationship(startPerson, endPerson);							
										if (!existsRel){
											logger.info("#### the relationship doesn't exist");
											logger.debug("####looking through user friends to determine if a relationship is necessary");
											boolean necessary=false;
											// TODO. should retrieve contact list
											ArrayList<String> contacts=new ArrayList<String>();
											for(int i=0;i<contacts.size();i++){
												String c=contacts.get(i);
												if (c.equals(previous_id)){
													necessary=true;
												}
											}
											if (necessary==true){
												logger.debug("a relationship is necessary and will be created");
												String nameDescription=current_id+previous_id;
												logger.debug("---Creating relationship between "+current_id+" and "+previous_id+" with name "+nameDescription);
												graph.createDescription(startPerson, endPerson, current_id,previous_id);	
											}else{
												logger.debug("NO relationship is necessary - end of check");
											}
										}else{
											logger.info("####a relationship was found between the 2 nodes- end of check");
										}
									}
						
						
						switch (option){
							case UPDATE_EVERYTHING :{
//								createFanPagesAndCategories(current_id, startPerson, client); //adding additional fan pages if necessary
								createGroupsAndCategories(current_id, startPerson, groups);	// adding additional groups if necessary
								updateUserInformation(current_id, startPerson, profiles);//modifying general info if necessary
								updateProfileContent(current_id, startPerson, activities);//updating profile content information if necessary
								break;
							}
							case UPDATE_ONLY_STREAM :{
								updateProfileContent(current_id, startPerson, activities);//updating profile content information if necessary
								break;
							}
							case UPDATE_STREAM_AND_FANPAGES_AND_GROUPS :{
//								createFanPagesAndCategories(current_id, startPerson, client); //adding additional fan pages if necessary
								createGroupsAndCategories(current_id, startPerson, groups);	// adding additional groups if necessary
								updateProfileContent(current_id, startPerson, activities);//updating profile content information if necessary
								break;
							}
							case UPDATE_STREAM_AND_USER_INFORMATION :{
								updateUserInformation(current_id, startPerson, profiles);//modifying general info if necessary
								updateProfileContent(current_id, startPerson, activities);//updating profile content information if necessary
								break;
							}
							default :{
								logger.debug("ERROR , nothing will be updated , the update option introduced doesn't exist");
							}
						}						
						
						credentials_sn.remove(current_id);
					}
				}else{ // dead code for now.
					logger.debug("removing current id"+current_id+" from neo network , index ");
					graph.deletePerson(current_id);
					databaseConnection.deleteUserFromDatabase(current_id);
				}
			}
		}	
		logger.info("=============================================================");
		logger.info("====          END of TREE GENERATION for "+current_id+" =====");
		logger.info("=============================================================");
		
	}

	
	/**
	 * Create associtation between USER <==> GROUP
	 * @param current_id
	 * @param startPerson
	 * @param groups
	 */
	public void createGroupsAndCategories(String current_id,  SocialPerson startPerson, List<?> groups){

		logger.debug(" === [ GROUPS ] followed by user "+current_id);	
					
		ArrayList <String> groups_ids=new ArrayList <String> ();
		
		ArrayList <Long> existent_groups_ids	=  graph.getListOfGroups(current_id);
		ArrayList <Long> remaining_groups_ids	=  graph.convertArrayOfStringToLong(groups_ids);
		
		graph.projectArrays(remaining_groups_ids, existent_groups_ids);
				
		for(int j=0;j<remaining_groups_ids.size();j++){
			
			String groupId=remaining_groups_ids.get(j).toString();
			
			if (groupId!=null){
			
				logger.debug("Group[id] => "+ groupId);
				SocialGroup group	=	graph.linkGroup(startPerson, groupId);
				logger.debug("[ADD] Content to [GROUP]:"+groupId);
				
				
				Group currentGroup 	= findGroup(groupId);
				String type			= currentGroup.getTitle();
				String subType		= currentGroup.getDescription();
				
				graph.updateGroup	(groupId, currentGroup.getId().getGroupId() , type, subType,
									null/*group_data.get(5)*/,null/*group_data.get(1)*/,null/*group_data.get(4)*/
					);
				
				if ((!type.equals(""))&&(type!=null)&&(!subType.equals(""))&&(subType!=null)){
//					logger.debug("checking if type "+type+" and subtype "+subType+" of Group "+groupId+" exists" );
//					group.linkGroupCategoryAndSubCategory(group, type, subType, startPerson);
				}
			}
			else logger.warn(" Group [NULL]");
	
		}
	}

	
	
	public void initialiseUserInformation(String current_id, SocialPerson person){
		
		logger.debug("[INIT] GeneralInfo and Interest for user "+current_id);
		
		logger.debug("[INTERESTS]");
		
		graph.linkInterests(person,current_id+"_Interests" );   
		
		graph.updateInterests(current_id+"_Interests","nothing_yet","nothing_yet","nothing_yet","nothing_yet","nothing_yet","nothing_yet","nothing_yet","0");
		
		logger.debug("[GENERAL_INFO]");
		
		graph.linkGeneralInfo(person,current_id+"_GeneralInfo"); 
		graph.updateGeneralInfo(current_id+"_GeneralInfo","nothing_yet","nothing_yet","nothing_yet","nothing_yet", "nothing_yet","nothing_yet","nothing_yet","nothing_yet");
	}
	
	public void updateUserInformation(String current_id,SocialPerson person, List<?> profiles){
		logger.debug("Checking if update is necessary for user"+current_id);
		
		Person information= (Person) profiles.get(0); // only considers first profile
		String new_profileUpdateTime="";
		
		try {
			new_profileUpdateTime=information.getUpdated().toString();
		} catch (NullPointerException e) {}

		if (new_profileUpdateTime.equals("0")){
			logger.debug("no update is necessary");
		}else {
			String profileUpdateTime="";
			Transaction tx = graph.getNeoService().beginTx();
			try{
				Interests interests=graph.getInterests(current_id+"_Interests");
				profileUpdateTime=interests.getProfileUpdateTime();
				tx.success();
			}catch (Exception e) {}
			finally{
				tx.finish();
			}	
			if ((profileUpdateTime==null)||(profileUpdateTime.equals(""))||(profileUpdateTime.equals("0")) ){
				generateUserInformation(current_id,  profiles);
			}else if ((Long)Long.parseLong(new_profileUpdateTime)>(Long)Long.parseLong(profileUpdateTime)){
				generateUserInformation(current_id, profiles);
			}
		}
	}
	
	public void updateProfileContent(String current_id, SocialPerson person, List<?> activities){
		ArrayList <Long> lastTimes=new ArrayList<Long>();
//		try {
//			lastTimes.add(Long.parseLong(graph.getNarcissismManiacLastTime(current_id+"_NarcissismManiac")));
//		} catch (NumberFormatException e) {
//			logger.warn("Error parsing service.getNarcissismManiacLastTime() into date. Property set to 0. Details: " + e.getMessage());
//			lastTimes.add(new Long("0"));
//		}
//		try {
//			lastTimes.add(Long.parseLong(graph.getSuperActiveManiacLastTime(current_id+"_SuperActiveManiac")));
//		} catch (NumberFormatException e) {
//			logger.warn("Error parsing service.getSuperActiveManiacLastTime() into date. Property set to 0. Details: " + e.getMessage());
//			lastTimes.add(new Long("0"));
//		}
//		try {
//			lastTimes.add(Long.parseLong(graph.getPhotoManiacLastTime(current_id+"_PhotoManiac")));
//		} catch (NumberFormatException e) {
//			logger.warn("Error parsing service.getPhotoManiacLastTime() into date. Property set to 0. Details: " + e.getMessage());
//			lastTimes.add(new Long("0"));
//		}
//		try {
//			lastTimes.add(Long.parseLong(graph.getSurfManiacLastTime(current_id+"_SurfManiac")));
//		} catch (NumberFormatException e) {
//			logger.warn("Error parsing service.getSurfManiacLastTime() into date. Property set to 0. Details: " + e.getMessage());
//			lastTimes.add(new Long("0"));
//		}
//		try {
//			lastTimes.add(Long.parseLong(graph.getQuizManiacLastTime(current_id+"_QuizManiac")));
//		} catch (NumberFormatException e) {
//			logger.warn("Error parsing service.getQuizManiacLastTime() into date. Property set to 0. Details: " + e.getMessage());
//			lastTimes.add(new Long("0"));
//		}
//		graph.qsortSimple(lastTimes,0, lastTimes.size()-1);
		// Workaround
		lastTimes.add(new Long("0"));
		
		long date=lastTimes.get(lastTimes.size()-1);
//		logger.debug("updating stream information for user "+current_id+" starting from timestamp : "+date);
		generateProfileContent(current_id, activities, date);
	}
	
	public void generateProfileContent(String current_id,List<?> posts,long date ){
		logger.debug("Generating profile information from stream for user "+current_id+" .....");
	
			long date_s=date*1000;
			Date d = new Date(date_s);
			
			logger.debug("analysing each post of the user "+current_id+" 's Wall");
			//NOTE going backwards through the DOM, the most recent are first
			for(int j=posts.size()-1;j>=0;j--){
				ActivityEntry activity=(ActivityEntry) posts.get(j);
				
				String viewer=current_id;
				String source=activity.getActor().getDisplayName();
				String type="note";
				try {
					type = activity.getObject().getObjectType();
				} catch (Exception e) {}
				String message=activity.getContent();
				logger.debug("----post: viewer "+viewer+" source "+source+" type "+type+" message "+message);
				
				if (viewer.equals(source)){  //viewer=source  -> the posts of the actual user from his Wall
					postFiltering(activity, current_id, viewer, source, type, message);
					repliesComments(activity, source, viewer, current_id, REPLY_TO_ME);
				} else {
//					String actor = source;
//					String target =activity.getTarget();
//					if (viewer.equals(actor)){
//						postFilteringAdvanced(activity, current_id, actor, target, type, message);
//					}																						// if (!serviceXml.checkIfUserExists(credentials_facebook_auxiliary, "facebook", source)){ //viewer!=source and source not on AUP ->posts of other non AUP users on his Wall  
					repliesComments(activity, source, viewer, current_id, REPLY_TO_STRANGER_CA);
				}
			}//for each post
	}
	
	private void postFiltering(ActivityEntry activity ,String current_id,String viewer, String source , String type , String message){
		logger.debug("+++++ analysing post , type "+type+", for user "+current_id);

		String lastTime=activity.getUpdated();
		if ("note".equals(type)){ //status message
				String  profile_last_time=graph.getNarcissismManiacLastTime(current_id+"_NarcissismManiac");
				if (Integer.parseInt(profile_last_time)<Integer.parseInt(lastTime)){
					logger.debug("Narcissism Profile interaction");
					graph.incrementNarcissismManiacNumber(current_id+"_NarcissismManiac");
					updateProfileStatistics(current_id, lastTime, profile_last_time, NARCISSISM_PROFILE);
				}
		} else if ("image".equals(type)){
				String profile_last_time=graph.getPhotoManiacLastTime(current_id+"_PhotoManiac");
				if (Integer.parseInt(profile_last_time)<Integer.parseInt(lastTime)){
					logger.debug("Photo Maniac interaction");
					//TODO : improvement : detect if album or photo ; normally is a photo but could be also an album
					graph.incrementPhotoManiacNumber(current_id+"_PhotoManiac");
					updateProfileStatistics(current_id,lastTime,profile_last_time,PHOTO_PROFILE);
				}
		} else if ("bookmark".equals(type)){ //link , youtube or others
			String  profile_last_time=graph.getSurfManiacLastTime(current_id+"_SurfManiac");
			if (Integer.parseInt(profile_last_time)<Integer.parseInt(lastTime)){
				logger.debug("Surf Maniac interaction");
				graph.incrementSurfManiacNumber(current_id+"_SurfManiac");
				updateProfileStatistics(current_id,lastTime,profile_last_time,SURF_PROFILE);
			}
		} else if ("quiz".equals(type)){  //TODO quiz,applications
				String  profile_last_time=graph.getQuizManiacLastTime(current_id+"_QuizManiac");
				if (Integer.parseInt(profile_last_time)<Integer.parseInt(lastTime)){
					logger.debug("Quiz Maniac interaction");
					graph.incrementQuizManiacNumber(current_id+"_QuizManiac");
					updateProfileStatistics(current_id,lastTime,profile_last_time,QUIZ_PROFILE);
				}
		} else if ("video".equals(type)){   //TODO posts containing movies , mp4 link inside the post 
				String  profile_last_time=graph.getSurfManiacLastTime(current_id+"_SurfManiac");
				if (Integer.parseInt(profile_last_time)<Integer.parseInt(lastTime)){
					logger.debug("Surf Maniac interaction");
					graph.incrementSurfManiacNumber(current_id+"_SurfManiac");
					updateProfileStatistics(current_id,lastTime,profile_last_time,SURF_PROFILE);
				}
		} else if ("profile".equals(type)){ //TODO profile photos -> this is consider narcissist
				String  profile_last_time=graph.getNarcissismManiacLastTime(current_id+"_NarcissismManiac");
				if (Integer.parseInt(profile_last_time)<Integer.parseInt(lastTime)){
					logger.debug("Narcissism Profile interaction");
					graph.incrementNarcissismManiacNumber(current_id+"_NarcissismManiac");
					updateProfileStatistics(current_id, lastTime, profile_last_time, NARCISSISM_PROFILE);
				}
		} else if ("message".equals(type)){ //TODO
				//logger.debug("the user received a direct message- however since no Popularity Profile still available nothing will be done with this post information");
		} else if ("message-event".equals(type)){ //TODO
				//messages with events , for the moment are not treated since no Informative Profile,
				//could be added to the narcissist category and also super active
		} else if ("message-link".equals(type)){ //TODO
				//messages with link , events , for the moment are not treated since no Informative Profile,
				//could be added to the narcissist category and also super active
		} else {
				logger.warn("****WARNING this type is unknown for the engine *** ::"+type);
		} //end switch case	
	}
	
	private void repliesComments(ActivityEntry activity, String source , String viewer , String current_id,int option ){
//		logger.debug("analysing possible comments of the post");
//		org.w3c.dom.Element comments=(Element)activity.item(12);
//		NodeList comment_list=comments.getElementsByTagName("comment");
//		for (int k=0;k<comment_list.getLength();k++){
//			org.w3c.dom.Node comment=comment_list.item(k).getFirstChild();
//			if(comment.getNodeName().equals("fromid")){
//				
//				//comments of the stream post
//				String comment_sourceId=comment.getTextContent();
//				String profile_lastTime=service.getSuperActiveManiacLastTime(current_id+"_SuperActiveManiac");
//				String lastTime=comment.getNextSibling().getTextContent();
//				String text=comment.getNextSibling().getNextSibling().getTextContent();
//				logger.debug(" comment fromid "+comment_sourceId+" text "+text+" lastTime "+lastTime );
//					
//				if (comment_sourceId.equals(viewer)){        
//					if (Integer.parseInt(profile_lastTime)<Integer.parseInt(lastTime)){
//						logger.debug("Super Active Altruist Maniac interaction - comment reply");
//						service.incrementSuperActiveManiacNumber(current_id+"_SuperActiveManiac");
//						updateProfileStatistics(current_id, lastTime, profile_lastTime, SUPERACTIVE_PROFILE);
//												
////					    try { //FIXME: disabilitato la pubblicazione sul blog
////							switch(option){
////								case REPLY_TO_ME:{
////									publishOnBlog("Super - Active Interaction",textGenerator.replyCommentToMe(lastTime, text),service.getPersonCAName(current_id));
////									break;
////								}
////								case REPLY_TO_OTHER_CA:{
////									publishOnBlog("Super - Active Interaction",textGenerator.replyCommentToCAUser(lastTime.toString(), text, service.getPersonCAName(source)), service.getPersonCAName(current_id));
////									break;
////								}
////								case REPLY_TO_STRANGER_CA:{
////									publishOnBlog("Super - Active Interaction",textGenerator.replyCommentToCAStranger(lastTime, text), service.getPersonCAName(current_id));
////									break;
////								}
////							}
////							
////						} catch (MalformedURLException e) {
////							logger.error("unable to publish post on blog;Reason "+e);
////							e.printStackTrace();
////						} catch (XmlRpcException e) {
////							logger.error("unable to publish post on blog;Reason "+e);
////							e.printStackTrace();
////						}
//					}
//				}
//			}// if from_id	
//		}//for comment_list	
	}

	private Group findGroup(String groupId) {
		for (int i=0; i<groups.size(); i++)
			if (groupId.equals(((Group)groups.get(i)).getId().getGroupId())) return (Group)groups.get(i);	
		return null;
	}
	
	
	public void generateUserInformation(String current_id, List<?> profiles){
		
		logger.info("===== [MAKE Basic INFO] GeneralInfo and Interests for user "+current_id);
		
		
		ArrayList <Long> userId=new ArrayList<Long> ();
		userId.add(Long.parseLong(current_id));
		
		Person user = null;
		
		if (profiles.size()>0)
			user = (Person) profiles.get(0); // to be improved!!!!
		
		if (user != null) {
			// TODO: Transform List of values into strings!
			java.util.Date updatedDate = user.getUpdated();
			String updatedDateS = null;
			if (updatedDate != null)
				updatedDateS = updatedDate.toString();
			graph.updateInterests(current_id+"_Interests", 
								  "activities", 
								  "interestList", 
							      "music",
							      "movies", 
							      "books", 
							      "quotations", 
							      user.getAboutMe(),
								  updatedDateS);
			try {
				graph.updateGeneralInfo(current_id+"_GeneralInfo", 
										user.getName().getGivenName(), 
										user.getName().getFamilyName(),	
										null, 
										null, 
										user.getLivingArrangement(), 
										null,
										user.getPoliticalViews(), 
										user.getReligion());
			} catch (Exception e){
				e.printStackTrace();
			}
		}else {
			graph.updateInterests(current_id+"_Interests", 
								  "activities", 
								  "interestList", 
							      "music",
							      "movies", 
							      "books", 
							      "quotations", 
							      null,
								  null);
				
			
			
			graph.updateGeneralInfo(current_id+"_GeneralInfo", 
									null, 
									null,	
									null, 
									null, 
									null, 
									null, 
									null, 
									null);
			
		}	
	}
	
	
	
	public void initialiseUserProfiles(String current_id, List<Person> profiles){
		
		logger.info("@@@@ creating and initialising the user profiles @@@@");
		//		logger.debug(" ---- NarcissismManiac---Profile  ");		
		//		graph.linkNarcissismManiac(person,current_id+"_NarcissismManiac" );
		//		graph.updateNarcissismManiac(current_id+"_NarcissismManiac", "0", "0", "0");
		//		logger.debug(" ---- SuperActiveManiac---Profile  ");
		//		graph.linkSuperActiveManiac(person, current_id+"_SuperActiveManiac");
		//		graph.updateSuperActiveManiac(current_id+"_SuperActiveManiac", "0", "0", "0");
		//		logger.debug(" ---- PhotoManiac---Profile  ");
		//		graph.linkPhotoManiac(person, current_id+"_PhotoManiac");
		//		graph.updatePhotoManiac(current_id+"_PhotoManiac", "0", "0", "0");
		//		logger.debug(" ---- SurfManiac---Profile  ");
		//		graph.linkSurfManiac(person, current_id+"_SurfManiac");
		//		graph.updateSurfManiac(current_id+"_SurfManiac", "0", "0", "0");
		//		logger.debug(" ---- QuizManiac---Profile  ");
		//		graph.linkQuizManiac(person, current_id+"_QuizManiac");
		//		graph.updateQuizManiac(current_id+"_QuizManiac", "0", "0","0");
	}
	
	public void updateProfileStatistics(String  userId, String lastTime, String lastTime_old,int option)
	{	
		
		switch(option){
			case NARCISSISM_PROFILE:{  
				int number=	Integer.parseInt(graph.getNarcissismManiacNumber(userId+"_NarcissismManiac"));								
				databaseConnection.sendMomentToDatabase(userId,lastTime,number,option);
				if (number >=2){
					int old_frequency=Integer.parseInt(graph.getNarcissismManiacFrequency(userId+"_NarcissismManiac"));
					int frequency=(Integer.parseInt(lastTime)-Integer.parseInt(lastTime_old)+((number-2)*old_frequency))/(number-1);
					graph.updateNarcissismManiac(userId+"_NarcissismManiac", String.valueOf(frequency),lastTime, null);
				}else{
					graph.updateNarcissismManiac(userId+"_NarcissismManiac", null,lastTime, null);;
				}
				break;
			}
			case SUPERACTIVE_PROFILE:{
				int number=	Integer.parseInt(graph.getSuperActiveManiacNumber(userId+"_SuperActiveManiac"));								
				databaseConnection.sendMomentToDatabase(userId,lastTime,number,option);
				if (number >=2){
					int old_frequency=Integer.parseInt(graph.getSuperActiveManiacFrequency(userId+"_SuperActiveManiac"));
					int frequency=(Integer.parseInt(lastTime)-Integer.parseInt(lastTime_old)+((number-2)*old_frequency))/(number-1);
					graph.updateSuperActiveManiac(userId+"_SuperActiveManiac", String.valueOf(frequency),lastTime, null);
				}else{
					graph.updateSuperActiveManiac(userId+"_SuperActiveManiac", null,lastTime, null);;
				}
				break;
			}
			case PHOTO_PROFILE:{
				int number=	Integer.parseInt(graph.getPhotoManiacNumber(userId+"_PhotoManiac"));								
				databaseConnection.sendMomentToDatabase(userId,lastTime,number,option);
				if (number >=2){
					int old_frequency=Integer.parseInt(graph.getPhotoManiacFrequency(userId+"_PhotoManiac"));
					int frequency=(Integer.parseInt(lastTime)-Integer.parseInt(lastTime_old)+((number-2)*old_frequency))/(number-1);
					graph.updatePhotoManiac(userId+"_PhotoManiac", String.valueOf(frequency),lastTime, null);
				}else{
					graph.updatePhotoManiac(userId+"_PhotoManiac", null,lastTime, null);;
				}
				break;
			}
			case SURF_PROFILE:{
				int number=	Integer.parseInt(graph.getSurfManiacNumber(userId+"_SurfManiac"));								
				databaseConnection.sendMomentToDatabase(userId,lastTime,number,option);
				if (number >=2){
					int old_frequency=Integer.parseInt(graph.getSurfManiacFrequency(userId+"_SurfManiac"));
					int frequency=(Integer.parseInt(lastTime)-Integer.parseInt(lastTime_old)+((number-2)*old_frequency))/(number-1);
					graph.updateSurfManiac(userId+"_SurfManiac", String.valueOf(frequency),lastTime, null);
				}else{
					graph.updateSurfManiac(userId+"_SurfManiac", null,lastTime, null);;
				}
				break;
			}
			case QUIZ_PROFILE:{
				int number=	Integer.parseInt(graph.getQuizManiacNumber(userId+"_QuizManiac"));								
				databaseConnection.sendMomentToDatabase(userId,lastTime,number,option);
				if (number >=2){
					int old_frequency=Integer.parseInt(graph.getQuizManiacFrequency(userId+"_QuizManiac"));
					int frequency=(Integer.parseInt(lastTime)-Integer.parseInt(lastTime_old)+((number-2)*old_frequency))/(number-1);
					graph.updateQuizManiac(userId+"_QuizManiac", String.valueOf(frequency),lastTime, null);
				}else{
					graph.updateQuizManiac(userId+"_QuizManiac", null,lastTime, null);;
				}
				break;
			}
		}
	}	
	
}
