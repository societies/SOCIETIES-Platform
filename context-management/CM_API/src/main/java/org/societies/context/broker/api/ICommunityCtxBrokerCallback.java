package org.societies.context.broker.api;

import java.util.List;

public interface ICommunityCtxBrokerCallback {

	/**
	 * 
	 * @param admCssRetr
	 */
	public void adminCSSRetrieved(ContextEntity admCssRetr);

	/**
	 * 
	 * @param ctxAttribute
	 */
	public void bondsRetrieved(ContextAttribute ctxAttribute);

	/**
	 * 
	 * @param childComms
	 */
	public void childCommsRetrieved(List<ContextEntityIdentifier> childComms);

	/**
	 * 
	 * @param commMembs
	 */
	public void commMembersRetrieved(List <ContextEntityIdentifier> commMembs);

	/**
	 * 
	 * @param parentComms
	 */
	public void parentCommsRetrieved(List<ContextEntityIdentifier> parentComms);
}