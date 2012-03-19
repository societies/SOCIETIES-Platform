/**
 * 
 */
package org.societies.personalization.socialprofiler.service;

import java.util.ArrayList;

import org.jfree.data.xy.XYSeries;

/**
 * @author X0145160
 * this interface holds all the method for connecting to the database
 */
public interface DatabaseConnection {
	
	/**
	 * creates a connection to the mysql database and sets time zone to GMT
	 */
	public boolean connectMysql();
	
	
	/**
	 * closes the connection with the mysql database
	 */
	public void closeMysql();

	
	/**
	 * add a user from neo and ca platform into the mysql database , table : users
	 * @param current_id
	 * 			id of the user from facebook and neo
	 * @param ca_Name
	 * 			id of user form ca_platform
	 */
	public void addUserToDatabase(String current_id,String ca_Name);
	
	
	/**
	 * deletes a user from the mysql database if the user is deleted from neo
	 * @param current_id
	 * 			id of the user= facebook id
	 */
	public void deleteUserFromDatabase(String current_id);
	
	
	/**
	 * add user data , so called moments into the database , these moments are used for reconstruction and 
	 * and time windows when doing request
	 * 
	 * @param userId
	 * 			id of user , facebook id
	 * @param lastTime
	 * 			last time the user interacted as a particular profile
	 * @param number
	 * 			number of actions as a particular user profile
	 * @param option
	 * 			profile type	
	 */
	public void sendMomentToDatabase(String userId,String lastTime, int number,int option);
	
	
	/**
	 * calculates the week number using a unix timestamp from facebook ,
	 * week 0 starts on 1 aug 2009 00:00 GMT
	 * note : in order to use in java , timestamp has to written in ms and not in s like in unix
	 * @param lastTime
	 * 			unix timestamp from facebook
	 * @return week number
	 */
	public int calculateWeek(String lastTime);
	
	
	/**
	 * converts a string containing a facebook timestamp into a string format mysql timestamp in order to be inserted 
	into mysql
	 * @param time
	 * 			facebook timestamp
	 * @return string format mysql timestamp yyyy-MM-dd HH:mm:ss
	 */
	public String getMysqlTimeStamp(String time);
	
	
	/**
	 * returns a string format mysql timestamp equivalent of first day of the week , week 0 being 1 aug 2009 GMT , 
	 * in order to be inserted into mysql and to permit to read the mysql database easily
	 * @param week
	 * @return
	 */
	public String getMysqlTimeStampForWeek(int week);
	
	
	/**
	 *this function returns the total number of actions for a particular user in the past , 
	 *using the number of week and the type of profile
	 * @param week
	 * 			week number , week 0= 1 aug 2009 00:00 GMT
	 * @param profile
	 * 			profile type
	 * @param user_id
	 * 			facebook id or neo id
	 * @return 
	 */
	public int getNumberOfActionInPast(int week, int profile , String user_id);
	
	
	/**
	 * this function fills in the XYSeries with values from the Mysql Db corrsponding to a user and a profile
	 * @param userId
	 * 			user id
	 * @param profile
	 * 			user profile
	 * @param caName
	 * 			user id for CA platform
	 * @param profile_name
	 * 			profile name (conversion from int)
	 * @param legend_option
	 * 			specify what to contain the legend , name of the users (PROFILE dimension in title) or
	 * 			name of the profiles , USER dimension in title
	 * @return
	 */
	public XYSeries createSeries(String userId , int profile,String caName, String profile_name,int legend_option);
	
	
	/************************************************************************************/
	/***********************************Group********************************************/
	/************************************************************************************/
	
	
	/**
	 * this functions returns ths sum of a number of interactions for a group of users passed through an array,
	 * for a particular week and profile; the result will be used to generate charts for group evolutions
	 * @param valid_group
	 * 			array containing a list with users ; all the users have to be valid , or this array checked before
	 * @param week
	 * 			number of the week
	 * @param profile
	 * 			type of profile
	 * @return sum
	 */
	public int getSumForGroup(ArrayList<String> valid_group ,int week , int profile);

	
	/**
	 * this functions creates an XYSeries with the profile of a group 
	 * @param group
	 * 			array containing a list with users;NOTE: all users in this array have to be valid or the array 
	 * 			ckecked and corrected before
	 * @param profile
	 * 			type of profile
	 * @param profile_name
	 * 			name of profile or DNS translation for type
	 * @return XYSeries wich will be used to generate the chart
	 */
	public XYSeries createSeriesForGroupProfile( ArrayList<String>group ,int profile, String profile_name);
	
	
	/************************************************************************************/
	/***********************************Community****************************************/
	/************************************************************************************/
	
	
	/**
	 * this function returns the last week which was introduced in the mysql table total_info, in other words , the week 
	 * from the last time the user made an update on the engine and network
	 *
	 * @return number of the last week
	 */
	public int getLastWeekForCommunity();
	
	
	/**
	 * this returns the sum of all interactions of the community , in other words the whole AUP platform
	 * @param week
	 * 			number of the week
	 * @param profile
	 * 			type of profile
	 * @return sum of interactions
	 * 	
	 */
	public int getSumForCommunity(int week , int profile);
	
	
	/**
	 * add a result , or a sum of interactions for the community into the mysql , Total_info table , result 
	 * corresponding to a particular week and profile
	 * @param week
	 * 			number of week
	 * @param profile
	 * 			type of profile
	 * @param sum 
	 * 			total number of interactions for the community
	 */
	public void addSumforWeekAndProfile(int week,int profile,int sum);
	
	
	/**
	 * this functions adds the total number of interactions of the community for each available profile
	 * @param week
	 */
	public void addSumsToTotalInfo(int week);
	
	
	/**
	 * this is the function that is called by the engine when updating; it actually updates the total_info table with the 
	 * latest information
	 */
	public void addInfoForCommunityProfile();

	
	/**
	 * creates and returns an XY series for the whole community and for a particular given profile
	 * @param profile
	 * 			type of profile
	 * @param profile_name
	 * 			name of profile
	 * @return
	 */
	public XYSeries createSeriesForCommunityProfile( int profile, String profile_name);
	
	
	/**
	 * returns the current week
	 * @return
	 */
	public int getCurrentWeek();

	
	/**
	 * returns the number of interactions from a user for a  particular profile and a week
	 * @param week
	 * 			number of week
	 * @param profile
	 * 			type of profile	
	 * @param userId
	 * 			user id=facebook id
	 * @return
	 */
	public int getNumberOfInteractionForWeek(int week, int profile , String userId);

	
	
}
