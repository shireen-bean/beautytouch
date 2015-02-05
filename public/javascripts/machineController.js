function machineController($scope) {
	//populate defaults if this is a new machine
	$scope.id = "ID not assigned yet",
    $scope.address = "Boston, MA",
    $scope.lat = "",
    $scope.levelTop,
    $scope.levelHeight,
	$scope.lon = "",
    $scope.containers =  [
                            {position:'1',numItems:'10',totalCapacity:'40',itemName:'Tampon',itemSku:'1',itemImg:'item1.jpg',price:'1.99'},
                            {position:'2',numItems:'0',totalCapacity:'40',itemName:'Tampon',itemSku:'1',itemImg:'item1.jpg',price:'1.99'},
                            {position:'3',numItems:'0',totalCapacity:'7',itemName:'Makeup',itemSku:'2',itemImg:'item2.jpg',price:'1.99'},
                            {position:'4',numItems:'0',totalCapacity:'7',itemName:'Makeup',itemSku:'2',itemImg:'item2.jpg',price:'1.99'},
                            {position:'5',numItems:'0',totalCapacity:'7',itemName:'Makeup',itemSku:'2',itemImg:'item2.jpg',price:'1.99'},
                            {position:'6',numItems:'0',totalCapacity:'7',itemName:'Makeup',itemSku:'2',itemImg:'item2.jpg',price:'1.99'},
                            {position:'7',numItems:'0',totalCapacity:'7',itemName:'Makeup',itemSku:'2',itemImg:'item2.jpg',price:'1.99'},
                            {position:'8',numItems:'0',totalCapacity:'7',itemName:'Makeup',itemSku:'2',itemImg:'item2.jpg',price:'1.99'},
                        ];
}

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
								document.getElementById("submit").disabled = false
							} else {
								alert("Lookup was not successful for the following reason: "
										+ status);
							}
						});
	//submit
	} else if (clicked_id == "submit") {
		//check if restname is filled
		var query = "?name=" + name + "&lat=" + lat + "&lon=" + lon
				+ "&address=" + address + "&id=" + "${temp.id}";
		window.location = "../../editLoc" + query;
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
