package org.societies.css.directory.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * This is the Class accepted by the CssDiroectory when a css wants to register
 * and advertisment record. This Object contains attributes used to retrieve
 * services shared from/to a CSS/CIS and also information to retrieve
 * organization that has developed the service.
 * 
 * @author mmannion
 * @version 1.0
 */

@Entity
@Table(name = "CssAdvertisementRecordEntry")
public class CssAdvertisementRecordEntry implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7819484667842436359L;
	/**
	 * 
	 */

	private String name;
	private String id;
	private String uri;

	/**
	 * @return the name
	 */
	@Column(name = "Name")
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the id
	 */
	@Column(name = "Id")
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the uri
	 */
	@Column(name = "Uri")
	public String getUri() {
		return uri;
	}

	/**
	 * @param uri
	 *            the uri to set
	 */
	public void setUri(String uri) {
		this.uri = uri;
	}

	/**
	 * @param name
	 * @param id
	 * @param uri
	 */
	public CssAdvertisementRecordEntry(String name, String id, String uri) {
		super();
		this.name = name;
		this.id = id;
		this.uri = uri;
	}
	
	

}