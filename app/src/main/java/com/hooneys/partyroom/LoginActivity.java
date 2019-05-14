package com.hooneys.partyroom;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.hooneys.partyroom.Application.MyApp;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private final String TAG = LoginActivity.class.getSimpleName();

    private long lastTouchTime = 0;
    private long currentTouchTime = 0;

    private EditText room, key;
    private Button loginBTN;
    private Spinner typeSpinner;
    private int roomType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();
        setEvents();
    }

    private void setEvents() {
        loginBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lastTouchTime = currentTouchTime;
                currentTouchTime = System.currentTimeMillis();

                if (currentTouchTime - lastTouchTime < 250) {
                    lastTouchTime = 0;
                    currentTouchTime = 0;

                    checkText();
                }
            }
        });
    }

    private void checkText() {
        if(roomType == 0){
            //Add
            addRoom();
        }else if(roomType == 1){
            //Enter
            enterRoom();
        }
    }

    private void enterRoom() {
        MainActivity.rootRef.child("Room")
                .child("Info")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String s_room = room.getText().toString();
                        String s_key = key.getText().toString();
                        boolean isCheck = false;
                        for(DataSnapshot item : dataSnapshot.getChildren()){
                            if(item.getKey().equals(s_room) && item.getValue().equals(s_key)){
                                MyApp.roomChannel = s_room;
                                isCheck = true;
                                break;
                            }
                        }

                        if(isCheck){
                            intentUserActivity();
                        }else{
                            Toast.makeText(LoginActivity.this, "일치하는 방이 없습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void addRoom() {
        final String s_room = room.getText().toString();
        String s_key = key.getText().toString();

        if(s_room.trim().isEmpty() || s_key.trim().isEmpty()){
            Toast.makeText(this, "방 정보를 정확히 작성해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        MainActivity.rootRef.child("Room")
                .child("Info")
                .child(s_room)
                .setValue(s_key)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                        Toast.makeText(LoginActivity.this, "방 생성에 실패했습니다.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        MyApp.roomChannel = s_room;
                        intentUserActivity();
                    }
                });
    }

    private void intentUserActivity(){
        startActivity(new Intent(getApplicationContext(), UserActivity.class));
        finish();
    }

    private void init() {
        room = (EditText) findViewById(R.id.login_room_num);
        key = (EditText) findViewById(R.id.login_room_key);
        loginBTN = (Button) findViewById(R.id.login_btn);
        typeSpinner = findViewById(R.id.login_room_type);
        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                roomType = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                roomType = 0;
            }
        });
    }
}
