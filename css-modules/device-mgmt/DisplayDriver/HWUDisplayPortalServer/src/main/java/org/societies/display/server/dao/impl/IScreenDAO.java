package org.societies.display.server.dao.impl;

/**
 * Created with IntelliJ IDEA.
 * User: sww2
 * Date: 15/07/13
 * Time: 14:21
 * To change this template use File | Settings | File Templates.
 */

import org.societies.display.server.model.Screen;

import java.util.List;

public interface IScreenDAO {

        List getAllScreens();
        void addScreen(Screen screen);


}
