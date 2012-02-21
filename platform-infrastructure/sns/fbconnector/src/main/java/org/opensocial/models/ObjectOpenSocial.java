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

import java.util.List;


public class ObjectOpenSocial extends Model {

  /**
  * 
  */
  private static final long serialVersionUID = -4554346836068745238L;
  public static final String OBJECTTYPE = "objectType";
  //public static final String ID = "id";
  public static final String NUMLIKES = "numLikes";
  public static final String NUMCOMMENTS = "numComments";
  public static final String NUMRETWEETS = "numRetweets";
	
  
  public static final String SUMMARY = "summary";
  public static final String PUBLISHED = "published";
  public static final String ATTACHMENTS = "attachments";
  public static final String DISPLAYNAME = "displayName";
  public static final String URL = "url";

  public List<ObjectOpenSocial> attachments = null; 
  
  /**
   * Returns the id identifier.
   
  public String getId() {
    return getFieldAsString("id");
  }
  */

  /**
   * @param the id to set
  
  public void setId(String id) {
   put(ID, id);
  }
  */
  
  
  
  /**
   * Returns the object type
   */
  public String getObjectType() {
    return getFieldAsString(OBJECTTYPE);
  }

  /**
   * @param the object type to set
  */
  public void setObjectType(String objectType) {
   put(OBJECTTYPE, objectType);
  }
  

  /**
   * Returns the num likes
   */
  public long getNumLikes() {
    return (Long) getField(NUMLIKES);
  }
  
  
 
  /**
    * @param num likes to set
   */
  public void setNumLikes(long numLikes) {
    put(NUMLIKES, numLikes);
  }

  /**
   * Returns the num comments
   */
  public long getNumComments() {
    return (Long) getField(NUMCOMMENTS);
  }
  
  /**
   * @param the num comments to set
  */
  public void setNumComments(long commsCount) {
   put(NUMCOMMENTS, commsCount);
  }
  
  /**
   * Returns the published
   */
  public String getPublished() {
    return getFieldAsString(PUBLISHED);
  }
  
  /**
   * @param the published to set
  */
  public void setPublished(String published) {
   put(PUBLISHED, published);
  }
  
  /**
   * Returns the summary
   */
  public String getSummary() {
    return getFieldAsString(SUMMARY);
  }
  
  /**
   * @param the summary to set
  */
  public void setSummary(String summary) {
   put(SUMMARY, summary);
  }
  
  //set Author
  public void setAuthor(Author author) {
      //addToListField("actor", actor); 
  	setField("author",author);
  }
  
  /**
   * Returns the item's display name.
   */
  public String getDisplayName() {
    return getFieldAsString(DISPLAYNAME);
  }
  
  /**
   * 
   * @param the displayname to set
   */
  public void setDisplayName(String displayname) {
    put(DISPLAYNAME, displayname);
  }
  
  /**
   * Returns the Open Social attachments
   */
  public List<ObjectOpenSocial> getAttachments() {
    return getFieldAsList(ATTACHMENTS);
  }
  
  /**
   * @param the attachments to set
  */
  public void setAttachments(List<ObjectOpenSocial> objects) {
   put(ATTACHMENTS, objects);
  }
  
  /**
   * Returns the num retweets
   */
  public long getNumRetweets() {
    return (Long) getField(NUMRETWEETS);
  }

  /**
    * @param num retweets to set
   */
  public void setNumRetweets(long numRetweets) {
    put(NUMRETWEETS, numRetweets);
  }
  
  /**
   * Returns the item's url .
   */
  public String getUrl() {
    return getFieldAsString(URL);
  }
  
  /**
   * 
   * @param the url to set
   */
  public void setUrl(String url) {
    put(URL, url);
  }
  
  
}
