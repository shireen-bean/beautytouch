package controllers;

import models.User;
import play.data.Form;
import play.mvc.*;

import views.html.*;

public class Application extends Controller {

	public static boolean loggedIn(){
		if(session("user")==null){
			return false;
		}
		return true;
	}
	
    public static Result index() {
    	Form<User> userForm = Form.form(User.class);
        return ok(index.render(userForm));
    }
    
    public static Result login() {
    	//get username and password from query string
        String username = request().getQueryString("username");
        String password = request().getQueryString("password");
        
        if(Database.userLogin(username,password)){
        	session("user",username);
        	return redirect("/dashboard");
        }else{
        	flash("error","login failed");
        	return redirect("/");
        }
        
    }
    
    public static Result logout(){
    	session().clear();
    	return redirect("/");
    }
    
}
