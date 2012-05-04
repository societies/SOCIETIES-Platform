package org.societies.platform.socialdata.service;




public class JsonToSocialDataServiceFoursquareTest {

//	public static void main(String[] args){
//		System.out.println("Convert JSON to SocialDATA");
//		String access_token = "5ZAFZUGOUSFQAEDSWPCXQLJVMBFY1GDI41T5SNMUJP5B2QNA";
//		ISocialConnector c = new FoursquareConnectorImpl(access_token,"dingqi");
//
//		//		profile test
//		try {
//
//			String dataPro = c.getUserProfile();
//			System.out.println("\n"+dataPro);
//
//			PersonConverterFromFoursquare parserP = new PersonConverterFromFoursquare();
//
//			Person profile = parserP.load(dataPro);
//			System.out.println("profile:"
//					+"\n Name : " + profile.getName().getGivenName()+" "+profile.getName().getFamilyName()
//					+"\n Short description : "+profile.getAboutMe()
//					+"\n home location : "+profile.getAddresses().get(0).getFormatted()
//					+"\n email : "+profile.getEmails().get(0).getValue());
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		
////		friends test
//		try {
//
//			String dataF = c.getUserFriends();
//			System.out.println("\n"+dataF);
//
//			FriendsConverterFromFoursquare parserF = new FriendsConverterFromFoursquare();
//
//			List<Person> f= parserF.load(dataF);
//			//			System.out.println("p:"+p.getTurnOns().toString());
//			System.out.println("friends are : ");
//			Iterator<Person> it = f.iterator();
//			while (it.hasNext()){
//				Person p = it.next();
//				System.out.println(p.getName().getFormatted() + " ("+p.getId()+")");
//			}
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
//			ActivityConverterFromFoursquare parserA = new ActivityConverterFromFoursquare();
//
//			//			Person pro= parserP.load(dataPro);
//			List<ActivityEntry> activities = parserA.load(dataAct);
//			System.out.println("Activity:");
//			Iterator<ActivityEntry> it = activities.iterator();
//			while (it.hasNext()){
//				ActivityEntry elm = it.next();
//				System.out.println("You "+ elm.getVerb() +" : " + elm.getContent());
//			}
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//	}

}
