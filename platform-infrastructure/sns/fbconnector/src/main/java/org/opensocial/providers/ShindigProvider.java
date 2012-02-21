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

public class ShindigProvider extends Provider {

  public ShindigProvider() {
    this(false);
  }

  public ShindigProvider(boolean useRest) {
    super();

    setName("localhost");
    setVersion("0.8");
    setRestEndpoint("http://localhost:8080/social/rest/");
    if (!useRest) {
      setRpcEndpoint("http://localhost:8080/social/rpc/");
    }
    setAuthorizeUrl("http://localhost:9090/oauth-provider/authorize");
    setAccessTokenUrl("http://localhost:9090/oauth-provider/access_token");
    setRequestTokenUrl("http://localhost:9090/oauth-provider/request_token");
  }
}
