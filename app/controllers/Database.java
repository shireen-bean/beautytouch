package controllers;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.AbstractMap.SimpleEntry;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;
import java.math.RoundingMode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import models.ActivityLogModel;
import models.Hook;
import models.Customer;
import models.Machine;
import models.Product;
import models.Receipt;
import models.Brand;
import models.Event;
import models.Sale;
import models.SaleProduct;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Expr;
import com.avaje.ebean.Query;
import com.avaje.ebean.SqlRow;
import com.fasterxml.jackson.databind.JsonNode;

import play.Logger;
import play.db.DB;
//Create account
//insert into Users (username,password) VALUES ('andres@oasysventures.com',ENCRYPT('O@s!s15!?',concat(_utf8'$1$',right(md5(rand()),8),_utf8'$')));

public class Database {

  static Connection connection;

  public static boolean userLogin(String username, String password) {
    try {

      if(connection==null){
        connection = DB.getConnection();
      }
      if(connection.isClosed()){
        connection = DB.getConnection();
      }

      Statement statement = connection.createStatement();
      ResultSet resultSet;
      resultSet = statement
        .executeQuery("SELECT username FROM Users WHERE username='"
            + username + "' AND password = ENCRYPT('"
            + password + "', password)");

      if (resultSet.next()) {
        return true;
      }
      return false;
    } catch (SQLException e) {
      return false;
    }
  }

  public static void editProduct(String item_sku, String item_name,
      String subtitle,
      String category, String brand_id,
      String item_img, String detail_img, String thumbnail,
      String price, String item_description,
      String package_type) throws SQLException {

	  System.out.println(detail_img);
    Product product = Ebean.find(Product.class, item_sku);
    product.item_name = item_name;
    product.subtitle = subtitle == "null" ? null : subtitle;
    product.category = category;
    product.brand_id = Integer.valueOf(brand_id);
    product.item_img = item_img;
    product.detail_img = detail_img == "null" ? null : detail_img;
    product.thumbnail = thumbnail == "null" ? null : thumbnail;
    product.price = price;
    product.item_description = item_description;
    product.package_type = package_type;

    Ebean.save(product);

  }

  public static void addProduct(String item_name, String subtitle, String category, String brand_id, String item_img,
      String detail_img, String thumbnail, String price, String item_description, String package_type)
    throws SQLException {

    Product product = new Product();
    product.item_name = item_name;
    product.subtitle = subtitle;
    product.category = category;
    product.brand_id = Integer.valueOf(brand_id);
    product.item_img = item_img;
    product.detail_img = detail_img;
    product.thumbnail = thumbnail;
    product.price = price;
    product.item_description = item_description;
    product.package_type = package_type;

    Ebean.save(product);
  }

  public static void logMachineStatus(int machine_id, int jammed, int traffic)
    throws SQLException {
    if (connection == null || connection.isClosed()) {
      connection = DB.getConnection();
    }

    Date date = new Date();
    Statement statement = connection.createStatement();
    statement.executeUpdate("INSERT INTO machine_log "
        + "(machine_id, jammed, traffic, time) VALUES ( "
        + machine_id + ", " + jammed + ", " + traffic
        + ", '" + new Timestamp(date.getTime()) +"')");
  }

  public static void logEvent(String machine_id, String event_type, String product_sku) {

    Integer product_id = Integer.parseInt(product_sku);
    if (product_id == 0) {
      product_id = null;
    }

    Event event = new Event();
    event.machine_id = Long.parseLong(machine_id, 10);
    event.event = event_type;
    event.product_sku = product_id;
    Ebean.save(event);
  }

  public static List<Product> getProductList() {
    List<Product> products = Ebean.find(Product.class).findList();
    return products;
  }

  public static List<Brand> getBrandList() {
    List<Brand> brands = Ebean.find(Brand.class).findList();
    return brands;
  }

  public static Product getProduct(String sku) {

    Product product = Ebean.find(Product.class, sku);
    if (product.brand_id != null) {
      Brand brand = Ebean.find(Brand.class, product.brand_id);
      product.brand = brand;
    }
    return product;
  }

  public static Brand getBrand(String id) {
    Brand brand = Ebean.find(Brand.class, id);
    return brand;
  }

  public static void editBrand(String id, String name,
      String logo, String description){

    Brand brand = Ebean.find(Brand.class, id);
    brand.name = name;
    brand.logo = logo;
    brand.description = description;

    Ebean.save(brand);
  }

  public static void addBrand(String name, String logo, String description) {

    Brand brand = new Brand();
    brand.name = name;
    brand.logo = logo;
    brand.description = description;

    Ebean.save(brand);
  }

  public static String getProductPrice(String sku) {

    Product product = Ebean.find(Product.class, sku);
    return product.price;
  }

  public static void addMachineAndHooks(JsonNode jn) {

    Machine machine = new Machine();
    machine.lat = jn.get("lat").asDouble();
    machine.lon = jn.get("lon").asDouble();
    machine.address = jn.get("address").asText();
    Ebean.save(machine);

    JsonNode HookArray = jn.get("Hooks");
    for(int i=0;i<HookArray.size();i++){
      Hook hook = new Hook();
      JsonNode jnHook = HookArray.get(i);
      hook.machine_id = machine.id;
      if (jnHook.get("product") != null) {
        hook.item_sku=jnHook.get("product").get("item_sku").asInt();
        hook.status = "full";
      } else {
    	  hook.status = "empty";
      }
      Ebean.save(hook);
    }
  }

  public static List<Machine> getMachineList() {

    List<Machine> machines = Ebean.find(Machine.class).findList();
    for (Machine m : machines) {
      List<Hook> Hooks = Ebean.find(Hook.class).where().eq("machine_id", m.id).findList();
      m.hooks = Hooks;
      m.total_capacity = 0;
      m.num_items = 0;
      for (Hook c : m.hooks) {
        m.total_capacity++;
        if (c.item_sku != null) {
          m.num_items++;
          Product product = Ebean.find(Product.class, c.item_sku);
          c.product = product;
          if (c.product.brand_id != null) {
            Brand brand = Ebean.find(Brand.class, c.product.brand_id);
            c.product.brand = brand;
          }
        }
      }
    }
    return machines;
  }

  public static Machine getMachine(String idMachine) {
    Machine machine = Ebean.find(Machine.class, idMachine);
    List<Hook> Hooks = Ebean.find(Hook.class).where().eq("machine_id", idMachine).findList();
    machine.hooks = Hooks;
    machine.total_capacity = 0;
    machine.num_items = 0;
    for (Hook c : machine.hooks) {
      machine.total_capacity++;
      machine.num_items += c.num_items;
      Product product = Ebean.find(Product.class, c.item_sku);
      c.product = product;
      if (c.product.brand_id != null) {
        Brand brand = Ebean.find(Brand.class, c.product.brand_id);
        c.product.brand = brand;
      }
    }
    return machine;
  }

  public static void editMachineAndHooks(JsonNode jn) {
    Machine machine = Ebean.find(Machine.class, jn.get("id"));
    machine.lat = jn.get("lat").asDouble();
    machine.lon = jn.get("lon").asDouble();
    machine.address = jn.get("address").asText();
    Ebean.save(machine);

    JsonNode HookArray = jn.get("Hooks");
    for(int i = 0; i<HookArray.size(); i++) {
      JsonNode j = HookArray.get(i);
      Hook c = Ebean.find(Hook.class).where()
        .eq("machine_id", machine.id)
        .eq("position", j.get("position").asInt())
        .findUnique();
      c.num_items = j.get("num_items").asInt();
      c.total_capacity = j.get("total_capacity").asInt();
      c.item_sku = j.get("product").get("item_sku").asInt();
      c.slot = j.get("slot").asInt();
      Ebean.save(c);
    }
  }

  public static void removeItem(String machine_id, String column) {
    Hook c = Ebean.find(Hook.class).where()
      .eq("machine_id",  machine_id)
      .eq("position", column)
      .findUnique();
    c.num_items = c.num_items - 1;
    Ebean.save(c);
  }

  public static Receipt getReceiptDetails(int salesId) {
    //System.out.println(idMachine);
    try {
      if(connection==null){
        connection = DB.getConnection();
      }
      if(connection.isClosed()){
        connection = DB.getConnection();
      }

      Statement statement = connection.createStatement();


      String query = "SELECT sales.id, products.item_sku, products.item_img, machines.address, products.item_name, sales.sales_total, sales_products.product_price "+
        "FROM machines, products, sales, sales_products " +
        "WHERE " +
        "sales.id='"+salesId+"' "+
        "AND sales.machine_id = machines.id "+
        "AND sales_products.sales_id = sales.id "+
        "AND products.item_sku = sales_products.product_sku ";

      ResultSet resultSet = statement.executeQuery(query);
      Receipt receipt = new Receipt();
      receipt.products = new ArrayList<Product>();


      while (resultSet.next()) {
        receipt.machineAddress = resultSet.getString("address");
        receipt.total = resultSet.getString("sales_total");

        Product pm = new Product();
        pm.item_sku = resultSet.getInt("item_sku");
        pm.item_name = resultSet.getString("item_name");
        pm.price = resultSet.getString("product_price");
        pm.item_img = resultSet.getString("item_img");
        receipt.products.add(pm);
      }
      receipt.beauty_hack = getBeautyHackForProduct(receipt.products.get(0).item_sku);

      return receipt;

    } catch (Exception e) {
      Logger.error("********Error" + e.toString());
      return null;
    }
  }

  public static String getBeautyHackForProduct(int product_id) {
	  try {
	      if(connection==null){
	        connection = DB.getConnection();
	      }
	      if(connection.isClosed()){
	        connection = DB.getConnection();
	      }

	      Statement statement = connection.createStatement();


	      String query = "SELECT * from content where product_sku = "
	    		  + product_id + " and content_type = 'beauty_hack'";

	      System.out.println(query);
	      ResultSet resultSet = statement.executeQuery(query);
	      while (resultSet.next()) {
	    	  return resultSet.getString("body");
	      }
	      return "";
	  } catch (Exception e){
		  Logger.error("********Error" + e.toString());
	      return null;
	  }
  }
  
  public static void addCustomer(String phone, String email, int salesId) {
    Customer customer = new Customer();
    customer.sales_id = salesId;
    customer.customer_email = email;
    customer.customer_phone = phone;
    Ebean.save(customer);
  }

  public static long recordSale(String machine_id, String productId, BigDecimal product_price) {
    //create the sale
    Sale sale = new Sale();
    sale.machine_id = Long.parseLong(machine_id, 10);
    sale.sales_total = product_price;
    Ebean.save(sale);

    //save products
    SaleProduct product = new SaleProduct();
    product.sales_id = sale.id;
    product.product_sku = Integer.parseInt(productId);
    product.product_price = product_price;
    Ebean.save(product);

    return sale.id;
  }

  public static ArrayList<ActivityLogModel> getStatusUpdates(String machine_id, String startDate,
      String endDate) {


    ArrayList<ActivityLogModel> almList = new ArrayList<ActivityLogModel>();

    try {
      if(connection==null){
        connection = DB.getConnection();
      }
      if(connection.isClosed()){
        connection = DB.getConnection();
      }

      Statement statement = connection.createStatement();


      String query = "SELECT jammed, traffic, time "+
        "FROM machine_log " +
        "WHERE machine_id = '"+machine_id+"' AND " +
        "time "+
        "BETWEEN '"+startDate+"' AND '"+endDate+"' ORDER BY time desc limit 100";
      
      System.out.println(query);

      ResultSet resultSet = statement.executeQuery(query);


      while (resultSet.next()) {
        ActivityLogModel alm = new ActivityLogModel();
        alm.entryType = "status";
        alm.date = resultSet.getTimestamp("time");
        alm.jammed = resultSet.getBoolean("jammed");
        alm.traffic = resultSet.getInt("traffic");
        almList.add(alm);
      }

      return almList;

    } catch (Exception e) {
      System.out.println(e.toString());
      return almList;
    }
  }

  public static ArrayList<ActivityLogModel> getUIEvents(String machine_id, String startDate,
      String endDate) {

    ArrayList<ActivityLogModel> almList = new ArrayList<ActivityLogModel>();

    try {
      if(connection==null){
        connection = DB.getConnection();
      }
      if(connection.isClosed()){
        connection = DB.getConnection();
      }

      Statement statement = connection.createStatement();

      String query = "SELECT event, product_sku, time "+
        "FROM events " +
        "WHERE machine_id = '"+machine_id+"' AND " +
        "time "+
        "BETWEEN '"+startDate+"' AND '"+endDate+"' ORDER BY time desc";

      ResultSet resultSet = statement.executeQuery(query);


      while (resultSet.next()) {
        ActivityLogModel alm = new ActivityLogModel();
        alm.entryType = "action";
        alm.date = resultSet.getTimestamp("time");
        alm.event = resultSet.getString("event");
        alm.productSku = resultSet.getInt("product_sku");
        almList.add(alm);
      }

      return almList;

    } catch (Exception e) {
      System.out.println(e.toString());
      return almList;
    }
  }

  public static ArrayList<ActivityLogModel> getSales(String machine_id, String startDate,
      String endDate) {

    ArrayList<ActivityLogModel> almList = new ArrayList<ActivityLogModel>();

    try {
      if(connection==null){
        connection = DB.getConnection();
      }
      if(connection.isClosed()){
        connection = DB.getConnection();
      }

      Statement statement = connection.createStatement();


      String query = "SELECT sales.id, sales_products.product_price, sales_products.product_sku, sales.time "+
        "FROM sales, sales_products " +
        "WHERE machine_id = '"+machine_id+"' AND " +
        "sales.id=sales_products.sales_id AND " +
        "sales.time "+
        "BETWEEN '"+startDate+"' AND '"+endDate+"' ORDER BY sales.time desc";

      ResultSet resultSet = statement.executeQuery(query);


      while (resultSet.next()) {
        ActivityLogModel alm = new ActivityLogModel();
        alm.entryType = "sale";
        alm.date = resultSet.getTimestamp("time");
        alm.salesId = resultSet.getInt("id");
        alm.productSku = resultSet.getInt("product_sku");
        alm.salesPrice = resultSet.getString("product_price");
        almList.add(alm);
      }

      return almList;

    } catch (Exception e) {
      System.out.println(e.toString());
      return almList;
    }
  }
  
  public static List<Sale> getSalesByProduct(int product_id) {
    List<SaleProduct> sales_products = Ebean.find(SaleProduct.class).where()
    		.eq("product_sku", product_id)
    		.findList();
    List<Long> sales_ids = new ArrayList<Long>();
    for (SaleProduct s : sales_products) {
    	sales_ids.add(s.sales_id);
    }
    Query<Sale> query = Ebean.createQuery(Sale.class);
    query.where(
    		Expr.in("id", sales_ids)
    		).order().desc("time");
    List<Sale> sales = query.findList();
    return sales;
  }
  
  public static List<Sale> getSalesByMachine(int machine_id) {
	  List<Sale> sales = Ebean.find(Sale.class).where()
			  .eq("machine_id", machine_id)
			  .findList();
	  return sales;
  }
  
  public static int getNumSalesByProduct(int product_id) {
	  int num_sales = Ebean.find(SaleProduct.class).where()
			  .eq("product_sku",  product_id)
			  .findRowCount();
	  return num_sales;
  }
  
  public static int getNumTapsByProduct(int product_id) {
	  int num_taps = Ebean.find(Event.class).where()
			  .eq("product_sku",  product_id)
			  .findRowCount();
	  return num_taps;
  }
  
  public static List<Event> getTapsByProduct(int product_id) {
	  List<Event> events = Ebean.find(Event.class).where()
			  .eq("product_sku",  product_id)
			  .order().desc("time")
			  .findList();
	  return events;
  }
  
  public static String getTapsByMachine(int machine_id) {
	  String sql = "select count(*) as taps, product_sku from events" 
			  + " where event = 'tap_product' and"
			  + " machine_id = '" + machine_id 
			  + "' group by product_sku";
	  List<SqlRow> sqlRows = Ebean.createSqlQuery(sql).findList();
	  JSONArray jo = new JSONArray();
	  for (SqlRow sqlRow : sqlRows) {
		  JSONObject jn = new JSONObject();
		  jn.put("product_sku", sqlRow.getInteger("product_sku"));
		  jn.put("taps", sqlRow.getInteger("taps"));
		  jo.put(jn);
	  }
	  System.out.println(jo);
	  return jo.toString();
  }

  public static String getNumTapsByMachine(int machine_id) {
	  JSONObject jo = new JSONObject();

	  int num_taps = Ebean.find(Event.class).where()
			  .eq("machine_id",  machine_id)
			  .eq("event",  "tap_product")
			  .findRowCount();
	  int num_about = Ebean.find(Event.class).where()
			  .eq("machine_id",  machine_id)
			  .eq("event",  "tap_about")
			  .findRowCount();
	  int num_report = Ebean.find(Event.class).where()
			  .eq("machine_id",  machine_id)
			  .eq("event", "tap_report")
			  .findRowCount();
	  
	  try {
		jo.put("product", num_taps);
		jo.put("about",  num_about);
		jo.put("report",  num_report);
	} catch (JSONException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	  
	  return jo.toString();
  }
  
  public static int getNumSalesByMachine(int machine_id ) {
	  int num_sales = Ebean.find(Sale.class).where() 
			  .eq("machine_id", machine_id)
			  .order().desc("time")
			  .findRowCount();
	  return num_sales;
  }
  
  public static String getAvgSaleByMachine(int machine_id) {
	  List<Sale> sales = Ebean.find(Sale.class).where()
			  .eq("machine_id",  machine_id).findList();
	  double sum = 0;
	  int num_sales = getNumSalesByMachine(machine_id);
	  if (num_sales == 0) {
		  return "0";
	  }
	  for (Sale sale : sales ) {
		  sum += sale.sales_total.longValue();
	  }
	  DecimalFormat df = new DecimalFormat("#.00");
	  double avg = sum/num_sales;
	  return df.format(avg);
  }
  
}
