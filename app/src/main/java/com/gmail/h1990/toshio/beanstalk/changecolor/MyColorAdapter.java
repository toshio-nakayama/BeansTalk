package com.gmail.h1990.toshio.beanstalk.changecolor;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gmail.h1990.toshio.beanstalk.R;

import java.util.List;

public class MyColorAdapter extends RecyclerView.Adapter<MyColorAdapter.ColorViewHolder> {

    private List<ColorModel> colorModelList;
    private Context context;

    public MyColorAdapter(List<ColorModel> colorModelList, Context context) {
        this.colorModelList = colorModelList;
        this.context = context;
    }

    @NonNull
    @Override
    public ColorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.color_btn_layout,
                parent, false);
        return new ColorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ColorViewHolder holder, int position) {
        ColorModel colorModel = colorModelList.get(position);
        GradientDrawable bgShape = (GradientDrawable) holder.ibColor.getBackground();
        bgShape.setColor(colorModel.getArgb());
        holder.ibColor.setOnClickListener(view -> {
        });
    }

    @Override
    public int getItemCount() {
        return colorModelList.size();
    }

    public static class ColorViewHolder extends RecyclerView.ViewHolder {
        private final ImageButton ibColor;

        public ColorViewHolder(@NonNull View itemView) {
            super(itemView);
            ibColor = itemView.findViewById(R.id.ib_color);
        }
    }
}
