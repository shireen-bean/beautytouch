package controllers;

import java.io.File;

import com.typesafe.plugin.*;

import play.mvc.*;

	public class Email extends Controller {
	       public static Result sendReceipt(){
	    	   System.out.println("email");
	    	   	MailerAPI mail = play.Play.application().plugin(MailerPlugin.class).email();
	    	   	mail.setSubject("Oasys Receipt");
	    	   	mail.setRecipient("amco1027@gmail.com");
	    	   	mail.setFrom("Oasys <service@oasysventures.com>");
	    	   	//sends html
	    	   	mail.sendHtml("<html>"
	    	   			+ "Thank you for your Oasys purchase!"
	    	   			+ "</html>" );
       	   	    return ok();
	       }
	}
