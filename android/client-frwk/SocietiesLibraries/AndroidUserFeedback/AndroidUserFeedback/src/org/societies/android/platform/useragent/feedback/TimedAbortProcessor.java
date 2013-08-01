package org.societies.android.platform.useragent.feedback;

import android.content.Context;
import android.util.Log;
import org.societies.android.api.comms.IMethodCallback;
import org.societies.android.api.events.IAndroidSocietiesEvents;
import org.societies.android.api.events.IPlatformEventsCallback;
import org.societies.android.api.events.PlatformEventsHelperNotConnectedException;
import org.societies.android.remote.helper.EventsHelper;
import org.societies.api.schema.useragent.feedback.ImpFeedbackResultBean;
import org.societies.api.schema.useragent.feedback.UserFeedbackBean;

import java.util.*;

public class TimedAbortProcessor implements Runnable {

    private static final TimedAbortProcessor instance;
    private static final Thread timedAbortProcessorThread;

    public static TimedAbortProcessor getInstance() {
        return instance;
    }

    static {
        instance = new TimedAbortProcessor();
        timedAbortProcessorThread = new Thread(instance);
        timedAbortProcessorThread.setName("TimedAbortProcessor");
        timedAbortProcessorThread.setDaemon(true);
    }


    private static final String LOG_TAG = TimedAbortProcessor.class.getSimpleName();

    private boolean abort = false;
    private final List<UserFeedbackBean> timedAbortsToWatch = new ArrayList<UserFeedbackBean>();
    private final Map<String, Date> expiryTime = new HashMap<String, Date>();
    private EventsHelper eventsHelper = null;
    private boolean isEventHelperConnected = false;
    private Context context;

    private TimedAbortProcessor() {

    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public void run() {
        while (!abort) {
            try {
                processTimedAborts();
            } catch (Exception ex) {
                Log.e(LOG_TAG, "Error on timed abort processing thread", ex);
            }

            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {
                Log.e(LOG_TAG, "Error sleeping on timed abort processing thread", ex);
            }
        }
    }

    public void stop() {
        abort = true;

        //FINISH
        if (eventsHelper != null)
            eventsHelper.tearDownService(new IMethodCallback() {
                @Override
                public void returnException(String result) {
                }

                @Override
                public void returnAction(String result) {
                }

                @Override
                public void returnAction(boolean resultFlag) {
                }
            });
    }

    private void processTimedAborts() {
        synchronized (timedAbortsToWatch) {
            for (int i = 0; i < timedAbortsToWatch.size(); i++) {
                UserFeedbackBean ufBean = timedAbortsToWatch.get(i);

//                    if (ufBean.isResponseSent()) {
//                        submitIgnoreEvent(ufBean.getRequestId());
//                        continue;
//                    }

                // check if this TA has expired
                if (!new Date().after(expiryTime.get(ufBean.getRequestId()))) continue;

                Log.d(LOG_TAG, "Timeout expired, aborting TA event with ID " + ufBean.getRequestId());

                // the TA has expired, send the response
                submitIgnoreEvent(ufBean.getRequestId());

                // remove from watch list
                removeTimedAbort(ufBean.getRequestId());
                i = 0; // this is really dirty, and will result in us potentially processing some items twice
                // but it's not a big performance issue, and will prevent us missing any on the off chance that
                // removeTimedAbort(...) removes more than 1 item
            }
        }
    }

    public void addTimedAbort(UserFeedbackBean userFeedbackBean) {
        Date arrivalTime = new Date();

        synchronized (timedAbortsToWatch) {
            timedAbortsToWatch.add(userFeedbackBean);
        }
        synchronized (expiryTime) {
            Date expiryDate = new Date(arrivalTime.getTime() + (long) userFeedbackBean.getTimeout());

            Log.d(LOG_TAG, "Watching TA event with ID " + userFeedbackBean.getRequestId() + ", expiring " + expiryDate);

            expiryTime.put(userFeedbackBean.getRequestId(), expiryDate);
        }
    }

    public void removeTimedAbort(String requestId) {
        synchronized (timedAbortsToWatch) {
            for (int i = 0; i < timedAbortsToWatch.size(); i++) {
                UserFeedbackBean bean = timedAbortsToWatch.get(i);

                if (!bean.getRequestId().equals(requestId)) continue;

                timedAbortsToWatch.remove(i);
                i--;
            }
        }
        synchronized (expiryTime) {
            expiryTime.remove(requestId);
        }
    }

    protected void submitIgnoreEvent(final String requestId) {
        if (isEventHelperConnected) {
            Log.d(LOG_TAG, "Connected to eventsManager - resultFlag true");
            publishIgnoreEvent(requestId);
        } else {
            eventsHelper = new EventsHelper(context);
            eventsHelper.setUpService(new IMethodCallback() {
                @Override
                public void returnAction(String result) {
                    Log.d(LOG_TAG, "eventMgr callback: ReturnAction(String) called");
                }

                @Override
                public void returnAction(boolean resultFlag) {
                    Log.d(LOG_TAG, "eventMgr callback: ReturnAction(boolean) called. Connected");
                    if (resultFlag) {
                        isEventHelperConnected = true;
                        Log.d(LOG_TAG, "Connected to eventsManager - resultFlag true");
                        publishIgnoreEvent(requestId);
                    }
                }

                @Override
                public void returnException(String result) {
                }
            });
        }
    }

    private void publishIgnoreEvent(String requestId) {
        try {
            ImpFeedbackResultBean bean = new ImpFeedbackResultBean();
            bean.setAccepted(true);
            bean.setRequestId(requestId);

            eventsHelper.publishEvent(IAndroidSocietiesEvents.UF_IMPLICIT_RESPONSE_INTENT, bean, new IPlatformEventsCallback() {
                @Override
                public void returnAction(int result) {
                }

                @Override
                public void returnAction(boolean resultFlag) {
                }

                @Override
                public void returnException(int exception) {
                }
            });


        } catch (PlatformEventsHelperNotConnectedException e) {
            Log.e(LOG_TAG, "Error sending response", e);
        }
    }
}
