package controllers;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import models.User;
import play.data.Form;
import play.libs.Json;
import play.mvc.*;
import views.html.*;

public class ProductList extends Controller {

	public static boolean loggedIn(){
		if(session("user")==null){
			return false;
		}
		return true;
	}

    public static Result productList(){
    	if(!loggedIn()){
    		return redirect("/");
    	}
    	return ok(productList.render());
    }  

    public static Result productListJson(){
    	
    	if(!loggedIn()){
    		return redirect("/");
    	}
    	ArrayNode list = Database.getProductList();
    	
     	return ok(list);
    }  
    
}
