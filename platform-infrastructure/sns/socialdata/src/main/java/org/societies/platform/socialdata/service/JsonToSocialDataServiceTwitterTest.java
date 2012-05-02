package org.societies.platform.socialdata.service;

import java.util.Iterator;
import java.util.List;

import org.apache.shindig.social.opensocial.model.ActivityEntry;
import org.apache.shindig.social.opensocial.model.Person;
import org.json.JSONException;
import org.json.JSONObject;
import org.societies.api.internal.sns.ISocialConnector;
import org.societies.platform.FacebookConn.impl.FacebookConnectorImpl;
import org.societies.platform.TwitterConnector.impl.TwitterConnectorImpl;
import org.societies.platform.socialdata.converters.FriendsConverterFromTwitter;
import org.societies.platform.socialdata.converters.PersonConverterFromTwitter;



public class JsonToSocialDataServiceTwitterTest {

	public static void main(String[] args){
		System.out.println("Convert JSON to SocialDATA");
		String access_token = "";
		ISocialConnector c = new TwitterConnectorImpl();

		//		friends test
		try {

			String dataF = c.getUserFriends();
			//			System.out.println(data);

			FriendsConverterFromTwitter parserF = new FriendsConverterFromTwitter();

			List<Person> f= parserF.load(dataF);
			//			System.out.println("p:"+p.getTurnOns().toString());
			System.out.println("friends are : ");
			Iterator<Person> it = f.iterator();
			while (it.hasNext()){
				Person p = it.next();
				System.out.println(p.getName().getFormatted() + " ("+p.getId()+")");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		//		profile test
		try {

			String dataPro = c.getUserProfile();
			System.out.println(dataPro);

			PersonConverterFromTwitter parserP = new PersonConverterFromTwitter();

			//			Person pro= parserP.load(dataPro);
			Person profile = parserP.load(dataPro);
			System.out.println("profile:"
					+"\n Name : " + profile.getName().getFormatted()
					+"\n Dsiplay Name : "+profile.getDisplayName()
					+"\n Short description : "+profile.getAboutMe()
					+"\n home location : "+profile.getCurrentLocation().getFormatted());
			//			System.out.println("friends are : ");
			//			Iterator<Person> it = f.iterator();
			//			while (it.hasNext()){
			//				Person p = it.next();
			//				System.out.println(p.getName().getFormatted() + " ("+p.getId()+")");
			//			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
