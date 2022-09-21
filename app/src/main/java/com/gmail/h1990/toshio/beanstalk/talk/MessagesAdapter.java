package com.gmail.h1990.toshio.beanstalk.talk;

import static com.gmail.h1990.toshio.beanstalk.common.Constants.EXT_JPG;
import static com.gmail.h1990.toshio.beanstalk.common.Constants.IMAGES_FOLDER;
import static com.gmail.h1990.toshio.beanstalk.common.NodeNames.PHOTO;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.gmail.h1990.toshio.beanstalk.R;
import com.gmail.h1990.toshio.beanstalk.model.MessageModel;
import com.gmail.h1990.toshio.beanstalk.util.GlideUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessageViewHolder> {
    private Context context;
    private List<MessageModel> messageModelList;
    private FirebaseAuth firebaseAuth;

    private AdapterCallback mAdapterCallback;

    public MessagesAdapter(Context context, List<MessageModel> messageModelList) {
        this.context = context;
        this.messageModelList = messageModelList;
        this.mAdapterCallback = ((AdapterCallback) context);
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.message_layout, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        MessageModel messageModel = messageModelList.get(position);
        firebaseAuth = FirebaseAuth.getInstance();
        String currentUserId = firebaseAuth.getCurrentUser().getUid();
        String fromUserId = messageModel.getMessageFrom();
        SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        String dateTime = sfd.format(new Date(messageModel.getMessageTime()));
        String[] splitString = dateTime.split(" ");
        String messageTime = splitString[1];
        if (fromUserId.equals(currentUserId)) {
            holder.llSent.setVisibility(View.VISIBLE);
            holder.llReceived.setVisibility(View.GONE);
            holder.tvSentMessage.setText(messageModel.getMessage());
            holder.tvSentMessageTime.setText(messageTime);
        } else {
            holder.llReceived.setVisibility(View.VISIBLE);
            holder.llSent.setVisibility(View.GONE);
            holder.tvReceivedMessage.setText(messageModel.getMessage());
            holder.tvReceivedMessageTime.setText(messageTime);
            String photoName = messageModel.getMessageFrom() + EXT_JPG;
            StorageReference mRef =
                    FirebaseStorage.getInstance().getReference().child(IMAGES_FOLDER).child(PHOTO).child(photoName);
            mRef.getDownloadUrl().addOnSuccessListener(uri -> {
                GlideUtils.setPhoto(context, uri, holder.ivProfile);
            });
            holder.tvReceivedMessage.setOnLongClickListener(view -> {
                try {
                    mAdapterCallback.onMethodCallback();
                } catch (ClassCastException exception) {
                    // do something
                }
                return true;
            });
        }
    }

    @Override
    public int getItemCount() {
        return messageModelList.size();
    }


    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        private final LinearLayout llSent;
        private final LinearLayout llReceived;
        private final TextView tvSentMessage;
        private final TextView tvSentMessageTime;
        private final TextView tvReceivedMessage;
        private final TextView tvReceivedMessageTime;
        private final ImageView ivProfile;
        private final ConstraintLayout clMessage;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            llSent = itemView.findViewById(R.id.ll_sent);
            llReceived = itemView.findViewById(R.id.ll_received);
            tvSentMessage = itemView.findViewById(R.id.tv_sent_message);
            tvSentMessageTime = itemView.findViewById(R.id.tv_sent_message_time);
            tvReceivedMessage = itemView.findViewById(R.id.tv_received_message);
            tvReceivedMessageTime = itemView.findViewById(R.id.tv_received_message_time);
            ivProfile = itemView.findViewById(R.id.iv_profile);
            clMessage = itemView.findViewById(R.id.cl_message);
        }
    }


    public static interface AdapterCallback {
        void onMethodCallback();
    }
}

