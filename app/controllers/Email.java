package controllers;

import java.io.File;
import java.text.NumberFormat;
import java.util.List;
import java.math.BigDecimal;

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
      "marisa@oasysventures.com",
      "shireen@oasysventures.com"
    };
    for (String recipient: recipients) {
      MailerAPI mail = play.Play.application().plugin(MailerPlugin.class).email();
      mail.setSubject("Oasys Sale");
      mail.setRecipient(recipient);
      mail.setFrom("Oasys <service@oasysventures.com>");

      mail.sendHtml("<p>Oasys purchase at machine " + machineId + ". Product(s) '" + product_names + "' sold for $" + price.toString() + " total.</p>");
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
      "marisa@oasysventures.com"
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


    //add customer to database
    try{
      Database.addCustomer("",email,salesId);
    }catch(Exception e){
      System.out.println(e.toString());
    }

    //get product details from database
    Receipt receipt = Database.getReceiptDetails(salesId);


    MailerAPI mail = play.Play.application().plugin(MailerPlugin.class).email();
    mail.setSubject("Oasys Receipt");
    mail.setRecipient(email);
    mail.setFrom("Oasys <service@oasysventures.com>");

    NumberFormat formatter = NumberFormat.getCurrencyInstance();
    Product product = receipt.products.get(0);
    String name = product.item_name;
    String price = formatter.format(Double.parseDouble(product.price));
    String image = product.item_img;
    String address = receipt.machineAddress;
    /*
       NumberFormat formatter = NumberFormat.getCurrencyInstance();

       String productRows = "";
       for(int i=0;i<receipt.products.size();i++){
       Product pm = receipt.products.get(i);
       productRows+=
       "<tr>"+
       "<td style='padding-right:250px;'>"+pm.item_name+"</td><td></td>"+
       " <td>"+formatter.format(Double.parseDouble(pm.price))+"</td>"+
       "</tr>";
       }
       */

    String imageSource="https://oasysventures.com/assets/images/logoInc.png";
    String htmlString="<!DOCTYPE html> <html> <head> <link href='https://fonts.googleapis.com/css?family=Source+Sans+Pro' rel='stylesheet' type='text/css'> <style> body{line-height:24px;font-family: 'Source Sans Pro', sans-serif;} </style> </head> <body> <table width='100%' border='0' cellspacing='0' cellpadding='20' style='background-image: url(https://s3.amazonaws.com/oasys-images/email-background.png); background-repeat: repeat; background-size: 300px 300px; width: 100%'> <tr><td style='text-align: center;padding: 100px 0px 0px 0px;'> <img style='width: 70%; display: block; margin-left: auto; margin-right: auto;' src='https://s3.amazonaws.com/oasys-images/thanks-banner.png'/> </td></tr> <tr><td style='padding:0px; text-align: center;'> <div style='width: 70%; display: block; margin-left: auto; margin-right: auto; padding:10px 0px 10px 0px; text-align:left; background-color: white'> <div id='image-container' style='width: 50%; display: inline-block; position: relative;'> <img style='max-height: 200px; max-width: 50%; display: block; margin: auto;' src='http://oasysventures.com/assets/dynamicFiles/products/" + image + "'/> </div> <div id='product-info' style='    width: 50%; float: right; position: relative; margin-top: 6px;'> <span style='font-size: 2em; float: left; width: 100%; margin-bottom: 16px;'>" + name + "</span> <span style='float:left; width: 50%; font-weight: bolder; font-size: 19px;'>Purchased at</span><span style='float: right; text-align: right; margin-right: 120px;font-weight: bolder; font-size: 19px;'>Total</span> <span style='float:left; width: 50%'>" + address + "</span><span style='float: right; text-align: right; margin-right: 120px;'>" + price + "</span> </div> </div> </td></tr> <tr><td style='padding:0px; text-align: center;'> <img style='width: 70%; display: block; margin-left: auto; margin-right: auto; padding-top: 15px; padding-bottom: 5px; background-color: #d9d3e8' src='https://s3.amazonaws.com/oasys-images/beauty-hack-header.png'/> </td></tr> <tr><td style='padding:0px; text-align: center;'> <div style='width: 50%; padding-left: 10%; padding-right: 10%; display: block; margin-left: auto; margin-right: auto; padding-top: 15px; padding-bottom: 25px; font-size: 20px; background-color: #d9d3e8'>" + receipt.beauty_hack + " </div> </td></tr> <tr><td style='text-align: center;padding: 0px 0px 100px 0px;'> <img style='    width: 70%; display: block; margin-left: auto; margin-right: auto;' src='https://s3.amazonaws.com/oasys-images/hashtag-banner.png'/> </td></tr> </table> </body> </html> ";

    mail.sendHtml(htmlString);
    return ok();
  }

  public static Result test() {
    return ok(receiptEmail.render());
  }

}
