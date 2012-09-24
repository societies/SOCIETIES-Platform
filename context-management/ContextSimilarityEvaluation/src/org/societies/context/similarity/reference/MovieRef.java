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
package org.societies.context.similarity.reference;

import java.util.HashMap;
import java.util.Map.Entry;

public class MovieRef {

	private HashMap<String,String> MovieList = new HashMap<String,String>();
	
	public MovieRef(){
		popList();
		
	}
	
	public String find(String movie){
		String cat = null;
		if ( MovieList.containsValue(movie)){
			cat = movie;
		}
		//
		return cat;
	}
	
	private void popList(){
		MovieList.put("Action", "");
		MovieList.put("Adventure", "");
		MovieList.put("Animation", "");
		MovieList.put("Biography", "");
		MovieList.put("Comedy", "");
		MovieList.put("Crime", "");
		MovieList.put("Documentary", "");
		MovieList.put("Drama", "");
		MovieList.put("Family", "");
		MovieList.put("Fantasy", "");
		MovieList.put("Film-Noir", "");
		MovieList.put("Game-Show", "");
		MovieList.put("History", "");
		MovieList.put("Horror", "");
		MovieList.put("Music", "");
		MovieList.put("Musical", "");
		MovieList.put("Mystery", "");
		MovieList.put("News", "");
		MovieList.put("Reality-TV", "");
		MovieList.put("Romance", "");
		MovieList.put("Sci-Fi", "");
		MovieList.put("Sport", "");
		MovieList.put("Talk-Show", "");
		MovieList.put("Thriller", "");
		MovieList.put("War", "");
		MovieList.put("Western", "");
	}
} 	 	 	 	 