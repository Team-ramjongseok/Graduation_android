package com.example.graduation_android;

import android.content.Intent;

import android.content.SharedPreferences;
import android.graphics.Color;

import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.graduation_android.logindata.JoinData;
import com.example.graduation_android.logindata.JoinResponse;
import com.example.graduation_android.logindata.LoginData;
import com.example.graduation_android.logindata.LoginResponse;
import com.example.graduation_android.logindata.TokenApi;
import com.google.gson.annotations.SerializedName;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginMain extends AppCompatActivity {
    private final String URL = "http://10.0.2.2:8001/"; //사용할 URL (localhost)

    private final String TAG = "LoginMain";

    EditText inputId, inputPw;
    Button loginBtn;
    TextView txtId, joinBtn;
    ImageView goBack;

    private Retrofit retrofit;
    private LoginServiceApi service;
    private Interceptor interceptor; //토큰 통신용 인터셉터 (헤더 추가)
    private SharedPreferences preferences; //토큰 저장 공간

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_main);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        inputId = findViewById(R.id.login_id_edittxt);
        inputPw = findViewById(R.id.login_pw_edittxt);
        loginBtn = findViewById(R.id.login_button);
        joinBtn = findViewById(R.id.join_button);
        txtId = findViewById(R.id.login_id_txt);
        goBack = findViewById(R.id.go_back_login);

        //sign up 글씨에 밑줄 긋기
        SpannableString underlineTxt = new SpannableString(joinBtn.getText().toString());
        underlineTxt.setSpan(new UnderlineSpan(), 0, underlineTxt.length(), 0);
        joinBtn.setText(underlineTxt);

        /* retrofit2 */
        retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create()) //json 분석하기 위해 추가
                .build();
        service = retrofit.create(LoginServiceApi.class);

        /* sharedPreference : 앱 내에서만 데이터가 사용되도록 */
        preferences = getSharedPreferences("Tokens", MODE_PRIVATE);


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
        
        //뒤로가기 버튼 클릭 시 동작
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
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

                    /* 로그인 성공했을 경우 */
                    if(result.getMessage().equals("login success")) {
                        Toast.makeText(LoginMain.this, result.getMessage(), Toast.LENGTH_SHORT).show();
                        txtId.setTextColor(Color.BLUE);

                        /* SharedPreferences */
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("accessToken", result.getAccessToken());
                        editor.putString("refreshToken", result.getRefreshToken());
                        editor.commit();
                        getPreferences();


//                        String tokenStr = null;
//                        /* 토큰 통신을 위한 interceptor */
//                        interceptor = new Interceptor() {
//                            @NonNull
//                            @Override
//                            public okhttp3.Response intercept(@NonNull Chain chain) throws IOException {
//                                String token = preferences.getString("accessToken", ""); //토큰이 존재하지 않을경우 빈 값을 저장
//                                Request newRequest;
//                                if(token!=null && !token.equals("")) { //토큰이 없는 경우
//                                    //Authorization 헤더에 토큰 추가
//                                    newRequest = chain.request().newBuilder().addHeader("LoginData", token).build();
//                                    Date expireDate = result.getExpireTime();
//                                    if(expireDate.getTime() <= System.currentTimeMillis()) { //토큰이 만료되었다면
//                                        //refreshToken 갱신 필요 -> api 호출
//                                        TokenApi api = null;
//                                        Map<String, String> bodyToken = api.refreshToken(preferences.getString("refreshToken", "")).execute().body();
//
//                                        if(bodyToken!=null) {
//                                            //클라이언트의 토큰 갱신 수행
//                                            newRequest = chain.request().newBuilder().addHeader("accessToken", tokenStr).build();
//                                            return chain.proceed(newRequest);
//                                        }
//                                    }
//                                } else newRequest = chain.request();
//                                return chain.proceed(newRequest);
//                            }
//                        };
//
//                        OkHttpClient.Builder builder = new OkHttpClient.Builder();
//                        builder.interceptors().add(interceptor);
//                        OkHttpClient client = builder.build();
//
//                        TokenApi api = retrofit.create(TokenApi.class);



                    }
                    else {
                        Toast.makeText(LoginMain.this, result.getMessage(), Toast.LENGTH_SHORT).show();
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

    public class User {
        @SerializedName("email")
        private String email;

        @SerializedName("password")
        private String password;

        @SerializedName("phone")
        private String phone;

        @SerializedName("nickname")
        private String nickname;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

    }

    private void getPreferences() {
        Log.e(TAG, "saved token: "+preferences.getString("accessToken", ""));
    }


}
