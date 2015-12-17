package controllers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.text.json.JsonContext;
import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.ClientTokenRequest;
import com.braintreegateway.Environment;
import com.braintreegateway.Transaction;
import com.braintreegateway.TransactionRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

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

import models.Machine;
import models.Product;
import models.Promo;
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

  public static Result getPromos() {
    List<Promo> promos = Ebean.find(Promo.class).where()
      .eq("status", 1)
      .findList();
    JsonContext json = Ebean.createJsonContext();
    String p = json.toJsonString(promos);
    return ok(p);
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

  public static Result processNonce(String nonce, String name, String productIds,final String machineId, String slot){
    BigDecimal total = new BigDecimal(0);

    long salesId=-1;
    System.out.println(productIds);
    System.out.println(slot);
    final List<String> products = new ArrayList<String>(Arrays.asList(productIds.split(",")));
    System.out.println(products);
    String promoCode = products.get(products.size() - 1);
    System.out.println(promoCode);
    String code = "";
    if (promoCode.indexOf("code") >= 0) {
      //last element is code;
      code = promoCode.substring(4);
      products.remove(products.size() - 1);
      System.out.println(code);
    }
    System.out.println(products);

    for (String product : products) {
      BigDecimal productPrice = new BigDecimal(Database.getProductPrice(product));
      total = total.add(productPrice);
    }
    System.out.println(total);
    if (code.length() > 0) {
      //apply code
      Promo promo = Ebean.find(Promo.class, code);
      System.out.println(promo);
      if (total.compareTo(promo.threshold) >= 0) {
        total = total.subtract(new BigDecimal(promo.flat_discount));
        BigDecimal percent = new BigDecimal(100 - +promo.percent_discount);
        total = total.multiply(percent);
        total = total.divide(new BigDecimal(100));
        promo.num_uses = promo.num_uses + 1;
        Ebean.save(promo);
      }
    }
    System.out.println("adj total: " + total);
    TransactionRequest request = new TransactionRequest()
      .amount(total)
      .paymentMethodNonce(nonce)
      .customer()
      .firstName(name)
      .done()
      .options()
      .submitForSettlement(true)
      .done();

    final com.braintreegateway.Result<Transaction> result = gateway.transaction().sale(request);

    ObjectNode response = Json.newObject();
    System.out.println("response"+result.isSuccess());
    if(result.isSuccess()){
      salesId=-1;
      try{
        //log purchase
        salesId = Database.recordSale(machineId, products, total);
        System.out.println(salesId);

        //decrement inventory
        Database.removeItem(machineId,slot);

        //send email alert
        Email.alertSale(machineId, products, total);
        final String stotal=total.toString();
        final String ssalesId=Long.toString(salesId);
        //this is vtiger user key found in user account and preferences
        ExecutorService fixedPool = Executors.newFixedThreadPool(1);
        Runnable aRunnable = new Runnable(){
          @Override
            public void run() {
              String userkey="NiOsG78vNVN6ByO9";
              String vtigerURL="https://beautytouch.od2.vtiger.com/webservice.php";
              String username="aramirez@serpol.com";
              String SessionId="";
              String Status="";
              String JsonFields;
              String Module="sales";
              System.out.println("Getting Session");

              for (int i = 0; i < 5; i++) {
                SessionId=VTiger.GetLoginSessionId(vtigerURL,userkey,username);
                System.out.print("Checkout Session:"+SessionId);
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
                Transaction transaction = result.getTarget();
                String FirstName=transaction.getCustomer().getFirstName().toString();
                String BTreeCustomerName=FirstName;
                String BTreeid=transaction.getId();
                String CardType=transaction.getCreditCard().getCardType();
                String CardNumber=transaction.getCreditCard().getLast4();
                BigDecimal BTreeAmount = transaction.getAmount();
                int year=transaction.getCreatedAt().YEAR;
                int month=transaction.getCreatedAt().MONTH;
                int day=transaction.getCreatedAt().DAY_OF_MONTH;
                String PurchaseDate="";
                if (month<10)
                  PurchaseDate=year+"-0"+month;
                else
                  PurchaseDate=year+"-"+month;

                if (day<10)
                  PurchaseDate=PurchaseDate+"-0"+day;
                else
                  PurchaseDate=PurchaseDate+"-"+day;

                String sTime="";

                int hour=transaction.getCreatedAt().HOUR_OF_DAY;
                int min=transaction.getCreatedAt().MINUTE;

                if (min<10)
                  sTime=hour+":0"+min;
                else
                  sTime=hour+":"+min;

                PurchaseDate=PurchaseDate+" "+sTime;

                JsonFields="{\"fld_salesname\":\"New Sale\""
                  +",\"assigned_user_id\":\""+username+"\""
                  +",\"fld_machineid\":\""+machineId+"\""
                  +",\"fld_productid\":\""+products+"\""
                  +",\"cf_1014\":\""+stotal+"\""
                  +",\"cf_1016\":\""+BTreeAmount.toString()+"\""
                  +",\"cf_1020\":\""+CardType+"\""
                  +",\"cf_1022\":\""+CardNumber+"\""
                  +",\"cf_1018\":\""+PurchaseDate+"\""
                  +",\"cf_1024\":\""+ssalesId+"\""
                  +",\"fld_name\":\""+BTreeCustomerName+"\""
                  +",\"fld_braintreeid\":\""+BTreeid+"\"}";


                Status=VTiger.Create(vtigerURL,SessionId,Module,JsonFields);
                Status=VTiger.Logout(vtigerURL,SessionId);
                System.out.println(JsonFields);
              } else {
                System.out.println("fail");
              }
            }
        };
        Future<?> runnableFuture = fixedPool.submit(aRunnable);

        fixedPool.shutdown();
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
