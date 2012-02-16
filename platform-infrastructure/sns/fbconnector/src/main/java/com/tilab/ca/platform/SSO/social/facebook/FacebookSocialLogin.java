/*********************************************
  Context Awareness Platform
 ********************************************
  Copyright (c) 2006-2010 Telecom Italia S.p.A
 ******************************************** 
  $Id: PortalServlet.java 8692 2010-12-17 15:27:11Z antonazzo $
  $HeadURL: http://163.162.93.163:90/svn/ca/platform/aup/branches/rel-1_3-ev/src/main/java/com/tilab/ca/platform/aup/presentation/portal/PortalServlet.java $
 *********************************************
 */
package com.tilab.ca.platform.SSO.social.facebook;

import java.io.IOException;
import java.io.UTFDataFormatException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.StringTokenizer;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.json.JSONException;
import org.json.JSONObject;

import com.tilab.ca.platform.SSO.social.facebook.exceptions.ArgumentApplicationException;
import com.tilab.ca.platform.SSO.social.facebook.exceptions.FacebookConnectException;
import com.tilab.ca.platform.SSO.social.facebook.oauthutil.OAuthSocialTokenUtils;
import com.tilab.ca.platform.SSO.social.facebook.util.ConfigParameters;
import com.tilab.ca.platform.SSO.social.facebook.util.ConnectUserInfo;
import com.tilab.ca.platform.SSO.social.facebook.util.HTTPUtilsTimedoutConnection;
import com.tilab.ca.platform.SSO.social.facebook.util.HttpServletRequestWrapper;
import com.tilab.ca.platform.SSO.social.facebook.util.HttpServletUtil;

//import com.tilab.ca.platform.SSO.social.facebook.util.HttpServletUtil;




public class FacebookSocialLogin extends HttpServlet {

	
	
	/**
	 * Logger
	 */
	private static final Logger log = Logger.getLogger(FacebookSocialLogin.class);

	FacebookUtils fbUtil = null;
	String fbAppId		 = "368482799848413";
	String fbAppSecret 	 = "c1788688a3091638768ed803d6ebdbd0";

	private FacebookUtils fbUtils;

	/**
	 * 
	 */
	public FacebookSocialLogin() {
		super();
		
		log.debug("FacebookSocialLogin()");
		fbUtil = new FacebookUtils(fbAppId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.GenericServlet#destroy()
	 */
	public void destroy() {
		log.trace("destroy()");
		super.destroy();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.GenericServlet#init(javax.servlet.ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		log.trace("init FacebookSocialLogin config");
		try {
			super.init(config);

		} catch (Exception e) {
			// throw new ServletException("Lookup of java:/comp/env/ failed");
			throw new ServletException("There was a problem on our systems. Please try later, thank you.",e);
		}

	}

	/**
	 * This method handles both GET and POST requests. This method is called by
	 * the servlet container to process a GET or POST request.
	 * 
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            HttpServletResponse
	 * @param currentSession
	 *            HttpSession
	 */
	protected void doGetOrPost(HttpServletRequest request, HttpServletResponse response, HttpSession currentSession)
			  throws ServletException, IOException {
		
		log.trace("doGetOrPost(HttpServletRequest request, HttpServletResponse response, HttpSession currentSession)");
		String actionCommand = HttpServletUtil.getServletMethodName(request);
	
		
		HttpServletRequestWrapper requestWrapper = new HttpServletRequestWrapper(request, null);

		String viewPage				 	= "";
		String clientUrlParams 			= null;
		String returnToClientUrlBack	= null;

		returnToClientUrlBack = (String) request.getSession().getAttribute("baseUri");
		if (returnToClientUrlBack == null) {
			
			
			log.warn("ReturnToClientUrlBack is NULL");
			/// LUCA TO BE CHECKED!!!!
			//returnToClientUrlBack = this.getTempBaseUri();  // METHOD JBOSS?????
		}
		
		String remoteAddress = "";

		try {
			remoteAddress = request.getRemoteAddr();
			log.info("We have received a request (through "
					+ requestWrapper.getMethod() + " method) for \""
					+ actionCommand + "\"..." + "from IP address ="
					+ remoteAddress);

			// Sign In done with Facebook from mobile
			if (FacebookEvent.DOCONNECT.equals(actionCommand)){
			   
				fbUtil.redirectToOAuthDialog(request, response, false, false);
				
			} else if (FacebookEvent.CONNECTDONEFBCONNECT.equals(actionCommand)) {
				doConnectDoneFacebook(request, response);
			}

		}
		catch (FacebookConnectException e) {
		
			log.error("FacebookConnectException msg=" + e.getMessage());
			clientUrlParams = "????";
			
			log.debug("CLIENT redirect to:" + returnToClientUrlBack + clientUrlParams);
			
//			redirectToClient(false, 
//							request, 
//							response,
//							returnToClientUrlBack,
//							URLEncoder.encode(clientUrlParams, "UTF-8"), 
//							null);
		
		} catch (Exception e) {

			log.debug("Exception occurred=" + e.getMessage());
			if (FacebookEvent.SIGNINDONEFBCONNECT.equals(actionCommand)) {
				
			} 
			else {
				log.debug("CLIENT redirect to:" + returnToClientUrlBack + clientUrlParams);
			
			}
		}

}
		
	

	/**
	 * @param request
	 * @param response
	 * @throws JSONException
	 *             ,FacebookConnectException
	 * @throws UTFDataFormatException
	 * @throws ArgumentApplicationException
	 * @throws ConnectException
	 * @throws SocketTimeoutException
	 * @throws Exception
	 * 
	 *             Callback called from Facebook Provider after authentication
	 *             and authorization
	 */
	private void doConnectDoneFacebook(HttpServletRequest request, HttpServletResponse response) throws Exception {

		boolean isMobile = true;
		int aupUserId = -1;
		String clientId = null;
		String appId = null;
		String aupUsername = null;

		try {

			// Get request user info : clientid, aup uid and username .. from
			// cookie
			ConnectUserInfo user = getConnectUserInfoFromSession(request, response);
			if (user != null) {
				clientId = user.getClientid();
				aupUserId = new Integer(user.getUserid()).intValue();
				aupUsername = user.getUsername();
				isMobile = new Boolean(user.isMobile()).booleanValue();
			}

		

			// if (fbUtils == null)
			fbUtils = new FacebookUtils(appId);

			// OAuth2 -> retrieve facebook code
			String code = request.getParameter("code");
			log.debug("facebook code=" + code);

			if (code == null) {
				if (request.getParameter("error") != null && ((String) request.getParameter("error")).equals("access_denied")){
					log.debug("Code ERROR");
					throw new Exception();
					
				}
			}

			// OAuth2 -> exchange Facebook code for access token
			FacebookToken fbtoken = fbUtils.getAccessToken(code, isMobile, true);
			String access_token = fbtoken.getAccessToken();

			if (access_token == null)
				throw new FacebookConnectException(" Error occurred when contacting Facebook");

			String facebookMeURL = "https://graph.facebook.com/me?access_token="+ access_token;
			
			String meResponse = HTTPUtilsTimedoutConnection.sendGet(facebookMeURL, null, ConfigParameters.connTimeout_ms);
			log.debug("Facebook response received:" + meResponse);

			if (meResponse == null)
				throw new FacebookConnectException(
						" Error occurred when contacting Facebook");

			JSONObject jsonMeResponse = null;
			jsonMeResponse = new JSONObject(meResponse);

			String id = null;
			try {
				id = (String) jsonMeResponse.getString("id");
				log.debug("id from FB=" + id);

			} catch (Exception e) {
			}

			String email = null;
			try {
				email = (String) jsonMeResponse.getString("email");
				log.debug("email from FB=" + email);
			} 
			
			catch (Exception e) {
			}

//			// FB user id / email -> AUP username
//			String username = null;
//			String fbUserId = (String) properties
//					.getProperty(CaProperties.PROPERTY_PREFIX
//							+ "username.fb.userid");
//			log.debug("fbUserId = " + fbUserId);

//			if (fbUserId != null && fbUserId.equals("true"))
//				username = id;
//			else
//				username = email;

			// Save Facebook Social Access Token
			String token = null;
			
			String username = email;
			
			
			OAuthSocialTokenUtils oauthSocialTokenUtils = new OAuthSocialTokenUtils();
			token = oauthSocialTokenUtils.addOAuth2SocialToken(aupUserId, username, "facebook", appId, fbtoken.getExpires(), fbtoken.getAccessToken(),"");
			
			if (token != null) {
				if (isMobile) {
					
					String goBackToApp = "";
				
					// this.returnToClientConnectUrl.replace(
					// "<client_id>", clientId);
					goBackToApp = goBackToApp + "#provider=FB&socialusername="
							+ URLEncoder.encode(username, "UTF-8")
							+ "&status=OK";
					request.getSession().setAttribute("goback", goBackToApp);

					// pagina mobile conferma esito
					// viewPage =
					// "msocialconnect.jsp?provider=Facebook&socialusername="
					// + username + "&status=OK";
					// loginUtils.doForwardToPage(request, response, viewPage);

					// AUTOMATIC REDIRECT
					log.info("Automatic redirect to:" + goBackToApp);
					response.sendRedirect(goBackToApp);

				} else {
//					viewPage = "socialconnect.jsp?provider=Facebook&socialusername="
//							+ username
//							+ "&status=OK&aupUserId="
//							+ aupUserId
//							+ "&aupUsername=" + aupUsername + "&app=" + appId;
//					// loginUtils.doForwardToPage(request, response, viewPage);
					//response.sendRedirect(ssoUrl + viewPage);
				}

			} else
				throw new FacebookConnectException();

		} catch (Exception e) {
			log.error("Error when adding Facebook social token:"
					+ e.getMessage());

			if (isMobile) {
				String goBackToApp = "";
				goBackToApp = goBackToApp + "#provider=FB&status=ERROR";
				request.getSession().setAttribute("goback", goBackToApp);

				// AUTOMATIC REDIRECT
				log.debug("Automatic redirect to:" + goBackToApp);
				response.sendRedirect(goBackToApp);

			} else {
//				viewPage = "socialconnect.jsp?provider=Facebook&status=ERROR&aupUserId="
//						+ aupUserId
//						+ "&aupUsername="
//						+ aupUsername
//						+ "&errormsg=" + e.getMessage() + "&app=" + appId;
//				response.sendRedirect(ssoUrl + viewPage);
				// loginUtils.doForwardToPage(request, response, viewPage);
			}
		}

		// MDC.remove("user_id");
		// MDC.remove("clientId");
		// MDC.remove("isMobile");

	}
	
	
	
	/**
	 * @param request
	 * @param response
	 * @return
	 */
	public ConnectUserInfo getConnectUserInfoFromSession(HttpServletRequest request, HttpServletResponse response) {
		
		Cookie cookie[] 	= request.getCookies();
		String cookieName 	= null;
		String cookieValue 	= null;
		String cid 			= null;
		String clientid 	= null;
		String userid 		= null;
		String username 	= null;
		String isMobile 	= null;

		ConnectUserInfo connUser = new ConnectUserInfo();

		try {
			
			String connectCookieSep 	  =  "";
			log.debug("CONNECT Cookie Sep = " + connectCookieSep);

			if (cookie != null && cookie.length > 0) {
				
				for (int i = 0; i < cookie.length; i++) {
				
					cookieName = cookie[i].getName();
					log.debug("cookieName=" + cookieName);
					if (cookieName.equals("snconnect_user_info")) {
						cookieValue = cookie[i].getValue();
						
						log.debug("checking .."+ "FOUND snconnect_user_info cookie Value=" + cookieValue);
						StringTokenizer st = new StringTokenizer(cookieValue,connectCookieSep);
				
						cid = st.nextToken();
						log.debug("cid=" + cid);
						clientid = st.nextToken();
						log.debug("clientid=" + clientid);
						userid = st.nextToken();
						log.debug("userid=" + userid);
						username = URLDecoder.decode(st.nextToken(), "UTF-8");
						log.debug("username=" + username);
						isMobile = st.nextToken();
						log.debug("isMobile=" + isMobile);
					}
				}
			}

			// ????To evaluate if remove the code below ... ??
			if (cid == null) {
				cid = (String) MDC.get("cid");
				if (cid == null)
					cid = (String) request.getSession().getAttribute("cid");
			}
			//if (clientid == null) {
				//clientid = (String) MDC.get("clientId");
				if (clientid == null)
					clientid = (String) request.getSession().getAttribute(
							"client_id");
			//}

			if (userid == null) {
				Integer useridI = null; 
					//((Integer) MDC.get("user_id"));
				if (useridI == null)
					useridI = (Integer) request.getSession().getAttribute(
							"user_id");

				if (useridI != null)
					userid = useridI.toString();

			}

			//if (username == null) {
				//username = (String) MDC.get("username");
				if (username == null)
					username = (String) request.getSession().getAttribute(
							"username");
			//}

			if (isMobile == null) {
				Boolean isMobileB = null;
					//(Boolean) MDC.get("isMobile");
				if (isMobileB == null)
					isMobileB = (Boolean) request.getSession().getAttribute(
							"isMobile");
				isMobile = "true";
				if (isMobileB != null)
					isMobile = isMobileB.toString();
			}

			log.debug("clientid=" + clientid);
			log.debug("cid=" + cid);
			log.debug("isMobile=" + isMobile);
			log.debug("userid=" + userid);
			log.debug("username=" + username);

			if (clientid == null || userid == null
					|| username == null || isMobile == null)
				throw new Exception("Invalid attributes, Some errors occurred");

			connUser.setCid(cid);
			connUser.setClientid(clientid);
			connUser.setUserid(userid);
			connUser.setUsername(username);
			connUser.setMobile(isMobile);

		} catch (Exception e) {
			log.error("Exception msg=" + e.getMessage());
		}
		return connUser;
	}

	/**
	 * @param request
	 * @param response
	 * @throws ArgumentApplicationException
	 * @throws JSONException
	 * @throws FacebookConnectException
	 * @throws IOException
	 * @throws UnsupportedEncodingException
	 * @throws NoSuchAlgorithmException
	 * @throws FatalApplicationException
	 * @throws UserNotFoundException
	 * @throws UserCidInactiveException
	 * @throws UserInactiveException
	 * @throws UserDeniedException
	 */
//	private void doSignInDoneFacebook(HttpServletRequest request, HttpServletResponse response) throws ArgumentApplicationException,
//			JSONException, FacebookConnectException, UserNotFoundException,
//			FatalApplicationException, NoSuchAlgorithmException,
//			UnsupportedEncodingException, IOException, UserInactiveException,
//			UserCidInactiveException, UserDeniedException {
//		boolean alreadyExisting = false;
//		boolean cantCreate = false;
//		boolean cantAuthenticate = false;
//		String error = null;
//		String appId = null;
//		String clientId = (String) request.getSession().getAttribute(
//				"client_id");
//
//		appId = oauthSocialTokenUtils.getAppIdByClientId(clientId);
//
//		// 24/05/2011
//		// if (fbUtils == null)
//		fbUtils = new FacebookUtils(appId);
//
//		// Retrieve Facebook code
//		String code = request.getParameter("code");
//		log.debug("facebook code=" + code);
//
//		if (code == null) {
//			if (request.getParameter("error") != null
//					&& ((String) request.getParameter("error"))
//							.equals("access_denied"))
//				throw new UserDeniedException(clientId);
//
//		}
//
//		// Exchange Facebook code for Facebook Access Token
//		FacebookToken fbtoken = fbUtils.getAccessToken(code, false, false);
//		String access_token = fbtoken.getAccessToken();
//
//		if (access_token == null)
//			throw new ArgumentApplicationException(
//					" Error occurred when contacting Facebook");
//
//		String facebookMeURL = facebookUrl
//				+ "me?access_token="
//				+ access_token
//				+ "&"
//				+ "fields=id,name,email,username,first_name,last_name,gender,locale,birthday,location,picture";
//
//		String meResponse = HTTPUtilsTimedoutConnection.sendGet(facebookMeURL,
//				null, ConfigParameters.connTimeout_ms);
//		log.debug("Facebook response received:" + meResponse);
//
//		if (meResponse == null)
//			throw new ArgumentApplicationException(
//					" Error occurred when contacting Facebook");
//
//		JSONObject jsonMeResponse = null;
//
//		jsonMeResponse = new JSONObject(meResponse);
//
//		String id = null;
//		try {
//			id = (String) jsonMeResponse.getString("id");
//			log.debug("id from FB=" + id);
//
//		} catch (Exception e) {
//		}
//
//		String email = null;
//		try {
//			email = (String) jsonMeResponse.getString("email");
//			log.debug("email from FB=" + email);
//		} catch (Exception e) {
//		}
//
//		String nickname = null;
//		try {
//			nickname = (String) jsonMeResponse.getString("username");
//			log.debug("nickname from FB=" + nickname);
//			if (nickname.equals("undefined"))
//				nickname = null;
//		} catch (Exception e) {
//		}
//
//		String firstName = null;
//		try {
//			firstName = (String) jsonMeResponse.getString("first_name");
//			log.debug("firstName from FB=" + firstName);
//			if (firstName.equals("undefined"))
//				firstName = null;
//		} catch (Exception e) {
//		}
//
//		String lastName = null;
//		try {
//			lastName = (String) jsonMeResponse.getString("last_name");
//			log.debug("lastName from FB=" + lastName);
//			if (lastName.equals("undefined"))
//				lastName = null;
//		} catch (Exception e) {
//		}
//
//		String gender = null;
//		try {
//			gender = (String) jsonMeResponse.getString("gender");
//
//			if (gender.equalsIgnoreCase("male"))
//				gender = "M";
//			else if (gender.equalsIgnoreCase("female"))
//				gender = "F";
//			else
//				gender = "";
//			log.debug("gender from FB=" + gender);
//		} catch (Exception e) {
//		}
//
//		String language = null;
//		try {
//			language = (String) jsonMeResponse.getString("locale");
//			log.debug("language from FB=" + language);
//		} catch (Exception e) {
//		}
//
//		String dob = null;
//		try {
//			dob = (String) jsonMeResponse.getString("birthday");
//			log.debug("dob from FB=" + dob);
//			if (dob.equals("undefined"))
//				dob = null;
//		} catch (Exception e) {
//		}
//
//		String country = null;
//		String location = null;
//		try {
//			location = (String) jsonMeResponse.getString("location");
//			country = new JSONObject(location).getString("name");
//			log.debug("country from FB=" + country);
//			if (country.equals("undefined"))
//				country = null;
//		} catch (Exception e) {
//		}
//
//		String avatar = null;
//		try {
//			avatar = (String) jsonMeResponse.getString("picture");
//			log.debug("avatar from FB=" + avatar);
//		} catch (Exception e) {
//		}
//		// 24/05/2011
//
//		// FB user id / email -> AUP username
//		String userid = null;
//		String fbUserId = (String) properties
//				.getProperty(CaProperties.PROPERTY_PREFIX
//						+ "username.fb.userid");
//		log.debug("fbUserId = " + fbUserId);
//		if (fbUserId != null && fbUserId.equals("true"))
//			userid = id;
//		else
//			userid = email;
//
//		String aupUsername = null;
//		String hash = (String) properties
//				.getProperty(CaProperties.PROPERTY_PREFIX + "username.hash");
//		log.debug("hash = " + hash);
//		if (hash != null && hash.equals("true"))
//			aupUsername = AeSimpleMD5.MD5(userid);
//		else
//			aupUsername = userid;
//		log.debug("aupUsername =" + aupUsername);
//
//		if (userid == null) {
//			// userid = identity.substring(7, identity.length() - 1);
//			throw new FacebookConnectException(
//					SSOLoginUtils.errorLoginFacebookMessageIdNotReturned);
//
//		}
//
//		int chkStr = checkUserExistence(aupUsername,
//				ConfigParameters.default_pwd);
//
//		if (loginUtils == null)
//			loginUtils = new SSOLoginUtils();
//
//		String cid = (String) MDC.get("cid");
//		log.debug("cid=" + cid);
//		if (cid == null || cid.equals("") || cid.length() < 1) {
//			try {
//				CaProperties.bindProperties(log);
//				properties = CaProperties.getProperties(log);
//				cid = properties.getProperty(CaProperties.PROPERTY_PREFIX
//						+ "default.cid");
//				log.debug("default cid = " + cid);
//			} catch (IOException e) {
//				log.error("Lookup of \"AUP_config_properties\" failed", e);
//				cid = "teamlife";
//			}
//			if (cid == null || cid.equals("") || cid.length() < 1)
//				cid = "teamlife";
//		}
//
//		String ssoCookieValue = null;
//		User u = null;
//		switch (chkStr) {
//		case 0: // esiste e la pwd e' corretta
//			log.trace("The user already exist and pwd is correct, START LOGIN user via FB connect ");
//			alreadyExisting = true;
//
//			u = loginUtils.doSSOLogin(request, response, ssoModel, aupUsername,
//					AeSimpleMD5.MD5(ConfigParameters.default_pwd), cid, true);
//
//			userProfileRemote.addUserAvatar(aupUsername, avatar);
//
//			// Save Facebook Social Token
//			try {
//				if (oauthSocialTokenUtils != null && u != null)
//
//					oauthSocialTokenUtils.addOAuth2SocialToken(u.getUserid()
//							.intValue(), userid, SocialLogin.facebookProvider,
//							appId, fbtoken.getExpires(), fbtoken
//									.getAccessToken(),"");
//			} catch (Exception e) {
//				log.error("Error adding FB social token:" + e.getMessage());
//			}
//
//			// Manage redirect to calling system based on session
//			// attribute
//			// (return_to)
//
//			/*
//			 * if(nickname!=null) ssoModel.setLogin(nickname); else
//			 * ssoModel.setLogin(firstName+" "+lastName);
//			 */
//
//			loginUtils.handleRedirect(request, response, viewPage, u, ssoModel,
//					SocialLogin.tempClientId, false);
//
//			break;
//		case 1: // esiste ma pwd errata
//			// TODO LOGIN ERROR
//			log.debug("User exist but pwd is wrong!");
//			alreadyExisting = true;
//			cantAuthenticate = true;
//			log.debug("SSO Login failure - Redirect to WELCOME");
//			viewPage = "/login.jsp" + "?errormsg="
//					+ SSOLoginUtils.errorLoginMessage;
//
//			loginUtils.doForwardToPage(request, response, viewPage);
//			break;
//		case 2: // non esiste
//
//			log.debug("Create user via FB connect and LOGIN user");
//
//			if (createUserFromIUP(aupUsername, userid,
//					UUID.randomUUID().toString().substring(0,13), email, firstName, lastName,
//					nickname, gender, dob, country, language, null,
//					AccType.FBCONNECT, avatar)) {
//				log.debug("SSO Login success - manage Redirect ");
//				// Manage redirect to calling system based on session
//				// attribute
//				// (redirect_uri)
//
//				u = loginUtils.doSSOLogin(request, response, ssoModel,
//						aupUsername,
//						AeSimpleMD5.MD5(ConfigParameters.default_pwd), cid,
//						true);
//
//				// Save Facebook Social Access Token
//				try {
//					if (oauthSocialTokenUtils != null && u != null) {
//						appId = oauthSocialTokenUtils
//								.getAppIdByClientId((String) request
//										.getSession().getAttribute("client_id"));
//						oauthSocialTokenUtils
//								.addOAuth2SocialToken(u.getUserid().intValue(),
//										userid, SocialLogin.facebookProvider,
//										appId, fbtoken.getExpires(),
//										fbtoken.getAccessToken(),"");
//					}
//
//				} catch (Exception e) {
//					log.error("Error adding FB social token:" + e.getMessage());
//				}
//
//				/*
//				 * if(nickname!=null) ssoModel.setLogin(nickname); else
//				 * ssoModel.setLogin(firstName+" "+lastName);
//				 */
//
//				loginUtils.handleRedirect(request, response, viewPage, u,
//						ssoModel, SocialLogin.tempClientId, true);
//			} else {
//				viewPage = "/login.jsp" + "?errormsg="
//						+ SSOLoginUtils.errorLoginGenericMessage;
//				loginUtils.doForwardToPage(request, response, viewPage);
//			}
//
//			break;
//		case 3: // eccezione nel parsing
//			log.debug("Error in parsing IUP response");
//			error = "Error in parsing IUP response";
//			viewPage = "/login.jsp" + "?errormsg="
//					+ SSOLoginUtils.errorLoginGenericMessage;
//			loginUtils.doForwardToPage(request, response, viewPage);
//			break;
//		default: // errore sconosciuto
//			log.debug("Unknown error code from IUP");
//			error = "Unknown error code from IUP";
//			viewPage = "/login.jsp" + "?errormsg="
//					+ SSOLoginUtils.errorLoginGenericMessage;
//			loginUtils.doForwardToPage(request, response, viewPage);
//			break;
//		}
//
//		MDC.remove("cid");
//
//	}

//	/**
//	 * @param request
//	 * @param response
//	 * @throws Exception
//	 * @throws UserAlreadyExistsException
//	 * @throws ArgumentApplicationException
//	 */
//	private void doMobileSignInDoneFacebook(HttpServletRequest request,
//			HttpServletResponse response, String clientUrlParams,
//			String returnToClientUrlBack) throws UserAlreadyExistsException,
//			ArgumentApplicationException, Exception {
//
//		String appId = null;
//		String clientId = (String) request.getSession().getAttribute(
//				"client_id");
//		log.debug("client_id=" + clientId);
//		appId = oauthSocialTokenUtils.getAppIdByClientId(clientId);
//
//		boolean privacyOn = false;
//		String checkboxPrivacy = (String) request.getSession().getAttribute(
//				"checkboxPrivacy");
//		log.debug("checkboxPrivacy =" + checkboxPrivacy);
//		if (checkboxPrivacy != null && checkboxPrivacy.equalsIgnoreCase("on"))
//			privacyOn = true;
//
//		// if (fbUtils == null)
//		fbUtils = new FacebookUtils(appId);
//
//		// Retrieve Facebook code
//		String code = request.getParameter("code");
//		log.debug("facebook code=" + code);
//
//		if (code == null) {
//			if (request.getParameter("error") != null
//					&& ((String) request.getParameter("error"))
//							.equals("access_denied"))
//				throw new UserDeniedException(clientId);
//
//		}
//
//		// Exchange Facebook code for access token
//		FacebookToken fbtoken = fbUtils.getAccessToken(code, true, false);
//		String access_token = fbtoken.getAccessToken();
//
//		if (access_token == null || fbtoken == null)
//			throw new ArgumentApplicationException(
//					" Error occurred when contacting Facebook");
//
//		String facebookMeURL = facebookUrl
//				+ "me?access_token="
//				+ access_token
//				+ "&"
//				+ "fields=id,name,email,username,first_name,last_name,gender,locale,birthday,location,picture";
//		String meResponse = HTTPUtilsTimedoutConnection.sendGet(facebookMeURL,
//				null, ConfigParameters.connTimeout_ms);
//		log.info("Facebook response received:" + meResponse);
//
//		// String client_id = null;
//
//		if (meResponse == null)
//			throw new ArgumentApplicationException(
//					" Error occurred when contacting Facebook");
//
//		JSONObject jsonMeResponse = null;
//
//		jsonMeResponse = new JSONObject(meResponse);
//
//		String error = null;
//		String id = null;
//		Set usersAlias = null;
//
//		try {
//			id = (String) jsonMeResponse.getString("id");
//			log.debug("id from FB=" + id);
//
//		} catch (Exception e) {
//		}
//
//		String email = null;
//		try {
//			email = (String) jsonMeResponse.getString("email");
//			log.debug("email from FB=" + email);
//		} catch (Exception e) {
//		}
//
//		String nickname = null;
//		try {
//			nickname = (String) jsonMeResponse.getString("username");
//		} catch (Exception e) {
//		}
//
//		log.debug("nickname (alias) from FB=" + nickname);
//		String assignedAlias = null;
//
//		String firstName = null;
//		try {
//			firstName = (String) jsonMeResponse.getString("first_name");
//			log.debug("firstName from FB=" + firstName);
//			if (firstName.equals("undefined"))
//				firstName = null;
//		} catch (Exception e) {
//		}
//
//		String lastName = null;
//		try {
//			lastName = (String) jsonMeResponse.getString("last_name");
//			log.debug("lastName from FB=" + lastName);
//			if (lastName.equals("undefined"))
//				lastName = null;
//		} catch (Exception e) {
//		}
//
//		String gender = null;
//
//		if (privacyOn) {
//			try {
//				gender = (String) jsonMeResponse.getString("gender");
//
//				if (gender.equalsIgnoreCase("male"))
//					gender = "M";
//				else if (gender.equalsIgnoreCase("female"))
//					gender = "F";
//				else
//					gender = "";
//				log.debug("gender from FB=" + gender);
//			} catch (Exception e) {
//			}
//		}
//
//		String language = null;
//		if (privacyOn) {
//			try {
//				language = (String) jsonMeResponse.getString("locale");
//				log.debug("language from FB=" + language);
//			} catch (Exception e) {
//			}
//		}
//
//		String dob = null;
//		if (privacyOn) {
//			try {
//				dob = (String) jsonMeResponse.getString("birthday");
//				log.debug("dob from FB=" + dob);
//				if (dob.equals("undefined"))
//					dob = null;
//			} catch (Exception e) {
//			}
//		}
//
//		String country = null;
//		String location = null;
//		if (privacyOn) {
//			try {
//				location = (String) jsonMeResponse.getString("location");
//				country = new JSONObject(location).getString("name");
//				log.debug("country from FB=" + country);
//				if (country.equals("undefined"))
//					country = null;
//			} catch (Exception e) {
//			}
//		}
//
//		String avatar = null;
//		if (privacyOn) {
//			try {
//				avatar = (String) jsonMeResponse.getString("picture");
//				log.debug("avatar from FB=" + avatar);
//			} catch (Exception e) {
//			}
//		}
//
//		// 24/05/2011
//
//		// FB user id / email -> AUP username
//		String userid = null;
//		String fbUserId = (String) properties
//				.getProperty(CaProperties.PROPERTY_PREFIX
//						+ "username.fb.userid");
//		log.debug("fbUserId = " + fbUserId);
//		if (fbUserId != null && fbUserId.equals("true"))
//			userid = id;
//		else
//			userid = email;
//
//		String aupUsername = null;
//		String hash = (String) properties
//				.getProperty(CaProperties.PROPERTY_PREFIX + "username.hash");
//		log.debug("hash = " + hash);
//		if (hash != null && hash.equals("true"))
//			aupUsername = AeSimpleMD5.MD5(userid);
//		else
//			aupUsername = userid;
//		log.debug("aupUsername =" + aupUsername);
//
//		if (userid == null) {
//
//			clientUrlParams = "status=ERROR&msg="
//					+ SSOLoginUtils.errorLoginFacebookMessageIdNotReturned;
//			log.debug("CLIENT redirect to:" + returnToClientUrlBack
//					+ clientUrlParams);
//
//			throw new ArgumentApplicationException(
//					SSOLoginUtils.errorLoginFacebookMessageIdNotReturned);
//
//		} else {
//
//			// verify if the user already exist with that username
//			// String username = userid;
//			User user = null;
//			if (loginUtils == null)
//				loginUtils = new SSOLoginUtils();
//			if (userProfileRemote != null) {
//				try {
//					user = userProfileRemote.getUserByUserName(aupUsername);
//				} catch (UserNotFoundException e) {
//					log.debug("User not found");
//				}
//
//				if (user != null) {
//					log.debug("Already found Facebook user:"
//							+ user.getUsername());
//
//					String cid = (String) request.getSession().getAttribute(
//							"cid");
//					if (cid == null)
//						cid = (String) MDC.get("cid");
//					log.debug("cid=" + cid);
//					if (cid == null || cid.equals("") || cid.length() < 1) {
//						try {
//							CaProperties.bindProperties(log);
//							properties = CaProperties.getProperties(log);
//							cid = properties
//									.getProperty(CaProperties.PROPERTY_PREFIX
//											+ "default.cid");
//							log.debug("default cid = " + cid);
//						} catch (IOException e) {
//							log.error(
//									"Lookup of \"AUP_config_properties\" failed",
//									e);
//							cid = "teamlife";
//						}
//						if (cid == null || cid.equals("") || cid.length() < 1)
//							cid = "teamlife";
//					}
//
//					// SSO SIGN IN
//					user = loginUtils.doSSOLogin(request, response, ssoModel,
//							aupUsername,
//							AeSimpleMD5.MD5(ConfigParameters.default_pwd), cid,
//							true);
//
//					if (user == null) // wrong username or password
//						throw new UserNotFoundException();
//
//					// update User From IUP basing on FB profile
//					// params
//					User u = createOrUpdateMobileUserFromIUP(request, response,
//							aupUsername, userid, user.getPassword(),
//							null, email, null, firstName, lastName, nickname,
//							gender, dob, country, language, null,
//							AccType.FBCONNECT, avatar);
//					if (u != null) {
//
//						Device device = (Device) request.getSession()
//								.getAttribute("device");
//						if (device == null)
//							device = MobileSignUp.getTempDevice();
//
//						String deviceid = null;
//						if (device != null) {
//							device.setUsername(aupUsername);
//							assignUserDevice(aupUsername, device);
//
//							deviceid = device.getImei();
//							if (deviceid == null)
//								deviceid = device.getWfid();
//
//							log.debug("Save Facebook Social OAuth2 Token");
//							try {
//								if (oauthSocialTokenUtils != null) {
//
//									oauthSocialTokenUtils.addOAuth2SocialToken(
//											u.getUserid().intValue(), userid,
//											SocialLogin.facebookProvider,
//											appId, fbtoken.getExpires(),
//											fbtoken.getAccessToken(),"");
//								}
//
//							} catch (Exception e) {
//								log.error("Error adding FB social token:"
//										+ e.getMessage());
//							}
//
//						}
//
//						clientUrlParams = createSuccessUserParams(user, userid,
//								clientId, deviceid, cid);
//
//						log.debug("CLIENT redirect to:" + returnToClientUrlBack
//								+ clientUrlParams);
//
//						// redirect to - pagina di sintesi
//						// redirectToClient(true, request, response,
//						// returnToClientUrlBack, clientUrlParams,
//						// "mthanks.jsp");
//
//						// AUTOMATIC REDIRECT
//						log.info("Automatic redirect to:"
//								+ returnToClientUrlBack + clientUrlParams);
//						response.sendRedirect(returnToClientUrlBack
//								+ clientUrlParams);
//					}
//
//				} else {
//
//					// FB SIGN UP
//					log.debug("NEW FB user to create:" + userid);
//
//					// if Facebook username (AUP alias) is already
//					// taken from an internal user -> msign up fails
//					// and error is returned
//					try {
//						usersAlias = userProfileRemote
//								.searchUsersByAlias(nickname);
//
//					} catch (Exception e) {
//
//					}
//					if (usersAlias != null && usersAlias.size() > 0)
//						throw new UserAliasExistsException();
//
//					if (email != null && nickname == null) {
//						int i = email.indexOf("@");
//						if (i != -1)
//							assignedAlias = email.substring(0, i);
//						log.debug("Trying to assign an alias to the user:"
//								+ assignedAlias);
//
//						if (email.contains("proxymail.facebook.com")
//								&& firstName != null && lastName != null)
//							assignedAlias = firstName.toLowerCase()
//									+ lastName.toLowerCase();
//
//						if (assignedAlias.length() >= 50)
//							throw new ArgumentApplicationException(
//									SSOLoginUtils.errorLoginFacebookAccountError);
//
//						try {
//							usersAlias = userProfileRemote
//									.searchUsersByAlias(assignedAlias);
//						} catch (Exception e) {
//						}
//						if (usersAlias == null || usersAlias.size() == 0) {
//							nickname = assignedAlias;
//							log.debug("Assigned alias=" + nickname);
//						} else
//							throw new ArgumentApplicationException(
//									SSOLoginUtils.errorLoginFacebookAccountError);
//					}
//
//					// create User From IUP basing on FB profile
//					// params
//					User u = createOrUpdateMobileUserFromIUP(request, response,
//							aupUsername, userid, UUID.randomUUID().toString().substring(0,13),
//							null, email, null, firstName, lastName, nickname,
//							gender, dob, country, language, null,
//							AccType.FBCONNECT, avatar);
//
//					// Add user device
//					if (u != null) {
//
//						String emailUser = u.getUsername();
//
//						Device device = (Device) request.getSession()
//								.getAttribute("device");
//						if (device == null)
//							device = MobileSignUp.getTempDevice();
//						String deviceid = null;
//						if (device != null) {
//							device.setUsername(aupUsername);
//							assignUserDevice(aupUsername, device);
//
//							deviceid = device.getImei();
//							if (deviceid == null)
//								deviceid = device.getWfid();
//
//							log.debug("Save Facebook Social OAuth2 Token");
//							try {
//								if (oauthSocialTokenUtils != null) {
//
//									oauthSocialTokenUtils.addOAuth2SocialToken(
//											u.getUserid().intValue(), userid,
//											SocialLogin.facebookProvider,
//											appId, fbtoken.getExpires(),
//											fbtoken.getAccessToken(),"");
//								}
//
//							} catch (Exception e) {
//								log.error("Error adding FB social token:"
//										+ e.getMessage());
//							}
//
//						}
//
//						// OLD flow redirect to mwelcome
//						// redirectToClient(true, request, response,
//						// returnToClientUrlBack, "status=OK&username="
//						// + aupUsername + "&login=" + userid
//						// + "&alias=" + nickname, "mwelcome.jsp");
//
//						// -> NEW flow return to client
//
//						log.debug(".. Redirecting to client:");
//
//						String cid = (String) MDC.get("cid");
//						log.debug("cid=" + cid);
//						if (cid == null || cid.equals("") || cid.length() < 1) {
//							try {
//								CaProperties.bindProperties(log);
//								properties = CaProperties.getProperties(log);
//								cid = properties
//										.getProperty(CaProperties.PROPERTY_PREFIX
//												+ "default.cid");
//								log.debug("default cid = " + cid);
//							} catch (IOException e) {
//								log.error(
//										"Lookup of \"AUP_config_properties\" failed",
//										e);
//								cid = "teamlife";
//							}
//							if (cid == null || cid.equals("")
//									|| cid.length() < 1)
//								cid = "teamlife";
//						}
//
//						// SSO Login
//						user = loginUtils.doSSOLogin(request, response,
//								ssoModel, aupUsername,
//								AeSimpleMD5.MD5(ConfigParameters.default_pwd),
//								cid, true);
//
//						if (user == null) // wrong username or password
//							throw new UserNotFoundException();
//
//						clientUrlParams = createSuccessUserParams(null,
//								aupUsername, clientId, deviceid, cid);
//
//						// AUTOMATIC REDIRECT
//						log.info("...Automatic redirect to "
//								+ returnToClientUrlBack + clientUrlParams);
//						response.sendRedirect(returnToClientUrlBack
//								+ clientUrlParams);
//
//					}
//
//				}
//
//			}
//
//		}
//
//		MDC.remove("cid");
//
//	}

}
