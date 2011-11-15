package org.societies.context.broker.api.platform;

import java.util.List;

import org.societies.context.model.api.CtxAttribute;
import org.societies.context.model.api.CtxEntity;
import org.societies.context.model.api.CtxEntityIdentifier;

public interface ICommunityCtxBrokerCallback {
	/**
	 * 
	 * @param admCssRetr
	 */
	public void adminCSSRetrieved(CtxEntity admCssRetr);

	/**
	 * 
	 * @param ctxAttribute
	 */
	public void bondsRetrieved(CtxAttribute ctxAttribute);

	/**
	 * 
	 * @param childComms
	 */
	public void childCommsRetrieved(List<CtxEntityIdentifier> childComms);

	/**
	 * 
	 * @param commMembs
	 */
	public void commMembersRetrieved(List <CtxEntityIdentifier> commMembs);

	/**
	 * 
	 * @param parentComms
	 */
	public void parentCommsRetrieved(List<CtxEntityIdentifier> parentComms);

}
