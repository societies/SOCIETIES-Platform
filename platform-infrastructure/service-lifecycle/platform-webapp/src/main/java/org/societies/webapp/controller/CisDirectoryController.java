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

package org.societies.webapp.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import javax.validation.Valid;

import org.societies.api.cis.directory.ICisDirectoryRemote;
import org.societies.api.cis.management.ICis;
import org.societies.api.cis.management.ICisManager;
import org.societies.api.css.directory.ICssDirectoryRemote;
import org.societies.api.schema.cis.directory.CisAdvertisementRecord;
import org.societies.api.schema.cis.community.*;
import org.societies.cis.directory.client.CisDirectoryRemoteClient;
import org.societies.css.mgmt.CssDirectoryRemoteClient;
import org.societies.webapp.models.CISDirectoryForm;
import org.societies.webapp.models.CisDirectoryCombinedDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.societies.api.schema.cis.community.MembershipCrit;
//import org.societies.cis.directory.model.CriteriaRecordEntry;
import org.societies.api.schema.css.directory.CssAdvertisementRecord;

@Controller
public class CisDirectoryController {
	
	/**
	 * OSGI service get auto injected
	 */
	@Autowired
	private ICisDirectoryRemote cisDirectoryRemote;
	
	@Autowired
	private ICssDirectoryRemote cssDirectoryRemote;
	
	@Autowired
	private ICisManager cisManager;
	
	
	public ICisDirectoryRemote getCisDirectoryRemote() {
		return cisDirectoryRemote;
	}

	public void setCisDirectoryRemote(ICisDirectoryRemote cisDirectoryRemote) {
		this.cisDirectoryRemote = cisDirectoryRemote;
	}
	
	public ICssDirectoryRemote getCssDirectoryRemote() {
		return cssDirectoryRemote;
	}

	public void setCssDirectoryRemote(ICssDirectoryRemote cssDirectoryRemote) {
		this.cssDirectoryRemote = cssDirectoryRemote;
	}

	public ICisManager getCisManager() {
		return cisManager;
	}
	public void setCisManager(ICisManager cisManager) {
		this.cisManager = cisManager;
	}

	
	@RequestMapping(value = "/oldcisdirectory.html", method = RequestMethod.GET)
	public ModelAndView CISDirectoryold() {

		//CREATE A HASHMAP OF ALL OBJECTS REQUIRED TO PROCESS THIS PAGE
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("message", "Please input values and submit");
		
		//ADD THE BEAN THAT CONTAINS ALL THE FORM DATA FOR THIS PAGE
		CISDirectoryForm cdForm = new CISDirectoryForm();
		model.put("cdForm", cdForm);
		
		//ADD ALL THE SELECT BOX VALUES USED ON THE FORM
		Map<String, String> methods = new LinkedHashMap<String, String>();
		methods.put("GetCisAdverts", "Get CIS Advertisements");
		methods.put("AddCisRecord", "Add a CIS Advertisement");
		model.put("methods", methods);
		
		model.put("cisdirectoryresult", "CIS Directory Result :");
		return new ModelAndView("cisdirectory", model);
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/oldcisdirectory.html", method = RequestMethod.POST)
	public ModelAndView oldCISDirectory(@Valid CISDirectoryForm cdForm,
			BindingResult result, Map model) {

		if (result.hasErrors()) {
			model.put("result", "CIS Directory form error");
			return new ModelAndView("cisdirectory", model);
		}

		if (getCisDirectoryRemote() == null) {
			model.put("errormsg", "CIS Directory reference not avaiable");
			return new ModelAndView("error", model);
		}

		
		String method = cdForm.getMethod();
		Future<List<CisAdvertisementRecord>> asynchResult = null;
		//CisAdvertisementRecord record = null;

		List<CisAdvertisementRecord> adverts =  new ArrayList<CisAdvertisementRecord>();
		
		String res = null;
		
		try {
		
			if (method.equalsIgnoreCase("GetCisAdverts")) {
				res="CIS Directory Result ";
				
				CisDirectoryRemoteClient callback = new CisDirectoryRemoteClient();

				getCisDirectoryRemote().findAllCisAdvertisementRecords(callback);
				adverts = callback.getResultList();
				
				model.put("result", res);
				model.put("adverts", adverts);
				
			}else if (method.equalsIgnoreCase("AddCisRecord")) {
				
				res="CIS Advertisement added Successfully ";
				
				CisAdvertisementRecord record= new CisAdvertisementRecord();
				Criteria critEntry = new Criteria();
				MembershipCrit crit = new MembershipCrit();
				
				
					record.setName(cdForm.getName()); 
					record.setCssownerid(cdForm.getCssownerid());
					record.setType(cdForm.getType());
					record.setId(cdForm.getId());
					//record.setMode(cdForm.getMode());
					record.setPassword(cdForm.getPassword());
					
					critEntry.setAttrib(cdForm.getAttrib());
					critEntry.setOperator(cdForm.getOperator());
					critEntry.setValue1(cdForm.getvalue1());
					critEntry.setValue2(cdForm.getvalue2());
					critEntry.setRank(cdForm.getRank());
					
					//crit = (MembershipCrit) record.getMembershipCrit().getCriteria();
					//record.setMembershipCrit(crit);
					crit.getCriteria().add(critEntry);
					record.setMembershipCrit(crit); 
					
					
				getCisDirectoryRemote().addCisAdvertisementRecord(record);
				model.put("message", "CisAdvertisement added");
									
				// Go a read all the records again so we can display all after
				// a new record is added
				CisDirectoryRemoteClient callback = new CisDirectoryRemoteClient();
				getCisDirectoryRemote().findAllCisAdvertisementRecords(callback);
				adverts = callback.getResultList();
				critEntry.getAttrib();
				
				model.put("result", res);
				model.put("adverts", adverts);
				
				model.put("adverts", adverts);
					
			}else{
				res="error unknown metod";
			}
		
			model.put("result", res);
			
		}
		catch (Exception e)
		{
			res = "Oops!!!!<br/>";
		};
		
		
		return new ModelAndView("cisdirectoryresult", model);
	}

	@RequestMapping(value = "/cisdirectory.html", method = RequestMethod.GET)
	public ModelAndView CISDirectory() {

		Map<String, Object> model = new HashMap<String, Object>();
		
		if (getCisDirectoryRemote() == null) {
			model.put("errormsg", "CIS Directory reference not avaiable");
			return new ModelAndView("error", model);
		}
		
		if (getCssDirectoryRemote() == null) {
			model.put("errormsg", "CSS Directory reference not avaiable");
			return new ModelAndView("error", model);
		}

	
		CisDirectoryRemoteClient callback = new CisDirectoryRemoteClient();

		getCisDirectoryRemote().findAllCisAdvertisementRecords(callback);
		List<CisAdvertisementRecord> adverts = callback.getResultList();
		List<CssAdvertisementRecord> cssDetails = null;
		List<String> cssIds = new ArrayList<String>();
		
		// now we have two lists, we have to 
		List<CisDirectoryCombinedDetails> cssNiceDetails =  new ArrayList<CisDirectoryCombinedDetails>(); 
					
					
		if ((adverts != null) && (adverts.size() > 0))
		{
			
	
			for ( int i = 0; i < adverts.size(); i++)
			{
				cssIds.add(new String(adverts.get(i).getCssownerid()));
			}
			// Make a call to the Css Directory
			
			// first get all the cssdirectory records
			CssDirectoryRemoteClient cssDirCallback = new CssDirectoryRemoteClient();

			getCssDirectoryRemote().searchByID(cssIds, cssDirCallback);
			cssDetails = cssDirCallback.getResultList();
						
		
			
			// now we have two lists, we have to 
			for ( int i = 0; i < adverts.size(); i++)
			{
				
				// don't want to show the one's that were create by this user, or one's we are already a member of
				
				ICis checkcis = this.getCisManager().getCis(adverts.get(i).getId());
				
				if (checkcis == null) // we are not a member so we should show it
				{
				
				
					CisDirectoryCombinedDetails niceDets = new  CisDirectoryCombinedDetails();
					niceDets.setAdrecord(adverts.get(i));
				
					if ((cssDetails != null) && (cssDetails.size() > 0))
					{
				
					// list is returned in order we sent, so only have to check as far as 'i'
					for ( int j = 0; ((j <= i) && (j < cssDetails.size())); j++)
					{
						if (adverts.get(i).getCssownerid().contains(cssDetails.get(j).getId()))
						{
							niceDets.setCssownername(cssDetails.get(j).getName());
							// found it, stop searching
							j = cssDetails.size();
						}
						
					}
					
					niceDets.setCssownerid(adverts.get(i).getCssownerid());
					
					// If we didn't find it, then just use the cssowneer id
					if (niceDets.getCssownername().isEmpty())
					{
						niceDets.setCssownername(adverts.get(i).getCssownerid());
					}
						
					
					}
					cssNiceDetails.add(niceDets);
				}
			}
		}
		
		model.put("cssNiceDetails", cssNiceDetails);
		
		return new ModelAndView("cisdirpilotresult", model);
	}
	
}
