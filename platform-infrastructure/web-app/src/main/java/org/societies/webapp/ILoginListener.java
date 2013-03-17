package org.societies.webapp;

import java.util.EventListener;

public interface ILoginListener extends EventListener {
    public void userLoggedIn();

    public void userLoggedOut();
}
