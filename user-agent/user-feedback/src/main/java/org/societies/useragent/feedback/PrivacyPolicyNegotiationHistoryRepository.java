package org.societies.useragent.feedback;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.internal.schema.useragent.feedback.UserFeedbackPrivacyNegotiationEvent;
import org.societies.api.schema.useragent.feedback.FeedbackStage;
import org.societies.useragent.api.feedback.IPrivacyPolicyNegotiationHistoryRepository;

import java.util.Date;
import java.util.List;

public class PrivacyPolicyNegotiationHistoryRepository implements IPrivacyPolicyNegotiationHistoryRepository {
    private static final Logger log = LoggerFactory.getLogger(PrivacyPolicyNegotiationHistoryRepository.class);

    private SessionFactory sessionFactory;
    private Session session;

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void init() {
        session = sessionFactory.openSession();
    }

    public void destroy() {
        session.flush();
        session.close();
    }

    @Override
    public List<UserFeedbackPrivacyNegotiationEvent> listPrevious(int howMany) {
        Query query = session.createQuery("FROM UserFeedbackPrivacyNegotiationEvent uf ORDER BY uf.requestDate");
        query.setMaxResults(howMany);

        return query.list();
    }

    @Override
    public List<UserFeedbackPrivacyNegotiationEvent> listSince(Date sinceWhen) {
        Query query = session.createQuery("FROM UserFeedbackPrivacyNegotiationEvent uf WHERE uf.requestDate > :date ORDER BY uf.requestDate");
        query.setDate("date", sinceWhen);

        return query.list();
    }

    @Override
    public List<UserFeedbackPrivacyNegotiationEvent> listIncomplete() {
        Query query = session.createQuery("FROM UserFeedbackPrivacyNegotiationEvent uf WHERE uf.stage != :stage ORDER BY uf.requestDate");
        query.setParameter("stage", FeedbackStage.COMPLETED);

        return query.list();
    }

    @Override
    public UserFeedbackPrivacyNegotiationEvent getByRequestId(String requestId) {
        Query query = session.createQuery("FROM UserFeedbackPrivacyNegotiationEvent uf WHERE uf.requestId = :id");
        query.setString("id", requestId);

        List results = query.list();

        if (results.size() == 0) {
            log.warn("Found no UserFeedbackPrivacyNegotiationEvent with requestId=" + requestId);
            return null;
        }

        return (UserFeedbackPrivacyNegotiationEvent) results.get(0);
    }

    @Override
    public void insert(UserFeedbackPrivacyNegotiationEvent event) {
        Transaction transaction = session.beginTransaction();

        session.save(event);

        transaction.commit();
        session.flush();
    }

    @Override
    public void updateStage(String requestId, FeedbackStage newStage) {
        Transaction transaction = session.beginTransaction();

        UserFeedbackPrivacyNegotiationEvent item = getByRequestId(requestId);
        item.setStage(newStage);
        session.update(item);

        transaction.commit();
        session.flush();
    }

}
