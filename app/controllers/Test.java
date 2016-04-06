package controllers;

import java.sql.SQLException;

import models.User;
import play.data.Form;
import play.mvc.*;
import views.html.*;

public class Test extends Controller {

    public static Result test(){
    	return ok();
    }  
    public static Result testLog(){
    	try {
    		for(int i=0;i<100;i++){
    			Database.logMachineStatus(13, 0, Math.round((float)Math.random()), null);
    		}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return ok();
    }
    
}
