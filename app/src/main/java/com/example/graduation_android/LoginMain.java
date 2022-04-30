package com.example.graduation_android;

import android.content.Intent;

import android.graphics.Color;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.graduation_android.logindata.JoinData;
import com.example.graduation_android.logindata.JoinResponse;
import com.example.graduation_android.logindata.LoginData;
import com.example.graduation_android.logindata.LoginResponse;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginMain extends AppCompatActivity {
    private final String URL = "http://10.0.2.2:8001"; //사용할 URL (localhost)

    private final String TAG = "LoginMain";

    EditText inputId, inputPw;
    Button loginBtn, joinBtn;
    TextView msgFromNode;
    TextView txtId;

    private Retrofit retrofit;
    private LoginServiceApi service;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_main);

        inputId = findViewById(R.id.login_id_edittxt);
        inputPw = findViewById(R.id.login_pw_edittxt);
        loginBtn = findViewById(R.id.login_button);
        joinBtn = findViewById(R.id.join_button);
        msgFromNode = findViewById(R.id.msg_from_node);
        txtId = findViewById(R.id.login_id_txt);

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
                Intent intent = new Intent(getApplicationContext(), JoinMain.class);
                startActivity(intent);
            }
        });


        //로그인 버튼 클릭 시 동작
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = inputId.getText().toString();
                String password = inputPw.getText().toString();

                startLogin(new LoginData(email, password));
            }
        });


        /* retrofit test */
        /*
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Call<ResponseBody> call_get = service.getFunc("get data");
                call_get.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if(response.isSuccessful()) {
                            try {
                                String result = response.body().string();
                                Log.v(TAG, "result= " + result);
                                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        else {
                            Log.v(TAG, "err= " + String.valueOf(response.code()));
                            Toast.makeText(getApplicationContext(), "err= " + String.valueOf(response.code()), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.v(TAG, "fail");
                        Toast.makeText(getApplicationContext(), "response fail", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

         */
    }



    /* 로그인 */
    private void startLogin(LoginData data) {
        service.userLogin(data).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if(response.isSuccessful()) {
                    LoginResponse result = response.body();
                    Log.v(TAG, "result= " + result.getMessage());
                    Toast.makeText(LoginMain.this, result.getMessage(), Toast.LENGTH_SHORT).show();

                    if(result.getMessage().equals("login success")) {
                        txtId.setTextColor(Color.BLUE);
                    }
                    else {
                        txtId.setTextColor(Color.RED);
                    }
                }
                else {
                    Log.v(TAG, "err= " + String.valueOf(response.code()));
                    Toast.makeText(getApplicationContext(), "response error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(LoginMain.this, "접속 에러", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "접속 에러 발생");
                t.printStackTrace();
            }
        });
    }
}
