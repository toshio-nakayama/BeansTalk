package com.gmail.h1990.toshio.beanstalk.profile;

import static android.app.Activity.RESULT_OK;
import static com.gmail.h1990.toshio.beanstalk.common.Constants.EXT_JPG;
import static com.gmail.h1990.toshio.beanstalk.common.Constants.IMAGES_FOLDER;
import static com.gmail.h1990.toshio.beanstalk.common.Extras.CONTENTS;
import static com.gmail.h1990.toshio.beanstalk.common.Extras.EDIT_TYPE;
import static com.gmail.h1990.toshio.beanstalk.common.Extras.REQUEST_KEY;
import static com.gmail.h1990.toshio.beanstalk.common.NodeNames.PHOTO;
import static com.gmail.h1990.toshio.beanstalk.common.NodeNames.USERS;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.gmail.h1990.toshio.beanstalk.R;
import com.gmail.h1990.toshio.beanstalk.model.UserModel;
import com.gmail.h1990.toshio.beanstalk.util.GlideUtils;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Objects;

public class ProfileEditFragment extends Fragment {
    private TextView tvName;
    private TextView tvStatusMessage;
    private ImageView ivProfile, ivBackgroundPhoto;
    private Button btAddPhoto, btAddBackgroundPhoto;
    private FirebaseUser currentUser;
    private DatabaseReference databaseRootRef, currentUserRef;
    private StorageReference storageRootRef;
    private ValueEventListener valueEventListener;
    private static final String DIALOG_TAG = "message_edit_fragment";


    public ProfileEditFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        databaseRootRef = FirebaseDatabase.getInstance().getReference();
        currentUserRef = databaseRootRef.child(USERS).child(currentUser.getUid());
        storageRootRef = FirebaseStorage.getInstance().getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile_edit, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        setupTvName();
        setupTvStatusMessage();
        setProfile();
        btAddPhoto.setOnClickListener(view1 -> pickPhoto());
        btAddBackgroundPhoto.setOnClickListener(view12 -> pickBackgroundPhoto());

    }

    private void initView(View view) {
        tvName = view.findViewById(R.id.tv_name);
        tvStatusMessage = view.findViewById(R.id.tv_status_message);
        ivProfile = view.findViewById(R.id.iv_profile);
        ivBackgroundPhoto = view.findViewById(R.id.iv_background_photo);
        btAddPhoto = view.findViewById(R.id.bt_add_photo);
        btAddBackgroundPhoto = view.findViewById(R.id.bt_add_background_photo);
    }

    private void setupTvName() {
        tvName.setSingleLine(true);
        tvName.setOnClickListener(view -> {
            DialogFragment dialogFragment = new MessageEditFragment();
            Bundle args = new Bundle();
            String name = tvName.getText().toString();
            args.putString(CONTENTS, name);
            args.putInt(EDIT_TYPE, 0);
            getParentFragmentManager().setFragmentResult(REQUEST_KEY, args);
            dialogFragment.show(getParentFragmentManager(), DIALOG_TAG);
        });
    }


    private void setupTvStatusMessage() {
        tvStatusMessage.setEms(12);
        tvStatusMessage.setSingleLine(true);
        tvStatusMessage.setSelected(true);
        tvStatusMessage.setEllipsize(TextUtils.TruncateAt.END);
        tvStatusMessage.setOnClickListener(view -> {
            DialogFragment dialogFragment = new MessageEditFragment();
            Bundle args = new Bundle();
            String message = tvStatusMessage.getText().toString();
            args.putString(CONTENTS, message);
            args.putInt(EDIT_TYPE, 1);
            getParentFragmentManager().setFragmentResult(REQUEST_KEY, args);
            dialogFragment.show(getParentFragmentManager(), DIALOG_TAG);
        });
    }

    private void setProfile() {
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserModel userModel = snapshot.getValue(UserModel.class);
                GlideUtils.setPhoto(getContext(), currentUser.getPhotoUrl(), ivProfile);
                tvName.setText(currentUser.getDisplayName());
                tvStatusMessage.setText(userModel.getStatusMessage());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        currentUserRef.addValueEventListener(valueEventListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        currentUserRef.removeEventListener(valueEventListener);
    }

    public void pickPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        takePhoto.launch(intent);
    }

    private final ActivityResultLauncher<Intent> takePhoto =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK) {
                            Uri localFileUri = result.getData().getData();
                            ivProfile.setImageURI(localFileUri);
                            updatePhoto(localFileUri);
                        }
                    });

    public void pickBackgroundPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        takeBackGroundPhoto.launch(intent);
    }

    private final ActivityResultLauncher<Intent> takeBackGroundPhoto =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK) {
                            Uri localFileUri = result.getData().getData();
                            ivBackgroundPhoto.setImageURI(localFileUri);
                        }
                    });

    private void updatePhoto(Uri photoUri) {
        String photo = currentUser.getUid() + EXT_JPG;
        StorageReference fileRef = storageRootRef.child(IMAGES_FOLDER).child(PHOTO).child(photo);
        UploadTask uploadTask = fileRef.putFile(photoUri);
        Task<Uri> urlTask = uploadTask.continueWithTask((Continuation<UploadTask.TaskSnapshot, Task<Uri>>) task -> {
            if (!task.isSuccessful()) {
                throw Objects.requireNonNull(task.getException());
            }
            return fileRef.getDownloadUrl();
        }).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Uri uri = task.getResult();
                UserProfileChangeRequest profileUpdates =
                        new UserProfileChangeRequest.Builder().setPhotoUri(uri).build();
                currentUser.updateProfile(profileUpdates).addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        currentUserRef.child(PHOTO).setValue(uri.getPath()).addOnCompleteListener(task2 -> {
                        });
                    }
                });
            }
        });
    }

}