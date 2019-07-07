package com.londonappbrewery.climapm;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.util.Calendar;

import cz.msebera.android.httpclient.Header;


public class WeatherController extends AppCompatActivity {

    final int REQUEST_CODE=123;
    // Constants:
    final String WEATHER_URL = "http://api.openweathermap.org/data/2.5/weather";
    // App ID to use OpenWeather data
    final String APP_ID = "0ca2ba5c5dc6c560ddbc5bb80a702eb8";
    // Time between location updates (5000 milliseconds or 5 seconds)
    final long MIN_TIME = 5000;
    // Distance between location updates (1000m or 1km)
    final float MIN_DISTANCE = 10;

    // TODO: Set LOCATION_PROVIDER here:




    // Member Variables:
    TextView mCityLabel;
    ImageView mWeatherImage;
    TextView mTemperatureLabel;
    String LocationProvider=LocationManager.NETWORK_PROVIDER;

    // TODO: Declare a LocationManager and a LocationListener here:
    LocationManager mLocationManager;
    LocationListener mLocationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_controller_layout);

        // Linking the elements in the layout to Java code
        mCityLabel = (TextView) findViewById(R.id.locationTV);
        mWeatherImage = (ImageView) findViewById(R.id.weatherSymbolIV);
        mTemperatureLabel = (TextView) findViewById(R.id.tempTV);
        ImageButton changeCityButton = (ImageButton) findViewById(R.id.changeCityButton);
        getWeatherForCurrentLocation();

        // TODO: Add an OnClickListener to the changeCityButton here:
        changeCityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent t1=new Intent(WeatherController.this,ChangeCityController.class);
                startActivity(t1);
            }
        });

    }


    // TODO: Add onResume() here:
    @Override
    protected void onResume(){
        super.onResume();
        Intent t2=getIntent();
        String cityName=t2.getStringExtra("cityname");
        if(cityName!=null){
            getweatherForNewCity(cityName);
        }else {
            getWeatherForCurrentLocation();
        }
    }


    // TODO: Add getWeatherForNewCity(String city) here:
    private void getweatherForNewCity(String city){
        RequestParams params=new RequestParams();
        params.put("q",city);
        params.put("appid",APP_ID);
        letsDoSomeNetworking(params);
    }



    // TODO: Add getWeatherForCurrentLocation() here:
    private void getWeatherForCurrentLocation(){
        mLocationManager=(LocationManager)getSystemService(Context.LOCATION_SERVICE);
        mLocationListener=new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                String latitude=String.valueOf(location.getLatitude());
                String longitude=String.valueOf(location.getLongitude());
                Log.i("123456789lat",latitude);
                Log.i("123456789long",longitude);
                RequestParams params=new RequestParams();
                params.put("lat",latitude);
                params.put("lon",longitude);
                params.put("appid",APP_ID);
                letsDoSomeNetworking(params);

            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
    /* changes*/ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE);

            return;
        }

        mLocationManager.requestLocationUpdates(LocationProvider,MIN_TIME,MIN_DISTANCE,mLocationListener);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==REQUEST_CODE){
            if(grantResults.length > 0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Log.i("123456789grant","Location granted!!!! yeah");
                getWeatherForCurrentLocation();
            }
            else{
                Toast.makeText(getApplicationContext(),"Turn on your Location",Toast.LENGTH_LONG).show();
            }
        }
    }

    // TODO: Add letsDoSomeNetworking(RequestParams params) here:
    private void letsDoSomeNetworking(RequestParams params){
        AsyncHttpClient client=new AsyncHttpClient();

        client.get(WEATHER_URL,params,new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.i("123456789ans",response.toString());
                WeatherDataModel weatherData=WeatherDataModel.fromJson(response);
                updateUI(weatherData);

            }
            @Override
            public void onFailure(int statuscode,Header[] headers,Throwable e,JSONObject response){
                Log.i("123456789stat",String.valueOf(statuscode));
                if(String.valueOf(statuscode).trim().equals("404"))
                    Toast.makeText(WeatherController.this,"Check the spelling or city is not available",Toast.LENGTH_LONG).show();
                Log.i("123456789fail", "i dont know why it fails...");
            }
        });
    }



    // TODO: Add updateUI() here:
    public void updateUI(WeatherDataModel weatherData){
        mCityLabel.setText(weatherData.getCity());
        mTemperatureLabel.setText(weatherData.getTempertaure());
        int resourceId=getResources().getIdentifier(weatherData.getIconName(),"drawable",getPackageName());
        mWeatherImage.setImageResource(resourceId);
    }



    // TODO: Add onPause() here:


    @Override
    protected void onPause() {
        super.onPause();

        if(mLocationManager !=null)
            mLocationManager.removeUpdates(mLocationListener);
    }
}
