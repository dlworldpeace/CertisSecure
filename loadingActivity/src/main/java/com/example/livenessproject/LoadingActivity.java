package com.example.livenessproject;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.livenessproject.util.HttpHelper;
import com.megvii.livenesslib.LivenessActivity;
import com.megvii.livenessproject.R;
import com.megvii.livenessdetection.Detector;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

public class LoadingActivity extends Activity implements View.OnClickListener {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.loading_layout);
		init();

		// Allow Network Connection to be made on main thread
		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
	}

	private void init() {
		findViewById(R.id.loading_layout_livenessBtn).setOnClickListener(this);
		TextView versionNameView = (TextView) findViewById(R.id.loading_layout_version);
		versionNameView.setText(Detector.getVersion());

		// init textbox icons
		EditText username = (EditText) findViewById(R.id.loading_layout_username);
		Drawable drawable=getResources().getDrawable(R.drawable.icon_email);
		drawable.setBounds(0,0,64,64);//restrict size
		username.setCompoundDrawables(drawable,null,null,null);// use as drawableLeft
		EditText password = (EditText) findViewById(R.id.loading_layout_password);
		Drawable drawable2=getResources().getDrawable(R.drawable.icon_lock);
		drawable2.setBounds(0,0,64,64);//restrict size
		password.setCompoundDrawables(drawable2,null,null,null);// use as drawableLeft

		// init login with password
		Button loginWithPassword = (Button) findViewById(R.id.loading_layout_loginBtn);
		loginWithPassword.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				//EditText username = (EditText) findViewById(R.id.loading_layout_username);
				//EditText password = (EditText) findViewById(R.id.loading_layout_password);
				//String result = HttpHelper.Companion.loginWithPassword_Post(username.getText().toString().trim(), password.getText().toString().trim());
				String result = HttpHelper.Companion.loginWithPassword("david@sharker.com.sg", "test");

				try{
					JSONObject jsonObject = new JSONObject(result);
					boolean invalidLogin = jsonObject.has("message");
					if(invalidLogin) {
						Toast.makeText(getApplicationContext(), "Incorrect Username or Password,",
								Toast.LENGTH_SHORT).show();
					} else {
						Intent intent = new Intent(LoadingActivity.this, MainActivity.class);
						intent.putExtra("json", jsonObject.getString("user"));
						startActivity(intent);
					}
				}catch(JSONException e){
					e.printStackTrace();
				}
			}
		});
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
			startActivityForResult(new Intent(this, LivenessActivity.class), PAGE_INTO_LIVENESS);
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