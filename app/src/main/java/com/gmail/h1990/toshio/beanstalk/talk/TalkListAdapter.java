package com.gmail.h1990.toshio.beanstalk.talk;

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
                FirebaseStorage.getInstance().getReference().child(IMAGES_FOLDER).child(PHOTO).child(talkListModel.getPhotoName());
        storageReference.getDownloadUrl().addOnSuccessListener(uri -> GlideUtils.setPhoto(context
                , uri, R.drawable.default_profile, holder.binding.ivProfile));

        holder.binding.llTalkList.setOnClickListener(v -> {
            Intent intent = new Intent(context, TalkActivity.class);
            intent.putExtra(USER_KEY, talkListModel.getUserId());
            intent.putExtra(USER_NAME, talkListModel.getUserName());
            intent.putExtra(PHOTO_NAME, talkListModel.getPhotoName());
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
