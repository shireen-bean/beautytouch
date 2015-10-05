function vendingMain($scope,$http) {


  $scope.formData = {};
  $scope.suggestion = "";
  $scope.selectedName="Item Name";
  $scope.selectedSubtitle = "";
  $scope.seletedImg="";
  $scope.selectedPrice="0.00";
  $scope.selectedDescription="Description";
  $scope.selectedBrandName = "Brand Name";
  $scope.selectedBrandDescription = "Brand Description";
  $scope.selectedBrandLogo = "";
  $scope.cart = { product_list: [], total: 0};


  $(document).ready(function(){
    $scope.machine=JSON.parse($("#machineJsonVariable").html());
    console.log($scope.machine.hooks);
    var alreadyListedProd = "";
    var lengthhooks = $scope.machine.hooks.length;
    console.log(lengthhooks);
    for(var i=0; i<lengthhooks;i++){
      if ($scope.machine.hooks[i].item_sku != null) {
        var currentSku = $scope.machine.hooks[i].product.item_sku;
        if(!(alreadyListedProd.indexOf(currentSku)>=0)){
        	console.log('now here');
            $scope.availableProducts.push($scope.machine.hooks[i].product);
            alreadyListedProd+=currentSku+",";
        }
      }
    }
    var lengthUniqueSkus = $scope.availableProducts.length;
    console.log($scope.availableProducts);
    $scope.productSelected($scope.availableProducts[0].item_sku, false);
    console.log($scope.selectedName);
    $scope.$digest();
  });

  var pageTimeout = setTimeout(function() {
    window.location="/vendingMain?machineId="+getParameterByName("machineId");
  }, 180000);

  $scope.pageReset = function() {
    clearTimeout(pageTimeout);
    pageTimeout = setTimeout(function() {
      window.location="/vendingMain?machineId="+getParameterByName("machineId");
    }, 180000);
  };

  $scope.availableProducts=[];

  //get list of available products
  var machineID=getParameterByName("machineId");

  $scope.productSelected = function(id, tap) {

	  if (tap == true){
        $.ajax({
          type: "POST",
          url: "/logEvent",
          data: JSON.stringify({ "machine_id": machineID, "event_type": "tap_product", "product_sku": id}),
          dataType: "json",
          headers: {
            "content-type": "application/json"
          },
        });
	  }
    $scope.selectedId=id;

    var lengthhooks = $scope.machine.hooks.length;
    for(var i=0; i<lengthhooks;i++){
      if ($scope.machine.hooks[i].item_sku != null){
        var iSku = $scope.machine.hooks[i].product.item_sku;
        if(iSku==id){

          var productCurrent = $scope.machine.hooks[i].product;
          console.log(productCurrent);
          $scope.selectedPrice=productCurrent.price;
          $scope.selectedDescription= productCurrent.item_description.split("//");
          console.log($scope.selectedDescription);
          $scope.mainImg = productCurrent.item_img;
          $scope.selectedImg = productCurrent.item_img;
          $scope.selectedDetailImg = productCurrent.detail_img;
          $scope.selectedThumbnail = productCurrent.thumbnail;
          $scope.selectedName= productCurrent.item_name;
          $scope.selectedSubtitle = productCurrent.subtitle;
          $scope.selectedBrandName = productCurrent.brand.name;
          $scope.selectedBrandLogo = productCurrent.brand.logo;
          $scope.selectedBrandDescription = productCurrent.brand.description;
          $scope.selectedBrandId = productCurrent.brand.id;
           break;
        }
      }
    }
    console.log(productCurrent);
  };


  var timeoutHandle;

  $scope.addToCart = function(){

    console.log("add product: " + $scope.selectedId);
    $scope.pageReset();
    var slot = 0;
    var lengthhooks = $scope.machine.hooks.length;
    for(var i=0; i<lengthhooks;i++){
      if ($scope.machine.hooks[i].status == 1) {
        var currentSku = $scope.machine.hooks[i].product.item_sku;
        if(currentSku==$scope.selectedId){
          slot = $scope.machine.hooks[i].id;
          console.log(i);
          console.log(slot);
          break;
        }
      }
    }
    console.log(slot);
    if(slot != 0){
      //find product details and display checkout window
      //$("#productList").css('opacity','.1');
      //$("#productView").show();
      $.ajax({
        type: "POST",
        url: "/logEvent",
        data: JSON.stringify({ "machine_id": machineID, "event_type": "add_to_cart", "product_sku": $scope.selectedId}),
        dataType: "json",
        headers: {
          "content-type": "application/json"
        },
      });

      timeoutHandle = setTimeout(function() {
        console.log("%OASYS,screen=list&?");
        $scope.cart.product_list = [];
        $scope.cart.total = 0;
        console.log($scope.cart);
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

      $scope.cart.product_list.push($scope.selectedId);
      $scope.cart.total = +$scope.cart.total + +$scope.selectedPrice;
      console.log($scope.cart);

      console.log("%OASYS,screen=pay&machineId="+machineID+"&productId="+$scope.selectedId+"&slot="+slot+"?");


      //window.location="/pay?machineId="+machineID+"&productId="+id+"&slot="+slot+"&column="+columnWithProduct;
    }
  };
  $scope.changeImage = function(img) {
    if (img == 'selectedImg') {
      $('#main-image').attr('src','/productImage/' + $scope.selectedImg);
    } else if (img == 'selectedDetailImg') {
      $('#main-image').attr('src','/productImage/' + $scope.selectedDetailImg);
    } else if (img == 'thumbnail') {
      $('#main-image').attr('src','/productImage/' + $scope.selectedThumbnail);
    }
    $scope.pageReset();
  };
  $scope.reportProblem = function(issue, screen) {
    $('.problem-dialog').show();
    $.ajax({
      type: "POST",
      url: "/logEvent",
      data: JSON.stringify({ "machine_id": machineID, "event_type": "tap_report", "product_sku": 0}),
      dataType: "json",
      headers: {
        "content-type": "application/json"
      },
    });
    setTimeout(function() {
      $('.problem-dialog').hide();
    }, 30000);
    window.clearTimeout(pageTimeout);
  };
  $scope.howItWorks = function() {
    $.ajax({
      type: "POST",
      url: "/logEvent",
      data: JSON.stringify({ "machine_id": machineID, "event_type": "tap_about", "product_sku": 0}),
      dataType: "json",
      headers: {
        "content-type": "application/json"
      },
    });
    $('.how-it-works').show();
    setTimeout(function() {
      $('.how-it-works').hide();
    }, 180000);
    $scope.pageReset();
  }
  $scope.closeHowItWorks = function() {
    $('.suggestion-input').val("");
    $scope.suggestion = "";
    $('.how-it-works').hide();
    $scope.pageReset();
  }
  $scope.reportBack = function() {
    $('.problem-dialog').hide();
    $('#problem-email').val("");
    $scope.pageReset();
  };
  $scope.submitSuggestion = function() {
    console.log($scope.suggestion);
    if ($scope.suggestion != "") {
      $.ajax({
        type: "POST",
        url: "/sendSuggestion",
        data: JSON.stringify({"machine_id": machineID, "suggestion": $scope.suggestion }),
        dataType: "json",
        headers: {
          "content-type": "application/json"
        },
      });
    }
    $scope.closeHowItWorks();
    $scope.pageReset();
  }
  $scope.addChar= function(char) {
    $scope.suggestion=$scope.suggestion+char;
    $scope.pageReset();
  }
  $scope.deleteChar = function() {
    $scope.suggestion=$scope.suggestion.substring(0, $scope.suggestion.length-1);
    $scope.pageReset();
  }
  $scope.submitReport = function() {
    console.log($scope.formData);
    $.ajax({
      type: "POST",
      url: "/reportProblem",
      data: JSON.stringify({"machine_id": machineID, "formData": $scope.formData }),
      dataType: "json",
      headers: {
        "content-type": "application/json"
      },
    });
    $('.problem-dialog').hide();
    $('#problem-email').val("");
    $('.report-problem').hide();
    $('.thank-you-report').show();
    //show thank you message
    setTimeout(function() {
      $('.thank-you-report').hide();
      $('.report-problem').show();
    }, 5000);
    $scope.pageReset();
  };
  $scope.closeProduct = function(){
    console.log("%OASYS,screen=list&?");
    $('#main-image').attr('src','/productImage/' + $scope.selectedImg);
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
    $scope.pageReset();
    clearTimeout(timeoutHandle);
  };

}



function getParameterByName(name) {
  name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
  var regex = new RegExp("[\\?&]" + name + "=([^&#]*)"),
      results = regex.exec(location.search);
  return results === null ? "" : decodeURIComponent(results[1].replace(/\+/g, " "));
}
