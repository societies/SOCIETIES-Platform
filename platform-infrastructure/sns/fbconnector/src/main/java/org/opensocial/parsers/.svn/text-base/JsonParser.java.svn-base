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

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ContainerFactory;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.opensocial.Response;
import org.opensocial.models.Model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class JsonParser implements Parser {

  public Response getResponseObject(String json,
      final Class<? extends Model> modelClass, String version) {
    if (version.equals("0.8")) {
      return getResponseObject0p8(json, modelClass);
    } else if (version.equals("0.9")) {
      return getResponseObject0p9(json, modelClass);
    }

    return null;
  }

  public Map<String, Response> getResponseMap(String json,
      Map<String, Class<? extends Model>> modelClasses, String version) {
    if (version.equals("0.8")) {
      return getResponseMap0p8(json, modelClasses);
    } else if (version.equals("0.9")) {

    }

    return null;
  }

  private Response getResponseObject0p8(String json,
      final Class<? extends Model> modelClass) {
    Response response = new Response();

    JSONParser parser = new JSONParser();
    ContainerFactory containerFactory = getContainerFactory(modelClass);

    try {
      Map rootObject = (Map) parser.parse(json, containerFactory);

      if (rootObject.containsKey("startIndex")) {
        response.setTotalResults(rootObject.get("startIndex"));
      }
      if (rootObject.containsKey("totalResults")) {
        response.setTotalResults(rootObject.get("totalResults"));
      }
      if (rootObject.containsKey("entry")) {
        Object entry = rootObject.get("entry");
        if (entry.getClass().equals(JSONArray.class)) {
          for (int i = 0; i < ((List) entry).size(); i++) {
            response.getEntries().add((Model) ((List) entry).get(i));
          }
        } else if (entry.getClass().equals(modelClass)) {
          response.getEntries().add((Model) entry);
        }
      }
    } catch (ParseException e) {
      return null;
    }

    return response;
  }

  private Map<String, Response> getResponseMap0p8(String json,
      Map<String, Class<? extends Model>> modelClasses) {
    Map<String, Response> responses = new HashMap<String, Response>();

    JSONParser parser = new JSONParser();
    ContainerFactory containerFactory = getContainerFactory(Model.class);

    try {
      List<Map> rootArray = (List<Map>) parser.parse(json, containerFactory);

      for (Map responseObject : rootArray) {
        String id = null;
        Class<? extends Model> modelClass = null;
        Response response = new Response();

        if (responseObject.containsKey("id")) {
          id = (String) responseObject.get("id");
          modelClass = modelClasses.get(id);
        }

        if (responseObject.containsKey("data")) {
          Map dataObject = (Map) responseObject.get("data");

          if (dataObject.containsKey("startIndex")) {
            response.setStartIndex(dataObject.get("startIndex"));
          }
          if (dataObject.containsKey("totalResults")) {
            response.setStartIndex(dataObject.get("totalResults"));
          }
          if (dataObject.containsKey("list")) {
            Object list = dataObject.get("list");
            if (list.getClass().equals(JSONArray.class)) {
              for (int i = 0; i < ((List) list).size(); i++) {
                Model object = (Model) ((List) list).get(i);
                response.getEntries().add(cloneModelObject(object,
                    modelClass));
              }
            } else if (list.getClass().equals(JSONObject.class)) {
              response.getEntries().add(cloneModelObject((Model) list,
                  modelClass));
            }
          } else {
            response.getEntries().add(cloneModelObject((Model) dataObject,
                modelClass));
          }
        }

        responses.put(id, response);
      }
    } catch (ParseException e) {
      return null;
    }

    return responses;
  }

  private Response getResponseObject0p9(String json,
      final Class<? extends Model> modelClass) {
    Response response = new Response();

    JSONParser parser = new JSONParser();
    ContainerFactory containerFactory = getContainerFactory(modelClass);

    if (json.startsWith("{")) {
      try {
        Map rootObject = (Map) parser.parse(json, containerFactory);

        if (rootObject.containsKey("startIndex")) {
          response.setTotalResults(rootObject.get("startIndex"));
        }
        if (rootObject.containsKey("totalResults")) {
          response.setTotalResults(rootObject.get("totalResults"));
        }
        if (rootObject.containsKey("itemsPerPage")) {
          response.setItemsPerPage(rootObject.get("itemsPerPage"));
        }
        if (rootObject.containsKey("statusLink")) {
          response.setStatusLink(rootObject.get("statusLink"));
        }
        if (rootObject.containsKey("isFiltered")) {
          response.setIsFiltered(rootObject.get("isFiltered"));
        }
        if (rootObject.containsKey("person")) {
          response.getEntries().add((Model) rootObject.get("person"));
        } else if (rootObject.containsKey("entry")) {
          Object entry = rootObject.get("entry");
          if (entry.getClass().equals(JSONArray.class)) {
            for (int i = 0; i < ((List) entry).size(); i++) {
              Map currentEntry = (Map) ((List) entry).get(i);
              if (currentEntry.containsKey("person")) {
                response.getEntries().add((Model) currentEntry.get("person"));
              } else if (currentEntry.containsKey("activity")) {
                response.getEntries().add(
                    (Model) currentEntry.get("activity"));
              } else if (currentEntry.containsKey("album")) {
                response.getEntries().add((Model) currentEntry.get("album"));
              } else if (currentEntry.containsKey("mediaItem")) {
                response.getEntries().add(
                    (Model) currentEntry.get("mediaItem"));
              }
              else {
                response.getEntries().add((Model) currentEntry);
              }
            }
          }
        } else if (rootObject.containsKey("album")) {
          response.getEntries().add((Model) rootObject.get("album"));
        } else if (rootObject.containsKey("mediaItem")) {
          response.getEntries().add((Model) rootObject.get("mediaItem"));
        } else {
          response.getEntries().add((Model) rootObject);
        }
      } catch (ParseException e) {
        return null;
      }
    } else if (json.startsWith("[")) {
      try {
        List<Map> rootArray = (List<Map>) parser.parse(json, containerFactory);
        for (Map responseObject : rootArray) {
          response.getEntries().add((Model) responseObject);
        }
      } catch (ParseException e) {
        return null;
      }
    }

    return response;
  }

  private static ContainerFactory getContainerFactory(
      final Class<? extends Model> modelClass) {
    ContainerFactory containerFactory = new ContainerFactory() {
      public List creatArrayContainer() {
        return new JSONArray();
      }

      public Map createObjectContainer() {
        try {
          return modelClass.newInstance();
        } catch (InstantiationException e) {
          return new Model();
        } catch (IllegalAccessException e) {
          return new Model();
        }
      }
    };

    return containerFactory;
  }

  private static Model cloneModelObject(Model model,
      final Class<? extends Model> modelClass) {
    Model clone = null;
    try {
      clone = modelClass.newInstance();
    } catch (InstantiationException e) {
      return model;
    } catch (IllegalAccessException e) {
      return model;
    }

    for (Map.Entry entry : (Set<Map.Entry>) model.entrySet()) {
      clone.put(entry.getKey(), entry.getValue());
    }

    return clone;
  }
}
