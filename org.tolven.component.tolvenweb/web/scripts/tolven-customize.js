/*
*  Match the Attribute: rowstatus with the anchor text.
*/
function fixRowStatus(column){
	var rowStatus = column.readAttribute("rowstatus");
	var imgHolder = column.getElementsByClassName("fullms-td-nodelink")[0];

	if(rowStatus == leaf){
		imgHolder.removeClassName("fullms-td-collapse");
		imgHolder.removeClassName("fullms-td-expand");
	}else	if(rowStatus == expanded){
		imgHolder.removeClassName("fullms-td-collapse");
		imgHolder.addClassName("fullms-td-expand");
	}else if(rowStatus == collapsed){
		imgHolder.removeClassName("fullms-td-expand");
		imgHolder.addClassName("fullms-td-collapse");
	}

}

/*
*	Use the attribute "rowstatus" to check if the row has been collapsed or expanded. 
*	
*/
var collapsed = "collapsed";
var expanded = "expanded";
var leaf = "leaf";

var transKeyColumns = null;

function setColumns(){
	if(null == transKeyColumns){
		transKeyColumns = document.getElementsByClassName('fullms-td-path');
	}
}
/*
*  Setup Show All/ Hide All controls.
*/
function setupControls(){
	$('_show-all').onclick= function(){
		showAllRows();
	}

	$('_hide-all').onclick= function(){
		hideAllRows();
	}

}

function getPathFromColumn(pathHolder){
	return pathHolder.innerHTML;
}
function getRowForPath(pathHolder){
	return pathHolder.parentNode.parentNode;
}

function showAllRows(){
	var path = getPathFromColumn(transKeyColumns[0]);
	expandRow(getRowForPath(transKeyColumns[0]), 0, path, true);
}

function hideAllRows(){
	var path = getPathFromColumn(transKeyColumns[0]);
	// show first level children

	expandRow(getRowForPath(transKeyColumns[0]), 0, path);
	//hide others
	var children = findImmediateChildren(0, path);
	for( var i = 0; i < children.length; i++){
		path = getPathFromColumn(children[i]);
		var index = findIndexOfChild(path, 0);
		collapseRow(getRowForPath( children[i]), index, path);
	}
}

function initCustomizePage(){
	setColumns();
	setupControls();
	var path = getPathFromColumn(transKeyColumns[0]);
	var firstRow = getRowForPath(transKeyColumns[0]);
	setRowStatus(firstRow, collapsed);
	showRow(firstRow);
	hideAllRows();

	initInPlaceEditors();
}

//These arrays hold the inplace editors.
var toEditors;
var vEditors;

function initInPlaceEditors(){
	toEditors = new Array();
	vEditors = new Array();
}

function toggleRow(path, rowIndex){
	setColumns();

	var found = false;
	var toShow = false;
	var rowStatus = getRowStatus( getRowForPath( transKeyColumns[rowIndex]) );
	if(rowStatus == null || rowStatus == expanded){
		toShow = false;
	}else if(rowStatus == collapsed){
		toShow = true;
	}

	if(toShow == true){
		expandRow( getRowForPath( transKeyColumns[rowIndex]), rowIndex, path);
	}else{
		collapseRow( getRowForPath( transKeyColumns[rowIndex]), rowIndex, path);
	}

}

function collapseRow( rowtr, rowIndex, path){
	if(getRowStatus(rowtr) != leaf){
		setRowStatus(rowtr, collapsed);
	}
	var children = findImmediateChildren(rowIndex, path);
	if(children.length <= 0 ){
	   setRowStatus(rowtr, leaf);
	}else{
	   setRowStatus(rowtr, collapsed);
	for(var i = 0; i < children.length; i++){
	   var childPath = getPathFromColumn(children[i]);
	   var index = findIndexOfChild(childPath, rowIndex);
	   if( index >= 0){
		collapseRow( getRowForPath( children[i]), index, childPath);
	   }else{
		setRowStatus( getRowForPath(children[i]), leaf);
	   }
	   hideRow( getRowForPath(children[i]));
	}
	}
	fixRowStatus(rowtr);
}

function expandRow( rowtr, rowIndex, path, showall){
	if(getRowStatus(rowtr) != leaf){
		setRowStatus(rowtr, expanded);
	}
	var children = findImmediateChildren(rowIndex, path);
	if(children.length <= 0 ){
	   setRowStatus(rowtr, leaf);
	}else{
	for(var i = 0; i < children.length; i++){
	   var childPath = getPathFromColumn( children[i]);
	   var index = findIndexOfChild(childPath, rowIndex);
	   var rowStatus = getRowStatus( getRowForPath( children[i]));

	   if( index >= 0 && (rowStatus == expanded || showall == true)){
		expandRow(getRowForPath( children[i]), index, childPath, showall);
	   }else if(rowStatus == null && index < 0){
		setRowStatus(getRowForPath( children[i]), leaf);
	   }else if(rowStatus == null && index > 0){
		setRowStatus(getRowForPath( children[i]) , collapsed);
	   }

	   showRow( getRowForPath(children[i]) );
	}
	}
	fixRowStatus(rowtr);
}

function showRow(rowtr){
   if(rowtr.hasClassName("fullms-tr-hideall")){
   	rowtr.removeClassName("fullms-tr-hideall");
	var role = rowtr.getElementsByClassName("td_role")[0];
	var roleClass = role.readAttribute("role");
	rowtr.addClassName(roleClass);
   }
   rowtr.show();
}

function hideRow(rowtr){
   rowtr.hide();
}
function findImmediateChildren(rowIndex, path){
	var children = new Array();
	var index = 0;
	var found = false;
	for(var i = rowIndex + 1; i < transKeyColumns.length; i++){
		var childPath = getPathFromColumn(transKeyColumns[i]);
		if( childPath.startsWith(path + '.') == true ){
			if( childPath.lastIndexOf('.') == path.length ){
				children[index++] = transKeyColumns[i];
			}
			found = true;
		}else if(found == true){ 
			/*This condition is satisfied after the node's children have been identified.
			*/
			break;
		}

		// found should be true after the first iteration if the node has any children. 
		// If not, break instead of looping through the list.
		if( found == false) break;
	}
	return children;
}

function findIndexOfChild(path, fromIndex){
	var cIndex = -1;
	for(var i = fromIndex + 1; i < transKeyColumns.length; i++){
		if( path == getPathFromColumn( transKeyColumns[i])){
			cIndex = i;
			break;
		}
	}
	return cIndex;	
}

function getRowStatus(column){
	var rowStatus = column.readAttribute("rowstatus");

	return rowStatus;
}
function setRowStatus(column, status){
	column.setAttribute("rowstatus", status);
	fixRowStatus(column);
}

/*
* hardcode: "menuForm:column: -> id of form:table-id from menu structure edit page.
*/
function setupMenuEditPage(tableid, containerid){
	
	var children = $(tableid).childElements();
	var tbody= undefined;
	for(var i = 0; i < children.length; i++){
	   if( (children[i].tagName).capitalize() == "Tbody"){
		tbody = children[i];
	   }
	}
	tbody.id = containerid;

	if(tbody != undefined){
	  var trs = tbody.childElements();
	  for( var i = 0; i < trs.length; i++){
		trs[i].id = containerid + "_" + (i + 1);
	  }
	}


}

function enableMenuEditSortable(methodName, tableName, containerid, pathName){
	var createSortableFunction = function(){
	Sortable.create(containerid, {tag:'tr', 
		constraint:'vertical',
		hoverclass:'ondrag',
		scroll:window,
		ghosting:true,
		handle:'dragRow',
		onUpdate: function()
		{
		var fn = new Tolven.Util.functionUtil(methodName);
		fn.callMethod(tableName,containerid, pathName);
		}
	});
	};
	DynaLoad.downloadAndCallScript(createSortableFunction, undefined, 'DRAGDROP');

}

function disableSortable(containerid){
	Sortable.destroy(containerid);
}

function updateSequenceOfMSColumns(tableName, containerid, pathName){
	var sequence = getSequence(tableName, containerid, "heading");
	
	if( pathName == undefined || sequence == undefined || sequence.length < 1 ) {
//		Tolven.Util.log( "No change");
	}else{
	   try{
	      var ajaxparams = 'mspath=' + pathName + '&sequence=' + sequence;
	      Tolven.Util.sendUpdateToServer('updateMenuColumnSequence.ajaxc', ajaxparams);
	   }catch(err) {
		alert("Error updating server. Please refresh");
	   }
	}
}

function updateSequenceForMSChildren(tableName, containerid, pathName){
	var sequence = getSequence(tableName, containerid, "childPath");
	
	if( sequence == undefined || sequence.length < 1 ) {
		alert( "Error updating server. Please refresh");
	}else{
	   var ajaxparams = 'type=sequence' + '&sequence=' + sequence;
	   Tolven.Util.sendUpdateToServer('updateMenuStructure.ajaxc', ajaxparams);
	}

}

function getSequence(tableName, containerid, attrName){
	var sequenceArray = new Array();
	var nameArray = new Array();
	var reorder = Sortable.serialize(containerid);

	var neworder = new Array();
	var index = -1;

	var cols = reorder.split('&');

	for(var i = 0; i < cols.length; i++){
	   var carray = cols[i].split('=');

	   if( (i + 1) != carray[1] ){

		var tr = $(containerid + "_" + carray[1]);
		// oldid = carray, newid = i+1

		var headingContainer = tr.getElementsByClassName("mshidden")[0];
		var attr = headingContainer.readAttribute(attrName);
		index++;
		sequenceArray[index] = attr + "-" + (i + 1);
//		Tolven.Util.log(sequenceArray[index]);
	   // Change the name for all input box in this row.
	   var inputs = tr.getElementsByTagName("input");

   	   var splittable = tableName.split(':');
	   for(var inp = 0; inp < inputs.length; inp++){
	   	var inputBox =  inputs[inp]; 
	   	var name = inputBox.readAttribute("name");
	   	var splitnames = name.split(':');
	   	if(splittable.length < splitnames.length){
		  splitnames[splittable.length] = i;
		  var name = splitnames.join(':');
		  inputBox.removeAttribute("name");
		  inputBox.setAttribute("name", name);
	   	}
	   }
	   }
	}

	var sequence;
	for(var i = 0; i < sequenceArray.length; i++){
	   if(undefined == sequence ) sequence = sequenceArray[i];
	   else sequence += ";" + sequenceArray[i];
	}
	// reset row(tr) ids
	var trs = $(containerid).childElements();
	for(var i = 0; i < trs.length; i++){
	  trs[i].removeAttribute("id");
	}
	for( var i = 0; i < trs.length; i++){
	   trs[i].id = containerid + "_" + (i + 1);
	}

	return sequence;
}

/* Util method to create an editor based on the type.
 */
function getInPlaceEditor(id, type, options){
	var editor;

	if('text' == type){
		editor = new Ajax.InPlaceEditor(id, 'updateMenuStructure.ajaxc', {
			cancelControl:'button',
			callback: handleInPlaceEditorCallBack
		});

	}else if('select' == type){
		editor = new Ajax.InPlaceCollectionEditor( id, 'updateMenuStructure.ajaxc', { 
			collection: ['true', 'false'],
			value:$(id).innerHTML,
			ajaxOptions: { method: 'post' } 

		});

	}else if('dynamicselect' == type){


	}
	return editor;
}

/* Get the editor associated with the type and rowindex from 
 *  the list of saved editors.
 */
function getSavedEditor(type, rowIndex){
	var editor;
	if('translationoverride' == type){
		editor = toEditors[rowIndex];
	}else if('visibility' == type){
		editor = vEditors[rowIndex];
	}
	return editor;
}
/* Get the editor associated with the type and rowindex from 
 *  the list of saved editors.
 */
function saveEditor(type, rowIndex, editor){
	if('translationoverride' == type){
		toEditors[rowIndex] = editor;
	}else if('visibility' == type){
		vEditors[rowIndex] = editor;
	}
}

/* call using parameters in the order 
 *  1: classname; helps to identity the cells that need to be edited in place.
 *  2: type;  -> Request type to be sent to the server. 
 *  3: parameter name; -> eg. if type is 'translationoverride' then send the value assigned to 'overridetext' to the server.
 *  4: editorType; -> one of "text" for inPlaceEditor, "select" for InPlaceCollectionEditor.
 */
function enableInPlaceEditors(classname, type, paramname, editorType){
	var textContainers = document.getElementsByClassName(classname);

	for(var i = 0; i < textContainers.length; i++){
		var td = textContainers[i].parentNode;

		// spl case for visibility  nodes. 
		if( 'visibility' == type && textContainers[i].innerHTML == ""){
			saveEditor(type, i, undefined);
			continue;
		}

		td.parentNode.setAttribute('rowindex', i); // set row index in tr.
		td.setAttribute('type', type);
		td.setAttribute('paramname', paramname);

		var editor = getInPlaceEditor(textContainers[i].id, editorType);
		saveEditor(type , i, editor);
		td.observe('click', function(event){ 
			try{ Tolven.Util.log('clicked' );
			var rowindex = Event.element(event).parentNode.readAttribute('rowindex');
			var type = Event.element(event).readAttribute('type');
			enterClick( getSavedEditor(type, rowindex ));
			}catch(err){}
		});
	}
}

/*	returns the parameters to be passed to backend ajax handler. 
	The function looks for its parameters and names in the enclosing cell's attributes.
	for example: it gets type and parametername from the enclosing 'td'
 */
function handleInPlaceEditorCallBack(form, value){
	var parentid = form.id.substring(0, form.id.indexOf('-inplaceeditor'));
	var path = $(parentid).parentNode.parentNode.getElementsByClassName("fullms-td-path")[0].innerHTML;
	var td = $(parentid).parentNode;
	var type = td.readAttribute('type');
	var paramname=td.readAttribute('paramname');
	var params = 'type=' + type + '&path=' + path + '&' + paramname + '=' + encodeURIComponent(value);
	return params;
}

function enterClick( editor){
	editor.enterEditMode('click');
}

DynaLoad.alertScriptLoaded();