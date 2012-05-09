/**
 * Copyright (c) 2011, SOCIETIES Consortium
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
package org.societies.privacytrust.privacyprotection.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.Requestor;
import org.societies.api.internal.privacytrust.privacyprotection.IPrivacyDataManager;
import org.societies.api.internal.privacytrust.privacyprotection.IPrivacyPolicyManager;
import org.societies.api.internal.privacytrust.privacyprotection.model.PrivacyException;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.Action;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.ResponseItem;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.constants.ActionConstants;

/**
 * @author Olivier Maridat (Trialog)
 *
 */
public class Test {
	private static Logger log = LoggerFactory.getLogger(Test.class.getSimpleName());
	
	private IPrivacyDataManager privacyDataManager;
	private IPrivacyPolicyManager privacyPolicyManager;
	
	public void setPrivacyDataManager(IPrivacyDataManager privacyDataManager) {
		this.privacyDataManager = privacyDataManager;
		log.info("************* privacyDataManager injected");
		try {
			ResponseItem permission = privacyDataManager.checkPermission(null, null, null, null);
			log.info("Permission checked: "+permission.toString());
		} catch (Exception e) {
			log.error("************* Error PrivacyException: "+e.getMessage(), e);
		}
	}
	
	public void setPrivacyPolicyManager(IPrivacyPolicyManager privacyPolicyManager) {
		this.privacyPolicyManager = privacyPolicyManager;
		log.info("************* privacyPolicyManager injected");
		IIdentity cisId = null;
		boolean result = false;
		try {
			result = privacyPolicyManager.deletePrivacyPolicy(cisId);
			log.info("************* privacyPolicyManager "+result);
		} catch (Exception e) {
			log.error("************* Error PrivacyException: "+e.getMessage(), e);
		}
	}

}
