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

package org.societies.orchestration.ui;

import java.util.ArrayList;
import java.util.List;

import org.societies.orchestration.api.IUserInput;
import org.societies.orchestration.api.IUserNotification;

public class UserInterface implements IUserInput, IUserNotification{

	@Override
	public boolean configureCIS(Object CIS, Object details) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean createCISs(ArrayList<Object> CISs) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean deleteCISs(ArrayList<Object> CISs) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Object getCISInfo(Object CIS) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<Object> getMyCISs() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<Object> getRecommendedCISs() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getUserInfo(Object CSS) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<Object> searchAvailableCISs(String filter) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object sendInvitations(ArrayList<Object> CSSnodes, Object CIS) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean setCSSStatus(String status) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setDeletedCISsNotification(boolean notifier) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setIntervalTrigger(long milliseconds) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setPermissions(Object CIS, Object permissions) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean showRecommendedCISes(List<Object> CISes) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeRecommendedCISes(List<Object> CISes) {
		// TODO Auto-generated method stub
		return false;
	}

}