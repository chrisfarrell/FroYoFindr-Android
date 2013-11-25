/**
 * Activity for displaying a location based map with markers for Fro-Yo venues.
 *
 * This activity is used to show the user nearby frozen yogurt venues and enable them to ge more details.
 *
 * @author Chris Farrell
 * @version 1.0
 * @since 1.0
 */

package com.example.hellogooglemaps;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.PushService;

public class MyMapActivity extends Activity implements LocationListener{

	/**
	 * id of resource image for user location
	 */
	private int userIcon;
	
	/**
	 * the google map
	 */
	private GoogleMap theMap;
	
	/**
	 * the location manager 
	 */
	private LocationManager locMan;
	
	/**
	 * marker object for user
	 */
	private Marker userMarker;
	
	/**
	 * array of markers for for yo joints
	 */
	private Marker[] placeMarkers;
	
	/**
	 * google caps us at 20 places
	 */
	private final int MAX_PLACES = 20;
	
	/**
	 * array of marker options for the places we want
	 */
	private MarkerOptions[] places;
	
	/**
	 * array of places
	 */
	private String[] placeReferences;
	
	/**
	 * hash map of markers and places, so we can display place details when a marker info window is tapped
	 */
	HashMap<String, String> mMarkerPlaceLink = new HashMap<String, String>();
	
	/**
     * Activity override 
     *
     * @param savedInstanceState 
     * @return void
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_map);
        
        userIcon = R.drawable.cone;

        if(theMap==null){
        		theMap = ((MapFragment)getFragmentManager().findFragmentById(R.id.the_map)).getMap();
        		if(theMap != null){
        			//theMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        			placeMarkers = new Marker[MAX_PLACES];
        			updatePlaces();
        			
        			theMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {

        		        public void onInfoWindowClick(Marker marker) {
        		            //Log.v("onInfoWindowClick", marker.getTitle());
        		            Intent intent = new Intent(getBaseContext(), PlaceDetailsActivity.class);
                        String reference = mMarkerPlaceLink.get(marker.getId());
                        intent.putExtra("reference", reference);

                        startActivity(intent);
        		        }
        		    });
        		}
        }
    }

	/**
     * Activity override 
     */
	@Override
	protected void onResume() {
		super.onResume();
		if(theMap!=null){
			//locMan.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 30000, 100, this);
		}
	}

	/**
     * Activity override 
     */
	@Override
	protected void onPause() {
		super.onPause();
		if(theMap!=null){
			//locMan.removeUpdates(this);
		}
	}
	
	/**
     * Activity override to handle menu
     *
     * @param menu 
     * @return boolean
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_map, menu);
        return true;
    }

	/**
     * Activity override to handle menu selection
     *
     * @param item menu item selected 
     * @return boolean 
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    		switch (item.getItemId()) {
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

	/**
     * LocationListener override
     *
     * @param location 
     */
	@Override
	public void onLocationChanged(Location location) {
		Log.v("MyMapActivity", "location changed");
		updatePlaces();
	}
	
	/**
     * LocationListener override
     *
     * @param location 
     */
	@Override
	public void onProviderDisabled(String provider){
		Log.v("MyMapActivity", "provider disabled");
	}
	
	/**
     * LocationListener override
     *
     * @param location 
     */
	@Override
	public void onProviderEnabled(String provider) {
		Log.v("MyMapActivity", "provider enabled");
	}
	
	/**
     * LocationListener override
     *
     * @param location 
     */
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		Log.v("MyMapActivity", "status changed");
	}
	
	
	/**
     * utility method to reverse geocode user location
     * adds user location to Parse installation table
     * adds custom marker for user location
     * initiates search for forzen yogurt places near user
     */
    private void updatePlaces(){
    		locMan = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
    		Location lastLoc = locMan.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
    		
    		double lat = lastLoc.getLatitude();
    		double lng = lastLoc.getLongitude();
    		LatLng lastLatLng = new LatLng(lat, lng);
    		
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());
        try {
			addresses = geocoder.getFromLocation(lat, lng, 1);
			String line1 = addresses.get(0).getAddressLine(1);
			String arr[] = line1.split(",", 2);
			String city = arr[0];
			String msg = "FroYoFindr found you in " + city;
			Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
			
			//might need to update this logic, if we turn on 'requestLocationUpdates'
			//store the found city locally
			//compare to previously stored found city
			//remove previous from installation table, and add new, manually
			//rather than use subscribe/unsubscribe (which was found to be unworkable for flavor changes)
			
			PushService.subscribe(getBaseContext(), city, DeepLinkActivity.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
            		
    		if(userMarker!=null) userMarker.remove();
    		
    		userMarker = theMap.addMarker(new MarkerOptions()
    	    .position(lastLatLng)
    	    .title("You are here")
    	    .icon(BitmapDescriptorFactory.fromResource(userIcon))
    	    .snippet("Your last recorded location"));
    		
    		theMap.animateCamera(CameraUpdateFactory.newLatLng(lastLatLng), 3000, null);
    		
    		String types = "food|bar|store|restaurant|art_gallery";
    		try {
    			types = URLEncoder.encode(types, "UTF-8");
    		} catch (UnsupportedEncodingException e1) {
    			e1.printStackTrace();
    		}
    		
    		String placesTextSearchStr = "https://maps.googleapis.com/maps/api/place/textsearch/" +
    	    		"json?location="+lat+","+lng+
    	    		"&radius=1000&sensor=true" +
    	    		"&query=frozen+yogurt"+
    	    		"&key="+getResources().getString(R.string.google_places_browser);
    		
    		Log.v("search string", placesTextSearchStr);
    		new GetPlaces().execute(placesTextSearchStr);
    		
    		//locMan.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 30000, 100, this);
    	}
    
    
	/**
     * async task to get places 
     *
     * @param String
     * @param Void
     * @param String 
     */
    private class GetPlaces extends AsyncTask<String, Void, String> {
    		//fetch and parse place data
	    	@Override
	    	protected String doInBackground(String... placesURL) {
	    	    //fetch places
	    		StringBuilder placesBuilder = new StringBuilder();
	    		
	    		//process search parameter string(s)
	    		for (String placeSearchURL : placesURL) {
	    			//execute search
	    			HttpClient placesClient = new DefaultHttpClient();
	    			try {
	    			    //try to fetch the data
	    				HttpGet placesGet = new HttpGet(placeSearchURL);
	    				HttpResponse placesResponse = placesClient.execute(placesGet);
	    				
	    				StatusLine placeSearchStatus = placesResponse.getStatusLine();
	    				if (placeSearchStatus.getStatusCode() == 200) {
	    					//we have an OK response
	    					HttpEntity placesEntity = placesResponse.getEntity();
	    					InputStream placesContent = placesEntity.getContent();
	    					InputStreamReader placesInput = new InputStreamReader(placesContent);
	    					BufferedReader placesReader = new BufferedReader(placesInput);
	    					String lineIn;
	    					while ((lineIn = placesReader.readLine()) != null) {
	    					    placesBuilder.append(lineIn);
	    					}
	    				}
	    			}
	    			catch(Exception e){
	    			    e.printStackTrace();
	    			}
	    		}
	    		return placesBuilder.toString();
	    	}
	    	
	    	
	    	protected void onPostExecute(String result) {
	    	    //parse place data returned from Google Places
	    		if(placeMarkers!=null){
	    		    for(int pm=0; pm<placeMarkers.length; pm++){
	    		        if(placeMarkers[pm]!=null)
	    		            placeMarkers[pm].remove();
	    		    }
	    		}
	    		try {
	    			JSONObject resultObject = new JSONObject(result);
	    			JSONArray placesArray = resultObject.getJSONArray("results");
	    			places = new MarkerOptions[placesArray.length()];
	    			placeReferences = new String[placesArray.length()];	    			
	    			
	    			for (int p=0; p<placesArray.length(); p++) {
	    				boolean missingValue		= false;
		    			LatLng placeLL			= null;
		    			String placeName			= "";	
		    			JSONObject placeObject	= null;
		    			JSONObject loc 			= null;
		    	
		    			try{
		    				missingValue = false;
		    				placeObject = placesArray.getJSONObject(p);
		    				loc = placeObject.getJSONObject("geometry").getJSONObject("location");
		    				placeLL = new LatLng(
		    					    Double.valueOf(loc.getString("lat")),
		    					    Double.valueOf(loc.getString("lng")));
		    				placeName = placeObject.getString("name");   				
		    			}
		    			catch(JSONException jse){
		    			    missingValue=true;
		    			    jse.printStackTrace();
		    			}
		    			
		    			if(missingValue){
		    				places[p]=null;
		    			}else{
		    			    places[p]=new MarkerOptions()
		    			    .position(placeLL)
		    			    .title(placeName);
		    			    
		    			    placeReferences[p] = placeObject.getString("reference");
		    			}
	    				
	    			}
	    		}
	    		catch (Exception e) {
	    		    e.printStackTrace();
	    		}
	    		
	    		if(places != null && placeMarkers != null){
	    		    for(int p=0; p<places.length && p<placeMarkers.length; p++){
	    		        //will be null if a value was missing
	    		        if(places[p]!=null){
	    		            placeMarkers[p]=theMap.addMarker(places[p]);
	    		        }
	    		        if(placeMarkers[p] != null && placeReferences[p] != null){
	    		        		mMarkerPlaceLink.put(placeMarkers[p].getId(), placeReferences[p]);
	    		        }
	    		    }
	    		}
	    	}
    	}
    

    
}
