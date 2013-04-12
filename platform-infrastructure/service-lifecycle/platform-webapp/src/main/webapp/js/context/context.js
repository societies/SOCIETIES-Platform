
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
	if(query.pathIndex>=0){
		document.forms["ctxForm"]["pathIndex"].value = query.pathIndex;
	}
	document.forms["ctxForm"].submit();
	
}


function lookup(ctxmodel, ctxtype){
	query.type = ctxtype;
	query.id="";
	query.model = ctxmodel;
	query.method="lookup_model";
	exe();
}


//Query
function exeQuery(value){
    $('#method').val("retrieve");
    $("#retrieve_value").val(value);
    $("#retrieve").show();
 
}

function retrieveWithCut(id,pathIndex){
	query.pathIndex=pathIndex
	retrieve(id);
	
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
   alert("Function not avaliable");
 	
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
	$("#lookup_model").hide();
	$("#retrieve_value").hide();
	$("#create").hide()
	$('#executeQuery').hide();
	$('#idList').hide();
}

(function($) {
     $(document).ready(function() {
      
      /* $('#method').change(function() {
       			query.method=$(this).val();
  				hideMethodOptions();
  				hideModelOptions();
  				$("#"+$(this).val()).show("fast");
  				if ($(this).val() == "retrieve") {
  					$("#idList").show();
  					$('#executeQuery').show();
  				}
  				
  				
  	    });
       */
       $('#lookup_model').change(function() {
    	   		query.method="LOOKUP"
       			query.model=$(this).val();
       			query.id='';
  				hideModelOptions();
  				$("#"+$(this).val()).show("fast");
  				
  	    });
       
     
       
        $('#retrieve_value').keyup(function(){	
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
        	$('#retrieve_value').val($(this).val());
        	query.id=$(this).val();
        	query.method= "retreive";
        });
       
     				
       $('select[context="type"]').change(function() {
       		 query.type = $(this).val(); 
       		 if (query.method=="create")  $("#create-value").show();
       		 $('#executeQuery').show(); });
      
       
       $('#executeLookup').click(function(){ 
       		exe();
       });
       
       $('#executeRetrieve').click(function(){ 
      		exe();
      });
   });
})(jQuery);


function changeView(view){
	ctxForm.viewType.value=view; 
	ctxForm.submit();
}

function showAction(){
	
	$('table#action_table tr.action').hide();
	
	var method = $('#method').val();
	query.method=method;
	$('#'+method).show();
}

function rmvSpclChrs(str){return str.replace(/[^\w\s]/gi, '');}

function showSelect(obj){
	
	$('#type_newValue_').css('display','none');
	$('#type_newValue_entity').css('display','none');
	$('#type_newValue_attribute').css('display','none');
	$('#type_newValue_association').css('display','none');
	$('#value_newValue_tr').css('display','none');
	
	$('#type_newValue_'+$(obj).val()).css('display','inline');
	
	if($(obj).val() == 'attribute'){
		$('#value_newValue').css('display','table-row');
	}
}


filterValue = new Array(); 
function setFilter(tableId, filterColName, object){
	
	filterValue[filterColName] = object.value;
	
	removeFilter(tableId);
	
	if(filterValue['ctx-model'] != null && filterValue['ctx-model']!=''){
		doFilter('ctx-model',"ctx-model-val",true);		
	}
	
	if(filterValue['ctx-type'] != null && filterValue['ctx-type']!=''){
		doFilter('ctx-type',"ctx-type-val",true);		
	}
	
	if(filterValue['ctx-value'] != null && filterValue['ctx-value']!=''){
		doFilter('ctx-value',"ctx-value-val",false);		
	}
	
	if(filterValue['ctx-id'] != null && filterValue['ctx-id']!=''){
		doFilter('ctx-id',"ctx-id-val",false);		
	}
}

function removeFilter(tableId){
	$('table#'+tableId+' tr.ctx-row').css('display','table-row');
}

function doFilter(filterColName,colName,strctlyEqual){
	var modelColList = $('.'+colName);
	for(i = 0 ; i< modelColList.length ; i++){
		if(strctlyEqual){
			if($(modelColList[i]).html() != filterValue[filterColName]){
				$(modelColList[i]).parent().css('display','none');
			}
		} else {
			//if contains
			if($(modelColList[i]).html().toLowerCase().indexOf(filterValue[filterColName].toLowerCase()) == -1){
				$(modelColList[i]).parent().css('display','none');
			}
		}
	}
}

function link(association_id){
	var entity = $('#model_linkEntity').val();
	if(entity =='' || entity == null){
		return;
	}
	if(confirm("Do you want link this Enity?")){
		$.ajax({
			type: 'GET',
			url:"linkEntity.html",
			data:{
				"parentId":association_id,
				"entity":entity
			},
			success : function(data, status, jqXHR){
				createNewRow(
						'entity_table',
						entity,
						"ENTITY",
						data,
						"");
				
				//add counter
				var count = $('#entity_table_counter').html();
				count++;
				$('#entity_table_counter').html(count);
				
				//show table body
				$('#entity_table_body').show();
				
				//remove action values
				document.getElementById('model_linkEntity').selectedIndex = 0;
				$('table#action_table tr.action').hide();
			},
			error: function(jqXHR,textStatus,errorThrown ){
				alert('Error occured, no data saved!');
			}			
		});
	}
}

function save(idRow){
	var model = document.getElementById('model_newValue').value;
	if(model =='' || model == null){
		return;
	}
	
	if(confirm("Do you want save this Model?")){
		
		//open table
		if($('#'+model.toLowerCase()+"_table_body").css("display") == 'none'){
			$('#'+model.toLowerCase()+"_table_arrow").html("<img src='images/arrow_down.png'/>");
			$('#'+model.toLowerCase()+"_table_body").show();
		}
		
		var type = "type_newValue_"+model;
		var tableId = model.toLowerCase()+"_table";
		
		$.ajax({
			type: 'GET',
			url:"saveModel.html",
			data:{
				"parentId":idRow,
				"model":model,
				"type" : document.getElementById(type).value,
				"value" : document.getElementById('value_newValue').value
			},
			success : function(data, status, jqXHR){
				createNewRow(
						tableId,
						data,
						model,
						document.getElementById(type).value,
						document.getElementById('value_newValue').value);
				
				//add counter
				var count = $('#'+tableId+'_counter').html();
				count++;
				$('#'+tableId+'_counter').html(count);
				
				//delete insert fields value
				document.getElementById(type).selectedIndex = 0;
				document.getElementById('model_newValue').selectedIndex = 0;
				document.getElementById('value_newValue').value="";
				document.getElementById('method').selectedIndex = 0;
				
				$('table#action_table tr.action select.type_value').hide();
				$('select#type_newValue_').show();
				$('table#action_table tr.action').hide();
				
			},
			error: function(jqXHR,textStatus,errorThrown ){
				alert('Error occured, no data saved!');
			}
		});
	}
}

function createNewRow(tableId, idRow,model,type, value){
	var id = rmvSpclChrs(idRow);
	
	var tr = "<tr class='ctx-row' id='"+id+"'>";
	
	if(model.toUpperCase() != 'ENTITY' && model.toUpperCase() != 'ASSOCIATION'){
		tr +=	"<td class='ctx-mod-btn'> <div class='button' onclick=\"setRowModificable('"+idRow+"')\"><img class='icon' src='images/modify.png'> </div> </td>"+
				"<td class='ctx-del-btn'> <div class='button' onclick=\"deleteRow('"+idRow+"','"+model+"')\"><img class='icon' src='images/delete.png'> </div> </td>";
	} else {
		tr+=	"<td class='ctx-del-btn'> <div class='button' onclick=\"deleteRow('"+idRow+"','"+model+"')\"><img class='icon' src='images/delete.png'> </div> </td>"+
				"<td class='ctx-retr-btn'> <div class='button' onclick='retrieve(&#39;"+idRow+"&#39;)'><img class='icon' src='images/ahead.png'></div> </td>";
	}
		tr+=	"<td class='ctx-id-val'>num#</td>"+
				"<td class='ctx-model-val' name='model'>"+model.toUpperCase()+"</td>"+
				"<td class='ctx-type-val' name='type'>"+type+"</td>"+
				"<td class='ctx-value-val'>"+value+"</td>"+
				"<td></td>"+
			"</tr>";
	
	
	$("table#"+tableId+" #lastHeadRow").after(tr);
}

function setRowModificable(idRow){
	var id = rmvSpclChrs(idRow);
	$("tr#"+id+" td.ctx-mod-btn").html("<div class='button' onclick=\"modifyRow('"+idRow+"')\"><img class='icon' src='images/save.png'> </div>");
	//setColModificable(idRow,'ctx-model-val');
	//setColModificable(idRow,'ctx-type-val');
	setColModificable(idRow,'ctx-value-val');
}

function setColModificable(idRow, colClass){
	var id = rmvSpclChrs(idRow);
	var val = $("tr#"+id+" td."+colClass).html();
	$("tr#"+id+" td."+colClass).html("<input type='text' name='"+colClass+"' value='"+val+"'/>");
}

function setColFixed(idRow, colClass){
	var id = rmvSpclChrs(idRow);
	var val =  $("tr#"+id+" td."+colClass+" input").val();
	$("tr#"+id+" td."+colClass).html(val);
}

function modifyRow(idRow){
	if(confirm("Do you want modify this Model?")){
		var id = rmvSpclChrs(idRow);
		var val =  $("tr#"+id+" td.ctx-value-val input").val();
		var model =  $("tr#"+id+" td.ctx-model-val").html();
		$.ajax({
			type: 'GET',
			url:"updateModel.html",
			data : {
				"id" : idRow,
				"model" : model,
				"value" : val
			},
			success : function(data, status, jqXHR){
				$("tr#"+id+" td.ctx-mod-btn").html("<div class='button' onclick='setRowModificable("+id+")'><img class='icon' src='images/modify.png'> </div>");
				//setColFixed(idRow,'ctx-model-val');
				//setColFixed(idRow,'ctx-type-val');
				setColFixed(idRow,'ctx-value-val');
			},
			error: function(jqXHR,textStatus,errorThrown ){
				alert('Error occured, no data updated!');
			}
		});
	}
}	

function deleteRow(idRow,tableId){
	if(confirm("Do you want delete this Model?")){
		var id = rmvSpclChrs(idRow);
		$.ajax({
			type:'GET',
			url:"deleteModel.html",
			data:{
				"id":idRow
			},
			success : function(data, status, jqXHR){
				$("tr#"+id).remove();
				
				//sub counter
				var count = $('#'+tableId+'_counter').html();
				count--;
				$('#'+tableId+'_counter').html(count);
			},
			error: function(jqXHR,textStatus,errorThrown ){
				alert('Error occured, no data deleted!');
			}
		});
	}
	
}

function showHide(tableId){
	
	if($('#'+tableId+"_body").css("display") == 'none'){
		$('#'+tableId+"_arrow").html("<img src='images/arrow_down.png'/>");
		$('#'+tableId+"_body").show();
	} else {
		$('#'+tableId+"_arrow").html("<img src='images/arrow_right.png'/>");
		$('#'+tableId+"_body").hide();
	}
	
}
