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
import org.opensocial.models.Album;

/**
 * OpenSocial API class for album requests; contains static methods for
 * fetching, creating, updating and deleting albums (collections of media
 * items).
 *
 * @author Jason Cooper
 */
public class AlbumsService extends Service {

  private static final String restTemplate =
    "albums/{guid}/{groupId}/{albumId}";

  /**
   * Returns a new Request instance which, when submitted, fetches the current
   * viewer's albums and makes this data available as a List of Album objects.
   *
   * @return new Request object to fetch the current viewer's albums
   * @see    Album
   */
  public static Request getAlbums() {
    Request request = new Request(restTemplate, "albums.get", "GET");
    request.setModelClass(Album.class);
    request.setGroupId(SELF);
    request.setGuid(ME);

    return request;
  }

  /**
   * Returns a new Request instance which, when submitted, fetches the
   * specified album and makes this data available as an Album object.
   *
   * @param  albumId ID of album to fetch
   * @return         new Request object to fetch the specified album
   * @see            Album
   */
  public static Request getAlbum(String albumId) {
    Request request = getAlbums();
    request.addComponent(Request.ALBUM_ID, albumId);

    return request;
  }

  /**
   * Returns a new Request instance which, when submitted, creates a new
   * album in the current viewer's library.
   *
   * @param  album Album object specifying the album parameters to pass into
   *               the request; typically, caption and description are set
   * @return       new Request object to create a new album
   */
  public static Request createAlbum(Album album) {
    Request request = new Request(restTemplate, "albums.create", "POST");
    request.setGroupId(SELF);
    request.setGuid(ME);

    // Add REST payload parameters
    request.addRestPayloadParameters(album);

    return request;
  }

  /**
   * Returns a new Request instance which, when submitted, updates an existing
   * album in the current viewer's library.
   *
   * @param  album Album object specifying the album parameters to pass into
   *               the request; id must be set in order to specify which album
   *               to update and values associated with any other property,
   *               e.g. caption and description, are updated
   * @return       new Request object to update an existing album
   *
   * @throws RequestException if the passed Album object does not have an id
   *                          property set
   */
  public static Request updateAlbum(Album album) throws RequestException {
    if (album.getId() == null || album.getId().equals("")) {
      throw new RequestException("Passed Album object does not have ID " +
          "property set");
    }

    Request request = new Request(restTemplate, "albums.update", "PUT");
    request.addComponent(Request.ALBUM_ID, album.getId());
    request.setGroupId(SELF);
    request.setGuid(ME);

    // Add REST payload parameters
    request.addRestPayloadParameters(album);

    return request;
  }

  /**
   * Returns a new Request instance which, when submitted, deletes an existing
   * album from the current viewer's library.
   *
   * @param  albumId ID of album to delete
   * @return         new Request object to delete an existing album
   */
  public static Request deleteAlbum(String albumId) {
    Request request = new Request(restTemplate, "albums.delete", "DELETE");
    request.addComponent(Request.ALBUM_ID, albumId);
    request.setGroupId(SELF);
    request.setGuid(ME);

    return request;
  }
}
