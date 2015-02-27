$(document).unload(function() {
	//unload map
	GUnload();
});

var map = null;
var geocoder = null;
var infowindow = null;
//var marker = null;
var lat = null;
var lon = null;

var myOptions = {
	zoom : 3,
	mapTypeId : google.maps.MapTypeId.ROADMAP
};

$(document).ready(function() {
	//initialize map
	initialize();
});

function initialize() {
	geocoder = new google.maps.Geocoder();
	map = new google.maps.Map(document.getElementById('map_canvas'),
			myOptions);
	var content = 'Enter address and verify.';
	var options = {
		map : map,
		position : new google.maps.LatLng( 42.3600807, -71.0588837),
		content : content
	};
	//infowindow = new google.maps.InfoWindow(options);
	map.setCenter(options.position);
	map.setZoom(15);
}



function machineListController($scope,$http) {

    	$scope.activeId = -1;
	
    	$http.get('/machineListJson').
    	  success(function(data, status, headers, config) {
    		  $scope.machines = data;

    		  console.log($scope.machines);
    		  for(var i=0;i<$scope.machines.length;i++){
    			var latMachine = $scope.machines[i].lat;
    			var lonMachine = $scope.machines[i].lon;
				$scope.machines[i].levels=Math.round($scope.machines[i].numItems/$scope.machines[i].totalCapacity*100);
				  
    			var red = Math.round(255-255*$scope.machines[i].levels/100);
    			var blue = 255-red;
    			var color = rgbToHex(red,0,blue);
    			console.log(color);
    			
    			 var pinImage = new google.maps.MarkerImage("http://www.googlemapsmarkers.com/v1/"+color+"/");

    			 var markerMachine = new google.maps.Marker({
    				 		position: new google.maps.LatLng(latMachine, lonMachine),
    			            icon: pinImage,
    			            map: map
    			        });
    			
				  markerMachine.set('id', $scope.machines[i].id);
				  google.maps.event.addListener(markerMachine, 'click', function() {
					    markerClicked(this.get("id"));
				  });
				  google.maps.event.addListener(markerMachine, 'mouseover', function() {
					    markerHover(this.get("id"));
				  });
				  
				  //$scope.machines[i].activeClass = ($scope.machines[i].id==$scope.activeId);
				  $scope.machines[i].activeClass = function() {
				        return ($scope.machines[i].id==$scope.activeId);
				    }
    		  }
    		  

    		  
    	  }).
    	  error(function(data, status, headers, config) {
    	  });
    	
    
    $scope.loadProduct = function(sku){
    	window.location = "/product?sku="+sku;
    };
    
    function markerClicked(i){
    	window.location="/machine?id="+i;
    }
    $scope.listItemClicked = function(i){
    	window.location="/machine?id="+i;
    }
    function componentToHex(c) {
        var hex = c.toString(16);
        return hex.length == 1 ? "0" + hex : hex;
    }
    function rgbToHex(r, g, b) {
        return componentToHex(r) + componentToHex(g) + componentToHex(b);
    }
    function markerHover(i){
    	//highlight list item
    	$scope.activeId=i;
    	$scope.$apply();
    }
    $scope.searchNear=function(){
    
    var address = document.getElementById("address").value;
  	addressVerify=address;
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

						} else {
							alert("Lookup was not successful for the following reason: "
									+ status);
						}
					});
    }
}

$(document).ready(function(){
	//set active tab in sidebar
	$("#machineList").addClass("active");
});
