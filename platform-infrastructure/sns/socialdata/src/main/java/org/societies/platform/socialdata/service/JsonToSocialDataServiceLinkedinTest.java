package org.societies.platform.socialdata.service;


import org.societies.api.internal.sns.ISocialConnector;
import org.societies.platform.sns.connecor.linkedin.LinkedinConnector;




public class JsonToSocialDataServiceLinkedinTest {

	public static void main(String[] args){
		System.out.println("Convert JSON to SocialDATA");
		String access_token = "";
		ISocialConnector c = new LinkedinConnector(access_token,"luca");

		
		// Test FRIENDS PART
		
//		String friends = c.getUserFriends();
//		FriendsConverterFromLinkedin parserF = new FriendsConverterFromLinkedin();
//		List<Person> people = parserF.load(friends);
//		int i=0;
//		for(Person p: people){
//			System.out.println(i +")" +p.getName().getFormatted() + " works in "+ p.getJobInterests() + " in "+ p.getLivingArrangement());
//			i++;
//		}
		
		
		// PROFILE
		
//		String myself = c.getUserProfile();
//		PersonConverterFromLinkedin parserP = new PersonConverterFromLinkedin();
//		Person me = parserP.load(myself);
//		System.out.println(me.getName().getFormatted() + " has "+ me.getJobInterests());
		
		
		// Groups
		
//		String groups = c.getUserGroups();
//		GroupConverterFromLinkedin parserG = new GroupConverterFromLinkedin();
//		List<Group> list = parserG.load(groups);	
//		int i=1;
//		for(Group g: list){
//			System.out.println(i+")" +g.getId().getGroupId() + " " + g.getDescription() + " > " +g.getTitle());
//			i++;
//		}
		
		
		String activities = c.getUserActivities();
		System.out.println(activities);
		
		
//		//		friends test
//		try {
//
//			String dataF = c.getUserFriends();
//			System.out.println("\n"+dataF);
//
//			 parserF = new FriendsConverterFromTwitter();
//
//			List<Person> f= parserF.load(dataF);
//			//			System.out.println("p:"+p.getTurnOns().toString());
//			System.out.println("friends are : ");
//			Iterator<Person> it = f.iterator();
//			while (it.hasNext()){
//				Person p = it.next();
////				System.out.println(p.getName().getFormatted() + " ("+p.getId()+")");
//				System.out.println(" ("+p.getId()+")");
//			}
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		//		profile test
//		try {
//
//			String dataPro = c.getUserProfile();
//			System.out.println("\n"+dataPro);
//
//			PersonConverterFromTwitter parserP = new PersonConverterFromTwitter();
//
//			//			Person pro= parserP.load(dataPro);
//			Person profile = parserP.load(dataPro);
//			System.out.println("profile:");
//			System.out.println("\n Name : " + profile.getName().getFormatted()
//					+"\n Dsiplay Name : "+profile.getDisplayName()
//					+"\n Short description : "+profile.getAboutMe()
//					+"\n home location : "+profile.getCurrentLocation().getFormatted());
//			//			System.out.println("friends are : ");
//			//			Iterator<Person> it = f.iterator();
//			//			while (it.hasNext()){
//			//				Person p = it.next();
//			//				System.out.println(p.getName().getFormatted() + " ("+p.getId()+")");
//			//			}
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		//		activity test
//		try {
//
//			String dataAct = c.getUserActivities();
//			System.out.println("\n"+dataAct);
//
//			ActivityConverterFromTwitter parserA = new ActivityConverterFromTwitter();
//
//			//			Person pro= parserP.load(dataPro);
//			List<ActivityEntry> activities = parserA.load(dataAct);
//			System.out.println("Activity:");
//			Iterator<ActivityEntry> it = activities.iterator();
//			while (it.hasNext()){
//				ActivityEntry elm = it.next();
//				System.out.println(elm.getActor().getDisplayName() + " "+ elm.getVerb() +" : " + elm.getContent()+" "+ elm.getPublished()+" "+elm.getObject().getObjectType());
//			}
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
	}

}
