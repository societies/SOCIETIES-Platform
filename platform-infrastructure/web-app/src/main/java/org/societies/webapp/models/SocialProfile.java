package org.societies.webapp.models;

public class SocialProfile {
	
	private String name;
	private String id;
	private String icon;
	private String thumbnail;
	private String connection_id;
	/**
	 * @return the connection_id
	 */
	public String getConnection_id() {
		return connection_id;
	}
	/**
	 * @param connection_id the connection_id to set
	 */
	public void setConnection_id(String connection_id) {
		this.connection_id = connection_id;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * @return the icon
	 */
	public String getIcon() {
		return icon;
	}
	/**
	 * @param icon the icon to set
	 */
	public void setIcon(String icon) {
		this.icon = icon;
	}
	/**
	 * @return the thumbnail
	 */
	public String getThumbnail() {
		return thumbnail;
	}
	/**
	 * @param thumbnail the thumbnail to set
	 */
	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}

}
