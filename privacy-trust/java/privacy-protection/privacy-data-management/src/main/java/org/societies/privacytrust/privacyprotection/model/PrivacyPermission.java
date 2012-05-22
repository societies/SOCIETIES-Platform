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
package org.societies.privacytrust.privacyprotection.model;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxIdentifierFactory;
import org.societies.api.context.model.MalformedCtxIdentifierException;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.Requestor;
import org.societies.api.identity.RequestorCis;
import org.societies.api.identity.RequestorService;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.Action;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.Condition;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.Decision;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.RequestItem;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.Resource;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.ResponseItem;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.constants.PrivacyPolicyTypeConstants;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Entity to store privacy permissions for access control management
 * @author Olivier Maridat (Trialog)
 *
 */
@Entity
@Table(name = "PrivacyPermission")
public class PrivacyPermission implements Serializable {
	private static final long serialVersionUID = -5745233622018708564L;


	@Id
	@GeneratedValue
	private Long id;

	private String requestorId;
	@Enumerated
	private PrivacyPolicyTypeConstants permissionType; 
	@Basic(optional=true)
	private String serviceId;
	@Basic(optional=true)
	private String cisId;

	private String ownerId;

	private String dataId;

	/**
	 * Formatted:  [{"action": READ, "optional": true}, ...]
	 */
	private String actions;

	@Enumerated
	private Decision permission;


	public PrivacyPermission() {
		super();
	}

	/**
	 * @param requestorId
	 * @param permissionType
	 * @param serviceId
	 * @param cisId
	 * @param ownerId
	 * @param dataId
	 * @param actions
	 * @param permission
	 */
	public PrivacyPermission(String requestorId,
			PrivacyPolicyTypeConstants permissionType, String serviceId,
			String cisId, String ownerId, String dataId, String actions,
			Decision permission) {
		super();
		this.requestorId = requestorId;
		this.permissionType = permissionType;
		this.serviceId = serviceId;
		this.cisId = cisId;
		this.ownerId = ownerId;
		this.dataId = dataId;
		this.actions = actions;
		this.permission = permission;
	}
	
	/**
	 * @param requestor
	 * @param ownerId
	 * @param dataId
	 * @param actions
	 * @param permission
	 */
	public PrivacyPermission(Requestor requestor, IIdentity ownerId, CtxIdentifier dataId, List<Action> actions, Decision permission) {
		super();
		setRequestor(requestor);
		setOwnerId(ownerId);
		setDataId(dataId);
		setActions(actions);
		setPermission(permission);
	}
	
	/**
	 * @param requestor
	 * @param permission
	 */
	public PrivacyPermission(Requestor requestor, IIdentity ownerId, ResponseItem permission) {
		this(requestor, ownerId, permission.getRequestItem().getResource().getCtxIdentifier(), permission.getRequestItem().getActions(), permission.getDecision());
	}


	
	/* --- Intelegint Setters --- */
	
	/**
	 * Retrieve the access control permission as a ResponseItem
	 * @return Permission wrapped as a ResponseItem
	 * @throws MalformedCtxIdentifierException 
	 */
	public ResponseItem createResponseItem() throws MalformedCtxIdentifierException {
		// - Create the resource
		CtxAttributeIdentifier dataId = (CtxAttributeIdentifier) CtxIdentifierFactory.getInstance().fromString(this.dataId);
		Resource resource = new Resource(dataId);

		// - Create the list of actions from JSON
		Gson jsonManager = new Gson();
		List<Action> actions = new ArrayList<Action>();
		if (null != this.actions && !"".equals(this.actions)) {
			Type actionsType = new TypeToken<List<Action>>(){}.getType();
			actions = jsonManager.fromJson(this.actions, actionsType);
		}

		// - Create the ResponseItem
		RequestItem requestItem = new RequestItem(resource, actions, new ArrayList<Condition>());
		ResponseItem reponseItem = new ResponseItem(requestItem, permission);

		return reponseItem;
	}
	
	public void setRequestor(Requestor requestor) {
		if (requestor != null) {
			this.requestorId = requestor.getRequestorId().getJid();
			if (requestor instanceof RequestorCis) {
				this.permissionType = PrivacyPolicyTypeConstants.CIS;
				this.serviceId = null;
				this.cisId = ((RequestorCis) requestor).getCisRequestorId().getIdentifier();
			}
			else if (requestor instanceof RequestorService) {
				this.permissionType = PrivacyPolicyTypeConstants.CIS;
				this.serviceId = ((RequestorService) requestor).getRequestorServiceId().getIdentifier().toString();
				this.cisId = null;
			}
		}
	}

	public void setOwnerId(IIdentity ownerId) {
		if (ownerId != null) {
			this.ownerId = ownerId.getJid();
		}

	}

	public void setDataId(CtxIdentifier dataId) {
		if (dataId != null) {
			this.dataId = dataId.toUriString();
		}
	}

	/*
	 * Set a list of actions as a JSOn formatted string
	 */
	public void setActions(List<Action> actions) {
		this.actions = "[";
		if (null != actions) {
			for(int i=0; i<actions.size(); i++) {
				this.actions += "{\"action\": "+actions.get(i).getActionType().toString()+", \"optional\": "+actions.get(i).isOptional()+"}";
				if (i != (actions.size()-1)) {
					this.actions += ", ";
				}
			}
		}
		this.actions += "]";
	}

	/**
	 * @param permission ResponseItem permission
	 */
	public void setResponseItem(ResponseItem permission) {
		setDataId(permission.getRequestItem().getResource().getCtxIdentifier());
		setActions(permission.getRequestItem().getActions());
		setPermission(permission.getDecision());
	}
	
	
	
	/* --- Normal Setters ---*/
	
	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the permission
	 */
	public Decision getPermission() {
		return permission;
	}

	/**
	 * @param permission the permission to set
	 */
	public void setPermission(Decision permission) {
		this.permission = permission;
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
	 * @return the serviceId
	 */
	public String getServiceId() {
		return serviceId;
	}

	/**
	 * @param serviceId the serviceId to set
	 */
	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

	/**
	 * @return the cisId
	 */
	public String getCisId() {
		return cisId;
	}

	/**
	 * @param cisId the cisId to set
	 */
	public void setCisId(String cisId) {
		this.cisId = cisId;
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
				+ ", permissionType=" + permissionType + ", serviceId="
				+ serviceId + ", cisId=" + cisId + ", ownerId=" + ownerId
				+ ", dataId=" + dataId + ", actions=" + actions
				+ ", permission=" + permission + "]";
	}
}
