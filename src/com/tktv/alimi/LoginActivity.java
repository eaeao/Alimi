package com.tktv.alimi;

import org.json.JSONArray;

import com.tktv.alimi.Settings.Functions;
import com.tktv.alimi.Settings.Settings;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
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

public class LoginActivity extends Activity {

	Settings settings;

	Intent service_intent;
	BroadcastReceiver broadcastReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);

		settings = (Settings) getApplicationContext();
		
		setBroadcast_stopService();
		
		if(service_intent!=null){
			stopService(service_intent);
			service_intent = null;
		}

		if(service_intent==null){
			ComponentName componentName = new ComponentName("com.tktv.alimi", "com.tktv.alimi.Settings.Socket");
			service_intent = new Intent();
			service_intent.setComponent(componentName);
			startService(service_intent);
		}

		if(!settings.getPref("shop_id").equals("")){
			settings.shop_id = settings.getPref("shop_id");
			settings.shop_name = settings.getPref("shop_name");
			settings.shop_url = settings.getPref("shop_url");
			startActivity(new Intent(this, MainActivity.class));
			finish();
		}

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction().add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	public void setBroadcast_stopService(){
		broadcastReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				Log.i("setBroadcast_stopService","LOAD");
				stopService(service_intent);
			}
		};

		registerReceiver(broadcastReceiver, new IntentFilter(MainActivity.BROADCAST_ACTION_STOP_SERVICE));
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		unregisterReceiver(broadcastReceiver);
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
			View rootView = inflater.inflate(R.layout.fragment_login,container, false);

			settings = (Settings) getActivity().getApplicationContext();

			ll_loading = (LinearLayout)rootView.findViewById(R.id.ll_loading);
			btnLogin = (Button)rootView.findViewById(R.id.btnLogin);
			etID = (EditText)rootView.findViewById(R.id.etID);
			etPW = (EditText)rootView.findViewById(R.id.etPW);

			btnLogin.setOnClickListener((OnClickListener)this);

			return rootView;
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId()){
			case R.id.btnLogin :
				str_id = etID.getText().toString();
				str_pw = etPW.getText().toString();
				if(!str_id.equals("")&&!str_pw.equals("")){
					ll_loading.setVisibility(View.VISIBLE);
					Log.i("Click","!!");
					new LoginTask().execute((Void)null);
				}
				break;
			}
		}

		class LoginTask extends AsyncTask<Void, Void, Boolean> {
			protected Boolean doInBackground(Void... Void) {
				try{
					ja = Functions.GET("http://eaeao.herokuapp.com/login/?sid="+str_id+"&pw="+str_pw);
					settings.shop_id = ja.getJSONObject(0).getString("sid");
					settings.shop_name = ja.getJSONObject(0).getString("sname");
					settings.shop_url = ja.getJSONObject(0).getString("url");
					return true;
				}catch(Exception e){
					return false;
				}
			}
			protected void onPostExecute(Boolean result) {
				ll_loading.setVisibility(View.GONE);
				if(result){
					settings.setPref("shop_id", settings.shop_id);
					settings.setPref("shop_name", settings.shop_name);
					settings.setPref("shop_url", settings.shop_url);
					startActivity(new Intent(getActivity(), MainActivity.class));
					getActivity().finish();
				}else{
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
