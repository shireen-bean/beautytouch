function thankYouController($scope,$http) {
	$scope.skipReceipt=function(){
		window.location="/checkoutProductSelect?machineId="+getParameterByName("machineId");
	}
	$scope.submitEmail=function(){
		if(!validateEmail($scope.emailAddress)){
			$scope.emailError="Invalid email";
		}else{
			console.log("%OASYS,screen=receiptEmail&phoneNumber="+$scope.emailAddress+"?");
			window.location="/checkoutProductSelect?machineId="+getParameterByName("machineId");
		}
	}
	$scope.submitPhoneNumber=function(){
		if($scope.phoneNumber==null){
			$scope.phoneError="Invalid phone number";}
		else if(!validateNumber($scope.phoneNumber)){
			$scope.phoneError="Invalid phone number";
		}else{
			var number=$scope.phoneNumber.replaceAll("-","").replaceAll("(","").replaceAll(")","").replaceAll(" ","");
			console.log("%OASYS,screen=receiptSMS&phoneNumber="+number+"?");
			window.location="/checkoutProductSelect?machineId="+getParameterByName("machineId");
		}
	}
}
$(document).ready(function(){
	$("#message").fadeIn();
	setTimeout(function() {
		window.location="/checkoutProductSelect?machineId="+getParameterByName("machineId");
	}, 200000);
});

function validateEmail(email) {
    var re = /^([\w-]+(?:\.[\w-]+)*)@((?:[\w-]+\.)*\w[\w-]{0,66})\.([a-z]{2,6}(?:\.[a-z]{2})?)$/i;
    return re.test(email);
}

function validateNumber(inputtxt)  
{  
  var phoneno = /^\(?([0-9]{3})\)?[-. ]?([0-9]{3})[-. ]?([0-9]{4})$/;  

  if(inputtxt.match(phoneno)){  
	  console.log("valid");
      return true;  
        }  
      else  
        {  
    	  console.log("invalid");
        return false;  
        }  
}  

function getParameterByName(name) {
    name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
    var regex = new RegExp("[\\?&]" + name + "=([^&#]*)"),
        results = regex.exec(location.search);
    return results === null ? "" : decodeURIComponent(results[1].replace(/\+/g, " "));
}

String.prototype.replaceAll = function(search, replace, ignoreCase) {
	  if (ignoreCase) {
	    var result = [];
	    var _string = this.toLowerCase();
	    var _search = search.toLowerCase();
	    var start = 0, match, length = _search.length;
	    while ((match = _string.indexOf(_search, start)) >= 0) {
	      result.push(this.slice(start, match));
	      start = match + length;
	    }
	    result.push(this.slice(start));
	  } else {
	    result = this.split(search);
	  }
	  return result.join(replace);
}

