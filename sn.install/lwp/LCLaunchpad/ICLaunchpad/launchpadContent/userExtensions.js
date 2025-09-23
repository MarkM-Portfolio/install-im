<script>
/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2014                                          */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */


//This check can be passed as an argument to an secure functions 
//in the API that you call or any secure functions you define in
//secureUserExtensions.js
var userSecurityCheck = new Function('return window');

//Define user functions below
//For example
// 
// function myFunc()
// {
//   if (top.OS == "Linux")
//   {
//      alert("I am on linux");
//   }
// }
// 
// To use this function in your content, you must refer to it as top.myFunc()


// Example Functions to display license text.  For more details see the launchpad documentation 
var licenseDiv = 'licenseDialogDiv';
var licenseDialogId = 'licenseDialog' + Math.round(Math.random() * 1000000000);
var licenseDialogContent = '<div id="' + top.licenseDiv + '"></div>';
	
function myLicenseProvider(locale, id) {
  	return 'Sample license text: ' + id; //top.readTextFile(top.findFileInPaths(null, [locale, 'en'], id + '.txt'));
}
    	
function showMyLicensePanel() {

	top.showDialog(top.licenseDialogContent, {
		id: top.licenseDialogId,
		buttons: [
			{
				id: "licensePromptCancelButton",
				name: "licensePromptCancelButton",
				value: "Cancel",
				onclick: function(){ top.showDialog(top.licenseDialogId, false); alert('License declined.');}
			},
			{
				id: "licensePromptContinueButton",
				name: "licensePromptContinueButton",
				value: "Continue",
				onclick: function(){ top.showDialog(top.licenseDialogId, false); alert('License accepted.'); },
				enabled: false
			}

		],
		width: "550px"
	});

	top.createLicensePanel({ 
		id: top.licenseDiv,
		provider: top.myLicenseProvider,
		accept: function(){ top.document.getElementById('licensePromptContinueButton').disabled = true },
		decline: function(){ top.document.getElementById('licensePromptContinueButton').disabled = true },
		groups: [{id: 'IBM', name: 'IBM', licenses: ['license','license2']}, {id: 'nonIBM', name: 'non-IBM', licenses: ['nonIBMLicense1','nonIBMLicense2','nonIBMLicense3']}]
	});
	
}



</script>
