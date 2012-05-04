package org.societies.personalisation.socialprofiler;


public interface Variables {
	public final int NONE=0;
	public final int NARCISSISM_PROFILE=1;
	public final int SUPERACTIVE_PROFILE=2;
	public final int PHOTO_PROFILE=3;
	public final int SURF_PROFILE=4;
	public final int QUIZ_PROFILE=5;
	
	public final int BETWEENESS_CENTALITY=6;
	public final int EIGENVECTOR_CENTRALITY=7;
	
	public final int FINAL=8;
	public final int INTERNAL=9;

	public final int FIRST_TIME=100;
	public final int UPDATE_ONLY_STREAM=200;
	public final int UPDATE_STREAM_AND_USER_INFORMATION=300;
	public final int UPDATE_STREAM_AND_FANPAGES=400;
	public final int UPDATE_EVERYTHING=500;

	public final int SIMPLE_INFO=10;
	public final int DOUBLE_INFO=11;
	public final static double undefined=-10;
	public final static int undefined1=-10;
		
	public final int EVERYTHING=12;
	public final int LAST_WEEK=13;
	public final int LAST_2_WEEKS=14;
	public final int LAST_MONTH=15;
	public final int LAST_2_MONTHS=16;
	public final int LAST_3_MONTHS=17;
	public final int LAST_6_MONTHS=18;
	public final int LAST_YEAR=19;
	
	public final int PROFILE_DIMENSION=25;
	public final int USER_DIMENSION=26;

	public final int REPLY_TO_ME=1101;
	public final int REPLY_TO_OTHER_CA=1102;
	public final int REPLY_TO_STRANGER_CA=1103;
	
}
