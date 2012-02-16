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
import org.opensocial.models.Person;

/**
 * OpenSocial API class for people requests; contains static methods for
 * fetching profile information.
 *
 * @author Jason Cooper
 */
public class PeopleService extends Service {

  private static final String restTemplate = "people/{guid}/{selector}/{pid}";

  /**
   * Returns a new Request instance which, when submitted, fetches the current
   * viewer's profile information and makes this data available as a Person
   * object. Equivalent to calling getUser("@me").
   *
   * @return new Request object to fetch the current viewer's profile data
   * @see    Person
   */
  public static Request getViewer() {
    return getUser(ME);
  }

  /**
   * Returns a new Request instance which, when submitted, fetches the profile
   * information of the user with the specified ID and makes this data
   * available as a Person object.
   *
   * @param  guid OpenSocial ID of the user to fetch
   * @return      new Request object to fetch the specified user's profile data
   * @see         Person
   */
  public static Request getUser(String guid) {
    return get(guid, SELF);
  }

  /**
   * Returns a new Request instance which, when submitted, fetches the profile
   * information of the current viewer's friends and makes this data available
   * as a List of Person objects. Equivalent to calling getFriends("@me").
   *
   * @return new Request object to fetch the current viewer's friends' profile
   *         data
   * @see    Person
   */
  public static Request getFriends() {
    return getFriends(ME);
  }

  /**
   * Returns a new Request instance which, when submitted, fetches the profile
   * information of the specified user's friends and makes this data available
   * as a List of Person objects.
   *
   * @param  guid OpenSocial ID of the user whose friends are to be fetched
   * @return      new Request object to fetch the specified user's friends'
   *              profile data
   * @see         Person
   */
  public static Request getFriends(String guid) {
    return get(guid, FRIENDS);
  }

  private static Request get(String guid, String selector) {
    Request request = new Request(restTemplate, "people.get", "GET");
    request.setModelClass(Person.class);
    request.setSelector(selector);
    request.setGuid(guid);

    return request;
  }
}
