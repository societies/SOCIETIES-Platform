package org.societies.device.common;

public class OSDetailsImpl implements OSDetails {
	
	String name,  version, build;
	
	public OSDetailsImpl(String name, String version,String build){
		this.name = name;
		this.version = version;
		this.build = build;
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getVersion() {
		return version;
	}

	@Override
	public String getBuild() {
		return build;
	}

}
