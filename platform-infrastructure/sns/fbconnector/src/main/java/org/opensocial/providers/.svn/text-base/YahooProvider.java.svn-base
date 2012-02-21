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

public class YahooProvider extends Provider {

  private String callback;

  public YahooProvider() {
    super();

    setName("Yahoo!");
    setVersion("0.8");
    setRestEndpoint("http://appstore.apps.yahooapis.com/social/rest/");
    setAuthorizeUrl("https://api.login.yahoo.com/oauth/v2/request_auth");
    setAccessTokenUrl("https://api.login.yahoo.com/oauth/v2/get_token");
    setRequestTokenUrl(
        "https://api.login.yahoo.com/oauth/v2/get_request_token");
  }

  public YahooProvider(String callback) {
    this();

    setCallback(callback);
  }

  public String getCallback() {
    return callback;
  }

  public void setCallback(String callback) {
    this.callback = callback;
    addRequestTokenParameter("oauth_callback", callback);
  }
}
