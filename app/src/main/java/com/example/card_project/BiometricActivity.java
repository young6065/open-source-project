package com.example.card_project;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.example.card_project.R;

import java.util.concurrent.Executor;

public class BiometricActivity extends AppCompatActivity {

    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_biometric );


        executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(this,
                executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode,
                                              @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(getApplicationContext(),
                                R.string.auth_error_message, Toast.LENGTH_SHORT)
                        .show();
            }

            @Override
            public void onAuthenticationSucceeded(
                    @NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(getApplicationContext(),"지문 인식을 성공했습니다.",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(getApplicationContext(), R.string.auth_fail_message,
                                Toast.LENGTH_SHORT)
                        .show();
            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("지문 인증")
                .setSubtitle("기기에 등록된 지문을 이용하여 지문을 인증해주세요.")
                .setNegativeButtonText("취소")
                .setDeviceCredentialAllowed(false)
                .build();

        //  사용자가 다른 인증을 이용하길 원할 때 추가하기

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Button biometricLoginButton = findViewById(R.id.buttonAuthWithFingerprint);
        biometricLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                biometricPrompt.authenticate(promptInfo);
            }
        });

    }
}