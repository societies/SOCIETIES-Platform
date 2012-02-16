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
import org.opensocial.models.myspace.Comment;
import org.opensocial.services.Service;

/**
 * OpenSocial API class for MySpace profile comment requests; contains static
 * methods for fetching MySpace profile comments.
 *
 * @author Jason Cooper
 */
public class ProfileCommentsService extends Service {

  private static final String restTemplate =
    "profilecomments/{guid}/{groupId}";

  /**
   * Returns a new Request instance which, when submitted, fetches the current
   * viewer's profile comments and makes this data available as a List of
   * Comment objects.
   *
   * @return new Request object to fetch the current viewer's profile comments
   * @see    Comment
   */
  public static Request getComments() {
    Request request = new Request(restTemplate, null, "GET");
    request.setModelClass(Comment.class);
    request.setGroupId(SELF);
    request.setGuid(ME);

    return request;
  }
}
