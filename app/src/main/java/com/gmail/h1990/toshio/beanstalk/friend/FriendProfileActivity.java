package com.gmail.h1990.toshio.beanstalk.friend;

import static com.gmail.h1990.toshio.beanstalk.common.Constants.IMAGES_FOLDER;
import static com.gmail.h1990.toshio.beanstalk.common.Extras.PHOTO_NAME;
import static com.gmail.h1990.toshio.beanstalk.common.Extras.USER_KEY;
import static com.gmail.h1990.toshio.beanstalk.common.Extras.USER_NAME;
import static com.gmail.h1990.toshio.beanstalk.common.NodeNames.PHOTO;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.gmail.h1990.toshio.beanstalk.R;
import com.gmail.h1990.toshio.beanstalk.changecolor.ColorUtils;
import com.gmail.h1990.toshio.beanstalk.databinding.ActivityFriendProfileBinding;
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
        binding.btTalk.setOnClickListener(view1 -> {
            Intent intent = new Intent(this, TalkActivity.class);
            intent.putExtra(USER_KEY, friendUserId);
            intent.putExtra(USER_NAME, friendUserName);
            intent.putExtra(PHOTO_NAME, friendUserPhotoName);
            startActivity(intent);
        });
        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }

    private void setupFirebase() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        DatabaseReference rootReference = FirebaseDatabase.getInstance().getReference();
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
        binding.tvStatusMessage.setText("");
        StorageReference storageReference =
                FirebaseStorage.getInstance().getReference().child(IMAGES_FOLDER).child(PHOTO).child(friendUserPhotoName);
        storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
            GlideUtils.setPhoto(this, uri, R.drawable.default_profile, binding.ivProfile);
        });

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