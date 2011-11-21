package org.societies.context.model.api;

/**
 * Used to represent the origin of a context attribute value. This information
 * is part of the Quality of Context (QoC) parameters of
 * <code>ICtxAttribute</code> objects. The context origin can be one of the
 * following types:
 * <ul>
 * <li>{@link #MANUALLY_SET}: Denotes a manually set context attribute value</li>
 * <li>{@link #SENSED}: Denotes a sensed context attribute value</li>
 * <li>{@link #INFERRED}: Denotes an inferred context attribute value</li>
 * </ul>
 * 
 * @author <a href="mailto:nliampotis@users.sourceforge.net">Nicolas
 *         Liampotis</a> (ICCS)
 * @see ICtxQuality
 * @since 0.3.0
 */
public enum CtxOriginType {

    /**
     * The enum constant for a manually set context attribute values
     */
    MANUALLY_SET,

    /**
     * The enum constant for a sensed context attribute values
     */
    SENSED,

    /**
     * The enum constant for an inferred context attribute value
     */
    INFERRED,
}