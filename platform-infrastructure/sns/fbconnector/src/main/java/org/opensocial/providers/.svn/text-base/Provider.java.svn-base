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

package org.opensocial.providers;

import org.opensocial.Request;
import org.opensocial.Response;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Provider implements Serializable {

  private String name;
  private String version;
  private String contentType;
  private String rpcEndpoint;
  private String restEndpoint;
  private String authorizeUrl;
  private String accessTokenUrl;
  private String requestTokenUrl;
  private Map<String, String> requestTokenParameters;
  private boolean signBodyHash = true;

  public String getName() {
    return name;
  }

  public String getVersion() {
    if (version == null) {
      return "0.8";
    }

    return version;
  }

  public String getContentType() {
    if (contentType == null) {
      return "application/json";
    }

    return contentType;
  }

  public String getRpcEndpoint() {
    return rpcEndpoint;
  }

  public String getRestEndpoint() {
    return restEndpoint;
  }

  public String getAuthorizeUrl() {
    return authorizeUrl;
  }

  public String getAccessTokenUrl() {
    return accessTokenUrl;
  }

  public String getRequestTokenUrl() {
    return requestTokenUrl;
  }

  public Map<String, String> getRequestTokenParameters() {
    return requestTokenParameters;
  }

  public boolean getSignBodyHash() {
    return signBodyHash;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public void setContentType(String contentType) {
    this.contentType = contentType;
  }

  public void setRpcEndpoint(String rpcEndpoint) {
    this.rpcEndpoint = rpcEndpoint;
  }

  public void setRestEndpoint(String restEndpoint) {
    this.restEndpoint = restEndpoint;
  }

  public void setAuthorizeUrl(String authorizeUrl) {
    this.authorizeUrl = authorizeUrl;
  }

  public void setAccessTokenUrl(String accessTokenUrl) {
    this.accessTokenUrl = accessTokenUrl;
  }

  public void setRequestTokenUrl(String requestTokenUrl) {
    this.requestTokenUrl = requestTokenUrl;
  }

  public void addRequestTokenParameter(String key, String value) {
    if (requestTokenParameters == null) {
      requestTokenParameters = new HashMap<String, String>();
    }

    requestTokenParameters.put(key, value);
  }

  public void setRequestTokenParameters(
      Map<String, String> requestTokenParameters) {
    this.requestTokenParameters = requestTokenParameters;
  }

  public void setSignBodyHash(boolean signBodyHash) {
    this.signBodyHash = signBodyHash;
  }

  public void preRequest(Request request) {}

  public void postRequest(Request request, Response response) {}
}
