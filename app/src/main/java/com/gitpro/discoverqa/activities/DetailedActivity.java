package com.gitpro.discoverqa.activities;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.viewpager.widget.ViewPager;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.bitvale.switcher.SwitcherX;
import com.gitpro.discoverqa.adapters.ViewPagerAdapter;
import com.gitpro.discoverqa.networks.OpenWeatherApi;
import com.gitpro.discoverqa.models.Place;
import com.gitpro.discoverqa.R;
import com.gitpro.discoverqa.models.Sharedpreference;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.tiper.MaterialSpinner;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class DetailedActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener, View.OnTouchListener {
    private static final String TAG = "DetailedActivity";
    private GoogleMap mMap;
    ViewPager viewPager;
    List<String> imageUrls;
    List<Place> mPlace;
    ViewPagerAdapter adapter;
    double latitude, longitude;
    String placeName;
    MediaPlayer mp;
    AlertDialog alertDialog;
    Button cancel_temp_dialog_btn, cancel_airquality_dialog_btn;
    TextView min_temp, max_temp, humidity_tv, wind_tv;
    private final String APIKEY = "4e4480d5039580a36c576fa58a0c1d3a";
    private OpenWeatherApi openWeatherApi;
    private double tempApiResult, min, max, speed;
    private int humidity;
    ImageButton zoom_in, zoom_out;
    View map_view, tempView, qualityView, internetView;
    private Toolbar mToolbar;
    private TextView mTextView;
    ScrollView scrollView;
    SupportMapFragment mapFragment;
    FrameLayout seasonFlip, timeToGoFlip, estimationFlip, ageFlip;
    TextView seasonTv, timeToGoTv, ageTv1, ageTv2, estimationTv, costTv, tempTv, tempOverallTv, airQualityTv, internetTv, placeNameInfo, placeNameRecommendations, placeNameLocation, description, placeNameTitle, entrenceFeesPrice, foodPrice, transportationPrice, nearbyTv;
    RoundCornerProgressBar costProgressBar, tempProgressBar, airQualityProgressBar, internetProgressBar;
    View frontLayoutSeason, backLayoutSeason, frontLayoutTime, backLayouTime, frontLayoutAge, backLayoutAge, frontLayoutEstimated, backLayoutEstimated;
    ImageView seasonImg, timeToGoImg, estimationImg, costDetails;
    private AnimatorSet mSetRightOut;
    private AnimatorSet mSetLeftIn;
    private boolean[] mIsBackVisible = {false, false, false, false};
    int i = 1, size;
    public static Sharedpreference prefs;
    String Tour_id;
    int index = 1;
    SwitcherX foodCheckbox, entrenceFeesCheckbox, transportationCheckbox, sleepCheckBox;
    String Parent_Key;
    double lat;
    double lng;
    MaterialSpinner overNightSpinner;
    Button applyBtn, cancelBtn, cancel_internet_dialog_btn;
    int PublicPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed);
        fillView();
        ActionBarChecker();
        ApiConfig();
        loadAnimations();
        setFirstPlaceDetails();
    }

    private void showClarificationDialog() {
        Button cancel_clarification_dialog_btn;
        final CheckBox dont_show_again;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View mView = getLayoutInflater().inflate(R.layout.clarification_dialog,null);
        builder.setView(mView);
        cancel_clarification_dialog_btn = mView.findViewById(R.id.cancel_clarification_dialog);
        dont_show_again = mView.findViewById(R.id.clarification_checkBox);
        alertDialog = builder.create();
        alertDialog.show();
        cancel_clarification_dialog_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dont_show_again.isChecked()) {
                    prefs.setboolPrefs("clarification_dialog", true);
                } else {
                    prefs.setboolPrefs("clarification_dialog", false);
                }
                alertDialog.cancel();
            }
        });
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }


    public void showDetailedCost(final int position) {

        overNightSpinner.setSelection(prefs.getintPrefs("sleep", 1));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (SplashScreenActivity.lan.equals("ar"))
                overNightSpinner.setTextDirection(View.TEXT_DIRECTION_RTL);
            else if (SplashScreenActivity.lan.equals("en"))
                overNightSpinner.setTextDirection(View.TEXT_DIRECTION_LTR);
        }

        overNightSpinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(@NotNull MaterialSpinner materialSpinner, @Nullable View view, int i, long l) {
                index = i;
            }

            @Override
            public void onNothingSelected(@NotNull MaterialSpinner materialSpinner) {
                index = 1;
            }
        });

        if (!sleepCheckBox.isChecked()) {
            overNightSpinner.setVisibility(View.GONE);
        } else {
            overNightSpinner.setVisibility(View.VISIBLE);
        }
        sleepCheckBox.setOnCheckedChangeListener(new Function1<Boolean, Unit>() {
            @Override
            public Unit invoke(Boolean checked) {


                if (checked) {
                    overNightSpinner.setVisibility(View.VISIBLE);
                } else {

                    overNightSpinner.setVisibility(View.GONE);
                }
                return null;
            }
        });


        alertDialog.dismiss();
        prefs.setboolPrefs("trans", transportationCheckbox.isChecked());
        prefs.setboolPrefs("food", foodCheckbox.isChecked());
        prefs.setboolPrefs("enterence", entrenceFeesCheckbox.isChecked());
        prefs.setintPrefs("sleep", index);
        prefs.setboolPrefs("sleep_checkbox", sleepCheckBox.isChecked());


        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(alertDialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        alertDialog.show();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    public void temp_btn(View view) {

        Button cancel_temp_dialog_btn;
        TextView min_temp, max_temp, humidity_tv, wind_tv;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View mView = getLayoutInflater().inflate(R.layout.temperature_popup, null);
        builder.setView(mView);
        cancel_temp_dialog_btn = mView.findViewById(R.id.cancel_temp_dialog);
        max_temp = mView.findViewById(R.id.max_temp);
        min_temp = mView.findViewById(R.id.min_temp);
        humidity_tv = mView.findViewById(R.id.humidity_tv);
        wind_tv = mView.findViewById(R.id.wind_tv);
        if (tempView.getParent() != null) {
            ((ViewGroup) tempView.getParent()).removeView(tempView);
        }
        builder.setView(tempView);
        max_temp.setText((int) max + "");
        min_temp.setText((int) min + "");
        humidity_tv.setText(humidity + "");
        wind_tv.setText((int) speed + "");
        alertDialog = builder.create();
        alertDialog.show();
        cancel_temp_dialog_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.cancel();
            }
        });
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    public void quality_btn(View view) {
        Button cancel_airquality_dialog_btn;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View mView = getLayoutInflater().inflate(R.layout.airquality_popup, null);
        builder.setView(mView);
        cancel_airquality_dialog_btn = mView.findViewById(R.id.cancel_airquality_dialog);
        alertDialog = builder.create();
        alertDialog.show();
        cancel_airquality_dialog_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.cancel();
            }
        });
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    String place_id(String name) {
        name = name.replaceAll("\\s+", "");
        return name.toLowerCase();
    }

    public void internet_btn(View view) {
        Button cancel_internet_dialog_btn;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View mView = getLayoutInflater().inflate(R.layout.internet_popup, null);
        builder.setView(mView);
        cancel_internet_dialog_btn = mView.findViewById(R.id.cancel_internet_dialog);
        alertDialog = builder.create();
        alertDialog.show();
        cancel_internet_dialog_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.cancel();
            }
        });
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
    }


    private void getTempApi(double latitude, double longitude) {
        Call<JsonObject> call = openWeatherApi.getTemp(latitude, longitude, APIKEY);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if (!response.isSuccessful()) {
                    Log.d(TAG, "Code: " + response.code());
                    return;
                }
                JsonObject root = response.body();
                JsonObject main = root.getAsJsonObject("main");
                JsonElement element_temp = main.get("temp");
                JsonElement element_min_temp = main.get("temp_min");
                JsonElement element_max_temp = main.get("temp_max");
                JsonElement element_humidity = main.get("humidity");

                JsonObject wind = root.getAsJsonObject("wind");
                JsonElement element_speed = wind.get("speed");

                tempApiResult = element_temp.getAsDouble() - 273.15;
                min = element_min_temp.getAsDouble() - 273.15;
                max = element_max_temp.getAsDouble() - 273.15;
                humidity = element_humidity.getAsInt();
                speed = element_speed.getAsDouble() * 3.6;
                setTempProgress((int) tempApiResult);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });

    }

    // send Lat and Lng to PlacesActivity.
    public void place_btn(View view) {
        Intent intent = new Intent(this, PlacesActivity.class);
        intent.putExtra("lat", lat);
        intent.putExtra("lng", lng);
        startActivity(intent);
    }

    // update Lat and Lng for each place.
    public void updateLocation(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }


    public void pageSelectedPlace(int position) {
        Parent_Key = Tour_id + place_id(mPlace.get(position).nameEN);
        prefs = new Sharedpreference(DetailedActivity.this, Parent_Key);
        setSeason(mPlace.get(position).recommendedSeason);
        getTempApi(mPlace.get(position).latitude, mPlace.get(position).longitude);
        setAirQualityProgress(mPlace.get(position).airQuality);
        setInternetProgress(mPlace.get(position).internet);
        setTimeToGo(mPlace.get(position).recommendedTime);
        setAge(mPlace.get(position).recommendedAge);
        setEstimatedTime(mPlace.get(position).estimatedTime + prefs.getintPrefs("act_time", 0));
        if (SplashScreenActivity.lan.equalsIgnoreCase("ar")) {
            description.setText(mPlace.get(position).descAR);
            placeNameRecommendations.setText(mPlace.get(position).nameAR);
            placeNameInfo.setText(mPlace.get(position).nameAR);
            placeNameLocation.setText(mPlace.get(position).nameAR);
            placeNameTitle.setText(mPlace.get(position).nameAR);
        } else {
            description.setText(mPlace.get(position).descEN);
            placeNameRecommendations.setText(mPlace.get(position).nameEN);
            placeNameInfo.setText(mPlace.get(position).nameEN);
            placeNameLocation.setText(mPlace.get(position).nameEN);
            placeNameTitle.setText(mPlace.get(position).nameEN);
        }

        latitude = mPlace.get(position).latitude;
        longitude = mPlace.get(position).longitude;
        placeName = mPlace.get(position).nameEN;
        mapFragment.getMapAsync(DetailedActivity.this);

        scrollView.fullScroll(View.FOCUS_UP);
        description.scrollTo(0, 0);
    }

    private void run_viewPager() {
        imageUrls = new ArrayList<>();

        for (int i = 0; i < mPlace.size(); i++) {
            imageUrls.add(mPlace.get(i).imageURL);
        }

        adapter = new ViewPagerAdapter(imageUrls, this);
        viewPager = findViewById(R.id.ViewPager);
        viewPager.setAdapter(adapter);
        viewPager.setPadding(100, 0, 100, 0);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(final int position) {

                Parent_Key = Tour_id + place_id(mPlace.get(position).nameEN);
                prefs = new Sharedpreference(DetailedActivity.this, Parent_Key);
                setSeason(mPlace.get(position).recommendedSeason);
                getTempApi(mPlace.get(position).latitude, mPlace.get(position).longitude);
                updateLocation(mPlace.get(position).latitude, mPlace.get(position).longitude);
                setAirQualityProgress(mPlace.get(position).airQuality);
                setInternetProgress(mPlace.get(position).internet);
                setTimeToGo(mPlace.get(position).recommendedTime);
                setAge(mPlace.get(position).recommendedAge);
                setEstimatedTime(mPlace.get(position).estimatedTime + prefs.getintPrefs("act_time", 0));

                if (SplashScreenActivity.lan.equalsIgnoreCase("ar")) {
                    description.setText(mPlace.get(position).descAR);
                    placeNameRecommendations.setText(mPlace.get(position).nameAR);
                    placeNameInfo.setText(mPlace.get(position).nameAR);
                    placeNameLocation.setText(mPlace.get(position).nameAR);
                    placeNameTitle.setText(mPlace.get(position).nameAR);
                } else {
                    description.setText(mPlace.get(position).descEN);
                    placeNameRecommendations.setText(mPlace.get(position).nameEN);
                    placeNameInfo.setText(mPlace.get(position).nameEN);
                    placeNameLocation.setText(mPlace.get(position).nameEN);
                    placeNameTitle.setText(mPlace.get(position).nameEN);
                }

                latitude = mPlace.get(position).latitude;
                longitude = mPlace.get(position).longitude;
                placeName = mPlace.get(position).nameEN;
                mapFragment.getMapAsync(DetailedActivity.this);

                scrollView.fullScroll(View.FOCUS_UP);
                description.scrollTo(0, 0);

                pageSelectedPlace(position);

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                System.out.println(state);
            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng googleMapPlace = new LatLng(latitude, longitude);
        MarkerOptions my_own_marker = new MarkerOptions().position(googleMapPlace).title(placeName);
        my_own_marker.icon((getBitmapDescriptor(R.drawable.ic_pin)));
        mMap.addMarker(my_own_marker);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(googleMapPlace, 16.0f));
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);
        //mMap.animateCamera(CameraUpdateFactory.zoomOut());
    }

    private BitmapDescriptor getBitmapDescriptor(@DrawableRes int id) {
        Drawable vectorDrawable = ResourcesCompat.getDrawable(getResources(), id, null);
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }



    private void setTempProgress(int temp) {

        if (temp <= 10) {
            tempProgressBar.setProgressColor(Color.RED);
            tempProgressBar.setProgress(temp);
            tempOverallTv.setText(getResources().getString(R.string.cold));
            tempTv.setText(temp + "");

        } else if (temp > 10 && temp <= 18) {
            tempProgressBar.setProgress(temp);
            tempProgressBar.setProgressColor(Color.rgb(255, 165, 0));
            tempOverallTv.setText(getResources().getString(R.string.normal));
            tempTv.setText(temp + "");
        } else if (temp > 18 && temp <= 30) {
            tempProgressBar.setProgressColor(Color.GREEN);
            tempProgressBar.setProgress(55);
            tempOverallTv.setText(getResources().getString(R.string.perfect));
            tempTv.setText(temp + "");
        } else {
            tempProgressBar.setProgressColor(Color.RED);
            tempProgressBar.setProgress(55 - temp);
            tempOverallTv.setText(getResources().getString(R.string.hot));
            tempTv.setText(temp + "");
        }

        ObjectAnimator progressAnimator;
        progressAnimator = ObjectAnimator.ofFloat(tempProgressBar, "progress", 0.0f, tempProgressBar.getProgress());
        progressAnimator.setDuration(1000);
        progressAnimator.setStartDelay(300);
        progressAnimator.start();

    }

    private void setAirQualityProgress(int airQuality) {
        airQualityTv.setText(airQuality + " " + "\u00B5" + "g/m3");

        airQualityProgressBar.setProgress(100 - airQuality);
        if (airQuality <= 25) {
            airQualityProgressBar.setProgressColor(Color.GREEN);
        } else if (airQuality > 25 && airQuality <= 50) {
            airQualityProgressBar.setProgressColor(Color.rgb(255, 165, 0));
        } else {
            airQualityProgressBar.setProgressColor(Color.RED);

        }

        ObjectAnimator progressAnimator;
        progressAnimator = ObjectAnimator.ofFloat(airQualityProgressBar, "progress", 0.0f, airQualityProgressBar.getProgress());
        progressAnimator.setDuration(1000);
        progressAnimator.setStartDelay(300);
        progressAnimator.start();
    }

    private void setInternetProgress(int internet) {
        ObjectAnimator progressAnimator;

        switch (internet) {
            case 0:
                internetProgressBar.setProgressColor(Color.RED);
                internetProgressBar.setProgress(25);
                internetTv.setText(getResources().getString(R.string.bad_internet));
                break;

            case 1:
                internetProgressBar.setProgressColor(Color.rgb(255, 165, 0));
                internetProgressBar.setProgress(50);
                internetTv.setText(getResources().getString(R.string.okay_internet));
                break;
            case 2:
                internetProgressBar.setProgressColor(Color.rgb(255, 215, 0));
                internetProgressBar.setProgress(75);
                internetTv.setText(getResources().getString(R.string.good_internet));
                break;
            case 3:
                internetProgressBar.setProgressColor(Color.GREEN);
                internetProgressBar.setProgress(100);
                internetTv.setText(getResources().getString(R.string.great_internet));
                break;

        }

        progressAnimator = ObjectAnimator.ofFloat(internetProgressBar, "progress", 0.0f, internetProgressBar.getProgress());
        progressAnimator.setDuration(1000);
        progressAnimator.setStartDelay(300);
        progressAnimator.start();
    }

    private void setSeason(int season) {
        changeCameraDistance(frontLayoutSeason, backLayoutSeason);
        switch (season) {
            case 0:
                seasonTv.setText(getResources().getString(R.string.summer_season));
                seasonImg.setImageResource(R.drawable.ic_summer);
                break;

            case 1:
                seasonTv.setText(getResources().getString(R.string.winter_season));
                seasonImg.setImageResource(R.drawable.ic_winter);
                break;

            case 2:
                seasonTv.setText(getResources().getString(R.string.spring_season));
                seasonImg.setImageResource(R.drawable.ic_spring);
                break;
            case 3:
                seasonTv.setText(getResources().getString(R.string.autumn_season));
                seasonImg.setImageResource(R.drawable.ic_autumn);
                break;
        }
    }

    private void setTimeToGo(Date time) {

        changeCameraDistance(frontLayoutTime, backLayouTime);

if(time.getTime()==0){
    timeToGoTv.setText("Day");
}
else if(time.getTime()==1){
    timeToGoTv.setText("Night");
}
        timeToGoImg.setImageResource(R.drawable.ic_day_and_night);
    }

    private void setAge(String age) {

        ageTv2.setTextSize(20);
        ageTv1.setTextSize(20);
        ageTv1.setText(age);
        ageTv2.setText(age);

        changeCameraDistance(frontLayoutAge, backLayoutAge);

    }

    private void setEstimatedTime(int estimatedTime) {

        estimationTv.setText(estimatedTime + "Hrs");
        estimationImg.setImageResource(R.drawable.ic_stopwatch);

        changeCameraDistance(frontLayoutEstimated, backLayoutEstimated);

    }

    public void flipCard(View front, View back, boolean mIsBackVisible[], int position) {
        if (!mIsBackVisible[position]) {
            mSetRightOut.setTarget(front);
            mSetLeftIn.setTarget(back);
            mSetRightOut.start();
            mSetLeftIn.start();
            mIsBackVisible[position] = true;
        } else {
            mSetRightOut.setTarget(back);
            mSetLeftIn.setTarget(front);
            mSetRightOut.start();
            mSetLeftIn.start();
            mIsBackVisible[position] = false;
        }
    }

    private void changeCameraDistance(View front, View back) {
        int distance = 8000;
        float scale = getResources().getDisplayMetrics().density * distance;
        front.setCameraDistance(scale);
        back.setCameraDistance(scale);
    }

    private void loadAnimations() {
        mSetRightOut = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.out_animation);
        mSetLeftIn = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.in_animation);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item);
    }

    // calculate the cost of transportation using the yellow taxi day charging rate
    private int calculateTransportationCost(int distance, int duration) {
        int cost = 250; // starter fee in fils
        cost += (distance / 100) * 24; // 24 fils per 100 meters
        cost += duration * 30; // 30 fils per 1 minute
        System.out.println(cost);

        return cost / 1000; // return cost in JD
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.cancel_temp_dialog:
            case R.id.cancel_airquality_dialog:
                alertDialog.dismiss();
                break;
            case R.id.cancel_internet_dialog:
                alertDialog.cancel();
                break;
            case R.id.zoomin:
                mMap.animateCamera(CameraUpdateFactory.zoomIn());
                break;
            case R.id.zoomout:
                mMap.animateCamera(CameraUpdateFactory.zoomOut());
                break;
            case R.id.season_btn:
                flipCard(frontLayoutSeason, backLayoutSeason, mIsBackVisible, 0);
                break;
            case R.id.time_btn:
                flipCard(frontLayoutTime, backLayouTime, mIsBackVisible, 1);
                break;
            case R.id.age_btn:
                flipCard(frontLayoutAge, backLayoutAge, mIsBackVisible, 2);
                break;
            case R.id.estimated_btn:
                flipCard(frontLayoutEstimated, backLayoutEstimated, mIsBackVisible, 3);
                break;

        }
    }


    public void fillView() {
        tempView = getLayoutInflater().inflate(R.layout.temperature_popup, null);
        qualityView = getLayoutInflater().inflate(R.layout.airquality_popup, null);
        internetView = getLayoutInflater().inflate(R.layout.internet_popup, null);

        cancel_temp_dialog_btn = tempView.findViewById(R.id.cancel_temp_dialog);
        cancel_temp_dialog_btn.setOnClickListener(this);
        max_temp = tempView.findViewById(R.id.max_temp);
        min_temp = tempView.findViewById(R.id.min_temp);
        humidity_tv = tempView.findViewById(R.id.humidity_tv);
        wind_tv = tempView.findViewById(R.id.wind_tv);
        cancel_airquality_dialog_btn = qualityView.findViewById(R.id.cancel_airquality_dialog);
        cancel_airquality_dialog_btn.setOnClickListener(this);
        cancel_temp_dialog_btn.setOnClickListener(this);
        mToolbar = findViewById(R.id.detailed_toolbar);
        mTextView = findViewById(R.id.toolbar_title);
        placeNameRecommendations = findViewById(R.id.place_name_recommendations);
        placeNameInfo = findViewById(R.id.place_name_information_about);
        placeNameLocation = findViewById(R.id.place_name_location);
        zoom_in = findViewById(R.id.zoomin);
        zoom_out = findViewById(R.id.zoomout);
        zoom_in.setOnClickListener(this);
        zoom_out.setOnClickListener(this);
        description = findViewById(R.id.description_tv);
        description.setOnTouchListener(this);
        scrollView = findViewById(R.id.scrollview);
        scrollView.setOnTouchListener(this);
        placeNameTitle = findViewById(R.id.place_name_title);
        cancel_internet_dialog_btn = internetView.findViewById(R.id.cancel_internet_dialog);
        cancel_internet_dialog_btn.setOnClickListener(this);
        viewPager = findViewById(R.id.ViewPager);
        frontLayoutTime = findViewById(R.id.front_time);
        backLayouTime = findViewById(R.id.back_time);
        timeToGoTv = backLayouTime.findViewById(R.id.back_text);
        timeToGoImg = frontLayoutTime.findViewById(R.id.front_icon);
        frontLayoutAge = findViewById(R.id.front_ag);
        backLayoutAge = findViewById(R.id.back_age);
        ageTv1 = backLayoutAge.findViewById(R.id.back_text);
        ageTv2 = frontLayoutAge.findViewById(R.id.back_text);
        frontLayoutEstimated = findViewById(R.id.front_estimated);
        backLayoutEstimated = findViewById(R.id.back_estimated);
        estimationTv = backLayoutEstimated.findViewById(R.id.back_text);
        estimationImg = frontLayoutEstimated.findViewById(R.id.front_icon);


        //information about the place deceleration

        tempProgressBar = findViewById(R.id.temp_progress);
        airQualityProgressBar = findViewById(R.id.air_quality_progress);
        internetProgressBar = findViewById(R.id.internet_progress);
        tempTv = findViewById(R.id.temp_tv);
        tempOverallTv = findViewById(R.id.temp_overall_tv);
        airQualityTv = findViewById(R.id.air_quality_tv);
        internetTv = findViewById(R.id.internet_tv);

        //recommendations about the place deceleration
        seasonFlip = findViewById(R.id.season_btn);
        seasonFlip.setOnClickListener(this);
        frontLayoutSeason = seasonFlip.findViewById(R.id.front_season);
        backLayoutSeason = seasonFlip.findViewById(R.id.back_season);
        seasonTv = backLayoutSeason.findViewById(R.id.back_text);
        seasonImg = frontLayoutSeason.findViewById(R.id.front_icon);
        timeToGoFlip = findViewById(R.id.time_btn);
        timeToGoFlip.setOnClickListener(this);
        ageFlip = findViewById(R.id.age_btn);
        ageFlip.setOnClickListener(this);
        estimationFlip = findViewById(R.id.estimated_btn);
        estimationFlip.setOnClickListener(this);

        //progress details deceleration
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        //clarification_dialog
        prefs = new Sharedpreference(this, Parent_Key);
        if (!prefs.getboolPrefs("clarification_dialog", false)) {
            showClarificationDialog();
        }

        nearbyTv = findViewById(R.id.nearby_tv);
        if(nearbyTv.getText().toString().length()!=0){
            nearbyTv.setText(getResources().getString(R.string.nearby)+" ");
        }
    }

    public void ActionBarChecker() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(null);
        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        mTextView.setText(R.string.detailed_activity_title);
    }

    public void ApiConfig() {
        //retrofit config
        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://api.openweathermap.org/").addConverterFactory(GsonConverterFactory.create()).build();
        openWeatherApi = retrofit.create(OpenWeatherApi.class);
    }

    public void setFirstPlaceDetails() {

        Intent i = getIntent();
        mPlace = (List<Place>) i.getSerializableExtra("places");
        Tour_id = i.getStringExtra("tour_id");
        Parent_Key = Tour_id + place_id(mPlace.get(0).nameEN);
        description.setMovementMethod(new ScrollingMovementMethod());
        latitude = mPlace.get(0).latitude;
        longitude = mPlace.get(0).longitude;
        placeName = mPlace.get(0).nameEN;
        map_view = mapFragment.getView();
        mapFragment.getMapAsync(this);
        run_viewPager();
      //
        getTempApi(mPlace.get(0).latitude, mPlace.get(0).longitude);
        setAirQualityProgress(mPlace.get(0).airQuality);
        setInternetProgress(mPlace.get(0).internet);
        setSeason(mPlace.get(0).recommendedSeason);
        setAge(mPlace.get(0).recommendedAge);
        setEstimatedTime(mPlace.get(0).estimatedTime + prefs.getintPrefs("act_time", 0));
      //
      //



        if (SplashScreenActivity.lan.equalsIgnoreCase("ar")) {
            description.setText(mPlace.get(0).descAR);
            placeNameRecommendations.setText(mPlace.get(0).nameAR);
            placeNameInfo.setText(mPlace.get(0).nameAR);
            placeNameLocation.setText(mPlace.get(0).nameAR);
            placeNameTitle.setText(mPlace.get(0).nameAR);
        } else {
            placeNameTitle.setText(mPlace.get(0).nameEN);
            description.setText(mPlace.get(0).descEN);
            placeNameRecommendations.setText(mPlace.get(0).nameEN);
            placeNameInfo.setText(mPlace.get(0).nameEN);
            placeNameLocation.setText(mPlace.get(0).nameEN);
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (view.getId()) {
            case R.id.scrollview:
                description.getParent().requestDisallowInterceptTouchEvent(false);
                break;
            case R.id.description_tv:
                description.getParent().requestDisallowInterceptTouchEvent(true);
                break;
        }
        return false;
    }


}