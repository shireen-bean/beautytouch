


function productController($scope,$http) {

  $scope.packageTypeOptions = [
  { label: 'box', value: 'box' },
  { label: 'cylinder', value: 'cylinder' }
  ];

  //populate defaults
  $scope.item_name = "";
  $scope.subtitle = "";
  $scope.category = "";
  $scope.item_sku = "";
  $scope.item_img = "";
  $scope.detail_img = "";
  $scope.thumbnail = "";
  $scope.price = "";
  $scope.brand = "";
  $scope.item_description = "";
  $scope.package_type=$scope.packageTypeOptions[0];

  var sku = getParameterByName("sku");
  if(sku!=""){
    //get existing item details
    $http.get('/productJson?sku='+sku).
      success(function(data, status, headers, config) {
    	console.log(data);
        $scope.item_name = data.item_name;
        $scope.subtitle = data.subtitle;
        $scope.category = data.category;
        $scope.item_sku = data.item_sku;
        $scope.item_img = data.item_img;
        $scope.detail_img = data.detail_img;
        $scope.thumbnail = data.thumbnail;
        $scope.price = data.price;
        $scope.item_description = data.item_description;
        $scope.package_type = data.package_type;
        $scope.brand = data.brand;

        $(document).ready(function(){
          var selectedIndex;
          for(var i=0;i<$scope.packageTypeOptions.length;i++){
            if($scope.packageTypeOptions[i].value==$scope.package_type){
              selectedIndex=i;
            }
          }

          var selectedBrand;
          $http.get('/brandListJson')
          .success( function(data) {
            $scope.brandList = data;
            for (var j = 0; j < $scope.brandList.length; j++){
            	console.log($scope.brand.id);
              if ($scope.brandList[j].id == $scope.brand.id) {
            	  console.log($scope.brandList[j].id);
                selectedBrand = j;
                console.log(j);
                break;
              }
            }

            $scope.brand = $scope.brandList[selectedBrand];
            console.log($scope.brand);
          });
        $scope.package_type=$scope.packageTypeOptions[selectedIndex];
        });

        //load image
        srcLink = "/productImage/"+$scope.item_img;
        showImageOnceLoaded(srcLink, "main");
        
        detailSrcLink = "/productImage/"+$scope.detail_img;
        showImageOnceLoaded(detailSrcLink, "detail");
        
        thumbnailLink = "/productImage/"+$scope.thumbnail;
        showImageOnceLoaded(thumbnailLink, "thumbnail");

        $scope.itemSkuLabel=$scope.item_sku;

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
      "item_name": $scope.item_name,
      "subtitle": $scope.subtitle,
      "category": $scope.category,
      "item_sku": $scope.item_sku,
      "item_img":$scope.item_img,
      "detail_img":$scope.detail_img,
      "thumbnail":$scope.thumbnail,
      "price":$scope.price,
      "item_description":$scope.item_description,
      "package_type":$scope.package_type.value,
      "brand_id": $scope.brand.id
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
        $scope.item_img=data.result.filename;
        $scope.$apply();
        d = new Date();
        //srcLink = "assets/images/products/"+$scope.item_img;
        srcLink = "/productImage/"+$scope.item_img;
        loadingAnimation();
        showImageOnceLoaded(srcLink, "main");
      }
    });
  });
  

  $(function () {
    $('#fileupload-detail').fileupload({
      dataType: 'json',
      done: function (e, data) {
        $scope.detail_img=data.result.filename;
        $scope.$apply();
        d = new Date();
        //srcLink = "assets/images/products/"+$scope.item_img;
        srcLink = "/productImage/"+$scope.detail_img;
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
        //srcLink = "assets/images/products/"+$scope.item_img;
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



