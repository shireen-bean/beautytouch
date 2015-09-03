


function machineSales($scope,$http) {

  var id = getParameterByName("id");

  $http.get('/machineSalesList?id=' + id).
    success(function(data, status, headers, config) {
      console.log(data);
      $scope.sales = data;
      $scope.sales.forEach( function (sale) { 
    	  //get products for sale
    	  $http.get('/productNameBySale?id=' + sale.id).
    	  success (function(data, status, headers, config) {
    		  sale.product_name = data;
    	  });
      });
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



