package com.hooneys.partyroom;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.hooneys.partyroom.DO.User;

import java.util.HashMap;

public class UserActivity extends AppCompatActivity {
    private final String TAG = UserActivity.class.getSimpleName();

    private EditText nickName, pwd;
    private Spinner markerColor, loginType;
    private Button enterBtn;
    private int typeLogin;
    private float markerColorFloat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        init();
    }

    private void init() {
        nickName = findViewById(R.id.user_nickname);
        pwd = findViewById(R.id.user_pwd);
        markerColor = findViewById(R.id.user_marker_spinner);
        markerColor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] markerFloats = getResources().getStringArray(R.array.map_markers_float);
                markerColorFloat = Float.parseFloat(markerFloats[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                markerColorFloat = 0.0f;
            }
        });
        loginType = findViewById(R.id.user_login_type);
        loginType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                typeLogin = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                typeLogin = 0;
            }
        });
        enterBtn = findViewById(R.id.user_enter_btn);
        enterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(typeLogin == 0){
                    //Add
                    addUser();
                }else if(typeLogin == 1){
                    //Enter
//                    enterUser();
                }
            }
        });

    }

    private void addUser() {
        final String s_nickname = nickName.getText().toString();
        String s_pwd = pwd.getText().toString();

        if(s_nickname.trim().isEmpty() || s_pwd.trim().isEmpty()){
            Toast.makeText(this, "정보를 모두 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        MainActivity.rootRef.child("User").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot item : dataSnapshot.getChildren()){
                    if(item.getKey().equals(s_nickname)){
                        Toast.makeText(UserActivity.this, "중복된 닉네임입니다.", Toast.LENGTH_SHORT).show();
                        break;
                    }else{
                        User user = new User();
                        user.setNickName(s_nickname);
                        user.setMarkerColor(markerColorFloat);
                        MainActivity.rootRef
                                .child("User")
                                .child(s_nickname)
                                .setValue(user, User.class)
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(UserActivity.this, "생성에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                    }
                                });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
