package controllers;

import com.fasterxml.jackson.databind.JsonNode;

import models.User;
import play.data.Form;
import play.mvc.*;
import views.html.*;

public class Application extends Controller {

    public static boolean loggedIn() {
        return session("user") != null;
    }
	
    public static Result index() {
    	//Form<User> userForm = Form.form(User.class);
        return ok(index.render());
    }
    
    public static Result admin() {
    	Form<User> userForm = Form.form(User.class);
        return ok(admin.render(userForm));
    }
    
    public static Result login() {
    	//get username and password from query string
        String username = request().getQueryString("username");
        String password = request().getQueryString("password");
        
        if(Database.userLogin(username,password)){
        	session("user",username);
        	return redirect("/machineList");
        }else{
        	flash("error","login failed");
        	return redirect("/admin");
        }
        
    }
    
    public static Result loginVending() {
    	//get username and password from query string
        String username = "";
        String password = "";

    	JsonNode jn = request().body().asJson();
    	
    	username = jn.get("username").asText();
    	password = jn.get("password").asText();
    	
    	System.out.println(username);
        
        if(Database.userLogin(username,password)){
        	session("user",username);
        	return redirect("/machineList");
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
