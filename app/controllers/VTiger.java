package controllers;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import play.libs.Json;
import play.mvc.*;
import views.html.*;

public class VTiger extends Controller {

public static String ListTypes(String vtigerURL,String SessionId){
	String LineText="";
	String ConnectionStatus="";
	String token="";
	String accesskey="";
	String parameters="";
	String sError="";
	try{
	URL url = new URL(vtigerURL+"?operation=listtypes&sessionName="+SessionId);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Accept", "application/json");

		if (conn.getResponseCode() != 200) {
			sError="FAIL: "+conn.getResponseCode();
			conn.disconnect();
		    return sError;
		}

		BufferedReader br = new BufferedReader(new InputStreamReader(
			(conn.getInputStream())));

			
		LineText = br.readLine();
	}catch (MalformedURLException e) {
        SessionId="FAIL: MalformedURLException "+e.getMessage();

	  } catch (IOException e) {

		SessionId="FAIL: IOException "+e.getMessage();

	  }
return "";
}
public static String Create(String vtigerURL,String SessionId,String Module, String JsonFields){
	String LineText="";
	String ConnectionStatus="";
	String token="";
	String accesskey="";
	String parameters="";
	String sError="";
	
	try{
		
		
        // logout operation
        URL url2 = new URL(vtigerURL);
		HttpURLConnection conn2 = (HttpURLConnection) url2.openConnection();
	    conn2.setDoOutput(true);
		conn2.setRequestMethod("POST");
		conn2.setRequestProperty("Accept", "application/json");
		
		parameters="operation=create&sessionName="+SessionId+"&elementType="+Module+"&element="+JsonFields;		
		DataOutputStream wr = new DataOutputStream(conn2.getOutputStream());
		wr.writeBytes(parameters);
		wr.flush();
		wr.close();
		if (conn2.getResponseCode() != 200) {
			sError="FAIL: "+conn2.getResponseCode();
			conn2.disconnect();
		    return sError;
		}

		BufferedReader br2 = new BufferedReader(new InputStreamReader(
			(conn2.getInputStream())));
		
		LineText = br2.readLine();
		//Rest_WebService_Client json = new Rest_WebService_Client();
	    ConnectionStatus=GetJSON("success", LineText);
		if (ConnectionStatus.equals("false")){
			String message=GetJSON("message", LineText);
	        sError="FAIL: " + message;
	        conn2.disconnect();
	    	return sError;
	    }
		conn2.disconnect();
		
		
	} catch (MalformedURLException e) {
        SessionId="FAIL: MalformedURLException "+e.getMessage();

	  } catch (IOException e) {

		SessionId="FAIL: IOException "+e.getMessage();

	  }
	  
return "ok";		
}
public static String Logout(String vtigerURL,String SessionId){
	String LineText="";
	String ConnectionStatus="";
	String token="";
	String accesskey="";
	String parameters="";
	String sError="";
	
	try{
		
		
        // logout operation
        URL url2 = new URL(vtigerURL);
		HttpURLConnection conn2 = (HttpURLConnection) url2.openConnection();
	
		conn2.setRequestMethod("POST");
		conn2.setRequestProperty("Accept", "application/json");
		conn2.setDoOutput(true);
		parameters="operation=logout&sessionName="+SessionId;		
		DataOutputStream wr = new DataOutputStream(conn2.getOutputStream());
		wr.writeBytes(parameters);
		wr.flush();
		wr.close();
		if (conn2.getResponseCode() != 200) {
			sError="FAIL: "+conn2.getResponseCode();
			conn2.disconnect();
		    return sError;
		}

		BufferedReader br2 = new BufferedReader(new InputStreamReader(
			(conn2.getInputStream())));
		
		LineText = br2.readLine();
		//Rest_WebService_Client json = new Rest_WebService_Client();
	    ConnectionStatus=GetJSON("success", LineText);
		if (ConnectionStatus.equals("false")){
	        sError="FAIL: logout failure";
	        conn2.disconnect();
	    	return sError;
	    }
		conn2.disconnect();
		
		
	} catch (MalformedURLException e) {
        SessionId="FAIL: MalformedURLException "+e.getMessage();

	  } catch (IOException e) {

		SessionId="FAIL: IOException "+e.getMessage();

	  }
	  
return "ok";		
}    
public static String GetLoginSessionId(String vtigerURL,String userkey, String username){
	String LineText="";
	String ConnectionStatus="";
	String token="";
	String accesskey="";
	String parameters="";
	String SessionId="";
	String sError="";
	
	try{
	
	URL url = new URL(vtigerURL+"?operation=getchallenge&username="+username);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Accept", "application/x-www-form-urlencoded");

		if (conn.getResponseCode() != 200) {
			sError="FAIL: "+conn.getResponseCode();
			conn.disconnect();
		    return sError;
		}

		BufferedReader br = new BufferedReader(new InputStreamReader(
			(conn.getInputStream())));

			
		LineText = br.readLine();
		//Rest_WebService_Client json = new Rest_WebService_Client();
	    ConnectionStatus=GetJSON("success", LineText);
	    if (ConnectionStatus.equals("false")){
	        sError="FAIL: Can not retrieve token";
	        conn.disconnect();
	    	return sError;
	    }	
	    token=GetJSON("token", LineText);
	    token=token.replace("\"","");
	    
	    accesskey=md5(token+userkey);	
	    conn.disconnect();		
		
        // login operation
        URL url2 = new URL(vtigerURL);
		HttpURLConnection conn2 = (HttpURLConnection) url2.openConnection();
	
		conn2.setRequestMethod("POST");
		conn2.setRequestProperty("Accept", "application/json");
		parameters="operation=login&username="+username+"&accessKey="+accesskey;
		conn2.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(conn2.getOutputStream());
		wr.writeBytes(parameters);
		wr.flush();
		wr.close();
		if (conn2.getResponseCode() != 200) {
			sError="FAIL: "+conn2.getResponseCode();
			conn2.disconnect();
		    return sError;
		}

		BufferedReader br2 = new BufferedReader(new InputStreamReader(
			(conn2.getInputStream())));
		
		LineText = br2.readLine();
		ConnectionStatus=GetJSON("success", LineText);
		if (ConnectionStatus.equals("false")){
		
	        sError="FAIL: " + GetJSON("message", LineText);;
	        conn2.disconnect();
	    	return sError;
	    }
		SessionId=GetJSON("sessionName", LineText);
	    SessionId=SessionId.replace("\"","");
		conn2.disconnect();
		if (SessionId.equals("")){
		    sError="FAIL: SessionId is Empty";
			conn2.disconnect();
			return sError;
		}
		
	} catch (MalformedURLException e) {
        SessionId="FAIL: MalformedURLException "+e.getMessage();

	  } catch (IOException e) {

		SessionId="FAIL: IOException "+e.getMessage();

	  }
	  
return SessionId;		
}    
public static String GetJSON(String name, String inputstring){
	int posI=0;
	int posF=0;
	String Output="";
	
	posI=inputstring.indexOf(name);
	if (posI>=0){
		posI=inputstring.indexOf(":",posI)+1;
		posF=inputstring.indexOf(",",posI);
		if (posF<0){
			posF=inputstring.indexOf("}",posI);
		}
		Output=inputstring.substring(posI,posF);
		return Output;
	}
	
return "";		
}
public static String md5(String s) 
{
    MessageDigest digest;
        try 
            {
                digest = MessageDigest.getInstance("MD5");
                digest.update(s.getBytes(),0,s.length());
                String hash = new BigInteger(1, digest.digest()).toString(16);
                return hash;
            } 
        catch (NoSuchAlgorithmException e) 
            {
                e.printStackTrace();
            }
        return "";
}
    
}