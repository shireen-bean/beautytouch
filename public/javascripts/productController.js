


function productController($scope,$http) {
	
    $scope.packageTypeOptions = [
                                 { label: 'box', value: 'box' },
                                 { label: 'cylinder', value: 'cylinder' }
                               ];
    
	//populate defaults
    $scope.itemName = "";
    $scope.itemSku = "";
    $scope.itemImg = "";
    $scope.price = "";
    $scope.itemDescription = "";
    $scope.packageType=$scope.packageTypeOptions[0];
    
    var sku = getParameterByName("sku");
    if(sku!=""){
    	//get existing item details
    	$http.get('/productJson?sku='+sku).
	  	  success(function(data, status, headers, config) {
	  		  console.log(data);
	  	    $scope.itemName = data.itemName;
	  	    $scope.itemSku = data.itemSku;
	  	    $scope.itemImg = data.itemImg;
	  	    $scope.price = data.price;
	  	    $scope.itemDescription = data.itemDescription;
	  	    $scope.packageType = data.packageType;
	  	    
	  	    $(document).ready(function(){
	  	    	var selectedIndex;
	  	    	for(var i=0;i<$scope.packageTypeOptions.length;i++){
	  	    		if($scope.packageTypeOptions[i].value==$scope.packageType){
	  	    			selectedIndex=i;
	  	    		}
	  	    	}

	  	    	$scope.packageType=$scope.packageTypeOptions[selectedIndex];

	  	    });
	  	    
	  	    //load image
        	srcLink = "assets/images/products/"+$scope.itemImg;
        	showImageOnceLoaded(srcLink);

      	  	$scope.itemSkuLabel=$scope.itemSku;
        	
	  	  }).
	  	  error(function(data, status, headers, config) {
	  	  });
    }else{
    	$scope.itemSkuLabel="not assigned yet"
    }
    $scope.submit=function(){
    	
    	//build product json object
    	 var jsonProduct = 
         {
             "itemName": $scope.itemName,
             "itemSku": $scope.itemSku,
             "itemImg":$scope.itemImg,
             "price":$scope.price,
             "itemDescription":$scope.itemDescription,
             "packageType":$scope.packageType.value
         };
    	 
    	loadingAnimation();
    	
    	$http.post('/postProduct', jsonProduct).
		  success(function(data, status, headers, config) {
			console.log(data);
			console.log(data.success);
			if(data.success=="true"){
				window.location="/productList";
			}
			
			//show errors
			$scope.itemNameError=data.itemNameError;
			$scope.itemImgError=data.itemImgError;
			$scope.priceError=data.priceError;
			
		    stopLoadingAnimation();
		  }).
		  error(function(data, status, headers, config) {
		    stopLoadingAnimation();
		  });
    };
    
    
    $(function () {
        $('#fileupload').fileupload({
            dataType: 'json',
            done: function (e, data) {
            	$scope.itemImg=data.result.filename;
            	$scope.$apply();
            	d = new Date();
            	srcLink = "assets/images/products/"+$scope.itemImg;
            	loadingAnimation();
            	showImageOnceLoaded(srcLink);
            }
        });
    });
    
    function showImageOnceLoaded(srcLink){
		  if(imageExists(srcLink)){
			  $("#productImage").attr("src", srcLink);
			  stopLoadingAnimation();
		  }
		  else{
          	setTimeout(
      			  function() 
      			  {
      				  showImageOnceLoaded(srcLink);
      			  }, 500);
		  }
		  
		  
    }
    
    function imageExists(url) 
    {
       var img = new Image();
       img.src = url;
       return img.height != 0;
    }
    
    function getParameterByName(name) {
    	console.log("test");
        name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
        var regex = new RegExp("[\\?&]" + name + "=([^&#]*)"),
            results = regex.exec(location.search);
        return results === null ? "" : decodeURIComponent(results[1].replace(/\+/g, " "));
    }
    
}


$(document).ready(function() {
	//set active tab in sidebar
	$("#product").addClass("active");
});

function loadingAnimation(){
	$("#loadingDiv").css('display','block');
}
function stopLoadingAnimation(){
	$("#loadingDiv").css('display','none');
}

