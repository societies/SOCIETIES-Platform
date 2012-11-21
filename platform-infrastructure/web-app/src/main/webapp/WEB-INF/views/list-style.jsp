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
<!-- Left Column -->
<article id="left_col" class="grid_8">
<section>
<div class="breadcrumbs"><a href="">Home</a> / <a href="">Page</a></div>
</section>
<section class="itemlist">
<header>
<h3>List Style</h3>
</header>
<div class="hr dotted clearfix">&nbsp;</div>
<ol class="keyinfolist">
<li class="keyinfo bypostauthor">
<figure class="gravatar">
<img alt="" src="images/gravatar.png" height="48" width="48" />
<a class="keyinfo-reply-link" href="">INFO</a>
</figure>
<div class="keyinfo_content">
<div class="clearfix">
<time datetime="2012-09-30T00:01Z" class="keyinfo-meta keyinfometadata">Location, Sep 30, 2012 at 0:01 am</time><br/>
<cite class="author_name"><a href="">Sara Weber</a></cite>
</div>
<div class="keyinfo_text">
<p>Latest details... Aliquam risus elit, luctus vel, interdum vitae, malesuada eget, elit. Nulla vitae ipsum. Donec ligula ante, bibendum sit amet, elementum quis, viverra eu, ante. Fusce tincidunt. Mauris pellentesque, arcu eget feugiat accumsan, ipsum mi molestie orci, ut pulvinar sapien lorem nec dui.</p>
</div>
</div>
</li>
<li class="keyinfo">
<figure class="gravatar">
<img alt="" src="images/gravatar.png" height="48" width="48" />
<a class="keyinfo-reply-link" href="">INFO</a>
</figure>
<div class="keyinfo_content">
<div class="clearfix">
<time datetime="2012-09-30T00:01Z" class="keyinfo-meta keyinfometadata">Location, Sep 30, 2012 at 0:01 am</time><br/>
<cite class="author_name"><a href="">Joe Bloggs</a></cite>
</div>
<div class="keyinfo_text">
<p>Latest details... Lorem ipsum dolor sit amet, consectetur adipiscing elit. Pellentesque orci velit, malesuada et varius ac, egestas eget nunc. Donec non porttitor massa.</p>
</div>
</div>
</li>
<li class="keyinfo bypostauthor">
<figure class="gravatar">
<img alt="" src="images/gravatar.png" height="48" width="48" />
<a class="keyinfo-reply-link" href="">INFO</a>
</figure>
<div class="keyinfo_content">
<div class="clearfix">
<time datetime="2012-09-30T00:01Z" class="keyinfo-meta keyinfometadata">Location, Sep 30, 2012 at 0:01 am</time><br/>
<cite class="author_name"><a href="">Mary Bloggs</a></cite>
</div>
<div class="keyinfo_text">
<p>Latest details... Donec leo. Aliquam risus elit, luctus vel, interdum vitae, malesuada eget, elit. Nulla vitae ipsum. Donec ligula ante, bibendum sit amet, elementum quis, viverra eu, ante. Fusce tincidunt. Mauris pellentesque, arcu eget feugiat accumsan, ipsum mi molestie orci, ut pulvinar sapien lorem nec dui.</p>
</div>
</div>
</li>
<li class="keyinfo bypostauthor">
<figure class="gravatar">
<img alt="" src="images/gravatar.png" height="48" width="48" />
<a class="keyinfo-reply-link" href="">INFO</a>
</figure>
<div class="keyinfo_content">
<div class="clearfix">
<time datetime="2012-09-30T00:01Z" class="keyinfo-meta keyinfometadata">Location, Sep 30, 2012 at 0:01 am</time><br/>
<cite class="author_name"><a href="">Jim Bloggs</a></cite>
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

<!-- .................END PLACE YOUR CONTENT HERE ................ -->
	<!-- FOOTER -->
	<jsp:include page="footer.jsp" />
	<!-- END FOOTER -->

</div>
</body>
</html>