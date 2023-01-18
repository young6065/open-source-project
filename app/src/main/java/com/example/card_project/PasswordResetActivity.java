package com.example.card_project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.card_project.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class PasswordResetActivity extends AppCompatActivity {
    private static final String TAG = "PasswordResetActivity";
    private FirebaseAuth mAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);
        mAuth = FirebaseAuth.getInstance();
        findViewById(R.id.sendButton).setOnClickListener(onClickListener);

    }


    View.OnClickListener onClickListener = v -> {
        switch (v.getId()) {
            case R.id.sendButton:
                send();
                break;
        }
    };


    private void send() {
        String email = ((EditText) findViewById(R.id.user_id)).getText().toString();

        if (email.length() > 0) {
            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                startToast("이메일을 보냈습니다.");

                            }
                        }

                    });

        } else {
            startToast("이메일 또는 비밀번호를 입력해 주새요.");
        }


    }

    private void startToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }


}


