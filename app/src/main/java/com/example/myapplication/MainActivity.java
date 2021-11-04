package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public static final String Extra = "com.example.application.example.Extra";
    Button btn;
    EditText etx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn = findViewById(R.id.btn);
        etx = findViewById(R.id.etx);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //inicjalizacja connectivityMenager
                ConnectivityManager connectivityManager = (ConnectivityManager)
                        getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                //pobranie informacji o sieci
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                //sprawdzenie statusu sieci
                if (networkInfo == null || !networkInfo.isConnected() || !networkInfo.isAvailable()){
                    //gdy internet jest połączony
                    Toast toast = Toast.makeText(getApplicationContext(), "Connection lost!!!", Toast.LENGTH_LONG);
                    toast.show();
                }else{
                    //pobranie nazwy miasta
                    String City = etx.getText().toString();
                    saveData(City);
                    //zabezpieczenie orzed pustym polem
                    if (TextUtils.isEmpty(City)) {
                        etx.setError("Please choose the city.");
                        return;
                    }
                    //Przeniesienie do nowego Activity
                    Intent second = new Intent(getApplicationContext(), SecondActivity.class);
                    second.putExtra(Extra, City);
                    startActivity(second);
                }
            }
        });
        loadData();
    }
    private void saveData (String City){
        SharedPreferences sharedPreferences = getSharedPreferences("City", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("City", City);
        editor.apply();
    }
    private void loadData (){
        SharedPreferences sharedPreferences = getSharedPreferences("City", MODE_PRIVATE);
        String dane = sharedPreferences.getString("City", "");
        etx.setText(dane);
    }
}
