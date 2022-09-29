package com.gmail.h1990.toshio.beanstalk.friend;

import static com.gmail.h1990.toshio.beanstalk.common.Constants.IMAGES_FOLDER;
import static com.gmail.h1990.toshio.beanstalk.common.Extras.PHOTO_NAME;
import static com.gmail.h1990.toshio.beanstalk.common.Extras.USER_KEY;
import static com.gmail.h1990.toshio.beanstalk.common.Extras.USER_NAME;
import static com.gmail.h1990.toshio.beanstalk.common.NodeNames.PHOTO;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gmail.h1990.toshio.beanstalk.R;
import com.gmail.h1990.toshio.beanstalk.databinding.FriendListLayoutBinding;
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
        FriendListLayoutBinding binding =
                FriendListLayoutBinding.inflate(LayoutInflater.from(parent.getContext()), parent,
                        false);
        return new FriendListViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendListViewHolder holder, int position) {
        FriendListModel friendListModel = friendModelList.get(position);
        holder.binding.tvName.setText(friendListModel.getUserName());
        holder.binding.tvStatusMessage.setText(friendListModel.getStatusMessage());
        StorageReference storageReference =
                FirebaseStorage.getInstance().getReference().child(IMAGES_FOLDER).child(PHOTO).child(friendListModel.getPhotoName());
        storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
            GlideUtils.setPhoto(context, uri, R.drawable.default_profile, holder.binding.ivProfile);
        });

        holder.binding.llFriendList.setOnClickListener(v -> {
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
        private final FriendListLayoutBinding binding;

        public FriendListViewHolder(@NonNull FriendListLayoutBinding item) {
            super(item.getRoot());
            binding = item;
        }
    }
}

