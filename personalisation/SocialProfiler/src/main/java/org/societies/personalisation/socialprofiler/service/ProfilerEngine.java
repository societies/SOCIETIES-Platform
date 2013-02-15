/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske druÅ¾be in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAÃ‡ÃƒO, SA (PTIN), IBM Corp., 
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
package org.societies.personalisation.socialprofiler.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

import org.apache.shindig.social.opensocial.model.ActivityEntry;
import org.apache.shindig.social.opensocial.model.ActivityObject;
import org.apache.shindig.social.opensocial.model.Person;
import org.json.JSONException;
import org.json.JSONObject;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.internal.sns.ISocialData;
import org.societies.personalisation.socialprofiler.Variables;
import org.societies.personalisation.socialprofiler.datamodel.Interests;
import org.societies.personalisation.socialprofiler.datamodel.SocialPage;
import org.societies.personalisation.socialprofiler.datamodel.SocialPerson;
import org.societies.personalisation.socialprofiler.datamodel.behaviour.Profile;
import org.societies.personalisation.socialprofiler.datamodel.impl.RelTypes;
import org.societies.personalisation.socialprofiler.datamodel.impl.SocialPersonImpl;
import org.societies.personalisation.socialprofiler.exception.NeoException;

public class ProfilerEngine implements Variables{

	private static final Logger logger = LoggerFactory.getLogger(ProfilerEngine.class);
	private GraphManager					graph;
//	private DatabaseConnection 				databaseConnection;
	private ISocialData						socialData;
	
	private List<?> 			friends 	= new ArrayList<Person>();
	private List<?> 			profiles 	= new ArrayList<Person>();
	private List<?> 			activities = new ArrayList<ActivityEntry>();
	
	public static final String	INITIAL_USER_ID = "0";
	
		
//	public ProfilerEngine(GraphManager graph, DatabaseConnection databaseConnection, ISocialData socialData){
	public ProfilerEngine(GraphManager graph, ISocialData socialData){
			
		this.graph 					= graph;
//		this.databaseConnection 	= databaseConnection;
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
		activities	= socialData.getSocialActivity();
		

//		if (!databaseConnection.connectMysql()){
//		   logger.error("Cannot proceed with request due to database connection problems.");
//		   return;
//	    }
		
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
		
		
		
//		databaseConnection.addInfoForCommunityProfile();

//		databaseConnection.closeMysql();
		logger.debug("=============================================================");
		logger.debug("====      SOCIAL PROFILER COMPLETED UPDATE              =====");
		logger.debug("=============================================================");
	}
	
	private void generateCompleteNetwork(){
		logger.debug("GENERATING the whole network including isolated clusters and/or nodes");
		graph.createPerson(SocialPerson.ROOT);
		
//		if (!databaseConnection.connectMysql()){
//		   logger.error("Cannot proceed with request due to database connection problems.");
//		   return;
//	    }
		
		createInitialUsers();
		
//		databaseConnection.addInfoForCommunityProfile();
//		databaseConnection.closeMysql();
	}
	
	private void createInitialUsers(){
		// creating base user (needs to be a number)
		logger.debug("Creating initial user: "+INITIAL_USER_ID); 
		generateTree(INITIAL_USER_ID,null,FIRST_TIME);		
	}
	
	
	/**
	 * Add a new User
	 * @param p
	 */
	public void linkToRoot(SocialPerson p){
		Transaction tx = graph.getNeoService().beginTx();
		try{
			Node startPersonNode	=  ((SocialPersonImpl) p).getUnderlyingNode();
			Node rootNode			=  ((SocialPersonImpl) graph.getPerson(SocialPerson.ROOT)).getUnderlyingNode();
			
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
			
//			databaseConnection.addUserToDatabase(current_id, startPerson.getName());
						
			logger.debug("---# initialising user"+current_id+" profile percentages");
			
			// TODO - initialize percentages
			graph.updatePersonPercentages(current_id,"0", "0", "0", "0","0","0");
			
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
			
			// Initialize USER Generic information
			initialiseUserInformation(current_id, startPerson);			
			generateUserInformation(current_id, profiles);		
			
			// Initialize USER Behavioural profiles
			initialiseUserProfiles(current_id, startPerson);	
			
			createFanPagesAndCategories(current_id, startPerson, profiles);    
						
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
			
			// ANALYSE ACTIVITIES
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
					(option==UPDATE_STREAM_AND_FANPAGES)	||
					(option==UPDATE_STREAM_AND_USER_INFORMATION)){
					// checking if current user still exists. always true for now
					boolean exists=true;
					if (exists){
							logger.debug("--current user "+current_id+" found");	
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
								createFanPagesAndCategories(current_id, startPerson, profiles); //adding additional fan pages if necessary
								updateUserInformation(current_id, startPerson, profiles);//modifying general info if necessary
								updateProfileContent(current_id, startPerson, activities);//updating profile content information if necessary
								break;
							}
							case UPDATE_ONLY_STREAM :{
								updateProfileContent(current_id, startPerson, activities);//updating profile content information if necessary
								break;
							}
							case UPDATE_STREAM_AND_FANPAGES :{
								createFanPagesAndCategories(current_id, startPerson, profiles); //adding additional fan pages if necessary
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
					}
				}else{ // dead code for now.
					logger.debug("removing current id"+current_id+" from neo network , index ");
					graph.deletePerson(current_id);
//					databaseConnection.deleteUserFromDatabase(current_id);
				}
			}
		}	
		logger.info("=============================================================");
		logger.info("====          END of TREE GENERATION for "+current_id+" =====");
		logger.info("=============================================================");
		
	}

	
	
	
	
	public void initialiseUserInformation(String current_id, SocialPerson person){
		
		logger.debug("[INIT] GeneralInfo and Interests for user "+current_id);
		logger.debug("[INTERESTS]");
		
		graph.linkInterests(person,current_id+"_Interests");		
		graph.updateInterests(current_id+"_Interests","","","","","","","","0");
		
		logger.debug("[GENERAL_INFO]");
		
		graph.linkGeneralInfo(person,current_id+"_GeneralInfo"); 
		graph.updateGeneralInfo(current_id+"_GeneralInfo","","","","", "","","","");
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
	
	public void createFanPagesAndCategories(String current_id,SocialPerson startPerson,List<?> profiles)
	{
		logger.debug("retrieving the fanpages ,for which user "+current_id+" is fan of");				
		ArrayList <String> pages_ids=new ArrayList <String> ();
		
		Person information = null;
		
		if (profiles.size()>0)
			information = (Person) profiles.get(0);
		
		Hashtable <String,ArrayList<String>> pages_data = new Hashtable<String, ArrayList<String>>();
		
		if (information != null && information.getTurnOns() != null) {
			List<String> turnOns = information.getTurnOns();
			Iterator<String> it =  turnOns.iterator();
			while(it.hasNext()){
				try {
					JSONObject info = new JSONObject(it.next());
					ArrayList<String> pageData = new ArrayList<String>();
					pageData.add(info.getString("value"));
					pageData.add(info.getString("type"));
					pages_data.put(info.getString("id"), pageData);
					pages_ids.add(info.getString("id"));
				} 
				catch (JSONException e) {
					
					e.printStackTrace();
				}
				
			}
		}
		
		ArrayList <Long> existent_pages_ids=graph.getListOfPagesOfInterest(current_id);
		ArrayList <Long> remaining_pages_ids=graph.convertArrayOfStringToLong(pages_ids);
		graph.projectArrays(remaining_pages_ids, existent_pages_ids);
		
		for(int j=0;j<remaining_pages_ids.size();j++){
			String page=remaining_pages_ids.get(j).toString();
			if (page==null){
				logger.warn("retrieved a null page");
			}else{
				logger.debug("----   page id "+ page);
				SocialPage fanPage=graph.linkPageOfInterest(startPerson, page);
				ArrayList<String> page_data=pages_data.get(page);
				String type=page_data.get(1);
				graph.updatePageOfInterest(page, page_data.get(0),type );
				if ((!type.equals(""))&&(type!=null)){
					graph.linkPageOfInterestCategory(fanPage, type, startPerson);
				}
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
		logger.debug("Generating profile information from stream of "+posts.size() + " activities for user "+current_id+" .....");
	
			long date_s=date*1000;
			Date d = new Date(date_s);
			
			logger.info("analysing each post of the user "+current_id+" 's Wall");
			//NOTE going backwards through the DOM, the most recent are first
			for(int j=posts.size()-1;j>=0;j--){
				ActivityEntry activity=(ActivityEntry) posts.get(j);
				
				String viewer=current_id;
				try {
					//TODO improve to match viewer/source over all profiles
					Person p = (Person) profiles.get(0);
					viewer = p.getDisplayName();
					if (viewer == null || viewer.equals(""))
						viewer = p.getId();
				} catch (Exception e) {}
				String source=viewer;
				ActivityObject a = activity.getActor();
				if (a != null) {
					source = a.getDisplayName();
					if (source == null || source.equals(""))
						source = a.getId();
				}
				String type="note";
				try {
					type = activity.getObject().getObjectType();
				} catch (Exception e) {}
				String message=activity.getContent();
				
				// TODO support multiple users. check if viewer (current_id) equals the source (author)
//				if (viewer.equals(source)){  //viewer=source  -> the posts of the actual user from his Wall
					postFiltering(activity, current_id, viewer, source, type, message);
					repliesComments(activity, source, viewer, current_id, REPLY_TO_ME);
//				} else {
////					String actor = source;
////					String target =activity.getTarget();
////					if (viewer.equals(actor)){
////						postFilteringAdvanced(activity, current_id, actor, target, type, message);
////					}																						// if (!serviceXml.checkIfUserExists(credentials_facebook_auxiliary, "facebook", source)){ //viewer!=source and source not on AUP ->posts of other non AUP users on his Wall  
//					repliesComments(activity, source, viewer, current_id, REPLY_TO_STRANGER_CA);
//				}
			}//for each post
	}
	
	private void postFiltering(ActivityEntry activity ,String current_id,String viewer, String source , String type , String message){

		String lastTime=activity.getPublished();
		
		if ("note".equals(type)){ //status message
			if (viewer.equals(source)) {
				if (activity.getTarget() == null) // no target, go to wall -> this is consider narcissist			
					incrementManiacStatistics(lastTime, current_id, "_NarcissismManiac", Profile.Type.EGO_CENTRIC, NARCISSISM_PROFILE);
				else // post to someone else's activity or wall
					incrementManiacStatistics(lastTime, current_id, "_SuperActiveManiac", Profile.Type.SUPER_ACTIVE, SUPERACTIVE_PROFILE);
			} else // remote post from someone else. no impact on user behaviour profile
				logger.info("----(neutral) post: viewer "+viewer+", source: "+source+", type: "+type+", content: "+message+", object: "+activity.getObject().getDisplayName());
			
		} else if ("image".equals(type)){
			incrementManiacStatistics(lastTime, current_id, "_PhotoManiac", Profile.Type.PHOTO_MANIAC, PHOTO_PROFILE);
		} else if ("bookmark".equals(type)){ //link , youtube or others
			incrementManiacStatistics(lastTime, current_id, "_SurfManiac", Profile.Type.SURF_MANIAC, SURF_PROFILE);
		} else if ("video".equals(type)){   //TODO posts containing movies , mp4 link inside the post 
			incrementManiacStatistics(lastTime, current_id, "_SurfManiac", Profile.Type.SURF_MANIAC, SURF_PROFILE);
		} else if ("application".equals(type)){  //TODO quiz, applications
			incrementManiacStatistics(lastTime, current_id, "_QuizManiac", Profile.Type.QUIZ_MANIAC, QUIZ_PROFILE);
		} else if ("person".equals(type)){ 
			if ("update".equals(activity.getVerb()) && viewer.equals(source)) // update profile photos -> this is consider narcissist			
				incrementManiacStatistics(lastTime, current_id, "_NarcissismManiac", Profile.Type.EGO_CENTRIC, NARCISSISM_PROFILE);
			else { // e.g. "tag" someone or "make-friend". 
				//TODO this is actually receiving tags, so it is a sign of popularity (as well as the number of likes or comments to own activities)
				logger.info("----(popularity) post: viewer "+viewer+", source: "+source+", type: "+type+", content: "+message+", object: "+activity.getObject().getDisplayName());
				incrementManiacStatistics(lastTime, current_id, "_SuperActiveManiac", Profile.Type.SUPER_ACTIVE, SUPERACTIVE_PROFILE);
			}
		} else if ("comment".equals(type)){ // comment someone else's activity
			incrementManiacStatistics(lastTime, current_id, "_SuperActiveManiac", Profile.Type.SUPER_ACTIVE, SUPERACTIVE_PROFILE);
		} else if ("event".equals(type)){ // deal with events (e.g. attend). TODO improve
			incrementManiacStatistics(lastTime, current_id, "_SuperActiveManiac", Profile.Type.SUPER_ACTIVE, SUPERACTIVE_PROFILE);
		} else if ("question".equals(type)){ // ask a question
			incrementManiacStatistics(lastTime, current_id, "_SuperActiveManiac", Profile.Type.SUPER_ACTIVE, SUPERACTIVE_PROFILE);
		} else if ("place".equals(type) && viewer.equals(source)){ // checkin a place			
			incrementManiacStatistics(lastTime, current_id, "_NarcissismManiac", Profile.Type.EGO_CENTRIC, NARCISSISM_PROFILE);
		} else {
			logger.info("----post: viewer "+viewer+", source: "+source+", type: "+type+", content: "+message+", object: "+activity.getObject().getDisplayName());
			logger.info("****WARNING this type is unknown for the engine *** :"+type);
//		} else if ("message".equals(type)){ //TODO
//				//logger.debug("the user received a direct message- however since no Popularity Profile still available nothing will be done with this post information");
//		} else if ("message-event".equals(type)){ //TODO
//				//messages with events , for the moment are not treated since no Informative Profile,
//				//could be added to the narcissist category and also super active
//		} else if ("message-link".equals(type)){ //TODO
//				//messages with link , events , for the moment are not treated since no Informative Profile,
//				//could be added to the narcissist category and also super active
		} //end switch case	
	}
	
	private void incrementManiacStatistics(String lastTime, String current_id, String maniacType, Profile.Type ptype, int profile) {
		String profile_last_time=graph.getManiacLastTime(current_id+maniacType, ptype);

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
		try {
			if (sdf.parse(profile_last_time).
				before(sdf.parse(lastTime))){
				graph.incrementManiacNumber(current_id+maniacType, ptype);
				updateProfileStatistics(current_id, lastTime, profile_last_time, profile);
			}
		} catch (ParseException e){
			e.printStackTrace();
		}
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
								  "", 
								  "", 
							      "",
							      "", 
							      "", 
							      "", 
							      user.getAboutMe(),
								  updatedDateS);
			try {
				String first = null;
				if (user.getName() != null) {
					first = user.getName().getGivenName();
					if (first == null)
						first = user.getName().getFormatted();
				}
				
				String currentLoc = null;
				if (user.getCurrentLocation() != null)
					currentLoc = user.getCurrentLocation().getFormatted();
				
				String birthday = null;
				if (user.getBirthday() != null)
					birthday = user.getBirthday().toString();
				
				String gender = null;
				if (user.getGender() != null)
					gender = user.getGender().toString();
				
				graph.updateGeneralInfo(current_id+"_GeneralInfo", 
										first, 
										user.getName().getFamilyName(),	
										birthday, 
										gender, 
										user.getLivingArrangement(), 
										currentLoc,
										user.getPoliticalViews(), 
										user.getReligion());
			} catch (Exception e){
				e.printStackTrace();
			}
		}else {
			graph.updateInterests(current_id+"_Interests", 
								  "", 
								  "", 
							      "",
							      "", 
							      "", 
							      "", 
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
	
	
	
	public void initialiseUserProfiles(String current_id, SocialPerson person){
		
		Date d = new Date(0);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
		
		logger.info("@@@@ creating and initialising the user profiles @@@@");
		logger.debug(" ---- NarcissismManiac---Profile  ");
		graph.linkManiac(person,current_id+"_NarcissismManiac", Profile.Type.EGO_CENTRIC);
		graph.updateManiac(current_id+"_NarcissismManiac", Profile.Type.EGO_CENTRIC, "0", sdf.format(d), "0");
		logger.debug(" ---- SuperActiveManiac---Profile  ");
		graph.linkManiac(person, current_id+"_SuperActiveManiac", Profile.Type.SUPER_ACTIVE);
		graph.updateManiac(current_id+"_SuperActiveManiac", Profile.Type.SUPER_ACTIVE, "0", sdf.format(d), "0");
		logger.debug(" ---- PhotoManiac---Profile  ");
		graph.linkManiac(person, current_id+"_PhotoManiac", Profile.Type.PHOTO_MANIAC);
		graph.updateManiac(current_id+"_PhotoManiac", Profile.Type.PHOTO_MANIAC, "0", sdf.format(d), "0");
		logger.debug(" ---- SurfManiac---Profile  ");
		graph.linkManiac(person, current_id+"_SurfManiac", Profile.Type.SURF_MANIAC);
		graph.updateManiac(current_id+"_SurfManiac", Profile.Type.SURF_MANIAC, "0", sdf.format(d), "0");
		logger.debug(" ---- QuizManiac---Profile  ");
		graph.linkManiac(person, current_id+"_QuizManiac", Profile.Type.QUIZ_MANIAC);
		graph.updateManiac(current_id+"_QuizManiac", Profile.Type.QUIZ_MANIAC, "0", sdf.format(d),"0");
	}
	
	public void updateProfileStatistics(String  userId, String lastTime, String lastTime_old,int option)
	{	
		String maniacType;
		Profile.Type ptype;
		
		switch(option){
			case NARCISSISM_PROFILE:{  
				maniacType="_NarcissismManiac";
				ptype=Profile.Type.EGO_CENTRIC;	
				break;
			}
			case SUPERACTIVE_PROFILE:{
				maniacType="_SuperActiveManiac";
				ptype=Profile.Type.SUPER_ACTIVE;
				break;
			}
			case PHOTO_PROFILE:{
				maniacType="_PhotoManiac";
				ptype=Profile.Type.PHOTO_MANIAC;
				break;
			}
			case SURF_PROFILE:{
				maniacType="_SurfManiac";
				ptype=Profile.Type.SURF_MANIAC;
				break;
			}
			case QUIZ_PROFILE:{
				maniacType="_QuizManiac";
				ptype=Profile.Type.QUIZ_MANIAC;
				break;
			}
			default:{
				return;
			}
		}
		int number=	graph.getManiacNumber(userId+maniacType, ptype);								
//		databaseConnection.sendMomentToDatabase(userId,lastTime,number,option);
		if (number >=2){
			long old_frequency=graph.getManiacFrequency(userId+maniacType, ptype);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
			long frequency;
			try {
				frequency = (sdf.parse(lastTime).getTime() - sdf.parse(lastTime_old).getTime()+((number-2)*old_frequency))/(number-1);
				graph.updateManiac(userId+maniacType, ptype, String.valueOf(frequency),lastTime, null);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			graph.updateManiac(userId+maniacType, ptype, null,lastTime, null);
		}
	}


	public void generateUniformProfilePercentagesUsingBayesianSystem() {
		ArrayList<String> list_usersIds;
		try {
			list_usersIds = graph.getGraphNodesIds(graph.getAllGraphNodes());
		} catch (NeoException e) {
			logger.error("Cannot get graph nodes IDs: " + e.getMessage());
			return;
		}
		update_Users_Profile_Percentages(list_usersIds);
		//ArrayList<Float> parameters=calculate_Parameters_mc(list_usersIds);
		ArrayList<Float> parameters1=calculate_Parameters_mc_BoxPlot(list_usersIds);
		updateProfilePercentages_UsingBayesianEstimator(list_usersIds,parameters1);
	}	
	
	private void update_Users_Profile_Percentages(ArrayList <String> list_usersIds){
		logger.debug("going through the network and updating the profile percentages for each user ..");
		
		for (int i=0;i<list_usersIds.size();i++){
			String userId=list_usersIds.get(i);
			int narcissismNumber=graph.getManiacNumber(userId+"_NarcissismManiac", Profile.Type.EGO_CENTRIC);
			int photoNumber=graph.getManiacNumber(userId+"_PhotoManiac", Profile.Type.PHOTO_MANIAC);
			int superActiveNumber=graph.getManiacNumber(userId+"_SuperActiveManiac", Profile.Type.SUPER_ACTIVE);
			int surfNumber=graph.getManiacNumber(userId+"_SurfManiac", Profile.Type.SURF_MANIAC);
			int quizNumber=graph.getManiacNumber(userId+"_QuizManiac", Profile.Type.QUIZ_MANIAC);
			
			float total=narcissismNumber+photoNumber+superActiveNumber+surfNumber+quizNumber;
			
			if(total==0){
				logger.info("person "+userId+" hasn't done any type of actions ->will be ignored from this analysis");
				graph.updatePersonPercentages(userId, "0","0","0","0","0","0"); 
			}else{
				logger.info("person "+userId+" has done "+narcissismNumber+" narcissism, "+superActiveNumber
						+" super active, "+photoNumber +" photoManiac, "+surfNumber +" surfManiac, "+quizNumber +" quizManiac, ");
				float narcissismPercentage=(float)(narcissismNumber/total);
				float superActivePercentage=(float)(superActiveNumber/total);
				float photoPercentage=(float)(photoNumber/total);
				float surfPercentage=(float)(surfNumber/total);
				float quizPercentage=(float)(quizNumber/total);
				graph.updatePersonPercentages(userId, String.valueOf(narcissismPercentage), 
								String.valueOf(superActivePercentage),String.valueOf(photoPercentage),
								String.valueOf(surfPercentage),String.valueOf(quizPercentage),
								String.valueOf(total));
				logger.info("profile percentages : narcissism "+narcissismPercentage+
								", superActive "+superActivePercentage+", photo "+photoPercentage+", surf "+
								surfPercentage+", quiz "+quizPercentage);
			}
		}	
	}
	
	private ArrayList<Float> calculate_Parameters_mc_BoxPlot(ArrayList <String> list_usersIds){
		//parameter m = average number of points for a profile
		//parameter c=average rating for a profile
		ArrayList <Float> parameters=new ArrayList<Float>();
		
		ArrayList <Float> narcissismNumbers = new ArrayList<Float>(),
				superActiveNumbers= new ArrayList<Float>(),
				photoNumbers= new ArrayList<Float>(),
				surfNumbers= new ArrayList<Float>(),
				quizNumbers= new ArrayList<Float>();
		
		ArrayList <Float>narcissismPercentages= new ArrayList<Float>(),
					superActivePercentages= new ArrayList<Float>(),
					photoPercentages= new ArrayList<Float>(),
					surfPercentages= new ArrayList<Float>(),
					quizPercentages=new ArrayList<Float>();
				
		logger.debug("calculating parameters M and C for each profile ");
		int narcissism=0,superActive=0,photo=0,surf=0,quiz=0;
		float narcissismPercentage=0,superActivePercentage=0,photoPercentage=0,surfPercentage=0,quizPercentage=0;
			
		for (int i=0;i<list_usersIds.size();i++){
			String userId=list_usersIds.get(i);

			narcissism=graph.getManiacNumber(userId+"_NarcissismManiac", Profile.Type.EGO_CENTRIC);
			photo=graph.getManiacNumber(userId+"_PhotoManiac", Profile.Type.PHOTO_MANIAC);
			superActive=graph.getManiacNumber(userId+"_SuperActiveManiac", Profile.Type.SUPER_ACTIVE);
			surf=graph.getManiacNumber(userId+"_SurfManiac", Profile.Type.SURF_MANIAC);
			quiz=graph.getManiacNumber(userId+"_QuizManiac", Profile.Type.QUIZ_MANIAC);

			logger.info("calculating parameters M and C for " + userId + ": n " + narcissism);
			if (narcissism>0){
				narcissismNumbers.add(Float.valueOf((float)narcissism));
				narcissismPercentage=Float.parseFloat(graph.getPersonProfilePercentage(userId, Profile.Type.EGO_CENTRIC));
				narcissismPercentages.add(narcissismPercentage);
			}
			if (superActive>0){
				superActiveNumbers.add(Float.valueOf((float)superActive));
				superActivePercentage=Float.parseFloat(graph.getPersonProfilePercentage(userId, Profile.Type.SUPER_ACTIVE));
				superActivePercentages.add(superActivePercentage);
			}
			if (photo>0){
				photoNumbers.add(Float.valueOf((float)photo));
				photoPercentage=Float.parseFloat(graph.getPersonProfilePercentage(userId, Profile.Type.PHOTO_MANIAC));
				photoPercentages.add(photoPercentage);
			}
			if (surf>0){
				surfNumbers.add(Float.valueOf((float)surf));
				surfPercentage=Float.parseFloat(graph.getPersonProfilePercentage(userId, Profile.Type.SURF_MANIAC));
				surfPercentages.add(surfPercentage);
			}
			if (quiz>0){
				quizNumbers.add(Float.valueOf((float)quiz));
				quizPercentage=Float.parseFloat(graph.getPersonProfilePercentage(userId, Profile.Type.QUIZ_MANIAC));
				quizPercentages.add(quizPercentage);
			}
		}
		//m parameters
		parameters.add(graph.calculateAverageUsingBoxplot(narcissismNumbers));
		parameters.add(graph.calculateAverageUsingBoxplot(superActiveNumbers));
		parameters.add(graph.calculateAverageUsingBoxplot(photoNumbers));
		parameters.add(graph.calculateAverageUsingBoxplot(surfNumbers));
		parameters.add(graph.calculateAverageUsingBoxplot(quizNumbers));
		//c parameters
		parameters.add(graph.calculateAverageUsingBoxplot(narcissismPercentages));
		parameters.add(graph.calculateAverageUsingBoxplot(superActivePercentages));
		parameters.add(graph.calculateAverageUsingBoxplot(photoPercentages));
		parameters.add(graph.calculateAverageUsingBoxplot(surfPercentages));
		parameters.add(graph.calculateAverageUsingBoxplot(quizPercentages));
		
		logger.info("Parameters m :narcissism "+parameters.get(0)+
				" superActive "+graph.calculateAverageUsingBoxplot(superActiveNumbers)+" photo "+
				graph.calculateAverageUsingBoxplot(photoNumbers)+" surf "+
				graph.calculateAverageUsingBoxplot(surfNumbers)+" quiz "+
				graph.calculateAverageUsingBoxplot(quizNumbers));
		logger.info("Parameters c :narcissism "+graph.calculateAverageUsingBoxplot(narcissismPercentages)+
				" superActive "+graph.calculateAverageUsingBoxplot(superActivePercentages)+" photo "+
				graph.calculateAverageUsingBoxplot(photoPercentages)+" surf "+
				graph.calculateAverageUsingBoxplot(surfPercentages)+" quiz "+
				graph.calculateAverageUsingBoxplot(quizPercentages));
		
		return parameters;
	}
	
	
	private void updateProfilePercentages_UsingBayesianEstimator(ArrayList <String> list_usersIds,ArrayList <Float> parameters){
		//parameter m = average number of points for a profile
		//parameter c=average rating for a profile
		//Weighted rating -> replaces the old rating (percentage) using bayes formula
		logger.debug("updating profiles percentages using bayesian estimator");
		
		//ArrayList <String> list_usersIds=service.getGraphNodesIds(service.getGraphNodes(person));
		for (int i=0;i<list_usersIds.size();i++){
			String userId=list_usersIds.get(i);
			logger.debug("userId "+userId);	
			boolean updated = false;
			float narcissismNumber=(float) graph.getManiacNumber(userId+"_NarcissismManiac", Profile.Type.EGO_CENTRIC);
			float wr_narcissism=Float.parseFloat(graph.getPersonProfilePercentage(userId, Profile.Type.EGO_CENTRIC));
			if (narcissismNumber!=parameters.get(0)){
				wr_narcissism=((narcissismNumber*wr_narcissism)+(parameters.get(0)*parameters.get(5)))/(narcissismNumber+parameters.get(0));
				updated = true;
				logger.info("replacing narcissist global percentage ,previous value "+narcissismNumber+
						" current value"+wr_narcissism+" limit "+parameters.get(0));
			}

			float superActiveNumber=(float)graph.getManiacNumber(userId+"_SuperActiveManiac", Profile.Type.SUPER_ACTIVE);
			float wr_superActive=Float.parseFloat(graph.getPersonProfilePercentage(userId, Profile.Type.SUPER_ACTIVE));
			if (superActiveNumber!=parameters.get(1)){
				wr_superActive=((superActiveNumber*wr_superActive)+(parameters.get(1)*parameters.get(6)))/(superActiveNumber+parameters.get(1));
				updated = true;
				logger.debug("replacing super active global percentage ,previous value "+superActiveNumber+
						" current value"+wr_superActive+" limit "+parameters.get(0));
			}
				
			float photoNumber=graph.getManiacNumber(userId+"_PhotoManiac", Profile.Type.PHOTO_MANIAC);
			float wr_photo=Float.parseFloat(graph.getPersonProfilePercentage(userId, Profile.Type.PHOTO_MANIAC));
			if (photoNumber!=parameters.get(2)){
				wr_photo=((photoNumber*wr_photo)+(parameters.get(2)*parameters.get(7)))/(photoNumber+parameters.get(2));
				updated = true;
				logger.debug("replacing photo global percentage ,previous value "+photoNumber+
						" current value"+wr_photo+" limit "+parameters.get(0));
			}

			float surfNumber=graph.getManiacNumber(userId+"_SurfManiac", Profile.Type.SURF_MANIAC);
			float wr_surf=Float.parseFloat(graph.getPersonProfilePercentage(userId, Profile.Type.SURF_MANIAC));
			if (surfNumber!=parameters.get(3)){
				wr_surf=((surfNumber*wr_surf)+(parameters.get(3)*parameters.get(8)))/(surfNumber+parameters.get(3));
				updated = true;
				logger.debug("replacing surf global percentage ,previous value "+surfNumber+
						" current value"+wr_surf+" limit "+parameters.get(0));
			}
				
			int quizNumber=graph.getManiacNumber(userId+"_QuizManiac", Profile.Type.QUIZ_MANIAC);
			float wr_quiz=Float.parseFloat(graph.getPersonProfilePercentage(userId, Profile.Type.QUIZ_MANIAC));
			if (quizNumber!=parameters.get(4)){
				wr_quiz=((quizNumber*wr_quiz)+(parameters.get(4)*parameters.get(9)))/(quizNumber+parameters.get(4));
				updated = true;
				logger.debug("replacing quiz global percentage ,previous value "+quizNumber+
						" current value"+wr_quiz+" limit "+parameters.get(0));
			}

			if (updated)	
				graph.updatePersonPercentages(userId, String.valueOf(wr_narcissism), String.valueOf(wr_superActive),
					String.valueOf(wr_photo), String.valueOf(wr_surf), String.valueOf(wr_quiz),null);
		}
		
	}
	
	
	
}
