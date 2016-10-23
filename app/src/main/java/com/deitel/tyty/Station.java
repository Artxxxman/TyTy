package com.deitel.tyty;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.deitel.tyty.parser.JSONParser;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class Station extends Activity {

    ListView lvSimple;
    EditText inputSearch;
    private ProgressDialog pDialog;
    private AlertDialog aDialog;
    private GoogleApiClient client;
    String mark;
    // Создаем JSON парсер
    JSONParser jParser = new JSONParser();



    ArrayList<HashMap<String, String>> stationsList;
    // url получения списка всех станций
    private static String url_all_products = "https://raw.githubusercontent.com/tutu-ru/hire_android_test/master/allStations.json";
    // JSON Node names
    private static final String TAG_STATIONS = "stations";
    private static final String COUNTRY_TITLE = "countryTitle";
    private static final String DISTRICT_TITLE = "districtTitle";
    private static final String CITY_TITLE = "cityTitle";
    private static final String STATION_TITLE = "stationTitle";
    private static final String REGION_TITLE = "regionTitle";
    private static final String CITIES_FROM = "citiesFrom";
    private static final String CITIES_TO = "citiesTo";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.station);
        // Hashmap для ListView
        stationsList = new ArrayList<HashMap<String, String>>();
        Intent intent = getIntent();
        String date = intent.getStringExtra("inputDate");
        // Загружаем станции в фоновом потоке
        new LoadAllStations().execute(date);
        // получаем ListView
        lvSimple = (ListView) findViewById(R.id.lvSimple);
        inputSearch = (EditText) findViewById(R.id.inputSearch);

        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Station Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    //Фоновый Async Task для загрузки всех продуктов по HTTP запросу
    class LoadAllStations extends AsyncTask<String, String, String> {

        //Перед началом фонового потока Show Progress Dialog
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Station.this);
            pDialog.setMessage("Загрузка станций. Подождите...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }


        protected String doInBackground(String... args) {
            mark = args[0];
            // Будет хранить параметры
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            // получаем JSON строк с URL
            JSONObject json = jParser.makeHttpRequest(url_all_products, "GET", params);
            try {
                // Получаем масив пунктов отправления/назначения
                JSONArray cities = null;
                if (args[0].equals("Dep")) cities = json.getJSONArray(CITIES_FROM);
                else cities = json.getJSONArray(CITIES_TO);
                // перебор всех пунктов отправления/назначения
                for (int i = 0; i < cities.length(); i++) {
                    JSONObject stations = cities.getJSONObject(i);
                    // Получаем масив станций
                    JSONArray c = stations.getJSONArray(TAG_STATIONS);
                    //Перебираем все станции
                    for (int j = 0; j < c.length(); j++) {
                        JSONObject stat = c.getJSONObject(j);
                        // Сохраняем каждый json елемент в переменную
                        String titleStation = stat.getString(STATION_TITLE);
                        String titleCountry = stat.getString(COUNTRY_TITLE);
                        String titleDistrict = stat.getString(DISTRICT_TITLE);
                        String titleCity = stat.getString(CITY_TITLE);
                        String titleRegion = stat.getString(REGION_TITLE);
                        // Создаем новый HashMap
                        HashMap<String, String> map = new HashMap<String, String>();
                        // добавляем каждый елемент в HashMap ключ => значение
                        map.put(STATION_TITLE, titleStation);
                        map.put(COUNTRY_TITLE, titleCountry);
                        map.put(DISTRICT_TITLE, titleDistrict);
                        map.put(CITY_TITLE, titleCity);
                        map.put(REGION_TITLE, titleRegion);
                        // добавляем HashList в ArrayList
                        stationsList.add(map);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        //После завершения фоновой задачи закрываем прогрес диалог
        protected void onPostExecute(String file_url) {
            // закрываем прогресс диалог после получение всех станций
            pDialog.dismiss();
            // обновляем UI форму в фоновом потоке
            runOnUiThread(new Runnable() {
                public void run() {
                    //Обновляем распарсенные JSON данные в ListView
                    final SimpleAdapter adapter = new SimpleAdapter(
                            Station.this, stationsList,
                            R.layout.item, new String[]{STATION_TITLE,
                            COUNTRY_TITLE, CITY_TITLE},
                            new int[]{R.id.tv1, R.id.tv2, R.id.tv3});
                    // обновляем listview
                    lvSimple.setAdapter(adapter);
                    inputSearch.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                            //Когда пользователь вводит какой-нибудь текст:
                            adapter.getFilter().filter(cs);
                        }

                        @Override
                        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                                      int arg3) {
                        }

                        @Override
                        public void afterTextChanged(Editable arg0) {
                        }
                    });
                    //Создаем метки для разбития строки Item'а на понятные пользователю
                    final String pattern = "cityTitle=";
                    final String pattern2 = "regionTitle=";
                    final String pattern3 = "countryTitle=";
                    final String pattern4 = "stationTitle=";
                    final String pattern5 = "districtTitle=";
                    lvSimple.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            //Получаем строку инфы Item'а
                            final String s = parent.getAdapter().getItem(position).toString();
                            //Разбиваем строку на название станции,города и тп.
                            String[] str = s.split(pattern);
                            String[] str2 = str[1].split(pattern2);
                            String[] str3 = str2[1].split(pattern3);
                            String[] str4 = str3[1].split(pattern4);
                            String[] str5 = str4[1].split(pattern5);
                            //Формируем строку для удобного вывода
                            final String strfinal =str5[0]+"\n"+str2[0]+"\n"+str4[0];
                            //Реализуем диалоговое окно вывода информации о выбранной станции
                            aDialog = new AlertDialog.Builder(Station.this)
                                    .setMessage(strfinal)
                                    //создаем кнопку "ok"
                                    .setPositiveButton("Выбрать", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface adialog,int which){
                                            Intent intent = new Intent();
                                            //Проверяем станции бли запрощены(прибытия или отпраки)
                                            if(mark.equals("Dep")){
                                                intent.putExtra("inputDate","Dep");
                                            }
                                            else intent.putExtra("inputDate","Arr");
                                            intent.putExtra("station",strfinal);
                                            setResult(RESULT_OK,intent);
                                            finish();
                                        }
                                    }).setNegativeButton("Отмена", new DialogInterface.OnClickListener(){
                                        @Override
                                        public void onClick(DialogInterface adialog,int which){
                                            aDialog.dismiss();
                                        }
                                    }).create();
                            aDialog.show();
                        }
                    });

                }
            });
        }
    }
}
