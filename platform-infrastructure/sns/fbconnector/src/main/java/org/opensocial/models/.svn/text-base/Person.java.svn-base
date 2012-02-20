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

import java.util.Map;

/**
 * OpenSocial model class representing a person/user. For reference:
 * http://wiki.opensocial.org/index.php?title=Opensocial.Person_(v0.9)
 * http://www.opensocial.org/Technical-Resources/opensocial-spec-v09/REST-API.html#rfc.section.3.2
 *
 * @author Jason Cooper
 */
public class Person extends Model {

  /**
   * Returns the user's OpenSocial ID.
   */
  public String getId() {
    return (String) getField("id");
  }

  /**
   * Returns the user's display name or nickname; if neither field is set,
   * "unknown" is returned.
   */
  public String getDisplayName() {
    StringBuilder name = new StringBuilder();

    if (getField("displayName") != null) {
      name.append(getField("displayName"));
    } else if (getField("nickname") != null) {
      name.append(getField("nickname"));
    } else if (getField("name") != null) {
      if (isFieldMultikeyed("name")) {
        Map nameMap = getFieldAsMap("name");

        if (nameMap.containsKey("givenName")) {
          name.append(nameMap.get("givenName"));
        }
        if (nameMap.containsKey("givenName") &&
            nameMap.containsKey("familyName")) {
          name.append(" ");
        }
        if (nameMap.containsKey("familyName")) {
          name.append(nameMap.get("familyName"));
        }
      } else {
        name.append((String) getField("name"));
      }
    } else {
      return "unknown";
    }

    return name.toString();
  }

  /**
   * Returns the user's photo thumbnail URL as a string.
   */
  public String getThumbnailUrl() {
    return (String) getField("thumbnailUrl");
  }
}
