package org.societies.context.model.api;

/**
 * This class defines common {@link ICtxAssociation context association} types.
 * 
 * @author <a href="mailto:nikoskal@users.sourceforge.net">Nikos Kalatzis</a>
 *         (ICCS)
 * @since 0.4.0
 */
public class CtxAssociationTypes {

    /**
     * 
     */
    public static final String ARE_FRIENDS = "areFriends";

    /**
     * 
     */
    public static final String HAS_PREFERENCES = "hasPreferences";

    /**
     * @since 0.5.1
     */
    public static final String HAS_PRIVACY_POLICIES = "hasPrivacyPolicies";

    /**
     * 
     */
    public static final String HAS_PRIVACY_PREFERENCES = "hasPrivacyPreferences";
    
    /**
     * Associates context entities of type {@link CtxEntityTypes#DEVICE} that
     * represent the devices of this PSS.
     * 
     * @since 0.5.2
     */
//    public static final String PSS_DEVICES = "pssDevices";
    public static final String CSS_DEVICES = "cssDevices";
}