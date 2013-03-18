/*
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske držbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOAÇÃO, SA (PTIN), IBM Corp.,
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

package org.societies.api.activity;

import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.identity.IIdentity;
import org.societies.utilities.annotations.SocietiesExternalInterface;

/**
 * Created with IntelliJ IDEA.
 * User: bjornmagnus
 * Date: 2/8/13
 * Time: 16:06
 */

@SocietiesExternalInterface(type = SocietiesExternalInterface.SocietiesInterfaceType.PROVIDED)
public interface IActivityFeedManager {
    /**
     *
     *
     * @param owner The owner id of the existing or new ActivityFeed, this would typically be a CSS jid.
     * @param feedId The id of the existing or new ActivityFeed.
     * @param pubSub
     * @return {@link IActivityFeed}
     */
    public IActivityFeed getOrCreateFeed(String owner, String feedId, Boolean pubSub);
    public boolean deleteFeed(String owner, String id);
    
    
    /**
     * 
     * This method returns a handler to a remote activity feed
     * 
     * @param {@link ICommManager} iCommMgr is the commManager responsible for dispatching the messages
     * to the remote node
     * @param {@link IIdentity} remoteCISid is the Identity of the remote node
     * @return {@link IActivityFeed} is the handler to the remote activity feed 
     */
    public IActivityFeed getRemoteActivityFeedHandler(ICommManager iCommMgr, IIdentity remoteCISid);
}
