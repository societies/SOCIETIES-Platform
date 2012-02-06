package org.societies.interfaces;

public interface Callback {
	void receiveResult(String xml);

	void receiveError(String xml); 
	
	void receiveMessage(String xml);
}
