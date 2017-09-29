package com.itesm.digital.solar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Thread timerThread = new Thread(){
            public void run(){
                try{
                    sleep(3000);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }finally{

                    SharedPreferences userProfile = getSharedPreferences("AccessUser", Context.MODE_PRIVATE);

                    if(userProfile.contains("Token")){
                        Intent i = new Intent(SplashActivity.this, Proyects.class);
                        startActivity(i);
                    }else{
                        Intent i = new Intent(SplashActivity.this, Login.class);
                        startActivity(i);
                    }

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
}
