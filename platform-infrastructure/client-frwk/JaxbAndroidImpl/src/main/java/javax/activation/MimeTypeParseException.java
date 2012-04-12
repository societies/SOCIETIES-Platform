/*
 * %W% %E%
 *
 * Copyright (c) 2006, Oracle and/or its affiliates. All rights reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package javax.activation;

/**
 * A class to encapsulate MimeType parsing related exceptions.
 *
 * @since 1.6
 */
public class MimeTypeParseException extends Exception {

    /**
     * Constructs a MimeTypeParseException with no specified detail message. 
     */
    public MimeTypeParseException() {
	super();
    }

    /**
     * Constructs a MimeTypeParseException with the specified detail message. 
     *
     * @param   s   the detail message.
     */
    public MimeTypeParseException(String s) {
	super(s);
    }
}
