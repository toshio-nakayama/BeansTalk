package com.gmail.h1990.toshio.beanstalk.changecolor;

import static com.gmail.h1990.toshio.beanstalk.common.Extras.STYLE_RESOURCE;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gmail.h1990.toshio.beanstalk.R;

import java.util.ArrayList;
import java.util.List;

public class MyColorFragment extends DialogFragment implements MyColorAdapter.ColorBtnClickListener {

    private PreferenceSavedListener callback;
    public static final String DIALOG_TAG = "dialog_color_selection";

    public MyColorFragment() {
    }

    public interface PreferenceSavedListener {
        public void onPreferenceSaved();
    }

    @Override
    public void onColorBtnClick(@StyleRes int styleRes) {
        SharedPreferences sharedPref = requireActivity().getSharedPreferences("theme", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(STYLE_RESOURCE, styleRes);
        editor.commit();
        callback.onPreferenceSaved();
        dismiss();
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

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            callback = (PreferenceSavedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(e.getMessage());
        }
    }


    private void setRecyclerView(View view) {
        RecyclerView rvContainer = view.findViewById(R.id.rv_container);
        rvContainer.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rvContainer.setLayoutManager(manager);
        RecyclerView.Adapter adapter = new MyColorAdapter(generateColorList(requireContext()),
                requireActivity(), this);
        rvContainer.setAdapter(adapter);
    }

    public List<ColorModel> generateColorList(Context context) {
        List<ColorModel> colorModels = new ArrayList<>();
        colorModels.add(new ColorModel(R.color.magenta, R.style.Theme_BeansTalk));
        colorModels.add(new ColorModel(R.color.turquoise, R.style.Theme_BeansTalk_Turquoise));
        colorModels.add(new ColorModel(R.color.yellow, R.style.Theme_BeansTalk_Yellow));
        colorModels.add(new ColorModel(R.color.lime, R.style.Theme_BeansTalk_Lime));
        colorModels.add(new ColorModel(R.color.emerald, R.style.Theme_BeansTalk_Emerald));
        return colorModels;
    }


}