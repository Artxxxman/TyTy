package com.deitel.tyty;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.app.Activity;
import android.widget.Button;

public class MainActivity extends Activity implements OnClickListener {

    Button BM1,BM2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BM1 = (Button) findViewById(R.id.BM1);
        BM1.setOnClickListener(this);
        BM2 = (Button) findViewById(R.id.BM2);
        BM2.setOnClickListener(this);
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.BM1:
                Intent intent = new Intent(this, Train.class);
                startActivity(intent);
                break;
            case R.id.BM2:
                String versionName = BuildConfig.VERSION_NAME;
                int versionCode = BuildConfig.VERSION_CODE;

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("О приложение")
                        .setMessage("© А. А. Сенюков, 2016" + "\n\nВерсия "+versionName+" (сборка "+versionCode+")")
                        .setCancelable(false)
                        .setNegativeButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                AlertDialog alert = builder.create();
                alert.show();
                break;
            default:
                break;
        }
    }
}
