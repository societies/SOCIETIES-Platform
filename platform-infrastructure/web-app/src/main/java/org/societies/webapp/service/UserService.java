package org.societies.webapp.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.identity.IIdentity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author Maria Mannion
 */
@Service
@Scope("Session")
public class UserService {

    private static Logger log = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private ICommManager commMngrRef;

    private boolean userLoggedIn;
    private String username;
    private String userID;
    private IIdentity identity;

    public UserService() {
        log.info("UserService constructor");
        userLoggedIn = false;
    }

    public ICommManager getCommMngrRef() {
        return commMngrRef;
    }

    public void setCommMngrRef(ICommManager commMngrRef) {
        this.commMngrRef = commMngrRef;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public boolean isUserLoggedIn() {
        return userLoggedIn;
    }

    public void setUserLoggedIn(boolean userLoggedIn) {
        this.userLoggedIn = userLoggedIn;
    }

    public void destroyOnSessionEnd() {
        log.info("UserService destroyOnSessionEnd");
    }

    public void setIdentity(IIdentity identity) {
        this.identity = identity;
    }

    public IIdentity getIdentity() {
        return identity;
    }

    public void loadUserDetailsFromCommMgr() {
        if (!isUserLoggedIn()) {
            clearCurrentUser();
            return;
        }

        IIdentity identity = commMngrRef.getIdManager().getThisNetworkNode();

        if (identity == null) {
            clearCurrentUser();
            return;
        }

        String userID = identity.getBareJid();
        String username = userID.substring(0, userID.indexOf('.'));

        setIdentity(identity);
        setUserID(userID);
        setUsername(username);
    }

    private void clearCurrentUser() {
        setUserLoggedIn(false);
        setIdentity(null);
        setUserID(null);
        setUsername(null);
    }

    public void loadUserDetailsIntoModel(Map<String, Object> model) {
        if (!isUserLoggedIn()) {
            clearCurrentUser();
            // don't return - continue to load all the nulls into the model
        }

        model.put("identity", identity);
        model.put("userid", userID);
        model.put("username", username);
    }
}
