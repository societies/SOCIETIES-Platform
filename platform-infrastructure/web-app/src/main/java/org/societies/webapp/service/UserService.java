package org.societies.webapp.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.identity.IIdentity;
import org.societies.webapp.ILoginListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Maria Mannion, Paddy Skillen
 */
@Service
@Scope("Session")
@SessionScoped // JSF
@ManagedBean // JSF
public class UserService implements Serializable {
    private static Logger log = LoggerFactory.getLogger(UserService.class);

    @Autowired
    @ManagedProperty(value = "#{commMngrRef}")
    private ICommManager commMngrRef;

    public final List<ILoginListener> loginListeners = new ArrayList<ILoginListener>();

    private boolean userLoggedIn;
    private String username;
    private String userID;
    private IIdentity identity;

    public UserService() {
        log.info("UserService constructor");
        userLoggedIn = false;
    }

    @SuppressWarnings("UnusedDeclaration")
    public ICommManager getCommMngrRef() {
        return commMngrRef;
    }

    @SuppressWarnings("UnusedDeclaration")
    public void setCommMngrRef(ICommManager commMngrRef) {
        this.commMngrRef = commMngrRef;
    }

    @SuppressWarnings("UnusedDeclaration")
    public boolean addLoginListener(ILoginListener loginListener) {
        synchronized (loginListeners) {
            return loginListeners.add(loginListener);
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    public boolean removeLoginListener(ILoginListener loginListener) {
        synchronized (loginListeners) {
            return loginListeners.remove(loginListener);
        }
    }

    public String getUsername() {
        return username;
    }

    public String getUserID() {
        return userID;
    }

    public boolean isUserLoggedIn() {
        return userLoggedIn;
    }

    public IIdentity getIdentity() {
        return identity;
    }

    public void login() {
        if (isUserLoggedIn()) {
            logout();
        }

        IIdentity identity = commMngrRef.getIdManager().getThisNetworkNode();

        if (identity == null) {
            clearCurrentUser();
            return;
        }

        String userID = identity.getBareJid();
        String username = userID.substring(0, userID.indexOf('.'));

        this.userLoggedIn = true;
        this.identity = identity;
        this.userID = userID;
        this.username = username;
        this.log.info("#CODE2#: User "+this.identity.getBareJid()+" logged in on webapp ");
        // let listeners know
        for (ILoginListener listener : loginListeners) {
            try {
                listener.userLoggedIn();
            } catch (Exception ex) {
                log.error("Exception in login listener", ex);
                // do nothing?
            }
        }
    }

    public void logout() {
    	this.log.info("User "+this.identity.getBareJid()+" logged out of webapp ");
    	clearCurrentUser();
        
        // let listeners know
        for (ILoginListener listener : loginListeners) {
            try {
                listener.userLoggedOut();
            } catch (Exception ex) {
                log.error("Exception in logout listener", ex);
                // do nothing?
            }
        }
    }

    private void clearCurrentUser() {
        this.userLoggedIn = false;
        this.identity = null;
        this.userID = null;
        this.username = null;
    }

}
