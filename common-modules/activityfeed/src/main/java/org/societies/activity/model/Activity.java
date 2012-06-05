package org.societies.activity.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.shindig.social.core.model.ActivityEntryImpl;
import org.apache.shindig.social.opensocial.model.ActivityObject;
import org.societies.activity.ActivityFeed;
import org.societies.api.activity.IActivity;

@Entity
@Table(name = "org_societies_cis_activity_model_Activity")
public class Activity implements IActivity {
	/**
	 * Serializable .. 
	 */
	private static final long serialVersionUID = 1L;
	private ActivityEntryImpl act;
	private String verb;
	private String actor;
	private String object;
	private String subject;
	private String target;
	private long time;
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private String id;
	
	@ManyToOne
	private ActivityFeed feed;
	public Activity(){}
	public Activity(IActivity icis)
	{
		this.setActor(icis.getActor());
		this.setObject(icis.getObject());
		this.setTarget(icis.getTarget());
		this.setVerb(icis.getVerb());
	}
	
	@Column(name = "Verb")
	@Override
	public String getVerb() {
		return verb;
	}

	@Override
	public void setVerb(String verb) {
		this.verb = verb;
		
	}
	
	@Column(name = "Actor")
	@Override
	public String getActor() {
		return actor;
	}

	@Override
	public void setActor(String actor) {
		this.actor = actor;
	}

	@Column(name = "Object")
	@Override
	public String getObject() {
		return object;
	}

	@Override
	public void setObject(String object) {
		this.object = object;
		
	}

	@Column(name = "Target")
	@Override
	public String getTarget() {
		return target;
	}

	@Override
	public void setTarget(String target) {
		this.target = target;
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
	public ActivityEntryImpl getAct() {
		return act;
	}
	public void setAct(ActivityEntryImpl act) {
		this.act = act;
	}
	@Override
	public long getTime() {
		return time;
	}
	@Override
	public void setTime(long time) {
		this.time = time;
	}
}
