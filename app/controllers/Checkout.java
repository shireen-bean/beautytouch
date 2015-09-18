package controllers;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import com.avaje.ebean.Ebean;
import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.ClientTokenRequest;
import com.braintreegateway.Environment;
import com.braintreegateway.Transaction;
import com.braintreegateway.TransactionRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import models.Machine;
import models.Product;
import models.SaleProduct;
import play.libs.Json;
import play.mvc.*;
import views.html.*;

public class Checkout extends Controller {
	private static boolean gatewayIsSetup = false;
    private static BraintreeGateway gateway;// 
    
	public static boolean loggedIn(){
		if(session("user")==null){
			return false;
		}
		return true;
	}
	
    public static void setupGateway(){
    	String mode = play.api.Play.current().mode().toString();
    	if(mode.equals("Dev")){
    		gateway = new BraintreeGateway(
                Environment.SANDBOX,
                "twxn6752pgdz9t5w",
                "8c6fj7fj2zv7s4cv",
                "7ca3fbd88b4afe21357d170ad5b6cd03"
            );
    	}else{
    		gateway = new BraintreeGateway(
    				  Environment.PRODUCTION,
    				  "4s2q3wpqv7czv643",
    				  "d7759v5wt29n6jw5",
    				  "8a68d2bedf0a15fe57740b33ff4ef337"
    				);
    	}
    	gatewayIsSetup=true;
    }
    
    
    public static Result vendingMain(String machineId){
    	if(!gatewayIsSetup){
    		setupGateway();
    	}
    	
    	Machine machine = new Machine();
    	
        	machine = Database.getMachine(machineId);
        	System.out.println(machine);
    	JsonNode jsonMachine = Json.toJson(machine);
    	String jsonString = jsonMachine.toString();
    	return ok(vendingMain.render(jsonString));
    }
    
    public static Result thankYou(){
    	return ok(thankYou.render());
    }

    public static Result receipt(){
    	System.out.println("receipt");
    	return ok(receipt.render());
    }
    
    public static Result pay(String productId){
    	return ok(pay.render());
    }
  
    public static Result processing(){
    	return ok(processing.render());
    }

    public static Result test(String saleId) {
    	return ok(receiptEmail.render());
    }
    public static Result vending(){
    	return ok(vending.render());
    }
    
    public static Result salesProduct() {
    	return ok(salesProduct.render());
    }
    
    public static Result productEngagements() {
    	return ok(productEngagements.render());
    }
    
    public static Result machineEngagements() {
    	return ok(machineEngagements.render());
    }
    public static Result salesMachine() {
    	return ok(salesMachine.render());
    }

    public static Result paymentFailed(){
    	return ok(paymentFailed.render());
    }
    
    public static Result getTokenBT(){
    	ClientTokenRequest clientTokenRequest = new ClientTokenRequest();
    	String token = gateway.clientToken().generate(clientTokenRequest);
    	ObjectNode response = Json.newObject();
    	
    	response.put("token", token);
    	
     	return ok(response);
    }
    
    public static Result processNonce(String nonce, String name, String productId, String machineId, String column){
    	
    	BigDecimal productPrice = new BigDecimal(Database.getProductPrice(productId));
    	TransactionRequest request = new TransactionRequest()
        .amount(productPrice)
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
    		long salesId=-1;
    		try{
    	      //log purchase
    	      //TODO: update when selling multiple items per purchase
    	      salesId = Database.recordSale(machineId, productId, productPrice);
    		  //decrement inventory
    		  Database.removeItem(machineId,column);
    		  //send email alert
    		  Email.alertSale(machineId, productId, productPrice);
    		}catch(Exception e){
    			
    		}
    		response.put("result","success");
        	response.put("salesId", Objects.toString(salesId));
    	}
    	else{
    		response.put("result","failure");
        	response.put("salesId", "-1");
    	}
    	
     	return ok(response);
    }
    
    
    public static Result productSaleCount(String sku) {
    	int id = Integer.parseInt(sku);
    	return ok("" + Database.getNumSalesByProduct(id));
    }
    
    public static Result productTapCount(String sku) {
    	int id = Integer.parseInt(sku);
    	return ok("" + Database.getNumTapsByProduct(id));
    }
    
    public static Result machineSaleCount(String id) {
    	int machine = Integer.parseInt(id);
    	return ok(""+ Database.getNumSalesByMachine(machine));
    }
    
    public static Result machineTapCount(String id) {
    	int machine = Integer.parseInt(id);
    	return ok("" + Database.getNumTapsByMachine(machine));
    }
    
    public static Result machineSaleAverage(String id) {
    	int machine = Integer.parseInt(id);
    	return ok("" + Database.getAvgSaleByMachine(machine));
    }
    
    public static Result getProduct(String id) {
    	List<SaleProduct> data = Ebean.find(SaleProduct.class).where()
    			.eq("sales_id", id)
    			.setMaxRows(1)
    			.findList();
    	int product_id = data.get(0).product_sku;
    	Product product = Ebean.find(Product.class, product_id);
    	return ok(product.item_name);
    
    }
    
}
