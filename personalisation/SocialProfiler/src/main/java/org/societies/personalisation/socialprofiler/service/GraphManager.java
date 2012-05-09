package org.societies.personalisation.socialprofiler.service;

import java.util.ArrayList;
import java.util.HashSet;
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
import org.societies.personalisation.socialprofiler.Variables;
import org.societies.personalisation.socialprofiler.datamodel.GeneralInfo;
import org.societies.personalisation.socialprofiler.datamodel.Interests;
import org.societies.personalisation.socialprofiler.datamodel.RelationshipDescription;
import org.societies.personalisation.socialprofiler.datamodel.SocialPage;
import org.societies.personalisation.socialprofiler.datamodel.SocialPageCategory;
import org.societies.personalisation.socialprofiler.datamodel.SocialPerson;
import org.societies.personalisation.socialprofiler.datamodel.UserInfo;
import org.societies.personalisation.socialprofiler.datamodel.behaviour.Profile;
import org.societies.personalisation.socialprofiler.datamodel.impl.GeneralInfoImpl;
import org.societies.personalisation.socialprofiler.datamodel.impl.InterestsImpl;
import org.societies.personalisation.socialprofiler.datamodel.impl.ProfileImpl;
import org.societies.personalisation.socialprofiler.datamodel.impl.RelTypes;
import org.societies.personalisation.socialprofiler.datamodel.impl.RelationshipDescriptionImpl;
import org.societies.personalisation.socialprofiler.datamodel.impl.SocialPersonImpl;
import org.societies.personalisation.socialprofiler.datamodel.utils.IntegerAdder;
import org.societies.personalisation.socialprofiler.datamodel.utils.IntegerDivider;
import org.societies.personalisation.socialprofiler.exception.NeoException;

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
		
			logger.info("creating person with name "+name);
			logger.debug(" verifying there is no person in the index with the same name");
			SocialPerson test=getPerson(name);
			if (test!=null){
				logger.info("unable to create person with name "+name+", a person with this name already exists");
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
	
	public void deletePerson(String name) {
		Transaction tx = getNeoService().beginTx();
		try{
			logger.debug("deleting person with name "+name);
			Node personNode = luceneIndexService.getSingleNode( NAME_INDEX, name );
			if ( personNode == null )
			{
				logger.debug("person "+name+" was not found in lucene index , nothing to delete");
			}else{
				logger.debug("deleting person "+name+" from neo and from index");
				luceneIndexService.removeIndex(personNode, NAME_INDEX, name);
				Iterator <Relationship> list_relationships=(Iterator <Relationship>) personNode.getRelationships(Direction.BOTH);
				while (list_relationships.hasNext()){
					Relationship rel=list_relationships.next();
					rel.delete();
				}
				personNode.delete();
				
			}
			tx.success();
		}finally{
			tx.finish();
		}			
	}
	
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
	
	public String getPersonTotalActions(String personId) {
		String number="";;
		logger.debug("returning total number of actions for user "+personId);
		Transaction tx = getNeoService().beginTx();
		try{
			SocialPerson person=getPerson(personId);
			if (person==null){
				logger.error(" person is null - impossible to return its total number of actions");
			}else{
				number=person.getTotalNumberOfActions();
				logger.debug("total number of actions was returned successfully");
			}
			tx.success();
		}finally{
			tx.finish();
		}	
		return number;
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
	
	public double getPersonBetweenessCentrality(String personId) {
		double betweenessCentrality = -100;
		logger.debug("returning betweeness centrality parameter for user "+personId);
		Transaction tx = getNeoService().beginTx();
		try{
			SocialPerson person=getPerson(personId);
			if (person==null){
				logger.error(" person is null - impossible to return its betweeness centrality parameter");
			}else{
				betweenessCentrality=person.getParamBetweenessCentr();
				logger.debug("betweeness centrality parameter was returned successfully");
			}
			tx.success();
		}finally{
			tx.finish();
		}	
		return betweenessCentrality;
	}
	
	public void setPersonBetweenessCentrality(String personId,
			double betweenessCentrality) {
		logger.debug("updating betweeness centrality parameter for user "+personId);
		Transaction tx = getNeoService().beginTx();
		try{
			SocialPerson person=getPerson(personId);
			if (person==null){
				logger.error(" person is null - impossible to update its betweeness centrality parameter");
			}else{
				person.setParamBetweenessCentr(betweenessCentrality);
				logger.debug("betweeness centrality parameter was updated successfully");
			}
			tx.success();
		}finally{
			tx.finish();
		}	
	}

	
	public void setPersonEigenVectorCentrality(String personId,
			double eigenVectorCentrality) {
		logger.debug("updating eigenVector centrality parameter for user "+personId);
		Transaction tx = getNeoService().beginTx();
		try{
			SocialPerson person=getPerson(personId);
			if (person==null){
				logger.error(" person is null - impossible to update its eigenVector centrality parameter");
			}else{
				person.setParamEigenVectorCentr(eigenVectorCentrality);
				logger.debug("eigenVector centrality parameter was updated successfully");
			}
			tx.success();
		}finally{
			tx.finish();
		}	
	}

		
	public double getPersonEigenVectorCentrality(String personId) {
		double eigenVectorCentrality = -100;
		logger.debug("returning eigenVector centrality parameter for user "+personId);
		Transaction tx = getNeoService().beginTx();
		try{
			SocialPerson person=getPerson(personId);
			if (person==null){
				logger.error(" person is null - impossible to return its eigenvector centrality parameter");
			}else{
				eigenVectorCentrality=person.getParamEigenVectorCentr();
				logger.debug("eigenvector centrality parameter was returned successfully");
			}
			tx.success();
		}finally{
			tx.finish();
		}	
		return eigenVectorCentrality;
	}
	
	public void setPersonClosenessCentrality(String personId,
			int closenessCentrality) {
		logger.debug("updating closeness centrality parameter for user "+personId);
		Transaction tx = getNeoService().beginTx();
		try{
			SocialPerson person=getPerson(personId);
			if (person==null){
				logger.error(" person is null - impossible to update its closeness centrality parameter");
			}else{
				person.setParamClosenessCentr(closenessCentrality);
				logger.debug("closeness centrality parameter was updated successfully");
			}
			tx.success();
		}finally{
			tx.finish();
		}	
	}
	
	public int getPersonClosenessCentrality(String personId) {
		int closenessCentrality = -100;
		logger.debug("returning closeness centrality parameter for user "+personId);
		Transaction tx = getNeoService().beginTx();
		try{
			SocialPerson person=getPerson(personId);
			if (person==null){
				logger.error(" person is null - impossible to return its closeness centrality parameter");
			}else{
				closenessCentrality=person.getParamClosenessCentr();
				logger.debug("closeness centrality parameter was returned successfully");
			}
			tx.success();
		}finally{
			tx.finish();
		}	
		return closenessCentrality;
	}
	
	public void createDescription(SocialPerson startPerson, SocialPerson endPerson,
			String first, String second) {
		
			//TODO description could be ignored in the future , for the moment it is used 
			RelationshipDescription description;
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
					logger.info("symetric links created between the 2 nodes");
					description = new RelationshipDescriptionImpl( rel );
					if ( description != null )
					{
						description.setName( first+second );
						logger.debug("name of the created description is "+description.getName());//could be removed
					}
					RelationshipDescription description_inv=new RelationshipDescriptionImpl(rel_inv);
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
	
	public RelationshipDescription getDescription(SocialPerson startPerson, SocialPerson endPerson) {
		RelationshipDescription description = null;  
		Transaction tx = getNeoService().beginTx();
		try{
			logger.debug("looking for description between 2 persons");
			if ((startPerson!=null)&&(endPerson!=null)){
				final Node startNode = ((SocialPersonImpl) startPerson).getUnderlyingNode();
				final Node endNode = ((SocialPersonImpl) endPerson).getUnderlyingNode();
				Iterator <Relationship> list_relationships=
					(Iterator <Relationship>) startNode.getRelationships(RelTypes.IS_FRIEND_WITH);
				
				boolean running=true;
				Relationship rel_needed = null;
				logger.debug("testing if any of the relationships corresponds to the searched one");
				while ((list_relationships.hasNext())&&(running==true)){
					Relationship rel=list_relationships.next();
					Node rel_endNode=rel.getEndNode();
					Node rel_startNode=rel.getStartNode();//TODO
					if (( (startNode.equals(rel_startNode))&& (endNode.equals(rel_endNode)) ))// ||
	        		 //( (startNode.equals(rel_endNode))&& (endNode.equals(rel_startNode)) ))
					{
						logger.debug("relationship if type IS_FRIEND_WITH found between the 2 nodes");
						rel_needed=rel;
						running=false;
					}
				}
				if (rel_needed==null) {
					logger.info("the relationship searched doesn't exist");
				}
				description = new RelationshipDescriptionImpl( rel_needed );
			}else if ( startPerson == null )
			{
				logger.error("error while determining the first person of the link- impossible to keep looking for the description");
			}else if ( endPerson == null )
			{
				logger.error("error while determining the second person of the link - impossible to keep looking for the description");
			}
			tx.success();
		}finally{
			tx.finish();
		}		 
        return description;
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
	
	public ArrayList<String> getTopNInterests(SocialPerson person, int n) {
		ArrayList <String> result=new ArrayList<String>();
		Transaction tx = getNeoService().beginTx();
		try{
			final Node startNode = ((SocialPersonImpl) person).getUnderlyingNode();
			Iterator <Relationship> list_relationships=(Iterator <Relationship>) startNode.getRelationships(RelTypes.LIKES,Direction.OUTGOING);
	
			ArrayList<Relationship> array_relationships=new ArrayList<Relationship>();
			ArrayList<Integer> array_number=new ArrayList<Integer>();
			while(list_relationships.hasNext()){
				Relationship rel=list_relationships.next();
				array_relationships.add(rel);
				array_number.add(Integer.parseInt(rel.getProperty("number").toString()));
			}
			qsortRelationship(array_number, 0, array_number.size()-1,array_relationships);
			int size=array_number.size();
			int size_end=size-n;
			if ((size-1)==0){
				size=1;
			}
			if (size_end<0){
				size_end=0;
			}
			for(int i=size-1;i>=size_end;i--){
				logger.debug("number "+array_number.get(i)+" type "+array_relationships.get(i).getProperty("type").toString());
				result.add(array_number.get(i).toString());
				result.add(array_relationships.get(i).getProperty("type").toString());
			}
			tx.success();
		}finally{
			tx.finish();
		}		 
		return result;
	}
	
	public ArrayList<String> getInterestsForUser(String personId) {
		ArrayList <String> result=new ArrayList<String>();
		SocialPerson person=getPerson(personId);
		if (person==null) {
			return null;
		}
		Transaction tx = getNeoService().beginTx();
		try{
			final Node startNode = ((SocialPersonImpl) person).getUnderlyingNode();
			Iterator <Relationship> list_relationships=(Iterator <Relationship>) startNode.getRelationships(RelTypes.LIKES,Direction.OUTGOING);
	
			ArrayList<Relationship> array_relationships=new ArrayList<Relationship>();
			ArrayList<Integer> array_number=new ArrayList<Integer>();
			while(list_relationships.hasNext()){
				Relationship rel=list_relationships.next();
				array_relationships.add(rel);
				array_number.add(Integer.parseInt(rel.getProperty("number").toString()));
			}
			qsortRelationship(array_number, 0, array_number.size()-1,array_relationships);
			for(int i=0;i<array_number.size();i++){
				result.add(array_relationships.get(i).getProperty("type").toString());
				result.add(array_number.get(i).toString());
			}
			tx.success();
		}finally{
			tx.finish();
		}		 
		return result;
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
		if(end <= start) return;
        int comp=array.get(start);
        Relationship comp_rel=relationships.get(start);
        int i = start,j = end + 1;
        for(;;){
            do i++; while(i<end && array.get(i).compareTo(comp)<0);
            do j--; while(j>start && array.get(j).compareTo(comp)>0);
            if(j <= i)   break;
            
            int temp_i = array.get(i);
            int temp_j = array.get(j);
            array.add(i, temp_j);        
            array.remove(i+1);
            array.add(j,temp_i);
            array.remove(j+1);
            
            Relationship rel_i = relationships.get(i);
            Relationship rel_j = relationships.get(j);
            relationships.add(i, rel_j);        
            relationships.remove(i+1);
            relationships.add(j,rel_i);
            relationships.remove(j+1);
        
        }
        array.add(start,array.get(j));
        array.remove(start+1);
        array.add(j,comp);
        array.remove(j+1);
        
        relationships.add(start,relationships.get(j));
        relationships.remove(start+1);
        relationships.add(j,comp_rel);
        relationships.remove(j+1);
        
        
        qsortRelationship(array,start,j-1,relationships);
        qsortRelationship(array,j+1,end,relationships);
	}
	
	public void qsortString(ArrayList<Float> array, int start, int end,
			ArrayList<String> arrayString) {
		 if(end <= start) return;
	        float comp=array.get(start);
	        String comp_str=arrayString.get(start);
	        int i = start,j = end + 1;
	        for(;;){
	            do i++; while(i<end && array.get(i).compareTo(comp)<0);
	            do j--; while(j>start && array.get(j).compareTo(comp)>0);
	            if(j <= i)   break;
	            
	            float temp_i = array.get(i);
	            float temp_j = array.get(j);
	            array.add(i, temp_j);        
	            array.remove(i+1);
	            array.add(j,temp_i);
	            array.remove(j+1);
	            
	            String str_i = arrayString.get(i);
	            String str_j = arrayString.get(j);
	            arrayString.add(i, str_j);        
	            arrayString.remove(i+1);
	            arrayString.add(j,str_i);
	            arrayString.remove(j+1);
	        
	        }
	        array.add(start,array.get(j));
	        array.remove(start+1);
	        array.add(j,comp);
	        array.remove(j+1);
	        
	        arrayString.add(start,arrayString.get(j));
	        arrayString.remove(start+1);
	        arrayString.add(j,comp_str);
	        arrayString.remove(j+1);
	        
	        
	        qsortString(array,start,j-1,arrayString);
	        qsortString(array,j+1,end,arrayString);
	}
	
	public void qsortStringDouble(ArrayList<Double> array, int start, int end,
			ArrayList<String> arrayString) {
		if(end <= start) return;
        double comp=array.get(start);
        String comp_str=arrayString.get(start);
        int i = start,j = end + 1;
        for(;;){
            do i++; while(i<end && array.get(i).compareTo(comp)<0);
            do j--; while(j>start && array.get(j).compareTo(comp)>0);
            if(j <= i)   break;
            
            double temp_i = array.get(i);
            double temp_j = array.get(j);
            array.add(i, temp_j);        
            array.remove(i+1);
            array.add(j,temp_i);
            array.remove(j+1);
            
            String str_i = arrayString.get(i);
            String str_j = arrayString.get(j);
            arrayString.add(i, str_j);        
            arrayString.remove(i+1);
            arrayString.add(j,str_i);
            arrayString.remove(j+1);
        
        }
        array.add(start,array.get(j));
        array.remove(start+1);
        array.add(j,comp);
        array.remove(j+1);
        
        arrayString.add(start,arrayString.get(j));
        arrayString.remove(start+1);
        arrayString.add(j,comp_str);
        arrayString.remove(j+1);
        
        
        qsortStringDouble(array,start,j-1,arrayString);
        qsortStringDouble(array,j+1,end,arrayString);
	}
	
	public void qsortStringInt(ArrayList<Integer> array, int start, int end,
			ArrayList<String> arrayString) {
		 if(end <= start) return;
	        int comp=array.get(start);
	        String comp_str=arrayString.get(start);
	        int i = start,j = end + 1;
	        for(;;){
	            do i++; while(i<end && array.get(i).compareTo(comp)<0);
	            do j--; while(j>start && array.get(j).compareTo(comp)>0);
	            if(j <= i)   break;
	            
	            int temp_i = array.get(i);
	            int temp_j = array.get(j);
	            array.add(i, temp_j);        
	            array.remove(i+1);
	            array.add(j,temp_i);
	            array.remove(j+1);
	            
	            String str_i = arrayString.get(i);
	            String str_j = arrayString.get(j);
	            arrayString.add(i, str_j);        
	            arrayString.remove(i+1);
	            arrayString.add(j,str_i);
	            arrayString.remove(j+1);
	        
	        }
	        array.add(start,array.get(j));
	        array.remove(start+1);
	        array.add(j,comp);
	        array.remove(j+1);
	        
	        arrayString.add(start,arrayString.get(j));
	        arrayString.remove(start+1);
	        arrayString.add(j,comp_str);
	        arrayString.remove(j+1);
	        
	        
	        qsortStringInt(array,start,j-1,arrayString);
	        qsortStringInt(array,j+1,end,arrayString);
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
	
	public void qsortSimple(ArrayList<Long> array, int start, int end) {
		 if(end <= start) return;
	        long comp=array.get(start);
	        int i = start,j = end + 1;
	        for(;;){
	            do i++; while(i<end && array.get(i).compareTo(comp)<0);
	            do j--; while(j>start && array.get(j).compareTo(comp)>0);
	            if(j <= i)   break;
	            
	            long temp_i = array.get(i);
	            long temp_j = array.get(j);
	            array.add(i, temp_j);        
	            array.remove(i+1);
	            array.add(j,temp_i);
	            array.remove(j+1);
	        }
	        array.add(start,array.get(j));
	        array.remove(start+1);
	        array.add(j,comp);
	        array.remove(j+1);
	        qsortSimple(array,start,j-1);
	        qsortSimple(array,j+1,end);
	}
	
	public void qsortSimpleInteger(ArrayList<Integer> array, int start, int end) {
		 if(end <= start) return;
	        int comp=array.get(start);
	        int i = start,j = end + 1;
	        for(;;){
	            do i++; while(i<end && array.get(i).compareTo(comp)<0);
	            do j--; while(j>start && array.get(j).compareTo(comp)>0);
	            if(j <= i)   break;
	            
	            int temp_i = array.get(i);
	            int temp_j = array.get(j);
	            array.add(i, temp_j);        
	            array.remove(i+1);
	            array.add(j,temp_i);
	            array.remove(j+1);
	        }
	        array.add(start,array.get(j));
	        array.remove(start+1);
	        array.add(j,comp);
	        array.remove(j+1);
	        qsortSimpleInteger(array,start,j-1);
	        qsortSimpleInteger(array,j+1,end);
	}
	
	public void qsortSimpleDouble(ArrayList<Double> array, int start, int end) {
		if(end <= start) return;
        double comp=array.get(start);
        int i = start,j = end + 1;
        for(;;){
            do i++; while(i<end && array.get(i).compareTo(comp)<0);
            do j--; while(j>start && array.get(j).compareTo(comp)>0);
            if(j <= i)   break;
            
            double temp_i = array.get(i);
            double temp_j = array.get(j);
            array.add(i, temp_j);        
            array.remove(i+1);
            array.add(j,temp_i);
            array.remove(j+1);
        }
        array.add(start,array.get(j));
        array.remove(start+1);
        array.add(j,comp);
        array.remove(j+1);
        qsortSimpleDouble(array,start,j-1);
        qsortSimpleDouble(array,j+1,end);
	}
	
	public void qsortSimpleFloat(ArrayList<Float> array, int start, int end) {
		if(end <= start) return;
        float comp=array.get(start);
        int i = start,j = end + 1;
        for(;;){
            do i++; while(i<end && array.get(i).compareTo(comp)<0);
            do j--; while(j>start && array.get(j).compareTo(comp)>0);
            if(j <= i)   break;
            
            float temp_i = array.get(i);
            float temp_j = array.get(j);
            array.add(i, temp_j);        
            array.remove(i+1);
            array.add(j,temp_i);
            array.remove(j+1);
        }
        array.add(start,array.get(j));
        array.remove(start+1);
        array.add(j,comp);
        array.remove(j+1);
        qsortSimpleFloat(array,start,j-1);
        qsortSimpleFloat(array,j+1,end);
	}
	
	public void projectArrays(ArrayList<Long> a, ArrayList<Long> b) {
		qsortSimple(a, 0, a.size()-1);
		qsortSimple(b, 0, b.size()-1);
		int i=0,j=0;
		while ((i<a.size())&&(j<b.size())){
			long ai=a.get(i);
			long bj=b.get(j);
			if (ai<bj){
				i++;
			}
			if (ai==bj){
				a.remove(i);
				b.remove(j);
			}
			if (ai>bj){
				j++;
			}
		}
	}
	
	public ArrayList<Long> intersectArrays(ArrayList<Long> a, ArrayList<Long> b) {
		ArrayList <Long> result=new ArrayList <Long>();
		qsortSimple(a, 0, a.size()-1);
		qsortSimple(b, 0, b.size()-1);
		int i=0,j=0;
		while ((i<a.size())&&(j<b.size())){
			long ai=a.get(i);
			long bj=b.get(j);
			if (ai<bj){
				i++;
			}
			if (ai==bj){
				result.add(a.get(i));
				a.remove(i);
				b.remove(j);
			}
			if (ai>bj){
				j++;
			}
		}
		return result;
	}
	
	public ArrayList<String> intersectArraysOfStrings(ArrayList<String> a,
			ArrayList<String> b) {
		ArrayList <String> result=new ArrayList<String>();
		int i=0,j=0;
		while (i<a.size()&& j<b.size()){
			String ai=a.get(i);
			boolean keep_running=true;
			while((j<b.size())&&(keep_running)){
				if (ai.compareToIgnoreCase(b.get(j))==0){
					keep_running=false;
					b.remove(j);
					result.add(ai);
					j=0;
				}else{
					j++;
				}
			}
			i++;j=0;
		}
		return result;
	}
	
	public float calculateAverageUsingBoxplot(ArrayList<Float> a) {
		qsortSimpleFloat(a, 0, a.size()-1);
		if (a.size()>16){ //more than 16 users (minimum lot size for boxplot is 20)
			float sum=0;
			int count=0;
			for(int i=(a.size()/4);i<=(a.size()*3/4);i++){   //consider only those points between 1st and 3rd quartile
				sum+=a.get(i);
				count++;
			}
			return sum/count;
		}else if (a.size()==0){
			return 0;
		}else{
			int count=0;
			float sum=0;
			for(int i=0;i<(a.size()*3/4);i++){ //since lot is not enough , consider only the points till 3rd quartile 
				sum+=a.get(i);
				count++;
			}
			return sum/count;
		}
	}
	
	public ArrayList<String> convertArrayOfLongToString(ArrayList<Long> a) {
		ArrayList<String> result=new ArrayList<String>();
		for (int i=0;i<a.size();i++){
			result.add((a.get(i)).toString());
		}
		return result;
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
	
	@SuppressWarnings("unchecked")
	public boolean existsRelationship(final SocialPerson startPerson, final SocialPerson endPerson){
		boolean result=false;
		Transaction tx = getNeoService().beginTx();
		try{
			logger.debug("looking for description between 2 persons");
			if ((startPerson!=null)&& (endPerson!=null)){
				final Node startNode = ((SocialPersonImpl) startPerson).getUnderlyingNode();
				final Node endNode = ((SocialPersonImpl) endPerson).getUnderlyingNode();
				Iterator <Relationship> list_relationships=(Iterator <Relationship>) startNode.getRelationships(RelTypes.IS_FRIEND_WITH);
				boolean running=true;
				logger.debug("testing if any of the relationships corresponds to the searched one");
				while ((list_relationships.hasNext())&&(running==true)){
					Relationship rel=list_relationships.next();
					Node rel_endNode=rel.getEndNode();
					Node rel_startNode=rel.getStartNode();
					if (( (startNode.equals(rel_startNode))&& (endNode.equals(rel_endNode)) ) ||
							( (startNode.equals(rel_endNode))&& (endNode.equals(rel_startNode)) ))
					{
						logger.debug("relationship if type IS_FRIEND_WITH found between the 2 nodes");
						
						running=false;
						result=true;
						return result;
					}
				}
				logger.info("the relationship searched doesn't exist");
			}else if ( startPerson == null )
			{
				logger.error("error while determining the first person of the link- impossible to continue this check");
			}else if ( endPerson == null )
			{
				logger.error("error while determining the second person of the link - impossible to continue this check");
			}
			tx.success();
		}finally{
			tx.finish();
		}		 
        return result;
	}
	
	public ArrayList<String> getTopNGlobalPreferences(SocialPerson person, int n) {
		ArrayList <String> result=new ArrayList<String>();
		Transaction tx = getNeoService().beginTx();
		try{
			final Node startNode = ((SocialPersonImpl) person).getUnderlyingNode();
			Iterator <Relationship> list_relationships=(Iterator <Relationship>) startNode.getRelationships(
					RelTypes.PREFERS_TYPE,Direction.OUTGOING);
			ArrayList<Relationship> array_relationships=new ArrayList<Relationship>();
			ArrayList<Integer> array_number=new ArrayList<Integer>();
			while(list_relationships.hasNext()){
				Relationship rel=list_relationships.next();
				array_relationships.add(rel);
				array_number.add(Integer.parseInt(rel.getProperty("number").toString()));
			}
			qsortRelationship(array_number, 0, array_number.size()-1,array_relationships);
			int size=array_number.size();
			int size_end=size-n;
			if ((size-1)==0){
				size=1;
			}
			if (size_end<0){
				size_end=0;
			}
			for(int i=size-1;i>=size_end;i--){
				logger.debug("number "+array_number.get(i)+" type "+array_relationships.get(i).getProperty("type").toString());
				result.add(array_number.get(i).toString());
				result.add(array_relationships.get(i).getProperty("type").toString());
			}
			tx.success();
		}finally{
			tx.finish();
		}		 
		return result;
	}
	
	public ArrayList<String> getGlobalPreferencesForUser(String personId) {
		ArrayList <String> result=new ArrayList<String>();
		SocialPerson person=getPerson(personId);
		if (person==null) {
			return null;
		}
		Transaction tx = getNeoService().beginTx();
		try{
			final Node startNode = ((SocialPersonImpl) person).getUnderlyingNode();
			Iterator <Relationship> list_relationships=(Iterator <Relationship>) startNode.getRelationships(
					RelTypes.PREFERS_TYPE,Direction.OUTGOING);
			ArrayList<Relationship> array_relationships=new ArrayList<Relationship>();
			ArrayList<Integer> array_number=new ArrayList<Integer>();
			while(list_relationships.hasNext()){
				Relationship rel=list_relationships.next();
				array_relationships.add(rel);
				array_number.add(Integer.parseInt(rel.getProperty("number").toString()));
			}
			qsortRelationship(array_number, 0, array_number.size()-1,array_relationships);
			for(int i=0;i<array_relationships.size();i++){
				result.add(array_relationships.get(i).getProperty("type").toString());
			}
			tx.success();
		}finally{
			tx.finish();
		}		 
		return result;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#getCommonGlobalPreferences(java.util.ArrayList)
	 */
	
	public ArrayList<String> getCommonGlobalPreferences(
			ArrayList<String> usersIds) {
		// TODO Auto-generated method stub
		return null;
	}

	public ArrayList<String> getTopNDetailedPreferences(SocialPerson person, int n) {
		ArrayList <String> result=new ArrayList<String>();
		Transaction tx = getNeoService().beginTx();
		try{
			final Node startNode = ((SocialPersonImpl) person).getUnderlyingNode();
			Iterator <Relationship> list_relationships=(Iterator <Relationship>) startNode.getRelationships(
					RelTypes.PREFERS_SUBTYPE,Direction.OUTGOING);
			ArrayList<Relationship> array_relationships=new ArrayList<Relationship>();
			ArrayList<Integer> array_number=new ArrayList<Integer>();
			while(list_relationships.hasNext()){
				Relationship rel=list_relationships.next();
				array_relationships.add(rel);
				array_number.add(Integer.parseInt(rel.getProperty("number").toString()));
			}
			qsortRelationship(array_number, 0, array_number.size()-1,array_relationships);
			int size=array_number.size();
			int size_end=size-n;
			if ((size-1)==0){
				size=1;
			}
			if (size_end<0){
				size_end=0;
			}
			for(int i=size-1;i>=size_end;i--){
				logger.debug("number "+array_number.get(i)+" subtype "+array_relationships.get(i).getProperty("subtype").toString());
				result.add(array_number.get(i).toString());
				result.add(array_relationships.get(i).getProperty("subtype").toString());
			}
			tx.success();
		}finally{
			tx.finish();
		}		 
		return result;
	}
	
	public ArrayList<String> getDetailedPreferencesForUser(String personId) {
		ArrayList <String> result=new ArrayList<String>();
		SocialPerson person=getPerson(personId);
		if (person==null) {
			return null;
		}
		Transaction tx = getNeoService().beginTx();
		try{
			final Node startNode = ((SocialPersonImpl) person).getUnderlyingNode();
			Iterator <Relationship> list_relationships=(Iterator <Relationship>) startNode.getRelationships(
					RelTypes.PREFERS_SUBTYPE,Direction.OUTGOING);
			ArrayList<Relationship> array_relationships=new ArrayList<Relationship>();
			ArrayList<Integer> array_number=new ArrayList<Integer>();
			while(list_relationships.hasNext()){
				Relationship rel=list_relationships.next();
				array_relationships.add(rel);
				array_number.add(Integer.parseInt(rel.getProperty("number").toString()));
			}
			qsortRelationship(array_number, 0, array_number.size()-1,array_relationships);
			for(int i=0;i<array_relationships.size();i++){
				result.add(array_relationships.get(i).getProperty("subtype").toString());
			}
			tx.success();
		}finally{
			tx.finish();
		}		 
		return result;
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
	
	public Profile createManiac(final String name, Profile.Type type){
		Profile maniac=null;
		Transaction tx_maniac = getNeoService().beginTx();
		try{
		
			logger.debug("creating maniac with name "+name);
			logger.debug(" verying there is no maniac in the index with the same name");
			Profile test=getManiac(name, type);
			if (test!=null){
				logger.info("unable to create maniac with name "+name+", already " +
						"exists a Maniac profile with this name");
				return null;
			}
		
			logger.debug("no maniac found with the same name=>ALLOW-> creating " +
					"maniac properly");
			final Node maniacNode=neoService.createNode();
			maniac=new ProfileImpl(maniacNode, type);
			maniac.setName(name);
		
			logger.debug("indexing new created maniac to Lucene");
			luceneIndexService.index(maniacNode,NAME_INDEX,name);
			tx_maniac.success();
		}finally{
			tx_maniac.finish();
		}																			
		return maniac;
	}
	
	public void linkManiac(SocialPerson person, String maniacId, Profile.Type type) {
		logger.debug("linking NarcissismManiac to person");
		Transaction tx = getNeoService().beginTx();
		try{
			if (person==null){
				logger.error("ERROR-person which was suposed to be linked with narcissismManiac is null");
			}else{
				logger.debug("verifying there is no other narcissim Maniac for this person");
				Profile p=getManiac(maniacId, type);
				if (p==null){
					logger.debug("creating the narcissismManiac and then linking it");
					p=createManiac(maniacId, type);
					if (p==null){
						logger.fatal("ERROR - Narcissism Maniac seemed not to exist - " +
								"was created - but is null");
					}
				}else{
					logger.debug("NarcissismManiac was found succesfully => linking");
				}
				final Node startNode = ((SocialPersonImpl) person).getUnderlyingNode();
				final Node endNode = ((ProfileImpl) p).getUnderlyingNode();
				@SuppressWarnings("unused")
				final Relationship relationship = startNode.createRelationshipTo( endNode,
	        		RelTypes.HAS_A_PROFILE );
				logger.debug("relationship was created");
				logger.debug("Now user "+person.getName()+"HAS A PROFILE , MANIAC"+p.getName());
			}
			tx.success();
		}finally{
			tx.finish();
		}		
	}
	
	public void updateNarcissismManiac(String narcissismManiacId,
			String frequency, String lastTime, String number) {
		logger.debug("updating NarcissismManiac information using the latest info found");
		Transaction tx = getNeoService().beginTx();
		try{
			Profile narcissismManiac=getManiac(narcissismManiacId, Profile.Type.EGO_CENTRIC);
			if (narcissismManiac==null){
				logger.error("narcissismManiac is null - impossible to update it");
			}else{
				if (frequency!=null){
					narcissismManiac.setFrequency(frequency);
				}
				if (lastTime!=null){
					narcissismManiac.setLastTime(lastTime);
				}
				if (number!=null){
					narcissismManiac.setNumber(number);
				}
				
				logger.debug("NarcissismManiac information was updated successfully");
			}
			tx.success();
		}finally{
			tx.finish();
		}	
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
	
	public Profile getManiac(String name, Profile.Type type){
		Profile maniac = null;
		Transaction tx_Man = getNeoService().beginTx();
		try{
			logger.debug("**Reading Lucene Index**  searching for Maniac profile "+name);
			Node maniacNode = luceneIndexService.getSingleNode( NAME_INDEX, name );
			if ( maniacNode == null )
			{
				logger.debug("Maniac "+name+" was not found in lucene index");
			}
			
			if ( maniacNode != null )
			{
				logger.debug("Maniac "+name+" was found on Lucene index => returning it");
				maniac = new ProfileImpl( maniacNode, type );
				if(maniac==null){logger.error("ERROR while creating instance of " +
						"Maniac - to be returned");}
			}else{
				logger.debug("returning NULL: Reason : no Maniac found " +
						"on Lucene with that name ");
			}
			tx_Man.success();
		}finally{
			tx_Man.finish();
		}						
		return maniac;
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
			} else {
				logger.info(" [Interest] "+name+" was found on Lucene index => returning it");
				interests = new InterestsImpl( interestsNode );
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
		logger.info("updating Interests information using the latest info found");
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
	
	public String getInterestsProfileUpdateTime(String interestsId) {
		String profileUpdateTime="";;
		logger.debug("returning from Interets "+interestsId+" the profile Update Time");
		Transaction tx = getNeoService().beginTx();
		try{
			Interests interests=getInterests(interestsId);
			if (interests==null){
				logger.error("Interests is null - impossible to return its profileUpdateTime");
			}else{
				profileUpdateTime=interests.getProfileUpdateTime();
				logger.debug("Interests ProfileUpdateTime was returned successfully");
			}
			tx.success();
		}finally{
			tx.finish();
		}	
		return profileUpdateTime;
	}
	
	public GeneralInfo createGeneralInfo(String name) {
		GeneralInfo generalInfo=null;
		Transaction tx_active = getNeoService().beginTx();
		try{
		
			logger.debug("creating GeneralInfo with name "+name);
			logger.debug(" verying there is no GeneralInfo in the index with the same name");
			GeneralInfo test=getGeneralInfo(name);
			if (test!=null){
				logger.info("unable to create GeneralInfo with name "+name+", already " +
						"exists an GeneralInfo with this name");
				return null;
			}
		
			logger.debug("no GeneralInfo found with the same name=>ALLOW-> creating " +
					"GeneralInfo properly");
			final Node generalInfoNode=neoService.createNode();
			generalInfo=new GeneralInfoImpl(generalInfoNode);
			generalInfo.setName(name);
		
			logger.debug("indexing new created GeneralInfo to Lucene");
			luceneIndexService.index(generalInfoNode,NAME_INDEX,name);
			tx_active.success();
		}finally{
			tx_active.finish();
		}																			
		return generalInfo;
	}

	public GeneralInfo getGeneralInfo(String name) {
		GeneralInfo generalInfo = null;
		Transaction tx_active = getNeoService().beginTx();
		try{
			logger.debug("**Reading Lucene Index**  searching for GeneralInfo "+name);
			Node generalInfoNode = luceneIndexService.getSingleNode( NAME_INDEX, name );
			if ( generalInfoNode == null )
			{
				logger.debug("GeneralInfo "+name+" was not found in lucene index");
			}
			
			if ( generalInfoNode != null )
			{
				logger.debug("generalInfo "+name+" was found on Lucene index => returning it");
				generalInfo = new GeneralInfoImpl( generalInfoNode );
				if(generalInfo==null){logger.error("ERROR while creating instance of " +
						"GeneralInfo - to be returned");}
			}else{
				logger.debug("returning NULL: Reason : no GeneralInfo found " +
						"on Lucene with that name ");
			}
			tx_active.success();
		}finally{
			tx_active.finish();
		}						
		return generalInfo;
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
			String lastName, String birthday, String gender, String hometown,
			String current_location, String political, String religion) {
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
				if (gender!=null){
					generalInfo.setGender(gender);
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
				if (religion!=null){
					generalInfo.setReligion(religion);
				}
				logger.debug("GeneralInfo information was updated successfully");
			}
			tx.success();
		}finally{
			tx.finish();
		}	
	}

	public String listTraverser(Traverser traverser) {
		String res="";
		for ( Node friend : traverser )
		{
			res+="At depth %d => %s%n"+traverser.currentPosition().depth()+
				friend.getProperty( "name" )+"\n";
		}
		if (res.equals("")){
			return null;
		}
		return res;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#setUndefinedParameters(java.util.ArrayList)
	 */
	
	public void setUndefinedParameters(ArrayList<String> usersIds) {
		for (int i=0;i<usersIds.size();i++){
			setPersonBetweenessCentrality(usersIds.get(i), undefined);
			setPersonEigenVectorCentrality(usersIds.get(i), undefined);
			//setPersonClosenessCentrality(usersIds.get(i), undefined1);
		}
	}
	
	public void setUndefinedParameters(SocialPerson person) {
		SocialPersonImpl p=(SocialPersonImpl) person;
		Transaction tx = getNeoService().beginTx();
		try{
		Traverser graph = p.getUnderlyingNode().traverse(
				Traverser.Order.BREADTH_FIRST,
				StopEvaluator.END_OF_GRAPH,
				ReturnableEvaluator.ALL,
				RelTypes.IS_FRIEND_WITH,
				Direction.BOTH );
		
			for ( Node node : graph )
			{
				node.setProperty(PARAM_BETWEEN_PROPERTY,undefined);
			}
			tx.success();
		}finally{
			tx.finish();
		}	
	}

	
	public SingleSourceShortestPath<Integer> calculateSingleSourceShortestPathBFS() {
		//TODO this method calculates separately single Source Shortest Path because later this parameter is needed for more graph algorithms and the objective is to calculate it only once to improve speed by wrapping in in a parallelcalculate ... class from neo4j
				SingleSourceShortestPath<Integer> singleSourceShortestPath;
				singleSourceShortestPath= new org.neo4j.graphalgo.impl.shortestpath.SingleSourceShortestPathBFS(
						null,
						Direction.BOTH,
						RelTypes.IS_FRIEND_WITH
				);
				return singleSourceShortestPath;
	}

		
	public Traverser getGraphNodes(SocialPerson person) {
		Traverser graph;
		Transaction tx = getNeoService().beginTx();
		try{
				SocialPersonImpl persona=(SocialPersonImpl) person;
			    graph = persona.getUnderlyingNode().traverse(
				Traverser.Order.BREADTH_FIRST,
				StopEvaluator.END_OF_GRAPH,
				ReturnableEvaluator.ALL,
				RelTypes.IS_FRIEND_WITH,
				Direction.BOTH );
				tx.success();
		}finally{
			tx.finish();
		}	
		return graph;
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
			SocialPersonImpl person=  (SocialPersonImpl) getPerson(SocialPerson.ROOT);
		   
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
	
	public ArrayList<Long> getGraphNodesIdsAsLong(Traverser traverser) {
		ArrayList <Long> result=new ArrayList <Long>();
		Transaction tx = getNeoService().beginTx();
		try{
			for ( Node friendNode : traverser )
			{
				SocialPersonImpl user=new SocialPersonImpl(friendNode);
				String userId=user.getName();
				long userIdAsLong=Long.parseLong(userId);
				result.add(userIdAsLong);
			}
			tx.success();
		}finally{
			tx.finish();
		}		
		return result;
	}

	
	public Set<Node> createSetOfNodes(ArrayList<String> usersIds) {
		HashSet<Node> setNode = new HashSet <Node> () ;
		for ( int i=0;i<usersIds.size();i++ )
		{
			SocialPersonImpl p=(SocialPersonImpl)getPerson(usersIds.get(i));
			setNode.add(p.getUnderlyingNode());
		}
		if (setNode==null) {
			logger.error("ERROR set of nodes is null");
		}
		return setNode;
	}

	
	public Set<Node> createSetOfNodes(Traverser traverser) {
		HashSet<Node> setNode = new HashSet <Node> () ;
		for ( Node friend : traverser )
		{
			setNode.add(friend);
		}
		
		if (setNode==null) {
			System.out.println("set of nodes is null !!!possible error");
		}
		return setNode;
	}

		
	public BetweennessCentrality<Integer> generateBetweennessCentrality(
			ArrayList<String> usersIds) {
		BetweennessCentrality<Integer> betweennessCentrality= new BetweennessCentrality<Integer>(calculateSingleSourceShortestPathBFS(), createSetOfNodes(usersIds));
		return betweennessCentrality;
	}
	
	
	public BetweennessCentrality<Integer> generateBetweennessCentrality(
			SocialPerson person) {
		BetweennessCentrality<Integer> betweennessCentrality=new BetweennessCentrality<Integer>(
				calculateSingleSourceShortestPathBFS(),
				createSetOfNodes(getGraphNodes(person))
		);
		return betweennessCentrality;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.socialprofiler.service.Service#createSetOfRelationships(java.util.ArrayList)
	 */
	
	public Set<Relationship> createSetOfRelationships(ArrayList<String> usersIds) {
		HashSet<Relationship> setRelationships = new HashSet <Relationship> () ;
		for ( int i=0;i<usersIds.size();i++ )
		{
			SocialPerson person=getPerson(usersIds.get(i));
			Transaction tx = getNeoService().beginTx();
			try{
				final Node startNode = ((SocialPersonImpl) person).getUnderlyingNode();
				Iterator <Relationship> list_relationships=(Iterator <Relationship>) startNode.getRelationships(
						RelTypes.IS_FRIEND_WITH,Direction.OUTGOING);
				while(list_relationships.hasNext()){
					Relationship rel=list_relationships.next();
					setRelationships.add(rel);
				}	
				tx.success();
			}finally{
				tx.finish();
			}		 	
		}
		if (setRelationships==null) {
			logger.error("ERROR set of Relationships is null");
		}
		return setRelationships;
	}
	
	public EigenvectorCentralityPower generateEigenVectorCentralityUsingPower(
			ArrayList<String> usersIds) {
		Double precision=0.01; // 1% error Note that this is not the error from the correct values, 
				//but the amount of change tolerated in one iteration.
		String name="cost";
		EigenvectorCentralityPower eigenVectorCentrality=new EigenvectorCentralityPower(
		Direction.OUTGOING, 
		new org.neo4j.graphalgo.impl.util.DoubleEvaluator(name), 
		createSetOfNodes(usersIds), 
		createSetOfRelationships(usersIds), 
		precision);
		
		return eigenVectorCentrality;
	}

		
	public EigenvectorCentralityArnoldi generateEigenVectorCentralityUsingArnoldi(
			ArrayList<String> usersIds) {
		Double precision=0.01; // 1% error Note that this is not the error from the correct values, 
				//but the amount of change tolerated in one iteration.
		String name="cost";
		EigenvectorCentralityArnoldi eigenVectorCentrality=new EigenvectorCentralityArnoldi(
		Direction.BOTH, 
		new org.neo4j.graphalgo.impl.util.DoubleEvaluator(name), 
		createSetOfNodes(usersIds), 
		createSetOfRelationships(usersIds), 
		precision);
		
		return eigenVectorCentrality;
	}
	
	public ClosenessCentrality<Integer> generateClosenessCentrality(
			ArrayList<String> usersIds) {
		Integer zeroValue=0;
		SocialPersonImpl p=(SocialPersonImpl)getPerson(usersIds.get(0));
		@SuppressWarnings("unused")
		Node startNode=p.getUnderlyingNode();
		ClosenessCentrality<Integer> closenessCentrality= new ClosenessCentrality<Integer>(
				calculateSingleSourceShortestPathBFS(),
				new IntegerAdder(), 
				zeroValue,
				createSetOfNodes(usersIds),
				new IntegerDivider()
				
		);
		return closenessCentrality;
	}
	
	// FIXME: resolve bugs for BETWEENESS & EIGENVECTOR
	public void updateParameters(ArrayList<String> usersIds) {
		setUndefinedParameters(usersIds);
		logger.debug("+++++  GENERATING Centrality Parameters   +++++");
		logger.debug("BETWEENESS Centrality");
		BetweennessCentrality<Integer> generatorBetweeness=generateBetweennessCentrality(usersIds);
		logger.debug("EIGENVECTOR Centrality");
		EigenvectorCentralityPower eigenVectorCEntrality=generateEigenVectorCentralityUsingPower(usersIds);
		logger.debug("modyfying values");
		//EigenvectorCentralityArnoldi eigenVectorCEntrality=generateEigenVectorCentralityUsingArnoldi(usersIds);
		//logger.debug("CLOSENESS Centrality");
		///ClosenessCentrality<Integer> closenessCentrality=generateClosenessCentrality(usersIds);
		
//		usersIds.add(0, "myself");   //a bug was found on neo4j library , they don't check if first node has a relationship of type IS__FRIEND_WITH ; if the first node does not have any relationship the whole algorithm is blocked ;
										//TODO add normal check instead of putting first a familiar id "myself"
		for(int i=0;i<usersIds.size();i++){
			String userId=usersIds.get(i);
			logger.debug(" user "+userId);
			SocialPersonImpl p=(SocialPersonImpl)getPerson(userId);
			Transaction tx = getNeoService().beginTx();
			try{
				Node user_node=p.getUnderlyingNode();
				Double betweenessValue = null, eigenVectorValue = null;
				if (user_node!=null){
					betweenessValue=generatorBetweeness.getCentrality(user_node);
					eigenVectorValue=eigenVectorCEntrality.getCentrality(user_node);
				}
				/*if (p.getUnderlyingNode()!=null){
					//int closenessValue=
					//closenessCentrality.getCentrality(p.getUnderlyingNode());
				}*/
				if (betweenessValue!=null){
					setPersonBetweenessCentrality(userId, betweenessValue);
				}else{
					setPersonBetweenessCentrality(userId, Double.parseDouble("-999"));
				}
				
				
				if (eigenVectorValue!=null){
					setPersonEigenVectorCentrality(userId, eigenVectorValue);
				}else{
					setPersonEigenVectorCentrality(userId,Double.parseDouble("999"));
				}
				//setPersonClosenessCentrality(usersIds.get(i), closenessValue);
				tx.success();
			}finally{
				tx.finish();
			}			
		}
	}
	
	public void updateParameters(SocialPerson person) {
		setUndefinedParameters(person);
		BetweennessCentrality<Integer> generatorBetween=generateBetweennessCentrality(person);
		Transaction tx = getNeoService().beginTx();
		try{
			for (Node node : getGraphNodes(person)){
				node.setProperty(PARAM_BETWEEN_PROPERTY ,generatorBetween.getCentrality(node));
			}
			tx.success();
		}finally{
			tx.finish();
		}	
	}
	
	public void updatePersonPercentages (String personId,String narcissismManiac,String superActiveManiac,
			String photoManiac,	String surfManiac,String quizManiac,String totalActions){
		logger.debug("updating person profile percentages ");
		Transaction tx = getNeoService().beginTx();
		try{
			SocialPerson person=getPerson(personId);
			if (person==null){
				logger.error("person is null - impossible to update it");
			}else{
				if (narcissismManiac!=null){
					person.setProfilePercentage(Profile.Type.EGO_CENTRIC, narcissismManiac);
				}
				if (superActiveManiac!=null){
					person.setProfilePercentage(Profile.Type.SUPER_ACTIVE, superActiveManiac);
				}
				if (photoManiac!=null){
					person.setProfilePercentage(Profile.Type.PHOTO_MANIAC, photoManiac);
				}
				if (surfManiac!=null){
					person.setProfilePercentage(Profile.Type.SURF_MANIAC, surfManiac);
				}
				if (quizManiac!=null){
					person.setProfilePercentage(Profile.Type.QUIZ_MANIAC, quizManiac);
				}
				if (totalActions!=null){
					person.setTotalNumberOfActions(totalActions);
				}
				logger.debug("person information was updated successfully");
			}
			tx.success();
		}finally{
			tx.finish();
		}	
		
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