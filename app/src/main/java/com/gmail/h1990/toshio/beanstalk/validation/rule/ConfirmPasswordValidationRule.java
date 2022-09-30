package com.gmail.h1990.toshio.beanstalk.validation.rule;

import android.content.Context;
import android.view.View;
import android.widget.EditText;

import com.gmail.h1990.toshio.beanstalk.R;
import com.mobsandgeeks.saripaar.QuickRule;

import org.apache.commons.lang3.StringUtils;

public class ConfirmPasswordValidationRule extends QuickRule {

    private EditText etPassword;

    public ConfirmPasswordValidationRule(EditText etPassword) {
        super();
        this.etPassword = etPassword;
    }

    @Override
    public boolean isValid(View view) {
        String password = etPassword.getText().toString();
        String confirmPassword = null;
        if (view instanceof EditText) {
            confirmPassword = ((EditText) view).getText().toString();
        }
        if (StringUtils.isNotEmpty(password) && StringUtils.isNotEmpty(confirmPassword)) {
            if (password.equals(confirmPassword)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getMessage(Context context) {
        StringBuilder sb = new StringBuilder();
        sb.append(context.getResources().getString(R.string.require_validation_messages))
                .append("\n")
                .append(context.getResources().getString(R.string.confirm_password_validation_messages));
        return sb.toString();
    }

}
