/**
 * 
 */
package org.societies.personalization.socialprofiler.service;

import java.util.ArrayList;
import java.util.Set;

import org.neo4j.graphalgo.CostEvaluator;
import org.neo4j.graphalgo.impl.centrality.BetweennessCentrality;
import org.neo4j.graphalgo.impl.centrality.ClosenessCentrality;
import org.neo4j.graphalgo.impl.centrality.EigenvectorCentralityArnoldi;
import org.neo4j.graphalgo.impl.centrality.EigenvectorCentralityPower;
import org.neo4j.graphalgo.impl.shortestpath.SingleSourceShortestPath;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Traverser;
import org.societies.personalization.socialprofiler.datamodel.Description;
import org.societies.personalization.socialprofiler.datamodel.GeneralInfo;
import org.societies.personalization.socialprofiler.datamodel.Group;
import org.societies.personalization.socialprofiler.datamodel.GroupCategory;
import org.societies.personalization.socialprofiler.datamodel.GroupSubCategory;
import org.societies.personalization.socialprofiler.datamodel.Interests;
import org.societies.personalization.socialprofiler.datamodel.PageCategory;
import org.societies.personalization.socialprofiler.datamodel.PageOfInterest;
import org.societies.personalization.socialprofiler.datamodel.Person;
import org.societies.personalization.socialprofiler.datamodel.UserInfo;
import org.societies.personalization.socialprofiler.exception.NeoException;




public interface Service {
	
	
	/**
     * Store a new Person in the node space and add the name to the index or
     * if the person already exists in the database return null
     * @param name
     * @return the new Person
     */
    public Person createPerson( final String name );

    /**
     * this function delete removes a node from the index and then it deletes it from the neo network
     * @param name
     * 		id of the person/node
     */
    public void deletePerson(final String name);
    
     /**
     * Returns the person with the given <code>name</code> or <code>null</code>
     * if not found, this function uses LuceneIndexService
     * @param name
     *            name of person
     * @return person or <code>null</code> if not found
     */
    public Person getPerson(String name );

//    /**
//     * updates the percentages of the person
//     * @param personId
//     * 			id of the person
//     * @param narcissismManiac
//     * 			narcissismManiac percentage
//     * @param superActiveManiac
//     * 			super Active percentage
//     * @param photoManiac
//     * 			photo Maniac percentage
//     * @param surfManiac
//     * 			surfManiac percentage
//     * @param quizManiac
//     * 			quiz Maniac percentage
//     * @param totalActions
//     * 			total number of actions
//     */
//    public void updatePersonPercentages (String personId,String narcissismManiac,String superActiveManiac,
//			String photoManiac,	String surfManiac,String quizManiac , String totalActions);
    
//    /**
//     * setting the CA name for a person using its id
//     * @param personId
//     * 			id of the person
//     * @param caName
//     * 			context awareness platform
//     */
//    public void setPersonCAName (String personId,String caName);
    
//    /**
//     * returning the Context Awareness username for a person from neo network
//     * @param personId
//     * @return
//     */
//    public String getPersonCAName (String personId);
    
    /**
     * returns the total number of actions of the user
     * @param personId
     * 			id of the person
     * @return String total number of actions of the user
     */
    public String getPersonTotalActions (String personId);
        
    /**
     * returns the narcissismPercentage for that user
     * @param personId
     * 			id of the person
     * @return String total narcissism percentage
     */
    public String getPersonNarcissismPercentage (String personId);
    
    /**
     * returns the super active percentage for the user
     * @param personId
     * 			id of the person
     * @return String total super active percentage
     */
    public String getPersonSuperActivePercentage (String personId);
    
    /**
     * returns the photo percentage of the user
     * @param personId
     * 			id of the person
     * @return String photo percentage
     */
    public String getPersonPhotoPercentage (String personId);
    
    /**
     * returns the surf percentage for the user
     * @param personId
     * 			id of the person
     * @return String surf percentage
     */
    public String getPersonSurfPercentage (String personId);
    
    /**
     * returns the quiz percentage for the user
     * @param personId
     * 			id of the person
     * @return String quiz percentage
     */
    public String getPersonQuizPercentage (String personId);
    
    /**
     * function which returns the betweeness centrality parameter of the person
     * @param personId
     * 			id of the person
     * @return double
     * 			betweeness cetrality parameter
     */
	public double getPersonBetweenessCentrality (String personId);
	
	/**
	 * function which allows to set up the betweeness centrality parameter of the person
	 * @param personId
	 * 			id of the person
	 * @param betweenessCentrality
	 * 			double
	 */
	public void setPersonBetweenessCentrality (String personId,double betweenessCentrality);
	
	/**
	 * function which allows to set up the eigenvector centrality parameter of the person
	 * @param personId
	 * 			id of the person
	 * @param eigenVectorCentrality
	 */
	public void setPersonEigenVectorCentrality (String personId,double eigenVectorCentrality);
		
	/**function which returns the eigenvector centrality parameter of the person
	 * 
	 * @param personId
	 * 			id of the person
	 * @return
	 */
	public double getPersonEigenVectorCentrality (String personId);
	
	/**
	 * function which sets up the closeness centrality parameter for a particulat person
	 * @param personId
	 * 			id of the person
	 * @param closenessCentrality
	 * 			value of the parameter to be added
	 */
	public void setPersonClosenessCentrality (String personId,int closenessCentrality);
	
	/**
	 * function which returns the closenesss centrality parameter of a particular person, using
	 * its id
	 * @param personId
	 * 			id of the person
	 * @return Integer - closeness value
	 */
	public int getPersonClosenessCentrality (String personId);
		
	/**
     * Establish a link between the 2 persons , A IS_FRIEND_WITH B
     * reason :what things they have in common
     *  
     * @params startPerson 
     *            name of the first person 
     *          endPerson
     *          	name of the second person 
     *          nameDescription
     *          	name of the description  
     * 
     */
    public void createDescription(final Person startPerson ,
    		final Person endPerson, final String first , final String second);
    
    /**
     * returns the description between 2 nodes if it exists ; this means 
     * that there is a link between the 2 nodes and moreover that a decription exists for that link
     * normally the name of the description would be the name of first node concatenated with name of 
     * second node
     * 
     * @param nameDescription
     * @return  Description of null is not found
    */ 
    public Description getDescription(Person startPerson,Person endPerson);         
     
    /**
     *  creates a new group with the id given as name
     * @param name
     * 			id of the group
     * @return Group
     * 
     */			
    public Group createGroup(final String name);
    
    /**Returns the group with the given <code>name</code> or <code>null</code>
     * if not found, this function uses LuceneIndexService
     * 
     * @param name
     * @return Group or null if no group is found
     */
    public Group getGroup(String name);
    
    /**
     * links a group to a person using the group id and the person id
     * if the person is not found then the link operation is not realised
     * if no group is found then a new one is created , if it exists the person is 
     * linked to the existing group
     * @param person
     * @param groupId
     * @return Group
     */
    public Group linkGroup(Person person,String groupId);
        
    /**
     * updates the fields of the group
     * 
     * @param groupId
     * 			the id of the group
     * @param realName
     * 			the name of the group
     * @param type
     * 			the type of the group
     * @param subType
     * 			the sub type of the group
     * @param updateTime
     * 			the last time the group was updated
     * @param description
     * 			the description of the group
     * @param creator
     * 			the if of the creator of the group
     */
    public void updateGroup (String groupId,String realName,String type,String subType,
			String updateTime,String description,String creator);
    
    /**
     * creates a new PageOfInterest with the id given as name
     * @param name
     * 			id of the PageOfInterest
     * @return
     */
    public PageOfInterest createPageOfInterest(final String name);
    
    /**
     * Returns the group with the given <code>name</code> or <code>null</code>
     * if not found, this function uses LuceneIndexService
     * @param name
     * @return PageOfInterest if exists or null if not found
     */
    public PageOfInterest getPageOfInterest(String name);
    
    /**
     * links a PageOfInterest to a person using the PageOfInterest id and the person id
     * if the person is not found then the link operation is not realised
     * if no PageOfInterest is found then a new one is created , if it exists the person is 
     * linked to the existing PageOfInterest
     * 
     * this function returns the created PageOfInterest
     * @param person
     * @param PageOfInterestId
     * @return PageOfInterest created
     */
    public PageOfInterest linkPageOfInterest(Person person,String PageOfInterestId);
    
    /**
     * updates the fields of the PageOfInterest using the PageOfInterest id
     * @param PageOfInterestId
     * 			id of the PageOfInterest
     * @param realName
     * 			name of the PageOfInterest
     * @param type
     * 			type of the PageOfInterest
     */	
    public void updatePageOfInterest (String PageOfInterestId,String realName,String type);
    
    /**
     * create a new fan page category
     * @param name
     * 			name of the fan page category
     * @return String PageOfInterestCategory
     */
    public PageCategory createPageOfInterestCategory(final String name);
    
    /**
     * returns a PageOfInterestCategory given its name
     * if no fan page category is found null is returned
     * @param name
     * 			name of the fan page category
     * @return PageOfInterestCategory
     */
    public PageCategory getPageOfInterestCategory(String name);
    
    /**
     * function which creates and links or only links is existent a PageOfInterest to its PageOfInterestCategory
     * moreover a second link is done between the person and the PageOfInterestCategory directly
     * @param PageOfInterest
     * 			PageOfInterest needed to linked with its PageOfInterestCategory
     * @param type
     * 			the name of the new or already existent PageOfInterestCategory
     * @param person
     * 			the person which is fan of this PageOfInterest and will be linked directly to its PageOfInterestCategory
     */
    public void linkPageOfInterestCategory(PageOfInterest PageOfInterest,String type,Person person);
    
    /**
     * checks if there is a relationship of type BELONGS_TO - OUTGOING between a fan page and a fan page category
     * returns a boolean true/ false.
     * @param PageOfInterest
     * 			id of the PageOfInterest
     * @param PageOfInterestCategory
     * 			if of the fan page category
     * @return boolean , true if there is a relationships , false is there isn't
     */
    public boolean existsRelationshipPageOfInterestPageOfInterestCategory(final PageOfInterest PageOfInterest, final PageCategory PageOfInterestCategory);
    
    /**
     * checks if there is a relationship of type LIKES - INCOMING between a fan page category and a person
     * returns a boolean true/ false.
     * if the result is true then a second consequence is generated : the number which is given as property to the LIKES relationships
     * is increased with one unity
     * @param startPerson
     * 			id of the person
     * @param PageOfInterestCategory
     * 			id of the fan page category
     * @return boolean true / false
     */
    public boolean existsRelationshipPersonPageOfInterestCategory(final Person startPerson, final PageCategory PageOfInterestCategory);
    
    /**
     * prints the top N interests of the user and returns the information using an ArrayList <String > of size 2*N if
     * possible , both number of likes and type for each relationship
     * @param person
     * 			id of the person
     * @param n
     * 			number of interests of the top
     * @returns ArrayList<String>
     * 			list of pairs number+type for the LIKES relationships
     */
    public ArrayList<String> getTopNInterests(Person person, int n);
    
    /**
     * returns an arrays with all the interests of an user
     * @param personId
     * 			id of the person
     * @return array of String
     */
    public ArrayList<String> getInterestsForUser(String personId);
        
    /**
     * returns the common interests for a group of users 
     * @param usersIds
     * @return Array of String
     */
    public ArrayList<String> getCommonInterests (ArrayList <String> usersIds);
        
    /**
     * this function performs a quicksort algoritm in function of the first array with integers , which is actually an array with all the 
     * numbers belonging to all the LIKES relationships for than user , in the same time a second array is sorted , in the mirror , this array
     * contains all the relationships of type LIKE
     * NOTE : this technique was used in order to reduce the number of necessary tranzactions needed to wrap the operations
     * @param array
     * 			array of integers , all the numbers from the LIKES relationships
     * @param start
     * 			start position of the quicksort algo
     * @param end
     * 			end position of the quicksort algo
     * @param relationships
     * 			second array which will be sorted in the mirror with the first one
     */
    public void qsortRelationship(ArrayList<Integer> array ,int start,int end,ArrayList<Relationship> relationships);
    
    /**
     * this function performs a quicksort algoritm in function of the first array with integers , which is actually an 
     * array with all the numbers belonging to all the LIKES relationships for than user , in the same time a second array 
     * is sorted , in the mirror , this array contains all the users IDS as String
     * NOTE : this technique was used in order to reduce the number of necessary tranzactions needed to wrap the operations
     * @param array
     * 			array of integers , all the numbers from the LIKES relationships
     * @param start
     * 			start position of the quicksort algo
     * @param end
     * 			end position of the quicksort algo
     * @param arrayString
     * 			second array which will be sorted in the mirror with the first one
     */
    public void qsortString(ArrayList<Float> array ,int start,int end,ArrayList<String> arrayString);
    
    /**
     * this function performs a quicksort algoritm in function of the first array with doubles , in the same time a second array 
     * is sorted , in the mirror , this array contains all the users IDS as String
     * NOTE : this technique was used in order to reduce the number of necessary tranzactions needed to wrap the operations
     * @param array
     * 			array of doubles
     * @param start
     * 			start position for quicksort
     * @param end
     * 			end position for quicksort
     * @param arrayString
     * 			second array which will be sorted in the mirror with the first one
     */
    public void qsortStringDouble(ArrayList<Double> array ,int start,int end,ArrayList<String> arrayString);
    
    /**
     * this function performs a quicksort algoritm in function of the first array with int , in the same time a second array 
     * is sorted , in the mirror , 
     * NOTE : this technique was used in order to reduce the number of necessary tranzactions needed to wrap the operations
     * @param array
     * 			array of int
     * @param start
     * 			start position for quicksort
     * @param end
     * 			end position for quicksort
     * @param arrayString
     * 			second array which will be sorted in the mirror with the first one
     */
    public void qsortStringInt(ArrayList<Integer> array ,int start,int end,ArrayList<String> arrayString);
    
    /**
     * this function returns an array list with all the ids of the users who have a LIKES relationship with the PageOfInterestcatagory
     * the second parameter will the particulat number which characterises each LIKES relationship
     * @param PageOfInterestCategoryName
     * 			name of the PageOfInterest category(e.g CAFE)
     * @param array_users_numbers
     * 			array which will contain all the numbers of the LIKES relationships for the particular users
     * @param option
     * 			if option=FINAL then the result will be a double result containing a string od users ids + ca name for each user
     * 				option=INTERNAL then the result will contain only a string with ids of users
     * @return ArrayList<String> , null if the fan page category is not found
     */
    public ArrayList<String> getUsersWhoLikePageOfInterestCategory(String PageOfInterestCategoryName,
    		ArrayList<Float> array_users_numbers,int option);
    
    /**
     * this function returns the top N users who have a LIKES relationships with the fan pageCategory
     * 
     * @param PageOfInterestCategoryName
     * 			name of the PageOfInterestCategory
     * @param n
     * 			number of users to be returned if possible
     * @return ArrayList of String with the ids of the N users , null is the fan page category is not found
     */
    public ArrayList<UserInfo> getTopNUsersWhoLikePageOfInterestCategory(String PageOfInterestCategoryName,int n);
         
    /**
     * function which performs a quicksort over an array of Longs
     * @param array
     * 			array to be sorted
     * @param start
     * 			start of array
     * @param end
     * 			end of array
     */
    public void qsortSimple(ArrayList<Long> array ,int start,int end);
    
    /**
     * function which performs a quicksort over an array of Integers
     * @param array
     * 			array to be sorted
     * @param start
     * 			start of array
     * @param end
     * 			end of array
     */
    public void qsortSimpleInteger(ArrayList<Integer> array ,int start,int end);
        
    /**
     * function which performs a quicksort over an array of doubles
     * @param array
     * @param start
     * @param end
     */
    public void qsortSimpleDouble(ArrayList<Double> array ,int start,int end);
        
    /**
     * function which performs a quicksort over an array of Floats
     * @param array
     * @param start
     * @param end
     */
    public void qsortSimpleFloat(ArrayList<Float> array ,int start,int end);
    /**
     * function which projects an array A over an array B using mathematical A/B , at the end , array a will contain
     * only those elements which don't exist in b
     * NOTE : to use this function B has to be included into A
     * @param a
     * 			first array - this will give the result
     * @param b
     * 			second array - normally , at the end this will be empty
     */
    public void projectArrays(ArrayList<Long> a, ArrayList<Long> b);
    
    /**
     * function which returns the intersection of 2 s
     * @param a
     * 		first array
     * @param b
     * 		second array
     * @return
     * 	ArrayList of Long
     */		
    public ArrayList<Long> intersectArrays(ArrayList<Long> a, ArrayList<Long> b);
    /**
     * returns the intersection of 2 arrays of Strings
     * @param a
     * 		first array	
     * @param b
     * 		second array
     * @return
     * 		Array of String
     */
    public ArrayList <String> intersectArraysOfStrings(ArrayList <String> a,ArrayList <String> b);
    
    /**
     * function which calculates an average of the elements contained in the " boxplot" , which are 
     * all elements between 1/4 and 3/4
     * @param a
     * 		string for which we calculate the average of the boxplot
     */
    public float calculateAverageUsingBoxplot(ArrayList <Float>a);
        
    /**
     * converts an array of longs into an array of strings
     * @param a
     * 		array to be converted
     * @return array of string
     */
    public ArrayList<String> convertArrayOfLongToString(ArrayList<Long> a);
    /**
     * this function converts an arrays list of string into an arraylist of long
     * note : no check is made if the string cannot be parse into a long
     * @param a
     * 		array to be parsed into long
     * @return
     * 		ArrayList of Long 	
     */
    public ArrayList<Long> convertArrayOfStringToLong(ArrayList<String> a);
    
    /**
     * this function returns the list of fan pages , the user is fan of , in other words ,
     * thos PageOfInterests belonging to neo network for which the user has a relationship IS_A_FAN_OF
     * @param userId
     * 			id of the user
     * @return ArrayList <Long>
     */
    public ArrayList<Long> getListOfPageOfInterests(String userId);
    
    /**
     * creates a new group category
     * @param name
     * 			name of the group category - this corresponds to the group type
     * @return GroupCategory created
     */
    public GroupCategory createGroupCategory(final String name);
    
    /**
     * returns a groupCategory using its name as given parameter
     * @param name
     * 			name of the groupCategory
     * @return GroupCategory
     */
    public GroupCategory getGroupCategory(String name);
    
    /**
     * creates a new Group Sub Category
     * @param name
     * 			name of the group sub category
     * @return GroupSubCategory created
     */
    public GroupSubCategory createGroupSubCategory(final String name);
    
    /**
     * returns a group sub category using its given parameter
     * @param name
     * 			name of the group sub category
     * @return GroupSubCategory
     */
    public GroupSubCategory getGroupSubCategory(String name);
    
    /**
     * function which creates and links or only links is existent a group to its groupSubCategory , the groupSubCategory
     * to its groupCategory ;
     * moreover a second link is done between the person and the groupSubCategory directly and also
     * between the person and the groupCategory
     * @param group
     * 		  Group in cause 	
     * @param type
     * 			String , type - GroupCategory
     * @param subType
     * 			String , subType- GroupSubCategory
     * @param person
     * 			Person in cause
     */
    public void linkGroupCategoryAndSubCategory(Group group,String type,String subType,Person person);
    
    /**
     * checks if there is a relationship of type BELONGS_TO_SUBTYPE , direction OUTGOING 
     * between the group and the groupSubCategory
     * passed as parameters
     * returns a boolean true / false
     * @param group
     * 			Group
     * @param groupSubCategory
     * 			GroupSubCategory
     * @return boolean true / false if found or not found
     */
    public boolean existsRelationshipGroupGroupSubCategory(final Group group, final GroupSubCategory groupSubCategory);
    
    /**
     * checks if there is a relationship of type BELONGS_TO_TYPE, direction OUTGOING 
     * between the groupSubCategory and the groupCategory
     * passed as parameters
     * returns a boolean true / false
     * @param groupSubCategory
     * @param groupCategory
     * @return boolean true/false , if found or not found
     */
    public boolean existsRelationshipGroupSubCategoryGroupCategory(final GroupSubCategory groupSubCategory,
			final GroupCategory groupCategory);
    
    /**
     * checks if there is a relationship of type PREFERS_SUBTYPE, direction OUTGOING 
     *  between the person and the groupSubCategory given as parameters
     * @param startPerson
     * @param groupSubCategory
     * @return boolean true/ false if relationship is found or not found
     */
    public boolean existsRelationshipPersonGroupSubCategory(final Person startPerson, final GroupSubCategory groupSubCategory);
    
    /**
     * checks if there is a relationship of type PREFERS_TYPE , direction OUTGOING
     * between the person and the groupCategory
     * @param startPerson
     * @param groupCategory
     * @return boolean true/ false if relationship is found or not found
     */
    public boolean existsRelationshipPersonGroupCategory(final Person startPerson, final GroupCategory groupCategory);
    
    /**
     * prints the top N global preferences of the user and returns the information using an ArrayList <String > of
     *  size 2*N if possible , both number of PREFERS_TYPE and type for each relationship
     * @param person
     * 			Person  for whom we request the info
     * @param n
     * 			number of global prefernces to print , if possible
     * @return ArrayList <String> list of global preferences
     */
    public ArrayList<String> getTopNGlobalPreferences(Person person, int n);
    
    /**
     * returns all global preferences for an user
     * @param personId
     * 			id of the user
     * @return array of string or null
     */
    public ArrayList<String> getGlobalPreferencesForUser(String personId);
    
    /**
     * returns all common global preferences for a group of users
     * @param usersIds
     * 			array containing all the ids of the users
     * @return array of string or null
     */
    public ArrayList<String> getCommonGlobalPreferences (ArrayList <String> usersIds);
    /**
     * prints the top N detailed preferences of the user and returns the information using an ArrayList <String > of
     *  size 2*N if possible , both number of PREFERS_SUBTYPE and type for each relationship
     * @param person
     * 			Person , for whom we request the info
     * @param n
     * 			number of detailed preferences to print
     * @return ArrayList <String> list of detailed preferences
     */
    public ArrayList<String> getTopNDetailedPreferences(Person person, int n);
    
    /**
     * returns all the detailed preferences for a user
     * @param personId
     * 			id of the user
     * @return array of string or null
     */
    public ArrayList<String> getDetailedPreferencesForUser(String personId);
    
    /**
     * returns all common detailed preferences for a group of users
     * @param usersIds
     * 		array containing all the ids of the users
     * @return array of string or null
     */
    public ArrayList<String> getCommonDetailedPreferences (ArrayList <String> usersIds);
    
    /**
     * returns an array with top N users for a particular centrality parameter , betweeness , eigenvector ..
     * @param centrality_type
     * 			1=BETWEENESS
     * 			2=EIGENVECTOR	
     * @param usersIds
     * @param n
     * 			number of users to print if possible
     * @return
     */
    public ArrayList<UserInfo> getTopNUsersForCentrality(int centrality_type,double centrality_thld ,ArrayList <String> usersIds,int n);
    
    /**
     * this function returns an array list with all the ids of the users who have a PREFERS_TYPE relationship 
     * with the groupcategory
     * the second parameter will the particulat number which characterises each PREFERS_TYPE relationship
     * @param groupCategoryName
     * 			name of group Category Name
     * @param array_users_numbers
     * 			ArrayList <String> list of users numbers corresponding to each PREFERS_TYPE relationship
     * @param option
     * 			if option=FINAL then the result will be a double result containing a string od users ids + ca name for each user
     * 				option=INTERNAL then the result will contain only a string with ids of users
     * @return ArrayList of Strings of ids of users who have a PREFERS_TYPE relationship with the Group Category
     */
    public ArrayList<String> getUsersWhoLikeGroupCategory(String groupCategoryName, ArrayList<Float> array_users_numbers,int option);
    
    /**
     * this function returns the top N users who have a PREFERS_TYPE relationship with the groupCategory
     * @param groupCategoryName
     * 			name of the groupCategory
     * @param n
     * 			numbers of users to return if possible
     * @return ArrayList <String> ids of users or null if the group category is not found
     */
    public ArrayList<UserInfo> getTopNUsersWhoLikeGroupCategory(String groupCategoryName,int n);
    
    /**
     * this function returns an array list with all the ids of the users who have a PREFERS_SUBTYPE relationship 
     * with the groupSubcategory
     * the second parameter will the particulat number which characterises each PREFERS_TYPE relationship
     * @param groupSubCategoryName
     * 			name of the group sub category
     * @param array_users_numbers
     * 			ArrayList <String> list of users numbers corresponding to each PREFERS_SUBTYPE relationship
     * @param option
     * 			if option=FINAL then the result will be a double result containing a string od users ids + ca name for each user
     * 				option=INTERNAL then the result will contain only a string with ids of users
     * @return ArrayList of Strings of ids of users who have a PREFERS_SUBTYPE relationship with the GroupSubCategory
     */
    public ArrayList<String> getUsersWhoLikeGroupSubCategory(String groupSubCategoryName, 
			ArrayList<Float> array_users_numbers,int option);
    
    
    /**
     * this function returns the top N users who have a PREFERS_SUBTYPE relationship with the groupSubCategory
     * @param groupSubCategoryName
     * 			name of the group sub category
     * @param n
     * 			number of users to return if possible
     * @return ArrayList <String> ids of users or null if the group category is not found
     */
    public ArrayList<UserInfo> getTopNUsersWhoLikeGroupSubCategory(String groupSubCategoryName,int n);
        
    /**
     * this function returns a list of all the groups the user is connected to , in other words , all the groups
     * belonging to neo network for which the user has a IS_A_MEMBER_OF relationship
     * @param userId
     * @return
     */
    public ArrayList<Long> getListOfGroups(String userId);
    
   
        
    /**
     * links a narcissismManiac to a person using the narcissismManiac id and the person id
     * if the person is not found then the link operation is not realised
     * if no narcisismManiac profile is found then a new one is created , if it exists the person is 
     * linked to its existing and unique narcissism Maniac profile
     * @param person
     * @param narcissismManiacId
     */
    public void linkNarcissismManiac(Person person,String narcissismManiacId);
        
    /**
     * updates the information of the narcissim maniac profile using the id of the narcissism maniac given 
     * as parameter
     * @param narcissismManiacId
     * 			if of narcissism maniac=if of person+_narcissmManiac
     * @param frequency
     * 			the frequency of this narcissism profile
     * 			how often does the user usually acts as narcissist
     * @param lastTime
     * 			the last time the user acted as a narcissist
     * @param number
     * 			the number of times the user acted as a narcissist	
     */
    public void updateNarcissismManiac (String narcissismManiacId,String frequency,
			String lastTime , String number);
    
    
    /**
     * increments with one unity the total number of time the user acted as a narcissist
     * @param narcissismManiacId
     * 			id of narcissism maniac=id of person+_narcissismManiac
     */
    public void incrementNarcissismManiacNumber (String narcissismManiacId);
        
    /**
     * returns the frequency of this narcissismprofile
     * @param narcissismManiacId
     * 			id of the narcissism Maniac
     * @return String frequency of this narcissist in seconds (unix timestamp model)
     */
    public String getNarcissismManiacFrequency (String narcissismManiacId);
        
    /**
     * returns the lasttime the user acted as a narcissist
     * @param narcissismManiacId
     * 			id of the narcissist Maniac
     * @return String last time (timestamp)
     */
    public String getNarcissismManiacLastTime (String narcissismManiacId);
    
    /**
     * returns the nu;ber of times this user acted as a narcissist Maniac
     * @param narcissismManiacId
     * 			id of the narcissismmaniac
     * @return String total number of narcissism actions
     */
    public String getNarcissismManiacNumber (String narcissismManiacId);
        
   
    
    /**
     * links a superactiveManiac to a person using the superactive Maniac id and the person id
     * if the person is not found then the link operation is not realised
     * if no super active maniac profile is found then a new one is created , if it exists the person is 
     * linked to its existing and unique super active Maniac profile
     * @param person
     * @param superActiveManiacId
     */
    public void linkSuperActiveManiac(Person person,String superActiveManiacId);
        
    /**
     * updates the information of this super active maniac using its id as given parameter
     * @param superActiveManiacId
     * 			id of the super active maniac=id of the person+_SUper active maniac
     * @param frequency
     * 			the frequency of actions of this user as super active
     * @param lastTime
     * 			the last time this user acted as super active maniac
     * @param number
     * 			the total number of times this user acted as a super active maniac
     */
    public void updateSuperActiveManiac (String superActiveManiacId,String frequency,
			String lastTime , String number);
    
        
    /**
     * increments the total number of times the user acted as super active with one unity
     * @param superActiveManiacId
     * 				id of the super active maniac
     */
    public void incrementSuperActiveManiacNumber (String superActiveManiacId);
        
    /**
     * returns the frequency of this super active profile , how often this user acted as super active
     * @param superActiveManiacId
     * 		id of super active maniac
     * @return String frequency
     */
    public String getSuperActiveManiacFrequency (String superActiveManiacId);
        
    /**
     * returns the last time the user acted as a super active maniac
     * @param superActiveManiacId
     * 			id of the super active maniac
     * @return String LastTime
     */
    public String getSuperActiveManiacLastTime (String superActiveManiacId);
        
    /**
     * returns the number of times the user acted as super active maniac
     * @param superActiveManiacId
     * @return String number
     */
    public String getSuperActiveManiacNumber (String superActiveManiacId);
    
   
    
    /**
     * links a photoManiac to a person using the photo Maniac id and the person id
     * if the person is not found then the link operation is not realised
     * if no photo maniac profile is found then a new one is created , if it exists the person is 
     * linked to its existing and unique photo Maniac profile
     * @param person
     * 			id of the person
     * @param photoManiacId
     * 			id of the photo maniac
     */
    public void linkPhotoManiac(Person person,String photoManiacId);
        
    /**
     * updates the information of the photo maniac
     * @param photoManiacId
     * 			id of the photo maniac profile
     * @param frequency
     * 			frequency of this user as photo maniac
     * @param lastTime
     * 			the last time this user acted as photo maniac
     * @param number
     * 			the total number of times this user acted as photo maniac
     */
    public void updatePhotoManiac (String photoManiacId,String frequency,
			String lastTime , String number);
    
    
    /**
     * increments the total number the user acted as photo maniac with one unity
     * @param photoManiacId
     * 			id of the photo maniac
     */
    public void incrementPhotoManiacNumber (String photoManiacId);
        
    /**
     * returns the frequency of this user as photo maniac using the id as given parameter
     * @param photoManiacId
     * 			id of photo maniac profile
     * @return String frequency
     */
    public String getPhotoManiacFrequency (String photoManiacId);
        
    /**
     * returns the last time this user acted as a photo maniac
     * @param photoManiacId
     * 			id of the photo maniac
     * @return String last Time
     */
    public String getPhotoManiacLastTime (String photoManiacId);
        
    /**
     * returns the total number of times the user acted as a photo maniac
     * @param photoManiacId
     * 		id of the photo maniac
     * @return String number
     */
    public String getPhotoManiacNumber (String photoManiacId);
    
    
        
    /**
     * links a surfManiac to a person using the surf Maniac id and the person id
     * if the person is not found then the link operation is not realised
     * if no surf maniac profile is found then a new one is created , if it exists the person is 
     * linked to its existing and unique surf Maniac profile
     * @param person
     * 			id of the person
     * @param surfManiacId
     * 			id of the surf Maniac Profile
     */
    public void linkSurfManiac(Person person,String surfManiacId);
        
    /**
     * updates the information of the surf maniac profile
     * @param surfManiacId
     * 			id of the surf maniac profile
     * @param frequency
     * 			frequency of interactions of the surf maniac profile
     * @param lastTime
     * 			last time the user performed an action as Surf Maniac
     * @param number
     * 			number of total actions as surf maniac	
     */
    public void updateSurfManiac (String surfManiacId,String frequency,
			String lastTime , String number);
    
    
    /**
     * increment the surf maniac number with one unity , this number
     * represents the total number of interactions as SurfManiac
     * @param surfManiacId
     */
    public void incrementSurfManiacNumber (String surfManiacId);
        
    /**
     * returns the frequency of the surf maniac profile 
     * @param surfManiacId
     * 			id of the surf maniac profile
     * @return String frequency
     */
    public String getSurfManiacFrequency (String surfManiacId);
        
    /**
     * returns the last time the user interacted as surf maniac(timestamp)
     * 
     * @param surfManiacId
     * 			id of the surf maniac id		
     * @return String lastTime
     */
    public String getSurfManiacLastTime (String surfManiacId);
    
    /**
     * returns the total number of interactions as surf maniac
     * @param surfManiacId
     * 			id of the surf maniac profile
     * @return String number
     */
    public String getSurfManiacNumber (String surfManiacId);
        
    
        
    /**
     * links a quizManiac to a person using the quiz Maniac id and the person id
     * if the person is not found then the link operation is not realised
     * if no quiz maniac profile is found then a new one is created , if it exists the person is 
     * linked to its existing and unique quiz Maniac profile
     * @param person
     * 			id of the Person
     * @param quizManiacId
     * 			id of the QuizManiac
     */
    public void linkQuizManiac(Person person,String quizManiacId);
    
    /**
     * updates the information of q quiz maniac profile
     * @param quizManiacId
     * 			id of the quiz maniac
     * @param frequency
     * 			frequency of interactions of the quiz maniac profile
     * @param lastTime
     * 			last time the user interacted as a Quiz maniac
     * @param number
     * 			number of interactions as Quiz Maniac
     */
    public void updateQuizManiac (String quizManiacId,String frequency,
			String lastTime , String number);
    
    
    /**
     * increments the total number of quiz interactions with one unity
     * @param quizManiacId
     * 			id of the quiz maniac profile
     */
    public void incrementQuizManiacNumber (String quizManiacId);
        
    /**
     * returns the frequency of interactions of the user as Quiz Maniac
     * @param quizManiacId
     * 			id of the quiz maniac profile
     * @return String frequency
     */
    public String getQuizManiacFrequency (String quizManiacId);
        
    /**
     * returns the last time the user interacted as Quiz Maniac
     * @param quizManiacId
     * 			id of the quiz maniac profile	
     * @return String last time - timestamp
     */
    public String getQuizManiacLastTime (String quizManiacId);
        
    /**
     * returns the total number of actions as quiz maniac 
     * @param quizManiacId
     * 			id of the quiz maniac profile
     * @return String number
     */
    public String getQuizManiacNumber (String quizManiacId);
        
    /**
     * this function returns all the users who are profile predominant of type , given as parameter
     * @param profile_type
     * @param usersIds
     * @param option
     * 			option=FINAL returns users+ CA names in the same array
     * 			option=INTERNAL returns only users
     * @return
     */
    public ArrayList<String> getUsersWhoArePredominantProfileManiac(int profile_type, ArrayList <String> usersIds ,int option);
    
    /**
     * this function returns all the users who have a profile percentage superior than the limit but prints only the first N
     * @param profile_type
     * 			type of profile
     * @param usersIds
     * @param percentage_limit
     * @param option : SIMPLE_INFO the resulst contains only the ids of the users , DOUBLE_INFO the reuslt contains the ids of the users and the percentages
     * @return ArrayList of String 
     */
    public ArrayList<UserInfo> getUsersWhoAreProfileManiac(int profile_type, ArrayList <String> usersIds,double percentage_limit,int number);
        
    /**
     * prints the users predominant profile , this is actually calculated locally
     * @param personId
     * @param user_number_actions
     * 			this array contains the number for all profiles at a particular time instance 
     * 			corresponding to the option
     * @return ArrayList of string containing number of actions for each profile and also the name of profile
     */
    public ArrayList<String> getPredominantProfileForUser(String personId,ArrayList <Integer> user_number_actions);
    
    /**
     * this function gives the TOP N users for a specific profile from the network
     * @param profile_type
     * 			option which specifies for which profile we want the top N
     * @param startPerson
     * 			the person who triggers the test
     * @param n
     * 			number of users to be listed
     * @return ArrayList<String>
     */
    public ArrayList<String> getTopNProfileManiac(int profile_type , ArrayList <String> usersIds,int n);
    
    /**
     * create an Interests information using the id given as name
     * @param name
     * 			id of the new Interests
     * @return Interests created
     */
    public Interests createInterests(final String name);
        
    /**
     * return the Interests using the id given as parameter ,
     * is nothing is found then null is returned    
     * @param name
     * 			id of the Interests
     * @return Interests or null if not found
     */
    public Interests getInterests(String name);
    
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
    public void linkInterests(Person person,String interestsId);
    
    /**
     * updates the information of the interests
     * 
     * @param interestsId
     * 			id of the interests
     * @param activities
     * 			activities of the user
     * @param interestsList
     * 			interests of the user
     * @param music
     * 			favorite music
     * @param movies
     * 			favorite movies
     * @param books
     * 			favorite books
     * @param quotations
     * 			favorite quotations
     * @param aboutMe
     * 			about me section - written by the user
     * @param profileUpdateTime
     * 			the last time the user updated its profile
     */
    public void updateInterests (String interestsId,String activities,String interestsList,String music,
			String movies , String books,String quotations,String aboutMe,String profileUpdateTime);
    
    
    /**
     * returns the last time the user updated its profile
     * @param interestsId
     * 		id of the interests
     * @return String profileLastTime
     * 		
     */
    public String getInterestsProfileUpdateTime (String interestsId);
        
    /**
     * creates a new GeneralInfo using the name given as parameter
     * @param name
     * 			id of the new genralInfo
     * @return GeneralInfo created
     */
    public GeneralInfo createGeneralInfo(final String name);
        
    /**
     * return the GeneralInfo using the id given as parameter ,
     * is nothing is found then null is returned 
     * @param name
     * 			id of the generalInfo
     * @return  GeneralInfo or null if not found
     */
    public GeneralInfo getGeneralInfo(String name);
        
    /**
     * links a GeneralInfo to a person using the GeneralInfo id and the person id
     * if the person is not found then the link operation is not realised
     * if no GeneralInfo is found then a new one is created , if it exists the person is 
     * linked to its existing and unique GeneralInfo
     * @param person
     * @param generalInfoId
     */
    public void linkGeneralInfo(Person person,String generalInfoId);
        
    /**
     * updates the information of the general info
     * @param generalInfoId
     * 			id of the generalInfo
     * @param firstName
     * 			first name of the user
     * @param lastName
     * 			last name of the user
     * @param birthday
     * 			birthday of the user
     * @param sex
     * 			sex of the user
     * @param hometown
     * 			hometown of the user
     * @param current_location
     * 			current location of the user
     * @param political
     * 			political views of the user
     * @param religious
     * 			religious views of the user
     */
    public void updateGeneralInfo (String generalInfoId,String firstName,String lastName,String birthday,
			String sex , String hometown,String current_location,String political,String religious);
    
    
    /**
     * prints the Traverser content , each node , its depth and its name , in order 
     * to have an ideea of the architecture of the graph
     * @params traverser
     * 
     * @result res
     * 		String with all the results
     *  
     */
    public String listTraverser(Traverser traverser); 
        
    /**
     * function to set the parameters to undefined stated - except this one support isolated users
     * and different clusters
     * 
     * @param usersIds
     * 			list of users from neo
     */
    public void setUndefinedParameters(ArrayList<String> usersIds);
    
    /**
     * setting the parameters to undefined state .er
     * this function works only with one cluster
     * 
     * @param person
     * 			the starting person
     */
    public void setUndefinedParameters(Person person);
    
    /**calculates a single source shortestpath using BFS , algo which doesn't consider
     * the cost of the relationship , in the future a more complex algo can be used
     * 
     * @return SingleSourceShortestPath<Integer>
     */
    public SingleSourceShortestPath<Integer> calculateSingleSourceShortestPathBFS ();
    
    /**
     * traverser all the graph starting with the person given as parameter , at the end only the nodes related 
     * with the starting node will be shown , no matter what depth
     * 
     * 
     * @param person
     * @return Traverser
     */
    public Traverser getGraphNodes(Person person);
    
    /**
     * this method returns all graph nodes from the neo network , using the traverser relationships all nodes are linked to 
     * a root node in order to avoid multi clustering problems
     * @return
     * @throws NeoException 
     */
    public Traverser getAllGraphNodes() throws NeoException;
    
    /**
     * returns an ArrayList with all the ids of the NEO network
     * @param traverser
     * 			Traverser of the network
     * @return ArrayList <String>
     * @throws NeoException 
     */
    public ArrayList <String> getGraphNodesIds(Traverser traverser) throws NeoException;
    
    /**
     * returns an Arraylist <Long> with all the ids of the users on NEO network in Long format
     * @param traverser
     * 			Traverser of the network
     * @return ArrayList <Long>
     */
    public ArrayList <Long> getGraphNodesIdsAsLong(Traverser traverser);
    
    /**
     * creates a java.util.Set of Nodes needed as a pameter for the centrality algorithms , such as
     * betweenness centrality
     * NOTE : support multiple clusters and isolated users
     * @param usersIds
     * @return java.util.Set <Node>
     */
    public Set<Node> createSetOfNodes(ArrayList <String> usersIds);
    
    /**
     * creates a java.util.Set of Nodes needed as a pameter for the centrality algorithms , such as
     * betweenness centrality
     * 
     * NOTE : supports only one cluster , cause traverser
     * 
     * @param traverser
     * @return java.util.Set <Node>
     */
    public Set<Node> createSetOfNodes(Traverser traverser);
        
    /**
     * calculates a betweenness centrality algorithm for a given set of nodes , not necessarly linked , 
     * thus supporting isolated users and multiple clusters - 
     * returns a generator variable BetweenessCentrality which
     * can be used to return the values of each node related to the starting person
     * 
     * Class for computing betweenness centrality as defined by Linton C. Freeman
	 * (1977) using the algorithm by Ulrik Brandes (2001).
	 * @complexity: Using a {@link SingleSourceShortestPath} algorithm with time
	 *              complexity A, this algorithm runs in time O(n * (A + m)).
	 *              Examples: This becomes O(n * m) for BFS search and O(n^2 *
	 *              log(n) + n * m) for Dijkstra.
     * 
     * @param usersIds
     * @return BetweennessCentrality<Integer>
     */
    public BetweennessCentrality<Integer> generateBetweennessCentrality (ArrayList <String> usersIds);
    
    /**calculates a betweenness centrality algorithm starting with the person given as parameter till
     * the end of the graph or relationships - returns a generator variable BetweenessCentrality which
     * can be used to return the values of each node related to the starting person
     * 
     * NOTE : support only one cluster as it depends on a travers and it works as long as there are relationships
     * 
     * Class for computing betweenness centrality as defined by Linton C. Freeman
	 * (1977) using the algorithm by Ulrik Brandes (2001).
	 * @complexity: Using a {@link SingleSourceShortestPath} algorithm with time
	 *              complexity A, this algorithm runs in time O(n * (A + m)).
	 *              Examples: This becomes O(n * m) for BFS search and O(n^2 *
	 *              log(n) + n * m) for Dijkstra.

     * @param person
     * @return BetweennessCentrality<Integer>
     */
    public BetweennessCentrality<Integer> generateBetweennessCentrality (Person person);
    
    /**
     *  creates a java.util.Set of Relationships needed as a parameter for the centrality algorithms , such as
     *eigenvector centrality
     * @param usersIds
     * 		list of users
     * @return
     */
    public Set<Relationship> createSetOfRelationships(ArrayList <String> usersIds);
        
    /**
     * calculates a eigenvector centrality algorithm , using power method ,for a given set of nodes , not necessarly linked , 
     * thus supporting isolated users and multiple clusters - 
     * returns a generator variable EigenVectorCentrality which
     * can be used to return the values of each node related to neo network
     * 
     *      * Computing eigenvector centrality with the "power method". Convergence is
	 * dependent of the eigenvalues of the input adjacency matrix (the network). If
	 * the two largest eigenvalues are u1 and u2, a small factor u2/u1 will give a
	 * faster convergence (i.e. faster computation). NOTE: Currently only works on
	 * Doubles.
	 * @complexity The {@link CostEvaluator} is called once for every relationship
	 *             in each iteration. Assuming this is done in constant time, the
	 *             total time complexity is O(i(n + m)) when i iterations are done.
     * 
     * @param usersIds
     * @return
     */
    public EigenvectorCentralityPower generateEigenVectorCentralityUsingPower (ArrayList <String> usersIds);
    
    /**
     * * calculates a eigenvector centrality algorithm , using arnoldi method ,for a given set of nodes , not necessarly linked , 
     * thus supporting isolated users and multiple clusters - 
     * returns a generator variable EigenVectorCentrality which
     * can be used to return the values of each node related to neo network
     * 

		
		
		Computing eigenvector centrality with the "Arnoldi iteration". Convergence is
	 * dependent of the eigenvalues of the input adjacency matrix (the network). If
	 * the two largest eigenvalues are u1 and u2, a small factor u2/u1 will give a
	 * faster convergence (i.e. faster computation). NOTE: Currently only works on
	 * Doubles.
	 * @complexity The {@link CostEvaluator} is called once for every relationship
	 *             in each iteration. Assuming this is done in constant time, the
	 *             total time complexity is O(j(n + m + i)) when j internal restarts
	 *             are required and i iterations are done in the internal
	 *             eigenvector solving of the H matrix. Typically j = the number of
	 *             iterations / k, where normally k = 3.
		
     * @param usersIds
     * @return
     */
    public EigenvectorCentralityArnoldi generateEigenVectorCentralityUsingArnoldi (ArrayList <String> usersIds);
    
    /**
     * calculates a closeness centrality algorithm , ,for a given set of nodes , not necessarly linked , 
     * thus supporting isolated users and multiple clusters - 
     * returns a generator variable EigenVectorCentrality which
     * can be used to return the values of each node related to neo network
     * @param usersIds
     * @return
     */
    public ClosenessCentrality<Integer> generateClosenessCentrality (ArrayList <String> usersIds);
    
    /**
     * ethod used to update parameters , it will generate for e.g a betweeness centrality 
     * algorithm in order to update the parameters of all nodes of the neo network
     * NOTE : supports isolated users or mutiple clusters
     * @param usersIds
     */
    public void updateParameters(ArrayList <String> usersIds);
    
    /**method used to update parameters , it will generate for e.g a betweeness centrality 
     * algorithm in order to update the parameters of all nodes related to the person given as parameter
     * NOTE : works with a single cluster , does not support isolated or mutiple clusters
     * 
     * @param person
     */
    public void updateParameters(Person person);
    
    
    //************************************************************************************************/
    //************************QUERIES COMPLEMENTARY **************************************************/
	//************************************************************************************************/
    
	/**
	 * returns the predominant profile for a user 
	 * @param personId
	 * 			id of the user , facebook id
	 * @param user_number_actions
	 * 			array containing moments in the past with user interactions 
	 */
    public String getOnlyPredominantProfileForUser(String personId,ArrayList <Integer> user_number_actions);
    
  
    
}
