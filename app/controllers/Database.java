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
import java.math.BigDecimal;

import models.Container;
import models.MachineModel;
import models.ProductModel;
import models.Receipt;
import models.Brand;

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
  //
  //	public static ArrayList<MachineModel> getMachines(){
  //		try {
  //
  //			if(connection==null){
  //				connection = DB.getConnection();
  //			}
  //			if(connection.isClosed()){
  //				connection = DB.getConnection();
  //			}
  //
  //			Statement statement = connection.createStatement();
  //			ResultSet resultSet;
  //			resultSet = statement
  //					.executeQuery("SELECT * FROM Machines");
  //
  //			ArrayList<MachineModel> machines = new ArrayList<MachineModel>();
  //
  //			if (resultSet.next()) {
  //				MachineModel m = new MachineModel();
  //				m.id=resultSet.getLong("id");
  //				m.lat=resultSet.getDouble("lat");
  //				m.lon=resultSet.getDouble("lon");
  //				m.address=resultSet.getString("address");
  //				m.status=resultSet.getString("status");
  //				System.out.println(m.id.toString()+","+m.lat.toString()+","+m.lon.toString()+","+m.address.toString()+","+m.status.toString());
  //			}
  //		} catch (SQLException e) {
  //		}
  //
  //		return null;
  //	}

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

  public static void editProduct(String itemSku, String itemName,
      String category, String brand_id,
      String itemImg, String price, String itemDescription,
      String packageType) throws SQLException {

    if(connection==null){
      connection = DB.getConnection();
    }
    if(connection.isClosed()){
      connection = DB.getConnection();
    }
    Statement statement = connection.createStatement();
    statement.executeUpdate("UPDATE products SET "
        +"itemName='"+itemName+"',"
        +"category='"+category+"',"
        +"brand_id='" + brand_id + "',"
        +"itemImg='"+itemImg+"',"
        +"price='"+price+"',"
        +"itemDescription='"+itemDescription+"',"
        +"packageType='"+packageType+"'"
        +" WHERE itemSku='"+itemSku+"'");
  }

  public static void addProduct(String itemName, String category, String brand_id, String itemImg,
      String price, String itemDescription, String packageType)
    throws SQLException {

    if(connection==null || connection.isClosed()){
      connection = DB.getConnection();
    }

    Statement statement = connection.createStatement();
    statement
      .executeUpdate("INSERT INTO products "
          + "(itemName,category,itemImg,price,itemDescription,packageType, brand_id) VALUES ("
          + "'" + itemName + "','"+ category + "','"+ itemImg + "'," + "'"
          + price + "'," + "'" + itemDescription + "'," + "'"
          + packageType + "', '" + brand_id + "')");
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

  public static ArrayNode getProductList() {
    try {
      if(connection==null){
        connection = DB.getConnection();
      }
      if(connection.isClosed()){
        connection = DB.getConnection();
      }


      Statement statement = connection.createStatement();

      ResultSet resultSet = statement.executeQuery("SELECT products.*, "
      	  + "brands.name as brandName, brands.logo as brandLogo, brands.description as brandDescription"
          + " FROM products LEFT JOIN brands on products.brand_id = brands.id ");

      ObjectMapper mapper = new ObjectMapper();
      ArrayNode nodeArray=mapper.createArrayNode();

      while (resultSet.next()) {
        ObjectNode result = Json.newObject();
        result.put("itemSku",resultSet.getString("itemSku"));
        result.put("itemName",resultSet.getString("itemName"));
        result.put("category",  resultSet.getString("category"));
        result.put("itemImg",resultSet.getString("itemImg"));
        result.put("itemDescription", resultSet.getString("itemDescription"));
        result.put("price", resultSet.getString("price"));
        result.put("packageType", resultSet.getString("packageType"));
        nodeArray.add(result);
      }

      return nodeArray;

    } catch (Exception e) {
      Logger.error("********Error" + e.toString());
      return null;
    }
  }

  public static ArrayNode getBrandList() {
    try {
      if (connection == null || connection.isClosed()) {
        connection = DB.getConnection();
      }

      Statement statement = connection.createStatement();
      ResultSet resultSet = statement.executeQuery("SELECT * FROM brands");

      ObjectMapper mapper = new ObjectMapper();
      ArrayNode nodeArray = mapper.createArrayNode();

      while (resultSet.next()) {
        ObjectNode result = Json.newObject();
        result.put("id",  resultSet.getString("id"));
        result.put("name",  resultSet.getString("name"));
        result.put("description",  resultSet.getString("description"));
        result.put("logo", resultSet.getString("logo"));
        nodeArray.add(result);
      }
      return nodeArray;

    } catch (Exception e) {
      Logger.error("********Error: " + e.toString());
      return null;
    }
  }

  public static ObjectNode getProduct(String sku) {
    try {
      if(connection==null){
        connection = DB.getConnection();
      }
      if(connection.isClosed()){
        connection = DB.getConnection();
      }
      Statement statement = connection.createStatement();
      ResultSet resultSet = statement.executeQuery(""
          + "SELECT itemName, category, itemSku,"
          + "itemImg, itemDescription, packageType, price, brand_id, "
          + "brands.name as brandName, brands.logo as brandLogo, brands.description as brandDescription"
          + " FROM products JOIN brands on products.brand_id = brands.id "
          + "WHERE itemSku="+sku);

      if (resultSet.next()) {
        ObjectNode result = Json.newObject();
        result.put("itemSku",resultSet.getString("itemSku"));
        result.put("itemName",resultSet.getString("itemName"));
        result.put("category", resultSet.getString("category"));
        result.put("itemImg",resultSet.getString("itemImg"));
        result.put("itemDescription", resultSet.getString("itemDescription"));
        result.put("packageType", resultSet.getString("packageType"));
        result.put("price", resultSet.getString("price"));
        result.put("brandId", resultSet.getString("brand_id"));
        return(result);
      }

      return null;

    } catch (Exception e) {
      Logger.error("getAll users and emails error:" + e.getMessage());
      return null;
    }
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
      ResultSet resultSet = statement.executeQuery("SELECT itemName,itemSku,itemImg,itemDescription,packageType,price FROM products WHERE itemSku="+sku);

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
    PreparedStatement getMachineId = connection.prepareStatement(query);
    ResultSet rs = getMachineId.executeQuery();

    int machineId=0;
    // extract data from the ResultSet
    if (rs.next()) {
      machineId = rs.getInt(1);
    }

    for(int i=0;i<containerArray.size();i++){
      JsonNode jnContainer = containerArray.get(i);
      String position=jnContainer.get("position").asText();
      String numItems=jnContainer.get("numItems").asText();
      String totalCapacity=jnContainer.get("totalCapacity").asText();
      String itemSku=jnContainer.get("product").get("itemSku").asText();
      String slot=jnContainer.get("slot").asText();

      PreparedStatement insertContainer = connection.prepareStatement("INSERT INTO containers "
          + "(machineId,position,slot,numItems,totalCapacity,itemSku) VALUES ("
          + "'" + machineId + "',"
          + "'" + position + "',"
          + "'"+slot+"',"
          + "'" + numItems + "',"
          + "'" + totalCapacity + "',"
          + "'" + itemSku + "')");
      insertContainer.execute();
    }

    connection.commit();
    connection.setAutoCommit(true);
  }

  public static ArrayList<MachineModel> getMachineList() {
    try {
      if(connection==null){
        connection = DB.getConnection();
      }
      if(connection.isClosed()){
        connection = DB.getConnection();
      }

      Statement statement = connection.createStatement();

      String query = "SELECT machines.id, machines.address, machines.lat, machines.lon, "+
        "containers.id AS containerId, containers.machineId, containers.position, containers.numItems, containers.totalCapacity, containers.itemSku, "+
        "products.itemName, products.category, products.itemImg, products.price, products.itemDescription, products.packageType "+
        "FROM machines, containers, products " +
        "WHERE " +
        "machines.id = containers.machineId "+
        "AND containers.itemSku = products.itemSku"+
        "";

      ResultSet resultSet = statement.executeQuery(query);
      ArrayList<MachineModel> machines = new ArrayList<MachineModel>();

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
          ProductModel product = new ProductModel();
          product.itemName=resultSet.getString("itemName");
          product.category=resultSet.getString("category");
          product.itemSku=resultSet.getInt("itemSku");
          product.itemImg=resultSet.getString("itemImg");
          product.price=resultSet.getString("price");
          product.itemDescription=resultSet.getString("itemDescription");
          product.packageType=resultSet.getString("packageType");

          Container container = new Container();
          container.id=resultSet.getInt("containerId");
          container.position=resultSet.getInt("position");
          container.numItems=resultSet.getInt("numItems");
          container.totalCapacity=resultSet.getInt("totalCapacity");
          container.product = product;
          container.machineId=resultSet.getInt("machineId");

          machines.get(machineIndex).totalCapacity+=container.totalCapacity;
          machines.get(machineIndex).numItems+=container.numItems;
          machines.get(machineIndex).containers.add(container);
        }

        //else create new machine and add container
        else{
          ProductModel product = new ProductModel();
          product.itemName=resultSet.getString("itemName");
          product.category= resultSet.getString("category");
          product.itemSku=resultSet.getInt("itemSku");
          product.itemImg=resultSet.getString("itemImg");
          product.price=resultSet.getString("price");
          product.itemDescription=resultSet.getString("itemDescription");
          product.packageType=resultSet.getString("packageType");

          Container container = new Container();
          container.id=resultSet.getInt("containerId");
          container.position=resultSet.getInt("position");
          container.numItems=resultSet.getInt("numItems");
          container.totalCapacity=resultSet.getInt("totalCapacity");
          container.product = product;
          container.machineId=resultSet.getInt("machineId");

          MachineModel machine = new MachineModel();
          machine.id = resultSet.getLong("id");
          machine.address = resultSet.getString("address");
          machine.lat=resultSet.getDouble("lat");
          machine.lon=resultSet.getDouble("lon");
          machine.containers = new ArrayList<Container>();
          machine.containers.add(container);
          machine.totalCapacity=container.totalCapacity;
          machine.numItems=container.numItems;

          machines.add(machine);
        }
      }
      return machines;

    } catch (Exception e) {
      Logger.error("********Error" + e.toString());
      return null;
    }
  }


  public static MachineModel getMachine(String idMachine) {
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
        "containers.id AS containerId, containers.machineId, containers.position, containers.numItems, containers.totalCapacity, containers.itemSku, containers.slot, "+
        "products.itemName, products.category, products.itemImg, products.price, products.itemDescription, products.packageType, products.brand_id,"+
        "brands.name as brandName, brands.logo as brandLogo, brands.description as brandDescription " +
        "FROM machines, containers, products " +
        "LEFT JOIN brands on products.brand_id = brands.id " +
        "WHERE " +
        "machines.id='"+idMachine+"' "+
        "AND machines.id = containers.machineId "+
        "AND containers.itemSku = products.itemSku";
      
      

      ResultSet resultSet = statement.executeQuery(query);
      MachineModel machine = new MachineModel();
      machine.containers = new ArrayList<Container>();
      machine.totalCapacity=0;
      while (resultSet.next()) {
    	  
    	Brand brand = new Brand();
    	brand.name = resultSet.getString("brandName");
    	brand.logo = resultSet.getString("brandLogo");
    	brand.description = resultSet.getString("brandDescription");
    	brand.id = resultSet.getInt("products.brand_id");
    	
        ProductModel product = new ProductModel();
        product.itemName=resultSet.getString("itemName");
        product.category=resultSet.getString("category");
        product.itemSku=resultSet.getInt("itemSku");
        product.itemImg=resultSet.getString("itemImg");
        product.price=resultSet.getString("price");
        product.itemDescription=resultSet.getString("itemDescription");
        product.packageType=resultSet.getString("packageType");
        product.brand = brand;

        Container container = new Container();
        container.id=resultSet.getInt("containerId");
        container.position=resultSet.getInt("position");
        container.numItems=resultSet.getInt("numItems");
        container.totalCapacity=resultSet.getInt("totalCapacity");
        container.product = product;
        container.machineId=resultSet.getInt("machineId");
        container.slot=resultSet.getInt("slot");

        machine.id = resultSet.getLong("id");
        machine.address = resultSet.getString("address");
        machine.lat=resultSet.getDouble("lat");
        machine.lon=resultSet.getDouble("lon");
        machine.containers.add(container);
        machine.totalCapacity+=container.totalCapacity;
        machine.numItems=container.numItems;
      }

      return machine;

    } catch (Exception e) {
      Logger.error("********Error" + e.toString());
      return null;
    }
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
      String numItems=jnContainer.get("numItems").asText();
      String totalCapacity=jnContainer.get("totalCapacity").asText();
      String itemSku=jnContainer.get("product").get("itemSku").asText();
      String slot=jnContainer.get("slot").asText();

      statement.addBatch("UPDATE containers "
          + "SET machineId='"+jn.get("id")+"',"
          +"position='"+position+"',"
          +"numItems='"+numItems+"',"
          +"totalCapacity='"+totalCapacity+"',"
          +"slot='"+slot+"',"
          +"itemSku='"+itemSku+"'"
          + " WHERE machineId='"+jn.get("id")+"' "
          + " AND position='"+position+"'");
    }

    statement.executeBatch();
    statement.close();

  }

  public static ArrayList<ProductModel> getAvailableProducts(String machineId) {

    //	    String query = "SELECT machines.id, machines.address, machines.lat, machines.lon, "+
    //	    		"containers.id AS containerId, containers.machineId, containers.position, containers.numItems, containers.totalCapacity, containers.itemSku, "+
    //	    		"products.itemName, products.category, products.itemImg, products.price, products.itemDescription, products.packageType "+
    //	             "FROM machines, containers, products " +
    //	             "WHERE " +
    //	             "machines.id='"+idMachine+"' "+
    //	             "AND machines.id = containers.machineId "+
    //	             "AND containers.itemSku = products.itemSku";
    //
    //

    // TODO Auto-generated method stub
    return null;
  }

  public static void removeItem(String machineId, String column) throws SQLException {
    //UPDATE Orders SET Quantity = Quantity + 1 WHERE ...
    if(connection==null){
      connection = DB.getConnection();
    }
    if(connection.isClosed()){
      connection = DB.getConnection();
    }
    Statement statement = connection.createStatement();
    statement.executeUpdate("UPDATE containers SET "
        +"numItems=numItems-1 "
        +"WHERE machineId='"+machineId+"' AND position='"+column+"'");
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


      String query = "SELECT sales.id, products.itemSku, machines.address, products.itemName, sales.sales_total, sales_products.product_price "+
        "FROM machines, products, sales, sales_products " +
        "WHERE " +
        "sales.id='"+salesId+"' "+
        "AND sales.machine_id = machines.id "+
        "AND sales_products.sales_id = sales.id "+
        "AND products.itemSku = sales_products.product_sku ";

      ResultSet resultSet = statement.executeQuery(query);
      Receipt receipt = new Receipt();
      receipt.products = new ArrayList<ProductModel>();

      while (resultSet.next()) {
        receipt.machineAddress = resultSet.getString("address");
        receipt.total = resultSet.getString("sales_total");

        ProductModel pm = new ProductModel();
        pm.itemName = resultSet.getString("itemName");
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

  public static int recordSale(String machineId, String productId, BigDecimal productPrice) throws SQLException {
    if (connection == null || connection.isClosed()){
      connection = DB.getConnection();
    }

    //log sale
    Date date = new Date();
    Statement statement = connection.createStatement();
    statement.executeUpdate("INSERT INTO sales "
        + "(machine_id, sales_total, time) VALUES ( "
        + machineId + ", " + productPrice
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

}
