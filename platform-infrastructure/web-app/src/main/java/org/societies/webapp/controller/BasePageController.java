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
        FacesMessage message = new FacesMessage(severity, summary, detail);
        FacesContext.getCurrentInstance().addMessage(component, message);

        String logMsg = "MESSAGE:" + severity.toString()
                + (component != null ? ":" + component : ":GLOBAL")
                + " [" + summary + "] " + detail;

        if (severity == FacesMessage.SEVERITY_ERROR)
            log.error(logMsg);
        else if (severity == FacesMessage.SEVERITY_WARN)
            log.warn(logMsg);
        else
            log.trace(logMsg);

    }
}
