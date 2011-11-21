//package org.personalsmartspace.cm.reasoning.api.platform;
package org.societies.context.user.inference.api.platform;

//import org.personalsmartspace.cm.model.api.pss3p.ICtxAttribute;
import org.societies.context.model.CtxAttribute;

/**
 * This exception is thrown whenever a CtxRefinement algorithm
 * is not able to infer a value for a Ctx Attribute.
 */
public class NotInferredException extends Exception {
    
    private static final long serialVersionUID = 1L;

    public NotInferredException(CtxAttribute unrefinedAttr) {
        super();
        this.unrefinedAttr = unrefinedAttr;
    }

    
    
    private CtxAttribute unrefinedAttr = null;
    
    /** 
     * Returns the Ctx Attribute whose value has not been set
     * through CtxInference
     */
    public synchronized CtxAttribute getUnrefinedAttr() {
        return unrefinedAttr;
    }
    
        
    

}
