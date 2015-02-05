function productListController($scope,$http) {
    	$http.get('/productListJson').
    	  success(function(data, status, headers, config) {
    		  $scope.products = data;
    	  }).
    	  error(function(data, status, headers, config) {
    	  });
    $scope.loadProduct = function(sku){
    	window.location = "/product?sku="+sku;
    };
}

$(document).ready(function(){
	//set active tab in sidebar
	$("#productList").addClass("active");
});
