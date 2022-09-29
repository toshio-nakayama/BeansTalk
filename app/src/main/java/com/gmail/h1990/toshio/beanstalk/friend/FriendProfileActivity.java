package com.gmail.h1990.toshio.beanstalk.friend;

import static com.gmail.h1990.toshio.beanstalk.common.Constants.IMAGES_FOLDER;
import static com.gmail.h1990.toshio.beanstalk.common.Extras.PHOTO_NAME;
import static com.gmail.h1990.toshio.beanstalk.common.Extras.USER_KEY;
import static com.gmail.h1990.toshio.beanstalk.common.Extras.USER_NAME;
import static com.gmail.h1990.toshio.beanstalk.common.NodeNames.BACKGROUND_PHOTO;
import static com.gmail.h1990.toshio.beanstalk.common.NodeNames.PHOTO;
import static com.gmail.h1990.toshio.beanstalk.common.NodeNames.USERS;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.gmail.h1990.toshio.beanstalk.R;
import com.gmail.h1990.toshio.beanstalk.changecolor.ColorUtils;
import com.gmail.h1990.toshio.beanstalk.databinding.ActivityFriendProfileBinding;
import com.gmail.h1990.toshio.beanstalk.model.UserModel;
import com.gmail.h1990.toshio.beanstalk.talk.TalkActivity;
import com.gmail.h1990.toshio.beanstalk.util.GlideUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

public class FriendProfileActivity extends AppCompatActivity {
    private String friendUserId;
    private String friendUserName;
    private String friendUserPhotoName;
    private DatabaseReference databaseRootRef;
    private StorageReference storageRootRef;
    private ActivityFriendProfileBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ColorUtils.setTheme(this);
        binding = ActivityFriendProfileBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        acceptData();
        setupFirebase();
        setProfile();
        binding.tvStatusMessage.setSelected(true);
        binding.ibTalk.setOnClickListener(view1 -> {
            Intent intent = new Intent(this, TalkActivity.class);
            intent.putExtra(USER_KEY, friendUserId);
            intent.putExtra(USER_NAME, friendUserName);
            intent.putExtra(PHOTO_NAME, friendUserPhotoName);
            startActivity(intent);
        });
        Toolbar toolbar = binding.toolbar;
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }

    private void setupFirebase() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        databaseRootRef = FirebaseDatabase.getInstance().getReference();
        storageRootRef = FirebaseStorage.getInstance().getReference();
    }

    private void acceptData() {
        if (getIntent().hasExtra(USER_KEY)) {
            friendUserId = getIntent().getStringExtra(USER_KEY);
        }
        if (getIntent().hasExtra(USER_NAME)) {
            friendUserName = getIntent().getStringExtra(USER_NAME);
        }
        if (getIntent().hasExtra(PHOTO_NAME)) {
            friendUserPhotoName = getIntent().getStringExtra(PHOTO_NAME);
        }
    }

    private void setProfile() {
        binding.tvName.setText(friendUserName);
        final DatabaseReference databaseReferenceFriend = databaseRootRef.child(USERS).child(friendUserId);
        databaseReferenceFriend.get().addOnSuccessListener(dataSnapshot -> {
            UserModel userModel = dataSnapshot.getValue(UserModel.class);
            binding.tvStatusMessage.setText(Objects.requireNonNull(userModel).getStatusMessage());
        });
        StorageReference photoReference =
                storageRootRef.child(IMAGES_FOLDER).child(PHOTO).child(friendUserPhotoName);
        photoReference.getDownloadUrl().addOnSuccessListener(uri -> {
            GlideUtils.setPhoto(FriendProfileActivity.this, uri, R.drawable.default_profile, binding.ivProfile);
        });
        StorageReference backgroundPhotoReference =
                storageRootRef.child(IMAGES_FOLDER).child(BACKGROUND_PHOTO).child(friendUserPhotoName);
        backgroundPhotoReference.getDownloadUrl().addOnSuccessListener(
                uri -> GlideUtils.setPhoto(FriendProfileActivity.this, uri, R.drawable.default_background,
                        binding.ivBackgroundPhoto));

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}