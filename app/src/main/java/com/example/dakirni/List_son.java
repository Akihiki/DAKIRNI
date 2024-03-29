package com.example.dakirni;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dakirni.AdapterContact.AdapterContact;
import com.example.dakirni.AdapterContact.Contact;
import com.example.dakirni.AdapterFather.ModelClass;
import com.example.dakirni.AdapterFather.MyAdapter;
import com.example.dakirni.database.son.SonDbHelper;
import com.example.dakirni.environements.environementVariablesOfDakirni;
//import com.google.android.gms.location.FusedLocationProviderClient;
//import com.google.android.gms.location.LocationServices;
//import com.google.android.gms.maps.CameraUpdateFactory;
//import com.google.android.gms.maps.model.LatLng;
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class List_son extends AppCompatActivity {

    RecyclerView sonrecyclerView;
    LinearLayoutManager layoutManager;
    List<Contact> sonList;
    MyAdapter adapter;
    environementVariablesOfDakirni env;

    private String BASE_URL = env.BASE_URL;
    private Retrofit retrofit;
    private RetrofitInterface retrofitInterface;

    private static final int PERMISSIONS_REQUEST = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_sons);

        retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        retrofitInterface = retrofit.create(RetrofitInterface.class);

        initData();

        //Check whether GPS tracking is enabled

        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//            finish();
        }

        //Check whether this app has access to the location permission

        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        //Start the TrackerService if the location permission has been granted

        if (permission == PackageManager.PERMISSION_GRANTED) {
            startTrackerService();
        } else {

        //Request access if the app doesn’t have access to the user’s location

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST);
        }

//        initRecyclerView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //Start the GPS tracking service if the permission has been granted

        if (requestCode == PERMISSIONS_REQUEST && grantResults.length == 1
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            startTrackerService();

        } else {

        //Display a toast with more information if the user denies the permission request

            Toast.makeText(this, "Please enable location services to allow GPS tracking", Toast.LENGTH_SHORT).show();
        }
    }

    //Start the TrackerService

    private void startTrackerService() {
        startService(new Intent(this, TrackingService.class));

    //Notify the user that tracking has been enabled

        Toast.makeText(this, "GPS tracking enabled", Toast.LENGTH_SHORT).show();

    }



    private void initRecyclerView(List<Contact> sonsList) {
        sonrecyclerView=findViewById(R.id.RecyclerView_list_sons);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        sonrecyclerView.setLayoutManager(layoutManager);
        System.out.println(sonsList);
        adapter= new MyAdapter(sonsList, this);
        sonrecyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void initData() {

//        SonDbHelper sonDbHelper = new SonDbHelper(getApplicationContext());
//        ArrayList<String> arrayList = sonDbHelper.lireToken();
//        StringBuffer maListe = new StringBuffer();

//        try {
//            Iterator<String> iter = arrayList.iterator();
//            while (iter.hasNext()) {
//                maListe.append(iter.next());
//            }
////            Toast.makeText(getApplicationContext(), "maListe.toString()", Toast.LENGTH_SHORT).show();
//            Toast.makeText(getApplicationContext(), maListe.toString(), Toast.LENGTH_SHORT).show();
//        } catch (ArrayIndexOutOfBoundsException e) {
//            Toast.makeText(getApplicationContext(), "Aucun Résultat trouvé !", Toast.LENGTH_SHORT).show();
//        }

        Toast.makeText(getApplicationContext(), "env" + environementVariablesOfDakirni.key, Toast.LENGTH_SHORT).show();

        Call<List<Contact>> call = retrofitInterface.getSons(environementVariablesOfDakirni.key);
        call.enqueue(new Callback<List<Contact>>() {


            @Override
            public void onResponse(Call<List<Contact>> call, Response<List<Contact>> response) {
                if (response.body() != null && response.code() == 200) {
                    sonList = new ArrayList<>();
                    for (Contact c : response.body()) {
                        Toast.makeText(getApplicationContext(), "contact"+c.getTextview1(), Toast.LENGTH_SHORT).show();
                        sonList.add(c);
                    }
                }
                Log.d("listSons", sonList.toString());
                Toast.makeText(getApplicationContext(), "respo", Toast.LENGTH_SHORT).show();
                initRecyclerView(sonList);
            }

            @Override
            public void onFailure(Call<List<Contact>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Fail : " + t.getMessage(), Toast.LENGTH_LONG).show();
            }

        });

//        sonList = new ArrayList<>();
//
//        sonList.add(new ModelClass(R.drawable.bo,"nabil waldak","0665478912","_______________________________________"));
//        sonList.add(new ModelClass(R.drawable.girl,"saida abila","0854789221","_______________________________________"));
//        sonList.add(new ModelClass(R.drawable.gi,"maroni zwina","123456789","_______________________________________"));
//        sonList.add(new ModelClass(R.drawable.bo,"nabil waldak","0665478912","_______________________________________"));
//        sonList.add(new ModelClass(R.drawable.girl,"saida abila","0854789221","_______________________________________"));
//        sonList.add(new ModelClass(R.drawable.gi,"maroni zwina","123456789","_______________________________________"));
//        sonList.add(new ModelClass(R.drawable.bo,"nabil waldak","0665478912","_______________________________________"));
//        sonList.add(new ModelClass(R.drawable.girl,"saida abila","0854789221","_______________________________________"));
//        sonList.add(new ModelClass(R.drawable.gi,"maroni zwina","123456789","_______________________________________"));

    }


}