package org.societies.platform.FacebookConn;

import org.societies.api.internal.sns.ISocialConnectorInternal;




public interface FacebookConnector extends ISocialConnectorInternal {

	
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
	
	
	static final String FB_CLIENT_ID		= "427731297305104";
	static final String FB_ClIENT_SECRET	= "34795f0cd520ba4190572d9b839f426d";
	
	static final String FB_CALLBACK_URL		= "http://127.0.0.1:8080/societies-test/doConnect.html?type=fb";
	
}