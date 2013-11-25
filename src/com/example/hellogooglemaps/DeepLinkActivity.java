package com.example.hellogooglemaps;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class DeepLinkActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        examineIntent();
    }
	
	private void examineIntent()
    {
		Intent i = getIntent();
		Bundle extras = i.getExtras(); 
		String jsonData = extras.getString( "com.parse.Data" );
		JSONObject resultObject = null;
		JSONArray flavors = null;
		String flavor = "chocolate";
		try {
			resultObject = new JSONObject(jsonData);
			flavors = resultObject.getJSONArray("flavors");
			flavor = flavors.getString(0);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		Log.v("DeepLinkActivity intent data", flavor);
		
		
		LinearLayout mLinearLayout = new LinearLayout(this);
        ImageView imageView = new ImageView(this);
        
        if(flavor.equals(getResources().getString(R.string.chocolate))){
        		imageView.setImageResource(R.drawable.chocolate);
        }else if(flavor.equals(getResources().getString(R.string.vanilla))){
        		imageView.setImageResource(R.drawable.vanilla);
        }else if(flavor.equals(getResources().getString(R.string.strawberry))){
        		imageView.setImageResource(R.drawable.strawberry);
        }
        
        imageView.setAdjustViewBounds(true);
        imageView.setLayoutParams(new ViewGroup.LayoutParams(
        		ViewGroup.LayoutParams.MATCH_PARENT, 
        		ViewGroup.LayoutParams.MATCH_PARENT));
        mLinearLayout.addView(imageView);
        setContentView(mLinearLayout);
        
    }
	
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_map, menu);
        return true;
    }
    
    @Override
    public void onResume(){
    		super.onResume();
    		examineIntent();
    }
    
    @Override
    public void onRestart(){
    		super.onRestart();
    		examineIntent();
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
    				Intent prefIntent = new Intent(getBaseContext(), PreferencesActivity.class);
    				startActivity(prefIntent);
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
}
