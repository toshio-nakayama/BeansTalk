package com.gmail.h1990.toshio.beanstalk.changecolor;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gmail.h1990.toshio.beanstalk.R;

import java.util.List;

public class MyColorAdapter extends RecyclerView.Adapter<MyColorAdapter.ColorViewHolder> {

    private ColorBtnClickListener callback;

    public interface ColorBtnClickListener {
        public void onColorBtnClick(@ColorInt int argb);
    }

    private List<ColorModel> colorModelList;
    private Context context;

    public MyColorAdapter(List<ColorModel> colorModelList, Context context,
                          ColorBtnClickListener callback) {
        this.colorModelList = colorModelList;
        this.context = context;
        this.callback = callback;
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
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(colorModel.getStyleRes(), typedValue, true);
        int styleRes = typedValue.data;
        int argb = context.getColor(colorModel.getArgb());
        GradientDrawable bgShape = (GradientDrawable) holder.ibColor.getBackground();
        bgShape.setColor(argb);
        holder.ibColor.setOnClickListener(view -> {
            callback.onColorBtnClick(colorModel.getStyleRes());
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
