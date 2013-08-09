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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SuppressWarnings("unchecked")
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

        try {
            Query query = session.createQuery("FROM UserFeedbackBean uf");
//        Query query = session.createQuery("FROM UserFeedbackBean uf ORDER BY uf.requestDate");
            query.setMaxResults(howMany);

            return query.list();
        } finally {
            session.close();
        }
    }

    @Override
    public List<UserFeedbackBean> listSince(Date sinceWhen) {
        Session session = sessionFactory.openSession();

        try {
            Query query = session.createQuery("FROM UserFeedbackBean uf");
//        Query query = session.createQuery("FROM UserFeedbackBean uf WHERE uf.requestDate > :date ORDER BY uf.requestDate");
//        query.setDate("date", sinceWhen);

            return query.list();
        } finally {
            session.close();
        }
    }

    @Override
    public List<UserFeedbackBean> listIncomplete() {
        Session session = sessionFactory.openSession();

        try {
            Query query = session.createQuery("FROM UserFeedbackBean uf WHERE uf.stage != :stage");
//        Query query = session.createQuery("FROM UserFeedbackBean uf WHERE uf.stage != :stage ORDER BY uf.requestDate");
            query.setParameter("stage", FeedbackStage.COMPLETED);

            return query.list();
        } finally {
            session.close();
        }
    }

    @Override
    public UserFeedbackBean getByRequestId(String requestId) {
        Session session = sessionFactory.openSession();

        try {
            Query query = session.createQuery("FROM UserFeedbackBean uf WHERE uf.requestId = :id");
            query.setString("id", requestId);

            List results = query.list();

            if (results.size() == 0) {
                log.warn("Found no UserFeedbackBean with requestId=" + requestId);
                return null;
            }

            return (UserFeedbackBean) results.get(0);
        } finally {
            session.close();
        }
    }

    @Override
    public void insert(UserFeedbackBean ufBean) {
        Session session = sessionFactory.openSession();

        try {
            Transaction transaction = session.beginTransaction();

            session.save(ufBean);

            transaction.commit();
            session.flush();
        } finally {
            session.close();
        }
    }

    @Override
    public void updateStage(String requestId, FeedbackStage newStage) {
        Session session = sessionFactory.openSession();

        try {
            Transaction transaction = session.beginTransaction();

            UserFeedbackBean item = getByRequestId(requestId);
            item.setStage(newStage);
            session.update(item);

            transaction.commit();
            session.flush();
        } finally {
            session.close();
        }
    }

    @Override
    public void completeExpFeedback(String requestId, List<String> values) {
        Session session = sessionFactory.openSession();

        try {
            Transaction transaction = session.beginTransaction();

            UserFeedbackBean item = getByRequestId(requestId);
            item.setStage(FeedbackStage.COMPLETED);
            item.setOptions(values);
            session.update(item);

            transaction.commit();
            session.flush();
        } finally {
            session.close();
        }
    }

    @Override
    public void completeImpFeedback(String requestId, boolean accepted) {
        Session session = sessionFactory.openSession();

        try {
            Transaction transaction = session.beginTransaction();

            UserFeedbackBean item = getByRequestId(requestId);
            item.setStage(FeedbackStage.COMPLETED);
            List<String> options = new ArrayList<String>();
            options.add(accepted ? "true" : "false");
            item.setOptions(options);
            session.update(item);

            transaction.commit();
            session.flush();
        } finally {
            session.close();
        }
    }

    @Override
    public int truncate() {
        Session session = getSessionFactory().openSession();
        Transaction transaction = null;
        int count = -1;
        try {
            transaction = session.beginTransaction();
            Query query = session.createQuery("delete from UserFeedbackBean");
            count = query.executeUpdate();
            transaction.commit();

            session.flush();
        } catch (RuntimeException ex) {
            if (transaction != null)
                transaction.rollback();

            log.error("Error clearing table", ex);
            throw ex;
        } finally {
            session.close();
        }

        return count;
    }

}
