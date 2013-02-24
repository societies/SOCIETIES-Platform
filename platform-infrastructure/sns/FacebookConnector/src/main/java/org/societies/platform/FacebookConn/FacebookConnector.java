package org.societies.platform.FacebookConn;

import org.societies.api.internal.sns.ISocialConnector;



public interface FacebookConnector extends ISocialConnector {

	
	public String ME		 = "me";
	public String BOOKS 	 = "me/books";
	public String EVENTS     = "me/events";
	public String GROUPS     = "me/groups";
	public String CHECKINS   = "me/checkins";
	public String HOME     	 = "me/home";
	public String FRIENDS    = "me/friends";
	public String FEED     	 = "me/feed";
	public String ALBUMS	 = "me/album";
	public String FAMILY	 = "me/family";
	public String INTERESTS	 = "me/interests";
	public String MOVIES	 = "me/movies";
	public String MUSIC	 	 = "me/music";
	public String TV	 	 = "me/television";
	public String TAGGED     = "me/tagged";
	public String STATUSES	 = "me/statuses";
	public String POSTS	     = "me/posts";
	public String MYPICTURE  = "me/picture";
	public String PHOTOS	 = "me/photos";
	public String NOTES	 	 = "me/notes";
	public String ACCOUNTS	 = "me/accounts";
	public String THUMB		 = "me/picture";
	public String LIKES		 = "me/likes";
	
}