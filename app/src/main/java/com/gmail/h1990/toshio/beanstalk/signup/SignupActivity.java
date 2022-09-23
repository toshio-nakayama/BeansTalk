package com.gmail.h1990.toshio.beanstalk.signup;

import static com.gmail.h1990.toshio.beanstalk.common.Constants.EXT_JPG;
import static com.gmail.h1990.toshio.beanstalk.common.Constants.IMAGES_FOLDER;
import static com.gmail.h1990.toshio.beanstalk.common.Constants.TAG;
import static com.gmail.h1990.toshio.beanstalk.common.Extras.PHOTO_URI_KEY;
import static com.gmail.h1990.toshio.beanstalk.common.NodeNames.PHOTO;
import static com.gmail.h1990.toshio.beanstalk.common.NodeNames.USERS;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.gmail.h1990.toshio.beanstalk.R;
import com.gmail.h1990.toshio.beanstalk.changecolor.ColorUtil;
import com.gmail.h1990.toshio.beanstalk.login.LoginActivity;
import com.gmail.h1990.toshio.beanstalk.model.UserModel;
import com.gmail.h1990.toshio.beanstalk.util.Validation;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.ConfirmPassword;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.Length;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;
import com.mobsandgeeks.saripaar.annotation.Pattern;

import java.util.Objects;

public class SignupActivity extends AppCompatActivity {
    @NotEmpty
    @Length(min = 3, max = 10)
    private EditText etName;

    @NotEmpty
    @Email
    private EditText etEmail;

    @NotEmpty
    @Password
    @Pattern(regex = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{6,20})")
    private EditText etPassword;

    @ConfirmPassword
    private EditText etConfirmPassword;

    private String name, email, password, confirmPassword;
    private ImageView ivProfile;
    private View progressBar;

    private Validator validator;
    private Validation validation;

    private FirebaseUser currentUser;
    private DatabaseReference databaseRootRef;
    private StorageReference storageRootRef;
    private Uri localFileUri;
    private FirebaseAuth firebaseAuth;
    private SharedPreferences pref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ColorUtil.setTheme(this);
        setContentView(R.layout.activity_signup);

        etName = findViewById(R.id.et_name);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        ivProfile = findViewById(R.id.iv_profile);
        progressBar = findViewById(R.id.progress_bar);
        firebaseAuth = FirebaseAuth.getInstance();
        storageRootRef = FirebaseStorage.getInstance().getReference();
        databaseRootRef = FirebaseDatabase.getInstance().getReference();
        pref = getPreferences(Context.MODE_PRIVATE);
        validator = new Validator(this);
        validation = new Validation(validator);
        validator.setValidationListener(validation);
    }


    public void pickUpImage(View view) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
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
                            localFileUri = result.getData().getData();
                            ivProfile.setImageURI(localFileUri);
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
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build();
        currentUser.updateProfile(profileUpdates)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, getString(R.string.user_profile_updated));
                    } else {
                        Log.e(TAG, getString(R.string.failed_to_update));
                    }
                });
    }


    private void updatePhoto() {
        String photo = currentUser.getUid() + EXT_JPG;
        StorageReference fileRef =
                storageRootRef.child(IMAGES_FOLDER).child(PHOTO).child(photo);
        UploadTask uploadTask = fileRef.putFile(localFileUri);
        Task<Uri> urlTask = uploadTask.continueWithTask((Continuation<UploadTask.TaskSnapshot, Task<Uri>>) task -> {
            if (!task.isSuccessful()) {
                throw Objects.requireNonNull(task.getException());
            }
            return fileRef.getDownloadUrl();
        }).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Uri uri = task.getResult();
                SharedPreferences.Editor editor = pref.edit();
                editor.putString(PHOTO_URI_KEY, uri.getPath());
                editor.commit();
                UserProfileChangeRequest profileUpdates =
                        new UserProfileChangeRequest.Builder().setPhotoUri(uri).build();
                currentUser.updateProfile(profileUpdates).addOnCompleteListener(task1 -> {
                });
            }
        });
    }

    private void writeNewUser() {
        String userId = currentUser.getUid();
        String photo = pref.getString(PHOTO_URI_KEY, "");
        SharedPreferences.Editor editor = pref.edit();
        editor.remove(PHOTO_URI_KEY);
        editor.commit();
        String statusMessage = "";
        String backgroundPhoto = "";
        UserModel userModel = new UserModel(name, email, statusMessage, photo, backgroundPhoto);
        databaseRootRef.child(USERS).child(userId).setValue(userModel).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast toast = Toast.makeText(SignupActivity.this, R.string.signup_successfully,
                        Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                startActivity(new Intent(SignupActivity.this, LoginActivity.class));
            }
        });
    }


    public void onSignupButtonClick(View view) {
        name = validation.trimAndNormalize(etName.getText().toString());
        email = validation.trimAndNormalize(etEmail.getText().toString());
        password = validation.trimAndNormalize(etPassword.getText().toString());
        confirmPassword = validation.trimAndNormalize(etConfirmPassword.getText().toString());
        if (validation.validate()) {
            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    Log.d(TAG, getString(R.string.create_user_success));
                    currentUser = firebaseAuth.getCurrentUser();
                    if (localFileUri != null) {
                        updatePhoto();
                    }
                    updateName();
                    writeNewUser();
                }
            });
        }
    }
}