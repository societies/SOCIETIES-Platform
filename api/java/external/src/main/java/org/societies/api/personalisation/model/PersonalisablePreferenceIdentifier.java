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

    public static PersonalisablePreferenceIdentifier stringPreference(String preferenceName, int maxStringChars) {
        PersonalisablePreferenceIdentifier pref = new PersonalisablePreferenceIdentifier(preferenceName, DataType.STRING);

        pref.maxStringChars = maxStringChars;

        return pref;
    }

    public static PersonalisablePreferenceIdentifier intPreference(String preferenceName, int min, int max) {
        PersonalisablePreferenceIdentifier pref = new PersonalisablePreferenceIdentifier(preferenceName, DataType.INTEGER);

        pref.minNumericValue = min;
        pref.maxNumericValue = max;

        return pref;
    }

    public static PersonalisablePreferenceIdentifier doublePreference(String preferenceName, double min, double max) {
        PersonalisablePreferenceIdentifier pref = new PersonalisablePreferenceIdentifier(preferenceName, DataType.DOUBLE);

        pref.minNumericValue = min;
        pref.maxNumericValue = max;

        return pref;
    }

    public static PersonalisablePreferenceIdentifier boolPreference(String preferenceName) {
        PersonalisablePreferenceIdentifier pref = new PersonalisablePreferenceIdentifier(preferenceName, DataType.BOOLEAN);

        return pref;
    }

    public static PersonalisablePreferenceIdentifier enumPreference(String preferenceName, String[] possibleValues) {
        PersonalisablePreferenceIdentifier pref = new PersonalisablePreferenceIdentifier(preferenceName, DataType.ENUM);

        pref.listValues = possibleValues;

        return pref;
    }

    private String preferenceName;
    private DataType dataType;
    private double minNumericValue;
    private double maxNumericValue;
    private int maxStringChars;
    private String[] listValues;

    // NB: This
    private PersonalisablePreferenceIdentifier(String preferenceName, DataType dataType) {
        this.preferenceName = preferenceName;
        this.dataType = dataType;
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

}
