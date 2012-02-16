package com.tilab.ca.platform.SSO.social.facebook.util;

public class ConfigParameters {
	
  //Default configuration parameters

  public static String PROVIDER_NAME = "OpenID4J";
  
  public static String PROVIDER_VERSION = "1.0.0";
  
  public static String return_to_url = "https://beta.teamlife.it/SSO/openid4j";
  
  public static String mreturn_to_url = "https://beta.teamlife.it/SSO/mopenid4j";
  
  public static String iup_base_url = "http://192.168.93.162:8080/IUP";
  
  public static String aup_base_url = "http://192.168.93.162:8080/AUP";
  
  public static int connTimeout_ms = 4000;
  public static int readTimeout_ms = 4000;
  
  public static String default_pwd = "8080";
  public static String defaultCid = "teamlife";

  public static String google_url = "https://www.google.com/accounts/o8/id";
  
  public static String google_alias = "ext1";
  
  public static String virgilio_url = "http://oidprov.alice.it/oidprov/xrds.oip";
  
  public static String virgilio_alias = "ext1";
  
  public static String yahoo_url = "http://open.login.yahooapis.com/openid20/www.yahoo.com/xrds";
  
  public static String yahoo_alias = "ax";
  
  public static String proxy_host = null;
	  //"127.0.0.1";
  
  public static int proxy_port = 3128;
  
  public static String proxy_usr = null;
  
  public static String proxy_pwd = null;
  
  //public static String non_proxy_hosts = null;	
    
}
