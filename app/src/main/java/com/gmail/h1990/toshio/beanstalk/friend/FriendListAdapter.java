package com.gmail.h1990.toshio.beanstalk.friend;

import static com.gmail.h1990.toshio.beanstalk.common.Constants.IMAGES_FOLDER;
import static com.gmail.h1990.toshio.beanstalk.common.Extras.PHOTO_NAME;
import static com.gmail.h1990.toshio.beanstalk.common.Extras.USER_KEY;
import static com.gmail.h1990.toshio.beanstalk.common.Extras.USER_NAME;
import static com.gmail.h1990.toshio.beanstalk.common.NodeNames.PHOTO;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gmail.h1990.toshio.beanstalk.R;
import com.gmail.h1990.toshio.beanstalk.model.FriendListModel;
import com.gmail.h1990.toshio.beanstalk.util.GlideUtils;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class FriendListAdapter extends RecyclerView.Adapter<FriendListAdapter.FriendListViewHolder> {
    private Context context;
    private List<FriendListModel> friendModelList;

    public FriendListAdapter(Context context, List<FriendListModel> friendModelList) {
        this.context = context;
        this.friendModelList = friendModelList;
    }

    @NonNull
    @Override
    public FriendListAdapter.FriendListViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                                     int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.friend_list_layout, parent, false);
        return new FriendListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendListViewHolder holder, int position) {
        FriendListModel friendListModel = friendModelList.get(position);
        holder.tvName.setText(friendListModel.getUserName());
        StorageReference storageReference =
                FirebaseStorage.getInstance().getReference().child(IMAGES_FOLDER).child(PHOTO).child(friendListModel.getPhotoName());
        storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
            GlideUtils.setPhoto(context, uri, R.drawable.default_profile, holder.ivProfile);
        });

        holder.llFriendList.setOnClickListener(v -> {
            Intent intent = new Intent(context, FriendProfileActivity.class);
            intent.putExtra(USER_KEY, friendListModel.getUserId());
            intent.putExtra(USER_NAME, friendListModel.getUserName());
            intent.putExtra(PHOTO_NAME, friendListModel.getPhotoName());
            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return friendModelList.size();
    }

    public class FriendListViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout llFriendList;
        private TextView tvName, tvStatusMessage;
        private ImageView ivProfile;

        public FriendListViewHolder(@NonNull View itemView) {
            super(itemView);

            llFriendList = itemView.findViewById(R.id.ll_friend_list);
            tvName = itemView.findViewById(R.id.tv_name);
            tvStatusMessage = itemView.findViewById(R.id.tv_status_message);
            ivProfile = itemView.findViewById(R.id.iv_profile);
        }
    }
}

