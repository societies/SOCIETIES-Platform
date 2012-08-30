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
package org.societies.cis.directory.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import static javax.persistence.GenerationType.IDENTITY;

/**
 * Describe your class here...
 *
 * @author aleckey
 *
 */
@Entity
@Table(name="cis_directory_membershipcriteria")
public class CriteriaRecordEntry implements Serializable {

	private static final long serialVersionUID = 1819484667842436359L;
	
    private Integer criteria_id;
	protected String attrib;
    protected String operator;
    protected String value1;
    protected String value2;
    protected Integer rank;
    private CisAdvertisementRecordEntry cisAdvertRecord;

	/**@return the criteria_id
	 */
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name="criteria_id")
    public Integer getCriteria_id() {
		return criteria_id;
	}

	/**@param criteria_id the criteria_id to set
	 */
	public void setCriteria_id(Integer criteria_id) {
		this.criteria_id = criteria_id;
	}
	
    /**Gets the attrib property. */
    @Column(name="attrib")
    public String getAttrib() {
        return attrib;
    }

    /**Sets the value of the attrib property */
    public void setAttrib(String value) {
        this.attrib = value;
    }

    /**Gets the value of the operator property */
    @Column(name="operator")
    public String getOperator() {
        return operator;
    }

    /**Sets the value of the operator property */
    public void setOperator(String value) {
        this.operator = value;
    }

    /**Gets the value of the value1 property */
    @Column(name="value1")
    public String getValue1() {
        return value1;
    }

    /**Sets the value of the value1 property */
    public void setValue1(String value) {
        this.value1 = value;
    }

    /**Gets the value of the value2 property */
    @Column(name="value2")
    public String getValue2() {
        return value2;
    }

    /**Sets the value of the value2 property */
    public void setValue2(String value) {
        this.value2 = value;
    }

    /**Gets the value of the rank property */
    @Column(name="rank")
    public Integer getRank() {
        return rank;
    }

    /**Sets the value of the rank property */
    public void setRank(Integer value) {
        this.rank = value;
    }
    
	/**@return the cisAdvertRecord
	 */
    @ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="cis_id", nullable=false)
	public CisAdvertisementRecordEntry getCisAdvertRecord() {
		return cisAdvertRecord;
	}

	/**
	 * @param cisAdvertRecord the cisAdvertRecord to set
	 */
	public void setCisAdvertRecord(CisAdvertisementRecordEntry cisAdvertRecord) {
		this.cisAdvertRecord = cisAdvertRecord;
	}
	
	/**
	 * @param attrib
	 * @param operator
	 * @param value1
	 * @param value2
	 * @param rank
	 * @param cisAdvertRecord
	 */
	public CriteriaRecordEntry(String attrib, String operator, String value1, String value2, Integer rank) {
		super();
		this.attrib = attrib;
		this.operator = operator;
		this.value1 = value1;
		this.value2 = value2;
		this.rank = rank;
	}
	
	public CriteriaRecordEntry() {
		super();
	}
}
