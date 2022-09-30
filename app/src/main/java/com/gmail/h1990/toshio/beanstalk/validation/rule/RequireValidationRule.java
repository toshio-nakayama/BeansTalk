package com.gmail.h1990.toshio.beanstalk.validation.rule;

import android.content.Context;
import android.view.View;
import android.widget.EditText;

import com.gmail.h1990.toshio.beanstalk.R;
import com.mobsandgeeks.saripaar.QuickRule;

import org.apache.commons.lang3.StringUtils;

public class RequireValidationRule extends QuickRule {
    @Override
    public boolean isValid(View view) {
        String value = null;
        if (view instanceof EditText) {
            value = ((EditText) view).getText().toString();
        }
        if (StringUtils.isNotEmpty(value)) {
            return true;
        }
        return false;
    }

    @Override
    public String getMessage(Context context) {
        String message =
                context.getResources().getString(R.string.require_validation_messages);
        return message;
    }
}
