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

package org.opensocial.parsers;

import java.util.Map;

import org.opensocial.Response;
import org.opensocial.models.Model;

public interface Parser {

  public Response getResponseObject(String in,
      final Class<? extends Model> modelClass, String version);

  public Map<String, Response> getResponseMap(String in,
      Map<String, Class<? extends Model>> modelClasses, String version);
}
