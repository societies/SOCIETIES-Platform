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
<p class="error">Please, insert...</p>
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
<p class="error">Please, select an option</p>
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