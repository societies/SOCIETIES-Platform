package org.societies.webapp.models;

import javax.validation.constraints.Size;

public class LoginForm {
   
	@Size(min = 1, max = 50)
    private String userName;
    @Size(min = 1, max = 20)
    private String password;

    public void setUserName(String userName) {
            this.userName = userName;
    }
    public String getUserName() {
            return userName;
    }
    public void setPassword(String password) {
            this.password = password;
    }
    public String getPassword() {
            return password;
    }
}
