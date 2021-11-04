package com.example.myapplication;

import androidx.annotation.IntRange;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.zain.android.internetconnectivitylibrary.ConnectionUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Objects;
import java.util.Random;
import java.util.SimpleTimeZone;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;

import static android.os.SystemClock.sleep;
import static com.example.myapplication.R.drawable.d01;

public class SecondActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    //========================

    public static final String Extra = "com.example.application.example.Extra";
    TextView tx1, tx2, tx3, tx4, tx5, tx6, tx7;
    ImageView ikonka;
    ConstraintLayout back;
    SwipeRefreshLayout swipeRefreshLayout;
    Handler mHandler;

    public static String City;

    class Weather extends AsyncTask<String,Void,String>{//kolejno w <>, zapisuje w String, nic, odczytuje w String
        @Override
        protected String doInBackground(String... address) {
            try {
                URL url = new URL(address[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream dane = connection.getInputStream();
                InputStreamReader daner = new InputStreamReader(dane);

                int data = daner.read();
                String content = "";
                char ch;
                while (data != -1){
                    ch = (char) data;
                    content = content + ch;
                    data = daner.read();
                }return content;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace(); }
            return null;
        }
    }
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        this.handleRefreshData();

        this.mHandler = new Handler();
        mHandler.post(m_Runnable);
    }

    public void handleRefreshData() {

        swipeRefreshLayout = findViewById(R.id.refresh);
        swipeRefreshLayout.setOnRefreshListener(this);

        tx1 = findViewById(R.id.tx1);
        tx2 = findViewById(R.id.tx2);
        tx3 = findViewById(R.id.tx3);
        tx4 = findViewById(R.id.tx4);
        tx5 = findViewById(R.id.tx5);
        tx6 = findViewById(R.id.tx6);
        tx7 = findViewById(R.id.tx7);
        ikonka = findViewById(R.id.ikonka);
        back = findViewById(R.id.back);

        //Przeniesienie wpisanego miasta
        Intent second = getIntent();
        City = second.getStringExtra(MainActivity.Extra);
        //========================================================

        String content;
        Weather weather = new Weather();
        try {
            content = weather.execute("http://api.openweathermap.org/data/2.5/weather?q=" +
                    City + "&units=metric&APPID=749561a315b14523a8f5f1ef95e45864").get();
            //zabezpieczenie przed błędną nazwą
            if(content == null){
                Intent first = new Intent(getApplicationContext(), MainActivity.class);
                Toast toast = Toast.makeText(getApplicationContext(), "Invalid city name", Toast.LENGTH_LONG);
                toast.show();
                startActivity(first);
                return;
            }
            //JSON - spisanie wartości z serwera
            JSONObject jsono = new JSONObject(content);
            String weatherData = jsono.getString("weather");
            String name = jsono.getString("name");
            String temp = jsono.getString("main");
            String dt = jsono.getString("dt");
            String tz = jsono.getString("timezone");

            //logi do sprawdzania działania
            Log.i("wszystko", content);
            Log.i("weatherData", weatherData);
            Log.i("name", name);
            Log.i("temp", temp);
            Log.i("date", dt);

            // \/---Podobiekty "main"---\/
            JSONObject tempe = new JSONObject(temp);
            JSONObject press = new JSONObject(temp);
            JSONObject humi = new JSONObject(temp);
            JSONObject tempmi = new JSONObject(temp);
            JSONObject tempma = new JSONObject(temp);
            String temperatura = tempe.getString("temp");
            String pressure = press.getString("pressure");
            String humidity = humi.getString("humidity");
            String tempmin = tempmi.getString("temp_min");
            String tempmax = tempma.getString("temp_max");

            //zamiana temperatury na forme bez przecinka
            double tempd = Double.parseDouble(temperatura);
            double tempr = Math.round(tempd);
            NumberFormat format = new DecimalFormat("0.#");

            //czas
            Long czas = Long.parseLong(dt);
            Long tz1 = Long.parseLong(tz);
            Long czas1 = (czas + tz1) * 1000;
            Date df = new java.util.Date(czas1);
            String godzina = new SimpleDateFormat("HH:mm").format(df);

            //array do ikony
            //wyciągnięcie zmiennej ikona z API wyymaga użycia Array
            JSONArray array = new JSONArray(weatherData);
            String icons = "";
            for (int i = 0; i < array.length(); i++) {
                JSONObject icon = array.getJSONObject(i);
                icons = icon.getString("icon");
            }
            //dobieranie ikon
            if (icons.equals("01d")) {
                ikonka.setImageResource(R.drawable.d01);
            } else if (icons.equals("01n")) {
                ikonka.setImageResource(R.drawable.n01);
            } else if (icons.equals("02d")) {
                ikonka.setImageResource(R.drawable.d02);
            } else if (icons.equals("02n")) {
                ikonka.setImageResource(R.drawable.n02);
            } else if (icons.equals("03d")) {
                ikonka.setImageResource(R.drawable.d03);
            } else if (icons.equals("03n")) {
                ikonka.setImageResource(R.drawable.n03);
            } else if (icons.equals("04d")) {
                ikonka.setImageResource(R.drawable.d04);
            } else if (icons.equals("04n")) {
                ikonka.setImageResource(R.drawable.n04);
            } else if (icons.equals("09d")) {
                ikonka.setImageResource(R.drawable.d09);
            } else if (icons.equals("09n")) {
                ikonka.setImageResource(R.drawable.n09);
            } else if (icons.equals("10d")) {
                ikonka.setImageResource(R.drawable.d10);
            } else if (icons.equals("10n")) {
                ikonka.setImageResource(R.drawable.n10);
            } else if (icons.equals("11d")) {
                ikonka.setImageResource(R.drawable.d11);
            } else if (icons.equals("11n")) {
                ikonka.setImageResource(R.drawable.n11);
            } else if (icons.equals("13d")) {
                ikonka.setImageResource(R.drawable.d13);
            } else if (icons.equals("13n")) {
                ikonka.setImageResource(R.drawable.n13);
            } else if (icons.equals("50d")) {
                ikonka.setImageResource(R.drawable.d50);
            } else if (icons.equals("50n")) {
                ikonka.setImageResource(R.drawable.n50);
            } else {
                ikonka.setImageResource(R.drawable.d03);
            }//ikony ze strony https://erikflowers.github.io/weather-icons/

            //tło
            //uzywam zmiennej ikony, bo jest dokładniejsza niz godzina (nie wszędzie wschód i zachód są o tej samej godzinie)
            if (icons.equals("01d")||icons.equals("02d")||icons.equals("03d")||icons.equals("04d")||icons.equals("09d")||icons.equals("10d")||icons.equals("11d")||icons.equals("13d")||icons.equals("50d")){
                back.setBackgroundResource(R.drawable.dzien1080);
            } else if (icons.equals("01n")||icons.equals("02n")||icons.equals("03n")||icons.equals("04n")||icons.equals("09n")||icons.equals("10n")||icons.equals("11n")||icons.equals("13n")||icons.equals("50n")) {
                back.setBackgroundResource(R.drawable.noc1080);
            } else {
                back.setBackgroundResource(R.drawable.dzien1080);
            }
            //przykładowe tła, można użyc samego koloru, rozwiąże to problem z errorem wynikającym z rozdzielczości obrazu i ekranu.
            Log.i("icon", icons); //sprawdzenie poprawności odczytu ikon, można śmiało usuwać

            //Wyświetlanie wyników
            tx1.setText("" + name);
            tx2.setText("" + format.format(tempr) + "°");
            tx3.setText("Pressure:  " + pressure + "hPa");
            tx4.setText("Humidity:  " + humidity + "%");
            tx5.setText("" + tempmin + "°C");
            tx6.setText("" + tempmax + "°C");
            tx7.setText("" + godzina);

            City = tx1.getText().toString();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRefresh() {
        //inicjalizacja connectivityMenager
        ConnectivityManager connectivityManager = (ConnectivityManager)
                getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        //pobranie informacji o sieci
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        //sprawdzenie statusu sieci
        if (networkInfo == null || !networkInfo.isConnected() || !networkInfo.isAvailable()){
            //gdy internet jest połączony
            Intent to = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(to);
            Toast toast = Toast.makeText(getApplicationContext(), "Connection lost!", Toast.LENGTH_LONG);
            toast.show();
            finish();
        }else{
            handleRefreshData();
            swipeRefreshLayout.setRefreshing(false);
        }
    }
    private final Runnable m_Runnable = new Runnable() {
        @Override
        public void run() {
            //inicjalizacja connectivityMenager
            ConnectivityManager connectivityManager = (ConnectivityManager)
                    getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            //pobranie informacji o sieci
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            //sprawdzenie statusu sieci
            if (networkInfo == null || !networkInfo.isConnected() || !networkInfo.isAvailable()){
                //gdy internet jest połączony
                Intent to = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(to);
                Toast toast = Toast.makeText(getApplicationContext(), "Connection lost!", Toast.LENGTH_LONG);
                toast.show();
                finish();
            }else{
                handleRefreshData();
                mHandler.postDelayed(this, 20000);
            }
        }

    };
}

/*=============================================================
                       WITOLD JAWORSKI
==============================================================*/