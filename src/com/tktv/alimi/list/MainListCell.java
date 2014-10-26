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

public class MainListCell extends View {
	private AQuery aq;
	
	private View feed;
	private Context con;
	
	private LinearLayout ll_list;
	private TextView tv_no;
	private TextView tv_title;
	private TextView tv_cdate;
	private TextView tv_date;
	
	Settings settings;

	public MainListCell(Context context, String[] str, int i) {
		super(context);
		this.con = context;
		aq = new AQuery(con);
		// TODO Auto-generated method stub
		settings = (Settings) context.getApplicationContext();
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		feed = inflater.inflate(R.layout.cell_main_list, null);
		
		ll_list = (LinearLayout)feed.findViewById(R.id.ll_list);
		tv_no = (TextView)feed.findViewById(R.id.tv_no);
		tv_title = (TextView)feed.findViewById(R.id.tv_title);
		tv_cdate = (TextView)feed.findViewById(R.id.tv_cdate);
		tv_date = (TextView)feed.findViewById(R.id.tv_date);
		
		tv_no.setText(str[0]);
		tv_title.setText(str[1]);
		tv_cdate.setText("예약일자 : "+str[2]);
		tv_date.setText(Functions.getDateFormat(str[3]));
		
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