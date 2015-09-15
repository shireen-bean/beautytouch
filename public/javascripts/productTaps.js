


function productTaps($scope,$http) {

  var sku = getParameterByName("sku");

  $http.get('/productTapsList?sku=' + sku).
    success(function(data, status, headers, config) {
      $scope.events = data;
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



