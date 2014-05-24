package org.foxteam.noisyfox.widget;

import org.foxteam.asupernewsclient.R;

import android.content.Context;
import android.graphics.Point;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

public class HeaderPagerContainer extends RelativeLayout implements
		OnPageChangeListener {

	private boolean mStateChanged = false;
	private ViewPager mViewPager;
	private Point mPointPagerCenter = new Point();
	private Point mPointTouch = new Point();

	public HeaderPagerContainer(Context context) {
		super(context);
	}

	public HeaderPagerContainer(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public HeaderPagerContainer(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	public ViewPager getViewPager() {
		return mViewPager;
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		try {
			mViewPager = (ViewPager) findViewById(R.id.header_pager);
			if (mViewPager == null) {
				mViewPager = (ViewPager) getChildAt(0);
			}
		} catch (Exception localException) {
			throw new IllegalStateException(
					"The first child of PagerContainer must be a ViewPager");
		}
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		mStateChanged = arg0 != 0;
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		if (mStateChanged) {
			invalidate();
		}
	}

	@Override
	public void onPageSelected(int arg0) {
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		mPointPagerCenter.x = w / 2;
		mPointPagerCenter.y = h / 2;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mPointTouch.x = (int) event.getX();
			mPointTouch.y = (int) event.getY();
		}
		event.offsetLocation(mPointPagerCenter.x - mPointTouch.x,
				mPointPagerCenter.y - mPointTouch.y);

		return mViewPager.dispatchTouchEvent(event);
	}
}
