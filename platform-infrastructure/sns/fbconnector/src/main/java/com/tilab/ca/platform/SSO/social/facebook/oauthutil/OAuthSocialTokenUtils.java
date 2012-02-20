package com.tilab.ca.platform.SSO.social.facebook.oauthutil;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Properties;
import java.util.UUID;

import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.apache.log4j.Logger;

/**
 * @author Administrator
 * 
 *         Social Token Database read/write utilities
 * 
 */
public class OAuthSocialTokenUtils {

	/**
	 * Logger
	 */
	private static final Logger log = Logger.getLogger(OAuthSocialTokenUtils.class);

	Properties properties;
	DataSource dataSource;

	private String appId = "societeis";

	// private UserProfileHome userProfileHomeUtils = null;
	// private UserProfile userProfileUtils = null;

	private static String facebookCode 		= "FB";
	private static String twitterCode 		= "TW";
	private static String misoCode 			= "MI";
	private static String foursquareCode = "FS";
	private static String gowallaCode = "GW";
	private static String flickrCode = "FL";
	private static String youtubeCode = "YT";

	private static String facebookString = "facebook";
	private static String twitterString = "twitter";
	private static String misoString = "miso";
	private static String foursquareString = "foursquare";
	private static String gowallaString = "gowalla";
	private static String flickrString = "flickr";
	private static String youtubeString = "youtube";

	/**
	 * 
	 */
	public OAuthSocialTokenUtils() {

		log.debug("New OAuthSocialTokenUtils");
		try {

		
			// creo il contesto
			javax.naming.Context initialContext = new InitialContext();
		
			/*
			 * Object ref =
			 * initialContext.lookup("java:/comp/env/ejb/UserProfile");
			 * userProfileHomeUtils = (UserProfileHome)
			 * PortableRemoteObject.narrow( ref, UserProfileHome.class);
			 * userProfileUtils = userProfileHomeUtils.create();
			 */

		} catch (Exception e) {
			log.error("Exception msg:" + e.getMessage());
		}
	}

	private static String makeRandomString() {
		return UUID.randomUUID().toString().replaceAll("-", "");

	}

	/**
	 * @param username
	 * @param provider
	 * @param appId
	 * @param expires
	 * @param access_token
	 * @return
	 * @throws Exception
	 */
	public String addOAuth2SocialToken(int userid, String username,
			String provider, String appId, int expires, String access_token,
			String refreshToken) throws Exception {

		Connection con = null;
		Statement st = null;
		int expiresTimestamp = -1;
		String oldToken = null;

		if (appId != null && username != null && provider != null
				&& access_token != null) {

			try {

				log.debug("adding access_token = " + access_token);
				log.debug("userid = " + userid);
				log.debug("username = " + username);
				log.debug("provider = " + provider);
				log.debug("AppId = " + appId);
				log.debug("expires = " + expires);

				provider = provider.toLowerCase();

				if (expires != -1) {
					java.util.Date date = new java.util.Date();
					expiresTimestamp = (int) (expires * 1000 + date.getTime());
					log.debug("expires Timestamp =" + expiresTimestamp);
				}

				oldToken = getSocialTokenByUserIdProvAndAppId(userid, provider,
						appId);
				if (oldToken == null) {
					log.debug("New social token for:" + userid + "-" + provider
							+ "-" + appId);
					String connts = getConnTs();
					con = dataSource.getConnection();
					st = con.createStatement();
					st.executeUpdate("INSERT INTO socialtoken (USERID,SOCIALUSERNAME,PROVIDER,APPID,TOKEN,EXPIRES,CONNTS,REFRESHTOKEN) "
							+ "VALUES ('"
							+ userid
							+ "','"
							+ username
							+ "','"
							+ provider
							+ "','"
							+ appId
							+ "','"
							+ access_token
							+ "','"
							+ expires
							+ "','"
							+ connts
							+ "','"
							+ refreshToken + "');");
				} else {
					log.debug("Update social token for:" + userid + "-"
							+ provider + "-" + appId);
					con = dataSource.getConnection();
					st = con.createStatement();
					st.executeUpdate("UPDATE socialtoken SET TOKEN='"
							+ access_token + "',EXPIRES='" + expires
							+ "',APPID='" + appId + "' WHERE SOCIALUSERNAME='"
							+ username + "' AND PROVIDER='" + provider
							+ "' AND APPID='" + appId + "';");
				}

				con.close();

			} catch (Exception e) {
				// System.out.println("################# ERROR ###############");
				log.error("Exception msg:" + e.getMessage());
				access_token = null;
				throw e;
			}

		} else
			log.error("Error adding NEW access_token = " + access_token);

		return access_token;

	}

	/**
	 * @param username
	 * @param provider
	 * @param appId
	 * @param expires
	 * @param token
	 * @param token_secret
	 * @return
	 * @throws Exception
	 */
	public String addOAuth1SocialToken(int userid, String username,
			String provider, String appId, int expires, String token,
			String token_secret) throws Exception {

		Connection con = null;
		Statement st = null;
		int expiresTimestamp = -1;
		String oldToken = null;

		if (appId != null && username != null && provider != null
				&& token != null && token_secret != null) {

			try {

				log.debug("adding token = " + token);
				log.debug("token secret = " + token_secret);
				log.debug("userid = " + userid);
				log.debug("username = " + username);
				log.debug("provider = " + provider);
				log.debug("appId = " + appId);
				log.debug("expires = " + expires);

				provider = provider.toLowerCase();

				if (expires != -1) {
					java.util.Date date = new java.util.Date();
					expiresTimestamp = (int) (expires * 1000 + date.getTime());
					log.debug("expires Timestamp =" + expiresTimestamp);
				}

				oldToken = getSocialTokenByUserIdProvAndAppId(userid, provider,
						appId);
				if (oldToken == null) {
					log.debug("New social token for:" + userid + "-" + provider
							+ "-" + appId);
					String connts = getConnTs();
					con = dataSource.getConnection();
					st = con.createStatement();
					st.executeUpdate("INSERT INTO socialtoken (USERID,SOCIALUSERNAME,PROVIDER,APPID,TOKEN,TOKENSECRET,EXPIRES,CONNTS) "
							+ "VALUES ('"
							+ userid
							+ "','"
							+ username
							+ "','"
							+ provider
							+ "','"
							+ appId
							+ "','"
							+ token
							+ "','"
							+ token_secret
							+ "','"
							+ expires
							+ "','"
							+ connts
							+ "');");
				} else {
					log.debug("Update social token for:" + userid + "-"
							+ provider + "-" + appId);
					con = dataSource.getConnection();
					st = con.createStatement();
					st.executeUpdate("UPDATE socialtoken SET TOKEN='" + token
							+ "',TOKENSECRET='" + token_secret + "',EXPIRES='"
							+ expires + "',APPID='" + appId
							+ "' WHERE SOCIALUSERNAME='" + username
							+ "' AND PROVIDER='" + provider + "' AND APPID='"
							+ appId + "';");
				}

				con.close();

			} catch (Exception e) {
				// System.out.println("################# ERROR ###############");
				log.error("Exception msg:" + e.getMessage());
				token = null;
				throw e;
			}

		} else
			log.error("Error adding NEW access_token = " + token);

		return token;

	}

	/**
	 * @param username
	 * @param provider
	 * @return
	 * @throws Exception
	 */
	public String getSocialTokenSecretByUserIdProvAndAppId(int userid,
			String provider, String appId) throws Exception {

		// username = social username
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		String ret = null;

		try {
			provider = provider.toLowerCase();
			// creo la connessione
			con = dataSource.getConnection();
			st = con.createStatement();
			rs = st.executeQuery("SELECT TOKENSECRET FROM socialtoken WHERE USERID='"
					+ userid
					+ "' AND PROVIDER='"
					+ provider
					+ "'AND APPID='"
					+ appId + "';");
			while (rs.next()) {
				ret = rs.getString(1);
				log.debug("Already present social token =" + ret);
			}
			con.close();
		} catch (Exception e) {
			// System.out.println("################# ERROR ###############");
			log.error("Exception msg:" + e.getMessage());
		}

		return ret;
	}

	/**
	 * @param userid
	 * @param provider
	 * @return
	 * @throws Exception
	 */
	public String getSocialTokenByUserIdAndProv(int userid, String provider)
			throws Exception {

		// userid = AUP user

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		String ret = null;

		try {

			provider = provider.toLowerCase();
			// creo la connessione
			con = dataSource.getConnection();
			st = con.createStatement();
			rs = st.executeQuery("SELECT TOKEN FROM socialtoken WHERE USERID='"
					+ userid + "' AND PROVIDER='" + provider + "';");
			while (rs.next()) {
				ret = rs.getString(1);
				log.debug("Already present token =" + ret);
			}
			con.close();
		} catch (Exception e) {
			// System.out.println("################# ERROR ###############");
			log.error("Exception msg:" + e.getMessage());
		}

		return ret;
	}

	/**
	 * @param userid
	 * @param provider
	 * @return
	 * @throws Exception
	 */
	public String getSocialTokenByUserIdProvAndAppId(int userid,
			String provider, String appId) throws Exception {

		// userid = AUP user
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		String ret = null;

		try {
			provider = provider.toLowerCase();
			// creo la connessione
			con = dataSource.getConnection();
			st = con.createStatement();
			rs = st.executeQuery("SELECT TOKEN FROM socialtoken WHERE USERID='"
					+ userid + "' AND PROVIDER='" + provider + "' AND APPID='"
					+ appId + "';");
			while (rs.next()) {
				ret = rs.getString(1);
				log.debug("Already present token =" + ret);
			}
			con.close();
		} catch (Exception e) {
			// System.out.println("################# ERROR ###############");
			log.error("Exception msg:" + e.getMessage());
		}

		return ret;
	}

	/**
	 * @param username
	 * @param provider
	 * @return
	 * @throws Exception
	 */
	public String getSocialTokenByUsernameProvAndAppId(String username,
			String provider, String app) throws Exception {

		// userid = AUP user
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		String ret = null;
		String userid = null;

		try {

			log.debug("username=" + username);
			log.debug("provider=" + provider);
			log.debug("app=" + app);

			if (username == null || provider == null || app == null)
				return ret;
			else {

				con = dataSource.getConnection();
				st = con.createStatement();

				rs = st.executeQuery("SELECT user.ID FROM user "
						+ "WHERE user.USERNAME='" + username + "'");
				while (rs.next()) {
					userid = rs.getString(1);
					log.debug(" Found userid =" + userid);
				}
				con.close();

				if (userid == null)
					return ret;
				else {

					provider = provider.toLowerCase();
					// creo la connessione
					con = dataSource.getConnection();
					st = con.createStatement();
					rs = st.executeQuery("SELECT TOKEN FROM socialtoken WHERE USERID='"
							+ userid
							+ "' AND PROVIDER='"
							+ provider
							+ "' AND APPID='" + app + "';");
					while (rs.next()) {
						ret = rs.getString(1);
						log.debug("Already present token =" + ret);
					}
					con.close();
				}

			}

		} catch (Exception e) {
			// System.out.println("################# ERROR ###############");
			log.error("Exception msg:" + e.getMessage());
		}

		return ret;
	}

	/**
	 * @param username
	 * @param provider
	 * @return
	 * @throws Exception
	 */
	public String getSocialTokenSecretByUsernameProvAndAppId(String username,
			String provider, String app) throws Exception {

		// userid = AUP user
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		String ret = null;
		String userid = null;

		try {

			log.debug("username=" + username);
			log.debug("provider=" + provider);
			log.debug("app=" + app);

			if (username == null || provider == null || app == null)
				return ret;
			else {

				con = dataSource.getConnection();
				st = con.createStatement();

				rs = st.executeQuery("SELECT user.ID FROM user "
						+ "WHERE user.USERNAME='" + username + "'");
				while (rs.next()) {
					userid = rs.getString(1);
					log.debug(" Found userid =" + userid);
				}
				con.close();

				if (userid == null)
					return ret;
				else {

					provider = provider.toLowerCase();
					// creo la connessione
					con = dataSource.getConnection();
					st = con.createStatement();
					rs = st.executeQuery("SELECT TOKENSECRET FROM socialtoken WHERE USERID='"
							+ userid
							+ "' AND PROVIDER='"
							+ provider
							+ "' AND APPID='" + app + "';");
					while (rs.next()) {
						ret = rs.getString(1);
						log.debug("Already present token secret =" + ret);
					}
					con.close();
				}

			}

		} catch (Exception e) {
			// System.out.println("################# ERROR ###############");
			log.error("Exception msg:" + e.getMessage());
		}

		return ret;
	}

	/**
	 * @param userid
	 * @param provider
	 * @return
	 * @throws Exception
	 */
	public String getSocialUsernameByUserIdProvAndAppId(int userid,
			String provider, String appId) throws Exception {

		// userid = AUP user
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		String ret = null;

		try {
			provider = provider.toLowerCase();
			// creo la connessione
			con = dataSource.getConnection();
			st = con.createStatement();
			rs = st.executeQuery("SELECT SOCIALUSERNAME FROM socialtoken WHERE USERID='"
					+ userid
					+ "' AND PROVIDER='"
					+ provider
					+ "' AND APPID='"
					+ appId + "';");
			while (rs.next()) {
				ret = rs.getString(1);
				log.debug("Already present social username =" + ret);
			}
			con.close();
		} catch (Exception e) {
			// System.out.println("################# ERROR ###############");
			log.error("Exception msg:" + e.getMessage());
		}

		return ret;
	}

	/**
	 * @param userid
	 * @param provider
	 * @return
	 * @throws Exception
	 */
	public String getSocialUserByUserIdAndProv(int userid, String provider)
			throws Exception {

		// userid = AUP user
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		String ret = null;

		try {
			provider = provider.toLowerCase();
			// creo la connessione
			con = dataSource.getConnection();
			st = con.createStatement();
			rs = st.executeQuery("SELECT SOCIALUSERNAME FROM socialtoken WHERE USERID='"
					+ userid + "' AND PROVIDER='" + provider + "';");
			while (rs.next()) {
				ret = rs.getString(1);
				log.debug("Already present social user =" + ret);
			}
			con.close();
		} catch (Exception e) {
			// System.out.println("################# ERROR ###############");
			log.error("Exception msg:" + e.getMessage());
		}

		return ret;
	}

	/**
	 * @param userid
	 * @param provider
	 * @param appId
	 * @return
	 * @throws Exception
	 */
	public String getSocialUserByUserIdProvAndAppId(int userid,
			String provider, String appId) throws Exception {

		// userid = AUP user

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		String ret = null;

		try {

			provider = provider.toLowerCase();
			// creo la connessione
			con = dataSource.getConnection();
			st = con.createStatement();
			rs = st.executeQuery("SELECT SOCIALUSERNAME FROM socialtoken WHERE USERID='"
					+ userid
					+ "' AND PROVIDER='"
					+ provider
					+ "' AND APPID='"
					+ appId + "';");
			while (rs.next()) {
				ret = rs.getString(1);
				log.debug("Already present social user =" + ret);
			}
			con.close();
		} catch (Exception e) {
			// System.out.println("################# ERROR ###############");
			log.error("Exception msg:" + e.getMessage());
		}

		return ret;
	}

	/**
	 * @param userid
	 * @param provider
	 * @return
	 * @throws Exception
	 */
	public int deleteSocialTokenByUserId(int userid) throws Exception {

		// userid = AUP user
		log.debug("deleteSocialTokenByUserId =" + userid);
		Connection con = null;
		Statement st = null;

		int ret = -1;
		try {
			// creo la connessione
			con = dataSource.getConnection();
			st = con.createStatement();
			ret = st.executeUpdate("DELETE FROM socialtoken WHERE USERID='"
					+ userid + "';");

			con.close();
		} catch (Exception e) {
			// System.out.println("################# ERROR ###############");
			log.error("Exception msg:" + e.getMessage());
		}

		return ret;
	}

	/**
	 * @param username
	 * @param provider
	 * @return
	 * @throws Exception
	 */
	public int deleteSocialTokenByUserIdAndProv(int userid, String provider)
			throws Exception {

		Connection con = null;
		Statement st = null;
		int rs = -1;
		String ret = null;

		try {

			provider = provider.toLowerCase();
			// creo la connessione
			con = dataSource.getConnection();
			st = con.createStatement();
			rs = st.executeUpdate("DELETE FROM socialtoken WHERE USERID='"
					+ userid + "' AND PROVIDER='" + provider + "';");

			con.close();
		} catch (Exception e) {
			// System.out.println("################# ERROR ###############");
			log.error("Exception msg:" + e.getMessage());
		}

		return rs;
	}

	/**
	 * @param username
	 * @param provider
	 * @param appId
	 * @return
	 * @throws Exception
	 */
	public int deleteSocialTokenByUserIdProvAndAppId(int userid,
			String provider, String appId) throws Exception {

		Connection con = null;
		Statement st = null;
		int rs = -1;
		String ret = null;

		try {
			provider = provider.toLowerCase();
			// creo la connessione
			con = dataSource.getConnection();
			st = con.createStatement();
			rs = st.executeUpdate("DELETE FROM socialtoken WHERE USERID='"
					+ userid + "' AND PROVIDER='" + provider + "' AND APPID='"
					+ appId + "';");

			con.close();
		} catch (Exception e) {
			// System.out.println("################# ERROR ###############");
			log.error("Exception msg:" + e.getMessage());
		}

		return rs;
	}

	/**
	 * @param userid
	 * @param appId
	 * @return
	 */
	public String getSConnect(int userid, String appId) {
		log.debug("Enter getSConnect");
		String sConnect = "";

		// FACEBOOK
		String fbToken = null;
		try {
			fbToken = this.getSocialTokenByUserIdProvAndAppId(userid,
					"facebook", appId);
		} catch (Exception e) {
			// TODO Auto-generated catch block

		}
		if (fbToken != null)
			sConnect = sConnect + facebookCode + ",";

		// TWITTER
		String twToken = null;
		String twTokenSecret = null;
		try {
			twToken = this.getSocialTokenByUserIdProvAndAppId(userid,
					"twitter", appId);
			twTokenSecret = this.getSocialTokenSecretByUserIdProvAndAppId(
					userid, "twitter", appId);
		} catch (Exception e) {
			// TODO Auto-generated catch block

		}
		if (twToken != null && twTokenSecret != null)
			sConnect = sConnect + twitterCode + ",";

		// MISO
		String miToken = null;
		String miTokenSecret = null;
		try {
			miToken = this.getSocialTokenByUserIdProvAndAppId(userid, "miso",
					appId);
			miTokenSecret = this.getSocialTokenSecretByUserIdProvAndAppId(
					userid, "miso", appId);
		} catch (Exception e) {
			// TODO Auto-generated catch block

		}
		if (miToken != null && miTokenSecret != null)
			sConnect = sConnect + misoCode + ",";

		String fsToken = null;
		try {
			fsToken = this.getSocialTokenByUserIdProvAndAppId(userid,
					"foursquare", appId);
		} catch (Exception e) {

		}
		if (fsToken != null)
			sConnect = sConnect + foursquareCode + "," ;

		// FOUSQUARE
		/*
		 * String fsToken=null; try { fsToken =
		 * this.getSocialTokenByUserIdProvAndClientId(userid, "foursquare",
		 * clientId); } catch (Exception e) { // TODO Auto-generated catch block
		 * 
		 * } if(fsToken!=null) sConnect = sConnect+","+foursquareCode;
		 * 
		 * //GOWALLA String gwToken=null; try { gwToken =
		 * this.getSocialTokenByUserIdProvAndClientId(userid, "gowalla",
		 * clientId); } catch (Exception e) { // TODO Auto-generated catch block
		 * 
		 * } if(gwToken!=null) sConnect = sConnect+","+gowallaCode;
		 * 
		 * //YOUTUBE String ytToken=null; String ytTokenSecret=null; try {
		 * ytToken = this.getSocialTokenByUserIdProvAndClientId(userid,
		 * "youtube", clientId); ytTokenSecret =
		 * this.getSocialTokenSecretByUserIdProvAndClientId(userid, "youtube",
		 * clientId); } catch (Exception e) { // TODO Auto-generated catch block
		 * 
		 * } if(ytToken!=null&&ytTokenSecret!=null) sConnect =
		 * sConnect+","+youtubeCode;
		 * 
		 * //FLICKR String flToken=null; try { flToken =
		 * this.getSocialTokenByUserIdProvAndClientId(userid, "flickr",
		 * clientId); } catch (Exception e) { // TODO Auto-generated catch block
		 * 
		 * } if(flToken!=null) sConnect = sConnect+","+flickrCode;
		 */

		if (sConnect != null && sConnect.length() > 1)
			sConnect = sConnect.substring(0, sConnect.length() - 1);

		log.debug("sConnect=" + sConnect);

		return sConnect;

	}

	/**
	 * @param clientId
	 * @return
	 */
	public String getAppIdByClientId(String clientId) {
		log.debug("Enter getAppIdByClientId:" + clientId);

		if (clientId == null)
			return "aup";

		int i = clientId.indexOf("-");
		if (i != -1)
			clientId = clientId.substring(0, i);
		log.debug("normalized clientId=" + clientId);

		
		log.debug("mapped appId = " + appId );

		if (appId == null)
			return "aup";

		return appId;
	}

	/**
	 * @param providerCode
	 * @return
	 */
	public String getProviderStringByProviderCode(String providerCode) {

		if (providerCode.equals(facebookCode))
			return facebookString;
		else if (providerCode.equals(twitterCode))
			return twitterString;
		else if (providerCode.equals(misoCode))
			return misoString;
		else if (providerCode.equals(gowallaCode))
			return gowallaString;
		else if (providerCode.equals(foursquareCode))
			return foursquareString;
		else if (providerCode.equals(youtubeCode))
			return youtubeString;
		else if (providerCode.equals(flickrCode))
			return flickrString;
		else
			return providerCode;

	}

	/**
	 * @param clientId
	 * @return
	 */
	public String getNormalizedClientId(String clientId) {
		log.debug("Enter getNormalizedClientId:" + clientId);

		int i = clientId.indexOf("-");
		if (i != -1)
			clientId = clientId.substring(0, i);
		log.debug("normalized clientId=" + clientId);

		return clientId;
	}

	/**
	 * @return
	 */
	private String getConnTs() {
		// 2011-09-06 START Add Connection Time
		// create a java calendar instance
		Calendar calendar = Calendar.getInstance();
		// get a java.util.Date from the calendar instance.
		// this date will represent the current instant, or "now".
		java.util.Date now = null;
		java.sql.Timestamp connTime = null;
		String connTs = null;
		SimpleDateFormat formatter = null;
		if (calendar != null)
			now = calendar.getTime();
		// a java current time (now) instance
		if (now != null)
			connTime = new java.sql.Timestamp(now.getTime());
		if (connTime != null) {
			formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			// regTs = String.format("%1$TD %1$TT", regTime);
			connTs = formatter.format(connTime);
		}
		// 2011-09-06 STOP Add Connection Time

		return connTs;
	}

}
