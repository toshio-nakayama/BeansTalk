package com.gmail.h1990.toshio.beanstalk.login;

import static com.gmail.h1990.toshio.beanstalk.changecolor.MyColorFragment.DIALOG_TAG;
import static com.gmail.h1990.toshio.beanstalk.common.Constants.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.gmail.h1990.toshio.beanstalk.MainActivity;
import com.gmail.h1990.toshio.beanstalk.R;
import com.gmail.h1990.toshio.beanstalk.changecolor.ColorUtil;
import com.gmail.h1990.toshio.beanstalk.changecolor.MyColorFragment;
import com.gmail.h1990.toshio.beanstalk.signup.SignupActivity;
import com.gmail.h1990.toshio.beanstalk.util.ConnectivityCheck;
import com.gmail.h1990.toshio.beanstalk.util.Validation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

public class LoginActivity extends AppCompatActivity implements MyColorFragment.PreferenceSavedListener {
    @NotEmpty
    private EditText etEmail;

    @NotEmpty
    private EditText etPassword;

    private Validation validation;
    private View progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ColorUtil.setTheme(this);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        Button btMyColor = findViewById(R.id.bt_my_color);
        progressBar = findViewById(R.id.progress_bar);

        Validator validator = new Validator(this);
        validation = new Validation(validator);
        validator.setValidationListener(validation);
        btMyColor.setOnClickListener(view -> {
            showDialog();
        });
    }


    public void onTextViewSignupClick(View view) {
        startActivity(new Intent(this, SignupActivity.class));
    }


    public void onLoginButtonClick(View view) {
        String email = validation.trimAndNormalize(etEmail.getText().toString());
        String password = validation.trimAndNormalize(etPassword.getText().toString());
        if (!validation.validate()) {
            return;
        }
        if (!ConnectivityCheck.connectionAvailable(this)) {
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

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    private void showDialog() {
        DialogFragment dialogFragment = new MyColorFragment();
        dialogFragment.show(getSupportFragmentManager(), DIALOG_TAG);
    }

    @Override
    public void onPreferenceSaved() {
        finish();
        startActivity(getIntent());
    }
}