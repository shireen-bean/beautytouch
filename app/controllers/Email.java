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
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

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

  private static final String MC_APIKEY = "apikey 2885d7d565c38d48642383308d7c8671-us12";
  private static final String DEL_AUTH = "Basic SVhOSGhpUXVvNkFTNExzQzE5TDc5c2pIcnFkaHZ3MUM6";
  private static final String MC_MASTER_LIST_ID = "01227e9c11";

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
      "james@oasysventures.com",
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
    String[] recipients = {
      "alina@oasysventures.com",
      "jackie@oasysventures.com",
      "mackenzie@oasysventures.com",
      "james@oasysventures.com",
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

  public static Result alertSuccess() {
    JsonNode jn = request().body().asJson();
    String machineId = jn.get("machine_id").asText();
    String message = jn.get("message").asText();
    String[] recipients = {
      "alina@oasysventures.com",
      "jackie@oasysventures.com",
      "mackenzie@oasysventures.com",
      "james@oasysventures.com",
      "shireen@oasysventures.com"
    };
    for (String recipient: recipients) {
      MailerAPI mail = play.Play.application().plugin(MailerPlugin.class).email();
      mail.setSubject("Sales Success");
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
      "james@oasysventures.com",
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

    System.out.println("receipt");


    JsonNode jn = request().body().asJson();
    final int salesId = jn.get("sales_id").asInt();
    final String email = jn.get("email").asText();
    String key = jn.get("key").asText();
    if(!Security.validKey(key)){
      System.out.println("fail");
      return ok();
    }


    /*

       int salesId = 152;
       String email = "alina+test3@beautytouch.co";
       */

    //add customer to database
    try{
      Database.addCustomer("",email,salesId);
    }catch(Exception e){
      System.out.println(e.toString());
    }

    //get product details from database
    final Receipt receipt = Database.getReceiptDetails(salesId);
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
      BufferedReader in = new BufferedReader(new FileReader("../../oasys/app/views/receipt_top.scala.html"));
      //BufferedReader in = new BufferedReader(new FileReader("app/views/receipt_top.scala.html"));
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
        BufferedReader in = new BufferedReader(new FileReader("../../oasys/app/views/receipt_product.scala.html"));
        //BufferedReader in = new BufferedReader(new FileReader("app/views/receipt_product.scala.html"));
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
      BufferedReader in = new BufferedReader(new FileReader("../../oasys/app/views/receipt_bottom.scala.html"));
      //BufferedReader in = new BufferedReader(new FileReader("app/views/receipt_bottom.scala.html"));
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
          addToMailChimp(email, receipt.machine_id);
          addToDelighted(email);
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
  public static Result test(String email, String id) {
    System.out.println(id);
    String address = "alina+test2@beautytouch.co";
    addToMailChimp(address, id);
    return ok();
  }

  public static void addToMailChimp(String email, String machine_id) {

    //get interests
    HttpClient httpClient = new DefaultHttpClient();
    HttpGet httpGet = new HttpGet("https://us12.api.mailchimp.com/3.0/lists/" + MC_MASTER_LIST_ID + "/interests");
    httpGet.setHeader("Authorization", MC_APIKEY);

    JSONObject interests = new JSONObject();
    try {
      HttpResponse response = httpClient.execute(httpGet);
      HttpEntity entity = response.getEntity();
      String responseString = EntityUtils.toString(entity, "UTF-8");

      final JSONObject jsonResponse = new JSONObject(responseString);
      JSONArray interestResults = jsonResponse.getJSONArray("interests");
      //find group that matches machine_id
      for (int i = 0; i < interestResults.length(); i++) {
        JSONObject interest = interestResults.getJSONObject(i);
        String id = interest.getString("id");
        String name = interest.getString("name");
        if (name.startsWith(machine_id) ){
          interests.accumulate(id, true);
        }
      }
      System.out.println(interests);
    } catch (Exception e) {
      System.out.println(e);
    }
    HttpPost httpPost = new HttpPost("https://us12.api.mailchimp.com/3.0/lists/" + MC_MASTER_LIST_ID + "/members");

    JSONObject jsonObject = new JSONObject();
    jsonObject.accumulate("email_address", email);
    jsonObject.accumulate("status", "subscribed");
    jsonObject.accumulate("interests",  interests);
    try {
      String json = jsonObject.toString();
      StringEntity se = new StringEntity(json);
      httpPost.setEntity(se);
      httpPost.setHeader("Authorization", MC_APIKEY);
      httpPost.setHeader("content-type", "application/json");
      HttpResponse httpResponse = httpClient.execute(httpPost);
      System.out.println(httpResponse.toString());
    } catch (Exception e) {
      System.out.println(e);
    }
  }

  public static void addToDelighted(String email) {
    HttpClient httpClient = new DefaultHttpClient();
    HttpPost httpPost = new HttpPost("https://api.delighted.com/v1/people.json");

    JSONObject jsonObject = new JSONObject();
    jsonObject.accumulate("email", email);
    jsonObject.accumulate("delay", 43200);

    try {
      String json = jsonObject.toString();
      StringEntity se = new StringEntity(json);
      httpPost.setEntity(se);
      httpPost.setHeader("Authorization", DEL_AUTH);
      httpPost.setHeader("content-type", "application/json");
      HttpResponse httpResponse = httpClient.execute(httpPost);
      System.out.println(httpResponse.toString());
    } catch (Exception e) {
      System.out.println(e);
    }
  }

  public static Result recordFeedback() {
    return ok(feedback.render());
  }

}
