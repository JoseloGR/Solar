package com.itesm.digital.solar;

import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.CycleInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class SplashActivity extends Activity {

    ImageView ivLogo, drone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ivLogo = (ImageView) findViewById(R.id.iv_solar);
        drone = (ImageView) findViewById(R.id.drone);

        float propertyStart = drone.getX();
        float propertyEnd = drone.getX() + 20;

        ObjectAnimator xAnim = ObjectAnimator.ofFloat(drone, "translationX",
                propertyStart,propertyEnd).setDuration(5000);
        xAnim.setStartDelay(0);
        xAnim.setRepeatCount(0);
        //xAnim.setRepeatMode(ValueAnimator.REVERSE);
        xAnim.setInterpolator(new CycleInterpolator(2f));
        xAnim.start();

        Thread timerThread = new Thread(){
            public void run(){
                try{
                    sleep(5000);
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
            Intent i = new Intent(SplashActivity.this, Proyects.class);
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
