package com.gmail.h1990.toshio.beanstalk.reaction;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gmail.h1990.toshio.beanstalk.R;

import java.util.List;

public class ReactionAdapter extends RecyclerView.Adapter<ReactionAdapter.ReactionViewHolder> {

    private ReactionSelectedListener callback;

    public interface ReactionSelectedListener {
        public void onReactionSelected();
    }

    private List<ReactionModel> reactionModels;
    private Context context;

    public ReactionAdapter(List<ReactionModel> reactionModels, Context context) {
        this.reactionModels = reactionModels;
        this.context = context;
    }

    @NonNull
    @Override
    public ReactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reaction_btn_layout
                , parent, false);
        return new ReactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReactionViewHolder holder, int position) {
        ReactionModel reactionModel = reactionModels.get(position);
        holder.ibReaction.setBackground(reactionModel.getGraphics());
        holder.ibReaction.setOnClickListener(view -> {
        });

    }

    @Override
    public int getItemCount() {
        return reactionModels.size();
    }

    public class ReactionViewHolder extends RecyclerView.ViewHolder {
        private final ImageButton ibReaction;

        public ReactionViewHolder(@NonNull View itemView) {
            super(itemView);
            ibReaction = itemView.findViewById(R.id.ib_reaction);

        }
    }
}
