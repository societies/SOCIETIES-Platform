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

import org.opensocial.models.MediaItem;
import org.opensocial.models.Model;

/**
 * MySpace OpenSocial model class representing a MySpace notification. For
 * reference:
 * http://developerwiki.myspace.com/index.php?title=OpenSocial_v0.9_Notifications
 *
 * @author Jason Cooper
 */
public class Notification extends Model {

  /**
   * Sets the notification's content as a template parameter; the passed string
   * can contain tokens such as ${recipient} which are substituted for
   * appropriate values when the content is rendered.
   *
   * @param content content to set; can contain template "tokens" of the form
   *                ${token}, the values of which are substituted when the
   *                content block is rendered
   */
  public void setContent(String content) {
    addTemplateParameter("content", content);
  }

  /**
   * Adds the specified user to the list of users that will receive the
   * notification.
   *
   * @param id OpenSocial ID of user to receive notification
   */
  public void addRecipient(String id) {
    addToListField("recipientIds", id);
  }

  /**
   * Adds the passed media item to the list of media items (i.e. images) to
   * post with the notification.
   *
   * @param item media item, i.e. image, to post with the notification
   */
  public void addMediaItem(MediaItem item) {
    addToListField("mediaItems", item);
  }
}
