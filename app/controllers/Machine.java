package controllers;

import java.sql.SQLException;
import java.util.ArrayList;
import java.sql.Timestamp;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import models.Container;
import models.MachineModel;
import models.ProductModel;
import models.User;
import play.data.Form;
import play.libs.Json;
import play.mvc.*;
import views.html.*;

public class Machine extends Controller {

	public static boolean loggedIn(){
		if(session("user")==null){
			return false;
		}
		return true;
	}

    public static Result machine(){
    	if(!loggedIn()){
    		return redirect("/");
    	}
    	return ok(machine.render());
    }




    public static Result postMachine(){

    	if(!loggedIn()){
    		return redirect("/");
    	}

    	JsonNode jn = request().body().asJson();

    	String id = jn.get("id").asText();

    	boolean errorsFlag=false;
    	ObjectNode response = Json.newObject();

    	if(id.length()>0){
    		try {
				Database.editMachineAndContainers(jn);
			} catch (SQLException e) {
				System.out.println(e.toString());
				errorsFlag=true;
				response.put("mainError","Database error");
			}
    	}else{
    		try {
				Database.addMachineAndContainers(jn);
			} catch (SQLException e) {
				errorsFlag=true;
				System.out.println(e.toString());
				response.put("mainError","Database error");
			}
    	}


    	if(errorsFlag){
    		response.put("success", "false");
    	}else{
    		response.put("success", "true");
    	}

     	return ok(response);
    }

    public static Result logStatus(){
    	//System.out.println(request().body().asJson().toString() + new Timestamp(System.currentTimeMillis()));
    	JsonNode jn = request().body().asJson();
        int machine_id = jn.get("machine_id").asInt();
        int traffic = jn.get("traffic").asInt();
    	int jammed = jn.get("jammed").asInt();
    	try {
    		Database.logMachineStatus(machine_id, jammed, traffic);
    	} catch (SQLException e) {
      	  System.out.println(e.toString());
    	}
    	return ok();
    }

    public static Result logEvent() {
    	JsonNode jn = request().body().asJson();
    	String machine_id = jn.get("machine_id").asText();
    	String event_type = jn.get("event_type").asText();
    	String product_sku = jn.get("product_sku").asText();
    	try {
    		Database.logEvent(machine_id, event_type, product_sku);
    	} catch (SQLException e) {
    		System.out.println(e.toString());
    	}
    	return ok();
    }
    
    public static Result machineJson(String id){

    	MachineModel machine = new MachineModel();

    	if(id.equals("-1")){
        	machine.containers=new ArrayList<Container>();
        	for(int i=0;i<2;i++){
        		Container container= new Container();
        		container.product=new ProductModel();
        		machine.containers.add(container);
        	}
        	for(int i=2; i<10; i++){
        		Container container= new Container();
        		container.product=new ProductModel();
        		machine.containers.add(container);
        	}
    	}else{
        	machine = Database.getMachine(id);
    	}


		return ok(Json.toJson(machine));
    }


}
