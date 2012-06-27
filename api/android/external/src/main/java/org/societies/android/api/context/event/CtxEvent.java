package org.societies.android.api.context.event;

import java.util.EventObject;

public abstract class CtxEvent extends EventObject {

	private static final long serialVersionUID = 2584003286855450802L;

	/**
	 * Constructs a <code>CtxEvent</code> with the specified source.
	 * 
	 * @param source
	 *            the object upon which this event occurred.
	 */
	public CtxEvent(Object source) {
		
		super(source);
	}
}