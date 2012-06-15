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
	private HashMap<String,ActivityString> data = null;
	private long time;
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@ManyToOne
	private ActivityFeed feed;
	public Activity()
	{
		data = new HashMap<String,ActivityString>();
		this.setActor("");
		this.setObject("");
		this.setTarget("");
		this.setVerb("");
		this.setPublished("");
	}
	public Activity(IActivity icis)
	{
		data = new HashMap<String,ActivityString>();
		this.setActor(icis.getActor());
		this.setObject(icis.getObject());
		this.setTarget(icis.getTarget());
		this.setVerb(icis.getVerb());
		this.setPublished(icis.getPublished());
	}
	
	@Column(name="verb")
	@Override
	public String getVerb() {
		return data.get("verb").toString();
	}

	@Column(name="verb")
	@Override
	public void setVerb(String verb) {
		data.put("verb", new ActivityString(verb));
		
	}
	@Column(name="actor")
	@Override
	public String getActor() {
		return data.get("actor").toString();
	}
	@Column(name="actor")
	@Override
	public void setActor(String actor) {
		data.put("actor", new ActivityString(actor));
	}
	@Column(name="object")
	@Override
	public String getObject() {
		return data.get("object").toString();
	}
	@Column(name="object")
	@Override
	public void setObject(String object) {
		data.put("object", new ActivityString(object));
		
	}
	@Column(name="target")
	@Override
	public String getTarget() {
		return data.get("target").toString();
	}
	@Column(name="target")
	@Override
	public void setTarget(String target) {
		data.put("target", new ActivityString(target));
	}
	public ActivityFeed getFeed() {
		return feed;
	}
	public void setFeed(ActivityFeed feed) {
		this.feed = feed;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
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
		return data.get("published").toString();
	}
	@Override
	public void setPublished(String published) {
		data.put("published", new ActivityString(published));
		
	}
	public ActivityString getValue(String key){
		return data.get(key);
	}
}
