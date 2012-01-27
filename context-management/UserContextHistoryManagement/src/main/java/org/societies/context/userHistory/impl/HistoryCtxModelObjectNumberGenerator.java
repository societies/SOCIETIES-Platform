package org.societies.context.userHistory.impl;

public class HistoryCtxModelObjectNumberGenerator {
    
	private static Long nextValue = 0L;

    public static Long getNextValue() {

        return nextValue++;
    }
}
