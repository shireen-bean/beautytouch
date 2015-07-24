function activityLogController($scope,$http) {
	
	$scope.machineId = getParameterByName("machineId");
	$scope.salesCheck=true;
	$scope.actionsCheck=true;
	$scope.trafficCheck=true;
	
	$scope.loadData = function(){
		var startDate = $("#datepickerStart").val();
		var endDate = $("#datepickerEnd").val();
		
		startDate = startDate.replaceAll("/","-");
		endDate = endDate.replaceAll("/","-");
		
	  
		$http.get('/activityLogData?machineId='+$scope.machineId+'&startDate='+startDate+'&endDate='+endDate).
		  success(function(data, status, headers, config) {
			  console.log(data);
			  $scope.entries = data;
		  }).
		  error(function(data, status, headers, config) {
		  });
	}
	
	$(document).ready(function() {
		var height = $(document).height();
		$(".container").height(height-350);
		
		//set active tab in sidebar
		$("#machine").addClass("active");
		
			//initialize datepickers
		
		    $( "#datepickerStart" ).datepicker();
		    $( "#datepickerEnd" ).datepicker();
		    $('#datepickerEnd').datepicker("setDate", new Date() );
		    var d = new Date();
		    d.setDate(d.getDate()-5);
		    $('#datepickerStart').datepicker("setDate",d)
		    
		    //load data
		    $scope.loadData();
	});
	
	$scope.reloadData=function(){
		$scope.loadData();
	}
	
	
	
	
}
//end scope

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

function getParameterByName(name) {
    name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
    var regex = new RegExp("[\\?&]" + name + "=([^&#]*)"),
        results = regex.exec(location.search);
    return results === null ? "" : decodeURIComponent(results[1].replace(/\+/g, " "));
}



