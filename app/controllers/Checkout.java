package controllers;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;

import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.ClientTokenRequest;
import com.braintreegateway.Environment;
import com.braintreegateway.Transaction;
//import com.braintreegateway.Result;
import com.braintreegateway.TransactionRequest;
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

public class Checkout extends Controller {
	
    private static BraintreeGateway gateway = new BraintreeGateway(
            Environment.SANDBOX,
            "twxn6752pgdz9t5w",
            "8c6fj7fj2zv7s4cv",
            "7ca3fbd88b4afe21357d170ad5b6cd03"
        );
    
    public static Result productSelect(){
    	return ok(checkoutProductSelect.render());
    }
    
    public static Result thankYou(String productId){
    	return ok(thankYou.render());
    }

    public static Result pay(String productId){
    	return ok(pay.render());
    }
  
    public static Result processing(){
    	return ok(processing.render());
    }

    public static Result vending(){
    	return ok(vending.render());
    }
    
    public static Result getTokenBT(){
    	ClientTokenRequest clientTokenRequest = new ClientTokenRequest();
    	String token = gateway.clientToken().generate(clientTokenRequest);
    	ObjectNode response = Json.newObject();
    	
    	response.put("token", token);
    	
     	return ok(response);
    }
    
    public static Result processNonce(String nonce, String name, String productId, String machineId, String column){
    	
    	
    	TransactionRequest request = new TransactionRequest()
        .amount(new BigDecimal(Database.getProductPrice(productId)))
        .paymentMethodNonce(nonce)
        .customer()
        .firstName(name)
        .done()
        .options()
        .submitForSettlement(true)
        .done();
        

    	com.braintreegateway.Result<Transaction> result = gateway.transaction().sale(request);
    	
    	ObjectNode response = Json.newObject();
    	System.out.println("response"+result.isSuccess());
    	if(result.isSuccess()){
    		try{
    		Database.removeItem(machineId,column);
    		}catch(Exception e){
    			
    		}
        	response.put("result", "success");
    	}
    	else{
        	response.put("result", "failed");
    	}
    	
     	return ok(response);
    }
}
