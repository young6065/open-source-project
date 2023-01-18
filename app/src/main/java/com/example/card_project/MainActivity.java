package com.example.card_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


        if (user == null) {
            myStartActivity(SignUpActivity.class);

        } else {

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference docRef = db.collection("users").document(user.getUid());
            docRef.get().addOnCompleteListener((task) -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        if (document.exists()) {
                            Log.e(TAG, "DocumentSnapshot data: " + document.getData());
                        } else {
                            Log.d(TAG, "No such document");
                            myStartActivity(MemberInitActivity.class);


                        }
                    }
                } else {
                    Log.d(TAG, "get failed with", task.getException());

                }
            });
            BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
            bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {

                        case R.id.home:
                            myStartActivity(MainActivity.class);
                            myStartActivity(BiometricActivity.class);
                            return true;
                        case R.id.user:
                            UserInfoFragment userInfoFragment = new UserInfoFragment();
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.container, userInfoFragment)
                                    .commit();
                            return true;
                        case R.id.userList:
                            myStartActivity(MainAct.class);
                            break;
                        case R.id.map:
                            myStartActivity(MapMainActivity.class);
                            break;



                    }
                    return false;
                }
            });





        }




        findViewById(R.id.logoutButton).setOnClickListener(onClickListener);

    }
    View.OnClickListener onClickListener = (v) -> {

        switch (v.getId()) {
            case R.id.logoutButton:
                FirebaseAuth.getInstance().signOut();
                myStartActivity(SignUpActivity.class);
                break;


        }
    };




    private void myStartActivity(Class c) {
        Intent intent = new Intent(MainActivity.this, c);
        startActivity(intent);
    }
}

