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

package org.societies.api.internal.privacytrust.privacyprotection.model.privacyassessment;

import java.util.Date;

import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.interfaces.ICommCallback;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.broker.ICtxBroker;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.Requestor;

/**
 * Interface for appending to log
 * 
 * @author Mitja Vardjan
 */
public interface IPrivacyLogAppender {

	/**
	 * Log any outgoing information that is being sent through Communication Framework with either:<br/>
	 * - {@link ICommManager#sendIQGet(Stanza, Object, ICommCallback)} <br/>
	 * - {@link ICommManager#sendMessage(Stanza, Object)} <br/>
	 * - {@link ICommManager#sendMessage(Stanza, String, Object)} <br/>
	 * 
	 * @param sender    CSS ID of the sender
	 * @param receiver    CSS ID of the receiver
	 * @param payload The payload for sendMessage() or sendIQGet()
	 * 
	 * @return true if OK to send the data, false to cancel
	 */
	public boolean logCommsFw(IIdentity sender, IIdentity receiver, Object payload);

	/**
	 * Log any outgoing information that is being sent from local CSS to a social network.
	 * 
	 * @param dataType
	 * @param time
	 * @param sentToGroup    True if multicast
	 * @param sender    CSS ID of the sender
	 * @param receiver    CSS ID of the receiver
	 * @param channelId    ID of the channel. Examples: facebook, facebook wall post,
	 * twitter, XMPP
	 * 
	 * @return true if OK to send the data, false to cancel
	 */
//	@Deprecated
//	public boolean logSN(String dataType, Date time, boolean sentToGroup, IIdentity sender,
//			IIdentity receiver, ChannelType channelId);

	/**
	 * Log any read access to context with
	 * {@link ICtxBroker#retrieve(Requestor, CtxIdentifier)}
	 * and other relevant retrieve* methods.
	 * 
	 * @param requestor The requestor for 3P invocations, or null for platform invocations
	 * @param dataOwner The CSS or CIS that owns the data, the CSS that data is related to.
	 */
	public void logContext(Requestor requestor, IIdentity dataOwner);

	/**
	 * Log any read access to context with
	 * {@link ICtxBroker#retrieve(Requestor, CtxIdentifier)}
	 * and other relevant retrieve* methods.
	 * 
	 * @param requestor The requestor for 3P invocations, or null for platform invocations
	 * @param dataOwner The CSS or CIS that owns the data, the CSS that data is related to.
	 * @param dataSize Size of data in bytes
	 */
	public void logContext(Requestor requestor, IIdentity dataOwner, int dataSize);
	
	/**
	 * General logger for transmission of data.
	 * 
	 * @param entry
	 * @return
	 */
	public boolean log(DataTransmissionLogEntry entry);
	
	/**
	 * General logger for accessing data.
	 * 
	 * @param entry
	 */
	public void log(DataAccessLogEntry entry);
}
