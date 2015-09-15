


function engagements($scope,$http) {
  $http.get('/productListJson').
    success(function(data, status, headers, config) {
      $scope.products = data;
      console.log(data);
      $scope.products.forEach( function(product) {
        console.log(product);
        $http.get('/productTapCount?sku=' + product.item_sku).
        success(function (data) {
        	console.log(data);
          product.num_taps = parseInt(data);
        });
      });
    }).
  error(function (data, status, headers, config){

  });

  $http.get('/machineListJson').
    success(function(data, status, headers, config) {
      $scope.machines = data;
      $scope.machines.forEach( function (machine) {
        $http.get('/machineTapCount?id=' + machine.id).
        success(function (data) {
          machine.num_product = data.product;
          machine.num_about = data.about;
          machine.num_report = data.report;
        });
      });
    }).
  error (function (data, status, headers, config) {

  });

  $scope.loadProduct = function(sku) {
    window.location = "/productTaps?sku=" + sku;
  };
  $scope.loadMachine = function(id) {
    window.location = "/machineTaps?id=" + id;
  };
}



