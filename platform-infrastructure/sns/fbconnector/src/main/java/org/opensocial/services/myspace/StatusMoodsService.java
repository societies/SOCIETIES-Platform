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

package org.opensocial.services.myspace;

import org.opensocial.Request;
import org.opensocial.models.myspace.StatusMood;
import org.opensocial.services.Service;

/**
 * OpenSocial API class for MySpace status and mood requests; contains static
 * methods for fetching and updating MySpace statuses and for fetching
 * supported moods and mood history.
 *
 * @author Jason Cooper
 */
public class StatusMoodsService extends Service {

  private static final String restTemplate =
    "statusmood/{guid}/{groupId}/{friendId}/{moodId}/{history}";

  /**
   * Returns a new Request instance which, when submitted, fetches the current
   * viewer's status and mood and makes this data available as a StatusMood
   * object. Equivalent to getStatus("@me").
   *
   * @return new Request object to fetch the current viewer's status/mood
   * @see    StatusMood
   */
  public static Request getStatus() {
    return getStatus(ME);
  }

  /**
   * Returns a new Request instance which, when submitted, fetches the
   * specified user's status and mood and makes this data available as a
   * StatusMood object.
   *
   * @param  guid OpenSocial ID of the user whose status is to be fetched
   * @return      new Request object to fetch the specified user's status/mood
   * @see         StatusMood
   */
  public static Request getStatus(String guid) {
    return getStatus(guid, SELF);
  }

  /**
   * Returns a new Request instance which, when submitted, fetches the status
   * and mood of all of the specified user's friends and makes this data
   * available as a List of StatusMood objects. Pass "@me" to fetch the
   * statuses and moods for all friends of the current viewer.
   *
   * @param  guid OpenSocial ID of the user whose friends' statuses and moods
   *              are to be fetched
   * @return      new Request object to fetch the specified user's friends'
   *              statuses/moods
   * @see         StatusMood
   */
  public static Request getFriendStatuses(String guid) {
    return getStatus(guid, FRIENDS);
  }

  private static Request getStatus(String guid, String groupId) {
    Request request = new Request(restTemplate, null, "GET");
    request.setModelClass(StatusMood.class);
    request.setGroupId(groupId);
    request.setGuid(guid);

    return request;
  }

  /**
   * Returns a new Request instance which, when submitted, fetches the current
   * viewer's previous statuses and moods and makes this data available as a
   * List of StatusMood objects.
   *
   * @return new Request object to fetch the current viewer's previous
   *         statuses/moods
   * @see    StatusMood
   */
  public static Request getStatusHistory() {
    return getStatusHistory(SELF, null);
  }

  /**
   * Returns a new Request instance which, when submitted, fetches the previous
   * statuses and moods for all of the current viewer's friends and makes this
   * data available as a List of StatusMood objects.
   *
   * @return new Request object to fetch the current viewer's friends' previous
   *         statuses/moods
   * @see    StatusMood
   */
  public static Request getFriendStatusHistories() {
    return getStatusHistory(FRIENDS, null);
  }

  /**
   * Returns a new Request instance which, when submitted, fetches the previous
   * statuses and moods for the specified friend and makes this data available
   * as a List of StatusMood objects.
   *
   * @param  friendId OpenSocial ID of the current viewer's friend whose
   *                  previous statuses/moods are to be fetched
   * @return          new Request object to fetch the specified friend's
   *                  previous statuses/moods
   * @see             StatusMood
   */
  public static Request getFriendStatusHistory(String friendId) {
    return getStatusHistory(FRIENDS, friendId);
  }

  private static Request getStatusHistory(String groupId,
      String friendId) {
    Request request = new Request(restTemplate, null, "GET");
    request.setModelClass(StatusMood.class);
    request.addComponent(Request.FRIEND_ID, friendId);
    request.addComponent(Request.HISTORY, Request.HISTORY);
    request.setGroupId(groupId);
    request.setGuid(ME);

    return request;
  }

  /**
   * Returns a new Request instance which, when submitted, fetches all moods
   * supported by and available on MySpace and makes this data available as a
   * List of StatusMood objects.
   *
   * @return new Request object to fetch all available MySpace moods
   * @see    StatusMood
   */
  public static Request getSupportedMoods() {
    Request request = new Request(restTemplate, null, "GET");
    request.setModelClass(StatusMood.class);
    request.setGroupId("@supportedMood");
    request.setGuid(ME);

    return request;
  }

  /**
   * Returns a new Request instance which, when submitted, fetches the
   * specified mood and makes this data available as a StatusMood object.
   *
   * @param  moodId ID of mood to fetch
   * @return        new Request object to fetch the specified mood
   * @see           StatusMood
   */
  public static Request getSupportedMood(long moodId) {
    Request request = getSupportedMoods();
    request.addComponent(Request.MOOD_ID, "" + moodId);

    return request;
  }

  /**
   * Returns a new Request instance which, when submitted, updates the current
   * viewer's status and/or mood.
   *
   * @param  statusMood StatusMood object specifying the status and/or mood
   *                    parameters to pass into the request; typically, status
   *                    and moodId are set
   * @return            new Request object to update the current viewer's
   *                    status and/or mood
   */
  public static Request updateStatus(StatusMood statusMood) {
    Request request = new Request(restTemplate, null, "PUT");
    request.setGroupId(SELF);
    request.setGuid(ME);

    // Add REST payload parameters
    try {
      long moodId = Long.parseLong(statusMood.getMoodId());
      request.addRestPayloadParameter("moodId", moodId);
    } catch (NumberFormatException e) {
      request.addRestPayloadParameter("moodId", statusMood.getMoodId());
    }
    request.addRestPayloadParameter("status", statusMood.getStatus());

    return request;
  }
}
