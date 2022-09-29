package com.gmail.h1990.toshio.beanstalk.login;

import static com.gmail.h1990.toshio.beanstalk.changecolor.MyColorFragment.DIALOG_TAG;
import static com.gmail.h1990.toshio.beanstalk.common.Constants.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.gmail.h1990.toshio.beanstalk.MainActivity;
import com.gmail.h1990.toshio.beanstalk.R;
import com.gmail.h1990.toshio.beanstalk.changecolor.ColorUtils;
import com.gmail.h1990.toshio.beanstalk.changecolor.MyColorFragment;
import com.gmail.h1990.toshio.beanstalk.databinding.ActivityLoginBinding;
import com.gmail.h1990.toshio.beanstalk.signup.SignupActivity;
import com.gmail.h1990.toshio.beanstalk.util.NetworkChecker;
import com.gmail.h1990.toshio.beanstalk.util.Validation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

public class LoginActivity extends AppCompatActivity implements MyColorFragment.PreferenceSavedListener {
    @NotEmpty(message = "必須項目です。入力をお願いします")
    private EditText etEmail;

    @NotEmpty(message = "必須項目です。入力をお願いします")
    private EditText etPassword;

    private Validation validation;
    Validator validator;
    private ActivityLoginBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ColorUtils.setTheme(this);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        validator = new Validator(this);
        validation = new Validation(validator);
        validator.setValidationListener(validation);
        binding.btMyColor.setOnClickListener(view1 -> generateMyColorDialog());
        binding.tvSignup.setOnClickListener(view2 -> onTvSignupClick());
        binding.btLogin.setOnClickListener(view13 -> onLoginBtnClick());
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    public void onTvSignupClick() {
        startActivity(new Intent(this, SignupActivity.class));
        finish();
    }

    public void onLoginBtnClick() {
        String email = validation.trimAndNormalize(binding.etEmail.getText().toString());
        String password = validation.trimAndNormalize(binding.etPassword.getText().toString());
        if (!validation.validate()) {
            return;
        }
        if (!NetworkChecker.connectionAvailable(this)) {
            Toast toast = Toast.makeText(this, R.string.offline, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            return;
        }
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    } else {
                        Log.e(TAG, getString(R.string.failed_to_login, task.getException()));
                    }
                });
    }

    private void generateMyColorDialog() {
        DialogFragment dialogFragment = new MyColorFragment();
        dialogFragment.show(getSupportFragmentManager(), DIALOG_TAG);
    }

    @Override
    public void onPreferenceSaved() {
        finish();
        startActivity(getIntent());
    }
}