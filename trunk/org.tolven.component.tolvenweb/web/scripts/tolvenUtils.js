
/* Color picker*/


Tolven.ColorPicker = function(){
	this._container = createElement('table');
	this._container.style.width="3em"; 
	var rowObj = this._container.insertRow(0);
	var selectCell = rowObj.insertCell(0);
	this._colorSelect = createElement('select');
	this._colorSelect.style.width="7em"; 

	var colorSelect = this._colorSelect; 
	selectCell.appendChild(this._colorSelect);
	this._colorSelect.id = '_colorSelect'; 
	this._showColor = createElement('li');
	var showColor = this._showColor
	this._showColor.id = '_showColor ';
	this._showColor.style.width="4em";
	this._showColor.style.height="1.5em";
	var colorCell = rowObj.insertCell(1);
	colorCell.style.listStyle='none';	
	colorCell.appendChild(this._showColor);
	this._selectedColor = null;
	this._colors = ['#99FFCC','#CCFFFF','#D4FFD4','#E0E0E0','#996699','#FFFFAD','#99FF99','#99CC99'];
	for(var x=0;x<this._colors.length;x++){
		var _option = new Option(this._colors[x],this._colors[x]);
		_option.style.background=this._colors[x];
		this._colorSelect.options[x] = _option;
	}
	 Event.observe(this._colorSelect, 'change', function() {
		showColor.style.background = colorSelect.options[colorSelect.selectedIndex].value;
	  });
};
Tolven.ColorPicker.prototype.setColorPicker=function(element){
	element.appendChild(this._container);
}
Tolven.ColorPicker.prototype.setColor=function(colorHexVal){
	var newColor = true;
	for(var x=0;x<this._colors.length;x++){
		if(this._colors[x] == colorHexVal){
			newColor = false;
			this._colorSelect.selectedIndex = x;			
		}
	}
	if(newColor){
		var _option = new Option(colorHexVal,colorHexVal);
		_option.style.background=colorHexVal;
		_option.selected = true;
		this._colorSelect.options[this._colorSelect.options.length] = _option;
	}
	this._showColor.style.background = colorHexVal; 
}