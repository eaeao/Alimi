package com.tktv.alimi;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import org.json.JSONArray;

import com.tktv.alimi.Settings.Functions;
import com.tktv.alimi.Settings.Settings;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.io.IOException;

public class LoginActivity extends Activity {

	public static final String EXTRA_MESSAGE = "message";
	public static final String PROPERTY_REG_ID = "registration_id";
	private static final String PROPERTY_APP_VERSION = "appVersion";
	private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	static final String TAG = "AlimiService";

	GoogleCloudMessaging gcm;
	String SENDER_ID = "814451836315";
	String regid;
	Context context;

	Settings settings;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);

		settings = (Settings) getApplicationContext();

		context = this;

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction().add(R.id.container, new PlaceholderFragment()).commit();
		}

		if (checkPlayServices()) {
			gcm = GoogleCloudMessaging.getInstance(this);
			regid = getRegistrationId(context);

			if (regid.isEmpty()) {
				registerInBackground();
			}
		} else {
			Log.i(TAG, "No valid Google Play Services APK found.");
		}
	}

	private boolean checkPlayServices() {
		try{
			int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
			if (resultCode != ConnectionResult.SUCCESS) {
				if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
					GooglePlayServicesUtil.getErrorDialog(resultCode, this,
							PLAY_SERVICES_RESOLUTION_REQUEST).show();
				} else {
					Log.i(TAG, "This device is not supported.");
					finish();
				}
				return false;
			}
			return true;
		}catch(Exception e){
			return false;
		}
	}

	private void storeRegistrationId(Context context, String regId) {
		final SharedPreferences prefs = getGcmPreferences(context);
		int appVersion = getAppVersion(context);
		Log.i(TAG, "Saving regId on app version " + appVersion);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(PROPERTY_REG_ID, regId);
		editor.putInt(PROPERTY_APP_VERSION, appVersion);
		editor.commit();
	}

	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	private String getRegistrationId(Context context) {
		final SharedPreferences prefs = getGcmPreferences(context);
		String registrationId = prefs.getString(PROPERTY_REG_ID, "");
		if (registrationId.isEmpty()) {
			Log.i(TAG, "Registration not found.");
			return "";
		}
		// Check if app was updated; if so, it must clear the registration ID
		// since the existing regID is not guaranteed to work with the new
		// app version.
		int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
		int currentVersion = getAppVersion(context);
		if (registeredVersion != currentVersion) {
			Log.i(TAG, "App version changed.");
			return "";
		}
		Log.d("SF", registrationId);
		return registrationId;
	}

	private void registerInBackground() {
		new AsyncTask<Void, Void, String>() {
			@Override
			protected String doInBackground(Void... params) {
				String msg = "";
				try {
					if (gcm == null) {
						gcm = GoogleCloudMessaging.getInstance(context);
					}
					regid = gcm.register(SENDER_ID);
					msg = "Device registered, registration ID=" + regid;
					settings.Phone_regid = regid;
					sendRegistrationIdToBackend();
				} catch (IOException ex) {
					msg = "Error :" + ex.getMessage();
				}
				return msg;
			}

			@Override
			protected void onPostExecute(String msg) {
				Log.i("@@@@@@@@@@@@@@@", msg);
			}
		}.execute(null, null, null);
	}

	private static int getAppVersion(Context context) {
		try {
			PackageInfo packageInfo = context.getPackageManager()
					.getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (PackageManager.NameNotFoundException e) {
			// should never happen
			throw new RuntimeException("Could not get package name: " + e);
		}
	}

	/**
	 * @return Application's {@code SharedPreferences}.
	 */
	private SharedPreferences getGcmPreferences(Context context) {
		// This sample app persists the registration ID in shared preferences, but
		// how you store the regID in your app is up to you.
		return getSharedPreferences(LoginActivity.class.getSimpleName(),
				Context.MODE_PRIVATE);
	}

	private void sendRegistrationIdToBackend() {
		// Your implementation here.
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	public static class PlaceholderFragment extends Fragment implements OnClickListener {

		private LinearLayout ll_loading;
		private Button btnLogin;
		private EditText etID;
		private EditText etPW;

		JSONArray ja;
		private String str_id = "";
		private String str_pw = "";

		Settings settings;

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_login, container, false);

			settings = (Settings) getActivity().getApplicationContext();

			ll_loading = (LinearLayout) rootView.findViewById(R.id.ll_loading);
			btnLogin = (Button) rootView.findViewById(R.id.btnLogin);
			etID = (EditText) rootView.findViewById(R.id.etID);
			etPW = (EditText) rootView.findViewById(R.id.etPW);

			btnLogin.setOnClickListener((OnClickListener) this);

			if (!settings.getPref("shop_id").equals("")) {
				settings.shop_id = settings.getPref("shop_id");
				settings.shop_pw = settings.getPref("shop_pw");
				settings.shop_name = settings.getPref("shop_name");
				settings.shop_url = settings.getPref("shop_url");
				settings.shop_url2 = settings.getPref("shop_url2");
				tryLogin(settings.shop_id,settings.shop_pw);
			}

			return rootView;
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
				case R.id.btnLogin:
					tryLogin(etID.getText().toString(),etPW.getText().toString());
					break;
			}
		}

		private void tryLogin(String sid, String spw){
			str_id = sid;
			str_pw = spw;
			if (!str_id.equals("") && !str_pw.equals("")) {
				ll_loading.setVisibility(View.VISIBLE);
				Log.i("Click", "!!");
				new LoginTask().execute((Void) null);
			}
		}

		class LoginTask extends AsyncTask<Void, Void, Boolean> {
			protected Boolean doInBackground(Void... Void) {
				try {
					String url = "http://eaeao.herokuapp.com/login/?sid=" + str_id + "&pw=" + str_pw + "&regid=" + settings.Phone_regid;
					Log.i("URL>>>>>>>>>>",url);
					ja = Functions.GET(url);
					settings.shop_id = ja.getJSONObject(0).getString("sid");
					settings.shop_pw = str_pw;
					settings.shop_name = ja.getJSONObject(0).getString("sname");
					settings.shop_url = ja.getJSONObject(0).getString("url");
					settings.shop_url2 = ja.getJSONObject(0).getString("url2");
					return true;
				} catch (Exception e) {
					return false;
				}
			}

			protected void onPostExecute(Boolean result) {
				ll_loading.setVisibility(View.GONE);
				if (result) {
					settings.setPref("shop_id", settings.shop_id);
					settings.setPref("shop_pw", settings.shop_pw);
					settings.setPref("shop_name", settings.shop_name);
					settings.setPref("shop_url", settings.shop_url);
					settings.setPref("shop_url2", settings.shop_url2);
					startActivity(new Intent(getActivity(), MainActivity.class));
					getActivity().finish();
				} else {
					new AlertDialog.Builder(getActivity()).setTitle("로그인 실패")
							.setMessage("아이디와 비밀번호를 다시 확인 후 시도해주세요.")
							.setPositiveButton("예", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub
								}
							}).show();
				}
				return;
			}
		}
	}
}