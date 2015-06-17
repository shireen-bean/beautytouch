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

import models.Container;
import models.MachineModel;
import models.ProductModel;

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
			+"itemImg='"+itemImg+"',"
			+"price='"+price+"',"
			+"itemDescription='"+itemDescription+"',"
			+"packageType='"+packageType+"'"
			+" WHERE itemSku='"+itemSku+"'");
	}

	public static void addProduct(String itemName, String itemImg,
			String price, String itemDescription, String packageType)
			throws SQLException {

		if(connection==null || connection.isClosed()){
			connection = DB.getConnection();
		}
		
		Statement statement = connection.createStatement();
		statement
				.executeUpdate("INSERT INTO products "
						+ "(itemName,itemImg,price,itemDescription,packageType) VALUES ("
						+ "'" + itemName + "'," + "'" + itemImg + "'," + "'"
						+ price + "'," + "'" + itemDescription + "'," + "'"
						+ packageType + "'" + ")");
	}

	public static void logMachineStatus(int machine_id, int jammed, int traffic) 
	    throws SQLException {
		System.out.println("logging status");
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
			
			ResultSet resultSet = statement.executeQuery("SELECT * FROM products");

			ObjectMapper mapper = new ObjectMapper();
			ArrayNode nodeArray=mapper.createArrayNode();

			while (resultSet.next()) {
				ObjectNode result = Json.newObject();
				result.put("itemSku",resultSet.getString("itemSku"));
				result.put("itemName",resultSet.getString("itemName"));
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

	public static ObjectNode getProduct(String sku) {
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
				ObjectNode result = Json.newObject();
				result.put("itemSku",resultSet.getString("itemSku"));
				result.put("itemName",resultSet.getString("itemName"));
				result.put("itemImg",resultSet.getString("itemImg"));
				result.put("itemDescription", resultSet.getString("itemDescription"));
				result.put("packageType", resultSet.getString("packageType"));
				result.put("price", resultSet.getString("price"));
				return(result);
			}
			
			return null;

		} catch (Exception e) {
			Logger.error("getAll users and emails error:" + e.getMessage());
			return null;
		}
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
		    		"products.itemName, products.itemImg, products.price, products.itemDescription, products.packageType "+
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
		    		"products.itemName, products.itemImg, products.price, products.itemDescription, products.packageType "+
		             "FROM machines, containers, products " +
		             "WHERE " +
		             "machines.id='"+idMachine+"' "+
		             "AND machines.id = containers.machineId "+
		             "AND containers.itemSku = products.itemSku";
			
			ResultSet resultSet = statement.executeQuery(query);
			MachineModel machine = new MachineModel();
			machine.containers = new ArrayList<Container>();
			machine.totalCapacity=0;
			while (resultSet.next()) {
					ProductModel product = new ProductModel();
					product.itemName=resultSet.getString("itemName");
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
//	    		"products.itemName, products.itemImg, products.price, products.itemDescription, products.packageType "+
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
	
	
	

}
