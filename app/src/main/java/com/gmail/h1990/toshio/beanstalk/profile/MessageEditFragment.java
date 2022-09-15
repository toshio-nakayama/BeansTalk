package com.gmail.h1990.toshio.beanstalk.profile;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gmail.h1990.toshio.beanstalk.R;

public class MessageEditFragment extends Fragment {

    private String mParam1;
    private String mParam2;

    public MessageEditFragment() {
        // Required empty public constructor
    }

    public static MessageEditFragment newInstance() {
        MessageEditFragment fragment = new MessageEditFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_message_edit, container, false);
    }
}