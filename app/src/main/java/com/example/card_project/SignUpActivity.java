package com.example.card_project;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.jetbrains.annotations.NotNull;

public class SignUpActivity extends AppCompatActivity {
    private static final String TAG = "SignUpActivity";
    private FirebaseAuth mAuth;
    private AuthemailDialog authemailDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        authemailDialog = new AuthemailDialog(this, positiveListener);
        findViewById(R.id.check).setOnClickListener(onClickListener);
        findViewById(R.id.loginButton).setOnClickListener(onClickListener);
    }




    View.OnClickListener onClickListener = new View.OnClickListener() {
        @SuppressLint("NonConstantResourceId")
        @Override
        public void onClick(View view) {
            switch (view.getId()){

                case R.id.check:
                    signUp();
                    break;
                case R.id.loginButton:
                    startLoginActivity();
                    break;
            }
        }
    };

    private View.OnClickListener positiveListener = new View.OnClickListener() {
        public void onClick(View v) {
            // 다이얼로그 종료
            authemailDialog.dismiss();
            // 로그인 화면으로 이동
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        }
    };



    private void signUp() {
        String email = ((EditText)findViewById(R.id.user_id)).getText().toString();
        String password = ((EditText)findViewById(R.id.user_password)).getText().toString();
        String passwordCheck = ((EditText)findViewById(R.id.user_password_check)).getText().toString();


        if (email.length() > 0 && password.length() > 0 && passwordCheck.length() > 0)
        {

            if(password.equals(passwordCheck)){
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(SignUpActivity.this,"메일 발송완료", Toast.LENGTH_SHORT).show();

                                           authemailDialog.show();
                                        }
                                    });

                                    //성공했을 때 UI로직

                                } else {
                                    if(task.getException() != null){
                                        // If sign in fails, display a message to the user.
                                        startToast(task.getException().toString());
                                        //실패했을 때 UI로직
                                    }
                                }
                            }
                        });
            }else{
                startToast("비밀번호가 일치하지 않습니다.");
            }
        }else{
            startToast("입력한 정보를 다시 한 번 확인해주세요.");
        }

    }

    public static class AuthemailDialog extends Dialog {

        private Button positivebutton;
        private View.OnClickListener positiveListener;

        public AuthemailDialog(@NonNull Context context, View.OnClickListener positiveListener) {

            super(context);
            this.positiveListener = positiveListener;
        }
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // 다이얼로그 밖의 화면은 흐리게 함
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            layoutParams.dimAmount = 0.8f;
            getWindow().setAttributes(layoutParams);
            setContentView(R.layout.authemail_dialog);

            // 확인 버튼
            positivebutton = findViewById(R.id.positivebutton);
            // 클릭 리스너
            positivebutton.setOnClickListener(positiveListener);
        }
    }

    private void startToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
    private void startLoginActivity(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}
