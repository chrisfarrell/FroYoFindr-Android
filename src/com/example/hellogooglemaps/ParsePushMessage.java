package com.example.hellogooglemaps;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("PushLog")
public class ParsePushMessage extends ParseObject{
  
	public ParsePushMessage(){

	}

  public String getMessage(){
      return getString("message");
  }


}