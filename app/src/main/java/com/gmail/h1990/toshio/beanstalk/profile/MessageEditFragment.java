package com.gmail.h1990.toshio.beanstalk.profile;

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
import com.gmail.h1990.toshio.beanstalk.common.Constants;
import com.gmail.h1990.toshio.beanstalk.common.Extras;
import com.gmail.h1990.toshio.beanstalk.common.NodeNames;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.MessageFormat;

public class MessageEditFragment extends DialogFragment {
    private EditText etInput;
    private TextView tvWordCount;
    private ImageButton ibClose;
    private Button btSave;
    private int editType;
    private FirebaseUser currentUser;
    private DatabaseReference currentUserRef;
    public static final String DIALOG_TAG = "message_edit_fragment";

    public MessageEditFragment() {
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
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
        DatabaseReference databaseRootRef = FirebaseDatabase.getInstance().getReference();
        currentUserRef = databaseRootRef.child(NodeNames.USERS).child(currentUser.getUid());
        initView();
    }


    @Override
    public void onStart() {
        super.onStart();
        getParentFragmentManager().setFragmentResultListener(Extras.REQUEST_KEY, this,
                (requestKey, result) -> {
                    editType = result.getInt(Extras.EDIT_TYPE);
                    if (editType == 0) {
                        setupInputBasedOnName();
                    } else if (editType == 1) {
                        setupInputBasedOnStatusMessage();
                    }
                    String contents = result.getString(Extras.CONTENTS);
                    etInput.setText(contents);
                });
        ibClose.setOnClickListener(view -> dismiss());
        btSave.setOnClickListener(view -> onBtnSaveClick());
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
        filters[0] = new InputFilter.LengthFilter(Constants.MAX_LENGTH_NAME);
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
                tvWordCount.setText(MessageFormat.format("{0}/{1}", name.length(), Constants.MAX_LENGTH_NAME));
                if (name.length() < Constants.MIN_LENGTH_NAME) {
                    if (isAdded()) {
                        etInput.setError(getString(R.string.less_characters_warning,
                                String.valueOf(Constants.MIN_LENGTH_NAME)));
                    }
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
        filters[0] = new InputFilter.LengthFilter(Constants.MAX_LENGTH_STATUS_MESSAGE);
        etInput.setFilters(filters);
        etInput.setMaxLines(Integer.MAX_VALUE);
        etInput.setHorizontallyScrolling(false);
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
                tvWordCount.setText(MessageFormat.format("{0}/{1}", statusMessage.length(), Constants.MAX_LENGTH_STATUS_MESSAGE));
            }
        });
    }

    private void onBtnSaveClick() {
        if (editType == 0) {
            updateName();
            dismiss();
        } else if (editType == 1) {
            updateStatusMessage();
            dismiss();
        }
    }

    private void updateName() {
        String name = etInput.getText().toString().trim();
        UserProfileChangeRequest profileUpdates =
                new UserProfileChangeRequest.Builder().setDisplayName(name).build();
        currentUser.updateProfile(profileUpdates).addOnSuccessListener(
                unused -> currentUser.updateProfile(profileUpdates).addOnSuccessListener(unused1 -> {
                    currentUserRef.child(NodeNames.NAME).setValue(name).addOnFailureListener(e -> {
                        if (isAdded())
                            Log.e(Constants.TAG, getString(R.string.failed_to_update));
                    }).addOnSuccessListener(unused2 -> {
                        if (isAdded()) {
                            Log.d(Constants.TAG, getString(R.string.user_profile_updated));
                        }
                    });

                }));
    }

    private void updateStatusMessage() {
        String statusMessage = etInput.getText().toString().trim();
        currentUserRef.child(NodeNames.STATUS_MESSAGE).setValue(statusMessage).addOnFailureListener(e -> {
            if (isAdded())
                Log.e(Constants.TAG, getString(R.string.failed_to_update));
        }).addOnSuccessListener(unused -> {
            if (isAdded())
                Log.d(Constants.TAG, getString(R.string.user_profile_updated));
        });
    }
}