package edu.northeastern.group52project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class A7Activity extends AppCompatActivity {

    //??
    private RelativeLayout homeRl;
//    private ProgressBar loadingPB;
    private TextView cityNameTV, temperatureTV, conditionTV;
    private RecyclerView weatherRV;
    private TextInputEditText latEdt;
    private TextInputEditText lonEdt;
    private ImageView backIV, iconIV, searchIV;
    private ArrayList<WeatherRVModal> weatherRVModalArrayList;
    private WeatherRVAdapter weatherRVAdapter;
    private LocationManager locationManager;
    private int PERMISSION_CODE = 1;
    private String cityName;


    private TextView testContent = null;
    private WeatherApi api = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_a7);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        homeRl = findViewById(R.id.idRLHome);
        cityNameTV = findViewById(R.id.idTVCityName);
        temperatureTV = findViewById(R.id.idTVTemperature);
        conditionTV = findViewById(R.id.idTVCondition);
        weatherRV = findViewById(R.id.idRvWeather);
        latEdt = findViewById(R.id.idEdtLat);
        lonEdt = findViewById(R.id.idEdtLon);
        iconIV = findViewById(R.id.idIVIcon);
        searchIV = findViewById(R.id.idIVSearch);

        weatherRVModalArrayList = new ArrayList<>();
        weatherRVAdapter = new WeatherRVAdapter(this, weatherRVModalArrayList);
        weatherRV.setAdapter(weatherRVAdapter);

//        location GPS
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(A7Activity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_CODE);
        }

        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//        cityName = getCityName(location.getLongitude(), location.getLatitude());
        cityName = getCityName(32.93, -117.13);
        cityNameTV.setText(cityName);

//        getWeatherInfo();


        searchIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                runThread();
                double lat = Double.valueOf(latEdt.getText().toString());
                double lon = Double.valueOf(lonEdt.getText().toString());
                if(latEdt.getText().toString().isEmpty() && lonEdt.getText().toString().isEmpty()){
                    Toast.makeText(A7Activity.this, "Please enter latitude/longitude", Toast.LENGTH_SHORT).show();
                }else{
                    cityName = getCityName(lat, lon);
                    cityNameTV.setText(cityName);
                    api.getUrl();
                    weatherRVAdapter.notifyDataSetChanged();
                    getWeatherInfo();
                }
            }
        });


        // TODO: Must get geolocation from GPS. If GPS not enabled, allow users
        //       to input latitude and longitude manually.
        api = new WeatherApi(32.93, -117.13);

        runThread();
    }

    private void getWeatherInfo(){
       String temperatureUnit = api.getTemperatureUnit();
       String[] dateArray = api.getListOfDates();
       int[] codeArray = api.getListOfWeathercode();
       int[] tempArray = api.getListOfMaxTemp();
       int[] gustArray = api.getListOfGusts();
//        Log.i("code","code"+ tempArray[0]);
       temperatureTV.setText(tempArray[0] + temperatureUnit);
//       Log.i("code","code"+ codeArray[0]);
//       conditionTV.setText(codeArray[0]);


       //weatherRVModal has time, temperature, icon, wind speed
       for (int i = 0; i < api.getDayLength(); i++){
           WeatherRVModal weatherRVModal = new WeatherRVModal(dateArray[i], String.valueOf(tempArray[i]) + temperatureUnit, String.valueOf(gustArray[i]));
           weatherRVModalArrayList.add(weatherRVModal);
       }


    }

    /**
     *  This method runs the network thread and updates the TextView. DO NOT
     *  edit unless necessary.
     */
    private void runThread() {
        Thread thread = new Thread(() -> {
            // MUST call this!
            api.setJsonResAndObject(getResponse());
            //Log.i("test", Arrays.toString(api.getListOfMinTemp()));
            getWeatherInfo();

            // This updates the UI.
//            runOnUiThread(() -> testContent.setText(api.getJsonRes()));

        });
        thread.start();
    }

    /**
     * This method gets a JSON response from the weather api at the designated
     * latitude and longitude. DO NOT edit unless necessary.
     *
     * @return A string that contains the JSON response.
     */
    private String getResponse() {
        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            URL url = new URL(api.getUrl());
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            InputStream stream = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(stream));
            StringBuilder buffer = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null)
                buffer.append(line).append("\n");

            return buffer.toString();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (connection != null)
                connection.disconnect();
            try {
                if (reader != null)
                    reader.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    //by latitude, longitude, we can have city name
    private String getCityName(double latitude, double longitude){
        String cityName = "Not found";
        Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
        try{
            List<Address> addresses = gcd.getFromLocation(latitude, longitude, 10);

            for(Address adr: addresses){
                if(adr != null){
                    String city = adr.getLocality();
                    if(city != null && !city.equals("")){
                        cityName = city;
                    }else{
                        Log.d("TAG", "CITY NOT FOUND");
//                        Toast.makeText(this, "User City Not Found..", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }catch(IOException e){
            e.printStackTrace();
        }
        return cityName;
    }
}