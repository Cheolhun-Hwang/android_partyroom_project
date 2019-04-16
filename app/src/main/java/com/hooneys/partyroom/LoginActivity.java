package com.hooneys.partyroom;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {
    private final String TAG = LoginActivity.class.getSimpleName();
    private final String ROOM_NUMBER = "1001";
    private final String ROOM_KEY = "1458";
    private final String ROOM_MEMBER = "4";

    private long lastTouchTime = 0;
    private long currentTouchTime = 0;

    private boolean isClick;
    private EditText room, key, member;
    private Button loginBTN;

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
        String s_room = room.getText().toString();
        String s_key = key.getText().toString();
        String s_memeber = member.getText().toString();

        Log.d(TAG, s_room+"/"+s_key+"/"+s_memeber);

        if(s_room.equals("") || s_key.equals("") || s_memeber.equals("")){
            Toast.makeText(getApplicationContext(), "모두 작성해주세요.", Toast.LENGTH_SHORT).show();
        }else{
            if(s_room.equals(ROOM_NUMBER) && s_key.equals(ROOM_KEY) && s_memeber.equals(ROOM_MEMBER)){
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }else{
                //하나라도 틀리면 이쪽
                Toast.makeText(getApplicationContext(), "올바른 정보가 아닙니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void init() {
        isClick = false;

        room = (EditText) findViewById(R.id.login_room_num);
        key = (EditText) findViewById(R.id.login_room_key);
        member = (EditText) findViewById(R.id.login_room_member);
        loginBTN = (Button) findViewById(R.id.login_btn);
    }
}
