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
import org.opensocial.models.myspace.Notification;
import org.opensocial.services.Service;

/**
 * OpenSocial API class for MySpace notification requests; contains static
 * methods for creating MySpace notifications.
 *
 * @author Jason Cooper
 */
public class NotificationsService extends Service {

  private static final String restTemplate = "notifications/{guid}/{groupId}";

  /**
   * Returns a new Request instance which, when submitted, creates a new
   * MySpace notification to the recipients specified in the passed object.
   *
   * @param  notification Notification object specifying the parameters to pass
   *                      into the request; typically, recipient IDs are added,
   *                      but content and media items can also be set
   * @return              new Request object to create a new MySpace
   *                      notification
   * @see                 Notification
   */
  public static Request createNotification(Notification notification) {
    Request request = new Request(restTemplate, null, "POST");
    request.setModelClass(Notification.class);
    request.setGroupId(SELF);
    request.setGuid(ME);

    // Add REST payload parameters
    request.addRestPayloadParameters(notification);

    return request;
  }
}
