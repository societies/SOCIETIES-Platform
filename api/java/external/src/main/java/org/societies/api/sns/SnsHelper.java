package org.societies.api.sns;

import org.societies.api.schema.sns.socialdata.model.SocialNetwork;

public class SnsHelper {

   public static String getSocialNetworkStringName(SocialNetwork socialNetork){
       switch (socialNetork) {
          
           case FACEBOOK: return org.societies.api.schema.sns.socialdata.model.SocialNetwork.FACEBOOK.value();
           case TWITTER: return org.societies.api.schema.sns.socialdata.model.SocialNetwork.TWITTER.value();
           case LINKEDIN: return org.societies.api.schema.sns.socialdata.model.SocialNetwork.LINKEDIN.value();
           case GOOGLEPLUS: return org.societies.api.schema.sns.socialdata.model.SocialNetwork.GOOGLEPLUS.value();
           case FOURSQUARE: return org.societies.api.schema.sns.socialdata.model.SocialNetwork.FOURSQUARE.value();
           
           default: return "notAvailable";
       }
       
   }
    
}
