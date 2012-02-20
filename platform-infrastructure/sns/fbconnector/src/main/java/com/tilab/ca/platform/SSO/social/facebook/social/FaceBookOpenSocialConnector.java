package com.tilab.ca.platform.SSO.social.facebook.social;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.opensocial.models.Actor;
import org.opensocial.models.Author;
import org.opensocial.models.Image;
import org.opensocial.models.ObjectOpenSocial;

import com.restfb.Connection;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.types.CategorizedFacebookType;
import com.restfb.types.Comment;
import com.restfb.types.Location;
import com.restfb.types.NamedFacebookType;
import com.restfb.types.Post;
import com.restfb.types.Post.Comments;
import com.restfb.types.Post.Likes;

/**
 * @author
 * 
 */
public class FaceBookOpenSocialConnector implements OpenSocialConnector {

	private static FaceBookOpenSocialConnector fb = null;
	private static final String serviceName = "FB";
	SimpleDateFormat df = null;

	// DEFAUL value
	private String idBaseUrl 	= "http://socialstar.teamlife.it/activitystream/FB:";
	private String imageUrl 	=   "";//https://graph.facebook.com/FBID/picture?type=normal"+"&"+ConstantsTimSocial.FB_ACCESS_TOKEN+"=";
	private String previewUrl 	= "";//https://graph.facebook.com/OBJECTID/picture?type=normal"+"&"+ConstantsTimSocial.FB_ACCESS_TOKEN+"=";


	// max post number returned from Facebook
	private int maxPostLimit = 100;

	
	
	
	/**
	 * RestFB Graph API client.
	 */
	private FacebookClient facebookClient;

	/**
	 * Logger
	 */
	Logger log = Logger.getLogger(FaceBookOpenSocialConnector.class);

	HashMap mapTypes = null;

	/**
	 */
	public FaceBookOpenSocialConnector() {

		df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz");

		mapTypes = new HashMap();
		mapTypes.put("status", 		TimSocialActivity.objectTypeNote);
		mapTypes.put("link", 		TimSocialActivity.objectTypeLink);
		mapTypes.put("photo", 		TimSocialActivity.objectTypePhoto);
		mapTypes.put("video", 		TimSocialActivity.objectTypeVideo);

		// TODO
		// idBaseUrl = TODO CONFIGURABILE;

		// TODO
		// maxPostLimit = TODO CONFIGURABILE;

		//

		// TODO
		// if (proxy_enabled)
		// systemProperties = System.getProperties();
		// systemProperties.setProperty("http.proxyHost",proxy);
		// systemProperties.setProperty("http.proxyPort",port);

	}

	/**
	 * @return
	 */
	public static FaceBookOpenSocialConnector getInstance() {

		if (fb == null)
			fb = new FaceBookOpenSocialConnector();
		return fb;

	}

	/**
	 * 
	 */
	public static void freeInstance() {
		fb = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tilab.ca.platform.util.opensocial.connector.OpenSocialConnector#
	 * getActivityStreams(java.lang.String, java.lang.String)
	 */
	public ArrayList<TimSocialActivity> getActivityStreams(String token, String tokenSecret) {

		log.debug("Enter get ActivityStreams");

		ArrayList<TimSocialActivity> activities = new ArrayList<TimSocialActivity>();

		// JSONArray activitiesArray = new JSONArray();
		facebookClient 				= new DefaultFacebookClient(token);
		Connection<Post> myFeeds 	= facebookClient.fetchConnection("me/home", Post.class, Parameter.with("limit", maxPostLimit));

		//adding token to images
		imageUrl   	 = "https://graph.facebook.com/FBID/picture?type=normal"+"&"+ConstantsTimSocial.FB_ACCESS_TOKEN+"=";      
		previewUrl	 = "https://graph.facebook.com/OBJECTID/picture?type=normal"+"&"+ConstantsTimSocial.FB_ACCESS_TOKEN+"=";
		imageUrl	 += token;
		previewUrl 	 += token;
		
		
		String idPost    	= "";
		String idActor  	= "";
		String name 		= "";
		String message 		= "";
		long likesCount 	= 0;
		long commsCount 	= 0;
		String createdTime 	= null;
		String story 		= null;
		Location location	= null;
		
		CategorizedFacebookType fbtype   = null;
		String publishedTime			 = null;
		TimSocialActivity activity 		 = null;
		
		Actor actor = null;
		Image image = null;
		
		String actorImageUrl = null;
		String authorImageUrl = null;
		List<ObjectOpenSocial> attachments = null;
		List<ObjectOpenSocial> tags = null;
		List<ObjectOpenSocial> objectList = null;
		ObjectOpenSocial object = null;
		ObjectOpenSocial objectLike = null;
		ObjectOpenSocial objectComment = null;
		ObjectOpenSocial objectTag = null;
		Image imgActor = null;
		Image imgAuthor = null;
		String url = "";
		String objectId = null;
		String picture = null;
		
		// FB Post type: A string indicating the type for this post (including link, photo, video)
		String typeOfPost = TimSocialActivity.objectTypeNote;

		List<Post> myFeedConnectionPage = myFeeds.getData();
		for (Post post : myFeedConnectionPage)

		{
			//create attachments
			attachments = new ArrayList<ObjectOpenSocial>();

			idPost = post.getId();
			log.debug("Id: " + idPost);
			
			url = "";

			if (post.getType() != null) {
				typeOfPost = (String) mapTypes.get(post.getType());
				log.debug("type of post:" + post.getType());				
			}
			
			//Actor of the POST
			//Information about the user who posted the message
			fbtype = post.getFrom();
			log.debug("Id actor: " + fbtype.getId());
			idActor = fbtype.getId();
			log.debug("Name actor: " + fbtype.getName());
			if (fbtype.getName() != null)
				name = fbtype.getName();

			// Message Title for the post
			
			if (post.getMessage() != null)
				message = post.getMessage();
			else if(post.getDescription() != null)
				message = post.getDescription();
			
			log.debug("Message post: " + message);
			//else if(post.getCaption() != null)
				//message = post.getCaption();
			
			//else if(post.getDescription() != null)
			//	message = post.getDescription();
			
			
			actorImageUrl = imageUrl.replaceAll("FBID", idActor);
			if (idActor != null) {
				imgActor = new Image();
				imgActor.setUrl(actorImageUrl);

			}

			//Likes for this post
			Likes likes = post.getLikes();
			if (likes != null && post.getLikesCount() != null) {
				likesCount = post.getLikesCount().longValue();
				log.debug("# Likes: " + post.getLikesCount());

				Author author = null;
				for (NamedFacebookType like : likes.getData()) {
					objectLike = new ObjectOpenSocial();
					objectLike.setObjectType(TimSocialActivity.objectTypeLike);
					author = new Author();
					author.setId(like.getId());

					authorImageUrl = imageUrl.replaceAll("FBID", like.getId());
					if (like.getId() != null) {
						imgActor = new Image();
						imgActor.setUrl(authorImageUrl);
						author.setImage(imgActor);
					}

					author.setDisplayName(like.getName());
					objectLike.setAuthor(author);
					attachments.add(objectLike);
				}
			}

			//Comments for this post
			Comments comms = post.getComments();
			if (comms != null && comms.getCount() != null) {
				log.debug("# Comments: " + comms.getCount());
				commsCount = comms.getCount().longValue();

				Author author = null;
				NamedFacebookType from = null;
				for (Comment comment : comms.getData()) {
					objectComment = new ObjectOpenSocial();
					objectComment
							.setObjectType(TimSocialActivity.objectTypeComment);
					from = comment.getFrom();
					if (from != null) {
						author = new Author();
						author.setId(from.getId());

						authorImageUrl = imageUrl.replaceAll("FBID",
								from.getId());
						if (from.getId() != null) {
							imgActor = new Image();
							imgActor.setUrl(authorImageUrl);
							author.setImage(imgActor);
						}

						author.setDisplayName(from.getName());
						objectComment.setAuthor(author);
					}

					Date time = comment.getCreatedTime();
					if (time != null) {
						publishedTime = df.format(time);
						objectComment.setPublished(publishedTime);
					}
					objectComment.setSummary(comment.getMessage());
					attachments.add(objectComment);
				}

			}

			// story ?? TODO

			//URL for this post - type ->> photo / video
			objectId = post.getObjectId();
			log.debug("objectId="+objectId);
			picture = post.getPicture();
			log.debug("picture="+picture);
			
			if(objectId!=null)
			{
				url = previewUrl.replaceAll("OBJECTID", objectId);  
			}
			else if (picture!=null)
			{
				url = picture;
			}
			
			/*if(typeOfPost.equals(TimSocialActivity.objectTypePhoto))
			{
				url = post.getPicture();
				log.debug("photo url="+url);
			}
			
			if(typeOfPost.equals(TimSocialActivity.objectTypeVideo))
			{
				url = post.getSource();
				log.debug("video url="+url);
			}*/
	
			// Created time for the POST
			// 
			Date dateTime = post.getCreatedTime();
			if (dateTime != null) {
				publishedTime = df.format(dateTime);
				log.debug("Created Time: " + publishedTime);
			}

			Post.Place place = post.getPlace();
			String osPosition = "";
			if (place != null) {
				location = place.getLocation();

				if (location != null) {
					osPosition = "+" + location.getLatitude() + "+"
							+ location.getLongitude() + "/";
				}

			}

			tags = new ArrayList<ObjectOpenSocial>();
			objectTag = new ObjectOpenSocial();
			objectTag.setObjectType(TimSocialActivity.objectTypeService);
			objectTag.setDisplayName(serviceName);
			tags.add(objectTag);

			// START create new TIM Social Activity (Open Social)
			activity = new TimSocialActivity();

			// set activity id
			activity.setId(idBaseUrl + idPost);

			activity.setPublished(publishedTime);
			activity.setPublishedTime(dateTime);

			activity.setTitle(message);

			// set POST actor (the user created the post)
			actor = new Actor();
			actor.setId(idActor);
			actor.setDisplayName(name);

			if (idActor != null) {
				image = new Image();
				image.setUrl(actorImageUrl);
				actor.setImage(image);
			}
			activity.setActor(actor);

			activity.setVerb(TimSocialActivity.postVerb);

			org.opensocial.models.Location locationModel = new org.opensocial.models.Location();
			locationModel.setPosition(osPosition);
			activity.setLocation(locationModel);

			object = new ObjectOpenSocial();

			object.setObjectType(typeOfPost);
			
			// object.setId(id);
			object.setNumLikes(likesCount);
			object.setNumComments(commsCount);

			object.setAttachments(attachments);
			
			object.setUrl(url);

			activity.setTags(tags);

			activity.setObject(object);

			// activity.setBody("<activity body>");
			activities.add(activity);
			// activitiesArray.add(activity);

			// END create new TIM Social Activity (Open Social)

			log.debug("END create -----------------------------");
			log.debug("size=" + activities.size());
		}

		// Request request = ActivitiesService.getActivities();
		// Response response = client.send(request);
		// List<Activity> activities = response.getEntries();

		log.debug("return activities");
		
		return activities;
	}

}
