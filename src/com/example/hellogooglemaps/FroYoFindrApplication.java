package com.example.hellogooglemaps;

import android.app.Application;
import android.content.res.Configuration;

import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.PushService;

public class FroYoFindrApplication extends Application {
	 
	private static FroYoFindrApplication singleton;
	
	public FroYoFindrApplication getInstance(){
		return singleton;
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
 
	@Override
	public void onCreate() {
		super.onCreate();
		singleton = this;
        Parse.initialize(this, getResources().getString(R.string.parse_app_id), getResources().getString(R.string.parse_client_key)); 
        ParseObject.registerSubclass(ParsePushMessage.class);
        PushService.setDefaultPushCallback(this, DeepLinkActivity.class);
        ParseInstallation.getCurrentInstallation().saveInBackground();
	
	}
	
 
	@Override
	public void onLowMemory() {
		super.onLowMemory();
	}
 
	@Override
	public void onTerminate() {
		super.onTerminate();
	}
 
}