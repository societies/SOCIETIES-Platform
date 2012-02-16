/*********************************************
  Context Awareness Platform
 ********************************************
  Copyright (c) 2006-2010 Telecom Italia S.p.A
 ******************************************** 
  $Id: LMD5Gen.java 6618 2009-12-16 18:47:42Z papurello $
  $HeadURL: http://163.162.93.163:90/svn/ca/platform/aup/branches/rel-1_3-ev/src/main/java/com/tilab/ca/platform/aup/util/utils/LMD5Gen.java $
 *********************************************
 */
package com.tilab.ca.platform.SSO.social.facebook;

import java.io.IOException;
import java.io.UTFDataFormatException;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.URLEncoder;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.json.JSONException;

import com.tilab.ca.platform.SSO.social.facebook.exceptions.ArgumentApplicationException;
import com.tilab.ca.platform.SSO.social.facebook.util.ConfigParameters;
import com.tilab.ca.platform.SSO.social.facebook.util.HTTPUtilsTimedoutConnection;

/**
 * @author Administrator
 * 
 */
public class FacebookUtils {

	/**
	 * Logger
	 */
	private static final Logger log = Logger.getLogger(FacebookUtils.class); 
	
	Properties properties   = null;
	String facebookUrl      = null;
	String fbAppId		    = "368482799848413";
	String fbAppSecret 	    = "c1788688a3091638768ed803d6ebdbd0";
	String ssoUrl		    = "http://localhost:8080/ ";

	/**
	 * 
	 */
	public FacebookUtils(String appId) {

		log.debug("New Facebook Utils for appId=" + appId);
		try {
			
			this.facebookUrl = "";
			log.debug("facebookUrl = " + this.facebookUrl);

			if (appId != null) {
				this.fbAppId = appId;
			}
			
			log.debug("fbAppId 		= " + fbAppId);
			log.debug("fbAppSecret 	= " + fbAppSecret);
			log.debug("ssoUrl 		= " + ssoUrl);

		} catch (Exception e) {
			log.error("Exception msg:" + e.getMessage());
		}
	}

	/**
	 * @param code
	 * @return
	 * @throws ArgumentApplicationException
	 * @throws UTFDataFormatException
	 * @throws ConnectException
	 * @throws SocketTimeoutException
	 * @throws JSONException
	 * @throws UnsupportedEncodingException
	 */
	public FacebookToken getAccessToken(String code, boolean isMobile, boolean isConnect) throws ArgumentApplicationException,
			SocketTimeoutException, ConnectException, UTFDataFormatException,
			JSONException, UnsupportedEncodingException {
		// TODO Auto-generated method stub
		if (code == null)
			throw new ArgumentApplicationException("Facebook code not returned");

		String action = null;

		if (isConnect)
			action = "connect/FB/connectdone";
		else if (isMobile)
			action = "msignin/FB/msignindone";
		else
			action = "signin/FB/signindone";

		// https://graph.facebook.com/oauth/access_token?
		// client_id=YOUR_APP_ID&redirect_uri=YOUR_URL&
		// client_secret=YOUR_APP_SECRET&code=THE_CODE_FROM_ABOVE
	
		String facebookAccessTokenURL = facebookUrl
										+ "oauth/access_token?client_id=" + this.fbAppId
										+ "&redirect_uri=" + URLEncoder.encode(this.ssoUrl, "UTF-8")
										+ action + "&client_secret=" + this.fbAppSecret + "&code="
										+ code;

		String accessTokenResponse = HTTPUtilsTimedoutConnection.sendGet(facebookAccessTokenURL, null, ConfigParameters.connTimeout_ms,	ConfigParameters.readTimeout_ms);
		log.debug("Facebook response (token) received:" + accessTokenResponse);

		if (accessTokenResponse == null)
			throw new ArgumentApplicationException(
					"Error occurred when contacting Facebook");

		FacebookToken access_token = new FacebookToken();
		try {

			String[] params = accessTokenResponse.split("&");
			for (String param : params) {
				String name = param.split("=")[0];
				String value = param.split("=")[1];
				log.debug("name=" + name + ",value=" + value);

				if (name.equals("access_token"))
					access_token.setAccessToken(value);
				else if (name.equals("expires"))
					access_token.setExpires(new Integer(value).intValue());

			}

			log.debug("access_token from FB=" + access_token.getAccessToken());
		} catch (Exception e) {
			log.error("Error:" + e.getMessage());
		}

		return access_token;
	}

	/**
	 * @param request
	 * @param response
	 * @param isMobile
	 * @throws IOException
	 */
	public void redirectToOAuthDialog(HttpServletRequest request, HttpServletResponse response, boolean isMobile,  boolean isSocialLogin) throws IOException {
		

		
		
		String client_id = (String) request.getSession().getAttribute("client_id");
		log.debug("client id = " + client_id);
		
		if (client_id == null)	  client_id = (String) MDC.get("clientId");
		if (client_id == null)    client_id = fbAppId;
		log.debug(" client id = " + client_id);
		
	
		String scope = "user_about_me,email,user_birthday,user_location,offline_access,publish_stream,publish_checkins,user_checkins,friends_checkins,user_status,friends_status,user_photos,read_stream";
		log.debug("Setting scope = " + scope);

		
		String display 	= "page";
		if (isMobile)   display = "touch";

		String action = "signin/FB/signindone";
		if (isMobile && isSocialLogin)
			action = "msignin/FB/msignindone";
		else if (!isSocialLogin)
			action = "connect/FB/connectdone";

		String url = "http://www.facebook.com/dialog/oauth?client_id="
				+ client_id + "&redirect_uri="
				+ URLEncoder.encode(this.ssoUrl, "UTF-8") + action
				+ "&display=" + display + "&scope=" + scope
				+ "&response_type=code";
		log.debug("Redirecting to URL = " + url);

		response.sendRedirect(url);
		
		// http://www.facebook.com/dialog/oauth?client_id=368482799848413&redirect_uri=http://wd.teamlife.it/doconnect.php&scope=user_about_me,email,user_birthday,user_location,offline_access,publish_stream,publish_checkins,user_checkins,friends_checkins,user_status,friends_status,user_photos,read_stream
		
		
		//http://wd.teamlife.it/fbconnection.php
		/*
		 * log.debug("test red"); RequestDispatcher dispatcher =
		 * request.getRequestDispatcher("redirectProvider.jsp?url="+url); try {
		 * dispatcher.forward(request, response); } catch (Exception e) {
		 * log.debug("Exception msg=" + e.getMessage()); // e.printStackTrace();
		 * }
		 */

	}

	

}
