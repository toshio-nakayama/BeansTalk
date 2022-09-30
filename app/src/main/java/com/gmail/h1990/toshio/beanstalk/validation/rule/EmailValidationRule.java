package com.gmail.h1990.toshio.beanstalk.validation.rule;

import android.content.Context;
import android.view.View;
import android.widget.EditText;

import com.gmail.h1990.toshio.beanstalk.R;
import com.mobsandgeeks.saripaar.QuickRule;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailValidationRule extends QuickRule {
    private static final String REGEXP = "^[a-zA-Z0-9_+-]+(.[a-zA-Z0-9_+-]+)*@" +
            "([a-zA-Z0-9][a-zA-Z0-9-]*[a-zA-Z0-9]*\\.)+[a-zA-Z]{2,}$";

    @Override
    public boolean isValid(View view) {
        String value = null;
        if (view instanceof EditText) {
            value = ((EditText) view).getText().toString();
        }
        if (StringUtils.isNotEmpty(value)) {
            Pattern pattern = Pattern.compile(REGEXP);
            Matcher matcher = pattern.matcher(value);
            return matcher.matches();
        }
        return false;
    }

    @Override
    public String getMessage(Context context) {
        StringBuilder sb = new StringBuilder();
        sb.append(context.getResources().getString(R.string.require_validation_messages))
                .append("\n")
                .append(context.getResources().getString(R.string.email_validation_messages));
        return sb.toString();
    }
}
