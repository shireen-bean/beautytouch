


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
    
    $http.get('/machineListJson').
    success(function(data, status, headers, config) {
    	$scope.machines = data;
    	$scope.machines.forEach( function (machine) {
    		$http.get('/machineSaleCount?id=' + machine.id).
    		success(function (data) {
    			machine.num_sales = data;
    		});
    		$http.get('/machineSaleAverage?id=' +  machine.id).
    		success(function (data) {
    			machine.avg_sale = data;
    		});
    	});
    }).
    error (function (data, status, headers, config) {
    	
    });
    
    $scope.loadProduct = function(sku) {
    	window.location = "/productSales?sku=" + sku;
    };
    $scope.loadMachine = function(id) {
    	window.location = "/machineSales?id=" + id;
    };
}



