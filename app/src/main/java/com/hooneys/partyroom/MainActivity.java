package com.hooneys.partyroom;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.hooneys.partyroom.Application.MyApp;
import com.hooneys.partyroom.DO.User;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements LocationListener, OnMapReadyCallback {
    private final String TAG = MainActivity.class.getSimpleName();
    private final int SIGNAL_PERMISSION = 1001;
    private final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; //10미터 당
    private final long MIN_TIME_UPDATES = 1000 * 30 * 1; // 30초마다
    private final int SIGNAL_LOCATION = 2001;

    private LocationManager manager;
    private Location nowAppLocation;
    private ArrayList<User> userList;
    private SupportMapFragment maps;
    private FloatingActionButton floatMsg, floatLocation;
    private GoogleMap tempMap;

    private RelativeLayout notifyLayout;
    private TextView notifyText;
    private ImageButton notifyClose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        getData();
        initGPS();
    }

    private void initGPS() {
        int flag = checkLocationManager();
        if (flag == 0) {
            //permission require
            callPermission();
        }else if(flag == 1) {
            //need wifi or location
            Toast.makeText(getApplicationContext(), "GPS 또는 Wifi 를 실행해주세요.", Toast.LENGTH_SHORT).show();
        }else{
            //start OK.
            startGPS();
        }
    }

    private void getData() {
        userList = new ArrayList<>();

        User item1 = new User();
        item1.setNickName("나");
        item1.setMsg("바쁨");
        item1.setLat(37.4673786f);
        item1.setLon(127.126781f);
        item1.setMarkerColor(BitmapDescriptorFactory.HUE_ORANGE);
        userList.add(item1);

        User item2 = new User();
        item2.setNickName("친구");
        item2.setMsg("친구임");
        item2.setLat(37.4684886f);
        item2.setLon(127.126781f);
        item2.setMarkerColor(BitmapDescriptorFactory.HUE_BLUE);
        userList.add(item2);
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "On Start ... Calling");
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        manager.removeUpdates(MainActivity.this);
        super.onDestroy();
    }

    private int checkLocationManager() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
            &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return 0;
        }else{
            if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER) &&
                    !manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                Log.e(TAG, "Location Net, GPS Func is off...");
                return 1;
            } else {
                return 2;
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void startGPS(){
        Toast.makeText(getApplicationContext(), "모든 권한 확인 완료.", Toast.LENGTH_SHORT).show();

        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Log.i(TAG, "Request Location Update GPS Provider");

            manager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    MIN_TIME_UPDATES,
                    MIN_DISTANCE_CHANGE_FOR_UPDATES, this
            );
        }

        if (manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            Log.i(TAG, "Request Location Update Net Provider");
            manager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    MIN_TIME_UPDATES,
                    MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
        }

        if(manager != null){
            if(manager.getLastKnownLocation(LocationManager.GPS_PROVIDER) != null){
                nowAppLocation = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                Toast.makeText(getApplicationContext(),"GPS 값 받음,", Toast.LENGTH_SHORT).show();
            }else if(manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER) != null){
                nowAppLocation = manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                Toast.makeText(getApplicationContext(),"Wifi 값 받음", Toast.LENGTH_SHORT).show();
            }else{
                // 1. GPS 또는 Wifi 켜지지 않은 경우
                Toast.makeText(getApplicationContext(),"GPS 또는 Wifi 켜주세요,", Toast.LENGTH_SHORT).show();
                nowAppLocation = null;
            }
        }else{
            //Location Error.
            // 1. GPS 또는 Wifi 켜지지 않은 경우
            // 2. GPS Low 인 경우.
            Log.d(TAG, "Location Manager Error.");
        }

        maps.getMapAsync(this);
    }

    private void init() {
        manager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);

        maps = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);

        notifyLayout = findViewById(R.id.main_notify_layout);
        notifyText = findViewById(R.id.main_notify_text);
        notifyClose = findViewById(R.id.main_notify_close);

        notifyClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notifyLayout.setVisibility(View.GONE);
            }
        });

        floatMsg = (FloatingActionButton) findViewById(R.id.main_floating_msg);
        floatMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                alert.setTitle("공지사항");
                View view = getLayoutInflater().inflate(R.layout.dialog_notify, null);
                Button notifyShow = view.findViewById(R.id.dialog_notify_show_btn);
                Button notify_create = view.findViewById(R.id.dialog_notify_create_btn);
                final AlertDialog dialog = alert.setView(view).show();

                notifyShow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(notifyLayout.getVisibility() != View.VISIBLE){
                            notifyLayout.setVisibility(View.VISIBLE);
                        }
                        dialog.dismiss();
                    }
                });

                notify_create.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Firebase 연결!
                        dialog.dismiss();
                        askNotify();
                    }
                });

            }
        });
        floatLocation = (FloatingActionButton) findViewById(R.id.main_floating_location);
        floatLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(
                        new Intent(getApplicationContext(), LocationActivity.class),
                        SIGNAL_LOCATION
                );
            }
        });
    }

    private void callPermission(){
        if(checkAllPermission()){
            startGPS();
        }else{
            commitPermission();
        }
    }

    private boolean checkAllPermission(){
        boolean isAll = true;
        int permissionCheck = PackageManager.PERMISSION_GRANTED;
        for (int i = 0; i < MyApp.permissions.length; i++) {
            permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this, MyApp.permissions[i]);
            if (permissionCheck == PackageManager.PERMISSION_DENIED) {
                isAll = false;
                break;
            }
        }
        return isAll;
    }

    private void commitPermission(){
        ActivityCompat.requestPermissions(MainActivity.this, MyApp.permissions, SIGNAL_PERMISSION);
    }

    private void requirePermission(){
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("권한 설정");
        alert.setMessage("동작하기 위해서는 모든 권한이 필요합니다.");
        alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                callPermission();
            }
        });
        alert.setNegativeButton("종료", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               finish();
            }
        });
        alert.show();
    }

    @Override
    public void onLocationChanged(Location location) {
        nowAppLocation = location;
        Log.d(TAG, "First init Location : " + nowAppLocation.getLatitude()
                + " / " + nowAppLocation.getLongitude() + " / Acc : "
                + nowAppLocation.getAccuracy() + " / time : "
                + nowAppLocation.getTime() + " / provider : "
                + nowAppLocation.getProvider());
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] perms, int[] grantResults) {
        if (requestCode == SIGNAL_PERMISSION) {
            boolean isAll = true;
            for(int temp : grantResults){
                if(temp == PackageManager.PERMISSION_DENIED){
                    isAll = false;
                    break;
                }
            }

            if(isAll){
                startGPS();
            }else{
                requirePermission();
            }
        }
    }//end of onRequestPermissionsResult

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        Log.d(TAG, "on Map Ready...");
        tempMap = googleMap;
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                askAppointment(googleMap, latLng);
            }
        });

        float total_lat = 0.0f, total_lon = 0.0f;
        for(User user : userList){
            addMarker(googleMap, user.getLat(),
                    user.getLon(), user.getMarkerColor(),
                    user.getNickName(), user.getMsg());
            total_lat+= user.getLat();
            total_lon+= user.getLon();
        }
        googleMap.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                        new LatLng(
                                (total_lat/(float) userList.size()),
                                (total_lon/(float) userList.size())
                        ),
                        14)
        );

        googleMap.setOnInfoWindowLongClickListener(new GoogleMap.OnInfoWindowLongClickListener() {
            @Override
            public void onInfoWindowLongClick(final Marker marker) {
                String title = marker.getTitle();
                if(title.contains("메모")){
                    AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                    alert.setTitle("약속장소")
                            .setMessage("약속 장소를 삭제합니다.")
                            .setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    marker.remove();
                                }
                            }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
                }
            }
        });


    }

    private void askAppointment(final GoogleMap googleMap, final LatLng latLng) {
        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
        alert.setTitle("약속 장소 설정");
        View view = getLayoutInflater().inflate(R.layout.dialog_just_edit, null);
        final EditText appoint_edit = (EditText) view.findViewById(R.id.dialog_just_edit_edit);
        alert.setView(view).setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                addAppoint(googleMap, latLng, appoint_edit.getText().toString());
            }
        }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();

    }

    private void askNotify() {
        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
        alert.setTitle("공지사항 작성");
        View view = getLayoutInflater().inflate(R.layout.dialog_just_edit, null);
        final EditText notify_edit = (EditText) view.findViewById(R.id.dialog_just_edit_edit);
        notify_edit.setHint("공지사항을 작성해주세요.");
        notify_edit.setGravity(Gravity.LEFT);
        alert.setView(view).setPositiveButton("작성완료", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(notify_edit.getText().length() > 0){
                    notifyText.setText(notify_edit.getText().toString());
                    if(notifyLayout.getVisibility() != View.VISIBLE){
                        notifyLayout.setVisibility(View.VISIBLE);
                    }
                }
            }
        }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();

    }

    private void addMarker(GoogleMap googleMap, float lat, float lon, float marker, String title, String msg){
        googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(lat, lon)) //37.450626, 127.128847
                .icon(BitmapDescriptorFactory.defaultMarker(marker))
                .snippet(msg)
                .title(title)
                .zIndex((float) 1));
    }

    private void addAppoint(GoogleMap googleMap, LatLng latLng, String title){
        googleMap.addMarker(new MarkerOptions()
                .position(latLng)
                .icon(MyApp.getAppointMarker(getResources().getDrawable(R.drawable.ic_flag_black_24dp, null)))
                .snippet("등록인 : "+ "나")
                .title("메모 : " + title)
                .zIndex((float) 1));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode){
            case SIGNAL_LOCATION:
                if(resultCode == RESULT_OK){
                    //성공
                    float lat = data.getFloatExtra("location_lat", 0.0f);
                    float lon = data.getFloatExtra("location_lon", 0.0f);
                    String location = data.getStringExtra("location_name");
                    Toast.makeText(getApplicationContext(), "받은 위치는 : " + lat + " / " + lon + " / " + location, Toast.LENGTH_SHORT).show();

                    addAppoint(tempMap, new LatLng(lat, lon), location);

                }else{
                    //실패
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }
}
