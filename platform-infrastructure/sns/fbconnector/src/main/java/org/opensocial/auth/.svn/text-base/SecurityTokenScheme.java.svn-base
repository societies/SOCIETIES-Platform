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

import net.oauth.http.HttpMessage;

import org.opensocial.RequestException;
import org.opensocial.providers.Provider;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;

/**
 * Authentication class that uses a security token to authenticate requests by
 * appending ?st={token} to the request URL. Security tokens can be lifted from
 * running gadgets, but generally expire after a short time.
 *
 * @author Jason Cooper
 */
public class SecurityTokenScheme implements AuthScheme {

  private String tokenName;
  private String token;

  public SecurityTokenScheme(String token) {
    this("st", token);
  }

  public SecurityTokenScheme(String tokenName, String token) {
    this.tokenName = tokenName;
    this.token = token;
  }

  public HttpMessage getHttpMessage(Provider provider, String method,
      String url, Map<String, String> headers, byte[] body) throws
      RequestException, IOException {
    url = appendTokenToQueryString(url);

    HttpMessage message = new HttpMessage(method, new URL(url),
        byteArrayToStream(body));
    for (Map.Entry<String, String> header : headers.entrySet()) {
      message.headers.add(header);
    }

    return message;
  }

  private String appendTokenToQueryString(String url) {
    if (token == null) {
      return url;
    }

    StringBuilder builder = new StringBuilder(url);

    if (url.indexOf('?') == -1) {
      builder.append("?");
    } else {
      builder.append("&");
    }

    builder.append(tokenName);
    builder.append("=");
    builder.append(token);

    return builder.toString();
  }

  protected InputStream byteArrayToStream(byte[] bytes) {
    InputStream stream = null;

    if (bytes != null) {
      stream = new ByteArrayInputStream(bytes);
    }

    return stream;
  }
}
