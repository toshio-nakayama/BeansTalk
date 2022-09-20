package com.gmail.h1990.toshio.beanstalk.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.gmail.h1990.toshio.beanstalk.R;

public class ProfileEditFragment extends Fragment {
    private EditText etName;
    private EditText etStatusMessage;


    public ProfileEditFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile_edit, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);

    }

    private void initView(View view) {
        etName = view.findViewById(R.id.et_name);
        etStatusMessage = view.findViewById(R.id.et_status_message);
    }

    private void setupEtName() {
        etName.setFocusable(true);
        etName.setClickable(true);
    }

    private void setupEtStatusMessage() {

    }

}