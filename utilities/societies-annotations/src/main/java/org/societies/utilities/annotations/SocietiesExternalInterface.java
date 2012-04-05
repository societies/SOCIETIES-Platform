package org.societies.utilities.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to distinguish between Societies External Interfaces that:<br>
 * <ol>
 * <li>
 * are provided by the Societies Framework 
 * for 3rd Party Developers to use (<b>type = provided</b>)
 * </li>
 * <li>
 * are required to be implemented by 3rd Party Services 
 * from the Societies Framework (<b>type = required</b>)
 * </li></ol>
 * 
 * This annotation can only be applied to types (see @Target value). 
 * 
 * @author gspadotto
 *
 */
@Documented
@Target(value = {ElementType.TYPE})
@Retention(RetentionPolicy.SOURCE)
public @interface SocietiesExternalInterface {
	/**
	 * The type of Societies Interface.
	 * Can be either Required or Provided.<br>
	 * <b>Required</b> interfaces are functionalities that need to be implemented by Societies Clients so that framework components can invoke them.<br>
	 * <b>Provided</b> interfaces are functionalities that are exposed to Societies Clients from framework components.<br>
	 */
	public enum SocietiesInterfaceType { REQUIRED, PROVIDED};
	
	SocietiesInterfaceType type() default SocietiesInterfaceType.PROVIDED;
}
