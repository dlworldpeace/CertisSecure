package com.example.livenessproject;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.megvii.livenesslib.LivenessActivity;
import com.megvii.livenessproject.R;
import com.megvii.livenessdetection.Detector;
import com.umeng.analytics.MobclickAgent;

/**
 * Created by binghezhouke on 14-7-25.
 */
public class LoadingActivity extends Activity implements View.OnClickListener {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.loading_layout);
		init();
	}

	private void init() {
		findViewById(R.id.loading_layout_livenessBtn).setOnClickListener(this);
		TextView versionNameView = ((TextView) findViewById(R.id.loading_layout_version));
		versionNameView.setText(Detector.getVersion());
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.loading_layout_livenessBtn) {
			startActivityForResult(new Intent(this, LivenessActivity.class),
					PAGE_INTO_LIVENESS);
		}
	}

	private static final int PAGE_INTO_LIVENESS = 100;

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == PAGE_INTO_LIVENESS && resultCode == RESULT_OK) {
			String result = data.getStringExtra("result");
			ResultActivity.startActivity(this, result);
		}
	}
}