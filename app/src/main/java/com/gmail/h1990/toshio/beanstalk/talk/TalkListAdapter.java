package com.gmail.h1990.toshio.beanstalk.talk;

import static com.gmail.h1990.toshio.beanstalk.common.Constants.IMAGES_FOLDER;
import static com.gmail.h1990.toshio.beanstalk.common.Constants.SLASH;
import static com.gmail.h1990.toshio.beanstalk.common.Extras.PHOTO_NAME;
import static com.gmail.h1990.toshio.beanstalk.common.Extras.USER_KEY;
import static com.gmail.h1990.toshio.beanstalk.common.Extras.USER_NAME;

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
        View view = LayoutInflater.from(context).inflate(R.layout.talk_list_layout, parent, false);
        return new TalkListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TalkListAdapter.TalkListViewHolder holder, int position) {
        TalkListModel talkListModel = talkListModelList.get(position);
        holder.tvName.setText(talkListModel.getUserName());

        StorageReference storageReference =
                FirebaseStorage.getInstance().getReference().child(IMAGES_FOLDER + SLASH + talkListModel.getPhotoName());
        storageReference.getDownloadUrl().addOnSuccessListener(uri -> GlideUtils.setPhoto(context, uri, holder.ivProfile));

        holder.llTalkList.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, TalkActivity.class);
                intent.putExtra(USER_KEY, talkListModel.getUserId());
                intent.putExtra(USER_NAME, talkListModel.getUserName());
                intent.putExtra(PHOTO_NAME, talkListModel.getPhotoName());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return talkListModelList.size();
    }

    public class TalkListViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout llTalkList;
        private TextView tvName, tvLastMessage, tvTime, tvUnreadCount;
        private ImageView ivProfile;

        public TalkListViewHolder(@NonNull View itemView) {
            super(itemView);

            llTalkList = itemView.findViewById(R.id.ll_talk_list);
            tvName = itemView.findViewById(R.id.tv_name);
            tvLastMessage = itemView.findViewById(R.id.tv_last_message);
            tvTime = itemView.findViewById(R.id.tv_time);
            tvUnreadCount = itemView.findViewById(R.id.tv_unread_count);
            ivProfile = itemView.findViewById(R.id.iv_profile);
        }
    }
}
