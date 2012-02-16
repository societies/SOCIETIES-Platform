package com.tilab.ca.platform.SSO.social.facebook.util;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.tilab.ca.platform.SSO.social.facebook.exceptions.StatusCode;


/**
 * 
 * @author UE014084
 * @deprecated since 1.0.3
 * @see com.tilab.ca.platform.commons.servlet.HttpServletRequestWrapper
 */
public abstract class HttpServletImpl extends HttpServlet implements StatusCode {

	/**
	 * 
	 */
	protected static final String METHOD_GET = "GET";

	/**
	 * 
	 */
	protected static final String PARAMETER_NAME_PREFIX = "p_";


	/**
	 * 
	 * @param servletMethodName
	 * @return String[]
	 */
	protected abstract String[] createRealParameterNames(String servletMethodName);

	/**
	 * Logger
	 */
	private static final Logger log = Logger.getLogger(HttpServletImpl.class);


	//http://hostname.com:80/mywebapp/servlet/MyServlet/a/b;c=123?d=789

	/**
	 * 
	 * @param request
	 * @param response
	 * @return HttpServletRequest
	 */
	protected HttpServletRequest fixCustomSintax(HttpServletRequest request, HttpServletResponse response) {
		String methodName = new Throwable().getStackTrace()[0].getMethodName();
		log.log(TraceLevel.TRACE, DefaultMessages.BEGIN+" "+methodName);
		if (METHOD_GET.equalsIgnoreCase(request.getMethod())) {//DELETE HEAD GET OPTIONS POST PUT TRACE
			//--> http://hostname.com:80/mywebapp/servlet/MyServlet/a/b;c=123
			String pathInfo = request.getPathInfo();// /a/b;c=123 --> GET with "/" sintax//ATTENZIONE!!! La gestione de "p1//p3" NON e' possibile.
			if (pathInfo!=null && pathInfo.length()>0){
				// GET with "/" sintax
				pathInfo = pathInfo.substring(1, pathInfo.length());//togliamo il "/" iniziale.
				String[] splittedPathInfo = pathInfo.split("/");
				String[] realParameterNames = createRealParameterNames(HttpServletUtil.getServletMethodName(request));
				for(int i=0;i<splittedPathInfo.length;i++){
					String[] requestValueParamStringArray = new String[1];
					requestValueParamStringArray[0]=splittedPathInfo[i];
					if (realParameterNames!=null && realParameterNames.length>0 && i<realParameterNames.length){
						//ATTENTION!!! con "request.setAttribute" modifichiamo la "request" di origine...
						request.setAttribute(realParameterNames[i], requestValueParamStringArray);//Please use this sintax: request.setAttribute(String key, String[] value);
					} else {
						//ATTENTION!!! con "request.setAttribute" modifichiamo la "request" di origine...
						request.setAttribute((PARAMETER_NAME_PREFIX+i), requestValueParamStringArray);//Please use this sintax: request.setAttribute(String key, String[] value);
					}
					requestValueParamStringArray = null;
				}
			}
		}
		log.log(TraceLevel.TRACE, DefaultMessages.END+" "+methodName);
		return request;
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @return HashMap
	 */
	protected HashMap getParametersAndAttributes(HttpServletRequest request, HttpServletResponse response){
		String methodName = new Throwable().getStackTrace()[0].getMethodName();
		log.log(TraceLevel.TRACE, DefaultMessages.BEGIN+" "+methodName);

		HashMap hashMap = new HashMap(0);

		request = fixCustomSintax(request, response);//GET with custom sintax...

		Enumeration paramNames = request.getParameterNames();//tutti quelli del form (<input>...</input>, GET/POST standard)
		while(paramNames.hasMoreElements()) {
			String paramName = (String)paramNames.nextElement();
			String[] paramValues = request.getParameterValues(paramName);
			hashMap.put(paramName, paramValues);//Please use this sintax: request.setAttribute(String key, String[] value);
		}

		Enumeration attributeEnumeration = request.getAttributeNames();//(setted programatically)
		for (; attributeEnumeration.hasMoreElements(); ) {
			String attributeName = (String)attributeEnumeration.nextElement();
			hashMap.put(attributeName, request.getAttribute(attributeName));//Please use this sintax: request.setAttribute(String key, Object value);//Attention! "value" can be a String or a String[] or a YourCustomObject...
		}

		log.log(TraceLevel.TRACE, DefaultMessages.END+" "+methodName);
		return hashMap;
	}

	/**
	 * 
	 */
	protected final void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String methodName = new Throwable().getStackTrace()[0].getMethodName();
		log.log(TraceLevel.TRACE, DefaultMessages.BEGIN+" "+methodName);
		doGetOrPostWrapper(request, response);
		log.log(TraceLevel.TRACE, DefaultMessages.END+" "+methodName);
	}

	/**
	 * 
	 */
	protected final void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String methodName = new Throwable().getStackTrace()[0].getMethodName();
		log.log(TraceLevel.TRACE, DefaultMessages.BEGIN+" "+methodName);
		doGetOrPostWrapper(request, response);
		log.log(TraceLevel.TRACE, DefaultMessages.END+" "+methodName);
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void doGetOrPostWrapper(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String methodName = new Throwable().getStackTrace()[0].getMethodName();
		log.log(TraceLevel.TRACE, DefaultMessages.BEGIN+" "+methodName);
		request.setCharacterEncoding("UTF-8");
		doGetOrPost(request, response, request.getSession());
		log.log(TraceLevel.TRACE, DefaultMessages.END+" "+methodName);
	}

	/**
	 * This method handles both GET and POST requests.
	 * This method is called by the servlet container to process a GET or POST request.
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 * @param request HttpServletRequest
	 * @param response HttpServletResponse
	 * @param currentSession HttpSession
	 */
	protected abstract void doGetOrPost(HttpServletRequest request, HttpServletResponse response, HttpSession currentSession) throws ServletException, IOException;


}
