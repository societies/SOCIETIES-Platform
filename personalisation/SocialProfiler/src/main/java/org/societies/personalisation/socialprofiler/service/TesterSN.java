package org.societies.personalisation.socialprofiler.service;
import java.util.Properties;
import java.util.logging.Logger;




public class TesterSN {

	private static final Logger 			logger 							= Logger.getAnonymousLogger();
	private static Properties props = new Properties();
	
    public static void main(String[] args) {
		
//    	
//		File f = new File("data");
//		
//		logger.info("PATH: "+f.getAbsolutePath());
//		if (f.exists()){
//			f.delete();
//		}
//		
//		if (f.exists())
//			logger.info("Neo Data is still there :(");
//		else 
//			logger.info("Neo Data removed");
//		
//		
//		SocialProfiler 	 profiler 		= new SocialProfiler();
//		SocialData		 socialData 	= new SocialData();
//
//		try{
//			logger.finest("Read keys...");
//			
//			InputStream inputStream = new FileInputStream(new File("config.properties"));
//            if (inputStream!=null){
//				props.load(inputStream);
//	            inputStream.close();
//	        }
//           
//            logger.info("engine activated.");
//            
//			// prepare Connector...
//			Map<String, String> pars = new HashMap<String, String>();
//			pars.put(ISocialConnector.AUTH_TOKEN, props.getProperty("facebook.token"));
//			socialData.addSocialConnector(
//					socialData.createConnector(ISocialConnector.SocialNetwork.Facebook, pars));
//
//			pars.put(ISocialConnector.AUTH_TOKEN, props.getProperty("twitter.token"));
//			socialData.addSocialConnector(
//					socialData.createConnector(ISocialConnector.SocialNetwork.twitter, pars));
//		   
//			pars.put(ISocialConnector.AUTH_TOKEN, props.getProperty("foursquare.token"));
//			socialData.addSocialConnector(
//					socialData.createConnector(ISocialConnector.SocialNetwork.Foursquare, pars));
//		   
//			System.out.println("===================================================");
//			System.out.println("=== STARTING");
//			System.out.println("===================================================");
//		  
//		    profiler.setSocialdata(socialData); 
//		    
//			System.out.println("===================================================");
//			System.out.println("=== START Elaboration");
//			
//			Thread.sleep(180000);
//		    System.out.println("Social Profiles:   "+socialData.getSocialProfiles().size());
//		    System.out.println("Social Activities: "+socialData.getSocialActivity().size());
//		    System.out.println("Social Friends:    "+socialData.getSocialPeople().size());
//		    System.out.println("Social Groups:     "+socialData.getSocialGroups().size());
//			    
//		    String personId = "0";
//			System.out.println("Retrieving info for user "+personId);
//			GeneralInfo info = profiler.getGraph().getGeneralInfo(personId+"_GeneralInfo");
//			System.out.println("User is: " + info.getFirstName()  + " " +info.getLastName());
//			
//			ArrayList<String> prefs = profiler.getGraph().getInterestsForUser(personId);
//			if (prefs != null && prefs.size() >= 2)
//				System.out.println("Main category of interest: " + prefs.get(prefs.size()-2));
//
//			ArrayList<String> p = profiler.getPredominantProfileForUser(personId, ProfilerEngine.EVERYTHING);
//			// 1: Narcissism, 2: super active, 3: photo, 4: surf, 5: quiz
//			if (p != null)
//					System.out.println("Dominant profile: " + p.get(p.size()-1) + " (1: Narcissism, 2: super active, 3: photo, 4: surf, 5: quiz)");
//				
//			System.out.println("===================================================");
//			System.out.println("=== END Elaboration");
//		}
//		catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//		finally {
//			profiler.shutdown();
//		}
	}

	
	
	
		    
}
