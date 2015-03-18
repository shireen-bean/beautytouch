function checkoutProductSelect($scope,$http) {
//	setTimeout(function() {
//		window.location="/checkoutProductSelect?machineId="+getParameterByName("machineId");
//	}, 1000);
	
	$scope.availableProducts=[];
	
	//get list of available products
	var machineID=getParameterByName("machineId");
	
	$http.get('/machineJson?id='+machineID).
	  success(function(data, status, headers, config) {
		    $scope.machine = data;
			$(document).ready(function(){
				var alreadyListedProd = "";
				var lengthContainers = $scope.machine.containers.length;
				for(var i=0; i<lengthContainers;i++){
					var currentSku = $scope.machine.containers[i].product.itemSku;
					
					if(!(alreadyListedProd.indexOf(currentSku)>=0)){
						if($scope.machine.containers[i].numItems>0){
						    $scope.availableProducts.push($scope.machine.containers[i].product);
							alreadyListedProd+=currentSku+",";
						}
					}
				}
				
				var lengthUniqueSkus = $scope.availableProducts.length;
				var halfUnique = Math.ceil(lengthUniqueSkus/2);
				var widthWindow = $(window).width();
				$(".availableListDiv").width("1%");
			});
	  });
	

	
    $scope.productSelected=function(id){

		var divNameSelected = "#product"+id;


//		var selectedWidth = $(divNameSelected+" img").width();
//		var selectedWidthReg = $(divNameSelected).width();
//		$(divNameSelected+" img").width(selectedWidth+'px');
//		$(divNameSelected).width(selectedWidthReg+'px');
//		$(divNameSelected).css('position','absolute');
//		$(divNameSelected).css('z-index','1000');
		//$(divNameSelected).animate({marginTop:"1000px"},1000,function(){
			for(var i=0;i<$scope.availableProducts.length;i++){
				//if(!($scope.availableProducts[i].itemSku==id)){
					var divName = "#product"+$scope.availableProducts[i].itemSku;
//					if(i==($scope.availableProducts.length-1)){
			    		$( divName).hide(function(){
			    				  window.location="/pay?machineId="+machineID+"&productId="+id;
			    		});
//					}else{
//			    		$( divName).hide(function(){
//		    				  window.location="/pay?machineId="+machineID+"&productId="+id;
//			    		});
//					}
				//}
				
				
			}
		//});

		    	
    	$scope.selectedId=id;
    }
}
function getParameterByName(name) {
    name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
    var regex = new RegExp("[\\?&]" + name + "=([^&#]*)"),
        results = regex.exec(location.search);
    return results === null ? "" : decodeURIComponent(results[1].replace(/\+/g, " "));
}
