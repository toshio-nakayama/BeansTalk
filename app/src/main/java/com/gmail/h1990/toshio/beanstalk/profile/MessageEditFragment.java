package com.gmail.h1990.toshio.beanstalk.profile;

import static com.gmail.h1990.toshio.beanstalk.common.Constants.MAX_LENGTH_NAME;
import static com.gmail.h1990.toshio.beanstalk.common.Constants.MAX_LENGTH_STATUS_MESSAGE;
import static com.gmail.h1990.toshio.beanstalk.common.Constants.MIN_LENGTH_NAME;
import static com.gmail.h1990.toshio.beanstalk.common.Constants.TAG;
import static com.gmail.h1990.toshio.beanstalk.common.Extras.CONTENTS;
import static com.gmail.h1990.toshio.beanstalk.common.Extras.EDIT_TYPE;
import static com.gmail.h1990.toshio.beanstalk.common.Extras.REQUEST_KEY;
import static com.gmail.h1990.toshio.beanstalk.common.NodeNames.NAME;
import static com.gmail.h1990.toshio.beanstalk.common.NodeNames.STATUS_MESSAGE;
import static com.gmail.h1990.toshio.beanstalk.common.NodeNames.USERS;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MessageEditFragment extends DialogFragment {
    private EditText etInput;
    private TextView tvWordCount;
    private ImageButton ibClose;
    private Button btSave;
    private int editType;
    private FirebaseUser currentUser;
    private DatabaseReference databaseRootRef, currentUserRef;

    public MessageEditFragment() {
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
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseRootRef = FirebaseDatabase.getInstance().getReference();
        currentUserRef = databaseRootRef.child(USERS).child(currentUser.getUid());
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
        getParentFragmentManager().setFragmentResultListener(REQUEST_KEY, this,
                (requestKey, result) -> {
                    editType = result.getInt(EDIT_TYPE);
                    switch (editType) {
                        case 0:
                            setupInputBasedOnName();
                            break;
                        case 1:
                            setupInputBasedOnStatusMessage();
                            break;
                    }
                    String contents = result.getString(CONTENTS);
                    etInput.setText(contents);
                });
        ibClose.setOnClickListener(view -> {
            dismiss();
        });
        btSave.setOnClickListener(view -> {
            onBtnSaveClick();
        });
    }

    private void initView() {
        Dialog dialog = getDialog();
        etInput = dialog.findViewById(R.id.et_input);
        tvWordCount = dialog.findViewById(R.id.tv_word_count);
        ibClose = dialog.findViewById(R.id.ib_close);
        btSave = dialog.findViewById(R.id.bt_save);
    }

    private void setupInputBasedOnName() {
        InputFilter[] filters = new InputFilter[1];
        filters[0] = new InputFilter.LengthFilter(MAX_LENGTH_NAME);
        etInput.setFilters(filters);
        etInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String name = editable.toString();
                tvWordCount.setText(new StringBuilder().append(name.length()).append("/").append(MAX_LENGTH_NAME).toString());
                if (name.length() < MIN_LENGTH_NAME) {
                    etInput.setError(getString(R.string.less_characters_warning,
                            String.valueOf(MIN_LENGTH_NAME)));
                    btSave.setEnabled(false);
                } else {
                    etInput.setError(null);
                    btSave.setEnabled(true);
                }
            }
        });
    }

    private void setupInputBasedOnStatusMessage() {
        etInput.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        InputFilter[] filters = new InputFilter[1];
        filters[0] = new InputFilter.LengthFilter(MAX_LENGTH_STATUS_MESSAGE);
        etInput.setFilters(filters);
        etInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String statusMessage = editable.toString();
                tvWordCount.setText(new StringBuilder().append(statusMessage.length()).append("/")
                        .append(MAX_LENGTH_STATUS_MESSAGE).toString());
            }
        });
    }

    private void onBtnSaveClick() {
        switch (editType) {
            case 0:
                updateName();
                dismiss();
                break;
            case 1:
                updateStatusMessage();
                dismiss();
                break;
            default:
                break;
        }
    }

    private void updateName() {
        String name = etInput.getText().toString().trim();
        UserProfileChangeRequest profileUpdates =
                new UserProfileChangeRequest.Builder().setDisplayName(name).build();
        currentUser.updateProfile(profileUpdates).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                currentUserRef.child(NAME).setValue(name).addOnCompleteListener(task1 -> {
                    Log.d(TAG, getString(R.string.user_profile_updated));
                });
            } else {
                Log.e(TAG, getString(R.string.failed_to_update));
            }
        });

    }

    private void updateStatusMessage() {
        String statusMessage = etInput.getText().toString().trim();
        currentUserRef.child(STATUS_MESSAGE).setValue(statusMessage).addOnCompleteListener(task -> {

        });
    }
}