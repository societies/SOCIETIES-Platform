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
package org.societies.android.platform.comms;

import org.jivesoftware.smackx.packet.VCard;
import org.societies.android.api.comms.xmpp.AddressField;
import org.societies.android.api.comms.xmpp.GenericField;
import org.societies.android.api.comms.xmpp.PhoneType;
import org.societies.android.api.comms.xmpp.VCardParcel;

/**
 * Describe your class here...
 *
 * @author aleckey
 *
 */
public class VCardUtilities {

	/**
	 * Converts a VCard to a Parcelable version
	 * @param xmppVCard
	 * @return
	 */
	public static VCardParcel convertToParcelVCard(VCard xmppVCard) {
    	VCardParcel vcard = new VCardParcel();
    	
    	//PHONES
    	for(PhoneType type: PhoneType.class.getEnumConstants()) {
    		String homePhone = xmppVCard.getPhoneHome(type.name());
    		if (homePhone!=null) vcard.setPhoneHome(type, homePhone);
    		
    		String workPhone = xmppVCard.getPhoneWork(type.name());
    		if (workPhone!=null) vcard.setPhoneWork(type, workPhone);
    	}
    	//ADDRESSES
    	for(AddressField type: AddressField.class.getEnumConstants()) {
    		String homeAddress = xmppVCard.getAddressFieldHome(type.name());
    		if (homeAddress!=null) vcard.setAddressFieldHome(type, homeAddress);
    		
    		String workAddress = xmppVCard.getAddressFieldWork(type.name());
    		if (workAddress!=null) vcard.setAddressFieldWork(type, workAddress);
    	}
    	//SIMPLE FIELDS
    	vcard.setFirstName(xmppVCard.getFirstName());
    	vcard.setLastName(xmppVCard.getLastName());
    	vcard.setMiddleName(xmppVCard.getMiddleName());
    	vcard.setEmailHome(xmppVCard.getEmailHome());
    	vcard.setEmailWork(xmppVCard.getEmailWork());
    	vcard.setOrganization(xmppVCard.getOrganization());
    	vcard.setOrganizationUnit(xmppVCard.getOrganizationUnit());
    	vcard.setAvatar(xmppVCard.getAvatar());
    	//GENERIC FIELDS
    	for(GenericField type: GenericField.class.getEnumConstants()) {
    		String value = xmppVCard.getField(type.name());
    		if (value!=null) vcard.setField(type, value);
       	}
    	return vcard;
    }
	
	/**
	 * Converts a Parcelable VCard version to XMPP VCard
	 * @param vcardParcel
	 * @return
	 */
	public static VCard convertToXMPPVCard(VCardParcel vcardParcel) {
		VCard vcard = new VCard();
    	
    	//PHONES
    	for(PhoneType type: PhoneType.class.getEnumConstants()) {
    		String homePhone = vcardParcel.getPhoneHome(type);
    		if (homePhone!=null) vcard.setPhoneHome(type.name(), homePhone);
    		
    		String workPhone = vcardParcel.getPhoneWork(type);
    		if (workPhone!=null) vcard.setPhoneWork(type.name(), workPhone);
    	}
    	//ADDRESSES
    	for(AddressField type: AddressField.class.getEnumConstants()) {
    		String homeAddress = vcardParcel.getAddressFieldHome(type);
    		if (homeAddress!=null) vcard.setAddressFieldHome(type.name(), homeAddress);
    		
    		String workAddress = vcardParcel.getAddressFieldWork(type);
    		if (workAddress!=null) vcard.setAddressFieldWork(type.name(), workAddress);
    	}
    	//SIMPLE FIELDS
    	vcard.setFirstName(vcardParcel.getFirstName());
    	vcard.setLastName(vcardParcel.getLastName());
    	vcard.setMiddleName(vcardParcel.getMiddleName());
    	vcard.setEmailHome(vcardParcel.getEmailHome());
    	vcard.setEmailWork(vcardParcel.getEmailWork());
    	vcard.setOrganization(vcardParcel.getOrganization());
    	vcard.setOrganizationUnit(vcardParcel.getOrganizationUnit());
    	vcard.setAvatar(vcardParcel.getAvatar());
    	//GENERIC FIELDS
    	for(GenericField type: GenericField.class.getEnumConstants()) {
    		String value = vcardParcel.getField(type);
    		if (value!=null) vcard.setField(type.name(), value);
       	}
    	return vcard;
	}
}
