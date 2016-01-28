package controllers;

import java.io.File;
import java.text.NumberFormat;
import java.util.Date;
import java.util.List;
import java.math.BigDecimal;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.lang3.time.DateUtils;

import models.Product;
import models.Receipt;

import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.databind.JsonNode;
import com.typesafe.plugin.*;

import play.mvc.*;
import play.mvc.Http.RequestBody;
import views.html.receiptEmail;
import views.html.feedback;

//{
//	"sales_id": "2",
//	"email": "amco1027@gmail.com"
//  "key":"6b6f7834-1392-11e5-b60b-1697f925ec7b"
//}

public class Email extends Controller {

  public static Result alertSale(String machineId, List<String> productIds, BigDecimal price) {
    System.out.println("alertSale");
    String product_names = "";
    for (String productId : productIds) {
      Product product = Ebean.find(Product.class, productId);
      product_names += ", " + product.item_name;
    }
    String[] recipients = {
      "alina@oasysventures.com",
      "jackie@oasysventures.com",
      "mackenzie@oasysventures.com",
      "shireen@oasysventures.com"
    };
    for (String recipient: recipients) {
      MailerAPI mail = play.Play.application().plugin(MailerPlugin.class).email();
      mail.setSubject("Oasys Sale");
      mail.setRecipient(recipient);
      mail.setFrom("Oasys <service@oasysventures.com>");

      mail.sendHtml("<p>Oasys purchase at machine " + machineId + ". Product(s)  '" + product_names + "' sold for $" + price.toString() + " total.</p>");
    }
    return ok();
  }

  public static Result alertFail() {
    JsonNode jn = request().body().asJson();
    String machineId = jn.get("machine_id").asText();
    String message = jn.get("message").asText();
    System.out.println(jn);
    String[] recipients = {
      "alina@oasysventures.com",
      "jackie@oasysventures.com",
      "mackenzie@oasysventures.com",
      "shireen@oasysventures.com"
    };
    for (String recipient: recipients) {
      MailerAPI mail = play.Play.application().plugin(MailerPlugin.class).email();
      mail.setSubject("Sales Error");
      mail.setRecipient(recipient);
      mail.setFrom("Oasys <service@oasysventures.com>");
      mail.sendHtml("<p>" + message + " at machine " + machineId +".");
    }
    return ok();
  }


  public static Result sendSuggestion() {
    JsonNode jn = request().body().asJson();
    String machineId = jn.get("machine_id").asText();
    String suggestion = jn.get("suggestion").asText();
    String[] recipients = {
      "alina@oasysventures.com",
      "jackie@oasysventures.com",
      "mackenzie@oasysventures.com",
      "shireen@oasysventures.com"
    };
    for (String recipient: recipients) {
      MailerAPI mail = play.Play.application().plugin(MailerPlugin.class).email();
      mail.setSubject("Email Captured");
      mail.setRecipient(recipient);
      mail.setFrom("Oasys <service@oasysventures.com>");
      mail.sendHtml("<p>Oasys email captured from machine " + machineId + ". Email: " + suggestion + "</p>");
    }
    return ok();
  }

  public static Result sendReceipt(){
	  
    JsonNode jn = request().body().asJson();
    final int salesId = jn.get("sales_id").asInt();
    final String email = jn.get("email").asText();
    String key = jn.get("key").asText();
    if(!Security.validKey(key)){
      return ok();
    }

    /*
    
       int salesId = 152;
       String email = "alina@beautytouch.co";
    */

    //add customer to database
    try{
      Database.addCustomer("",email,salesId);
    }catch(Exception e){
      System.out.println(e.toString());
    }

    //get product details from database
    Receipt receipt = Database.getReceiptDetails(salesId);
    System.out.println(receipt);


    MailerAPI mail = play.Play.application().plugin(MailerPlugin.class).email();
    mail.setSubject("Your BeautyTouch Receipt");
    mail.setRecipient(email);
    mail.setFrom("BeautyTouch <service@oasysventures.com>");

    NumberFormat formatter = NumberFormat.getCurrencyInstance();

    String address = receipt.machineAddress;
    
    //read in top
    StringBuilder contentBuilder = new StringBuilder();
    try {
        BufferedReader in = new BufferedReader(new FileReader("app/views/receipt_top.scala.html"));
        String str;
        while ((str = in.readLine()) != null) {
            contentBuilder.append(str);
        }
        in.close();
    } catch (IOException e) {
    	System.out.println(e);
    }
    String content = contentBuilder.toString();
    //read in products
    for (int i = 0; i < receipt.products.size(); i++) {
    	StringBuilder productBuilder = new StringBuilder();
    	Product p = receipt.products.get(i);
    	try {
            BufferedReader in = new BufferedReader(new FileReader("app/views/receipt_product.scala.html"));
            String str;
            while ((str = in.readLine()) != null) {
                productBuilder.append(str);
            }
            in.close();
        } catch (IOException e) {
        	System.out.println(e);
        }
    	String productContent = productBuilder.toString();
    	productContent = productContent.replace("{{product-name}}", p.item_name);
    	productContent = productContent.replace("{{product-image}}", p.item_img);
    	productContent = productContent.replace("{{product-price}}", formatter.format(Double.parseDouble(p.price)));
    	content = content + productContent;
    }
    
    //read in bottom
    StringBuilder bottomBuilder = new StringBuilder();
    try {
        BufferedReader in = new BufferedReader(new FileReader("app/views/receipt_bottom.scala.html"));
        String str;
        while ((str = in.readLine()) != null) {
            bottomBuilder.append(str);
        }
        in.close();
    } catch (IOException e) {
    	System.out.println(e);
    }
    content = content + bottomBuilder.toString();
    
    //replace content
    content = content.replace("{{product-total}}", receipt.total);
    content = content.replace("{{purchase-location}}", address);
    Date saleDate = DateUtils.addHours(receipt.time, 5);
    content = content.replace("{{purchase-date}}", saleDate.toString());
    content = content.replace("{{machine-id}}",  receipt.machine_id);
    
    mail.sendHtml(content);
    ExecutorService fixedPool = Executors.newFixedThreadPool(1);
    Runnable aRunnable = new Runnable(){
      @Override
        public void run() {
          String userkey="NiOsG78vNVN6ByO9";
          String vtigerURL="https://beautytouch.od2.vtiger.com/webservice.php";
          String username="aramirez@serpol.com";
          String assignedToId="19x6";
          String SessionId="";
          String Status="";
          String JsonFields;
          String Module="salescontacts";
          System.out.print("Getting Session");
          //SessionId=VTiger.GetLoginSessionId(vtigerURL,userkey,username);
          for (int i = 0; i < 5; i++) {
            SessionId=VTiger.GetLoginSessionId(vtigerURL,userkey,username);
            System.out.print("Email Session:"+SessionId);
            if (!SessionId.substring(0,5).equals("FAIL:")){
              break;
            }
            try {
              Thread.sleep(1000);
            } catch(InterruptedException ex) {
              Thread.currentThread().interrupt();
            }
          }
          System.out.print("End of loop");

          if (!SessionId.substring(0,5).equals("FAIL:")){
            JsonFields="{\"fld_salescontactsname\":\""+email+"\""
              +",\"assigned_user_id\":\""+assignedToId+"\""
              +",\"fld_systemsalesid\":\""+salesId+"\"}";

            System.out.print("BeforeCreate");
            Status=VTiger.Create(vtigerURL,SessionId,Module,JsonFields);
            System.out.print("Create "+Status);
            System.out.print("AfterCreate");
            Status=VTiger.Logout(vtigerURL,SessionId);
            System.out.print("Logout "+Status);
          }
        }
    };
    Future<?> runnableFuture = fixedPool.submit(aRunnable);

    fixedPool.shutdown();
    return ok();

  }
  
  public static void replace( String target, String replacement, 
          StringBuilder builder ) { 
	  int indexOfTarget = -1;
	  while( ( indexOfTarget = builder.indexOf( target ) ) >= 0 ) { 
		  builder.replace( indexOfTarget, indexOfTarget + target.length() , replacement );
	  }
  }

  public static Result recordFeedback() {
	  return ok(feedback.render());
  }
  public static Result test() {
    return ok();
  }
}
