package com.example.hellogooglemaps;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.parse.ParseInstallation;

public class PreferencesActivity extends Activity {

	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);
        
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences( getBaseContext() );
        String favorite = prefs.getString(getResources().getString(R.string.flavor_key), "none");
        RadioGroup radioGroup = (RadioGroup)findViewById(R.id.radio_group_flavors);
        
        if(favorite.equals(getResources().getString(R.string.chocolate))){
        		Toast.makeText(this, "Your favorite flavor is chocolate!", Toast.LENGTH_SHORT).show();
        		radioGroup.check(R.id.radio_chocolate);
        }else if(favorite.equals(getResources().getString(R.string.vanilla))){
        		Toast.makeText(this, "Your favorite flavor is vanilla!", Toast.LENGTH_SHORT).show();
        		radioGroup.check(R.id.radio_vanilla);
        }else if(favorite.equals(getResources().getString(R.string.strawberry))){
        		Toast.makeText(this, "Your favorite flavor is strawberry!", Toast.LENGTH_SHORT).show();
        		radioGroup.check(R.id.radio_strawberry);
        }else{
        		Toast.makeText(this, "Select a favorite flavor!", Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_map, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    		switch (item.getItemId()) {
    			case R.id.action_map:
    				Toast.makeText(this, "Map selected", Toast.LENGTH_SHORT).show();
    				Intent mapIntent = new Intent(getBaseContext(), MyMapActivity.class);
    				startActivity(mapIntent);
    				break;
    			case R.id.action_preferences:
    				Toast.makeText(this, "Preferences selected", Toast.LENGTH_SHORT).show();
    				break;
    			case R.id.action_inbox:
    				Toast.makeText(this, "Inbox selected", Toast.LENGTH_SHORT).show();
    				Intent inboxIntent = new Intent(getBaseContext(), InboxActivity.class);
    				startActivity(inboxIntent);
    				break;
    			default:
    				break;
    		}
    		return true;
    }
    
    
    public void onRadioButtonClicked(View view) {
        
    		String mNewFavorite = null;
        
        switch(view.getId()) {
            case R.id.radio_chocolate:
            		mNewFavorite = getResources().getString(R.string.chocolate);
            		Toast.makeText(this, "Chocolate selected as your favorite flavor!", Toast.LENGTH_SHORT).show();	
                break;
            case R.id.radio_vanilla:
            		mNewFavorite = getResources().getString(R.string.vanilla);
                	Toast.makeText(this, "Vanilla selected as your favorite flavor!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.radio_strawberry:
            		mNewFavorite = getResources().getString(R.string.strawberry);
                	Toast.makeText(this, "Strawberry selected as your favorite flavor!", Toast.LENGTH_SHORT).show();
                break;
        }
        
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences( getBaseContext() );
        String mPreviousFavorite = prefs.getString(getResources().getString(R.string.flavor_key), "none");
        
        if(!mPreviousFavorite.equals(mNewFavorite)){
        		
        		List<String> channelsToAdd = new ArrayList<String>();
        		channelsToAdd.add(mNewFavorite);
        		
        		List<String> channelsToRemove = new ArrayList<String>();
        		channelsToRemove.add(mPreviousFavorite);
        		
        		ParseInstallation installation = ParseInstallation.getCurrentInstallation();			
			installation.addAllUnique(getResources().getString(R.string.channels), channelsToAdd);
			installation.saveEventually();
		    
		    installation.removeAll(getResources().getString(R.string.channels), channelsToRemove);
		    installation.saveInBackground();
		    		
			SharedPreferences.Editor editor = prefs.edit();
			editor.putString(getResources().getString(R.string.flavor_key), mNewFavorite);
			editor.commit();
        }
        
    }
    
}
