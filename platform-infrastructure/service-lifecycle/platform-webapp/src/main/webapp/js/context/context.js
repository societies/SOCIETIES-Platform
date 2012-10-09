
/**
*	JS for context gui
*   @author: Luca Lamorte
**/

var query = {};


// Execute
function exe(){
   
	document.forms["ctxForm"]["type"].value = query.type;
	document.forms["ctxForm"]["value"].value = query.id;
	document.forms["ctxForm"]["ctxID"].value = query.id;
	document.forms["ctxForm"]["method"].value = query.method;
	document.forms["ctxForm"]["model"].value = query.model;
	document.forms["ctxForm"].submit();
	
}


function lookup(ctxmodel, ctxtype){
	query.type = ctxtype;
	query.id="";
	query.model = ctxmodel;
	query.method="lookup";
	exe();
}


//Query
function exeQuery(value){
    $('#method').val("retrieve");
    $("#retrieve").val(value);
    $("#retrieve").show();
 
}

function retrieve(id){
	query.type="";
	query.id=id;
	query.model="";
	query.method="retreive";
	exe();
	
}

//Query
function edit(value){
   alert("Function not avalable");
 	
}

//Query
function del(value){
  $("#"+value).remove();
}

function hideModelOptions(){
	$('select[context="type"]').hide();
  	$("#create-value").hide();

}

function hideMethodOptions(){
	$("#lookup").hide();
	$("#retrieve").hide();
	$("#create").hide()
	$('#executeQuery').hide();
	$('#idList').hide();
}

(function($) {
     $(document).ready(function() {
      
       $('#method').change(function() {
       			query.method=$(this).val();
  				hideMethodOptions();
  				hideModelOptions();
  				$("#"+$(this).val()).show("fast");
  				if ($(this).val() == "retrieve") {
  					$("#idList").show();
  					$('#executeQuery').show();
  				}
  				
  	    });
       
       $('#lookup').change(function() {
       			query.model=$(this).val();
  				hideModelOptions();
  				$("#"+$(this).val()).show("fast");
  				
  	    });
       
     
       
        $('#retrieve').keyup(function(){	
        	
        	query.id=$(this).val();
        	
       		if ($(this).val().length>0)
       			$('#executeQuery').show();
       		else 
       			$('#executeQuery').hide();
       		
       });
       
        $('#create').change(function() {
       			query.model=$(this).val();
  				hideModelOptions();
  				$("#"+$(this).val()).show("fast");
  				
  	    });
        
        $('#idList').change(function(){
        	$('#retrieve').val($(this).val());
        	query.id=$(this).val();
        	query.id=$(this).val();
        	query.method= "retreive";
        });
       
     				
       $('select[context="type"]').change(function() {
       		 query.type = $(this).val(); 
       		 if (query.method=="create")  $("#create-value").show();
       		 $('#executeQuery').show(); });
      
       
       $('#executeQuery').click(function(){ 
       		exe();
       });
       
       
       /*
       // Filtering results
       
        $('input[name="lookup"]').search('#ctx-id tr', function(on) {
            on.all(function(results) {
              var size = results ? results.size() : 0
              $('#count').text(size + ' matches');
            });

            on.reset(function() {
              $('#none').hide();
              $('#ctx-id tr').show();
               $('#count').text("");
            });

            on.empty(function() {
              $('#none').show();
              $('#ctx-id tr').hide();
            });

            on.results(function(results) {
              $('#none').hide();
              $('#ctx-id tr').hide();
              results.show();
            });
          });
        
        */
        
        });
      })(jQuery);