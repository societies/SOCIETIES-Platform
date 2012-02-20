/*********************************************
  Context Awareness Platform
 ********************************************
  Copyright (c) 2006-2010 Telecom Italia S.p.A
 ******************************************** 
  $Id: StatusCode.java 6992 2010-02-24 18:39:40Z papurello $
  $HeadURL: http://163.162.93.163:90/svn/ca/platform/commons-ca/branches/rel-1_0-ev/src/com/tilab/ca/platform/commons/decode/StatusCode.java $
 *********************************************
 */
package com.tilab.ca.platform.SSO.social.facebook.exceptions;

/**
 * Server status codes; see RFC 2068.
 * <br/>
 * Contiene tutte le costanti inerenti i codici di errore.
 * Per maggiori informazioni: <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html">http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html</a>
 * <br/><br/>
 * 
 * <table border="1">
 * <tr><td>Status Code</td><td>Status Information</td></tr>
 * <tr><td>1XX</td><td>INFO</td></tr>
 * <tr><td>2XX</td><td>SUCCESSFUL</td></tr>
 * <tr><td>3XX</td><td>REDIRECTION</td></tr>
 * <tr><td>4XX</td><td>CLIENT ERROR</td></tr>
 * <tr><td>5XX</td><td>SERVER ERROR</td></tr>
 * </table>
 * 
 * @author UE014084
 */
public interface StatusCode {
	
	/**
	 * CA Status message: "OK"
	 */
	public static final String STATUS_OK = "OK";
	
	/**
	 * CA Status message: "ERROR"
	 */
	public static final String STATUS_ERROR = "ERROR";
	
	
	
	/**
	 * INFO<br/>
	 * Status code (100) indicating the client can continue.
	 */
	public static final int SC_100_CONTINUE = 100;

	/**
	 * INFO<br/>
	 * Status code (101) indicating the server is switching protocols according
	 * to Upgrade header.
	 */
	public static final int SC_101_SWITCHING_PROTOCOLS = 101;

	/**
	 * SUCCESSFUL<br/>
	 * Status code (200) indicating the request succeeded normally.
	 */
	public static final int SC_200_OK = 200;

	/**
	 * SUCCESSFUL<br/>
	 * Status code (201) indicating the request succeeded and created a new
	 * resource on the server.
	 */
	public static final int SC_201_CREATED = 201;

	/**
	 * SUCCESSFUL<br/>
	 * Status code (202) indicating that a request was accepted for processing,
	 * but was not completed.
	 */
	public static final int SC_202_ACCEPTED = 202;

	/**
	 * SUCCESSFUL<br/>
	 * Status code (203) indicating that the meta information presented by the
	 * client did not originate from the server.
	 */
	public static final int SC_203_NON_AUTHORITATIVE_INFORMATION = 203;

	/**
	 * SUCCESSFUL<br/>
	 * Status code (204) indicating that the request succeeded but that there
	 * was no new information to return.
	 */
	public static final int SC_204_NO_CONTENT = 204;

	/**
	 * SUCCESSFUL<br/>
	 * Status code (205) indicating that the agent <em>SHOULD</em> reset the
	 * document view which caused the request to be sent.
	 */
	public static final int SC_205_RESET_CONTENT = 205;

	/**
	 * SUCCESSFUL<br/>
	 * Status code (206) indicating that the server has fulfilled the partial
	 * GET request for the resource.
	 */
	public static final int SC_206_PARTIAL_CONTENT = 206;

	/**
	 * REDIRECTION<br/>
	 * Status code (300) indicating that the requested resource corresponds to
	 * any one of a set of representations, each with its own specific location.
	 */
	public static final int SC_300_MULTIPLE_CHOICES = 300;

	/**
	 * REDIRECTION<br/>
	 * Status code (301) indicating that the resource has permanently moved to a
	 * new location, and that future references should use a new URI with their
	 * requests.
	 */
	public static final int SC_301_MOVED_PERMANENTLY = 301;

	/**
	 * REDIRECTION<br/>
	 * Status code (302) indicating that the resource has temporarily moved to
	 * another location, but that future references should still use the
	 * original URI to access the resource.
	 * 
	 * This definition is being retained for backwards compatibility. SC_FOUND
	 * is now the preferred definition.
	 */
	public static final int SC_302_MOVED_TEMPORARILY = 302;

	/**
	 * REDIRECTION<br/>
	 * Status code (302) indicating that the resource reside temporarily under a
	 * different URI. Since the redirection might be altered on occasion, the
	 * client should continue to use the Request-URI for future
	 * requests.(HTTP/1.1) To represent the status code (302), it is recommended
	 * to use this variable.
	 */
	public static final int SC_302_FOUND = 302;

	/**
	 * REDIRECTION<br/>
	 * Status code (303) indicating that the response to the request can be
	 * found under a different URI.
	 */
	public static final int SC_303_SEE_OTHER = 303;

	/**
	 * REDIRECTION<br/>
	 * Status code (304) indicating that a conditional GET operation found that
	 * the resource was available and not modified.
	 */
	public static final int SC_304_NOT_MODIFIED = 304;

	/**
	 * REDIRECTION<br/>
	 * Status code (305) indicating that the requested resource <em>MUST</em>
	 * be accessed through the proxy given by the <code><em>Location</em></code>
	 * field.
	 */
	public static final int SC_305_USE_PROXY = 305;
	
	/**
	 * REDIRECTION<br/>
	 * Status code (307) indicating that the requested resource resides
	 * temporarily under a different URI. The temporary URI <em>SHOULD</em> be
	 * given by the <code><em>Location</em></code> field in the response.
	 */
	public static final int SC_307_TEMPORARY_REDIRECT = 307;

	/**
	 * CLIENT ERROR<br/>
	 * Status code (400) indicating the request sent by the client was
	 * syntactically incorrect.
	 */
	public static final int SC_400_BAD_REQUEST = 400;

	/**
	 * CLIENT ERROR<br/>
	 * Status code (401) indicating that the request requires HTTP
	 * authentication.
	 */
	public static final int SC_401_UNAUTHORIZED = 401;

	/**
	 * CLIENT ERROR<br/>
	 * Status code (402) reserved for future use.
	 */
	public static final int SC_402_PAYMENT_REQUIRED = 402;

	/**
	 * CLIENT ERROR<br/>
	 * Status code (403) indicating the server understood the request but
	 * refused to fulfill it.
	 */
	public static final int SC_403_FORBIDDEN = 403;

	/**
	 * CLIENT ERROR<br/>
	 * Status code (404) indicating that the requested resource is not
	 * available.
	 */
	public static final int SC_404_NOT_FOUND = 404;

	/**
	 * CLIENT ERROR<br/>
	 * Status code (405) indicating that the method specified in the
	 * <code><em>Request-Line</em></code> is not allowed for the resource
	 * identified by the <code><em>Request-URI</em></code>.
	 */
	public static final int SC_405_METHOD_NOT_ALLOWED = 405;

	/**
	 * CLIENT ERROR<br/>
	 * Status code (406) indicating that the resource identified by the request
	 * is only capable of generating response entities which have content
	 * characteristics not acceptable according to the accept headers sent in
	 * the request.
	 */
	public static final int SC_406_NOT_ACCEPTABLE = 406;

	/**
	 * CLIENT ERROR<br/>
	 * Status code (407) indicating that the client <em>MUST</em> first
	 * authenticate itself with the proxy.
	 */
	public static final int SC_407_PROXY_AUTHENTICATION_REQUIRED = 407;

	/**
	 * CLIENT ERROR<br/>
	 * Status code (408) indicating that the client did not produce a request
	 * within the time that the server was prepared to wait.
	 */
	public static final int SC_408_REQUEST_TIMEOUT = 408;

	/**
	 * CLIENT ERROR<br/>
	 * Status code (409) indicating that the request could not be completed due
	 * to a conflict with the current state of the resource.
	 */
	public static final int SC_409_CONFLICT = 409;

	/**
	 * CLIENT ERROR<br/>
	 * Status code (410) indicating that the resource is no longer available at
	 * the server and no forwarding address is known. This condition
	 * <em>SHOULD</em> be considered permanent.
	 */
	public static final int SC_410_GONE = 410;

	/**
	 * CLIENT ERROR<br/>
	 * Status code (411) indicating that the request cannot be handled without a
	 * defined <code><em>Content-Length</em></code>.
	 */
	public static final int SC_411_LENGTH_REQUIRED = 411;

	/**
	 * CLIENT ERROR<br/>
	 * Status code (412) indicating that the precondition given in one or more
	 * of the request-header fields evaluated to false when it was tested on the
	 * server.
	 */
	public static final int SC_412_PRECONDITION_FAILED = 412;

	/**
	 * CLIENT ERROR<br/>
	 * Status code (413) indicating that the server is refusing to process the
	 * request because the request entity is larger than the server is willing
	 * or able to process.
	 */
	public static final int SC_413_REQUEST_ENTITY_TOO_LARGE = 413;

	/**
	 * CLIENT ERROR<br/>
	 * Status code (414) indicating that the server is refusing to service the
	 * request because the <code><em>Request-URI</em></code> is longer than
	 * the server is willing to interpret.
	 */
	public static final int SC_414_REQUEST_URI_TOO_LONG = 414;

	/**
	 * CLIENT ERROR<br/>
	 * Status code (415) indicating that the server is refusing to service the
	 * request because the entity of the request is in a format not supported by
	 * the requested resource for the requested method.
	 */
	public static final int SC_415_UNSUPPORTED_MEDIA_TYPE = 415;

	/**
	 * CLIENT ERROR<br/>
	 * Status code (416) indicating that the server cannot serve the requested
	 * byte range.
	 */
	public static final int SC_416_REQUESTED_RANGE_NOT_SATISFIABLE = 416;

	/**
	 * CLIENT ERROR<br/>
	 * Status code (417) indicating that the server could not meet the
	 * expectation given in the Expect request header.
	 */
	public static final int SC_417_EXPECTATION_FAILED = 417;

	/**
	 * SERVER ERROR<br/>
	 * Status code (500) indicating an error inside the HTTP server which
	 * prevented it from fulfilling the request.
	 */
	public static final int SC_500_INTERNAL_SERVER_ERROR = 500;

	/**
	 * SERVER ERROR<br/>
	 * Status code (501) indicating the HTTP server does not support the
	 * functionality needed to fulfill the request.
	 */
	public static final int SC_501_NOT_IMPLEMENTED = 501;

	/**
	 * SERVER ERROR<br/>
	 * Status code (502) indicating that the HTTP server received an invalid
	 * response from a server it consulted when acting as a proxy or gateway.
	 */
	public static final int SC_502_BAD_GATEWAY = 502;

	/**
	 * SERVER ERROR<br/>
	 * Status code (503) indicating that the HTTP server is temporarily
	 * overloaded, and unable to handle the request.
	 */
	public static final int SC_503_SERVICE_UNAVAILABLE = 503;

	/**
	 * SERVER ERROR<br/>
	 * Status code (504) indicating that the server did not receive a timely
	 * response from the upstream server while acting as a gateway or proxy.
	 */
	public static final int SC_504_GATEWAY_TIMEOUT = 504;

	/**
	 * SERVER ERROR<br/>
	 * Status code (505) indicating that the server does not support or refuses
	 * to support the HTTP protocol version that was used in the request
	 * message.
	 */
	public static final int SC_505_HTTP_VERSION_NOT_SUPPORTED = 505;

}
