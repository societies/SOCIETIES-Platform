
package com.tilab.ca.platform.SSO.social.facebook.util;

import javax.servlet.http.HttpServletRequest;


public abstract class HttpServletUtil {
	
	/**
	 * Don't let anyone instantiate this class.
	 */
	private HttpServletUtil() {
		//no code here
	}

	//http://hostname.com:80/mywebapp/servlet/MyServlet/a/b;c=123?d=789

	/**
	 * ServletMethodName (from "http://hostname.com:80/mywebapp/servlet/MyServlet/a/b;c=123?d=789".. it returns "MyServlet")
	 * @param request HttpServletRequest
	 * @return "MyServlet" i.e. your servlet name/method 
	 */
	public static String getServletMethodName(HttpServletRequest request) {
		String servletMethodName = null;
		if (request!=null && request.getServletPath()!=null && request.getServletPath().length()>0){
			servletMethodName = (request.getServletPath()).substring(1, (request.getServletPath()).length());
			servletMethodName = (servletMethodName.split("/"))[((servletMethodName.split("/")).length-1)];	
		}

		return servletMethodName;
	}

	/**
	 * Returns the original URL, reconstructed from more basic components available to the servlet.
	 * @param request HttpServletRequest
	 * @return http://hostname.com:80/mywebapp/servlet/MyServlet/a/b;c=123?d=789 i.e. the original requesting URL (NOT DECODED by the web container)
	 * @see #getFullUrl(HttpServletRequest)
	 */
	public static String getExplicitFullUrl(HttpServletRequest request) {
		// Reconstruct original requesting URL: http://hostname.com:80/mywebapp/servlet/MyServlet
		StringBuffer fullUrl = new StringBuffer("");
		fullUrl.append(request.getScheme()).append("://").append(request.getServerName()).append(":").append(request.getServerPort());
		fullUrl.append(request.getContextPath()).append(request.getServletPath());
		//fullUrl e fullUrl.toString() saranno sempre diverse da null (ammesso che la request in entrata sia un oggetto valido...)
		if (request.getPathInfo() != null) {//fullUrl!=null &&
			fullUrl.append(request.getPathInfo());// /a/b;c=123
		}
		if (request.getQueryString() != null) {//fullUrl!=null &&
			fullUrl.append("?").append(request.getQueryString());// ?d=789
		}

		return fullUrl.toString();//fullUrl
	}

	/**
	 * The most convenient method for reconstructing the original URL.
	 * @param request HttpServletRequest
	 * @return http://hostname.com/mywebapp/servlet/MyServlet/a/b;c=123?d=789 i.e. the original requesting URL (NOT DECODED by the web container)
	 * @see #getExplicitFullUrl(HttpServletRequest)
	 */
	public static String getFullUrl(HttpServletRequest request) {
		String fullUrl = request.getRequestURL().toString();
		String queryString = request.getQueryString();// d=789
		if (fullUrl!=null && queryString != null) {
			fullUrl+= "?"+queryString;
		}
		return fullUrl;
	}

	/**
	 * Returns a relative url, from your appname (without protocol, hostname, port).
	 * @param request HttpServletRequest
	 * @return /mywebapp/servlet/MyServlet/a/b;c=123?d=789 i.e. a String (NOT DECODED by the web container)
	 */
	public static String getPartialUrl(HttpServletRequest request) {
		String partialUrl = request.getRequestURI();// (NOT decoded by the web container)
		String queryString = request.getQueryString();// d=789
		if (partialUrl!=null && queryString != null) {
			partialUrl+= "?"+queryString;
		}
		return partialUrl;
	}

}
