package com.tktv.alimi.list;

import com.tktv.alimi.R;
import com.tktv.alimi.Settings.Settings;
import com.tktv.alimi.Settings.xmlParser;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

public class MainListFragment extends Fragment {

	View layout;
	private LinearLayout ll_list;
	ProgressBar pb;

	String[] str_no=null;
	String[] str_name=null;
	String[] str_rdate=null;
	String[] str_date=null;
	
	private String[] nodeTitle=null;
	private String[] nodeName=null;

	Settings settings;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		layout = inflater.inflate(R.layout.fragment_main_list, container, false);
		settings = (Settings) getActivity().getApplicationContext();

		ll_list = (LinearLayout)layout.findViewById(R.id.ll_list);
		pb = new ProgressBar(getActivity());

		return layout;
	}

	@Override
	public void onResume(){
		super.onResume();
		ll_list.removeAllViews();
		ll_list.addView(pb);
		new ListTask().execute((Void)null);
	}

	class ListTask extends AsyncTask<Void, Void, Boolean> {
		protected Boolean doInBackground(Void... Void) {
			nodeTitle = xmlParser.XMLParse(settings.shop_url,"item", "title");
			nodeName = xmlParser.XMLParse(settings.shop_url,"item", "name");
			
			for(int i=0;i<nodeTitle.length;i++){
				if(nodeName[i].equals("번호")) str_no = xmlParser.XMLParse(settings.shop_url,"rsv", nodeTitle[i]);
				else if(nodeName[i].equals("이름")) str_name = xmlParser.XMLParse(settings.shop_url,"rsv", nodeTitle[i]);
				else if(nodeName[i].equals("예약일시")) str_rdate = xmlParser.XMLParse(settings.shop_url,"rsv", nodeTitle[i]);
				else if(str_date == null) str_date = xmlParser.XMLParse(settings.shop_url,"rsv", "RSubDate");
			}
			return true;
		}
		protected void onPostExecute(Boolean result) {
			if(result){
				for(int i=0;i<str_name.length;i++){
					String[] parse = {str_no[i], str_name[i], str_rdate[i], str_date[i]};
					MainListCell mlc = new MainListCell(getActivity(),parse,i);
					View v = mlc.getView();
					v.setTag(i);
					v.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							Vibrator Vibe = (Vibrator)getActivity().getSystemService(Context.VIBRATOR_SERVICE);
							Vibe.vibrate(20);

							Intent gin = new Intent(getActivity(),DetailActivity.class);
							gin.putExtra("id", v.getTag().toString());
							startActivity(gin);
						}
					});
					ll_list.addView(v);
				}
				ll_list.removeView(pb);
			}
			return;
		}
	}


	@Override
	public void onDestroy(){
		super.onDestroy();
	}
}