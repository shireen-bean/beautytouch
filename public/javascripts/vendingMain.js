function vendingMain($scope,$http) {
	
	$scope.selectedName="Item Name";
	$scope.seletedImg="";
	$scope.selectedPrice="0.00";
	$scope.selectedDescrition="Description";
	$scope.selectedBrandName = "Brand Name";
	$scope.selectedBrandDescription = "Brand Description";
	$scope.selectedBrandLogo = "";
		

	$(document).ready(function(){
		$scope.machine=JSON.parse($("#machineJsonVariable").html());
		console.log($scope.machine.containers);
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
		
		$scope.$digest();
	});
	
	setTimeout(function() {
		window.location="/vendingMain?machineId="+getParameterByName("machineId");
	}, 7200000);
	
	
	$scope.availableProducts=[];
	
	//get list of available products
	var machineID=getParameterByName("machineId");
	
    var timeoutHandle;
    
    $scope.productSelected=function(id){
		var divNameSelected = "#product"+id;
		
		var lengthContainers = $scope.machine.containers.length;
		for(var i=0; i<lengthContainers;i++){
			var currentSku = $scope.machine.containers[i].product.itemSku;
			if(currentSku==id){
				var slotWithProduct = $scope.machine.containers[i].slot-1;
		    	var columnWithProduct = i+1;
		    	break;
			}else{
				var columnWithProduct = 0;
			}
		}
		
		if(columnWithProduct!=0){
//			var slot = 0;
//	    	switch(columnWithProduct){
//	        case 1:
//	            slot= 2;
//	            break;
//	        case 2:
//	        	slot=  7;
//	            break;
//	        case 3:
//	        	slot=  12;
//	            break;
//	        case 4:
//	        	slot=  17;
//	            break;
//	        case 5:
//	        	slot=  22;
//	            break;
//	        case 6:
//	        	slot=  26;
//	            break;
//	    	}

	    	//find product details and display checkout window
	    	$("#productList").css('opacity','.1');
	    	$("#productView").show();
	    	$.ajax({
	        	type: "POST",
	        	url: "/logEvent",
	        	data: JSON.stringify({ "machine_id": machineID, "event_type": "tap_product", "product_sku": id}),
	        	dataType: "json",
	        	headers: {
	               "content-type": "application/json"
	            },
	        });
	    	
	    	timeoutHandle = setTimeout(function() {
	    		console.log("%OASYS,screen=list&?");
    	        $("#productView").hide();
    	        $("#productList").css('opacity','1');
    	        $("#productList").show();
    	        $.ajax({
    	        	type: "POST",
    	        	url: "/logEvent",
    	        	data: JSON.stringify({ "machine_id": machineID, "event_type": "timeout", "product_sku": 0}),
    	        	dataType: "json",
    	        	headers: {
    	               "content-type": "application/json"
    	            },
    	        });
	    	}, 60000)
	    
			var lengthContainers = $scope.machine.containers.length;
			for(var i=0; i<lengthContainers;i++){
				var iSku = $scope.machine.containers[i].product.itemSku;
				if(iSku==currentSku){
					
					var productCurrent = $scope.machine.containers[i].product;
					console.log(productCurrent);
					$scope.selectedPrice=productCurrent.price;
					$scope.selectedDescription= productCurrent.itemDescription;
					$scope.selectedImg = productCurrent.itemImg;
					$scope.selectedName= productCurrent.itemName;
					$scope.selectedBrandName = productCurrent.brand.name;
					$scope.selectedBrandLogo = productCurrent.brand.logo;
					$scope.selectedBrandDescription = productCurrent.brand.description;
					$scope.selectedBrandId = productCurrent.brand.id;
					break;
				}
			}
			
			console.log("%OASYS,screen=pay&machineId="+machineID+"&productId="+id+"&slot="+slotWithProduct+"&column="+columnWithProduct+"?");
	    	
			
	    	//window.location="/pay?machineId="+machineID+"&productId="+id+"&slot="+slot+"&column="+columnWithProduct;
		}
    	$scope.selectedId=id;
    	
    };
    
    $scope.closeProduct = function(){
    	console.log("%OASYS,screen=list&?");
    	$("#productView").hide();
    	$("#productList").css('opacity','1');
    	$("#productList").show();
    	$.ajax({
        	type: "POST",
        	url: "/logEvent",
        	data: JSON.stringify({ "machine_id": machineID, "event_type": "tap_back", "product_sku": 0}),
        	dataType: "json",
        	headers: {
               "content-type": "application/json"
            },
        });
    	clearTimeout(timeoutHandle);
    };

}



function getParameterByName(name) {
    name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
    var regex = new RegExp("[\\?&]" + name + "=([^&#]*)"),
        results = regex.exec(location.search);
    return results === null ? "" : decodeURIComponent(results[1].replace(/\+/g, " "));
}
