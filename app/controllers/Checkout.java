package controllers;

import java.sql.SQLException;
import java.util.ArrayList;

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

    public static Result productSelect(){
    	return ok(checkoutProductSelect.render());
    }
    
    public static Result thankYou(String productId){
    	return ok(thankYou.render());
    }

    public static Result pay(String productId){
    	return ok(pay.render());
    }
    
}
