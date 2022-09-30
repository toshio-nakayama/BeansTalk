package com.gmail.h1990.toshio.beanstalk.profile;

import android.app.Dialog;
import android.os.Bundle;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.gmail.h1990.toshio.beanstalk.common.Extras;
import com.gmail.h1990.toshio.beanstalk.databinding.FragmentMessageDisplayBinding;

public class MessageDisplayFragment extends DialogFragment {

    private FragmentMessageDisplayBinding binding;
    public static final String DIALOG_TAG = "message_display_fragment";


    public MessageDisplayFragment() {
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        binding = FragmentMessageDisplayBinding.inflate(requireActivity().getLayoutInflater());
        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(binding.getRoot());
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        String message = getArguments().getString(Extras.STATUS_MESSAGE, "");
        binding.tvStatusMessage.setText(message);
        binding.ibClose.setOnClickListener(view -> {
            dismiss();
        });
    }
}