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
</header>
<div class="hr grid_12 clearfix">&nbsp;</div>
<section class="grid_12">
<section>
<div class="breadcrumbs"><a href="">Home</a> / <a href="">Page</a></div>
</section>
<div class="websearchbar">
<div class="websearchtitle">
<h4 class="form_title">Friends</h4>
</div>
<div class="groupsearch">
<form action="" class="websearch-form frame nobtn rsmall">
<input type="text" name="search" class="websearch-input" placeholder="Search for Friends..." />
</form>
</div>
</div>
</section>
<!-- Left Column -->
<article id="left_col" class="grid_8">
<section class="itemlist">
<header>
</header>
<ol class="keyinfolist">
<li class="keyinfo bypostauthor">
<figure class="gravatar">
<a class="friend_profile.html"><img alt="" src="images/webprofile_pic_sample1.jpg" height="48" width="48" /></a>
<a class="keyinfo-reply-link" href="friend_profile.html">INFO</a>
</figure>
<div class="keyinfo_content">
<div class="clearfix">
<time datetime="2012-09-30T00:01Z" class="keyinfo-meta keyinfometadata">Location, Sep 30, 2012 at 0:01 am</time>
<br/>
<cite class="author_name"><a href="profile.html">Sara Weber</a></cite>
</div>
<div class="keyinfo_text">
<p>Latest details... Aliquam risus elit, luctus vel, interdum vitae, malesuada eget, elit. Nulla vitae ipsum. Donec ligula ante, bibendum sit amet, elementum quis, viverra eu, ante. Fusce tincidunt. Mauris pellentesque, arcu eget feugiat accumsan, ipsum mi molestie orci, ut pulvinar sapien lorem nec dui.</p>
</div>
</div>
</li>
<li class="keyinfo">
<figure class="gravatar">
<img alt="" src="images/webprofile_pic_sample2.jpg" height="48" width="48" />
<a class="keyinfo-reply-link" href="">INFO</a>
</figure>
<div class="keyinfo_content">
<div class="clearfix">
<time datetime="2012-09-30T00:01Z" class="keyinfo-meta keyinfometadata">Location, Sep 30, 2012 at 0:01 am</time>
<br/>
<cite class="author_name"><a href="">Joe Bloggs</a></cite>
</div>
<div class="keyinfo_text">
<p>Latest details... Lorem ipsum dolor sit amet, consectetur adipiscing elit. Pellentesque orci velit, malesuada et varius ac, egestas eget nunc. Donec non porttitor massa.</p>
</div>
</div>
</li>
<li class="keyinfo bypostauthor">
<figure class="gravatar">
<img alt="" src="images/webprofile_pic_sample3.jpg" height="48" width="48" />
<a class="keyinfo-reply-link" href="">INFO</a>
</figure>
<div class="keyinfo_content">
<div class="clearfix">
<time datetime="2012-09-30T00:01Z" class="keyinfo-meta keyinfometadata">Location, Sep 30, 2012 at 0:01 am</time>
<br/>
<cite class="author_name"><a href="">Jim Bloggs</a></cite>
</div>
<div class="keyinfo_text">
<p>Latest details... Donec leo. Aliquam risus elit, luctus vel, interdum vitae, malesuada eget, elit. Nulla vitae ipsum. Donec ligula ante, bibendum sit amet, elementum quis, viverra eu, ante. Fusce tincidunt. Mauris pellentesque, arcu eget feugiat accumsan, ipsum mi molestie orci, ut pulvinar sapien lorem nec dui.</p>
</div>
</div>
</li>
<li class="keyinfo bypostauthor">
<figure class="gravatar">
<img alt="" src="images/webprofile_pic_sample4.jpg" height="48" width="48" />
<a class="keyinfo-reply-link" href="">INFO</a>
</figure>
<div class="keyinfo_content">
<div class="clearfix">
<time datetime="2012-09-30T00:01Z" class="keyinfo-meta keyinfometadata">Location, Sep 30, 2012 at 0:01 am</time>
<br/>
<cite class="author_name"><a href="">Mary Bloggs</a></cite>
</div>
<div class="keyinfo_text">
<p>Latest details... Donec leo. Aliquam risus elit, luctus vel, interdum vitae, malesuada eget, elit. Nulla vitae ipsum. Donec ligula ante, bibendum sit amet, elementum quis, viverra eu, ante. Fusce tincidunt. Mauris pellentesque, arcu eget feugiat accumsan, ipsum mi molestie orci, ut pulvinar sapien lorem nec dui.</p>
</div>
</div>
</li>
</ol>
<div class="hr clearfix">&nbsp;</div>
</section>
</article>
<!-- Right Column / Sidebar -->
<aside id="sidebar_right" class="grid_4">
<div class="sidebar_top_BG"></div>
<div class="hr dotted clearfix">&nbsp;</div>
<section>
<header>
<h3>Get to know...</h3>
</header>
<ul class="sidebar">
<li><a href="">Tim Bloggs</a></li>
<li><a href="">John Doe</a></li>
<li><a href="">Jack Bloggs</a></li>
<li><a href="">Sara Doe</a></li>
<li><a href="">Ann Doe</a></li>
</ul>
</section>
<section>
<header>
<h3>Other Activity</h3>
</header>
<div class="hr dotted clearfix">&nbsp;</div>
<ul class="contact_data">
<li><figure class="gravatar">
<img alt="" src="images/webprofile_pic_sample4.jpg" height="48" width="48" />
</figure><strong>Mary Bloggs</strong> dolor sit amet, consectetur adipiscing elit. Ipsum dolor sit amet, elit.</li>
</ul>
<ul class="contact_data">	
<li>Other Link - <a href="#">Link 1</a></li>
<li>Other Link - <a href="#">Link 2</a></li>
<li>Other Link - <a href="#">Link 3</a></li>
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
</section><!-- footer -->
</footer>
</div>
</body>
</html>