package com.example.livenessproject;

import org.json.JSONException;
import org.json.JSONObject;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.livenessproject.util.HttpHelper;
import com.example.livenessproject.view.RotaterView;
import com.megvii.livenessproject.R;

/**
 * Created by binghezhouke on 14-10-24.
 */
public class ResultActivity extends Activity {
	private TextView mTextView;
	private ImageView mImageView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_result);
		init();
	}

	private void init() {
		mImageView = (ImageView) findViewById(R.id.result_status);
		mTextView = (TextView) findViewById(R.id.result_text_result);
		Button button_next = (Button) findViewById(R.id.result_next);
		String resultOBJ = getIntent().getStringExtra("result");
		boolean needLogin = getIntent().getBooleanExtra("needLogin", false);

		try {
			JSONObject result = new JSONObject(resultOBJ);
			mTextView.setText(result.getString("result"));

			int resID = result.getInt("resultcode");
			if (resID == R.string.verify_success) {
				doPlay(R.raw.meglive_success);
			} else if (resID == R.string.liveness_detection_failed_not_video) {
				doPlay(R.raw.meglive_failed);
			} else if (resID == R.string.liveness_detection_failed_timeout) {
				doPlay(R.raw.meglive_failed);
			} else if (resID == R.string.liveness_detection_failed) {
				doPlay(R.raw.meglive_failed);
			} else {
				doPlay(R.raw.meglive_failed);
			}

			boolean isSuccess = result.getString("result").equals(getResources().getString(R.string.verify_success));
			mImageView.setImageResource(isSuccess ? R.drawable.result_success : R.drawable.result_failded);
			doRotate(isSuccess);

			button_next.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						finish();
					}
				});
			if (isSuccess && needLogin){
				String bestImageBase64 = result.getJSONArray("imgs").get(0).toString();
				String faceLoginResult = HttpHelper.Companion.oneToManyComparison(bestImageBase64);
				if (faceLoginResult.isEmpty() || faceLoginResult.equals("null")) {
					Toast.makeText(this, "Unable to log in.", Toast.LENGTH_SHORT).show();
				} else {
					JSONObject jsonObject = new JSONObject(faceLoginResult);
					final String user = jsonObject.getString("user");
					button_next.setText("Login");
					button_next.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							Intent intent = new Intent(ResultActivity.this, MainActivity.class);
							intent.putExtra("json", user);
							startActivity(intent);
						}
					});
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public static void startActivity(Context context, String status, Boolean needLogin) {
		Intent intent = new Intent(context, ResultActivity.class);
		intent.putExtra("result", status);
		intent.putExtra("needLogin", needLogin);
		context.startActivity(intent);
	}

	private void doRotate(boolean success) {
		RotaterView rotaterView = (RotaterView) findViewById(R.id.result_rotater);
		rotaterView.setColour(success ? 0xff4ae8ab : 0xfffe8c92);
		final ImageView statusView = (ImageView) findViewById(R.id.result_status);
		statusView.setVisibility(View.INVISIBLE);
		statusView.setImageResource(success ? R.drawable.result_success
				: R.drawable.result_failded);

		ObjectAnimator objectAnimator = ObjectAnimator.ofInt(rotaterView,
				"progress", 0, 100);
		objectAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
		objectAnimator.setDuration(600);
		objectAnimator.addListener(new Animator.AnimatorListener() {
			@Override
			public void onAnimationStart(Animator animation) {}

			@Override
			public void onAnimationEnd(Animator animation) {
				Animation scaleanimation = AnimationUtils.loadAnimation(
						ResultActivity.this, R.anim.scaleoutin);
				statusView.startAnimation(scaleanimation);
				statusView.setVisibility(View.VISIBLE);
			}

			@Override
			public void onAnimationCancel(Animator animation) {}

			@Override
			public void onAnimationRepeat(Animator animation) {}
		});
		objectAnimator.start();
	}

	private MediaPlayer mMediaPlayer = null;

	private void doPlay(int rawId) {
		if (mMediaPlayer == null)
			mMediaPlayer = new MediaPlayer();

		mMediaPlayer.reset();
		try {
			AssetFileDescriptor localAssetFileDescriptor = getResources()
					.openRawResourceFd(rawId);
			mMediaPlayer.setDataSource(
					localAssetFileDescriptor.getFileDescriptor(),
					localAssetFileDescriptor.getStartOffset(),
					localAssetFileDescriptor.getLength());
			mMediaPlayer.prepare();
			mMediaPlayer.start();
		} catch (Exception localIOException) {
			localIOException.printStackTrace();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mMediaPlayer != null) {
			mMediaPlayer.reset();
			mMediaPlayer.release();
		}
	}
}