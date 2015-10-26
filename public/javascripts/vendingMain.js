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
  $scope.cart = { product_info: [], product_list: [], total: 0, slots: []};


  $(document).ready(function(){
    $scope.machine=JSON.parse($("#machineJsonVariable").html());
    var alreadyListedProd = "";
    var lengthhooks = $scope.machine.hooks.length;
    for(var i=0; i<lengthhooks;i++){
      if ($scope.machine.hooks[i].item_sku != null) {
        var currentSku = $scope.machine.hooks[i].product.item_sku;
        if(!(alreadyListedProd.indexOf(currentSku)>=0)){
            $scope.availableProducts.push($scope.machine.hooks[i].product);
            alreadyListedProd+=currentSku+",";
        }
      }
    }
    var lengthUniqueSkus = $scope.availableProducts.length;
    $scope.productSelected($scope.availableProducts[0].item_sku, false);
    $scope.$digest();
  });

  var pageTimeout = setTimeout(function() {
    window.location="/vendingMain?machineId="+getParameterByName("machineId");
  }, 1800000);

  $scope.pageReset = function() {
    clearTimeout(pageTimeout);
    pageTimeout = setTimeout(function() {
      window.location="/vendingMain?machineId="+getParameterByName("machineId");
    }, 1800000);
  };

  $scope.availableProducts=[];

  //get list of available products
  var machineID=getParameterByName("machineId");

  $scope.productSelected = function(id, tap) {
	  $('.add-success').animate({opacity: 0}, 100);
	  var inCart = false;
      $.each($scope.cart.product_list, function (index, item) {
    	  if (item == id) {
    		  inCart = true;
    		  return false;
    	  }
      });
      if (inCart == true)  {
    	  console.log('in cart');
    	  $('.add-to-cart-button').addClass('desense');
      } else {
    	  console.log('not in cart');
    	  $('.add-to-cart-button').removeClass('desense');
      }

      if ($scope.cart.product_list.length > 2)
    	  $('.add-to-cart-button').addClass('desense');

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
	  var products = $('.product');
	  $.each(products, function(index, value) {
		  if (value.id == id) {
			  $('td.selected').removeClass('selected');
			  $('#'+id).addClass('selected');
	      }
	  });

    var lengthhooks = $scope.machine.hooks.length;
    for(var i=0; i<lengthhooks;i++){
      if ($scope.machine.hooks[i].item_sku != null){
        var iSku = $scope.machine.hooks[i].product.item_sku;
        if(iSku==id){

          var productCurrent = $scope.machine.hooks[i].product;
          $scope.selectedPrice=productCurrent.price;
          $scope.selectedDescription= productCurrent.item_description.split("//");
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
    clearTimeout(timeoutHandle);
    timeoutHandle = setTimeout(function() {
  	  $('.add-to-cart-button').removeClass('desense');
      console.log("%OASYS,screen=list&?");
      $scope.cart.product_list = [];
      $scope.cart.product_info = [];
      $scope.cart.total = 0;
      $scope.cart.slots = [];
      $.ajax({
        type: "POST",
        url: "/logEvent",
        data: JSON.stringify({ "machine_id": machineID, "event_type": "timeout_product", "product_sku": id}),
        dataType: "json",
        headers: {
          "content-type": "application/json"
        },
      });
      $scope.$digest();
    }, 60000)
  };


  var timeoutHandle;

  $scope.removeItem = function(item_id){
	  var item_index = 0;
	  $.each($scope.cart.product_info, function (index, item) {
		  if (item.id == item_id) {
			  $scope.cart.product_list.splice(index, 1);
		      $scope.cart.total = +$scope.cart.total - +item.price;
			  $scope.cart.slots.splice(index, 1);
			  item_index = index;
			  return false;
		  }
	  });
	  $scope.cart.product_info.splice(item_index, 1);
      $.ajax({
          type: "POST",
          url: "/logEvent",
          data: JSON.stringify({ "machine_id": machineID, "event_type": "remove_from_cart", "product_sku": item_id}),
          dataType: "json",
          headers: {
            "content-type": "application/json"
          },
        });


      if (item_id == $scope.selectedId) {
    	  $('.add-to-cart-button').removeClass('desense');
    	  $('.add-success').animate({opacity: 0}, 100);
      }
      
	  if ($scope.cart.product_list.length != 0) {
	    console.log("%OASYS,screen=pay&machineId="+machineID+"&productId="+$scope.cart.product_list.toString()+"&slot="+$scope.cart.slots.toString()+"?");
	  } else {
	    console.log("%OASYS,screen=list&?");
	  }
	  clearTimeout(timeoutHandle);
      timeoutHandle = setTimeout(function() {

    	$('.add-to-cart-button').removeClass('desense');
        console.log("%OASYS,screen=list&?");
        $scope.cart.product_list = [];
        $scope.cart.product_info = [];
        $scope.cart.total = 0;
        $scope.cart.slots = [];
        $.ajax({
          type: "POST",
          url: "/logEvent",
          data: JSON.stringify({ "machine_id": machineID, "event_type": "timeout_remove", "product_sku": 0}),
          dataType: "json",
          headers: {
            "content-type": "application/json"
          },
        });
        $scope.$digest();
      }, 60000)

  };
  $scope.addToCart = function(){
    $scope.pageReset();
    $('.add-to-cart-button').addClass('desense');
    var slot = 0;
    var lengthhooks = $scope.machine.hooks.length;
    for(var i=0; i<lengthhooks;i++){
      if ($scope.machine.hooks[i].status == 1) {
        var currentSku = $scope.machine.hooks[i].product.item_sku;
        if(currentSku==$scope.selectedId){
          slot = $scope.machine.hooks[i].id;
          break;
        }
      }
    }
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

      clearTimeout(timeoutHandle);
      timeoutHandle = setTimeout(function() {
    	$('.add-to-cart-button').removeClass('desense');
        console.log("%OASYS,screen=list&?");
        $scope.cart.product_list = [];
        $scope.cart.product_info = [];
        $scope.cart.total = 0;
        $scope.cart.slots = [];
        $.ajax({
          type: "POST",
          url: "/logEvent",
          data: JSON.stringify({ "machine_id": machineID, "event_type": "timeout_add", "product_sku": 0}),
          dataType: "json",
          headers: {
            "content-type": "application/json"
          },
        });
        $scope.$digest();
      }, 60000)


      var inCart = false;
      $.each($scope.cart.product_list, function (index, item) {
    	  if (item == $scope.selectedId) {
    		  console.log('already in cart');
    		  inCart = true;
    		  return false;
    	  }
      });

      if (inCart) return;
      $scope.cart.product_list.push($scope.selectedId);
      var product_info = {};
      product_info.name = $scope.selectedName;
      product_info.price = $scope.selectedPrice;
      product_info.id = $scope.selectedId;
      $scope.cart.product_info.push(product_info);
      $scope.cart.total = +$scope.cart.total + +$scope.selectedPrice;
      $scope.cart.slots.push(slot);
      var slots = "";
      var products = "";
      console.log("%OASYS,screen=pay&machineId="+machineID+"&productId="+$scope.cart.product_list.toString()+"&slot="+$scope.cart.slots.toString()+"?");

      $('.add-success').animate({opacity: 1}, 500);
      setTimeout(function() {
      	$('.add-success').animate({opacity: 0}, 500);
      }, 2000);
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
    clearTimeout(pageTimeout);
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
	  console.log('here');
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

}




function getParameterByName(name) {
  name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
  var regex = new RegExp("[\\?&]" + name + "=([^&#]*)"),
      results = regex.exec(location.search);
  return results === null ? "" : decodeURIComponent(results[1].replace(/\+/g, " "));
}
