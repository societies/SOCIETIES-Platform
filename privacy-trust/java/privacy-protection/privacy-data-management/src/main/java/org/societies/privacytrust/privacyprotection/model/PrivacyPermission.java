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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.model.MalformedCtxIdentifierException;
import org.societies.api.identity.util.DataIdentifierFactory;
import org.societies.api.identity.util.DataIdentifierUtils;
import org.societies.api.identity.util.RequestorUtils;
import org.societies.api.privacytrust.privacy.util.privacypolicy.ActionUtils;
import org.societies.api.privacytrust.privacy.util.privacypolicy.RequestItemUtils;
import org.societies.api.privacytrust.privacy.util.privacypolicy.ResourceUtils;
import org.societies.api.privacytrust.privacy.util.privacypolicy.ResponseItemUtils;
import org.societies.api.schema.identity.DataIdentifier;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Condition;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Decision;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestItem;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Resource;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponseItem;

/**
 * Entity to store privacy permissions for access control persistence
 * 
 * @author Olivier Maridat (Trialog)
 *
 */
@Entity
@Table(name = "PrivacyPermission")
public class PrivacyPermission implements Serializable {
	private static final Logger LOG = LoggerFactory.getLogger(PrivacyPermission.class);
	private static final long serialVersionUID = -5745233622018708564L;

	@Id
	@GeneratedValue
	private Long id;
	private String dataId;
	@Column(length=9000)
	private String requestorId;
	/**
	 * List of actions.
	 * Syntax: action1:action2:action3
	 * E.g.: READ/WRITE
	 */
	private String actions;
	/**
	 * List of action optional flags. In the same order of the list of actions.
	 * Syntax: optional1/optional2/optional3
	 * E.g.: 1/1/0
	 */
	private String actionOptionalFlags;
	/**
	 * Number of actions for this tuple
	 */
	private int nbOfActions;
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
	 * @param actionsStatus
	 * @param permission
	 */
	public PrivacyPermission(String requestorId, String dataId, String actions, String actionOptionalFlags, Decision permission) {
		super();
		this.requestorId = requestorId;
		this.dataId = dataId;
		this.actions = actions;
		this.actionOptionalFlags = actionOptionalFlags;
		this.permission = permission;
	}

	/**
	 * @param requestor
	 * @param ownerId
	 * @param dataId
	 * @param actions
	 * @param permission
	 */
	public PrivacyPermission(RequestorBean requestor, DataIdentifier dataId, List<Action> actions, Decision permission) {
		super();
		setRequestor(requestor);
		setDataId(dataId);
		setActionsToData(actions);
		setPermission(permission);
	}

	/**
	 * @param requestor
	 * @param permission
	 * @throws MalformedCtxIdentifierException 
	 */
	public PrivacyPermission(RequestorBean requestor, ResponseItem permission) throws MalformedCtxIdentifierException {
		this(requestor, ResourceUtils.getDataIdentifier(permission.getRequestItem().getResource()), permission.getRequestItem().getActions(), permission.getDecision());
	}


	/* --- Intelligent Setters --- */

	/**
	 * Retrieve the access control permission as a ResponseItem
	 * @return Permission wrapped as a ResponseItem
	 */
	public ResponseItem createResponseItem() {
		// - Create the resource
		Resource resource = null;
		try {
			resource = ResourceUtils.create(DataIdentifierFactory.fromUri(this.dataId));
		} catch (Exception e) {
			LOG.error("Can't retrieve the data identifier", e);
		}

		// - Create the list of actions
		List<Action> actions = getActionsFromData();

		// - Create the ResponseItem
		RequestItem requestItem = RequestItemUtils.create(resource, actions, new ArrayList<Condition>());
		ResponseItem reponseItem = ResponseItemUtils.create(permission, requestItem);
		return reponseItem;
	}

	public static List<ResponseItem> createResponseItems(List<PrivacyPermission> privacyPermissions) {
		if (null == privacyPermissions || privacyPermissions.size() <= 0) {
			return null;
		}
		List<ResponseItem> permissions = new ArrayList<ResponseItem>();
		for(PrivacyPermission privacyPermission : privacyPermissions) {
			permissions.add(privacyPermission.createResponseItem());
		}
		return permissions;
	}

	public void setDataId(DataIdentifier dataId) {
		if (dataId != null) {
			this.dataId = DataIdentifierUtils.toUriString(dataId);
		}
	}

	public void setRequestor(RequestorBean requestor) {
		if (requestor != null) {
			this.requestorId = RequestorUtils.toUriString(requestor);
		}
	}

	public List<Action> getActionsFromData() {
		List<Action> actions = new ArrayList<Action>();
		if (null != this.actions && !"".equals(this.actions)) {
			int pos = 0, end;
			int posOptional = 0, endOptional;
			// Loop over actions
			while ((end = this.actions.indexOf('/', pos)) >= 0) {
				String action = this.actions.substring(pos, end);
				pos = end + 1;

				endOptional = this.actionOptionalFlags.indexOf('/', posOptional);
				boolean optional = "true".equals(this.actionOptionalFlags.substring(posOptional, endOptional));
				posOptional = endOptional + 1;

				actions.add(ActionUtils.create(action, optional));
			}
		}
		return actions;
	}

	public void setActionsToData(List<Action> actions) {
		if (null == actions)
			return;
		Collections.sort(actions, new ActionUtils.ActionComparator());
		StringBuilder strActions = new StringBuilder();
		StringBuilder strAtionOptionalFlags = new StringBuilder();
		for(Action action : actions) {
			// sb.append(action.getActionConstant().name()+":"+action.isOptional()+"/");
			strActions.append(action.getActionConstant().value()+"/");
			strAtionOptionalFlags.append(action.isOptional()+"/");
		}
		this.nbOfActions = actions.size();
		this.actions = strActions.toString();
		this.actionOptionalFlags = strAtionOptionalFlags.toString();
	}

	/**
	 * @param permission ResponseItem permission
	 */
	public void setResponseItem(ResponseItem permission) {
		setDataId(ResourceUtils.getDataIdUri(permission.getRequestItem().getResource()));
		setActionsToData(permission.getRequestItem().getActions());
		setPermission(permission.getDecision());
	}



	/* --- Normal Setters ---*/
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	public String getDataId() {
		return dataId;
	}
	public void setDataId(String dataId) {
		this.dataId = dataId;
	}

	public String getRequestorId() {
		return requestorId;
	}
	public void setRequestorId(String requestorId) {
		this.requestorId = requestorId;
	}

	public String getActions() {
		return actions;
	}
	public void setActions(String actions) {
		this.actions = actions;
	}

	public String getActionOptionalFlags() {
		return actionOptionalFlags;
	}
	public void setActionOptionalFlags(String actionOptionalFlags) {
		this.actionOptionalFlags = actionOptionalFlags;
	}

	public int getNbOfActions() {
		return nbOfActions;
	}
	public void setNbOfActions(int nbOfActions) {
		this.nbOfActions = nbOfActions;
	}

	public Decision getPermission() {
		return permission;
	}
	public void setPermission(Decision permission) {
		this.permission = permission;
	}


	@Override
	public String toString() {
		return "PrivacyPermission ["
				+ (id != null ? "id=" + id + ", " : "")
				+ (requestorId != null ? "requestorId=" + requestorId + ", " : "")
				+ (dataId != null ? "dataId=" + dataId + ", " : "")
				+ (actions != null ? nbOfActions+" actions=" + actions + ", " : "")
				+ (actionOptionalFlags != null ? "actionOptionalFlags=" + actionOptionalFlags + ", " : "")
				+ (permission != null ? "permission=" + permission : "") + "]";
	}
}
