<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
         pageEncoding="ISO-8859-1" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Societies</title>
    <!-- JAVASCRIPT INCLUDES -->
    <jsp:include page="js_includes.jsp"/>
    <!-- END JAVASCRIPT INCLUDES  -->
</head>
<body>
<div id="wrapper" class="clearfix">
    <div id="container" class="container_12 clearfix">
        <!-- HEADER -->
        <jsp:include page="header.jsp"/>
        <!-- END HEADER -->
        <!-- .................PLACE YOUR CONTENT HERE ................ -->
        <jsp:useBean id="form" scope="request" type="org.societies.webapp.models.ProfileSettingsForm"/>
        <%--@elvariable id="identity" type="org.societies.api.identity.IIdentity"--%>

        <div class="hr grid_12 clearfix">&nbsp;</div>
        <!-- Left Column -->
        <section id="left_col" class="grid_12">
            <div class="breadcrumbs"><a href="index.html">Home</a> / <a href="profilesettings.html">Profile Settings</a>
            </div>
            <!-- Form -->
            <section class="form_style_main">
                <form:form action="" method="" id="" commandName="profilesettingsform">
                    <h4 class="form_title">Profile Settings for <c:out value="${form.fullName}"/></h4>

                    <ul>
                        <li>Identifier:
                            <c:out value="${identity.identifier}"/></li>
                        <li>Domain: <c:out value="${identity.domain}"/></li>
                        <li>Type: <c:out value="${identity.type}"/></li>
                        <li>JID: <c:out value="${identity.jid}"/></li>
                        <li>Bare JID: <c:out value="${identity.bareJid}"/></li>

                    </ul>

                    <c:out value="${form.preferenceHtml}" escapeXml="false"/>


                    <div class="hr dotted clearfix">&nbsp;</div>
                </form:form>
            </section>
        </section>
        <div class="hr grid_12 clearfix">&nbsp;</div>
    </div>

    <!-- .................END PLACE YOUR CONTENT HERE ................ -->
    <!-- FOOTER -->
    <jsp:include page="footer.jsp"/>
    <!-- END FOOTER -->

</div>
</body>
</html>
