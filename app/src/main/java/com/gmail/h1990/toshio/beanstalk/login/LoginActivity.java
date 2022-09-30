package com.gmail.h1990.toshio.beanstalk.login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.gmail.h1990.toshio.beanstalk.MainActivity;
import com.gmail.h1990.toshio.beanstalk.R;
import com.gmail.h1990.toshio.beanstalk.changecolor.ColorUtils;
import com.gmail.h1990.toshio.beanstalk.changecolor.MyColorFragment;
import com.gmail.h1990.toshio.beanstalk.common.Constants;
import com.gmail.h1990.toshio.beanstalk.databinding.ActivityLoginBinding;
import com.gmail.h1990.toshio.beanstalk.signup.SignupActivity;
import com.gmail.h1990.toshio.beanstalk.util.NetworkChecker;
import com.gmail.h1990.toshio.beanstalk.util.ToastGenerator;
import com.gmail.h1990.toshio.beanstalk.validation.Validation;
import com.gmail.h1990.toshio.beanstalk.validation.rule.RequireValidationRule;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mobsandgeeks.saripaar.Validator;

public class LoginActivity extends AppCompatActivity implements MyColorFragment.PreferenceSavedListener {

    private Validation validation;
    private Validator validator;
    private ActivityLoginBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ColorUtils.setTheme(this);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        setupValidation();
        binding.btMyColor.setOnClickListener(view1 -> generateMyColorDialog());
        binding.tvSignup.setOnClickListener(view2 -> onTvSignupClick());
        binding.btLogin.setOnClickListener(view13 -> onLoginBtnClick());
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

    private void setupValidation() {
        validator = new Validator(this);
        validator.put(binding.etEmail, new RequireValidationRule());
        validator.put(binding.etPassword, new RequireValidationRule());
        validation = new Validation(validator);
        validator.setValidationListener(validation);
    }

    public void onTvSignupClick() {
        startActivity(new Intent(this, SignupActivity.class));
    }

    public void onLoginBtnClick() {
        String email = validation.trimAndNormalize(binding.etEmail.getText().toString());
        String password = validation.trimAndNormalize(binding.etPassword.getText().toString());
        if (!NetworkChecker.connectionAvailable(this)) {
            new ToastGenerator.Builder(getApplicationContext()).resId(R.string.offline).build();
            return;
        }
        if (validation.validate()) {
            final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnFailureListener(e -> Log.e(Constants.TAG, getString(R.string.failed_to_login, e.getMessage())))
                    .addOnSuccessListener(authResult -> {
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    });
        }

    }

    private void generateMyColorDialog() {
        DialogFragment dialogFragment = new MyColorFragment();
        dialogFragment.show(getSupportFragmentManager(), MyColorFragment.DIALOG_TAG);
    }

    @Override
    public void onPreferenceSaved() {
        finish();
        startActivity(getIntent());
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }

}