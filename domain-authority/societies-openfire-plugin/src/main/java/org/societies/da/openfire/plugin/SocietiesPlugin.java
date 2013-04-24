package org.societies.da.openfire.plugin;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.auth.AuthFactory;
import org.jivesoftware.openfire.auth.ConnectionException;
import org.jivesoftware.openfire.auth.InternalUnauthenticatedException;
import org.jivesoftware.openfire.auth.UnauthorizedException;
import org.jivesoftware.openfire.container.Plugin;
import org.jivesoftware.openfire.container.PluginManager;
import org.jivesoftware.openfire.group.Group;
import org.jivesoftware.openfire.group.GroupManager;
import org.jivesoftware.openfire.group.GroupNotFoundException;
import org.jivesoftware.openfire.lockout.LockOutManager;
import org.jivesoftware.openfire.user.User;
import org.jivesoftware.openfire.user.UserAlreadyExistsException;
import org.jivesoftware.openfire.user.UserManager;
import org.jivesoftware.openfire.user.UserNotFoundException;
import org.jivesoftware.openfire.vcard.VCardManager;
import org.jivesoftware.util.JiveGlobals;
import org.jivesoftware.util.StringUtils;
import org.jivesoftware.util.PropertyEventListener;
import org.jivesoftware.util.PropertyEventDispatcher;
import org.xmpp.packet.JID;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.Writer;
import java.util.*;

public class SocietiesPlugin implements Plugin, PropertyEventListener {
    private UserManager userManager;
    private VCardManager vcardManager;
    private XMPPServer server;

    private String secret;
    private Collection<String> cloudProviderUrls;

    public void initializePlugin(PluginManager manager, File pluginDirectory) {
        server = XMPPServer.getInstance();
        userManager = server.getUserManager();
        vcardManager = server.getVCardManager();

        secret = JiveGlobals.getProperty("plugin.societies.secret", "");
        secret = ""; // TODO remove this that is forcing the secret to be "defaultSecret"
        
        // If no secret key has been assigned to the user service yet, assign "defaultSecret" to it.
        if (secret.equals("")){
            secret = "defaultSecret";
            setSecret(secret);
        }

        // Get the list of IP addresses that can use this service. An empty list means that this filter is disabled.
        cloudProviderUrls = StringUtils.stringToCollection(JiveGlobals.getProperty("plugin.societies.cloudProviderUrls", ""));
        
        // Listen to system property events
        PropertyEventDispatcher.addListener(this);
    }

    public void destroyPlugin() {
        userManager = null;
        // Stop listening to system property events
        PropertyEventDispatcher.removeListener(this);
    }

    public void createUser(String username, String password, String name, String email, String groupNames)
            throws UserAlreadyExistsException
    {
        userManager.createUser(username, password, name, email);

        if (groupNames != null) {
            Collection<Group> groups = new ArrayList<Group>();
            StringTokenizer tkn = new StringTokenizer(groupNames, ",");
            while (tkn.hasMoreTokens()) {
                try {
                    groups.add(GroupManager.getInstance().getGroup(tkn.nextToken()));
                } catch (GroupNotFoundException e) {
                    // Ignore this group
                }
            }
            for (Group group : groups) {
                group.getMembers().add(server.createJID(username, null));
            }
        }
    }
    
    public void deleteUser(String username) throws UserNotFoundException{
        User user = getUser(username);
        userManager.deleteUser(user);
    }

    /**
     * Lock Out on a given username
     *
     * @param username the username of the local user to disable.
     * @throws UserNotFoundException if the requested user
     *         does not exist in the local server.
     */
    public void disableUser(String username) throws UserNotFoundException
    {
        User user = getUser(username);
        LockOutManager.getInstance().disableAccount(username, null, null);
    }

    /**
     * Remove the lockout on a given username
     *
     * @param username the username of the local user to enable.
     * @throws UserNotFoundException if the requested user
     *         does not exist in the local server.
     */
    public void enableUser(String username) throws UserNotFoundException
    {
        User user = getUser(username);
        LockOutManager.getInstance().enableAccount(username);
    }
    
    public void updateUser(String username, String password, String name, String email, String groupNames)
            throws UserNotFoundException
    {
        User user = getUser(username);
        if (password != null) user.setPassword(password);
        if (name != null) user.setName(name);
        if (email != null) user.setEmail(email);

        if (groupNames != null) {
            Collection<Group> newGroups = new ArrayList<Group>();
            StringTokenizer tkn = new StringTokenizer(groupNames, ",");
            while (tkn.hasMoreTokens()) {
                try {
                    newGroups.add(GroupManager.getInstance().getGroup(tkn.nextToken()));
                } catch (GroupNotFoundException e) {
                    // Ignore this group
                }
            }

            Collection<Group> existingGroups = GroupManager.getInstance().getGroups(user);
            // Get the list of groups to add to the user
            Collection<Group> groupsToAdd =  new ArrayList<Group>(newGroups);
            groupsToAdd.removeAll(existingGroups);
            // Get the list of groups to remove from the user
            Collection<Group> groupsToDelete =  new ArrayList<Group>(existingGroups);
            groupsToDelete.removeAll(newGroups);

            // Add the user to the new groups
            for (Group group : groupsToAdd) {
                group.getMembers().add(server.createJID(username, null));
            }
            // Remove the user from the old groups
            for (Group group : groupsToDelete) {
                group.getMembers().remove(server.createJID(username, null));
            }
        }
    }
    
    /**
     * Returns the the requested user or <tt>null</tt> if there are any
     * problems that don't throw an error.
     *
     * @param username the username of the local user to retrieve.
     * @return the requested user.
     * @throws UserNotFoundException if the requested user
     *         does not exist in the local server.
     */
    private User getUser(String username) throws UserNotFoundException {
        JID targetJID = server.createJID(username, null);
        // Check that the sender is not requesting information of a remote server entity
        if (targetJID.getNode() == null) {
            // Sender is requesting presence information of an anonymous user
            throw new UserNotFoundException("Username is null");
        }
        return userManager.getUser(targetJID.getNode());
    }
    
    /**
     * Returns the secret key that only valid requests should know.
     *
     * @return the secret key.
     */
    public String getSecret() {
        return secret;
    }

    /**
     * Sets the secret key that grants permission to use the societies plugin.
     *
     * @param secret the secret key.
     */
    public void setSecret(String secret) {
        JiveGlobals.setProperty("plugin.societies.secret", secret);
        this.secret = secret;
    }
    
    public Collection<String> getCloudProviderUrls() {
		return cloudProviderUrls;
	}

	public void setCloudProviderUrls(Collection<String> cloudProviderUrls) {
		JiveGlobals.setProperty("plugin.societies.cloudProviderUrls", StringUtils.collectionToString(cloudProviderUrls));
		this.cloudProviderUrls = cloudProviderUrls;
	}

	public void propertySet(String property, Map<String, Object> params) {
        if (property.equals("plugin.societies.secret")) {
            this.secret = (String)params.get("value");
        }
        else if (property.equals("plugin.societies.cloudProviderUrls")) {
            this.cloudProviderUrls = StringUtils.stringToCollection((String)params.get("value"));
        }
    }

    public void propertyDeleted(String property, Map<String, Object> params) {
        if (property.equals("plugin.societies.secret")) {
            this.secret = "";
        }
        else if (property.equals("plugin.societies.cloudProviderUrls")) {
            this.cloudProviderUrls = Collections.emptyList();
        }
    }

    public void xmlPropertySet(String property, Map<String, Object> params) {
        // Do nothing
    }

    public void xmlPropertyDeleted(String property, Map<String, Object> params) {
        // Do nothing
    }

	public boolean loginUser(String username, String password) throws UnauthorizedException, ConnectionException, InternalUnauthenticatedException {
		AuthFactory.getAuthProvider().authenticate(username, password);
		return true;
	}
	
	// VCard Methods
	public void getVcard(String username, Writer out) throws IOException {
		Element vcard = vcardManager.getVCard(username);
		if (vcard!=null) {
			XMLWriter xmlw = new XMLWriter(out);
			xmlw.write(vcard);
		}
	}
	
	public void setVcard(String username, String vcard) throws Exception {
		SAXReader reader = new SAXReader();
		Document vcardDocument = reader.read(new StringReader(vcard));
		vcardManager.setVCard(username, vcardDocument.getRootElement());
	}
}
