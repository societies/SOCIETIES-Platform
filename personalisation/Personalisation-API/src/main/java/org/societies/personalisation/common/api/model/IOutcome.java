package org.societies.personalisation.common.api;

import java.io.Serializable;

/**
 * Interface that extends the @see IAction
 * @author Elizabeth
 * @version 1.0
 * @created 08-Nov-2011 13:25:58
 */
public interface IOutcome extends Serializable, IAction {

	public int getConfidenceLevel();

}