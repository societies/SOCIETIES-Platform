/* Copyright (c) 2009 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensocial;

import net.oauth.http.HttpMessage;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.opensocial.auth.AuthScheme;
import org.opensocial.http.HttpClient;
import org.opensocial.http.HttpResponseMessage;
import org.opensocial.providers.Provider;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * OpenSocial RESTful client supporting both the RPC and REST protocols defined
 * in the OpenSocial specification as well as two- and three-legged OAuth for
 * authentication. This class handles the transmission of requests to
 * OpenSocial containers such as orkut and MySpace. Typical usage:
 * <pre>
 *   Client client = new Client(new OrkutProvider(),
         new OAuth2LeggedScheme(ORKUT_KEY, ORKUT_SECRET, ORKUT_ID));
     Response response = client.send(PeopleService.getViewer());
 * </pre>
 * The send method either returns a single {@link Response} or a {@link Map} of
 * Response objects mapped to ID strings. The data returned from the container
 * can be extracted from these objects.
 *
 * @author Jason Cooper
 */
public class Client {

  private Provider provider;
  private AuthScheme authScheme;
  private HttpClient httpClient;

  private static Logger logger = Logger.getLogger("org.opensocial.client");

  /**
   * Creates and returns a new {@link Client} associated with the passed
   * {@link Provider} and {@link AuthScheme}.
   *
   * @param provider   Provider to associate with new Client
   * @param authScheme AuthScheme to associate with new Client
   */
  public Client(Provider provider, AuthScheme authScheme) {
    this.provider = provider;
    this.authScheme = authScheme;
    this.httpClient = new HttpClient();
  }

  /**
   * Returns the associated {@link Provider}.
   */
  public Provider getProvider() {
    return provider;
  }

  /**
   * Returns the associated {@link AuthScheme}.
   */
  public AuthScheme getAuthScheme() {
    return authScheme;
  }

  /**
   * Submits the passed {@link Request} to the associated {@link Provider} and
   * returns the container's response data as a {@link Response} object.
   *
   * @param  request Request object (typically returned from static methods in
   *                 service classes) encapsulating all request data including
   *                 endpoint, HTTP method, and any required parameters
   * @return         Response object encapsulating the response data returned
   *                 by the container
   *
   * @throws RequestException if the passed request cannot be serialized, the
   *                          container returns an error code, or the response
   *                          cannot be parsed
   * @throws IOException      if an I/O error prevents a connection from being
   *                          opened or otherwise causes request transmission
   *                          to fail
   */
  public Response send(Request request) throws RequestException, IOException {
    final String KEY = "key";

    Map<String, Request> requests = new HashMap<String, Request>();
    requests.put(KEY, request);

    Map<String, Response> responses = send(requests);

    return responses.get(KEY);
  }

  /**
   * Submits the passed {@link Map} of {@link Request}s to the associated
   * {@link Provider} and returns the container's response data as a Map of
   * {@link Response} objects mapped to the same IDs as the passed requests. If
   * the associated provider supports the OpenSocial RPC protocol, only one
   * HTTP request is sent; otherwise, one HTTP request is executed per
   * container request.
   *
   * @param  requests Map of Request objects (typically returned from static
   *                  methods in service classes) to ID strings; each object
   *                  encapsulates the data for a single container request
   *                  such as fetching the viewer or creating an activity.
   * @return          Map of Response objects, each encapsulating the response
   *                  data returned by the container for a single request, to
   *                  the associated ID strings in the passed Map of Request
   *                  objects
   *
   * @throws RequestException if the passed request cannot be serialized, the
   *                          container returns an error code, or the response
   *                          cannot be parsed
   * @throws IOException      if an I/O error prevents a connection from being
   *                          opened or otherwise causes request transmission
   *                          to fail
   */
  public Map<String, Response> send(Map<String, Request> requests) throws
      RequestException, IOException {
    if (requests.size() == 0) {
      throw new RequestException("Request queue is empty");
    }

    Map<String, Response> responses = new HashMap<String, Response>();

    if (provider.getRpcEndpoint() != null) {
      responses = submitRpc(requests);
    } else if (provider.getRestEndpoint() != null) {
      for (Map.Entry<String, Request> entry : requests.entrySet()) {
        Request request = entry.getValue();

        provider.preRequest(request);

        Response response = submitRestRequest(request);
        responses.put(entry.getKey(), response);

        provider.postRequest(request, response);
      }
    } else {
      throw new RequestException("Provider has no REST or RPC endpoint set");
    }

    return responses;
  }

  private Map<String, Response> submitRpc(Map<String, Request> requests) throws
      RequestException, IOException {
    Map<String, String> requestHeaders = new HashMap<String, String>();
    requestHeaders.put(HttpMessage.CONTENT_TYPE, provider.getContentType());

    HttpMessage message = authScheme.getHttpMessage(provider, "POST",
        buildRpcUrl(), requestHeaders, buildRpcPayload(requests));

    HttpResponseMessage responseMessage = httpClient.execute(message);

    logger.finest(buildLogRecord(requests, responseMessage));

    Map<String, Response> responses = Response.parseRpcResponse(requests,
        responseMessage, provider.getVersion());

    return responses;
  }

  private Response submitRestRequest(Request request) throws RequestException,
      IOException{
    Map<String, String> requestHeaders = new HashMap<String, String>();
    if (request.getContentType() != null) {
      requestHeaders.put(HttpMessage.CONTENT_TYPE, request.getContentType());
    } else {
      requestHeaders.put(HttpMessage.CONTENT_TYPE, provider.getContentType());
    }

    HttpMessage message = authScheme.getHttpMessage(provider,
        request.getRestMethod(), buildRestUrl(request), requestHeaders,
        buildRestPayload(request));

    HttpResponseMessage responseMessage = httpClient.execute(message);

    logger.finest(buildLogRecord(request, responseMessage));

    Response response = Response.parseRestResponse(request, responseMessage,
        provider.getVersion());

    return response;
  }

  private String buildRpcUrl() {
    StringBuilder builder = new StringBuilder(provider.getRpcEndpoint());

    // Remove trailing forward slash
    if (builder.charAt(builder.length() - 1) == '/') {
      builder.deleteCharAt(builder.length() - 1);
    }

    return builder.toString();
  }

  private byte[] buildRpcPayload(Map<String, Request> requests) {
    JSONArray requestArray = new JSONArray();
    for (Map.Entry<String, Request> requestEntry : requests.entrySet()) {
      JSONObject request = new JSONObject();
      request.put("id", requestEntry.getKey());
      request.put("method", requestEntry.getValue().getRpcMethod());

      JSONObject requestParams = new JSONObject();
      for (Map.Entry<String, Object> parameter :
          requestEntry.getValue().getRpcPayloadParameters().entrySet()) {
        requestParams.put(parameter.getKey(), parameter.getValue());
      }

      request.put("params", requestParams);
      requestArray.add(request);
    }

    try {
      return requestArray.toJSONString().getBytes("UTF-8");
    } catch (UnsupportedEncodingException e) {
      return null;
    }
  }

  private String buildRestUrl(Request request) {
    StringBuilder builder = new StringBuilder(provider.getRestEndpoint());
    String[] components = request.getRestUrlTemplate().split("/");

    for (String component : components) {
      if (component.startsWith("{") && component.endsWith("}")) {
        String tag = component.substring(1, component.length()-1);

        if (request.getComponent(tag) != null) {
          builder.append(request.getComponent(tag));
          builder.append("/");
        }
      } else {
        builder.append(component);
        builder.append("/");
      }
    }

    // Remove trailing forward slash
    builder.deleteCharAt(builder.length() - 1);

    // Append query string parameters
    Map<String, String> parameters = request.getRestQueryStringParameters();
    if (parameters != null && parameters.size() > 0) {
      boolean runOnce = false;

      for (Map.Entry<String, String> parameter: parameters.entrySet()) {
        if (!runOnce) {
          builder.append("?");
          runOnce = true;
        } else {
          builder.append("&");
        }

        try {
          builder.append(URLEncoder.encode(parameter.getKey(), "UTF-8"));
          builder.append("=");
          builder.append(URLEncoder.encode(parameter.getValue(), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
          // Ignore
        }
      }
    }

    return builder.toString();
  }

  private byte[] buildRestPayload(Request request) {
    if (request.getCustomPayload() != null) {
      return request.getCustomPayload();
    }

    Map<String, Object> parameters = request.getRestPayloadParameters();
    if (parameters == null || parameters.size() == 0) {
      return null;
    }

    JSONObject payload = new JSONObject();
    for (Map.Entry<String, Object> parameter : parameters.entrySet()) {
      payload.put(parameter.getKey(), parameter.getValue());
    }

    try {
      return payload.toJSONString().getBytes("UTF-8");
    } catch (UnsupportedEncodingException e) {
      return null;
    }
  }

  private String buildLogRecord(Map<String, Request> requests,
      HttpResponseMessage message) {
    String payload = null;

    byte[] bytes = buildRpcPayload(requests);
    if (bytes != null) {
      try {
        payload = new String(bytes, "UTF-8");
      } catch (UnsupportedEncodingException e) {
        // Ignore
      }
    }

    return buildLogRecord(payload, message);
  }

  private String buildLogRecord(Request request, HttpResponseMessage message) {
    String payload = null;

    if (request.getCustomPayload() == null) {
      byte[] bytes = buildRestPayload(request);
      if (bytes != null) {
        try {
          payload = new String(bytes, "UTF-8");
        } catch (UnsupportedEncodingException e) {
          // Ignore
        }
      }
    }

    return buildLogRecord(payload, message);
  }

  private String buildLogRecord(String payload, HttpResponseMessage message) {
    StringBuilder builder = new StringBuilder("\n");
    builder.append(message.getMethod());
    builder.append("\n");
    builder.append(message.getUrl().toString());
    builder.append("\n");
    if (payload != null) {
      builder.append(payload);
      builder.append("\n");
    }
    builder.append(message.getStatusCode());
    builder.append("\n");
    builder.append(message.getResponse());

    return builder.toString();
  }
}
