package com.gmail.h1990.toshio.beanstalk.addfriend;

import static com.gmail.h1990.toshio.beanstalk.common.Constants.EXT_JPG;
import static com.gmail.h1990.toshio.beanstalk.common.Constants.IMAGES_FOLDER;
import static com.gmail.h1990.toshio.beanstalk.common.Constants.TAG;
import static com.gmail.h1990.toshio.beanstalk.common.Extras.USER_KEY;
import static com.gmail.h1990.toshio.beanstalk.common.NodeNames.NAME;
import static com.gmail.h1990.toshio.beanstalk.common.NodeNames.PHOTO;
import static com.gmail.h1990.toshio.beanstalk.common.NodeNames.TALK;
import static com.gmail.h1990.toshio.beanstalk.common.NodeNames.TIME_STAMP;
import static com.gmail.h1990.toshio.beanstalk.common.NodeNames.USERS;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
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
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

public class AddFriendActivity extends AppCompatActivity {
    private FirebaseUser currentUser;
    private DatabaseReference databaseReferenceUser, databaseReferenceTalk;
    private StorageReference storageRootRef;
    private String friendUserId;
    private ActivityAddFriendBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ColorUtils.setTheme(this);
        binding = ActivityAddFriendBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        if (getIntent().hasExtra(USER_KEY)) {
            friendUserId = getIntent().getStringExtra(Extras.USER_KEY);
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
        storageRootRef = FirebaseStorage.getInstance().getReference();
    }

    private void setProfile() {
        databaseReferenceUser.child(friendUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String photoName = friendUserId + EXT_JPG;
                final StorageReference fileRef = storageRootRef.child(IMAGES_FOLDER).child(PHOTO).child(photoName);
                fileRef.getDownloadUrl().addOnFailureListener(e -> {
                    Log.e(TAG, getString(R.string.failed_to_download_uri));
                }).addOnSuccessListener(uri -> {
                    binding.tvName.setText(Objects.requireNonNull(snapshot.child(NAME).getValue()).toString());
                    GlideUtils.setPhoto(AddFriendActivity.this, uri, R.drawable.default_profile,
                            binding.ivProfile);
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void onAddBtnClick() {
        String currentUserId = currentUser.getUid();
        databaseReferenceTalk.child(currentUserId).child(friendUserId).child(TIME_STAMP)
                .setValue(ServerValue.TIMESTAMP).addOnFailureListener(e -> {
                    Log.e(TAG, getString(R.string.failed_to_add_friend));
                }).addOnSuccessListener(unused -> {
                    databaseReferenceTalk.child(friendUserId).child(currentUser.getUid()).child(TIME_STAMP)
                            .setValue(ServerValue.TIMESTAMP).addOnFailureListener(e -> {
                                Log.e(TAG, getString(R.string.failed_to_add_friend));
                            }).addOnSuccessListener(unused1 -> {
                                startActivity(new Intent(AddFriendActivity.this,
                                        MainActivity.class));
                                finish();
                            });
                });
    }
}