function payController($scope,$http) {
//	$(document).ready(function(){
//		$(".blueBackground").fadeIn(1000,function(){});
//	});
	
	//loadProductDetails
	$(document).ready(function(){
      	setTimeout(
  			  function() 
  			  {
				$http.get('/productJson?sku='+getParameterByName("productId")).
			  	  success(function(data, status, headers, config) {
			  		$scope.product=data;
		        				 var image = $scope.product.itemImg;
		        				 $scope.product.itemImg = image;
		        				 $(".completePurchaseImage").show();
			  	  }).
			  	  error(function(data, status, headers, config) {
			  		  console.log("error");
			  	  });

			  }, 100);
		
	});
}
function back(){
	window.location="/vendingMain?machineId="+getParameterByName("machineId");
}
function getParameterByName(name) {
    name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
    var regex = new RegExp("[\\?&]" + name + "=([^&#]*)"),
        results = regex.exec(location.search);
    return results === null ? "" : decodeURIComponent(results[1].replace(/\+/g, " "));
}
