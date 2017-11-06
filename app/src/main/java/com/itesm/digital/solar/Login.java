package com.itesm.digital.solar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.itesm.digital.solar.Interfaces.RequestInterface;
import com.itesm.digital.solar.Models.RequestLogin;
import com.itesm.digital.solar.Models.RequestProject;
import com.itesm.digital.solar.Models.ResponseLogin;
import com.itesm.digital.solar.Models.ResponseProject;
import com.itesm.digital.solar.Utils.GlobalVariables;
import com.itesm.digital.solar.Utils.Validations;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Login extends AppCompatActivity {

    private EditText user;    //guarda el usuario a ingresar
    private EditText password;  //guarda la contraseña introducida por el usuario
    ImageView ivLogo;

    MaterialDialog.Builder builder;
    MaterialDialog dialog;

    RequestInterface loginInterface;

    Retrofit.Builder builderR = new Retrofit.Builder()
            .baseUrl(GlobalVariables.API_BASE+GlobalVariables.API_VERSION)
            .addConverterFactory(GsonConverterFactory.create());

    Retrofit retrofit = builderR.build();

    RequestInterface connectInterface = retrofit.create(RequestInterface.class);

    String TOKEN="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ejecutar();

        user = (EditText) findViewById(R.id.user_text);  //recibe lo que el usuario ingreso
        password = (EditText) findViewById(R.id.password_text); //recibe lo que el usuario ingreso

        Button btn_access = (Button) findViewById(R.id.login_button);  //relaciona el objeto con el boton
        Button btn_register = (Button) findViewById(R.id.register_button);

        ivLogo = (ImageView) findViewById(R.id.logo_solar);

        btn_access.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin();
            }
        });

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //lleva a la pantalla de registro
                Intent mainIntent = new Intent().setClass(Login.this, Register.class);
                startActivity(mainIntent);
                //SendDataProject();
            }
        });

        builder = new MaterialDialog.Builder(this)
                .title(R.string.progress_dialog)
                .content(R.string.please_wait)
                .progress(true, 0);

        dialog = builder.build();
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {

        // Reset errors.
        user.setError(null);
        password.setError(null);

        // Store values at the time of the login attempt.
        String usernameValue = user.getText().toString();
        String passwordValue = password.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid email address.
        if (TextUtils.isEmpty(usernameValue)) {
            user.setError(getString(R.string.error_field_required));
            focusView = user;
            cancel = true;
        }

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(passwordValue)) {
            password.setError(getString(R.string.error_user_pass_empty));
            focusView = password;
            cancel = true;
        }else if(!Validations.isValidPassword(passwordValue)){
            password.setError(getString(R.string.error_user_pass));
            focusView = password;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            LoginProcess();

        }
    }

    private void LoginProcess(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GlobalVariables.API_BASE+GlobalVariables.API_VERSION)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        loginInterface = retrofit.create(RequestInterface.class);

        RequestLogin requestLogin = new RequestLogin();

        requestLogin.setUsername(user.getText().toString());
        requestLogin.setPassword(password.getText().toString());

        Call<ResponseLogin> responseLogin = loginInterface.LoginAccess(requestLogin);

        responseLogin.enqueue(new Callback<ResponseLogin>() {
            @Override
            public void onResponse(Call<ResponseLogin> call, Response<ResponseLogin> response) {
                int statusCode = response.code();
                ResponseLogin responseBody = response.body();
                showProgress(false);

                if(statusCode != 200) {
                    showMessage("Iniciar sesión", "Verifica tu nombre de usuario y contraseña");

                }
                else if(statusCode==200){

                    SharedPreferences tokenUser = getSharedPreferences("AccessUser", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = tokenUser.edit();

                    editor.putString("Token", responseBody.getId());
                    editor.putString("Ttl", responseBody.getTtl());
                    editor.putString("IdUser", responseBody.getUserId());
                    editor.putString("User", user.getText().toString());
                    editor.apply();

                    password.setText("");
                    TOKEN = responseBody.getId();
                    OnLoginResult();
                }
            }

            @Override
            public void onFailure(Call<ResponseLogin> call, Throwable t) {
                showProgress(false);
                showMessage("Iniciar sesión", "Tuvimos un problema con el servidor, intentalo de nuevo por favor");
            }
        });
    }

    private void OnLoginResult(){
        Intent intent = new Intent(Login.this, Proyects.class);
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(this, ivLogo,  ViewCompat.getTransitionName(ivLogo));
        startActivity(intent, options.toBundle());
        finish();
    }

    private void showProgress(boolean show) {
        if (show)
            dialog.show();
        else
            dialog.dismiss();
    }

    private void showMessage(String title, String message){

        if(message.isEmpty())
            message = "Tuvimos un problema con la conexión, inténtalo de nuevo por favor";
        new MaterialDialog.Builder(this)
                .title(title)
                .content(message)
                .positiveText("Ok")
                .show();
    }

    public void SendDataProject(){
        String DATE="2017-10-12T17:22:31.316Z", NAME="Solar", ADDRESS="CSF TEC", COST="10", SURFACE="10", ID_USER="1";


        RequestProject projectRegister = new RequestProject();
        projectRegister.setName(NAME);
        projectRegister.setAddress(ADDRESS);
        projectRegister.setCost(COST);
        projectRegister.setDate(DATE);
        projectRegister.setSurface(SURFACE);
        projectRegister.setUserId(ID_USER);

        Call<ResponseProject> responseRegister = connectInterface.RegisterProject(TOKEN, projectRegister);

        responseRegister.enqueue(new Callback<ResponseProject>() {
            @Override
            public void onResponse(Call<ResponseProject> call, Response<ResponseProject> response) {
                dialog.dismiss();
                int statusCode = response.code();
                ResponseProject responseBody = response.body();
                if (statusCode==201 || statusCode==200){
                    //SuccessProject("Proyecto Solar", "Tu proyecto ha sido registrado exitosamente.");
                    Log.d("SUCCESS",response.toString());
                }
                else{
                    showMessage("Proyecto Solar", "Hubo un problema al crear el proyecto. Contacte al administrador.");
                    Log.d("PROJECT",response.toString());
                }

            }

            @Override
            public void onFailure(Call<ResponseProject> call, Throwable t) {
                dialog.dismiss();
                Log.d("OnFail", t.getMessage());
                showMessage("Error en la comunicación", "No es posible conectar con el servidor. Intente de nuevo por favor");
            }
        });


    }

    public void hilo(){
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void ejecutar(){
        time time = new time();
        time.execute();

    }

    public class time extends AsyncTask<Void,Integer,Boolean>{

        @Override
        protected Boolean doInBackground(Void... voids) {
            for(int i=0;i<3;i++){
                hilo();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            Toast.makeText(Login.this,"hola",Toast.LENGTH_SHORT).show();
            ejecutar();
        }
    }

}


