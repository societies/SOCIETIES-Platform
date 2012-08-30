/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAÇÃO, SA (PTIN), IBM Corp., 
 * INSTITUT TELECOM (ITSUD), AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA PERIORISMENIS EFTHINIS (AMITEC), TELECOM 
 * ITALIA S.p.a.(TI),  TRIALOG (TRIALOG), Stiftelsen SINTEF (SINTEF), NEC EUROPE LTD (NEC))
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
 * conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
 *    disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.societies.orchestration.cpa.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.societies.api.activity.IActivity;
import org.societies.api.cis.management.ICisOwned;

public class SimpleTextSearch {
	private HashMap<String,HashMap<String,Double>> topicToUserMap;
    private String[] textPosts = {"posted","said"};      //TODO: set this ..
	public SimpleTextSearch(){

	}
    private static int minimum(int a, int b, int c) {
        return Math.min(Math.min(a, b), c);
    }
	
	public int computeLevenshteinDistance(CharSequence str1,
			CharSequence str2) {
		int[][] distance = new int[str1.length() + 1][str2.length() + 1];

		for (int i = 0; i <= str1.length(); i++)
			distance[i][0] = i;
		for (int j = 0; j <= str2.length(); j++)
			distance[0][j] = j;

		for (int i = 1; i <= str1.length(); i++)
			for (int j = 1; j <= str2.length(); j++)
				distance[i][j] = minimum(
						distance[i - 1][j] + 1,
						distance[i][j - 1] + 1,
						distance[i - 1][j - 1]
								+ ((str1.charAt(i - 1) == str2.charAt(j - 1)) ? 0
										: 1));

		return distance[str1.length()][str2.length()];
	}
	public int userDistance(List<IActivity> actList, String user1, String user2){
		int ret = Integer.MAX_VALUE;
		String userText[] = gatherUserText(actList,user1,user2);
		ret = this.computeLevenshteinDistance(userText[0], userText[1]);
		return ret;
	}
	private String[] gatherUserText(List<IActivity> actList, String user1, String user2){
		List<IActivity> user1acts = new ArrayList<IActivity>();
		List<IActivity> user2acts = new ArrayList<IActivity>();
        //sort the activities into two heaps, one for user1 and one for user2
        for(IActivity act : actList){
            if(act.getActor().contains(user1))
                user1acts.add(act);
            else if(act.getActor().contains(user2))
                user2acts.add(act);
        }
        String ret[] = new String[2];
        String user1text="";
        String user2text="";
        //sort out the "posted", this should be generalized a bit more, to capture all textual interaction
		for(IActivity user1act : user1acts)
			if(checkIfTextPost(user1act.getVerb()))
				user1text += user1act.getObject();
		for(IActivity user2act : user1acts)
			if(checkIfTextPost(user2act.getVerb()))
				user1text += user2act.getObject();
		ret[0] = user1text;
		ret[1] = user2text;
		return ret;
	}
    private boolean checkIfTextPost(String s){
        for(String candidates : this.textPosts)
            if(s.contains(candidates))
                return true;
        return false;

    }
	public static void main(String args[]){
		String s="kitten"; String t="sitting";
		String s2="burdeikkeligne"; String t2="paadette";
		SimpleTextSearch sts = new SimpleTextSearch();
		System.out.println("notmylevenstein distance of \""+s+"\" and \""+t+"\":" + sts.computeLevenshteinDistance(s, t));
		System.out.println("notmylevenstein distance of \""+s2+"\" and \""+t2+"\":" + sts.computeLevenshteinDistance(s2, t2));
	}
}
