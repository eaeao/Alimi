package com.tktv.alimi.list;

import com.androidquery.AQuery;
import com.tktv.alimi.R;
import com.tktv.alimi.Settings.Functions;
import com.tktv.alimi.Settings.Settings;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DetailListCell extends View {
	private AQuery aq;
	
	private View feed;
	private Context con;
	
	private LinearLayout ll_list;
	private TextView tv_title;
	private TextView tv_con;
	
	Settings settings;

	public DetailListCell(Context context, String[] str, int i) {
		super(context);
		this.con = context;
		aq = new AQuery(con);
		// TODO Auto-generated method stub
		settings = (Settings) context.getApplicationContext();
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		feed = inflater.inflate(R.layout.cell_detail_list, null);
		
		ll_list = (LinearLayout)feed.findViewById(R.id.ll_list);
		tv_title = (TextView)feed.findViewById(R.id.tv_title);
		tv_con = (TextView)feed.findViewById(R.id.tv_con);
		
		tv_title.setText(str[0]);
		tv_con.setText(str[1]);
		
		if(i%2 == 0){
			ll_list.setBackgroundColor(Color.parseColor("#f7f7f7"));
		}else{
			ll_list.setBackgroundColor(Color.parseColor("#ffffff"));
		}

	}
	
	public View getView(){
		return feed;
	}
}