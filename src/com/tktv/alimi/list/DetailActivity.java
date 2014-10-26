package com.tktv.alimi.list;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.tktv.alimi.R;
import com.tktv.alimi.R.id;
import com.tktv.alimi.R.layout;
import com.tktv.alimi.R.menu;
import com.tktv.alimi.Settings.Settings;
import com.tktv.alimi.Settings.xmlParser;
import com.tktv.alimi.list.MainListFragment.ListTask;

import android.app.Activity;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.os.Build;

public class DetailActivity extends Activity implements OnClickListener {

	private LinearLayout ll_list;

	private String[] strTitle;
	private String[] strName;
	private String[] strContent;
	private String strCall="";
	private String strNo="";
	private int list_no=0;
	ProgressBar pb;

	Settings settings;
	private Bundle extra;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail);
		settings = (Settings) getApplicationContext();
		extra = getIntent().getExtras();

		list_no = Integer.parseInt(extra.getString("id"));

		ll_list = (LinearLayout)findViewById(R.id.ll_list);
		pb = new ProgressBar(this);
		ll_list.addView(pb);

		new DetailTask().execute((Void)null);

	}

	class DetailTask extends AsyncTask<Void, Void, Boolean> {
		protected Boolean doInBackground(Void... Void) {
			strTitle = xmlParser.XMLParse(settings.shop_url,"item", "title");
			strName = xmlParser.XMLParse(settings.shop_url,"item", "name");
			strContent = new String[strTitle.length];
			for(int i=0; i<strTitle.length;i++){
				strContent[i] = xmlParser.XMLParse(settings.shop_url,"rsv", strTitle[i])[list_no];
			}
			return true;
		}
		protected void onPostExecute(Boolean result) {
			if(result){
				for(int i=0; i<strTitle.length;i++){
					if(strName[i].equals("이름")) setTitle(strContent[i]);
					if(strName[i].equals("연락처")) strCall=strContent[i];
					if(strName[i].equals("번호")) strNo=strContent[i];
					String[] parse = {strName[i], strContent[i]};
					DetailListCell dlc = new DetailListCell(DetailActivity.this, parse, i);
					View v = dlc.getView();
					ll_list.addView(v);
				}
				ll_list.removeView(pb);
			}
			return;
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Vibrator Vibe = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
		Vibe.vibrate(20);

		switch(v.getId()){
		case android.R.id.home:
			this.finish();
			break;
		}
		return;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.detail, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_call) {
			new AlertDialog.Builder(this).setTitle(strCall+"에 전화하기")
			.setIcon(android.R.drawable.ic_menu_call)
			.setMessage("정말로 "+strCall+"에 전화하시겠습니까? ")
			.setNegativeButton("아니오", null)
			.setPositiveButton("예", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					String telNumber = "tel:" + strCall;
					Intent intent = new Intent (Intent.ACTION_CALL, Uri.parse(telNumber));
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
					startActivity(intent);
				}
			}).show();
			return true;
		}else if (id == R.id.action_sms) {
			new AlertDialog.Builder(this).setTitle(strCall+"에 문자하기")
			.setIcon(android.R.drawable.ic_menu_send)
			.setMessage("정말로 "+strCall+"에 문자하시겠습니까? ")
			.setNegativeButton("아니오", null)
			.setPositiveButton("예", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					String smsNumber = "smsto:" + strCall;
					Intent intent = new Intent (Intent.ACTION_SENDTO, Uri.parse(smsNumber));
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
					startActivity(intent);
				}
			}).show();
			return true;
		}else if (id == R.id.action_delete) {
			new AlertDialog.Builder(this).setTitle(strNo+"번 삭제")
			.setIcon(android.R.drawable.ic_menu_delete)
			.setMessage("정말로 "+strNo+"번 내역을 삭제하시겠습니까?")
			.setNegativeButton("아니오", null)
			.setPositiveButton("예", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
					URL url;
					try {
						url = new URL(settings.shop_url+"&delete="+strNo);
						HttpURLConnection conn = (HttpURLConnection)url.openConnection();
						if(conn != null){
							conn.setConnectTimeout(20000);
							conn.setUseCaches(false);
							if(conn.getResponseCode()==HttpURLConnection.HTTP_OK){
								conn.disconnect();
							}
						}
					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					ll_list.removeAllViews();
					DetailActivity.this.finish();
				}
			}).show();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

}
