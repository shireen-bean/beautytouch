package controllers;

import java.util.List;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.text.json.JsonContext;

import models.Product;
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
    	List<Product> list = Database.getProductList();
        JsonContext json = Ebean.createJsonContext();
        String p = json.toJsonString(list);
        return ok(p);
    	
    }  
    
}
