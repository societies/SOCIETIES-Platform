<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<title>Download - SOCIETIES</title>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css" type="text/css" title="SOCIETIES" />
</head>
<body>

<jsp:include page="common/header.jsp" />

<div id="content" class="download">
	<h2>Download the SOCIETIES Android Client</h2>
	<p>SOCIETIES is also available on your Android smartphone. Just download and install the two SOCIETIES Android app.</p>
	<p><em>You need to allow your Android phone to install application outside of Google Play. Parameters > Security, and then check "Allow Unknown Sources".</em></p>
	${errormsg}
	${debugmsg}
	<ul class="downloadBox">
		<li><img src="${societiesAndroidCommsAppQrCodePath}" width="250" height="250" alt="SOCIETIES Android Comms App QrCode" /><br /><a href="${societiesAndroidCommsAppPath}" class="greatButton">SOCIETIES Android Comms App</a></li>
		<li><img src="${societiesAndroidAppQrCodePath}" width="250" height="250" alt="SOCIETIES Android App QrCode" /><br /><a href="${societiesAndroidAppPath}" class="greatButton">SOCIETIES Android App</a></li>
	</ul>
	
	<div class="article">
		<h2>Login and Configuration Steps</h2>
		<p>
			As soon as the SOCIETIES administrator has notified you that your SOCIETIES account is ready, you can login to your SOCIETIES Android client!<br />
			But before any login, your Android App shall be associated to your SOCIETIES account properly. However, the SOCIETIES administrator may have provide you some configuration parameters.
			In order to create this association, launch your App and, using the Android menu button, select Preferences then CSS Configuration.
			<br />
			<strong>Menu > Preference > CSS Configuration</strong>
			<br />
			Now fills your account credentials, and eventually the parameters provided by the SOCIETIES administrator. <small>The XMPP Server IP address is only required if this SOCIETIES platform is not accessible through the Internet.</small>
		</p>
		<img src="${pageContext.request.contextPath}/images/societies-android-app_configuration.png" alt="Configuration step" />
		<p>
		You are now ready to login. To perform the login, open your App, check the box "update with app preferences", verify your credentials and then click on the LOGIN button.
		</p>
		<img src="${pageContext.request.contextPath}/images/societies-android-app_login.png" alt="Login step" />
		
		<p><strong>That's it, your logged in, and ready to use SOCIETIES! Enjoy!</strong></p>
	</div>	
</div>

<jsp:include page="common/footer.jsp" />
</body>
</html>

