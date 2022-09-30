package com.gmail.h1990.toshio.beanstalk.changecolor;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gmail.h1990.toshio.beanstalk.R;
import com.gmail.h1990.toshio.beanstalk.common.Extras;
import com.gmail.h1990.toshio.beanstalk.model.ColorModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MyColorFragment extends DialogFragment implements MyColorAdapter.ColorBtnClickListener {

    private PreferenceSavedListener callback;
    public static final String DIALOG_TAG = "dialog_color_selection";
    private static final Float INITIAL_VERTICAL_MARGIN = 0f;
    private static final Float INITIAL_HORIZONTAL_MARGIN = 0f;
    private static final int POSITION_Y = 300;


    public MyColorFragment() {
    }

    public interface PreferenceSavedListener {
        public void onPreferenceSaved();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_my_color, null);
        setRecyclerView(view);
        builder.setView(view);
        return builder.create();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Objects.requireNonNull(getDialog()).getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        adjustDialogPosition();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            callback = (PreferenceSavedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(e.getMessage());
        }
    }

    @Override
    public void onColorBtnClick(@StyleRes int styleRes) {
        SharedPreferences sharedPref = requireActivity().getSharedPreferences(Extras.THEME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(Extras.STYLE_RESOURCE, styleRes);
        editor.apply();
        if (callback != null) {
            callback.onPreferenceSaved();
        }
        dismiss();
    }

    private void setRecyclerView(View view) {
        RecyclerView rvContainer = view.findViewById(R.id.rv_container);
        rvContainer.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rvContainer.setLayoutManager(manager);
        RecyclerView.Adapter<MyColorAdapter.ColorViewHolder> adapter = new MyColorAdapter(generateColorList(),
                requireActivity(), this);
        rvContainer.setAdapter(adapter);
    }

    public List<ColorModel> generateColorList() {
        List<ColorModel> colorModels = new ArrayList<>();
        colorModels.add(new ColorModel(R.color.magenta, R.style.Theme_BeansTalk));
        colorModels.add(new ColorModel(R.color.turquoise, R.style.Theme_BeansTalk_Turquoise));
        colorModels.add(new ColorModel(R.color.yellow, R.style.Theme_BeansTalk_Yellow));
        colorModels.add(new ColorModel(R.color.lime, R.style.Theme_BeansTalk_Lime));
        colorModels.add(new ColorModel(R.color.emerald, R.style.Theme_BeansTalk_Emerald));
        return colorModels;
    }

    private void adjustDialogPosition() {
        WindowManager.LayoutParams params = Objects.requireNonNull(getDialog()).getWindow().getAttributes();
        params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
        params.verticalMargin = INITIAL_VERTICAL_MARGIN;
        params.horizontalMargin = INITIAL_HORIZONTAL_MARGIN;
        params.y = POSITION_Y;
    }
}