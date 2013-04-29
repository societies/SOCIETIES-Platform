package org.societies.useragent.feedback;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.schema.useragent.feedback.FeedbackStage;
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


    @Override
    public UserFeedbackBean getByRequestId(String requestId) {
        Session session = sessionFactory.openSession();
        return getByRequestId(session, requestId);
    }

    private UserFeedbackBean getByRequestId(Session session, String requestId) {
        Query query = session.createQuery("FROM UserFeedbackBean uf WHERE uf.requestId = :id");
        query.setString("id", requestId);

        List results = query.list();

        if (results.size() == 0)
            return null;

        return (UserFeedbackBean) results.get(0);
    }


    @Override
    public void insert(UserFeedbackBean ufBean) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();

        session.persist(ufBean);

        transaction.commit();
    }

    @Override
    public void updateStage(String requestId, FeedbackStage newStage) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();

        UserFeedbackBean item = getByRequestId(requestId);
        item.setStage(newStage);
        session.update(item);

        transaction.commit();
    }
}