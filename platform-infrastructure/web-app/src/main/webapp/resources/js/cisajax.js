function doAjaxGetMyCommunities() {  
	
  $.ajax({  
    type: "POST",  
    url: request.getContextPath() + "/get_my_communities.html",
    //url: "/get_my_communities.html",
  	success: function(response){
      // we have the response 
    	
    	if(response.status == "SUCCESS"){
    		$("ul#mycis").empty();
    		cislistInfo = "";
    		for(i =0 ; i < response.result.length ; i++){
    		// 	for each cis returned do the following
    			cislistInfo += "<li class=\"keyinfo bypostauthor\">";
    			cislistInfo += "<figure class=\"gravatar\">";
    			cislistInfo += "<a href=\"#\" onclick=\"doAjaxGetCisDetails('" + response.result[i].cisid + "');return false\" > <img alt=\"\" src=\"images/webcommunity_pic_sample1.jpg\" height=\"48\" width=\"48\" /></a>";
    			cislistInfo += "<a class=\"keyinfo-reply-link\" href=\"#\" onclick=\"doAjaxGetCisDetails('" + response.result[i].cisid + "');return false\" > INFO </a>";
    			cislistInfo += "</figure>";
    			cislistInfo += "<div class=\"keyinfo_content\">";
    			cislistInfo += "<div class=\"clearfix\">";
    			cislistInfo += "<time datetime=\"2012-09-30T00:01Z\" class=\"keyinfo-meta keyinfometadata\">Location, Sep 30, 2012 at 0:01 am</time>";
    			cislistInfo += "<br/>";
    			cislistInfo += "<cite class=\"author_name\"><a href=\"#\" onclick=\"doAjaxGetCisDetails('" + response.result[i].cisid + "');return false\" >" + response.result[i].cisname + " </a></cite>";
    			cislistInfo += "</div>";
    			cislistInfo += "<div class=\"keyinfo_text\">";
    			cislistInfo += "<p>Need to Add description to Cis Directory to be displayed here</p>";
    			cislistInfo += "</div>";
    			cislistInfo += "</div>";
    			cislistInfo += "</li>";
    		}
    		$("ul#mycis").append(cislistInfo);
    		$("ul#mycis li:first").slideDown("slow");
    	//	$("ul#mycis").show();
	      }else{
	    	  
	      }	      
    	
     
    },  
    error: function(e){  
      alert('Error: ' + e);  
    }  
  });  
  
}
  
  function doAjaxGetSuggestedCommunities() {  
		
	  $.ajax({  
	    type: "POST",  
	//    url: contexPath + "/get_suggested_communities.html",
	    url:  request.getContextPath() + "/get_suggested_communities.html",
	  	success: function(response){
	      // we have the response 
	    	
	    	if(response.status == "SUCCESS"){
	    		$("ul#mysuggestedcis").empty();
	    		cislistInfo = "";
	    		for(i =0 ; i < response.result.length ; i++){
	    		// 	for each cis returned do the following
	    			cislistInfo += "<li>"
	    			cislistInfo += "<a href=\"#\" onclick=\"doAjaxGetCisDetails('" + response.result[i].cisid + "');return false\" >" 
	    			cislistInfo += 	response.result[i].cisname;
	    			cislistInfo +=	"</a></li>";
	    		}
	    		$("ul#mysuggestedcis").append(cislistInfo);
	    		$("ul#mysuggestedcis li:first").slideDown("slow");
	    	//	$("ul#mycis").show();
		      }else{
		    	  
		      }	      
	    	
	     
	    },  
	    error: function(e){  
	      alert('Error: ' + e);  
	    }  
	  });  
}  
  
  