package com.example.hellogooglemaps;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseQuery.CachePolicy;

public class InboxActivity extends Activity {

	private ParsePushMessageAdapter mAdapter;
	private ListView mListView;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);
        
        mListView = (ListView)findViewById(R.id.task_list);
        mAdapter = new ParsePushMessageAdapter(this, new ArrayList<ParsePushMessage>());
        mListView.setAdapter(mAdapter);
        
        updateData();
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
    				Intent prefIntent = new Intent(getBaseContext(), PreferencesActivity.class);
    				startActivity(prefIntent);
    				break;
    			case R.id.action_inbox:
    				Toast.makeText(this, "Inbox selected", Toast.LENGTH_SHORT).show();
    				break;
    			default:
    				break;
    		}
    		return true;
    }
    
    public void updateData(){
    	  ParseQuery<ParsePushMessage> query = ParseQuery.getQuery(ParsePushMessage.class);
    	  query.setCachePolicy(CachePolicy.CACHE_THEN_NETWORK);
    	  query.findInBackground(new FindCallback<ParsePushMessage>() {
    	          
    	      @Override
    	      public void done(List<ParsePushMessage> tasks, ParseException error) {
    	          if(tasks != null){
    	              mAdapter.clear();
    	              mAdapter.addAll(tasks);
    	          }
    	      }
    	  });
    	}
}
