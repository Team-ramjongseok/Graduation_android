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
import com.example.graduation_android.tokens.TokenData;
import com.example.graduation_android.tokens.TokenResponse;
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
    private Interceptor interceptor;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
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


        /* 토큰 갱신을 위한 intercepter */
        interceptor = new Interceptor() {
            @NonNull
            @Override
            public okhttp3.Response intercept(@NonNull Chain chain) throws IOException {
                String acsToken = preferences.getString("accessToken", "");
                String refToken = preferences.getString("refreshToken", "");
                Request newRequest;
                if(acsToken != null && !acsToken.equals("")) { //if token dosen't exist
                    newRequest = chain.request().newBuilder().addHeader("accessToken", acsToken).build();
                    if(System.currentTimeMillis() - preferences.getInt("expiresIn", 0) < 100) {
                        /*
                        토큰 유효기간 얼마 안남았으면
                        기존에 저장되어 있던 유저 정보와 accessToken을 제거하고
                        토큰을 다시 요청해야 함
                         */

                        editor = preferences.edit();
                        editor.remove("nickname");
                        editor.commit();

                        //refreshToken 갱신하기 위해 api 호출
                        tokenCheck(new TokenData(acsToken, refToken));

                        //새로운 토큰들로 갱신
                        acsToken = preferences.getString("accessToken", "");
                        refToken = preferences.getString("refreshToken", "");
                        
                        //통신을 위해 헤더에 추가
                        newRequest = chain.request().newBuilder().addHeader("accessToken", acsToken).build();

                        return chain.proceed(newRequest);
                    }
                }
                else {
                    newRequest = chain.request();
                }
                return chain.proceed(newRequest);
            }
        };

        OkHttpClient.Builder httpBuilder = new OkHttpClient.Builder();
        httpBuilder.interceptors().add(interceptor);


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


                        /* sharedPreferences */
                        editor = preferences.edit();
                        editor.putString("accessToken", result.getAccessToken());
                        editor.putString("refreshToken", result.getRefreshToken());
                        editor.putString("nickname", result.getNickname());
                        editor.putInt("expiresIn", result.getExpiresIn());

                        editor.commit();
                        getPreferences();
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


    /* 토큰 통신 */
    private void tokenCheck(TokenData data) {
        service.userTokens(data).enqueue(new Callback<TokenResponse>() {
            @Override
            public void onResponse(Call<TokenResponse> call, Response<TokenResponse> response) {
                if(response.isSuccessful()) {
                    TokenResponse tokenRes = response.body();
                    Log.v(TAG, "token check init");
                    Toast.makeText(LoginMain.this, "token check init", Toast.LENGTH_SHORT).show();

                    if(tokenRes.getAccessToken() != null) {
                        Toast.makeText(LoginMain.this, "accepted tokens", Toast.LENGTH_SHORT).show();

                        /* sharedPreferences */
                        editor = preferences.edit();
                        editor.putString("accessToken", tokenRes.getAccessToken());
                        editor.putString("refreshToken", tokenRes.getRefreshToken());
                        editor.commit();
                        getPreferences();
                    }
                }
            }

            @Override
            public void onFailure(Call<TokenResponse> call, Throwable t) {
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


    //sharedPreferences 확인용
    private void getPreferences() {
        Log.e(TAG, "saved token: "+preferences.getString("accessToken", ""));
        Log.e(TAG, "saved refresh token: "+preferences.getString("refreshToken", ""));
        Log.e(TAG, "saved nick: "+preferences.getString("nickname", ""));
    }


}
