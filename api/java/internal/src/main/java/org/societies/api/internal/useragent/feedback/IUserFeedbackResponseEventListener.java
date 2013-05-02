package org.societies.api.internal.useragent.feedback;

import java.util.EventListener;

public interface IUserFeedbackResponseEventListener<V> extends EventListener {

    void responseReceived(V result);
}
