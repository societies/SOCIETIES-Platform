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
package org.societies.context.api.event;

import org.societies.api.context.CtxException;
import org.societies.api.context.event.CtxChangeEvent;
import org.societies.api.context.event.CtxChangeEventListener;
import org.societies.api.context.event.CtxEvent;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.identity.IIdentity;

/**
 * The Context Event Manager is responsible for the subscription and publishing
 * of context events.
 *
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 0.0.4
 */
public interface ICtxEventMgr {

	/**
     * Publishes the specified {@link CtxEvent} to the supplied topics with the
     * specified scope.
     * 
     * @param event
     *            the event to be published.
     * @param topics
     *            the topics to which the event will be published.
     * @param scope
     *            the scope under which the event will be published.
     * @throws NullPointerException
     *             if any of the specified parameters is <code>null</code>.
     */
    public void post(final CtxEvent event, final String[] topics,
    		final CtxEventScope scope);
	
    /**
     * Registers the specified {@link CtxChangeEventListener} for events of the
     * supplied topics. Once registered, the <code>CtxChangeEventListener</code>
     * will handle {@link CtxChangeEvent CtxChangeEvents} associated with the
     * identified owner of context information, i.e. CSS or CIS.
     * <p>
     * To unregister the specified <code>CtxChangeEventListener</code>, use the
     * {@link #unregisterListener(CtxChangeEventListener, String[], IIdentity)}
     * method.
     * 
     * @param listener
     *            the <code>CtxChangeEventListener</code> to register.
     * @param topics
     *            the event topics to register for.
     * @param ownerId
     *            the identifier of the CSS or CIS owning the context model
     *            objects whose events to register for.
     * @throws CtxException
     *             if the registration process of the specified
     *             <code>CtxChangeEventListener</code> fails.
     * @throws NullPointerException
     *             if any of the specified parameters is <code>null</code>.
     * @since 0.4.1
     */
    public void registerChangeListener(final CtxChangeEventListener listener,
            final String[] topics, final IIdentity ownerId) throws CtxException;
    
    /**
     * Unregisters the specified {@link CtxChangeEventListener} for events of
     * the supplied topics related to the identified owner of context
     * information.
     * 
     * @param listener
     *            the <code>CtxChangeEventListener</code> to unregister.
     * @param topics
     *            the event topics to unregister from.
     * @param ownerId
     *            the identifier of the context model object whose events to
     *            unregister from.
     * @throws CtxException
     *             if the unregistration process of the specified
     *             <code>CtxChangeEventListener</code> fails.
     * @throws NullPointerException
     *             if any of the specified parameters is <code>null</code>.
     * @see #registerListener(CtxChangeEventListener, String[], IIdentity)
     * @since 0.4.1
     */
    public void unregisterChangeListener(final CtxChangeEventListener listener,
            final String[] topics, final IIdentity ownerId) throws CtxException;
    
	/**
     * Registers the specified {@link CtxChangeEventListener} for events of the
     * supplied topics. Once registered, the <code>CtxChangeEventListener</code>
     * will handle {@link CtxChangeEvent CtxChangeEvents} associated with the
     * identified context model object.
     * <p>
     * To unregister the specified <code>CtxChangeEventListener</code>, use the
     * {@link #unregisterListener(CtxChangeEventListener, String[], CtxIdentifier)}
     * method.
     * 
     * @param listener
     *            the <code>CtxChangeEventListener</code> to register.
     * @param topics
     *            the event topics to register for.
     * @param ctxId
     *            the identifier of the context model object whose events to
     *            register for.
     * @throws CtxException
     *             if the registration process of the specified
     *             <code>CtxChangeEventListener</code> fails.
     * @throws NullPointerException
     *             if any of the specified parameters is <code>null</code>.
     */
    public void registerChangeListener(final CtxChangeEventListener listener,
            final String[] topics, final CtxIdentifier ctxId) throws CtxException;
    
    /**
     * Unregisters the specified {@link CtxChangeEventListener} for events of
     * the supplied topics related to the identified context model object.
     * 
     * @param listener
     *            the <code>CtxChangeEventListener</code> to unregister.
     * @param topics
     *            the event topics to unregister from.
     * @param ctxId
     *            the identifier of the context model object whose events to
     *            unregister from.
     * @throws CtxException
     *             if the unregistration process of the specified
     *             <code>CtxChangeEventListener</code> fails.
     * @throws NullPointerException
     *             if any of the specified parameters is <code>null</code>.
     * @see #registerListener(CtxChangeEventListener, String[], CtxIdentifier)
     */
    public void unregisterChangeListener(final CtxChangeEventListener listener,
            final String[] topics, final CtxIdentifier ctxId) throws CtxException;
    
    /**
     * Registers the specified {@link CtxChangeEventListener} for events of the
     * supplied topics. Once registered, the <code>CtxChangeEventListener</code>
     * will handle {@link CtxChangeEvent CtxChangeEvents} associated with the
     * the context attribute(s) of the specified scope and type. Note that if a 
     * <code>null</code> type is specified then the supplied listener will
     * receive events associated with any CtxAttribute under the given scope
     * regardless of their type.
     * <p>
     * To unregister the specified <code>CtxChangeEventListener</code>, use the
     * {@link #unregisterListener(CtxChangeEventListener, String[], CtxEntityIdentifier, String)}
     * method.
     * 
     * @param listener
     *            the <code>CtxChangeEventListener</code> to register.
     * @param topics
     *            the event topics to register for.
     * @param scope
     *            the scope of the context attribute(s) whose change events to
	 *            register for. 
	 * @param attrType
	 *            the type of the context attribute(s) whose change events to
	 *            register for, or <code>null</code> to indicate all attributes
     * @throws CtxException
     *             if the registration process of the specified
     *             <code>CtxChangeEventListener</code> fails.
     * @throws NullPointerException
     *             if any of the listener, topics or scope parameter is
     *             <code>null</code>.
     */
    public void registerChangeListener(final CtxChangeEventListener listener,
            final String[] topics, final CtxEntityIdentifier scope,
            final String attrType) throws CtxException;
    
    /**
     * Unregisters the specified {@link CtxChangeEventListener} for events of
     * the supplied topics related to the context attribute(s) with the
     * specified scope and type.
     * 
     * @param listener
     *            the <code>CtxChangeEventListener</code> to unregister.
     * @param topics
     *            the event topics to unregister from.
     * @param scope
     *            the scope of the context attribute(s) whose change events to
	 *            unregister from. 
	 * @param attrType
	 *            the type of the context attribute(s) whose change events to
	 *            unregister from, or <code>null</code> to indicate all
	 *            attributes.
     * @throws CtxException
     *             if the unregistration process of the specified
     *             <code>CtxChangeEventListener</code> fails.
     * @throws NullPointerException
     *             if any of the listener, topics or scope parameter is
     *             <code>null</code>.
     * @see #registerListener(CtxChangeEventListener, String[], CtxEntityIdentifier, String)
     */
    public void unregisterChangeListener(final CtxChangeEventListener listener,
            final String[] topics, final CtxEntityIdentifier scope,
            final String attrType) throws CtxException;
    
    /**
     * 
     * @param ownerId
     * @param topics
     * @throws CtxException
     * @since 1.0
     */
    public void createTopics(final IIdentity ownerId, final String[] topics)
    		throws CtxException;
}