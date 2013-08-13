package org.societies.android.platform.pubsub;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import org.jabber.protocol.pubsub.*;
import org.jabber.protocol.pubsub.owner.Delete;
import org.jabber.protocol.pubsub.owner.Purge;
import org.jivesoftware.smack.packet.IQ;
import org.societies.android.api.comms.IMethodCallback;
import org.societies.android.api.comms.xmpp.*;
import org.societies.android.api.pubsub.IPubsubService;
import org.societies.android.platform.androidutils.MarshallUtils;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.InvalidFormatException;
import org.societies.utilities.DBC.Dbc;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.*;

public class PubsubServiceBase implements IPubsubService {
    private static final String LOG_TAG = PubsubServiceBase.class.getCanonicalName();
    private static final boolean DEBUG_LOGGING = false;

    private final static List<String> NAMESPACES = Collections
            .unmodifiableList(Arrays.asList("http://jabber.org/protocol/pubsub",
                    "http://jabber.org/protocol/pubsub#errors",
                    "http://jabber.org/protocol/pubsub#event",
                    "http://jabber.org/protocol/pubsub#owner",
                    "http://jabber.org/protocol/disco#items"));
    private static final List<String> PACKAGES = Collections
            .unmodifiableList(Arrays.asList("org.jabber.protocol.pubsub",
                    "org.jabber.protocol.pubsub.errors",
                    "org.jabber.protocol.pubsub.event",
                    "org.jabber.protocol.pubsub.owner"));

    private static final List<String> ELEMENTS = Collections.unmodifiableList(
            Arrays.asList("pubsub",
                    "event",
                    "query"));

    private PubsubCommsMgr pubsubClientMgr;
    private Context androidContext;
    private boolean restrictBroadcast;
    private final Map<String, Integer> subscribedNodes;

    public PubsubServiceBase(Context androidContext, PubsubCommsMgr pubsubClientMgr, boolean restrictBroadcast) {
        this.pubsubClientMgr = pubsubClientMgr;
        this.androidContext = androidContext;
        this.restrictBroadcast = restrictBroadcast;
        this.subscribedNodes = Collections.synchronizedMap(new HashMap<String, Integer>());

        if (DEBUG_LOGGING) {
            Log.d(LOG_TAG, "Broadcast restricted : " + this.restrictBroadcast);
            Log.d(LOG_TAG, "PubsubServiceBase object constructed");
        }
    }

    @Override
    public boolean bindToAndroidComms(final String client, final long remoteCallID) {

        //Send intent
        final Intent intent = new Intent();
        if (PubsubServiceBase.this.restrictBroadcast) {
            intent.setPackage(client);
        }
        intent.putExtra(INTENT_RETURN_CALL_ID_KEY, remoteCallID);
        intent.setAction(IPubsubService.BIND_TO_ANDROID_COMMS);
        intent.putExtra(IPubsubService.INTENT_RETURN_VALUE_KEY, false);

        this.pubsubClientMgr.bindCommsService(new IMethodCallback() {

            @Override
            public void returnAction(String result) {
            }

            @Override
            public void returnAction(boolean resultFlag) {
                if (resultFlag) {
                    PubsubServiceBase.this.pubsubClientMgr.register(ELEMENTS, new ICommCallback() {

                        @Override
                        public void receiveResult(Stanza stanza, Object payload) {
                            boolean result = (Boolean) payload;
                            intent.putExtra(IPubsubService.INTENT_RETURN_VALUE_KEY, result);
                            PubsubServiceBase.this.androidContext.sendBroadcast(intent);
                        }

                        @Override
                        public void receiveMessage(Stanza stanza, Object payload) {
                        }

                        @Override
                        public void receiveItems(Stanza stanza, String node, List<String> items) {
                        }

                        @Override
                        public void receiveInfo(Stanza stanza, String node, XMPPInfo info) {
                        }

                        @Override
                        public void receiveError(Stanza stanza, XMPPError error) {
                        }

                        @Override
                        public List<String> getXMLNamespaces() {
                            return NAMESPACES;
                        }

                        @Override
                        public List<String> getJavaPackages() {
                            return PACKAGES;
                        }
                    });
                } else {
                    PubsubServiceBase.this.androidContext.sendBroadcast(intent);
                }
            }

            @Override
            public void returnException(String result) {
                // TODO Auto-generated method stub

            }
        });
        return false;
    }

    @Override
    public boolean unBindFromAndroidComms(final String client, final long remoteCallID) {
        //Send intent
        final Intent intent = new Intent();
        if (PubsubServiceBase.this.restrictBroadcast) {
            intent.setPackage(client);
        }
        intent.putExtra(INTENT_RETURN_CALL_ID_KEY, remoteCallID);
        intent.setAction(IPubsubService.UNBIND_FROM_ANDROID_COMMS);
        intent.putExtra(IPubsubService.INTENT_RETURN_VALUE_KEY, false);

        this.pubsubClientMgr.unregister(ELEMENTS, new ICommCallback() {

            @Override
            public void receiveResult(Stanza stanza, Object payload) {
                boolean result = (Boolean) payload;
                if (DEBUG_LOGGING) {
                    Log.d(LOG_TAG, "Unregister elements successful: " + result);
                }
                if (result && PubsubServiceBase.this.pubsubClientMgr.unbindCommsService()) {
                    intent.putExtra(IPubsubService.INTENT_RETURN_VALUE_KEY, true);
                }
                PubsubServiceBase.this.androidContext.sendBroadcast(intent);
            }

            @Override
            public void receiveMessage(Stanza stanza, Object payload) {
            }

            @Override
            public void receiveItems(Stanza stanza, String node, List<String> items) {
            }

            @Override
            public void receiveInfo(Stanza stanza, String node, XMPPInfo info) {
            }

            @Override
            public void receiveError(Stanza stanza, XMPPError error) {
            }

            @Override
            public List<String> getXMLNamespaces() {
                return NAMESPACES;
            }

            @Override
            public List<String> getJavaPackages() {
                return PACKAGES;
            }
        });

        return false;
    }


    @Override
    public String[] discoItems(final String client, String pubsubService, String node, final long remoteCallID) {
        Dbc.require("Client must be supplied", null != client && client.length() > 0);
        Dbc.require("Pubsub node must be supplied", null != node && node.length() > 0);
        Dbc.require("Pubsub service must be specified", null != pubsubService && pubsubService.length() > 0);

        if (DEBUG_LOGGING) {
            Log.d(LOG_TAG, "discoItems called with domain authority: " + pubsubService + " and node: " + node);
        }

        try {
            this.pubsubClientMgr.getItems(convertStringToIdentity(pubsubService), node, new ICommCallback() {

                @Override
                public void receiveResult(Stanza arg0, Object arg1) {
                }

                @Override
                public void receiveMessage(Stanza arg0, Object result) {
                }

                @Override
                public void receiveItems(Stanza stanza, String mapKey, List<String> mapValue) {

                    String returnValue[] = new String[mapValue.size()];
                    for (int i = 0; i < mapValue.size(); i++) {
                        returnValue[i] = mapValue.get(i);
                    }
                    //Send intent
                    Intent intent = new Intent();
                    if (PubsubServiceBase.this.restrictBroadcast) {
                        intent.setPackage(client);
                    }
                    intent.putExtra(INTENT_RETURN_CALL_ID_KEY, remoteCallID);
                    intent.setAction(IPubsubService.DISCO_ITEMS);
                    intent.putExtra(IPubsubService.INTENT_RETURN_VALUE_KEY, returnValue);
                    PubsubServiceBase.this.androidContext.sendBroadcast(intent);
                }

                @Override
                public void receiveInfo(Stanza arg0, String arg1, XMPPInfo arg2) {
                }

                @Override
                public void receiveError(Stanza arg0, XMPPError arg1) {
                }

                @Override
                public List<String> getXMLNamespaces() {
                    return NAMESPACES;
                }

                @Override
                public List<String> getJavaPackages() {
                    return PACKAGES;
                }
            });
        } catch (CommunicationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (XMPPError e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public boolean ownerCreate(final String client, String pubsubService, String node, final long remoteCallID) {
        Dbc.require("Client must be supplied", null != client && client.length() > 0);
        Dbc.require("Pubsub node must be supplied", null != node && node.length() > 0);
        Dbc.require("Pubsub service must be specified", null != pubsubService && pubsubService.length() > 0);

        if (DEBUG_LOGGING) {
            Log.d(LOG_TAG, "ownerCreate called with domain authority: " + pubsubService + " and node: " + node);
        }

        Stanza stanza = null;
        try {
            stanza = new Stanza(convertStringToIdentity(pubsubService));
        } catch (XMPPError e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        Pubsub payload = new Pubsub();
        Create c = new Create();
        c.setNode(node);
        payload.setCreate(c);

        try {
            this.pubsubClientMgr.sendIQ(stanza, IQ.Type.SET, payload, new ICommCallback() {

                @Override
                public void receiveResult(Stanza stanza, Object payload) {

                    //Send intent
                    Intent intent = new Intent();
                    if (PubsubServiceBase.this.restrictBroadcast) {
                        intent.setPackage(client);
                    }
                    intent.putExtra(INTENT_RETURN_CALL_ID_KEY, remoteCallID);
                    intent.setAction(IPubsubService.OWNER_CREATE);
                    intent.putExtra(IPubsubService.INTENT_RETURN_VALUE_KEY, (String) payload);
                    PubsubServiceBase.this.androidContext.sendBroadcast(intent);
                }

                @Override
                public void receiveMessage(Stanza arg0, Object arg1) {
                }

                @Override
                public void receiveItems(Stanza arg0, String arg1, List<String> arg2) {
                }

                @Override
                public void receiveInfo(Stanza arg0, String arg1, XMPPInfo arg2) {
                }

                @Override
                public void receiveError(Stanza arg0, XMPPError arg1) {
                }

                @Override
                public List<String> getXMLNamespaces() {
                    return NAMESPACES;
                }

                @Override
                public List<String> getJavaPackages() {
                    return PACKAGES;
                }
            });
        } catch (CommunicationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return false;
    }


    @Override
    public boolean ownerDelete(final String client, String pubsubService, String node, final long remoteCallID) {
        Dbc.require("Client must be supplied", null != client && client.length() > 0);
        Dbc.require("Pubsub node must be supplied", null != node && node.length() > 0);
        Dbc.require("Pubsub service must be specified", null != pubsubService && pubsubService.length() > 0);

        if (DEBUG_LOGGING) {
            Log.d(LOG_TAG, "ownerDelete called with domain authority: " + pubsubService + " and node: " + node);
        }

        Stanza stanza = null;
        try {
            stanza = new Stanza(convertStringToIdentity(pubsubService));
        } catch (XMPPError e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        org.jabber.protocol.pubsub.owner.Pubsub payload = new org.jabber.protocol.pubsub.owner.Pubsub();
        Delete delete = new Delete();
        delete.setNode(node);
        payload.setDelete(delete);

        try {
            this.pubsubClientMgr.sendIQ(stanza, IQ.Type.SET, payload, new ICommCallback() {

                @Override
                public void receiveResult(Stanza stanza, Object object) {
                    //Send intent
                    Intent intent = new Intent();
                    if (PubsubServiceBase.this.restrictBroadcast) {
                        intent.setPackage(client);
                    }
                    intent.putExtra(INTENT_RETURN_CALL_ID_KEY, remoteCallID);
                    intent.setAction(IPubsubService.OWNER_DELETE);
                    intent.putExtra(IPubsubService.INTENT_RETURN_VALUE_KEY, (String) object);
                    PubsubServiceBase.this.androidContext.sendBroadcast(intent);
                }

                @Override
                public void receiveMessage(Stanza arg0, Object arg1) {
                }

                @Override
                public void receiveItems(Stanza arg0, String arg1, List<String> arg2) {
                }

                @Override
                public void receiveInfo(Stanza arg0, String arg1, XMPPInfo arg2) {
                }

                @Override
                public void receiveError(Stanza arg0, XMPPError arg1) {
                }

                @Override
                public List<String> getXMLNamespaces() {
                    return NAMESPACES;
                }

                @Override
                public List<String> getJavaPackages() {
                    return PACKAGES;
                }
            });
        } catch (CommunicationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }


    @Override
    public boolean ownerPurgeItems(final String client, String pubsubServiceJid, String node, final long remoteCallID) {
        Dbc.require("Client must be supplied", null != client && client.length() > 0);
        Dbc.require("Pubsub node must be supplied", null != node && node.length() > 0);
        Dbc.require("Pubsub service must be specified", null != pubsubServiceJid && pubsubServiceJid.length() > 0);

        if (DEBUG_LOGGING) {
            Log.d(LOG_TAG, "ownerPurgeItems called with domain authority: " + pubsubServiceJid + " and node: " + node);
        }

        IIdentity pubsubService = null;
        try {
            pubsubService = convertStringToIdentity(pubsubServiceJid);
        } catch (XMPPError e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        Stanza stanza = new Stanza(pubsubService);
        org.jabber.protocol.pubsub.owner.Pubsub payload = new org.jabber.protocol.pubsub.owner.Pubsub();
        Purge purge = new Purge();
        purge.setNode(node);
        payload.setPurge(purge);

        try {
            this.pubsubClientMgr.sendIQ(stanza, IQ.Type.SET, payload, new ICommCallback() {

                @Override
                public void receiveResult(Stanza stanza, Object object) {
                    //Send intent
                    Intent intent = new Intent();
                    if (PubsubServiceBase.this.restrictBroadcast) {
                        intent.setPackage(client);
                    }
                    intent.putExtra(INTENT_RETURN_CALL_ID_KEY, remoteCallID);
                    intent.setAction(IPubsubService.OWNER_PURGE_ITEMS);
                    intent.putExtra(IPubsubService.INTENT_RETURN_VALUE_KEY, (String) object);
                    PubsubServiceBase.this.androidContext.sendBroadcast(intent);
                }

                @Override
                public void receiveMessage(Stanza arg0, Object arg1) {
                }

                @Override
                public void receiveItems(Stanza arg0, String arg1, List<String> arg2) {
                }

                @Override
                public void receiveInfo(Stanza arg0, String arg1, XMPPInfo arg2) {
                }

                @Override
                public void receiveError(Stanza arg0, XMPPError arg1) {
                }

                @Override
                public List<String> getXMLNamespaces() {
                    return NAMESPACES;
                }

                @Override
                public List<String> getJavaPackages() {
                    return PACKAGES;
                }
            });
        } catch (CommunicationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }


    @Override
    public String publisherPublish(final String client, String pubsubService, String node, String itemId, String item, final long remoteCallID) {
        Dbc.require("Client must be supplied", null != client && client.length() > 0);
        Dbc.require("Pubsub node must be supplied", null != node && node.length() > 0);
        Dbc.require("Pubsub service must be specified", null != pubsubService && pubsubService.length() > 0);
        Dbc.require("Pubsub item ID", null != itemId && itemId.length() > 0);
        Dbc.require("Pubsub event payload", null != item && item.length() > 0);

        if (DEBUG_LOGGING) {
            Log.d(LOG_TAG, "publisherPublish called with domain authority: " + pubsubService + " and node: " + node);
        }

        //Send intent
        final Intent intent = new Intent();
        if (PubsubServiceBase.this.restrictBroadcast) {
            intent.setPackage(client);
        }
        intent.putExtra(INTENT_RETURN_CALL_ID_KEY, remoteCallID);
        intent.putExtra(IPubsubService.INTENT_RETURN_VALUE_KEY, "");
        intent.setAction(IPubsubService.PUBLISHER_PUBLISH);

        Stanza stanza;
        try {
            stanza = new Stanza(convertStringToIdentity(pubsubService));

            Pubsub payload = new Pubsub();
            Publish publish = new Publish();
            publish.setNode(node);
            Item i = new Item();

            if (itemId != null) {
                i.setId(itemId);
                i.setAny(MarshallUtils.stringToElement(item));

                publish.setItem(i);
                payload.setPublish(publish);

                this.pubsubClientMgr.sendIQ(stanza, IQ.Type.SET, payload, new ICommCallback() {

                    @Override
                    public void receiveResult(Stanza stanza, Object object) {
                        intent.putExtra(IPubsubService.INTENT_RETURN_VALUE_KEY, (String) object);
                        PubsubServiceBase.this.androidContext.sendBroadcast(intent);
                    }

                    @Override
                    public void receiveMessage(Stanza arg0, Object arg1) {
                    }

                    @Override
                    public void receiveItems(Stanza arg0, String arg1, List<String> arg2) {
                    }

                    @Override
                    public void receiveInfo(Stanza arg0, String arg1, XMPPInfo arg2) {
                    }

                    @Override
                    public void receiveError(Stanza arg0, XMPPError arg1) {
                    }

                    @Override
                    public List<String> getXMLNamespaces() {
                        return NAMESPACES;
                    }

                    @Override
                    public List<String> getJavaPackages() {
                        return PACKAGES;
                    }
                });
            } else {
                PubsubServiceBase.this.androidContext.sendBroadcast(intent);
            }

        } catch (XMPPError e1) {
            Log.e(LOG_TAG, "Unable to create stanza", e1);
            PubsubServiceBase.this.androidContext.sendBroadcast(intent);
        } catch (CommunicationException e) {
            Log.e(LOG_TAG, "CommunicationException when sending IQ", e);
            PubsubServiceBase.this.androidContext.sendBroadcast(intent);
        } catch (SAXException e) {
            Log.e(LOG_TAG, "SAXException when parsing string to XML Element", e);
            PubsubServiceBase.this.androidContext.sendBroadcast(intent);
        } catch (IOException e) {
            Log.e(LOG_TAG, "IOException when parsing string to XML Element", e);
            PubsubServiceBase.this.androidContext.sendBroadcast(intent);
        } catch (ParserConfigurationException e) {
            Log.e(LOG_TAG, "ParserConfigurationException when parsing string to XML Element", e);
            PubsubServiceBase.this.androidContext.sendBroadcast(intent);
        }
        return null;
    }

    @Override
    public boolean publisherDelete(final String client, String pubsubServiceJid, String node, String itemId, final long remoteCallID) {
        Dbc.require("Client must be supplied", null != client && client.length() > 0);
        Dbc.require("Pubsub node must be supplied", null != node && node.length() > 0);
        Dbc.require("Pubsub service must be specified", null != pubsubServiceJid && pubsubServiceJid.length() > 0);

        if (DEBUG_LOGGING) {
            Log.d(LOG_TAG, "publisherDelete called with domain authority: " + pubsubServiceJid + " and node: " + node);
        }

        IIdentity pubsubService = null;
        try {
            pubsubService = convertStringToIdentity(pubsubServiceJid);
        } catch (XMPPError e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        Stanza stanza = new Stanza(pubsubService);
        Pubsub payload = new Pubsub();

        Retract retract = new Retract();
        retract.setNode(node);
        Item i = new Item();
        i.setId(itemId);
        retract.getItem().add(i);
        payload.setRetract(retract);

        try {
            this.pubsubClientMgr.sendIQ(stanza, IQ.Type.SET, payload, new ICommCallback() {

                @Override
                public void receiveResult(Stanza stanza, Object object) {
                    //Send intent
                    Intent intent = new Intent();
                    if (PubsubServiceBase.this.restrictBroadcast) {
                        intent.setPackage(client);
                    }
                    intent.putExtra(INTENT_RETURN_CALL_ID_KEY, remoteCallID);
                    intent.setAction(IPubsubService.PUBLISHER_DELETE);
                    intent.putExtra(IPubsubService.INTENT_RETURN_VALUE_KEY, (String) object);
                    PubsubServiceBase.this.androidContext.sendBroadcast(intent);
                }

                @Override
                public void receiveMessage(Stanza arg0, Object arg1) {
                }

                @Override
                public void receiveItems(Stanza arg0, String arg1, List<String> arg2) {
                }

                @Override
                public void receiveInfo(Stanza arg0, String arg1, XMPPInfo arg2) {
                }

                @Override
                public void receiveError(Stanza arg0, XMPPError arg1) {
                }

                @Override
                public List<String> getXMLNamespaces() {
                    return NAMESPACES;
                }

                @Override
                public List<String> getJavaPackages() {
                    return PACKAGES;
                }
            });
        } catch (CommunicationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }


    @Override
    public boolean subscriberSubscribe(final String client, String pubsubService, final String node, final long remoteCallID) {
        Dbc.require("Client must be supplied", null != client && client.length() > 0);
        Dbc.require("Pubsub node must be supplied", null != node && node.length() > 0);
        Dbc.require("Pubsub service must be specified", null != pubsubService && pubsubService.length() > 0);

        if (DEBUG_LOGGING) {
            Log.d(LOG_TAG, "subscriberSubscribe called with domain authority: " + pubsubService + " and node: " + node);
        }

        //Send intent
        final Intent intent = new Intent();
        if (PubsubServiceBase.this.restrictBroadcast) {
            intent.setPackage(client);
        }
        intent.putExtra(INTENT_RETURN_CALL_ID_KEY, remoteCallID);
        intent.setAction(IPubsubService.SUBSCRIBER_SUBSCRIBE);
        intent.putExtra(IPubsubService.INTENT_RETURN_VALUE_KEY, "");

        try {
            final IIdentity pubsubServiceIdentity;
            pubsubServiceIdentity = convertStringToIdentity(pubsubService);

            synchronized (this.subscribedNodes) {
                if (!this.subscribedNodes.containsKey(node)) {

                    Stanza stanza = new Stanza(pubsubServiceIdentity);
                    Pubsub payload = new Pubsub();
                    Subscribe sub = new Subscribe();
                    sub.setJid(localIdentity().getJid());
                    sub.setNode(node);
                    payload.setSubscribe(sub);

                    this.pubsubClientMgr.sendIQ(stanza, IQ.Type.SET, payload, new ICommCallback() {

                        @Override
                        public void receiveResult(Stanza arg0, Object response) {
                            intent.putExtra(IPubsubService.INTENT_RETURN_VALUE_KEY, (String) response);
                            PubsubServiceBase.this.androidContext.sendBroadcast(intent);

                            //Add the node from the subscribed node store
                            PubsubServiceBase.this.subscribedNodes.put(node, 1);
                        }

                        @Override
                        public void receiveMessage(Stanza arg0, Object arg1) {
                        }

                        @Override
                        public void receiveItems(Stanza arg0, String arg1, List<String> arg2) {
                        }

                        @Override
                        public void receiveInfo(Stanza arg0, String arg1, XMPPInfo arg2) {
                        }

                        @Override
                        public void receiveError(Stanza arg0, XMPPError arg1) {
                        }

                        @Override
                        public List<String> getXMLNamespaces() {
                            return NAMESPACES;
                        }

                        @Override
                        public List<String> getJavaPackages() {
                            return PACKAGES;
                        }
                    });
                } else {
                    //Increment the number of subscribers to a particular node
                    Integer subscribedCount = PubsubServiceBase.this.subscribedNodes.get(node);
                    PubsubServiceBase.this.subscribedNodes.put(node, subscribedCount + 1);

                    intent.putExtra(IPubsubService.INTENT_RETURN_VALUE_KEY, "Subscribed count increased");
                    PubsubServiceBase.this.androidContext.sendBroadcast(intent);
                }
            }
        } catch (XMPPError e1) {
            Log.e(LOG_TAG, "XMPP error", e1);
            PubsubServiceBase.this.androidContext.sendBroadcast(intent);
        } catch (CommunicationException e) {
            Log.e(LOG_TAG, "CommunicationException error", e);
            PubsubServiceBase.this.androidContext.sendBroadcast(intent);
        }
        return false;
    }


    @Override
    public boolean subscriberUnsubscribe(final String client, String pubsubService, final String node, final long remoteCallID) {
        Dbc.require("Client must be supplied", null != client && client.length() > 0);
        Dbc.require("Pubsub node must be supplied", null != node && node.length() > 0);
        Dbc.require("Pubsub service must be specified", null != pubsubService && pubsubService.length() > 0);

        if (DEBUG_LOGGING) {
            Log.d(LOG_TAG, "subscriberUnsubscribe called with domain authority: " + pubsubService + " and node: " + node);
        }

        //Send intent
        final Intent intent = new Intent();
        if (PubsubServiceBase.this.restrictBroadcast) {
            intent.setPackage(client);
        }
        intent.putExtra(INTENT_RETURN_CALL_ID_KEY, remoteCallID);
        intent.setAction(IPubsubService.SUBSCRIBER_UNSUBSCRIBE);
        intent.putExtra(IPubsubService.INTENT_RETURN_VALUE_KEY, "");

        IIdentity pubsubServiceIdentity;
        try {
            pubsubServiceIdentity = convertStringToIdentity(pubsubService);

            synchronized (this.subscribedNodes) {
                if (this.subscribedNodes.containsKey(node) && 1 == this.subscribedNodes.get(node)) {
                    Stanza stanza = new Stanza(pubsubServiceIdentity);
                    Pubsub payload = new Pubsub();
                    Unsubscribe unsub = new Unsubscribe();
                    unsub.setJid(localIdentity().getJid());
                    unsub.setNode(node);
                    payload.setUnsubscribe(unsub);

                    this.pubsubClientMgr.sendIQ(stanza, IQ.Type.SET, payload, new ICommCallback() {

                        @Override
                        public void receiveResult(Stanza stanza, Object object) {
                            intent.putExtra(IPubsubService.INTENT_RETURN_VALUE_KEY, (String) object);
                            PubsubServiceBase.this.androidContext.sendBroadcast(intent);

                            //Remove the node from the subscribed node store
                            PubsubServiceBase.this.subscribedNodes.remove(node);
                        }

                        @Override
                        public void receiveMessage(Stanza arg0, Object arg1) {
                        }

                        @Override
                        public void receiveItems(Stanza arg0, String arg1, List<String> arg2) {
                        }

                        @Override
                        public void receiveInfo(Stanza arg0, String arg1, XMPPInfo arg2) {
                        }

                        @Override
                        public void receiveError(Stanza arg0, XMPPError arg1) {
                        }

                        @Override
                        public List<String> getXMLNamespaces() {
                            return NAMESPACES;
                        }

                        @Override
                        public List<String> getJavaPackages() {
                            return PACKAGES;
                        }
                    });
                } else {
                    //Decrement the number of subscribers to a particular node
                    Integer subscribedCount = PubsubServiceBase.this.subscribedNodes.get(node);
                    PubsubServiceBase.this.subscribedNodes.put(node, subscribedCount - 1);

                    intent.putExtra(IPubsubService.INTENT_RETURN_VALUE_KEY, "Subscribed count decreased");
                    PubsubServiceBase.this.androidContext.sendBroadcast(intent);
                }
            }
        } catch (XMPPError e1) {
            Log.e(LOG_TAG, "XMPP error", e1);
            PubsubServiceBase.this.androidContext.sendBroadcast(intent);
        } catch (CommunicationException e) {
            Log.e(LOG_TAG, "CommunicationException error", e);
            PubsubServiceBase.this.androidContext.sendBroadcast(intent);
        }

        return false;
    }

    private IIdentity convertStringToIdentity(String jid) throws XMPPError {
        IIdentity returnIdentity;
        try {
            returnIdentity = this.pubsubClientMgr.getIdManager().fromJid(jid);
        } catch (InvalidFormatException e) {
            throw new XMPPError(StanzaError.jid_malformed, "Invalid JID: " + jid);
        }
        return returnIdentity;
    }

    private IIdentity localIdentity() {
        IIdentity returnIdentity = null;

        try {
            returnIdentity = this.pubsubClientMgr.getIdManager().getThisNetworkNode();
        } catch (InvalidFormatException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return returnIdentity;
    }
}
