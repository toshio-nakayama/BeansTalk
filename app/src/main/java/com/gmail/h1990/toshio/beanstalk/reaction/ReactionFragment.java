package com.gmail.h1990.toshio.beanstalk.reaction;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gmail.h1990.toshio.beanstalk.R;

import java.util.ArrayList;
import java.util.List;


public class ReactionFragment extends DialogFragment {
    public static final String DIALOG_TAG = "dialog_reaction";

    public ReactionFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        return inflater.inflate(R.layout.fragment_reaction, container, false);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_reaction, null);
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
        RecyclerView.Adapter adapter = new ReactionAdapter(generateReactionList(),
                requireActivity());
        rvContainer.setAdapter(adapter);
    }


    public List<ReactionModel> generateReactionList() {
        List<ReactionModel> reactionModels = new ArrayList<>();
        reactionModels.add(new ReactionModel(ResourcesCompat.getDrawable(getResources(),
                R.drawable.celebrate, null)));
        reactionModels.add(new ReactionModel(ResourcesCompat.getDrawable(getResources(),
                R.drawable.crying, null)));
        reactionModels.add(new ReactionModel(ResourcesCompat.getDrawable(getResources(),
                R.drawable.furious, null)));
        reactionModels.add(new ReactionModel(ResourcesCompat.getDrawable(getResources(),
                R.drawable.pleading, null)));
        reactionModels.add(new ReactionModel(ResourcesCompat.getDrawable(getResources(),
                R.drawable.wink, null)));
        return reactionModels;
    }
}