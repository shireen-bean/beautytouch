package controllers;
import java.util.Map;
import java.util.HashMap;
 
import com.twilio.sdk.resource.instance.Account;
import com.twilio.sdk.TwilioRestClient;
import com.twilio.sdk.TwilioRestException;
import com.twilio.sdk.resource.factory.MessageFactory;
import com.twilio.sdk.resource.instance.Message;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import play.mvc.*;


	public class SMS extends Controller {

	    public static final String ACCOUNT_SID = "AC8758e524bc09f7a4fb5da074d4da0f0c";
	    public static final String AUTH_TOKEN = "75f2dc2fceca3353d55e883cb473b32e";
	    
	       public static Result sendReceipt(){
	    	   try{
	           TwilioRestClient client = new TwilioRestClient(ACCOUNT_SID, AUTH_TOKEN);
	    
	           Account account = client.getAccount();
	    
	           MessageFactory messageFactory = account.getMessageFactory();
	           List<NameValuePair> params = new ArrayList<NameValuePair>();
	           params.add(new BasicNameValuePair("To", "+15612717115")); // Replace with a valid phone number for your account.
	           params.add(new BasicNameValuePair("From", "+18597590660")); // Replace with a valid phone number for your account.
	           
	           String messageBody = "Thank you for your Oasys purchase!";
	          
	           params.add(new BasicNameValuePair("Body", messageBody));
	           Message sms = messageFactory.create(params);
	    	   }catch(Exception e){
	    		  System.out.println(e.toString()); 
	    	   }
	    	   return ok();
	       }
	}
