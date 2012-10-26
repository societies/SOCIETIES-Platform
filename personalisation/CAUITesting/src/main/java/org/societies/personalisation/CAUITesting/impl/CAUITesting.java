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
package org.societies.personalisation.CAUITesting.impl;

import org.societies.personalisation.CAUI.api.CAUIDiscovery.ICAUIDiscovery;
import org.societies.personalisation.CAUI.api.CAUIPrediction.ICAUIPrediction;
import org.societies.personalisation.CAUI.api.CAUITaskManager.ICAUITaskManager;
import org.societies.personalisation.CAUIDiscovery.impl.CAUIDiscovery;
import org.societies.personalisation.CAUIPrediction.impl.CAUIPrediction;
import org.societies.personalisation.CAUITaskManager.impl.CAUITaskManager;


/**
 * CAUITesting
 * 
 * @author nikosk
 * @created 12-Jan-2012 7:15:15 PM
 */
public class CAUITesting {

	private ICAUITaskManager cauiTaskManager;
	private ICAUIDiscovery cauiDiscovery;
	private ICAUIPrediction cauiPrediction;
	
	public CAUITesting(){
		
		print("start 2 ");
		cauiTaskManager = new CAUITaskManager();
		//cauiPrediction = new CAUIPrediction();
		//cauiDiscovery = new  CAUIDiscovery();
		
		//cauiDiscovery = new  CAUIDiscovery();
		//cauiPrediction = new CAUIPrediction();
	}
	
	public static void main(String[] args) {
		System.out.println("start 1 ");
		CAUITesting cauiTest = new CAUITesting();
		//cauiTest.startTesting();
	}
	
	public void startTesting(){
		
		print(cauiDiscovery.toString());
		print(cauiTaskManager.toString());
		print(cauiPrediction.toString());
		
	}
	
	private void print(String msg ){
		System.out.println(msg);
	}
	
	
	
}