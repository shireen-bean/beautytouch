package controllers;


	public class Security{
		
		   public static String uuid = "cc2be036-6f09-4d92-a618-0a478caaad99";
	       public static boolean validKey(String key){
	    	      System.out.println(key);
	    	   	  if(key.equals(uuid)){
	    	   		  return true;
	    	   	  }else{
	    	   		  return false;
	    	   	  }
	       }
	}
