package controllers;

import java.io.File;
import java.text.NumberFormat;
import java.math.BigDecimal;

import models.Product;
import models.Receipt;

import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.databind.JsonNode;
import com.typesafe.plugin.*;

import play.mvc.*;
import play.mvc.Http.RequestBody;

//{
//	"sales_id": "2",
//	"email": "amco1027@gmail.com"
//  "key":"6b6f7834-1392-11e5-b60b-1697f925ec7b"
//}

public class Email extends Controller {

  public static Result alertSale(String machineId, String productId, BigDecimal productPrice) {
    System.out.println("alertSale");
    Product product = Ebean.find(Product.class, productId);
    MailerAPI mail = play.Play.application().plugin(MailerPlugin.class).email();
    mail.setSubject("Oasys Sale");
    mail.setRecipient("jackie@oasysventures.com");
    mail.setCc("marisa@oasysventures.com");
    mail.setCc("mackenzie@oasysventures.com");
    mail.setCc("alina@oasysventures.com");
    mail.setFrom("Oasys <service@oasysventures.com>");
    System.out.println(mail);

    mail.sendHtml("<p>Oasys purchase at machine " + machineId + ". Product '" + product.item_name + "' sold for $" + productPrice.toString() + ".</p>");
    return ok();
  }
  
  public static Result sendSuggestion() {
	  JsonNode jn = request().body().asJson();
	  String machineId = jn.get("machine_id").asText();
	  String suggestion = jn.get("suggestion").asText();
	  MailerAPI mail = play.Play.application().plugin(MailerPlugin.class).email();
	  mail.setSubject("Product Recommendation");
	  mail.setRecipient("alina@oasysventures.com");
	  mail.setFrom("Oasys <service@oasysventures.com>");
	  
	  mail.sendHtml("<p>Oasys product recommendation from machine " + machineId + ". Product/Idea: " + suggestion + "</p>");
      return ok();
  }
  
  public static Result sendReceipt(){
    //get sales id and customer info
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

    String productRows = "";
    for(int i=0;i<receipt.products.size();i++){
      Product pm = receipt.products.get(i);
      productRows+=
        "<tr>"+
        "<td style='padding-right:250px;'>"+pm.item_name+"</td><td></td>"+
        " <td>"+formatter.format(Double.parseDouble(pm.price))+"</td>"+
        "</tr>";
    }

    String imageSource="https://oasysventures.com/assets/images/logoInc.png";
    String htmlString=""+
      "<!DOCTYPE html>"+
      "<html>"+
      "<head>"+
      "<style>"+
      "body{line-height:24px;font-family:Arial, Helvetica, sans-serif;}"+
      "table{text-align:left;}"+
      "th{border-top: 1px solid #ccc;padding: 10px 40px 10px 0px;margin:0px;}"+
      "td{border-top: 1px solid #ddd;padding:10px 50px 10px 0px}"+
      "</style>"+
      "</head>"+
      "<body>"+
      "<style>"+
      "body{line-height:24px;font-family:Arial, Helvetica, sans-serif;}"+
      "table{text-align:left;}"+
      "th{border-top: 1px solid #ccc;padding: 10px 40px 10px 0px;margin:0px;}"+
      "td{border-top: 1px solid #ddd;padding:20px 50px 40px 0px}"+
      "</style>"+
      "<img height='30px' src='"+imageSource+"'></html>"+
      "<br><br>"+
      "<br>Thank you for shopping with Oasys."+
      "<br><br>"+receipt.machineAddress+
      "<br><br>Sales #: "+salesId+
      "<br><br>"+
      "<table>"+
      "<tr><th>Product</th><th></th><th>Total</th></tr>"+
      productRows+
      " <td></td><td><b>Total:</b></td><td>"+formatter.format(Double.parseDouble(receipt.total))+"</td>"+
      "</tr>"+
      "</table>"+
      "<br><br>"+
      "</body>"+
      "</html>";




    mail.sendHtml(htmlString);
    return ok();
  }


}
