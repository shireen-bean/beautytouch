package controllers;

import models.User;
import play.data.Form;
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
    
}
