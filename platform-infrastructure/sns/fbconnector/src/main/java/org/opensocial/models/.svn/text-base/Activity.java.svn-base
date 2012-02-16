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

/**
 * OpenSocial model class representing an activity. For reference:
 * http://wiki.opensocial.org/index.php?title=Opensocial.Activity_(v0.9)
 * http://www.opensocial.org/Technical-Resources/opensocial-spec-v09/REST-API.html#rfc.section.3.4
 *
 * @author Jason Cooper
 */
public class Activity extends Model {

  /**
   * Returns the activity's unique identifier.
   */
  public String getId() {
    return getFieldAsString("id");
  }

  /**
   * Returns the activity's body, a string specifying an optional expanded
   * version of an activity.
   */
  public String getBody() {
    return getFieldAsString("body");
  }

  /**
   * Returns the activity's body ID, a string specifying the body template
   * message ID in the accompanying gadget specification.
   */
  public String getBodyId() {
    return getFieldAsString("bodyId");
  }

  /**
   * Returns the activity's title, a string specifying the primary text of an
   * activity.
   */
  public String getTitle() {
    return getFieldAsString("title");
  }

  /**
   * Returns the activity's title ID, a string specifying the title template
   * message ID in the accompanying gadget specification.
   */
  public String getTitleId() {
    return getFieldAsString("titleId");
  }

  /**
   * Sets the activity's body, a string specifying an optional expanded version
   * of an activity
   *
   * @param body body to set
   */
  public void setBody(String body) {
    put("body", body);
  }

  /**
   * Sets the activity's body ID, a string specifying the body template message
   * ID in the accompanying gadget specification.
   *
   * @param bodyId body ID to set
   */
  public void setBodyId(String bodyId) {
    put("bodyId", bodyId);
  }

  /**
   * Sets the activity's title, a string specifying the primary text of an
   * activity.
   *
   * @param title title to set
   */
  public void setTitle(String title) {
    put("title", title);
  }

  /**
   * Sets the activity's title ID, a string specifying the title template
   * message ID in the accompanying gadget specification.
   *
   * @param titleId title ID to set
   */
  public void setTitleId(String titleId) {
    put("titleId", titleId);
  }
  
  /**
   * Sets the activity's ID
   *
   * @param id ID to set
   */
  public void setId(String id) {
    put("id", id);
  }
}
