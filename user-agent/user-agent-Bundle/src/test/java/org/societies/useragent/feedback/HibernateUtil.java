package org.societies.useragent.feedback;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HibernateUtil {
    private static final Logger log = LoggerFactory.getLogger(HibernateUtil.class);

    private static final SessionFactory sessionFactory;

    static {
        try {
            sessionFactory = new AnnotationConfiguration()
                    .configure()
                    .addPackage("org.societies.api.internal.schema.useragent.feedback")
                    .addPackage("org.societies.api.schema.useragent.feedback")
                    .buildSessionFactory();

        } catch (Throwable ex) {
            log.error("Initial SessionFactory creation failed", ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
