package com.gmail.h1990.toshio.beanstalk.qrcode;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.gmail.h1990.toshio.beanstalk.R;
import com.google.firebase.auth.FirebaseAuth;

public class QRGenerateFragment extends Fragment {
    private ImageButton ibClose;
    private ImageView ivQrcode;
    private OnCloseButtonClickListener onCloseButtonClickListener;
    private FirebaseAuth firebaseAuth;

    public QRGenerateFragment() {
    }

    public interface OnCloseButtonClickListener {
        public void onFragmentClose();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_qrgenerate, container, false);
        ivQrcode = view.findViewById(R.id.iv_qrcode);
        ibClose = view.findViewById(R.id.ib_close);
        ibClose.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction().remove(QRGenerateFragment.this).commit();
            onCloseButtonClickListener.onFragmentClose();
        });
        generateQRCode();
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnCloseButtonClickListener) {
            onCloseButtonClickListener = (OnCloseButtonClickListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onCloseButtonClickListener = null;
    }

    private void generateQRCode() {
        String userId = firebaseAuth.getCurrentUser().getUid();
        Bitmap bitmap = ZxingUtils.createBitmap(userId);
        ivQrcode.setImageBitmap(bitmap);
    }

}