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

package org.opensocial.models.myspace;

import java.util.Map;

import org.opensocial.models.Model;

/**
 * MySpace OpenSocial model class representing a MySpace profile comment. For
 * reference:
 * http://developerwiki.myspace.com/index.php?title=OpenSocial_v0.9_ProfileComments
 *
 * @author Jason Cooper
 */
public class Comment extends Model {

  /**
   * Returns the profile comment's associated ID.
   */
  public String getId() {
    return getFieldAsString("commentId");
  }

  /**
   * Returns the profile comment's body or text.
   */
  public String getBody() {
    return getFieldAsString("body");
  }

  /**
   * Returns the OpenSocial ID of the user who published the comment.
   */
  public String getAuthorId() {
    Map author = getFieldAsMap("author");

    if (author == null || !author.containsKey("id")) {
      return null;
    }

    return (String) author.get("id");
  }

  /**
   * Returns the date that the comment was published as a string.
   */
  public String getPostedDate() {
    return getFieldAsString("postedDate");
  }
}
