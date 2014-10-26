package com.tktv.alimi;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MainSettingFragment extends Fragment {

	View layout;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		layout = inflater.inflate(R.layout.fragment_main_setting, container, false);

		
		return layout;
	}


	@Override
	public void onDestroy(){
		super.onDestroy();
	}
}