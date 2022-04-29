package com.example.graduation_android;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
<<<<<<< HEAD
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
=======
import android.widget.Toast;

import androidx.annotation.Nullable;
>>>>>>> eb84c441a00721d58a9fac9632f8f70e8be6ea5c
import androidx.appcompat.app.AppCompatActivity;

import com.example.graduation_android.logindata.JoinData;
import com.example.graduation_android.logindata.JoinResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class JoinMain extends AppCompatActivity {
<<<<<<< HEAD
    private final String URL = "http://10.0.2.2:8001"; //사용할 URL (localhost)
    private final String TAG = "JoinMain";

    EditText inputId, inputNick, inputPw, inputPhone;
=======
    private final String URL = "http://10.0.2.2:8001";
    private final String TAG = "JoinMain";

    EditText inputId, inputNick, inputPw;
>>>>>>> eb84c441a00721d58a9fac9632f8f70e8be6ea5c
    Button joinBtn;

    private Retrofit retrofit;
    private LoginServiceApi service;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.join_main);

        inputId = findViewById(R.id.join_id_edittxt);
        inputNick = findViewById(R.id.join_nick_edittxt);
        inputPw = findViewById(R.id.join_pw_edittxt);
<<<<<<< HEAD
        inputPhone = findViewById(R.id.join_phone_edittxt);
        joinBtn = findViewById(R.id.join_main_button);
=======
>>>>>>> eb84c441a00721d58a9fac9632f8f70e8be6ea5c

        /* retrofit2 */
        retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create()) //json 분석하기 위해 추가
                .build();
        service = retrofit.create(LoginServiceApi.class);

<<<<<<< HEAD

=======
>>>>>>> eb84c441a00721d58a9fac9632f8f70e8be6ea5c
        //회원가입 버튼 클릭 시 동작
        joinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = inputId.getText().toString();
                String nick = inputNick.getText().toString();
                String password = inputPw.getText().toString();
<<<<<<< HEAD
                String phone = inputPhone.getText().toString();


                startJoin(new JoinData(email, nick, password, phone));
            }
        });
    }

=======
                
                startJoin(new JoinData(email, nick, password));
            }
        });
    }
    
    
    /* 회원가입 */
>>>>>>> eb84c441a00721d58a9fac9632f8f70e8be6ea5c
    private void startJoin(JoinData data) {
        service.userJoin(data).enqueue(new Callback<JoinResponse>() {
            @Override
            public void onResponse(Call<JoinResponse> call, Response<JoinResponse> response) {
<<<<<<< HEAD
                AlertDialog.Builder builder = new AlertDialog.Builder(JoinMain.this);
                builder.setTitle("회원가입 오류").setMessage("회원가입에 에러가 발생했습니다.");
                if(response.isSuccessful()) {
                    JoinResponse result = response.body();
                    Toast.makeText(JoinMain.this, result.getMessage(), Toast.LENGTH_SHORT).show();

                    if(result.getMessage().equals("already exist")) {
                        builder.show();
                    }
                    if(result.getMessage().equals("join success")) {
                        joinBtn.setBackgroundColor(Color.BLUE);
                    }
=======
                JoinResponse result = response.body();
                Toast.makeText(JoinMain.this, result.getMessage(), Toast.LENGTH_SHORT).show();
                if(result.getMessage().equals("login success")) {
                    joinBtn.setBackgroundColor(Color.BLUE);
                }
                else {
                    joinBtn.setBackgroundColor(Color.RED);
>>>>>>> eb84c441a00721d58a9fac9632f8f70e8be6ea5c
                }
            }

            @Override
            public void onFailure(Call<JoinResponse> call, Throwable t) {
<<<<<<< HEAD
                Toast.makeText(JoinMain.this, "회원가입 에러", Toast.LENGTH_SHORT).show();
                Log.e("회원가입 에러 발생", t.getMessage());
            }
        });
    }

}
=======
                Toast.makeText(getApplicationContext(), "접속 에러", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "접속 에러 발생");
                t.printStackTrace();
            }
        });
    }
    
}
>>>>>>> eb84c441a00721d58a9fac9632f8f70e8be6ea5c
