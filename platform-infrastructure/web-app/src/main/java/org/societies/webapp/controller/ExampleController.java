package org.societies.webapp.controller;

import org.societies.webapp.service.UserService;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;

@ManagedBean(name = "exampleController")
@RequestScoped
public class ExampleController extends BasePageController {

    @ManagedProperty(value = "#{userService}")
    private UserService userService; // NB: MUST include public getter/setter
    private String stringProperty;
    private boolean boolProperty;

    public ExampleController() {
        // controller constructor - called every time this page is requested!
    }

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public String getStringProperty() {
        return stringProperty;
    }

    public void setStringProperty(String stringProperty) {
        this.stringProperty = stringProperty;
    }

    public boolean isBoolProperty() {
        return boolProperty;
    }

    public void setBoolProperty(boolean boolProperty) {
        this.boolProperty = boolProperty;
    }

    public void showMeAMessage() {
        addGlobalMessage("You asked for it", "Now here's your message!", FacesMessage.SEVERITY_INFO);
    }
}
