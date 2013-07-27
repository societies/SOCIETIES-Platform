package org.societies.webapp.dao;

/**
 * Created with IntelliJ IDEA.
 * User: sww2
 * Date: 15/07/13
 * Time: 14:21
 * To change this template use File | Settings | File Templates.
 */

import org.societies.webapp.model.Screens;

import java.util.List;

public interface IScreenDAO {

        void save(Screens screen);
        void update(Screens screen);
        void deleteScreens(Screens screen);
        List getAllScreens();


}
