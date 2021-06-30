package org.foxteam.asupernewsclient;

import java.util.ArrayList;
import java.util.Date;

import org.foxteam.noisyfox.widget.ArcProgress;
import org.foxteam.noisyfox.widget.HeaderPagerContainer;
import org.foxteam.noisyfox.widget.OnPullListener;
import org.foxteam.noisyfox.widget.PullHeaderListView;
import org.foxteam.noisyfox.widget.StickyBaseAdapter;

import com.viewpagerindicator.CirclePageIndicator;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.app.Activity;
import android.content.Intent;

public class MainActivity extends Activity implements OnItemClickListener,
		OnClickListener {

	private PullHeaderListView mMainList;
	private ArcProgress progressView;
	private View mHeaderView;
	private MyAdapter mAdapter = new MyAdapter();
	private MyPagerAdapter mPagerAdapter = new MyPagerAdapter();
	private HeaderPagerContainer mHeaderViewPagerContainer;
	private CirclePageIndicator mCirclePageIndicator;

	private ArrayList<NewsData> mCurrentData = new ArrayList<NewsData>();
	private ArrayList<HeaderNews> mCurrentHeaderData = new ArrayList<HeaderNews>();
	private long mCurrentDayTime = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mMainList = (PullHeaderListView) findViewById(R.id.pullHeaderListView);
		mHeaderView = LayoutInflater.from(this).inflate(
				R.layout.header_mainlist, null);
		progressView = (ArcProgress) mHeaderView
				.findViewById(R.id.news_progress);
		mHeaderViewPagerContainer = (HeaderPagerContainer) mHeaderView
				.findViewById(R.id.header_pager_container);
		mCirclePageIndicator = (CirclePageIndicator) mHeaderView
				.findViewById(R.id.indicator);

		mMainList.setOnPullListener(new OnPullListener() {
			@Override
			public void onPull(int progress) {
				if (progress > 100) {
					progress = 100;
				}
				progressView.setProgress(progress);
			}

			@Override
			public void onRelease() {
				progressView.setProgress(0);
			}
		});

		mCurrentDayTime = Util.getDateStamp(System.currentTimeMillis());

		for (int i = 1; i <= 20; i++) {
			NewsData nd = new NewsData();

			nd.title = "新闻 " + i;
			nd.time = System.currentTimeMillis();
			nd.time_day = Util.getDateStamp(nd.time);
			nd.time_display = NewsData.dateformat.format(new Date(nd.time));

			mCurrentData.add(nd);
		}

		mMainList.addHeaderView(mHeaderView);
		mMainList.setAdapter(mAdapter);
		mMainList.setOnItemClickListener(this);

		for (int i = 1; i <= 5; i++) {
			HeaderNews hn = new HeaderNews();

			hn.title = "新闻 " + i;
			hn.time = System.currentTimeMillis();
			hn.time_day = Util.getDateStamp(hn.time);
			hn.time_display = NewsData.dateformat.format(new Date(hn.time));

			mCurrentHeaderData.add(hn);
		}

		mHeaderViewPagerContainer.setAdapter(mPagerAdapter);
		mCirclePageIndicator.setViewPager(mHeaderViewPagerContainer
				.getViewPager());
		mCirclePageIndicator.setOnPageChangeListener(mHeaderViewPagerContainer);
	}

	private class HeaderNews extends NewsData {
		private View mView = null;
	}

	private class MyPagerAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return mCurrentHeaderData.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			HeaderNews hn = mCurrentHeaderData.get(position);

			ViewHolder holder;
			if (hn.mView == null) {
				hn.mView = LayoutInflater.from(MainActivity.this).inflate(
						R.layout.header_mainlist_pager, container, false);
				holder = new ViewHolder();
				hn.mView.setTag(holder);
				holder.image = (ImageView) hn.mView
						.findViewById(R.id.header_image);
				holder.text = (TextView) hn.mView
						.findViewById(R.id.header_title);

				holder.image.setImageResource(R.drawable.ic_launcher);
				holder.text.setText(hn.title);
				hn.mView.setOnClickListener(MainActivity.this);
			} else {
				holder = (ViewHolder) hn.mView.getTag();
			}

			container.addView(hn.mView);

			return hn.mView;
		}

		class ViewHolder {
			TextView text;
			ImageView image;
		}
	}

	private class MyAdapter extends StickyBaseAdapter {

		@Override
		public View getHeaderView(int position, View convertView,
				ViewGroup parent) {
			HeaderViewHolder holder;
			if (convertView == null) {
				holder = new HeaderViewHolder();
				convertView = LayoutInflater.from(MainActivity.this).inflate(
						R.layout.mainlist_header, parent, false);
				convertView.setTag(holder);

				holder.text_type = (TextView) convertView
						.findViewById(R.id.left_label);
				holder.text_time = (TextView) convertView
						.findViewById(R.id.right_label);
			} else {
				holder = (HeaderViewHolder) convertView.getTag();
			}
			NewsData nd = mCurrentData.get(position);

			if (nd.time_day == mCurrentDayTime) {
				holder.text_type.setVisibility(View.VISIBLE);
				holder.text_type.setText(R.string.header_today);
			} else {
				holder.text_type.setVisibility(View.GONE);
			}

			holder.text_time.setText(nd.time_display);

			return convertView;
		}

		@Override
		public long getHeaderId(int position) {
			return mCurrentData.get(position).time_day;
		}

		@Override
		public int getCount() {
			return mCurrentData.size();
		}

		@Override
		public Object getItem(int position) {
			return mCurrentData.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = LayoutInflater.from(MainActivity.this).inflate(
						R.layout.mainlist_item, parent, false);
				convertView.setTag(holder);

				holder.image = (ImageView) convertView
						.findViewById(R.id.imageView_news);
				holder.text = (TextView) convertView
						.findViewById(R.id.textView_title);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			NewsData nd = mCurrentData.get(position);

			holder.image.setImageResource(R.drawable.ic_launcher);
			holder.text.setText(nd.title);
			return convertView;
		}

		class HeaderViewHolder {
			TextView text_type;
			TextView text_time;
		}

		class ViewHolder {
			TextView text;
			ImageView image;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent i = new Intent();
		i.setClass(this, NewsActivity.class);
		startActivity(i);
		overridePendingTransition(R.anim.slide_in_right, R.anim.stay);
	}

	@Override
	public void onClick(View v) {
		Intent i = new Intent();
		i.setClass(this, NewsActivity.class);
		startActivity(i);
		overridePendingTransition(R.anim.slide_in_right, R.anim.stay);
	}
}
