package com.tilab.ca.platform.SSO.social.facebook.social;

import java.util.ArrayList;
import java.util.Iterator;

public class TestSocialActivities {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		String luca_fb_token = "AAAFPIhZAkC90BAOxwcXg96DZBfxH5ZAgCL7vEMRgb59gTtfAWxZCn0TAfOmvFSeKZCGko3hi85jeC8X6mTfzAQrlIRSWb7P732RM9IJHBeAZDZD";
		String luca_fb_secret_token  = "";
		FaceBookOpenSocialConnector connector = new FaceBookOpenSocialConnector();
		ArrayList<TimSocialActivity> activities =  connector.getActivityStreams(luca_fb_token, luca_fb_secret_token);
		Iterator<TimSocialActivity> it = activities.iterator();
		System.out.println(" TEST AcTIVITIES Stream");
		while (it.hasNext()){
			TimSocialActivity act = it.next();
			System.out.println("Activity: "+ act.getTitle() + " - " +act.getBody() +  "@"+act.getLocation());
		}

	}

}
