package org.societies.webapp.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import java.io.Serializable;

public abstract class BasePageController implements Serializable {
    protected final Logger log = LoggerFactory.getLogger(getClass()); // NB: NOT static


    protected void addGlobalMessage(String summary, String detail, FacesMessage.Severity severity) {
        addFacesMessage(null, summary, detail, severity);
    }

    protected void addFacesMessage(String component, String summary, String detail, FacesMessage.Severity severity) {
        try {
            if (component == null)
                component = "";

            FacesContext facesContext = FacesContext.getCurrentInstance();

            if (facesContext == null) {
                log.warn(String.format("facesContext is null - Couldn't deliver message '%s' to component '%s'",
                        summary + " : " + detail,
                        component));
                return;
            }

            FacesMessage message = new FacesMessage(severity, summary, detail);
            facesContext.addMessage(component, message);

            String logMsg = "MESSAGE:" + severity.toString()
                    + (component.length() > 0 ? ":" + component : ":GLOBAL")
                    + " [" + summary + "] " + detail;

            if (severity == FacesMessage.SEVERITY_ERROR)
                log.error(logMsg);
            else if (severity == FacesMessage.SEVERITY_WARN)
                log.warn(logMsg);
            else
                log.debug(logMsg);
        } catch (Exception ex) {
            // This method gets called from so many places, that it's worth catching any possible exception
            // Generally speaking, missing a message to a user is not a problem as the messages are usually non critical
            log.error("Error sending faces message to component " + component, ex);
        }
    }
}
