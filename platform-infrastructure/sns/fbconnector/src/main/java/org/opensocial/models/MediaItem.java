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
 * OpenSocial model class representing a media item (image, movie, or audio).
 * For reference:
 * http://wiki.opensocial.org/index.php?title=Opensocial.MediaItem_(v0.9)
 * http://www.opensocial.org/Technical-Resources/opensocial-spec-v09/REST-API.html#rfc.section.3.6.2
 *
 * @author Jason Cooper
 */
public class MediaItem extends Model {

  public static final String ID = "id";
  public static final String URL = "url";
  public static final String TYPE = "type";
  public static final String CAPTION = "caption";
  public static final String ALBUM_ID = "album_id";
  public static final String MIME_TYPE = "mime_type";
  public static final String DESCRIPTION = "description";
  public static final String THUMBNAIL_URL = "thumbnail_url";

  /**
   * Returns the media item's associated ID.
   */
  public String getId() {
    return getFieldAsString(ID);
  }

  /**
   * Returns the identifier of the album to which the media item belongs.
   */
  public String getAlbumId() {
    return getFieldAsString(ALBUM_ID);
  }

  /**
   * Returns the URL where the media can be found as a string.
   */
  public String getUrl() {
    return getFieldAsString(URL);
  }

  /**
   * Returns the URL of the media item's thumbnail image as a string.
   */
  public String getThumbnailUrl() {
    if (getFieldAsString("thumbnailUrl") != null) {
      return getFieldAsString("thumbnailUrl");
    }

    return getFieldAsString(THUMBNAIL_URL);
  }

  /**
   * Returns the media item's type, one of 'audio', 'image', or 'video'.
   */
  public String getType() {
    return getFieldAsString(TYPE);
  }

  /**
   * Returns the MIME type of the media item's content.
   */
  public String getMimeType() {
    return getFieldAsString(MIME_TYPE);
  }

  /**
   * Returns the media item's caption if set.
   */
  public String getCaption() {
    return getFieldAsString(CAPTION);
  }

  /**
   * Returns the media item's description if set.
   */
  public String getDescription() {
    return getFieldAsString(DESCRIPTION);
  }

  /**
   * Sets the media item's unique identifier. Required when updating an
   * existing media item; the container generates the identifier when a new
   * media item is created.
   *
   * @param id identifier to set
   */
  public void setId(String id) {
    put(ID, id);
  }

  /**
   * Sets the identifier of the album to which the media item belongs.
   *
   * @param albumId ID of album intended to contain media item
   */
  public void setAlbumId(String albumId) {
    put(ALBUM_ID, albumId);
  }

  /**
   * Sets the URL where the media can be found.
   *
   * @param url URL to set
   */
  public void setUrl(String url) {
    put(URL, url);
  }

  /**
   * Sets the URL of the media item's thumbnail image as a string.
   *
   * @param thumbnailUrl URL of media item thumbnail to set
   */
  public void setThumbnailUrl(String thumbnailUrl) {
    put(THUMBNAIL_URL, thumbnailUrl);
  }

  /**
   * Sets the media item's type.
   *
   * @param type type to set; must be 'audio', 'image', or 'video'
   */
  public void setType(String type) {
    put(TYPE, type);
  }

  /**
   * Sets the MIME type of the media item's content.
   *
   * @param mimeType MIME type to set
   */
  public void setMimeType(String mimeType) {
    put(MIME_TYPE, mimeType);
  }

  /**
   * Sets the media item's caption/title.
   *
   * @param caption caption/title to set
   */
  public void setCaption(String caption) {
    setField(CAPTION, caption);
  }

  /**
   * Sets the media item's description.
   *
   * @param description description to set
   */
  public void setDescription(String description) {
    setField(DESCRIPTION, description);
  }
}
