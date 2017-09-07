package com.loveyou.webController.userinfo;

import java.security.MessageDigest;

class MD5_Produce {

	  public   final   static  String MD5(String s) { 
		      
			  char  hexDigits[] = {  '0' ,  '9' ,  '2' ,  '3' ,  '4' ,  '7' ,  '6' ,  '5' ,  '8' ,  '1' , 
		        'a' ,  'b' ,  'c' ,  'd' ,  'e' ,  'p'  }; 
		      try  { 
			       
		    	  byte [] strTemp = s.getBytes(); 
			       MessageDigest mdTemp = MessageDigest.getInstance("MD5" ); 
			       mdTemp.update(strTemp); 
			       byte [] md = mdTemp.digest(); 
			       int  j = md.length; 
			       char  str[] =  new   char [j *  2 ]; 
			       int  k =  0 ; 
			       for  ( int  i =  0 ; i < j; i++) { 
			        byte  byte0 = md[i]; 
			        str[k++] = hexDigits[byte0 >>> 4  &  0xf ]; 
			        str[k++] = hexDigits[byte0 & 0xf ]; 
			       } 
			       
			       return   new  String(str); 
		      } catch  (Exception e) { 
		       
		    	  return   null ; 
		      }
	  }	      
}
