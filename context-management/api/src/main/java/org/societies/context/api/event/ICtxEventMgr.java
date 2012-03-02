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
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxIdentifier;

/**
 * The Context Event Manager is responsible for the subscription and publishing
 * of context events.
 *
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 0.0.4
 */
public interface ICtxEventMgr {

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
     */
    public void registerListener(final CtxChangeEventListener listener,
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
     * @see #registerListener(CtxChangeEventListener, String[], CtxIdentifier)
     */
    public void unregisterListener(final CtxChangeEventListener listener,
            final String[] topics, final CtxIdentifier ctxId) throws CtxException;
    
    /**
     * Registers the specified {@link CtxChangeEventListener} for events of the
     * supplied topics. Once registered, the <code>CtxChangeEventListener</code>
     * will handle {@link CtxChangeEvent CtxChangeEvents} associated with the
     * the context attribute(s) of the specified scope and type.
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
	 *            register for.
     * @throws CtxException
     *             if the registration process of the specified
     *             <code>CtxChangeEventListener</code> fails.
     */
    public void registerListener(final CtxChangeEventListener listener,
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
	 *            unregister from.
     * @throws CtxException
     *             if the unregistration process of the specified
     *             <code>CtxChangeEventListener</code> fails.
     * @see #registerListener(CtxChangeEventListener, String[], CtxEntityIdentifier, String)
     */
    public void unregisterListener(final CtxChangeEventListener listener,
            final String[] topics, final CtxEntityIdentifier scope,
            final String attrType) throws CtxException;
    
    /**
     * Publishes the specified {@link CtxChangeEvent} under the supplied topics
     * and scope.
     * 
     * @param event
     *            the event to be published.
     * @param topics
     *            the topics under which the event will be published.
     * @param scope
     *            the scope of the event to be published.
     * @throws CtxException if publishing of the specified event fails
     */
    public void publish(final CtxChangeEvent event, final String[] topics,
    		final CtxEventScope scope) throws CtxException;
}