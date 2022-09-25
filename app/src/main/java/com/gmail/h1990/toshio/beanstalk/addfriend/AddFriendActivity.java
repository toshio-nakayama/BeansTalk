package com.gmail.h1990.toshio.beanstalk.addfriend;

import static com.gmail.h1990.toshio.beanstalk.common.Constants.EXT_JPG;
import static com.gmail.h1990.toshio.beanstalk.common.Constants.IMAGES_FOLDER;
import static com.gmail.h1990.toshio.beanstalk.common.Extras.USER_KEY;
import static com.gmail.h1990.toshio.beanstalk.common.NodeNames.NAME;
import static com.gmail.h1990.toshio.beanstalk.common.NodeNames.PHOTO;
import static com.gmail.h1990.toshio.beanstalk.common.NodeNames.TALK;
import static com.gmail.h1990.toshio.beanstalk.common.NodeNames.TIME_STAMP;
import static com.gmail.h1990.toshio.beanstalk.common.NodeNames.USERS;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.gmail.h1990.toshio.beanstalk.MainActivity;
import com.gmail.h1990.toshio.beanstalk.R;
import com.gmail.h1990.toshio.beanstalk.changecolor.ColorUtils;
import com.gmail.h1990.toshio.beanstalk.common.Extras;
import com.gmail.h1990.toshio.beanstalk.databinding.ActivityAddFriendBinding;
import com.gmail.h1990.toshio.beanstalk.util.GlideUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

public class AddFriendActivity extends AppCompatActivity {
    private FirebaseUser currentUser;
    private DatabaseReference databaseReferenceUser, databaseReferenceTalk;
    private String userId;
    private ActivityAddFriendBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ColorUtils.setTheme(this);
        binding = ActivityAddFriendBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        if (getIntent().hasExtra(USER_KEY)) {
            userId = getIntent().getStringExtra(Extras.USER_KEY);
        }
        setupFirebase();
        setProfile();
        binding.ibClose.setOnClickListener(view1 -> finish());
        binding.btAdd.setOnClickListener(view2 -> onAddBtnClick());
    }

    private void setupFirebase() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        databaseReferenceUser = FirebaseDatabase.getInstance().getReference().child(USERS);
        databaseReferenceTalk = FirebaseDatabase.getInstance().getReference().child(TALK);
    }


    private void setProfile() {
        databaseReferenceUser.child(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot snapshot = task.getResult();
                if (snapshot.exists()) {
                    String photoName = userId + EXT_JPG;
                    final StorageReference fileRef = FirebaseStorage.getInstance().getReference()
                            .child(IMAGES_FOLDER).child(PHOTO).child(photoName);
                    fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        binding.tvName.setText(Objects.requireNonNull(snapshot.child(NAME).getValue()).toString());
                        GlideUtils.setPhoto(this, uri, R.drawable.default_profile, binding.ivProfile);
                    });
                }
            }
        });
    }

    private void onAddBtnClick() {
        databaseReferenceTalk.child(currentUser.getUid()).child(userId).child(TIME_STAMP)
                .setValue(ServerValue.TIMESTAMP).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        databaseReferenceTalk.child(userId).child(currentUser.getUid()).child(TIME_STAMP)
                                .setValue(ServerValue.TIMESTAMP).addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        startActivity(new Intent(AddFriendActivity.this,
                                                MainActivity.class));
                                    }
                                });
                    }
                });
    }
}