package org.foxteam.noisyfox.widget;

import com.actionbarsherlock.internal.nineoldandroids.animation.ValueAnimator;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AbsListView;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public class PullHeaderListView extends StickyListHeadersListView implements
		IHeaderPull, OnTouchListener {

	private View mHeaderView;
	private int mHeaderHeightMin, mHeaderHeightMax;
	private float mHeadY;
	private OnPullListener mOnPullListener;
	private OnTouchListener mOnTouchListener;

	public PullHeaderListView(Context context) {
		super(context);
		super.setOnTouchListener(this);
		mHeaderHeightMin = Util.dip2px(context, 200);
		mHeaderHeightMax = Util.dip2px(context, 300);
	}

	public PullHeaderListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		super.setOnTouchListener(this);
		mHeaderHeightMin = Util.dip2px(context, 200);
		mHeaderHeightMax = Util.dip2px(context, 300);
	}

	public PullHeaderListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		super.setOnTouchListener(this);
		mHeaderHeightMin = Util.dip2px(context, 200);
		mHeaderHeightMax = Util.dip2px(context, 300);
	}

	public final void setHeaderHeight(int minHeight, int maxHeight) {
		mHeaderHeightMin = minHeight;
		mHeaderHeightMax = maxHeight;
	}

	@Override
	public void addHeaderView(View v) {
		mHeaderView = v;
		AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(
				getLayoutParams().width, mHeaderHeightMin);
		mHeaderView.setLayoutParams(layoutParams);
		super.addHeaderView(v);
	}

	@Override
	public void addHeaderView(View v, Object data, boolean isSelectable) {
		mHeaderView = v;
		AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(
				getLayoutParams().width, mHeaderHeightMin);
		mHeaderView.setLayoutParams(layoutParams);
		super.addHeaderView(v, data, isSelectable);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if (mHeaderView.getTop() == 0) {
				mHeadY = ev.getY();
			}
		}
		return super.onInterceptTouchEvent(ev);
	}

	@Override
	public void setOnTouchListener(final OnTouchListener l) {
		mOnTouchListener = l;
	}

	@Override
	public void pull(int height) {
		if (mOnPullListener != null) {
			float p = 100.0F * ((float) (height - mHeaderHeightMin) / (float) (mHeaderHeightMax - mHeaderHeightMin));
			mOnPullListener.onPull((int) (p / 0.7F));
		}
	}

	@Override
	public void setOnPullListener(OnPullListener l) {
		mOnPullListener = l;
	}

	@Override
	public OnPullListener getOnPullListener() {
		return mOnPullListener;
	}

	@Override
	public int getHeaderMaxHeight() {
		return mHeaderHeightMax;
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (mOnTouchListener != null && mOnTouchListener.onTouch(this, ev)) {
			return true;
		}

		return super.onTouchEvent(ev);
	}

	@Override
	public final boolean onTouch(View v, MotionEvent ev) {
		if (mOnTouchListener != null && mOnTouchListener.onTouch(this, ev)) {
			return true;
		}

		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mHeadY = ev.getY();
			break;
		case MotionEvent.ACTION_MOVE: {
			float y = ev.getY();
			int distance = (int) (y - mHeadY);
			ViewGroup.LayoutParams layoutParams = mHeaderView.getLayoutParams();
			int i;
			if (distance > 0) {
				i = Math.min(mHeaderHeightMax - layoutParams.height, distance);
			} else {
				i = Math.max(mHeaderHeightMin - layoutParams.height, distance);
			}

			if ((mHeaderView.getTop() != 0)
					|| (layoutParams.height < mHeaderHeightMin)
					|| (layoutParams.height > mHeaderHeightMax) || (i == 0)) {
				mHeadY = y;
				break;
			}
			int j = layoutParams.height
					+ (int) (i * (0.1f + (float) layoutParams.height
							/ (float) mHeaderHeightMax));
			if (j >= mHeaderHeightMin) {
				if (j > mHeaderHeightMax) {
					layoutParams.height = mHeaderHeightMax;
				} else {
					layoutParams.height = j;
				}
			} else {
				layoutParams.height = mHeaderHeightMin;
			}
			mHeaderView.requestLayout();
			mHeadY = y;

			pull(layoutParams.height);

			return true;
		}
		case MotionEvent.ACTION_UP:
		default: {
			ViewGroup.LayoutParams layoutParams = mHeaderView.getLayoutParams();
			if (layoutParams.height <= mHeaderHeightMin) {
				break;
			}
			HeaderEvaluator headerEvaluator = new HeaderEvaluator(mHeaderView,
					this);
			Object[] values = new Object[2];
			values[0] = Integer.valueOf(mHeaderView.getHeight());
			values[1] = Integer.valueOf(mHeaderHeightMin);
			ValueAnimator valueAnimator = ValueAnimator.ofObject(
					headerEvaluator, values);
			valueAnimator
					.setInterpolator(new AccelerateDecelerateInterpolator());
			valueAnimator.setDuration(200).addListener(
					new HeaderAnimatorListener(this, layoutParams));
			valueAnimator.start();
			return true;
		}
		}

		return false;
	}
}
