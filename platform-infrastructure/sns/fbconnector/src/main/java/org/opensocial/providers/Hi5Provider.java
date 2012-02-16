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

public class Hi5Provider extends Provider {

  public Hi5Provider() {
    this(false);
  }

  public Hi5Provider(boolean useRest) {
    super();

    setName("Hi5");
    setVersion("0.8");
    setRestEndpoint("http://api.hi5.com/social/rest/");
    if (!useRest) {
      setRpcEndpoint("http://api.hi5.com/social/rpc/");
    }
    setAuthorizeUrl("https://login.hi5.com/oauth/authorize");
    setAccessTokenUrl("https://api.hi5.com/oauth/accessToken");
    setRequestTokenUrl("https://api.hi5.com/oauth/requestToken");
  }
}
