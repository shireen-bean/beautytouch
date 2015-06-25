function brandListController($scope,$http) {
  $http.get('/brandListJson')
    .success(function(data, status, headers, config) {
      $scope.brands = data;
      })
    .error(function(data, status, headers, config) {
    });
  $scope.loadBrand = function(id){
    window.location = "/brand?id="+id;
  };
}

$(document).ready(function(){
	//set active tab in sidebar
	$("#brandList").addClass("active");
});
