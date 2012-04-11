/*
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.xml.bind;

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
    	System.out.println("bundleNameJaxb: "+Messages.class.getName());
        String text = ResourceBundle.getBundle(Messages.class.getName()).getString(property);
    	//String text = getMessageFormat(property);
        return MessageFormat.format(text,args);
    }
    
//
//
// Message resources
//
//
    static final String PROVIDER_NOT_FOUND = // 1 arg
        "ContextFinder.ProviderNotFound";

    static final String COULD_NOT_INSTANTIATE = // 2 args
        "ContextFinder.CouldNotInstantiate";
        
    static final String CANT_FIND_PROPERTIES_FILE = // 1 arg
        "ContextFinder.CantFindPropertiesFile";
        
    static final String CANT_MIX_PROVIDERS = // 0 args
        "ContextFinder.CantMixProviders";
        
    static final String MISSING_PROPERTY = // 2 args
        "ContextFinder.MissingProperty";

    static final String NO_PACKAGE_IN_CONTEXTPATH = // 0 args
        "ContextFinder.NoPackageInContextPath";

    static final String NAME_VALUE = // 2 args
        "PropertyException.NameValue";
        
    static final String CONVERTER_MUST_NOT_BE_NULL = // 0 args
        "DatatypeConverter.ConverterMustNotBeNull";

    static final String ILLEGAL_CAST = // 2 args
        "JAXBContext.IllegalCast";
    
    
    // Replacement to the properties file by jmgoncalves - ugly stuff, dont look
    static String getMessageFormat(String property) {
    	if (property.equals(PROVIDER_NOT_FOUND))
    		return "Provider {0} not found";
    	
    	if (property.equals(COULD_NOT_INSTANTIATE))
    		return "Provider {0} could not be instantiated: {1}";	

    	if (property.equals(CANT_FIND_PROPERTIES_FILE))
    		return "Unable to locate jaxb.properties for package {0}";
    	
    	if (property.equals(CANT_MIX_PROVIDERS))
    			return "You may not mix JAXB Providers on the context path";
    	
    	if (property.equals(MISSING_PROPERTY))
    			return "jaxb.properties in package {0} does not contain the {1} property.";

    	if (property.equals(NO_PACKAGE_IN_CONTEXTPATH))
    			return "No package name is given";

        if (property.equals(NAME_VALUE))
        		return "name: {0} value: {1}";
    	
    	if (property.equals(CONVERTER_MUST_NOT_BE_NULL))
    			return "The DatatypeConverterInterface parameter must not be null";
    	
    	if (property.equals(ILLEGAL_CAST))
    			return "ClassCastException: attempting to cast {0} to {1}.  Please make sure that you are specifying the proper ClassLoader.";
    	
    	return "";
    }
}
