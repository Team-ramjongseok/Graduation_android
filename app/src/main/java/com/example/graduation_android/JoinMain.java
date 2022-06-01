package com.example.graduation_android;

import android.content.DialogInterface;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;


import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.graduation_android.logindata.JoinData;
import com.example.graduation_android.logindata.JoinResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class JoinMain extends AppCompatActivity {
    private final String URL = "http://3.38.128.16:8001/"; //사용할 URL
    private final String TAG = "JoinMain";

    EditText inputId, inputNick, inputPw, inputPhone;
    Button joinBtn;
    ImageView goBack;

    private Retrofit retrofit;
    private LoginServiceApi service;
    private SharedPreferences preferences; //토큰 저장을 위한 sharedPreferences

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.join_main);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        inputId = findViewById(R.id.join_id_edittxt);
        inputNick = findViewById(R.id.join_nick_edittxt);
        inputPw = findViewById(R.id.join_pw_edittxt);
        inputPhone = findViewById(R.id.join_phone_edittxt);
        joinBtn = findViewById(R.id.join_main_button);
        goBack = findViewById(R.id.go_back_join);

        /* retrofit2 */
        retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create()) //json 분석하기 위해 추가
                .build();
        service = retrofit.create(LoginServiceApi.class);

        /* sharedPreference : 앱 내에서만 데이터가 사용되도록 */
        preferences = getSharedPreferences("Tokens", MODE_PRIVATE);


        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LoginMain.class);
                startActivity(intent);
            }
        });

        //회원가입 버튼 클릭 시 동작
        joinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /* 입력값들중에
                앞 뒤에 공백이 있으면 제거
                UX 개선용
                 */
                String email = inputId.getText().toString().trim();
                String nick = inputNick.getText().toString().trim();
                String password = inputPw.getText().toString().trim();
                String phone = inputPhone.getText().toString().trim();

                startJoin(new JoinData(email, nick, password, phone));
            }
        });
    }



    /* 회원가입 */
    private void startJoin(JoinData data) {
        service.userJoin(data).enqueue(new Callback<JoinResponse>() {
            @Override
            public void onResponse(Call<JoinResponse> call, Response<JoinResponse> response) {
                AlertDialog.Builder builder = new AlertDialog.Builder(JoinMain.this);
                builder.setTitle("회원가입 오류").setMessage("이미 존재하는 계정입니다.");
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });


                if(response.isSuccessful()) {
                    JoinResponse result = response.body();

                    if (result.getMessage().equals("already exist")) {
                        builder.show();
                    }
                    else if (result.getMessage().equals("join success")) {
                        Toast.makeText(JoinMain.this, "로그인 성공, 다시 로그인 해주세요.", Toast.LENGTH_SHORT).show();

                        /* sharedPreferences */
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("accessToken", result.getAccessToken());
                        editor.commit();
                        getPreferences();

                        /* 회원가입 성공 후 메인화면으로 돌아감 */
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);

                    }
                }

            }

            @Override
            public void onFailure(Call<JoinResponse> call, Throwable t) {
                Toast.makeText(JoinMain.this, "회원가입 에러", Toast.LENGTH_SHORT).show();
                Log.e("회원가입 에러 발생", t.getMessage());
            }
        });
    }

    //sharedPreferences 확인용
    private void getPreferences() {
        Log.e(TAG, "saved token: "+preferences.getString("accessToken", ""));
    }
}
