package com.example.livenessproject;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.megvii.livenesslib.LivenessActivity;
import com.megvii.livenessproject.R;
import com.megvii.livenessdetection.Detector;
import com.umeng.analytics.MobclickAgent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

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
		TextView versionNameView = findViewById(R.id.loading_layout_version);
		versionNameView.setText(Detector.getVersion());

		// init textbox icons
		EditText username = findViewById(R.id.loading_layout_username);
		Drawable drawable=getResources().getDrawable(R.drawable.icon_email);
		drawable.setBounds(0,0,64,64);//max size
		username.setCompoundDrawables(drawable,null,null,null);// use as drawableLeft
		EditText password = findViewById(R.id.loading_layout_password);
		Drawable drawable2=getResources().getDrawable(R.drawable.icon_lock);
		drawable2.setBounds(0,0,64,64);//max size
		password.setCompoundDrawables(drawable2,null,null,null);// use as drawableLeft

		// init login with password
		Button loginWithPassword = findViewById(R.id.loading_layout_loginBtn);
		loginWithPassword.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				EditText username = findViewById(R.id.loading_layout_username);
				EditText password = findViewById(R.id.loading_layout_password);
				String result = loginWithPassword_Post(username.getText().toString(), password.getText().toString());
				Log.d("loginWithPasswordResult", "_____________________" + result + "_____________________");
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

	private String loginWithPassword_Post(String username, String password) {
		// HttpClient 6.0 is already outdated
		String result = "";
		BufferedReader reader = null;

		try {
			URL url = new URL("http://3.0.121.132:3000/auth/login");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("Charset", "UTF-8");
			conn.setRequestProperty("Content-Type","application/json");
			// Set accept type here, if not will receive Error 415
			// You can use conn.setRequestProperty("accept","*/*") to accept all types
			conn.setRequestProperty("accept","application/json");

            String Json = "{\"username\":\""+ username +"\", \"password\":\"" + password + "\"}";
            byte[] writebytes = Json.getBytes();
            conn.setRequestProperty("Content-Length", String.valueOf(writebytes.length));
            OutputStream outwritestream = conn.getOutputStream();
            outwritestream.write(Json.getBytes());
            outwritestream.flush();
            outwritestream.close();
            Log.d("Login_usrnm&pswd", "doJsonPost: conn"+conn.getResponseCode());

			if (conn.getResponseCode() == 200) {
				reader = new BufferedReader(
						new InputStreamReader(conn.getInputStream()));
				result = reader.readLine();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}
}