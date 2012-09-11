<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<!DOCTYPE html>
<html lang="en">
<head>
<title>Societies</title>
<meta charset="utf-8">
<link rel="stylesheet" href="css/style.css">
<link rel="stylesheet" href="css/dialog.css">
<!-- Menu -->
<script src="js/webmenu_nav.js"></script>
<script>!window.jQuery && document.write('<script src="js/jquery-v1.8.1.js"><\/script>')</script>
<!--[if IE]>
<script src="http://html5shiv.googlecode.com/svn/trunk/html5.js"></script><![endif]--> 
<!--[if lt IE 7]>
<link rel="stylesheet" type="text/css" media="all" href="css/ie7.css" />
<script src="http://ie7-js.googlecode.com/svn/version/2.1(beta4)/IE7.js"></script><![endif]-->
<!--[if IE 7]>
<link rel="stylesheet" type="text/css" media="all" href="css/ie7.css" /><![endif]-->
<!-- Accordian req-->
<script src="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8/jquery-ui.min.js"></script>
<!-- Accordian Menu-->
<script>
  $(document).ready(function() {
    $("#accordion").accordion();
  });
  </script>			
</head>
<body>
<div id="wrapper" class="clearfix">
<div id="container" class="container_12 clearfix">
<!--Header -->
<header id="header" class="grid_12">
<div class="login-form">
</div>
<!--Logo -->
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
<div class="hr grid_12 clearfix">&nbsp;</div>
<!-- Left Column -->
<section id="left_col" class="grid_8">
<div class="breadcrumbs"><a href="">Home</a> / <a href="">Page</a></div>
<!-- Form -->
<section id="form_style_main">
<form action="" method="" id="">	
<h4 class="form_title">FORM SAMPLE</h4>
<div class="hr dotted clearfix">&nbsp;</div>
<ul>						
<li class="clearfix">
<label for="">Text</label>
<input type="text" name="" id="smalltext" />
<div class="clear"></div>
<p class="error">Please, insert ...</p>
</li> 
<li class="clearfix">
<label for="">Text</label>
<input type="text" name="" id="smalltext" />
<div class="clear"></div>
<p  class="error">Please, insert...</p>
</li> 
<li class="clearfix">
<label for="">Text</label>
<input type="text" name="" id="smalltext" />
<div class="clear"></div>
<p class="error">Please, insert ...</p>
</li> 
<li class="clearfix"> 
<label for="">Text</label>
<input type="text" name="" id="smalltext" />
<div class="clear"></div>
<p  class="error">Please, enter ...</p>
</li> 
<li class="clearfix"> 
<label for="">Text</label>
<input type="text" name="" id="smalltext" />
<div class="clear"></div>
<p class="error">Please, enter ...</p>
</li> 
<li class="clearfix"> 
<label for="">Lrge Text Area</label>
<textarea name="" id="largetextarea" rows="30" cols="30"></textarea>
<div class="clear"></div>
<p class="error">Please, enter ...</p>
</li>
<li class="clearfix"> 
<label for="">Choose an option</label>
<div class="select-wrapper">
<select name="select" required>
<option value="1">Value 1</option>
<option value="2">Value 2</option>
<option value="3">Value 3</option>
<option value="4">Value 4</option>
</select>
</div>
<div class="clear"></div>
<p id="option" class="error">Please, select an option</p>
</li> 
<li class="clearfix"> 
<label for="">Choose opt</label>
<div class="option-group radio">
<input type="radio" name="" id="radio1" />
<label for="radio1">Option 1</label>
<input type="radio" name="" id="radio2" />
<label for="radio2">Option 2</label>			
<input type="radio" name="" id="radio3" />
<label for="radio3">Option 3</label>
</div>
<div class="clear"></div>
<p id="option" class="error">Please, select an option</p>
</li> 
<li class="clearfix"> 
<label for="option">Choose check </label>
<div class="option-group check">
<input type="checkbox" name="" id="check1" />
<label for="check1">Check 1</label>			
<input type="checkbox" name="" id="check2" />
<label for="check2">Check 2</label>
<input type="checkbox" name="" id="check3" />
<label for="check3">Check 3</label>
</div>
<div class="clear"></div>
<p id="option" class="error">Please, select an option</p>
</li> 
<li class="clearfix"> 
<p class="success">Thank you! Success Message here.</p>
<p class="error">Sorry, an error has occured. Please try again later.</p>	
<div id="button">
<input type="submit" id="send_message" class="sendButton" value="Send" />
</div>				
</li> 
</ul> 
</form>
</section>
</section><!-- #left_col -->
<!--Right Column / Sidebar -->
<aside id="sidebar_right" class="grid_4">
<div class="sidebar_top_BG"></div>
<div class="hr dotted clearfix">&nbsp;</div>
<div id="accordion">
<div class="accordion-menu-header"><a href="#">First Item</a></div>
<div>
<ul class="sidebar">
<li>First content</li>
<li>First content</li>
<li>First content</li>
</div>
<div class="accordion-menu-header"><a href="#">Second Item</a></div>
<div>
<ul class="sidebar">
<li>Second content</li>
<li>Second content</li>
<li>Second content</li>
</div>
<div class="accordion-menu-header"><a href="#">Third Item</a></div>
<div>
<ul class="sidebar">
<li>Third content</li>
<li>Third content</li>
<li>Third content</li>
</div>
</div>             
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
</aside><!-- sidebar_right -->
<div class="hr grid_12 clearfix">&nbsp;</div>
</div><!-- #container -->
<!-- =Footer -->
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