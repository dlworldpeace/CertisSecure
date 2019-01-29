package com.example.livenessproject;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.widget.ImageView;
import android.widget.TextView;

import com.megvii.livenessproject.R;

import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

import static android.os.SystemClock.sleep;

public class WelcomeActivity extends Activity {

    @Bind(R.id.iv_entry)
    ImageView mIVEntry;

    @Bind(R.id.logo)
    ImageView logo;

    private static final int ANIM_TIME = 2000;

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
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_welcome);
        ButterKnife.bind(this);

//        Random random = new Random(SystemClock.elapsedRealtime());//SystemClock.elapsedRealtime() 从开机到现在的毫秒数（手机睡眠(sleep)的时间也包括在内）
//        mIVEntry.setImageResource(Imgs[random.nextInt(Imgs.length)]);
        mIVEntry.setImageResource(R.drawable.certis_security_plus_slider_mobile);

        AnimationSet aset=new AnimationSet(true);
        AlphaAnimation aa=new AlphaAnimation(0,1);
        aa.setDuration(3000);
        aset.addAnimation(aa);
        logo.startAnimation(aset);

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
                sleep(1000);
                startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
                finish();
            }
        });
    }
}
