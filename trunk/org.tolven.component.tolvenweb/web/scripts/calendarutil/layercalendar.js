
var today = new Date();


// Utility function to populate an array with values
function arr() {
	var n;
	for (n=0;n<arr.arguments.length;n++) {
		this[n+1] = arr.arguments[n];
	}
	this.length = n;
}

var LayerCalendar = new Object();

LayerCalendar.longMonthNames = [];
LayerCalendar.shortMonthNames = [];
LayerCalendar.calendarIntervals = [];
LayerCalendar.longWeekdayNames = [];
LayerCalendar.shortWeekdayNames = [];

LayerCalendar.getLongMonthNames = function(month, locale) {
    var mtName;
    try{
    	mtName = LayerCalendar.longMonthNames[locale][month - 1];
    }catch(err){
	// if not supported, use default
    	mtName = LayerCalendar.longMonthNames["en"][month - 1];
    }
    return mtName;
};

LayerCalendar.getShortMonthNames = function(month, locale) {
    var mtName;
    try{
    	mtName = LayerCalendar.shortMonthNames[locale][month - 1];
    }catch(err){
	// if not supported, use default
    	mtName = LayerCalendar.shortMonthNames["en"][month - 1];
    }
    return mtName;
};

LayerCalendar.getCalendarIntervals = function(interval, locale) {
    var mtName;
    try{
    	mtName = LayerCalendar.calendarIntervals[locale][interval - 1];
    }catch(err){
	// if not supported, use default
    	mtName = LayerCalendar.calendarIntervals["en"][interval - 1];
    }
    return mtName;
};

LayerCalendar.getLongWeekdayNames = function(interval, locale) {
    var mtName;
    try{
    	mtName = LayerCalendar.longWeekdayNames[locale][interval - 1];
    }catch(err){
	// if not supported, use default
    	mtName = LayerCalendar.longWeekdayNames["en"][interval - 1];
    }
    return mtName;
};

LayerCalendar.getShortWeekdayNames = function(interval, locale) {
    var mtName;
    try{
    	mtName = LayerCalendar.shortWeekdayNames[locale][interval- 1];
    }catch(err){
	// if not supported, use default
    	mtName = LayerCalendar.shortWeekdayNames["en"][interval - 1];
    }
    return mtName;
};


var months = new Array("January","February","March","April","May","June","July","August","September","October","November","December");
var calendarIntervals = new arr("DAY","WEEK","MONTH");
var weekdayNames = new arr("Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday");
var weekdays = new arr("Sun","Mon","Tue","Wed","Thu","Fri","Sat");
var pre, post, tmp, gloMo, gloYr, gloYearmonth;

function EventCalendar() {
	addNavigationLinks = function(){
		calendarGrid.className='calendarTable';
		row = calendarGrid.insertRow(calendarGrid.rows.length); // to show navigation arrows an the month name
		row.style.height = '5%';

		addIntervalLabels(row.insertCell(0));

		cell_1 = row.insertCell(1);
		navigateDiv = createElement('div');
		navigateDiv.className='navigateLinks';

		prevLi = createElement('li');
		prevLi.innerHTML = prev; 
		navigateDiv.appendChild(prevLi);

		monthLi = createElement('li');
		if(calParams._interval =='DAY'){
			monthLi.innerHTML = calParams.getDate()+"  " + LayerCalendar.getLongMonthNames(calParams.getMonth() + 1, userLocale) + " " + calParams.getYear();  
		}else if(calParams._interval =='WEEK'){
	
			weekstart = calParams.weekdates[0];
			weekend = calParams.weekdates[6];
			displayStr = weekstart.getDate();
			if(weekstart.getMonth() != weekend.getMonth())
			displayStr += "/"+LayerCalendar.getLongMonthNames(weekstart.getMonth() + 1, userLocale);
			if(weekstart.getFullYear() != weekend.getFullYear())
			displayStr += "/"+weekstart.getFullYear(); 
			monthLi.innerHTML = displayStr+" - "+ weekend.getDate()+"/"+LayerCalendar.getLongMonthNames(weekend.getMonth() + 1, userLocale)+"/" + weekend.getFullYear();  
		}else if(calParams._interval =='MONTH'){
			monthLi.innerHTML = LayerCalendar.getLongMonthNames(calParams.getMonth() + 1, userLocale) + " " + calParams.getYear();   
		}
		navigateDiv.appendChild(monthLi);

		nextLi = createElement('li');
		nextLi.innerHTML = next; 
		navigateDiv.appendChild(nextLi);

		goToDiv = createElement('div');
		goToDiv.className= 'gotoDiv';
		goToMonthSelect = createElement('select');
		addMonthOptions(goToMonthSelect);
		goToDiv.appendChild(goToMonthSelect);
		goToYearText = createElement('input');
		goToYearText.type='text';
		goToYearText.size='4';
		goToYearText.style.marginLeft='2px';
		goToDiv.appendChild(goToYearText);
		goToButton = createElement('input');
		goToButton.type='button';
		goToButton.value='Go To';
		goToButton.onclick=function(){
			gotoYear = goToYearText.value;
			if(gotoYear.length!=4){
				alert('invalid year');
				return;
			}
			selectedMonth = goToMonthSelect.options[goToMonthSelect.selectedIndex].value;
			if(selectedMonth== 0){
				alert('Please select a month');
				return;
			}
			calParams.setMonth(parseInt(selectedMonth));
			calParams.setYear(gotoYear);
			CalendarImpl(id,calParams);  // TODO : populate events??
		}
		goToDiv.appendChild(goToButton);

		//navigateDiv.appendChild(goToDiv);

		cell_1.appendChild(navigateDiv);
		cell_2 = row.insertCell(2);
		cell_2.appendChild(goToDiv);
	}// END function addNavigationLinks

	gotoPrevMonth=function(){
	  calParams.setMonth(calParams.getMonth()-1);
	  calParams.weekdates = new Array();
	  populateEvents(calParams);
	}

	gotoNextMonth=function(){
	  calParams.setMonth(calParams.getMonth()+1);
	  calParams.weekdates = new Array();
	  populateEvents(calParams);
	  //CalendarImpl(id,calParams);
	}
	gotoDate=function(dateStr){
	 calParams.setDate(dateStr%100);
	  dateStr =Math.floor(dateStr/100);
	  calParams.setMonth(dateStr%100);
	  calParams.setYear(Math.floor(dateStr/100));
	  //calParams.currentDay = new Date(months[calParams.currentMonth] + " "+calParams.currentDate+"," + calParams.currentYear).getDay();
	  bgn = new Date(LayerCalendar.getLongMonthNames(calParams.getMonth() + 1, userLocale) + " 1," + calParams.getYear());
	  calParams.firstday = bgn.getDay();
	  calParams.lastdate = NumDaysIn(calParams.getMonth(),calParams.getYear());
	  calParams.lastday = 1+(calParams.firstday + calParams.lastdate - 1)%7;
	  calParams.weekdates = new Array();	
	  calParams._interval = 'DAY';  
	  populateEvents(calParams);
	}

	gotoNextDay=function(){
		calParams.setDate(calParams.getDate()+1);	
		populateEvents(calParams);
		//CalendarImpl(id,calParams);
	}
	gotoPrevDay=function(){
		calParams.setDate(calParams.getDate()-1);	
		populateEvents(calParams);
	//	CalendarImpl(id,calParams);
	}
	gotoNextWeek=function(){
		var _d = calParams.weekdates[6];
			_d.setDate(_d.getDate()+1);
		calParams.weekdates[0] = _d;
		calParams = populateWeekDays(calParams);
		populateEvents(calParams);
		//CalendarImpl(id,calParams);
	}
	gotoPrevWeek=function(){
		var _d = calParams.weekdates[0];
			_d.setDate(_d.getDate()-7);
		calParams.weekdates[0] = _d;
		calParams = populateWeekDays(calParams,true);
		populateEvents(calParams);
		//CalendarImpl(id,calParams);
	}
	paintMiniMonth=function(dateObj){
		  var _date = new Date(dateObj.toString());
		  _date.setDate(1);
		  firstday = _date.getDay();
		  lastdate = NumDaysIn(_date.getMonth()+1,_date.getFullYear());
		  lastday = 1+(firstday + lastdate - 1)%7;
		  var _rows = Math.ceil((firstday+lastdate)/7);
		  miniMonth.setMonth(_date.getMonth());
		  miniMonth.setYear(_date.getFullYear());
		  	
		  miniMonth.addWeek();
		  for (var i=1;i<=weekdays.length;i++){   // add weekdays to first row
			miniMonth.addDate(LayerCalendar.getShortWeekdayNames(i, userLocale).charAt(0));
		  }
		  miniMonth.addWeek();
		  _date.setDate(_date.getDate()- firstday);
	
		  for(var r=0;r<_rows;r++){
			  for(var d=0;d<7;d++){
				var cssClassNames ="";
				if(_date.getMonth() != dateObj.getMonth())
					cssClassNames = 'mmDayNotInMonth';
				if(d ==0 || d==6)
					cssClassNames += " weekend";
				else
					cssClassNames += " weekday";	

				if(getDateAsString(_date) == getDateAsString(dateObj) && calParams._interval == "DAY" ){
				  cssClassNames += " displayDate";	
				 }
				 if(_date.getDate() == new Date().getDate()){
				  cssClassNames += " today";	
				 }
				miniMonth.addDate(_date.getDate(),cssClassNames);
				 _date.setDate(_date.getDate()+1);
			  }
			miniMonth.addWeek();
		  }
	
	 }// END paintMiniMonth
	addActionButtons=function(){
		actionButtonsDiv = createElement('div');
		actionButtonsDiv.id = calParams.id+"_actions";
		actionButtonsDiv.className = 'actionsDiv';
		serialNo++;	
		 var ajax = new Ajax.Updater(
			 actionButtonsDiv.id,
			 contextPath + '/ajax/getActions.ajaxc',
			 {   method: 'get',
				 parameters: 'element='+calParams._id+
							 '&serialNo='+serialNo +
							 '&accountUserId='+accountUserId});
		 return actionButtonsDiv;
	}
	addIntervalLabels=function(cell_1){ 
		var _intervalSelect = createElement('div');
		_intervalSelect.className = "calendarIntervalSelect";
		_intervalSelect.innerHTML = "<a href='javascript:showCalendarDaily()'>" + LayerCalendar.getCalendarIntervals(1, userLocale) + "</a>  ";
		_intervalSelect.innerHTML += "<a href='javascript:showCalendarWeekly()'>" + LayerCalendar.getCalendarIntervals(2, userLocale) + "</a>  ";
		_intervalSelect.innerHTML += "<a href='javascript:showCalendarMonthly()'>" + LayerCalendar.getCalendarIntervals(3, userLocale) + "</a>";
		cell_1.appendChild(_intervalSelect);
	}//END addIntervalLabels

	showCalendarDaily=function(){
		if(calParams._interval != LayerCalendar.getCalendarIntervals(1, userLocale)){
			calParams._interval = LayerCalendar.getCalendarIntervals(1, userLocale);
			populateEvents(calParams);
		}
	}
	showCalendarWeekly=function(){
		if(calParams._interval != LayerCalendar.getCalendarIntervals(2, userLocale)){
			calParams._interval = LayerCalendar.getCalendarIntervals(2, userLocale);
			populateEvents(calParams);
		}
	}
	showCalendarMonthly=function(){
		if(calParams._interval != LayerCalendar.getCalendarIntervals(3, userLocale)){
			calParams._interval = LayerCalendar.getCalendarIntervals(3, userLocale);
			populateEvents(calParams);
		}
	}
}

function CalendarImpl(containerId,params) {		
	  $(containerId).setAttribute('type','calendar');
	  arrow_left = '<img src="../images/cyan_left.gif" border="0" height="20px" width="20px"/>';
	  arrow_right = '<img src="../images/cyan_right.gif" border="0" height="20px" width="20px"/>';
	  id = containerId;
		calParams = null;
	  if(!params){
		  calParams  = new TolvenCalendarParams(containerId,accountUserId);
	  }
	  else
		  calParams  = params;
	  
	  Tolven.CalendarCache.put(containerId+":params",calParams);
	  if(!calParams._interval)
		 calParams._interval = "MONTH";
	  prev = next ="";
	  if(calParams._interval == 'MONTH'){
		  calParams.weekdates = new Array();
		prev = '<a class="leftArrow" href="javascript:gotoPrevMonth()">'+arrow_left+' </a>';
		next = '<a href="javascript:gotoNextMonth()">'+arrow_right+'</a>';
	  }
	  else if(calParams._interval == 'WEEK'){
   	    if(calParams.weekdates.length ==0)
  			calParams = populateWeekDays(calParams);
		prev = '<a href="javascript:gotoPrevWeek()">'+arrow_left+'</a>';
		next = '<a href="javascript:gotoNextWeek()">'+arrow_right+'</a>';
	  }
	  else if(calParams._interval == 'DAY'){
		calParams.weekdates = new Array();
		prev = '<a href="javascript:gotoPrevDay()">'+arrow_left+'</a>';
		next = '<a href="javascript:gotoNextDay()">'+arrow_right+'</a>';
	  }
	  calendarGrid = createElement('table');
	 addNavigationLinks();
	  row = calendarGrid.insertRow(calendarGrid.rows.length);
	  miniMonthCell = row.insertCell(0);	
	  miniMonthCell.style.verticalAlign = 'top';
	  
	  miniMonth = new MiniMonth();
	  miniMonthTable = miniMonth.create();

	  miniMonthTable.className='miniMonth'; 	
	  //miniMonth.addHeader('<a href="javascript:gotoPrevMonth()"> � </a> '+months[calParams.currentMonth] + '   ' + calParams.currentYear+'<a href="javascript:gotoNextMonth()">  � </a)');
	  miniMonth.addHeader(LayerCalendar.getLongMonthNames(calParams.getMonth() + 1, userLocale) + '   ' + calParams.getYear());
	  paintMiniMonth(calParams.getDateObj());
	  miniMonthCell.appendChild(miniMonthTable);
	  miniMonthCell.appendChild(addActionButtons());
	  
	  calGridCell = row.insertCell(1);
	  calGridCell.colSpan = 2;
      calGridCell.style.width='100%';
	  calGridCell.style.height='100%';
	  calGridCell.style.padding='0';
	  calGridCell.style.background='#FFFFFF';	
      calGridCell.style.position='relative';	
      if(calParams._interval == 'MONTH')
	    calGridCell.appendChild(new MonthlyCalendarGrid(calParams));
	  else if(calParams._interval == 'WEEK')
		calGridCell.appendChild(createWeeklyCalendar(calParams)); 
	  else if(calParams._interval == 'DAY')
		calGridCell.appendChild(DailyCalendarGrid(calParams)); 

	  
	  divElm = $(id+'_calendar');  //TODO: replace with prototype
	  divElm.innerHTML= "";
	  divElm.appendChild(calendarGrid);	
    }
    CalendarImpl.prototype = new EventCalendar;

function addDayHourSEventsGrid(hourCell,calParams){
	hourEventsGrid = createElement('div');
	hourEventsGrid.className = 'hourEventsGrid';
	hoursDiv = createElement('div');
	hoursDiv.style.height='100%';
	hoursDiv.style.width='5%';
	eventsDiv = createElement('div');
	eventsDiv.style.height='100%';
	eventsDiv.style.width='95%';
	hoursDiv.className='hoursDiv';
	eventsDiv.className='eventsDiv';
	buildHoursDiv(hoursDiv,calParams);
	buildHourEventsDiv(eventsDiv,calParams);
	hourEventsGrid.appendChild(hoursDiv);
	hourEventsGrid.appendChild(eventsDiv);
	hourCell.appendChild(hourEventsGrid);
}
function buildHoursDiv(hoursDiv,calParams){
	var mer = "am";
  for(p=0;p<2;p++){
	 h =12; 
    for(i=0;i<12;i++){
      hourDiv = createElement('div');
	  hourDiv.innerHTML = h+mer;		
	  hourDiv.style.height = '50px';
	  hourDiv.className='hourDiv';
	  hoursDiv.appendChild(hourDiv);
	  if(h==12)
		  h =1;
	  else
	    h++;
   }
   mer = "pm";
  }
}
function buildHourEventsDiv(eventsDiv,calParams,weekdate){
  var mer = "am";
  var hrs =0;
  var idStr = ""
  if(!weekdate){
	  idStr = getDateAsString(calParams.getDateObj());
	  hrStr = hrs<10?'0'+hrs:hrs;
	  idStr = calParams.getYear()+moStr+dateStr;
	}
	else{
	  idStr = getDateAsString(calParams.getDateObj());
	  hrStr = hrs<10?'0'+hrs:hrs;
	}
	eventObjs = Tolven.CalendarEventsCache.get(idStr);		
	//if(eventObjs)
	//Tolven.Util.log(idStr+'found   '+Object.toJSON(eventObjs[0]));
   for(p=0;p<2;p++){
     h =12; 
     for(i=0;i<12;i++){
	   dayDiv = createElement('div');
	   dayDiv.id = weekdate?'day_'+weekdate.getDay():'day_0';
	   dayDiv.style.height = '50px';
	   dayDiv.className='eventDiv';
	   eventsDiv.appendChild(dayDiv);
	   eventDiv = createElement('div');
	   eventDiv.id = idStr+hrs+'_events';
	   eventDiv.className = 'eventLabel';
		eventDiv.style.margin='0';
	   dayDiv.appendChild(eventDiv);
	  	   	
	   if(eventObjs)	
	   for(e=0;e<eventObjs.length;e++){
		  if(parseInt(eventObjs[e]._start.getUTCHours()) == hrs){
			eventText = createElement('div');
			eventText.className='eventText';
			eventId = idStr+hrs+'_'+e;
			eventText.innerHTML = '<a href="javascript:showBubbleForCalendar(\''+eventId+'\')">'+eventObjs[e].eventObj.title+'</a>';
			eventText.id = eventId;
			eventDiv.appendChild(eventText);
		  }
	   }  
	    hrs++;
	
	if(h==12) h =1;  else  h++;
    }	
    mer = "pm";
  }
}

createWeeklyCalendar=function(calParams){
	weeklyCalendarGrid = createElement('table');
	weeklyCalendarGrid.style.width='100%';
	weeklyCalendarGrid.style.textAlign = 'center';
	headerRow = weeklyCalendarGrid.insertRow(weeklyCalendarGrid.rows.length);
	headerCell_0 = headerRow.insertCell(0);
    headerCell_0.style.width = '5%';
	headerCell_1 = headerRow.insertCell(1);
	headerCell_1.style.width='95%';
	headerCell_1.style.position = 'relative';
    addWeekDays(headerCell_1,calParams);
	hoursEventsRow = weeklyCalendarGrid.insertRow(weeklyCalendarGrid.rows.length);
	hoursEventsCell = hoursEventsRow.insertCell(0);
    hoursEventsCell.colSpan = '2';	
	hoursEventsDiv = createElement('div');
	hoursEventsDiv.className = 'weeklyEvents';
	hoursEventsCell.appendChild(hoursEventsDiv);
	hoursDiv = createElement('div');
	hoursDiv.style.position='absolute';
	hoursDiv.style.left='0';
	hoursDiv.style.width = '5%';
	buildHoursDiv(hoursDiv,calParams);

	eventsDiv = createElement('div');
	eventsDiv.style.width = '95%';
	eventsDiv.style.position='absolute';
	eventsDiv.style.left='5%';
	
	pSize = (100/weekdays.length); 
    leftPos = 0;
    for (var i=1;i<=weekdays.length;i++){   // add weekdays to first row
      dayEventsDiv = createElement('div');
      dayEventsDiv.className = 'dayEventsDiv';
      dayEventsDiv.style.width = (pSize-0.1)+"%";
      dayEventsDiv.style.left = leftPos+"%";
	  buildHourEventsDiv(dayEventsDiv,calParams,calParams.weekdates[i-1]);
      eventsDiv.appendChild(dayEventsDiv);
     leftPos +=pSize;
    }	
	hoursEventsDiv.appendChild(hoursDiv);
	hoursEventsDiv.appendChild(eventsDiv);
	return weeklyCalendarGrid;
}
function addWeekDays(headerCell_1,calParams){
  pSize = (100/weekdays.length); 
  leftPos = 0;
  for (var i=1;i<=weekdays.length;i++){   // add weekdays to first row
    dayDiv = createElement('div');
    dayDiv.className = 'colHeader';
	headerLabel = LayerCalendar.getShortWeekdayNames(i, userLocale);
	if(calParams._interval =='WEEK')
		headerLabel +=' '+ calParams.weekdates[i-1].getDate()+'/'+(calParams.weekdates[i-1].getMonth()+1);		
	dayDiv.innerHTML=headerLabel;
    dayDiv.style.width = (pSize-0.1)+"%";
    dayDiv.style.left = leftPos+"%";
    headerCell_1.appendChild(dayDiv);
    leftPos +=pSize;
  }
}
TolvenCalendarParams = function(containerId,accountUserId){
	var _dateObj;
	var _id = containerId;
	var _today;
	var _interval;
	var _weekDates;
	this.getDate = function(){			return this._dateObj.getDate();			}
	this.setDate = function(date){		this._dateObj.setDate(date);			}
	this.getMonth = function(){			return this._dateObj.getMonth();		}
	this.setMonth = function(month){	return this._dateObj.setMonth(month);	}
	this.getYear = function(){			return this._dateObj.getFullYear();		}
	this.setYear = function(year){	return this._dateObj.setYear(year);			}
	this.getId =function(){				return _id;								}
	this.getDateObj = function(){		return this._dateObj;					}
	this.initialize(containerId);
}
TolvenCalendarParams.prototype.initialize = function(containerId){
	this._dateObj = new Date();
	this._today = new Date();
	this._id = containerId;
	this._interval = "MONTH";
	this._weekDates =  new Array();
}

function NumDaysIn(mo,yr) {
	if (mo==4 || mo==6 || mo==9 || mo==11) return 30;
	else if ((mo==2) && LeapYear(yr)) return 29;
	else if (mo==2) return 28;
	else return 31;
}

function LeapYear(yr) {
	return ( (yr%4 == 0 && yr%100 != 0) || yr % 400 == 0 ? true : false );
}

// fixes a Netscape 2 and 3 bug
function GetFullYear(d) { // d is a date object
	var yr = d.getYear();
	return ( yr < 1000 ? yr + 1900 : yr );
}

function PrevMonth(mth) {
	return ( mth == 1 ? 12 : mth - 1 );
}

function NextMonth(mth) {
	return ( mth == 12 ? 1 : mth + 1 );
}

function addHRule(gridContainer,topPos){
	hrule = createElement('div');
	hrule.className = 'hrule';
	hrule.style.top = topPos+"px";
	gridContainer.appendChild(hrule);	
}

function addMonthOptions(goToMonthSelect){
    var option = new Option("",0);
		goToMonthSelect.options.add(option);
	for(i=0;i<months.length;i++){
		option = new Option(LayerCalendar.getLongMonthNames(i+1, userLocale), i+1);
		goToMonthSelect.options.add(option);
	}
}
 function showBubbleForCalendar(eventId,eCell,clickEvent){
	ids = eventId.split('_');
	eventObjs = Tolven.CalendarEventsCache.get(parseInt(eventId));
	var isMonthDisplay = true;
	dateStr = ids[0];
	if(dateStr.length >8){
     dateStr = ids[0].substring(0,8);
	 isMonthDisplay = false;
	 dateCellObj = eCell.parentNode; 
	}
	else
	  dateCellObj = eCell.parentNode; 
	dayID = dateCellObj.id;
	eventIndex = ids[1];
	if(eCell){
		var _pos = Position.cumulativeOffset(eCell);
		x = eCell.offsetLeft;
		y = getPixelValue(eCell.style.top) - 100;
	//	Tolven.Util.log(x +"" + y);
	}
	else{
		x = dateCellObj.offsetLeft-50;
		if(dayID.indexOf('7')>-1)
			x-=130;
		y = dateCellObj.offsetTop-210;
		if(ids[0].length>8){  // adjust position for WEEK and DAY calendar
			x-=5;
		}
	}
	

	//CalendarBubble.create(x,y,dateStr,eventIndex,dateCellObj,isMonthDisplay);
	var bubble =  Timeline.Graphics.createBubbleForPoint(eventObjs[0]._div.ownerDocument,clickEvent.clientX+window.scrollX,clickEvent.clientY+window.scrollY,200,200);
	var _drilldown = createElement("div");
	_drilldown.id ='_drilldown';
	$(_drilldown).setStyle({position:'relative',top:'33px',height:'200px',width:'75%',left:'33px',background:'#FFFFFF',overflow:'auto'});
	 serialNo++;	 
	 var ajax = new Ajax.Request(
		 contextPath + '/ajax/bubbleDispatch.jsf',
		 {   method: 'get',
              parameters: 'element='+eventObjs[0].eventObj.path+
			             '&serialNo='+serialNo +
			             '&accountUserId='+accountUserId,
		  onComplete: function(req) {$(_drilldown).update(req.responseText); $("eventBubble").down(0).appendChild(_drilldown);} });	
	 
 }

//WE are not using this. Keeping this for reference
var CalendarBubble = new Object();
CalendarBubble.create=function(x,y,dateStr,eventIndex,eCell,isMonthDisplay){
	this.x=x;
	this.y=y;
	this.messageContainer = null;
	this.arrow = null;
	var bubble = $('calendarEventBubble');
	
	if(!bubble){
	 this.layer_1 = createElement('div'); // inner div to hold the images and the text
	 this.layer_1.className="bubbleLayer_1";	

	 messageContainer = createElement('div');
	 messageContainer.id='calendarEventBubble_detail';
	 messageContainer.className='bubbleMessageContainer';
	 this.messageContainer=messageContainer;
	 this.layer_1.appendChild(messageContainer);

	 bubbleTopLeft = createElement('div');
	 bubbleTopLeft.className='bubbleTopLeft';
	 this.layer_1.appendChild(bubbleTopLeft);

	 bubbleTop = createElement('div');
	 bubbleTop.className='bubbleTop';
	 this.layer_1.appendChild(bubbleTop);
	
	 bubbleTopRight = createElement('div');
	 bubbleTopRight.className='bubbleTopRight';
	 this.layer_1.appendChild(bubbleTopRight);
	
	 bubbleLeft = createElement('div');
	 bubbleLeft.className='bubbleLeft';
	 this.layer_1.appendChild(bubbleLeft);
	
     bubbleRight = createElement('div');
	 bubbleRight .className='bubbleRight';
	 this.layer_1.appendChild(bubbleRight);

	 
	 bubbleBottomRight = createElement('div');
	 bubbleBottomRight.className='bubbleBottomRight';
	 this.layer_1.appendChild(bubbleBottomRight);
	
	 bubbleBottomLeft = createElement('div');
	 bubbleBottomLeft.className='bubbleBottomLeft';
	 this.layer_1.appendChild(bubbleBottomLeft);

	 bubbleBottom = createElement('div');
	 bubbleBottom.className='bubbleBottom';
	 this.layer_1.appendChild(bubbleBottom);
     
	 arrow = createElement('div');
	 this.layer_1.appendChild(arrow);

	closeBubble = createElement('div');
	 closeBubble.className='closeBubble';
	

	 this.layer_1.appendChild(closeBubble);
	  bubble = createElement('div');
	  bubble.appendChild(this.layer_1);
	  bubble.id = 'calendarEventBubble';
	  Event.observe(closeBubble, 'click', function(event) {
		bubble.style.display='none';
	  });
	}
	
	 if(y < 0 ){
		 if(isMonthDisplay){
		   y = dateCell.offsetHeight;
		 }
		 else
		   y = dateCell.offsetHeight
		 arrow.className='bubbleTopArrow';
	 }
	 else
       arrow.className='bubbleBottomArrow';

	bubble.className='calendarBubble';
	 bubble.style.left=x+"px"; 
	 bubble.style.top=y+"px";
	 fillEventData(messageContainer,dateStr,eventIndex);
	 bubble.style.display='block';
    eCell.parentNode.appendChild(bubble);
}
function fillEventData(messageContainerElement,dateStr,eventIndex){
messageContainerElement.innerHTML="";
eventObjs = Tolven.CalendarEventsCache.get(parseInt(dateStr));
 if(eventObjs){
	 messageContainerElement.id = eventObjs[eventIndex].eventObj.path+"_content";
	 serialNo++;
	 var ajax = new Ajax.Updater(
		 messageContainerElement.id,
		 contextPath + '/ajax/bubbleDispatch.jsf',
		 {   method: 'get',
             evalScripts: true,
             parameters: 'element='+eventObjs[eventIndex].eventObj.path+
			             '&serialNo='+serialNo +
			             '&accountUserId='+eventObjs[eventIndex].eventObj.accountUser});		
      }
}
function MiniMonth(){
	container = null;
	month= null;
	year = null;
	currentWeek = null;
	this.create=function(){
	  this.container = createElement('table');
	  this.container.className = 'miniMonth';	
	  return this.container;
	}
	this.setMonth=function(month){
		this.month = month<10?'0'+month:month;
	}
	this.setYear=function(year){
		this.year= year;
	}
	this.weeks = new Array();
	this.days = new Array();
	this.addHeader=function(textStr){
	  this.header = this.container.insertRow(this.container.rows.length);
	  var temp = this.header.insertCell(0);
	  //miniMonth.addHeader('<a href="javascript:gotoPrevMonth()"> � </a> '+months[calParams.currentMonth] + '   ' + calParams.currentYear+'<a href="javascript:gotoNextMonth()">  � </a)');
	  temp.innerHTML = '<img src="../images/cyan_left.gif" style="border:0;height:13px;width:12px;cursor:pointer" onclick="javascript:gotoPrevMonth()"/>';
	  temp = this.header.insertCell(1);
	  temp.colSpan = 5;
	  temp.style.textAlign = 'center';	
	  var tempDiv = createElement('div');	
	  tempDiv.innerHTML = textStr;	
	  temp.appendChild(tempDiv);
	  temp = this.header.insertCell(2);
	  temp.innerHTML = '<img src="../images/cyan_right.gif" style="border:0;height:13px;width:12px;cursor:pointer" onclick="javascript:gotoNextMonth()"/>';
	}
	this.addWeek=function(){		
	  this.currentWeek = this.container.insertRow(this.container.rows.length);
	  this.currentWeek.style.padding='0';
	  this.weeks[this.weeks.length] = this.currentWeek;
	}
	this.addDate=function(dateLabel,styleClass){
	  dateCell = createElement('td');
	  dateStr = dateLabel<10?'0'+dateLabel:dateLabel;
	  thisDate = ''+this.year+this.month+dateStr;
	  dateCell.id = ''+this.year+this.month+dateStr;	
	  if(styleClass && styleClass.indexOf('mmDayNotInMonth ') == -1)
		  dateCell.innerHTML = '<a href="javascript:gotoDate(\''+thisDate+'\')">'+dateLabel+'</a>';
	  else
        dateCell.innerHTML =dateLabel;
	  dateCell.style.textAlign="center";
	  dateCell.className = styleClass;
	  this.days[this.days.length] = dateCell;
	  this.currentWeek.appendChild(dateCell);	  
	}
}
function populateWeekDays(calParams,prevWeek){
	var _d = calParams.weekdates.length == 0? calParams.getDateObj():calParams.weekdates[0];
	_d = new Date(_d.toString());
	var _day = _d.getDay();
	_d.setDate(_d.getDate() - _day);
	for(var i=0;i<7;i++){
		calParams.weekdates[i] = new Date(_d.toString());
		_d.setDate(_d.getDate()+1);
	}
  return calParams;
}

function closeEventBubble(){
	bubble = $('calendarEventBubble');
	if(bubble)
		bubble.style.display='none';
}
function populateEvents(calParams){
  var fromDate = findFromDate(calParams);
  var toDate = findToDate(calParams);
  var instAjax = new Ajax.Request(
    'getCalendarEvents.ajaxc', 
    {
	  method: 'get', 
	  parameters: 'element='+calParams._id+'&accountUser='+accountUserId+'&fromDate='+fromDate+'&toDate='+toDate+'&interval='+calParams._interval, 
	  onComplete: function(req) {loadEvents(calParams,req);} 
    });
	CalendarImpl(calParams._id,calParams);
}

function loadEvents(calParams,request){
	Tolven.CalendarEventsCache = new Map();
	dat = request.responseText.evalJSON();
	for(var i=0;i<dat.events.length;i++){
		addCalendarEvent(dat.events[i]); //AddCalendarEvent(DateStr(yearmonth), Description)
	}
  CalendarImpl(calParams._id,calParams);
  //Tolven.Util.log('found '+dat.events.length);
}
function getYearMonth(param){	
	dateparams = param.split('/');
    return ''+dateparams[2]+dateparams[0]+dateparams[1];
}

DailyCalendarGrid=function(calParams){
	this._container= createElement('table');
	this._container.style.width='100%';
	headerRow = this._container.insertRow(this._container.rows.length);
	headerCell = headerRow.insertCell(0);
	headerCell.colSpan = 2;
	headerCell.style.position = 'relative';
	headerCell.innerHTML = LayerCalendar.getLongWeekdayNames(calParams.getDateObj().getDay()+1, userLocale);
	headerCell.className = 'dailyCalHeader';
	
	var _gridRow = this._container.insertRow(this._container.rows.length);
	var _gridCell  = _gridRow.insertCell(0);
	var _grid = createElement('div');
	_gridCell.appendChild(_grid);
	_grid.className= 'dayGrid';

	var _dateStr = getDateAsString(calParams.getDateObj());
	eventObjs = Tolven.CalendarEventsCache.get(parseInt(_dateStr));
	var hrs = 0;
	var mr ="am";
	var _top = 0;
	for(var p=0;p<2;p++){
		var h =12; 
		for(var i=0;i<12;i++){
			var _hrDiv = createElement('div');
			_hrDiv.style.width= '5%';
			_hrDiv.className ="hourDiv"
			_hrDiv.innerHTML = h+mr;
			_hrDiv.style.top = _top+"px";
			var _eventDiv = createElement('div');
			_eventDiv.id = _dateStr+hrs+'_events';
			_eventDiv.className = 'eventLabel';
			_eventDiv.style.top = _top+"px";
		
			if(eventObjs)	
				for(var k=0;k<eventObjs.length;k++){
					if(parseInt(eventObjs[k]._start.getHours()) == hrs){
						var _obj = eventObjs[k];  // to pass to the event observer
						_obj._div.id =_obj.eventId = _obj.eventId +"_"+k;	
						eventObjs[k].index = k;
						_eventDiv.appendChild(eventObjs[k]._div);
						Event.observe(eventObjs[k]._div, 'click', function(clickEvent) {
							showBubbleForCalendar(_obj.eventId,_eventDiv,clickEvent);
						});
					}	
				}  
			hrs++;
			_top += 50;
			if(h==12) h =1;  else  h++;
			_grid.appendChild(_hrDiv);
			_grid.appendChild(_eventDiv);
		}	
		mer = "pm";
	}
	return this._container;
}

function MonthlyCalendarGrid(calParams){
	this._month = calParams.getMonth();
	this._year = calParams.getYear();
	this._container = createElement('table');
    this._container.style.width='100%';   
	var _cellWidth = (100/weekdays.length); 
	var _cellHeight = 100;  // cell height
	this._currentLeftOffset = 0;
	
	this._i = createElement('div');
	this._i.className='calgrid';
	this._i.id = calParams._id+"_grid";	

	this._e = createElement('div');
	this._e.className = "monthEventsContainerGrid";
	
	
	this._grid = createElement('div');
	this._grid.style.position = 'relative';

	var addHeader = function(_rowObj){  
		var cell_0 = _rowObj.insertCell(0);
		cell_0.style.position= 'relative';
		for(var i=1;i<=weekdays.length;i++){   // add weekdays to first row
			var _divElm = createElement('div');
			_divElm.className = 'colHeader';
			_divElm.innerHTML=LayerCalendar.getShortWeekdayNames(i,userLocale);
			_divElm.style.width = (_cellWidth-0.1)+"%";
			_divElm.style.left = this._currentLeftOffset?this._currentLeftOffset+"%":0; //if this._currentLeftOffset is not defined
			cell_0.appendChild(_divElm);
			this._currentLeftOffset +=_cellWidth;
		}
	}
	addHeader(this._container.insertRow(this._container.rows.length)); // week days 
	row = this._container.insertRow(this._container.rows.length); // insert date cells
	cell_1 = row.insertCell(0);
	cell_1.appendChild(this._grid);
	this._grid.appendChild(this._i);
	this._grid.appendChild(this._e);
	

	
// calculate top position %
	_rows = Math.ceil((lastdate+firstday)/weekdays.length);	  // 
	this._i.style.height= this._e.style.height =_rows*_cellHeight+"px";

	var leftPos = 0;	
	var addColSeparators = function(_gridElem){
	  for (var i=1;i<=weekdays.length;i++){   // Colomn separators
		vrule = createElement('div');
		vrule.className = 'vrule';
		vrule.style.left = (leftPos-0.1)+"%";
		_gridElem.appendChild(vrule);
		leftPos +=_cellWidth;
	  }
	}
	/* Adds div to the date cell for showing the event title*/
	var  addEvents = function(dateStr,eCell){
	  eventObjs = Tolven.CalendarEventsCache.get(parseInt(dateStr));
	  if(eventObjs){
		for(var k=0;k<eventObjs.length;k++){
			var _obj = eventObjs[k];  // to pass to the event observer
			_obj._div.id =_obj.eventId = _obj.eventId +"_"+k;	
			eventObjs[k].index = k;
			eventObjs[k]._div.innerHTML = eventObjs[k].eventObj.title;
			eCell.appendChild(eventObjs[k]._div);
			if(!eventObjs[k].eventObj.isDuration)
				eventObjs[k]._div.style.width = _cellWidth +"%"; 
			else {
				eventObjs[k]._div.style.width = '100%';
			}
			Event.observe(eventObjs[k]._div, 'click', function(clickEvent) {
				//showColor.style.background = colorSelect.options[colorSelect.selectedIndex].value;
				showBubbleForCalendar(_obj.eventId,eCell,clickEvent);
			});
		}
	  }
	 }
	addColSeparators(this._i);
	 
	this.topPos = 0;
	var _date = new Date(calParams.getDateObj().toString());
	_date.setDate(1);
	_date.setDate(_date.getDate() - _date.getDay());
	
	for(var r=0;r<_rows;r++){
		leftPos = 0;
	  for(var d=0;d<7;d++){
		cell = createElement('div');
		eCell = createElement('div');
		cell.id='day_'+i;
		cell.className = 'dayOfMonth';
		eCell.className= 'eventLabel';
		cell.style.left = eCell.style.left = leftPos + "%";
		cell.style.width= _cellWidth + "%";
		cell.style.top = eCell.style.top = this.topPos+"px";
		cell.style.height = eCell.style.height = _cellHeight+"px";
		this._i.appendChild(cell);
		this._e.appendChild(eCell);
		
		eCell.style.position= 'absolute';

		var _dateLabel = createElement('div');
		if(_date.getMonth() == calParams.getMonth())
			_dateLabel.className= 'dayInMonth';
		else
			_dateLabel.className= 'dayNotInMonth';
	
		_dateLabel.innerHTML = _date.getDate();
		cell.appendChild(_dateLabel);
		addEvents(getDateAsString(_date),eCell);		
		_date.setDate(_date.getDate()+1);
		leftPos += _cellWidth;
	 }		
	 addHRule(this._i,this.topPos);
	this.topPos += _cellHeight;
	}
	 addHRule(this._i,this.topPos);
	return this._container;
}
getDateAsString = function(_date){
	var _monthStr = _date.getMonth()+1;
	_monthStr = _monthStr <10?"0"+_monthStr:_monthStr ;
	var _dateStr = _date.getDate();
	_dateStr = _dateStr <10?"0"+_dateStr:_dateStr;	

	return ""+_date.getFullYear()+_monthStr+_dateStr;
}

function findFromDate(calParams){
	var _tempDate = new Date(calParams.getDateObj().toString());
	if(calParams._interval == 'DAY'){
	  _tempDate.setDate(_tempDate.getDate()-1);
	  return getDateAsString(_tempDate);
	}
	else
	 if(calParams._interval =='WEEK'){
		if(calParams.weekdates.length == 0)
		  calParams = populateWeekDays(calParams);
		return getDateAsString(calParams.weekdates[0]);
	 }else if(calParams._interval == 'MONTH'){
		_tempDate.setDate(1);
		return getDateAsString(_tempDate);
	 }
	 return '';
}

function findToDate(calParams){
  var _tempDate = new Date(calParams.getDateObj().toString());
	if(calParams._interval == 'DAY'){
	  _tempDate.setDate(_tempDate.getDate()+1);
	  return getDateAsString(_tempDate);
	}
	else
	 if(calParams._interval =='WEEK'){
		if(calParams.weekdates.length == 0)
		  calParams = populateWeekDays(calParams);
		return getDateAsString(calParams.weekdates[6]);
	 }else if(calParams._interval == 'MONTH'){
		_tempDate.setDate(NumDaysIn(calParams.getMonth(),calParams.getYear()));
		return getDateAsString(_tempDate);
	 }
	 return '';
}