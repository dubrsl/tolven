liveGrids = {};

/**
 * Function used to create Performance grid
 * 
 * added on 12/Jan/2011 
 * @author Suja Sundaresan
 * @param menuPath
 * @param id
 * @param methodName
 * @param methodArgs
 * @param ajaxUrl
 */
createPerformanceGrid = function( menuPath,id, methodName, methodArgs, ajaxUrl) {
	if(!id)
		id = menuPath;
	var root = $(id);
	var grid = $(id+'-grid');
	if (root.getAttribute( 'gridOffset' )==null) { 
		root.setAttribute( 'gridOffset', grid.getAttribute( 'gridOffset'));
		root.setAttribute( 'gridSortCol', grid.getAttribute( 'gridSortCol'));
		root.setAttribute( 'gridSortDir', grid.getAttribute( 'gridSortDir'));
		root.setAttribute( 'filterPerValue', "");
		root.setAttribute( 'menuPath', menuPath);
		root.setAttribute( 'element', menuPath);
	}

	var methodNameValue = methodName;
	var methodArgsValue = methodArgs;

	if (methodName == undefined) {
		methodNameValue = "";
	}
	if (methodArgs == undefined) {
		methodArgsValue = "";
	}

	if (ajaxUrl==null)
		ajaxUrl = 'getPerformanceData.ajaxper';

	liveGrids[id] = new Rico.LiveGrid( id+"-LG",
		1*grid.getAttribute('visibleRows'),
		1*grid.getAttribute('totalRows'),
		ajaxUrl,
		{	prefetchBuffer: true,
			tableClass: 'gridBody',
			loadingClass: 'gridLoading',
            scrollerBorderRight: '0px solid #FF0000',
			offset: 1*root.getAttribute('gridOffset'),
			sortCol: root.getAttribute('gridSortCol'),
			sortDir: root.getAttribute('gridSortDir'),
			largeBufferSize: 5.0,
			nearLimitFactor: 0.4,
			rootId: id,
			onscroll : updateOffset,
			onRefreshComplete: updatePerformanceSortInfo,
			requestParameters: [{name: 'element', value: root.getAttribute('element')},
								{name: 'filter', value: root.getAttribute('filterPerValue')},
								{name: 'methodName', value: methodNameValue},
								{name: 'methodArgs', value: methodArgsValue },
								{name: 'path', value: root.getAttribute('menuPath') }],
			sortAscendImg: '../images/sort_asc.gif',
			sortDescendImg:'../images/sort_desc.gif'
		});

	grid.style.width=($(id+'-LG_header').offsetWidth+19)*2+'px';
	grid.style.width=($(id+'-LG_header').offsetWidth+19)+'px';
	grid.style.border='#999999 solid 1px';
	if( $(id).getAttribute('filterPerValue') != null && $(id).getAttribute('filterPerValue') != "" ) {
		alert($(id).getAttribute('filterPerValue'));
		performanceFilterValueChange($(id).getAttribute('filterPerValue'), id, methodNameValue, methodArgsValue ,menuPath );
	}

	if($(id).className=="popupgrid"){
		$(id).style.width = $(id+"-grid").getWidth();
	}
}

/**
 * On filter event
 * 
 * added on 12/Jan/2011 
 * @author Suja Sundaresan
 * @param element
 * @param methodNameValue
 * @param methodArgsValue
 * @param ajaxUrl
 */
function perfFilterClick(element, methodNameValue, methodArgsValue, ajaxUrl) {
	var filterValue = "";
	if($('FieldFrom').value != '' || $('FieldTo').value != '') {
		if ($('FieldFrom').value != '')
			filterValue = 'date:' + $('FieldFrom').value + '-';
		else
			filterValue = 'date:-';
		if ($('FieldTo').value!='')
			filterValue += $('FieldTo').value;
	}
	if($('user').value != '') {
		if (filterValue != '')  filterValue += "@"; 
		filterValue += 'user:'+$('user').value;
	}
	if($('patient').value!='') {
		if (filterValue != '')  filterValue += "@"; 
		filterValue += 'patient:'+$('patient').value;
	}
	if($('method').value!='') {
		if (filterValue != '')  filterValue += "@"; 
		filterValue += 'method:'+$('method').value;
	}
	$(element).setAttribute('filterPerValue', filterValue);
	checkPerformanceInput(filterValue, element, methodNameValue, methodArgsValue,element, ajaxUrl);
} 

/**
 * Function to user to check performance input
 * 
 * added on 12/Jan/2011 
 * @author Suja Sundaresan
 * @param value
 * @param id
 * @param methodName
 * @param methodArgs
 * @param menuPath
 * @param ajaxUrl
 */
function cpi(value, id, methodName, methodArgs, menuPath, ajaxUrl){
	performanceFilterValueChange(value, id, methodName, methodArgs, menuPath, ajaxUrl);
}

/**
 * Function to user call cpi function at regular interval
 * 
 * added on 12/Jan/2011 
 * @author Suja Sundaresan
 * @param value
 * @param id
 * @param methodName
 * @param methodArgs
 * @param menuPath
 * @param ajaxUrl
 */
function checkPerformanceInput(value, id , methodName, methodArgs,menuPath, ajaxUrl){
	//$(id).setAttribute('filterPerValue', value );
	setTimeout( "cpi('" + value + "', '" + id+  "', '" + methodName + "', '" +  methodArgs + "','" + menuPath + "', '" + ajaxUrl + "')", 125);
}

/**
 * This function caluculate Performance count on filter value change
 * 
 * added on 12/Jan/2011 
 * @author Suja Sundaresan
 * @param value
 * @param id
 * @param methodName
 * @param methodArgs
 * @param menuPath
 * @param ajaxUrl
 */
function performanceFilterValueChange(val, id, methodName, methodArgs,menuPath, ajaxUrl) {
	if(!menuPath)
		menuPath = $(id).getAttribute('menuPath');

	var lg = liveGrids[id];
	var requestParams = new Array();
	//$(id).setAttribute('filterPerValue', val );
	lg.setRequestParams( {name: 'element', value: $(id).getAttribute('element')}, {name: 'path', value: menuPath}, {name: 'filter', value: val}, {name: 'methodName', value: methodName}, {name: 'methodArgs', value: methodArgs} );
	if (val) {
		if (ajaxUrl=='getPerformanceData.ajaxper')
			ajaxUrl = 'countPerformanceData.ajaxper';
		else
			ajaxUrl = 'countPerformanceData.ajaxper';

		var params = 'element='+menuPath+'&filter='+val;
		var countMDAjax = new Ajax.Request(
			ajaxUrl,
			{
				method: 'get',
				parameters: 'element='+menuPath+'&filter='+val,
				onSuccess: function(req) {
					new Ajax.Request(
						ajaxUrl,
						{
							method: 'get',
							parameters: 'element='+menuPath,
							onSuccess: function(req) {
								$(id+"-totalCount").innerHTML = req.responseText + " " ;
							}
						});
				countPerComplete( id, req );
				}
			});
	} else {
		$(id+"-foot").innerHTML = "" ;
		var grid = $(id+'-grid');
		lg.setTotalRows( 1*grid.getAttribute('totalRows') );
		lg.requestContentRefresh(0);
	}
}

/**
 * This function display filtered items count on the footer section
 * 
 * added on 12/Jan/2011 
 * @author Suja Sundaresan
 * @param rootId
 * @param request
 */
function countPerComplete( rootId, request ) {
	$(rootId+"-foot").innerHTML = " / " + request.responseText + " filtered items" ;
	var lg = liveGrids[rootId];
	lg.setTotalRows( 1*request.responseText );
	lg.requestContentRefresh(0);
}

/**
 * This function calls on sucess of the performance grid display
 * 
 * added on 12/Jan/2011 
 * @author Suja Sundaresan
 * @param rootId
 * @param request
 */
function updatePerformanceSortInfo( liveGrid ) {
	var sortCol = liveGrid.sort.getSortedColumnIndex();
	var root = $(liveGrid.metaData.options.rootId);

	if (sortCol >= 0) {
		var sortColName = liveGrid.sort.options.columns[sortCol].name;
		var sortDir = liveGrid.sort.options.columns[sortCol].getSortDirection();
		root.setAttribute( 'gridSortCol', sortColName );
		root.setAttribute( 'gridSortDir', sortDir );
	}
	doPerSubstring(root);
}

/**
 * This function user to limit the long string value and display the name on the tooltip
 * 
 * added on 12/Jan/2011 
 * @author Suja Sundaresan
 * @param rootId
 * @param request
 */
function doPerSubstring(root){
	var _ths = root.getElementsByTagName("th");
	var headerRowWidth = {};
	for( var i = 0; i < _ths.length; i++){
	   headerRowWidth[ i ] = _ths[i].offsetWidth;
	}
	var testChars = "AAAAAAAAAA";
	$('testFontSize').innerHTML = testChars;
	var charWidth = Math.round( $('testFontSize').offsetWidth / testChars.length );

	var trs = root.getElementsByTagName("tr");

	if( trs.length > 5){
		for( var i = 5; i < trs.length; i++){
			if(trs[ i ].offsetHeight > currentTextsize * 1.5){
				var tds = trs[ i ].getElementsByTagName("td");
				for( var j = 0; j < tds.length; j++){
					var td_text = tds[ j ].innerHTML;
					var allowedChars = Math.round ( headerRowWidth[ j ] / charWidth )+10;

					if( td_text.indexOf("<a") >= 0 || td_text.indexOf("<A") >= 0 ) {
						var td_original_content = "";
						var startIndex = td_text.indexOf(">") + 1;
						var endIndex = td_text.indexOf("</", startIndex );
						if( endIndex <= startIndex) 
							td_text = "";
						else
							td_text = td_text.substring( startIndex, endIndex );

						td_original_content = td_text;

						if( td_text.length > allowedChars ){
							if( allowedChars > 5 ) allowedChars -= 5;
							td_text = td_text.substring(0, allowedChars ) + " ...";

							var innerhtml = tds[ j ].innerHTML;
							tds[ j ].innerHTML = innerhtml.replace(td_original_content, td_text);
						}
					}else{
						if( td_text.length > allowedChars ){
							if( allowedChars > 5 ) allowedChars -= 5;
							if (td_text.substring(0, allowedChars).length>30)
								tds[ j ].innerHTML = td_text;
							else
								tds[ j ].innerHTML = td_text.substring(0, allowedChars) + " ...";
						}
					}
					if (td_text.length>allowedChars){
						
						if (j==4)
							tds[ j ].innerHTML = "<span id='spColTxt"+i+"'>"+td_text.substring(0, allowedChars) + "..." + "<a href='javascript:displayQuerString(\"sp"+i+"\");'>more</a></span><span id='sp"+i+"' style='display:none;'>"+td_text+"</span>";
						else
							tds[ j ].innerHTML = "<span title='"+td_text+"'>"+td_text.substring(0, allowedChars) + "..." + "</span>";
					}
				}
			}
		}
	}
}

/**
 * This function is used to hide/show the date field and patient drop down, depending on the filter type
 * 
 * added on 12/Jan/2011 
 * @author Suja Sundaresan
 * @param rootId
 * @param request
 */
function filterchange(val) {
	if (val=="date"){
		$('dateDiv').style.display="block";
		$('usereDiv').style.display="none";
		$('patientDiv').style.display="none";
	} else if (val=="user"){
		$('dateDiv').style.display="none";
		$('usereDiv').style.display="block";
		$('patientDiv').style.display="none";
	} else if (val=="patient"){
		$('dateDiv').style.display="none";
		$('usereDiv').style.display="none";
		$('patientDiv').style.display="block";
	}
}

/**
 * Clear filter values
 * 
 * added on 03/Feb/2011 
 * @author Suja Sundaresan
 * @param element
 * @param methodNameValue
 * @param methodArgsValue
 * @param ajaxUrl
 */
function clearFilterValues(element, methodNameValue, methodArgsValue, ajaxUrl) {
	$('FieldFrom').value = "";
	$('FieldTo').value = "";
	$('user').value = "";
	$('patient').value = "";
	$('method').value = "";
	$(element).setAttribute('filterPerValue', "");
	checkPerformanceInput("", element, methodNameValue, methodArgsValue,element, ajaxUrl);
} 

/**
 * To display Query String pop up
 * 
 * added on 09/Feb/2011 
 * @author Suja Sundaresan
 * @param obj
 */
function displayQuerString(obj){
	$('queryPopUP').style.display="block";
	var str = $(obj).innerHTML;
	var outstr = "";
	var strLen = str.length;
	st=0;
	while (strLen>0){
		outstr += "<div>"+str.substring(st, st+65) + "</div>";
		str = str.substring(st+65, strLen);
		strLen = str.length;
	}
	$('queryPopUPStr').innerHTML = outstr;
	if ($('queryPopUpId').value!="")
		$('spColTxt'+$('queryPopUpId').value).style.fontWeight="normal";
	$('queryPopUpId').value=obj.replace('sp','');
	$('spColTxt'+$('queryPopUpId').value).style.fontWeight="bold";
}

/**
 * To hide Query String pop up
 * 
 * added on 09/Feb/2011 
 * @author Suja Sundaresan
 * @param obj
 */
function hideQuerString(){
	$('queryPopUP').hide();
	if ($('queryPopUpId').value!="")
		$('spColTxt'+$('queryPopUpId').value).style.fontWeight="normal";
}