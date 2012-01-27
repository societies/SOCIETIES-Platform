package org.societies.api.comm.xmpp.datatypes;

public enum ErrorType {
	AUTH("auth"),			// -- retry after providing credentials
	CANCEL("cancel"),		// -- do not retry (the error cannot be remedied)
	CONTINUE("continue"),	// -- proceed (the condition was only a warning)
	MODIFY("modify"),		// -- retry after changing the data sent
	WAIT("wait");			// -- retry after waiting (the error is temporary)
	
	private final String typeString;
	
	private ErrorType(String typeString) {
		this.typeString = typeString;
	}

	@Override
	public String toString() {
		return typeString;
	}
}
