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
import play.*;
import play.libs.Json;
import play.mvc.*;
import views.html.*;

public class Checkout extends Controller {
  private static BraintreeGateway sGateway;

  private static final Logger.ALogger logger = Logger.of(Checkout.class);

  public static boolean loggedIn() {
    return session("user") != null;
  }

  public static BraintreeGateway getBraintreeGateway(){
    if (sGateway == null) {
      String mode = play.api.Play.current().mode().toString();
      if(mode.equals("Dev")){
        logger.info("Setting up Dev environment Braintree Gateway");
        sGateway = new BraintreeGateway(
            Environment.SANDBOX,
            "twxn6752pgdz9t5w",
            "8c6fj7fj2zv7s4cv",
            "7ca3fbd88b4afe21357d170ad5b6cd03"
            );
      }else{
        logger.info("Setting up production environment Braintree Gateway");
        sGateway = new BraintreeGateway(
            Environment.PRODUCTION,
            "4s2q3wpqv7czv643",
            "d7759v5wt29n6jw5",
            "8a68d2bedf0a15fe57740b33ff4ef337"
            );
      }
    }
    return sGateway;
  }

  public static Result vendingMain(String machineId){
    Machine machine = Database.getMachine(machineId);
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
    logger.info("getTokenBT initiated");
    try {
      String token = getBraintreeGateway().clientToken().generate(new ClientTokenRequest());
      logger.info("getTokenBT generated " + token);
      ObjectNode response = Json.newObject();
      response.put("token", token);
      return ok(response);
    }
    catch (Throwable e) {
      logger.error("getTokenBT error", e);
      throw e;
    }
  }

  public static Result processNonce(
      final String nonce,
      final String customerName,
      final String productIds,
      final String machineId,
      final String slots)
  {
    logger.info("processNonce nonce=" + nonce +
      " customerName=" + customerName +
      " productIds=" + productIds +
      " machineId=" + machineId +
      " slots=" + slots);

    PurchaseTransaction purchase = null;
    boolean ignoreException = false;
    try {
      purchase = new PurchaseTransaction(nonce, customerName, productIds, machineId, slots);
      purchase.process();
      if (purchase.isProcessed()) {
        purchase.record();
        // Notify vtiger in the background.
        notifyVtiger(purchase);
      }
    }
    catch (Throwable e) {
      logger.error("processNonce error", e);
      if (purchase == null || !purchase.isProcessed()) {
        throw e;
      }
    }

    ObjectNode response = Json.newObject();
    response.put("result", purchase != null && purchase.isProcessed() ? "success" : "failure");
    response.put("salesId", Objects.toString(purchase != null ? purchase.salesId : -1));
    return ok(response);
  }

  private static class PurchaseTransaction {
    final String nonce;
    final String customerName;
    final List<String> productIds;
    final String machineId;
    final List<String> slots;
    final String promoCode;
    final BigDecimal total;
    com.braintreegateway.Result<Transaction> result;
    long salesId = -1;
    
    PurchaseTransaction(
      final String nonce,
      final String customerName,
      final String productIds,
      final String machineId,
      final String slots)
    {
      this.nonce = nonce;
      this.customerName = customerName;
      this.productIds = parseList(productIds);
      this.machineId = machineId;
      this.slots = parseList(slots);
      this.promoCode = peelOffPromoCode();
      this.total = calculateTotalPrice();
      log("init purchase");
    }

    private void log(String tag) {
      logger.info(tag + " nonce=" + nonce);
      logger.info(tag + " customerName=" + customerName);
      logger.info(tag + " productIds=" + productIds);
      logger.info(tag + " machineId=" + machineId);
      logger.info(tag + " slots=" + slots);
      logger.info(tag + " promoCode=" + promoCode);
      logger.info(tag + " total=" + total);
      logger.info(tag + " result=" + (result == null ? "null" : (result.isSuccess() ? "success" : "failed")));
      logger.info(tag + " salesId=" + salesId);
    }

    // If there's a promo code, it appears at the end of the product ID list.
    private String peelOffPromoCode() {
      String lastProduct = productIds.get(productIds.size() - 1);
      System.out.println(promoCode);
      if (lastProduct.startsWith("code")) {
        lastProduct = lastProduct.substring(4);
        productIds.remove(productIds.size() - 1);
        if (lastProduct.length() > 0) {
          return lastProduct;
        }
      }
      return null;
    }

    private BigDecimal calculateTotalPrice() {
      BigDecimal total = BigDecimal.ZERO;
      for (String product : productIds) {
        // If product ID is invalid, the following will throw an exception.
        BigDecimal productPrice = new BigDecimal(Database.getProductPrice(product));
        total = total.add(productPrice);
      }
      return applyPromoCode(total);
    }

    private BigDecimal applyPromoCode(BigDecimal total) {
      if (promoCode != null) {
        Promo promo = Ebean.find(Promo.class, promoCode);
        if (promo == null) {
          logger.warn("invalid promo code " + promoCode);
        }
        else {
          if (total.compareTo(promo.threshold) < 0) {
            logger.warn("total=" + total + " does not exceed threshold=" + promo.threshold + " for promo code=" + promoCode);
          }
          else {
            logger.info("processNonce applying promo=" + promo + " to inital total=" + total);
            total = total.subtract(new BigDecimal(promo.flat_discount));
            BigDecimal percent = new BigDecimal(100 - +promo.percent_discount);
            total = total.multiply(percent);
            total = total.divide(new BigDecimal(100));
            promo.num_uses = promo.num_uses + 1;
            Ebean.save(promo);
            logger.info("processNonce total after promo=" + total);
          }
        }
      }
      return total;
    }

    // Perform the transaction.
    void process() {
      result = getBraintreeGateway().transaction().sale(new TransactionRequest()
        .amount(total)
        .paymentMethodNonce(nonce)
        .customer()
          .firstName(customerName)
          .done()
        .options()
          .submitForSettlement(true)
          .done());
      log("after braintree transaction");
    }

    boolean isProcessed() {
      return result != null && result.isSuccess();
    }

    // Record the sale.
    void record() {
      salesId = Database.recordSale(machineId, productIds, total, promoCode);

      // Decrement inventory.
      Database.removeItem(machineId, slots);

      // Send email alert.
      Email.alertSale(machineId, productIds, total);
      
      log("purchase recorded");
    }
  }

  private static List<String> parseList(String str)
  {
    return new ArrayList<String>(Arrays.asList(str.split(",")));
  }

  private static void notifyVtiger(final PurchaseTransaction purchase) {
    Runnable aRunnable = new Runnable(){
      @Override
      public void run() {
        //this is vtiger user key found in user account and preferences
        String userkey="NiOsG78vNVN6ByO9";
        String vtigerURL="https://beautytouch.od2.vtiger.com/webservice.php";
        String username="aramirez@serpol.com";
        String assignedToId="19x6";
        String SessionId="";
        String Status="";
        String JsonFields;
        String Module="sales";

        for (int i = 0; i < 5; i++) {
          SessionId=VTiger.GetLoginSessionId(vtigerURL,userkey,username);
          logger.info("notifyVtiger: Checkout Session:"+SessionId);
          if (!SessionId.substring(0,5).equals("FAIL:")){
            break;
          }
          try {
            Thread.sleep(1000);
          } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
          }
        }

        if (!SessionId.substring(0,5).equals("FAIL:")){
          Transaction transaction = purchase.result.getTarget();
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
            +",\"assigned_user_id\":\""+assignedToId+"\""
            +",\"fld_machineid\":\""+purchase.machineId+"\""
            +",\"fld_productid\":\""+purchase.productIds+"\""
            +",\"cf_1014\":\""+purchase.total+"\""
            +",\"cf_1016\":\""+BTreeAmount.toString()+"\""
            +",\"cf_1020\":\""+CardType+"\""
            +",\"cf_1022\":\""+CardNumber+"\""
            +",\"cf_1018\":\""+PurchaseDate+"\""
            +",\"cf_1024\":\""+purchase.salesId+"\""
            +",\"fld_name\":\""+BTreeCustomerName+"\""
            +",\"fld_braintreeid\":\""+BTreeid+"\"}";


          Status=VTiger.Create(vtigerURL,SessionId,Module,JsonFields);
          Status=VTiger.Logout(vtigerURL,SessionId);
          logger.info("notifyVtiger: " +  JsonFields);
        } else {
          logger.warn("notifyVtiger: fail");
        }
      }
    };
    ExecutorService fixedPool = Executors.newFixedThreadPool(1);
    Future<?> runnableFuture = fixedPool.submit(aRunnable);
    fixedPool.shutdown();
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
