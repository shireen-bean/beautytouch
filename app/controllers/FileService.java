package controllers;

import java.io.File;
import play.cache.Cache;

import play.mvc.*;

	public class FileService extends Controller {
	       public static Result getProductImage(String file){
	    	   	  Object fileObj = Cache.get(file);
	    	   	  File c =(File)fileObj;
	    	   	  if(fileObj!=null){
	    	   		  return ok(c);
	    	   	  }
	    	   	  else{
			       	  String path = "/public/dynamicFiles/products/";
		              File myfile = new File (System.getenv("PWD")+path+file);
		              Cache.set(file, myfile,60 * 15);
		              return ok(myfile);
	    	   	  }
	       }
	       
	       public static Result getBrandLogo(String file){
	    	   Object fileObj = Cache.get(file);
	    	   File c = (File)fileObj;
	    	   if (fileObj != null) {
	    		   return ok(c);
	    	   } else {
	    		   String path = "/public/dynamicFiles/brands/";
	    		   File myfile = new File (System.getenv("PWD") + path + file);
	    		   Cache.set(file, myfile, 60*15);
	    		   return ok(myfile);
	    	   }
	       }
	}
