<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="xc"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">


<html lang="en">
	<!-- HEADER -->
	<jsp:include page="header.jsp" />
	<!-- END HEADER -->
	
<body>
<div id="wrapper" class="clearfix">
<div id="container" class="container_12 clearfix">
<!-- Header -->
<header id="header" class="grid_12">
	<!-- LOGIN POPUP -->
	<div class="login-form">
<a href="#login-box" class="login-window">LOGIN / REGISTER</a>
</div>
<div id="login-box" class="login-popup">
<a href="#" class="close"><img src="images/close_pop.png" class="btn_close" title="Close Window" alt="Close" /></a>
<form:form method="POST" class="signin" action="login.html" commandName="loginform">
<fieldset class="textbox">
<label class="username">
<span>Username</span>
</label>
<form:input id="username" path="username" value="${username}" type="text" placeholder="Username" width="100%" readonly="true"/>
<label class="password">
<span>Password</span>
</label>
<form:input id="password" path="password" type="password" placeholder="Password" width="100%"/>

<input class="submit button" type="submit">Sign in</input>
<p>Don't have a login?
<a class="" href="new_account.html">Sign Up</a>
</p>        
</fieldset>
</form:form>
</div>
	<!-- END LOGIN POPUP -->
	
	<!-- MENU -->
	<jsp:include page="menu.jsp" />
	<!-- END MENU -->
<div class="clear"></div>
</header>
<!--Main Content START -->
<section id="featured" class="clearfix grid_12">
<div>
<article> <img src="images/societies-v1.png" alt="Where Pervasive Meets Social" width="960" height="400" class="landing_img" />
</article>			
</div>
</section>
<div class="hr grid_12 clearfix boxhr">&nbsp;</div>
<section class="homebox_entries grid_12">
<!-- FIRST ROW -->
<header>
  <h4 class="box_title">DISCOVER, CONNECT &amp; ORGANISE WITH SOCIETIES </h4></header>
<div class="hr grid_12 clearfix">&nbsp;</div>
<article>
<a class="homeboxgrid box " href="your_societies_friends_list.html" title="Friends">
<img src="images/home_friends.png" width="270" height="150" alt="Friends"></a>
</article>
<article>
<a class="homeboxgrid box " href="your_installed_apps.html" title="Apps">
<img src="images/home_apps.png" width="270" height="150" alt="Apps"> </a>
</article>
<article>
<a class="homeboxgrid box " href="your_communities_list.html" title="Communities">
<img src="images/home_communities.png" width="270" height="150" alt="Communities"> </a>
</article>
</section>
<div class="hr grid_12 clearfix">&nbsp;</div>
</div>

<!--Main Content END -->
	<!-- FOOTER -->
	<jsp:include page="footer.jsp" />
	<!-- END FOOTER -->
</div>
</body>
</html>