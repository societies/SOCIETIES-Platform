package org.societies.useragent.feedback;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.internal.schema.useragent.feedback.UserFeedbackPrivacyNegotiationEvent;
import org.societies.useragent.api.feedback.IPrivacyPolicyNegotiationHistoryRepository;

import java.util.Date;
import java.util.List;

public class PrivacyPolicyNegotiationHistoryRepository implements IPrivacyPolicyNegotiationHistoryRepository {
    private static final Logger log = LoggerFactory.getLogger(PrivacyPolicyNegotiationHistoryRepository.class);

    private SessionFactory sessionFactory;

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public List<UserFeedbackPrivacyNegotiationEvent> listPrevious(int howMany) {
        Session session = sessionFactory.openSession();

        Query query = session.createQuery("FROM UserFeedbackPrivacyNegotiationEvent uf ORDER BY uf.requestDate");
        query.setMaxResults(howMany);

        return query.list();
    }

    @Override
    public List<UserFeedbackPrivacyNegotiationEvent> listSince(Date sinceWhen) {
        Session session = sessionFactory.openSession();

        Query query = session.createQuery("FROM UserFeedbackPrivacyNegotiationEvent uf WHERE uf.requestDate > :date ORDER BY uf.requestDate");

        return query.list();
    }

}
