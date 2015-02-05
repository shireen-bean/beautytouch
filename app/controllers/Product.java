package controllers;

import models.User;
import play.Play;
import play.api.libs.Codecs;
import play.data.Form;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

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
