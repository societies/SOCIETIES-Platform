package org.societies.orchestration.cpa.test.util;


import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.lang.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

/**
 * Object representing a status.
 */
public class Status {
  private transient static final JsonParser parser = new JsonParser();

  private long id;
  private String screenName;
  private String createdAt;
  private String text;
  private transient JsonObject jsonObject;
  private transient String jsonString;

  protected Status() {}
  
  public long getId() {
    return id;
  }

  public String getText() {
    return text;
  }

  public String getCreatedAt() {
    return createdAt;
  }

  public String getScreenName() {
    return screenName;
  }

  public JsonObject getJsonObject() {
    return jsonObject;
  }

  public String getJsonString() {
    return jsonString;
  }

  public static Status fromJson(String json) {
    Preconditions.checkNotNull(json);

    JsonObject obj = (JsonObject) parser.parse(json);
    if (obj.get("html") == null)
      return null;

    Status status = new Status();
    String html = obj.get("html").getAsString();
    html = StringEscapeUtils.unescapeXml(html);
    
    // use some jsoup magic to parse html and fetch require elements
    org.jsoup.nodes.Document document = Jsoup.parse(html);

    Element dateElement = document.select("a[class*=tweet-timestamp]").last();
    status.setCreatedAt(dateElement.text());

    Element textElement = document.select("p[class*=js-tweet-text]").first();
    status.setText(textElement.text());

    String idRaw = parseUrlGetLastElementInPath(obj.get("url").getAsString());
    status.setId(Long.parseLong(idRaw));

    status.setScreenName(parseUrlGetLastElementInPath(obj.get("author_url").getAsString()));

    // TODO: We need to parse out the other fields.

    status.jsonObject = obj;
    status.jsonString = json;

    return status;
  }
  
  /**
   * Brittle implementation to get twitter data from webpage instead of api
   * @param html
   * @return
   */
  public static Status fromHtml(String html) {
    Preconditions.checkNotNull(html);
    // use some jsoup magic to parse html and fetch require elements
    org.jsoup.nodes.Document document = Jsoup.parse(html);
    Status status = new Status();
    
    Element dateElement = document.select("div.client-and-actions span.metadata span[title]").last();
    status.setCreatedAt(dateElement.text());


    Element textElement = document.select("p.js-tweet-text").first();
    status.setText(textElement.text());

    
    Element dataElement = document.select("div.permalink-tweet").first();
    
    status.setId(Long.parseLong(dataElement.attr("data-tweet-id")));

    status.setScreenName(dataElement.attr("data-screen-name"));

      return status;
  }

  private static String parseUrlGetLastElementInPath(String string) {
    String[] split = string.split("/");
    String idRaw = split[split.length-1];
    return idRaw;
  }

    public void setId(long id) {
        this.id = id;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setText(String text) {
        this.text = text;
    }
}
