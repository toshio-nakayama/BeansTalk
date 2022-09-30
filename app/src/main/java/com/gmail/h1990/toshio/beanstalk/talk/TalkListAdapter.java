package com.gmail.h1990.toshio.beanstalk.talk;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gmail.h1990.toshio.beanstalk.R;
import com.gmail.h1990.toshio.beanstalk.common.Constants;
import com.gmail.h1990.toshio.beanstalk.common.Extras;
import com.gmail.h1990.toshio.beanstalk.common.NodeNames;
import com.gmail.h1990.toshio.beanstalk.databinding.TalkListLayoutBinding;
import com.gmail.h1990.toshio.beanstalk.model.TalkListModel;
import com.gmail.h1990.toshio.beanstalk.util.GlideUtils;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class TalkListAdapter extends RecyclerView.Adapter<TalkListAdapter.TalkListViewHolder> {

    private Context context;
    private List<TalkListModel> talkListModelList;

    public TalkListAdapter(Context context, List<TalkListModel> talkListModelList) {
        this.context = context;
        this.talkListModelList = talkListModelList;
    }

    @NonNull
    @Override
    public TalkListAdapter.TalkListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        TalkListLayoutBinding binding =
                TalkListLayoutBinding.inflate(LayoutInflater.from(parent.getContext()), parent,
                        false);
        return new TalkListViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TalkListAdapter.TalkListViewHolder holder, int position) {
        TalkListModel talkListModel = talkListModelList.get(position);
        holder.binding.tvName.setText(talkListModel.getUserName());

        StorageReference storageReference =
                FirebaseStorage.getInstance().getReference().child(Constants.IMAGES_FOLDER).child(NodeNames.PHOTO).child(talkListModel.getPhotoName());
        storageReference.getDownloadUrl().addOnSuccessListener(uri -> GlideUtils.setPhoto(context
                , uri, R.drawable.default_profile, holder.binding.ivProfile));

        if (!talkListModel.getUnreadCount().equals("0")) {
            holder.binding.rlUnread.setVisibility(View.VISIBLE);
            holder.binding.tvUnreadCount.setText(talkListModel.getUnreadCount());
        } else {
            holder.binding.rlUnread.setVisibility(View.GONE);
        }

        holder.binding.llTalkList.setOnClickListener(v -> {
            Intent intent = new Intent(context, TalkActivity.class);
            intent.putExtra(Extras.USER_KEY, talkListModel.getUserId());
            intent.putExtra(Extras.USER_NAME, talkListModel.getUserName());
            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return talkListModelList.size();
    }

    public class TalkListViewHolder extends RecyclerView.ViewHolder {

        private TalkListLayoutBinding binding;

        public TalkListViewHolder(@NonNull TalkListLayoutBinding item) {
            super(item.getRoot());
            binding = item;
        }
    }
}
