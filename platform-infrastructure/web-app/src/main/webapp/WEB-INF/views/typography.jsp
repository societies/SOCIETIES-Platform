<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<!DOCTYPE html>
<html lang="en">
<head>
<title>Societies</title>
<meta charset="utf-8">
<link rel="stylesheet" href="css/style.css">
<link rel="stylesheet" href="css/dialog.css">
<script>!window.jQuery && document.write('<script src="js/jquery-v1.8.1.js"><\/script>')</script>
<!--[if IE]>
<script src="http://html5shiv.googlecode.com/svn/trunk/html5.js"></script><![endif]--> 
<!--[if lt IE 7]>
<link rel="stylesheet" type="text/css" media="all" href="css/ie7.css" />
<script src="http://ie7-js.googlecode.com/svn/version/2.1(beta4)/IE7.js"></script><![endif]-->
<!--[if IE 7]>
<link rel="stylesheet" type="text/css" media="all" href="css/ie7.css" /><![endif]-->
<!-- Menu -->
<script src="js/webmenu_nav.js"></script>
</head>
<body>
<div id="wrapper" class="clearfix">
<div id="container" class="container_12 clearfix">
<!-- Header -->
<header id="header" class="grid_12">
<div class="login-form">   
</div>
<!-- Logo -->
<h1 id="logo" class="logo-pos">
<a href="index.html"><img src="images/societies_logo.jpg" alt="Logo" /></a>
</h1>
<!--WebMenu -->
<nav id="webmenu_nav">
<ul id="navigation" class="grid_8">
<li><a href="myProfile.html"><br />My Account</a>
<ul class="sub-menu"> 
<li><a href="myProfile.html">My Profile</a></li>
<li><a href="profilesettings.html">Profile Settings</a></li>
<li><a href="settings.html">Security Settings</a></li>
<li><a href="privacysettings.html">Privacy Settings</a></li>
</ul>
</li>
<li><a href="your_installed_apps.html"><br />Apps</a>
</li>
<li><a href="your_societies_friends_list.html"><br />Friends</a>
<ul class="sub-menu"> 
<li><a href="your_societies_friends_list.html">Your Friends</a></li>
<li><a href="suggested_societies_friends_list.html">Suggested Friends</a></li>
</ul>
</li>
<li><a href="your_communities_list.html"><br />Communities</a>
<ul class="sub-menu"> 
<li><a href="your_communities_list.html">Your Communities</a></li>
<li><a href="manage_communities.html">Manage your Communities</a></li>
<li><a href="create_community.html">Create a Community</a></li>
<li><a href="your_suggested_communities_list.html">Suggested Communities</a></li>
</ul>
</li>
<li><a href="index.html"><br />Home</a></li>
</ul>
</nav><!-- #webmenu -->
<div class="clear"></div>
</header><!-- #header -->
<div class="hr grid_12 clearfix">&nbsp;</div>
<!-- Left Column -->
<section id="left_col" class="grid_8">
<div class="breadcrumbs"><a href="">Home</a> / <a href="">Page</a></div>
<section>	
<h4 class="form_title">Typography, Iconography</h4>
<div class="hr dotted clearfix">&nbsp;</div>	
<!-- Header's and Formats-->
<h1>H1 Heading</h1> 
<h2>H2 Heading</h2> 
<h3>H3 Heading</h3> 
<h4>H4 Heading</h4> 
<h5>H5 Heading</h5> 
<h6>H6 Heading</h6>
<p>This paragraph shows how all text encapsulated only within <strong>&lt;p&gt;&lt;/p&gt;</strong> tags will appear. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Mauris vel porta erat. Quisque sit amet risus at odio pellentesque sollicitudin. Proin suscipit molestie facilisis.</p> 
<p>More text examples are as follows:</p> 
<p><em>This is emphasised text</em></p> 
<p><strong>This is strong text</strong></p>
<p><del>This is deleted text</del></p> 
<p><a class="tooltip" href="#" title="">This is a link</a></p> 
<a class="textlink" href="#">This is a TEXT BUTTON</a>
<br/>
<br/>
<blockquote>This is if you wish to indent details or provide addtional information. Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Duis uploadsor. Nullam tortor. Nulla vel dui. Curabitur et metus.  Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Duis uploadsor. Nullam tortor. Nulla vel dui. Curabitur et metus.</blockquote>
<!-- Lists -->
<!-- Unordered -->
<ul>
<li>List item example</li>
<li>List item example
<ul>
<li>Sub list item example</li>
<li>Sub list item example</li>
</ul>
</li>
<li><a class="tooltip" href="#" title="">List item example</a></li>
</ul>
<!-- Ordered -->
<ol>
<li>List item example</li>
<li>List item example
<ol> 
<li>Sub list item example</li>
<li>Sub list item example</li>
</ol>
</li>
<li><a class="tooltip" href="#" title="">List item example</a></li>
</ol>
<div>
<table width="302" border="1" bordercolor="#FFFFFF">
<tr>
<td width="48"><img src="images/home_web_48x48.png" width="48" height="48" alt="Home"></td>
<td width="48"><img src="images/menu_list_web_48x48.png" width="48" height="48" alt="Menu"></td>
<td width="48"><img src="images/settings_web_48x48.png" width="48" height="48" alt="Settings"></td>
<td width="48"><img src="images/information_web_48x48.png" width="48" height="48" alt="Info"></td>
<td width="0"><img src="images/person_web_48x48.png" width="48" height="48" alt="Person"></td>
<td width="0"><img src="images/friends_web_48x48.png" width="48" height="48" alt="Friends"></td>
<td width="80"><img src="images/group_web_48x48.png" width="48" height="48" alt="Group"></td>
</tr>
<tr>
<td><img src="images/search_web_48x48.png" width="48" height="48" alt="Search"></td>
<td><img src="images/pin_web_48x48.png" width="48" height="48" alt="Pin"></td>
<td><img src="images/refresh_web_48x48.png" width="48" height="48" alt="Refresh"></td>
<td><img src="images/chat_web_48x48.png" width="48" height="48" alt="Chat"></td>
<td><img src="images/close_web_48x48.png" width="48" height="48" alt="Close"></td>
<td><img src="images/folder_web_48x48.png" width="48" height="48" alt="Folder"></td>
<td><img src="images/favfolder_web_48x48.png" width="48" height="48" alt="FavFolder"></td>
</tr>
<tr>
<td><img src="images/email_web_48x48.png" width="48" height="48" alt="Email"></td>
<td><img src="images/world_web_48x48.png" width="48" height="48" alt="World"></td>
<td><img src="images/star_web_48x48.png" width="48" height="48" alt="Star"></td>
<td><img src="images/badge_web_48x48.png" width="48" height="48" alt="Badge"></td>
<td><img src="images/desktop_web_48x48.png" width="48" height="48" alt="Desktop"></td>
<td><img src="images/mobile_web_48x48.png" width="48" height="48" alt="Mobile"></td>
<td><img src="images/tablet_web_48x48.png" width="48" height="48" alt="Tablet"></td>
</tr>
<tr>
<td><img src="images/arrowdown_web_48x48.png" width="48" height="48" alt="Arrow Down"></td>
<td><img src="images/foursquare_web_48x48.png" width="48" height="48" alt="Foursquare"></td>
<td><img src="images/linkedin_web_48x48.png" width="48" height="48" alt="Linked In"></td>
<td><img src="images/twitter_web_48x48.png" width="48" height="48" alt="Twitter"></td>
<td><img src="images/facebook_web_48x48.png" width="48" height="48" alt="Facebook"></td>
<td><img src="images/vimeo_web_48x48.png" width="48" height="48" alt="Vimeo"></td>
<td><img src="images/youtube_web_48x48.png" width="48" height="48" alt="YouTube"></td>
</tr>
</table>
</div>
<div class="hr dotted clearfix">&nbsp;</div>
</section>
</section>
<!-- Right Column / Sidebar -->
<aside id="sidebar_right" class="grid_4">
<div class="sidebar_top_BG"></div>		
<div class="hr dotted clearfix">&nbsp;</div>	
<section>
<header>
<h3>Right Nav</h3>
</header>
<ul class="sidebar">
<li><a href="">Nav Item 1</a></li>
<li><a href="">Nav Item 2</a></li>
<li><a href="">Nav Item 3</a></li>
<li><a href="">Nav Item 4</a></li>
<li><a href="">Nav Item 5</a></li>
</ul>
</section>
<section>
<header>
<h3>Other Heading</h3>
</header>
<div class="hr dotted clearfix">&nbsp;</div>
<ul class="contact_data">
<li>Lorem ipsum dolor sit amet, consectetur adipiscing elit.</li>
</ul>
<ul class="contact_data">	
<li>Links - <a href="#">Link 1</a></li>
<li>Links - <a href="#">Link 2</a></li>
</ul> 
</section>
<div class="hr dotted clearfix">&nbsp;</div>
<div class="sidebar_bottom_BG"></div>
</aside>
<div class="hr grid_12 clearfix">&nbsp;</div>
</div>
<!-- Footer -->
<footer class="container_12 clearfix">
<section class="footer">
<p class="footer-links">
<span><a href="termsofuse.html">Terms of Use</a> | <a href="disclaimer.html">Disclaimer</a> | <a href="privacy.html">Privacy</a> | <a href="help.html">Help</a> | <a href="about.html">About</a></span>
<a class="float right toplink" href="#">top</a>
</p>
</section>
</footer>
</div> 	
</body>
</html>