package com.gmail.h1990.toshio.beanstalk.util;

import android.view.View;
import android.widget.TextView;

import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;

import org.apache.commons.lang3.StringUtils;

import java.text.Normalizer;
import java.util.List;

public class Validation implements Validator.ValidationListener{
    protected Validator validator;
    protected boolean validated;

    public Validation(Validator validator) {
        this.validator = validator;
    }

    public boolean validate() {
        if (validator != null) {
            validator.validate();
        }
        return validated;
    }

    @Override
    public void onValidationSucceeded() {
        validated = true;
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        validated = false;
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(view.getContext());
            if (view instanceof TextView) {
                TextView et = (TextView) view;
                et.setError(message);
            }
        }
    }

    public String trimAndNormalize(String str){
        str = StringUtils.strip(str);
        str = Normalizer.normalize(str, Normalizer.Form.NFKC);
        return str;
    }

}
