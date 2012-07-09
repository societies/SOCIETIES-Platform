/**
 * Copyright 2009 PERSIST consortium
 *  
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at
 *  
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 */
package org.personalsmartspace.spm.servicepolicygui.impl;

import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.personalsmartspace.log.impl.PSSLog;

/**
 * @author <a href="mailto:epapadopoulou@users.sourceforge.net">Eliza Papadopoulou</a> (HWU)
 *
 */
public class Activator implements BundleActivator {

	private initiatePolicyGUI initGUI;
	private final PSSLog logging = new PSSLog(this);
	private BundleContext context;
	
	public void start(BundleContext bc) throws Exception {
		this.log("Starting");
		System.out.println("Starting " + getClass().getName());
		this.context = bc;
		this.initGUI = new initiatePolicyGUI();

		
		context.registerService(initiatePolicyGUI.class.getName(),this.initGUI, new Hashtable<String, Object>());
	}

	public void stop(BundleContext bundleContext) throws Exception {
		System.out.println("Stopping " + getClass().getName());
		this.log("Stopping");
		
	}
	private void log(String message){
		this.logging.info(this.getClass().getName()+" : "+message);
	}

}