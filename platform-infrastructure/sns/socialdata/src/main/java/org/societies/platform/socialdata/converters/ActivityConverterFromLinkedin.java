package org.societies.platform.socialdata.converters;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.shindig.protocol.model.ExtendableBean;
import org.apache.shindig.protocol.model.ExtendableBeanImpl;
import org.apache.shindig.social.core.model.AccountImpl;
import org.apache.shindig.social.core.model.ActivityEntryImpl;
import org.apache.shindig.social.core.model.ActivityObjectImpl;
import org.apache.shindig.social.core.model.NameImpl;
import org.apache.shindig.social.core.model.PersonImpl;
import org.apache.shindig.social.opensocial.model.Account;
import org.apache.shindig.social.opensocial.model.ActivityEntry;
import org.apache.shindig.social.opensocial.model.ActivityObject;
import org.apache.shindig.social.opensocial.model.Person;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ActivityConverterFromLinkedin implements ActivityConverter {

	
	
	@Override
	public List<ActivityEntry> load(String data) {
		ArrayList<ActivityEntry> activities = new ArrayList<ActivityEntry>();
		

		return activities;
	}
}