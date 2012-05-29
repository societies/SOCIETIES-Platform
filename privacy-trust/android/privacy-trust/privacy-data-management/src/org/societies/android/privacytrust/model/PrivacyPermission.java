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
package org.societies.android.privacytrust.model;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.societies.android.api.internal.privacytrust.model.PrivacyPolicyTypeConstants;
import org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.Action;
import org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.Decision;
import org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.RequestItem;
import org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.Resource;
import org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.ResponseItem;
import org.societies.api.schema.identity.DataIdentifier;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.identity.RequestorCisBean;
import org.societies.api.schema.identity.RequestorServiceBean;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Entity to store privacy permissions for access control management
 * @author Olivier Maridat (Trialog)
 *
 */
public class PrivacyPermission implements Serializable {
	private static final long serialVersionUID = -5745233622018708564L;


	private int id;
	private String requestorId;
	private String subRequestorId;
	private String ownerId;
	private String dataId;
	/**
	 * Formatted:  [{"action": READ, "optional": true}, ...]
	 */
	private String actions;
	private Decision decision;
	private PrivacyPolicyTypeConstants permissionType; 


	public PrivacyPermission() {
		super();
		this.id = -1;
	}

	/**
	 * @param requestorId
	 * @param permissionType
	 * @param serviceId
	 * @param cisId
	 * @param ownerId
	 * @param dataId
	 * @param actions
	 * @param decision
	 */
	public PrivacyPermission(RequestorBean requestor, String ownerId, String dataId, String actions, Decision decision) {
		super();
		this.id = -1;
		this.requestorId = requestor.getRequestorId();
		this.subRequestorId = ((requestor instanceof RequestorServiceBean) ? ((RequestorServiceBean)requestor).getRequestorServiceId().getIdentifier().toString() : ((requestor instanceof RequestorCisBean) ?  ((RequestorCisBean)requestor).getCisRequestorId() : ""));
		this.permissionType =  ((requestor instanceof RequestorServiceBean) ? PrivacyPolicyTypeConstants.SERVICE : ((requestor instanceof RequestorCisBean) ? PrivacyPolicyTypeConstants.CIS : PrivacyPolicyTypeConstants.NOTHING));
		this.ownerId = ownerId;
		this.dataId = dataId;
		this.actions = actions;
		this.decision = decision;
	}

	/**
	 * @param requestor
	 * @param permission
	 */
	public PrivacyPermission(RequestorBean requestor, String ownerId, ResponseItem permission) {
		this(requestor, ownerId, permission.getRequestItem().getResource().getCtxUriIdentifier(), getActionsToJson(permission.getRequestItem().getActions()), permission.getDecision());
	}

	
	/* --- Intelegint Setters --- */
	
	/**
	 * Retrieve the access control permission as a ResponseItem
	 * @return Permission wrapped as a ResponseItem
	 * @throws MalformedCtxIdentifierException 
	 */
	public ResponseItem createResponseItem() {
		// - Create the resource
		Resource resource = new Resource();
		resource.setCtxUriIdentifier(dataId);

		// - Create the list of actions from JSON
		Gson jsonManager = new Gson();
		List<Action> actions = new ArrayList<Action>();
		if (null != this.actions && !"".equals(this.actions)) {
			Type actionsType = new TypeToken<List<Action>>(){}.getType();
			actions = jsonManager.fromJson(this.actions, actionsType);
		}

		// - Create the RrequestItem
		RequestItem requestItem = new RequestItem();
		requestItem.setOptional(false);
		requestItem.setResource(resource);
		// TODO manage list
//		requestItem.setActions(actions);
//		requestItem.setConditions(new ArrayList<Condition>());
		// - Create the ResponseItem
		ResponseItem reponseItem = new ResponseItem();
		reponseItem.setDecision(decision);
		reponseItem.setRequestItem(requestItem);

		return reponseItem;
	}
	
	public void setRequestor(RequestorBean requestor) {
		if (requestor != null) {
			this.requestorId = requestor.getRequestorId();
			if (requestor instanceof RequestorCisBean) {
				permissionType = PrivacyPolicyTypeConstants.CIS;
				this.subRequestorId = ((RequestorCisBean) requestor).getCisRequestorId();
			}
			else if (requestor instanceof RequestorServiceBean) {
				permissionType = PrivacyPolicyTypeConstants.SERVICE;
				this.subRequestorId = ((RequestorServiceBean) requestor).getRequestorServiceId().getIdentifier().toString();
			}
			else {
				permissionType = PrivacyPolicyTypeConstants.NOTHING;
				this.subRequestorId = "";
			}
		}
	}

	public void setDataId(DataIdentifier dataId) {
		if (dataId != null) {
			this.dataId = dataId.getUri();
		}
	}

	/*
	 * Set a list of actions as a JSOn formatted string
	 */
	public void setActions(List<Action> actions) {
		this.actions = getActionsToJson(actions);
	}
	
	/*
	 * Set a list of actions as a JSOn formatted string
	 */
	public static String getActionsToJson(List<Action> actions) {
		StringBuffer result = new StringBuffer("[");
		if (null != actions) {
			for(int i=0; i<actions.size(); i++) {
				result.append("{\"action\": "+actions.get(i).getActionConstant().name()+", \"optional\": "+actions.get(i).isOptional()+"}");
				if (i != (actions.size()-1)) {
					result.append(", ");
				}
			}
		}
		result.append("]");
		return result.toString();
	}

	/**
	 * @param permission ResponseItem permission
	 */
	public void setResponseItem(ResponseItem permission) {
		setDataId(permission.getRequestItem().getResource().getCtxUriIdentifier());
		setActions(permission.getRequestItem().getActions());
		setDecision(permission.getDecision());
	}
	
	
	
	/* --- Normal Setters ---*/
	
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the ownerId
	 */
	public String getOwnerId() {
		return ownerId;
	}

	/**
	 * @param ownerId the ownerId to set
	 */
	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}


	/**
	 * @return the dataId
	 */
	public String getDataId() {
		return dataId;
	}


	/**
	 * @param dataId the dataId to set
	 */
	public void setDataId(String dataId) {
		this.dataId = dataId;
	}

	/**
	 * @return the requestorId
	 */
	public String getRequestorId() {
		return requestorId;
	}

	/**
	 * @param requestorId the requestorId to set
	 */
	public void setRequestorId(String requestorId) {
		this.requestorId = requestorId;
	}
	
	/**
	 * @return the subRequestorId
	 */
	public String getSubRequestorId() {
		return subRequestorId;
	}

	/**
	 * @param subRequestorId the subRequestorId to set
	 */
	public void setSubRequestorId(String subRequestorId) {
		this.subRequestorId = subRequestorId;
	}

	/**
	 * @return the decision
	 */
	public Decision getDecision() {
		return decision;
	}

	/**
	 * @param decision the decision to set
	 */
	public void setDecision(Decision decision) {
		this.decision = decision;
	}

	/**
	 * @return the permissionType
	 */
	public PrivacyPolicyTypeConstants getPermissionType() {
		return permissionType;
	}

	/**
	 * @param permissionType the permissionType to set
	 */
	public void setPermissionType(PrivacyPolicyTypeConstants permissionType) {
		this.permissionType = permissionType;
	}

	/**
	 * @return the actions
	 */
	public String getActions() {
		return actions;
	}

	/**
	 * @param actions the actions to set
	 */
	public void setActions(String actions) {
		this.actions = actions;
	}

	

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "PrivacyPermission [id=" + id + ", requestorId=" + requestorId
				+ ", serviceId="
				+ subRequestorId + ", permissionType=" + permissionType.name() + ", ownerId=" + ownerId
				+ ", dataId=" + dataId + ", actions=" + actions
				+ ", permission=" + decision.name() + "]";
	}
}
