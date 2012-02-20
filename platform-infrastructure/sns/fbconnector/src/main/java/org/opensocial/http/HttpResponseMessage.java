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

package org.opensocial.http;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;

public class HttpResponseMessage extends net.oauth.http.HttpResponseMessage {

  private int statusCode;
  private String response;

  public HttpResponseMessage(String method, URL url, int statusCode) throws
      IOException {
    this(method, url, statusCode, null);
  }

  public HttpResponseMessage(String method, URL url, int statusCode,
      InputStream responseStream) throws IOException {
    super(method, url);
    this.statusCode = statusCode;
    setResponse(responseStream);
  }

  @Override
  public int getStatusCode() {
    return statusCode;
  }

  public String getResponse() {
    return response;
  }

  public String getMethod() {
    return method;
  }

  public URL getUrl() {
    return url;
  }

  /**
   * OAuth needs the response as InputStream.
   */
  @Override
  protected InputStream openBody() {
    if (response != null) {
      try {
        return new ByteArrayInputStream(response.getBytes("UTF-8"));
      } catch (UnsupportedEncodingException e) {
        // Ignore
      }
    }

    return null;
  }

  private void setResponse(InputStream in) {
    if (in != null) {
      try {
        String line = null;
        StringBuilder builder = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));

        while ((line = reader.readLine()) != null) {
          builder.append(line);
        }

        response = builder.toString();
        in.close();
      } catch(IOException e) {
        response = null;
      }
    }
  }
}
