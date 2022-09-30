package com.gmail.h1990.toshio.beanstalk.reaction;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gmail.h1990.toshio.beanstalk.databinding.ReactionBtnLayoutBinding;
import com.gmail.h1990.toshio.beanstalk.model.ReactionModel;
import com.gmail.h1990.toshio.beanstalk.talk.TalkActivity;

import java.util.List;

public class ReactionAdapter extends RecyclerView.Adapter<ReactionAdapter.ReactionViewHolder> {

    private ReactionSelectedListener callback;
    private String selectedMessageId;
    private ReactionBtnLayoutBinding binding;

    public interface ReactionSelectedListener {
        public void onReactionSelected();
    }

    private List<ReactionModel> reactionModels;
    private Context context;

    public ReactionAdapter(List<ReactionModel> reactionModels, Context context,
                           String selectedMessageId, ReactionSelectedListener callback) {
        this.reactionModels = reactionModels;
        this.context = context;
        this.selectedMessageId = selectedMessageId;
        this.callback = callback;
    }

    @NonNull
    @Override
    public ReactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ReactionBtnLayoutBinding binding =
                ReactionBtnLayoutBinding.inflate(LayoutInflater.from(parent.getContext()), parent,
                        false);
        return new ReactionViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ReactionViewHolder holder, int position) {
        ReactionModel reactionModel = reactionModels.get(position);
        holder.binding.ibReaction.setBackground(reactionModel.getGraphics());
        holder.binding.ibReaction.setOnClickListener(view -> {
            ((TalkActivity) context).sendReaction(selectedMessageId, reactionModel.getReactionState());
            if (callback != null) {
                callback.onReactionSelected();
            }
        });

    }

    @Override
    public int getItemCount() {
        return reactionModels.size();
    }

    public class ReactionViewHolder extends RecyclerView.ViewHolder {

        private final ReactionBtnLayoutBinding binding;


        public ReactionViewHolder(@NonNull ReactionBtnLayoutBinding item) {
            super(item.getRoot());
            binding = item;

        }
    }
}
