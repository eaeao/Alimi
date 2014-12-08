package com.tktv.alimi;

import java.util.Locale;

import com.tktv.alimi.Settings.Settings;
import com.tktv.alimi.board.MainBoardFragment;
import com.tktv.alimi.list.MainListFragment;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.v13.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends Activity implements ActionBar.TabListener {

	SectionsPagerAdapter mSectionsPagerAdapter;
	ViewPager mViewPager;
	
	Settings settings;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		settings = (Settings) getApplicationContext();

		setTitle(settings.shop_name);
		
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		
		
		mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				actionBar.setSelectedNavigationItem(position);
			}
		});
		
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			actionBar.addTab(actionBar.newTab().setText(mSectionsPagerAdapter.getPageTitle(i)).setTabListener(this));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuItem item1 = menu.add(0, 0, 0, "·Î±×¾Æ¿ô");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item != null){
			Toast.makeText(this,""+item.getTitle(), Toast.LENGTH_SHORT).show();
			settings.clearPref();
			startActivity(new Intent(this, LoginActivity.class));
			finish();
			return true;
		} else {
			return false;
		}
	}	

	@Override
	public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
	}

	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			if(settings.shop_url2.equals("")){
				switch(position){
				case 0:
					return new MainListFragment();
				}
			}
			else{
				switch(position){
				case 0:
					return new MainListFragment();
				case 1:
					return new MainBoardFragment();
				}
			}
			return null;
		}

		@Override
		public int getCount() {
			if(settings.shop_url2.equals("")) return 1;
			else return 2;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			if(settings.shop_url2.equals("")){
				switch (position) {
				case 0:
					return getString(R.string.title_section1).toUpperCase(l);
				}
			}
			else{
				switch (position) {
				case 0:
					return getString(R.string.title_section1).toUpperCase(l);
				case 1:
					return getString(R.string.title_section2).toUpperCase(l);
				}
			}
			return null;
		}
	}

}
