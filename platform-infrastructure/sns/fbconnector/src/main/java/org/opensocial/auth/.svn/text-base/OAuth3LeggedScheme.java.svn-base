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
import net.oauth.OAuthException;
import net.oauth.OAuthMessage;
import net.oauth.OAuthServiceProvider;
import net.oauth.client.OAuthClient;
import net.oauth.http.HttpMessage;

import org.opensocial.RequestException;
import org.opensocial.http.HttpClient;
import org.opensocial.providers.Provider;

import java.io.IOException;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

/**
 * Authentication class that exposes methods for retrieving request and access
 * tokens and an appropriate authorization URL for the 3-Legged OAuth "dance."
 * For reference:
 * http://sites.google.com/site/oauthgoog/2leggedoauth/2opensocialrestapi
 *
 * @author Christoph Renner
 * @author Jason Cooper
 *
 */
public class OAuth3LeggedScheme extends OAuthScheme implements AuthScheme,
    Serializable {

  public static class Token implements Serializable {
    public String token;
    public String secret;

    public Token() {}

    public Token(String token, String secret) {
      this.token = token;
      this.secret = secret;
    }
  }

  private Provider provider;
  private Token requestToken;
  private Token accessToken;

  protected OAuth3LeggedScheme() {
    super();
  }

  /**
   * Creates and returns a new {@link OAuth3LeggedScheme} configured with the
   * passed {@link Provider}, key, and secret.
   *
   * @param provider       OpenSocial provider that the current user should be
   *                       authenticated against; must have the three required
   *                       3-legged OAuth endpoints set (see GoogleProvider and
   *                       MySpaceProvider for reference)
   * @param consumerKey    key provided by an OpenSocial container after
   *                       registering a new application
   * @param consumerSecret secret provided by an OpenSocial container after
   *                       registering a new application
   */
  public OAuth3LeggedScheme(Provider provider, String consumerKey,
      String consumerSecret) {
    super(consumerKey, consumerSecret);

    this.provider = provider;
  }

  public HttpMessage getHttpMessage(Provider provider, String method,
      String url, Map<String, String> headers, byte[] body) throws
      RequestException, IOException {
    return getHttpMessage(provider, method, url, headers, body, null);
  }

  public HttpMessage getHttpMessage(Provider provider, String method,
      String url, Map<String, String> headers, byte[] body,
      Collection<? extends Entry> parameters) throws
      RequestException, IOException {
    OAuthAccessor accessor = getOAuthAccessor(accessToken.token,
        accessToken.secret);
    OAuthMessage message = new OAuthMessage(method, url, parameters,
        byteArrayToStream(body));

    for (Map.Entry<String, String> header : headers.entrySet()) {
      message.getHeaders().add(header);
    }

    return getHttpMessage(message, accessor, body, provider.getSignBodyHash());
  }

  /**
   * Sends a signed request to the associated provider to retrieve an initial
   * request token. If successful, returns a URL to the associated provider's
   * authorization page; after being forwarded to this URL, the user will be
   * prompted to enter their account credentials and upon successful sign-in,
   * will be forwarded again to the specified callback URL.
   *
   * @param  callbackUrl URL to forward user to after successful sign-in
   * @return             URL to provider's authorization page as a string
   *
   * @throws OAuthException
   * @throws URISyntaxException
   * @throws IOException
   */
  public String getAuthorizationUrl(String callbackUrl) throws OAuthException,
      URISyntaxException, IOException {
    requestToken = requestRequestToken();

    if (requestToken.token == null) {
      // This is an unregistered OAuth request
      return provider.getAuthorizeUrl() + "?oauth_callback=" + callbackUrl;
    }

    return provider.getAuthorizeUrl() + "?oauth_token=" + requestToken.token +
        "&oauth_callback=" + callbackUrl;
  }

  /**
   * Sends a signed request to the associated provider to exchange the passed
   * request token for an access token; if successfully exchanged, this token
   * can then be accessed using getAccessToken().
   *
   * @param oAuthToken previously fetched request token to exchange for access
   *                   token
   *
   * @throws OAuthException
   * @throws URISyntaxException
   * @throws IOException
   */
  public void requestAccessToken(String oAuthToken) throws OAuthException,
      URISyntaxException, IOException {
    requestAccessToken(oAuthToken, null);
  }

  /**
   * Sends a signed request to the associated provider to exchange the passed
   * request token for an access token; if successfully exchanged, this token
   * can then be accessed using getAccessToken().
   *
   * @param oAuthToken    previously fetched request token to exchange for
   *                      access token
   * @param oAuthVerifier verification code returned by some providers, e.g.
   *                      Yahoo
   *
   * @throws OAuthException
   * @throws URISyntaxException
   * @throws IOException
   */
  public void requestAccessToken(String oAuthToken, String oAuthVerifier)
      throws OAuthException, URISyntaxException, IOException {
    Set<Map.Entry<String, String>> parameters = null;
    if (oAuthVerifier != null) {
      Map<String, String> parameterMap = new HashMap<String, String>();
      parameterMap.put("oauth_verifier", oAuthVerifier);
      parameters = parameterMap.entrySet();
    }

    OAuthAccessor accessor = getOAuthAccessor(oAuthToken,
        this.requestToken.secret);
    OAuthMessage message = getOAuthClient().invoke(accessor, "GET",
        provider.getAccessTokenUrl(), parameters);

    accessToken = new Token(message.getToken(),
        message.getParameter(OAuth.OAUTH_TOKEN_SECRET));
  }

  /**
   * Returns the associated provider.
   */ 
  public Provider getProvider() {
    return provider;
  }

  /**
   * Returns the request token previously retrieved from the provider when
   * {@code getAuthorizationUrl} was executed or null if no request token is
   * associated with the current instance.
   *
   * @see #getAuthorizationUrl(String)
   */
  public Token getRequestToken() {
    return requestToken;
  }

  /**
   * Returns the access token for which the original request token was
   * exchanged or null if the exchange hasn't occurred and no access token is
   * associated with the current instance.
   *
   * @see #requestAccessToken(String)
   */
  public Token getAccessToken() {
    return accessToken;
  }

  /**
   * Associates the specified request token with the current instance.
   * 
   * @param token request token to associate
   */
  public void setRequestToken(Token token) {
    requestToken = token;
  }

  /**
   * Associates the specified access token with the current instance.
   *
   * @param token access token to associate
   */
  public void setAccessToken(Token token) {
    accessToken = token;
  }

  private Token requestRequestToken() throws OAuthException,
      URISyntaxException, IOException {
    if (provider.getRequestTokenUrl() == null) {
      return new Token();
    }

    Set<Map.Entry<String,String>> extraParams = null;
    if (provider.getRequestTokenParameters() != null) {
      extraParams = provider.getRequestTokenParameters().entrySet();
    }

    OAuthAccessor accessor = getOAuthAccessor();
    getOAuthClient().getRequestToken(accessor, "GET", extraParams);

    return new Token(accessor.requestToken, accessor.tokenSecret);
  }

  private OAuthAccessor getOAuthAccessor() {
    OAuthServiceProvider serviceProvider = new OAuthServiceProvider(
        provider.getRequestTokenUrl(), provider.getAuthorizeUrl(),
        provider.getAccessTokenUrl());

    OAuthConsumer consumer = new OAuthConsumer(null, consumerKey,
        consumerSecret, serviceProvider);
    consumer.setProperty(OAuth.OAUTH_SIGNATURE_METHOD, OAuth.HMAC_SHA1);

    return new OAuthAccessor(consumer);
  }

  private OAuthAccessor getOAuthAccessor(String token, String secret) {
    OAuthAccessor accessor = getOAuthAccessor();
    accessor.accessToken = token;
    accessor.tokenSecret = secret;

    return accessor;
  }

  private OAuthClient getOAuthClient() {
    return new OAuthClient(new HttpClient());
  }
}
