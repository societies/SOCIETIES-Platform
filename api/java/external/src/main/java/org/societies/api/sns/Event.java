package org.societies.api.sns;

public class Event {

	/**
	 * EXAMPLE for Facebook
	 * 
	 * { "event": { "name": "Event NAME", "from": "2013-08-11", "to" :
	 * "2013-08-12", "location": "NoWhere", "description" : "Event Post Test" }
	 * }
	 * 
	 */

	String name;
	String fromDate;
	String toDate;
	String place;
	String description;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFromDate() {
		return fromDate;
	}

	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}

	public String getToDate() {
		return toDate;
	}

	public void setToDate(String toDate) {
		this.toDate = toDate;
	}

	public String getPlace() {
		return place;
	}

	public void setPlace(String place) {
		this.place = place;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
