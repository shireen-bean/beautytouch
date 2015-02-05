function productController($scope) {
	//populate defaults if item doesn't exist
    $scope.itemName = "Tampon";
    $scope.itemSku = "not assigned yet";
    $scope.itemImg = "item1.jpg";
    $scope.price = "1.99";
    $scope.itemDescription = "";
    $scope.packageTypeOptions = [
                         { label: 'box', value: 'box' },
                         { label: 'cylinder', value: 'cylinder' }
                       ];
    $scope.packageType=$scope.packageTypeOptions[0];
    
    
    $(function () {
        $('#fileupload').fileupload({
            dataType: 'json',
            done: function (e, data) {
            	console.log(data.result.filename);
            	console.log(data.result.success);
            	
            	$scope.itemImg=data.result.filename;
            	
            	$scope.$apply();
            	d = new Date();
            	srcLink = "assets/images/products/"+$scope.itemImg;
            	setTimeout(
            			  function() 
            			  {
            				  $("#productImage").attr("src", srcLink);
            			  }, 2000);
            	
    /*          $.each(data.result.files, function (index, file) {
                    $('<p/>').text(file.name).appendTo(document.body);
                }); */
            }
        });
    });
    
    
    
}

$(document).ready(function() {
	//set active tab in sidebar
	$("#product").addClass("active");
});

function loadingAnimation(){
	
}

