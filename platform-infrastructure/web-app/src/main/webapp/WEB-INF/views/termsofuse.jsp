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
<section id="left_col" class="grid_8">
<div class="breadcrumbs"><a href="">Home</a> / <a href="">Page</a></div>
<section>	
<h4 class="form_title">Terms of Use</h4>
<div class="hr dotted clearfix">&nbsp;</div>	
<h6>Subheading</h6>
<p>Proin at eros non eros adipiscing mollis. Donec semper turpis sed diam. Sed consequat ligula nec tortor. Integer eget sem. Ut vitae enim eu est vehicula gravida. Morbi ipsum ipsum, porta nec, tempor id, auctor vitae, purus. Pellentesque neque. Nulla luctus erat vitae libero. Integer nec enim. Phasellus aliquam enim et tortor. </p>
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
<p>Quisque aliquet, quam elementum condimentum feugiat, tellus odio consectetuer wisi, vel nonummy sem neque in elit. Curabitur eleifend wisi iaculis ipsum. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. In non velit non ligula laoreet ultrices. Praesent ultricies facilisis nisl. Vivamus luctus elit sit amet mi. Phasellus pellentesque, erat eget elementum volutpat, dolor nisl porta neque, vitae sodales ipsum nibh in ligula. Maecenas mattis pulvinar diam. Curabitur sed leo.</p> 
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

<!-- .................END PLACE YOUR CONTENT HERE ................ -->
	<!-- FOOTER -->
	<jsp:include page="footer.jsp" />
	<!-- END FOOTER -->

</div> 	
</body>
</html>