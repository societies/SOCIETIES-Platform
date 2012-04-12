package org.societies.cis.activity.impl;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.societies.api.cis.management.ICisActivity;

@Entity
@Table(name = "Activity")
public class CisActivity implements ICisActivity {

	@Override
	public String getVerb() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setVerb(String verb) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getActor() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setActor(String actor) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getObject() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setObject(String object) {
		// TODO Auto-generated method stub
		
	}

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
