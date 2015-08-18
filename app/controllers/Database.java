package controllers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.math.BigDecimal;

import models.ActivityLogModel;
import models.Container;
import models.Machine;
import models.Product;
import models.Receipt;
import models.Brand;
import models.Sale;

import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import play.Logger;
import play.Play;
import play.db.DB;
import play.libs.Json;
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
	
	  Product product = Ebean.find(Product.class, item_sku);
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

  public static void logEvent(String machine_id, String event_type, String product_sku)
    throws SQLException {
    if (connection == null || connection.isClosed()) {
      connection = DB.getConnection();
    }

    if (product_sku == "0") product_sku = null;
    Statement statement = connection.createStatement();
    statement.executeUpdate("INSERT INTO events "
        + "(machine_id, event, product_sku) VALUES ( "
        + machine_id + ", '" + event_type + "', " + product_sku + ")"
        );
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

  public static void addMachineAndContainers(JsonNode jn) {

    Machine machine = new Machine();
    machine.lat = jn.get("lat").asDouble();
    machine.lon = jn.get("lon").asDouble();
    machine.address = jn.get("address").asText();
    Ebean.save(machine);
    
    JsonNode containerArray = jn.get("containers");
    for(int i=0;i<containerArray.size();i++){
      Container container = new Container();
      JsonNode jnContainer = containerArray.get(i);
      container.machine_id = machine.id;
      container.position=jnContainer.get("position").asInt();
      container.num_items=jnContainer.get("num_items").asInt();
      container.total_capacity=jnContainer.get("total_capacity").asInt();
      container.item_sku=jnContainer.get("product").get("item_sku").asInt();
      container.slot = jnContainer.get("slot").asInt();
      Ebean.save(container);
    }
  }

  public static List<Machine> getMachineList() {
	  
	  List<Machine> machines = Ebean.find(Machine.class).findList();
	  for (Machine m : machines) {
	  List<Container> containers = Ebean.find(Container.class).where().eq("machine_id", m.id).findList();
	  m.containers = containers;
	  m.total_capacity = 0;
	  m.num_items = 0;
	    for (Container c : m.containers) {
		    m.total_capacity += c.total_capacity;
		    m.num_items += c.num_items;
		    Product product = Ebean.find(Product.class, c.item_sku);
		    c.product = product;
		    if (c.product.brand_id != null) {
		      Brand brand = Ebean.find(Brand.class, c.product.brand_id);
		      c.product.brand = brand;
		    }
	    }
	  }
	  return machines; 
  }

  public static Machine getMachine(String idMachine) {
	  Machine machine = Ebean.find(Machine.class, idMachine);
	  List<Container> containers = Ebean.find(Container.class).where().eq("machine_id", idMachine).findList();
	  machine.containers = containers;
	  machine.total_capacity = 0;
	  machine.num_items = 0;
	  for (Container c : machine.containers) {
		  machine.total_capacity += c.total_capacity;
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

  public static void editMachineAndContainers(JsonNode jn) {
    Machine machine = Ebean.find(Machine.class, jn.get("id"));
    machine.lat = jn.get("lat").asDouble();
    machine.lon = jn.get("lon").asDouble();
    machine.address = jn.get("address").asText();
    Ebean.save(machine);
    
    JsonNode containerArray = jn.get("containers");
    for(int i = 0; i<containerArray.size(); i++) {
      JsonNode j = containerArray.get(i);
      Container c = Ebean.find(Container.class).where()
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
    Container c = Ebean.find(Container.class).where()
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


      String query = "SELECT sales.id, products.item_sku, machines.address, products.item_name, sales.sales_total, sales_products.product_price "+
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
        pm.item_name = resultSet.getString("item_name");
        pm.price = resultSet.getString("product_price");
        receipt.products.add(pm);
      }

      return receipt;

    } catch (Exception e) {
      Logger.error("********Error" + e.toString());
      return null;
    }
  }

  public static void addCustomer(String phone, String email, int salesId) throws SQLException {
    if (connection == null || connection.isClosed()) {
      connection = DB.getConnection();
    }

    Statement statement = connection.createStatement();
    statement.executeUpdate("INSERT INTO sales_customer "
        + "(sales_id, customer_email, customer_phone) VALUES ( '"
        + salesId + "', '" + email + "', '" + phone +"')");
  }

  public static int recordSale(String machine_id, String productId, BigDecimal product_price) throws SQLException {
	  /*
	  Sale sale = new Sale();
	  sale.machine_id = Long.parseLong(machine_id, 10);
	  sale.sales_total = product_price;
	  Ebean.save(sale);
	  
	  System.out.println(sale.id);
	  */
    if (connection == null || connection.isClosed()){
      connection = DB.getConnection();
    }

    //log sale
    Date date = new Date();
    Statement statement = connection.createStatement();
    statement.executeUpdate("INSERT INTO sales "
        + "(machine_id, sales_total, time) VALUES ( "
        + machine_id + ", " + product_price
        + ", '" + new Timestamp(date.getTime()) + "')",
        Statement.RETURN_GENERATED_KEYS);

    ResultSet results = statement.getGeneratedKeys();
    //log products
    if (results.next()) {
      int salesId = results.getInt(1);
      statement.executeUpdate("INSERT INTO sales_products "
          + "(sales_id, product_sku, product_price) VALUES ( "
          + salesId + ", " + productId + ", " + product_price + ")");
      return salesId;
    }
    return -1;

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

      String startDateMonth = startDate.substring(0,2);
      String startDateDay = startDate.substring(3,5);
      String startDateYear = startDate.substring(6,10);
      startDate = startDateYear+"-"+startDateMonth+"-"+startDateDay;

      String endDateMonth = endDate.substring(0,2);
      String endDateDay = endDate.substring(3,5);
      String endDateYear = endDate.substring(6,10);
      endDate = endDateYear+"-"+endDateMonth+"-"+endDateDay;


      String query = "SELECT jammed, traffic, time "+
        "FROM machine_log " +
        "WHERE machine_id = '"+machine_id+"' AND " +
        "time "+
        "BETWEEN '"+startDate+"' AND '"+endDate+"' ORDER BY time";

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

      String startDateMonth = startDate.substring(0,2);
      String startDateDay = startDate.substring(3,5);
      String startDateYear = startDate.substring(6,10);
      startDate = startDateYear+"-"+startDateMonth+"-"+startDateDay;

      String endDateMonth = endDate.substring(0,2);
      String endDateDay = endDate.substring(3,5);
      String endDateYear = endDate.substring(6,10);
      endDate = endDateYear+"-"+endDateMonth+"-"+endDateDay;


      String query = "SELECT event, product_sku, time "+
        "FROM events " +
        "WHERE machine_id = '"+machine_id+"' AND " +
        "time "+
        "BETWEEN '"+startDate+"' AND '"+endDate+"' ORDER BY time";

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

      String startDateMonth = startDate.substring(0,2);
      String startDateDay = startDate.substring(3,5);
      String startDateYear = startDate.substring(6,10);
      startDate = startDateYear+"-"+startDateMonth+"-"+startDateDay;

      String endDateMonth = endDate.substring(0,2);
      String endDateDay = endDate.substring(3,5);
      String endDateYear = endDate.substring(6,10);
      endDate = endDateYear+"-"+endDateMonth+"-"+endDateDay;


      String query = "SELECT sales.id, sales_products.product_price, sales_products.product_sku, sales.time "+
        "FROM sales, sales_products " +
        "WHERE machine_id = '"+machine_id+"' AND " +
        "sales.id=sales_products.sales_id AND " +
        "sales.time "+
        "BETWEEN '"+startDate+"' AND '"+endDate+"' ORDER BY sales.time";

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
}
