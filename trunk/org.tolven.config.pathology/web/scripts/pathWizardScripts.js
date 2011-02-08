
//-------------------START : procedure wizard related script--------------------

// Save a new procedure added in the wizard.
wizCreateProcedure=function(widgetId,anchorId,element,root){
  stopAsync(root);
  var rootForm = $(root);
  var currentStep = 1 * rootForm.getAttribute('currentStep');
  var lastStep = 1 * rootForm.getAttribute('lastStep');
  var tblElm = $(widgetId+"TBL");
  var _proc = $(root+':procedure');
  var _lat = $(root+':laterality');
  var _loc = $(root+':location');
  var _newRowStr = "";
  var _params = "";
  if($F(_proc) != ""){ // not possible !!
    _newRowStr+= "<td>"+ _proc.options[_proc.selectedIndex].text+"</td>";
    _params+= '&template='+$F(_proc)+'&procTitle='+_proc.options[_proc.selectedIndex].text;
  }
  if(!_lat.disabled){
    _newRowStr+= "<td>"+ _lat.options[_lat.selectedIndex].text+"</td>";
    _params+= '&laterality='+$F(_lat)+'&latTitle='+_lat.options[_lat.selectedIndex].text;
  }
  if(!_loc.disabled){
    _newRowStr+= "<td>"+ _loc.options[_loc.selectedIndex].text+"</td>";
    _params+= '&location='+$F(_loc)+'&locTitle='+_loc.options[_loc.selectedIndex].text;
  }
  _params+="&methodCode="+$CF(root,root+":methodCode");
  var newRow = tblElm.insertRow(tblElm.rows.length-2);
  $(newRow).update(_newRowStr);
  var instAjax = new Ajax.Request(
    'saveUpdateProcedure.ajaxp',
    {
    method: 'get',
    parameters: 'actionType=0&element='+element+_params,
    onSuccess: function(req) {
		ajaxSubmit4(rootForm, element, currentStep);
	},
    onFailure: function(request) { displayError(request,element+_params);}
    });
  $(widgetId).hide();
 
}

wizUpdateProcedure = function(element,root,procIndex){
  stopAsync(root);
  var rootForm = $(root);
  var currentStep = 1 * rootForm.getAttribute('currentStep');
  var _proc = $(root+':procedure'+procIndex).value;
  var _procTitle = $(root+':procedureTitle'+procIndex).innerHTML;
  var _lat = $(root+':laterality'+procIndex);
  var _loc = $(root+':location'+procIndex);
  var _newRowStr = "";
  var _params = "";
	_newRowStr+= "<td>"+ _procTitle+"</td>";
	_params+= '&template='+_proc+'&procTitle='+_procTitle;

  if(!_lat.disabled){
    _newRowStr+= "<td>"+ _lat.options[_lat.selectedIndex].text+"</td>";
    _params+= '&laterality='+$F(_lat)+'&latTitle='+_lat.options[_lat.selectedIndex].text;
  }
  if(!_loc.disabled){
    _newRowStr+= "<td>"+ _loc.options[_loc.selectedIndex].text+"</td>";
    _params+= '&location='+$F(_loc)+'&locTitle='+_loc.options[_loc.selectedIndex].text;
  }
  // add method codes to save
  _params+="&methodCode="+$CF(root,root+":methodCode"+procIndex+"menu");
  $("save"+procIndex).update(_newRowStr);
  var instAjax = new Ajax.Request(
    'saveUpdateProcedure.ajaxp',
    {
    method: 'get',
    parameters: 'actionType=1&element='+element+'&procedureIndex='+procIndex+_params,
	onSuccess: function(req) {
		setTimeout(function(){refreshWizard(element,root,currentStep);},50);
	},
    onFailure: function(request) {displayError(request,element);}
    });
  $("save"+procIndex).show();
  $("edit"+procIndex).hide();
  startAsync(root);
}
wizCancelCreateProcedure = function(widgetId,anchorDivId){
  $(widgetId).hide();
  $(anchorDivId).show();
}
wizCancelUpdateProcedure=function(procIndex){
  $("save"+procIndex).toggle();
  $("edit"+procIndex).toggle();
}
wizRemoveProcedure = function(element,root,procIndex){
  var rootForm = $(root);
  var currentStep = 1 * rootForm.getAttribute('currentStep');
  var lastStep = 1 * rootForm.getAttribute('lastStep');
  $("edit"+procIndex).remove();
  $("specimen"+procIndex).remove();
  var instAjax = new Ajax.Request(
    'saveUpdateProcedure.ajaxp',
    {
    method: 'get',
    parameters: 'actionType=2&element='+element+'&procedureIndex='+procIndex,
    onSuccess: function(req) {
		setTimeout(function(){refreshWizard(element,root,currentStep); },50);
	},
    onFailure: function(request) {displayError(request,element+'actionType=remove');}
    });
}
wizSelectProcedure=function(root,procIndex){
  if(procIndex == undefined)  // new procedure
    procIndex = "";
  var _trimName = $F(root+":procedure"+procIndex);
  if(_trimName == ""){
    $('saveProc'+procIndex).disabled = true;
    $(root+':laterality'+procIndex).disabled = true;;
    $(root+':location'+procIndex).disabled = true;;
    return;
  }else{
    $('saveProc'+procIndex).removeAttribute('disabled');
  }
  var instAjax = new Ajax.Request(
    'getTrimOptions.ajaxp',
    {
    method: 'get',
    parameters: 'trimName='+_trimName,
    onSuccess: function(req){wizGotProcedureOptions(req,root,procIndex); }
    });
}
wizSelectLaterality = function(root,procIndex){
  if(!procIndex)  // new procedure
    procIndex = "";
   var _lat = $F(root+":laterality"+procIndex);
   var _loc = $(root+":location"+procIndex);

   if(_lat.indexOf('UNK') > -1 || _loc.options.length == 1){
     $(root+":location"+procIndex).disabled = true;
   }else{
    $(root+":location"+procIndex).removeAttribute("disabled");
   }
}
wizGotProcedureOptions = function( request,root, procIndex ) {
  var _options = request.responseText.evalJSON();
    //clear method codes
    if(!procIndex)   // new procedure
      $("method"+procIndex).update("");
    if(procIndex && !_options.methodCode){
      $("method"+procIndex).update("");
    }
    for(prop in _options){
      if( _options.methodCode){
        if(procIndex){ // update procedure
          if($("method"+procIndex).innerHTML =="")
            $("method"+procIndex).update("<span style='color:red;'>save procedure to edit methods</span>");
        }else{ // create new procedure
          var _methodCode = _options.methodCode;
          var _methodTitle = _options.methodCodeTitle;
          if(prop == _methodCode){
            var _methods = _options[prop].split(",");
            var _methodHTML = "<table><tr>";
            _methodHTML += "<tr><td><b>"+_methodTitle+"</b></td></tr><tr><td>";
            for(i=0;i<_methods.length;i++){
              var _method = _methods[i].split("|");
                _methodHTML+="<input type='checkbox' name='"+root+":methodCode"+procIndex+"' value='"+_methods[i]+"' ";
                if(_method[2]=='true') _methodHTML+="checked='true'";
                _methodHTML+=">"+_method[_method.length-1]+"</input>";
              //if(i%2 ==1)
              //  _methodHTML+="</tr><tr>";
            }
            $("method"+procIndex).update(_methodHTML+"</td></tr></table>");
          }
        }
      }
    var _sel = $(root+":"+prop+procIndex);
    if(_sel == null){
      continue;
    }
    _sel.options.length = 0;
    _sel.disabled = true;
    if(_options[prop] == ""){
      var _opt = new Option("Select", "");
      _sel.options.add(_opt);
      continue;
    }else{
      var _opts = _options[prop].split(",");
      for(var i=0;i<_opts.length;i++){
        var _optVals = _opts[i].split("|");
        var _opt = new Option(_optVals[_optVals.length-1], _opts[i]);
        _sel.options.add(_opt);
        _sel.removeAttribute("disabled");
      }
    }
  }
}
wizEditProcedure = function(element,procIndex){
  var _saveProc = $("save"+procIndex);
  var _editProc = $("edit"+procIndex);
  wizSelectProcedure(element,procIndex);
  $(_saveProc).hide();
  $(_editProc).show();

}
wizAddProcedure=function(procWidgetId,anchorId,root){
  wizSelectProcedure(root);
  $(anchorId).hide();
  $(procWidgetId).show();
}
wizDisableContainer=function(elementId,containerType,root){
  //alert(elementId+"   "+containerType);
  if($(root+":"+elementId) == null)
    return;
  var _ansectors = $(root+":"+elementId).ancestors();
  var _fieldsToDisable;
  for(i=0;i<_ansectors.length;i++){
    if(_ansectors[i].tagName.toLowerCase() == containerType){
      _fieldsToDisable = _ansectors[i].descendants();
      break;
    }
  }
  if(!_fieldsToDisable)
    return;
  for(i=0;i<_fieldsToDisable.length;i++){
    if(_fieldsToDisable[i].tagName.toLowerCase() == 'input' || _fieldsToDisable[i].tagName.toLowerCase() == 'select'){
      if(_fieldsToDisable[i].id != root+":"+elementId){
        if($(root+":"+elementId).checked){
          _fieldsToDisable[i].removeAttribute("disabled");
        }else{
          _fieldsToDisable[i].disabled = true;
          if($(_fieldsToDisable[i]).className && ($(_fieldsToDisable[i]).className.indexOf('pNode') > -1 ||$(_fieldsToDisable[i]).className.indexOf('eNode') > -1)){
						if($(_fieldsToDisable[i]).value!=0){
							$(_fieldsToDisable[i]).value = 0;
							eval($(_fieldsToDisable[i]).getAttribute("onkeyup"));
						}
		  		}
        }
      }
    }
  }
}
wizHistologySelector = function(tblId,root){
  var _tbls = $(tblId).getElementsBySelector("table.result");
  for(i=0;i<_tbls.length;i++){
    var _selectedValue = "";
    $(_tbls[i]).getElementsBySelector("input[type='radio']").each(function(item){
        if(item.checked){
          _selectedValue = item.value.substring(item.value.lastIndexOf("|")+1);
        }
    });

    if(_selectedValue == 'Yes'){
      for(j =0;j<_tbls.length;j++){
        if(i != j){
          $(_tbls[j]).getElementsBySelector("input[type='radio']").each(function(item){
              //item.setAttribute("disabled","true");
          });
        }
      }
      break;
    }else{
      _tbls.each(function(item){
          $(item).getElementsBySelector("input[type='radio']").each(function(nitem){
              //item.removeAttribute("disabled");
          });
      });
    }
  }
}
calculateNodes = function(nodeType,totalType,root){
  var _nodes = $$('form#'+root+' input.'+nodeType);
  var _totalNode = $(root+':'+totalType);
  var _totalNodeLBL =  $$('form#'+root+' label#'+totalType+'LBL');;
  var _total = 0;
  for(i=0;i<_nodes.length;i++){
    if(_nodes[i].value && !isNaN(_nodes[i].value))
      _total += parseInt(_nodes[i].value);
  }
  _totalNode.value = _total;
  $(_totalNodeLBL)[0].update(_total);
}

calulateStaging = function(laterality,root){
  var _tumorCE = $RF(root,root+":"+"stagingTumor"+laterality+"Field");
  var _tumor = _tumorCE? _tumorCE.substring(_tumorCE.lastIndexOf("|")+1):"";
  var _stgNodeCE = $RF(root,root+":"+"stagingNode"+laterality+"Field");
  var _stgNode = _stgNodeCE?_stgNodeCE.substring(_stgNodeCE.lastIndexOf("|")+1):"";
  var _stgMetastasisCE = $RF(root,root+":"+"stagingMetastasis"+laterality+"Field");
  var _stgMetastasis = _stgMetastasisCE? _stgMetastasisCE.substring(_stgMetastasisCE.lastIndexOf("|")+1):"";
  var _stgModifCE = $RF(root,root+":"+"stagingModifiers"+laterality+"Field");
  var _stgModif = _stgModifCE?_stgModifCE.substring(_stgModifCE.lastIndexOf("|")+1).charAt(0).toLowerCase():"";
  var _stgDescCE = $CF(root,root+":"+"stgDesr"+laterality);

  var _stgCode = _stgModif+_tumor+_stgNode+_stgMetastasis;

  var _calStgStr = "";
  if(_stgMetastasis == 'M0'){
    if(_stgNode.indexOf('N0') > -1){
      if(_tumor.indexOf('Tis') > -1)
        _calStgStr = 'Stage 0';
      else if(_tumor.indexOf('T1') > -1 )
        _calStgStr = 'Stage I';
      else if(_tumor.indexOf('T2') > -1)
        _calStgStr = 'Stage IIA';
      else if(_tumor.indexOf('T3') > -1)
        _calStgStr = 'Stage IIB';
      else if(_tumor.indexOf('T4') > -1)
        _calStgStr = 'Stage IIIB';
    } else if(_stgNode.indexOf('N1') > -1){
      if(_tumor.indexOf('T1') > -1 || _tumor.indexOf('T0') > -1)
        _calStgStr = 'Stage IIA';
      else if(_tumor.indexOf('T2') > -1)
        _calStgStr = 'Stage IIB';
      else if(_tumor.indexOf('T3') > -1)
        _calStgStr = 'Stage IIIA';
      else if(_tumor.indexOf('T4') > -1)
        _calStgStr = 'Stage IIIB';
    } else if(_stgNode.indexOf('N2') > -1){
      if(_tumor.indexOf('T1') > -1 || _tumor.indexOf('T0') > -1 || _tumor.indexOf('T2') > -1 || _tumor.indexOf('T3') > -1)
        _calStgStr = 'Stage IIIA';
      else if(_tumor.indexOf('T4') > -1)
        _calStgStr = 'Stage IIIB';
    } else if(_stgNode.indexOf('N3') > -1){
      if(_tumor.indexOf('T') > -1)
        _calStgStr = 'Stage IIIC';
    }
  }else if(_stgMetastasis == 'M1'){
    if(_stgNode.indexOf('N') > -1)
      if(_tumor.indexOf('T') > -1)
        _calStgStr = 'Stage IV';
  }
  $$('form#'+root+' label#'+'CalStgLBL'+laterality)[0].update(_calStgStr);
  $(root+':'+'CalStg'+laterality).value=_calStgStr;

  $$('form#'+root+' label#'+'StgCodeLBL'+laterality)[0].update(_stgCode);
  $(root+':'+'StgCode'+laterality).value=_stgCode;
  //var _opts = $(root+":AdjStg"+laterality).options;
  //for(i=0;i<_opts.length;i++){
  //  var _value = _opts[i].value.split("|");
  //  if(_value[_value.length-1] == _calStgStr){
  ///    $(root+":AdjStg"+laterality).selectedIndex = i;
  //    return;
  //  }
 // }
 // $(root+":AdjStg"+laterality).selectedIndex = 0;
}

/*
  param1 receptor index
  param2 form
  get the receptors scores for class name is rscore#{receptorIndex.index}#{lateralityIndex.index}
*/
wizCalculateReceptorTotal = function(rindex,root){
    var _total = 0;
    var _nodes =  $$('form#'+root+' select.rscore'+rindex).each(function(item) {
        var _tempVal = $F(item);
        _total += parseInt(_tempVal.substring(_tempVal.lastIndexOf("|")+1));
    });
    $("TSLBL"+rindex).update(_total);
    $(root+":TS"+rindex).value=_total;
}


 function $RF(root, radioGroup) {
  var _nodes = $$('form#'+root+' input[type=radio][name='+radioGroup+']');
  var _values= new Array();
  for(i=0,j=0;i<_nodes.length;i++){
    if(_nodes[i].checked){
      return _nodes[i].value;
    }
  }
  return null;
  }

  // finds the selectedIndex of radio group
 function $RI(root, radioGroup) {
  var _nodes = $$('form#'+root+' input[type=radio][name='+radioGroup+']');
  var _values= new Array();
  for(i=0;i<_nodes.length;i++){
    if(_nodes[i].checked){
      return i+1;
    }
  }
  return -1;
  }


 function $CF(root, checkboxGroup) {
  var _nodes;
  if(root && checkboxGroup)
    _nodes = $$('form#'+root+' input[type=checkbox][name='+checkboxGroup+']');
  else
    _nodes = $$('input[type=checkbox][name='+checkboxGroup+']');
  var _values= new Array();
  for(i=0,j=0;i<_nodes.length;i++){
    if(_nodes[i].checked){
      _values[j++] = _nodes[i].value;
    }
  }
  return _values;
  }
  function wizCalculateInvasiveTumor(lateralityIndex,root){
    var _ng = $RI(root,root+":invasiveNuclearGrade"+lateralityIndex+"ceField");
    var _mc = $RI(root,root+":mitoticCountID"+lateralityIndex+"ceField");
    var _tf =  $RI(root,root+":tubeleFormationID"+lateralityIndex+"ceField");
    var _total = _ng+_mc+_tf;
    var _grade = "";
    if(_ng > 3 || _mc > 3 || _tf > 3){
      $$('form#'+root+' label#invasiveTumorTotalLBL'+lateralityIndex).each(function(item){        item.update(" N/A"); });
      $$('form#'+root+' label#invasiveTumorGradeLBL'+lateralityIndex).each(function(item){        item.update(" N/A"); });
      $(root+':invasiveTumorTotal'+lateralityIndex).value = " N/A";
      $(root+':invasiveTumorGrade'+lateralityIndex).value =" N/A";
      //invasiveTumorTotal
    }else{
      $$('form#'+root+' label#invasiveTumorTotalLBL'+lateralityIndex).each(function(item){item.update(" "+_total); });
      $(root+':invasiveTumorTotal'+lateralityIndex).value = " "+_total;
      if(_total == 3 || _total == 4 || _total == 5  )
        _grade ="I";
      else  if(_total == 6 || _total == 7 )
        _grade ="II";
      else if(_total == 8 || _total == 9  )
        _grade ="III";
      else
        _grade ="N/A";
      $$('form#'+root+' label#invasiveTumorGradeLBL'+lateralityIndex).each(function(item){item.update(" "+_grade);});
      $(root+':invasiveTumorGrade'+lateralityIndex).value =" "+_grade;
    }
  }


  function addSpecimen(element,procIndex,template,formId){
  var rootForm = $(formId);
  var currentStep = 1 * rootForm.getAttribute('currentStep');
  var lastStep = 1 * rootForm.getAttribute('lastStep');
   var instAjax = new Ajax.Request(
    'modifySpecimen.ajaxp',
    {
    method: 'get',
    parameters: 'element='+element+'&procedureIndex='+procIndex+'&template='+template+'&actionType='+0,
    onSuccess: function(req) {
		setTimeout(function(){refreshWizard(element,formId,currentStep); },50);
	},
	onFailure: function(request) {displayError(request,element);}
    });
}
function removeSpecimen(element,procIndex,removeIndex,formId){
  var rootForm = $(formId);
  var currentStep = 1 * rootForm.getAttribute('currentStep');
  var lastStep = 1 * rootForm.getAttribute('lastStep');
   var instAjax = new Ajax.Request(
    'modifySpecimen.ajaxp',
    {
    method: 'get',
     parameters: 'element='+element+'&procedureIndex='+procIndex+'&specimenIndex='+removeIndex+'&actionType='+1,
    onSuccess: function(req) {
		setTimeout(function(){refreshWizard(element,formId,currentStep); },50);
	},
    onFailure: function(request) {displayError(request,element);}
    });
}
function wizEnableHistologyBenign(elem,toEnable){
	if(elem.value.substring(elem.value.lastIndexOf('|')+1) == 'Yes')
		$(toEnable).value=true;
	else
		$(toEnable).value=false;
}
//-------------------END : procedure wizard related script ---------------------