package controllers;

import models.User;
import play.Play;
import play.api.libs.Codecs;
import play.data.Form;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import play.libs.Json;
import play.mvc.*;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import views.html.*;

public class Product extends Controller {

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
    	ObjectNode product = Database.getProduct(sku);
    	return ok(product);
    }
    
    public static Result postProduct(){
    	
    	JsonNode jn = request().body().asJson();
    	
    	String itemName = jn.get("itemName").asText();
    	String itemSku = jn.get("itemSku").asText();
    	String itemImg = jn.get("itemImg").asText();
    	String price = jn.get("price").asText();
    	String itemDescription = jn.get("itemDescription").asText();
    	String packageType = jn.get("packageType").asText();
    	
    	
    	//validation
    	boolean errorsFlag = false;
    	ObjectNode response = Json.newObject();
    	if(itemName.length()==0){
    		response.put("itemNameError", "name required");
      	  	errorsFlag= true;
    	}if(itemImg.length()==0){
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
	    	if(itemSku.length()>0){
	    		try {
					Database.editProduct(itemSku,itemName,itemImg,price,itemDescription,packageType);
				} catch (SQLException e) {
					System.out.println(e.toString());
					errorsFlag=true;
					response.put("mainError","Database error");
				}
	    	}else{
	    		try {
					Database.addProduct(itemName,itemImg,price,itemDescription,packageType);
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
        	
     	   	if(!file.renameTo(new File("public/images/products/"+uidString+extension))){
       			//error
    	     	ObjectNode resultFailed = Json.newObject();
    	     	resultFailed.put("success", "false");
    	     	return ok(resultFailed);
     	   	}
     	   	
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
