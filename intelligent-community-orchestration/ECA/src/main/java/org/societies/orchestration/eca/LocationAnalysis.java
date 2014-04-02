package org.societies.orchestration.eca;

import java.util.Date;
import java.util.HashMap;
import java.util.Set;

public class LocationAnalysis {
	
	HashMap<String, HashMap<Date, String>> userMap;
	
	public LocationAnalysis() {
		userMap = new HashMap<String, HashMap<Date, String>>();
		
		HashMap<Date, String> user1 = new HashMap<Date, String>();
		HashMap<Date, String> user2 = new HashMap<Date, String>();
		HashMap<Date, String> user3 = new HashMap<Date, String>();
		HashMap<Date, String> user4 = new HashMap<Date, String>();
		
		String work = "work";
		String shop = "work";
		String user1home = "user1Home";
		String user2home = "user2Home";
		String user3home = "user3Home";
		String user4home = "user4Home";
		
		//POPULATE
		Date d = new Date();
		d.setHours(9);
		user1.put(d, work);
		
		
		d.setHours(11);
		user2.put(d, work);
		user4.put(d, shop);
		
		d.setHours(12);
		user4.put(d, user4home);
	
		d.setHours(13);
		user3.put(d, shop);
		
		d.setHours(14);
		user3.put(d, user3home);
		
		d.setHours(17);
		user1.put(d, user1home);
		d.setHours(19);
		user2.put(d, user2home);
		

		
		d.setHours(23);
		
		userMap.put("user1", user1);
		userMap.put("user2", user2);
		userMap.put("user3", user3);
		userMap.put("user4", user4);
		
	}
	
	public void analyse() {
		Set<String> users = userMap.keySet();
		for(String user : users) {
			userMap.get(user).values();
		}
	}

}
