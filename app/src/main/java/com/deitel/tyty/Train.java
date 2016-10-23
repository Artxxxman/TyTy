package com.deitel.tyty;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.content.Intent;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.util.Calendar;

public class Train extends Activity implements View.OnClickListener{
    TextView tvDP,tvAR,tvDate;
    private int mYear, mMonth, mDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.train);
        tvAR = (TextView) findViewById(R.id.tvAR);
        tvAR.setOnClickListener(this);
        tvDP = (TextView) findViewById(R.id.tvDep);
        tvDP.setOnClickListener(this);
        tvDate = (TextView) findViewById(R.id.tvDate);
        tvDate.setOnClickListener(this);
    }
    @Override
    public void onClick(View v){
        //прописываем реакцию на нажатие для каждого TextView
        switch (v.getId()){
            case R.id.tvDep :

                if(isNetworkAvailable()) {
                    Intent intentDep = new Intent(this,Station.class );
                    intentDep.putExtra("inputDate", "Dep");
                    startActivityForResult(intentDep,1);}
                else {Toast toast = Toast.makeText(getApplicationContext(),
                        "Подключение к интернету отсутствует", Toast.LENGTH_SHORT);
                    toast.show();}

                    break;
            case R.id.tvAR :
                if(isNetworkAvailable()) {
                    Intent intentAr = new Intent(this,Station.class );
                    intentAr.putExtra("inputDate", "Arr");
                    startActivityForResult(intentAr,1);}
                    else {Toast toast = Toast.makeText(getApplicationContext(),
                            "Подключение к интернету отсутствует", Toast.LENGTH_SHORT);
                        toast.show();}
                break;
            case R.id.tvDate :
                callDatePicker();
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data == null){
            return;
        }
        //получем значения
        String station = data.getStringExtra("station");
        String mark = data.getStringExtra("inputDate");
        //определяем в какой TextView вставить полученные значения
        if(mark.equals("Arr")) tvAR.setText(station);
        else tvDP.setText(station);
    }

    private void callDatePicker() {
        // получаем текущую дату
        final Calendar cal = Calendar.getInstance();
        mYear = cal.get(Calendar.YEAR);
        mMonth = cal.get(Calendar.MONTH);
        mDay = cal.get(Calendar.DAY_OF_MONTH);

        // инициализируем диалог выбора даты текущими значениями
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String editTextDateParam = dayOfMonth + "." + (monthOfYear + 1) + "." + year;
                        tvDate.setText(editTextDateParam);
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }
}

