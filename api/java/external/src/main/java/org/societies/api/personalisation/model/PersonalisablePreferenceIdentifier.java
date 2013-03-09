/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske druzbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVACAO, SA (PTIN), IBM Corp.,
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

package org.societies.api.personalisation.model;

import java.io.Serializable;

/**
 * Represents a personalisable preference which a third party service may implement
 * (e.g. a music player 3PS may implement the "volume" preference; this class models that preference and its particular properties) *
 *
 * @author Paddy S (p.skillen@hw.ac.uk)
 */
public class PersonalisablePreferenceIdentifier implements Serializable {

    public enum DataType {
        STRING("Free text"), INTEGER("Integer"), DOUBLE("Decimal"), BOOLEAN("True/false"), ENUM("From list");

        private String friendlyName;

        private DataType(String friendlyName) {
            this.friendlyName = friendlyName;
        }

        public String getFriendlyName() {
            return friendlyName;
        }
    }

    public static PersonalisablePreferenceIdentifier stringPreference(IActionConsumer actionConsumer, String preferenceName, int maxStringChars) {
        PersonalisablePreferenceIdentifier pref = new PersonalisablePreferenceIdentifier(actionConsumer, preferenceName, DataType.STRING);

        pref.maxStringChars = maxStringChars;

        return pref;
    }

    public static PersonalisablePreferenceIdentifier intPreference(IActionConsumer actionConsumer, String preferenceName, int min, int max) {
        PersonalisablePreferenceIdentifier pref = new PersonalisablePreferenceIdentifier(actionConsumer, preferenceName, DataType.INTEGER);

        pref.minNumericValue = min;
        pref.maxNumericValue = max;

        return pref;
    }

    public static PersonalisablePreferenceIdentifier doublePreference(IActionConsumer actionConsumer, String preferenceName, double min, double max) {
        PersonalisablePreferenceIdentifier pref = new PersonalisablePreferenceIdentifier(actionConsumer, preferenceName, DataType.DOUBLE);

        pref.minNumericValue = min;
        pref.maxNumericValue = max;

        return pref;
    }

    public static PersonalisablePreferenceIdentifier boolPreference(IActionConsumer actionConsumer, String preferenceName) {
        PersonalisablePreferenceIdentifier pref = new PersonalisablePreferenceIdentifier(actionConsumer, preferenceName, DataType.BOOLEAN);

        return pref;
    }

    public static PersonalisablePreferenceIdentifier enumPreference(IActionConsumer actionConsumer, String preferenceName, String[] possibleValues) {
        PersonalisablePreferenceIdentifier pref = new PersonalisablePreferenceIdentifier(actionConsumer, preferenceName, DataType.ENUM);

        pref.listValues = possibleValues;

        return pref;
    }

    private IActionConsumer actionConsumer;
    private String preferenceName;
    private DataType dataType;
    private double minNumericValue;
    private double maxNumericValue;
    private int maxStringChars;
    private String[] listValues;

    // NB: This
    private PersonalisablePreferenceIdentifier(IActionConsumer actionConsumer, String preferenceName, DataType dataType) {
        this.actionConsumer = actionConsumer;
        this.preferenceName = preferenceName;
        this.dataType = dataType;
    }

    public IActionConsumer getActionConsumer() {
        return actionConsumer;
    }

    public String getPreferenceName() {
        return preferenceName;
    }

    public DataType getDataType() {
        return dataType;
    }

    public double getMinNumericValue() {
        return minNumericValue;
    }

    public double getMaxNumericValue() {
        return maxNumericValue;
    }

    public int getMaxStringChars() {
        return maxStringChars;
    }

    public String[] getListValues() {
        return listValues;
    }

    @Override
    public boolean equals(Object o) {
        // equals() and hashCode() rely on the actionConsumer and preferenceName only

        if (this == o) return true;
        if (!(o instanceof PersonalisablePreferenceIdentifier)) return false;

        PersonalisablePreferenceIdentifier that = (PersonalisablePreferenceIdentifier) o;

        if (!actionConsumer.equals(that.actionConsumer)) return false;
        if (!preferenceName.equals(that.preferenceName)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        // equals() and hashCode() rely on the actionConsumer and preferenceName only

        int result = actionConsumer.hashCode();
        result = 31 * result + preferenceName.hashCode();
        return result;
    }
}
