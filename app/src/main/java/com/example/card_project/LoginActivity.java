package com.example.card_project;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Matrix;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        findViewById(R.id.loginButton).setOnClickListener(onClickListener);
        findViewById(R.id.gotoPassword).setOnClickListener(onClickListener);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @SuppressLint("NonConstantResourceId")
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.loginButton:
                    login();
                    break;

                case R.id.gotoPassword:
                    myStartActivity(PasswordResetActivity.class);
                    break;
            }
        }
    };



    private void login() { //로그인 로직 처리
        String email = ((EditText) findViewById(R.id.user_id)).getText().toString(); //입력한 텍스트를 얻어오는 작업
        String password = ((EditText) findViewById(R.id.user_password)).getText().toString();

        if (email.length() > 0 && password.length() > 0) {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                if(mAuth.getCurrentUser().isEmailVerified()){
                                    // 로그인에 성공하면 "로그인 성공" 토스트를 보여줌
                                    Toast.makeText(LoginActivity.this, "로그인에 성공했습니다.", Toast.LENGTH_SHORT).show();
                                    FirebaseUser user = mAuth.getCurrentUser();

                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();

                            } else {
                                    Toast.makeText(LoginActivity.this, "이메일 인증을 완료해주세요.", Toast.LENGTH_SHORT).show();
                                }
                                }else {
                                    // 로그인에 실패하면 "로그인 실패" 토스트를 보여줌
                                    Toast.makeText(LoginActivity.this, "로그인에 실패했습니다.", Toast.LENGTH_SHORT).show();
                                }
                                }

                    });

                    }
        }


    private void startToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void myStartActivity(Class c) {
        Intent intent = new Intent(LoginActivity.this, c);
        startActivity(intent);
    }

}
