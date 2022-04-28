package com.example.graduation_android;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
    private final String URL = "http://10.0.2.2:8001";
    private final String TAG = "JoinMain";

    EditText inputId, inputNick, inputPw;
    Button joinBtn;

    private Retrofit retrofit;
    private LoginServiceApi service;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.join_main);

        inputId = findViewById(R.id.join_id_edittxt);
        inputNick = findViewById(R.id.join_nick_edittxt);
        inputPw = findViewById(R.id.join_pw_edittxt);

        /* retrofit2 */
        retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create()) //json 분석하기 위해 추가
                .build();
        service = retrofit.create(LoginServiceApi.class);

        //회원가입 버튼 클릭 시 동작
        joinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = inputId.getText().toString();
                String nick = inputNick.getText().toString();
                String password = inputPw.getText().toString();
                
                startJoin(new JoinData(email, nick, password));
            }
        });
    }
    
    
    /* 회원가입 */
    private void startJoin(JoinData data) {
        service.userJoin(data).enqueue(new Callback<JoinResponse>() {
            @Override
            public void onResponse(Call<JoinResponse> call, Response<JoinResponse> response) {
                JoinResponse result = response.body();
                Toast.makeText(JoinMain.this, result.getMessage(), Toast.LENGTH_SHORT).show();
                if(result.getMessage().equals("login success")) {
                    joinBtn.setBackgroundColor(Color.BLUE);
                }
                else {
                    joinBtn.setBackgroundColor(Color.RED);
                }
            }

            @Override
            public void onFailure(Call<JoinResponse> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "접속 에러", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "접속 에러 발생");
                t.printStackTrace();
            }
        });
    }
    
}