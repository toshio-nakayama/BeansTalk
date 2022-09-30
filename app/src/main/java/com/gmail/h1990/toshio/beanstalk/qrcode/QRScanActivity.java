package com.gmail.h1990.toshio.beanstalk.qrcode;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.KeyEvent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.gmail.h1990.toshio.beanstalk.R;
import com.gmail.h1990.toshio.beanstalk.changecolor.ColorUtils;
import com.gmail.h1990.toshio.beanstalk.databinding.ActivityQrscanBinding;
import com.journeyapps.barcodescanner.CaptureManager;

public class QRScanActivity extends AppCompatActivity implements QRGenerateFragment.OnCloseButtonClickListener {

    private CaptureManager captureManager;
    private ActivityQrscanBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ColorUtils.setTheme(this);
        binding = ActivityQrscanBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        captureManager = new CaptureManager(this, binding.dbvScanView);
        captureManager.initializeFromIntent(getIntent(), savedInstanceState);
        captureManager.decode();

        binding.ibClose.setOnClickListener(view1 -> onClose());
        binding.btMyQrcode.setOnClickListener(view12 -> onMyQRCodeBtnClick());
    }

    @Override
    protected void onPause() {
        super.onPause();
        captureManager.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        captureManager.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        captureManager.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        captureManager.onSaveInstanceState(outState);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return binding.dbvScanView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }

    public void onClose() {
        finish();
    }

    public void onMyQRCodeBtnClick() {
        binding.btMyQrcode.setEnabled(false);
        binding.ibClose.setEnabled(false);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.container, new QRGenerateFragment());
        fragmentTransaction.commit();
        onPause();
    }

    @Override
    public void onFragmentClose() {
        binding.btMyQrcode.setEnabled(true);
        binding.ibClose.setEnabled(true);
        onResume();
    }
}