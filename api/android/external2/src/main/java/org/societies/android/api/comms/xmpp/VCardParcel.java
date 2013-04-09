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
package org.societies.android.api.comms.xmpp;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import android.os.Parcel;
import android.os.Parcelable;




/**
 * Describe your class here...
 *
 * @author aleckey
 *
 */
public class VCardParcel implements Parcelable, Serializable {

	private static final long serialVersionUID = 5983010238884829741L;
	private Map<String, String> homePhones = new HashMap<String, String>();
    private Map<String, String> workPhones = new HashMap<String, String>();
    private Map<String, String> homeAddr = new HashMap<String, String>();
    private Map<String, String> workAddr = new HashMap<String, String>();
    private String firstName;
    private String lastName;
    private String middleName;
    private String emailHome;
    private String emailWork;
    private String organization;
    private String organizationUnit;
    private String avatar;
    private Map<String, String> otherSimpleFields = new HashMap<String, String>();
    private Map<String, String> otherUnescapableFields = new HashMap<String, String>();
    private String to;
    private String from;

    /**Set generic VCard field.
     *
     * @param field value of field. Possible values: NICKNAME, PHOTO, BDAY, JABBERID, MAILER, TZ,
     *              GEO, TITLE, ROLE, LOGO, NOTE, PRODID, REV, SORT-STRING, SOUND, UID, URL, DESC.
     */
    public String getField(GenericField field) {
        return otherSimpleFields.get(field.name());
    }

    /**Set generic VCard field.
     *
     * @param value value of field
     * @param field field to set. See {@link #getField(String)}
     * @see #getField(String)
     */
    public void setField(GenericField field, String value) {
        setField(field, value, false);
    }

    /**
     * Set generic, unescapable VCard field. If unescapable is set to true, XML maybe a part of the
     * value.
     *
     * @param value         value of field
     * @param field         field to set. See {@link #getField(String)}
     * @param isUnescapable True if the value should not be escaped, and false if it should.
     */
    public void setField(GenericField field, String value, boolean isUnescapable) {
        if (!isUnescapable) {
            otherSimpleFields.put(field.name(), value);
        }
        else {
            otherUnescapableFields.put(field.name(), value);
        }
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
        // Update FN field
        updateFN();
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
        // Update FN field
        updateFN();
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
        // Update FN field
        updateFN();
    }

    public String getNickName() {
        return otherSimpleFields.get("NICKNAME");
    }

    public void setNickName(String nickName) {
        otherSimpleFields.put("NICKNAME", nickName);
    }

    public String getEmailHome() {
        return emailHome;
    }

    public void setEmailHome(String email) {
        this.emailHome = email;
    }

    public String getEmailWork() {
        return emailWork;
    }

    public void setEmailWork(String emailWork) {
        this.emailWork = emailWork;
    }

    public String getJabberId() {
        return otherSimpleFields.get("JABBERID");
    }

    public void setJabberId(String jabberId) {
        otherSimpleFields.put("JABBERID", jabberId);
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getOrganizationUnit() {
        return organizationUnit;
    }

    public void setOrganizationUnit(String organizationUnit) {
        this.organizationUnit = organizationUnit;
    }

    /**
     * Get home address field
     *
     * @param addrField one of POSTAL, PARCEL, (DOM | INTL), PREF, POBOX, EXTADR, STREET,
     *                  LOCALITY, REGION, PCODE, CTRY
     */
    public String getAddressFieldHome(AddressField addrField) {
        return homeAddr.get(addrField.name());
    }

    /**
     * Set home address field
     *
     * @param addrField one of POSTAL, PARCEL, (DOM | INTL), PREF, POBOX, EXTADR, STREET,
     *                  LOCALITY, REGION, PCODE, CTRY
     */
    public void setAddressFieldHome(AddressField addrField, String value) {
        homeAddr.put(addrField.name(), value);
    }

    /**
     * Get work address field
     *
     * @param addrField one of POSTAL, PARCEL, (DOM | INTL), PREF, POBOX, EXTADR, STREET,
     *                  LOCALITY, REGION, PCODE, CTRY
     */
    public String getAddressFieldWork(AddressField addrField) {
        return workAddr.get(addrField.name());
    }

    /**
     * Set work address field
     *
     * @param addrField one of POSTAL, PARCEL, (DOM | INTL), PREF, POBOX, EXTADR, STREET,
     *                  LOCALITY, REGION, PCODE, CTRY
     */
    public void setAddressFieldWork(AddressField addrField, String value) {
        workAddr.put(addrField.name(), value);
    }


    /**
     * Set home phone number
     *
     * @param phoneType one of VOICE, FAX, PAGER, MSG, CELL, VIDEO, BBS, MODEM, ISDN, PCS, PREF
     * @param phoneNum  phone number
     */
    public void setPhoneHome(PhoneType phoneType, String phoneNum) {
        homePhones.put(phoneType.name(), phoneNum);
    }

    /**
     * Get home phone number
     *
     * @param phoneType one of VOICE, FAX, PAGER, MSG, CELL, VIDEO, BBS, MODEM, ISDN, PCS, PREF
     */
    public String getPhoneHome(PhoneType phoneType) {
        return homePhones.get(phoneType.name());
    }

    /**
     * Set work phone number
     *
     * @param phoneType one of VOICE, FAX, PAGER, MSG, CELL, VIDEO, BBS, MODEM, ISDN, PCS, PREF
     * @param phoneNum  phone number
     */
    public void setPhoneWork(PhoneType phoneType, String phoneNum) {
        workPhones.put(phoneType.name(), phoneNum);
    }

    /**
     * Get work phone number
     *
     * @param phoneType one of VOICE, FAX, PAGER, MSG, CELL, VIDEO, BBS, MODEM, ISDN, PCS, PREF
     */
    public String getPhoneWork(PhoneType phoneType) {
        return workPhones.get(phoneType.name());
    }

    /**
     * Set the avatar for the VCard by specifying the url to the image.
     *
     * @param avatarURL the url to the image(png,jpeg,gif,bmp)
     */
    public void setAvatar(URL avatarURL) {
        byte[] bytes = new byte[0];
        try {
            bytes = getBytes(avatarURL);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        setAvatar(bytes);
    }

    /**
     * Specify the bytes for the avatar to use.
     *
     * @param bytes the bytes of the avatar.
     */
    public void setAvatar(byte[] bytes) {
        if (bytes == null) {
            // Remove avatar (if any) from mappings
            otherUnescapableFields.remove("PHOTO");
            return;
        }

        // Otherwise, add to mappings.
        String encodedImage = android.util.Base64.encodeToString(bytes, 0);
        //String encodedImage = StringUtils.encodeBase64(bytes);
        avatar = encodedImage;

        setField(GenericField.PHOTO, "<TYPE>image/jpeg</TYPE><BINVAL>" + encodedImage + "</BINVAL>", true);
    }

    /**
     * Specify the bytes for the avatar to use as well as the mime type.
     *
     * @param bytes the bytes of the avatar.
     * @param mimeType the mime type of the avatar.
     */
    public void setAvatar(byte[] bytes, String mimeType) {
        if (bytes == null) {
            // Remove avatar (if any) from mappings
            otherUnescapableFields.remove("PHOTO");
            return;
        }

        // Otherwise, add to mappings.
        //String encodedImage = StringUtils.encodeBase64(bytes);
        String encodedImage = android.util.Base64.encodeToString(bytes, 0);
        avatar = encodedImage;

        setField(GenericField.PHOTO, "<TYPE>" + mimeType + "</TYPE><BINVAL>" + encodedImage + "</BINVAL>", true);
    }

    /**
     * Set the encoded avatar string. This is used by the provider.
     *
     * @param encodedAvatar the encoded avatar string.
     */
    public void setEncodedImage(String encodedAvatar) {
        //TODO Move VCard and VCardProvider into a vCard package.
        this.avatar = encodedAvatar;
    }

    /**
     * Return the byte representation of the avatar(if one exists), otherwise returns null if
     * no avatar could be found.
     * <b>Example 1</b>
     * <pre>
     * // Load Avatar from VCard
     * byte[] avatarBytes = vCard.getAvatar();
     * <p/>
     * // To create an ImageIcon for Swing applications
     * ImageIcon icon = new ImageIcon(avatar);
     * <p/>
     * // To create just an image object from the bytes
     * ByteArrayInputStream bais = new ByteArrayInputStream(avatar);
     * try {
     *   Image image = ImageIO.read(bais);
     *  }
     *  catch (IOException e) {
     *    e.printStackTrace();
     * }
     * </pre>
     *
     * @return byte representation of avatar.
     */
    public byte[] getAvatar() {
        if (avatar == null) {
            return null;
        }
        //return StringUtils.decodeBase64(avatar);
        return android.util.Base64.decode(avatar, 0);
    }

    /**
     * Common code for getting the bytes of a url.
     *
     * @param url the url to read.
     */
    public static byte[] getBytes(URL url) throws IOException {
        final String path = url.getPath();
        final File file = new File(path);
        if (file.exists()) {
            return getFileBytes(file);
        }

        return null;
    }

    private static byte[] getFileBytes(File file) throws IOException {
        BufferedInputStream bis = null;
        try {
            bis = new BufferedInputStream(new FileInputStream(file));
            int bytes = (int) file.length();
            byte[] buffer = new byte[bytes];
            int readBytes = bis.read(buffer);
            if (readBytes != buffer.length) {
                throw new IOException("Entire file not read");
            }
            return buffer;
        }
        finally {
            if (bis != null) {
                bis.close();
            }
        }
    }

    private void updateFN() {
        StringBuilder sb = new StringBuilder();
        if (firstName != null) {
            sb.append(firstName).append(' ');
        }
        if (middleName != null) {
            sb.append(middleName).append(' ');
        }
        if (lastName != null) {
            sb.append(lastName);
        }
        setField(GenericField.FN, sb.toString());
    }
    
    /**@return the to */
	public String getTo() {
		return to;
	}

	/**@param to the to to set */
	public void setTo(String to) {
		this.to = to;
	}

	/**@return the from */
	public String getFrom() {
		return from;
	}

	/**@param from the from to set */
	public void setFrom(String from) {
		this.from = from;
	}
	
    public VCardParcel() {
    }
    
    private VCardParcel(Parcel in) {
    	readFromParcel(in);
    }
    
    protected void readFromParcel(Parcel in) {
    	in.readMap(homePhones, VCardParcel.class.getClassLoader());
    	in.readMap(workPhones, VCardParcel.class.getClassLoader());
    	in.readMap(homeAddr, VCardParcel.class.getClassLoader());
    	in.readMap(workAddr, VCardParcel.class.getClassLoader());
    	firstName = in.readString();
    	lastName = in.readString();
    	middleName = in.readString();
    	emailHome = in.readString();
    	emailWork = in.readString();
    	organization = in.readString();
    	organizationUnit = in.readString();
    	avatar = in.readString();
    	to = in.readString();
    	from = in.readString();
    	in.readMap(otherSimpleFields, VCardParcel.class.getClassLoader());
    	in.readMap(otherUnescapableFields, VCardParcel.class.getClassLoader());
    }
    
	/* @see android.os.Parcelable#describeContents() */
	@Override
	public int describeContents() {
		return 0;
	}

	/* @see android.os.Parcelable#writeToParcel(android.os.Parcel, int) */
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeMap(homePhones);
		dest.writeMap(workPhones);
		dest.writeMap(homeAddr);
		dest.writeMap(workAddr);
		dest.writeString(firstName);
		dest.writeString(lastName);
		dest.writeString(middleName);
		dest.writeString(emailHome);
		dest.writeString(emailWork);
		dest.writeString(organization);
		dest.writeString(organizationUnit);
		dest.writeString(avatar);
		dest.writeString(to);
		dest.writeString(from);
		dest.writeMap(otherSimpleFields);
		dest.writeMap(otherUnescapableFields);
	}

	public static final Parcelable.Creator<VCardParcel> CREATOR = new Parcelable.Creator<VCardParcel>() {
		public VCardParcel createFromParcel(Parcel in) {
			return new VCardParcel(in);
		}
		public VCardParcel[] newArray(int size) {
			return new VCardParcel[size];
		}
	};

}
