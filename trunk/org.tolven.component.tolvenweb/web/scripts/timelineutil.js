function showSummaryTimline(element,interval){
    var instAjax = new Ajax.Request(
		'getTimelineDataJSON.ajaxc', 
		{
			method: 'get', 
			parameters: 'element='+element+'&accountUser='+accountUserId, 
			onSuccess: function(req) {
			  createTimeline( element+"_timeline", req, interval);
			}
		});
}
function createTimeline(timelineDivId,request,interval){
	//Tolven.Util.log(request.responseText);
	var dat;
	$(timelineDivId).parentNode.setAttribute('type','timeline'); // trick I created after watching david coffer field's video.
	if(timelineObjContainer == null){
		timelineObjContainer = new TimelineObjContainer();
	}
	try{
	//var tempTxt = "{bar_info:[{\'dateTimeFormat\':\'iso8601\',\'barPath\':\'echr:patient-55292:summary:timeline:probsum\',barColor:\'#99FFCC\',repeating:\'echr:patient:problems\',dataVersion:\'2\',interval:\'6\',barName:\'Problems\',events:[{start:\'2008-01-02 00:00:00.0\',path:\'echr:patient-55292:problem-55300\',title:\'Hypertension\',icon:\'../scripts/simile/images/red-circle.png\'},{start:\'2007-08-16 00:00:00.0\',path:\'echr:patient-55292:problem-55304\',title:\'Hay Fever\',icon:\'../scripts/simile/images/red-circle.png\'}],actions:[]}],barcount:\'1\',minDate:\'00/16/2007\',maxDate:\'00/02/2008\'}";
	//dat = tempTxt.evalJSON();
	dat = request.responseText.evalJSON();
	if(dat.hasData == 'false'){
	 $(timelineDivId).innerHTML = "No items to display in timeline";
		timelineObjContainer.addEmptyTimeline(timelineDivId,dat);
		return;
	}

	}catch(e){ alert(e);}
	var minDate = new Date(dat.minDate);
	var maxDate = new Date(dat.maxDate);
	
	switch(interval){
		case Timeline.DateTime.MONTH:
			minDate = prevCalendarMonth(minDate);
			maxDate = nextCalendarMonth(maxDate);
			break;
		case Timeline.DateTime.DAY:
			minDate.setDate(minDate.getDate()-1);
			maxDate.setDate(maxDate.getDate()+1);
			break;
		case Timeline.DateTime.WEEK:
			minDate.setDate(minDate.getDate()-1);
			maxDate.setDate(maxDate.getDate()+1);
			break;
  		default: 
			alert('Invalid interval seleted');
			break;

	}
	var dateToDisplay = (maxDate>new Date())?new Date():maxDate;
	if(dat.barcount == 0)
		return;
	var	eventSource = new Timeline.DefaultEventSource();
	var bandInfos = [
		Timeline.createBandInfo({
			eventSource:    eventSource,
			date:           dateToDisplay,
			width:          "350px", 
			intervalUnit:   interval, // change this to top level attribute 
			intervalPixels: 150,  // increase the width, math for scrollbar need to be fixed
			locale:	    userLocale
		})
	  ];
	try{
	timeLineObj = Timeline.create($(timelineDivId), bandInfos,null,null,minDate,maxDate);
	}catch(e){ alert(e);}
	for(var i=0;i<dat.bar_info.length;i++){
		var theme=Timeline.getDefaultTheme();
		var eventSource = new Timeline.DefaultEventSource();
		band = timeLineObj.getBand(0);
		var theme = Timeline.getDefaultTheme();
		var layout=new Timeline.StaticTrackBasedLayout({
		eventSource:eventSource,
		ether:band._ether,
		showText:true,
		theme:theme
		});
		var eventPainterParams={
		showText:true,
		layout:layout,
		theme:theme,
		eventSource: eventSource,
		localVersion:dat.bar_info[i].dataVersion,
		path:dat.bar_info[i].barPath,
		label:dat.bar_info[i].barName,
		menuActions:dat.bar_info[i].actions,
		color:dat.bar_info[i].barColor,
		index:i
		};
		var eventPainter=new Timeline.DurationEventPainter(eventPainterParams);
		band.addEventPainter(eventPainter);
		eventPainter.initialize(band,band._timeline);
		if(i == dat.bar_info.length-1){
			loadPainterEvents(eventPainter,true);
		}
		else
			loadPainterEvents(eventPainter);
	}
	timelineObjContainer.addTimeline(timeLineObj,timelineDivId,dat);
	setBandsStyle(timeLineObj,dat,timelineDivId);	
}
function setBandsStyle(timeLineObj,bandData,timelineDivId){
	var band = timeLineObj.getBand(0);
	var eventPaintersCount = band._eventPainters.length;
	for(var i=0;i<eventPaintersCount;i++){
		var eventPainter = band._eventPainters[i];
		 resizeEventPainter(band._eventPainters[i])
	}
  resizeTimelineDiv(timeLineObj);
}

// When the band get reloaded with the events reset the height of the band and the positions of the band labels
function resizeTimelineDiv(timeline){
	var band = timeLineObj.getBand(0);
	var eventPaintersCount = band._eventPainters.length;
	var prevLayer = band._eventPainters[0];
	var height = 0;
	for(var i=0;i<eventPaintersCount;i++){
		var eventPainter = band._eventPainters[i];
			if(i>0 && prevLayer ){
				eventPainter.setTop(prevLayer._top+prevLayer._height+'px');
				prevLayer = eventPainter;
			}
	}
	if((prevLayer._top+prevLayer._height)> 350){
		timeline._containerDiv.style.height = (prevLayer._top+prevLayer._height)+'px';
		timeline.getBand(0)._div.style.height = (prevLayer._top+prevLayer._height)+'px';
	}
}
/* Adjust the events layer height to show all the events*/
/* Find the maximum of the offsetTop for the events in the painter and adjust the size based on that*/
function resizeEventPainter(eventPainter){
	var _eventsLayer = eventPainter._eventLayer;
	if(_eventsLayer == null || _eventsLayer.innerHTML== ''){ // if there are no events in the current period eventsLayer is null
		return;
	}
	var eventDivs = null;
	try{
	eventDivs = _eventsLayer.getElementsByTagName('div');
	}catch(e) { alert('error now');alert(e);}
	if(eventDivs == null)
		return;
	var eventsCount = eventDivs.length;
	var offsetTopMax =0;
	for(var i=0;i<eventsCount;i++){
      if(eventDivs[i].offsetTop > offsetTopMax)
		  offsetTopMax= eventDivs[i].offsetTop;
	}
	if(getPixelValue(_eventsLayer.style.height)!= offsetTopMax){	
		eventPainter.setHeight(offsetTopMax+45+'px');	
	}
}

// The idea is to have multiple event painters in the single band
function TimelineObjContainer(){
	var timelineIds = new Array();
	var emptyTimelineIds = new Array();  // array to hold empty timelineIds
	var timelineEventPainters = new Array();
	var painterPaths = new Array(); // array to hold bandpaths for empty timeline
	this.addTimeline=function(timelineObj,timelineId,data){
	  timelineIds[timelineIds.length] = timelineId;
	  timelineEventPainters[timelineEventPainters.length] = timeLineObj.getBand(0)._eventPainters;
	}
	this.removeTimeline= function(timelineId){  // we may not use this hence timeline refresh is never needed
		for(var i=0;i<timelineIds.length;i++){
			if(timelineIds[i].indexOf(timelineId)!=-1){
			  timelineIds[i] = '';
			  break;
			}
		}
	}
	this.removeEmptyTimeline= function(timelineId){  // we may not use this hence timeline refresh is never needed
		for(var i=0;i<emptyTimelineIds.length;i++){
			if(emptyTimelineIds[i].indexOf(timelineId)!=-1){
			  emptyTimelineIds[i] = '';
			  break;
			}
		}
	}
	this.getTimelineEventPainters=function(timelineId){
		for(var i=0;i<timelineIds.length;i++){
			if(timelineIds[i].indexOf(timelineId)!=-1){
			  return timelineEventPainters[i];
			}
		}
		return null;
	}
	this.getTimelineEventPaintersPaths=function(timelineId){
		for(var i=0;i<emptyTimelineIds.length;i++){
			if(emptyTimelineIds[i].indexOf(timelineId)!=-1){
			  return painterPaths[i];
			}
		}
		return null;
	}
	this.getEventPainter=function(painterPath){
		var timelineId = removeLast(painterPath);
		var painters = this.getTimelineEventPainters(timelineId);
		for(var i=0;i<painters.length;i++){
			if(painters[i]._path == painterPath){
			  return painters[i];
			}
		}
		return null;
	}
	this.addEmptyTimeline=function(timelineId,data){
	  emptyTimelineIds[emptyTimelineIds.length] = timelineId;
	  var paths = new Array();
	  for(var i=0;i<data.bar_info.length;i++){
		paths[i] = data.bar_info[i].barPath+"="+data.bar_info[i].dataVersion;
	  }
	  painterPaths[painterPaths.length] = paths;
	}
	this.isTimelineEmpty=function(timelineId){
		for(var i=0;i<emptyTimelineIds.length;i++){
			if(emptyTimelineIds[i].indexOf(timelineId)!=-1){
			   return true;			  
			}
		}
		return false;
	}
}
// This function checks if the menu belongs to an empty timeline. If yes it removes the timelineID from 
// the timeline object container and repaints the timeline. If not(the menu belongs to a timeline which is already painted) it repaints
// the events in that band.
function reloadPainterEvents(eventPainterPath,serverVersion){
	if(timelineObjContainer.isTimelineEmpty(removeLast(eventPainterPath))){
		timelineObjContainer.removeEmptyTimeline(removeLast(eventPainterPath));
		showSummaryTimline(removeLast(eventPainterPath));
		return;
	}
	var eventPainter = timelineObjContainer.getEventPainter(eventPainterPath);
	eventPainter._localVersion = serverVersion;
	loadPainterEvents(eventPainter,true);
}

function loadPainterEvents(eventPainter,reloadEvetns){
	var eventSource = eventPainter._eventSource;
	var instAjax = new Ajax.Request(
	'getBandEventDataJSON.ajaxc', 
	{
		method: 'get', 
		parameters: 'element='+eventPainter._path+'&accountUser='+accountUserId+
			'&minDate='+eventSource.minDate+'&maxDate='+eventSource.maxDate, 
		onSuccess: function(req) {updateEvents(eventPainter,req,reloadEvetns);}
	});
}
function updateEvents(eventPainter,request,reloadEvetns){ 
    if(request.responseText == 'NoData'){
		eventPainter._eventSource.clear();
		eventPainter.setHeight('0px');
		eventPainter._hasEvents = false;
		eventPainter.paint();
		var _repaintTimeline = true;
		//var _eventPainters = timelineObjContainer.getTimelineEventPainters(removeLast(eventPainter._path));
		//for(var i=0;i<_eventPainters.length;i++){
		//	if(_eventPainters[i]._hasEvents == true){
		//		_repaintTimeline = false;
		//		break;
		//	}
		// }//
		//if(_repaintTimeline)
		//	 rePaintTimeline(eventPainter);
		return;
    }else{
       if(request.responseText == 'refreshTimeline'){
			rePaintTimeline(eventPainter,eventPainter._band._bandInfo.etherPainter._unit);
	   		return;
	   }
	   try{
	   var eventData = request.responseText.evalJSON();
	   eventPainter._hasEvents = true;
	   var eventSource = eventPainter._eventSource;
	   eventSource.clear();
	   eventSource.loadJSON(eventData, document.location.href,eventPainter); // passing event painter to calculate datetopixeloffset
	   eventPainter.paint();
	   resizeEventPainter(eventPainter);	
	   
	   //if(eventPainter.getIndex() == eventPainter._timeline._bandInfos.length || reloadEvetns){
		eventPainter._timeline._mirrorBand.paint(eventPainter);
	   //}
	  // if(eventPainter._index > (eventPainter._timeline.getBandCount()/2) || resizeTimeline){
		   resizeTimelineDiv(eventPainter._timeline);
	//	}
		eventSource.maxDate = eventData.maxDate;
		eventSource.minDate = eventData.minDate;
   	}catch(e){alert("Loading band data failed for path "+eventPainter._path + e);}
   }  
}
function rePaintTimeline(eventPainter,interval){
	var timelineObj = eventPainter._timeline;
	$(timelineObj._containerDiv).update("repainting...");
	timelineObj._scrollBar.parentNode.removeChild(timelineObj._scrollBar);
	timelineObj._scrollLabelsDiv.parentNode.removeChild(timelineObj._scrollLabelsDiv);
	var timelineId = removeLast(eventPainter._path);
	timelineObjContainer.removeTimeline(timelineId);
	showSummaryTimline(timelineId,interval);
}
function showTimelineDaily(timelineId){
	var _eventPainters = timelineObjContainer.getTimelineEventPainters(timelineId);
	if(_eventPainters && _eventPainters[0]._timeline._bandInfos[0].etherPainter._unit != Timeline.DateTime.DAY)
		rePaintTimeline(_eventPainters[0],Timeline.DateTime.DAY);
}
function showTimelineWeekly(timelineId){
	var _eventPainters = timelineObjContainer.getTimelineEventPainters(timelineId);
	if(_eventPainters && _eventPainters[0]._timeline._bandInfos[0].etherPainter._unit != Timeline.DateTime.WEEK)
		rePaintTimeline(_eventPainters[0],Timeline.DateTime.WEEK);
}
function showTimelineMonthly(timelineId){
	var _eventPainters = timelineObjContainer.getTimelineEventPainters(timelineId);
	if(_eventPainters && _eventPainters[0]._timeline._bandInfos[0].etherPainter._unit != Timeline.DateTime.MONTH)
		rePaintTimeline(_eventPainters[0],Timeline.DateTime.MONTH);
}
function showTimelineYearly(timelineId){
	var _eventPainters = timelineObjContainer.getTimelineEventPainters(timelineId);
	if(_eventPainters && _eventPainters[0]._unit != 7)
		rePaintTimeline(_eventPainters[0],7);
}

function getPixelValue(pixStr){
	if(pixStr.indexOf('px')!= -1)
		pixStr = pixStr.substring(0,pixStr.indexOf('px'));
	return parseInt(pixStr);
}

// Get the list from the popup and send it to the server in the same order. 
// The server will determine if the sequence/visibilty has been changed.
function saveTimelinePreferences(timelineId){
  var userMenuList = "";
  var defaultSelection = undefined;
  var parentPath;
	barPrefs = $('bandPrefs').getElementsByTagName('fieldset');
	var optionsStr = "";
	for(i=0;i<barPrefs.length;i++){	 
	  optionsStr += barPrefs[i].name+",";
		var _colorSelect = barPrefs[i].getElementsByTagName('select')[0];
		 optionsStr += _colorSelect.options[_colorSelect.selectedIndex].value.substring(1)+",";
	
	   if(barPrefs[i].getElementsByTagName('input')[0].checked) optionsStr += "true:"; else optionsStr += "false:";
	}
	  setUserTimelinePreferences(timelineId,optionsStr);
}


function getTimeLineSettingsHTML(element){
	var instAjax = new Ajax.Request(
		'getTimelinePreferences.ajaxc', 
		{
			method: 'get', 
			parameters: 'element='+element+'&accountUser='+accountUserId, 
			onSuccess: function(req) {prepareTimlinePreferences(element,req);}
		});
}
function prepareTimlinePreferences(timelineId,request){	
	var dat;
	try{
		dat = request.responseText.evalJSON();
		var bandPrefContainer = createElement('div');
		var title = createElement('div');
		title.className = "userpreftitle"
		title.innerHTML = 'Edit User Preferences';	
		bandPrefContainer.appendChild(title);
		bandPrefContainer.id = 'bandPrefs';
		for(var i=0;i<dat.barCount;i++){
			var colPicker = new Tolven.ColorPicker();
			var _fieldset = createElement('fieldset');
			 _fieldset.name= dat.settings[i].barPath;	
			_fieldset.id = 'item_'+i;
			var _legend = createElement('legend');
			_legend.appendChild(document.createTextNode(dat.settings[i].barName));
			_fieldset.appendChild(_legend);
			
			if(dat.settings[i].barColor.length == 6)
				colPicker.setColor('#'+dat.settings[i].barColor);
			else
				colPicker.setColor(dat.settings[i].barColor);
			var _cell = colPicker._container.rows[0].insertCell(2);
			_cell.innerHTML = 'Visible';
			_cell.style.paddingLeft = '15px';
			
			var checked = "";
			if(dat.settings[i].visible=='true'){
				checked = "checked=\"checked\"";
			}
			_cell = colPicker._container.rows[0].insertCell(3);
			_cell.innerHTML = "<input type=\"checkbox\" " + checked + " /> ";
			_fieldset.appendChild(colPicker._container);
			
			bandPrefContainer.appendChild(_fieldset);
		}
		
		// add buttons to close and save
		var _saveButton = createElement('input');
		_saveButton.type = 'button';
		_saveButton.value = 'Save';
		Event.observe(_saveButton,'click',function(){saveTimelinePreferences(timelineId);});
		
		var _closeButton = createElement('input');
		_closeButton.type = 'button';
		_closeButton.value = 'Close';
		Event.observe(_closeButton,'click',function(){closePrefDiv();});
	
		$("userprefDiv").appendChild(bandPrefContainer);
		createSortablePreferencesDialog('bandPrefs');
		$("userprefDiv").appendChild(_saveButton);
		$("userprefDiv").appendChild(_closeButton);
		$("userprefDiv").style.display = 'block';
		$("userprefDiv").style.left = document.body.clientWidth * .30 + "px";	
		
	}catch(e){ alert(e);}
}
function getMindate(dateStr){ // mm/dd/yyyy 
	var d = new Date();
	if(dateStr){
	  vals = dateStr.split("/");
      if(vals .length == 3){
		d.setMonth(00);
		d.setDate(01);
		d.setYear(parseInt(vals[2]));
	  }
	}
	return d;
}
function getMaxdate(dateStr){ // mm/dd/yyyy 
	var d = new Date();
	if(dateStr){
	  vals = dateStr.split("/");
      if(vals .length == 3){
		d.setMonth(11);
		d.setDate(31);
		d.setYear(parseInt(vals[2]));
	  }
	}
	return d;
}
function setUserTimelinePreferences(timelineId,optionsStr){
	//Tolven.Util.log(optionsStr);
  closePrefDiv();
  $(timelineId).innerHTML = ""; 
   var instAjax = new Ajax.Request(
	 'setUserTimelinePreferences.ajaxc', 
	 {
	  method: 'post', 
	  parameters: 'path='+timelineId+'&userpreferences='+optionsStr+'&accountUser='+accountUserId, 
	  onSuccess: reloadPage
	});

}
function getMenuPath(id){
  var nodes = id.split(":");
  var path = "";
  for(i=0;i<nodes.length;i++){
  	if(nodes[i].indexOf('-')>0)
      path += nodes[i].substring(0,nodes[i].indexOf('-'))+":";
	else
	  path += nodes[i]+":";
	}
  return path.substring(0,path.length-1);
}

function createSortablePreferencesDialog(dialogId){
	var createSortableFunction = function(){
	Sortable.create
	(
		dialogId,
		{
			tag:'fieldset',
			constraint: false,
			handle:'bandHead',
			dropOnEmpty:true
		}
	);
	};
	DynaLoad.downloadAndCallScript(createSortableFunction, undefined, 'DRAGDROP');
}
function reloadPage(){
	window.location.reload();
}

// hideBubble moved to tolven6.js


/*===============================================
* Utility functions for calendar view. Can be moved to a separate script
*================================================
*/

function showCalendar(element){
	today = new Date();
	moStr = (today.getMonth()+1)<10?'0'+(today.getMonth()+1):(today.getMonth()+1);
    fromDate = ''+today.getFullYear()+moStr+'01';
	toDate = ''+today.getFullYear()+moStr+NumDaysIn(parseInt(moStr),today.getFullYear());
    var instAjax = new Ajax.Request(
		'getCalendarEvents.ajaxc', 
		{
			method: 'get', 
			parameters: 'element='+element+'&accountUser='+accountUserId+'&fromDate='+fromDate+'&toDate='+toDate+'&getVersions=true&interval=MONTH', 
			onSuccess: function(req) {createCalendar(element,req);}
		});
	//createCalendar(element)
}
function createCalendar(element,request){
	try{
		dat = request.responseText.evalJSON();
		for(var i=0;i<dat.events.length;i++){
			addCalendarEvent(dat.events[i]); //AddCalendarEvent(DateStr(yearmonth), Description)
		}
		Tolven.CalendarCache.remove(element+":menus");
		Tolven.CalendarCache.put(element+":menus",dat.menus.split(","));
		CalendarImpl(element);
	}catch(e) { alert("Invalid JSON string for building calendar"+e);}
}

Tolven.CalendarEvent = new Object();
Tolven.CalendarEvent=function(eventId,eventObj){
	this.eventId = eventId;
	this._start= new Date(eventObj.start);
	this._end= new Date(eventObj.end);
	this._duration = eval(eventObj.isDuration)?true:false;
	this.eventObj = eventObj
	this._div = createElement('div');
	this._div.id = eventId+"_events";
	this._div.className='eventText';
	this._div.innerHTML = eventObj.title;
	//Tolven.Util.log(eventObj.isDuration + "  "+ eventObj.title + " " + this._duration);	
}

addCalendarEvent = function(eventObj) {
	var tStart = new Date(eventObj.start);
	var eventId = getDateAsString(tStart);
	if (Tolven.CalendarEventsCache.get(eventId) == null) {
		Tolven.CalendarEventsCache.put(eventId,new Array());
	}
	Tolven.CalendarEventsCache.get(eventId).push(new Tolven.CalendarEvent(eventId, eventObj));
}
function refreshCalendars(calendarsToRefresh){
	var refreshCount = calendarsToRefresh.size();
	for(i=0;i<refreshCount;i++){
		var calenderId = calendarsToRefresh.keyAt(i);
		var menusCache = Tolven.CalendarCache.get(calenderId+":menus");
		if(menusCache == null)
			alert('Tolven.CalendarCache is invalid');
		var menusModified = calendarsToRefresh.get(calenderId);
		if(menusModified == null){
			alert('invalid calendarsToRefresh');
		}
		for(i=0;i<menusModified.length;i++){
			var menuPathVer = menusModified[i].split("=");
			for(j=0;j<menusCache.length;j++){
				var cacheMenuPathVer = menusCache[j].split("=");
				if(cacheMenuPathVer[0] ==  menuPathVer[0]){
					menusCache[j] = menusModified[i];
				}
			}
		}
		//Tolven.CalendarCache.remove(calenderId+":menus");
		//Tolven.CalendarCache.put(calenderId+":menus",menusCache);			
		populateEvents(Tolven.CalendarCache.get(calenderId+":params"));
	}
}
nextCalendarMonth = function(_date){
	if(_date.getMonth() == 11){
	 _date.setMonth(1);
	 _date.setFullYear(_date.getFullYear()+1); 
	}
	else
		_date.setMonth(_date.getMonth()+1);
	return _date;
}

prevCalendarMonth = function(_date){
	if(_date.getMonth() == 1){
	 _date.setMonth(11);
	 _date.setFullYear(_date.getFullYear()-1); 
	}
	else
		_date.setMonth(_date.getMonth()-1);
	return _date;
}

DynaLoad.alertScriptLoaded();