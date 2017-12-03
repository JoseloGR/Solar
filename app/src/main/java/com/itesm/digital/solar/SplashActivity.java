package com.itesm.digital.solar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.widget.ImageView;

public class SplashActivity extends Activity {

    ImageView ivLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ivLogo = (ImageView) findViewById(R.id.iv_solar);

        Thread timerThread = new Thread(){
            public void run(){
                try{
                    sleep(3000);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }finally{
                    GoHome();
                }
            }
        };
        timerThread.start();


    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    private void GoHome(){
        SharedPreferences userProfile = getSharedPreferences("AccessUser", Context.MODE_PRIVATE);

        if(userProfile.contains("Token")){
            Intent i = new Intent(SplashActivity.this, ChooseAddress.class);
            // Pass data object in the bundle and populate details activity.
            ActivityOptionsCompat options = ActivityOptionsCompat.
                    makeSceneTransitionAnimation(this, ivLogo,  ViewCompat.getTransitionName(ivLogo));
            startActivity(i, options.toBundle());
        }else{
            Intent i = new Intent(SplashActivity.this, Login.class);
            ActivityOptionsCompat options = ActivityOptionsCompat.
                    makeSceneTransitionAnimation(this, ivLogo,  ViewCompat.getTransitionName(ivLogo));
            startActivity(i, options.toBundle());
        }

    }
}
