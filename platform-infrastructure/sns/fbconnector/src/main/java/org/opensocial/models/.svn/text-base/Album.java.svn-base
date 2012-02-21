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
 * OpenSocial model class representing an album, a collection of media items.
 * For reference:
 * http://wiki.opensocial.org/index.php?title=Opensocial.Album_(v0.9)
 * http://www.opensocial.org/Technical-Resources/opensocial-spec-v09/REST-API.html#rfc.section.3.6.1
 *
 * @author Jason Cooper
 */
public class Album extends Model {

  /**
   * Returns the album's unique identifier.
   */
  public String getId() {
    return getFieldAsString("id");
  }

  /**
   * Returns the OpenSocial ID of the album's owner.
   */
  public String getOwnerId() {
    return getFieldAsString("ownerId");
  }

  /**
   * Returns the album's caption or title.
   */
  public String getCaption() {
    return getFieldAsString("caption");
  }

  /**
   * Returns the album's description.
   */
  public String getDescription() {
    return getFieldAsString("description");
  }

  /**
   * Returns the album cover's thumbnail URL as a string.
   */
  public String getThumbnailUrl() {
    return getFieldAsString("thumbnailUrl");
  }

  /**
   * Sets the album's unique identifier. Required when updating an existing
   * album; the container generates the identifier when a new album is created.
   *
   * @param id identifier to set
   */
  public void setId(String id) {
    put("id", id);
  }

  /**
   * Sets the album's caption or title.
   *
   * @param caption caption to set
   */
  public void setCaption(String caption) {
    put("caption", caption);
  }

  /**
   * Sets the album's description.
   *
   * @param description description to set
   */
  public void setDescription(String description) {
    put("description", description);
  }

  /**
   * Sets the album cover's thumbnail URL.
   *
   * @param thumbnailUrl URL of album cover thumbnail to set
   */
  public void setThumbnailUrl(String thumbnailUrl) {
    put("thumbnailUrl", thumbnailUrl);
  }
}
