


function machineTaps($scope,$http) {

  var id = getParameterByName("id");

  $http.get('/machineTapsList?id=' + id).
    success(function(data, status, headers, config) {
      $scope.events = data;
      $scope.events.forEach( function (event) {
    	  $http.get('/productName?sku=' + event.product_sku)
    	  .success( function(data, status, headers, config) {
    		  event.product_name = data;
    	  })
      })
    }).
    error(function (data, status, headers, config){
    });

  function getParameterByName(name) {
	    name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
	    var regex = new RegExp("[\\?&]" + name + "=([^&#]*)"),
	        results = regex.exec(location.search);
	    return results === null ? "" : decodeURIComponent(results[1].replace(/\+/g, " "));
	  }
}



