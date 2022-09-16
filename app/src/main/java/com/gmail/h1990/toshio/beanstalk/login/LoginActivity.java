package com.gmail.h1990.toshio.beanstalk.login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.DialogFragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.gmail.h1990.toshio.beanstalk.MainActivity;
import com.gmail.h1990.toshio.beanstalk.R;
import com.gmail.h1990.toshio.beanstalk.signup.SignupActivity;
import com.gmail.h1990.toshio.beanstalk.util.ConnectivityCheck;
import com.gmail.h1990.toshio.beanstalk.util.Validation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import static com.gmail.h1990.toshio.beanstalk.common.Constants.*;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {
    @NotEmpty
    private EditText etEmail;

    @NotEmpty
    private EditText etPassword;

    private Validation validation;
    private View progressBar;

    private static final String DIALOG_TAG = "dialog_color_selection";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

//    private void showDialog() {
//        List<ColorModel> models = new ArrayList<>();
//        models.add(new ColorModel(ContextCompat.getDrawable(getApplicationContext(),
//                R.drawable.circle_magenta),
//                getResources().getColor(R.color.magenta, getTheme())));
//        models.add(new ColorModel(ContextCompat.getDrawable(getApplicationContext(),
//                R.drawable.circle_turquoise),
//                getResources().getColor(R.color.turquoise, getTheme())));
//
//        ColorListAdapter colorListAdapter = new ColorListAdapter(getApplicationContext(), 0, models);
//        ListView listView = findViewById(R.id.list_id);
//        listView.setAdapter(colorListAdapter);
//        AlertDialog alertDialog = new AlertDialog.Builder(this)
//                .setTitle("選択してください")
//                .setView(listView).create();
//        alertDialog.show();
//    }

    private void showDialog() {
        DialogFragment dialogFragment = new ColorSelectionFragment();
        dialogFragment.show(getSupportFragmentManager(), DIALOG_TAG);
    }


}