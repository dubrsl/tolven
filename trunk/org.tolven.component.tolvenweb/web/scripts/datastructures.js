
// Map Datastructure implementation for string literals
Map = function(){
	this._keys = new Array();
	this._values = new Array();
}
Map.prototype.put = function(_key,_value){
	this._keys[this._keys.length] = _key;
	this._values[this._values.length] = _value;
}
Map.prototype.get = function(_key)
{
	for(var i=0;i<this._keys.length;i++)
		if(this._keys[i] == _key)
			return this._values[i];
	return null;
}
Map.prototype.size = function(){
	return this._keys.length;
}
Map.prototype.contains = function(searchValue){
	for(var i = 0;i<this._keys.length;i++)
		if(this._keys[i] == searchValue)
			return true;
	return false;
}
Map.prototype.elementAt = function(index){
	if(index > this._keys.length)
		return null;  // should we return -1
	return this._values[index];
}
Map.prototype.keyAt = function(index){
	if(index > this._keys.length)
		return null;  // should we return -1?
	return this._keys[index];
}
Map.prototype.findKeyIndex = function(_key){
	for(var i=0;i<this._keys.length;i++)
		if(_key == this._keys[i])
			return i;
	return -1;
}
Map.prototype.remove = function(_key){
	var i = this.findKeyIndex(_key);
	if( i == -1)
		return false;
	this._keys.splice(i,1);
	this._values.splice(i,1);
	return true;
}
