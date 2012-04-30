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

import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.Requestor;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.IDSPreferenceDetails;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.PPNPreferenceDetails;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.constants.PrivacyPreferenceTypeConstants;

public class Registry implements Serializable{

	//the key refers to the name of the preference which will be either ppnp_preference_<n> or ids_preference_<n>
	private Hashtable<PPNPreferenceDetails, CtxAttributeIdentifier> ppnpMappings;
	private Hashtable<IDSPreferenceDetails, CtxAttributeIdentifier> idsMappings;

	int ppnp_index;
	int ids_index;


	public Registry(){
		this.ppnpMappings = new Hashtable<PPNPreferenceDetails, CtxAttributeIdentifier>();
		this.idsMappings = new Hashtable<IDSPreferenceDetails, CtxAttributeIdentifier>();
		ppnp_index = 0;
		ids_index = 0;
	}


	String getNameForNewPreference(PrivacyPreferenceTypeConstants preferenceType){
		if (preferenceType.equals(PrivacyPreferenceTypeConstants.PPNP)){
			this.ppnp_index +=1;
			return "ppnp_preference_"+this.ppnp_index; 
		}else{
			this.ids_index += 1;
			return "ids_preference_"+this.ids_index;
		}

	}

	void addPPNPreference(PPNPreferenceDetails details, CtxAttributeIdentifier preferenceCtxID){
		this.ppnpMappings.put(details, preferenceCtxID);
	}


	void addIDSPreference(IDSPreferenceDetails details, CtxAttributeIdentifier preferenceCtxID){
		//JOptionPane.showMessageDialog(null, "Registry: Adding detail: "+details.toString()+"\nctxID: "+preferenceCtxID.toString());
		this.idsMappings.put(details, preferenceCtxID);
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
		if (d!=null){
			this.idsMappings.remove(d);
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

	CtxAttributeIdentifier getPPNPreference(PPNPreferenceDetails details){
		Enumeration<PPNPreferenceDetails> e = this.ppnpMappings.keys();
		while (e.hasMoreElements()){
			PPNPreferenceDetails d = e.nextElement();
			
			if (details.equals(d)){
				return this.ppnpMappings.get(d);
			}
			
		}
		return null;
		//return this.getPPNPreference(details.getContextType(), details.getAffectedCtxID(), details.getRequestorDPI(), details.getServiceID());
		
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



	List<CtxAttributeIdentifier> getPPNPreferences(String contextType){
		List<CtxAttributeIdentifier> preferenceCtxIDs = new ArrayList<CtxAttributeIdentifier>();
		Enumeration<PPNPreferenceDetails> e = this.ppnpMappings.keys();
		while (e.hasMoreElements()){
			PPNPreferenceDetails d = e.nextElement();
			if (d.getContextType().equals(contextType)){
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
	List<CtxAttributeIdentifier> getPPNPreferences(String contextType, Requestor requestor){
		List<CtxAttributeIdentifier> preferenceCtxIDs = new ArrayList<CtxAttributeIdentifier>();
		Enumeration<PPNPreferenceDetails> e = this.ppnpMappings.keys();
		while (e.hasMoreElements()){
			PPNPreferenceDetails d = e.nextElement();
			if (d.getContextType().equals(contextType)){
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
			if (d.getContextType().equals(contextType)){
				if (d.getAffectedCtxID()!=null){
					if (d.getAffectedCtxID().toString().equalsIgnoreCase(affectedCtxID.toString())){
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
			if (d.getContextType().equals(contextType)){
				if (d.getAffectedCtxID()!=null){
					if (d.getAffectedCtxID().toString().equalsIgnoreCase(affectedCtxID.toString())){
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
			if (d.getContextType().equals(contextType)){
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
			if (d.getContextType().equals(contextType)){
				if (d.getAffectedCtxID()!=null){
					if (d.getAffectedCtxID().toString().equalsIgnoreCase(affectedCtxID.toString())){
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
}
