package com.tilab.ca.platform.SSO.social.facebook.util;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.servlet.ServletRequestWrapper;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


/**
 * Provides a convenient implementation of the HttpServletRequest interface that
 * can be subclassed by developers wishing to adapt the request to a Servlet.
 * This class implements the Wrapper or Decorator pattern. Methods default to
 * calling through to the wrapped request object.
 * @see javax.servlet.http.HttpServletRequest
 * @see com.tilab.ca.platform.commons.servlet.ServletRequestWrapper
 * @since v 2.3
 * @author UE014084
 */
public class HttpServletRequestWrapper extends ServletRequestWrapper implements HttpServletRequest {
	
	/**
	 * 
	 */
	private static final String METHOD_GET = "GET";
	
	/**
	 * 
	 */
	public static final String PARAMETER_NAME_PREFIX = "p_";

	/**
	 * 
	 */
	private String actionCommand=null;
	
	/**
	 * 
	 */
	private String[] realParameterNames=null;

	//-------------------------------------------------------

	/**
	 * Constructs a request object wrapping the given request.
	 * @throws java.lang.IllegalArgumentException
	 *             if the request is null
	 */
	public HttpServletRequestWrapper(HttpServletRequest request, String[] realParameterNames) {
		super(request);
		this.actionCommand=HttpServletUtil.getServletMethodName(this._getHttpServletRequest());
		this.realParameterNames = realParameterNames;
		this.fixCustomSintax();
	}

	/**
	 * @return
	 */
	private HttpServletRequest _getHttpServletRequest() {
		return (HttpServletRequest) super.getRequest();
	}

	/**
	 * The default behavior of this method is to return getAuthType() on the
	 * wrapped request object.
	 */

	public String getAuthType() {
		return this._getHttpServletRequest().getAuthType();
	}

	/**
	 * The default behavior of this method is to return getCookies() on the
	 * wrapped request object.
	 */
	public Cookie[] getCookies() {
		return this._getHttpServletRequest().getCookies();
	}

	/**
	 * The default behavior of this method is to return getDateHeader(String
	 * name) on the wrapped request object.
	 */
	public long getDateHeader(String name) {
		return this._getHttpServletRequest().getDateHeader(name);
	}

	/**
	 * The default behavior of this method is to return getHeader(String name)
	 * on the wrapped request object.
	 */
	public String getHeader(String name) {
		return this._getHttpServletRequest().getHeader(name);
	}

	/**
	 * The default behavior of this method is to return getHeaders(String name)
	 * on the wrapped request object.
	 */
	public Enumeration getHeaders(String name) {
		return this._getHttpServletRequest().getHeaders(name);
	}

	/**
	 * The default behavior of this method is to return getHeaderNames() on the
	 * wrapped request object.
	 */

	public Enumeration getHeaderNames() {
		return this._getHttpServletRequest().getHeaderNames();
	}

	/**
	 * The default behavior of this method is to return getIntHeader(String
	 * name) on the wrapped request object.
	 */

	public int getIntHeader(String name) {
		return this._getHttpServletRequest().getIntHeader(name);
	}

	/**
	 * The default behavior of this method is to return getMethod() on the
	 * wrapped request object.
	 */
	public String getMethod() {
		return this._getHttpServletRequest().getMethod();
	}

	/**
	 * The default behavior of this method is to return getPathInfo() on the
	 * wrapped request object.
	 */
	public String getPathInfo() {
		return this._getHttpServletRequest().getPathInfo();
	}

	/**
	 * The default behavior of this method is to return getPathTranslated() on
	 * the wrapped request object.
	 */

	public String getPathTranslated() {
		return this._getHttpServletRequest().getPathTranslated();
	}

	/**
	 * The default behavior of this method is to return getContextPath() on the
	 * wrapped request object.
	 */
	public String getContextPath() {
		return this._getHttpServletRequest().getContextPath();
	}

	/**
	 * The default behavior of this method is to return getQueryString() on the
	 * wrapped request object.
	 */
	public String getQueryString() {
		return this._getHttpServletRequest().getQueryString();
	}

	/**
	 * The default behavior of this method is to return getRemoteUser() on the
	 * wrapped request object.
	 */
	public String getRemoteUser() {
		return this._getHttpServletRequest().getRemoteUser();
	}

	/**
	 * The default behavior of this method is to return isUserInRole(String
	 * role) on the wrapped request object.
	 */
	public boolean isUserInRole(String role) {
		return this._getHttpServletRequest().isUserInRole(role);
	}

	/**
	 * The default behavior of this method is to return getUserPrincipal() on
	 * the wrapped request object.
	 */
	public java.security.Principal getUserPrincipal() {
		return this._getHttpServletRequest().getUserPrincipal();
	}

	/**
	 * The default behavior of this method is to return getRequestedSessionId()
	 * on the wrapped request object.
	 */
	public String getRequestedSessionId() {
		return this._getHttpServletRequest().getRequestedSessionId();
	}

	/**
	 * The default behavior of this method is to return getRequestURI() on the
	 * wrapped request object.
	 */
	public String getRequestURI() {
		return this._getHttpServletRequest().getRequestURI();
	}

	/**
	 * The default behavior of this method is to return getRequestURL() on the
	 * wrapped request object.
	 */
	public StringBuffer getRequestURL() {
		return this._getHttpServletRequest().getRequestURL();
	}

	/**
	 * The default behavior of this method is to return getServletPath() on the
	 * wrapped request object.
	 */
	public String getServletPath() {
		return this._getHttpServletRequest().getServletPath();
	}

	/**
	 * The default behavior of this method is to return getSession(boolean
	 * create) on the wrapped request object.
	 */
	public HttpSession getSession(boolean create) {
		return this._getHttpServletRequest().getSession(create);
	}

	/**
	 * The default behavior of this method is to return getSession() on the
	 * wrapped request object.
	 */
	public HttpSession getSession() {
		return this._getHttpServletRequest().getSession();
	}

	/**
	 * The default behavior of this method is to return
	 * isRequestedSessionIdValid() on the wrapped request object.
	 */

	public boolean isRequestedSessionIdValid() {
		return this._getHttpServletRequest().isRequestedSessionIdValid();
	}

	/**
	 * The default behavior of this method is to return
	 * isRequestedSessionIdFromCookie() on the wrapped request object.
	 */
	public boolean isRequestedSessionIdFromCookie() {
		return this._getHttpServletRequest().isRequestedSessionIdFromCookie();
	}

	/**
	 * The default behavior of this method is to return
	 * isRequestedSessionIdFromURL() on the wrapped request object.
	 */
	public boolean isRequestedSessionIdFromURL() {
		return this._getHttpServletRequest().isRequestedSessionIdFromURL();
	}

	/**
	 * The default behavior of this method is to return
	 * isRequestedSessionIdFromUrl() on the wrapped request object.
	 * @deprecated
	 */
	public boolean isRequestedSessionIdFromUrl() {
		return this._getHttpServletRequest().isRequestedSessionIdFromUrl();
	}
	
	//----------------------
	
	/**
	 * 
	 */
	protected void fixCustomSintax() {
		if (METHOD_GET.equalsIgnoreCase(this.getMethod())) {//DELETE HEAD GET OPTIONS POST PUT TRACE
			//--> http://hostname.com:80/mywebapp/servlet/MyServlet/a/b;c=123
			String pathInfo = this.getPathInfo();// /a/b;c=123 --> GET with "/" sintax//ATTENZIONE!!! La gestione de "p1//p3" NON e' possibile.
			if (pathInfo!=null && pathInfo.length()>0){
				// GET with "/" sintax
				pathInfo = pathInfo.substring(1, pathInfo.length());//togliamo il "/" iniziale.
				String[] splittedPathInfo = pathInfo.split("/");
				for(int i=0;i<splittedPathInfo.length;i++){
					String[] requestValueParamStringArray = new String[1];
					requestValueParamStringArray[0]=splittedPathInfo[i];
					if (realParameterNames!=null && realParameterNames.length>0 && i<realParameterNames.length){
						//ATTENTION!!! con "request.setAttribute" modifichiamo la "request" di origine...
						this.setAttribute(realParameterNames[i], requestValueParamStringArray);//Please use this sintax: request.setAttribute(String key, String[] value);
					} else {
						//ATTENTION!!! con "request.setAttribute" modifichiamo la "request" di origine...
						this.setAttribute((PARAMETER_NAME_PREFIX+i), requestValueParamStringArray);//Please use this sintax: request.setAttribute(String key, String[] value);
					}
					requestValueParamStringArray = null;
				}
				splittedPathInfo=null;
			}
			pathInfo=null;
		}
	}
	
	/**
	 * @param name
	 * @return
	 */
	public String getParameterOrAttribute(String name) {
		String value = this.getParameter(name);
		if(value==null){
			Object obj = this.getAttribute(name);
			String[] tmp = null;
			if(obj instanceof String[]){
				tmp = (String[])obj;
			} else {
				tmp = new String[]{ ((String)obj) };
			}
			value = (tmp==null || tmp.length<=0)?(null):(tmp[0]);
			tmp = null;
			obj = null;
		}
		return value;
	}

	/**
	 * @return
	 */
	public Map getParameterOrAttributeMap() {
		Map map = this.getParameterMap();
		if(map==null){
			map = new HashMap(0);
		}
		Enumeration attributeEnumeration = this.getAttributeNames();
		for (; attributeEnumeration.hasMoreElements(); ) {
			String attributeName = (String)attributeEnumeration.nextElement();
			map.put(attributeName, this.getAttribute(attributeName));//Please use this sintax: request.setAttribute(String key, Object value);//Attention! "value" can be a String or a String[] or a YourCustomObject...
			attributeName = null;
		}
		attributeEnumeration = null;
		return map;
	}

	/**
	 * @return
	 */
	public Enumeration getParameterOrAttributeNames() {
		Vector names = new Vector(0);
		
		Enumeration paramNames = this.getParameterNames();//tutti quelli del form (<input>...</input>, GET/POST standard)
		while(paramNames.hasMoreElements()) {
			names.add((String)paramNames.nextElement());
		}
		paramNames=null;
		
		Enumeration attributeNames = this.getAttributeNames();//(setted programatically)
		while(attributeNames.hasMoreElements()){//for (; attributeEnumeration.hasMoreElements(); ) {
			names.add((String)attributeNames.nextElement());
		}
		attributeNames=null;
		
		return names.elements();
	}

	/**
	 * @param name
	 * @return
	 */
	public Object getParameterOrAttributeValues(String name) {
		Object values = this.getParameterValues(name);//String[]
		if(values==null){
			values = this.getAttribute(name);//String[] or yourCustomObject[]
		}
		return values;
	}

}
