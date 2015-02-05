package controllers;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import play.db.DB;

//Create account
//insert into Users (username,password) VALUES ('andres@oasysventures.com',ENCRYPT('O@s!s15!?',concat(_utf8'$1$',right(md5(rand()),8),_utf8'$')));

public class Database {
	
	public static boolean userLogin(String username, String password){
		try {
			Connection connection = DB.getConnection();
		    Statement statement = connection.createStatement();
		    ResultSet resultSet;
			resultSet = statement.executeQuery("SELECT username FROM Users WHERE username='"+username+"' AND password = ENCRYPT('"+password+"', password)");

			if(resultSet.next()){
			  return true;
			}
			return false;
		} catch (SQLException e) {
			return false;
		}
	}
	
	
}
