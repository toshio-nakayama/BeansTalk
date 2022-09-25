package com.gmail.h1990.toshio.beanstalk.qrcode;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.gmail.h1990.toshio.beanstalk.R;
import com.gmail.h1990.toshio.beanstalk.changecolor.ColorUtils;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

public class QRScanActivity extends AppCompatActivity implements QRGenerateFragment.OnCloseButtonClickListener {
    private DecoratedBarcodeView decoratedBarcodeView;
    private CaptureManager captureManager;
    private ImageButton ibClose;
    private Button btMyQrcode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ColorUtils.setTheme(this);
        setContentView(R.layout.activity_qrscan);

        ibClose = findViewById(R.id.ib_close);
        btMyQrcode = findViewById(R.id.bt_my_qrcode);
        decoratedBarcodeView = findViewById(R.id.dbv_scan_view);
        captureManager = new CaptureManager(this, decoratedBarcodeView);
        captureManager.initializeFromIntent(getIntent(), savedInstanceState);
        captureManager.decode();
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
        return decoratedBarcodeView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }

    public void onClose(View view) {
        finish();
    }

    public void onMyQRCodeButtonClick(View view) {
        btMyQrcode.setEnabled(false);
        ibClose.setEnabled(false);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.container, new QRGenerateFragment());
        fragmentTransaction.commit();
        onPause();
    }

    @Override
    public void onFragmentClose() {
        btMyQrcode.setEnabled(true);
        ibClose.setEnabled(true);
        onResume();
    }
}