package org.societies.personalisation.socialprofiler.datamodel.impl;

import org.neo4j.graphdb.RelationshipType;

public enum RelTypes implements RelationshipType{
	IS_FRIEND_WITH ,
	IS_A_MEMBER_OF ,
	IS_A_FAN_OF,
	HAS_A_PROFILE,
	IS_KNOWN_AS,
	HAS,
	LIKES,
	BELONGS_TO,
	BELONGS_TO_SUBTYPE,
	BELONGS_TO_TYPE,
	PREFERS_SUBTYPE,
	PREFERS_TYPE,
	TRAVERSER
}
