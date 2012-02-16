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

import net.oauth.http.HttpMessage;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.Map;

public class HttpClient implements net.oauth.http.HttpClient {

  public HttpResponseMessage execute(HttpMessage message) throws IOException {
    return execute(message, null);
  }

  public HttpResponseMessage execute(HttpMessage message,
      Map<String, Object> parameters) throws IOException {
    HttpURLConnection connection = null;

    try {
      connection = getConnection(message);

      if (message.getBody() != null) {
        DataOutputStream out =
          new DataOutputStream(connection.getOutputStream());
        out.write(streamToByteArray(message.getBody()));
        out.flush();
        out.close();
        /*OutputStreamWriter out =
          new OutputStreamWriter(connection.getOutputStream());
        out.write(streamToByteArray(message.getBody()));
        out.flush();
        out.close();*/
      }

      return new HttpResponseMessage(message.method, message.url,
          connection.getResponseCode(), connection.getInputStream());
    } catch (IOException e) {
      if (connection != null) {
        return new HttpResponseMessage(message.method, message.url,
            connection.getResponseCode());
      } else {
        throw e;
      }
    }
  }

  private HttpURLConnection getConnection(HttpMessage message) throws
  IOException {
    HttpURLConnection connection =
      (HttpURLConnection) message.url.openConnection();

    for (Map.Entry<String, String> header : message.headers) {
      connection.setRequestProperty(header.getKey(), header.getValue());
    }

    connection.setRequestMethod(message.method);
    connection.setDoOutput(true);
    connection.connect();

    return connection;
  }

  private byte[] streamToByteArray(InputStream stream) throws IOException {
    byte[] buffer = new byte[1024];
    ByteArrayOutputStream out = new ByteArrayOutputStream();

    while(true) {
      int read = stream.read(buffer);
      if (read <= 0) {
        break;
      }

      out.write(buffer, 0, read);
    }

    return out.toByteArray();
    /*BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
    StringBuilder builder = new StringBuilder();

    String line = null;
    try {
      while ((line = reader.readLine()) != null) {
        builder.append(line);
      }
    } catch (IOException e) {
    } finally {
      try {
        stream.close();
      } catch (IOException e) {
      }
    }

    return builder.toString();*/
  }
}
