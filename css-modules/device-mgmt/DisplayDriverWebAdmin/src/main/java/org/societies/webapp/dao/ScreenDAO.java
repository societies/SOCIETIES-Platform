package org.societies.webapp.dao;

/**
 * Created with IntelliJ IDEA.
 * User: sww2
 * Date: 15/07/13
 * Time: 14:24
 * To change this template use File | Settings | File Templates.
 */

import java.util.List;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.societies.webapp.model.Screens;

public class ScreenDAO implements IScreenDAO{

    private final Logger log = LoggerFactory.getLogger(ScreenDAO.class);


    @Override
    public void save(Screens screen) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            session.save(screen);
            transaction.commit();
        }catch (HibernateException e) {
            transaction.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    @Override
    public void update(Screens screen) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void deleteScreens(Screens screen) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
                session.delete(screen);
                transaction.commit();

        } catch (HibernateException e){
            transaction.rollback();
            e.printStackTrace();
            log.debug(e.toString());
        } finally{
            session.close();
        }
    }

    @Override
    public List getAllScreens() {

        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        List screens = null;
        try {
            transaction = session.beginTransaction();
            screens = session.createQuery(" from  " + Screens.class.getName()).list();
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
}
