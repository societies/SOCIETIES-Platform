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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.jivesoftware.smackx.packet.VCard;
import org.societies.android.api.comms.xmpp.AddressField;
import org.societies.android.api.comms.xmpp.GenericField;
import org.societies.android.api.comms.xmpp.PhoneType;
import org.societies.android.api.comms.xmpp.VCardParcel;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

/**
 * Describe your class here...
 *
 * @author aleckey
 *
 */
public class VCardUtilities {

	private static final String LOG_TAG = AndroidCommsBase.class.getName();
	
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
    	vcard.setTo(xmppVCard.getTo());
    	vcard.setFrom(xmppVCard.getFrom());
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
    	vcard.setTo(vcardParcel.getTo());
    	vcard.setFrom(vcardParcel.getFrom());
    	//GENERIC FIELDS
    	for(GenericField type: GenericField.class.getEnumConstants()) {
    		String value = vcardParcel.getField(type);
    		if (value!=null) vcard.setField(type.name(), value);
       	}
    	return vcard;
	}
	
	/**
	 * Retrieves a VCard from disk storage
	 * @param filename
	 * @return
	 */
	public static VCardParcel getVCardFromDisk(Context context, String userIdentity) {
		VCardParcel vcard = null;
		String filename = userIdentity + ".vcf";
		//File file = context.getFileStreamPath(filename);
		File root = Environment.getExternalStorageDirectory();
	    File dir = new File(root.getAbsolutePath() + "/vcards");
	    File file = new File(dir, filename);
		
		if(file.exists()){
			Log.d(LOG_TAG, "File: " + userIdentity +".vcf exists. Retrieving...");
			FileInputStream fis;
			try {
				fis = new FileInputStream(filename);
				ObjectInputStream ois = new ObjectInputStream(fis);
				vcard = (VCardParcel) ois.readObject();
				ois.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			return vcard;
		}
	    else {
	    	return null;
	    }
	}
	
	/**
	 * Saves a VCard to disk storage
	 * @param filename
	 * @param vcard
	 */
	public static void saveVCardToDisk(Context context, String userIdentity, VCardParcel vcard) {
		String filename = userIdentity + ".vcf";
		
		File root = Environment.getExternalStorageDirectory();
	    File dir = new File(root.getAbsolutePath() + "/vcards");
	    dir.mkdirs();
		
		FileOutputStream fOut;
		ObjectOutputStream oos;
		try {
			//fOut = context.openFileOutput(filename, Context.MODE_WORLD_READABLE);
			File file = new File(dir, filename);
			fOut = new FileOutputStream(file);
			oos = new ObjectOutputStream(fOut);
			oos.writeObject(vcard);
			oos.flush();
			fOut.flush();
			oos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Log.d(LOG_TAG, "File: " + userIdentity +".vcf succcessfully saved to disk");
	}
	
	private static boolean checkExternalMedia() {
	    boolean mExternalStorageWriteable = false;
	    String state = Environment.getExternalStorageState();

	    if (Environment.MEDIA_MOUNTED.equals(state)) {
	        // Can read and write the media
	        mExternalStorageWriteable = true;
	    } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
	        mExternalStorageWriteable = false;
	    } else {
	        mExternalStorageWriteable = false;
	    }
	    return mExternalStorageWriteable;
	}
	
}
