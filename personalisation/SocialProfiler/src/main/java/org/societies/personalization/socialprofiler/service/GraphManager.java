package org.societies.personalization.socialprofiler.service;

import java.util.ArrayList;
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
import org.societies.personalization.socialprofiler.datamodel.GeneralInfo;
import org.societies.personalization.socialprofiler.datamodel.GroupCategory;
import org.societies.personalization.socialprofiler.datamodel.GroupSubCategory;
import org.societies.personalization.socialprofiler.datamodel.Interests;
import org.societies.personalization.socialprofiler.datamodel.PageCategory;
import org.societies.personalization.socialprofiler.datamodel.PageOfInterest;
import org.societies.personalization.socialprofiler.datamodel.RelationshipDescription;
import org.societies.personalization.socialprofiler.datamodel.SocialGroup;
import org.societies.personalization.socialprofiler.datamodel.SocialPerson;
import org.societies.personalization.socialprofiler.datamodel.UserInfo;
import org.societies.personalization.socialprofiler.datamodel.impl.SocialPersonImpl;
import org.societies.personalization.socialprofiler.exception.NeoException;
import org.societies.personalization.socialprofiler.datamodel.impl.RelTypes;





public class ServiceImpl implements Service,Variables{

	
	private static final Logger logger = Logger.getLogger(ServiceImpl.class);
	private GraphDatabaseService neoService;
	private LuceneIndexService luceneIndexService;
	private static final String NAME_INDEX = "name";
	private static final String PARAM_BETWEEN_PROPERTY="betweenness_centrality";
	private static final String COST_PROPERTY="cost";
	private static final String COST_INTEGER_PROPERTY="costInteger";
		
	
	/**Constructor
	 * @param neoService
	 */
	public ServiceImpl(GraphDatabaseService neoService) {
		
		this.neoService = neoService;
		this.luceneIndexService=new LuceneIndexService(neoService);
		
		//enable caching to improve performance
		this.luceneIndexService.enableCache(NAME_INDEX, 1000);		
		//this.searchEngine=new SearchEngineImpl(neoService,indexService);
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
	@Override
	public SocialPerson createPerson(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#deletePerson(java.lang.String)
	 */
	@Override
	public void deletePerson(String name) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#getPerson(java.lang.String)
	 */
	@Override
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
//	@Override
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
//	@Override
//	public void setPersonCAName(String personId, String caName) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	/* (non-Javadoc)
//	 * @see org.societies.personalisation.socialprofiler.service.Service#getPersonCAName(java.lang.String)
//	 */
//	@Override
//	public String getPersonCAName(String personId) {
//		// TODO Auto-generated method stub
//		return null;
//	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#getPersonTotalActions(java.lang.String)
	 */
	@Override
	public String getPersonTotalActions(String personId) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#getPersonNarcissismPercentage(java.lang.String)
	 */
	@Override
	public String getPersonNarcissismPercentage(String personId) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#getPersonSuperActivePercentage(java.lang.String)
	 */
	@Override
	public String getPersonSuperActivePercentage(String personId) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#getPersonPhotoPercentage(java.lang.String)
	 */
	@Override
	public String getPersonPhotoPercentage(String personId) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#getPersonSurfPercentage(java.lang.String)
	 */
	@Override
	public String getPersonSurfPercentage(String personId) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#getPersonQuizPercentage(java.lang.String)
	 */
	@Override
	public String getPersonQuizPercentage(String personId) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#getPersonBetweenessCentrality(java.lang.String)
	 */
	@Override
	public double getPersonBetweenessCentrality(String personId) {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#setPersonBetweenessCentrality(java.lang.String, double)
	 */
	@Override
	public void setPersonBetweenessCentrality(String personId,
			double betweenessCentrality) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#setPersonEigenVectorCentrality(java.lang.String, double)
	 */
	@Override
	public void setPersonEigenVectorCentrality(String personId,
			double eigenVectorCentrality) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#getPersonEigenVectorCentrality(java.lang.String)
	 */
	@Override
	public double getPersonEigenVectorCentrality(String personId) {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#setPersonClosenessCentrality(java.lang.String, int)
	 */
	@Override
	public void setPersonClosenessCentrality(String personId,
			int closenessCentrality) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#getPersonClosenessCentrality(java.lang.String)
	 */
	@Override
	public int getPersonClosenessCentrality(String personId) {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#createDescription(org.societies.personalisation.socialprofiler.datamodel.Person, org.societies.personalisation.socialprofiler.datamodel.Person, java.lang.String, java.lang.String)
	 */
	@Override
	public void createDescription(SocialPerson startPerson, SocialPerson endPerson,
			String first, String second) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#getDescription(org.societies.personalisation.socialprofiler.datamodel.Person, org.societies.personalisation.socialprofiler.datamodel.Person)
	 */
	@Override
	public RelationshipDescription getDescription(SocialPerson startPerson, SocialPerson endPerson) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#createGroup(java.lang.String)
	 */
	@Override
	public SocialGroup createGroup(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#getGroup(java.lang.String)
	 */
	@Override
	public SocialGroup getGroup(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#linkGroup(org.societies.personalisation.socialprofiler.datamodel.Person, java.lang.String)
	 */
	@Override
	public SocialGroup linkGroup(SocialPerson person, String groupId) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#updateGroup(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void updateGroup(String groupId, String realName, String type,
			String subType, String updateTime, String description,
			String creator) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#createPageOfInterest(java.lang.String)
	 */
	@Override
	public PageOfInterest createPageOfInterest(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#getPageOfInterest(java.lang.String)
	 */
	@Override
	public PageOfInterest getPageOfInterest(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#linkPageOfInterest(org.societies.personalisation.socialprofiler.datamodel.Person, java.lang.String)
	 */
	@Override
	public PageOfInterest linkPageOfInterest(SocialPerson person,
			String PageOfInterestId) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#updatePageOfInterest(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void updatePageOfInterest(String PageOfInterestId, String realName,
			String type) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#createPageOfInterestCategory(java.lang.String)
	 */
	@Override
	public PageCategory createPageOfInterestCategory(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#getPageOfInterestCategory(java.lang.String)
	 */
	@Override
	public PageCategory getPageOfInterestCategory(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#linkPageOfInterestCategory(org.societies.personalisation.socialprofiler.datamodel.PageOfInterest, java.lang.String, org.societies.personalisation.socialprofiler.datamodel.Person)
	 */
	@Override
	public void linkPageOfInterestCategory(PageOfInterest PageOfInterest,
			String type, SocialPerson person) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#existsRelationshipPageOfInterestPageOfInterestCategory(org.societies.personalisation.socialprofiler.datamodel.PageOfInterest, org.societies.personalisation.socialprofiler.datamodel.PageCategory)
	 */
	@Override
	public boolean existsRelationshipPageOfInterestPageOfInterestCategory(
			PageOfInterest PageOfInterest, PageCategory PageOfInterestCategory) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#existsRelationshipPersonPageOfInterestCategory(org.societies.personalisation.socialprofiler.datamodel.Person, org.societies.personalisation.socialprofiler.datamodel.PageCategory)
	 */
	@Override
	public boolean existsRelationshipPersonPageOfInterestCategory(
			SocialPerson startPerson, PageCategory PageOfInterestCategory) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#getTopNInterests(org.societies.personalisation.socialprofiler.datamodel.Person, int)
	 */
	@Override
	public ArrayList<String> getTopNInterests(SocialPerson person, int n) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#getInterestsForUser(java.lang.String)
	 */
	@Override
	public ArrayList<String> getInterestsForUser(String personId) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#getCommonInterests(java.util.ArrayList)
	 */
	@Override
	public ArrayList<String> getCommonInterests(ArrayList<String> usersIds) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#qsortRelationship(java.util.ArrayList, int, int, java.util.ArrayList)
	 */
	@Override
	public void qsortRelationship(ArrayList<Integer> array, int start, int end,
			ArrayList<Relationship> relationships) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#qsortString(java.util.ArrayList, int, int, java.util.ArrayList)
	 */
	@Override
	public void qsortString(ArrayList<Float> array, int start, int end,
			ArrayList<String> arrayString) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#qsortStringDouble(java.util.ArrayList, int, int, java.util.ArrayList)
	 */
	@Override
	public void qsortStringDouble(ArrayList<Double> array, int start, int end,
			ArrayList<String> arrayString) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#qsortStringInt(java.util.ArrayList, int, int, java.util.ArrayList)
	 */
	@Override
	public void qsortStringInt(ArrayList<Integer> array, int start, int end,
			ArrayList<String> arrayString) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#getUsersWhoLikePageOfInterestCategory(java.lang.String, java.util.ArrayList, int)
	 */
	@Override
	public ArrayList<String> getUsersWhoLikePageOfInterestCategory(
			String PageOfInterestCategoryName,
			ArrayList<Float> array_users_numbers, int option) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#getTopNUsersWhoLikePageOfInterestCategory(java.lang.String, int)
	 */
	@Override
	public ArrayList<UserInfo> getTopNUsersWhoLikePageOfInterestCategory(
			String PageOfInterestCategoryName, int n) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#qsortSimple(java.util.ArrayList, int, int)
	 */
	@Override
	public void qsortSimple(ArrayList<Long> array, int start, int end) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#qsortSimpleInteger(java.util.ArrayList, int, int)
	 */
	@Override
	public void qsortSimpleInteger(ArrayList<Integer> array, int start, int end) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#qsortSimpleDouble(java.util.ArrayList, int, int)
	 */
	@Override
	public void qsortSimpleDouble(ArrayList<Double> array, int start, int end) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#qsortSimpleFloat(java.util.ArrayList, int, int)
	 */
	@Override
	public void qsortSimpleFloat(ArrayList<Float> array, int start, int end) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#projectArrays(java.util.ArrayList, java.util.ArrayList)
	 */
	@Override
	public void projectArrays(ArrayList<Long> a, ArrayList<Long> b) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#intersectArrays(java.util.ArrayList, java.util.ArrayList)
	 */
	@Override
	public ArrayList<Long> intersectArrays(ArrayList<Long> a, ArrayList<Long> b) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#intersectArraysOfStrings(java.util.ArrayList, java.util.ArrayList)
	 */
	@Override
	public ArrayList<String> intersectArraysOfStrings(ArrayList<String> a,
			ArrayList<String> b) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#calculateAverageUsingBoxplot(java.util.ArrayList)
	 */
	@Override
	public float calculateAverageUsingBoxplot(ArrayList<Float> a) {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#convertArrayOfLongToString(java.util.ArrayList)
	 */
	@Override
	public ArrayList<String> convertArrayOfLongToString(ArrayList<Long> a) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#convertArrayOfStringToLong(java.util.ArrayList)
	 */
	@Override
	public ArrayList<Long> convertArrayOfStringToLong(ArrayList<String> a) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#getListOfPageOfInterests(java.lang.String)
	 */
	@Override
	public ArrayList<Long> getListOfPageOfInterests(String userId) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#createGroupCategory(java.lang.String)
	 */
	@Override
	public GroupCategory createGroupCategory(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#getGroupCategory(java.lang.String)
	 */
	@Override
	public GroupCategory getGroupCategory(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#createGroupSubCategory(java.lang.String)
	 */
	@Override
	public GroupSubCategory createGroupSubCategory(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#getGroupSubCategory(java.lang.String)
	 */
	@Override
	public GroupSubCategory getGroupSubCategory(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#linkGroupCategoryAndSubCategory(org.societies.personalisation.socialprofiler.datamodel.Group, java.lang.String, java.lang.String, org.societies.personalisation.socialprofiler.datamodel.Person)
	 */
	@Override
	public void linkGroupCategoryAndSubCategory(SocialGroup group, String type,
			String subType, SocialPerson person) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#existsRelationshipGroupGroupSubCategory(org.societies.personalisation.socialprofiler.datamodel.Group, org.societies.personalisation.socialprofiler.datamodel.GroupSubCategory)
	 */
	@Override
	public boolean existsRelationshipGroupGroupSubCategory(SocialGroup group,
			GroupSubCategory groupSubCategory) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#existsRelationshipGroupSubCategoryGroupCategory(org.societies.personalisation.socialprofiler.datamodel.GroupSubCategory, org.societies.personalisation.socialprofiler.datamodel.GroupCategory)
	 */
	@Override
	public boolean existsRelationshipGroupSubCategoryGroupCategory(
			GroupSubCategory groupSubCategory, GroupCategory groupCategory) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#existsRelationshipPersonGroupSubCategory(org.societies.personalisation.socialprofiler.datamodel.Person, org.societies.personalisation.socialprofiler.datamodel.GroupSubCategory)
	 */
	@Override
	public boolean existsRelationshipPersonGroupSubCategory(SocialPerson startPerson,
			GroupSubCategory groupSubCategory) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#existsRelationshipPersonGroupCategory(org.societies.personalisation.socialprofiler.datamodel.Person, org.societies.personalisation.socialprofiler.datamodel.GroupCategory)
	 */
	@Override
	public boolean existsRelationshipPersonGroupCategory(SocialPerson startPerson,
			GroupCategory groupCategory) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#getTopNGlobalPreferences(org.societies.personalisation.socialprofiler.datamodel.Person, int)
	 */
	@Override
	public ArrayList<String> getTopNGlobalPreferences(SocialPerson person, int n) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#getGlobalPreferencesForUser(java.lang.String)
	 */
	@Override
	public ArrayList<String> getGlobalPreferencesForUser(String personId) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#getCommonGlobalPreferences(java.util.ArrayList)
	 */
	@Override
	public ArrayList<String> getCommonGlobalPreferences(
			ArrayList<String> usersIds) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#getTopNDetailedPreferences(org.societies.personalisation.socialprofiler.datamodel.Person, int)
	 */
	@Override
	public ArrayList<String> getTopNDetailedPreferences(SocialPerson person, int n) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#getDetailedPreferencesForUser(java.lang.String)
	 */
	@Override
	public ArrayList<String> getDetailedPreferencesForUser(String personId) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#getCommonDetailedPreferences(java.util.ArrayList)
	 */
	@Override
	public ArrayList<String> getCommonDetailedPreferences(
			ArrayList<String> usersIds) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#getTopNUsersForCentrality(int, double, java.util.ArrayList, int)
	 */
	@Override
	public ArrayList<UserInfo> getTopNUsersForCentrality(int centrality_type,
			double centrality_thld, ArrayList<String> usersIds, int n) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#getUsersWhoLikeGroupCategory(java.lang.String, java.util.ArrayList, int)
	 */
	@Override
	public ArrayList<String> getUsersWhoLikeGroupCategory(
			String groupCategoryName, ArrayList<Float> array_users_numbers,
			int option) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#getTopNUsersWhoLikeGroupCategory(java.lang.String, int)
	 */
	@Override
	public ArrayList<UserInfo> getTopNUsersWhoLikeGroupCategory(
			String groupCategoryName, int n) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#getUsersWhoLikeGroupSubCategory(java.lang.String, java.util.ArrayList, int)
	 */
	@Override
	public ArrayList<String> getUsersWhoLikeGroupSubCategory(
			String groupSubCategoryName, ArrayList<Float> array_users_numbers,
			int option) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#getTopNUsersWhoLikeGroupSubCategory(java.lang.String, int)
	 */
	@Override
	public ArrayList<UserInfo> getTopNUsersWhoLikeGroupSubCategory(
			String groupSubCategoryName, int n) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#getListOfGroups(java.lang.String)
	 */
	@Override
	public ArrayList<Long> getListOfGroups(String userId) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#linkNarcissismManiac(org.societies.personalisation.socialprofiler.datamodel.Person, java.lang.String)
	 */
	@Override
	public void linkNarcissismManiac(SocialPerson person, String narcissismManiacId) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#updateNarcissismManiac(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void updateNarcissismManiac(String narcissismManiacId,
			String frequency, String lastTime, String number) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#incrementNarcissismManiacNumber(java.lang.String)
	 */
	@Override
	public void incrementNarcissismManiacNumber(String narcissismManiacId) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#getNarcissismManiacFrequency(java.lang.String)
	 */
	@Override
	public String getNarcissismManiacFrequency(String narcissismManiacId) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#getNarcissismManiacLastTime(java.lang.String)
	 */
	@Override
	public String getNarcissismManiacLastTime(String narcissismManiacId) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#getNarcissismManiacNumber(java.lang.String)
	 */
	@Override
	public String getNarcissismManiacNumber(String narcissismManiacId) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#linkSuperActiveManiac(org.societies.personalisation.socialprofiler.datamodel.Person, java.lang.String)
	 */
	@Override
	public void linkSuperActiveManiac(SocialPerson person, String superActiveManiacId) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#updateSuperActiveManiac(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void updateSuperActiveManiac(String superActiveManiacId,
			String frequency, String lastTime, String number) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#incrementSuperActiveManiacNumber(java.lang.String)
	 */
	@Override
	public void incrementSuperActiveManiacNumber(String superActiveManiacId) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#getSuperActiveManiacFrequency(java.lang.String)
	 */
	@Override
	public String getSuperActiveManiacFrequency(String superActiveManiacId) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#getSuperActiveManiacLastTime(java.lang.String)
	 */
	@Override
	public String getSuperActiveManiacLastTime(String superActiveManiacId) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#getSuperActiveManiacNumber(java.lang.String)
	 */
	@Override
	public String getSuperActiveManiacNumber(String superActiveManiacId) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#linkPhotoManiac(org.societies.personalisation.socialprofiler.datamodel.Person, java.lang.String)
	 */
	@Override
	public void linkPhotoManiac(SocialPerson person, String photoManiacId) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#updatePhotoManiac(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void updatePhotoManiac(String photoManiacId, String frequency,
			String lastTime, String number) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#incrementPhotoManiacNumber(java.lang.String)
	 */
	@Override
	public void incrementPhotoManiacNumber(String photoManiacId) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#getPhotoManiacFrequency(java.lang.String)
	 */
	@Override
	public String getPhotoManiacFrequency(String photoManiacId) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#getPhotoManiacLastTime(java.lang.String)
	 */
	@Override
	public String getPhotoManiacLastTime(String photoManiacId) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#getPhotoManiacNumber(java.lang.String)
	 */
	@Override
	public String getPhotoManiacNumber(String photoManiacId) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#linkSurfManiac(org.societies.personalisation.socialprofiler.datamodel.Person, java.lang.String)
	 */
	@Override
	public void linkSurfManiac(SocialPerson person, String surfManiacId) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#updateSurfManiac(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void updateSurfManiac(String surfManiacId, String frequency,
			String lastTime, String number) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#incrementSurfManiacNumber(java.lang.String)
	 */
	@Override
	public void incrementSurfManiacNumber(String surfManiacId) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#getSurfManiacFrequency(java.lang.String)
	 */
	@Override
	public String getSurfManiacFrequency(String surfManiacId) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#getSurfManiacLastTime(java.lang.String)
	 */
	@Override
	public String getSurfManiacLastTime(String surfManiacId) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#getSurfManiacNumber(java.lang.String)
	 */
	@Override
	public String getSurfManiacNumber(String surfManiacId) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#linkQuizManiac(org.societies.personalisation.socialprofiler.datamodel.Person, java.lang.String)
	 */
	@Override
	public void linkQuizManiac(SocialPerson person, String quizManiacId) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#updateQuizManiac(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void updateQuizManiac(String quizManiacId, String frequency,
			String lastTime, String number) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#incrementQuizManiacNumber(java.lang.String)
	 */
	@Override
	public void incrementQuizManiacNumber(String quizManiacId) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#getQuizManiacFrequency(java.lang.String)
	 */
	@Override
	public String getQuizManiacFrequency(String quizManiacId) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#getQuizManiacLastTime(java.lang.String)
	 */
	@Override
	public String getQuizManiacLastTime(String quizManiacId) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#getQuizManiacNumber(java.lang.String)
	 */
	@Override
	public String getQuizManiacNumber(String quizManiacId) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#getUsersWhoArePredominantProfileManiac(int, java.util.ArrayList, int)
	 */
	@Override
	public ArrayList<String> getUsersWhoArePredominantProfileManiac(
			int profile_type, ArrayList<String> usersIds, int option) {
		// TODO Auto-generated method stub
		return null;
	}

//	/* (non-Javadoc)
//	 * @see org.societies.personalisation.socialprofiler.service.Service#getUsersWhoAreProfileManiac(int, java.util.ArrayList, double, int)
//	 */
//	@Override
//	public ArrayList<UserInfo> getUsersWhoAreProfileManiac(int profile_type,
//			ArrayList<String> usersIds, double percentage_limit, int number) {
//		// TODO Auto-generated method stub
//		return null;
//	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#getPredominantProfileForUser(java.lang.String, java.util.ArrayList)
	 */
	@Override
	public ArrayList<String> getPredominantProfileForUser(String personId,
			ArrayList<Integer> user_number_actions) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#getTopNProfileManiac(int, java.util.ArrayList, int)
	 */
	@Override
	public ArrayList<String> getTopNProfileManiac(int profile_type,
			ArrayList<String> usersIds, int n) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#createInterests(java.lang.String)
	 */
	@Override
	public Interests createInterests(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#getInterests(java.lang.String)
	 */
	@Override
	public Interests getInterests(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#linkInterests(org.societies.personalisation.socialprofiler.datamodel.Person, java.lang.String)
	 */
	@Override
	public void linkInterests(SocialPerson person, String interestsId) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#updateInterests(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void updateInterests(String interestsId, String activities,
			String interestsList, String music, String movies, String books,
			String quotations, String aboutMe, String profileUpdateTime) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#getInterestsProfileUpdateTime(java.lang.String)
	 */
	@Override
	public String getInterestsProfileUpdateTime(String interestsId) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#createGeneralInfo(java.lang.String)
	 */
	@Override
	public GeneralInfo createGeneralInfo(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#getGeneralInfo(java.lang.String)
	 */
	@Override
	public GeneralInfo getGeneralInfo(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#linkGeneralInfo(org.societies.personalisation.socialprofiler.datamodel.Person, java.lang.String)
	 */
	@Override
	public void linkGeneralInfo(SocialPerson person, String generalInfoId) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#updateGeneralInfo(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void updateGeneralInfo(String generalInfoId, String firstName,
			String lastName, String birthday, String sex, String hometown,
			String current_location, String political, String religious) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#listTraverser(org.neo4j.graphdb.Traverser)
	 */
	@Override
	public String listTraverser(Traverser traverser) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#setUndefinedParameters(java.util.ArrayList)
	 */
	@Override
	public void setUndefinedParameters(ArrayList<String> usersIds) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#setUndefinedParameters(org.societies.personalisation.socialprofiler.datamodel.Person)
	 */
	@Override
	public void setUndefinedParameters(SocialPerson person) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#calculateSingleSourceShortestPathBFS()
	 */
	@Override
	public SingleSourceShortestPath<Integer> calculateSingleSourceShortestPathBFS() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#getGraphNodes(org.societies.personalisation.socialprofiler.datamodel.Person)
	 */
	@Override
	public Traverser getGraphNodes(SocialPerson person) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#getAllGraphNodes()
	 */
	@Override
	public Traverser getAllGraphNodes() throws NeoException {
		Traverser graph =null;
		Transaction tx = getNeoService().beginTx();
		try{
			SocialPersonImpl person=(SocialPersonImpl) getPerson("ROOT");
		   
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
	@Override
	public ArrayList<String> getGraphNodesIds(Traverser traverser) throws NeoException {
		
		ArrayList <String> result=new ArrayList <String>();
		Transaction tx = getNeoService().beginTx();
		try{
			for ( Node friendNode : traverser )
			{
				SocialPersonImpl user=new SocialPersonImpl(friendNode);
				result.add(user.getName());
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
	@Override
	public ArrayList<Long> getGraphNodesIdsAsLong(Traverser traverser) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#createSetOfNodes(java.util.ArrayList)
	 */
	@Override
	public Set<Node> createSetOfNodes(ArrayList<String> usersIds) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#createSetOfNodes(org.neo4j.graphdb.Traverser)
	 */
	@Override
	public Set<Node> createSetOfNodes(Traverser traverser) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#generateBetweennessCentrality(java.util.ArrayList)
	 */
	@Override
	public BetweennessCentrality<Integer> generateBetweennessCentrality(
			ArrayList<String> usersIds) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#generateBetweennessCentrality(org.societies.personalisation.socialprofiler.datamodel.Person)
	 */
	@Override
	public BetweennessCentrality<Integer> generateBetweennessCentrality(
			SocialPerson person) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#createSetOfRelationships(java.util.ArrayList)
	 */
	@Override
	public Set<Relationship> createSetOfRelationships(ArrayList<String> usersIds) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#generateEigenVectorCentralityUsingPower(java.util.ArrayList)
	 */
	@Override
	public EigenvectorCentralityPower generateEigenVectorCentralityUsingPower(
			ArrayList<String> usersIds) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#generateEigenVectorCentralityUsingArnoldi(java.util.ArrayList)
	 */
	@Override
	public EigenvectorCentralityArnoldi generateEigenVectorCentralityUsingArnoldi(
			ArrayList<String> usersIds) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#generateClosenessCentrality(java.util.ArrayList)
	 */
	@Override
	public ClosenessCentrality<Integer> generateClosenessCentrality(
			ArrayList<String> usersIds) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#updateParameters(java.util.ArrayList)
	 */
	@Override
	public void updateParameters(ArrayList<String> usersIds) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#updateParameters(org.societies.personalisation.socialprofiler.datamodel.Person)
	 */
	@Override
	public void updateParameters(SocialPerson person) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#getOnlyPredominantProfileForUser(java.lang.String, java.util.ArrayList)
	 */
	@Override
	public String getOnlyPredominantProfileForUser(String personId,
			ArrayList<Integer> user_number_actions) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public ArrayList<UserInfo> getUsersWhoAreProfileManiac(int profile_type,
			ArrayList<String> usersIds, double percentage_limit, int number) {
		// TODO Auto-generated method stub
		return null;
	}
	
}