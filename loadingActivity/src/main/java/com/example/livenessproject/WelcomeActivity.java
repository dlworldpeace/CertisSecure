package com.example.livenessproject;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.widget.ImageView;

import com.megvii.livenessproject.R;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class WelcomeActivity extends Activity {

    @Bind(R.id.iv_entry)
    ImageView mIVEntry;

    private static final int ANIM_TIME = 2500;

    private static final float SCALE_END = 1.15F;

//    Left here for extension of multiple welcome images
//    private static final int[] Imgs={
//            R.drawable.welcomeimg1,R.drawable.welcomeimg2,
//            R.drawable.welcomeimg3,R.drawable.welcomeimg4,
//            R.drawable.welcomeimg5, R.drawable.welcomeimg6,
//            R.drawable.welcomeimg7,R.drawable.welcomeimg8,
//            R.drawable.welcomeimg9,R.drawable.welcomeimg10,
//            R.drawable.welcomeimg11,R.drawable.welcomeimg12,};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        ButterKnife.bind(this);

//        Random random = new Random(SystemClock.elapsedRealtime());//SystemClock.elapsedRealtime() 从开机到现在的毫秒数（手机睡眠(sleep)的时间也包括在内）
//        mIVEntry.setImageResource(Imgs[random.nextInt(Imgs.length)]);
        mIVEntry.setImageResource(R.drawable.certis_security_plus_slider_mobile);

        Observable.timer(1000, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Long>()
                {

                    @Override
                    public void call(Long aLong)
                    {
                        startAnim();
                    }
                });
    }


    private void startAnim() {

        ObjectAnimator animatorX = ObjectAnimator.ofFloat(mIVEntry, "scaleX", 1f, SCALE_END);
        ObjectAnimator animatorY = ObjectAnimator.ofFloat(mIVEntry, "scaleY", 1f, SCALE_END);

        AnimatorSet set = new AnimatorSet();
        set.setDuration(ANIM_TIME).play(animatorX).with(animatorY);
        set.start();

        set.addListener(new AnimatorListenerAdapter()
        {

            @Override
            public void onAnimationEnd(Animator animation)
            {
                startActivity(new Intent(WelcomeActivity.this, LoadingActivity.class));
                WelcomeActivity.this.finish();
            }
        });
    }
}
