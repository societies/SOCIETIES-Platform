package org.societies.cis.activity.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.societies.api.cis.management.ICisActivity;

@Entity
@Table(name = "Activity")
public class CisActivity implements ICisActivity {
	
	public CisActivity(){}
	public CisActivity(ICisActivity icis)
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

}
