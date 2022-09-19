package com.gmail.h1990.toshio.beanstalk.changecolor;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gmail.h1990.toshio.beanstalk.R;

import java.util.ArrayList;
import java.util.List;

public class MyColorFragment extends DialogFragment {
    public static final String DIALOG_TAG = "dialog_color_selection";

    public MyColorFragment() {
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_my_color, null);
        setRecyclerView(view);
        builder.setView(view);
        return builder.create();
    }

    private void setRecyclerView(View view) {
        RecyclerView rvContainer = view.findViewById(R.id.rv_container);
        rvContainer.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rvContainer.setLayoutManager(manager);
        RecyclerView.Adapter adapter = new MyColorAdapter(generateColorList(requireContext()),
                requireActivity());
        rvContainer.setAdapter(adapter);
    }

    public List<ColorModel> generateColorList(Context context) {
        List<ColorModel> colorModels = new ArrayList<>();
        colorModels.add(new ColorModel(context.getColor(R.color.magenta)));
        colorModels.add(new ColorModel(context.getColor(R.color.turquoise)));
        colorModels.add(new ColorModel(context.getColor(R.color.yellow)));
        colorModels.add(new ColorModel(context.getColor(R.color.lime)));
        colorModels.add(new ColorModel(context.getColor(R.color.emerald)));
        return colorModels;
    }


}