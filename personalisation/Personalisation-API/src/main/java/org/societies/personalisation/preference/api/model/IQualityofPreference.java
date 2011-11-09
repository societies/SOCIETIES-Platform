package org.societies.personalisation.preference.api;

import java.util.Date;

/**
 * @author Elizabeth
 * @version 1.0
 * @created 08-Nov-2011 14:02:56
 */
public interface IQualityofPreference {

	/**
	 * Retrieves how many times the outcome has been aborted
	 * @return
	 */
	public int getAbortedCounter();

	/**
	 * Retrieves the Date the outcome was last aborted
	 * @return		the last aborted Date
	 */
	public Date getLastAborted();

	/**
	 * get the Date an outcome was last learnt
	 * @return	the Date
	 */
	public Date getLastModified();

	/**
	 * Retrieves the Date the outcome was last successfully implemented
	 * @return
	 */
	public Date getLastSuccess();

	/**
	 * Retrieves how many times the outcome has been successfully implemented
	 * @return		the successCounter
	 */
	public int getSuccessCounter();

	/**
	 * increases the abortedCounter by level
	 * 
	 * @param level    by how much to increase the abortedCounter
	 */
	public void increaseAbortedCounter(int level);

	/**
	 * increases the counter for successful implementations by level
	 * 
	 * @param level    by how much to increase the successCounter
	 */
	public void increaseSuccessCounter(int level);

	/**
	 * Changes the Date the outcome was last aborted
	 * 
	 * @param lastAborted    the Date last aborted
	 */
	public void setLastAborted(Date lastAborted);

	/**
	 * set the Date an outcome was last learnt
	 * 
	 * @param lastModified    lastModified
	 */
	public void setLastModified(Date lastModified);

	/**
	 * Changes the date the outcome was last successfully implemented
	 * 
	 * @param lastSuccess    lastSuccess
	 */
	public void setLastSuccess(Date lastSuccess);

}