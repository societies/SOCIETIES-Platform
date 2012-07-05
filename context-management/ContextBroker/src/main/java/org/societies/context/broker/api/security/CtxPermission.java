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
package org.societies.context.broker.api.security;

import java.security.Permission;

import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxIdentifier;

/**
 * This class represents access to context data. A <code>CtxPermission</code>
 * consists of a {@link CtxIdentifier} and a set of actions valid for the
 * context data specified by that identifier.
 * <p>
 * The actions to be granted are passed to the constructor in a String
 * containing a list of one or more comma-separated keywords. The possible
 * keywords are {@link #READ}, {@link #WRITE}, {@link #CREATE}, and 
 * {@link #DELETE}. Their meaning is defined as follows:
 * <dl>
 *   <dt>{@link #READ}
 *   <dd>Read access to the identified context model object. Allows
 *       {@link ICtxBroker#retrieve} to be called.
 *   <dt>{@link #WRITE}
 *   <dd>Write access to the identified context model object. Allows
 *       {@link ICtxBroker#update} to be called.
 *   <dt>{@link #CREATE}
 *   <dd>Ability to create entities, associations or add attributes to an
 *       existing context entity (scope). Allows
 *       {@link ICtxBroker#createEntity}, {@link ICtxBroker#createAttribute},
 *       or {@link ICtxBroker#createAssociation} to be called.
 *   <dt>{@link #DELETE}
 *   <dd>Ability to delete the identified context model object. Allows
 *       {@link ICtxBroker#remove} to be called.
 * </dl>
 * <p>
 * The actions string is converted to lower case before processing. The
 * following example shows how to create a "read" and "write" permission on a
 * context attribute:
 * 
 * <pre>
 * ICtxAttributeIdentifier attrId;
 * CtxPermission attrReadPerm = new CtxPermission(attrId, &quot;read,write&quot;);
 * </pre>
 *
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 0.4
 */
public final class CtxPermission extends Permission {

	private static final long serialVersionUID = -1210133900549828490L;
	
	/** READ action. */
	public static final String READ = "read"; 
    private static final short READ_CODE = 0x04;
    
    /** WRITE action. */
    public static final String WRITE = "write";
    private static final short WRITE_CODE = 0x02;
    
    /** CREATE action. */
    public static final String CREATE = "create";
    private static final short CREATE_CODE = 0x01;
    
    /** DELETE action. */
    public static final String DELETE = "delete";
    private static final short DELETE_CODE = 0x08;
    
    private volatile CtxIdentifier resource;

    /** The permitted actions mask. */
    private short mask;
    
    /**
     * Creates a new <code>CtxPermission</code> object with the specified
     * actions. <i>resource</i> is the {@link CtxIdentifier} of a context model
     * object, and <i>actions</i> contains a comma-separated list of the
     * desired actions granted on that context model object. Possible actions
     * are "read", "write", "create", and "delete".
     * <p>
     * The following example shows how to create a "read" and "write" permission
     * on a context attribute:
     * 
     * <pre>
     * CtxAttributeIdentifier attrId;
     * CtxPermission attrReadWritePerm = new CtxPermission(attrId, &quot;read,write&quot;);
     * </pre>
     * 
     * @param resource
     *            the identifier of the context model object this permission
     *            refers to.
     * @param actions
     *            the permitted actions on the identified context model object.
     * @throws NullPointerException
     *             if the specified resource is <code>null</code>.
     * @throws IllegalArgumentException
     *             if the specified actions String contains an action other than
     *             the specified possible actions.
     */
	public CtxPermission(final CtxIdentifier resource, final String actions) {
		
		super(resource.toString());
		this.resource = resource;
		this.mask = getMask(actions);
	}

	/**
	 * Returns the {@link CtxIdentfier} of the resource this
     * <code>CtxPermission</code> applies to.
     * 
     * @return the {@link CtxIdentfier} of the resource this
     *         <code>CtxPermission</code> applies to.
	 * 
	 */
	public CtxIdentifier getResource() {
		
		return this.resource;
	}
	
	/**
     * Checks if this <code>CtxPermission</code> object "implies" the specified
     * permission.
     * <p>
     * More specifically, this method returns <code>true</code> if:
     * <ul>
     * <li>
     *   <i>perm</i> is an instance of <code>CtxPermission</code>,
     * </li>
     * <li>
     *   <i>perm</i>'s actions are a proper subset of this object's actions,
     *   and
     * </li>
     * <li>
     *   <i>perm</i>'s context data item is implied by this object's context
     *   identifier. For example, the
     *   <tt>"ownerId/ENTITY/Person/3"</tt> context entity
     *   implies the
     *   <tt>"ownerId/ENTITY/Person/3/ATTRIBUTE/Name/999"</tt>
     *   attribute.
     * </li>
     * </ul>
     * 
     * @param perm
     *            the permission to check against.
     * @return <code>true</code> if the specified permission is a
     *         <code>CtxPermission</code> implied by this object;
     *         <code>false</code> otherwise.
     */
	@Override
	public boolean implies(Permission perm) {
		
		if (!(perm instanceof CtxPermission))
            return false;

        CtxPermission that = (CtxPermission) perm;
        return ((this.mask & that.mask) == that.mask)
                && this.impliesIgnoreMask(that);
	}

	/**
     * Return the canonical String representation of the actions. Always returns
     * permitted actions in the following order: read, write, create, delete.
     * 
     * @return the canonical String representation of the permitted actions.
     */
	@Override
	public String getActions() {
		
		StringBuilder sb = new StringBuilder();
        boolean comma = false;

        if ((this.mask & READ_CODE) == READ_CODE) {
            comma = true;
            sb.append(READ);
        }

        if ((this.mask & WRITE_CODE) == WRITE_CODE) {
            if (comma)
                sb.append(',');
            else
                comma = true;
            sb.append(WRITE);
        }

        if ((this.mask & CREATE_CODE) == CREATE_CODE) {
            if (comma)
                sb.append(',');
            else
                comma = true;
            sb.append(CREATE);
        }

        if ((this.mask & DELETE_CODE) == DELETE_CODE) {
            if (comma)
                sb.append(',');
            else
                comma = true;
            sb.append(DELETE);
        }

        return sb.toString();
	}
	
	/**
     * Returns the hash code value for this <code>CtxPermission</code>.
     * 
     * @return the hash code value for this <code>CtxPermission</code>.
     */
    @Override
    public int hashCode() {
    	
        final int prime = 31;
        int result = 1;
        result = prime * result + this.getName().hashCode();
        result = prime * result + this.mask;
        
        return result;
    }
    
    /**
     * Checks two <code>CtxPermission</code> objects for equality. Checks if
     * that <i>obj</i> is a <code>CtxPermission</code>, and has the same context
     * data identifier string and actions as this object.
     * <P>
     * 
     * @param obj
     *            the object we are testing for equality with this object.
     * @return <code>true</code> if obj is a <code>CtxPermission</code>, and
     *         has the same context data identifier string and actions as this
     *         <code>CtxPermission</code> object; <code>false</code> otherwise.
     */
    @Override
    public boolean equals(Object obj) {
    	
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (this.getClass() != obj.getClass())
            return false;

        CtxPermission that = (CtxPermission) obj;
        return (this.getName().equals(that.getName())
                && this.mask == that.mask);
    }
    
    /**
     * Extracts the permitted actions mask from an actions String.
     * 
     * @param actions
     *            the actions String.
     */
    private static short getMask(final String actions) {
    	
        short mask = 0x00;

        // Null actions String is considered valid, i.e. no actions permitted
        if (actions == null)
            return mask;

        char[] a = actions.toCharArray();
        int i = a.length - 1;

        // Empty actions String is considered valid, i.e. no actions permitted
        if (i < 0)
            return mask;

        while (i != -1) {
            char c;

            // skip whitespace
            while ((i != -1)
                    && ((c = a[i]) == ' ' || c == '\r' || c == '\n'
                            || c == '\f' || c == '\t'))
                i--;

            // check for the known strings
            int matchlen;

            if (i >= 3 && (a[i - 3] == 'r' || a[i - 3] == 'R')
                    && (a[i - 2] == 'e' || a[i - 2] == 'E')
                    && (a[i - 1] == 'a' || a[i - 1] == 'A')
                    && (a[i] == 'd' || a[i] == 'D')) {
                matchlen = 4;
                mask |= READ_CODE;

            } else if (i >= 4 && (a[i - 4] == 'w' || a[i - 4] == 'W')
                    && (a[i - 3] == 'r' || a[i - 3] == 'R')
                    && (a[i - 2] == 'i' || a[i - 2] == 'I')
                    && (a[i - 1] == 't' || a[i - 1] == 'T')
                    && (a[i] == 'e' || a[i] == 'E')) {
                matchlen = 5;
                mask |= WRITE_CODE;

            } else if (i >= 5 && (a[i - 5] == 'c' || a[i - 5] == 'C')
                    && (a[i - 4] == 'r' || a[i - 4] == 'R')
                    && (a[i - 3] == 'e' || a[i - 3] == 'E')
                    && (a[i - 2] == 'a' || a[i - 2] == 'A')
                    && (a[i - 1] == 't' || a[i - 1] == 'T')
                    && (a[i] == 'e' || a[i] == 'E')) {
                matchlen = 6;
                mask |= CREATE_CODE;

            } else if (i >= 5 && (a[i - 5] == 'd' || a[i - 5] == 'D')
                    && (a[i - 4] == 'e' || a[i - 4] == 'E')
                    && (a[i - 3] == 'l' || a[i - 3] == 'L')
                    && (a[i - 2] == 'e' || a[i - 2] == 'E')
                    && (a[i - 1] == 't' || a[i - 1] == 'T')
                    && (a[i] == 'e' || a[i] == 'E')) {
                matchlen = 6;
                mask |= DELETE_CODE;

            } else {
                // parse error
                throw new IllegalArgumentException("invalid permission: "
                        + actions);
            }

            // Make sure we didn't just match the tail of a word
            // like "qwertyread". Also, skip to the comma.
            boolean foundComma = false;
            while (i >= matchlen && !foundComma) {
                switch (a[i - matchlen]) {
                case ',':
                    foundComma = true;
                case ' ':
                case '\r':
                case '\n':
                case '\f':
                case '\t':
                    break;
                default:
                    throw new IllegalArgumentException("invalid permission: "
                            + actions);
                }
                i--;
            }

            // point i at the location of the comma minus one (or -1).
            i -= matchlen;
        }

        return mask;
    }
    
    /**
     * Checks if the context data item identified in the specified
     * <code>CtxPermission</code> is implied by this object's context
     * identifier.
     * <p>
     * For example, the <tt>"ownerId/ENTITY/person/3"</tt>
     * context entity implies the
     * <tt>"ownerId/ENTITY/person/3/ATTRIBUTE/Name/999"</tt>
     * attribute.
     * 
     * @param that
     *            the <code>CtxPermission</code> to check against this object's
     *            context identifier.
     * @return <code>true</code> if the context data item identified in the
     *         specified <code>CtxPermission</code> is implied by this object's
     *         context identifier; <code>false</code> otherwise.
     */
    private boolean impliesIgnoreMask(CtxPermission that) {
    	
        // Same context identifier
        if (this.getResource().equals(that.getResource()))
            return true;

        // That context attribute identifier's scope is this context entity
        // identifier
        if (this.getResource() instanceof CtxEntityIdentifier && 
        		that.getResource() instanceof CtxAttributeIdentifier) {
        	
        		final CtxEntityIdentifier thisEntCtxId = 
    				(CtxEntityIdentifier) this.getResource();
        		final CtxAttributeIdentifier thatAttrCtxId = 
        				(CtxAttributeIdentifier) that.getResource();
        		if (thisEntCtxId.equals(thatAttrCtxId.getScope()))
        			return true;
        }
        
        return false;
    }
}