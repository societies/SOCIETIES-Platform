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

package org.opensocial.services;

import org.opensocial.Request;
import org.opensocial.models.AppData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * OpenSocial API class for AppData (persistent storage) requests; contains
 * static methods for fetching, updating, and deleting AppData.
 *
 * @author Jason Cooper
 */
public class AppDataService extends Service {

  private static final String restTemplate =
    "appdata/{guid}/{selector}/{appid}";

  /**
   * Returns a new Request instance which, when submitted, fetches the current
   * viewer's persistent AppData (set of key/value strings) associated with the
   * current application and makes this available as an AppData object.
   * Equivalent to getAppData("@me").
   *
   * @return new Request object to fetch the current viewer's AppData
   * @see    AppData
   */
  public static Request getAppData() {
    return getAppData(ME);
  }

  /**
   * Returns a new Request instance which, when submitted, fetches the
   * specified user's AppData (set of key/value strings) associated with the
   * current application and makes this available as an AppData object.
   *
   * @param  guid OpenSocial ID of user whose AppData is to be fetched
   * @return      new Request object to fetch the specified user's AppData
   * @see         AppData
   */
  public static Request getAppData(String guid) {
    Request request = new Request(restTemplate, "appdata.get", "GET");
    request.setModelClass(AppData.class);
    request.setSelector(SELF);
    request.setAppId(APP);
    request.setGuid(guid);

    return request;
  }

  /**
   * Returns a new Request instance which, when submitted, fetches the AppData
   * (set of key/value strings) associated with the current application for the
   * specified user's friends and makes this available as an AppData object.
   * Pass "@me" to fetch AppData for all friends of the current viewer.
   *
   * @param  guid OpenSocial ID of user whose friends' AppData is to be fetched
   * @return      new Request object to fetch the AppData for the specified
   *              user's friends
   * @see         AppData
   */
  public static Request getFriendAppData(String guid) {
    Request request = new Request(restTemplate, "appdata.get", "GET");
    request.setModelClass(AppData.class);
    request.setSelector(FRIENDS);
    request.setAppId(APP);
    request.setGuid(guid);

    return request;
  }

  /**
   * Returns a new Request instance which, when submitted, updates the current
   * viewer's AppData by either adding the specified key if it doesn't already
   * exist or updating the associated value if it does exist.
   *
   * @param  key   AppData key to update
   * @param  value new value to be associated with the specified key
   * @return       new Request object to update the current viewer's AppData
   */
  public static Request updateAppData(String key, String value) {
    Map<String, String> data = new HashMap<String, String>();
    data.put(key, value);

    return updateAppData(data);
  }

  /**
   * Returns a new Request instance which, when submitted, updates the current
   * viewer's AppData with the key/value strings in the passed Map. If the keys
   * don't already exist, they are added; otherwise, the associated values are
   * replaced with those in the passed Map.
   *
   * @param  data Map of key/value pairs to be persisted in AppData
   * @return      new Request object to update the current viewer's AppData
   */
  public static Request updateAppData(Map<String, String> data) {
    Request request = new Request(restTemplate, "appdata.update", "PUT");
    request.setSelector(SELF);
    request.setAppId(APP);
    request.setGuid(VIEWER);

    // Add RPC payload parameters
    List<String> fields = new ArrayList<String>(data.size());
    for (Map.Entry<String, String> field : data.entrySet()) {
      fields.add(field.getKey());
    }

    request.addRpcPayloadParameter("data", data);
    request.addRpcPayloadParameter("fields", fields);

    // Add REST query string parameters
    StringBuilder fieldsBuilder = new StringBuilder();
    for (Map.Entry<String, String> datum : data.entrySet()) {
      if (fieldsBuilder.length() != 0) {
        fieldsBuilder.append(",");
      }
      fieldsBuilder.append(datum.getKey());
    }

    request.addRestQueryStringParameter("fields", fieldsBuilder.toString());

    // Add REST payload parameters
    for (Map.Entry<String, String> datum : data.entrySet()) {
      request.addRestPayloadParameter(datum.getKey(), datum.getValue());
    }

    return request;
  }

  /**
   * Returns a new Request which, when submitted, deletes the specified key and
   * its associated value in the current viewer's AppData.
   *
   * @param  key AppData key to delete
   * @return     new Request object to delete a single key in the current
   *             viewer's AppData
   */
  public static Request deleteAppData(String key) {
    return deleteAppData(new String[] {key});
  }

  /**
   * Returns a new Request which, when submitted, deletes the specified keys
   * and their associated values in the current viewer's AppData.
   *
   * @param  keys array of AppData keys to delete
   * @return      new Request object to delete one or more keys in the current
   *              viewer's AppData
   */
  public static Request deleteAppData(String[] keys) {
    List<String> keyList = new ArrayList<String>(keys.length);
    for (int i = 0; i < keys.length; i++) {
      keyList.add(keys[i]);
    }

    return deleteAppData(keyList);
  }

  /**
   * Returns a new Request which, when submitted, deletes the specified keys
   * and their associated values in the current viewer's AppData.
   *
   * @param  keys List of AppData keys to delete
   * @return      new Request object to delete one or more keys in the current
   *              viewer's AppData
   */
  public static Request deleteAppData(List<String> keys) {
    Request request = new Request(restTemplate, "appdata.delete", "DELETE");
    request.setSelector(SELF);
    request.setAppId(APP);
    request.setGuid(ME);

    // Add RPC parameters
    request.addRpcPayloadParameter("fields", keys);

    // Add REST parameters
    StringBuilder fieldsBuilder = new StringBuilder();
    for (String key : keys) {
      if (fieldsBuilder.length() != 0) {
        fieldsBuilder.append(",");
      }
      fieldsBuilder.append(key);
    }

    request.addRestQueryStringParameter("fields", fieldsBuilder.toString());

    return request;
  }
}
