package com.gmail.h1990.toshio.beanstalk.changecolor;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gmail.h1990.toshio.beanstalk.databinding.ColorBtnLayoutBinding;

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
        ColorBtnLayoutBinding binding =
                ColorBtnLayoutBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ColorViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ColorViewHolder holder, int position) {
        ColorModel colorModel = colorModelList.get(position);
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(colorModel.getStyleRes(), typedValue, true);
        int argb = context.getColor(colorModel.getArgb());
        GradientDrawable bgShape = (GradientDrawable) holder.binding.ibColor.getBackground();
        bgShape.setColor(argb);
        holder.binding.ibColor.setOnClickListener(view -> {
            if (callback != null) {
                callback.onColorBtnClick(colorModel.getStyleRes());
            }
        });
    }

    @Override
    public int getItemCount() {
        return colorModelList.size();
    }

    public static class ColorViewHolder extends RecyclerView.ViewHolder {
        private final ColorBtnLayoutBinding binding;

        public ColorViewHolder(@NonNull ColorBtnLayoutBinding item) {
            super(item.getRoot());
            binding = item;
        }
    }
}
