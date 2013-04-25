package org.societies.useragent.feedback;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.schema.useragent.feedback.UserFeedbackBean;
import org.societies.useragent.api.feedback.IUserFeedbackHistoryRepository;

import java.util.Date;
import java.util.List;

public class UserFeedbackHistoryRepository implements IUserFeedbackHistoryRepository {
    private static final Logger log = LoggerFactory.getLogger(UserFeedbackHistoryRepository.class);

    private SessionFactory sessionFactory;

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public List<UserFeedbackBean> listPrevious(int howMany) {

        Session session = sessionFactory.openSession();

        Query query = session.createQuery("FROM UserFeedbackBean uf ORDER BY uf.requestDate");
        query.setMaxResults(howMany);

        return query.list();
    }

    @Override
    public List<UserFeedbackBean> listSince(Date sinceWhen) {
        Session session = sessionFactory.openSession();

        Query query = session.createQuery("FROM UserFeedbackBean uf WHERE uf.requestDate > :date ORDER BY uf.requestDate");

        return query.list();
    }
}
