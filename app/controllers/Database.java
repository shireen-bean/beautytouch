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
import models.Containers;
import models.Machines;
import models.Products;
import models.Receipt;
import models.Brands;

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

    if(connection==null){
      connection = DB.getConnection();
    }
    if(connection.isClosed()){
      connection = DB.getConnection();
    }
    Statement statement = connection.createStatement();
    statement.executeUpdate("UPDATE products SET "
        +"item_name='"+item_name+"',"
        +"subtitle='"+subtitle+"',"
        +"category='"+category+"',"
        +"brand_id='" + brand_id + "',"
        +"item_img='"+item_img+"',"
        +"detail_img='"+detail_img+"',"
        +"thumbnail='"+thumbnail+"',"
        +"price='"+price+"',"
        +"item_description='"+item_description+"',"
        +"package_type='"+package_type+"'"
        +" WHERE item_sku='"+item_sku+"'");
  }

  public static void addProduct(String item_name, String subtitle, String category, String brand_id, String item_img,
		  String detail_img, String thumbnail, String price, String item_description, String package_type)
    throws SQLException {

    if(connection==null || connection.isClosed()){
      connection = DB.getConnection();
    }

    Statement statement = connection.createStatement();
    statement
      .executeUpdate("INSERT INTO products "
          + "(item_name,subtitle,category,item_img,detail_img,thumbnail,price,item_description,package_type, brand_id) VALUES ("
          + "'" + item_name + "','"
          + subtitle + "','"
          + category + "','"
          + item_img + "','"
          + detail_img + "','"
          + thumbnail + "','"
          + price + "','"
          + item_description + "','"
          + package_type + "','"
          + brand_id + "')");
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

  public static List<Products> getProductList() {
	  List<Products> products = Ebean.find(Products.class).findList();
	  return products;
  }

  public static List<Brands> getBrandList() {
	  List<Brands> brands = Ebean.find(Brands.class).findList();
	  return brands;
  }

  public static Products getProduct(String sku) {
	  
	  Products product = Ebean.find(Products.class, sku);
	  return product;
/*
    try {
      if(connection==null){
        connection = DB.getConnection();
      }
      if(connection.isClosed()){
        connection = DB.getConnection();
      }
      Statement statement = connection.createStatement();
      ResultSet resultSet = statement.executeQuery(""
          + "SELECT item_name, subtitle, category, item_sku,"
          + "item_img, detail_img, thumbnail, "
          + "item_description, package_type, price, brand_id "
          + " FROM products "
          + "WHERE item_sku="+sku);

      if (resultSet.next()) {
        ObjectNode result = Json.newObject();
        result.put("item_sku",resultSet.getString("item_sku"));
        result.put("item_name",resultSet.getString("item_name"));
        result.put("subtitle", resultSet.getString("subtitle"));
        result.put("category", resultSet.getString("category"));
        result.put("item_img",resultSet.getString("item_img"));
        result.put("detail_img",  resultSet.getString("detail_img"));
        result.put("thumbnail", resultSet.getString("thumbnail"));
        result.put("item_description", resultSet.getString("item_description"));
        result.put("package_type", resultSet.getString("package_type"));
        result.put("price", resultSet.getString("price"));
        result.put("brandId", resultSet.getString("brand_id"));
        System.out.println(result);
        return(result);
      }

      return null;

    } catch (Exception e) {
      Logger.error("getAll users and emails error:" + e.getMessage());
      return null;
    }
    */
  }

  public static ObjectNode getBrand(String id) {
    try {
      if (connection == null || connection.isClosed()){
        connection = DB.getConnection();
      }

      Statement statement = connection.createStatement();
      ResultSet resultSet = statement.executeQuery("SELECT * FROM brands WHERE id = " + id);

      if (resultSet.next()) {
        ObjectNode result = Json.newObject();
        result.put("id",  resultSet.getString("id"));
        result.put("name", resultSet.getString("name"));
        result.put("logo",  resultSet.getString("logo"));
        result.put("description", resultSet.getString("description"));
        return(result);
      }

      return null;
    } catch (Exception e) {
      Logger.error("getBrand error: " + e.getMessage());
      return null;
    }
  }

  public static void editBrand(String id, String name,
      String logo, String description) throws SQLException {

    if(connection==null){
      connection = DB.getConnection();
    }
    if(connection.isClosed()){
      connection = DB.getConnection();
    }

    Statement statement = connection.createStatement();
    statement.executeUpdate("UPDATE brands SET "
        + "name = '" + name + "', "
        + "logo = '" + logo + "', "
        + "description = '" + description + "' "
        + "WHERE id=" + id);
  }

  public static void addBrand(String name, String logo, String description)
    throws SQLException {

    if(connection==null || connection.isClosed()){
      connection = DB.getConnection();
    }

    Statement statement = connection.createStatement();
    statement.executeUpdate("INSERT INTO brands "
        + "(name, logo, description) VALUES ("
        + "'" + name + "','"+ logo + "','"
        + description + "')");
  }

  public static String getProductPrice(String sku) {
    try {
      if(connection==null){
        connection = DB.getConnection();
      }
      if(connection.isClosed()){
        connection = DB.getConnection();
      }
      Statement statement = connection.createStatement();
      ResultSet resultSet = statement.executeQuery("SELECT item_name,subtitle,item_sku,item_img,detail_img,thumbnail,item_description,package_type,price FROM products WHERE item_sku="+sku);

      if (resultSet.next()) {
        return resultSet.getString("price");
      }

      return null;

    } catch (Exception e) {
      Logger.error("getAll users and emails error:" + e.getMessage());
      return null;
    }
  }

  public static void addMachineAndContainers(JsonNode jn) throws SQLException{
    if(connection==null){
      connection = DB.getConnection();
    }
    if(connection.isClosed()){
      connection = DB.getConnection();
    }


    connection.setAutoCommit(false);
    String lat = jn.get("lat").asText();
    String lon = jn.get("lon").asText();
    //shorten lat and lon to fit
    System.out.println(lat);
    System.out.println(lat.substring(lat.indexOf("."),lat.length()-1).length());

    String address = jn.get("address").asText();
    JsonNode containerArray = jn.get("containers");


    PreparedStatement insertMachine = connection.prepareStatement("INSERT INTO machines "
        + "(lat,lon,address) VALUES ("
        + "'" + lat + "'," + "'" + lon + "'," + "'"
        + address + "')");

    insertMachine.execute();

    String query = "SELECT id FROM machines WHERE id=LAST_INSERT_ID()";
    PreparedStatement getmachine_id = connection.prepareStatement(query);
    ResultSet rs = getmachine_id.executeQuery();

    int machine_id=0;
    // extract data from the ResultSet
    if (rs.next()) {
      machine_id = rs.getInt(1);
    }

    for(int i=0;i<containerArray.size();i++){
      JsonNode jnContainer = containerArray.get(i);
      String position=jnContainer.get("position").asText();
      String num_items=jnContainer.get("num_items").asText();
      String total_capacity=jnContainer.get("total_capacity").asText();
      String item_sku=jnContainer.get("product").get("item_sku").asText();
      String slot=jnContainer.get("slot").asText();

      PreparedStatement insertContainer = connection.prepareStatement("INSERT INTO containers "
          + "(machine_id,position,slot,num_items,total_capacity,item_sku) VALUES ("
          + "'" + machine_id + "',"
          + "'" + position + "',"
          + "'"+slot+"',"
          + "'" + num_items + "',"
          + "'" + total_capacity + "',"
          + "'" + item_sku + "')");
      insertContainer.execute();
    }

    
    connection.commit();
    connection.setAutoCommit(true);
  }

  public static List<Machines> getMachineList() {
	  
	  List<Machines> machines = Ebean.find(Machines.class).findList();
	  return machines;
	  /*
    try {
      if(connection==null){
        connection = DB.getConnection();
      }
      if(connection.isClosed()){
        connection = DB.getConnection();
      }

      Statement statement = connection.createStatement();

      String query = "SELECT machines.id, machines.address, machines.lat, machines.lon, "+
        "containers.id AS containerId, containers.machine_id, containers.position, containers.num_items, containers.total_capacity, containers.item_sku, "+
        "products.item_name, products.subtitle, products.category, products.item_img, products.detail_img, products.thumbnail, products.price, products.item_description, products.package_type "+
        "FROM machines, containers, products " +
        "WHERE " +
        "machines.id = containers.machine_id "+
        "AND containers.item_sku = products.item_sku"+
        "";

      ResultSet resultSet = statement.executeQuery(query);
      ArrayList<Machines> machines = new ArrayList<Machines>();

      while (resultSet.next()) {
        //check if machine already exists
        boolean machineCreated=false;
        Long idTemp = resultSet.getLong("id");
        int machineIndex=0;
        for(int i=0;i<machines.size();i++){
          if(machines.get(i).id==idTemp){
            machineCreated=true;
            machineIndex=i;
            break;
          }
        }


        //if machine exists add container to machine
        if(machineCreated){
          Products product = new Products();
          product.item_name=resultSet.getString("item_name");
          product.subtitle = resultSet.getString("subtitle");
          product.category=resultSet.getString("category");
          product.item_sku=resultSet.getInt("item_sku");
          product.item_img=resultSet.getString("item_img");
          product.detail_img = resultSet.getString("detail_img");
          product.thumbnail = resultSet.getString("thumbnail");
          product.price=resultSet.getString("price");
          product.item_description=resultSet.getString("item_description");
          product.package_type=resultSet.getString("package_type");

          Containers container = new Containers();
          container.id=resultSet.getInt("containerId");
          container.position=resultSet.getInt("position");
          container.num_items=resultSet.getInt("num_items");
          container.total_capacity=resultSet.getInt("total_capacity");
          container.product = product;
          container.machine_id=resultSet.getInt("machine_id");

          machines.get(machineIndex).total_capacity+=container.total_capacity;
          machines.get(machineIndex).num_items+=container.num_items;
          machines.get(machineIndex).containers.add(container);
        }

        //else create new machine and add container
        else{
          Products product = new Products();
          product.item_name=resultSet.getString("item_name");
          product.subtitle=resultSet.getString("subtitle");
          product.category= resultSet.getString("category");
          product.item_sku=resultSet.getInt("item_sku");
          product.item_img=resultSet.getString("item_img");
          product.detail_img = resultSet.getString("detail_img");
          product.thumbnail = resultSet.getString("thumbnail");
          product.price=resultSet.getString("price");
          product.item_description=resultSet.getString("item_description");
          product.package_type=resultSet.getString("package_type");

          Containers container = new Containers();
          container.id=resultSet.getInt("containerId");
          container.position=resultSet.getInt("position");
          container.num_items=resultSet.getInt("num_items");
          container.total_capacity=resultSet.getInt("total_capacity");
          container.product = product;
          container.machine_id=resultSet.getInt("machine_id");

          Machines machine = new Machines();
          machine.id = resultSet.getLong("id");
          machine.address = resultSet.getString("address");
          machine.lat=resultSet.getDouble("lat");
          machine.lon=resultSet.getDouble("lon");
          machine.containers = new ArrayList<Containers>();
          machine.containers.add(container);
          machine.total_capacity=container.total_capacity;
          machine.num_items=container.num_items;

          machines.add(machine);
        }
      }
      return machines;

    } catch (Exception e) {
      Logger.error("********Error" + e.toString());
      return null;
    }
    */
  }


  public static Machines getMachine(String idMachine) {
	  Machines machine = Ebean.find(Machines.class, idMachine);
	  List<Containers> containers = Ebean.find(Containers.class).where().eq("machine_id", idMachine).findList();
	  machine.containers = containers;
	  for (Containers c : machine.containers) {
		  Products product = Ebean.find(Products.class, c.item_sku);
		  c.product = product;
	  }
	  return machine; 
	  /*
    //System.out.println(idMachine);
    try {
      if(connection==null){
        connection = DB.getConnection();
      }
      if(connection.isClosed()){
        connection = DB.getConnection();
      }

      Statement statement = connection.createStatement();


      String query = "SELECT machines.id, machines.address, machines.lat, machines.lon, "+
        "containers.id AS containerId, containers.machine_id, containers.position, containers.num_items, containers.total_capacity, containers.item_sku, containers.slot, "+
        "products.item_name, products.subtitle, products.category, products.item_img, products.detail_img, products.thumbnail, products.price, products.item_description, products.package_type, products.brand_id,"+
        "brands.name as brandName, brands.logo as brandLogo, brands.description as brandDescription " +
        "FROM machines, containers, products " +
        "LEFT JOIN brands on products.brand_id = brands.id " +
        "WHERE " +
        "machines.id='"+idMachine+"' "+
        "AND machines.id = containers.machine_id "+
        "AND containers.item_sku = products.item_sku";



      ResultSet resultSet = statement.executeQuery(query);
      Machines machine = new Machines();
      machine.containers = new ArrayList<Containers>();
      machine.total_capacity=0;
      while (resultSet.next()) {

        Brands brand = new Brands();
        brand.name = resultSet.getString("brandName");
        brand.logo = resultSet.getString("brandLogo");
        brand.description = resultSet.getString("brandDescription");
        brand.id = resultSet.getInt("products.brand_id");

        Products product = new Products();
        product.item_name=resultSet.getString("item_name");
        product.subtitle = resultSet.getString("subtitle");
        product.category=resultSet.getString("category");
        product.item_sku=resultSet.getInt("item_sku");
        product.item_img=resultSet.getString("item_img");
        product.detail_img = resultSet.getString("detail_img");
        product.thumbnail = resultSet.getString("thumbnail");
        product.price=resultSet.getString("price");
        product.item_description=resultSet.getString("item_description");
        product.package_type=resultSet.getString("package_type");
        product.brand = brand;

        Containers container = new Containers();
        container.id=resultSet.getInt("containerId");
        container.position=resultSet.getInt("position");
        container.num_items=resultSet.getInt("num_items");
        container.total_capacity=resultSet.getInt("total_capacity");
        container.product = product;
        container.machine_id=resultSet.getInt("machine_id");
        container.slot=resultSet.getInt("slot");

        machine.id = resultSet.getLong("id");
        machine.address = resultSet.getString("address");
        machine.lat=resultSet.getDouble("lat");
        machine.lon=resultSet.getDouble("lon");
        machine.containers.add(container);
        machine.total_capacity+=container.total_capacity;
        machine.num_items=container.num_items;
      }

      return machine;

    } catch (Exception e) {
      Logger.error("********Error" + e.toString());
      return null;
    }
    */
  }

  public static void editMachineAndContainers(JsonNode jn) throws SQLException {
    if(connection==null){
      connection = DB.getConnection();
    }
    if(connection.isClosed()){
      connection = DB.getConnection();
    }

    String lat = jn.get("lat").asText();
    String lon = jn.get("lon").asText();

    String address = jn.get("address").asText();
    JsonNode containerArray = jn.get("containers");

    Statement statement = connection.createStatement();
    statement.addBatch("UPDATE machines "
        + "SET lat='" + lat + "',lon='" + lon + "',address='"+address+"'"
        + " WHERE id='"+jn.get("id")+"'");


    for(int i=0;i<containerArray.size();i++){
      JsonNode jnContainer = containerArray.get(i);
      String position=jnContainer.get("position").asText();
      String num_items=jnContainer.get("num_items").asText();
      String total_capacity=jnContainer.get("total_capacity").asText();
      String item_sku=jnContainer.get("product").get("item_sku").asText();
      String slot=jnContainer.get("slot").asText();

      statement.addBatch("UPDATE containers "
          + "SET machine_id='"+jn.get("id")+"',"
          +"position='"+position+"',"
          +"num_items='"+num_items+"',"
          +"total_capacity='"+total_capacity+"',"
          +"slot='"+slot+"',"
          +"item_sku='"+item_sku+"'"
          + " WHERE machine_id='"+jn.get("id")+"' "
          + " AND position='"+position+"'");
    }

    statement.executeBatch();
    statement.close();

  }

  public static ArrayList<Products> getAvailableProducts(String machine_id) {

    //	    String query = "SELECT machines.id, machines.address, machines.lat, machines.lon, "+
    //	    		"containers.id AS containerId, containers.machine_id, containers.position, containers.num_items, containers.total_capacity, containers.item_sku, "+
    //	    		"products.item_name, products.category, products.item_img, products.price, products.item_description, products.package_type "+
    //	             "FROM machines, containers, products " +
    //	             "WHERE " +
    //	             "machines.id='"+idMachine+"' "+
    //	             "AND machines.id = containers.machine_id "+
    //	             "AND containers.item_sku = products.item_sku";
    //
    //

    // TODO Auto-generated method stub
    return null;
  }

  public static void removeItem(String machine_id, String column) throws SQLException {
    //UPDATE Orders SET Quantity = Quantity + 1 WHERE ...
    if(connection==null){
      connection = DB.getConnection();
    }
    if(connection.isClosed()){
      connection = DB.getConnection();
    }
    Statement statement = connection.createStatement();
    statement.executeUpdate("UPDATE containers SET "
        +"num_items=num_items-1 "
        +"WHERE machine_id='"+machine_id+"' AND position='"+column+"'");
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
      receipt.products = new ArrayList<Products>();

      while (resultSet.next()) {
        receipt.machineAddress = resultSet.getString("address");
        receipt.total = resultSet.getString("sales_total");

        Products pm = new Products();
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

  public static int recordSale(String machine_id, String productId, BigDecimal productPrice) throws SQLException {
    if (connection == null || connection.isClosed()){
      connection = DB.getConnection();
    }

    //log sale
    Date date = new Date();
    Statement statement = connection.createStatement();
    statement.executeUpdate("INSERT INTO sales "
        + "(machine_id, sales_total, time) VALUES ( "
        + machine_id + ", " + productPrice
        + ", '" + new Timestamp(date.getTime()) + "')",
        Statement.RETURN_GENERATED_KEYS);

    ResultSet results = statement.getGeneratedKeys();
    //log products
    if (results.next()) {
      int salesId = results.getInt(1);
      statement.executeUpdate("INSERT INTO sales_products "
          + "(sales_id, product_sku, product_price) VALUES ( "
          + salesId + ", " + productId + ", " + productPrice + ")");
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
