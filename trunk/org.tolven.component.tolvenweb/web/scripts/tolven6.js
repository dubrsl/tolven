	// Base page for AJAX updates
	// Starting page.
	initialPage = null;
	// The currently visible page, implying that every outer page is also visible.
	visiblePage = "";
	accountUserId = 0;
	helpBack = "";
	// Incremented and included in server requests so we don't get stale data from cache and old responses can be discarded.
	serialNo = 11;
	titlePrefix = "Tolven";
	controlpressed= 0;
	adjustableElements = new Array();
// name space object to hold all the global variables. We should move all the variables in to a single name space.
var Tolven = new Object();

Tolven.CalendarEventsCache = new Map();
Tolven.CalendarCache = new Map();

timelineObjContainer = null;
Event.observe(window, 'load', function() {
Event.observe(document, 'click', function(event) {

	if(Prototype.Browser.IE){
	if(event.srcElement.href == undefined || event.srcElement.href.indexOf('toggleDrpDwn')== -1)
		hideDialog();
	}
	if (event.ctrlKey) {
		controlpressed = 1;
		//Event.stop(event);
	}else{
		controlpressed = 0;
	}

});
Event.observe(document, 'keypress', function(event) {
	var element = Event.element(event);
	if (element && element.tagName.toLowerCase()=='input' && element.type!='submit') {
		if (event.keyCode == Event.KEY_RETURN) {
	    		Event.stop(event);
		}


	}
})});
Event.observe(window, 'resize', function() {
wrapTabsInCurrentPage();
unWrapTabsInPage();
hideDialog();
});

// Intercept a normal Submit and do it Ajax-style instead
function ajaxSubmit2(f ) {
    var elements = Form.getElements(f);
    var queryComponents = new Array();
    for (var i = 0; i < elements.length; i++) {
          var queryComponent = Form.Element.serialize(elements[i]);
          if (queryComponent) queryComponents.push(queryComponent);
    }
	queryComponents.push( 'element='+visiblePage );
//	Tolven.Util.log( "submit: " + visiblePage );
     Form.disable( f );  // Don't allow edits or button pushes
     var ajax = new Ajax.Updater(
         f.parentNode.id,           // Put results here
         f.action,        // URL
         {                // options
             method:'post',
             evalScripts: true,
             onFailure: function(request) {displayError(request,f.parentNode.id);},
             postBody: queryComponents.join('&')
        });
      return false;
}

if (!document.all && document.captureEvents) // for FF
{
  document.captureEvents(Event.MOUSEDOWN);
  document.onclick=click;
}

/* Util method to set focus on input
*/
function setFocus(id){
	if( $(id) && $(id).focus){
	   $(id).focus();
	}
}

/*	All key return events are blocked.
*	For Special cases, like the login page, use this method to submit a form when enter is pressed.
*/
function submitOnEnter(event, frm){
	if (event.keyCode == Event.KEY_RETURN) {
		document.forms[frm].submit();
	}
}

function submitOnEnter2(event, buttonid){
	if (event.keyCode == Event.KEY_RETURN) {
		$(buttonid).click();
	}
}

function click(e)
{
  if(e.target == undefined || e.target.toString().indexOf('toggleDrpDwn')== -1)
	if(e.target.id.toString() !='showDropDown'){
		hideDialog();
	}
  if (!document.all)
  {
	if (e.ctrlKey)
    {
	 controlpressed = 1;
	 e.preventDefault();
     e.stopPropagation();
	 thisTarget = e.target.toString();  //javascript:showPane('echr:patient-28283:summary',false,'echr:patients:all')
	params = thisTarget.substring(thisTarget.indexOf('(')+1,thisTarget.lastIndexOf(')')).split(',');
		for(i =0 ;i<params.length;i++){
			params[i] = params[i].replace(/'/g,''); // to replace '
			params[i] = params[i].replace(/^\s+|\s+$/,''); // to replace white chars

		}
	functionName = thisTarget.substring(thisTarget.indexOf(':')+1,thisTarget.indexOf('('));
	if(functionName == 'showPane'){
		switch(params.length){
		 case 1:
			 showPane(params[0]);
			break;
		 case 2:
			 showPane(params[0],params[1]);
			break;
		 case 3:
		  showPane(params[0],params[1],params[2]);  // will replace this req ex
			break;
		}
	}
 	}
	else
	controlpressed = 0;
  }
}

// Return the id of the parent that is a container node.
// Referenced in AjaxServlet.java
function containerId( node ) {
	if (node.className=='container') return node.id;
	var parent = node.parentNode;
	if (parent==null) return null;
	return containerId( parent );
}

// Periodic updating Submit
function ajaxUpdatingSubmit(action, rslt) {
     var updater = new Ajax.PeriodicalUpdater(
         rslt,           // Put results here
         action,        // URL
         {                // options
          	 frequency: 2,
          	 decay: 1,
             method:'get',
			 parameters: 'accountUserId='+accountUserId
        });
     return updater;
}
	// If there's an available placeholder for this menu item, then request the item from server.
	// we assume the id has already been determined to be absent
	function fillPlaceholder( id ) {alert(id);
		// Only do this is the leaf node has an object id
		var nodes = id.split(":");
		var nodeDetail = nodes[nodes.length-1].split("-");
		if (nodeDetail.length==1) return;
		// There must be a placeholder for this to make sense
		var dash = id.lastIndexOf('-',id.length-1);
		var placeholder = $(id.substring( 0, dash ) + ':sel');
		if (placeholder==null) return;
		// Create the node just before the placeholder
		var menuItem = document.createElement( 'li' );
		menuItem.id = id + ':sel';
		menuItem.className = 'active';
		menuItem.innerHTML = "<a href='javascript:showPane(" + '"' + id + '"' + ");'>" + id + "</a>";
		placeholder.parentNode.insertBefore(menuItem,placeholder);
		alert( "[fillPlaceholder]Request link for: " + id );
		getRemoteMenuItem( id );
	}
	// We need to tell the server about this missing item.
	function addRemoteMenuItem( id ) {

	}

	// This tells the servers to add a menu item similar to adding a menu item in the configuration page.
	function getRemoteMenuItem( id ) {
		serialNo++;
		var ajax = new Ajax.Updater(
		 id + ":sel",
		 contextPath + '/ajax/menuDispatch.jsf',
		 {   method: 'get',
			 parameters: 'element='+id+
			             '&serialNo='+serialNo +
			             '&accountUserId='+accountUserId });
	}

	// Add a dynamic menu item
	function requestMenuItem( id ) {
		// Only do this if the leaf node has an object id (eg echr:patient-123)
		var nodes = id.split(":");
		var nodeDetail = nodes[nodes.length-1].split("-");
		if (nodeDetail.length==1) return;
		// There must be a placeholder for this to make sense
		var dash = id.lastIndexOf('-',id.length-1);
		var placeholder = $(id.substring( 0, dash ) + ':sel');
		if (placeholder==null) return;

		var menuItem = document.createElement( 'li' );
		menuItem.id = id + ':sel';
		menuItem.className = 'active';
		menuItem.innerHTML = "<a href='javascript:showPane(" + '"' + id + '"' + ");'>" + menuItem.id +
		"</a>";

		placeholder.parentNode.insertBefore(menuItem,placeholder.nextSibling);
		checkOverFlow(id);

		getRemoteMenuItem( id );
	}
	function checkOverFlow(id){
		menuItm = $(id+":sel");
		if(menuItm!=null){
			 var parentDiv = getFirstAncestorByTagName( menuItm, "DIV", "div" );
			 if(adjustableElements.length < 1)
			   adjustableElements = findAdjustableElements();// save time if the array has been already initialized
			 for(var i=0;i<adjustableElements.length;i++){
				 if(parentDiv.id == adjustableElements[i].id){
					 wrapTabsInElement(parentDiv,menuItm);
					 break;
				 }
			 }
		}

	}
	function getInnerText( e ) {
		var c = e.getElementsByTagName( "a" );
		if (c.length<1) return "-";
		if (c[0].childNodes.length<1) return "-";
		if (c[0].childNodes[0].data == undefined ) return "-";
		return c[0].childNodes[0].data.replace(/[\0\n\r\f\t]*/g, "" );
	}

	// Make sure menus are selected (and visible) down to the item we're currently displaying
	function currentMenus( id, isHistoryAction ) {
		// For history sake; Identify the initial page.
		if( id.indexOf( defaultTab ) >= 0 && id != defaultTab){
			defaultTab = id;
		}
		if( enableBackButton == true ){
			if( currentTab.indexOf( id ) < 0 ){
			  if( isHistoryAction != true ){
			    window.location.hash = "H:" + id;
			    setIFrameHash( "H:" + id );
			  }
			  currentTab = id;
			}
		}

		// All parent containers except top level should have a corresponding menu item
		var menuItem = $(id+":sel");
		if (menuItem==null) {
			requestMenuItem( id );
		} else {
			menuItem.className = 'active';
		}
		var parent = removeLast( id );
		if (parent!=null) {
			currentMenus( parent );
		} else document.title = titlePrefix;
		// Add menu path to title
		if (menuItem!=null) document.title = document.title + " - " + getInnerText( menuItem );
	}

	// Recursive ascent to top of tree
	// Although we request the upper-most container first, there's no guarantee it will arrive first.
	function showContentContainer( dest, original ) {
		var container = removeLast( dest );
		// Recurse to get parent
		if (container!=null) showContentContainer( container, original );
		// If the item is not here, create the div and make plans to get the contents
		if ( $(dest) == null) {
			dest = addRemoteContent( dest, original );
		}
		// Let the original be the actual pane to display if a container is later selected.
		if (dest!=original)	$(dest).setAttribute("visibleSubPage", original);

		$(dest).style.display = 'block';
	}

	// If visibleSubPage has been removed, we'll need to find another candidate
	// In any case, recurse to the lowest level VSP.
	function fixVisibleSubPage( id ) {
		var content = $(id);
		if (content==null) {
			return addRemoteContent( id, id );
		}
		var vsp = content.getAttribute("visibleSubPage");
		if (vsp == null) return content;
		if (vsp == '') return content;
		if ($(vsp)!=null) return fixVisibleSubPage( vsp );
		// VSP no good, find another: Get the menu items
		var lis = content.getElementsByTagName('li');
		for (var x = 0; x < lis.length; x++) {
			if (lis[x].style!=null && lis[x].style.display!='none') {
				var id2 = removeLast(lis[x].id);	// Remove :sel
				content.setAttribute( "visibleSubPage", id2);
				return fixVisibleSubPage( id2 );
			}
		}
		return content;
	}

	// Ask the server to instantiate a new item and wait for the new element name
	function instantiate( templateId, context, source ) {
		var sourceParam;
		if (source == undefined) {
			sourceParam = "";
		} else {
			sourceParam = '&source='+source;
		}
		var param = 'templateId='+templateId+'&context='+context+sourceParam;
		var instAjax = new Ajax.Request(
			'instantiate.ajaxi',
			{
				method: 'get',
				parameters: param,
				onSuccess: showNewInstance,
				onFailure: function(request) {displayError(request,param);}
			});
	}
function displayError(req, param){
	$("errorDiv").innerHTML = req.responseText + "<br/>AJAX (" + req.status + "): " + param;
	$("faderPane").style.display = 'block';
	$("faderPane").observe('click', observeErrorFaderPane);
	$("errorDiv").style.display = 'block';
//	$("errorDiv").style.top = document.body.clientHeight * .30 + "px";
//	$("errorDiv").style.left = document.body.clientWidth * .30 + "px";
}

function closeErrorDiv(){
	$("errorDiv").style.display = 'none';
	$("errorDiv").innerHTML = "";
	$("faderPane").style.display = 'none';
    $("faderPane").stopObserving('click', observeErrorFaderPane);
}

function observeErrorFaderPane(event){
	closeErrorDiv();
}


	// Once instantiated, we can show the new item
	function showNewInstance(req) {
//		Tolven.Util.log( req.responseText );
		oneSecondShow = req.responseText;
		setTimeout("oneSecond()",200);
	}

	// return true if the specified pane is output date
	function outOfDate( pane ) {
		var serverVersion = pane.getAttribute( "serverVersion");
		if (serverVersion ==null ) return false;
		var ver = $(pane.id+'-ver');
		if (ver==null) return false;
		var localVersion = ver.getAttribute( "localVersion");
		if (localVersion == null) return false;
		if ((1*localVersion) < (1*serverVersion)) return true;
		return false;
	}

	function showPane( paneId, isHistoryAction, originalPaneId ) {
		if(checkDropDown(paneId))
			restoreTabToBar(paneId);
		hideDialog();
		Tolven.Util.hideBubble();
		var pane = $(paneId);
		// Is this pane new? If so, create it now.

		if ( pane==null ) {
			pane = addRemoteContent( paneId, paneId );
		}//else{ // don't need this. Keeping this commented to see possible issues of removing

		 // if( originalPaneId != undefined ){
		 //   var opane = $(originalPaneId );
		 //	if( opane != null ){
			 // pane = opane;
		//	}
		//  }
		//}
		wrapTabsInPage(paneId);
		var targetItem = fixVisibleSubPage( pane );

		if (targetItem.id==visiblePage) return;
		hideContent();
		showContentContainer( targetItem.id, targetItem.id );
		currentMenus( targetItem.id, isHistoryAction );
		visiblePage = targetItem.id;
		if (outOfDate( $(targetItem) )) {
//			Tolven.Util.log( "[showPane] updating: " + visiblePage );
			getRemoteContent( visiblePage, null );
		}
		if(controlpressed) {
			controlpressed = 0;
			showPane(originalPaneId);
		}
	}

	// Optional menu behavior
	function showMenu( e ) {
		var menus = document.getElementsByName( e.name );
		for (var x = 0; x < menus.length; x++) {
			if(menus[x].checked==true) {
				Element.show(menus[x].value);
			} else {
				slowHideElement(menus[x].value);
			}
		}
	}

	function slowHideElement( e ) {
		new Rico.Effect.FadeTo( e,.250,500,10, {complete: slowHideElementDone });
	}

	function slowHideElementDone( f ) {
		Element.hide( f.element );
		new Rico.Effect.FadeTo( f.element,1,0,1);
	}
	function debugShowElementList( pre, e ) {
		var rslt = pre;
		for (var x = 0; x < e.length; x++) {
			rslt = rslt + e[x].id + " (" + e[x].className + ")/";
		}
		alert( rslt );
	}

	// Find all content and container class elements and make sure they are hidden
	function hideContent() {
		var e = $('paneArea').getElementsByTagName('div');
		for (var x = 0; x < e.length; x++) {
			if (e[x].className=="container" && e[x].style.display!='none') {
				e[x].style.display = 'none';
//				Element.hide(e[x]);
				// Also, if we find a selected menu item, unselect it
				var lis = e[x].getElementsByTagName('li');
				for (var y = 0; y < lis.length;y++) {
						lis[y].className = '';
				}
			}
		}
		// Also the top-level tabs should now be active
		var tablis = $('tabArea').getElementsByTagName('li');
		for (var y = 0; y < tablis.length;y++) {
				tablis[y].className = '';
		}
	}

	// Removes the trailing ":xxx" from a path, return what's left. If there is no :, then null is returned.
	function removeLast( path ) {
		if (path==null) return null;
		if (path.length < 2 ) return null;
		var last = path.lastIndexOf(':',path.length-1);
		if (last>0) return path.substring( 0, last );
		return null;
	}

	// Get the level of this item
	function getLevel( id ) {
		var items = id.split(":");
		return items.length-1;
	}

	function refreshAll() {
		alert( "AJAX call failed, refreshing" );
		location.href=document.URL;
	}

	function getRemoteContent( contentName, originalId ) {
		var content = $(contentName);
		var nodes = content.id.split(":");
		var nodeDetail = nodes[nodes.length-1].split("-");
		if (nodeDetail.length==1) nodeDetail[1] = '0';
//		content.innerHTML = 'Awaiting content for ' + contentName;
		serialNo++;
		$('downloadStatus').innerHTML="Get " + contentName + "...";

		var ajax = new Ajax.Updater(
		 content.id,
		 contextPath + '/ajax/paneDispatch.jsf',
		 {   method: 'get',
             evalScripts: true,
             onComplete: function() {newContentArrived( contentName );},
             onFailure: function(request) {displayError(request,contentName);},
			 parameters: 'element='+contentName+
			             '&original='+originalId+
			             '&serialNo='+serialNo +
			             '&accountUserId='+accountUserId});
	}

	// Create a new container or content element and request the actual contents from the server using Ajax.Updater()
	function addRemoteContent( contentName, originalId ) {
		// We'll get it from memory if possible
		var content = $(contentName);
		// if not here get remote
		if (content==null) {
			// Create a new div that will accept the pane
			content = document.createElement( 'div' );
			content.id = contentName;
			content.className = 'container';
			content.style.display = 'block';
			var level = getLevel( contentName );
			if (level==0) {
				$('tabArea').appendChild( $(content) );
			} else {
				$('t_container' + level).appendChild( content );
			}
			getRemoteContent( contentName, originalId );
		}
		return content;
	}

	// When content arrives, find the first non-disabled menu item,
	// if any, and bring that in as well.
	function newContentArrived( id ) {
		var content = $(id);
//		if (id!=visiblePage) {
//			alert( id + " is obsolete");
//			return; // We've moved on
//		}
		$('downloadStatus').innerHTML="Content arrived " + $(content).id;
		if (content.className=='container' && content.getAttribute("visibleSubPage")==null) {
			// Get the menu items
			var uls = content.getElementsByTagName('ul');
			if( uls.length > 0 ){
			  for( var i = 0; i < uls.length; i++)  {
				if( uls[i].getAttribute("defaultSelection") != undefined){
					// Extract the default selection in the next level.
					var defSelect = uls[i].getAttribute("defaultSelection");
					if(defSelect == ""){
					  var lis = uls[i].getElementsByTagName('li');
					  for (var x = 0; x < lis.length; x++) {
   				        if (lis[x].style!=null && lis[x].style.display!='none') {
					      var childId = removeLast(lis[x].id);
						  defSelect = childId.substring(childId.lastIndexOf(":"));
						  break;
						}
			          }
					}

					var defArray = defSelect.split(":");
					if( defArray.length > 2){
					  defSelect = ":" + defArray[1];
					}
					var id = content.id + defSelect;
					visiblePage = id;
					content.setAttribute( "visibleSubPage", id);
					addRemoteContent( id, id );
					// call currentMenus here and return.
					// reason: we need to check if there are any filtered items
					// in case of version check
					// Second argument is true: We dont want these drilldowns added to the history.
					currentMenus( visiblePage, true );
					return;
				}
			  }
			}
		}
		// Double check current menus now that we have more stuff here
		currentMenus( visiblePage );
		// In case of a version check, the display "n filtered items" vannishes,
		// So check perform this task to get the display back.
		if( $(id).getAttribute('filterValue') != null && $(id).getAttribute('filterValue') != "" ) {
		  filterValueChange($(id + "-filter"), $(id).getAttribute('filterValue'), id);
		}
	}

	function setupPaneArea(paneArea) {
		for (var x=0; x < 10;x++) {
			var div = document.createElement('div');
			div.id = 't_container' + x;
			paneArea.appendChild( $(div) );
		}
	}

// removed function isInternetExplorer; using Prototype.Browser.IE instead

	/*
	* Replaces last value in Histroy IFrame(for IE), window's location(both IE and FF) and hash value(for FF)
	*/
	function replaceLastHistory(paneId){
		if( Prototype.Browser.IE ){
	      	$('DhtmlHistoryFrame').src.replace( blankHtmlFile + "?" + paneId );
		}
		var href = window.location.href;
		if( href.indexOf("#") > 0){
			href = href.substr(0, href.indexOf("#")+1);
		}
		href += "H:" + paneId;
	      window.location.replace( href );
		window.location.hash.replace(paneId);
	}

	function setIFrameHash( paneId ){
	  if( Prototype.Browser.IE ){
          $('DhtmlHistoryFrame').src = blankHtmlFile + "?" + paneId;
	  }
        window.location.hash  = paneId;
	}

	function getHashOnLoad(){
	  var href = window.location.href;
	  var hash = "";
	  if( href.indexOf( "#" ) > 0 ) {
		href = href.substr( href.indexOf( "#" ) );
		href = href.replace( "#", "");
		hash = href.replace( "H:", "");
	  }
	  return hash;
	}

	function getIFrameHash() {
      // get the new location
        var historyFrame = document.getElementById("DhtmlHistoryFrame");
        var doc = historyFrame.contentWindow.document;
        var hash = new String(doc.location.search);

        if (hash.length == 1 && hash.charAt(0) == "?")
           hash = "";
        else if (hash.length >= 2 && hash.charAt(0) == "?")
          hash = hash.substring(1);
        return hash;
   	}

	var hLock = false;
	function getLock(){
	  if( hLock == true ) return false;

	  hLock = true;
	  return true;
	}
	function releaseLock(){
	  hLock = false;
	}

 	var currentTab = "";
	var defaultTab = "";
	var currentLocation = "";

	function getHash(){
	 var hashValue = "";
	  if( !Prototype.Browser.IE ){ // for FF
		hashValue = window.location.hash;
		hashValue = hashValue.replace( "#", "");
	  }else{
		hashValue = getIFrameHash();
	  }
        hashValue = hashValue.replace( "H:", "");
	  return hashValue;
	}

	function checkNewLocation(){

	  if( true == getLock() ) {
 	       var hashValue = getHash();
		 if( hashValue != undefined && hashValue != null && hashValue != "" ){
		   if( hashValue != currentLocation ){
			  currentLocation = hashValue;
			  showPane( hashValue, true );
			     setIFrameHash ( "H:" + hashValue );

		   }else if( Prototype.Browser.IE ){
			  hashValue = getHashOnLoad();
			  if( hashValue != currentLocation ){
			     setIFrameHash ( "H:" + hashValue );
			  }
			}
		 }
		releaseLock();
	  }//else Tolven.Util.log( "check new location: busy");
	}

// To detect change in text size;
var currentTextsize = 11;
	function textSizeDetector(){
		sampleText = document.getElementById("textSizeDetector");
		if( sampleText != null ){
			newTextSize = sampleText.offsetHeight;
			if( currentTextsize != newTextSize ){
				hideDialog();
				if(currentTextsize > newTextSize )
				unWrapTabsInPage();
				if(currentTextsize < newTextSize )
				wrapTabsInPage();
				currentTextsize = newTextSize ;
				if( visiblePage.indexOf(":") > 0) getRemoteContent(visiblePage);
			}
		}
		//wrapTabsInPage();
	}

	var blankHtmlFile = "";
	var enableBackButton = false;
	function initHistory( blankFileName ){
		blankHtmlFile = blankFileName;
		enableBackButton = true;
		if( Prototype.Browser.IE ){
		// init IE history
		   	var initialHash = (window.location.hash).replace( "#", "" );
      	   	new Insertion.Bottom("historyDiv", "<iframe style='border: 0px; width: 1px; "
	                               + "height: 1px; position: absolute; bottom: 0px; "
                               + "right: 0px; visibility: visible;' "
                               + "name='DhtmlHistoryFrame' id='DhtmlHistoryFrame' "
                               + "src='" + blankHtmlFile + "?" + initialHash + "'>"
                               + "</iframe>");

		}

		// end init history
	}

var oneSecondHandle = null;

	function initPages(accountUser) {
		// For bookmarks.
		var hash = getHashOnLoad();
		if( "" != getHashOnLoad() ) initialPage = hash;

		if (initialPage!=null && initialPage!='') {//return;
			var paneArea = $('paneArea');
			if (paneArea!=null) {//return;
				if (accountUser!=null) accountUserId = accountUser;
				setupPaneArea(paneArea);
				oneSecondShow = initialPage;
				defaultTab = initialPage;
			}
		}
		oneSecondHandle = new PeriodicalExecuter(oneSecond, 1 );
		new PeriodicalExecuter(textSizeDetector, 1 );
		//new PeriodicalExecuter(wrapTabsInPage, .5 );
		if( true == enableBackButton ){
			new PeriodicalExecuter(checkNewLocation, .4 );
		}

		sampleText = document.getElementById("textSizeDetector");
		if( sampleText != null ) currentTextsize = sampleText.offsetHeight;
	}

oneSecondShow = null;
cvCountdown = 8;
cvNextCheck = 8;
cvLongest = 64;
cvIdle = 8;
cvBlank = 600;
cvLogout = 660;
skipBlankMessage = false;

	function recentSubmit() {
		cvCountdown = 2;
		cvNextCheck = 4;
		cvIdle = 0;
	}

	function recentActivity() {
		if (cvCountdown > 8) {
			cvNextCheck = 16;
			cvCountdown = 8;
		}
		cvIdle = 0;
	}

Event.observe(window, 'load', function() {
	Event.observe('pageContent', 'keypress', recentActivity );
	Event.observe('pageContent', 'mousedown', recentActivity );
});

	function resetCountdown( msg ) {
		if (cvCountdown==0)	cvCountdown = cvNextCheck;
		if (cvNextCheck < cvLongest) cvNextCheck = cvNextCheck*2;
		$('refreshStatus').innerHTML=msg;
	}

	function serverUnAvailable(){

	$('idlePage').hide();
	$('serverUnAvailablePage').show();

	  new PeriodicalExecuter(function(pe) {
	    pe.stop();
			new Ajax.Request('isserveravailable.ajaxc',
			{
				method: 'post',
				onSuccess: serverAvailable
			});
	  }, 20);
	}
	function serverAvailable( req ){
		try{
		if( req.status == 200 ){
			$('serverUnAvailablePage').hide();
			eval($('hform:logout').onclick)();
		}else{
			serverUnAvailable();
		}
		}catch(err){ serverUnAvailable(); }
	}

	function confirmIdle() {

		if (!skipBlankMessage)
		{
			if ($('pageContent').visible()) {
				$('pageContent').hide();
				hideDialog();
				$('idlePage').show();
			}
			document.title = titlePrefix + " session ending in " + (cvLogout - cvIdle) + " seconds";
			$('logoutIn').innerHTML= "" + (cvLogout - cvIdle)  + " " ;
		}
		if (cvIdle >= cvLogout ) {
			oneSecondHandle.stop();

			new Ajax.Request('isserveravailable.ajaxc',
			{
				method: 'post',
				onSuccess: serverAvailable
			});
		}
	}

	function restoreApplication() {
		cvIdle = 0;
		$('pageContent').show();
		$('idlePage').hide();
		currentMenus( visiblePage );
	}

	function oneSecond() {
		if (oneSecondShow!=null) {
			$('refreshStatus').innerHTML="Show pane: " + oneSecondShow ;
			showPane(oneSecondShow);
			oneSecondShow = null;
			return;
		}
		$('visiblePaneStatus').innerHTML = visiblePage;
		cvIdle += 1;
		if (cvCountdown==0) return;
		if (cvIdle > cvBlank ) {
  	 		eval($('hform:logout').onclick)();
// 			confirmIdle();
			return;
		}
		cvCountdown -= 1;
		if (cvCountdown == 0) {
			checkVersions();
			return;
		}
		$('refreshStatus').innerHTML="Update in " + cvCountdown + ", Blank in " + (cvBlank-cvIdle) ;
	}

	function checkVersions ( ) {
		$('refreshStatus').innerHTML="Checking";
		var containers = document.getElementsByClassName( "container" );
	    var elements = new Array();
		for (var x = 0; x < containers.length; x++) {
			var versionDiv = $(containers[x].id+'-ver');
			if (versionDiv!=null) {
//				Tolven.Util.log( "Checking: " + containers[x].id + " ver: " + versionDiv.id );
				if(containers[x].getAttribute('type') == 'timeline') {
					if(timelineObjContainer.isTimelineEmpty(containers[x].id)){
						var painterPaths = timelineObjContainer.getTimelineEventPaintersPaths(containers[x].id);
						if(painterPaths)
						for(var i=0;i<painterPaths.length;i++){
						  elements.push(painterPaths[i]);
						}
					}
					else{
						var eventPainters = timelineObjContainer.getTimelineEventPainters(containers[x].id);
						if(eventPainters)
							for(var i=0;i<eventPainters.length;i++){
								if(eventPainters[i]._path)
							  elements.push( eventPainters[i]._path + "=" + eventPainters[i]._localVersion);
							}
					}
				}
				else if(containers[x].getAttribute('type') == 'calendar'){
					var calendarMenus = Tolven.CalendarCache.get(containers[x].id+":menus");
					if(calendarMenus != null){
						for(i=0;i<calendarMenus.length;i++){
						  elements.push(calendarMenus[i]);
						}
					}
					else
					alert('something wrong with Tolven.CalendarCache!! menus not found for calendar '+containers[x].id);
				}
				else
					elements.push( containers[x].id + "=" + versionDiv.getAttribute( 'localVersion' ));
			}
		}


		// Also get summary items
		var summaryItems = document.getElementsByClassName( "summaryItem" );
		for (var y = 0; y < summaryItems.length; y++) {
			var versionDiv = $(summaryItems[y].id+'-ver');
			if (versionDiv!=null) {
//				Tolven.Util.log( "Checking: " + summaryItems[y].id + " ver: " + versionDiv.id );
				elements.push( summaryItems[y].id + "=" + versionDiv.getAttribute( 'localVersion' ));
			}
		}
		if (elements.length > 0) {
			elements.push( "accountUserId=" + accountUserId);
			new Ajax.Request(
			'versionCheck.ajaxi',
			{
				method: 'post',
				onSuccess: updateVersions,
				onFailure: updateFailed,
	            postBody: elements.join('&')
			});
		} else {
			resetCountdown("done");
		}
	}

	function updateFailed(  ) {
		window.location.replace('/Tolven/loggedOut.jsf');
	}

	function updateVersions( req ) {
		if (req.status==0 || req.getResponseHeader("Content-Type").indexOf("text/html") == 0) {
			window.location.replace('/Tolven/loggedOut.jsf');
			return;
		}
		resetCountdown("done");
		var elements = req.responseText.split(',');
		var calendarsToRefresh = new Map();
		try{
//		Tolven.Util.log( "Server response (" + elements.length + "): " + req.responseText );
		for (var x = 0; x < elements.length; x++ ) {
			var parts = elements[x].split('=');
			
			//check for messages for elements
			var msgElement = $(parts[0]+":message");
			if(msgElement != null){
				var msgVersion = msgElement.getAttribute("version");
				if(msgVersion != null && parseInt(msgVersion) != parseInt(parts[1])){
					msgElement.style.display = 'block';
				}
			}
			
			var element = $(parts[0]);
			if (element!=null) {
				element.setAttribute("serverVersion", parts[1] );
				if (element.className=='summaryItem') {
					getRemoteContent( element.id );
				} else {
					if (element.visible()) {
						var path = element.ancestors();
						var refreshNow = true;
						for (var p = 0; p < path.length; p++) {
							if (!path[p].visible()) {
								refreshNow = false;
								break;
							}
						}
						if (refreshNow) {
							getRemoteContent( element.id );
						}
					}
				}
			}else if(parts[0].indexOf(':timeline') != -1){
				reloadPainterEvents(parts[0],parts[1]);
			}else{
				var _conDiv = $(removeLast(parts[0]));
				if(_conDiv != null && _conDiv.getAttribute("type") == "calendar"){
					var menus = calendarsToRefresh.get(_conDiv.id);
					if(menus == null){
						menus = new Array();
						menus[0] = parts[0]+"="+parts[1];
						calendarsToRefresh.put(_conDiv.id,menus);
					}else{
						menus[menus.length] = parts[0]+"="+parts[1];
					}
				}
			}
		}
		if(calendarsToRefresh.size() > 0)
			refreshCalendars(calendarsToRefresh);
		}catch(e){  alert(" updateVersions "+e);}
	}

	function displayableSibling( id ) {
		var thisNode = $(id + ":sel");
		// 0 - find the default
		var tabContainerUl = getFirstAncestorByTagName( thisNode, "ul","UL");
		var defaultselection = $(tabContainerUl).getAttribute('defaultselection');
		var tabContainerDiv = getFirstAncestorByTagName( thisNode, "div","DIV");
		while(tabContainerDiv != null && tabContainerDiv.className != 'container'){
			tabContainerDiv = getFirstAncestorByTagName( tabContainerDiv, "div","DIV");
		}

		if(tabContainerDiv.className == 'container'){
			if(tabContainerDiv.id.indexOf(":") > -1){
				var elementNode = tabContainerDiv.id.substring(tabContainerDiv.id.lastIndexOf(":")+1);
			}
			else{
				return  tabContainerDiv.id+defaultselection;
			}
		}
		else
			alert('no container');
		// 1- Check for Immediate Previous Sibling.

		var siblings = thisNode.previousSiblings();
		var returnNodeId = null;
		if (siblings!= null)
		{
			for (i = 0; i < siblings.length; i++ )
			{
				adjSibling = siblings[i];
				if (adjSibling.getStyle("display") != 'none')
				{
					returnNodeId = removeLast(adjSibling.id);
					return returnNodeId;
				}
			}
		}

		// 2- Else Check for Immediate Next Sibling
		siblings = thisNode.nextSiblings();

		if (siblings!= null)
		{
			for (i = 0; i < siblings.length; i++ )
			{
				adjSibling = siblings[i];
				if (adjSibling.getStyle("display") != 'none')
				{
					returnNodeId = removeLast(adjSibling.id);
					return returnNodeId;
				}
			}
		}

		// 3- Else Return the Parent.

		returnNodeId = removeLast(id);

		return returnNodeId ;

		/*
		var thisNode = $(id + ":sel");
		var parentNode = thisNode.parentNode;
		var children = parentNode.childNodes;
		for (var w=0; w<children.length; w++) {
			var n = children[w];
	//		Tolven.Util.log( "Checking: " + n.nodeName + " id: " + n.id );
			if (n.nodeName!='#text' && (n.style==null || n.style.display!='none') && n != thisNode) {
				return n;
			}
		}
		*/
		return null;
	}

	// This closes a tab and everything on the page with that tab's id prefix.
	function closeTab( id ) {
		// We first need to find something to show other than what we're removing
		var n ;
		n = getTabFromDropDown(id);
		//Tolven.Util.log("found element in drop down "+n.id);
		if(n== null)
		n = displayableSibling( id );
		visiblePage = "xx";
		var np;
		/*
		if (n!=null) {
			np = removeLast( n.id );
	//		Tolven.Util.log( "Close 1 showing: " + np );
		} else {
			np = removeLast( id );
	//		Tolven.Util.log( "Close 2 showing: " + np );
		}
		*/
		// Now do the delete
		var idcomp = id + ":";
		var removeList = new Array();
		var e = document.body.getElementsByTagName('*');
		for (var x=0; x < e.length;x++) {
			if (idcomp==e[x].id.substr( 0, idcomp.length )) {
	//			Tolven.Util.log( "Count: " + e.length + " Close pass one: " + e[x].id );
				removeList.push( e[x] );
			}
		}
		for (var y=0; y < removeList.length; y++) {
			var element = removeList[y];
			if (element!=null) element.parentNode.removeChild(element);
		}
		Element.remove( id );
		showPane(n);
	}

liveGrids = {};


//menuPath - the original path of the menu for which the grid is created
//id - the html element id on the page
//methodName - javascript method to be called for the click event on the grid items
//methodArgs - javascript method arguments
function createGrid( menuPath,id, methodName, methodArgs,gridType ) {
	if(!id)
		id = menuPath;
	var root = $(id);
	var grid = $(id+'-grid');
	if (root.getAttribute( 'gridOffset' )==null) {
		root.setAttribute( 'gridOffset', grid.getAttribute( 'gridOffset'));
		root.setAttribute( 'gridSortCol', grid.getAttribute( 'gridSortCol'));
		root.setAttribute( 'gridSortDir', grid.getAttribute( 'gridSortDir'));
		root.setAttribute( 'filterValue', "");
		root.setAttribute( 'menuPath', menuPath);

//		root.setAttribute( 'gridPreset', grid.getAttribute( 'gridPreset' ));
	}

	var methodNameValue = methodName;
	var methodArgsValue = methodArgs;

	if (methodName == undefined)
	{
		methodNameValue = "";
	}

	if (methodArgs == undefined)
	{
		methodArgsValue = "";
	}

	liveGrids[id] = new Rico.LiveGrid( id+"-LG",
		1*grid.getAttribute('visibleRows'),
		1*grid.getAttribute('totalRows'),
		'menuData.ajax',
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
			onRefreshComplete: updateSortInfo,
			requestParameters: [{name: 'element', value: menuPath},
								{name: 'filter', value: root.getAttribute('filterValue')},
								{name: 'methodName', value: methodNameValue},
								{name: 'methodArgs', value: methodArgsValue },
								{name: 'gridType', value: gridType }],
			sortAscendImg: '../images/sort_asc.gif',
			sortDescendImg:'../images/sort_desc.gif'
		});

	grid.style.width=($(id+'-LG_header').offsetWidth+19)*2+'px';
	grid.style.width=($(id+'-LG_header').offsetWidth+19)+'px';
	grid.style.border='#999999 solid 1px';
	$(id+"-filter").value=root.getAttribute('filterValue');
	new Form.Element.Observer( $(id+"-filter"), 1, function(element, val) {checkUserInput(element, val, id, methodNameValue, methodArgsValue,menuPath);} );
	if( $(id).getAttribute('filterValue') != null && $(id).getAttribute('filterValue') != "" ) {
	  filterValueChange($(id + "-filter"), $(id).getAttribute('filterValue'), id, methodNameValue, methodArgsValue ,menuPath );
	}

	// Move Focus to Filter textbox.
	var filterBoxId = id + "-filter";
	setFocus(filterBoxId );
	
	//reset the popup grid width after the grid is created
	//this fix is needed only for IE. ok to keep for FF too.
	if($(id).className=="popupgrid"){
		$(id).style.width = $(id+"-grid").getWidth();
	}
}

function cui(element, value, id, methodName, methodArgs,menuPath){
	if( $( id + "-filter").value == value ){
	 filterValueChange(element, value, id ,methodName, methodArgs,menuPath);
	} else $(id).setAttribute('filterValue', $( id + "-filter").value );
}

function checkUserInput(element, value, id , methodName, methodArgs,menuPath){
	$(id).setAttribute('filterValue', value );
	setTimeout( "cui('" + element + "', '" + value + "', '" + id+  "', '" + methodName + "', '" +  methodArgs + "')", 125);
}

function filterValueChange(element, val, id, methodName, methodArgs,menuPath) {
//Tolven.Util.log(val);
	if(!menuPath)
		menuPath = $(id).getAttribute('menuPath');

	var lg = liveGrids[id];
	var requestParams = new Array();
	$(id).setAttribute('filterValue', val );
	lg.setRequestParams( {name: 'element', value: menuPath}, {name: 'filter', value: val}, {name: 'methodName', value: methodName}, {name: 'methodArgs', value: methodArgs} );
//	lg.resetContents();
//	lg.requestContentRefresh(0);
	if (val) {
		var countMDAjax = new Ajax.Request(
			'countMenuData.ajaxi',
			{
				method: 'get',
				parameters: 'element='+menuPath+'&filter='+val,
				onSuccess: function(req) {countMDComplete( id, req );}
			});
	} else {
		$(id+"-foot").innerHTML = "" ;
		var grid = $(id+'-grid');
		lg.setTotalRows( 1*grid.getAttribute('totalRows') );
		lg.requestContentRefresh(0);
	}
}

function countMDComplete( rootId, request ) {
//	Tolven.Util.log( rootId + " " + request.responseText );
	$(rootId+"-foot").innerHTML = " / " + request.responseText + " filtered items" ;
	var lg = liveGrids[rootId];
	lg.setTotalRows( 1*request.responseText );
	lg.requestContentRefresh(0);
}

function updateOffset( liveGrid, offset ) {
	var root = $(liveGrid.metaData.options.rootId);
	root.setAttribute( 'gridOffset', offset );
	$('listStatus').innerHTML = root.id + " scrolled to " + offset;
}

function doTruncate( root ){
	var _ths = root.getElementsByTagName("th");
//Tolven.Util.log( _ths[ _ths.length - 1].offsetWidth + " , " + _tds[ _tds.length - 1].offsetWidth);
	var trs = root.getElementsByTagName("tr");

	if( trs.length > 2){
	 for( var i = 1; i < trs.length; i++){
 	  if(trs[ i ].offsetHeight > currentTextsize * 2){
	   var tds = trs[ i ].getElementsByTagName("td");
	   for( var j = 0; j < tds.length; j++){

	     var td_Text = tds[ j ].innerHTML;
	     var td_original_content = "";
	     var isHREF = false;
	     if( td_Text.indexOf("<a") > 0){
		isHREF = true;
		var startIndex = td_Text.indexOf(">") + 1;
		var endIndex = td_Text.indexOf("</", startIndex );
		if( endIndex <= startIndex) td_Text = "";
		else
		td_Text = td_Text.substring( startIndex, endIndex );
		td_original_content = td_Text;
	     }

	    $('testFontSize').innerHTML = td_Text;
		var width_testFontSize = $('testFontSize').offsetWidth;
		var width_ths = _ths[ j ].offsetWidth;
	    if( width_testFontSize > width_ths ){
		if( td_Text.length == 0) break;
	     var charWidth = width_testFontSize / td_Text.length;
	     if( charWidth == 0) break;
		var allowedChars = Math.round( width_ths / charWidth );
	     if(allowedChars > 4) allowedChars -= 4;
	     td_Text = td_Text.substring(0, allowedChars) + "...";
	     if(isHREF == true){
		var innerhtml = tds[ j ].innerHTML;
		innerhtml = innerhtml.replace(td_original_content, td_Text);
		td_Text = innerhtml;
	     }
	     tds[ j ].innerHTML = td_Text;
	    }
	   }
	  }
	 }
//	Tolven.Util.log(trs[1].offsetHeight > currentTextsize * 2);
	}
}

function doSubstr(root){
	var _ths = root.getElementsByTagName("th");
	var headerRowWidth = {};
	for( var i = 0; i < _ths.length; i++){
	   headerRowWidth[ i ] = _ths[i].offsetWidth;
//		Tolven.Util.log(headerRowWidth[ i ]);
	}
	var testChars = "AAAAAAAAAA";
      $('testFontSize').innerHTML = testChars;
	var charWidth = Math.round( $('testFontSize').offsetWidth / testChars.length );

	var trs = root.getElementsByTagName("tr");

	if( trs.length > 2){
	 for( var i = 1; i < trs.length; i++){
 	  if(trs[ i ].offsetHeight > currentTextsize * 1.5){
	   var tds = trs[ i ].getElementsByTagName("td");
	   for( var j = 0; j < tds.length; j++){
	    var td_text = tds[ j ].innerHTML;
	    var allowedChars = Math.round ( headerRowWidth[ j ] / charWidth );

//	    Tolven.Util.log( td_text.length + " , " + allowedChars );

	     if( td_text.indexOf("<a") >= 0 || td_text.indexOf("<A") >= 0 ) {
	     	var td_original_content = "";
		var startIndex = td_text.indexOf(">") + 1;
		var endIndex = td_text.indexOf("</", startIndex );
		if( endIndex <= startIndex) td_text = "";
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
		 tds[ j ].innerHTML = td_text.substring(0, allowedChars ) + " ...";
	     }
	    }
	   if( trs[ i ].offsetHeight < currentTextsize * 1.5 ) break;
	   }
	  }
	 }
	}
}

function updateSortInfo( liveGrid ) {
	var sortCol = liveGrid.sort.getSortedColumnIndex();
	var root = $(liveGrid.metaData.options.rootId);

	if (sortCol >= 0) {
		var sortColName = liveGrid.sort.options.columns[sortCol].name;
		var sortDir = liveGrid.sort.options.columns[sortCol].getSortDirection();
		root.setAttribute( 'gridSortCol', sortColName );
		root.setAttribute( 'gridSortDir', sortDir );
	}

//	doTruncate(root);
	doSubstr(root);

	// Move Focus to Filter textbox.
	var filterBoxId = root.id + "-filter";
	setFocus(filterBoxId );
}

// edit: July 31, 2007
function setCvNextCheck( cvNC ){
	if( !isNaN(cvNC) ){
		cvNextCheck = cvNC;
	}
}
function setCvLongest( cvL ){
	if( !isNaN(cvL) ){
		cvLongest = cvL;
	}
}
function setCvBlank( cvB ){
	if( !isNaN(cvB) ){
		cvBlank = cvB;
	}
}
function setCvLogout( cvLO ){
	if( !isNaN(cvLO) ){
		cvLogout = cvLO;
	}
}

function skipBlankOutMsg(skipBlankOut)
{
	if (skipBlankOut != null && skipBlankOut == "true")
	{
		skipBlankMessage = true;
	}
}

function toggleVisibility( elem ){
	if( elem.className == "maxbox"){
	  elem.className = "minbox";
	  $(elem).childElements()[0].replace("<img src='../images/min_blue.gif' style='margin-bottom: 3px;' border='0' height='100%'/>");
	}else{
	  elem.className = "maxbox";
	  $(elem).childElements()[0].replace("<img src='../images/max_blue.gif' style='margin-bottom: 3px;' border='0' height='100%'/>");
	}
}

function getPreferencesMenuItems( element, role ){
	var userPrefFunction = function(){
		getPreferencesMenuItemsFromServer(element, role);
	};
	DynaLoad.downloadAndCallScript(userPrefFunction, undefined, 'USERPREF');
}

//callback function
function savedSummaryPreferences(req, dorefresh ){
//	Tolven.Util.log( "update complete now refresh"  );
	if( dorefresh == true ){
		Tolven.Util.windowrefresh();
	}
}
// Call to save the changes made to the summary page.
//todo: check if summaryorder is empty.
function saveSummaryOrder( summaryId ){
	setSummaryPreferences( $(summaryId).getAttribute("summaryorder") );
}

function setSummaryPreferences( summaryorder, dorefresh, callbackFn, params ){
	if( summaryorder.length < 1 ) alert( "Error updating server. Please refresh: " + summaryorder);

	var instAjax = new Ajax.Request(
			'setuserpreferencesforsummary.ajaxc',
			{
				method: 'post',
				parameters: 'summaryorder='+summaryorder,
				onSuccess: function(req){
					savedSummaryPreferences( req, dorefresh);
					if( undefined != callbackFn){
						callbackFn(req, params);
					}
				}
			});
}

var summaryOrderCache = "";

// returns an array. Each element in the array contains the list of elements inside each summary column arranged in the new order.
function getSummaryBuffers( summaryBoxId ){
 	var summaryBuffers = new Array( 3 );
	summaryBuffers[0] = Sortable.serialize(summaryBoxId + "_1" );
 	summaryBuffers[1] = Sortable.serialize(summaryBoxId + "_2" );
 	summaryBuffers[2] = Sortable.serialize(summaryBoxId + "_3" );
	// Remove escape characters
	for( var i = 0; i < 3; i++){
	  var sb = summaryBuffers[ i ];
	  while( sb.indexOf( "%3A") > 0 ) sb = sb.replace( /%3A/, ":");
	  summaryBuffers[ i ] = sb;
	}
	return summaryBuffers;
}

// This is called whenever the user modifies the sequence in the summary page.
// If it detects any change in the order, the function saves the change in "ul"'s attribute.
// Later the user can save the changes by clicking on the save button/link.
function deserializeSummaryOrder( summaryBoxId ){
 	var summaryBuffers = getSummaryBuffers( summaryBoxId );
	var summaryBuffer = summaryBuffers[0] + "&" + summaryBuffers[1] + "&" + summaryBuffers[2];

// to avoid duplicates .. This function is being called twice when a div is moved from one col to another.
	if( summaryBuffer == summaryOrderCache ) { return;}
	summaryOrderCache = summaryBuffer;

	var attributeBuffer = "";
	var newItemIds = new Array();
	var idNum = 0;
// Scan through all 3 columns and get the portlets in the new order.
	for( var j = 0; j < 3; j++){
	  var liList = $(summaryBoxId + "_" + ( j + 1) ).childElements();
	  var divList = new Array();
	  var dindex = 0;
	  var spanElem;

	    for(var ll = 0; ll < liList.length; ll++){
		if( liList[ ll ].tagName == "div" || liList[ ll ].tagName == "DIV" ){
		  divList[ dindex++ ] = liList[ ll ];
		}else if( liList[ ll ].tagName == "span" || liList[ ll ].tagName == "SPAN" ){
		  spanElem = liList[ ll ];
		}
	    }
	  if(divList.length <= 0 ){
		spanElem.className = "emptyspan";
	  }else{
		spanElem.className = "";
	  }

	  var s_Array = summaryBuffers[ j ].split("&");

	/* Check the summary buffers for change in sequence. If there is a change, then add it to the attributeBuffer;
	**AttributeBuffer contains the parameters that is passed to the backend. It has the path, and the property that has to be changed.
	** c: for column number, s: for sequence number. "s" HAS to follow "c".
	** v: for visibility.
	** the paths are seperated by ";"
	*/
	  for(var l = 0; l < divList.length; l++){
	     var oldOrderNum = s_Array[ l ].substr( s_Array[ l ].indexOf("[]=") + 3 ); //
	     oldOrderNum = parseInt( oldOrderNum );
	     if( oldOrderNum != l ){
	       var divId = divList[ l ].getElementsByTagName("div")[0].id;
	       divId = divId.replace( divId.substring(divId.indexOf("-"), divId.indexOf(":summary")), "" );
		   if( attributeBuffer != "" ) attributeBuffer += ";";
		   attributeBuffer += divId + "," + "c_" + ( j + 1 ) + "," + "s_" + ( l + 1 ) ;

		// renaming Ids to match the new order;
		// remove the number from item_(x) eg: item_1; and replace it with the new number
		var itemId = divList[ l ].id;
		itemId = itemId.substring(0, itemId.indexOf( "_") + 1);
		divList[ l ].id = itemId + l;
	     }
	  }
	}
	setSummaryPreferences(attributeBuffer);

  // update cache since we have renamed the summary box ids.
  summaryBuffers = getSummaryBuffers( summaryBoxId );
  summaryOrderCache = summaryBuffers[0] + "&" + summaryBuffers[1] + "&" + summaryBuffers[2];
}

// removed unused function disableSummaryReorg( summaryId )
// removed unused function toggleSummaryReorg

// This function enables drag drop for a patient's summary page.
function enableSummaryReorg( summaryBoxId){
	setTimeout("createSortable("+"'"+summaryBoxId+"'"+", '_1')",500);
	setTimeout("createSortable("+"'"+summaryBoxId+"'"+", '_2')",500);
	setTimeout("createSortable("+"'"+summaryBoxId+"'"+", '_3')",500);
}

function createSortable(summaryBoxId, elementNo){
	var createSortableFunction = function(){
		Sortable.create
		(
				summaryBoxId + elementNo,
				{
					tag:'div',
					constraint: false,
					containment:[summaryBoxId+'_1',summaryBoxId+'_2',summaryBoxId+'_3' ],
					handle:'portletHeader',
					dropOnEmpty:true,
					hoverclass:'summaryhoverborder',
					onUpdate: function()
					{
					deserializeSummaryOrder(summaryBoxId);
					}
				}
		);
	};
	DynaLoad.downloadAndCallScript(createSortableFunction, undefined, 'DRAGDROP');
}

//summaryPath= Node Path for the summary
//summaryBoxId = has id of the form: "summary-box-" + id of the summary page + end path of summary (like resultsum)
function removeSummaryBox( summaryPath, summaryBoxId ){
//	populate the summary box's attribute with the settings.
	var summaryBox = summaryBoxId.substring(0, summaryBoxId.lastIndexOf( ":"));
	var attributeBuffer = ""; //= $(summaryBox).getAttribute( "summaryorder");
	if( attributeBuffer == undefined || attributeBuffer == null ) attributeBuffer = "";
	if( attributeBuffer != "" ) attributeBuffer += ";"
	var attrStr = summaryPath + ",v_false";

//	remove the box
	var liId = summaryBoxId.replace("summary-box-", "");
	$( liId ).parentNode.style.display = 'none';

	var undoStr = summaryBoxId.replace("summary-box-", "");
	undoStr += ",v_false";
	$(summaryBox).setAttribute("undobuffer", undoStr);

//	Invoke Ajax call;
	var params = new Array(summaryPath, liId, summaryBox);
	setSummaryPreferences( attrStr, false, writeUndoTextFromServer, params );
}

function writeUndoTextFromServer(re, params){
	var xmlDoc = re.responseXML;
	var undotxt  = Tolven.Util.getXMLContent(xmlDoc.getElementsByTagName("undo")[0].childNodes[0]);
	// keep the order.
	writeUndoText(params[0], params[1], params[2], undotxt);
}

function showHideSummaryBox( boxId, elementPath ){
	var table = $(boxId).getElementsByTagName("table");

	if(table[0].className == "hidedataTableBody"){
		table[0].className = "";
		var summarysetting = elementPath + ",w_true";
	}else{
		table[0].className = "hidedataTableBody";
		var summarysetting = elementPath + ",w_false";
	}
	setSummaryPreferences( summarysetting );

	// Save settings
	var divChildren = $(boxId).immediateDescendants();
      if( divChildren != undefined ){
	  for( var i = 0; i < divChildren.length; i++ ){
		  if(divChildren[ i ].tagName == "a" || divChildren[ i ].tagName == "span" || divChildren[ i ].tagName == "A" || divChildren[ i ].tagName == "SPAN"){
		    Element.toggle(divChildren[ i ]);
		  }
	  }
	}
}

// path eg: echr:patient:summary:resultsum
// summaryBox: eg: summary-box-echr:patient-xxxxxxx:summary
//summaryBoxId =
function writeUndoText(path, summaryBox, summaryBoxId, displayText ){

	var undobox = summaryBoxId.replace("summary-box-", "undo-");
	var txt= "<a href=\"javascript:return false;\" ";
	txt += "onclick=\"javascript:undoPrevAction(this, '" + path + "' ,'" + summaryBox + "', '" + summaryBoxId + "');return false;\" style=\"text-decoration:none;\">";
	txt += displayText;
	txt += "</a> |"
	$(undobox).className = "showundo";
	$(undobox).innerHTML = txt;
}

function removeUndoText( summaryBox ){
	var undobox = summaryBox.replace("summary-box-", "undo-");
	$(undobox).className = "hideundo";
	$(undobox).innerHTML = "";
}

function observeFaderPane(event){
  closePrefDiv();
}

function enableSortable(){
	Position.includeScrollOffsets = true;
	var createSortableFunction = function(){
		Sortable.create('thelist2');
	};
	DynaLoad.downloadAndCallScript(createSortableFunction, undefined, 'DRAGDROP');

}
function disableSortable(){
 Sortable.destroy( 'thelist2' );
}

function closePrefDiv(){
	try{
		disableSortable();
	}catch(err){}
	$("userprefDiv").style.display = 'none';
	$("userprefDiv").innerHTML = "";
	$("faderPane").style.display = 'none';
	$("faderPane").stopObserving('click', observeFaderPane);
}

// It actually makes the previously closed summary tab, visible
/* elem: the calling anchor's this object
node: path of the summary box (to change setting on the server);
summarBoxid: Id of the summary box in order to display inline.
*/
function undoPrevAction( elem, node, summaryBoxid, summaryBoxId){

	setSummaryPreferences(node + ",v_true;");
	$( summaryBoxid ).parentNode.style.display = 'block';

	elem.parentNode.className="hideundo";
	elem.parentNode.innerHTML = "";

	// clear out undobuffer
	$(summaryBoxId).setAttribute("undobuffer", "");
}

function voidFn(){
 return false;
}

function getSummaryPreferences( element, role, timelineId){
	var summaryPrefFunction = function(){
		getSummaryPreferencesFromServer(element, role, timelineId);
	};
	DynaLoad.downloadAndCallScript(summaryPrefFunction, undefined, 'USERPREF');
}

function displayDatasets(command, path, element){
	var instAjax = new Ajax.Request(
		'menueventhandler.ajaxc',
		{
			method: 'get',
			parameters: 'command=' + command + '&path='+ encodeURIComponent(path) + '&element=' + encodeURIComponent(element),
			onSuccess: displaydatasets
		});
}

function displaydatasets(req) {
	writeDataSetsInnerHTML(req.responseText);
	$("graphDiv").style.display = 'block';
	$("graphDiv").style.top = document.body.clientHeight * .30 + "px";
	$("graphDiv").style.left = document.body.clientWidth * .30 + "px";
}

function writeDataSetsInnerHTML(reqHTML) {
	$("graphDiv").innerHTML = reqHTML;
	$("faderPane").style.display = 'block';
	$("faderPane").observe('click', observeGraphFaderPane);
}

function displayGraph(command, path, element) {
	var inputs = $("checkboxinputs").getElementsByTagName("input");
	var datasetunits = "";
	for(var i = 0; i < inputs.length; i++) {
		if(inputs[i].type == "checkbox" && inputs[i].checked) {
			datasetunits += inputs[i].value + ',';
		}
	}
	closeGraphDiv();
	var instAjax = new Ajax.Request(
		'menueventhandler.ajaxc',
		{
			method: 'get',
			parameters: 'command=' + command + '&path='+encodeURIComponent(path)+'&element=' + encodeURIComponent(element)+'&datasetunits=' + encodeURIComponent(datasetunits),
			onSuccess: displaygraph
		});
}

function displaygraph(req){
	writeGraphInnerHTML(req.responseText);
	$("graphDiv").style.display = 'block';
	$("graphDiv").style.top = document.body.clientHeight * .30 + "px";
	$("graphDiv").style.left = document.body.clientWidth * .30 + "px";
}

function writeGraphInnerHTML(reqHTML){
	$("graphDiv").innerHTML = reqHTML;
	$("faderPane").style.display = 'block';
	$("faderPane").observe('click', observeGraphFaderPane);
}

function closeGraphDiv(){
	$("graphDiv").style.display = 'none';
	$("graphDiv").innerHTML = "";
	$("faderPane").style.display = 'none';
    $("faderPane").stopObserving('click', observeGraphFaderPane);
}

function observeGraphFaderPane(event){
	closeGraphDiv();
}

function saveAddMoreSummaries(){
  var lis = $('thelist2').getElementsByTagName("li");
  var userMenuList = "";
  for( var ii = 0; ii < lis.length; ii++ ){
	var cboxs = lis[ii].getElementsByTagName("input");
	var seq = cboxs[ 0 ].getAttribute("sequence");
	userMenuList += cboxs[0].value + ",v_" + cboxs[0].checked + "," + seq + ";";
  }
	setSummaryPreferences( userMenuList, true );
	closePrefDiv();
}

function openOptionBox( obj, element, numItems ){
// First check if the option box is already open. if so, close and return;
	try{
	   var parentTHEAD = getFirstAncestorById( obj, "header", "HEADER" );
	   if( parentTHEAD != null){
	    var closebox = parentTHEAD.getElementsByTagName("input");
	    for( var i = 0; i < closebox.length; i++){
		 if( closebox[ i ].value == "Cancel" ){
		   closebox[i].click();
		   return;
		 }
	    }
	   }
	}catch(err){alert(err.description);}

// Now open option box;

   var parentDiv = getFirstAncestorByTagName( obj, "DIV", "div" );

   var isopen = parentDiv.getAttribute( "isopen" );
   if( isopen == "true" ) return;

   var optionrow = document.createElement( 'div' );
   optionrow.style.className = 'summaryoptions';

   var ih = "Show ";
   ih += "<select element=\"" + element + "\">";
   for( var num = 1; num < 10; num++ ){
	ih += "<option value=\"" + num + "\">" + num + "</option>";
   }
   ih += "</select>" + " items ";

   ih += "<input type=\"submit\" value=\"Save\" />";
   ih += "<input type=\"button\" onclick=\"closeOptions(this);\" value=\"Cancel\"/>";

   var frm = "<form onsubmit=\"return saveOptions(this);\" >" + ih + "</form>";
   optionrow.innerHTML = frm;
   parentDiv.appendChild( optionrow );

   var selectbox = optionrow.getElementsByTagName( "select" );
   if( !isNaN( numItems ) && selectbox.length > 0 ){
	   selectbox[0].options[ numItems - 1 ].selected = true;
   }

//	Tolven.Util.log( anc.length);
  toggleDisplayOptionsRow( obj );
}

function getFirstAncestorByTagName(target, tagLowerCase, tagUpperCase) {
    var parent = target;
    while (parent = parent.parentNode) {
        if (parent.tagName == tagLowerCase || parent.tagName == tagUpperCase ) {
			if(parent.id == '_drpDwn'){
				original = $(parent.name);
			  original.innerHTML = parent.innerHTML;
			  parent.innerHTML = "";
				return original;
			}
            return parent;
        }
    }
    return null;
}
function getFirstAncestorById(target, IdLowerCase, IdUpperCase) {
    var parent = target;
    while (parent = parent.parentNode) {
        if (parent.id == IdLowerCase || parent.id == IdUpperCase ) {
            return parent;
        }
    }
    return null;
}

function toggleDisplayOptionsRow( elem ){
   var anc = getFirstAncestorById( elem, "header", "HEADER" );
   var isopen = anc.getAttribute( "isopen" );
   if( isopen == "true" ){
	anc.setAttribute( "isopen", "false");
   }else{
	anc.setAttribute( "isopen", "true");
   }
}

function saveOptions( elem ){
	var selectbox = elem.getElementsByTagName("select")[0];
	var elementPath = selectbox.getAttribute( "element" );
	var param = elementPath + ",n_" + selectbox.options[ selectbox.selectedIndex].value;

	setSummaryPreferences( param, true );
	closeOptions( elem );
	return false;
}
function closeOptions( elem ){
	toggleDisplayOptionsRow( elem );
     	var targetRow = getFirstAncestorByTagName( elem, "div", "DIV" );
     	var parentTHEAD = getFirstAncestorById( elem, "header", "HEADER" );
	if( parentTHEAD != null && targetRow != null ) parentTHEAD.removeChild( targetRow );
}
function showActionOptions(placeholderid,dropDownID,context){
	var menuActionActionPath;
	if(dropDownID.indexOf("_drpDwn") != -1)
		menuActionActionPath = dropDownID.substring(0,dropDownID.indexOf("_drpDwn"));
	else
		menuActionActionPath = dropDownID;
	serialNo++;
	if(!context)
		context = "";
	var instAjax = new Ajax.Request(
	contextPath+'/ajax/instantiateGrid.ajaxi', {
		method: 'get',
		parameters: 'menuPath='+menuActionActionPath+
				 '&context='+context+
				 '&serialNo='+serialNo +
				 '&accountUserId='+accountUserId,
		onSuccess: function(req){gotActionOptions(req,placeholderid,dropDownID);},
		onFailure: function(req) {displayError(req,menuActionId);}
	});
}
function gotActionOptions(req,placeholderid,dropDownID){
	try{
	$(dropDownID).innerHTML = req.responseText;
	var type;
	var xmlDoc = req.responseXML;
	if( Prototype.Browser.IE ){
		type = xmlDoc.getElementsByTagName("response")[0].childNodes[0].text;
	}else{
		type = xmlDoc.getElementsByTagName("response")[0].childNodes[0].textContent;
	}
	//FF
	if(type == "wizard"){
		//instantiate the wizard
		oneSecondShow = $(dropDownID).getElementsByTagName("span")[0].innerHTML;
		setTimeout("oneSecond()",200);
	}else if(type == "grid"){
		//show the grid
		var placeholderid = placeholderid.substring(0,placeholderid.indexOf('_dropdown_loc'));
		var prefHTML = "";
		prefHTML += "<div class=\"popupgridheader\">";
		prefHTML += "<img class='closetab' src='../images/x_black.gif' onclick=\"javascript:hideElement('"+placeholderid+"')\"/>&nbsp;" ;
		prefHTML += "</div>";
		prefHTML += $(dropDownID).getElementsByTagName("span")[0].innerHTML;
		$(placeholderid).update(prefHTML);
		$(placeholderid).style.display='block';
		$(placeholderid).style.top = document.body.clientHeight * .30 + "px";
		$(placeholderid).style.left = document.body.clientWidth * .30 + "px";
		$($(placeholderid).getElementsBySelector("tr[class='odd'] td","tr[class='even'] td")).each(function(item) {
			item.observe('click',function(){hideElement(placeholderid);});
		});
	}
	}catch(e){
		alert(e);
	}
}
function toggleDrpDwn(placeholderid,dropDownID,returnDialog){
	dialog = $("_drpDwn");
	var show = false;
	drpDwnElement = $(dropDownID);
	if(drpDwnElement == null)
		return;
	place_drpDwn = $(placeholderid);
	var pos = Position.cumulativeOffset(place_drpDwn);
	lefx = pos[0] + "px";//+ place_drpDwn.parentNode.offsetWidth - dialog.offsetWidth)+"px";
	topy = pos[1]+"px";;
	dialog.style.left = lefx;
	dialog.style.top = topy;
	if(dialog.name != drpDwnElement.id)
		show = true;
	updateDialogContent(drpDwnElement);
	if(show)
		dialog.show();
	else
		dialog.toggle();
	if(dialog.visible()){
		dialogOffsetRt = dialog.offsetLeft+ dialog.offsetWidth;
		paneComp = $("paneArea");
		paneOffsetRt = paneComp.offsetLeft+  paneComp.offsetWidth;
		if(dialogOffsetRt > paneOffsetRt ){
			dialog.style.left = paneOffsetRt - dialog.offsetWidth + "px";
		}
	}
	if(returnDialog)
		return dialog;
}
function updateDialogContent(drpDwnElement){
   dialog.innerHTML = drpDwnElement.innerHTML;
   dialog.name = drpDwnElement.id;
}
function hideDialog(){
	$("_drpDwn").hide();
}
function wrapTabsInPage(tabId){
 if(tabId == undefined){
	 //Tolven.Util.log("wrapping all ");
    adjustableElements = findAdjustableElements();
   for(var i=0; i<adjustableElements.length;i++){
	 wrapTabsInElement(adjustableElements[i]);
    }
    //Tolven.Util.log('wrap all');
  }else{
	setTimeout("wrapTabsInCurrentPage()",150);
	//Tolven.Util.log('wrap current element');
	}
}
function wrapTabsInCurrentPage(){
   var whichStr = "tabs";
 //starttime = new Date().getTime();
	wrapTabsInElement($("tabs"));
	current_bar1 = $(getSubPath(currentTab, 2)+"_bar1");
	if(current_bar1 != null){
		wrapTabsInElement(current_bar1);
		whichStr +=" "+current_bar1.id;
	}
	// wrap WIP
	current_wip = $(getSubPath(currentTab)+"_wip");
	if(current_wip != null){
		wrapTabsInElement(current_wip);
		whichStr +=" "+current_wip.id;
	}

	current_bar2 = $(getSubPath(currentTab,3)+"_bar2");
	if(current_bar2 != null){
		wrapTabsInElement(current_bar2);
		whichStr +=" "+current_bar2.id;
	}
	current_bar3 = $(getSubPath(currentTab, 4)+"_bar3");
	if(current_bar3 != null){
		wrapTabsInElement(current_bar3);
		whichStr +=" "+current_bar3.id;
	}
//	endtime = new Date().getTime();

// Tolven.Util.log("wrapped "+whichStr+" in "+ (endtime - starttime)+ "ms");

}

function getSubPath(path,level){
	postin = 0;
	i = 0;
	while(i<level & postin!= -1){
	 postin = path.indexOf(":", postin+1);
	 i++;
	}
	if(postin != -1){
		return path.substring( 0, postin );
	}else{
		return path;
	}
}
function wrapTabsInElement(divElement,placeholder){

	if(divElement == null)
		return;
	if(placeholder == undefined){
		placeholder = getFirstVisibleChild(divElement,"li");
	}
	if(placeholder == undefined){
		return;
	}
	var nxtItems = placeholder.nextSiblings();
	for(var i=nxtItems.length-1; i>0;i--){
		if(placeholder.offsetTop < nxtItems[i].offsetTop){
			if(nxtItems[i].tagName.toLowerCase()!= 'a'){
				if(nxtItems[i].id.indexOf("showDrpDwn") > 0){
					nxtItems[i] = nxtItems[--i]; // remove previous item if the item is More[V]
				}
				divElement.firstDescendant().removeChild(nxtItems[i]);
				addtoDropDown(divElement.id,nxtItems[i]);
			}// != 'a'
		}else{
			if(nxtItems[i].visible())
			break;
		}
	}
	if($(divElement.id+"_showDrpDwn") && placeholder.offsetTop < $(divElement.id+"_showDrpDwn").offsetTop)
	 addtoDropDown(divElement.id,nxtItems[i]);
}
function unWrapTabsInPage(){
	adjustableElements = findAdjustableElements();
	for(var i=0; i<adjustableElements.length;i++){
		unWrapTabsInElement(adjustableElements[i]);
	}
}
function unWrapTabsInElement(divElement,placeholder){
//Tolven.Util.log('unwrap ');
	if(placeholder == undefined)
		placeholder = getFirstVisibleChild(divElement,"li");
	if(placeholder == undefined)
		return;
   	dropDown =  $(divElement.id+"_drpDwn");
	if(dropDown == null)
		return;
	var nxtItems = dropDown.firstDescendant().childElements();
	 showDropDownElm = $(divElement.id+"_showDrpDwn")
		  for(var i=0; i<nxtItems.length;i++){
		  tabToRestore = nxtItems[i];
		  if(tabToRestore.id.length <1)
			  continue;
		  divElement.firstDescendant().insertBefore(tabToRestore,divElement.firstDescendant().childElements().last());

		  if(placeholder.offsetTop < showDropDownElm.offsetTop){
			divElement.firstDescendant().removeChild(tabToRestore);
			//dropDown.firstDescendant().appendChild(tabToRestore);  // check this
			addtoDropDown(divElement.id,tabToRestore);
			break;
		  }
	  }
	  nxtItems = dropDown.firstDescendant().childElements();
	  if(nxtItems.length == 0)
	  showDropDownElm.firstDescendant().hide();
}

function findAdjustableElements(){
	return $("pageContent").getElementsBySelector('div[enableOverFlowListener="true"]');
}
function getFirstVisibleChild(parent,tagName){
	elements = parent.firstDescendant().getElementsBySelector(tagName);
	var visibleChild;
	for(var i=0;i<elements.length;i++){
		if(elements[i].visible()){
			visibleChild = elements[i];
			break;
		}
	}
	return visibleChild;
}
function checkDropDown(tabId){
	//Tolven.Util.log(tabId);
	if(getLevel(tabId)==1)
		tabId+=":sel";
	else
		tabId=removeLast(tabId)+ ":sel";
	tab = $(tabId);
	if(tab == null)
		return false;
   dropDown = getFirstAncestorByTagName( tab, "div", "DIV" );
   if(dropDown == null)
	   return false;
   if(dropDown.id.indexOf("_drpDwn")!= -1){
	   return true;
   }
   else
	return false;
}
function restoreTabToBar(id){
	if(getLevel(id)==1)
		tabId=id+":sel";
	else
		tabId=removeLast(id)+ ":sel";
 var dropDownElement  = getFirstAncestorByTagName( $(tabId), "div", "DIV" );
 bar = dropDownElement.id.substring(0,dropDownElement.id.length-7);
 barElement = $(bar);
 menuItems = barElement.firstDescendant().childElements();
 placeholder = menuItems[menuItems.length-2];
 barElement.firstDescendant().insertBefore($(tabId),placeholder);
 hideDialog();
}

function addtoDropDown(divElementId,tabElement){
	var showDrpDwnElm = $(divElementId+"_showDrpDwn");
	if(!showDrpDwnElm.firstDescendant().visible()){
		showDrpDwnElm.firstDescendant().show();
	}

	var dropDown = $(divElementId+"_drpDwn");
	ulItem = dropDown.firstDescendant();
	ulItem.insertBefore(tabElement,ulItem.firstDescendant());
}
function removeFromDropDown(dropDownElement,tab){
 return  dropDownElement.firstDescendant().removeChild(tab);
}
function getTabFromDropDown(id){
	id = id+":sel";
	 tabParent = getFirstAncestorByTagName( $(id), "div", "DIV" );
   if(tabParent == null || tabParent.id.length == 0)
	   return null;
   dropDown = $(tabParent.id+"_drpDwn");
    if(dropDown == null)
	   return null;
   var tabElements = dropDown.firstDescendant().childElements();
   if(tabElements.length == 0)
	   return null;
   tab = tabElements[0];
   if(tab.id.length < 1)
	   return null;
	hideDialog();
    removeFromDropDown(dropDown,tab);
   return tab;
}
function replaceSpecialChars(strValue){
	strValue = strValue.replace("&lt;", "<");
	strValue= strValue.replace("&gt;", ">");
	strValue = strValue.replace("&amp;", "&");
	strValue = strValue.replace("&quot;", '"');
	strValue = strValue.replace("&#039;", "'");
	strValue = strValue.replace("&#040;", "(");
	strValue = strValue.replace("&#041;", ")");
	strValue = strValue.replace("&#035;", "#");
	strValue = strValue.replace("&#037;", "%");
	strValue = strValue.replace("&#059;", ";");
	strValue = strValue.replace("&#043;", "+");
	strValue = strValue.replace("&#045;", "-");
	return strValue;
}
function createElement(tagName){
	return document.createElement(tagName);
}

/*
		UTIL Methods
*/

Tolven.Util = {
/* While extracting the contents of the xml document the the nodeValue of any element node is always null.
* Serialize the contents of the content tag to a string to get the inner text.
*/
getXMLContent: function (xmlNode) {
	if ( window.XMLSerializer ) {
		// FF
		return new XMLSerializer().serializeToString(xmlNode);
	}
	// IE
	return xmlNode.xml;
},
//Moved to tolven6 from timelineutil.js
hideBubble:function (){
	evtBubble = $('eventBubble');
	if(evtBubble){
		evtBubble.hide();
	}
},
// Log is available in FF, Chrome and Safari.
// To test in IE, use alert
log: function(str){
	if(window.console){
		window.console.log(str);
	}else {
		alert(str);
	}
},
functionUtil: function(fname){
	this.callMethod = fname;
},
callFunctionWithParameters: function(symbol, params){
	   var fn = new Tolven.Util.functionUtil(symbol);
	   fn.callMethod(params);
},
//Allows invocation of a method using the function name from the parameter
sendUpdateToServer: function(ajaxFn, ajaxparams){
	var instAjax = new Ajax.Request(
			ajaxFn,
			{
				method: 'post',
				parameters: ajaxparams,
				onSuccess: function(req){
				Tolven.Util.log("update complete");
			}
			});
},
voidFn: function(){
	 return false;
},
/*
Go up one level, set browser history and refresh.
This is a fix for part one of BUG: 1912841
Current pane is removed from the page history.
*/
windowrefresh: function(){
try{
	var hashval = getHash();
	hashval = removeLast(hashval);
	while( isNaN(hashval.charAt(hashval.length-1)) && hashval != null){
		hashval = removeLast(hashval);
	}

	if( hashval !=null)	replaceLastHistory(hashval);
}catch(err){}
window.location.reload();
},
getAttributeValue: function( arr, name){
	var str;
	for( var a =0 ; a < arr.length; a++){
		if( arr[ a ].nodeName == name ) {
			str = arr[ a ].nodeValue;
			break;
		}
	}
	return str;
},
/* Util method to set focus on input
*/
setFocus: function(id){
	if( $(id) && $(id).focus){
	   $(id).focus();
	}
},
/*	All key return events are blocked.
*	For Special cases, like the login page, use this method to submit a form when enter is pressed.
*/
submitOnEnter: function(event, frm){
	if (event.keyCode == Event.KEY_RETURN) {
    		document.forms[frm].submit();
	}
},
submitOnEnter2: function(event, buttonid){
	if (event.keyCode == Event.KEY_RETURN) {
		$(buttonid).click();
	}
}

};

/*
 * This utility provides a way to load JavaScript files on demand. This utility contains a Map which is hard coded.
 * SCRIPTMAP contains the list of files required for each functionality.
 * Instead of making a call to the functions, the tolven js code calls downloadAndCallScript with the method that needs to called and the type
 * script that its dependent on. downloadAndCallScript ensures only one copy of the script is downloaded. If script is not loaded, it puts the function
 * on queue and loads the script. The loaded script notifies this utility by making a call to alertScriptLoaded which later calls all the queued up
 * methods.
 * To add more scripts to be loaded this way, add the type of script and the list of dependent script files to SCRIPTMAP.
 * To use this utility, provide a function similar to this:
 *
    var tempFunction = function(){

		getPreferencesMenuItemsFromServer(element, role);
	};
	DynaLoad.downloadAndCallScript(tempFunction, undefined, 'USERPREF');
 */
var DynaLoad = {
SCRIPTMAP: {
	'CUSTOMIZE' : 'builder.js,effects.js,controls.js,tolven-customize.js',
	'TIMELINE'  : 'simile/timeline-api.js,simile/bundle.js,simile/l10n/{locale}/timeline.js,simile/l10n/{locale}/labellers.js,tolvenUtils.js,timelineutil.js',
	'DRAGDROP'  : 'builder.js,effects.js,dragdrop.js',
	'USERPREF'  : 'tolven-preferences.js',
	'TOLVENWIZ' : 'tolvenwiz.js'
},
LOADED_SCRIPTS: [],
METHOD_QUEUE: [],
downloadAndCallScript: function(fnName, params, scriptsToLoad){
	var fn = new Tolven.Util.functionUtil(fnName);

	try{
		fn.callMethod();
	}catch(e){
		this.METHOD_QUEUE.push(fnName);
		if(this.LOADED_SCRIPTS.indexOf(scriptsToLoad) < 0){
			this.LOADED_SCRIPTS.push(scriptsToLoad);
			this.loadScript(fnName, params, scriptsToLoad);
		}
	}
},
callMethodsInQueue:function(){
	for(var i = 0; i < DynaLoad.METHOD_QUEUE.length; i++){
		try{
			Tolven.Util.callFunctionWithParameters(DynaLoad.METHOD_QUEUE[i], undefined);
			DynaLoad.METHOD_QUEUE[i] = undefined;
		}catch(e){

		}
	}
	DynaLoad.METHOD_QUEUE = DynaLoad.METHOD_QUEUE.compact();
},
insertScript: function(jsFileName){
	var head = $$('head')[0];
// Check if the script is already loaded.
	if (head && DynaLoad.LOADED_SCRIPTS.indexOf(jsFileName) < 0){
//		Tolven.Util.log(' inserting : ' + jsFileName);
		var script = document.createElement('script');
		script.type = 'text/javascript';
		script.language = 'JavaScript';
		script.src = '/Tolven/scripts/' + jsFileName;
		//alert('inserting  /Tolven/scripts/' + jsFileName)
		head.appendChild(script);
		DynaLoad.LOADED_SCRIPTS.push(jsFileName);
	}
},
loadScript: function(fnName, params, scriptToLoad){
	var head;
	var script;
	var scriptFiles = DynaLoad.SCRIPTMAP[scriptToLoad];

	head = $$('head')[0];
	if (head)
	{
		var scripts = scriptFiles.split(',');
		for(var i = 0; i < scripts.length; i++){
			if(scripts[i].indexOf('{locale}') >= 0){
				if('en' != userLocale){
					var enscript = scripts[i].replace('{locale}', 'en');
					this.insertScript(enscript)
				}
				var localeScript = scripts[i].replace('{locale}', userLocale);
				this.insertScript(localeScript)

			}else{
				this.insertScript(scripts[i]);
			}
		}
	}
},
alertScriptLoaded: function(){
	DynaLoad.callMethodsInQueue();
}
};

//
function hideElement(elementId){
	$(elementId).hide();
}
function showElement(elementId){
	$(elementId).show();
}
