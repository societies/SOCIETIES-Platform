/**
 * Copyright (c) 2011, SOCIETIES Consortium
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
package org.societies.personalisation.CAUIDiscovery.impl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.societies.personalisation.CAUI.api.CAUIDiscovery.ICAUIDiscovery;

public class CAUIDiscovery implements ICAUIDiscovery{


	LinkedHashMap<String,Integer> dictionary = new LinkedHashMap<String,Integer>();
	List<String> charList = null;


	@Override
	public void generateNewUserModel() {
		charList = new ArrayList<String>();
	}

	public LinkedHashMap<String,Integer> getDictionary(){
		return this.dictionary;
	}



	void findOccurences(int step, String array){

		int arraySize = array.length();
		String currentPhrase = null;

		for (int i = 0; i < arraySize-step ; i++){
			currentPhrase = array.substring(i, i+step);
			System.out.println(" currentChar "+currentPhrase);

			if(this.dictionary.containsKey(currentPhrase)){
				//HashMap<String,Integer> container = dictionary.get(currentChar);
				Integer previousScore = this.dictionary.get(currentPhrase);
				this.dictionary.put(currentPhrase,previousScore+1);
			} else {
				this.dictionary.put(currentPhrase, 1);
			}
		}

		
	}

	public static void main(String[] args) {

		CAUIDiscovery cd = new CAUIDiscovery();
		//String array = "AAABABBBBAABCCDDCBAAA";
		//String array = "ABCEZDEFJKABCODEFPABCIOIKDEF";
		String array = "ABCDEFABCDEF";
		System.out.println("array: "+array);
		//	sd.findStates(array);
		//System.out.println("created dictionary1 :"+sd.getDictionary());
		for (int i=1; i<5; i++){
			cd.findOccurences(i ,array);
		}
		System.out.println("populated dictionary :"+cd.getDictionary());
	}

}
