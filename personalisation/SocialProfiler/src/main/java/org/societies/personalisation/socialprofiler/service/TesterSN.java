package org.societies.personalisation.socialprofiler.service;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.societies.api.internal.sns.ISocialConnector;
import org.societies.personalisation.socialprofiler.SocialProfiler;
import org.societies.personalisation.socialprofiler.datamodel.GeneralInfo;
import org.societies.personalisation.socialprofiler.service.ProfilerEngine;
import org.societies.platform.socialdata.SocialData;




public class TesterSN {

	private static final Logger 			logger 							= Logger.getAnonymousLogger();
	private static String access_token = "";

    public static void main(String[] args) {
		
    	
		File f = new File("data");
		
		logger.info("PATH: "+f.getAbsolutePath());
		if (f.exists()){
			f.delete();
		}
		
		if (f.exists())
			logger.info("Neo Data is still there :(");
		else 
			logger.info("Neo Data removed");
		
		
		SocialProfiler 	 profiler 		= new SocialProfiler();
		SocialData		 socialData 	= new SocialData();
		
try {
			
			
			// prepare Connector...
			Map<String, String> pars = new HashMap<String, String>();
			pars.put(ISocialConnector.AUTH_TOKEN, access_token);
			socialData.addSocialConnector(socialData.createConnector(ISocialConnector.SocialNetwork.Facebook, pars));
		   
//		   new Thread( new Runnable() {
//				
//				@Override
//				public void run() {
//					 socialData.updateSocialData();
//					
//				}
//			}).start();
//		    
//		    
//		    Thread.sleep(10000);


			System.out.println("===================================================");
			System.out.println("=== STARTING");
			System.out.println("===================================================");
		  
		    profiler.setSocialdata(socialData); 
		    
			System.out.println("===================================================");
			System.out.println("=== START Elaboration");
			
			Thread.sleep(60000);
		    System.out.println("Social Profile  Availables:"+socialData.getSocialProfiles().size());
		    System.out.println("Social Activity Availables:"+socialData.getSocialActivity().size());
		    System.out.println("Social Friends  Availables:"+socialData.getSocialPeople().size());
		    System.out.println("Social Groups  Availables:"+socialData.getSocialGroups().size());
			    
		    String personId = "0";
			System.out.println("Retrieving info for user "+personId);
			GeneralInfo info = profiler.getGraph().getGeneralInfo(personId+"_GeneralInfo");
			System.out.println("User is: " + info.getFirstName()  + " " +info.getLastName());
			
			ArrayList<String> prefs = profiler.getGraph().getInterestsForUser(personId);
			System.out.println("Preference: " + prefs.get(prefs.size()-2));

			ArrayList<String> p = profiler.getPredominantProfileForUser(personId, ProfilerEngine.EVERYTHING);
			// 1: Narcissism, 2: super active, 3: photo, 4: surf, 5: quiz
			System.out.println("Dominant profile: " + p.get(p.size()-1) + " (1: Narcissism, 2: super active, 3: photo, 4: surf, 5: quiz)");
				
			System.out.println("===================================================");
			System.out.println("=== END Elaboration");
		}
		catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		finally {
			profiler.shutdown();
		}
	}

	
	
	
		    
}
