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
package org.societies.privacytrust.trust.api.event;

import org.societies.api.privacytrust.trust.event.ITrustUpdateEventListener;
import org.societies.api.privacytrust.trust.event.TrustEvent;
import org.societies.api.privacytrust.trust.event.TrustUpdateEvent;
import org.societies.api.privacytrust.trust.model.TrustedEntityId;
import org.societies.api.privacytrust.trust.model.TrustedEntityType;

/**
 * The Trust Event Manager is responsible for the subscription and publishing
 * of trust-related events.
 *
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 0.0.5
 */
public interface ITrustEventMgr {

	/**
     * Publishes the specified {@link TrustEvent} to the supplied topics. All
     * supported event topics are defined in {@link TrustEventTopic}.
     * 
     * @param event
     *            the event to be published.
     * @param topics
     *            the topics to which the event will be published.
     * @throws NullPointerException
     *             if any of the specified parameters is <code>null</code>.
     * @since 0.5
     */
    public void postEvent(final TrustEvent event, final String[] topics);
    
    /**
     * Registers the specified {@link ITrustUpdateEventListener} for all local
     * events of the supplied topics. Once registered, the 
     * <code>ITrustUpdateEventListener</code> will handle 
     * {@link TrustUpdateEvent TrustUpdateEvents} associated with any locally
     * updated trust value.
     * <p>
     * To unregister the specified <code>ITrustUpdateEventListener</code>, use
     * the {@link #unregisterUpdateListener(ITrustUpdateEventListener, String[])}
     * method.
     * 
     * @param listener
     *            the <code>ITrustUpdateEventListener</code> to register.
     * @param topics
     *            the event topics to register for.
     * @throws TrustEventMgrException
     *             if the registration process of the specified
     *             <code>ITrustUpdateEventListener</code> fails.
     * @throws NullPointerException
     *             if any of the specified parameters is 
     *             <code>null</code>.
     * @see #unregisterUpdateListener(ITrustUpdateEventListener, String[])
     * @since 1.0
     */
    public void registerUpdateListener(final ITrustUpdateEventListener listener, 
			final String[] topics) throws TrustEventMgrException;
    
    /**
     * Unregisters the specified {@link ITrustUpdateEventListener} from all 
     * local events of the supplied topics. Once unregistered, 
     * the <code>ITrustUpdateEventListener</code> will no longer receive 
     * {@link TrustUpdateEvent TrustUpdateEvents} associated with any locally
     * updated trust value.
     * <p>
     * The method has no effect if the specified
     * <code>ITrustUpdateEventListener</code> has not been previously
     * registered using the 
     * {@link #registerUpdateListener(ITrustUpdateEventListener, String[])}
     * method.
     * 
     * @param listener
     *            the <code>ITrustUpdateEventListener</code> to unregister.
     * @param topics
     *            the event topics to unregister from.
     * @throws TrustEventMgrException
     *             if the unregistration process of the specified
     *             <code>ITrustUpdateEventListener</code> fails.
     * @throws NullPointerException
     *             if any of the specified parameters is 
     *             <code>null</code>.
     * @see #registerUpdateListener(ITrustUpdateEventListener, String[])
     * @since 1.0
     */
    public void unregisterUpdateListener(final ITrustUpdateEventListener listener, 
			final String[] topics) throws TrustEventMgrException;
    
    /**
     * Registers the specified {@link ITrustUpdateEventListener} for events of the
     * supplied topics. Once registered, the <code>ITrustUpdateEventListener</code>
     * will handle {@link TrustUpdateEvent TrustUpdateEvents} associated with the 
     * values assigned by the given trustor.
     * <p>
     * To unregister the specified <code>ITrustUpdateEventListener</code>, use
     * the {@link #unregisterUpdateListener(ITrustUpdateEventListener, String[], TrustedEntityId)}
     * method.
     * 
     * @param listener
     *            the <code>ITrustUpdateEventListener</code> to register.
     * @param topics
     *            the event topics to register for.
     * @param trustorId
	 *            the identifier of the entity which places trust in
	 *            the specified trustee.
     * @throws TrustEventMgrException
     *             if the registration process of the specified
     *             <code>ITrustUpdateEventListener</code> fails.
     * @throws NullPointerException
     *             if any of the specified listener or topics is 
     *             <code>null</code>.
     * @see #unregisterUpdateListener(ITrustUpdateEventListener, String[], TrustedEntityId)
     * @since 1.0
     */
    public void registerUpdateListener(final ITrustUpdateEventListener listener, 
			final String[] topics, final TrustedEntityId trustorId) 
					throws TrustEventMgrException;
    
    /**
     * Unregisters the specified {@link ITrustUpdateEventListener} from events
     * of the supplied topics. Once unregistered, 
     * the <code>ITrustUpdateEventListener</code> will no longer receive 
     * {@link TrustUpdateEvent TrustUpdateEvents} associated with the values
     * assigned by the given trustor.
     * <p>
     * The method has no effect if the specified
     * <code>ITrustUpdateEventListener</code> has not been previously
     * registered using the 
     * {@link #registerUpdateListener(ITrustUpdateEventListener, String[], TrustedEntityId)}
     * method.
     * 
     * @param listener
     *            the <code>ITrustUpdateEventListener</code> to unregister.
     * @param topics
     *            the event topics to unregister from.
     * @param trustorId
	 *            the identifier of the entity which places trust in
	 *            the specified trustees.
     * @throws TrustEventMgrException
     *             if the unregistration process of the specified
     *             <code>ITrustUpdateEventListener</code> fails.
     * @throws NullPointerException
     *             if any of the specified parameters is 
     *             <code>null</code>.
     * @see #registerUpdateListener(ITrustUpdateEventListener, String[], TrustedEntityId)
     * @since 1.0
     */
    public void unregisterUpdateListener(final ITrustUpdateEventListener listener, 
			final String[] topics, final TrustedEntityId trustorId)
					throws TrustEventMgrException;
    
    /**
     * Registers the specified {@link ITrustUpdateEventListener} for events of the
     * supplied topics. Once registered, the <code>ITrustUpdateEventListener</code>
     * will handle {@link TrustUpdateEvent TrustUpdateEvents} associated with the 
     * value assigned to the specified trustee by the given trustor.
     * <p>
     * To unregister the specified <code>ITrustUpdateEventListener</code>, use
     * the {@link #unregisterUpdateListener(ITrustUpdateEventListener, String[], TrustedEntityId, TrustedEntityId)}
     * method.
     * 
     * @param listener
     *            the <code>ITrustUpdateEventListener</code> to register.
     * @param topics
     *            the event topics to register for.
     * @param trustorId
	 *            the identifier of the entity which places trust in
	 *            the specified trustee.
	 * @param trusteeId
	 *            the identifier of the entity whose trust update events to
	 *            register for.
     * @throws TrustEventMgrException
     *             if the registration process of the specified
     *             <code>ITrustUpdateEventListener</code> fails.
     * @throws NullPointerException
     *             if any of the specified parameters is 
     *             <code>null</code>.
     * @see #unregisterUpdateListener(ITrustUpdateEventListener, String[], TrustedEntityId, TrustedEntityId)
     * @since 0.5
     */
    public void registerUpdateListener(final ITrustUpdateEventListener listener, 
			final String[] topics, final TrustedEntityId trustorId,
			final TrustedEntityId trusteeId) throws TrustEventMgrException;
    
    /**
     * Unregisters the specified {@link ITrustUpdateEventListener} from events
     * of the supplied topics. Once unregistered, 
     * the <code>ITrustUpdateEventListener</code> will no longer receive 
     * {@link TrustUpdateEvent TrustUpdateEvents} associated with the value
     * assigned to the specified trustee by the given trustor.
     * <p>
     * The method has no effect if the specified
     * <code>ITrustUpdateEventListener</code> has not been previously
     * registered using the 
     * {@link #registerUpdateListener(ITrustUpdateEventListener, String[], TrustedEntityId, TrustedEntityId)}
     * method.
     * 
     * @param listener
     *            the <code>ITrustUpdateEventListener</code> to unregister.
     * @param topics
     *            the event topics to unregister from.
     * @param trustorId
	 *            the identifier of the entity which places trust in
	 *            the specified trustee.
	 * @param trusteeId
	 *            the identifier of the entity whose trust update events to
	 *            unregister from.
     * @throws TrustEventMgrException
     *             if the unregistration process of the specified
     *             <code>ITrustUpdateEventListener</code> fails.
     * @throws NullPointerException
     *             if any of the specified parameters is 
     *             <code>null</code>.
     * @see #registerUpdateListener(ITrustUpdateEventListener, String[], TrustedEntityId, TrustedEntityId)
     * @since 1.0
     */
    public void unregisterUpdateListener(final ITrustUpdateEventListener listener, 
			final String[] topics, final TrustedEntityId trustorId,
			final TrustedEntityId trusteeId) throws TrustEventMgrException;
    
    /**
     * Registers the specified {@link ITrustUpdateEventListener} for events of the
     * supplied topics. Once registered, the <code>ITrustUpdateEventListener</code>
     * will handle {@link TrustUpdateEvent TrustUpdateEvents} associated with the 
     * values assigned to entities of the specified type by the given trustor.
     * <p>
     * To unregister the specified <code>ITrustUpdateEventListener</code>, use
     * the {@link #unregisterUpdateListener(ITrustUpdateEventListener, String[], TrustedEntityId, TrustedEntityType)}
     * method.
     * 
     * @param listener
     *            the <code>ITrustUpdateEventListener</code> to register.
     * @param topics
     *            the event topics to register for.
     * @param trustorId
	 *            the identifier of the entity which places trust in
	 *            the specified trustee.
	 * @param trusteeType
	 *            the type of the entities whose trust update events to
	 *            register for.
     * @throws TrustEventMgrException
     *             if the registration process of the specified
     *             <code>ITrustUpdateEventListener</code> fails.
     * @throws NullPointerException
     *             if any of the specified listener or topics is 
     *             <code>null</code>.
     * @see #unregisterUpdateListener(ITrustUpdateEventListener, String[], TrustedEntityId, TrustedEntityType)
     * @since 1.0
     */
    public void registerUpdateListener(final ITrustUpdateEventListener listener, 
			final String[] topics, final TrustedEntityId trustorId,
			final TrustedEntityType trusteeType) throws TrustEventMgrException;
    
    /**
     * Unregisters the specified {@link ITrustUpdateEventListener} from events
     * of the supplied topics. Once unregistered, 
     * the <code>ITrustUpdateEventListener</code> will no longer receive 
     * {@link TrustUpdateEvent TrustUpdateEvents} associated with the values
     * assigned to entities of the specified type by the given trustor.
     * <p>
     * The method has no effect if the specified
     * <code>ITrustUpdateEventListener</code> has not been previously
     * registered using the 
     * {@link #registerUpdateListener(ITrustUpdateEventListener, String[], TrustedEntityId, TrustedEntityType)}
     * method.
     * 
     * @param listener
     *            the <code>ITrustUpdateEventListener</code> to unregister.
     * @param topics
     *            the event topics to unregister from.
     * @param trustorId
	 *            the identifier of the entity which places trust in
	 *            the specified trustees.
	 * @param trusteeType
	 *            the type of the entities whose trust update events to
	 *            unregister from.
     * @throws TrustEventMgrException
     *             if the unregistration process of the specified
     *             <code>ITrustUpdateEventListener</code> fails.
     * @throws NullPointerException
     *             if any of the specified parameters is 
     *             <code>null</code>.
     * @see #registerUpdateListener(ITrustUpdateEventListener, String[], TrustedEntityId, TrustedEntityType)
     * @since 1.0
     */
    public void unregisterUpdateListener(final ITrustUpdateEventListener listener, 
			final String[] topics, final TrustedEntityId trustorId,
			final TrustedEntityType trusteeType) throws TrustEventMgrException;
    
    /**
     * Registers the specified {@link ITrustEvidenceUpdateEventListener} for
     * all local events of the supplied topics. Once registered, the <code>
     * ITrustEvidenceUpdateEventListener</code> will handle {@link 
     * TrustEvidenceUpdateEvent TrustEvidenceUpdateEvents} associated with the
     * identified subject and object.
     * <p>
     * To unregister the specified 
     * <code>ITrustEvidenceUpdateEventListener</code>, use the 
     * {@link #unregisterEvidenceUpdateListener(ITrustEvidenceUpdateEventListener, String[])} 
     * method.
     * 
     * @param listener
     *            the <code>ITrustEvidenceUpdateEventListener</code> to register.
     * @param topics
     *            the event topics to register for.
     * @throws TrustEventMgrException
     *             if the registration process of the specified
     *             <code>ITrustEvidenceUpdateEventListener</code> fails.
     * @throws NullPointerException
     *             if any of the specified parameters is <code>null</code>.
     * @see #unregisterEvidenceUpdateListener(ITrustEvidenceUpdateEventListener, String[])
     * @since 0.5
     */
    public void registerEvidenceUpdateListener(
    		final ITrustEvidenceUpdateEventListener listener, 
			final String[] topics) throws TrustEventMgrException;
    
    /**
     * Unregisters the specified {@link ITrustEvidenceUpdateEventListener} from
     * all local events of the supplied topics. Once unregistered, the <code>
     * ITrustEvidenceUpdateEventListener</code> will no longer receive {@link 
     * TrustEvidenceUpdateEvent TrustEvidenceUpdateEvents}.
     * <p>
     * The method has no effect if the specified 
     * <code>ITrustEvidenceUpdateEventListener</code> has not been previously
     * registered using the 
     * {@link #registerEvidenceUpdateListener(ITrustEvidenceUpdateEventListener, String[])}
     * method.
     * 
     * @param listener
     *            the <code>ITrustEvidenceUpdateEventListener</code> to unregister.
     * @param topics
     *            the event topics to unregister from.
     * @throws TrustEventMgrException
     *             if the unregistration process of the specified
     *             <code>ITrustEvidenceUpdateEventListener</code> fails.
     * @throws NullPointerException
     *             if any of the specified parameters is <code>null</code>.
     * @see #registerEvidenceUpdateListener(ITrustEvidenceUpdateEventListener, String[])
     * @since 1.0
     */
    public void unregisterEvidenceUpdateListener(
    		final ITrustEvidenceUpdateEventListener listener, 
			final String[] topics) throws TrustEventMgrException;
    
    /**
     * Registers the specified {@link ITrustEvidenceUpdateEventListener} for
     * events of the supplied topics. Once registered, the <code>
     * ITrustEvidenceUpdateEventListener</code> will handle {@link 
     * TrustEvidenceUpdateEvent TrustEvidenceUpdateEvents} associated with the
     * identified subject and object.
     * <p>
     * To unregister the specified 
     * <code>ITrustEvidenceUpdateEventListener</code>, use the 
     * {@link #unregisterEvidenceUpdateListener(ITrustEvidenceUpdateEventListener, String[], TrustedEntityId, TrustedEntityId)} 
     * method.
     * 
     * @param listener
     *            the <code>ITrustEvidenceUpdateEventListener</code> to register.
     * @param topics
     *            the event topics to register for.
     * @param subjectId
	 *            the identifier of the subject whose evidence updates to
	 *            register for.
	 * @param objectId
	 *            the identifier of the object whose evidence updates to
	 *            register for.
     * @throws TrustEventMgrException
     *             if the registration process of the specified
     *             <code>ITrustEvidenceUpdateEventListener</code> fails.
     * @throws NullPointerException
     *             if any of the specified parameters is <code>null</code>.
     * @see #unregisterEvidenceUpdateListener(ITrustEvidenceUpdateEventListener, String[], TrustedEntityId, TrustedEntityId)
     * @since 0.5
     */
    public void registerEvidenceUpdateListener(
    		final ITrustEvidenceUpdateEventListener listener, 
			final String[] topics, final TrustedEntityId subjectId,
			final TrustedEntityId objectId) throws TrustEventMgrException;
    
    /**
     * Unregisters the specified {@link ITrustEvidenceUpdateEventListener} from
     * events of the supplied topics. Once unregistered, the <code>
     * ITrustEvidenceUpdateEventListener</code> will no longer receive {@link 
     * TrustEvidenceUpdateEvent TrustEvidenceUpdateEvents} associated with the
     * identified subject and object.
     * <p>
     * The method has no effect if the specified 
     * <code>ITrustEvidenceUpdateEventListener</code> has not been previously
     * registered using the 
     * {@link #registerEvidenceUpdateListener(ITrustEvidenceUpdateEventListener, String[], TrustedEntityId, TrustedEntityId)}
     * method.
     * 
     * @param listener
     *            the <code>ITrustEvidenceUpdateEventListener</code> to unregister.
     * @param topics
     *            the event topics to unregister from.
     * @param subjectId
	 *            the identifier of the subject whose evidence updates to
	 *            unregister from.
	 * @param objectId
	 *            the identifier of the object whose evidence updates to
	 *            unregister from.
     * @throws TrustEventMgrException
     *             if the unregistration process of the specified
     *             <code>ITrustEvidenceUpdateEventListener</code> fails.
     * @throws NullPointerException
     *             if any of the specified parameters is <code>null</code>.
     * @see #registerEvidenceUpdateListener(ITrustEvidenceUpdateEventListener, String[], TrustedEntityId, TrustedEntityId)
     * @since 1.0
     */
    public void unregisterEvidenceUpdateListener(
    		final ITrustEvidenceUpdateEventListener listener, 
			final String[] topics, final TrustedEntityId subjectId,
			final TrustedEntityId objectId) throws TrustEventMgrException;
}