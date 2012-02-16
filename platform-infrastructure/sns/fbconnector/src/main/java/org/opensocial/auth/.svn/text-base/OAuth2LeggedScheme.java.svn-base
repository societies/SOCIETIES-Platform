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

package org.opensocial.auth;

import net.oauth.OAuth;
import net.oauth.OAuthAccessor;
import net.oauth.OAuthConsumer;
import net.oauth.OAuthMessage;
import net.oauth.http.HttpMessage;

import org.opensocial.RequestException;
import org.opensocial.providers.Provider;

import java.io.IOException;
import java.util.Map;

/**
 * Authentication class that uses 2-Legged OAuth to digitally sign requests
 * using a shared secret; this scheme is also known as Signed Fetch and Phone
 * Home. For reference:
 * http://sites.google.com/site/oauthgoog/2leggedoauth/2opensocialrestapi
 *
 * @author Jason Cooper
 */
public class OAuth2LeggedScheme extends OAuthScheme implements AuthScheme {

  private String requesterId;

  /**
   * Creates and returns a new {@link OAuth2LeggedScheme} configured with the
   * passed key and secret, leaving the requester ID field null.
   *
   * @param consumerKey    key provided by an OpenSocial container after
   *                       registering a new OpenSocial gadget
   * @param consumerSecret secret provided by an OpenSocial container after
   *                       registering a new OpenSocial gadget
   */
  public OAuth2LeggedScheme(String consumerKey, String consumerSecret) {
    this(consumerKey, consumerSecret, null);
  }

  /**
   * Creates and returns a new {@link OAuth2LeggedScheme} configured with the
   * passed key, secret, and requester ID.
   *
   * @param consumerKey    key provided by an OpenSocial container after
   *                       registering a new OpenSocial gadget
   * @param consumerSecret secret provided by an OpenSocial container after
   *                       registering a new OpenSocial gadget
   * @param requesterId    OpenSocial ID of user on whose behalf the OpenSocial
   *                       calls are being made
   */
  public OAuth2LeggedScheme(String consumerKey, String consumerSecret,
      String requesterId) {
    super(consumerKey, consumerSecret);

    this.requesterId = requesterId;
  }

  public HttpMessage getHttpMessage(Provider provider, String method,
      String url, Map<String, String> headers, byte[] body) throws
      RequestException, IOException {
    if (consumerKey == null || consumerSecret == null) {
      return null;
    }

    url = appendRequesterIdToQueryString(url);
    OAuthMessage message = new OAuthMessage(method, url, null,
        byteArrayToStream(body));

    for (Map.Entry<String, String> header : headers.entrySet()) {
      message.getHeaders().add(header);
    }

    OAuthConsumer consumer =
      new OAuthConsumer(null, consumerKey, consumerSecret, null);
    consumer.setProperty(OAuth.OAUTH_SIGNATURE_METHOD, OAuth.HMAC_SHA1);

    OAuthAccessor accessor = new OAuthAccessor(consumer);

    return getHttpMessage(message, accessor, body, provider.getSignBodyHash());
  }

  public String getRequesterId() {
    return requesterId;
  }

  private String appendRequesterIdToQueryString(String url) {
    if (requesterId == null) {
      return url;
    }

    StringBuilder builder = new StringBuilder(url);

    if (url.indexOf('?') == -1) {
      builder.append("?xoauth_requestor_id=");
    } else {
      builder.append("&xoauth_requestor_id=");
    }

    builder.append(requesterId);

    return builder.toString();
  }
}
