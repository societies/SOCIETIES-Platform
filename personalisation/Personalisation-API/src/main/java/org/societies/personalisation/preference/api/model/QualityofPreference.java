package org.societies.personalisation.preference.api.model;

import java.util.Date;

/**
 * @author Elizabeth
 * @version 1.0
 * @created 08-Nov-2011 14:02:57
 */
public class QualityofPreference implements IQualityofPreference {

	/**
	 * how many times this outcome has been aborted
	 */
	private int abortedCounter;
	/**
	 * when was the last time this outcome was aborted
	 */
	private Date lastAborted;
	/**
	 * when was this outcome last learnt
	 */
	private Date lastModified;
	/**
	 * when was the last time this outcome was successfully implemented
	 */
	private Date lastSuccess;
	/**
	 * how many times this outcome has been successfully implemented
	 */
	private int successCounter;



	public void finalize() throws Throwable {

	}

	public QualityofPreference(){

	}

	public int getAbortedCounter(){
		return 0;
	}

	public Date getLastAborted(){
		return null;
	}

	public Date getLastModified(){
		return null;
	}

	public Date getLastSuccess(){
		return null;
	}

	public int getSuccessCounter(){
		return 0;
	}

	/**
	 * (non-Javadoc)
	 * @see org.personalsmartspace.pm.prefmodel.api.platform.
	 * IQualityofPreference#increaseAbortedCounter(int)
	 * 
	 * @param level
	 */
	@Override
	public void increaseAbortedCounter(int level){

	}

	/**
	 * 
	 * @param level
	 */
	public void increaseSuccessCounter(int level){

	}

	/**
	 * 
	 * @param lastAborted
	 */
	public void setLastAborted(Date lastAborted){

	}

	/**
	 * 
	 * @param lastModified
	 */
	public void setLastModified(Date lastModified){

	}

	/**
	 * 
	 * @param lastSuccess
	 */
	public void setLastSuccess(Date lastSuccess){

	}

}