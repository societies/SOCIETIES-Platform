package org.societies.platform.sns.fbconnector;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.context.user.db.impl.UserCtxDBMgr;
import org.societies.context.broker.impl.*;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;

import com.restfb.Connection;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.types.Post;

public class FacebookConnector {

	/**
	 * RestFB Graph API client.
	 */
	private FacebookClient 	facebookClient;
	private String 			token =  "AAAFPIhZAkC90BAOxwcXg96DZBfxH5ZAgCL7vEMRgb59gTtfAWxZCn0TAfOmvFSeKZCGko3hi85jeC8X6mTfzAQrlIRSWb7P732RM9IJHBeAZDZD";
	private int	maxPostLimit = 50;
	
	private String meHome     = "me/home";
	private String friends    = "me/friends";
	private String groups     = "me/groups";
	private String checkins   = "me/checkins";
	private String events     = "me/events";
	
	
	private InternalCtxBroker 	internalCtxBroker;
	//private ICtxBroker 			ctxBroker;
	
	public FacebookConnector(){
		facebookClient 				= new DefaultFacebookClient(token);
		Connection<Post> myFeeds 	= facebookClient.fetchConnection(groups, Post.class, Parameter.with("limit", maxPostLimit));
		List<Post> myFeedConnectionPage = myFeeds.getData();
		
		for (Post post : myFeedConnectionPage){
			System.out.println("--- POST: " +post.toString());
		}
		
		
		myFeeds 	= facebookClient.fetchConnection(friends, Post.class, Parameter.with("limit", maxPostLimit));
		myFeedConnectionPage = myFeeds.getData();
		
		for (Post post : myFeedConnectionPage){
			System.out.println("--- GROUPS: " +post.toString());
		}
		
	}
	
	
	public void storeInCxB(){
		try {
			internalCtxBroker = new InternalCtxBroker();
		
			/// this for using as a JAVA 
			internalCtxBroker.setUserCtxDBMgr(new UserCtxDBMgr());
			
			Future<CtxEntity>  ctxEntityFuture = internalCtxBroker.createEntity("sns-activity");
			CtxEntity entity = ctxEntityFuture.get(); 
			Future<CtxAttribute> ctxAttributeFuture = internalCtxBroker.createAttribute(entity.getId(), "connector");
			CtxAttribute attr = ctxAttributeFuture.get();
			attr.setStringValue("Facebook");
			
			System.out.print(" Add new Entity:"+entity.getId().toString());
			
			
			
			
		} catch (CtxException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new FacebookConnector();
	
	}

}
