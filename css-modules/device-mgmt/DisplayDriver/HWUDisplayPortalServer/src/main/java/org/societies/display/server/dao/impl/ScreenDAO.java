package org.societies.display.server.dao.impl;

/**
 * Created with IntelliJ IDEA.
 * User: sww2
 * Date: 15/07/13
 * Time: 14:24
 * To change this template use File | Settings | File Templates.
 */

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.display.server.model.Screen;

import java.util.List;

public class ScreenDAO implements IScreenDAO{

    private final Logger log = LoggerFactory.getLogger(ScreenDAO.class);

    @Override
    public List getAllScreens() {

        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        List screens = null;
        try {
            transaction = session.beginTransaction();
            screens = session.createQuery(" from  " + Screen.class.getName()).list();
             transaction.commit();
        } catch (HibernateException e){
            transaction.rollback();
            e.printStackTrace();
            log.debug(e.toString());
        } finally{
            session.close();
        }
        return screens;
    }

    @Override
    public void addScreen(Screen screen)
    {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            //City city = new City();
            //city.setName(cityName);
            session.save(screen);
            transaction.commit();
        }catch (HibernateException e) {
            transaction.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }
}
