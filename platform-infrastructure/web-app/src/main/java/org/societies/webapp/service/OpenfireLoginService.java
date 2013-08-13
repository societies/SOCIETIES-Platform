package org.societies.webapp.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Maria Mannion
 */
@Service
@ApplicationScoped // JSF
@ManagedBean // JSF
public class OpenfireLoginService implements Serializable {

    private enum MethodType {

        ADD,
        DELETE,
        ENABLE,
        DISABLE,
        UPDATE,
        LOGIN
    }

    private static final String OPENFIRE_PLUGIN = "http://%s:9090/plugins/societies/societies";
    private static Logger log = LoggerFactory.getLogger(OpenfireLoginService.class);

    @Autowired
    @ManagedProperty(value = "#{commMngrRef}")
    private ICommManager commManager;

    private String xmppDomain;

    public OpenfireLoginService() {
        log.info("OpenfireLoginService constructor");

    }

    public ICommManager getCommManager() {
        return commManager;
    }

    @SuppressWarnings("UnusedDeclaration")
    public void setCommManager(ICommManager commManager) {
        log.debug("setCommManager() has been called with " + commManager);
        this.commManager = commManager;
    }

    public String doLogin(String username, String password) {

        if (xmppDomain == null) {
            log.debug("xmppDomain is null - populating");

            if (getCommManager() == null) {
                log.error("getCommManager() is null");
                return "false";
            }
            if (getCommManager().getIdManager() == null) {
                log.error("getCommManager().getIdManager() is null");
                return "false";
            }
            if (getCommManager().getIdManager().getDomainAuthorityNode() == null) {
                log.error("getCommManager().getIdManager().getDomainAuthorityNode() is null");
                return "false";
            }
            if (getCommManager().getIdManager().getDomainAuthorityNode().getDomain() == null) {
                log.error("getCommManager().getIdManager().getDomainAuthorityNode().getDomain() is null");
                return "false";
            }
            xmppDomain = getCommManager().getIdManager().getDomainAuthorityNode().getDomain();
        }

        log.info("Loging on to upfire account with details : Username :" + username + "passsword" + password);

        ////////////////LOGIN ////////////////
        // 	AUTHENTICATE JABBER ID
        Map<String, String> params = new LinkedHashMap<String, String>();
        params.put("username", username);
        params.put("password", password);
        params.put("secret", "defaultSecret");

        String xmppUrl = String.format(OPENFIRE_PLUGIN, xmppDomain);

        String resp = postData(MethodType.LOGIN, xmppUrl, params);
        try {
            if (resp.isEmpty()) {
                //model.put("loginError", "Error logging onto openfire Account. Please try again");
                //return new ModelAndView("pilot", model);
                log.info("Error logging onto openfire Account : Empty Response: Url was " + xmppUrl);
                return null;
            }
            //CHECK RESPONSE - DOES ACCOUNT ALREADY EXIST
            Document respDoc = loadXMLFromString(resp);
            if (respDoc.getDocumentElement().getNodeName().equals("error")) {
                //	model.put("loginError", "Username/password incorrect. Please try again");
                //	return new ModelAndView("pilot", model);
                log.info("Username/password incorrect");
                return null;
            }
        } catch (Exception e) {
            log.info("Error logging onto openfire Account", e);
            return null;
        }


        log.info("Logged on succesffuly");


        return "logged";
    }

    private static String postData(MethodType method, String openfireUrl, Map<String, String> params) {
        try {
            StringBuilder data = new StringBuilder();
            for (String s : params.keySet()) {
                String tmp = URLEncoder.encode(s, "UTF-8") + "=" + URLEncoder.encode(params.get(s), "UTF-8") + "&";
                data.append(tmp);
            }
            //ADD METHOD
            String methodStr = URLEncoder.encode("type", "UTF-8") + "=" + URLEncoder.encode(method.toString().toLowerCase(), "UTF-8");
            data.append(methodStr);

            // Send data
            URL url = new URL(openfireUrl);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(data.toString());
            wr.flush();

            // Get the response
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }
            wr.close();
            rd.close();

            //RESPONSE CODE
            return sb.toString();

        } catch (Exception e) {

            e.printStackTrace();

        }
        return "";
    }

    private static Document loadXMLFromString(String xml) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputSource is = new InputSource(new StringReader(xml));
        return builder.parse(is);
    }


}
