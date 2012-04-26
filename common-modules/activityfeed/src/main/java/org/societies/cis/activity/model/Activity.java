package org.societies.cis.activity.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.societies.api.activity.IActivity;
import org.societies.cis.activity.ActivityFeed;

@Entity
@Table(name = "org_societies_cis_activity_model_Activity")
public class Activity implements IActivity {
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setVerb(String verb) {
		// TODO Auto-generated method stub
		
	}
	
	@Column(name = "Actor")
	@Override
	public String getActor() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setActor(String actor) {
		// TODO Auto-generated method stub
		
	}

	@Column(name = "Object")
	@Override
	public String getObject() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setObject(String object) {
		// TODO Auto-generated method stub
		
	}

	@Column(name = "Target")
	@Override
	public String getTarget() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setTarget(String target) {
		// TODO Auto-generated method stub
		
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

}
