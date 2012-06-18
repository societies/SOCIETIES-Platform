package org.societies.android.api.context.event;

import org.societies.android.api.context.model.CtxIdentifier;

public class CtxChangeEvent extends CtxEvent {

	private static final long serialVersionUID = 7552325378500918037L;

	/**
	 * Constructs a <code>CtxChangeEvent</code> object for the specified context
	 * model object.
	 * 
	 * @param source
	 *            the <code>CtxIdentifier</code> of the context model object
	 *            upon which this event occurred.
	 */
	public CtxChangeEvent(CtxIdentifier source) {
		
		super(source);
	}
	
	/**
	 * Returns the <code>CtxIdentifier</code> of the context model object that
	 * was added, removed or modified.
	 * 
	 * @return the <code>CtxIdentifier</code> of the context model object that
	 *         was added, removed or modified.
	 */
	public CtxIdentifier getId() {
		
		return (CtxIdentifier) super.source;
	}
}