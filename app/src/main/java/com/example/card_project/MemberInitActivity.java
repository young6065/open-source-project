package com.example.card_project;

import android.app.Activity;
import android.content.Intent;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.example.card_project.MemberInfo;
import com.example.card_project.R;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import static com.example.card_project.Util.INTENT_PATH;
import static com.example.card_project.Util.showToast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;



public class MemberInitActivity extends AppCompatActivity {
    private static final String TAG = "MemberInitActivity";

    private ImageView profileImageView;
    private FirebaseStorage storage;

    private String profilePath;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member);
        profileImageView = findViewById(R.id.profileImageView);

        findViewById(R.id.profileImageView).setOnClickListener(onClickListener);

        profileImageView = findViewById(R.id.profileImageView);
        storage = FirebaseStorage.getInstance();

        profileImageView.setOnClickListener(onClickListener);
        findViewById(R.id.OK).setOnClickListener(onClickListener);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0: {
                if (resultCode == Activity.RESULT_OK) {
                    profilePath = data.getStringExtra(INTENT_PATH);
                    Glide.with(this).load(profilePath).centerCrop().override(500).into(profileImageView);
                }
                break;
            }
        }
    }


    View.OnClickListener onClickListener = v -> {
        switch (v.getId()) {
            case R.id.profileImageView:
                myStartActivity(GalleryActivity.class);
                break;
            case R.id.OK:
                profileUpdate();
                break;
        }

    };



    private void profileUpdate() {
        final String name = ((EditText) findViewById(R.id.username)).getText().toString();
        final String num1 = ((EditText) findViewById(R.id.usernum1)).getText().toString();
        final String num2 = ((EditText) findViewById(R.id.usernum2)).getText().toString();
        final String rank = ((EditText) findViewById(R.id.rankG)).getText().toString();
        final String address = ((EditText) findViewById(R.id.address)).getText().toString();
        final String date1 = ((EditText) findViewById(R.id.date)).getText().toString();



        if (name.length() > 0 && num1.length() > 0 && num2.length() > 0 && rank.length() > 0 && address.length() >0 && date1.length()>0) {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            user = FirebaseAuth.getInstance().getCurrentUser();
            final StorageReference mountainImagesRef = storageRef.child("users/" + user.getUid() + "/profileImage.jpg");

            if (profilePath == null) {
                MemberInfo memberInfo = new MemberInfo(name, num1, num2, rank, address, date1);
                storeUploader(memberInfo);
            } else {
                try {
                    InputStream stream = new FileInputStream(new File(profilePath));
                    UploadTask uploadTask = mountainImagesRef.putStream(stream);
                    uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            return mountainImagesRef.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadUri = task.getResult();

                                MemberInfo memberInfo = new MemberInfo(name, num1, num2, rank, address, date1, downloadUri.toString());
                                storeUploader(memberInfo);
                            } else {
                                showToast(MemberInitActivity.this, "회원정보를 보내는데 실패하였습니다.");
                            }
                        }
                    });
                } catch (FileNotFoundException e) {
                    Log.e("로그", "에러: " + e.toString());
                }
            }
        } else {
            showToast(MemberInitActivity.this, "복지카드정보를 입력해주세요.");
        }
    }

    private void storeUploader(MemberInfo memberInfo) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(user.getUid()).set(memberInfo)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        showToast(MemberInitActivity.this, "회원정보 등록을 성공하였습니다.");
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showToast(MemberInitActivity.this, "회원정보 등록에 실패하였습니다.");
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }


    private void startToast(String msg){
        Toast.makeText(this, msg,Toast.LENGTH_SHORT).show();
    }

    private void myStartActivity(Class c) {
        Intent intent = new Intent(this, c);
        startActivityForResult(intent, 0);

    }

}