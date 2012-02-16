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
import org.opensocial.models.Group;

/**
 * OpenSocial API class for group requests; contains static methods for
 * fetching groups used to tag or categorize people and their relationships.
 *
 * @author Jason Cooper
 */
public class GroupsService extends Service {

  private static final String restTemplate = "groups/{guid}";

  /**
   * Returns a new Request instance which, when submitted, fetches the current
   * viewer's groups and makes this data available as a List of Group objects.
   * Equivalent to getGroups("@me").
   *
   * @return new Request object to fetch the current viewer's groups
   * @see    Group
   */
  public static Request getGroups() {
    return getGroups(ME);
  }

  /**
   * Returns a new Request instance which, when submitted, fetches the
   * specified user's groups and makes this data available as a List of Group
   * objects.
   *
   * @param  guid OpenSocial ID of user whose groups are to be fetched
   * @return      new Request object to fetch the specified user's groups
   * @see         Group
   */
  public static Request getGroups(String guid) {
    Request request = new Request(restTemplate, "groups.get", "GET");
    request.setModelClass(Group.class);
    request.setGuid(guid);

    return request;
  }
}
