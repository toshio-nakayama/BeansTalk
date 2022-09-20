package com.gmail.h1990.toshio.beanstalk.profile;

import static com.gmail.h1990.toshio.beanstalk.common.Extras.STATUS_MESSAGE;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.gmail.h1990.toshio.beanstalk.R;

public class MessageDisplayFragment extends DialogFragment {
    private TextView tvStatusMessage;
    private ImageButton ibClose;


    public MessageDisplayFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Dialog dialog = getDialog();
        dialog.setContentView(R.layout.fragment_message_display);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        return inflater.inflate(R.layout.fragment_message_display, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        String message = getArguments().getString(STATUS_MESSAGE, "");
        tvStatusMessage.setText(message);
        ibClose.setOnClickListener(view -> {
            dismiss();
        });
    }

    private void initView() {
        tvStatusMessage = getDialog().findViewById(R.id.tv_status_message);
        ibClose = getDialog().findViewById(R.id.ib_close);
    }
}