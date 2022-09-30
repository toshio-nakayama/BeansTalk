package com.gmail.h1990.toshio.beanstalk.signup;

import static com.gmail.h1990.toshio.beanstalk.common.Constants.EXT_JPG;
import static com.gmail.h1990.toshio.beanstalk.common.Constants.IMAGES_FOLDER;
import static com.gmail.h1990.toshio.beanstalk.common.Constants.TAG;
import static com.gmail.h1990.toshio.beanstalk.common.NodeNames.PHOTO;
import static com.gmail.h1990.toshio.beanstalk.common.NodeNames.USERS;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.gmail.h1990.toshio.beanstalk.R;
import com.gmail.h1990.toshio.beanstalk.changecolor.ColorUtils;
import com.gmail.h1990.toshio.beanstalk.databinding.ActivitySignupBinding;
import com.gmail.h1990.toshio.beanstalk.login.LoginActivity;
import com.gmail.h1990.toshio.beanstalk.model.UserModel;
import com.gmail.h1990.toshio.beanstalk.util.ToastGenerator;
import com.gmail.h1990.toshio.beanstalk.validation.Validation;
import com.gmail.h1990.toshio.beanstalk.validation.rule.ConfirmPasswordValidationRule;
import com.gmail.h1990.toshio.beanstalk.validation.rule.EmailValidationRule;
import com.gmail.h1990.toshio.beanstalk.validation.rule.NameValidationRule;
import com.gmail.h1990.toshio.beanstalk.validation.rule.PasswordValidationRule;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mobsandgeeks.saripaar.Validator;

import java.util.Objects;

public class SignupActivity extends AppCompatActivity {
    private String name, email;
    private Validator validator;
    private Validation validation;
    private FirebaseUser currentUser;
    private DatabaseReference databaseRootRef;
    private StorageReference storageRootRef;
    private Uri localFileUri;
    private FirebaseAuth firebaseAuth;
    private ActivitySignupBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ColorUtils.setTheme(this);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        setupFirebase();
        setupValidation();
        binding.btSignup.setOnClickListener(view1 -> onSignupBtnClick());
        binding.btAddPhoto.setOnClickListener(view12 -> pickPhoto());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        validator = null;
        binding = null;
    }

    private void setupFirebase() {
        firebaseAuth = FirebaseAuth.getInstance();
        storageRootRef = FirebaseStorage.getInstance().getReference();
        databaseRootRef = FirebaseDatabase.getInstance().getReference();
    }

    private void setupValidation() {
        validator = new Validator(this);
        validator.put(binding.etName, new NameValidationRule());
        validator.put(binding.etEmail, new EmailValidationRule());
        validator.put(binding.etPassword, new PasswordValidationRule());
        validator.put(binding.etConfirmPassword, new ConfirmPasswordValidationRule(binding.etPassword));
        validation = new Validation(validator);
        validator.setValidationListener(validation);
    }

    public void pickPhoto() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            activityResultLauncher.launch(intent);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 102);
        }
    }

    private final ActivityResultLauncher<Intent> activityResultLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK) {
                            localFileUri = Objects.requireNonNull(result.getData()).getData();
                            binding.ivProfile.setImageURI(localFileUri);
                        }
                    });

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 102) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                activityResultLauncher.launch(intent);
            }
        }
    }

    private void updateName() {
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(name).build();
        currentUser.updateProfile(profileUpdates).addOnSuccessListener(unused -> {
            String userId = currentUser.getUid();
            final DatabaseReference databaseReferenceUser = databaseRootRef.child(USERS);
            UserModel userModel = new UserModel(name, email, "", "", "");
            databaseReferenceUser.child(userId).setValue(userModel).addOnSuccessListener(unused1 -> {
                new ToastGenerator.Builder(getApplicationContext()).resId(R.string.signup_successfully).build();
//                Toast toast = Toast.makeText(SignupActivity.this, R.string.signup_successfully,
//                        Toast.LENGTH_SHORT);
//                toast.setGravity(Gravity.CENTER, 0, 0);
//                toast.show();
                startActivity(new Intent(SignupActivity.this, LoginActivity.class));
            });
        });
    }


    private void updateNameAndPhoto() {
        String photo = currentUser.getUid() + EXT_JPG;
        final StorageReference fileRef = storageRootRef.child(IMAGES_FOLDER).child(PHOTO).child(photo);
        UploadTask uploadTask = fileRef.putFile(localFileUri);
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                UserProfileChangeRequest profileUpdates =
                        new UserProfileChangeRequest.Builder().setDisplayName(name).setPhotoUri(uri).build();
                currentUser.updateProfile(profileUpdates).addOnSuccessListener(unused -> {
                    String userId = currentUser.getUid();
                    final DatabaseReference databaseReferenceUser = databaseRootRef.child(USERS);
                    UserModel userModel = new UserModel(name, email, "", uri.getPath(), "");
                    databaseReferenceUser.child(userId).setValue(userModel).addOnSuccessListener(unused1 -> {
                        Toast toast = Toast.makeText(SignupActivity.this, R.string.signup_successfully,
                                Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                    });
                });
            });
        });
    }

    public void onSignupBtnClick() {
        name = validation.trimAndNormalize(binding.etName.getText().toString());
        email = validation.trimAndNormalize(binding.etEmail.getText().toString());
        String password = validation.trimAndNormalize(binding.etPassword.getText().toString());
        if (validation.validate()) {
            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnFailureListener(e -> {
                Log.e(TAG, getString(R.string.signup_failed));
            }).addOnSuccessListener(authResult -> {
                Log.d(TAG, getString(R.string.create_user_success));
                currentUser = firebaseAuth.getCurrentUser();
                if (localFileUri != null) {
                    updateNameAndPhoto();
                } else {
                    updateName();
                }
            });
        }
    }
}