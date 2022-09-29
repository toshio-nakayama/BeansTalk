package com.gmail.h1990.toshio.beanstalk;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.appcompat.app.AppCompatActivity;

import com.gmail.h1990.toshio.beanstalk.changecolor.ColorUtils;
import com.gmail.h1990.toshio.beanstalk.databinding.ActivitySplashBinding;
import com.gmail.h1990.toshio.beanstalk.login.LoginActivity;

public class SplashActivity extends AppCompatActivity {

    private Animation animation;
    private ActivitySplashBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ColorUtils.setTheme(this);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        animation = AnimationUtils.loadAnimation(this, R.anim.float_animation);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        binding.llLogo.startAnimation(animation);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}