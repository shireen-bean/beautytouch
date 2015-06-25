function brandController($scope,$http) {

	
  
  //populate defaults
  $scope.name = "";
  $scope.id = "";
  $scope.logo = "";
  $scope.description = "";

  var id = getParameterByName("id");
  if(id!=""){
    //get existing item details
    $http.get('/brandJson?id='+id)
      .success(function(data, status, headers, config) {
        console.log(data);
        $scope.name = data.name;
        $scope.logo = data.logo;
        $scope.description = data.description;
        $scope.id = data.id;

        //load image
        srcLink = "/brandLogo/" + $scope.logo;
        showImageOnceLoaded(srcLink);

        $scope.brandLabel = $scope.name + " - " + $scope.id;

      })
    .error(function(data, status, headers, config) {
    });
  }else{
    $scope.brandLabel=""
  }
  $scope.submit=function(){
    //build brand json object
    var jsonBrand =
    {
      "name": $scope.name,
      "id": $scope.id,
      "logo":$scope.logo,
      "description":$scope.description,
    };
    
    console.log(jsonBrand);

    loadingAnimation();

    $http.post('/postBrand', jsonBrand)
      .success(function(data, status, headers, config) {
        if(data.success=="true"){
          window.location="/brandList";
        }

        //show errors
        $(".errorMessage").fadeIn();
        $scope.nameError=data.nameError;
        $scope.logoError=data.logoError;

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
        $scope.logo = data.result.filename;
        $scope.$apply();
        d = new Date();
        //srcLink = "assets/images/products/"+$scope.itemImg;
        srcLink = "/brandLogo/"+ $scope.logo;
        loadingAnimation();
        showImageOnceLoaded(srcLink);
      }
    });
  });

  function showImageOnceLoaded(srcLink){
    if(imageExists(srcLink)){
      $("#brandLogo").attr("src", srcLink);
      stopLoadingAnimation();
    }
    else{
      setTimeout( function() { showImageOnceLoaded(srcLink); }, 500);
    }
  }

  function imageExists(url)
  {
    var img = new Image();
    img.src = url;
    return img.height != 0;
  }

  function getParameterByName(name) {
    name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
    var regex = new RegExp("[\\?&]" + name + "=([^&#]*)"),
        results = regex.exec(location.search);
    return results === null ? "" : decodeURIComponent(results[1].replace(/\+/g, " "));
  }
}

$(document).ready(function() {
  //set active tab in sidebar
  $("#brand").addClass("active");
});



