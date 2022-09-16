package com.gmail.h1990.toshio.beanstalk.login;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;

import com.gmail.h1990.toshio.beanstalk.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ColorSelectionFragment extends DialogFragment implements View.OnClickListener {
    private ImageButton ibMagenta, ibTurquoise, ibYellow, ibLime, ibEmerald;

    public ColorSelectionFragment() {
    }

    public static ColorSelectionFragment newInstance(String param1, String param2) {
        ColorSelectionFragment fragment = new ColorSelectionFragment();
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_color_selection, null);
        initView(view);
        List<ImageButton> ibList = Arrays.asList(ibMagenta, ibTurquoise, ibYellow, ibLime,
                ibEmerald);
        ibList.forEach(ib -> {ib.setOnClickListener(this);});
        builder.setView(view);
        return builder.create();
    }

    private void initView(View view) {
        ibMagenta = view.findViewById(R.id.ib_magenta);
        ibTurquoise = view.findViewById(R.id.ib_turquoise);
        ibYellow = view.findViewById(R.id.ib_yellow);
        ibLime = view.findViewById(R.id.ib_lime);
        ibEmerald = view.findViewById(R.id.ib_emerald);
    }

    @Override
    public void onClick(View view) {
        System.out.println("<<<<<<<<<<<<<<<<<<<<<<click");
    }
}