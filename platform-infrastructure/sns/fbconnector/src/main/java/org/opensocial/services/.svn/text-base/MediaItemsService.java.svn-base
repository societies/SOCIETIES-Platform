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
import org.opensocial.RequestException;
import org.opensocial.models.MediaItem;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * OpenSocial API class for media item requests; contains static methods for
 * fetching, creating, updating and deleting media items (video, image, or
 * sound files).
 *
 * @author Jason Cooper
 */
public class MediaItemsService extends Service {

  private static final String restTemplate =
    "mediaitems/{guid}/{groupId}/{albumId}/{itemId}";

  /**
   * Returns a new Request instance which, when submitted, fetches the current
   * viewer's media items from the specified album and makes this data
   * available as a List of MediaItem objects.
   *
   * @param  albumId ID of album whose media item contents are to fetched
   * @return         new Request object to fetch the current viewer's media
   *                 items
   * @see            MediaItem
   */
  public static Request getMediaItems(String albumId) {
    Request request = new Request(restTemplate, "mediaItems.get", "GET");
    request.setModelClass(MediaItem.class);
    request.addComponent(Request.ALBUM_ID, albumId);
    request.setGroupId(SELF);
    request.setGuid(ME);

    return request;
  }

  /**
   * Returns a new Request instance which, when submitted, fetches the
   * specified media item from the specified album and makes this data
   * available as a MediaItem object.
   *
   * @param  itemId  ID of media item to fetch
   * @param  albumId ID of album containing media item to fetch
   * @return         new Request object to fetch the specified media item
   * @see            MediaItem
   */
  public static Request getMediaItem(String itemId, String albumId) {
    Request request = getMediaItems(albumId);
    request.addComponent(Request.ITEM_ID, itemId);

    return request;
  }

  /**
   * Returns a new Request instance which, when submitted, fetches all video
   * categories supported by the container and makes this data available as a
   * JSON array of JSON objects; currently works with MySpace only.
   */
  public static Request getSupportedVideoCategories() {
    Request request = new Request(restTemplate, null, "GET");
    request.addComponent(Request.ALBUM_ID, "@videos");
    request.addComponent(Request.ITEM_ID, "@supportedcategories");
    request.setGuid(ME);

    return request;
  }

  /**
   * Returns a new Request instance which, when submitted, creates a new
   * media item in the specified viewer album.
   *
   * @param  item  MediaItem object specifying the media item parameters to
   *               pass into the request; album_id must be set and other
   *               properties, e.g. type and url, can also be set
   * @return       new Request object to create a new media item
   *
   * @throws RequestException if the passed MediaItem object does not have an
   *                          album_id property set
   */
  public static Request createMediaItem(MediaItem item) throws
      RequestException {
    if (item.getAlbumId() == null || item.getAlbumId().equals("")) {
      throw new RequestException("Passed MediaItem object does not have " +
          "album_id property set");
    }

    Request request = new Request(restTemplate, "mediaItems.create", "POST");
    request.addComponent(Request.ALBUM_ID, item.getAlbumId());
    request.setGroupId(SELF);
    request.setGuid(ME);

    // Add REST payload parameters
    request.addRestPayloadParameters(item);

    return request;
  }

  /**
   * Returns a new Request instance which, when submitted, uploads the
   * specified image file as a new media item in the specified viewer album.
   *
   * @param item    MediaItem object specifying the media item parameters to
   *                pass into the request; album_id and mime_type must be set
   * @param content local image file to be uploaded as a new media item
   * @return        new Request object to upload the specified image file as a
   *                new media item
   *
   * @throws RequestException if the passed MediaItem object does not have its
   *                          album_id or mime_type properties set
   * @throws IOException      if an I/O error occurs while reading the passed
   *                          file
   */
  public static Request uploadImage(MediaItem item, File content) throws
      RequestException, IOException {
    Request request = uploadMediaItem(item, content);

    // Add REST query string parameters
    request.addRestQueryStringParameter("type", "image");

    return request;
  }

  /**
   * Returns a new Request instance which, when submitted, uploads the
   * specified video file as a new media item in the specified viewer album
   * ("@videos" for MySpace). MySpace requires the caption, description, tags,
   * msCategories, and language fields to be set for the passed MediaItem.
   *
   * @param item    MediaItem object specifying the media item parameters to
   *                pass into the request; album_id and mime_type must be set,
   *                and MySpace also requires caption, description, tags,
   *                msCategories, and language
   * @param content local video file to be uploaded as a new media item
   * @return        new Request object to upload the specified video file as a
   *                new media item
   *
   * @throws RequestException if the passed MediaItem object does not have its
   *                          album_id or mime_type properties set
   * @throws IOException      if an I/O error occurs while reading the passed
   *                          file
   */
  public static Request uploadVideo(MediaItem item, File content) throws
      RequestException, IOException {
    Request request = uploadMediaItem(item, content);

    // Add REST query string parameters
    request.addRestQueryStringParameter("type", "video");

    if (item.getCaption() != null) {
      request.addRestQueryStringParameter("caption", item.getCaption());
    }
    if (item.getDescription() != null) {
      request.addRestQueryStringParameter("description",
          item.getDescription());
    }
    if (item.hasField("msCategories")) {
      request.addRestQueryStringParameter("msCategories",
          item.getFieldAsString("msCategories"));
    }
    if (item.hasField("language")) {
      request.addRestQueryStringParameter("language",
          item.getFieldAsString("language"));
    }
    if (item.hasField("tags")) {
      request.addRestQueryStringParameter("tags",
          item.getFieldAsString("tags"));
    }

    return request;
  }

  private static Request uploadMediaItem(MediaItem item, File content) throws
      RequestException, IOException {
    if (item.getAlbumId() == null || item.getAlbumId().equals("")) {
      throw new RequestException("Passed MediaItem object does not have " +
          "album_id property set");
    }
    if (item.getMimeType() == null || item.getMimeType().equals("")) {
      throw new RequestException("Passed MediaItem object does not have " +
          "mime_type property set");
    }

    Request request = new Request(restTemplate, "mediaItems.create", "POST");
    request.addComponent(Request.ALBUM_ID, item.getAlbumId());
    request.setGroupId(SELF);
    request.setGuid(ME);

    byte[] bytes = getBytes(content);
    if (bytes != null) {
      request.setCustomPayload(bytes);
      request.setContentType(item.getMimeType());
    }

    return request;
  }

  private static byte[] getBytes(File file) throws IOException {
    byte[] buffer = new byte[1024];
    InputStream in = new FileInputStream(file);
    ByteArrayOutputStream out = new ByteArrayOutputStream();

    while(true) {
      int read = in.read(buffer);
      if (read <= 0) {
        break;
      }

      out.write(buffer, 0, read);
    }

    byte[] bytes = out.toByteArray();
    out.close();
    in.close();

    return bytes;
  }

  /**
   * Returns a new Request instance which, when submitted, updates an existing
   * media item contained within the specified viewer album.
   *
   * @param  item MediaItem object specifying the media item parameters to pass
   *              into the request; id and album_id must be set in order to
   *              specify which media item to update and values associated
   *              with any other property, e.g. type and url, are updated
   * @return      new Request object to update an existing media item
   *
   * @throws RequestException if the passed MediaItem object does not have both
   *                          id and album_id properties set
   */
  public static Request updateMediaItem(MediaItem item) throws
      RequestException {
    if (item.getId() == null || item.getId().equals("")) {
      throw new RequestException("Passed MediaItem object does not have id " +
          "property set");
    }
    if (item.getAlbumId() == null || item.getAlbumId().equals("")) {
      throw new RequestException("Passed MediaItem object does not have " +
          "album_id property set");
    }

    Request request = new Request(restTemplate, "mediaItems.update", "PUT");
    request.addComponent(Request.ALBUM_ID, item.getAlbumId());
    request.addComponent(Request.ITEM_ID, item.getId());
    request.setGroupId(SELF);
    request.setGuid(ME);

    // Add REST payload parameters
    request.addRestPayloadParameters(item);

    return request;
  }

  /**
   * Returns a new Request instance which, when submitted, deletes an existing
   * media item from the specified viewer album.
   *
   * @param  itemId  ID of media item to delete
   * @param  albumId ID of album containing media item to delete
   * @return         new Request object to delete an existing media item
   */
  public static Request deleteMediaItem(String itemId, String albumId) {
    Request request = new Request(restTemplate, "mediaItems.delete", "DELETE");
    request.addComponent(Request.ALBUM_ID, albumId);
    request.addComponent(Request.ITEM_ID, itemId);
    request.setGroupId(SELF);
    request.setGuid(ME);

    return request;
  }
}
