package com.gmail.h1990.toshio.beanstalk.talk;

import static com.gmail.h1990.toshio.beanstalk.common.Constants.EXT_JPG;
import static com.gmail.h1990.toshio.beanstalk.common.Constants.IMAGES_FOLDER;
import static com.gmail.h1990.toshio.beanstalk.common.NodeNames.PHOTO;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.gmail.h1990.toshio.beanstalk.R;
import com.gmail.h1990.toshio.beanstalk.databinding.MessageLayoutBinding;
import com.gmail.h1990.toshio.beanstalk.model.MessageModel;
import com.gmail.h1990.toshio.beanstalk.reaction.ReactionState;
import com.gmail.h1990.toshio.beanstalk.util.GlideUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessageViewHolder> {
    private final Context context;
    private final List<MessageModel> messageModelList;
    private ConstraintLayout selectedView;

    public MessagesAdapter(Context context, List<MessageModel> messageModelList) {
        this.context = context;
        this.messageModelList = messageModelList;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MessageLayoutBinding binding =
                MessageLayoutBinding.inflate(LayoutInflater.from(parent.getContext()), parent,
                        false);
        MessageViewHolder holder = new MessageViewHolder(binding);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        MessageModel messageModel = messageModelList.get(position);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        String currentUserId = firebaseAuth.getCurrentUser().getUid();
        String fromUserId = messageModel.getMessageFrom();
        String messageTime = strDateFormat(messageModel.getMessageTime(), "dd-MM-yyyy HH:mm");
        if (fromUserId.equals(currentUserId)) {
            setSentMessage(holder, messageModel.getMessage(), messageTime);
            setReceivedReaction(holder, messageModel.getReactionStatus());
            setTag(holder, messageModel);
        } else {
            setReceivedMessage(holder, messageModel.getMessage(), messageTime);
            String photoName = messageModel.getMessageFrom() + EXT_JPG;
            setPhoto(holder, photoName);
            setSentReaction(holder, messageModel.getReactionStatus());
            setTag(holder, messageModel);
            holder.binding.tvReceivedMessage.setOnLongClickListener(view -> {
                selectedView = holder.binding.clMessage;
                String selectedMessageId = String.valueOf(selectedView.getTag(R.id.TAG_MESSAGE_ID));
                ((TalkActivity) context).generateReactionDialog(selectedMessageId);
                return true;
            });
        }
    }

    @Override
    public int getItemCount() {
        return messageModelList.size();
    }

    private void setTag(MessageViewHolder holder, MessageModel model) {
        holder.binding.clMessage.setTag(R.id.TAG_MESSAGE, model.getMessage());
        holder.binding.clMessage.setTag(R.id.TAG_MESSAGE_ID, model.getMessageId());
        holder.binding.clMessage.setTag(R.id.TAG_MESSAGE_TYPE, model.getMessageType());
    }


    private String strDateFormat(long time, String pattern) {
        SimpleDateFormat sfd = new SimpleDateFormat(pattern);
        String dateTime = sfd.format(new Date(time));
        String[] splitString = dateTime.split(" ");
        String messageTime = splitString[1];
        return messageTime;
    }

    private void setPhoto(MessageViewHolder holder, String photoName) {
        StorageReference mRef =
                FirebaseStorage.getInstance().getReference().child(IMAGES_FOLDER).child(PHOTO).child(photoName);
        mRef.getDownloadUrl().addOnSuccessListener(uri -> {
            GlideUtils.setPhoto(context, uri, R.drawable.default_profile, holder.binding.ivProfile);
        });
    }

    private void setSentMessage(MessageViewHolder holder, String message, String messageTime) {
        holder.binding.llSent.setVisibility(View.VISIBLE);
        holder.binding.llReceived.setVisibility(View.GONE);
        holder.binding.llReceivedReaction.setVisibility(View.VISIBLE);
        holder.binding.llSentReaction.setVisibility(View.GONE);
        holder.binding.tvSentMessage.setText(message);
        holder.binding.tvSentMessageTime.setText(messageTime);
    }


    private void setReceivedMessage(MessageViewHolder holder, String message, String messageTime) {
        holder.binding.llReceived.setVisibility(View.VISIBLE);
        holder.binding.llSent.setVisibility(View.GONE);
        holder.binding.llSentReaction.setVisibility(View.VISIBLE);
        holder.binding.llReceivedReaction.setVisibility(View.GONE);
        holder.binding.tvReceivedMessage.setText(message);
        holder.binding.tvReceivedMessageTime.setText(messageTime);
    }

    private void setSentReaction(MessageViewHolder holder, int reactionStatus) {
        if (ReactionState.STATE_CELEBRATE.containsFlag(reactionStatus)) {
            holder.binding.ivSentCelebrate.setVisibility(View.VISIBLE);
        }
        if (ReactionState.STATE_CRYING.containsFlag(reactionStatus)) {
            holder.binding.ivSentCrying.setVisibility(View.VISIBLE);
        }
        if (ReactionState.STATE_FURIOUS.containsFlag(reactionStatus)) {
            holder.binding.ivSentFurious.setVisibility(View.VISIBLE);
        }
        if (ReactionState.STATE_PLEADING.containsFlag(reactionStatus)) {
            holder.binding.ivSentPleading.setVisibility(View.VISIBLE);
        }
        if (ReactionState.STATE_WINK.containsFlag(reactionStatus)) {
            holder.binding.ivSentWink.setVisibility(View.VISIBLE);
        }
    }

    private void setReceivedReaction(MessageViewHolder holder, int reactionStatus) {
        if (ReactionState.STATE_CELEBRATE.containsFlag(reactionStatus)) {
            holder.binding.ivReceivedCelebrate.setVisibility(View.VISIBLE);
        }
        if (ReactionState.STATE_CRYING.containsFlag(reactionStatus)) {
            holder.binding.ivReceivedCrying.setVisibility(View.VISIBLE);
        }
        if (ReactionState.STATE_FURIOUS.containsFlag(reactionStatus)) {
            holder.binding.ivReceivedFurious.setVisibility(View.VISIBLE);
        }
        if (ReactionState.STATE_PLEADING.containsFlag(reactionStatus)) {
            holder.binding.ivReceivedPleading.setVisibility(View.VISIBLE);
        }
        if (ReactionState.STATE_WINK.containsFlag(reactionStatus)) {
            holder.binding.ivReceivedWink.setVisibility(View.VISIBLE);
        }
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        private MessageLayoutBinding binding;


        public MessageViewHolder(@NonNull MessageLayoutBinding item) {
            super(item.getRoot());
            binding = item;
        }
    }
}

