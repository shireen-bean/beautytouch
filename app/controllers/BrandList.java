package controllers;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import models.User;
import play.data.Form;
import play.libs.Json;
import play.mvc.*;
import views.html.*;

public class BrandList extends Controller {

  public static boolean loggedIn() {
    if (session("user") == null) {
       return false;
    }
    return true;
  }

  public static Result brandList() {
    if (!loggedIn()) {
      return redirect("/");
    }
    return ok(brandList.render());
  }

  public static Result brandListJson() {

    if (!loggedIn()) {
      return redirect("/");
    }
    ArrayNode list = Database.getBrandList();

    return ok(list);
  }

}
