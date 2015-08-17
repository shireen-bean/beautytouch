package controllers;

import java.util.List;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.text.json.JsonContext;

import models.Brands;
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
    List<Brands> list = Database.getBrandList();
    JsonContext json = Ebean.createJsonContext();
    String s = json.toJsonString(list);
    return ok(s);
  }

}
