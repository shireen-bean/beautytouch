package controllers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

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
			connection = DB.getConnection();
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

			//Connection connection = DB.getConnection();
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

		//Connection connection = DB.getConnection();
		Statement statement = connection.createStatement();
		statement
				.executeUpdate("INSERT INTO products "
						+ "(itemName,itemImg,price,itemDescription,packageType) VALUES ("
						+ "'" + itemName + "'," + "'" + itemImg + "'," + "'"
						+ price + "'," + "'" + itemDescription + "'," + "'"
						+ packageType + "'" + ")");
	}

	public static ArrayNode getProductList() {
		try {
			//Connection connection = DB.getConnection();
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
			Logger.error("getAll users and emails error:" + e.getMessage());
			return null;
		}
	}

	public static ObjectNode getProduct(String sku) {
		try {
			//Connection connection = DB.getConnection();
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

}
