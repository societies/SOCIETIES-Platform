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

import org.societies.api.identity.IIdentity;

/**
 * Interface for appending to log
 * 
 * @author Mitja Vardjan
 */
public interface IPrivacyLogAppender {

	/**
	 * Log any outgoing information
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
	//public boolean log(String dataType, Date time, boolean sentToGroup, IIdentity sender,
	//		IIdentity receiver, ChannelType channelId);

	/**
	 * Log any outgoing information that is being sent through Communication Framework with
	 * sendMessage() or sendIQGet()
	 * 
	 * @param sender    CSS ID of the sender
	 * @param receiver    CSS ID of the receiver
	 * @param payload The payload for sendMessage() or sendIQGet()
	 * 
	 * @return true if OK to send the data, false to cancel
	 */
	// the type parameter in comms fw is not type of data. It is not even used at the moment. Only option is to call getClass() on the payload. For any more info the Object payload should be typecasted and parsed (not feasible)
	// implementation: call IIdentity.getType() to see if receiver is CIS (CIS), some other CSS (CSS), or another node within same CSS (CSS_LIGHT, CSS_RICH)
	// implementation: sentToGroup: true if receiver is CIS
	// implementation: channelId = XMPP
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
	public boolean logSN(String dataType, Date time, boolean sentToGroup, IIdentity sender,
			IIdentity receiver, ChannelType channelId);

	public boolean log(LogEntry entry);
}
