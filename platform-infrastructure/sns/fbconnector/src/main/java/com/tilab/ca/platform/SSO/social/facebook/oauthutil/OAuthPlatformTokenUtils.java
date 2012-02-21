/*********************************************
  Context Awareness Platform
 ********************************************
  Copyright (c) 2006-2010 Telecom Italia S.p.A
 ******************************************** 
  $Id: LMD5Gen.java 6618 2009-12-16 18:47:42Z papurello $
  $HeadURL: http://163.162.93.163:90/svn/ca/platform/aup/branches/rel-1_3-ev/src/main/java/com/tilab/ca/platform/aup/util/utils/LMD5Gen.java $
 *********************************************
 */
package com.tilab.ca.platform.SSO.social.facebook.oauthutil;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;
import java.util.UUID;

import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.apache.log4j.Logger;


/**
 * @author Administrator
 * Manage Single Sign On / OAuth for the platform
 *
 */
public class OAuthPlatformTokenUtils {

	/**
	 * Logger
	 */
	private static final Logger log = Logger.getLogger(OAuthSocialTokenUtils.class);

	Properties properties;
	DataSource dataSource;
	String ssoOauthTokenExpires = "315360000";
	String ssoOauthTokenType 	= "cap";

	/**
	 * 
	 */
	public OAuthPlatformTokenUtils() {

		log.debug("New OAuthPlatformTokenUtils");
		try {

			// creo il contesto
			javax.naming.Context initialContext = new InitialContext();
			log.debug("sso Oauth Token Expires (sec) = " + ssoOauthTokenExpires);
			log.debug("sso Oauth Token Type = " + ssoOauthTokenType);

		} catch (Exception e) {
			log.error("Exception msg:" + e.getMessage());
		}
	}

	private static String makeRandomString() {
		return UUID.randomUUID().toString().replaceAll("-", "");

	}

	/**
	 * @param clientId
	 * @param username
	 * @param deviceid
	 * @return
	 * @throws Exception
	 */
	public String addConsumerKey(String clientId, String username, String deviceid)
			throws Exception {

		Connection con = null;
		Statement st = null;
		// ResultSet rs=null;
		String consumerKey = null;

		if(clientId!=null && username!=null && deviceid!=null)
		{
			try {

				String appId = null;
				OAuthSocialTokenUtils oAuthSocialTokenUtils = new OAuthSocialTokenUtils();
				appId = oAuthSocialTokenUtils.getAppIdByClientId(clientId);
				
				consumerKey = clientId + "-" + username + "-" + deviceid;
				log.debug("adding consumerkey = " + consumerKey);

				if (getConsumerKey(consumerKey) != null)
					return consumerKey;

				con = dataSource.getConnection();
				st = con.createStatement();
				st.executeUpdate("INSERT INTO consumer (CONSUMERKEY,USER,APP) "
						+ "VALUES ('" + consumerKey + "','" + username + "','"
						+ appId + "');");

				con.close();
				
			} catch (Exception e) {
				// System.out.println("################# ERROR ###############");
				log.error("Exception msg:" + e.getMessage());
				throw e;
			}
			
		}
		else
			log.error("Error adding NEW consumerkey = " + consumerKey);

		return consumerKey;

	}

	/**
	 * @param consumerKey
	 * @return
	 * @throws Exception
	 */
	public String getConsumerKey(String consumerKey) throws Exception {

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		String ret = null;

		try {
			// creo la connessione
			con = dataSource.getConnection();
			st = con.createStatement();
			rs = st.executeQuery("SELECT CONSUMERKEY FROM consumer WHERE CONSUMERKEY='"
					+ consumerKey + "';");
			while (rs.next()) {
				ret = rs.getString(1);
				log.debug("Already present consumerKey =" + consumerKey);
			}
			con.close();
		} catch (Exception e) {
			// System.out.println("################# ERROR ###############");
			log.error("Exception msg:" + e.getMessage());
		}

		return ret;
	}

	
	/**
	 * @param consumerKey
	 * @param scope
	 * @param ttl
	 * @return
	 */
	public String addAccessToken(String consumerKey, String scope, int ttl) {

		String tokenValue = null;

		try {

			String oldAccessToken = getAccessToken(consumerKey);
			if (oldAccessToken != null)
				return oldAccessToken;

			Connection conn = dataSource.getConnection();
			Statement st = null;
			ResultSet rs = null;
			st = conn.createStatement();
			rs = st.executeQuery("SELECT ID FROM consumer WHERE CONSUMERKEY='"
					+ consumerKey + "'");

			long consumerId = -1;
			while (rs.next())
				consumerId = rs.getLong(1);

			//int ttl = -1;
			
			//generate token value
			tokenValue = makeRandomString();
			
			scope = URLDecoder.decode(scope, "UTF-8");

			st.executeUpdate("INSERT INTO accesstoken (TOKEN,TIMETOLIVE,SCOPES,CONSUMER) "
					+ "VALUES('"
					+ tokenValue
					+ "','"
					+ ttl
					+ "','"
					+ scope
					+ "','"
					+ consumerId + "');");

			conn.close();
		} catch (Exception e) {
			// System.out.println("################# ERROR ###############");
			log.error("Exception msg:" + e.getMessage());
			tokenValue = null;
		}

		log.debug("Added accessToken=" + makeRandomString()
				+ " for consumerkey=" + consumerKey);
		return tokenValue;
	}

	/**
	 * @param consumerKey
	 * @return
	 */
	public String getAccessToken(String consumerKey) {

		String ret = null;
		try {
			Connection conn = dataSource.getConnection();
			Statement st = null;
			ResultSet rs = null;
			st = conn.createStatement();
			rs = st.executeQuery("SELECT accesstoken.TOKEN FROM accesstoken "
					+ "INNER JOIN consumer ON accesstoken.consumer=consumer.ID "
					+ "WHERE consumer.consumerkey='" + consumerKey + "'");
			while (rs.next()) {
				ret = rs.getString(1);
				log.debug("Already found accessToken =" + ret);
			}
			conn.close();
		} catch (Exception e) {
			// System.out.println("################# ERROR ###############");
			log.error("Exception msg:" + e.getMessage());
		}

		return ret;
	}

	
	/**
	 * @param clientId
	 * @param username
	 * @param deviceId
	 * @param b 
	 * @return
	 */
	public String getAccessTokenRedirectURI(String redirect_uri,
			String clientId, String username, String deviceId, boolean fragmentMode) {

		String ret = null;
		//HTTP/1.1 302 Found
	    // Location: http://example.com/rd#access_token=FJQbwq9&
	    //           token_type=example&expires_in=3600
		log.debug("Enter getAccessTokenRedirectURI()");
		String consumerkey;
		String scope;
		
		try {
			consumerkey = addConsumerKey(clientId,
					username, deviceId);
			if (consumerkey != null) {
				log.debug("Added consumerKey");
				
				String appId = null;
				OAuthSocialTokenUtils oAuthSocialTokenUtils = new OAuthSocialTokenUtils();
				appId = oAuthSocialTokenUtils.getAppIdByClientId(clientId);
				scope = getAppIdScope(appId);
					
				//temporarily TTL = -1
				String accessToken = addAccessToken(consumerkey,scope,-1);
				
				if (accessToken != null) {
					log.debug("Added accessToken");
					
					if(fragmentMode)
						redirect_uri = redirect_uri+"#access_token="+accessToken+"&token_type="+ssoOauthTokenType
						+"&expires_in="+ssoOauthTokenExpires+"&scope="+scope;
					else
						redirect_uri = redirect_uri+"?access_token="+accessToken+"&token_type="+ssoOauthTokenType
						+"&expires_in="+ssoOauthTokenExpires+"&scope="+scope;
					log.debug("redirect URI="+redirect_uri);
				    //redirect_uri = URLEncoder.encode(redirect_uri, "UTF-8");
				}
			}	

		} catch (Exception e) {
			// System.out.println("################# ERROR ###############");
			log.error("Exception msg:" + e.getMessage());
		}

		return redirect_uri;
	}
	
	/**
	 * @param clientId
	 * @return
	 */
	public String getAppIdScope(String appId) {

		String ret = null;
		//HTTP/1.1 302 Found
	    // Location: http://example.com/rd#access_token=FJQbwq9&
	    //           token_type=example&expires_in=3600
		appId = appId.toLowerCase();
		String scope = "";
		log.debug("Enter getAppIdScope() for appId="+appId);
		try {
						
			Connection conn = dataSource.getConnection();
			Statement st = null;
			ResultSet rs = null;
			st = conn.createStatement();
					
			rs = st.executeQuery("SELECT client.SCOPE FROM client "
					+ "WHERE client.CLIENTID='" + appId + "'");
			while (rs.next()) {
				scope = rs.getString(1);
				log.debug(" Found scope for appId =" + scope);
			}
			conn.close();
			
			scope = URLEncoder.encode(scope, "UTF-8");
			
		} catch (Exception e) {
			// System.out.println("################# ERROR ###############");
			log.error("Exception msg:" + e.getMessage());
		}

		return scope;
	}
	
}
