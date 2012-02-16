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

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Generic OpenSocial model class. Every OpenSocial resource class, whether it
 * models concrete (e.g. person) or abstract (e.g. AppData) concepts, extends
 * this class. Instance methods provide an interface for getting and setting
 * arbitrary properties (fields) of these resources.
 *
 * @author Jason Cooper
 */
public class Model extends JSONObject {

  /**
   * Returns the complete set of properties associated with the model instance.
   */
  public String[] getFieldNames() {
    int i = 0;
    String[] fieldNames = new String[size()];

    Set<Map.Entry<String, Object>> fields = entrySet();
    for (Map.Entry<String, Object> field : fields) {
      fieldNames[i] = field.getKey();
      i++;
    }

    return fieldNames;
  }

  /**
   * Returns {@code true} if a value is associated with the specified field
   * name, {@code false} otherwise.
   *
   * @param fieldName name of field to look up
   */
  public boolean hasField(String fieldName) {
    return containsKey(fieldName);
  }

  /**
   * Returns the value of the specified field as an Object.
   *
   * @param fieldName name of field whose value is to be returned
   */
  public Object getField(String fieldName) {
    return get(fieldName);
  }

  /**
   * Returns the value of the specified field as a {@link Map}. Equivalent to
   * {@code (Map) getField(fieldName)}, hence this method will throw a
   * ClassCastException if the field does not implement Map.
   *
   * @param fieldName name of field whose value is to be returned
   * @see             ClassCastException
   */
  public Map getFieldAsMap(String fieldName) {
    return (Map) get(fieldName);
  }

  /**
   * Returns the value of the specified field as a {@link List}. Equivalent to
   * {@code (List) getField(fieldName)}, hence this method will throw a
   * ClassCastException if the field does not implement List.
   *
   * @param fieldName name of field whose value is to be returned
   * @see             ClassCastException
   */
  public List getFieldAsList(String fieldName) {
    return (List) get(fieldName);
  }

  /**
   * Returns the value of the specified field as a {@link String}. Equivalent
   * to {@code (String) getField(fieldName)}, hence this method will throw a
   * ClassCastException if the field is not of type String.
   *
   * @param fieldName name of field whose value is to be returned
   * @see             ClassCastException
   */
  public String getFieldAsString(String fieldName) {
    try {
      return (String) get(fieldName);
    } catch (ClassCastException e) {
      return "" + get(fieldName);
    }
  }

  /**
   * Returns the template parameter with the specified name.
   *
   * @param name name of template parameter whose value is to be returned
   */
  protected String getTemplateParameter(String name) {
    if (containsKey("templateParameters")) {
      List<Map<String, String>> templateParameters =
        getFieldAsList("templateParameters");

      for (Map<String, String> parameter : templateParameters) {
        if (parameter.get("key").equals(name)) {
          return parameter.get("value");
        }
      }
    }

    return null;
  }

  /**
   * Returns {@code true} if the value of the specified field implements
   * {@link Map}, {@code false} otherwise.
   *
   * @param fieldName name of field to look up
   */
  public boolean isFieldMultikeyed(String fieldName) {
    Object field = get(fieldName);
    if (field.getClass().equals(String.class) ||
        field.getClass().equals(JSONArray.class)) {
      return false;
    }

    return true;
  }

  /**
   * Returns {@code true} if the value of the specified field implements
   * {@link List}, {@code false} otherwise.
   *
   * @param fieldName name of field to look up
   */
  public boolean isFieldMultivalued(String fieldName) {
    Object field = get(fieldName);
    if (field.getClass().equals(JSONArray.class)) {
      return true;
    }

    return false;
  }

  /**
   * Sets the value of the specified field to the passed Object.
   *
   * @param fieldName name of field to set
   * @param value     object to associate with passed field name
   */
  public void setField(String fieldName, Object value) {
    put(fieldName, value);
  }

  /**
   * Adds the passed Object to the list field with the specified name.
   *
   * @param fieldName name of list field for which the passed item should be
   *                  added
   * @param item      item to add
   */
  protected void addToListField(String fieldName, Object item) {
    List<Object> listField = null;

    if (containsKey(fieldName)) {
      listField = getFieldAsList(fieldName);
    } else {
      listField = new ArrayList<Object>();
    }

    listField.add(item);
    put(fieldName, listField);
  }

  /**
   * Adds a new template parameter with the specified name and value.
   *
   * @param name  name of new template parameter to add
   * @param value value of template parameter to associate with passed name
   */
  protected void addTemplateParameter(String name, String value) {
    List<Map<String, String>> templateParameters = null;

    if (containsKey("templateParameters")) {
      templateParameters = getFieldAsList("templateParameters");
    } else {
      templateParameters = new ArrayList<Map<String, String>>();
    }

    Map<String, String> templateParameter = new HashMap<String, String>();
    templateParameter.put("key", name);
    templateParameter.put("value", value);

    templateParameters.add(templateParameter);
    put("templateParameters", templateParameters);
  }
}
