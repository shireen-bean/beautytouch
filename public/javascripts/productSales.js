


function productSales($scope,$http) {
	
  var sku = getParameterByName("sku");
  
  $http.get('/productSalesList?sku=' + sku).
    success(function(data, status, headers, config) {
      console.log(data);
      $scope.sales = data;
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



