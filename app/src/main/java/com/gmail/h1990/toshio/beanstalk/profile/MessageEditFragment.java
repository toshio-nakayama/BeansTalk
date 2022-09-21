package com.gmail.h1990.toshio.beanstalk.profile;

import static com.gmail.h1990.toshio.beanstalk.common.Extras.REQUEST_KEY_STATUS_MESSAGE;
import static com.gmail.h1990.toshio.beanstalk.common.Extras.REQUEST_KEY_USER_NAME;
import static com.gmail.h1990.toshio.beanstalk.common.Extras.STATUS_MESSAGE;
import static com.gmail.h1990.toshio.beanstalk.common.Extras.USER_NAME;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.gmail.h1990.toshio.beanstalk.R;

public class MessageEditFragment extends DialogFragment {
    private EditText etInput;
    private TextView tvWordCount;
    private ImageButton ibClose;
    private Button btSave;

    public MessageEditFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Dialog dialog = getDialog();
        dialog.setContentView(R.layout.fragment_message_edit);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        return inflater.inflate(R.layout.fragment_message_edit, container, false);
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
        getParentFragmentManager().setFragmentResultListener(REQUEST_KEY_USER_NAME, this,
                (requestKey, result) -> {
                    String name = result.getString(USER_NAME);
                    etInput.setText(name);
                });
        getParentFragmentManager().setFragmentResultListener(REQUEST_KEY_STATUS_MESSAGE, this,
                (requestKey, result) -> {
                    String message = result.getString(STATUS_MESSAGE);
                    etInput.setText(message);
                });

    }

    private void initView() {
        Dialog dialog = getDialog();
        etInput = dialog.findViewById(R.id.et_input);
        tvWordCount = dialog.findViewById(R.id.tv_word_count);
        ibClose = dialog.findViewById(R.id.ib_close);
        btSave = dialog.findViewById(R.id.bt_save);
    }
}