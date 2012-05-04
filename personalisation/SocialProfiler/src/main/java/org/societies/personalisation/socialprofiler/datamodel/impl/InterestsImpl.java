package org.societies.personalisation.socialprofiler.datamodel.impl;


import org.neo4j.graphdb.Node;
import org.societies.personalisation.socialprofiler.datamodel.Interests;
import org.societies.personalisation.socialprofiler.datamodel.utils.NodeProperties;

public class InterestsImpl implements Interests,NodeProperties {

	private final Node underlyingNode;
	
	/**
	 * constructor of interests
	 * @param underlyingNode
	 * 			Node
	 */			
	public InterestsImpl(Node underlyingNode) {
		super();
		this.underlyingNode = underlyingNode;
	}

	/**
	 * returns the underlying node of the interests
	 * @return	Node underlyingNode
	 */
	public Node getUnderlyingNode() {
		return underlyingNode;
	}

	//@Override
	public String getAboutMe() {
		return (String) underlyingNode.getProperty( ABOUT_ME_PROPERTY );
	}

	//@Override
	public String getActivities() {
		return (String) underlyingNode.getProperty( ACTIVITIES_PROPERTY );
	}

	//@Override
	public String getBooks() {
		return (String) underlyingNode.getProperty( BOOKS_PROPERTY );
	}

	//@Override
	public String getInterests() {
		return (String) underlyingNode.getProperty( INTERESTS_PROPERTY );
	}

	//@Override
	public String getMovies() {
		return (String) underlyingNode.getProperty( MOVIES_PROPERTY );
	}

	//@Override
	public String getMusic() {
		return (String) underlyingNode.getProperty( MUSIC_PROPERTY );
	}

	//@Override
	public String getName() {
		return (String) underlyingNode.getProperty( NAME_PROPERTY );
	}

	//@Override
	public String getProfileUpdateTime() {
		return (String) underlyingNode.getProperty( PROFILE_UPDATETIME_PROPERTY );
	}

	//@Override
	public String getQuotations() {
		return (String) underlyingNode.getProperty( QUOTATIONS_PROPERTY );
	}

	//@Override
	public void setAboutMe(String aboutMe) {
		underlyingNode.setProperty( ABOUT_ME_PROPERTY, aboutMe );
		
	}

	//@Override
	public void setActivities(String activities) {
		underlyingNode.setProperty( ACTIVITIES_PROPERTY, activities );
		
	}

	//@Override
	public void setBooks(String books) {
		underlyingNode.setProperty( BOOKS_PROPERTY, books );
		
	}

	//@Override
	public void setInterests(String interests) {
		underlyingNode.setProperty( INTERESTS_PROPERTY, interests );
		
	}

	//@Override
	public void setMovies(String movies) {
		underlyingNode.setProperty( MOVIES_PROPERTY, movies );
	}

	//@Override
	public void setMusic(String music) {
		underlyingNode.setProperty( MUSIC_PROPERTY, music );
	}

	//@Override
	public void setName(String name) {
		underlyingNode.setProperty( NAME_PROPERTY, name );
		
	}

	//@Override
	public void setProfileUpdateTime(String profileUpdateTime) {
		underlyingNode.setProperty( PROFILE_UPDATETIME_PROPERTY, profileUpdateTime );
	}

	//@Override
	public void setQuotations(String quotations) {
		underlyingNode.setProperty( QUOTATIONS_PROPERTY, quotations );
	}

}
