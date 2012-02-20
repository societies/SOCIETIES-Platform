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

import net.oauth.OAuthAccessor;
import net.oauth.OAuthException;
import net.oauth.OAuthMessage;
import net.oauth.ParameterStyle;
import net.oauth.http.HttpMessage;

import org.apache.commons.codec.binary.Base64;
import org.opensocial.RequestException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;

abstract class OAuthScheme implements Serializable {

  protected String consumerKey;
  protected String consumerSecret;

  protected OAuthScheme() {
  }

  public OAuthScheme(String consumerKey, String consumerSecret) {
    this.consumerKey = consumerKey;
    this.consumerSecret = consumerSecret;
  }

  public String getConsumerKey() {
    return consumerKey;
  }

  public String getConsumerSecret() {
    return consumerSecret;
  }

  protected InputStream byteArrayToStream(byte[] bytes) {
    InputStream stream = null;

    if (bytes != null) {
      stream = new ByteArrayInputStream(bytes);
    }

    return stream;
  }

  protected HttpMessage getHttpMessage(OAuthMessage message,
      OAuthAccessor accessor, byte[] body, boolean signBodyHash) throws
      IOException, RequestException {
    if (body != null) {
      if (signBodyHash) {
        try {
          MessageDigest md = MessageDigest.getInstance("SHA-1");

          byte[] hash = md.digest(body);
          byte[] encodedHash = new Base64().encode(hash);

          message.addParameter("oauth_body_hash",
              new String(encodedHash, "UTF-8"));
        } catch (java.security.NoSuchAlgorithmException e) {
          // Ignore exception
        } catch (java.io.UnsupportedEncodingException e) {
          // Ignore exception
        }
      } else if (message.getHeader(HttpMessage.CONTENT_TYPE).equals(
          "application/x-www-form-urlencoded")){
        message.addParameter(byteArrayToString(body), "");
      }
    }

    try {
      message.addRequiredParameters(accessor);
    } catch (OAuthException e) {
      throw new RequestException(
          "OAuth error thrown while signing request " + e.getMessage());
    } catch (java.net.URISyntaxException e) {
      throw new RequestException(
          "Malformed request URL " + message.URL + " could not be signed");
    }

    return HttpMessage.newRequest(message, ParameterStyle.QUERY_STRING);
  }

  private String byteArrayToString(byte[] bytes) {
    try {
      return new String(bytes, "UTF-8");
    } catch (UnsupportedEncodingException e) {
      // Ignore
      return null;
    }
  }
}
