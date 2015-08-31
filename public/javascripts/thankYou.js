$(document).ready(function(){
	$("#message").fadeIn();
	setTimeout(function() {
		window.location="/vendingMain?machineId="+getParameterByName("machineId");
	}, 2000);
});

function getParameterByName(name) {
    name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
    var regex = new RegExp("[\\?&]" + name + "=([^&#]*)"),
        results = regex.exec(location.search);
    return results === null ? "" : decodeURIComponent(results[1].replace(/\+/g, " "));
}