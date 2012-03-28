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
package org.societies.android.platform.client;

import org.societies.api.cis.directory.ICisDirectory;
import org.societies.api.cis.management.ICisManager;
import org.societies.api.css.directory.ICssDirectory;
import org.societies.api.css.management.ISocietiesApp;

/**
 * @author Babak.Farshchian@sintef.no
 *
 */
public class SocietiesApp implements ISocietiesApp {
    private String cssId;
    private String cssPassword;
    private Object cssManager = null;
    private ICisManager cisManager = null;
    private ICssDirectory cssDirectory = null;
    private ICisDirectory cisDirectory = null;
    
    public SocietiesApp(String _cssId, String _cssPassword){
	this.cssId = _cssId;
	this.cssPassword = _cssPassword;
	/*
	 *  Here I need Android code to find out which platform components that are available
	 *  on this node:
	 *  - find CssManager content provider and initialize cssManager.
	 *  - find CisManager content provided and initialize cisManager.
	 *  - find CisDirectory content provided and initialize cisdirectory.
	 *  - find CssDirectory content provided and initialize cssdirectory.
	 */

	
    }

    public Object getCssManager() {
	// TODO Auto-generated method stub
	return cssManager;
    }

    public ICisManager getCisManager() {
	// TODO Auto-generated method stub
	return cisManager;
    }

    public ICssDirectory getCssDirectory() {
	// TODO Auto-generated method stub
	return cssDirectory;
    }

    public ICisDirectory getCisDirectory() {
	// TODO Auto-generated method stub
	return cisDirectory;
    }

    public String getCssId() {
        return cssId;
    }

    /**
     * Change the password only if the correct old password is provided.
     * 
     * @param _oldPassword
     * @param _newPassword
     */
    public boolean setCssPassword(String _oldPassword, String _newPassword) {
        if (_oldPassword == cssPassword){
            this.cssPassword = _newPassword;
            return true;
        }
        return false;
    }
}
