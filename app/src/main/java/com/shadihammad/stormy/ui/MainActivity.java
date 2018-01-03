package com.shadihammad.stormy.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.shadihammad.stormy.R;
import com.shadihammad.stormy.weather.Current;
import com.shadihammad.stormy.weather.Day;
import com.shadihammad.stormy.weather.Forecast;
import com.shadihammad.stormy.weather.Hour;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class
MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();
    public static final String DAILY_FORECAST = "DAILY_FORECAST";
    public static final String HOURLY_FORECAST = "HOURLY_FORECAST";
    private static final int PERMISSIONS_REQUEST_CODE = 10;
    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;

    private Forecast mForecast;
    private LocationManager locationManager;
    private Location location;
    private LocationListener locationListener;
    private double latitude;
    private double longitude;
    Place mPlace;
    @BindView(R.id.mainLayout) ConstraintLayout mMainLayout;
    @BindView(R.id.timeLabel) TextView mTimeLabel;
    @BindView(R.id.temperatureLabel) TextView mTemperatureLabel;
    @BindView(R.id.humidityValue) TextView mHumidityValue;
    @BindView(R.id.precipValue) TextView mPrecipValue;
    @BindView(R.id.summaryLabel) TextView mSummaryLabel;
    @BindView(R.id.iconImageView) ImageView mIconImageView;
    @BindView(R.id.refreshImageView) ImageView mRefreshImageView;
    @BindView(R.id.progressBar) ProgressBar mProgressBar;
    @BindView(R.id.locationLabel) TextView mLocationLabel;
    @BindView(R.id.windspeedValue) TextView mWindspeedValue;
    @BindView(R.id.visibilityValue) TextView mVisibilityValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET }, PERMISSIONS_REQUEST_CODE);
            }
            else {
                setUpLocation();

                mProgressBar.setVisibility(View.INVISIBLE);

                mRefreshImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getForecast(latitude, longitude);
                    }
                });

                getForecast(latitude, longitude);
            }
        }
        else {
            setUpLocation();

            mProgressBar.setVisibility(View.INVISIBLE);

            mRefreshImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getForecast(latitude, longitude);
                }
            });

            getForecast(latitude, longitude);
        }
    }

    private void setUpLocation() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (locationManager.getLastKnownLocation("gps") != null) {
            location = locationManager.getLastKnownLocation("gps");
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }
        else {
            Toast.makeText(this, "Please hit the refresh button to receive accurate info",
                    Toast.LENGTH_LONG).show();
        }

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                Log.i("Lat: ", latitude + "");
                Log.i("Long: ", longitude + "");
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        };
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET }, 10);
            }

        }
        else {
            configureLocation();
        }

    }

    private String getCity() {
        String cityName = "";

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            Log.e(TAG, "Exception caught: ", e);
        }
        if (addresses != null && addresses.size() > 0) {
            cityName = addresses.get(0).getAddressLine(0);
            String[] addArr = cityName.split(",");
            cityName = addArr[1];
        }

        return cityName;
    }

    private void configureLocation() {
        locationManager.requestLocationUpdates("gps", 120000, 0, locationListener);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setUpLocation();

                mProgressBar.setVisibility(View.INVISIBLE);

                mRefreshImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getForecast(latitude, longitude);
                    }
                });

                getForecast(latitude, longitude);
            }
        }
    }

    private void getForecast(double lati, double longi) {
        String apiKey = "5a67e7a6ac00ed33e49f822e9a31a05b";
        String forecastUrl = "https://api.darksky.net/forecast/" + apiKey + "/" + lati + ", " + longi;

        if (isNetworkAvailable()) {
            toggleRefresh();

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(forecastUrl).build();

            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toggleRefresh();
                        }
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toggleRefresh();
                        }
                    });

                    try {
                        String jsonData = response.body().string();
                        Log.v(TAG, jsonData);
                        if (response.isSuccessful()) {
                            mForecast = parseForecastDetails(jsonData);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    updateDisplay();
                                }
                            });
                        } else {
                            alertUserAboutError();
                        }
                    }
                    catch (IOException e) {
                        Log.e(TAG, "Exception caught: ", e);
                    }
                    catch (JSONException e) {
                        Log.e(TAG, "Exception caught: ", e);
                    }
                }
            });
        }
        else {
            Toast.makeText(this, R.string.network_unavailable_message, Toast.LENGTH_LONG).show();
        }
    }

    private void toggleRefresh() {
        if (mProgressBar.getVisibility() == View.INVISIBLE) {
            mProgressBar.setVisibility(View.VISIBLE);
            mRefreshImageView.setVisibility(View.INVISIBLE);
        }
        else {
            mProgressBar.setVisibility(View.INVISIBLE);
            mRefreshImageView.setVisibility(View.VISIBLE);
        }
    }

    private void updateDisplay() {
        Current mCurrent = mForecast.getCurrent();
        mTemperatureLabel.setText(mCurrent.getmTemperature() + "");
        mTimeLabel.setText("As of " + mCurrent.getFormattedTime() + " it is:");
        mHumidityValue.setText(mCurrent.getmHumidity() + "");
        mPrecipValue.setText(mCurrent.getmPrecipChance() + "%");
        mSummaryLabel.setText(mCurrent.getmSummary());
        mIconImageView.setImageResource(mCurrent.getIconId());
        try {
            if (mPlace != null) {
                mLocationLabel.setText(mPlace.getName());
            }
            else {
                mLocationLabel.setText(getCity());
            }
        }catch (ArrayIndexOutOfBoundsException oob) {
                mLocationLabel.setText("Name Unavailable");
                Toast.makeText(this, "Weather data has been retrieved, however location name was unattainable",
                        Toast.LENGTH_LONG).show();
        }
        mWindspeedValue.setText(mCurrent.getmWindSpeed() + "");
        mVisibilityValue.setText(mCurrent.getmVisibility() + "");
        mMainLayout.setBackgroundColor(getBackgroundColor());

    }

    private int getBackgroundColor() {
        String color;
        String icon = mForecast.getCurrent().getmIcon();
        switch (icon) {
            case "clear-night":
                color = "#2e4482";
                break;
            case "rain":
                color = "#87889c";
                break;
            case "snow":
                color = "#B8B4B4";
                break;
            case "sleet":
                color = "#B8B4B4";
                break;
            case "fog":
                color = "#87889c";
                break;
            case "cloudy":
                color = "#87889c";
                break;
            case "partly-cloudy-night":
                color = "#2e4482";
                break;
            default:
                color = "#FFFC970B";
        }
        int colorAsInt = Color.parseColor(color);

        return colorAsInt;
    }

    private Forecast parseForecastDetails(String jsonData) throws JSONException {
        Forecast forecast = new Forecast();

        forecast.setCurrent(getCurrentDetails(jsonData));
        forecast.setHourlyForecast(getHourlyForecast(jsonData));
        forecast.setDailyForecast(getDailyForecast(jsonData));

        return forecast;
    }

    private Day[] getDailyForecast(String jsonData) throws JSONException {
        JSONObject forecast = new JSONObject(jsonData);
        String timezone = forecast.getString("timezone");
        JSONObject daily = forecast.getJSONObject("daily");
        JSONArray data = daily.getJSONArray("data");

        Day[] dayArr = new Day[data.length()];

        for (int i = 0; i < data.length(); i++) {
            JSONObject dayObject = data.getJSONObject(i);
            Day day = new Day();
            day.setTemperatureMax(dayObject.getDouble("temperatureMax"));
            day.setSummary(dayObject.getString("summary"));
            day.setIcon(dayObject.getString("icon"));
            day.setTime(dayObject.getLong("time"));
            day.setTimeZone(timezone);

            dayArr[i] = day;
        }

        return dayArr;
    }

    private Hour[] getHourlyForecast(String jsonData) throws JSONException {
        JSONObject forecast = new JSONObject(jsonData);
        String timezone = forecast.getString("timezone");
        JSONObject hourly = forecast.getJSONObject("hourly");
        JSONArray data = hourly.getJSONArray("data");

        Hour[] hourArr = new Hour[data.length()];

        for (int i = 0; i < data.length(); i++) {
            Hour hour = new Hour();
            JSONObject hourObject = data.getJSONObject(i);
            hour.setTime(hourObject.getLong("time"));
            hour.setSummary(hourObject.getString("summary"));
            hour.setTemperature(hourObject.getDouble("temperature"));
            hour.setIcon(hourObject.getString("icon"));
            hour.setTimeZone(timezone);

            hourArr[i] = hour;
        }

        return hourArr;
    }

    private Current getCurrentDetails(String jsonData) throws JSONException {
        JSONObject forecast = new JSONObject(jsonData);
        String timezone = forecast.getString("timezone");
        Log.i(TAG, "From JSON: " + timezone);

        JSONObject currently = forecast.getJSONObject("currently");

        Current current = new Current();
        current.setmIcon(currently.getString("icon"));
        current.setmTime(currently.getLong("time"));
        current.setmTemperature(currently.getDouble("temperature"));
        current.setmHumidity(currently.getDouble("humidity"));
        current.setmPrecipChance(currently.getDouble("precipProbability"));
        current.setmSummary(currently.getString("summary"));
        current.setmTimeZone(timezone);
        current.setmWindSpeed(currently.getDouble("windSpeed"));
        current.setmVisibility(currently.getDouble("visibility"));

        Log.d(TAG, current.getFormattedTime());

        return current;
}

    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;

        if (networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        }

        return isAvailable;
    }

    private void alertUserAboutError() {
        AlertDialogFragment dialog = new AlertDialogFragment();
        dialog.show(getFragmentManager(), "error_dialog");
    }

    @OnClick (R.id.dailyButton)
    public void startDailyActivity(View view) {
        Intent intent = new Intent(this, DailyForecastActivity.class);
        intent.putExtra(DAILY_FORECAST, mForecast.getDailyForecast());
        intent.putExtra("background_color", getBackgroundColor());

        startActivity(intent);
    }

    @OnClick (R.id.hourlyButton)
    public void startHourlyActivity(View view) {
        Intent intent = new Intent(this, HourlyForecastActivity.class);
        intent.putExtra(HOURLY_FORECAST, mForecast.getHourlyForecast());
        intent.putExtra("background_color", getBackgroundColor());

        startActivity(intent);
    }

    @OnClick (R.id.editLocationButton)
    public void startPlacePicker() {
        int PLACE_PICKER_REQUEST = 1;

        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES)
                .build();

        try {
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                    .setFilter(typeFilter).build(this);
            startActivityForResult(intent, PLACE_PICKER_REQUEST);
        } catch(GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                mPlace = PlaceAutocomplete.getPlace(this, data);
                Log.i(TAG, "Place: " + mPlace.getName());

                latitude = mPlace.getLatLng().latitude;
                longitude = mPlace.getLatLng().longitude;
                getForecast(latitude, longitude);

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                Toast.makeText(this, "Please select another location", Toast.LENGTH_LONG).show();
                Log.i(TAG, status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }
}
























