package controllers;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.text.json.JsonContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import models.ActivityLogModel;
import models.Container;
import models.Machine;
import models.Product;
import models.Sale;
import models.User;
import play.data.Form;
import play.libs.Json;
import play.mvc.*;
import views.html.*;

public class MachineController extends Controller {

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

    public static Result activityLog(String machineId){
    	if(!loggedIn()){
    		return redirect("/");
    	}
    	return ok(activityLog.render());
    }
    
    public static Result activityLogData(String machineId, String startDate, String endDate){
    	if(!loggedIn()){
    		return redirect("/");
    	}
    	ArrayList<ActivityLogModel> statusLogEntries = Database.getStatusUpdates(machineId,startDate,endDate);
    	ArrayList<ActivityLogModel> actionLogEntries = Database.getUIEvents(machineId,startDate,endDate);
    	ArrayList<ActivityLogModel> salesLogEntries = Database.getSales(machineId,startDate,endDate);
    	
    	ArrayList<ActivityLogModel> combinedEntries = new ArrayList<ActivityLogModel>();
    	
    	int statusIndex = 0;
    	int actionIndex = 0;
    	int salesIndex = 0;
    	
    	int statusLength = statusLogEntries.size();
    	int actionLength = actionLogEntries.size();
    	int salesLength = salesLogEntries.size();
    	
    	boolean useStatus, useAction, useSales;
    	
    	try{
	    	while(statusIndex<statusLength || actionIndex<actionLength || salesIndex<salesLength){
	    		//find next event chronologically
	    		useStatus=false;
	    		useAction=false;
	    		useSales=false;
	    		
	    		String string ="2200-01-01 00:00:00.000";
	    		
	    		Date statusDate= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse(string);
				Date actionDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse(string);
	    		Date salesDate= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse(string);
	    	
	    		if(statusIndex<statusLength){
	    			statusDate = statusLogEntries.get(statusIndex).date;
	    		}
	    		if(actionIndex<actionLength){
	    			actionDate = actionLogEntries.get(actionIndex).date;
	    		}
	    		if(salesIndex<salesLength){
	    			salesDate = salesLogEntries.get(salesIndex).date;
	    		}
	    		
	    		String nextEvent = "";
	    		
    			if(statusDate.before(actionDate) && statusDate.before(salesDate)){
    				nextEvent="status";
    			}else if(actionDate.before(statusDate) && actionDate.before(salesDate)){
    				nextEvent="action";
    			}else{
    				nextEvent="sales";
    			}
	    		
    			if(nextEvent.equals("status")){
    				combinedEntries.add(statusLogEntries.get(statusIndex));
    				statusIndex++;
    			}else if(nextEvent.equals("action")){
    				combinedEntries.add(actionLogEntries.get(actionIndex));
    				actionIndex++;
    			}else{
    				combinedEntries.add(salesLogEntries.get(salesIndex));
    				salesIndex++;
    			}
    			
	    		
	    	}
    	}catch(Exception e){
    		
    	}
    	
//    	for(int i=0;i<combinedEntries.size(); i++){
//    		System.out.println(combinedEntries.get(i).date+"  "+combinedEntries.get(i).entryType);
//    	}
    	
    	return ok(Json.toJson(combinedEntries));
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
		  Database.editMachineAndContainers(jn);
    	}else{
		  Database.addMachineAndContainers(jn);
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
    	System.out.println("event_type");
        Database.logEvent(machine_id, event_type, product_sku);
    	return ok();
    }
    
    public static Result machineJson(String id){

    	Machine machine = new Machine();

    	if(id.equals("-1")){
        	List<Container> containers = new ArrayList<Container>();
        	for(int i=0;i<2;i++){
        		Container container = new Container();
        		container.product = new Product();
        		containers.add(container);
        	}
        	for(int i=2; i<10; i++){
        		Container container = new Container();
        		container.product = new Product();
        		containers.add(container);
        	}
        	machine.containers = containers;
    	} else {
        	machine = Database.getMachine(id);
    	}


		return ok(Json.toJson(machine));
    }
    
    public static Result machineSales(String id) {
    	return ok(machineSales.render());
    }

    public static Result getSales(String id) {
  	  Integer machine = Integer.parseInt(id);
  	  List<Sale> sales = Database.getSalesByMachine(machine);
  	  JsonContext json = Ebean.createJsonContext();
  	  String p = json.toJsonString(sales);
  	  return ok(p);
    }

}