package controllers;

import com.fasterxml.jackson.databind.JsonNode;

import models.User;
import play.data.Form;
import play.mvc.*;
import views.html.*;

public class Landing extends Controller {

//	public static boolean loggedIn(){
//		if(session("user")==null){
//			return false;
//		}
//		return true;
//	}
//	
	
    public static Result contactUs() {
    	Form<User> userForm = Form.form(User.class);
        return ok(contactus.render());
    }
    public static Result terms() {
    	Form<User> userForm = Form.form(User.class);
        return ok(terms.render());
    }
   
    
}
