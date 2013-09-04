package org.societies.useragent.feedback;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.internal.schema.useragent.feedback.UserFeedbackAccessControlEvent;
import org.societies.api.schema.useragent.feedback.FeedbackStage;
import org.societies.useragent.api.feedback.IAccessControlHistoryRepository;

import java.util.Date;
import java.util.List;

@SuppressWarnings("unchecked")
public class AccessControlHistoryRepository implements IAccessControlHistoryRepository {
    private static final Logger log = LoggerFactory.getLogger(AccessControlHistoryRepository.class);

    private SessionFactory sessionFactory;

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public List<UserFeedbackAccessControlEvent> listPrevious(int howMany) {
        Session session = sessionFactory.openSession();

        try {
            // TODO: Re-enable requestDate order/filter when requestDate field is available again
//        Query query = session.createQuery("FROM UserFeedbackAccessControlEvent uf ORDER BY uf.requestDate");
            Query query = session.createQuery("FROM UserFeedbackAccessControlEvent uf");
            query.setMaxResults(howMany);

            return query.list();
        } finally {
            session.close();
        }
    }

    @Override
    public List<UserFeedbackAccessControlEvent> listSince(Date sinceWhen) {
        Session session = sessionFactory.openSession();

        try {
            // TODO: Re-enable requestDate order/filter when requestDate field is available again
//        Query query = session.createQuery("FROM UserFeedbackAccessControlEvent uf WHERE uf.requestDate > :date ORDER BY uf.requestDate");
            Query query = session.createQuery("FROM UserFeedbackAccessControlEvent uf");
//        query.setDate("date", sinceWhen);

            return query.list();
        } finally {
            session.close();
        }
    }

    @Override
    public List<UserFeedbackAccessControlEvent> listIncomplete() {
        Session session = sessionFactory.openSession();

        try {
            // TODO: Re-enable requestDate order/filter when requestDate field is available again
//        Query query = session.createQuery("FROM UserFeedbackAccessControlEvent uf WHERE uf.stage != :stage ORDER BY uf.requestDate");
            Query query = session.createQuery("FROM UserFeedbackAccessControlEvent uf WHERE uf.stage != :stage");
            query.setParameter("stage", FeedbackStage.COMPLETED);

            return query.list();
        } finally {
            session.close();
        }
    }

    @Override
    public UserFeedbackAccessControlEvent getByRequestId(String requestId) {
        Session session = sessionFactory.openSession();

        try {
            Query query = session.createQuery("FROM UserFeedbackAccessControlEvent uf WHERE uf.requestId = :id");
            query.setString("id", requestId);

            List results = query.list();

            if (results.size() == 0) {
                log.warn("Found no UserFeedbackAccessControlEvent with requestId=" + requestId);
                return null;
            }

            return (UserFeedbackAccessControlEvent) results.get(0);
        } finally {
            session.close();
        }
    }

    @Override
    public void insert(UserFeedbackAccessControlEvent event) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();

        try {
            session.save(event);

            transaction.commit();
            session.flush();
        } catch (RuntimeException ex) {
            if (transaction != null)
                transaction.rollback();

            throw ex;
        } finally {
            session.close();
        }
    }

    @Override
    public void updateStage(String requestId, FeedbackStage newStage) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();

        UserFeedbackAccessControlEvent item = getByRequestId(requestId);
        item.setStage(newStage);

        try {
            session.update(item);

            transaction.commit();
            session.flush();
        } catch (RuntimeException ex) {
            if (transaction != null)
                transaction.rollback();

            throw ex;
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
            Query query = session.createQuery("delete from UserFeedbackAccessControlEvent");
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
