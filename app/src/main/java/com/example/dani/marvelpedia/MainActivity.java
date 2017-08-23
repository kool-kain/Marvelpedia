package com.example.dani.marvelpedia;

import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private final String userOk = "MasterUsal";
    private final String passOk = "USAL2017";
    private TextInputLayout userWrapper;
    private TextInputLayout passWrapper;

    public static String PUB_API_KEY="4e6f1f94c64d54aaa9cf7409640fcd90";
    public static String PRIV_API_KEY="7c5675fa91fddc01a86e515346e3f4812fbfcff3";
    public static String URL_BASE="http://gateway.marvel.com";
    public static String IMAGE_SIZE = "/portrait_xlarge.";
    public static String RESP_OK = "OK";
    public static final String ARG_PARAM1 = "param1";
    public static final String ARG_PARAM2 = "param2";
    public static final String ARG_PARAM3 = "param3";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userWrapper = (TextInputLayout) findViewById(R.id.text_user);
        passWrapper = (TextInputLayout) findViewById(R.id.text_pass);

        userWrapper.getEditText().setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                //Desactiva mensaje error si tiene el foco
                if(hasFocus) {
                    userWrapper.setErrorEnabled(false);
                    passWrapper.setErrorEnabled(false);
                }
            }
        });

        passWrapper.getEditText().setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                //Desactiva mensaje error si tiene el foco
                if(hasFocus) {
                    userWrapper.setErrorEnabled(false);
                    passWrapper.setErrorEnabled(false);
                }
            }
        });

        Button enter = (Button) findViewById(R.id.enter);
        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                if (userWrapper.getEditText().getText().toString().equals(userOk)){
                    if (passWrapper.getEditText().getText().toString().equals(passOk)){
                        //Validación de usuario/contraseña OK. Fin de la actividad de Login
                        Intent i = new Intent(getApplicationContext(),SearchActivity.class);
                        startActivity(i);
                        finish();
                    }
                    else{
                        passWrapper.setErrorEnabled(true);
                        passWrapper.setError("Contraseña incorrecta");
                    }
                }
                else{
                    userWrapper.setErrorEnabled(true);
                    userWrapper.setError("Usuario no registrado");
                }
            }
        });
    }
}
