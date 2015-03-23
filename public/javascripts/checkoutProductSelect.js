function checkoutProductSelect($scope,$http) {
	setTimeout(function() {
		window.location="/checkoutProductSelect?machineId="+getParameterByName("machineId");
	}, 30000);
	
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
				//$(".availableListDiv").width("1%");
			});
	  });
	
	$(document).ready(function(){
		setTimeout(function() {
		      // Do something after 5 seconds
			//same color:background:#EDE9D8'
			$("body").prepend("<div style='width:100%;height:100%;position:absolute;background:rgb(56, 151, 186);'></div>")
			
		}, 1000);
		});
	
    
    $scope.productSelected=function(id){
    	//$("body").hide();
		var divNameSelected = "#product"+id;
		
		var lengthContainers = $scope.machine.containers.length;
		for(var i=0; i<lengthContainers;i++){
			var currentSku = $scope.machine.containers[i].product.itemSku;
			if(currentSku==id){
		    	var columnWithProduct = i+1;
		    	break;
			}else{
				var columnWithProduct = 0;
			}
		}
		
		if(columnWithProduct!=0){
			var slot = 0;
	    	switch(columnWithProduct){
	        case 1:
	            slot= 1;
	            break;
	        case 2:
	        	slot=  3;
	            break;
	        case 3:
	        	slot=  5;
	            break;
	        case 4:
	        	slot=  7;
	            break;
	        case 5:
	        	slot=  9;
	            break;
	        case 6:
	        	slot=  11;
	            break;
	    	}
	    	
	    	
	    	window.location="/pay?machineId="+machineID+"&productId="+id+"&slot="+slot+"&column="+columnWithProduct;
		}
    	$scope.selectedId=id;
    	
    };
    

}

function getParameterByName(name) {
    name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
    var regex = new RegExp("[\\?&]" + name + "=([^&#]*)"),
        results = regex.exec(location.search);
    return results === null ? "" : decodeURIComponent(results[1].replace(/\+/g, " "));
}
