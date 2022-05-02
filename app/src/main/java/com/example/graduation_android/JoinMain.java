package com.example.graduation_android;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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
    private final String URL = "http://10.0.2.2:8001"; //사용할 URL (localhost)
    private final String TAG = "JoinMain";

    EditText inputId, inputNick, inputPw, inputPhone;

    Button joinBtn;

    private Retrofit retrofit;
    private LoginServiceApi service;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.join_main);

        inputId = findViewById(R.id.join_id_edittxt);
        inputNick = findViewById(R.id.join_nick_edittxt);
        inputPw = findViewById(R.id.join_pw_edittxt);
        inputPhone = findViewById(R.id.join_phone_edittxt);
        joinBtn = findViewById(R.id.join_main_button);


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
                String phone = inputPhone.getText().toString();

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
                    Toast.makeText(JoinMain.this, result.getMessage(), Toast.LENGTH_SHORT).show();

                    if (result.getMessage().equals("already exist")) {
                        builder.show();
                    }
                    if (result.getMessage().equals("join success")) {
                        joinBtn.setBackgroundColor(Color.BLUE);
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

}
