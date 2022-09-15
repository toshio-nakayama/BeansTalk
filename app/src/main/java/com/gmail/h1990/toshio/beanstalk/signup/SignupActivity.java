package com.gmail.h1990.toshio.beanstalk.signup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.gmail.h1990.toshio.beanstalk.R;
import com.gmail.h1990.toshio.beanstalk.login.LoginActivity;
import com.gmail.h1990.toshio.beanstalk.model.UserModel;
import com.gmail.h1990.toshio.beanstalk.util.Validation;
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

import static com.gmail.h1990.toshio.beanstalk.common.Constants.*;
import static com.gmail.h1990.toshio.beanstalk.common.Extras.PHOTO_URI_KEY;
import static com.gmail.h1990.toshio.beanstalk.common.NodeNames.USERS;

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

    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private Uri localFileUri, serverFileUri;
    private FirebaseAuth firebaseAuth;
    SharedPreferences pref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        etName = findViewById(R.id.et_name);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        ivProfile = findViewById(R.id.iv_profile);
        progressBar = findViewById(R.id.progress_bar);
        firebaseAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference();
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
        firebaseUser.updateProfile(profileUpdates)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, getString(R.string.user_profile_updated));
                    }
                });
    }

    private void updatePhoto() {
        String photo = firebaseUser.getUid() + EXT_JPG;
        StorageReference mRef = storageReference.child(IMAGES_FOLDER + SLASH + photo);
        UploadTask uploadTask = mRef.putFile(localFileUri);
        uploadTask.addOnFailureListener(exception -> Log.e(TAG, getString(R.string.failed_to_upload)))
                .addOnSuccessListener(taskSnapshot -> mRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String photo1 = uri.getPath();
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString(PHOTO_URI_KEY, photo1);
                    editor.commit();
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setPhotoUri(uri)
                            .build();
                    firebaseUser.updateProfile(profileUpdates)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, getString(R.string.user_profile_updated));
                                }
                            });
                }));
    }

    private void writeNewUser() {
        String photo = pref.getString(PHOTO_URI_KEY, "");
        String userId = firebaseUser.getUid();
        UserModel userModel = new UserModel(name, email, photo);
        databaseReference.child(USERS).child(userId).setValue(userModel).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(SignupActivity.this, R.string.signup_successfully,
                        Toast.LENGTH_SHORT).show();
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
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            Log.d(TAG, getString(R.string.create_user_success));
                            firebaseUser = firebaseAuth.getCurrentUser();
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