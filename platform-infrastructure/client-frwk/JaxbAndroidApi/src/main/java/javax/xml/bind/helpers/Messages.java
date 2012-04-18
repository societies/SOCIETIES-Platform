/*
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.xml.bind.helpers;

import java.text.MessageFormat;
import java.util.ResourceBundle;

/**
 * Formats error messages.
 */
class Messages
{
	static String format( String property ) {
        return format( property, null );
    }
    
    static String format( String property, Object arg1 ) {
        return format( property, new Object[]{arg1} );
    }
    
    static String format( String property, Object arg1, Object arg2 ) {
        return format( property, new Object[]{arg1,arg2} );
    }
    
    static String format( String property, Object arg1, Object arg2, Object arg3 ) {
        return format( property, new Object[]{arg1,arg2,arg3} );
    }
    
    // add more if necessary.
    
    /** Loads a string resource and formats it with specified arguments. */
    static String format( String property, Object[] args ) {
    	//System.err.println("............................................");
        //String text = ResourceBundle.getBundle(Messages.class.getName()).getString(property);
        String text = ResourceBundle.getBundle(HELPER_MESSAGES_ORIGINAL_NAME).getString(property);
        return MessageFormat.format(text,args);
    	/*if(property.equals("AbstractMarshallerImpl.MustBeBoolean"))
    		return "{0} must be boolean";
    	else if(property.equals("AbstractMarshallerImpl.MustBeString"))
    		return "{0} must be a String";
    	else if(property.equals("AbstractUnmarshallerImpl.ISNotNull"))
    		return "InputStream can not be null";
    	else if(property.equals("DefaultValidationEventHandler.Error"))
    		return "[ERROR]:";
    	else if(property.equals("DefaultValidationEventHandler.FatalError"))
    		return "[FATAL_ERROR]:";
    	else if(property.equals("DefaultValidationEventHandler.LocationUnavailable"))
    		return "unavailable";
    	else if(property.equals("DefaultValidationEventHandler.SeverityMessage"))
    		return "DefaultValidationEventHandler: {0} {1} Location: {2}";
    	else if(property.equals("DefaultValidationEventHandler.UnrecognizedSeverity"))
    		return "Unrecognized event severity field {0}";
    	else if(property.equals("DefaultValidationEventHandler.Warning"))
    		return "[WARNING]:";
    	else if(property.equals("Shared.MustNotBeNull"))
    		return "{0} parameter must not be null";
    	else if(property.equals("ValidationEventImpl.IllegalSeverity"))
    		return "Illegal severity"; 
    	return "";*/

    }
    
//
//
// Message resources
//
//
    static final String INPUTSTREAM_NOT_NULL = // 0 args
        "AbstractUnmarshallerImpl.ISNotNull";
        
    static final String MUST_BE_BOOLEAN = // 1 arg
        "AbstractMarshallerImpl.MustBeBoolean";
       
    static final String MUST_BE_STRING = // 1 arg
        "AbstractMarshallerImpl.MustBeString";
        
    static final String SEVERITY_MESSAGE = // 3 args
        "DefaultValidationEventHandler.SeverityMessage";

    static final String LOCATION_UNAVAILABLE = // 0 args
        "DefaultValidationEventHandler.LocationUnavailable";
        
    static final String UNRECOGNIZED_SEVERITY = // 1 arg
        "DefaultValidationEventHandler.UnrecognizedSeverity";
        
    static final String WARNING = // 0 args
        "DefaultValidationEventHandler.Warning";

    static final String ERROR = // 0 args
        "DefaultValidationEventHandler.Error";

    static final String FATAL_ERROR = // 0 args
        "DefaultValidationEventHandler.FatalError";
        
    static final String ILLEGAL_SEVERITY = // 0 args
        "ValidationEventImpl.IllegalSeverity";
        
    static final String MUST_NOT_BE_NULL = // 1 arg
        "Shared.MustNotBeNull";
    
    
    // Ugly fix for loading unshaded properties file
    static final String HELPER_MESSAGES_ORIGINAL_NAME = "javax.xml.bind.helpers.Messages";
}
