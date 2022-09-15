package com.gmail.h1990.toshio.beanstalk.addfriend;

import static com.gmail.h1990.toshio.beanstalk.common.Extras.USER_KEY;
import static com.gmail.h1990.toshio.beanstalk.common.NodeNames.NAME;
import static com.gmail.h1990.toshio.beanstalk.common.NodeNames.TALK;
import static com.gmail.h1990.toshio.beanstalk.common.NodeNames.TIME_STAMP;
import static com.gmail.h1990.toshio.beanstalk.common.NodeNames.USERS;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.gmail.h1990.toshio.beanstalk.MainActivity;
import com.gmail.h1990.toshio.beanstalk.R;
import com.gmail.h1990.toshio.beanstalk.common.Constants;
import com.gmail.h1990.toshio.beanstalk.common.Extras;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class AddFriendActivity extends AppCompatActivity {
    private ImageButton ibClose;
    private ImageView ivProfile;
    private TextView tvName;
    private Button btAdd;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private DatabaseReference databaseReferenceUser, databaseReferenceTalk;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        ibClose = findViewById(R.id.ib_close);
        ivProfile = findViewById(R.id.iv_profile);
        tvName = findViewById(R.id.tv_name);
        btAdd = findViewById(R.id.bt_add);
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        databaseReferenceUser = FirebaseDatabase.getInstance().getReference().child(USERS);
        databaseReferenceTalk = FirebaseDatabase.getInstance().getReference().child(TALK);
        if (getIntent().hasExtra(USER_KEY)) {
            userId = getIntent().getStringExtra(Extras.USER_KEY);
        }
        setProfile();
    }


    private void setProfile() {
        databaseReferenceUser.child(userId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    DataSnapshot snapshot = task.getResult();
                    if (snapshot.exists()) {
                        String photoName = userId + ".jpg";
                        final StorageReference fileReference = FirebaseStorage.getInstance().getReference()
                                .child(Constants.IMAGES_FOLDER + "/" + photoName);
                        fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                            tvName.setText(snapshot.child(NAME).getValue().toString());
                            Glide.with(AddFriendActivity.this)
                                    .load(uri)
                                    .placeholder(R.drawable.default_profile)
                                    .error(R.drawable.default_profile)
                                    .into(ivProfile);
                        });
                    }
                }
            }
        });
    }

    public void onAddBtnClick(View v) {
        databaseReferenceTalk.child(currentUser.getUid()).child(userId).child(TIME_STAMP)
                .setValue(ServerValue.TIMESTAMP).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            databaseReferenceTalk.child(userId).child(currentUser.getUid()).child(TIME_STAMP)
                                    .setValue(ServerValue.TIMESTAMP).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                startActivity(new Intent(AddFriendActivity.this,
                                                        MainActivity.class));
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    public void onCloseBtnClick(View v) {

    }

}