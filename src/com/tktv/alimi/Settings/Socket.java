package com.tktv.alimi.Settings;

import java.net.MalformedURLException;

import io.socket.IOAcknowledge;
import io.socket.IOCallback;
import io.socket.SocketIO;
import io.socket.SocketIOException;

import org.json.JSONException;
import org.json.JSONObject;

import com.tktv.alimi.LoginActivity;
import com.tktv.alimi.MainActivity;
import com.tktv.alimi.R;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class Socket extends Service {

	private SocketIO socket = null;
	public static final String TAG = "com.tktv.alimi.Settings.Socket"; 

	private String shop_id="";

	Settings settings;
	BroadcastReceiver broadcastReceiver;

	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();

		settings = (Settings) getApplicationContext();

		startSocket();
		setBroadcast_onConnect();
	}

	private void startSocket(){
		IOCallback ioCallBack = new IOCallback() {
			@Override
			public void onDisconnect() {
				// TODO Auto-generated method stub
				System.out.println("Connection terminated.");
				socket.emit("disconnect", "");
			}

			@Override
			public void onConnect() {
				// TODO Auto-generated method stub
				System.out.println("Connection established");
				setAdmin(shop_id);
			}

			@Override
			public void onMessage(String data, IOAcknowledge ack) {
				// TODO Auto-generated method stub
				System.out.println("Server said: " + data);
			}

			@Override
			public void onMessage(JSONObject json, IOAcknowledge ack) {
				// TODO Auto-generated method stub
				try {
					System.out.println("Server said:" + json.toString(2));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void on(String event, IOAcknowledge ack, Object... args) {
				// TODO Auto-generated method stub
				System.out.println("Server triggered event '" + event + "'");
				try {

					JSONObject jo = new JSONObject(args[0].toString());

					if (event.equals("sendMsg")){
						String name = jo.getString("name");
						Log.i("No",""+jo.getString("no"));
						Log.i("Name",""+name);
						setNotification(name+"님이 새로운 정보를 등록했습니다.");
					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			@Override
			public void onError(SocketIOException socketIOException) {
				// TODO Auto-generated method stub
				System.out.println("an Error occured");

				Log.d(TAG, "error: " + socketIOException.getMessage());
				socketIOException.printStackTrace();
				if (socketIOException.getMessage().endsWith("+0")) {
					socket.disconnect();
					try {
						socket = new SocketIO(settings.URL, this);
					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		};

		try {
			if (socket == null) {
				socket = new SocketIO(settings.URL, ioCallBack);
			}else if(!socket.isConnected()){
				socket = new SocketIO(settings.URL, ioCallBack);
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setBroadcast_onConnect(){
		broadcastReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				Log.i("setBroadcast_onConnect","LOAD");

				Bundle extra = intent.getExtras();
				shop_id = extra.getString("shop_id","");
				if(socket.isConnected()){
					setAdmin(extra.getString("shop_id",""));
				}
			}
		};

		registerReceiver(broadcastReceiver, new IntentFilter(MainActivity.BROADCAST_ACTION_ON_CONNECT));
	}

	private void setAdmin(String sid){
		if(!sid.equals("")){
			JSONObject jo_con = new JSONObject();
			try {
				jo_con.put("sid", sid);
				socket.emit("setAdmin", jo_con);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@SuppressWarnings("deprecation")
	private void setNotification(String msg){
		NotificationManager notiMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		Notification noti = new Notification(R.drawable.ic_launcher,"신규 알림입니다.",System.currentTimeMillis());
		Intent intent = new Intent(this, LoginActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra("msg", msg);
		PendingIntent pending = PendingIntent.getActivity(this, 0, intent, 0);
		noti.icon = R.drawable.ic_launcher;
		noti.setLatestEventInfo(this, "신규 알림입니다.", msg, pending);
		noti.defaults |= Notification.DEFAULT_ALL;
		noti.flags |= Notification.FLAG_AUTO_CANCEL;

		notiMgr.notify(0, noti);
	}

	static String replaceAll(String strOriginal, String from, String to){
		int startPos;
		String strReplacedString = strOriginal;
		while(strReplacedString.indexOf(from) != -1)
		{
			startPos = strReplacedString.indexOf(from);
			strReplacedString = strReplacedString.substring(0, startPos) + to + strReplacedString.substring(startPos + from.length());
		}
		return strReplacedString;
	}

	@Override
	public void onDestroy(){
		super.onDestroy();
		socket.disconnect();
		unregisterReceiver(broadcastReceiver);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}