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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxModelBeanTranslator;
import org.societies.api.context.model.MalformedCtxIdentifierException;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.Requestor;
import org.societies.api.identity.util.RequestorUtils;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.AccCtrlMappings;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.AccessControlPreferenceDetailsBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.DObfMappings;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.DObfPreferenceDetailsBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.IDSMappings;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.IDSPreferenceDetailsBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.PPNMappings;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.PPNPreferenceDetailsBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.RegistryBean;
import org.societies.api.schema.context.model.CtxAttributeIdentifierBean;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.constants.PrivacyPreferenceTypeConstants;
import org.societies.privacytrust.privacyprotection.api.util.PrivacyPreferenceUtils;

public class Registry implements Serializable{

	private Logger logging = LoggerFactory.getLogger(this.getClass());
	//the key refers to the name of the preference which will be either ppnp_preference_<n> or ids_preference_<n>
	private Hashtable<PPNPreferenceDetailsBean, CtxAttributeIdentifier> ppnpMappings;
	private Hashtable<IDSPreferenceDetailsBean, CtxAttributeIdentifier> idsMappings;
	private Hashtable<DObfPreferenceDetailsBean, CtxAttributeIdentifier> dobfMappings;
	private Hashtable<AccessControlPreferenceDetailsBean, CtxAttributeIdentifier> accCtrlMappings;

	int ppnp_index;
	int ids_index;
	int dobf_index;
	int accCtrl_index;


	public Registry(){
		this.ppnpMappings = new Hashtable<PPNPreferenceDetailsBean, CtxAttributeIdentifier>();
		this.idsMappings = new Hashtable<IDSPreferenceDetailsBean, CtxAttributeIdentifier>();
		this.dobfMappings = new Hashtable<DObfPreferenceDetailsBean, CtxAttributeIdentifier>();
		this.accCtrlMappings = new Hashtable<AccessControlPreferenceDetailsBean, CtxAttributeIdentifier>();
		this.dobf_index = 0;
		ppnp_index = 0;
		ids_index = 0;
		this.accCtrl_index = 0;
	}


	String getNameForNewPreference(PrivacyPreferenceTypeConstants preferenceType){
		
		if (preferenceType.equals(PrivacyPreferenceTypeConstants.PRIVACY_POLICY_NEGOTIATION)){
			this.ppnp_index +=1;
			return "ppnp_preference_"+this.ppnp_index; 
		}else if (preferenceType.equals(PrivacyPreferenceTypeConstants.IDENTITY_SELECTION)){
			this.ids_index += 1;
			return "ids_preference_"+this.ids_index;
		}else if (preferenceType.equals(PrivacyPreferenceTypeConstants.DATA_OBFUSCATION)){
			this.dobf_index +=1;
			return "dobf_preference_"+this.dobf_index;
		}else{
			this.accCtrl_index +=1;
			return "accCtrl_preference_"+this.accCtrl_index;
		}

	}

	void addPPNPreference(PPNPreferenceDetailsBean details, CtxAttributeIdentifier preferenceCtxID){
		this.ppnpMappings.put(details, preferenceCtxID);
	}


	void addIDSPreference(IDSPreferenceDetailsBean details, CtxAttributeIdentifier preferenceCtxID){
		//JOptionPane.showMessageDialog(null, "Registry: Adding detail: "+details.toString()+"\nctxID: "+preferenceCtxID.toString());
		this.idsMappings.put(details, preferenceCtxID);
	}

	void addDObfPreference(DObfPreferenceDetailsBean details, CtxAttributeIdentifier preferenceCtxID){
		this.dobfMappings.put(details, preferenceCtxID);
	}
	
	void addAccessCtrlPreference(AccessControlPreferenceDetailsBean details, CtxAttributeIdentifier preferenceCtxID){
		this.accCtrlMappings.put(details, preferenceCtxID);
	}
	void removePPNPreference(PPNPreferenceDetailsBean details){
		Enumeration<PPNPreferenceDetailsBean> e = this.ppnpMappings.keys();
		while (e.hasMoreElements()){
			PPNPreferenceDetailsBean d = e.nextElement();
			if (PrivacyPreferenceUtils.equals(details, d)){
				this.ppnpMappings.remove(d);
			}
		}
/*		PPNPreferenceDetailsBean d = this.containsPPNP(details);
		if (d!=null){
			this.ppnpMappings.remove(d);
		}*/
	}

	void removeIDSPreference(IDSPreferenceDetailsBean details){
		IDSPreferenceDetailsBean d = this.containsIDS(details);
		if (null!=d){
			this.idsMappings.remove(d);
		}
	}

	void removeDObfPreference(DObfPreferenceDetailsBean details){
		DObfPreferenceDetailsBean d = this.containsDObf(details);
		if (null!=d){
			this.dobfMappings.remove(d);
		}
	}
	
	void removeAccCtrlPreference(AccessControlPreferenceDetailsBean details){
		AccessControlPreferenceDetailsBean d = this.containsAccCtrl(details);
		if (null!=d){
			this.accCtrlMappings.remove(d);
		}
	}
	private PPNPreferenceDetailsBean containsPPNP(PPNPreferenceDetailsBean d){
		Enumeration<PPNPreferenceDetailsBean> e = this.ppnpMappings.keys();

		this.logging.debug("\n\n\n\nCONTAINS PPNP???\n\n\n\n\n");
		while (e.hasMoreElements()){
			this.logging.debug("\n\n\n"+this.getClass().getName()+"\nFOUND PREFERENCE of:"+d.toString()+"!\n\n\n\n");
			PPNPreferenceDetailsBean detail = e.nextElement();
			if (PrivacyPreferenceUtils.equals(detail, d)){
				return detail;
			}
		}
		return null;
	}
	
	private IDSPreferenceDetailsBean containsIDS(IDSPreferenceDetailsBean d){
		Enumeration<IDSPreferenceDetailsBean> e = this.idsMappings.keys();

		while (e.hasMoreElements()){
			IDSPreferenceDetailsBean detail = e.nextElement();
			if (PrivacyPreferenceUtils.equals(d, detail)){
				return detail;
			}
		}
		return null;
	}

	private DObfPreferenceDetailsBean containsDObf(DObfPreferenceDetailsBean d){
		Enumeration<DObfPreferenceDetailsBean> e = this.dobfMappings.keys();
		
		while(e.hasMoreElements()){
			DObfPreferenceDetailsBean detail = e.nextElement();
			if (PrivacyPreferenceUtils.equals(d, detail)){
				return detail;
			}
		}
		return null;
	}
	
	private AccessControlPreferenceDetailsBean containsAccCtrl(AccessControlPreferenceDetailsBean d){
		Enumeration<AccessControlPreferenceDetailsBean> e = this.accCtrlMappings.keys();
		
		while(e.hasMoreElements()){
			AccessControlPreferenceDetailsBean detail = e.nextElement();
			if (PrivacyPreferenceUtils.equals(d, detail)){
				return detail;
			}
		}
		return null;
	}
	
	CtxAttributeIdentifier getPPNPreference(PPNPreferenceDetailsBean details){
		Enumeration<PPNPreferenceDetailsBean> e = this.ppnpMappings.keys();
		while (e.hasMoreElements()){
			PPNPreferenceDetailsBean d = e.nextElement();
			if (PrivacyPreferenceUtils.equals(d, details)){
				return this.ppnpMappings.get(d);
			}
			
		}
		return null;
		//return this.getPPNPreference(details.getDataType(), details.getAffectedDataId(), details.getRequestorDPI(), details.getServiceID());
		
	}

	CtxAttributeIdentifier getIDSPreference(IDSPreferenceDetailsBean details){
		//return this.getIDSPreference(details.getAffectedDPI(), details.getProviderDPI(), details.getServiceID());
		Enumeration<IDSPreferenceDetailsBean> e = this.idsMappings.keys();
		while(e.hasMoreElements()){
			IDSPreferenceDetailsBean d = e.nextElement();
			//JOptionPane.showMessageDialog(null, "Registry: Comparing incoming:\n "+details.toString()+"\nwith existing:\n"+d.toString());
			if (PrivacyPreferenceUtils.equals(d, details)){
				//JOptionPane.showMessageDialog(null, "Registry: Found match details: "+d.toString());
				return this.idsMappings.get(d);
			}
		}
		
		return null;
	}

	CtxAttributeIdentifier getDObfPreference(DObfPreferenceDetailsBean details){
		Enumeration<DObfPreferenceDetailsBean> e = this.dobfMappings.keys();
		while (e.hasMoreElements()){
			DObfPreferenceDetailsBean d = e.nextElement();
			if (PrivacyPreferenceUtils.equals(d, details)){
				return this.dobfMappings.get(d);
			}
		}
		return null;
	}
	
	CtxAttributeIdentifier getAccCtrlPreference(AccessControlPreferenceDetailsBean details){
		Enumeration<AccessControlPreferenceDetailsBean> e = this.accCtrlMappings.keys();
		while(e.hasMoreElements()){
			AccessControlPreferenceDetailsBean d = e.nextElement();
			if (PrivacyPreferenceUtils.equals(d, details)){
				return this.accCtrlMappings.get(d);
			}
		}
		//JOptionPane.showMessageDialog(null, "Not found details in registry");
		return null;
	}
	List<CtxAttributeIdentifier> getPPNPreferences(String contextType){
		List<CtxAttributeIdentifier> preferenceCtxIDs = new ArrayList<CtxAttributeIdentifier>();
		Enumeration<PPNPreferenceDetailsBean> e = this.ppnpMappings.keys();
		while (e.hasMoreElements()){
			PPNPreferenceDetailsBean d = e.nextElement();
			if (d.getResource().getDataType().equals(contextType)){
				preferenceCtxIDs.add(this.ppnpMappings.get(d));
			}
		}
		return preferenceCtxIDs;
	}

	List<CtxAttributeIdentifier> getIDSPreferences(IIdentity identity){
		List<CtxAttributeIdentifier> preferenceCtxIDs = new ArrayList<CtxAttributeIdentifier>();
		Enumeration<IDSPreferenceDetailsBean> e = this.idsMappings.keys();
		while (e.hasMoreElements()){
			IDSPreferenceDetailsBean d = e.nextElement();
			if (d.getAffectedIdentity().equals(identity.getJid())){
				preferenceCtxIDs.add(this.idsMappings.get(d));
			}
		}

		return preferenceCtxIDs;
	}
	
	List<CtxAttributeIdentifier> getAccCtrlPreferences(String contextType){
		List<CtxAttributeIdentifier> preferenceCtxIDs = new ArrayList<CtxAttributeIdentifier>();
		Enumeration<AccessControlPreferenceDetailsBean> e = this.accCtrlMappings.keys();
		
		while(e.hasMoreElements()){
			AccessControlPreferenceDetailsBean d = e.nextElement();
			if (d.getResource().getDataType().equals(contextType)){
				preferenceCtxIDs.add(this.accCtrlMappings.get(d));
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
		Enumeration<PPNPreferenceDetailsBean> e = this.ppnpMappings.keys();
		while (e.hasMoreElements()){
			PPNPreferenceDetailsBean d = e.nextElement();
			if (d.getResource().getDataType().equals(contextType)){
				if (RequestorUtils.equals(d.getRequestor(),requestor)){
					preferenceCtxIDs.add(this.ppnpMappings.get(d));
				}
			}
		}
		return preferenceCtxIDs;
	}

	List<CtxAttributeIdentifier> getIDSPreferences(IIdentity identity, Requestor requestor){
		List<CtxAttributeIdentifier> preferenceCtxIDs = new ArrayList<CtxAttributeIdentifier>();
		Enumeration<IDSPreferenceDetailsBean> e = this.idsMappings.keys();

		while (e.hasMoreElements()){
			IDSPreferenceDetailsBean d = e.nextElement();
			if (d.getAffectedIdentity().equals(identity.getJid())){
				if (d.getRequestor().equals(requestor)){
					preferenceCtxIDs.add(this.idsMappings.get(d));
				}
			}
		}
		return preferenceCtxIDs;
	}
	
	List<CtxAttributeIdentifier> getAccCtrlPreferences(String contextType, Requestor requestor){
		List<CtxAttributeIdentifier> preferenceCtxIDs = new ArrayList<CtxAttributeIdentifier>();
		Enumeration<AccessControlPreferenceDetailsBean> e = this.accCtrlMappings.keys();
		while (e.hasMoreElements()){
			AccessControlPreferenceDetailsBean d = e.nextElement();
			if (d.getResource().getDataType().equals(contextType)){
				if (d.getRequestor().equals(requestor)){
					preferenceCtxIDs.add(this.accCtrlMappings.get(d));
				}
			}
		}
		return preferenceCtxIDs;
	}
	List<CtxAttributeIdentifier> getPPNPreferences(String contextType, CtxAttributeIdentifier affectedCtxID){
		List<CtxAttributeIdentifier> preferenceCtxIDs = new ArrayList<CtxAttributeIdentifier>();
		Enumeration<PPNPreferenceDetailsBean> e = this.ppnpMappings.keys();
		while (e.hasMoreElements()){
			PPNPreferenceDetailsBean d = e.nextElement();
			if (d.getResource().getDataType().equals(contextType)){
				if (d.getResource().getDataIdUri()!=null){
					if (d.getResource().getDataIdUri().equalsIgnoreCase(affectedCtxID.getUri())){
						preferenceCtxIDs.add(this.ppnpMappings.get(d));

					}
				}
			}
		}
		return preferenceCtxIDs;
	}



	List<CtxAttributeIdentifier> getAccCtrlPreferences(String contextType, CtxAttributeIdentifier affectedCtxID, Requestor requestor){
		List<CtxAttributeIdentifier> preferenceCtxIDs = new ArrayList<CtxAttributeIdentifier>();
		Enumeration<AccessControlPreferenceDetailsBean> e = this.accCtrlMappings.keys();
		while (e.hasMoreElements()){
			AccessControlPreferenceDetailsBean d = e.nextElement();
			if (d.getResource().getDataType().equals(contextType)){
				if (d.getResource().getDataIdUri()!=null){
					if (d.getResource().getDataIdUri().equalsIgnoreCase(affectedCtxID.getUri())){
						if (d.getRequestor().equals(requestor))
							preferenceCtxIDs.add(this.accCtrlMappings.get(d));
					}
				}
			}
		}
		return preferenceCtxIDs;
	}



/*	List<CtxAttributeIdentifier> getPPNPreferences(String contextType, Requestor requestor){
		List<CtxAttributeIdentifier> preferenceCtxIDs = new ArrayList<CtxAttributeIdentifier>();

		Enumeration<PPNPreferenceDetailsBean> e = this.ppnpMappings.keys();
		while (e.hasMoreElements()){
			PPNPreferenceDetailsBean d = e.nextElement();
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




	CtxAttributeIdentifier getIDSPreference(IIdentity identity, Requestor requestor){
		Enumeration<IDSPreferenceDetailsBean> e = this.idsMappings.keys();

		while (e.hasMoreElements()){
			IDSPreferenceDetailsBean d = e.nextElement();
			if (d.getAffectedIdentity().equalsIgnoreCase(identity.getJid())){
				if (d.getRequestor().equals(requestor)){
					return this.idsMappings.get(d);
				}			
			}
		}
		return null;
	}

	public String toString(){
		String toprint = "\n\n\n\n-- PPNP Registry --\n";
		Enumeration<PPNPreferenceDetailsBean> detailList = this.ppnpMappings.keys();
		while (detailList.hasMoreElements()){
			PPNPreferenceDetailsBean detail = detailList.nextElement();
			toprint = toprint.concat(PrivacyPreferenceUtils.toString(detail));
			toprint = toprint.concat("\nLocated In: "+this.ppnpMappings.get(detail).toString());
		}
		
		Enumeration<IDSPreferenceDetailsBean> idsList = this.idsMappings.keys();
		toprint = toprint.concat("\n-- IDS Registry --\n");
		while (idsList.hasMoreElements()){
			IDSPreferenceDetailsBean detail = idsList.nextElement();
			toprint = toprint.concat(detail.toString());
			toprint = toprint.concat("\nLocated In: "+this.idsMappings.get(detail).toString());
		}
		
		Enumeration<AccessControlPreferenceDetailsBean> accList = this.accCtrlMappings.keys();
		toprint = toprint.concat("\n-- AccCtrl Registry --\n");
		while (accList.hasMoreElements()){
			AccessControlPreferenceDetailsBean detail = accList.nextElement();
			toprint = toprint.concat(detail.toString());
			toprint = toprint.concat("\nLocated In: "+this.accCtrlMappings.get(detail).toString());
		}
		
		
		toprint = toprint.concat("\n\n\n");
		return toprint;
	}
	
	
	public List<PPNPreferenceDetailsBean> getPPNPreferenceDetails(){
		Enumeration<PPNPreferenceDetailsBean> keys = this.ppnpMappings.keys();
		ArrayList<PPNPreferenceDetailsBean> details = new ArrayList<PPNPreferenceDetailsBean>();
		while (keys.hasMoreElements()){
			details.add(keys.nextElement());
		}
		return details;
	}
	
	public List<IDSPreferenceDetailsBean> getIDSPreferenceDetails(){
		Enumeration<IDSPreferenceDetailsBean> keys = this.idsMappings.keys();
		ArrayList<IDSPreferenceDetailsBean> details = new ArrayList<IDSPreferenceDetailsBean>();
		while (keys.hasMoreElements()){
			details.add(keys.nextElement());
		}
		return details;		
	}


	public List<DObfPreferenceDetailsBean> getDObfPreferenceDetails() {
		Enumeration<DObfPreferenceDetailsBean> keys = this.dobfMappings.keys();
		ArrayList<DObfPreferenceDetailsBean> details = new ArrayList<DObfPreferenceDetailsBean>();
		while (keys.hasMoreElements()){
			details.add(keys.nextElement());
			
		}
		return details;
	}
	
	
	public List<AccessControlPreferenceDetailsBean> getAccCtrlPreferenceDetails(){
		Enumeration<AccessControlPreferenceDetailsBean> keys = this.accCtrlMappings.keys();
		
		ArrayList<AccessControlPreferenceDetailsBean> details = new ArrayList<AccessControlPreferenceDetailsBean>();
		while(keys.hasMoreElements()){
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
		registry.accCtrl_index = bean.getAccCtrlIndex();
		
		List<PPNMappings> ppnMappings = bean.getPpnMappings();
		
		for (PPNMappings ppnMap : ppnMappings){
			try {
				registry.ppnpMappings.put(ppnMap.getPpnPrefDetails(), (CtxAttributeIdentifier) CtxModelBeanTranslator.getInstance().fromCtxIdentifierBean(ppnMap.getCtxID()));
			} catch (MalformedCtxIdentifierException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		List<IDSMappings> idsMappings = bean.getIdsMappings();
		
		for (IDSMappings idsMap : idsMappings){
			try {
				registry.idsMappings.put(idsMap.getIdsPrefDetails(), (CtxAttributeIdentifier) CtxModelBeanTranslator.getInstance().fromCtxIdentifierBean(idsMap.getCtxID()));
			} catch (MalformedCtxIdentifierException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		List<DObfMappings> dobfMappings = bean.getDobfMappings();
		for (DObfMappings dobfMap : dobfMappings){
			try {
				registry.dobfMappings.put(dobfMap.getDobfPrefDetails(), (CtxAttributeIdentifier) CtxModelBeanTranslator.getInstance().fromCtxIdentifierBean(dobfMap.getCtxID()));
			} catch (MalformedCtxIdentifierException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		List<AccCtrlMappings> accCtrlMappings = bean.getAccCtrlMappings();
		for (AccCtrlMappings accCtrlMap : accCtrlMappings){
			try {
				registry.accCtrlMappings.put(accCtrlMap.getAccCtrlPrefDetails(), (CtxAttributeIdentifier) CtxModelBeanTranslator.getInstance().fromCtxIdentifierBean(accCtrlMap.getCtxID()));
			} catch (MalformedCtxIdentifierException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return registry;
	}
	



	/*
	 * TO BEAN
	 */
	public RegistryBean toRegistryBean(){
		RegistryBean bean = new RegistryBean();
		bean.setDobfIndex(this.dobf_index);
		bean.setPpnIndex(this.ppnp_index);
		bean.setIdsIndex(this.ids_index);
		bean.setAccCtrlIndex(this.accCtrl_index);
		
		ArrayList<PPNMappings> ppnList = new ArrayList<PPNMappings>();
		
		List<PPNPreferenceDetailsBean> ppnDetails = this.getPPNPreferenceDetails();
		
		
		for (PPNPreferenceDetailsBean detail : ppnDetails){
			PPNMappings ppnMap = new PPNMappings();
			ppnMap.setPpnPrefDetails(detail);
			ppnMap.setCtxID((CtxAttributeIdentifierBean) CtxModelBeanTranslator.getInstance().fromCtxIdentifier(this.ppnpMappings.get(detail)));
			ppnList.add(ppnMap);
			
		}
		
		bean.setPpnMappings(ppnList);
		
		
		ArrayList<IDSMappings> idsList = new ArrayList<IDSMappings>();
		List<IDSPreferenceDetailsBean> idsDetails = this.getIDSPreferenceDetails();
		
		for (IDSPreferenceDetailsBean detail : idsDetails){
			IDSMappings idsMap = new IDSMappings();
			idsMap.setIdsPrefDetails(detail);
			idsMap.setCtxID((CtxAttributeIdentifierBean) CtxModelBeanTranslator.getInstance().fromCtxIdentifier(this.idsMappings.get(detail)));
			idsList.add(idsMap);
		}
		
		bean.setIdsMappings(idsList);
		
		
		ArrayList<DObfMappings> dobfList = new ArrayList<DObfMappings>();
		List<DObfPreferenceDetailsBean> dobfDetails = this.getDObfPreferenceDetails();
		
		for (DObfPreferenceDetailsBean detail : dobfDetails){
			DObfMappings dobfMap = new DObfMappings();
			dobfMap.setDobfPrefDetails(detail);
			dobfMap.setCtxID((CtxAttributeIdentifierBean) CtxModelBeanTranslator.getInstance().fromCtxIdentifier(this.dobfMappings.get(detail)));
			dobfList.add(dobfMap);
		}
		
		bean.setDobfMappings(dobfList);
		
		
		ArrayList<AccCtrlMappings> accCtrlList = new ArrayList<AccCtrlMappings>();
		List<AccessControlPreferenceDetailsBean> accCtrlDetails = this.getAccCtrlPreferenceDetails();
		
		for (AccessControlPreferenceDetailsBean detail : accCtrlDetails){
			AccCtrlMappings accMap = new AccCtrlMappings();
			accMap.setAccCtrlPrefDetails(detail);
			accMap.setCtxID((CtxAttributeIdentifierBean) CtxModelBeanTranslator.getInstance().fromCtxIdentifier(this.accCtrlMappings.get(detail)));
			accCtrlList.add(accMap);
		}
		
		bean.setAccCtrlMappings(accCtrlList);
		
		return bean;
		
	}





}
