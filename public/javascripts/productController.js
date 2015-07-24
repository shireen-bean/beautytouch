


function productController($scope,$http) {

  $scope.packageTypeOptions = [
  { label: 'box', value: 'box' },
  { label: 'cylinder', value: 'cylinder' }
  ];

  //populate defaults
  $scope.itemName = "";
  $scope.category = "";
  $scope.itemSku = "";
  $scope.itemImg = "";
  $scope.detailImg = "";
  $scope.thumbnail = "";
  $scope.price = "";
  $scope.brandId = "";
  $scope.itemDescription = "";
  $scope.packageType=$scope.packageTypeOptions[0];

  var sku = getParameterByName("sku");
  if(sku!=""){
    //get existing item details
    $http.get('/productJson?sku='+sku).
      success(function(data, status, headers, config) {
        $scope.itemName = data.itemName;
        $scope.category = data.category;
        $scope.itemSku = data.itemSku;
        $scope.itemImg = data.itemImg;
        $scope.detailImg = data.detailImg;
        $scope.thumbnail = data.thumbnail;
        $scope.price = data.price;
        $scope.itemDescription = data.itemDescription;
        $scope.packageType = data.packageType;
        $scope.brandId = data.brandId;

        $(document).ready(function(){
          var selectedIndex;
          for(var i=0;i<$scope.packageTypeOptions.length;i++){
            if($scope.packageTypeOptions[i].value==$scope.packageType){
              selectedIndex=i;
            }
          }

          var selectedBrand;
          $http.get('/brandListJson')
          .success( function(data) {
            $scope.brandList = data;
            for (var j = 0; j < $scope.brandList.length; j++){
              if ($scope.brandList[j].id == $scope.brandId) {
                selectedBrand = j;
                break;
              }
            }

            $scope.brandId = $scope.brandList[selectedBrand];
          });
        $scope.packageType=$scope.packageTypeOptions[selectedIndex];
        });

        //load image
        srcLink = "/productImage/"+$scope.itemImg;
        showImageOnceLoaded(srcLink, "main");
        
        detailSrcLink = "/productImage/"+$scope.detailImg;
        showImageOnceLoaded(detailSrcLink, "detail");
        
        thumnailLink = "/productImage/"+$scope.thumbnail;
        showImageOnceLoaded(thumbnailLink, "thumbnail");

        $scope.itemSkuLabel=$scope.itemSku;

      }).
    error(function(data, status, headers, config) {
    });
  }else{
    $scope.itemSkuLabel="not assigned yet"
    $http.get('/brandListJson')
      .success( function(data) {
        $scope.brandList = data;
      });
  }
  $scope.submit=function(){
    //build product json object
    var jsonProduct =
    {
      "itemName": $scope.itemName,
      "category": $scope.category,
      "itemSku": $scope.itemSku,
      "itemImg":$scope.itemImg,
      "detailImg":$scope.detailImg,
      "thumbnail":$scope.thumbnail,
      "price":$scope.price,
      "itemDescription":$scope.itemDescription,
      "packageType":$scope.packageType.value,
      "brand_id": $scope.brandId.id
    };

    loadingAnimation();

    $http.post('/postProduct', jsonProduct).
      success(function(data, status, headers, config) {
        if(data.success=="true"){
          window.location="/productList";
        }

        //show errors
        $(".errorMessage").fadeIn();
        $scope.itemNameError=data.itemNameError;
        $scope.categoryError = data.categoryError;
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
        //srcLink = "assets/images/products/"+$scope.itemImg;
        srcLink = "/productImage/"+$scope.itemImg;
        loadingAnimation();
        showImageOnceLoaded(srcLink, "main");
      }
    });
  });
  

  $(function () {
    $('#fileupload-detail').fileupload({
      dataType: 'json',
      done: function (e, data) {
        $scope.detailImg=data.result.filename;
        $scope.$apply();
        d = new Date();
        //srcLink = "assets/images/products/"+$scope.itemImg;
        srcLink = "/productImage/"+$scope.detailImg;
        loadingAnimation();
        showImageOnceLoaded(srcLink, "detail");
      }
    });
  });
  

  $(function () {
    $('#fileupload-thumbnail').fileupload({
      dataType: 'json',
      done: function (e, data) {
        $scope.thumbnail=data.result.filename;
        $scope.$apply();
        d = new Date();
        //srcLink = "assets/images/products/"+$scope.itemImg;
        srcLink = "/productImage/"+$scope.thumbnail;
        loadingAnimation();
        showImageOnceLoaded(srcLink, "thumbnail");
      }
    });
  });

  function showImageOnceLoaded(srcLink, type){
    if(imageExists(srcLink)){
      $("#productImage-" + type).attr("src", srcLink);
      stopLoadingAnimation();
    }
    else{
      setTimeout(
          function()
          {
            showImageOnceLoaded(srcLink, type);
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



