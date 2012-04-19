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
package org.societies.personalisation.CACITaskManager.impl;

import java.util.List;

import org.societies.personalisation.CACI.api.CACITaskManager.ICACITaskManager;
import org.societies.personalisation.CAUI.api.model.CommunityIntentAction;
import org.societies.personalisation.CAUI.api.model.CommunityIntentTask;
import org.societies.personalisation.CAUI.api.model.UserIntentModelData;
import org.societies.personalisation.CAUI.api.model.UserIntentAction;
import org.societies.personalisation.CAUI.api.model.UserIntentTask;

public class CACITaskManager implements ICACITaskManager{

	@Override
	public void createCommAction(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void createCommTask(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public double getActionCommLevel(String arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<CommunityIntentAction> getCommActByType(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CommunityIntentAction getCommAction(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CommunityIntentTask getCommTask(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UserIntentAction getNextCommAction(CommunityIntentAction arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UserIntentTask getNextCommTask(CommunityIntentTask arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double getTaskCommLevel(String arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void populateCommTask(String arg0, List<CommunityIntentAction> arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resetTaskModel() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public UserIntentModelData retrieveModel(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setActionCommLevel(String arg0, Double arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setTaskCommLevel(String arg0, Double arg1) {
		// TODO Auto-generated method stub
		
	}

}
