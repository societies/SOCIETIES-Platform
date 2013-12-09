package org.societies.webapp.controller;

import java.io.IOException;
import java.util.Arrays;

import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.model.util.SerialisationHelper;
import org.societies.api.identity.IIdentity;
import org.societies.webapp.service.OpenfireLoginService;
import org.societies.webapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;

@Controller
@ManagedBean(name = "loginController") //required to access data from XHTML files
@SessionScoped // indicates the lifetime of this object
public class LoginController extends BasePageController {

    @ManagedProperty(value = "#{userService}")
    private UserService userService;

    @ManagedProperty(value = "#{openfireLoginService}")
    private OpenfireLoginService openfireLoginService;

    @Autowired
	private ICommManager commMngrRef;
    
    private String loginDialogUsername;
    private String loginDialogPassword;

    public LoginController() {
        log.debug("LoginController() ctor");
    }

    public String loginButtonAction() {
        // NB actions should return a String object - it is used by faces-config.xml

        // NB: use loginDialogUsername and loginDialogPassword - these are populated from the front end
        // "username" field should be populated by this method, then the loginDialogUsername and loginDialogPassword cleared

        if (isLoggedIn()) {
            // log out before logging in again
            logoutAction();
        }

        if (loginDialogUsername == null || loginDialogUsername.isEmpty()
                || loginDialogPassword == null || loginDialogPassword.isEmpty()) {
            String summary = "Login failed";
            String detail = "Username or password were blank";
            addGlobalMessage(summary, detail, FacesMessage.SEVERITY_WARN);
            return "false";
        }
        
        
        String currentNodeId = openfireLoginService.getCommManager().getIdManager().getThisNetworkNode().getIdentifier();
        if (!loginDialogUsername.trim().equals(currentNodeId)) {
            String summary = "Login failed";
            String detail = "Username or password were wrong";
            addGlobalMessage(summary, detail, FacesMessage.SEVERITY_WARN);
            return "false";
        }
        

        String result = openfireLoginService.doLogin(loginDialogUsername, loginDialogPassword);
        if (result == null) {
            String summary = "Login failed";
            String detail = "Incorrect username or password";
            addGlobalMessage(summary, detail, FacesMessage.SEVERITY_WARN);

            return "false";
        }

        userService.login();

//        String summary = "User " + getUsername() + " logged in";
//        String detail = "User successfully logged in";
//        addGlobalMessage(summary, detail, FacesMessage.SEVERITY_INFO);

        // clean up
        setLoginDialogUsername("");
        setLoginDialogPassword("");

        return "true";
    }

    public String logoutAction() {
        log.debug("logoutAction()");

        if (!isLoggedIn()){
            return "false";
        }        
        
        //redirect
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        try {
			externalContext.redirect("http://societies.local.macs.hw.ac.uk:8080/societies-platform/index.html");
			log.debug("redirected to http://societies.local.macs.hw.ac.uk:8080/societies-platform/index.html");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
  
        String summary = "User " + getUsername() + " logged out";
        String detail = "User logged out";

        userService.logout();
        
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove("contextManagementController");

        addGlobalMessage(summary, detail, FacesMessage.SEVERITY_INFO);
      

        return "true";
    }

    public boolean isLoggedIn() {
        if (userService == null) {
            log.error("userService is null - cannot determine login state");
            return false;
        }

        return userService.isUserLoggedIn();
    }

    public String getUsername() {
        return userService.getUsername();
    }

    @SuppressWarnings("UnusedDeclaration")
    public String getUserID() {
        return userService.getUserID();
    }

    public IIdentity getIdentity() {
        return userService.getIdentity();
    }

    public void setLoginDialogUsername(String username) {
        this.loginDialogUsername = username;
    }

    public String getLoginDialogUsername() {
        return loginDialogUsername;
    }

    public void setLoginDialogPassword(String password) {
        this.loginDialogPassword = password;
    }

    public String getLoginDialogPassword() {
        return loginDialogPassword;
    }

    public String restLogin(String username, String serializedPassword) {
    	return restLogin(username, serializedPassword, false);
    }
    
    public String restLogin(String username, String serializedPassword, boolean redirect) {
        String result;
		// Check credentiels
        try {
			result = openfireLoginService.doLogin(username, fromBytesString(serializedPassword));
		} catch (Exception e) {
			result = null;
		}
        // Login to the T6.5 webapp
        if (null != result) {
        	userService.login();
        }

        // Redirect
        if (redirect) {
        	FacesContext context = FacesContext.getCurrentInstance();
        	HttpServletResponse response = (HttpServletResponse)context.getExternalContext().getResponse();
        	try {
				response.sendRedirect("index.xhtml");
			} catch (IOException e) {
				return "Error...";
			}
        	return "";
    	}
        // Or display result number
        if (null == result) {
        	return "401";
        }
        return "200";
    }
    
	
	private String fromBytesString(String bytesStr) {
		String[] byteValues = bytesStr.split(":");
		byte[] bytes = new byte[byteValues.length];
		for (int i=0, len=bytes.length; i<len; i++) {
		   bytes[i] = Byte.valueOf(byteValues[i].trim());     
		}
		return new String(bytes);
	}
	
	 public UserService getUserService() {
	        return userService;
	    }

	    public void setUserService(UserService userService) {
	        log.debug("setUserService() has been called with " + userService);
	        this.userService = userService;
	    }

	    public OpenfireLoginService getOpenfireLoginService() {
	        return openfireLoginService;
	    }

	    public void setOpenfireLoginService(OpenfireLoginService openfireLoginService) {
	        log.debug("setOpenfireLoginService() has been called with " + openfireLoginService);
	        this.openfireLoginService = openfireLoginService;
	    }
	    
	    public ICommManager getCommManager() {
			return commMngrRef;
		}
		public void setCommManager(ICommManager commManager) {
			 log.info("#############setCommManager() has been called with");
			this.commMngrRef = commMngrRef;
		}
}
