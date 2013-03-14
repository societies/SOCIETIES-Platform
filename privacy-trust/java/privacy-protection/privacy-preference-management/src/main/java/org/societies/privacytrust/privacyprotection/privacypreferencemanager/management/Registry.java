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
package org.societies.privacytrust.privacyprotection.privacypreferencemanager.management;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import javax.swing.JOptionPane;

import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxModelBeanTranslator;
import org.societies.api.context.model.MalformedCtxIdentifierException;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.identity.Requestor;
import org.societies.api.identity.util.RequestorUtils;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.DObfMappings;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.DObfPreferenceDetailsBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.IDSMappings;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.IDSPreferenceDetailsBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.PPNMappings;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.PPNPreferenceDetailsBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.RegistryBean;
import org.societies.api.schema.context.model.CtxAttributeIdentifierBean;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.DObfPreferenceDetails;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.IDSPreferenceDetails;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.PPNPreferenceDetails;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.constants.PrivacyPreferenceTypeConstants;

public class Registry implements Serializable{

	//the key refers to the name of the preference which will be either ppnp_preference_<n> or ids_preference_<n>
	private Hashtable<PPNPreferenceDetails, CtxAttributeIdentifier> ppnpMappings;
	private Hashtable<IDSPreferenceDetails, CtxAttributeIdentifier> idsMappings;
	private Hashtable<DObfPreferenceDetails, CtxAttributeIdentifier> dobfMappings;

	int ppnp_index;
	int ids_index;
	int dobf_index;


	public Registry(){
		this.ppnpMappings = new Hashtable<PPNPreferenceDetails, CtxAttributeIdentifier>();
		this.idsMappings = new Hashtable<IDSPreferenceDetails, CtxAttributeIdentifier>();
		this.dobfMappings = new Hashtable<DObfPreferenceDetails, CtxAttributeIdentifier>();
		this.dobf_index = 0;
		ppnp_index = 0;
		ids_index = 0;
	}


	String getNameForNewPreference(PrivacyPreferenceTypeConstants preferenceType){
		if (preferenceType.equals(PrivacyPreferenceTypeConstants.PPNP)){
			this.ppnp_index +=1;
			return "ppnp_preference_"+this.ppnp_index; 
		}else if (preferenceType.equals(PrivacyPreferenceTypeConstants.IDS)){
			this.ids_index += 1;
			return "ids_preference_"+this.ids_index;
		}else{
			this.dobf_index +=1;
			return "dobf_preference_"+this.dobf_index;
		}

	}

	void addPPNPreference(PPNPreferenceDetails details, CtxAttributeIdentifier preferenceCtxID){
		this.ppnpMappings.put(details, preferenceCtxID);
	}


	void addIDSPreference(IDSPreferenceDetails details, CtxAttributeIdentifier preferenceCtxID){
		//JOptionPane.showMessageDialog(null, "Registry: Adding detail: "+details.toString()+"\nctxID: "+preferenceCtxID.toString());
		this.idsMappings.put(details, preferenceCtxID);
	}

	void addDObfPreference(DObfPreferenceDetails details, CtxAttributeIdentifier preferenceCtxID){
		this.dobfMappings.put(details, preferenceCtxID);
	}
	
	void removePPNPreference(PPNPreferenceDetails details){
		Enumeration<PPNPreferenceDetails> e = this.ppnpMappings.keys();
		while (e.hasMoreElements()){
			PPNPreferenceDetails d = e.nextElement();
			if (details.equals(d)){
				this.ppnpMappings.remove(d);
			}
		}
/*		PPNPreferenceDetails d = this.containsPPNP(details);
		if (d!=null){
			this.ppnpMappings.remove(d);
		}*/
	}

	void removeIDSPreference(IDSPreferenceDetails details){
		IDSPreferenceDetails d = this.containsIDS(details);
		if (null!=d){
			this.idsMappings.remove(d);
		}
	}

	void removeDObfPreference(DObfPreferenceDetails details){
		DObfPreferenceDetails d = this.containsDObf(details);
		if (null!=d){
			this.dobfMappings.remove(d);
		}
	}
	private PPNPreferenceDetails containsPPNP(PPNPreferenceDetails d){
		Enumeration<PPNPreferenceDetails> e = this.ppnpMappings.keys();

		System.out.println("\n\n\n\nCONTAINS PPNP???\n\n\n\n\n");
		while (e.hasMoreElements()){
			System.out.println("\n\n\n"+this.getClass().getName()+"\nFOUND PREFERENCE of:"+d.toString()+"!\n\n\n\n");
			PPNPreferenceDetails detail = e.nextElement();
			if (d.equals(detail)){
				return detail;
			}
		}
		return null;
	}
	
	private IDSPreferenceDetails containsIDS(IDSPreferenceDetails d){
		Enumeration<IDSPreferenceDetails> e = this.idsMappings.keys();

		while (e.hasMoreElements()){
			IDSPreferenceDetails detail = e.nextElement();
			if (d.equals(detail)){
				return detail;
			}
		}
		return null;
	}

	private DObfPreferenceDetails containsDObf(DObfPreferenceDetails d){
		Enumeration<DObfPreferenceDetails> e = this.dobfMappings.keys();
		
		while(e.hasMoreElements()){
			DObfPreferenceDetails detail = e.nextElement();
			if (d.equals(detail)){
				return detail;
			}
		}
		return null;
	}
	
	
	CtxAttributeIdentifier getPPNPreference(PPNPreferenceDetails details){
		Enumeration<PPNPreferenceDetails> e = this.ppnpMappings.keys();
		while (e.hasMoreElements()){
			PPNPreferenceDetails d = e.nextElement();
			if (details.equals(d)){
				return this.ppnpMappings.get(d);
			}
			
		}
		return null;
		//return this.getPPNPreference(details.getDataType(), details.getAffectedDataId(), details.getRequestorDPI(), details.getServiceID());
		
	}

	CtxAttributeIdentifier getIDSPreference(IDSPreferenceDetails details){
		//return this.getIDSPreference(details.getAffectedDPI(), details.getProviderDPI(), details.getServiceID());
		Enumeration<IDSPreferenceDetails> e = this.idsMappings.keys();
		while(e.hasMoreElements()){
			IDSPreferenceDetails d = e.nextElement();
			//JOptionPane.showMessageDialog(null, "Registry: Comparing incoming:\n "+details.toString()+"\nwith existing:\n"+d.toString());
			if (details.equals(d)){
				//JOptionPane.showMessageDialog(null, "Registry: Found match details: "+d.toString());
				return this.idsMappings.get(d);
			}
		}
		
		return null;
	}

	CtxAttributeIdentifier getDObfPreference(DObfPreferenceDetails details){
		Enumeration<DObfPreferenceDetails> e = this.dobfMappings.keys();
		while (e.hasMoreElements()){
			DObfPreferenceDetails d = e.nextElement();
			if (details.equals(d)){
				return this.dobfMappings.get(d);
			}
		}
		return null;
	}
	
	List<CtxAttributeIdentifier> getPPNPreferences(String contextType){
		List<CtxAttributeIdentifier> preferenceCtxIDs = new ArrayList<CtxAttributeIdentifier>();
		Enumeration<PPNPreferenceDetails> e = this.ppnpMappings.keys();
		while (e.hasMoreElements()){
			PPNPreferenceDetails d = e.nextElement();
			if (d.getDataType().equals(contextType)){
				preferenceCtxIDs.add(this.ppnpMappings.get(d));
			}
		}
		return preferenceCtxIDs;
	}

	List<CtxAttributeIdentifier> getIDSPreferences(IIdentity dpi){
		List<CtxAttributeIdentifier> preferenceCtxIDs = new ArrayList<CtxAttributeIdentifier>();
		Enumeration<IDSPreferenceDetails> e = this.idsMappings.keys();
		while (e.hasMoreElements()){
			IDSPreferenceDetails d = e.nextElement();
			if (d.getAffectedDPI().equals(dpi)){
				preferenceCtxIDs.add(this.idsMappings.get(d));
			}
		}

		return preferenceCtxIDs;
	}
	
	List<CtxAttributeIdentifier> getDObfPreferences(CtxAttributeIdentifier ctxId){
		//TODO: TBD with Olivier
		return new ArrayList<CtxAttributeIdentifier>();
		
	}
	List<CtxAttributeIdentifier> getPPNPreferences(String contextType, Requestor requestor){
		List<CtxAttributeIdentifier> preferenceCtxIDs = new ArrayList<CtxAttributeIdentifier>();
		Enumeration<PPNPreferenceDetails> e = this.ppnpMappings.keys();
		while (e.hasMoreElements()){
			PPNPreferenceDetails d = e.nextElement();
			if (d.getDataType().equals(contextType)){
				if (d.getRequestor().equals(requestor)){
					preferenceCtxIDs.add(this.ppnpMappings.get(d));
				}
			}
		}
		return preferenceCtxIDs;
	}

	List<CtxAttributeIdentifier> getIDSPreferences(IIdentity affectedDPI, Requestor requestor){
		List<CtxAttributeIdentifier> preferenceCtxIDs = new ArrayList<CtxAttributeIdentifier>();
		Enumeration<IDSPreferenceDetails> e = this.idsMappings.keys();

		while (e.hasMoreElements()){
			IDSPreferenceDetails d = e.nextElement();
			if (d.getAffectedDPI().equals(affectedDPI)){
				if (d.getRequestor().equals(requestor)){
					preferenceCtxIDs.add(this.idsMappings.get(d));
				}
			}
		}
		return preferenceCtxIDs;
	}
	List<CtxAttributeIdentifier> getPPNPreferences(String contextType, CtxAttributeIdentifier affectedCtxID){
		List<CtxAttributeIdentifier> preferenceCtxIDs = new ArrayList<CtxAttributeIdentifier>();
		Enumeration<PPNPreferenceDetails> e = this.ppnpMappings.keys();
		while (e.hasMoreElements()){
			PPNPreferenceDetails d = e.nextElement();
			if (d.getDataType().equals(contextType)){
				if (d.getAffectedDataId()!=null){
					if (d.getAffectedDataId().toString().equalsIgnoreCase(affectedCtxID.toString())){
						preferenceCtxIDs.add(this.ppnpMappings.get(d));

					}
				}
			}
		}
		return preferenceCtxIDs;
	}



	List<CtxAttributeIdentifier> getPPNPreferences(String contextType, CtxAttributeIdentifier affectedCtxID, Requestor requestor){
		List<CtxAttributeIdentifier> preferenceCtxIDs = new ArrayList<CtxAttributeIdentifier>();
		Enumeration<PPNPreferenceDetails> e = this.ppnpMappings.keys();
		while (e.hasMoreElements()){
			PPNPreferenceDetails d = e.nextElement();
			if (d.getDataType().equals(contextType)){
				if (d.getAffectedDataId()!=null){
					if (d.getAffectedDataId().toString().equalsIgnoreCase(affectedCtxID.toString())){
						if (d.getRequestor().equals(requestor))
							preferenceCtxIDs.add(ppnpMappings.get(d));
					}
				}
			}
		}
		return preferenceCtxIDs;
	}



/*	List<CtxAttributeIdentifier> getPPNPreferences(String contextType, Requestor requestor){
		List<CtxAttributeIdentifier> preferenceCtxIDs = new ArrayList<CtxAttributeIdentifier>();

		Enumeration<PPNPreferenceDetails> e = this.ppnpMappings.keys();
		while (e.hasMoreElements()){
			PPNPreferenceDetails d = e.nextElement();
			if (d.getDataType().equals(contextType)){
				if (d.getRequestorDPI()!=null){
					if (d.getRequestorDPI().toString().equalsIgnoreCase(dpi.toString())){
						if (d.getServiceID().toString().equalsIgnoreCase(serviceID.toString())){
							preferenceCtxIDs.add(ppnpMappings.get(d));
						}
					}



				}
			}
		}
		return preferenceCtxIDs;
	}*/


	CtxAttributeIdentifier getPPNPreference(String contextType, CtxAttributeIdentifier affectedCtxID, Requestor requestor){
		Enumeration<PPNPreferenceDetails> e = this.ppnpMappings.keys();
		while (e.hasMoreElements()){
			PPNPreferenceDetails d = e.nextElement();
			if (d.getDataType().equals(contextType)){
				if (d.getAffectedDataId()!=null){
					if (d.getAffectedDataId().toString().equalsIgnoreCase(affectedCtxID.toString())){
						if (d.getRequestor().equals(requestor)){
							return this.ppnpMappings.get(d);
						}						
					}
				}
			}
		}
		return null;
	}


	CtxAttributeIdentifier getIDSPreference(IIdentity affectedDPI, Requestor requestor){
		Enumeration<IDSPreferenceDetails> e = this.idsMappings.keys();

		while (e.hasMoreElements()){
			IDSPreferenceDetails d = e.nextElement();
			if (d.getAffectedDPI().toString().equalsIgnoreCase(affectedDPI.toString())){
				if (d.getRequestor().equals(requestor)){
					return this.idsMappings.get(d);
				}			
			}
		}
		return null;
	}

	public String toString(){
		String toprint = "\n\n\n\n-- PPNP Registry --\n";
		Enumeration<PPNPreferenceDetails> detailList = this.ppnpMappings.keys();
		while (detailList.hasMoreElements()){
			PPNPreferenceDetails detail = detailList.nextElement();
			toprint = toprint.concat(detail.toString());
			toprint = toprint.concat("\nLocated In: "+this.ppnpMappings.get(detail).toString());
		}
		
		Enumeration<IDSPreferenceDetails> idsList = this.idsMappings.keys();
		toprint = toprint.concat("\n-- IDS Registry --\n");
		while (idsList.hasMoreElements()){
			IDSPreferenceDetails detail = idsList.nextElement();
			toprint = toprint.concat(detail.toString());
			toprint = toprint.concat("\nLocated In: "+this.idsMappings.get(detail).toString());
		}
		
		toprint = toprint.concat("\n\n\n");
		return toprint;
	}
	
	
	public List<PPNPreferenceDetails> getPPNPreferenceDetails(){
		Enumeration<PPNPreferenceDetails> keys = this.ppnpMappings.keys();
		ArrayList<PPNPreferenceDetails> details = new ArrayList<PPNPreferenceDetails>();
		while (keys.hasMoreElements()){
			details.add(keys.nextElement());
		}
		return details;
	}
	
	public List<IDSPreferenceDetails> getIDSPreferenceDetails(){
		Enumeration<IDSPreferenceDetails> keys = this.idsMappings.keys();
		ArrayList<IDSPreferenceDetails> details = new ArrayList<IDSPreferenceDetails>();
		while (keys.hasMoreElements()){
			details.add(keys.nextElement());
		}
		return details;		
	}


	public List<DObfPreferenceDetails> getDObfPreferenceDetails() {
		Enumeration<DObfPreferenceDetails> keys = this.dobfMappings.keys();
		ArrayList<DObfPreferenceDetails> details = new ArrayList<DObfPreferenceDetails>();
		while (keys.hasMoreElements()){
			details.add(keys.nextElement());
			
		}
		return details;
	}
	
	
	/*
	 * converter methods
	 */
	
	
	/*
	 * FROM BEAN
	 */
	
	public static Registry fromBean(RegistryBean bean, IIdentityManager idMgr){
		Registry registry = new Registry();
		registry.ppnp_index = bean.getPpnIndex();
		registry.ids_index = bean.getIdsIndex();
		registry.dobf_index = bean.getDobfIndex();
		
		List<PPNMappings> ppnMappings = bean.getPpnMappings();
		
		for (PPNMappings ppnMap : ppnMappings){
			try {
				registry.ppnpMappings.put(toPPNPreferenceDetails(ppnMap.getPpnPrefDetails(), idMgr), (CtxAttributeIdentifier) CtxModelBeanTranslator.getInstance().fromCtxIdentifierBean(ppnMap.getCtxID()));
			} catch (MalformedCtxIdentifierException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		List<IDSMappings> idsMappings = bean.getIdsMappings();
		
		for (IDSMappings idsMap : idsMappings){
			try {
				registry.idsMappings.put(toIDSPreferenceDetails(idsMap.getIdsPrefDetails(), idMgr), (CtxAttributeIdentifier) CtxModelBeanTranslator.getInstance().fromCtxIdentifierBean(idsMap.getCtxID()));
			} catch (MalformedCtxIdentifierException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		List<DObfMappings> dobfMappings = bean.getDobfMappings();
		for (DObfMappings dobfMap : dobfMappings){
			try {
				registry.dobfMappings.put(toDObfPreferenceDetails(dobfMap.getDobfPrefDetails(), idMgr), (CtxAttributeIdentifier) CtxModelBeanTranslator.getInstance().fromCtxIdentifierBean(dobfMap.getCtxID()));
			} catch (MalformedCtxIdentifierException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return registry;
	}
	
	public static DObfPreferenceDetails toDObfPreferenceDetails(
			DObfPreferenceDetailsBean bean, IIdentityManager idMgr) {
		DObfPreferenceDetails details = new DObfPreferenceDetails(bean.getDataType());
		
		if (bean.getDataId()!=null){
			details.setAffectedDataId(bean.getDataId());
		}
		
		if (bean.getRequestor()!=null){
			try {
				details.setRequestor(RequestorUtils.toRequestor(bean.getRequestor(), idMgr));
			} catch (InvalidFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		details.setDataType(bean.getDataType());
		return details;
	}


	public static IDSPreferenceDetails toIDSPreferenceDetails(
			IDSPreferenceDetailsBean bean, IIdentityManager idMgr) {
		try {
			IDSPreferenceDetails details = new IDSPreferenceDetails(idMgr.fromJid(bean.getAffectedIdentity()));
			if (bean.getRequestor()!=null){
				details.setRequestor(RequestorUtils.toRequestor(bean.getRequestor(), idMgr));
			}
			
			return details;
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}


	public static PPNPreferenceDetails toPPNPreferenceDetails(
			PPNPreferenceDetailsBean bean, IIdentityManager idMgr) {
		PPNPreferenceDetails details = new PPNPreferenceDetails(bean.getDataType());
		if (bean.getDataId()!=null){
			details.setAffectedDataId(bean.getDataId());
		}
		
		if (bean.getRequestor()!=null){
			try {
				details.setRequestor(RequestorUtils.toRequestor(bean.getRequestor(), idMgr));
			} catch (InvalidFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		bean.setDataType(bean.getDataType());
		return details;
	}


	/*
	 * TO BEAN
	 */
	public RegistryBean toRegistryBean(){
		RegistryBean bean = new RegistryBean();
		bean.setDobfIndex(this.dobf_index);
		bean.setPpnIndex(this.ppnp_index);
		bean.setIdsIndex(this.ids_index);
		
		ArrayList<PPNMappings> ppnList = new ArrayList<PPNMappings>();
		
		List<PPNPreferenceDetails> ppnDetails = this.getPPNPreferenceDetails();
		
		
		for (PPNPreferenceDetails detail : ppnDetails){
			PPNMappings ppnMap = new PPNMappings();
			ppnMap.setPpnPrefDetails(toBean(detail));
			ppnMap.setCtxID((CtxAttributeIdentifierBean) CtxModelBeanTranslator.getInstance().fromCtxIdentifier(this.ppnpMappings.get(detail)));
			ppnList.add(ppnMap);
			
		}
		
		bean.setPpnMappings(ppnList);
		
		
		ArrayList<IDSMappings> idsList = new ArrayList<IDSMappings>();
		List<IDSPreferenceDetails> idsDetails = this.getIDSPreferenceDetails();
		
		for (IDSPreferenceDetails detail : idsDetails){
			IDSMappings idsMap = new IDSMappings();
			idsMap.setIdsPrefDetails(toBean(detail));
			idsMap.setCtxID((CtxAttributeIdentifierBean) CtxModelBeanTranslator.getInstance().fromCtxIdentifier(this.idsMappings.get(detail)));
			idsList.add(idsMap);
		}
		
		bean.setIdsMappings(idsList);
		
		
		ArrayList<DObfMappings> dobfList = new ArrayList<DObfMappings>();
		List<DObfPreferenceDetails> dobfDetails = this.getDObfPreferenceDetails();
		
		for (DObfPreferenceDetails detail : dobfDetails){
			DObfMappings dobfMap = new DObfMappings();
			dobfMap.setDobfPrefDetails(toBean(detail));
			dobfMap.setCtxID((CtxAttributeIdentifierBean) CtxModelBeanTranslator.getInstance().fromCtxIdentifier(this.dobfMappings.get(detail)));
			dobfList.add(dobfMap);
		}
		
		bean.setDobfMappings(dobfList);
		return bean;
		
	}


	private DObfPreferenceDetailsBean toBean(DObfPreferenceDetails detail) {

		DObfPreferenceDetailsBean bean = new DObfPreferenceDetailsBean();
		
		if (detail.getAffectedDataId()!=null){
			bean.setDataId(detail.getAffectedDataId());
		}
		if (detail.getRequestor()!=null){
			bean.setRequestor(RequestorUtils.toRequestorBean(detail.getRequestor()));
		}
		
		bean.setDataType(detail.getDataType());
		
		return bean;
		
	}


	private IDSPreferenceDetailsBean toBean(IDSPreferenceDetails detail) {
		IDSPreferenceDetailsBean bean = new IDSPreferenceDetailsBean();
		
		bean.setAffectedIdentity(detail.getAffectedDPI().getJid());
		if(detail.getRequestor()!=null){
			bean.setRequestor(RequestorUtils.toRequestorBean(detail.getRequestor()));
		}
		
		return bean;
	}


	private PPNPreferenceDetailsBean toBean(PPNPreferenceDetails detail) {
		PPNPreferenceDetailsBean bean = new PPNPreferenceDetailsBean();
		
		if (detail.getAffectedDataId()!=null){
			bean.setDataId(detail.getAffectedDataId());
		}
		bean.setDataType(detail.getDataType());
		if (detail.getRequestor()!=null){
			bean.setRequestor(RequestorUtils.toRequestorBean(detail.getRequestor()));
		}
		
		return bean;
	}
}
