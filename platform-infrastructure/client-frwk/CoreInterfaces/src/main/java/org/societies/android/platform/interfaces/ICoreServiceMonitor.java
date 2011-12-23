package org.societies.android.platform.interfaces;

public interface ICoreServiceMonitor {
	String methodsArray [] = {"getGreeting()", "getGreeting(String appendToMessage)", "getNumberGreeting(String appendToMessage, int number)"};

	String getGreeting();
	String getGreeting(String appendToMessage);
	String getNumberGreeting(String appendToMessage, int number);
}
