package org.societies.platform.socialdata.service;


import java.util.Iterator;
import java.util.List;

import org.apache.shindig.social.opensocial.model.ActivityEntry;
import org.apache.shindig.social.opensocial.model.Person;
import org.societies.api.internal.sns.ISocialConnector;
import org.societies.platform.TwitterConnector.impl.TwitterConnectorImpl;
import org.societies.platform.socialdata.converters.ActivityConverterFromTwitter;
import org.societies.platform.socialdata.converters.FriendsConverterFromTwitter;
import org.societies.platform.socialdata.converters.PersonConverterFromTwitter;




public class JsonToSocialDataServiceTwitterTest {

	public static void main(String[] args){
		System.out.println("Convert JSON to SocialDATA");
		String access_token = "13642262-wzt0KQGjadF1GAK48lUsdigcYHTwn3bjtfnBFWcxh,gbhIavlWyOUBcNY22bWSjextPzPBITJVJ3xPJ7oIliA";
//		String access_token = "468234144-7jBtrulMAriO1yjg2J9POY6aeW2TwnwrEXWeDWYn,1lY5pClLbeJ2MGC8A9995Dlx7gxNqdnLPQarsplwLpU";
//		String access_token = " , ";
		ISocialConnector c = new TwitterConnectorImpl(access_token,"dingqi");

		//		friends test
		try {

			String dataF = c.getUserFriends();
						System.out.println("\n"+dataF);

			FriendsConverterFromTwitter parserF = new FriendsConverterFromTwitter();

			List<Person> f= parserF.load(dataF);
			//			System.out.println("p:"+p.getTurnOns().toString());
			System.out.println("friends are : ");
			Iterator<Person> it = f.iterator();
			while (it.hasNext()){
				Person p = it.next();
//				System.out.println(p.getName().getFormatted() + " ("+p.getId()+")");
				System.out.println(" ("+p.getId()+")");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		//		profile test
		try {

			String dataPro = c.getUserProfile();
			System.out.println("\n"+dataPro);

			PersonConverterFromTwitter parserP = new PersonConverterFromTwitter();

			//			Person pro= parserP.load(dataPro);
			Person profile = parserP.load(dataPro);
			System.out.println("profile:");
			System.out.println("\n Name : " + profile.getName().getFormatted()
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

		//		activity test
		try {

			String dataAct = c.getUserActivities();
			System.out.println("\n"+dataAct);

			ActivityConverterFromTwitter parserA = new ActivityConverterFromTwitter();

			//			Person pro= parserP.load(dataPro);
			List<ActivityEntry> activities = parserA.load(dataAct);
			System.out.println("Activity:");
			Iterator<ActivityEntry> it = activities.iterator();
			while (it.hasNext()){
				ActivityEntry elm = it.next();
				System.out.println(elm.getActor().getDisplayName() + " "+ elm.getVerb() +" : " + elm.getContent()+" "+ elm.getPublished()+" "+elm.getObject().getObjectType());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
