/**
 * 
 */
package org.societies.personalisation.socialprofiler.service;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Properties;
import java.util.TimeZone;

import org.jfree.data.xy.XYSeries;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.personalisation.socialprofiler.Variables;



public class DatabaseConnection implements Variables {
	private static final Logger logger = LoggerFactory.getLogger(DatabaseConnection.class);
	private Connection connection;
	private Properties 	props; 			
	
	private static final String URL			= "db.url";
	private static final String DBNAME 		= "db.name";
	private static final String DRIVER 		= "db.driver";
	private static final String USERNAME 	= "db.username";
	private static final String PWD		 	= "db.password";
	
	
	/**
	 * DatabaseConnectionImpl
	 */
	public DatabaseConnection(Properties properties) {
		this.props = properties;
	}
	
	/**
	 * returns a java.sql.Connection , the actual connection to the mysql database
	 * @return
	 */
	public final Connection getConnection() {
		return connection;
	}
	
	
	public boolean connectMysql(){
		java.util.TimeZone.setDefault(TimeZone.getTimeZone("GMT")); 
		try {
		
			  String url 	= props.getProperty(URL); //"jdbc:mysql://localhost:3306/";
			  String dbName = props.getProperty(DBNAME); //"social";
			  //String driver = props.getProperty(DRIVER); //"com.mysql.jdbc.Driver";
			  String userName = props.getProperty(USERNAME); //"root"; 
			  String password = props.getProperty(PWD); //"";
			  connection = DriverManager.getConnection(url+dbName,userName,password);
		}
		catch (SQLException e) {
			logger.error("Connection to Mysql database was unsuccesful.");
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	
	public void closeMysql(){
		try {
			connection.close();
		} catch (SQLException e) {
			logger.error("Cannot close connection to Mysql database ;  "+ e.getMessage());
		}	
	}
	
	
	public void addUserToDatabase(String current_id,String ca_Name){
		try {
			logger.debug("adding user "+current_id+" : "+ca_Name+" to database");
			Statement st = connection.createStatement();
			st.execute("INSERT into users (facebook_id,ca_id) values ("+current_id +",'"+ca_Name+"') " +
					"on duplicate key update ca_id='"+ca_Name+"';");
		}catch (SQLException e) {
			logger.error("Error while inserting user "+current_id+" : "+ca_Name+" to Mysql: "+ e.getMessage());
		}
	}
	
	
	public void deleteUserFromDatabase(String current_id){
		try {
			logger.debug("deleting user "+current_id+" from database");
			Statement st = connection.createStatement();
			st.execute("Delete from users where facebook_id ="+current_id +";");
		}catch (SQLException e) {
			logger.error("error while deleting user "+current_id+" from Mysql: "+e.getMessage());
		}
	}
		
	
	public void sendMomentToDatabase(String userId,String lastTime, int number,int option){
		
		int week_number;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
		try {
			long long_lastTime=sdf.parse(lastTime).getTime();
			week_number = calculateWeek(lastTime);
			//logger.debug("adding moment for user "+userId+" profile "+option+"  week "+week_number+" to database");
			Statement st = connection.createStatement();
			logger.debug("insert into info (week ,starts , profile , last_time , number_actions ,user_id) values " +
					"( "+week_number+","+option+","+long_lastTime+","+number+","+userId+") " +
					"on duplicate key update last_time="+long_lastTime+" , number_actions="+number+" ;");
			st.execute("insert into info (week ,starts, profile , last_time ,last_time_timestamp, number_actions ,user_id) " +
					"values " +	"( "+week_number+", '"+getMysqlTimeStampForWeek(week_number)+"',"+option+","+long_lastTime+
					",'"+getMysqlTimeStamp(lastTime)+"',"+number+","+userId+") " +
					"on duplicate key update starts='"+getMysqlTimeStampForWeek(week_number)+"' ,last_time="+long_lastTime+" ,last_time_timestamp='"+getMysqlTimeStamp(lastTime)+
					"' ,  number_actions="+number+" ;");
			
		}catch (SQLException e) {
			logger.debug("error while adding moment for user "+userId+" to Mysql"+e);
			e.printStackTrace();
		}catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	
	public int calculateWeek(String lastTime) throws ParseException{
		//1249084800=1 aug 2009 00:00:00 GMT
		long date_start=0L;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
		long date_lastTime=sdf.parse(lastTime).getTime();
		long difference= date_lastTime-date_start;
		//1 week=7 x 24 x 60 x 60=604800
		long week_time=604800000;
		int week_number=(int) (difference/week_time);
		//logger.debug("date last time "+date_lastTime+" difference "+difference+" week "+week_number);
		return week_number;
	}
	
	 	
	public String getMysqlTimeStamp(String time) throws ParseException{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		df.setTimeZone(TimeZone.getTimeZone("GMT"));
		return df.format(sdf.parse(time));
	}
	
		
	public String getMysqlTimeStampForWeek(int week){
		long date_start=0L;
		long week_time=604800000L;
		long date =date_start+week*week_time;
		Date time = new Date(date);
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		df.setTimeZone(TimeZone.getTimeZone("GMT"));
		String mysql_time = df.format(time);	
		return mysql_time;
	}
	
	
	public int getNumberOfActionInPast(int week, int profile , String user_id){
		int number_actions=0;
		try {
			Statement st = connection.createStatement();
			boolean continue_quering=true;
			while (continue_quering==true&&week>=0){
				
				ResultSet result=st.executeQuery("select number_actions from info where week="+week+" " +
						"and profile="+profile+" and user_id="+user_id+" ;");
				if (result.next()){
					String number=result.getString("number_actions");
					if (number!=null){
						number_actions=Integer.parseInt(number);
						continue_quering=false;	
						//logger.debug("number is"+number);
					}
				}
				week--;
			}
		}catch (SQLException e) {
			logger.debug("error while retrieving number_actions for user "+user_id+" " +
					"profile="+profile+" week="+week+" from Mysql"+e);
			e.printStackTrace();
		}
		return number_actions;
	}
	
	
	public XYSeries createSeries(String userId , int profile,String caName, String profile_name,int legend_option){
		XYSeries series = null;
		if (legend_option==USER_DIMENSION){
			series=new XYSeries(profile_name+" Profile ");
		}else {     //PROFILE_DIMENSION
			series=new XYSeries("User "+caName+"("+userId+")");
		}
		java.util.Date today = new java.util.Date();//current date
	    java.sql.Timestamp timestamp=new java.sql.Timestamp(today.getTime());
	    long current_time = timestamp.getTime()/1000;
	    try {
		    int current_week=calculateWeek(String.valueOf(current_time));
		    int week=0,aux=0,aux_number=0; 
		    if (caName.equals("user_not_found_on_CA")){
		    	logger.error("error while trying to generate profile evolution for user "+
		    			userId+" reason: user was not found on CA platform");
		    	return series;
		    }
	    	Statement st = connection.createStatement();
	    	while ((week)<=current_week){   //adding procedure
	    		ResultSet result=st.executeQuery("select number_actions from info where week="+week+" " +
						"and profile="+profile+" and user_id="+userId+" ;");
				if (result.next()){
					String number=result.getString("number_actions");
					if (number!=null){
						int number_actions=Integer.parseInt(number);
						if (week-aux>1){                 //there is at least an empty week between
							for (int j=aux+1;j<week;j++){
								series.add(j-current_week,0);
								//logger.debug("XY point week "+j+" number 0");
							}
						}
						int delta_actions = number_actions-aux_number;
						series.add(week-current_week,delta_actions);
//						series.add(week-current_week,number_actions);
						logger.warn("[lukostaz] added number_actions=" + number_actions + " - aux_number=" + aux_number + "=number_actions= " + delta_actions); 
						//logger.debug("XY point week "+(week-current_week)+" number "+(number_actions-aux_number)+" aux "+aux);
						aux=week;
						aux_number=number_actions;
					}
				}
				week++;
	    	}
	    	//series.add(current_week, 0);
	    }catch (Exception e) {
			logger.debug("error (create_Series) while retrieving number_actions for user "+userId+" " +
					"profile="+profile+" from Mysql"+e);
			e.printStackTrace();
		}
	    return series;
	}
	
	
	/************************************************************************************/
	/***********************************Group********************************************/
	/************************************************************************************/
	
	
	public int getSumForGroup(ArrayList<String> valid_group ,int week , int profile){
		int sum=0;
		for(int i=0;i<valid_group.size();i++){
			sum+=getNumberOfActionInPast(week, profile, valid_group.get(i));
		}
		return sum;
	}
	
	
	public XYSeries createSeriesForGroupProfile( ArrayList<String>group ,int profile, String profile_name){
		XYSeries series = new XYSeries(profile_name+" Profile ");
		
		//current week
		java.util.Date today = new java.util.Date();
	    java.sql.Timestamp timestamp=new java.sql.Timestamp(today.getTime());
	    long current_time = timestamp.getTime()/1000;
	    int current_week;
		try {
			current_week = calculateWeek(String.valueOf(current_time));
		
			//add points to series
			int aux=getSumForGroup(group, 0, profile);
			series.add(0-current_week,aux);
			for (int j=1;j<=current_week;j++){
				int value=getSumForGroup(group, j, profile);
				series.add(j-current_week,value-aux);
				aux=value;
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	    return series;
	}
	
	
	/************************************************************************************/
	/***********************************Community****************************************/
	/************************************************************************************/
	
	
	public int getLastWeekForCommunity(){
		int lastWeek=0;
		try {
			Statement st = connection.createStatement();
			ResultSet result=st.executeQuery("select week from total_info order by week desc;");
			if (result.next()){
				String number=result.getString("week");
				if (number!=null){
						lastWeek=Integer.parseInt(number);
						logger.debug("number is "+lastWeek);
				}
			}
				
			
		}catch (SQLException e) {
			logger.debug("error while selecting last week from total info. Reason: "+e);
			e.printStackTrace();
		}
		return lastWeek;
	}
	
	
	public int getSumForCommunity(int week , int profile){
		int sum=0;
		try {
			Statement st = connection.createStatement();
			ResultSet result=st.executeQuery("select facebook_id from users ;");
			while (result.next()){
				String userId=result.getString("facebook_id");
				if (userId!=null){
					//logger.debug("userId is"+userId);
					sum+=getNumberOfActionInPast(week, profile, userId);
				}
			}
		}catch (SQLException e) {
			logger.debug("error while sum of number_actions for "+
					"profile="+profile+"and  week="+week+" from Mysql"+e);
			e.printStackTrace();
		}
		return sum;
	}
	
	
	public void addSumforWeekAndProfile(int week,int profile,int sum){
		try {
			logger.debug("adding SUM "+sum+" : week "+week+" profile "+profile+" to database");
			Statement st = connection.createStatement();
			st.execute("INSERT into total_info (week,profile,number_actions) values ("+week +","+profile+","+sum+") " +
					"on duplicate key update number_actions="+sum+";");
		}catch (SQLException e) {
			logger.debug("error while adding sum into total info for week "+week+" , profile "+profile+" Reason: "+e);
			e.printStackTrace();
		}
	}
	
	
	public void addSumsToTotalInfo(int week){
//		addSumforWeekAndProfile(week, NARCISSISM_PROFILE, getSumForCommunity(week, NARCISSISM_PROFILE));
//		addSumforWeekAndProfile(week, PHOTO_PROFILE, getSumForCommunity(week, PHOTO_PROFILE));
//		addSumforWeekAndProfile(week, SUPERACTIVE_PROFILE, getSumForCommunity(week, SUPERACTIVE_PROFILE));
//		addSumforWeekAndProfile(week, QUIZ_PROFILE, getSumForCommunity(week, QUIZ_PROFILE));
//		addSumforWeekAndProfile(week, SURF_PROFILE, getSumForCommunity(week, SURF_PROFILE));
	}
		
	
	public void addInfoForCommunityProfile(){
		logger.debug("==== updating global community info");
		java.util.Date today = new java.util.Date();
	    java.sql.Timestamp timestamp=new java.sql.Timestamp(today.getTime()); //FIXME: minor: il fuso orario e' sbagliato.
	    long current_time = timestamp.getTime()/1000;
	    int current_week;
		try {
			current_week = calculateWeek(String.valueOf(current_time));
			logger.debug("current week is "+current_week+" :"+timestamp);//current date and week
			
			int lastWeek=getLastWeekForCommunity(); //last week on total info
			for (int i=lastWeek;i<=current_week;i++){
				addSumsToTotalInfo(i);
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public XYSeries createSeriesForCommunityProfile( int profile, String profile_name){
		XYSeries series = new XYSeries(profile_name+" Profile ");
		//current week
		java.util.Date today = new java.util.Date();
	    java.sql.Timestamp timestamp=new java.sql.Timestamp(today.getTime());
	    long current_time = timestamp.getTime()/1000;
	    int current_week;
		try {
			current_week = calculateWeek(String.valueOf(current_time));
		
			int lastWeek=getLastWeekForCommunity(); //last week on total info
		    int week=0,aux_number=0; 

	    	Statement st = connection.createStatement();
	    	while ((week)<=lastWeek){   //adding procedure
	    		ResultSet result=st.executeQuery("select number_actions from total_info where week="+week+" " +
						"and profile="+profile+";");
				if (result.next()){
					String number=result.getString("number_actions");
					if (number!=null){
						int number_actions=Integer.parseInt(number);
						series.add(week-current_week,number_actions-aux_number);
						//logger.debug("XY point week "+week+" number "+(number_actions-aux_number));
						aux_number=number_actions;
					}
				}
				week++;
	    	}
	    }catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}catch (SQLException e) {
			logger.debug("error (create_XSeries) while retrieving number_actions for " +
					"profile="+profile+" from Mysql"+e);
			e.printStackTrace();
		}
	    return series;
	}
	
	
	
	
	//replace into code with this function - avoid redondancy
	public int getCurrentWeek() throws ParseException{
		java.util.Date today = new java.util.Date();
	    java.sql.Timestamp timestamp=new java.sql.Timestamp(today.getTime());
	    long current_time = timestamp.getTime()/1000;
	    int current_week=calculateWeek(String.valueOf(current_time));
		logger.debug("current week is "+current_week+" :"+timestamp);//current date and week
		return current_week; 
	}
	
	
	
	
	public int getNumberOfInteractionForWeek(int week, int profile , String userId){
		int result=-1;
		result=getNumberOfActionInPast(week, profile, userId)-getNumberOfActionInPast(week-1, profile, userId);
		if (result<0){
			logger.error("somehting is wrong : info from past superior than info from present");
		}
		return result;
	}
	
}
