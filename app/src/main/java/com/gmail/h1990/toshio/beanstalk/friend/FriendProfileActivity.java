package com.gmail.h1990.toshio.beanstalk.friend;

import static com.gmail.h1990.toshio.beanstalk.common.Constants.IMAGES_FOLDER;
import static com.gmail.h1990.toshio.beanstalk.common.Extras.PHOTO_NAME;
import static com.gmail.h1990.toshio.beanstalk.common.Extras.USER_KEY;
import static com.gmail.h1990.toshio.beanstalk.common.Extras.USER_NAME;
import static com.gmail.h1990.toshio.beanstalk.common.NodeNames.PHOTO;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.gmail.h1990.toshio.beanstalk.R;
import com.gmail.h1990.toshio.beanstalk.talk.TalkActivity;
import com.gmail.h1990.toshio.beanstalk.util.GlideUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class FriendProfileActivity extends AppCompatActivity {
    private String friendUserId;
    private String friendUserName;
    private ImageView ivBackGroundPhoto;
    private ImageView ivProfile;
    private TextView tvName;
    private TextView tvStatusMessage;
    private Button btTalk;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference rootReference;
    private String friendUserPhotoName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_profile);

        acceptData();
        initView();
        setupFirebase();
        setProfile();
        tvStatusMessage.setSelected(true);
        btTalk.setOnClickListener(view -> {
            Intent intent = new Intent(this, TalkActivity.class);
            intent.putExtra(USER_KEY, friendUserId);
            intent.putExtra(USER_NAME, friendUserName);
            intent.putExtra(PHOTO_NAME, friendUserPhotoName);
            startActivity(intent);
        });
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setupFirebase() {
        firebaseAuth = FirebaseAuth.getInstance();
        rootReference = FirebaseDatabase.getInstance().getReference();
    }

    private void initView() {
        ivBackGroundPhoto = findViewById(R.id.iv_background_photo);
        ivProfile = findViewById(R.id.iv_profile);
        tvName = findViewById(R.id.tv_name);
        tvStatusMessage = findViewById(R.id.tv_status_message);
        btTalk = findViewById(R.id.bt_talk);
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
        tvName.setText(friendUserName);
        tvStatusMessage.setText("");
        StorageReference storageReference =
                FirebaseStorage.getInstance().getReference().child(IMAGES_FOLDER).child(PHOTO).child(friendUserPhotoName);
        storageReference.getDownloadUrl().addOnSuccessListener(uri ->
                GlideUtils.setPhoto(this, uri,
                        ivProfile));
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