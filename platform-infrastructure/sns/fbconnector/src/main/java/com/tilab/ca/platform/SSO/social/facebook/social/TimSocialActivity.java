package com.tilab.ca.platform.SSO.social.facebook.social;

import java.util.Date;
import java.util.List;

import org.opensocial.models.Activity;
import org.opensocial.models.Actor;
import org.opensocial.models.Location;
import org.opensocial.models.ObjectOpenSocial;
import org.opensocial.models.Tags;

public class TimSocialActivity extends Activity implements Comparable<TimSocialActivity> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2037805828725949493L;

	/* COSTANTS */
	
	public static final String postVerb 			= "post";
	
	public static final String objectTypeNote 	 	= "note";
	public static final String objectTypeComment 	= "comment";
	public static final String objectTypeLike 	 	= "like";
	public static final String objectTypePhoto 		= "photo";
	public static final String objectTypeVideo 		= "video";
	public static final String objectTypeLink 		= "link";
	
	public static final String objectTypeService 	= "service";
	public static final String objectTypeTweet 		= "tweet";
	
	public static final String PUBLISHED 			= "published";
	public static final String ACTOR 				= "actor";
	public static final String VERB					= "verb";
	public static final String OBJECT				= "object";
	public static final String TAGS					= "tags";
	public static final String LOCATION 			= "location";
	public static final String TITLE 				= "title";
	public static final String OBJECT_TYPE			= "objectType";
	
	
	Date publishedTime = null;

	// set published
    public void setPublished(String published) {
        setField(PUBLISHED, published); 
    }

    // set Actor
    public void setActor(Actor actor) {
        //addToListField("actor", actor); 
    	setField(ACTOR,actor);
    }
    
    // set verb
    public void setVerb(String verb) {
        setField(VERB, verb); 
    }
    
    // set Location
    public void setLocation(Location location){
    	setField(LOCATION,location);
    }
    
    // set Object
    public void setObject(ObjectOpenSocial object) {
        setField(OBJECT, object); 
    }
    
    // set Tags
    public void setTags(List<ObjectOpenSocial> tags) {
        setField(TAGS, tags); 
    }
    
    // getters
    public List<ObjectOpenSocial> getTags(){
    	return (List<ObjectOpenSocial>) ((Tags)getField(TAGS)).getObjects() ;
    }
    public Actor getActor(){
    	return (Actor)getField(ACTOR);
    }
    public ObjectOpenSocial getObject(){
    	return (ObjectOpenSocial)getField(OBJECT);
    }
    public Location getLocation(){
    	return (Location)getField(LOCATION);
    }
    public String getTitle(){
    	return (String)getField(TITLE);
    }
    public String getVerb(){
    	return (String)getField(VERB);
    }
	
    /**
	 * @return the publishedTime
	 */
	public Date getPublishedTime() {
		return publishedTime;
	}

	/**
	 * @param publishedTime the publishedTime to set
	 */
	public void setPublishedTime(Date publishedTime) {
		this.publishedTime = publishedTime;
	}

	
	public int compareTo(TimSocialActivity t) {
		return t.getPublishedTime().compareTo(getPublishedTime());
	}

}
