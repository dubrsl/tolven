/**
*To select email id of the selected emergency account and to save values to trim.
*/
wizSelectEmergencyAccount=function(root) {
	var _accountSelected = $(root+':accountNameId');
	accountVal = _accountSelected.value.split("|");
	
	if (accountVal.length > 4) {
		var _account = accountVal[5];
		var _accountId = _account.split(" / ")[0];
		var _accountName = _account.split(" / ")[1];
		$(root+':accountId').value=_accountId;
		$(root+':accountName').value=_accountName;

		var instAjax = new Ajax.Request(
			'findEmergencyAccountMailId.ajaxea', 
			{
			   method: 'get', 
			   parameters: 'accountId=' + _accountId,
			   onComplete: function(req) {
			          	var _resultString = req.responseText;
			          	if (_resultString.indexOf('emailId=') != -1) {
							$(root+':accountEmail').value=_resultString.split("=")[1]; 
			          	}
					} 
			});
	}
}

/**
 * Removes current account from account selection dropdown.
 *
 * added on 02/07/2011
 * @author valsaraj
 * @param root - component tree root
 * @param accountId - account id
 * @param currentAccount - current account
 */
function removeCurrentAccountFromDropdown(root, accountId, currentAccount) {
	accounts = $(root + ':' + accountId);

	for (iter = 0; iter < accounts.options.length; iter++) {
		if (currentAccount==accounts.options[iter].text) {
			accounts.remove(iter);
		}
	}
}

/**
 * Synchronizes Procedures, Drug Allergies, Medications, Problems, Lab orders with emergency access account.
 *
 * added on 02/09/2011
 * @author valsaraj
 */
function synchronizeWithEmergencyAccount(root) {
	if ($(root + ':' + 'synchronizeCheck').checked == true) {
		var emergencyAccountDetailsObj = $(root + ':' + 'accountNameId');
		var selectedEmergencyAccountText = emergencyAccountDetailsObj.options[emergencyAccountDetailsObj.selectedIndex].text;
		wizShowAjaxLoader();
		var instAjax = new Ajax.Request(
			'synchronizeWithEmergencyAccount.ajaxea', 
			{
				method: 'get', 
				parameters: 'emergencyAccountDetails=' + selectedEmergencyAccountText,
			    onComplete: function(request) {
			    	wizHideAjaxLoader();
			    	
			    	if (request.responseText=='Success') {		    		
			    		displayPopup('Emergency access account', "Information synchronized with emergency access account successfully!", .30, .30);
			    	}
			    	else {
			    		displayPopup('Emergency access account', "Unable to synchronize information with emergency access account. Please try again later.", .30, .30);
			    	}
			    }
			});
	}
}