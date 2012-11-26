<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<!DOCTYPE html>

<html lang="en">
<head>
  <title>Societies</title>
	<!-- JAVASCRIPT INCLUDES -->
	<jsp:include page="js_includes.jsp" />
	<!-- END JAVASCRIPT INCLUDES  -->
</head>
<body>
  <div id="wrapper" class="clearfix">
  <div id="container" class="container_12 clearfix">
  <!-- HEADER -->
  <jsp:include page="header.jsp" />
  <!-- END HEADER -->
<!-- .................PLACE YOUR CONTENT HERE ................ -->  

<div class="hr grid_12 clearfix">&nbsp;</div>
<section id="left_col" class="grid_12">
	<section>
		<div class="breadcrumbs"><a href="">Home</a> / <a href="">Page</a></div>
	</section>
	<div class="websearchbar">
		<div class="websearchtitle">
			<h4 class="profile_title">Sarah Weber</h4>
		</div>
		<div class="groupsearch">
		<form action="" class="websearch-form frame nobtn rsmall">
			<input type="text" name="search" class="websearch-input" placeholder="Search for Friends..." />
		</form>
		</div>
	</div>
</section>

<!-- Left Column -->
<section id="left_col" class="grid_8">
<section>
<figure class="gravatar">
<img alt="" src="images/webprofile_pic_sample1.jpg" height="48" width="48" />
<a class="furtherinfo-link" href="profilesettings.html">EDIT PROFILE</a>
</figure>
<div class="keyinfo_content">
<div class="clearfix">
<cite class="author_name">My Profile</cite>
</div>
<div class="keyinfo_text">
<p>Latest details... Aliquam risus elit, luctus vel, interdum vitae, malesuada eget, elit. Nulla vitae ipsum. Donec ligula ante, bibendum sit amet, elementum quis, viverra eu, ante. Fusce tincidunt. Mauris pellentesque, arcu eget feugiat accumsan, ipsum mi molestie orci, ut pulvinar sapien lorem nec dui. </p>
<p>Nulla vitae ipsum. Donec ligula ante, bibendum sit amet, elementum quis, viverra eu, ante. Fusce tincidunt. Mauris pellentesque, arcu eget feugiat accumsan, ipsum mi molestie orci, ut pulvinar sapien lorem nec dui.</p>
</div>
<p><strong>Apps Info:</strong></p>
<!-- Unordered -->
<ul>
<li>List item example</li>
<li>List item example
</li>
<li><a class="tooltip" href="#" title="">List item example</a></li>
</ul>
<article class="post">
&nbsp;
</article>
<p><strong>Additional Info:</strong></p>
<!-- Unordered -->
<ul>
	<li>List item example</li>
	<li>List item example</li>
	<li><a class="tooltip" href="#" title="">List item example</a></li>
</ul>
<article class="post">
&nbsp;
</article>
<p><em>Further Information:</em></p> 
<p><strong>Location:</strong> Dublin, Ireland - 53°20' 52" N 6°1' 3" W</p>
<p><strong>Interests:</strong> Sport, ICT, Research</p>
<p><strong>Job Title:</strong> ICT Researcher</p>
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
		<h3>Friends</h3>
	</header>
	<ul class="sidebar">
		<li><a href="">Friend Name 1</a></li>
		<li><a href="">Friend Name 2</a></li>
		<li><a href="">Friend Name 3</a></li>
		<li><a href="">Friend Name 4</a></li>
		<li><a href="">Friend Name 5</a></li>
	</ul>
</section>
<section>
	<header>
		<h3>Friend Activity</h3>
	</header>
	<div class="hr dotted clearfix">&nbsp;</div>
	<ul class="contact_data">
		
	</ul>
	<ul class="contact_data">	
		<li>Other Link - <a href="#">Link 1</a></li>
		<li>Other Link - <a href="#">Link 2</a></li>
	</ul> 
</section>
<div class="hr dotted clearfix">&nbsp;</div>
<div class="sidebar_bottom_BG"></div>
</aside>
<div class="hr grid_12 clearfix">&nbsp;</div>
</div>

<!-- .................END PLACE YOUR CONTENT HERE ................ -->
	<!-- FOOTER -->
	<jsp:include page="footer.jsp" />
	<!-- END FOOTER -->

</div> 	
</body>
</html>