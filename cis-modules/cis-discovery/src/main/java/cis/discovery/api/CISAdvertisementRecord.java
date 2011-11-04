/**
 * This class defines a data type that is used stored in and managed by a CIS Directory.
 * 
 * @author Babak Farshchian
 * @version 0
 * 
 */

package cis.discovery.api;

public class CISAdvertisementRecord {
	private String name, id, uri;
	
	public CISAdvertisementRecord(String name, String id, String uri) {
		super();
		this.name = name;
		this.id = id;
		this.uri = uri;
	}
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}


}
