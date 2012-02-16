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

package org.opensocial.models;

import java.util.Map;
import java.util.Set;

/**
 * OpenSocial model class representing AppData, uninterpreted key/value pairs.
 * For reference:
 * http://www.opensocial.org/Technical-Resources/opensocial-spec-v09/REST-API.html#rfc.section.3.5
 *
 * @author Jason Cooper
 */
public class AppData extends Model {

  /**
   * Returns the value of the specified AppData key for the specified user.
   *
   * @param userId OpenSocial ID of user whose AppData is requested
   * @param key    AppData key to fetch for the specified user
   */
  public String getDataForUser(String userId, String key) {
    Map userData = getFieldAsMap(userId);
    if (userData != null) {
      return (String) userData.get(key);
    }

    return null;
  }

  /**
   * Returns the complete set of AppData keys set for the specified user.
   *
   * @param userId OpenSocial ID of user whose AppData keys are to be returned
   */
  public Set<String> getFieldNamesForUser(String userId) {
    Map userData = getFieldAsMap(userId);
    if (userData != null) {
      return userData.keySet();
    }

    return null;
  }
}
