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
<script type="text/javascript">
	var contexPath = "<%=request.getContextPath() %>";
</script>

<div id="wrapper" class="clearfix">
<div id="container" class="container_12 clearfix">
<!-- Header -->
<header id="header" class="grid_12">
	
	
<!-- MENU -->
<jsp:include page="menu.jsp" />
<!-- END MENU -->
<div class="clear"></div>
</header>
<!--Main Content START -->
<div class="hr grid_12 clearfix">&nbsp;</div>
<section class="grid_12">
<section>
<div class="breadcrumbs"><a href="">Home</a> / <a href="">Page</a></div>
</section>
<div class="websearchbar">
<div class="websearchtitle">
<h4 class="form_title">Your Communities</h4>
</div>
<div class="groupsearch">
<form action="" class="websearch-form frame nobtn rsmall">
<input type="text" name="search" class="websearch-input" placeholder="Search for Communities..." />
</form>
</div>
</div>
</section>
<!-- Left Column -->
<article id="left_col" class="grid_8">
<section class="itemlist">
<header>
</header>
<a href="#" onclick="doAjaxGetMyCommunities();">Refresh My Communities</a>  
<ul class="keyinfolist" id="mycis">
<li> Temp Note: To be loaded on document load but for now, select Refresh My Communities above to populate</li> 
</ul>

<div class="hr clearfix">&nbsp;</div>
</section>
</article>
<!-- Right Column / Sidebar -->
<aside id="sidebar_right" class="grid_4">
<div class="sidebar_top_BG"></div>
<div class="hr dotted clearfix">&nbsp;</div>
<section>
<header>
<h3>Suggested Communities...</h3>
</header>
<a href="#" onclick="doAjaxGetSuggestedCommunities();">Refresh Suggested Communities</a>
<ul class="sidebar" id="mysuggestedcis">
<li> Temp Note: To be loaded on document load but for now, select Refresh Suggested Communities above to populate</li> 
</ul>  
</section>
<section>
<header>
<h3>Other Activity</h3>
</header>
<div class="hr dotted clearfix">&nbsp;</div>
<ul class="contact_data">
<li><figure class="gravatar">
<img alt="" src="images/webcommunity_pic_sample1.jpg" height="48" width="48" />
</figure><strong>Community Name</strong> dolor sit amet, consectetur adipiscing elit. Ipsum dolor sit amet, elit.</li>
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


<!--Main Content END -->
	<!-- FOOTER -->
	<jsp:include page="footer.jsp" />
	<!-- END FOOTER -->
</div>
</body>
</html>