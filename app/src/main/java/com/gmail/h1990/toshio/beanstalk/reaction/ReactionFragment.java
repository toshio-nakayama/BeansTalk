package com.gmail.h1990.toshio.beanstalk.reaction;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gmail.h1990.toshio.beanstalk.R;
import com.gmail.h1990.toshio.beanstalk.common.Extras;
import com.gmail.h1990.toshio.beanstalk.model.ReactionModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class ReactionFragment extends DialogFragment implements ReactionAdapter.ReactionSelectedListener {
    public static final String DIALOG_TAG = "dialog_reaction";
    private String selectedMessageId;
    private static final Float INITIAL_VERTICAL_MARGIN = 0f;
    private static final Float INITIAL_HORIZONTAL_MARGIN = 0f;
    private static final int POSITION_Y = 100;

    public ReactionFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Objects.requireNonNull(getDialog()).getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        adjustDialogPosition();
        return inflater.inflate(R.layout.fragment_reaction, container, false);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        selectedMessageId = requireArguments().getString(Extras.MESSAGE_ID);
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
        RecyclerView.Adapter<ReactionAdapter.ReactionViewHolder> adapter = new ReactionAdapter(generateReactionList(),
                requireActivity(), selectedMessageId, this);
        rvContainer.setAdapter(adapter);
    }


    public List<ReactionModel> generateReactionList() {
        List<ReactionModel> reactionModels = new ArrayList<>();
        reactionModels.add(new ReactionModel(ResourcesCompat.getDrawable(getResources(),
                R.drawable.celebrate, null), ReactionState.STATE_CELEBRATE));
        reactionModels.add(new ReactionModel(ResourcesCompat.getDrawable(getResources(),
                R.drawable.crying, null), ReactionState.STATE_CRYING));
        reactionModels.add(new ReactionModel(ResourcesCompat.getDrawable(getResources(),
                R.drawable.furious, null), ReactionState.STATE_FURIOUS));
        reactionModels.add(new ReactionModel(ResourcesCompat.getDrawable(getResources(),
                R.drawable.pleading, null), ReactionState.STATE_PLEADING));
        reactionModels.add(new ReactionModel(ResourcesCompat.getDrawable(getResources(),
                R.drawable.wink, null), ReactionState.STATE_WINK));
        return reactionModels;
    }

    @Override
    public void onReactionSelected() {
        dismiss();
    }

    private void adjustDialogPosition() {
        WindowManager.LayoutParams params = Objects.requireNonNull(getDialog()).getWindow().getAttributes();
        params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
        params.verticalMargin = INITIAL_VERTICAL_MARGIN;
        params.horizontalMargin = INITIAL_HORIZONTAL_MARGIN;
        params.y = POSITION_Y;
    }
}