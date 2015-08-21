package controllers;

import java.util.ArrayList;
import java.util.List;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.text.json.JsonContext;
import com.fasterxml.jackson.databind.node.ArrayNode;

import models.Machine;
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
    	List<Machine> machines = Database.getMachineList();
    	JsonContext json = Ebean.createJsonContext();
        String p = json.toJsonString(machines);
        return ok(p);
    	
    }
    
    
}
