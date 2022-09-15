package com.gmail.h1990.toshio.beanstalk.login;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gmail.h1990.toshio.beanstalk.R;

public class ColorSelectionFragment extends DialogFragment {
    private View alertView;

    public ColorSelectionFragment() {
    }

    public static ColorSelectionFragment newInstance(String param1, String param2) {
        ColorSelectionFragment fragment = new ColorSelectionFragment();
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Activity activity = getActivity();
        if (activity!=null){
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setMessage(R.string.select_theme_color);
            LayoutInflater inflater = requireActivity().getLayoutInflater();
            View view = inflater.inflate(R.layout.fragment_color_selection, null);

        }
        return null;
    }
}