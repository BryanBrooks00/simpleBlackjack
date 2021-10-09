package com.darwin.simpleblackjack;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.admanager.AdManagerAdRequest;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private RewardedAd mRewardedAd;
    SharedPreferences sp;
    static final String TAG = "LOG";
    static final String KEY = "KEY";
    private long backPressedTime;

    int[] list = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
    int starterMoney = 5000;
    int bet = 1000;
    int listLength = list.length;
    int reward = 5000;
    int click = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadAd();
        setBet();
    }


    @SuppressLint("SetTextI18n")
    public void setBet() {
        TextView money_tv = findViewById(R.id.money_tv);
        sp = getSharedPreferences(KEY, MODE_PRIVATE);
        if (sp.getString(KEY, null) != null  &&  !sp.getString(KEY, null).equals("")){
            loadPref();
        } else {
            money_tv.setText(starterMoney + "");
        }
        startGame(bet);
    }


    @SuppressLint("SetTextI18n")
    public void startGame(int bet) {
        Log.i(TAG, "NEW GAME");

        Button btn_ad = findViewById(R.id.btn_ad);
        Button plus_btn = findViewById(R.id.plus_btn);
        Button minus_btn = findViewById(R.id.minus_btn);
        Button restart_btn = findViewById(R.id.restart_btn);

        plus_btn.setEnabled(true);
        minus_btn.setEnabled(true);
        plus_btn.setVisibility(View.VISIBLE);
        minus_btn.setVisibility(View.VISIBLE);
        restart_btn.setVisibility(View.INVISIBLE);

        TextView money_tv = findViewById(R.id.money_tv);
        TextView score_tv = findViewById(R.id.score_tv);
        TextView robotScore_tv = findViewById(R.id.robotScore_tv);
        TextView result_tv = findViewById(R.id.result_tv);

        robotScore_tv.setText("");
        score_tv.setText("");
        score_tv.setVisibility(View.VISIBLE);
        result_tv.setText("");

        Random random = new Random();
        int randomInt = random.nextInt(listLength);
        int score = randomInt + randomInt + 1;
        score_tv.setText(score + "");

        int money = Integer.parseInt(money_tv.getText().toString());
        if (money <= 0) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.noMoney),
                    Toast.LENGTH_SHORT).show();
            money_tv.setTextColor(Color.RED);
            score_tv.setVisibility(View.INVISIBLE);
            plus_btn.setVisibility(View.INVISIBLE);
            minus_btn.setVisibility(View.INVISIBLE);
            btn_ad.setVisibility(View.VISIBLE);
            btn_ad.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showAd();
                }
            });
        } else {
            money_tv.setTextColor(Color.WHITE);
            btn_ad.setVisibility(View.INVISIBLE);
            money_tv.setText(String.valueOf(money - bet));
        }

        if (money >= 15000){
            money_tv.setText((money + starterMoney) + "");
        }

        plus_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (score <= 21) {
                    int currentScore = Integer.parseInt(score_tv.getText().toString());
                    Log.i(TAG, "score = " + currentScore);

                    Random random = new Random();
                    int newRandomInt = random.nextInt(listLength);
                    int newScore = currentScore + newRandomInt + 1;
                    score_tv.setText(newScore + "");
                    Log.i(TAG, "new score = " + newScore);

                    if (newScore > 21) {
                        Log.i(TAG, "score >21");
                        getRobotResult(newScore, 1, bet);
                    }

                }
            }
        });


        minus_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentScore = Integer.parseInt(score_tv.getText().toString());
                getRobotResult(currentScore, 1, bet);
            }
        });
    }


    public void getRobotResult(int score, int robotScore, int bet) {
        if (robotScore < 17) {
            Random random = new Random();
            int randomInt = random.nextInt(listLength);
            robotScore += randomInt;
            getRobotResult(score, robotScore, bet);
        } else {
            Log.i(TAG, robotScore + "");
            getResult(score, robotScore, bet);
        }
    }


    @SuppressLint("SetTextI18n")
    public void getResult(int score, int robotScore, int bet) {

        Button plus_btn = findViewById(R.id.plus_btn);
        Button minus_btn = findViewById(R.id.minus_btn);
        plus_btn.setEnabled(false);
        minus_btn.setEnabled(false);

        String robotInt = getString(R.string.robotInt);
        String youWin = getString(R.string.youWin);
        String youLose = getString(R.string.youLose);
        String draw = getString(R.string.draw);

        TextView money_tv = findViewById(R.id.money_tv);
        TextView robotScore_tv = findViewById(R.id.robotScore_tv);

        robotScore_tv.setText(robotInt + "" + robotScore);
        int money = Integer.parseInt(money_tv.getText().toString());
        String result;

        if (score <= 21 & robotScore <= 21) {
            Log.i(TAG, "score = " + score + "; robotScore = " + robotScore);
            Log.i(TAG, "score and robotScore <=21");

            if (score < robotScore) {
                result = youLose;
                setResult(result);
                Log.i(TAG, "score = " + score + "; robotScore = " + robotScore);
                Log.i(TAG, "score < robotScore; LOSE");

            } else if (score > robotScore) {
                result = youWin;
                money_tv.setText(String.valueOf(money + (bet * 2)));
                setResult(result);
                Log.i(TAG, "score = " + score + "; robotScore = " + robotScore);
                Log.i(TAG, "score > robotScore; WIN");

            } else if (score == robotScore) {
                result = draw;
                money_tv.setText(String.valueOf(money + bet));
                setResult(result);
                Log.i(TAG, "score = " + score + "; robotScore = " + robotScore);
                Log.i(TAG, "score = robotScore; DRAW");
            }

        } else if (robotScore > 21 & score > 21) {
            result = draw;
            money_tv.setText(String.valueOf(money + bet));
            setResult(result);
            Log.i(TAG, "score = " + score + "; robotScore = " + robotScore);
            Log.i(TAG, "score and robotScore > 21");
            Log.i(TAG, "score = robotScore; DRAW");

        } else if (score <= 21 & robotScore > 21) {
            result = youWin;
            money_tv.setText(String.valueOf(money + (bet * 2)));
            setResult(result);
            Log.i(TAG, "score = " + score + "; robotScore = " + robotScore);
            Log.i(TAG, "score <=21 & robotScore > 21; WIN");

        } else if (score > 21 & robotScore <= 21) {
            result = youLose;
            setResult(result);
            Log.i(TAG, "score = " + score + "; robotScore = " + robotScore);
            Log.i(TAG, "score >21 & robotScore <= 21; LOSE");
        }
    }


    public void setResult(String result) {
        Log.i(TAG, result + "");

        String youWin = getString(R.string.youWin);
        String youLose = getString(R.string.youLose);

        TextView result_tv = findViewById(R.id.result_tv);
        result_tv.setText(result);

        if (result.equals(youWin)) {
            result_tv.setTextColor(Color.GREEN);
        } else if (result.equals(youLose)) {
            result_tv.setTextColor(Color.RED);
        } else {
            result_tv.setTextColor(Color.YELLOW);
        }

        savePref();
        Button restart_btn = findViewById(R.id.restart_btn);
        restart_btn.setVisibility(View.VISIBLE);
        restart_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startGame(bet);
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (backPressedTime + 2000 > System.currentTimeMillis()){
            super.onBackPressed();
            return;
        } else{
        Toast.makeText(getApplicationContext(), getResources().getString(R.string.exit),
                Toast.LENGTH_SHORT).show();

        }
        backPressedTime = System.currentTimeMillis();
    }

    public void loadAd(){
    // ad
    AdManagerAdRequest adRequest = new AdManagerAdRequest.Builder().build();

        RewardedAd.load(this,"ca-app-pub-2382402581294867/6664384301",
    adRequest,new RewardedAdLoadCallback() {
        @Override
        public void onAdFailedToLoad (@NonNull LoadAdError loadAdError){
            // Handle the error.
            Log.d(TAG, loadAdError.getMessage());
            Log.i(TAG, "Failed to load ad.");
            mRewardedAd = null;
        }

        @Override
        public void onAdLoaded (@NonNull RewardedAd rewardedAd){
            mRewardedAd = rewardedAd;
            Log.d(TAG, "Ad was loaded.");
        }
    });
    //
}

    @SuppressLint("SetTextI18n")
            public void showAd(){

                try {
                    mRewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                        @Override
                        public void onAdShowedFullScreenContent() {
                            // Called when ad is shown.
                            Log.d(TAG, "Ad was shown.");
                        }

                        @Override
                        public void onAdFailedToShowFullScreenContent(AdError adError) {
                            // Called when ad fails to show.
                                Log.d(TAG, "Ad failed to show.");
                        }

                        @Override
                        public void onAdDismissedFullScreenContent() {
                            // Called when ad is dismissed.
                            // Set the ad reference to null so you don't show the ad a second time.
                            Log.d(TAG, "Ad was dismissed.");
                            mRewardedAd = null;
                        }

                    });
                } catch (Exception e){
                    Log.i(TAG, "showAd error");
                }

                TextView money_tv = findViewById(R.id.money_tv);
                if (mRewardedAd!= null) {
                    Activity activityContext = MainActivity.this;
                    mRewardedAd.show(activityContext, new OnUserEarnedRewardListener() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                            // Handle the reward.
                            Log.d(TAG, "The user earned the reward.");
                            int rewardAmount = rewardItem.getAmount();
                            String rewardType = rewardItem.getType();
                            money_tv.setText(reward + "");
                            startGame(bet);
                        }
                    });
                } else {
                        Log.d(TAG, "The rewarded ad wasn't ready yet.");
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.tryLater),
                                Toast.LENGTH_SHORT).show();
                        money_tv.setText("1000");
                        startGame(bet);
                    }
                }

                public void savePref(){
                TextView money_tv = findViewById(R.id.money_tv);
                sp = getSharedPreferences(KEY, MODE_PRIVATE);
                String money = money_tv.getText().toString();
                SharedPreferences.Editor editor = sp.edit();
                editor.putString(KEY, money);
                editor.apply();
                    Log.i(TAG, "SharedPreferences saved: " + money);
                }

                public void loadPref(){
                TextView money_tv = findViewById(R.id.money_tv);
                sp = getSharedPreferences(KEY, MODE_PRIVATE);
                String money = sp.getString(KEY, null);
                money_tv.setText(money);
                    Log.i(TAG, "SharedPreferences loaded: " + money);
                }

    @Override
    public void onPause() {
        savePref();
        Log.i(TAG, "onPause");
        super.onPause();
    }

   /*@Override
    public void onResume() {
        loadPref();
        Log.i(TAG, "onResume");
        super.onResume();
    }

    */

    @Override
    public void onStop() {
        savePref();
        Log.i(TAG, "onStop");
        super.onStop();
    }

    @Override
    public void onDestroy() {
        savePref();
        Log.i(TAG, "onDestroy");
        super.onDestroy();
    }
}
            
