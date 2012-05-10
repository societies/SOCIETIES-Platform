package org.societies.personalisation.socialprofiler.service;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.societies.api.internal.sns.ISocialConnector;
import org.societies.personalisation.socialprofiler.SocialProfiler;
import org.societies.personalisation.socialprofiler.datamodel.GeneralInfo;
import org.societies.platform.socialdata.SocialData;




public class TesterSN {

	private static final Logger 			logger 							= Logger.getAnonymousLogger();
	private static String access_token = "AAAFs43XOj3IBAGbtrA2I7cibWs8YD1ODGr7JiqXl0ZCJ4DBkeXKeSsth9r2EbRGj6jh1eBIhUAkIZBNs1nKOJU1Ys81xKxUqZAC13DwBAZDZD";

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
		
		/////////////////////////////////////////////////////////////////
//		ISocialConnector fbConn 		= new FacebookConnectorImpl(access_token , null);
//		List <ISocialConnector> snList 	= new ArrayList<ISocialConnector>();
//		snList.add(fbConn);

		Map<String, String> pars = new HashMap<String, String>();
		pars.put(ISocialConnector.AUTH_TOKEN, access_token);
		
		
		
		try {
		
			
			
			socialData.addSocialConnector(socialData.createConnector(ISocialConnector.SocialNetwork.Facebook, pars));
		    socialData.updateSocialData();
		    
		    Thread.sleep(10000);
		    
		    System.out.println("Social Profile  Availables:"+socialData.getSocialProfiles().size());
		    System.out.println("Social Activity Availables:"+socialData.getSocialActivity().size());
		    System.out.println("Social Friends  Availables:"+socialData.getSocialPeople().size());
		  
		    profiler.setSocialdata(socialData); 
		    
			System.out.println("===================================================");
			System.out.println("=== START Elaboration");
			    
			System.out.println("Retrieving info for user 0");
			GeneralInfo info = profiler.getGraph().getGeneralInfo("0_GeneralInfo");
			System.out.println("Generate Graph for " + info.getFirstName()  + " " +info.getLastName());
				
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
			//profiler.shutdown();
		}
	}

	
	
	
		    
}
