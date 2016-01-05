package controllers;

import java.io.File;
import java.text.NumberFormat;
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
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import models.Product;
import models.Receipt;

import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.databind.JsonNode;
import com.typesafe.plugin.*;

import play.mvc.*;
import play.mvc.Http.RequestBody;
import views.html.receiptEmail;

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
    int salesId = jn.get("sales_id").asInt();
    String email = jn.get("email").asText();
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


    MailerAPI mail = play.Play.application().plugin(MailerPlugin.class).email();
    mail.setSubject("Your BeautyTouch Receipt");
    mail.setRecipient(email);
    mail.setFrom("BeautyTouch <service@oasysventures.com>");

    NumberFormat formatter = NumberFormat.getCurrencyInstance();

    String address = receipt.machineAddress;

    String productRows = "";
    for(int i=0;i<receipt.products.size();i++){
      Product pm = receipt.products.get(i);
      productRows+=
        "<div style='width:100%' class='product'>" +
        "<span class='name'>" + pm.item_name + "</span><span style='float: right;" +
        "margin-right: 15%;'>" + formatter.format(Double.parseDouble(pm.price)) + "</span></div>";
    }

    String imageSource="https://oasysventures.com/assets/images/logoInc.png";
    String htmlString="<!DOCTYPE html> <html> <head> <link href='https://fonts.googleapis.com/css?family=Source+Sans+Pro' rel='stylesheet' type='text/css'> <style> body{line-height:24px;font-family: 'roboto condensed', sans-serif;} </style> </head> <body> <table width='100%' border='0' cellspacing='0' cellpadding='20' style='background-image: url(https://s3.amazonaws.com/oasys-images/email-background.png); background-repeat: repeat; background-size: 300px 300px; width: 100%'> <tr><td style='text-align: center;padding: 100px 0px 0px 0px;'> <img style='width: 70%; display: block; margin-left: auto; margin-right: auto;' src='https://s3.amazonaws.com/oasys-images/thanks-banner.png'/> </td></tr> <tr><td style='padding:0px; text-align: center;'> <div style='width: 70%; display: block; margin-left: auto; margin-right: auto; padding:10px 0px 10px 0px; text-align:left; background-color: white'> <div id='image-container' style='width: 30%; display: inline-block; position: relative;'> <img style='margin-left:15%; max-height: 200px; display: block; margin: auto;' src='https://s3.amazonaws.com/oasys-images/b.png'/> </div> <div id='product-info' style='    width: 70%; float: right; position: relative; margin-top: 30px;'>"
      + productRows +
      "<div style='width: 85%; border-top: 2px solid #bbb; padding-bottom: 20px; margin-top: 20px;'class='sale-info'> </div> <div style='width:100%'><span style='font-weight: bolder; font-size: 19px;'>Purchased at</span><span style='float: right; margin-right: 15%;font-weight: bolder; font-size: 19px;'>Total</span></div><div style='width:100%'> <span>" + address + "</span><span style='float: right; margin-right: 15%;'>$" + receipt.total + "</span></div> </div> </div> </td></tr> <tr><td style='padding:0px; text-align: center;'> <img style='width: 70%; display: block; margin-left: auto; margin-right: auto; padding-top: 15px; padding-bottom: 5px; background-color: #d9d3e8' src='https://s3.amazonaws.com/oasys-images/beauty-hack-header.png'/> </td></tr> <tr><td style='padding:0px; text-align: center;'> <div style='width: 70%; display: block; margin-left: auto; margin-right: auto; padding-top: 15px; padding-bottom: 5px; background-color: #d9d3e8'>" + receipt.beauty_hack + "</div> </td></tr> <tr><td style='text-align: center;padding: 0px 0px 100px 0px;'> <img style='    width: 70%; display: block; margin-left: auto; margin-right: auto;' src='https://s3.amazonaws.com/oasys-images/hashtag-banner.png'/> </td></tr> </table> </body> </html> <link href='https://fonts.googleapis.com/css?family=Roboto+Condensed:400,300,700' rel='stylesheet' type='text/css'>";

    mail.sendHtml(htmlString);
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

  public static Result test() {
    return ok(receiptEmail.render());
  }
}
