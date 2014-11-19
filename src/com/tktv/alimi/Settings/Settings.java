package com.tktv.alimi.Settings;

import android.app.Application;
import android.content.SharedPreferences;

public class Settings extends Application{
	
	public String shop_id = "";
	public String shop_name = "";
	public String shop_url = "";
	public String shop_url2 = "";
	public String URL = "http://eaeao.herokuapp.com/";
	
	private SharedPreferences prefs_system;
	private SharedPreferences.Editor editor_system;
	
	private void setPrefSystem(){
		prefs_system = getSharedPreferences("com.tktv.alimi", MODE_PRIVATE);
		editor_system = prefs_system.edit();
	}
	
	public void setPref(String key, String value){
		setPrefSystem();
		editor_system.putString(key, value);
		editor_system.commit();
	}
	
	public String getPref(String key){
		setPrefSystem();
		return prefs_system.getString(key, "");
	}
	
	public void clearPref(){
		editor_system.clear();
		editor_system.commit();
	}
}