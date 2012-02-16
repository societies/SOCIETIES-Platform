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

import org.opensocial.models.Model;

/**
 * MySpace OpenSocial model class representing a MySpace status. For reference:
 * http://developerwiki.myspace.com/index.php?title=OpenSocial_v0.9_StatusMood
 *
 * @author Jason Cooper
 */
public class StatusMood extends Model {

  /**
   * Returns the status text.
   */
  public String getStatus() {
    return getFieldAsString("status");
  }

  /**
   * Returns the integer value of the mood associated with the status.
   */
  public String getMoodId() {
    return getFieldAsString("moodId");
  }

  /**
   * Returns the label of the mood associated with the status, e.g.
   * "accomplished"
   */
  public String getMoodName() {
    return getFieldAsString("moodName");
  }

  /**
   * Sets the status text.
   *
   * @param status status to set
   */
  public void setStatus(String status) {
    put("status", status);
  }

  /**
   * Sets the integer value of the mood to associate with the status.
   *
   * @param moodId ID of mood to associate with status
   */
  public void setMoodId(long moodId) {
    put("moodId", moodId);
  }
}
