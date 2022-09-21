package com.gmail.h1990.toshio.beanstalk.profile;

import static com.gmail.h1990.toshio.beanstalk.common.Extras.REQUEST_KEY_STATUS_MESSAGE;
import static com.gmail.h1990.toshio.beanstalk.common.Extras.REQUEST_KEY_USER_NAME;
import static com.gmail.h1990.toshio.beanstalk.common.Extras.STATUS_MESSAGE;
import static com.gmail.h1990.toshio.beanstalk.common.Extras.USER_NAME;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.gmail.h1990.toshio.beanstalk.R;

public class ProfileEditFragment extends Fragment {
    private TextView tvName;
    private TextView tvStatusMessage;
    private static final String DIALOG_TAG = "message_edit_fragment";


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
        setupTvName();
        setupTvStatusMessage();
    }

    private void initView(View view) {
        tvName = view.findViewById(R.id.tv_name);
        tvStatusMessage = view.findViewById(R.id.tv_status_message);
    }

    private void setupTvName() {
        tvName.setSingleLine(true);
        tvName.setOnClickListener(view -> {
            DialogFragment dialogFragment = new MessageEditFragment();
            Bundle args = new Bundle();
            String name = tvName.getText().toString();
            args.putString(USER_NAME, name);
            getParentFragmentManager().setFragmentResult(REQUEST_KEY_USER_NAME, args);
            dialogFragment.show(getParentFragmentManager(), DIALOG_TAG);
        });
    }


    private void setupTvStatusMessage() {
        tvStatusMessage.setEms(12);
        tvStatusMessage.setSingleLine(true);
        tvStatusMessage.setSelected(true);
        tvStatusMessage.setEllipsize(TextUtils.TruncateAt.END);
        tvStatusMessage.setOnClickListener(view -> {
            DialogFragment dialogFragment = new MessageEditFragment();
            Bundle args = new Bundle();
            String message = tvStatusMessage.getText().toString();
            args.putString(STATUS_MESSAGE, message);
            getParentFragmentManager().setFragmentResult(REQUEST_KEY_STATUS_MESSAGE, args);
            dialogFragment.show(getParentFragmentManager(), DIALOG_TAG);
        });
    }


}