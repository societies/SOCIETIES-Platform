package org.societies.personalization.socialprofiler.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.Logger;
import org.neo4j.graphalgo.impl.centrality.BetweennessCentrality;
import org.neo4j.graphalgo.impl.centrality.ClosenessCentrality;
import org.neo4j.graphalgo.impl.centrality.EigenvectorCentralityArnoldi;
import org.neo4j.graphalgo.impl.centrality.EigenvectorCentralityPower;
import org.neo4j.graphalgo.impl.shortestpath.SingleSourceShortestPath;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.ReturnableEvaluator;
import org.neo4j.graphdb.StopEvaluator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.Traverser;
import org.neo4j.index.lucene.LuceneIndexService;
import org.societies.personalization.socialprofiler.Variables;
import org.societies.personalization.socialprofiler.datamodel.Description;
import org.societies.personalization.socialprofiler.datamodel.GeneralInfo;
import org.societies.personalization.socialprofiler.datamodel.GroupCategory;
import org.societies.personalization.socialprofiler.datamodel.GroupSubCategory;
import org.societies.personalization.socialprofiler.datamodel.Interests;
import org.societies.personalization.socialprofiler.datamodel.RelationshipDescription;
import org.societies.personalization.socialprofiler.datamodel.SocialGroup;
import org.societies.personalization.socialprofiler.datamodel.SocialPage;
import org.societies.personalization.socialprofiler.datamodel.SocialPageCategory;
import org.societies.personalization.socialprofiler.datamodel.SocialPerson;
import org.societies.personalization.socialprofiler.datamodel.UserInfo;
import org.societies.personalization.socialprofiler.datamodel.impl.DescriptionImpl;
import org.societies.personalization.socialprofiler.datamodel.impl.InterestsImpl;
import org.societies.personalization.socialprofiler.datamodel.impl.RelTypes;
import org.societies.personalization.socialprofiler.datamodel.impl.SocialGroupImpl;
import org.societies.personalization.socialprofiler.datamodel.impl.SocialPersonImpl;
import org.societies.personalization.socialprofiler.datamodel.impl.GeneralInfoImpl;
import org.societies.personalization.socialprofiler.exception.NeoException;

public class GraphManager implements Variables{

	
	private static final Logger logger = Logger.getLogger(GraphManager.class);
	private GraphDatabaseService neoService;
	private LuceneIndexService luceneIndexService;
	private static final String NAME_INDEX = "name";
	private static final String PARAM_BETWEEN_PROPERTY="betweenness_centrality";
	private static final String COST_PROPERTY="cost";
	private static final String COST_INTEGER_PROPERTY="costInteger";
		
	
	/**Constructor
	 * @param neoService
	 */
	public GraphManager(GraphDatabaseService neoService) {
		
		this.neoService = neoService;
		this.luceneIndexService=new LuceneIndexService(neoService);
		
		//enable caching to improve performance
		this.luceneIndexService.enableCache(NAME_INDEX, 1000);		
		//this.searchEngine=new SearchEngineImpl(neoService,indexService);
	}
	

	public void shutdown(){
		luceneIndexService.shutdown();
		neoService.shutdown();	
    }
	
	
	/**
     * return the neoService used by ServiceImpl ,a.k.a the reference node
     * 
     * @return GraphDatabaseService
     */
	public GraphDatabaseService getNeoService()
	{
		return neoService;
	}
	
	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#createPerson(java.lang.String)
	 */
	
	public SocialPerson createPerson(final String name) {
		SocialPerson person=null;
		Transaction tx = getNeoService().beginTx();
		try{
		
			logger.debug("creating person with name "+name);
			logger.debug(" verifying there is no person in the index with the same name");
			SocialPerson test=getPerson(name);
			if (test!=null){
				logger.info("unable to create person with name "+name+", already exists a person with this name");
				return null;
			}
		
			logger.debug("no person found with the same name=> creating person properly");
			final Node personNode=neoService.createNode();
			person=new SocialPersonImpl(personNode);
			person.setName(name);
		
			logger.debug("indexing new created person to Lucene");
			luceneIndexService.index(personNode,NAME_INDEX,name);
			tx.success();
		}finally{
			tx.finish();
		}																			
		return person;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#deletePerson(java.lang.String)
	 */
	
	
	public void deletePerson(String name) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#getPerson(java.lang.String)
	 */
	
	public SocialPerson getPerson(String name) {
		
		SocialPerson person = null;
		Transaction tx = getNeoService().beginTx();
		try{
			logger.debug("**Reading Lucene Index**  searching for person "+name);
			Node personNode = luceneIndexService.getSingleNode( NAME_INDEX, name );
			if ( personNode == null )
			{
				logger.debug("person "+name+" was not found in lucene index");
				
			}
			
			if ( personNode != null )
			{
				logger.debug("person "+name+" was found on Lucene index => returning it");
				person = new SocialPersonImpl( personNode );
				
				if(person==null){logger.error("ERROR while creating instance of person - to be returned");}
			}else{
				logger.debug("returning NULL: Reason : no person found on Lucene with that name ");
			}
			tx.success();
		}finally{
			tx.finish();
		}						
		return person;
	}
	
	
	
	

//	/* (non-Javadoc)
//	 * @see org.societies.personalisation.socialprofiler.service.Service#updatePersonPercentages(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
//	 */
//	
//	public void updatePersonPercentages(String personId,
//			String narcissismManiac, String superActiveManiac,
//			String photoManiac, String surfManiac, String quizManiac,
//			String totalActions) {
//		// TODO Auto-generated method stub
//		
//	}

	/* (non-Javadoc)
//	 * @see org.societies.personalisation.socialprofiler.service.Service#setPersonCAName(java.lang.String, java.lang.String)
//	 */
//	
//	public void setPersonCAName(String personId, String caName) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	/* (non-Javadoc)
//	 * @see org.societies.personalisation.socialprofiler.service.Service#getPersonCAName(java.lang.String)
//	 */
//	
//	public String getPersonCAName(String personId) {
//		// TODO Auto-generated method stub
//		return null;
//	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#getPersonTotalActions(java.lang.String)
	 */
	
	public String getPersonTotalActions(String personId) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#getPersonNarcissismPercentage(java.lang.String)
	 */
	
	public String getPersonNarcissismPercentage(String personId) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#getPersonSuperActivePercentage(java.lang.String)
	 */
	
	public String getPersonSuperActivePercentage(String personId) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#getPersonPhotoPercentage(java.lang.String)
	 */
	
	public String getPersonPhotoPercentage(String personId) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#getPersonSurfPercentage(java.lang.String)
	 */
	
	public String getPersonSurfPercentage(String personId) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#getPersonQuizPercentage(java.lang.String)
	 */
	
	public String getPersonQuizPercentage(String personId) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#getPersonBetweenessCentrality(java.lang.String)
	 */
	
	public double getPersonBetweenessCentrality(String personId) {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#setPersonBetweenessCentrality(java.lang.String, double)
	 */
	
	public void setPersonBetweenessCentrality(String personId,
			double betweenessCentrality) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#setPersonEigenVectorCentrality(java.lang.String, double)
	 */
	
	public void setPersonEigenVectorCentrality(String personId,
			double eigenVectorCentrality) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#getPersonEigenVectorCentrality(java.lang.String)
	 */
	
	public double getPersonEigenVectorCentrality(String personId) {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#setPersonClosenessCentrality(java.lang.String, int)
	 */
	
	public void setPersonClosenessCentrality(String personId,
			int closenessCentrality) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#getPersonClosenessCentrality(java.lang.String)
	 */
	
	public int getPersonClosenessCentrality(String personId) {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#createDescription(org.societies.personalisation.socialprofiler.datamodel.Person, org.societies.personalisation.socialprofiler.datamodel.Person, java.lang.String, java.lang.String)
	 */
	
	public void createDescription(SocialPerson startPerson, SocialPerson endPerson,
			String first, String second) {
	
		
			//TODO description could be ignored in the future , for the moment it is used 
			Description description;
			Transaction tx = getNeoService().beginTx();
			try{
				logger.debug("creating relationship between 2 nodes , wrapping description over link");
				if ((first!=null) &&(second!=null) && (startPerson!=null)&& (endPerson!=null)){
					final Node startNode = ((SocialPersonImpl) startPerson).getUnderlyingNode();
					final Node endNode = ((SocialPersonImpl) endPerson).getUnderlyingNode();
					final Relationship rel = startNode.createRelationshipTo( endNode,
		        		RelTypes.IS_FRIEND_WITH );
					rel.setProperty(COST_PROPERTY, Double.parseDouble("1"));
					rel.setProperty(COST_INTEGER_PROPERTY, Integer.parseInt("1"));
					final Relationship rel_inv=endNode.createRelationshipTo(startNode,
			        		RelTypes.IS_FRIEND_WITH);
					rel_inv.setProperty(COST_PROPERTY, Double.parseDouble("1"));
					rel_inv.setProperty(COST_INTEGER_PROPERTY, Integer.parseInt("1"));
					logger.debug("symetric links created between the 2 nodes");
					description = new DescriptionImpl( rel );
					if ( description != null )
					{
						description.setName( first+second );
						logger.debug("name of the created description is "+description.getName());//could be removed
					}
					Description description_inv=new DescriptionImpl(rel_inv);
					if ( description_inv != null )
					{
						description_inv.setName( second+first );
						logger.debug("name of the created description is "+description.getName());// could be removed
					}
				}else if(first==null||second==null){
					logger.error("name of the description to be created is null-> the link will not be created");
				}else if( startPerson == null ){
					logger.error("error while determining the first person of the link-> the link will not be created");
				}else if ( endPerson == null ){
					logger.error("error while determining the second person of the link-> the link will not be created");
				}
				tx.success();
			}finally{
				tx.finish();
			}		
		
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#getDescription(org.societies.personalisation.socialprofiler.datamodel.Person, org.societies.personalisation.socialprofiler.datamodel.Person)
	 */
	
	public RelationshipDescription getDescription(SocialPerson startPerson, SocialPerson endPerson) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#createGroup(java.lang.String)
	 */
	
	public SocialGroup createGroup(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#getGroup(java.lang.String)
	 */
	
	public SocialGroup getGroup(String name) {
		SocialGroup group = null;
		Transaction tx = getNeoService().beginTx();
		try{
			logger.debug("**Reading Lucene Index**  searching for group "+name);
			Node groupNode = luceneIndexService.getSingleNode( NAME_INDEX, name );
			if ( groupNode == null )
			{
				logger.debug("group "+name+" was not found in lucene index");
			}
			
			if ( groupNode != null )
			{
				logger.debug("group "+name+" was found on Lucene index => returning it");
				group = new SocialGroupImpl( groupNode );
				if(group==null){logger.error("ERROR while creating instance of group - to be returned");}
			}else{
				logger.debug("returning NULL: Reason : no group found on Lucene with that name ");
			}
			tx.success();
		}finally{
			tx.finish();
		}						
		return group;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#linkGroup(org.societies.personalisation.socialprofiler.datamodel.Person, java.lang.String)
	 */
	
	public SocialGroup linkGroup(SocialPerson person,String groupId){
		logger.debug("linking group to person");
		SocialGroup group=null;
		Transaction tx = getNeoService().beginTx();
		try{
			if (person==null){
				logger.error("ERROR-person-null");
			}else{
				logger.debug("checking to see if a new group will be created or it exists already");
				group=getGroup(groupId);
				if (group==null){
					logger.debug("creating new group before linking");
					group=createGroup(groupId);
					if (group==null){
						logger.fatal("ERROR - group seemed not to exist - was created - but is null");
					}
				}else{
					logger.debug("group solved => linking");
				}
				final Node startNode = ((SocialPersonImpl) person).getUnderlyingNode();
				final Node endNode = ((SocialGroupImpl) group).getUnderlyingNode();
				@SuppressWarnings("unused")
				final Relationship relationship = startNode.createRelationshipTo( endNode, RelTypes.IS_A_MEMBER_OF );
			
				logger.debug(" [Relationship] created");
				logger.debug(" [USER] "+person.getName()+" IS A MEMBER OF [GROUP]"+group.getName());
			}
			tx.success();
		}finally{
			tx.finish();
		}		
		return group;
	}
	

	public void updateGroup (String groupId,String realName,String type,String subType, String updateTime,String description,String creator){
		
		logger.debug("[UPDATE GROUP]");
		Transaction tx = getNeoService().beginTx();
		try{
			SocialGroup group=getGroup(groupId);
			if (group==null){
				logger.error("[NULL] group is null - impossible to update it");
			}
			else{
				if (realName!=null){
					group.setRealName(realName);
				}
				if (type!=null){
					group.setGroupType(type);
				}
				if (subType!=null){
					group.setGroupSubType(subType);
				}
				if (updateTime!=null){
					group.setUpdateTime(updateTime);
				}
				if (description!=null){
					group.setDescription(description);
				}
				if (creator!=null){
					group.setCreator(creator);
				}
				logger.debug("group information was updated successfully");
			}
			tx.success();
		}finally{
			tx.finish();
		}	
		
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#createPageOfInterest(java.lang.String)
	 */
	
	public SocialPage createPageOfInterest(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#getPageOfInterest(java.lang.String)
	 */
	
	public SocialPage getPageOfInterest(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#linkPageOfInterest(org.societies.personalisation.socialprofiler.datamodel.Person, java.lang.String)
	 */
	
	public SocialPage linkPageOfInterest(SocialPerson person,
			String PageOfInterestId) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#updatePageOfInterest(java.lang.String, java.lang.String, java.lang.String)
	 */
	
	public void updatePageOfInterest(String PageOfInterestId, String realName,
			String type) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#createPageOfInterestCategory(java.lang.String)
	 */
	
	public SocialPageCategory createPageOfInterestCategory(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#getPageOfInterestCategory(java.lang.String)
	 */
	
	public SocialPageCategory getPageOfInterestCategory(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#linkPageOfInterestCategory(org.societies.personalisation.socialprofiler.datamodel.PageOfInterest, java.lang.String, org.societies.personalisation.socialprofiler.datamodel.Person)
	 */
	
	public void linkPageOfInterestCategory(SocialPage page,
			String type, SocialPerson person) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#existsRelationshipPageOfInterestPageOfInterestCategory(org.societies.personalisation.socialprofiler.datamodel.PageOfInterest, org.societies.personalisation.socialprofiler.datamodel.PageCategory)
	 */
	
	public boolean existsRelationshipPageOfInterestPageOfInterestCategory(
			SocialPage socialPage, SocialPageCategory PageOfInterestCategory) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#existsRelationshipPersonPageOfInterestCategory(org.societies.personalisation.socialprofiler.datamodel.Person, org.societies.personalisation.socialprofiler.datamodel.PageCategory)
	 */
	
	public boolean existsRelationshipPersonPageOfInterestCategory(
			SocialPerson startPerson, SocialPageCategory PageOfInterestCategory) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#getTopNInterests(org.societies.personalisation.socialprofiler.datamodel.Person, int)
	 */
	
	public ArrayList<String> getTopNInterests(SocialPerson person, int n) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#getInterestsForUser(java.lang.String)
	 */
	
	public ArrayList<String> getInterestsForUser(String personId) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#getCommonInterests(java.util.ArrayList)
	 */
	
	public ArrayList<String> getCommonInterests(ArrayList<String> usersIds) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#qsortRelationship(java.util.ArrayList, int, int, java.util.ArrayList)
	 */
	
	public void qsortRelationship(ArrayList<Integer> array, int start, int end,
			ArrayList<Relationship> relationships) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#qsortString(java.util.ArrayList, int, int, java.util.ArrayList)
	 */
	
	public void qsortString(ArrayList<Float> array, int start, int end,
			ArrayList<String> arrayString) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#qsortStringDouble(java.util.ArrayList, int, int, java.util.ArrayList)
	 */
	
	public void qsortStringDouble(ArrayList<Double> array, int start, int end,
			ArrayList<String> arrayString) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#qsortStringInt(java.util.ArrayList, int, int, java.util.ArrayList)
	 */
	
	public void qsortStringInt(ArrayList<Integer> array, int start, int end,
			ArrayList<String> arrayString) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#getUsersWhoLikePageOfInterestCategory(java.lang.String, java.util.ArrayList, int)
	 */
	
	public ArrayList<String> getUsersWhoLikePageOfInterestCategory(
			String PageOfInterestCategoryName,
			ArrayList<Float> array_users_numbers, int option) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#getTopNUsersWhoLikePageOfInterestCategory(java.lang.String, int)
	 */
	
	public ArrayList<UserInfo> getTopNUsersWhoLikePageOfInterestCategory(
			String PageOfInterestCategoryName, int n) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#qsortSimple(java.util.ArrayList, int, int)
	 */
	
	public void qsortSimple(ArrayList<Long> array, int start, int end) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#qsortSimpleInteger(java.util.ArrayList, int, int)
	 */
	
	public void qsortSimpleInteger(ArrayList<Integer> array, int start, int end) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#qsortSimpleDouble(java.util.ArrayList, int, int)
	 */
	
	public void qsortSimpleDouble(ArrayList<Double> array, int start, int end) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#qsortSimpleFloat(java.util.ArrayList, int, int)
	 */
	
	public void qsortSimpleFloat(ArrayList<Float> array, int start, int end) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#projectArrays(java.util.ArrayList, java.util.ArrayList)
	 */
	
	public void projectArrays(ArrayList<Long> a, ArrayList<Long> b) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#intersectArrays(java.util.ArrayList, java.util.ArrayList)
	 */
	
	public ArrayList<Long> intersectArrays(ArrayList<Long> a, ArrayList<Long> b) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#intersectArraysOfStrings(java.util.ArrayList, java.util.ArrayList)
	 */
	
	public ArrayList<String> intersectArraysOfStrings(ArrayList<String> a,
			ArrayList<String> b) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#calculateAverageUsingBoxplot(java.util.ArrayList)
	 */
	
	public float calculateAverageUsingBoxplot(ArrayList<Float> a) {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#convertArrayOfLongToString(java.util.ArrayList)
	 */
	
	public ArrayList<String> convertArrayOfLongToString(ArrayList<Long> a) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
     * this function converts an arrays list of string into an arraylist of long
     * note : no check is made if the string cannot be parse into a long
     * @param array to be parsed into long
     * @return ArrayList of Long 	
     */
	
	public ArrayList<Long> convertArrayOfStringToLong(ArrayList<String> a){
		ArrayList<Long> result=new ArrayList<Long>();
		if (a!=null){
			for (int i=0;i<a.size();i++){
				result.add(Long.parseLong(a.get(i)));
			}
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#getListOfPageOfInterests(java.lang.String)
	 */
	
	public ArrayList<Long> getListOfPageOfInterests(String userId) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#createGroupCategory(java.lang.String)
	 */
	
	public GroupCategory createGroupCategory(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#getGroupCategory(java.lang.String)
	 */
	
	public GroupCategory getGroupCategory(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#createGroupSubCategory(java.lang.String)
	 */
	
	public GroupSubCategory createGroupSubCategory(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#getGroupSubCategory(java.lang.String)
	 */
	
	public GroupSubCategory getGroupSubCategory(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#linkGroupCategoryAndSubCategory(org.societies.personalisation.socialprofiler.datamodel.Group, java.lang.String, java.lang.String, org.societies.personalisation.socialprofiler.datamodel.Person)
	 */
	
	public void linkGroupCategoryAndSubCategory(SocialGroup group, String type,
			String subType, SocialPerson person) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#existsRelationshipGroupGroupSubCategory(org.societies.personalisation.socialprofiler.datamodel.Group, org.societies.personalisation.socialprofiler.datamodel.GroupSubCategory)
	 */
	
	public boolean existsRelationshipGroupGroupSubCategory(SocialGroup group,
			GroupSubCategory groupSubCategory) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#existsRelationshipGroupSubCategoryGroupCategory(org.societies.personalisation.socialprofiler.datamodel.GroupSubCategory, org.societies.personalisation.socialprofiler.datamodel.GroupCategory)
	 */
	
	public boolean existsRelationshipGroupSubCategoryGroupCategory(
			GroupSubCategory groupSubCategory, GroupCategory groupCategory) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#existsRelationshipPersonGroupSubCategory(org.societies.personalisation.socialprofiler.datamodel.Person, org.societies.personalisation.socialprofiler.datamodel.GroupSubCategory)
	 */
	
	public boolean existsRelationshipPersonGroupSubCategory(SocialPerson startPerson,
			GroupSubCategory groupSubCategory) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#existsRelationshipPersonGroupCategory(org.societies.personalisation.socialprofiler.datamodel.Person, org.societies.personalisation.socialprofiler.datamodel.GroupCategory)
	 */
	
	public boolean existsRelationshipPersonGroupCategory(SocialPerson startPerson,
			GroupCategory groupCategory) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#getTopNGlobalPreferences(org.societies.personalisation.socialprofiler.datamodel.Person, int)
	 */
	
	public ArrayList<String> getTopNGlobalPreferences(SocialPerson person, int n) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#getGlobalPreferencesForUser(java.lang.String)
	 */
	
	public ArrayList<String> getGlobalPreferencesForUser(String personId) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#getCommonGlobalPreferences(java.util.ArrayList)
	 */
	
	public ArrayList<String> getCommonGlobalPreferences(
			ArrayList<String> usersIds) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#getTopNDetailedPreferences(org.societies.personalisation.socialprofiler.datamodel.Person, int)
	 */
	
	public ArrayList<String> getTopNDetailedPreferences(SocialPerson person, int n) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#getDetailedPreferencesForUser(java.lang.String)
	 */
	
	public ArrayList<String> getDetailedPreferencesForUser(String personId) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#getCommonDetailedPreferences(java.util.ArrayList)
	 */
	
	public ArrayList<String> getCommonDetailedPreferences(
			ArrayList<String> usersIds) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#getTopNUsersForCentrality(int, double, java.util.ArrayList, int)
	 */
	
	public ArrayList<UserInfo> getTopNUsersForCentrality(int centrality_type,
			double centrality_thld, ArrayList<String> usersIds, int n) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#getUsersWhoLikeGroupCategory(java.lang.String, java.util.ArrayList, int)
	 */
	
	public ArrayList<String> getUsersWhoLikeGroupCategory(
			String groupCategoryName, ArrayList<Float> array_users_numbers,
			int option) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#getTopNUsersWhoLikeGroupCategory(java.lang.String, int)
	 */
	
	public ArrayList<UserInfo> getTopNUsersWhoLikeGroupCategory(
			String groupCategoryName, int n) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#getUsersWhoLikeGroupSubCategory(java.lang.String, java.util.ArrayList, int)
	 */
	
	public ArrayList<String> getUsersWhoLikeGroupSubCategory(
			String groupSubCategoryName, ArrayList<Float> array_users_numbers,
			int option) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#getTopNUsersWhoLikeGroupSubCategory(java.lang.String, int)
	 */
	
	public ArrayList<UserInfo> getTopNUsersWhoLikeGroupSubCategory(
			String groupSubCategoryName, int n) {
		// TODO Auto-generated method stub
		return null;
	}

	
	
	public ArrayList<Long> getListOfGroups(String userId){
		
		ArrayList<Long> listOfGroups = new ArrayList<Long>();
		SocialPerson person=getPerson(userId);
		Transaction tx = getNeoService().beginTx();
		try{
			
			final Node startNode = ((SocialPersonImpl)person ).getUnderlyingNode();
			Iterator <Relationship> list_relationships= (Iterator <Relationship>) startNode.getRelationships(RelTypes.IS_A_MEMBER_OF,Direction.OUTGOING);
			while(list_relationships.hasNext()){
				Relationship rel	=	list_relationships.next();
				Node groupNode		=	rel.getEndNode();
				SocialGroup group	=	new SocialGroupImpl(groupNode);
				
				String group_name=group.getName();
				listOfGroups.add(Long.parseLong(group_name));
			}	
			tx.success();
		}finally{
			tx.finish();
		}		 	
		return listOfGroups;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#linkNarcissismManiac(org.societies.personalisation.socialprofiler.datamodel.Person, java.lang.String)
	 */
	
	public void linkNarcissismManiac(SocialPerson person, String narcissismManiacId) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#updateNarcissismManiac(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	
	public void updateNarcissismManiac(String narcissismManiacId,
			String frequency, String lastTime, String number) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#incrementNarcissismManiacNumber(java.lang.String)
	 */
	
	public void incrementNarcissismManiacNumber(String narcissismManiacId) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#getNarcissismManiacFrequency(java.lang.String)
	 */
	
	public String getNarcissismManiacFrequency(String narcissismManiacId) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#getNarcissismManiacLastTime(java.lang.String)
	 */
	
	public String getNarcissismManiacLastTime(String narcissismManiacId) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#getNarcissismManiacNumber(java.lang.String)
	 */
	
	public String getNarcissismManiacNumber(String narcissismManiacId) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#linkSuperActiveManiac(org.societies.personalisation.socialprofiler.datamodel.Person, java.lang.String)
	 */
	
	public void linkSuperActiveManiac(SocialPerson person, String superActiveManiacId) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#updateSuperActiveManiac(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	
	public void updateSuperActiveManiac(String superActiveManiacId,
			String frequency, String lastTime, String number) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#incrementSuperActiveManiacNumber(java.lang.String)
	 */
	
	public void incrementSuperActiveManiacNumber(String superActiveManiacId) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#getSuperActiveManiacFrequency(java.lang.String)
	 */
	
	public String getSuperActiveManiacFrequency(String superActiveManiacId) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#getSuperActiveManiacLastTime(java.lang.String)
	 */
	
	public String getSuperActiveManiacLastTime(String superActiveManiacId) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#getSuperActiveManiacNumber(java.lang.String)
	 */
	
	public String getSuperActiveManiacNumber(String superActiveManiacId) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#linkPhotoManiac(org.societies.personalisation.socialprofiler.datamodel.Person, java.lang.String)
	 */
	
	public void linkPhotoManiac(SocialPerson person, String photoManiacId) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#updatePhotoManiac(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	
	public void updatePhotoManiac(String photoManiacId, String frequency,
			String lastTime, String number) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#incrementPhotoManiacNumber(java.lang.String)
	 */
	
	public void incrementPhotoManiacNumber(String photoManiacId) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#getPhotoManiacFrequency(java.lang.String)
	 */
	
	public String getPhotoManiacFrequency(String photoManiacId) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#getPhotoManiacLastTime(java.lang.String)
	 */
	
	public String getPhotoManiacLastTime(String photoManiacId) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#getPhotoManiacNumber(java.lang.String)
	 */
	
	public String getPhotoManiacNumber(String photoManiacId) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#linkSurfManiac(org.societies.personalisation.socialprofiler.datamodel.Person, java.lang.String)
	 */
	
	public void linkSurfManiac(SocialPerson person, String surfManiacId) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#updateSurfManiac(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	
	public void updateSurfManiac(String surfManiacId, String frequency,
			String lastTime, String number) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#incrementSurfManiacNumber(java.lang.String)
	 */
	
	public void incrementSurfManiacNumber(String surfManiacId) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#getSurfManiacFrequency(java.lang.String)
	 */
	
	public String getSurfManiacFrequency(String surfManiacId) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#getSurfManiacLastTime(java.lang.String)
	 */
	
	public String getSurfManiacLastTime(String surfManiacId) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#getSurfManiacNumber(java.lang.String)
	 */
	
	public String getSurfManiacNumber(String surfManiacId) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#linkQuizManiac(org.societies.personalisation.socialprofiler.datamodel.Person, java.lang.String)
	 */
	
	public void linkQuizManiac(SocialPerson person, String quizManiacId) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#updateQuizManiac(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	
	public void updateQuizManiac(String quizManiacId, String frequency,
			String lastTime, String number) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#incrementQuizManiacNumber(java.lang.String)
	 */
	
	public void incrementQuizManiacNumber(String quizManiacId) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#getQuizManiacFrequency(java.lang.String)
	 */
	
	public String getQuizManiacFrequency(String quizManiacId) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#getQuizManiacLastTime(java.lang.String)
	 */
	
	public String getQuizManiacLastTime(String quizManiacId) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#getQuizManiacNumber(java.lang.String)
	 */
	
	public String getQuizManiacNumber(String quizManiacId) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#getUsersWhoArePredominantProfileManiac(int, java.util.ArrayList, int)
	 */
	
	public ArrayList<String> getUsersWhoArePredominantProfileManiac(
			int profile_type, ArrayList<String> usersIds, int option) {
		// TODO Auto-generated method stub
		return null;
	}

//	/* (non-Javadoc)
//	 * @see org.societies.personalisation.socialprofiler.service.Service#getUsersWhoAreProfileManiac(int, java.util.ArrayList, double, int)
//	 */
//	
//	public ArrayList<UserInfo> getUsersWhoAreProfileManiac(int profile_type,
//			ArrayList<String> usersIds, double percentage_limit, int number) {
//		// TODO Auto-generated method stub
//		return null;
//	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#getPredominantProfileForUser(java.lang.String, java.util.ArrayList)
	 */
	
	public ArrayList<String> getPredominantProfileForUser(String personId,
			ArrayList<Integer> user_number_actions) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#getTopNProfileManiac(int, java.util.ArrayList, int)
	 */
	
	public ArrayList<String> getTopNProfileManiac(int profile_type,
			ArrayList<String> usersIds, int n) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#createInterests(java.lang.String)
	 */
	
	public Interests createInterests(String name) {
		Interests interests=null;
		Transaction tx_active = getNeoService().beginTx();
		try{
		
			logger.debug("creating Interests with name "+name);
			logger.debug(" verying there is no Interests in the index with the same name");
			Interests test=getInterests(name);
			if (test!=null){
				logger.info("unable to create Interests with name "+name+", already " +
						"exists an Interests profile with this name");
				return null;
			}
		
			logger.debug("no Interests found with the same name=>ALLOW-> creating " +
					"Interests properly");
			final Node interestsNode=neoService.createNode();
			interests=new InterestsImpl(interestsNode);
			interests.setName(name);
		
			logger.debug("indexing new created Interests to Lucene");
			luceneIndexService.index(interestsNode,NAME_INDEX,name);
			tx_active.success();
		}finally{
			tx_active.finish();
		}																			
		return interests;
	}

	
	
	public Interests getInterests(String name){
		Interests interests = null;
		Transaction tx_active = getNeoService().beginTx();
		try{
			logger.debug("**Reading Lucene Index**  searching for Interests "+name);
			Node interestsNode = luceneIndexService.getSingleNode( NAME_INDEX, name );
			
			if ( interestsNode == null )
			{
				logger.debug("404 [Interest] "+name+" was not found in lucene index");
			}
			
			if ( interestsNode != null )
			{
				logger.debug(" [Interest] "+name+" was found on Lucene index => returning it");
				interests = new InterestsImpl( interestsNode );
			}
			else{
				
				logger.debug("returning NULL: Reason : no Interests found on Lucene with that name ");
			}
			tx_active.success();
		}finally{
			tx_active.finish();
		}						
		return interests;
	}

	
	/**
     * links an Interests to a person using the interests id and the person id
     * if the person is not found then the link operation is not realised
     * if no Interests is found then a new one is created , if it exists the person is 
     * linked to its existing and unique Interests    
     * @param person
     * 			id of the person
     * @param interestsId
     * 			id of the interests
     */
	
	public void linkInterests(SocialPerson person, String interestsId){
		
		logger.debug("[INTEREST] Make links");
		Transaction tx_active = getNeoService().beginTx();
		try{
			if (person==null){
				logger.error("[ERROR] - Person  null");
			}
			
			else{
				
				logger.debug("Get User Interests");
				Interests interests=getInterests(interestsId);
				if (interests==null){
					logger.debug("creating the Interests and then linking it");
					interests=createInterests(interestsId);
					if (interests==null){
						logger.fatal("ERROR - Interests seemed not to exist - " +
								"was created - but is null");
					}
				}else{
					logger.debug("Interests was found succesfully => linking");
				}
				final Node startNode 	= ((SocialPersonImpl) person).getUnderlyingNode();
				final Node endNode 		= ((InterestsImpl) interests).getUnderlyingNode();
				@SuppressWarnings("unused")
				final Relationship relationship = startNode.createRelationshipTo( endNode,
	        		RelTypes.HAS );
				logger.debug("relationship was created");
				logger.debug("Now user "+person.getName()+"HAS  INTERESTS"+
						interests.getName());
			}
			tx_active.success();
		}finally{
			tx_active.finish();
		}		
	}
	
	
	public void updateInterests (String interestsId,String activities,String interestsList,String music,
			String movies , String books,String quotations,String aboutMe,String profileUpdateTime){
		logger.debug("updating Interests information using the latest info found");
		Transaction tx = getNeoService().beginTx();
		try{
			Interests interests=getInterests(interestsId);
			if (interests==null){
				logger.error("Interests is null - impossible to update it");
			}else{
				if (activities!=null){
					interests.setActivities(activities);
				}
				if (interestsList!=null){
					interests.setInterests(interestsList);
				}
				if (music!=null){
					interests.setMusic(music);
				}
				if (movies!=null){
					interests.setMovies(movies);
				}
				if (books!=null){
					interests.setBooks(books);
				}
				if (quotations!=null){
					interests.setQuotations(quotations);
				}
				if (aboutMe!=null){
					interests.setAboutMe(aboutMe);
				}
				if ((profileUpdateTime!=null)&&(!profileUpdateTime.equals("")) &&(Integer.parseInt(profileUpdateTime)!=0)){
					interests.setProfileUpdateTime(profileUpdateTime);
				}
				logger.debug("Interests information was updated successfully");
			}
			tx.success();
		}finally{
			tx.finish();
		}	
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#getInterestsProfileUpdateTime(java.lang.String)
	 */
	
	public String getInterestsProfileUpdateTime(String interestsId) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#createGeneralInfo(java.lang.String)
	 */
	
	public GeneralInfo createGeneralInfo(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#getGeneralInfo(java.lang.String)
	 */
	
	public GeneralInfo getGeneralInfo(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#linkGeneralInfo(org.societies.personalisation.socialprofiler.datamodel.Person, java.lang.String)
	 */
	
	public void linkGeneralInfo(SocialPerson person, String generalInfoId) {
		logger.debug("linking generalInfo to person");
		Transaction tx_active = getNeoService().beginTx();
		try{
			if (person==null){
				logger.error("ERROR-person which was suposed to be linked with generalInfo is null");
			}else{
				logger.debug("verifying there is no other GeneralInfo for this person");
				GeneralInfo generalInfo=getGeneralInfo(generalInfoId);
				if (generalInfo==null){
					logger.debug("creating the GeneralInfo and then linking it");
					generalInfo=createGeneralInfo(generalInfoId);
					if (generalInfo==null){
						logger.fatal("ERROR - GeneralInfo seemed not to exist - " +
								"was created - but is null");
					}
				}else{
					logger.debug("GeneralInfo was found succesfully => linking");
				}
				final Node startNode = ((SocialPersonImpl) person).getUnderlyingNode();
				final Node endNode = ((GeneralInfoImpl) generalInfo).getUnderlyingNode();
				@SuppressWarnings("unused")
				final Relationship relationship = startNode.createRelationshipTo( endNode,
	        		RelTypes.IS_KNOWN_AS );
				logger.debug("relationship was created");
				logger.debug("Now user "+person.getName()+"IS KNOWN AS  GENERAL INFO"+
						generalInfo.getName());
			}
			tx_active.success();
		}finally{
			tx_active.finish();
		}		
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#updateGeneralInfo(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	
	public void updateGeneralInfo(String generalInfoId, String firstName,
			String lastName, String birthday, String sex, String hometown,
			String current_location, String political, String religious) {
		logger.debug("updating GeneralInfo information using the latest info found");
		Transaction tx = getNeoService().beginTx();
		try{
			GeneralInfo generalInfo=getGeneralInfo(generalInfoId);
			if (generalInfo==null){
				logger.error("GeneralInfo is null - impossible to update it");
			}else{
				if (firstName!=null){
					generalInfo.setFirstName(firstName);
				}
				if (lastName!=null){
					generalInfo.setLastName(lastName);
				}
				if (birthday!=null){
					generalInfo.setBirthday(birthday);
				}
				if (sex!=null){
					generalInfo.setSex(sex);
				}
				if (hometown!=null){
					generalInfo.setHometown(hometown);
				}
				if (current_location!=null){
					generalInfo.setCurrentLocation(current_location);
				}
				if (political!=null){
					generalInfo.setPolitical(political);
				}
				if (religious!=null){
					generalInfo.setReligious(religious);
				}
				logger.debug("GeneralInfo information was updated successfully");
			}
			tx.success();
		}finally{
			tx.finish();
		}	
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#listTraverser(org.neo4j.graphdb.Traverser)
	 */
	
	public String listTraverser(Traverser traverser) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#setUndefinedParameters(java.util.ArrayList)
	 */
	
	public void setUndefinedParameters(ArrayList<String> usersIds) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#setUndefinedParameters(org.societies.personalisation.socialprofiler.datamodel.Person)
	 */
	
	public void setUndefinedParameters(SocialPerson person) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#calculateSingleSourceShortestPathBFS()
	 */
	
	public SingleSourceShortestPath<Integer> calculateSingleSourceShortestPathBFS() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#getGraphNodes(org.societies.personalisation.socialprofiler.datamodel.Person)
	 */
	
	public Traverser getGraphNodes(SocialPerson person) {
		// TODO Auto-generated method stub
		return null;
	}

	
	
	/**
     * This method returns all graph nodes from the neo network , 
     * using the traverser relationships all nodes are linked to 
     * a root node in order to avoid multi clustering problems
     * @return Traverser obj
     * @throws NeoException 
     */
	public Traverser getAllGraphNodes() throws NeoException {
	
		Traverser graph =null;
		Transaction tx = getNeoService().beginTx();
		try{
			SocialPersonImpl person=  (SocialPersonImpl) getPerson("ROOT");
		   
			graph = person.getUnderlyingNode().traverse(
			Traverser.Order.BREADTH_FIRST,
			StopEvaluator.END_OF_GRAPH,
			ReturnableEvaluator.ALL_BUT_START_NODE,
			RelTypes.TRAVERSER,
			Direction.BOTH );
			tx.success();
			
		}catch(Exception e){
			logger.error("Error traversing NeoDB graph. Reason: " + e.getMessage());
			throw new NeoException("Error traversing graph: " + e.getMessage());
		}finally{
			tx.finish();
		}	
		return graph;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#getGraphNodesIds(org.neo4j.graphdb.Traverser)
	 */
	
	public ArrayList<String> getGraphNodesIds(Traverser traverser) throws NeoException {
		
		ArrayList <String> result=new ArrayList <String>();
		Transaction tx = getNeoService().beginTx();
		logger.debug("USERS in Node");
		logger.debug("--|");
		try{
			for ( Node friendNode : traverser )
			{
				SocialPersonImpl user=new SocialPersonImpl(friendNode);
				result.add(user.getName());
				logger.debug("   |--- User:"+user.getName());
			}
			tx.success();
		}catch(Exception e){
			logger.error("Error getting NeoDB nodes IDs: " + e.getMessage());
			throw new NeoException("Error getting NeoDB nodes IDs: " + e.getMessage());
		}finally{
			tx.finish();
		}		
		return result;
		
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#getGraphNodesIdsAsLong(org.neo4j.graphdb.Traverser)
	 */
	
	public ArrayList<Long> getGraphNodesIdsAsLong(Traverser traverser) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#createSetOfNodes(java.util.ArrayList)
	 */
	
	public Set<Node> createSetOfNodes(ArrayList<String> usersIds) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#createSetOfNodes(org.neo4j.graphdb.Traverser)
	 */
	
	public Set<Node> createSetOfNodes(Traverser traverser) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#generateBetweennessCentrality(java.util.ArrayList)
	 */
	
	public BetweennessCentrality<Integer> generateBetweennessCentrality(
			ArrayList<String> usersIds) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#generateBetweennessCentrality(org.societies.personalisation.socialprofiler.datamodel.Person)
	 */
	
	public BetweennessCentrality<Integer> generateBetweennessCentrality(
			SocialPerson person) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#createSetOfRelationships(java.util.ArrayList)
	 */
	
	public Set<Relationship> createSetOfRelationships(ArrayList<String> usersIds) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#generateEigenVectorCentralityUsingPower(java.util.ArrayList)
	 */
	
	public EigenvectorCentralityPower generateEigenVectorCentralityUsingPower(
			ArrayList<String> usersIds) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#generateEigenVectorCentralityUsingArnoldi(java.util.ArrayList)
	 */
	
	public EigenvectorCentralityArnoldi generateEigenVectorCentralityUsingArnoldi(
			ArrayList<String> usersIds) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#generateClosenessCentrality(java.util.ArrayList)
	 */
	
	public ClosenessCentrality<Integer> generateClosenessCentrality(
			ArrayList<String> usersIds) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#updateParameters(java.util.ArrayList)
	 */
	
	public void updateParameters(ArrayList<String> usersIds) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#updateParameters(org.societies.personalisation.socialprofiler.datamodel.Person)
	 */
	
	public void updateParameters(SocialPerson person) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#getOnlyPredominantProfileForUser(java.lang.String, java.util.ArrayList)
	 */
	
	public String getOnlyPredominantProfileForUser(String personId,
			ArrayList<Integer> user_number_actions) {
		// TODO Auto-generated method stub
		return null;
	}


	
	public ArrayList<UserInfo> getUsersWhoAreProfileManiac(int profile_type,
			ArrayList<String> usersIds, double percentage_limit, int number) {
		// TODO Auto-generated method stub
		return null;
	}
	
}