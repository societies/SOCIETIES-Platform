/*
 * Copyright (c) 1997, 2008, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms. 
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

package com.sun.xml.txw2;

/**
 * PCDATA.
 *
 * @author Kohsuke Kawaguchi
 */
final class Pcdata extends Text {
    Pcdata(Document document, NamespaceResolver nsResolver, Object obj) {
        super(document, nsResolver, obj);
    }

    void accept(ContentVisitor visitor) {
        visitor.onPcdata(buffer);
    }
}
