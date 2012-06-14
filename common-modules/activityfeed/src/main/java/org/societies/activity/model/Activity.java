package org.societies.activity.model;

import java.util.HashMap;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.societies.activity.ActivityFeed;
import org.societies.api.activity.IActivity;

@Entity
@Table(name = "org_societies_cis_activity_model_Activity")
public class Activity implements IActivity {
	/**
	 * Serializable .. 
	 */
	private static final long serialVersionUID = 1L;
	@Transient
	private HashMap<String,String> data = null;
	private long time;
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private String id;
	
	@ManyToOne
	private ActivityFeed feed;
	public Activity(){data = new HashMap<String,String>();}
	public Activity(IActivity icis)
	{
		data = new HashMap<String,String>();
		this.setActor(icis.getActor());
		this.setObject(icis.getObject());
		this.setTarget(icis.getTarget());
		this.setVerb(icis.getVerb());
		this.setPublished(icis.getPublished());
	}
	
	@Column(name="verb")
	@Override
	public String getVerb() {
		return data.get("verb");
	}

	@Column(name="verb")
	@Override
	public void setVerb(String verb) {
		data.put("verb", verb);
		
	}
	@Column(name="actor")
	@Override
	public String getActor() {
		return data.get("actor");
	}
	@Column(name="actor")
	@Override
	public void setActor(String actor) {
		data.put("actor", actor);
	}
	@Column(name="object")
	@Override
	public String getObject() {
		return data.get("object");
	}
	@Column(name="object")
	@Override
	public void setObject(String object) {
		data.put("object", object);
		
	}
	@Column(name="target")
	@Override
	public String getTarget() {
		return data.get("target");
	}
	@Column(name="target")
	@Override
	public void setTarget(String target) {
		data.put("target", target);
	}
	public ActivityFeed getFeed() {
		return feed;
	}
	public void setFeed(ActivityFeed feed) {
		this.feed = feed;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	@Override
	public long getTime() {
		return time;
	}
	@Override
	public void setTime(long time) {
		this.time = time;
	}
	@Override
	public String getPublished() {
		return data.get("published");
	}
	@Override
	public void setPublished(String published) {
		data.put("publised", published);
		
	}
}
