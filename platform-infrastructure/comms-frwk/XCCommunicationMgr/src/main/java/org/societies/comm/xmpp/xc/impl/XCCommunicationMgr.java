package org.societies.comm.xmpp.xc.impl;

import org.jivesoftware.whack.ExternalComponentManager;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.datatypes.XMPPNode;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommCallback;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.comm.xmpp.interfaces.IFeatureServer;
import org.societies.api.identity.*;
import org.societies.api.internal.comm.ICommManagerController;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacyassessment.IPrivacyLogAppender;
import org.societies.identity.IdentityManagerImpl;
import org.springframework.osgi.service.ServiceUnavailableException;
import org.xmpp.component.AbstractComponent;
import org.xmpp.component.ComponentException;
import org.xmpp.packet.IQ;
import org.xmpp.packet.Message;
import org.xmpp.packet.Message.Type;
import org.xmpp.packet.Presence;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class XCCommunicationMgr extends AbstractComponent implements ICommManagerController, ICommManager {

    private final CommManagerHelper helper;

    private String host;
    private String subDomain;
    private String secretKey;
    private String daNode;
    private ExternalComponentManager manager;
    private IIdentity thisIdentity;
    private IIdentityManager idm;
    private Set<INetworkNode> otherNodes;
    private IPrivacyLogAppender privacyLog;
    private boolean privacyLogEnabled = false;

    public XCCommunicationMgr(String host, String subDomain,
                              String secretKey, String daNode) {
        this.helper = new CommManagerHelper();
        this.host = host;
        this.subDomain = subDomain;
        this.secretKey = secretKey;
        this.daNode = daNode;
        otherNodes = new HashSet<INetworkNode>();
    }

    @Override
    public INetworkNode login(String identifier, String domain, String password) {
        this.host = domain;
        this.subDomain = identifier + "." + domain;
        this.secretKey = password;
        initWhackCommManager();
        if (idm != null)
            return idm.getThisNetworkNode();
        else
            return null;
    }

    @Override
    public INetworkNode loginFromConfig() {
        initWhackCommManager();
        if (idm != null)
            return idm.getThisNetworkNode();
        else
            return null;
    }

    @Override
    public boolean logout() {
        return UnRegisterCommManager();
    }

    private void initWhackCommManager() {
        manager = new ExternalComponentManager(host);
        manager.setSecretKey(subDomain, secretKey);

        log.info("Connecting...");
        try {
            manager.addComponent(subDomain, this);
            idm = new IdentityManagerImpl(subDomain, daNode);
            thisIdentity = idm.getThisNetworkNode();

            if (thisIdentity.getType() == IdentityType.CSS || thisIdentity.getType() == IdentityType.CSS_RICH)
                probePresence();

            log.info("Connected " + thisIdentity.getType().toString() + " '" + subDomain + "' to domain '" + host + "'!");
        } catch (ComponentException e) {
            log.warn("Could not connect to '" + host + "' as '" + subDomain + "': " + e.getMessage());
            e.printStackTrace();
        } catch (InvalidFormatException e) {
            log.warn("Could not connect to '" + host + "' as '" + subDomain + "': " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void probePresence() {
        Presence subscribe = new Presence();
        String bareJid = thisIdentity.getIdentifier() + "@" + thisIdentity.getDomain();
        subscribe.setFrom(subDomain);
        subscribe.setTo(bareJid);
        subscribe.setType(org.xmpp.packet.Presence.Type.subscribe);
        log.info("Sending presence subscribe: " + subscribe.toXML());
        this.send(subscribe);

        Presence probe = new Presence();
        probe.setFrom(subDomain);
        probe.setTo(bareJid);
        probe.setType(org.xmpp.packet.Presence.Type.probe);
        log.info("Sending presence probe: " + probe.toXML());
        this.send(probe);
    }

    /**
     * Unregisters the XC Manager as an external component from the XMPP Server
     */
    @Override
    public boolean UnRegisterCommManager() {
        try {
            manager.removeComponent(subDomain);
            log.info("'" + subDomain + "' disconnected!");
        } catch (ComponentException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

	/*
     * Implementation of AbstractComponent methods
	 */

    @Override
    public String getDescription() {
        return "this external component acts as a dispatcher to "
                + "facilitate the communication among Societies components";
    }

    @Override
    public String getName() {
        return "Societies Communication Manager";
    }

    @Override
    protected String[] discoInfoFeatureNamespaces() {
        return helper.getSupportedNamespaces();
    }

    @Override
    protected IQ handleDiscoItems(IQ iq) {
        log.info("IQ disco#items received: " + iq.toXML());
        IQ response = helper.handleDiscoItems(iq);
        response.setFrom(thisIdentity.getJid());
        log.info("Returning disco#items response: " + response.toXML());
        return response;
    }

    @Override
    protected IQ handleIQGet(IQ iq) {
        if (log.isDebugEnabled())
            log.debug("IQ GET received: " + iq.toXML());
        else if (log.isDebugEnabled()) {
            String msg = "IQ GET received: id=%s node=%s";
            log.debug(String.format(msg,
                    iq.getID(),
                    iq.getChildElement() != null ? iq.getChildElement().getName() : "null"
            ));
        }

        IQ response = helper.dispatchIQ(iq);
        response.setFrom(thisIdentity.getJid());
        // TODO mitja

        if (log.isDebugEnabled())
            log.debug("Sending IQ response: " + response.toXML());
        return response;
    }

    @Override
    protected IQ handleIQSet(IQ iq) throws Exception {
        return handleIQGet(iq); // TODO
    }

    @Override
    protected void handleIQResult(IQ iq) {
        if (log.isDebugEnabled())
            log.debug("IQ Result received: " + iq.toXML());
        else if (log.isDebugEnabled()) {
            String msg = "IQ Result received: id=%s node=%s";
            log.debug(String.format(msg,
                    iq.getID(),
                    iq.getChildElement() != null ? iq.getChildElement().getName() : "null"
            ));
        }
        helper.dispatchIQResult(iq);
    }

    @Override
    protected void handleIQError(IQ iq) {
        if (log.isDebugEnabled())
            log.debug("IQ Error: " + iq.toXML());
        helper.dispatchIQError(iq);
    }

    @Override
    protected void handleMessage(Message message) {
        if (log.isDebugEnabled())
            log.debug("Message received: " + message.toXML());
        helper.dispatchMessage(message);
    }

    @Override
    protected void handlePresence(Presence presence) {
        if (log.isDebugEnabled())
            log.debug("Received presence: " + presence.toXML());

        try {
            IIdentity nodeIdentity = idm.fromJid(presence.getFrom().toString());
            if (nodeIdentity instanceof INetworkNode) {
                INetworkNode node = (INetworkNode) nodeIdentity;
                if (presence.getType() == null) {
                    otherNodes.add(node);
                } else if (presence.getType().equals(Presence.Type.unavailable)) {
                    otherNodes.remove(node);
                }
            }
        } catch (InvalidFormatException e) {
            log.warn("Error handling presence", e);
            e.printStackTrace();
        }

        log.info("OtherNodes: " + Arrays.toString(otherNodes.toArray()));
    }

    /*
     * Implementation of CommunicationManager methods
     */
    @Override
    public void register(IFeatureServer fs) throws CommunicationException {
        helper.register(fs);
    }

    @Override
    public void register(ICommCallback messageCallback) throws CommunicationException {
        helper.register(messageCallback);
    }

    private void checkConnectivity() throws CommunicationException {
        if (idm != null)
            return;

        initWhackCommManager();
        if (idm == null) {
            boolean hostReachable = false;
            try {
                for (InetAddress addr : InetAddress.getAllByName(host)) {
                    hostReachable = addr.isReachable(4000);
                }
            } catch (UnknownHostException e) {
                log.error("UnknownHostException while resolving XMPP Server host '" + host + "'", e);
            } catch (IOException e) {
                log.error("IOException while pinging XMPP Server host '" + host + "'", e);
            }
            if (hostReachable)
                throw new CommunicationException("CommunicationManager could not connect to XMPP server at '" + host + "' for subDomain '" + subDomain + "' with secretKey '" + secretKey + "'");
            else
                throw new CommunicationException("CommunicationManager could not find an XMPP server at '" + host + "'");
        }
    }

    @Override
    public void sendMessage(Stanza stanza, String type, Object payload)
            throws CommunicationException {
        checkConnectivity();
        stanza.setFrom(thisIdentity);
        Type mType = Message.Type.valueOf(type);
        Message m = helper.sendMessage(stanza, mType, payload);

        if (log.isDebugEnabled())
            log.debug("Sending message: " + m.toXML());

        privacyLog(stanza, payload);
        this.send(m);
    }

    @Override
    public void sendMessage(Stanza stanza, Object payload)
            throws CommunicationException {
        checkConnectivity();
        stanza.setFrom(thisIdentity);
        Message m = helper.sendMessage(stanza, null, payload);

        if (log.isDebugEnabled())
            log.debug("Sending message: " + m.toXML());

        privacyLog(stanza, payload);
        this.send(m);
    }

    @Override
    public void sendIQGet(Stanza stanza, Object payload, ICommCallback callback)
            throws CommunicationException {
        checkConnectivity();
        stanza.setFrom(thisIdentity);
        IQ iq = helper.sendIQ(stanza, IQ.Type.get, payload, callback);

        if (log.isDebugEnabled())
            log.debug("Sending IQ: " + iq.toXML());
        else if (log.isDebugEnabled()) {
            String msg = "Sending IQ: id=%s node=%s";
            log.debug(String.format(msg,
                    iq.getID(),
                    iq.getChildElement() != null ? iq.getChildElement().getName() : "null"
            ));
        }
        privacyLog(stanza, payload);
        this.send(iq);
    }

    @Override
    public void sendIQSet(Stanza stanza, Object payload, ICommCallback callback)
            throws CommunicationException {
        checkConnectivity();
        stanza.setFrom(thisIdentity);
        IQ iq = helper.sendIQ(stanza, IQ.Type.set, payload, callback);

        if (log.isDebugEnabled())
            log.debug("Sending IQ: " + iq.toXML());
        else if (log.isDebugEnabled()) {
            String msg = "Sending IQ: id=%s node=%s";
            log.debug(String.format(msg,
                    iq.getID(),
                    iq.getChildElement() != null ? iq.getChildElement().getName() : "null"
            ));
        }

        privacyLog(stanza, payload);
        this.send(iq);
    }

    @Override
    public void addRootNode(XMPPNode newNode) {
        helper.addRootNode(newNode);
    }

    @Override
    public void removeRootNode(XMPPNode node) {
        helper.removeRootNode(node);
    }

    @Override
    public String getInfo(IIdentity entity, String node, ICommCallback callback) throws CommunicationException {
        checkConnectivity();
        IQ iq = helper.buildInfoIq(entity, node, callback);
        iq.setFrom(thisIdentity.getJid());
        this.send(iq);
        return iq.getID();
    }

    @Override
    public String getItems(IIdentity entity, String node, ICommCallback callback) throws CommunicationException {
        checkConnectivity();
        IQ iq = helper.buildItemsIq(entity, node, callback);
        iq.setFrom(thisIdentity.getJid());
        this.send(iq);
        return iq.getID();
    }

    @Override
    public IIdentityManager getIdManager() {
        return idm;
    }

    // Controll methods
    @Override
    public INetworkNode newMainIdentity(String identifier, String domain,
                                        String password) throws XMPPError {
        // TODO Auto-generated method stub
        // TODO when identity is created it needs to add the component to the roster for presence
        return null;
    }

    @Override
    public boolean destroyMainIdentity() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Set<INetworkNode> getOtherNodes() {
        return otherNodes;
    }

    @Override
    public boolean isConnected() {
        if (idm != null && idm.getThisNetworkNode() != null)
            return true;
        else
            return false;
    }

    public IPrivacyLogAppender getPrivacyLog() {
        return privacyLog;
    }

    public void setPrivacyLog(IPrivacyLogAppender privacyLog) {
        if (privacyLog != null) {
            this.privacyLog = privacyLog;
            privacyLogEnabled = true;
        }
    }

    private void privacyLog(Stanza stanza, Object payload) {
        if (privacyLogEnabled) {
            try {
                privacyLog.logCommsFw(stanza.getFrom(), stanza.getTo(), payload);
            } catch (ServiceUnavailableException e) {
                log.warn("Privacy Logger unavailable. Resuming without Privacy Logger...", e);
                privacyLogEnabled = false;
            }
        }
    }
}
