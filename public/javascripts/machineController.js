var addressVerify = "";

function machineController($scope,$http) {
    
    $scope.loadActivityLog=function(){
    	window.location="/activityLog?machineId="+getParameterByName("id");
    }
	
	
	$http.get('/productListJson').
	  success(function(data, status, headers, config) {
		  $scope.products = data;

		    var id = getParameterByName("id");
		    if(id != ""){
		    	//get existing item details
		    	$http.get('/machineJson?id='+id).
			  	  success(function(data, status, headers, config) {
			  		  $scope.machine = data;
			  		  $scope.itemIDLabel=$scope.machine.id;
			      	  mapSetLocation();
			  	  });
		    }else{
		    	$http.get('/machineJson?id=-1').
			  	  success(function(data, status, headers, config) {
			  		  $scope.machine = data;
			  		  
			   
			      	$scope.machine.id = "";
			        $scope.machine.address = "Boston, MA";
			        $scope.machine.lat = "";
			      	$scope.machine.lon = "";
			      	
			  		for(var i=0;i<70;i++){
			      		$scope.machine.hooks[i].id=i+1;
			      		$scope.machine.hooks[i].status = 0;
			  		}
			  		
			  		
			  	  });
		    	$scope.itemIDLabel="not assigned yet";
		
		    }
	
	  }).
	  error(function(data, status, headers, config) {
	  });

	
	$scope.saveClicked=function(){
		$scope.machine.lat=lat;
		$scope.machine.lon=lon;
		if(addressVerify=="" || addressVerify!=document.getElementById("address").value){
			window.scrollTo(0, 0);
			$(".errorMessage").fadeIn();
		}else{
			//submit new machine details
	    	 
	    	loadingAnimation();
	    	
	    	$http.post('/postMachine', $scope.machine).
			  success(function(data, status, headers, config) {
				if(data.success=="true"){
					window.location="/machineList";
				}
			    stopLoadingAnimation();
			  }).
			  error(function(data, status, headers, config) {
			    stopLoadingAnimation();
			  });
		}
	}
	
	$scope.location="0";
	
	$scope.changeItem=function(location){
		//getItems
		$scope.location=location;
		chooseItem();
	}
	
	function chooseItem(){
		$("#chooseItemDiv").fadeIn();
	}
	
	$scope.selectedProduct=function(item_sku){
		//get item details 
		var indexProduct;
		for(var i=0;i<$scope.products.length;i++){
			if($scope.products[i].item_sku==item_sku){
				indexProduct=i;
			}
		}
		
		var product = {};
		product.item_name=$scope.products[indexProduct].item_name;
		product.item_sku=$scope.products[indexProduct].item_sku;
		product.item_img=$scope.products[indexProduct].item_img;
		product.price=$scope.products[indexProduct].price;
		
		$scope.machine.hooks[$scope.location].product = product;
		$("#chooseItemDiv").fadeOut();
	}
	
    function getParameterByName(name) {
    	console.log("test");
        name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
        var regex = new RegExp("[\\?&]" + name + "=([^&#]*)"),
            results = regex.exec(location.search);
        return results === null ? "" : decodeURIComponent(results[1].replace(/\+/g, " "));
    }
    
    function mapSetLocation(){
    	addressVerify=$scope.machine.address;
    	
    	if (marker) {
    		marker.setMap(null);
    	}
    	geocoder.geocode(
    					{
    						'address' : $scope.machine.address
    					},
    					function(results, status) {
    						if (status == google.maps.GeocoderStatus.OK) {
    							lat = $scope.machine.lat;
    							
    							lon = $scope.machine.lon;
    							
    							map.setZoom(15);
    							map.setCenter(results[0].geometry.location);
    							marker = new google.maps.Marker(
    									{
    										map : map,
    										position : results[0].geometry.location,
    									});
    							map.marker = marker;
    							document.getElementById("submit").disabled = false;
    						} else {
    							alert("Lookup was not successful for the following reason: "
    									+ status);
    						}
    					});
    }
}
//end scope

$(document).ready(function() {
	//set active tab in sidebar
	$("#machine").addClass("active");
	//initialize map
	initialize();
});
$(document).unload(function() {
	//unload map
	GUnload();
});

var map = null;
var geocoder = null;
var infowindow = null;
var marker = null;
var lat = null;
var lon = null;

var myOptions = {
	zoom : 3,
	mapTypeId : google.maps.MapTypeId.ROADMAP
};

function initialize() {
	geocoder = new google.maps.Geocoder();
	map = new google.maps.Map(document.getElementById('map_canvas'),
			myOptions);
	var content = 'Enter address and verify.';
	var options = {
		map : map,
		position : new google.maps.LatLng(26, -81),
		content : content
	};
	//infowindow = new google.maps.InfoWindow(options);
	map.setCenter(options.position);
}



function button_click(clicked_id) {
	var address = document.getElementById("address").value;
	
	//verify
	if (clicked_id == "verify") {
		if (marker) {
			marker.setMap(null);
		}
		geocoder.geocode(
						{
							'address' : address
						},
						function(results, status) {
							if (status == google.maps.GeocoderStatus.OK) {
								lat = results[0].geometry.location.lat();
								lon = results[0].geometry.location.lng();
								
								map.setZoom(15);
								map.setCenter(results[0].geometry.location);
								marker = new google.maps.Marker(
										{
											map : map,
											position : results[0].geometry.location,
										});
								map.marker = marker;
								document.getElementById("submit").disabled = false;
								addressVerify=address;
							} else {
								alert("Lookup was not successful for the following reason: "
										+ status);
							}
						});
	} 
}

$(document).ready(function(){
	$("#address").change(function(){
		console.log("change");
		//remove marker
		if (marker) {
			marker.setMap(null);
		}
		document.getElementById("submit").disabled = true;
	});
});
