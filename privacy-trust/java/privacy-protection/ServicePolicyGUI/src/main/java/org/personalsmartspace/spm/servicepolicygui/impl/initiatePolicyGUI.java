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
/**
 * 
 */
package org.personalsmartspace.spm.servicepolicygui.impl;



import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;
import org.osgi.util.tracker.ServiceTracker;
import org.personalsmartspace.log.impl.PSSLog;
import org.personalsmartspace.spm.policy.api.platform.IPolicyManager;
import org.personalsmartspace.sre.agi.api.platform.AdminGUIFrameException;
import org.personalsmartspace.sre.agi.api.platform.IAdminGUIFrame;


/**
 * @scr.component name="ServicePolicyGUI" label="ServicePolicyGUI" immediate=true
 * 
 *  
 */
public class initiatePolicyGUI {

	private BundleContext context;
	private RequestPolicyGUI policyGUI;
	
    /**
     * @scr.reference interface = "org.personalsmartspace.sre.agi.api.platform.IAdminGUIFrame"
     * cardinality="1..1" policy="static" bind="setGUIFrame" unbind="unsetGUIFrame"
     * 
     */
	private IAdminGUIFrame adminGUIFrame;
	
	private final PSSLog logging = new PSSLog(this);
	
	
	protected void setGUIFrame(IAdminGUIFrame gframe){
		this.adminGUIFrame = gframe;
	}
	
	protected void unsetGUIFrame(IAdminGUIFrame gframe){
		this.adminGUIFrame = null;
	}
	protected void activate(ComponentContext cc){
		this.context = cc.getBundleContext();
		this.activatePolicyGUI();
		
	}
	public initiatePolicyGUI(){
		
	}
	
	public void activatePolicyGUI(){
		
		adminGUIFrame = getAdminGUIFrameService();
		if (this.adminGUIFrame==null){
			this.log("COULD NOT FIND IAdminGUIFrame!!!");
		}else{
			this.log("FOUND IAdminGUIFrame");
		}
		policyGUI = new RequestPolicyGUI(this.context);
		if (policyGUI==null){
			this.log("policyGUI obj is null");
		}
		else{
			this.log("policyGUI obj IS OK");
		}
		try {
			adminGUIFrame.addCategory("/ServicePrivacyPolicies");
			System.out.println("Added New Category to IAdminGUIFrame: /ServicePrivacyPolicies");
			adminGUIFrame.addGUI("Service Privacy Policy GUI",this.policyGUI,"/ServicePrivacyPolicies");
			System.out.println("RequestPolicyGUI added to IAdminGUIFrame");
		} catch (AdminGUIFrameException ex) {
			ex.printStackTrace();
		}
	}
	private void log(String message){
		this.logging.info(this.getClass().getName()+" : "+message);
	}

	private IAdminGUIFrame getAdminGUIFrameService(){
		ServiceTracker servTracker = new ServiceTracker(this.context, IAdminGUIFrame.class.getName(), null);
		servTracker.open();
		Object[] services = servTracker.getServices();
		if (null!=services){
			if (services.length >0){
				return (IAdminGUIFrame) services[0];
			}else{
				return null;
			}
		}else{
			return null;
		}
	}
	
	public static void main(String[] args) {
		RequestPolicyGUI gui = new RequestPolicyGUI(null);
		GUIFrame frame = new GUIFrame(gui);
		frame.setVisible(true);
		
	}
	
	
}
