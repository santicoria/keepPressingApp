package com.game.keeppressing;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

public class MainActivity extends AppCompatActivity {
    private InterstitialAd mInterstitialAd;
    private TextView record;
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String TIME = "time";
    Button restartBtn;
    Button mainMenuBtn;
    private TextView timer;
    private String currentRecord;
    private LinearLayout fullLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageButton goBack = findViewById(R.id.goBack);

        Chronometer chronometer = findViewById(R.id.chronometer);
        record = (TextView) findViewById(R.id.record);

        fullLayout = (LinearLayout) findViewById(R.id.fullLayout);

        View endGame = LayoutInflater.from(MainActivity.this).inflate(R.layout.end_game_dialog, null);
        AlertDialog.Builder endGameDialog = new AlertDialog.Builder(MainActivity.this);
        endGameDialog.setView(endGame);
        restartBtn = endGame.findViewById(R.id.restartBtn);
        mainMenuBtn = endGame.findViewById(R.id.mainMenuBtn);
        TextView winnerName = endGame.findViewById(R.id.winnerName);

        timer = (TextView) endGame.findViewById(R.id.lastTime);

        final AlertDialog dialog = endGameDialog.create();

        final ImageButton greenBtn = findViewById(R.id.greenButton);

        restartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                startActivity(intent);
                if (mInterstitialAd != null) {
                    mInterstitialAd.show(MainActivity.this);
                } else {
                    Log.d("TAG", "The interstitial ad wasn't ready yet.");
                }
            }
        });

        mainMenuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MainMenu.class);
                startActivity(intent);
                if (mInterstitialAd != null) {
                    mInterstitialAd.show(MainActivity.this);
                } else {
                    Log.d("TAG", "The interstitial ad wasn't ready yet.");
                }
            }
        });

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        InterstitialAd.load(this,"ca-app-pub-1456323163845853/8463848721", adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                        Log.i(TAG, "onAdLoaded");
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.d(TAG, loadAdError.toString());
                        mInterstitialAd = null;
                    }
                });

        greenBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    fullLayout.setBackgroundColor(Color.RED);
                    chronometer.setBase(SystemClock.elapsedRealtime());
                    chronometer.start();
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    chronometer.stop();
                    long durationInMillis = SystemClock.elapsedRealtime() - chronometer.getBase();
                    if(durationInMillis > Long.parseLong(currentRecord)){
                        saveRecord(durationInMillis);
                        winnerName.setText("NEW RECORD!");
                        timer.setText(timeFormater(durationInMillis));
                    }
                    else {
                        winnerName.setText("Nice Try!");
                        timer.setText(timeFormater(durationInMillis));
                    }
                    fullLayout.setBackgroundResource(R.drawable.background);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.show();
                }
                return true;
            }
        });

        loadData();
        updateData();

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MainMenu.class);
                startActivity(intent);
                if (mInterstitialAd != null) {
                    mInterstitialAd.show(MainActivity.this);
                } else {
                    Log.d("TAG", "The interstitial ad wasn't ready yet.");
                }
            }
        });

    }

    public String timeFormater(Long durationInMillis) {
        long second = (durationInMillis / 1000) % 60;
        long minute = (durationInMillis / (1000 * 60)) % 60;
        long hour = (durationInMillis / (1000 * 60 * 60)) % 24;
        long days = (durationInMillis / (1000 * 60 * 60 * 24)) % 365;
        return String.format("%02dD : %02dH : %02dM : %02dS", days, hour, minute, second);
    }

    public void saveRecord(long newRecord) {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(TIME, Long.toString(newRecord));
        editor.apply();
    }

    public void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        currentRecord = sharedPreferences.getString(TIME, "0");
    }

    public void updateData() {
        record.setText(timeFormater(Long.parseLong(currentRecord)));
    }

}