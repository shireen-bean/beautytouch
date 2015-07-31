package controllers;
import java.text.NumberFormat;
import java.util.Map;
import java.util.HashMap;

import com.fasterxml.jackson.databind.JsonNode;
import com.twilio.sdk.resource.instance.Account;
import com.twilio.sdk.TwilioRestClient;
import com.twilio.sdk.TwilioRestException;
import com.twilio.sdk.resource.factory.MessageFactory;
import com.twilio.sdk.resource.instance.Message;

import java.util.ArrayList;
import java.util.List;

import models.ProductModel;
import models.Receipt;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import play.mvc.*;


public class SMS extends Controller {

  public static final String ACCOUNT_SID = "AC8758e524bc09f7a4fb5da074d4da0f0c";
  public static final String AUTH_TOKEN = "75f2dc2fceca3353d55e883cb473b32e";


  //{
  //		"sales_id": "2",
  //		"phone_number": "amco1027@gmail.com"
  //}

  public static Result sendReceipt(){

    //get sales id and customer info
    JsonNode jn = request().body().asJson();
    int salesId = jn.get("sales_id").asInt();
    String phoneNumber = jn.get("phone_number").asText();
    String key = jn.get("key").asText();
    if(!Security.validKey(key)){
      return ok();
    }

    //add customer to database
    try{
      Database.addCustomer(phoneNumber,"",salesId);
    }catch(Exception e){
      System.out.println(e.toString());
    }

    //get product details from database
    Receipt receipt = Database.getReceiptDetails(salesId);


    try{
      TwilioRestClient client = new TwilioRestClient(ACCOUNT_SID, AUTH_TOKEN);

      Account account = client.getAccount();

      MessageFactory messageFactory = account.getMessageFactory();
      List<NameValuePair> params = new ArrayList<NameValuePair>();
      params.add(new BasicNameValuePair("To", phoneNumber));
      params.add(new BasicNameValuePair("From", "+18597590660"));


      NumberFormat formatter = NumberFormat.getCurrencyInstance();

      String messageBody = "Thank you for your Oasys purchase!\n"+
        receipt.machineAddress+
        "\nSales #: "+salesId;

      String productRows = "\n";
      for(int i=0;i<receipt.products.size();i++){
        ProductModel pm = receipt.products.get(i);
        productRows+=
          "\n"+pm.itemName+": "+formatter.format(Double.parseDouble(pm.price));
      }

      messageBody+=productRows;
      messageBody+="\n\nTotal: "+formatter.format(Double.parseDouble(receipt.total));


      params.add(new BasicNameValuePair("Body", messageBody));
      Message sms = messageFactory.create(params);
    }catch(Exception e){
      System.out.println(e.toString());
    }
    return ok();
  }

  public static Result reportProblem() {
    System.out.println("REPORTING PROBLEM");

    JsonNode jn = request().body().asJson();
    String machineId = jn.get("machine_id").asText();
    String email = "";
    String other = "";
    JsonNode fd = jn.get("formData");
    String problem = fd.get("problem").asText();
    if (fd.has("email")) {
    	email = fd.get("email").asText();
    }
    if (fd.has("other")) {
    	other = fd.get("other").asText();
    }
    System.out.println(problem);
    System.out.println(machineId);
    System.out.println(email);
    try {
      TwilioRestClient client = new TwilioRestClient(ACCOUNT_SID, AUTH_TOKEN);
      Account account = client.getAccount();

      MessageFactory messageFactory = account.getMessageFactory();
      List<NameValuePair> params = new ArrayList<NameValuePair>();
      params.add(new BasicNameValuePair("To", "+16095779836"));
      params.add(new BasicNameValuePair("From", "+18597590660"));

      String messageBody = "Problem reported with machine: " + machineId + ". Issue: " + problem + ". ";
      if (other != "") {
    	  messageBody += "\"" + other + "\". ";
      }
      if (email != "") {
    	  messageBody += "Email: " + email;
      }

      params.add(new BasicNameValuePair("Body", messageBody));
      Message sms = messageFactory.create(params);
    }catch (Exception e) {
      System.out.println(e.toString());
    }
    return ok();
  }
}
