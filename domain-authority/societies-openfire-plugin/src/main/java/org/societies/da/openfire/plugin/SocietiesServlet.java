package org.societies.da.openfire.plugin;

import gnu.inet.encoding.Stringprep;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jivesoftware.admin.AuthCheckFilter;
import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.auth.ConnectionException;
import org.jivesoftware.openfire.auth.InternalUnauthenticatedException;
import org.jivesoftware.openfire.auth.UnauthorizedException;
import org.jivesoftware.openfire.user.UserAlreadyExistsException;
import org.jivesoftware.openfire.user.UserNotFoundException;
import org.jivesoftware.util.Log;
import org.xmpp.packet.JID;

public class SocietiesServlet extends HttpServlet {

    private SocietiesPlugin plugin;


    @Override
	public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);
        plugin = (SocietiesPlugin) XMPPServer.getInstance().getPluginManager().getPlugin("societies");
 
        // Exclude this servlet from requiring the user to login
        AuthCheckFilter.addExclude("societies/societies");
        AuthCheckFilter.addExclude("societies/public/*");
    }

    @Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
        // Printwriter for writing out responses to browser
        PrintWriter out = response.getWriter();

        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String type = request.getParameter("type");
        String secret = request.getParameter("secret");
        String groupNames = request.getParameter("groups");
        //No defaults, add, delete, update only
        //type = type == null ? "image" : type;
       
        // Check this request is authorized
        if ((plugin.getSecret() != null || !plugin.getSecret().equals("")) && (secret == null || !secret.equals(plugin.getSecret()))) {
            String query = request.getQueryString();
        	Log.warn("An unauthorised user service request was received: " + ((query != null) ? query : ""));
            replyError("RequestNotAuthorised: Provided secret '"+secret+"' did not match", request, response, out);
            return;
         }

        // Some checking is required on the username
        if (username == null){
            replyError("IllegalArgumentException", request, response, out);
            return;
        }


        // Check the request type and process accordingly
        try {
            username = username.trim().toLowerCase();
            username = JID.escapeNode(username);
            username = Stringprep.nodeprep(username);
            if ("add".equals(type)) {
                plugin.createUser(username, password, name, email, groupNames);
                replyMessage("User account created successfully", request, response, out);
                //imageProvider.sendInfo(request, response, presence);
            }
            else if ("delete".equals(type)) {
                plugin.deleteUser(username);
                replyMessage("ok", request, response,out);
                //xmlProvider.sendInfo(request, response, presence);
            }
            else if ("enable".equals(type)) {
                plugin.enableUser(username);
                replyMessage("ok", request, response,out);
            }
            else if ("disable".equals(type)) {
                plugin.disableUser(username);
                replyMessage("ok", request, response,out);
            }
            else if ("update".equals(type)) {
                plugin.updateUser(username, password,name,email, groupNames);
                replyMessage("ok", request, response,out);
                //xmlProvider.sendInfo(request, response, presence);
            }
            else if ("login".equals(type)) {
            	if (plugin.loginUser(username,password))
            		replyMessage("ok", request, response,out);
            }
            else {
                Log.warn("The societies servlet received an invalid request of type: " + type);
                // TODO Do something
            }
        }
        catch (UserAlreadyExistsException e) {
            replyError("UserAlreadyExistsException: "+e.getMessage(), request, response, out);
        }
        catch (UserNotFoundException e) {
            replyError("UserNotFoundException: "+e.getMessage(), request, response, out);
        }
        catch (IllegalArgumentException e) {
            replyError("IllegalArgumentException: "+e.getMessage(), request, response, out);
        }
        catch (UnauthorizedException e) {
            replyError("UnauthorizedException: "+e.getMessage(), request, response, out);
        }
        catch (ConnectionException e) {
            replyError("ConnectionException: "+e.getMessage(), request, response, out);
        }
        catch (InternalUnauthenticatedException e) {
            replyError("InternalUnauthenticatedException: "+e.getMessage(), request, response, out);
        }
        catch (Exception e) {
            replyError("Exception: "+e.toString(), request, response, out);
        }
    }

    private void replyMessage(String message, HttpServletRequest request, HttpServletResponse response, PrintWriter out) throws IOException{
    	String referer = request.getHeader("Referer");
    	Log.debug("referer: " + referer);
    	if (referer!=null && referer.endsWith("public/signup.html"))
    		response.sendRedirect("public/signup-result.jsp?success="+message);
    	else {
    		response.setContentType("text/xml");
    		response.setStatus(200);
    		out.println("<result>"+message+"</result>");
    		out.flush();
    	}
    }

    private void replyError(String error, HttpServletRequest request, HttpServletResponse response, PrintWriter out) throws IOException{
    	String referer = request.getHeader("Referer");
    	if (referer!=null && referer.endsWith("public/signup.html"))
    		response.sendRedirect("public/signup-result.jsp?error="+error);
    	else {
    		response.setContentType("text/xml");
    		response.setStatus(200);
    		out.println("<error>"+error+"</error>");
    		out.flush();
    	}
    }
    
    @Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    @Override
	public void destroy() {
        super.destroy();
        // Release the excluded URL
        AuthCheckFilter.removeExclude("societies/societies");
        AuthCheckFilter.removeExclude("societies/public/*");
    }
}
