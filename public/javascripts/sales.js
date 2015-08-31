


function sales($scope,$http) {
    $http.get('/productListJson').
    success(function(data, status, headers, config) {
    	$scope.products = data;
    	console.log(data);
    	$scope.products.forEach( function(product) {
    		console.log(product);
    		$http.get('/productSaleCount?sku=' + product.item_sku).
    		success(function (data) {
    			product.num_sales = data;
    		});
    	});
    }).
    error(function (data, status, headers, config){
    	
    });
    $scope.loadProduct = function(sku) {
    	window.location = "/productSales?sku=" + sku;
    };
}



