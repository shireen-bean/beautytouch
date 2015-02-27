package controllers;

import java.util.ArrayList;

import com.fasterxml.jackson.databind.node.ArrayNode;

import models.MachineModel;
import models.User;
import play.data.Form;
import play.libs.Json;
import play.mvc.*;
import views.html.*;

public class MachineList extends Controller {

	public static boolean loggedIn(){
		if(session("user")==null){
			return false;
		}
		return true;
	}

    public static Result machineList(){
    	if(!loggedIn()){
    		return redirect("/");
    	}
    	return ok(machineList.render());
    }    
    
    public static Result machineListJson(){
    	if(!loggedIn()){
    		return redirect("/");
    	}
    	ArrayList<MachineModel> machines = Database.getMachineList();
     	return ok(Json.toJson(machines));
    }
    
    
}
