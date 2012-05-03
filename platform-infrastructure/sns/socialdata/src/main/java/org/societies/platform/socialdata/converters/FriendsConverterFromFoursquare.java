package org.societies.platform.socialdata.converters;

import java.util.ArrayList;
import java.util.List;

import org.apache.shindig.social.core.model.AccountImpl;
import org.apache.shindig.social.core.model.NameImpl;
import org.apache.shindig.social.core.model.PersonImpl;
import org.apache.shindig.social.opensocial.model.Account;
import org.apache.shindig.social.opensocial.model.Person;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FriendsConverterFromFoursquare implements FriendsConverter {

	private JSONObject response;
	List<Person> friends = new ArrayList<Person>();
	
	public List<Person> load(String data) {
		
		Account fq = new AccountImpl();
		fq.setDomain("foursquare.com");
		List<Account> accounts = new ArrayList<Account>();
		accounts.add(fq);

		try {
			response = new JSONObject(data);
			// System.out.println(response);
			JSONObject jdata = new JSONObject(data);
			if (response.has("response")) {
				JSONObject user = (JSONObject) response.get("response");
				// System.out.println(user);
				if (user.has("user")) {
					jdata = (JSONObject) user.get("user");
					// System.out.println(db);
					JSONObject jfriends = null;
					if (jdata.has("friends")) {
						jfriends = (JSONObject) jdata.get("friends");
					}

					int friendsCount = jfriends.getInt("count");
					JSONArray jgroups = jfriends.getJSONArray("groups");

					for (int i = 0; i < jgroups.length(); i++) {
						JSONArray itemsArray = jgroups.getJSONObject(i)
								.getJSONArray("items");
						for (int j = 0; j < itemsArray.length(); j++) {
							JSONObject jfriend = itemsArray.getJSONObject(j);
							Person p = new PersonImpl();
							p.setId("Foursquare:" + jfriend.getString("id"));
							p.setRelationshipStatus(jfriend
									.getString("relationship"));
							p.setName(new NameImpl(jfriend
									.getString("firstName")
									+ " "
									+ jfriend.getString("lastName")));
							p.setAccounts(accounts);
							// System.out.println(">>> new Friends"+p.getName().getFormatted());
							friends.add(p);
						}

					}

				}
			}

		}

		catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return friends;
	}

}
