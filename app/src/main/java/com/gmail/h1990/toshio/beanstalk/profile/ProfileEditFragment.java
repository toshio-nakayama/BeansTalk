package com.gmail.h1990.toshio.beanstalk.profile;

import static android.app.Activity.RESULT_OK;
import static com.gmail.h1990.toshio.beanstalk.common.Constants.EXT_JPG;
import static com.gmail.h1990.toshio.beanstalk.common.Constants.IMAGES_FOLDER;
import static com.gmail.h1990.toshio.beanstalk.common.Constants.TAG;
import static com.gmail.h1990.toshio.beanstalk.common.Extras.CONTENTS;
import static com.gmail.h1990.toshio.beanstalk.common.Extras.EDIT_TYPE;
import static com.gmail.h1990.toshio.beanstalk.common.Extras.REQUEST_KEY;
import static com.gmail.h1990.toshio.beanstalk.common.NodeNames.BACKGROUND_PHOTO;
import static com.gmail.h1990.toshio.beanstalk.common.NodeNames.PHOTO;
import static com.gmail.h1990.toshio.beanstalk.common.NodeNames.USERS;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.gmail.h1990.toshio.beanstalk.R;
import com.gmail.h1990.toshio.beanstalk.databinding.FragmentProfileEditBinding;
import com.gmail.h1990.toshio.beanstalk.model.UserModel;
import com.gmail.h1990.toshio.beanstalk.util.GlideUtils;
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
    private FirebaseUser currentUser;
    private DatabaseReference currentUserRef;
    private StorageReference storageRootRef;
    private ValueEventListener valueEventListener;
    private static final String DIALOG_TAG = "message_edit_fragment";
    private FragmentProfileEditBinding binding;


    public ProfileEditFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupFirebase();
    }

    private void setupFirebase() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        DatabaseReference databaseRootRef = FirebaseDatabase.getInstance().getReference();
        currentUserRef = databaseRootRef.child(USERS).child(currentUser.getUid());
        storageRootRef = FirebaseStorage.getInstance().getReference();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileEditBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupTvName();
        setupTvStatusMessage();
        setProfile();
        binding.btAddPhoto.setOnClickListener(view1 -> pickPhoto());
        binding.btAddBackgroundPhoto.setOnClickListener(view12 -> pickBackgroundPhoto());

    }

    private void setupTvName() {
        binding.tvName.setSingleLine(true);
        binding.tvName.setOnClickListener(view -> {
            DialogFragment dialogFragment = new MessageEditFragment();
            Bundle args = new Bundle();
            String name = binding.tvName.getText().toString();
            args.putString(CONTENTS, name);
            args.putInt(EDIT_TYPE, 0);
            getParentFragmentManager().setFragmentResult(REQUEST_KEY, args);
            dialogFragment.show(getParentFragmentManager(), DIALOG_TAG);
        });
    }


    private void setupTvStatusMessage() {
        binding.tvStatusMessage.setEms(12);
        binding.tvStatusMessage.setSingleLine(true);
        binding.tvStatusMessage.setSelected(true);
        binding.tvStatusMessage.setEllipsize(TextUtils.TruncateAt.END);
        binding.tvStatusMessage.setOnClickListener(view -> {
            DialogFragment dialogFragment = new MessageEditFragment();
            Bundle args = new Bundle();
            String message = binding.tvStatusMessage.getText().toString();
            args.putString(CONTENTS, message);
            args.putInt(EDIT_TYPE, 1);
            getParentFragmentManager().setFragmentResult(REQUEST_KEY, args);
            dialogFragment.show(getParentFragmentManager(), DIALOG_TAG);
        });
    }

    private void setBackground() {
        String photo = currentUser.getUid() + EXT_JPG;
        StorageReference fileRef =
                storageRootRef.child(IMAGES_FOLDER).child(BACKGROUND_PHOTO).child(photo);
        fileRef.getDownloadUrl()
                .addOnFailureListener(e -> Log.e(TAG, getString(R.string.failed_to_download_uri)))
                .addOnSuccessListener(uri ->
                        GlideUtils.setPhoto(getContext(), uri, R.drawable.default_background, binding.ivBackgroundPhoto));
    }

    private void setProfile() {
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserModel userModel = snapshot.getValue(UserModel.class);
                GlideUtils.setPhoto(getContext(), currentUser.getPhotoUrl(), R.drawable.default_profile, binding.ivProfile);
                binding.tvName.setText(currentUser.getDisplayName());
                binding.tvStatusMessage.setText(Objects.requireNonNull(userModel).getStatusMessage());
                setBackground();
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
                            Uri localFileUri = Objects.requireNonNull(result.getData()).getData();
                            binding.ivProfile.setImageURI(localFileUri);
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
                            Uri localFileUri = Objects.requireNonNull(result.getData()).getData();
                            binding.ivBackgroundPhoto.setImageURI(localFileUri);
                            updateBackgroundPhoto(localFileUri);
                        }
                    });

    private void updatePhoto(Uri photoUri) {
        String photo = currentUser.getUid() + EXT_JPG;
        StorageReference fileRef = storageRootRef.child(IMAGES_FOLDER).child(PHOTO).child(photo);
        UploadTask uploadTask = fileRef.putFile(photoUri);
        uploadTask.addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
            UserProfileChangeRequest profileUpdates =
                    new UserProfileChangeRequest.Builder().setPhotoUri(uri).build();
            currentUser.updateProfile(profileUpdates)
                    .addOnSuccessListener(unused -> currentUserRef.child(PHOTO).setValue(uri.getPath())
                            .addOnFailureListener(e -> Log.e(TAG, getString(R.string.failed_to_update)))
                            .addOnSuccessListener(unused1 -> Log.d(TAG, getString(R.string.user_profile_updated))));
        }));
    }

    private void updateBackgroundPhoto(Uri photoUri) {
        String photo = currentUser.getUid() + EXT_JPG;
        StorageReference fileRef = storageRootRef.child(IMAGES_FOLDER).child(BACKGROUND_PHOTO).child(photo);
        UploadTask uploadTask = fileRef.putFile(photoUri);
        uploadTask.addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl()
                .addOnSuccessListener(uri -> currentUserRef.child(BACKGROUND_PHOTO).setValue(uri.getPath())
                        .addOnFailureListener(e -> Log.e(TAG, getString(R.string.failed_to_update)))
                        .addOnSuccessListener(unused -> Log.d(TAG, getString(R.string.user_profile_updated)))));
    }

}