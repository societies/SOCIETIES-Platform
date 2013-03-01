package org.societies.da.openfire.plugin;

public enum OperationType {
	add("add"),
	delete("delete"),
	update("update"),
	enable("enable"),
	disable("disable"),
	login("login");
	
	private String s;
	
	private OperationType(String s) {
		this.s = s;
	}

	@Override
	public String toString() {
		return s;
	}
}
