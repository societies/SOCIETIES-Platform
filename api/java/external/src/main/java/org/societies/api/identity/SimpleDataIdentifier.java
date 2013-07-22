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
package org.societies.api.identity;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.societies.api.context.model.MalformedCtxIdentifierException;
import org.societies.api.identity.util.DataIdentifierFactory;
import org.societies.api.identity.util.DataIdentifierUtils;
import org.societies.api.schema.identity.DataIdentifier;
import org.societies.api.schema.identity.DataIdentifierScheme;

/**
 * Simple data identifier implementations that helps managing data identifiers
 *
 * @author Olivier Maridat (Trialog)
 *
 */
public class SimpleDataIdentifier extends DataIdentifier {
	private static final long serialVersionUID = 4137288721938940079L;
//	private static final Logger LOG = LoggerFactory.getLogger(SimpleDataIdentifier.class.getName());

	@Override
	public String getUri() {
		if (null != uri) {
			return uri;
		}
		uri = DataIdentifierUtils.toUriString(this);
		return uri;
	}

	@Override
	public String getType() {
		if (null == type && null != uri) {
			try {
				DataIdentifier dataId = DataIdentifierFactory.fromUri(uri);
				scheme = dataId.getScheme();
				type = dataId.getType();
				ownerId = dataId.getOwnerId();
			} catch (MalformedCtxIdentifierException e) {
//				LOG.error("Can't retrieve the data id from its URI");
			}
		}
		return type;
	}

	@Override
	public DataIdentifierScheme getScheme() {
		if (null == scheme && null != uri) {
			try {
				DataIdentifier dataId = DataIdentifierFactory.fromUri(uri);
				scheme = dataId.getScheme();
				type = dataId.getType();
				ownerId = dataId.getOwnerId();
			} catch (MalformedCtxIdentifierException e) {
//				LOG.error("Can't retrieve the data id from its URI");
			}
		}
		return scheme;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SimpleDataIdentifier ["
				+ (scheme != null ? "scheme=" + scheme + ", " : "")
				+ (ownerId != null ? "ownerId=" + ownerId + ", " : "")
				+ (type != null ? "type=" + type + ", " : "")
				+ (uri != null ? "uri=" + uri : "") + "]";
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		// -- Verify reference equality
		if (obj == null) { return false; }
		if (obj == this) { return true; }
		if (obj.getClass() != getClass()) {
			return false;
		}
		// -- Verify obj type
		SimpleDataIdentifier rhs = (SimpleDataIdentifier) obj;
		return new EqualsBuilder()
		.append(this.getScheme(), rhs.getScheme())
		.append(this.getOwnerId(), rhs.getOwnerId())
		.append(this.getType(), rhs.getType())
		.isEquals();
	}
}
