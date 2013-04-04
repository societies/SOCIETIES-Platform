package org.societies.platfrom.sns.android.socialapp;

public class Constants {
	// APP CONSTANTS
	public final static String DEBUG_TAG = "CASingleSignOn";

	// CA-SSO Library Constants
	public final static String VERSION	= "1.0";
	public final static String SSO_URL  = "https://beta.teamlife.it/SSO/msignup";

	public static final int FB_CODE =  1001;
	public static final int TW_CODE =  1002;
	public static final int FQ_CODE =  1003;
	public static final int LK_CODE =  1004;
	public static final int GP_CODE =  1005;

	/*public static final String FB_URL = "http://dev.lucasimone.eu/fbMobile.php";
	public static final String LK_URL = "http://dev.lucasimone.eu/auth.php";
	public static final String TW_URL = "http://157.159.160.188:8080/examples/servlets/servlet/TwitterLoginServlet";
	public static final String FQ_URL = "http://157.159.160.188:8080/examples/servlets/servlet/FoursquareLoginServlet";	*/
	
	public static final String FB_URL = "facebook";
	public static final String LK_URL = "linkedin";
	public static final String TW_URL = "twitter";
	public static final String FQ_URL = "foursquare";	

	public static final String ACCESS_TOKEN 		= "access_token";
	public static final String TOKEN_EXPIRATION 	= "expires";

	public static final String FROM = "from";

	//FACEBOOK	
	public static final String FB_CLIENT_ID		= "368482799848413";
	public static final String FB_CLIENT_SECRET	= "c1788688a3091638768ed803d6ebdbd0";	
	public static final String FB_CALLBACK_URL	= "http://societies.lucasimone.eu/facebook/callback.php";
	
	/*public static final String FB_CLIENT_ID		= "336830113081980";
	public static final String FB_CLIENT_SECRET	= "116c6acae7733297aaff3e7f3aba478f";	
	public static final String FB_CALLBACK_URL	= "http://127.0.0.1:8080/socialserver/";*/
	public static final String FB_SCOPES		=  "email,friends_about_me,friends_actions.music,friends_actions.news,friends_actions.video,friends_activities,friends_birthday,friends_education_history,friends_events,friends_games_activity,friends_groups,friends_hometown,friends_interests,friends_likes,friends_location,friends_notes,friends_photo_video_tags,friends_photos,friends_questions,friends_relationship_details,friends_relationships,friends_religion_politics,friends_status,friends_subscriptions,friends_videos,friends_website,friends_work_history,user_about_me,user_actions.music,user_actions.news,user_actions.video,user_activities,user_birthday,user_education_history,user_events,user_games_activity,user_groups,user_hometown,user_interests,user_likes,user_location,user_notes,user_photo_video_tags,user_photos,user_questions,user_relationship_details,user_relationships,user_religion_politics,user_status,user_subscriptions,user_videos,user_website,user_work_history";

	//Foursquare
	public static final String FQ_CLIENT_ID 	= "SZKIPIXWCQHOURERE4B5NHO3E2NFW4MRQRPI42B1Q5VLHJ1T";
	public static final String FQ_CLIENT_SECRET 	= "KVTR1YZKQWZL3BBYBE3MMAQRSVFLO11YE1S4JVYGU3QPBB4I";
	public static final String FQ_CALLBACK_URL	= "http://societies.lucasimone.eu/foursquare/callback.php";
	
	/*public static final String FQ_CLIENT_ID 	= "KYRTUENPSCILFGBZE2QVI5WE501GOFK2EY3QHIY5G5C2EZOX";
	public static final String FQ_CLIENT_SECRET = "0FWG0PODXFCWJB2DTFS41VDQYNCOVVXGTDUXEYM3R2CCTIDK";
	public static final String FQ_CALLBACK_URL	= "http://127.0.0.1:8080/socialserver/";*/

	//LINKEDIN
	public static final String LK_CLIENT_ID			= "6cex9yffh2cd";
	public static final String LK_CLIENT_SECRET		= "j1vTQq93i30q63fT";
	public static final String LK_CALLBACK_URL		= "http://societies.lucasimone.eu/linkedin/callback.php";
	public static final String LK_SCOPES			= "r_basicprofile,r_fullprofile,r_emailaddress,r_network,r_contactinfo,rw_nus,rw_groups";

	//Twitter
	public static final String TW_CLIENT_ID 		= "ncgOVLIBj2ezwFdg1ynj2A";
	public static final String TW_CLIENT_SECRET		= "Xtai6DrqtXmpxbgIch5Ut0sV9pyaYQ2rqiuIpSQL268";
	public static final String TW_CALLBACK_URL		= "http://societies.lucasimone.eu/twitter/callback.php";

}
