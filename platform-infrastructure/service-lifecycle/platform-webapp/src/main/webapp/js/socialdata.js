/**  
   Socialdata JS 
	private static final String ADD				= "add";
	private static final String REMOVE			= "remove";
	private static final String FRIENDS			= "friends";
	private static final String PROFILES		= "profiles";
	private static final String GROUPS			= "groups";
	private static final String ACTIVITIES		= "activities";
	private static final String LIST			= "list"


*/
  


function submitform(){
    token= document.getElementById("token");
    method= document.getElementById("method");
    
    if (method.value==""){
    	alert("Please add a valid method)");
    	return;
    }
    
    if (token.value==""){
    	alert("Please add a valid token (copy from popup)");
    	w.focus(); 
    	return;
    }
    else {
    	document.getElementById("addConnector").style.visibility= "hidden";
    	document.getElementById("popup").style.visibility="visible";
    	document.sd.submit();
    }
}

function countConnectors(){
   var ul = document.getElementById('listConn');
   var liNodes = [];
 
   for (var i = 0; i < ul.childNodes.length; i++) {
	if (ul.childNodes[i].nodeName == "LI") {
		liNodes.push(ul.childNodes[i]);
	}
   }
   
   return liNodes.length;
}

  
  function disconnect(id){
    document.getElementById("method").value  = "remove";
    document.getElementById("id").value  = id;
    if (id==""){
      alert("Connector id:"+id+ "not Valid!");
    }
    else  document.sd.submit();
  } 


  function exe(method){
  
     if(countConnectors()>0){
  	    document.getElementById("method").value  = method;
  	    document.sd.submit();
  	 }
  	 else{
  	 	alert("There is any connector available");
  	 }
  }
  

  function getToken(url, title){
     w = window.open(url, title, "width=600,height=400,resizeable,scrollbars");
     document.getElementById("addConnector").style.visibility= "visible"; 
    // setTimeout(CheckLoginStatus(), 3000);
    document.getElementById("method").value  = "add";
    document.getElementById("snName").value  = title;
 
  }
  
  
  function removeform(){
    document.getElementById("addConnector").style.visibility= "hidden";
    }

 function CheckLoginStatus() {
      
      
     
      try {
      
      
      if (document.readyState === "complete"){
      	alert("You need to copy these params in the form!");
      }
      /*
      var json = JSON.parse(w.document.body.innerHTML);
      
      if(json.connector!=""){
         
    	 document.getElementById("token").value   = json.connector.access_token;
         document.getElementById("expires").value = json.connector.expires;
         document.getElementById("snname").value  = json.connector.from;
         document.getElementById("method").value  = "add";
         //document.sdForm.submit();
         w.close();
      }
      */
      else setTimeout(CheckLoginStatus, 1000);
   } 
   catch(e)
   {
       //alert(e + " - " + body);
       setTimeout(CheckLoginStatus, 1000);
   }
  	   
 }


