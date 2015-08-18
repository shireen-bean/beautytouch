package controllers;

import play.Play;
import play.api.libs.Codecs;
import play.data.Form;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import models.Product;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.text.json.JsonContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import play.libs.Json;
import play.mvc.*;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.twirl.api.Content;
import views.html.*;

public class ProductController extends Controller {

  public static boolean loggedIn(){
    if(session("user")==null){
      return false;
    }
    return true;
  }

  public static Result product() {
    if(!loggedIn()){
      return redirect("/");
    }
    return ok(product.render());
  }

  public static Result productJson(String sku){
	  System.out.println(sku);
    Product product = Database.getProduct(sku);
    System.out.println(product);
    JsonContext json = Ebean.createJsonContext();
    String p = json.toJsonString(product);
    return ok(p);
  }

  public static Result postProduct(){
    if(!loggedIn()){
      return redirect("/");
    }
    JsonNode jn = request().body().asJson();

    System.out.println(jn);
    String item_name = jn.get("item_name").asText();
    String subtitle = jn.get("subtitle").asText();
    String category = jn.get("category").asText();
    String brand_id = jn.get("brand_id").asText();
    String item_sku = jn.get("item_sku").asText();
    String item_img = jn.get("item_img").asText();
    String detail_img = jn.get("detail_img").asText();
    String thumbnail = jn.get("thumbnail").asText();
    String price = jn.get("price").asText();
    String item_description = jn.get("item_description").asText();
    String package_type = jn.get("package_type").asText();


    //validation
    boolean errorsFlag = false;
    ObjectNode response = Json.newObject();
    if(item_name.length()==0){
      response.put("itemNameError", "name required");
      errorsFlag= true;
    } if (category.length() == 0 ){
      response.put("categoryError",  "category required");
      errorsFlag = true;
    }if(item_img.length()==0){
      response.put("itemImgError", "image required");
      errorsFlag= true;
    }if(price.length()==0){
      response.put("priceError", "price required");
      errorsFlag= true;
    }else if(!price.matches("\\d+(\\.\\d+)?")){
      response.put("priceError", "invalid price");
      errorsFlag= true;
    }

    if(!errorsFlag){
      if(item_sku.length()>0){
        try {
            Database.editProduct(item_sku, item_name, subtitle, category, brand_id, item_img, detail_img, thumbnail, price, item_description, package_type);
        } catch (SQLException e) {
          System.out.println(e.toString());
          errorsFlag=true;
          response.put("mainError","Database error");
        }
      }else{
        try {
            Database.addProduct(item_name, subtitle, category, brand_id, item_img, detail_img, thumbnail, price, item_description, package_type);
        } catch (SQLException e) {
          errorsFlag=true;
          response.put("mainError","Database error");
        }
      }
    }

    if(errorsFlag){
      response.put("success", "false");
    }else{
      response.put("success", "true");
    }

    return ok(response);
  }

  public static Result postProductImage(){

    if(!loggedIn()){
      return redirect("/");
    }
    MultipartFormData body = request().body().asMultipartFormData();
    FilePart picture = body.getFile("files[]");

    if (picture != null) {
      String fileName = picture.getFilename();
      String extension = fileName.substring(fileName.length() - 4);
      File file = picture.getFile();

      UUID uid = UUID.fromString("38400000-8cf0-11bd-b23e-10b96e4ef00d");

      // checking the value of random UUID
      String uidString = uid.randomUUID().toString();

      //     	   	if(!file.renameTo(new File("public/images/products/"+uidString+extension))){
      if(!file.renameTo(new File("public/dynamicFiles/products/"+uidString+extension))){
        //error
        ObjectNode resultFailed = Json.newObject();
        resultFailed.put("success", "false");
        return ok(resultFailed);
      }
      System.out.println("successfull upload"+uidString);
      System.out.println(Play.application().path());
      ObjectNode result = Json.newObject();
      result.put("success", "true");
      result.put("filename", uidString+extension);
      return ok(result);
    } else {
      //error
      ObjectNode resultFailed = Json.newObject();
      resultFailed.put("success", "false");
      return ok(resultFailed);
    }
    }
  }
