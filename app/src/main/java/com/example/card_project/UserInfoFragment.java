package com.example.card_project;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

public class UserInfoFragment extends Fragment {
    private static final String TAG ="UserInfoFragment";

    public UserInfoFragment(){

    }
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_user_info,container, false);

        final ImageView profileImageView = view.findViewById(R.id.profileImageView);
        final TextView nametxt = view.findViewById(R.id.nameText);
        final TextView num1txt = view.findViewById(R.id.num1Text);
        final TextView num2txt = view.findViewById(R.id.num2Text);
        final TextView ranktxt = view.findViewById(R.id.rankText);
        final TextView addresstxt = view.findViewById(R.id.addressText);
        final TextView datetxt = view.findViewById(R.id.dateText);


        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {


            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        if (document.exists()) {
                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                            if(document.getData().get("photoUrl") != null){
                                Glide.with(getActivity()).load(document.getData().get("photoUrl")).centerCrop().override(500).into(profileImageView);
                            }
                            nametxt.setText(document.getData().get("name").toString());
                            num1txt.setText(document.getData().get("num1").toString());
                            num2txt.setText(document.getData().get("num2").toString());
                            ranktxt.setText(document.getData().get("rank").toString());
                            addresstxt.setText(document.getData().get("address").toString());
                            datetxt.setText(document.getData().get("date1").toString());

                        } else {
                            Log.d(TAG, "No such document");
                        }
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onPause(){
        super.onPause();
    }

}
