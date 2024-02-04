package com.game.keeppressing;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

public class MultiplayerActivity extends AppCompatActivity {

    Boolean playerOneReady = false;
    Boolean playerTwoReady = false;
    Boolean gameOver = false;
    AlertDialog dialog;
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayer);

        Chronometer chronometer = findViewById(R.id.chronometer);
        ConstraintLayout fullLayout = findViewById(R.id.fullLayout);

        View endGame = LayoutInflater.from(MultiplayerActivity.this).inflate(R.layout.end_game_dialog, null);
        AlertDialog.Builder endGameDialog = new AlertDialog.Builder(MultiplayerActivity.this);
        endGameDialog.setView(endGame);
        Button restartBtn = endGame.findViewById(R.id.restartBtn);
        Button mainMenuBtn = endGame.findViewById(R.id.mainMenuBtn);
        TextView winnerName = endGame.findViewById(R.id.winnerName);
        TextView timer = endGame.findViewById(R.id.lastTime);

        dialog = endGameDialog.create();

        View playerOneBtn = findViewById(R.id.playerOneBtn);
        View playerTwoBtn = findViewById(R.id.playerTwoBtn);

        TextView playerOneName = findViewById(R.id.playerOneName);
        TextView playerTwoName = findViewById(R.id.playerTwoName);

        final String getPlayerOneName = getIntent().getStringExtra("playerOne");
        final String getPlayerTwoName = getIntent().getStringExtra("playerTwo");

        playerOneName.setText(getPlayerOneName);
        playerTwoName.setText(getPlayerTwoName);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        InterstitialAd.load(this,"ca-app-pub-1456323163845853/2421184553", adRequest,
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

        playerOneBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    fullLayout.setBackgroundColor(Color.GREEN);
                    playerOneReady = true;
                    playerTwoBtn.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            if(event.getAction() == MotionEvent.ACTION_DOWN){
                                fullLayout.setBackgroundColor(Color.GREEN);
                                playerTwoReady = true;
                                if (playerTwoReady && playerOneReady){
                                    fullLayout.setBackgroundColor(Color.RED);
                                    chronometer.setBase(SystemClock.elapsedRealtime());
                                    chronometer.start();
                                }
                            } else if (event.getAction() == MotionEvent.ACTION_UP){
                                if(playerOneReady && !gameOver) {
                                    long durationInMillis = SystemClock.elapsedRealtime() - chronometer.getBase();
                                    timer.setText(timeFormater(durationInMillis));
                                    fullLayout.setBackgroundResource(R.drawable.background);
                                    winnerName.setText(String.format("%s WINS!", getPlayerOneName));
                                    chronometer.stop();
                                    gameOver = true;
                                    endGame();
                                } else if (!playerOneReady) {
                                    fullLayout.setBackgroundResource(R.drawable.background);
                                    playerTwoReady = false;
                                }
                            }
                            return false;
                        }
                    });
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    if(playerTwoReady && !gameOver) {
                        long durationInMillis = SystemClock.elapsedRealtime() - chronometer.getBase();
                        timer.setText(timeFormater(durationInMillis));
                        chronometer.stop();
                        fullLayout.setBackgroundResource(R.drawable.background);
                        winnerName.setText(String.format("%s WINS!", getPlayerTwoName));
                        gameOver = true;
                        endGame();
                    } else if (playerTwoReady == false) {
                        fullLayout.setBackgroundResource(R.drawable.background);
                        playerOneReady = false;
                    }
                }
                return false;
            }
        });

        playerTwoBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    fullLayout.setBackgroundColor(Color.GREEN);
                    playerTwoReady = true;
                    playerOneBtn.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            if(event.getAction() == MotionEvent.ACTION_DOWN) {
                                fullLayout.setBackgroundColor(Color.GREEN);
                                playerOneReady = true;
                                if (playerTwoReady && playerOneReady){
                                    fullLayout.setBackgroundColor(Color.RED);
                                    chronometer.setBase(SystemClock.elapsedRealtime());
                                    chronometer.start();
                                }
                            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                                if(playerTwoReady && !gameOver) {
                                    long durationInMillis = SystemClock.elapsedRealtime() - chronometer.getBase();
                                    timer.setText(timeFormater(durationInMillis));
                                    chronometer.stop();
                                    fullLayout.setBackgroundResource(R.drawable.background);
                                    winnerName.setText(String.format("%s WINS!", getPlayerTwoName));
                                    gameOver = true;
                                    endGame();
                                } else if (playerTwoReady == false) {
                                    fullLayout.setBackgroundResource(R.drawable.background);
                                    playerOneReady = false;
                                }
                            }
                            return false;
                        }
                    });
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    if(playerOneReady && !gameOver) {
                        long durationInMillis = SystemClock.elapsedRealtime() - chronometer.getBase();
                        timer.setText(timeFormater(durationInMillis));
                        chronometer.stop();
                        fullLayout.setBackgroundResource(R.drawable.background);
                        gameOver = true;
                        winnerName.setText(String.format("%s WINS!", getPlayerOneName));
                        endGame();
                    } else if (playerOneReady == false) {
                        fullLayout.setBackgroundResource(R.drawable.background);
                        playerTwoReady = false;
                    }
                    return false;
                }
                return false;
            }
        });

        restartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MultiplayerActivity.this, MultiplayerActivity.class);
                intent.putExtra("playerOne", getPlayerOneName);
                intent.putExtra("playerTwo", getPlayerTwoName);
                startActivity(intent);
                if (mInterstitialAd != null) {
                    mInterstitialAd.show(MultiplayerActivity.this);
                } else {
                    Log.d("TAG", "The interstitial ad wasn't ready yet.");
                }
            }
        });

        mainMenuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MultiplayerActivity.this, MainMenu.class);
                startActivity(intent);
                if (mInterstitialAd != null) {
                    mInterstitialAd.show(MultiplayerActivity.this);
                } else {
                    Log.d("TAG", "The interstitial ad wasn't ready yet.");
                }
            }
        });

    }

    public void endGame() {
        if(playerOneReady && playerTwoReady) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        }

    }

    public String timeFormater(Long durationInMillis) {
        long second = (durationInMillis / 1000) % 60;
        long minute = (durationInMillis / (1000 * 60)) % 60;
        long hour = (durationInMillis / (1000 * 60 * 60)) % 24;
        long days = (durationInMillis / (1000 * 60 * 60 * 24)) % 365;
        return String.format("%02dD : %02dH : %02dM : %02dS", days, hour, minute, second);
    }

}