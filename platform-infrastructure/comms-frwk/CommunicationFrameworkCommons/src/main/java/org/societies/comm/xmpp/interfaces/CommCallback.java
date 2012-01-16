package org.societies.comm.xmpp.interfaces;

import org.societies.comm.xmpp.datatypes.Stanza;

/**
 * Implementors of this interface are meant to process the replies resulting
 * from {@link Stanza} messages of the IQ type.
 * 
 */
public interface CommCallback {
	/**
	 * Receive a result
	 * 
	 * @param stanza
	 *            information regarding the stanza that wrapped the payload
	 *            (e.g. To/From IDs)
	 * @param payload
	 *            the payload of the result message
	 */
	void receiveResult(Stanza stanza, Object payload);

	/**
	 * Receive an error
	 * 
	 * @param stanza
	 *            information regarding the error stanza (e.g. To/From IDs)
	 */
	void receiveError(Stanza stanza); // TODO error MAY have payload?? for
										// namespace-specific errors?

}
