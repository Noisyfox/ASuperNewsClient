package org.foxteam.noisyfox.widget;

import org.foxteam.asupernewsclient.R;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.ScrollView;

public class PullHeaderScrollView extends ScrollView implements IHeaderPull {

	private View mHeaderView;
	private int mHeaderHeightMin = 500, mHeaderHeightMax = 1000;
	private float mHeadY;

	public PullHeaderScrollView(Context context) {
		super(context);
	}

	public PullHeaderScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public PullHeaderScrollView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	public final void setHeaderHeight(int minHeight, int maxHeight) {
		mHeaderHeightMin = minHeight;
		mHeaderHeightMax = maxHeight;

		LinearLayout.LayoutParams localLayoutParams = new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT, mHeaderHeightMin);
		mHeaderView.setLayoutParams(localLayoutParams);
	}

	public int getMaxHeight() {
		return mHeaderHeightMax;
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		mHeaderView = findViewById(R.id.header_layout);
		if (mHeaderView == null) {
			mHeaderView = ((ViewGroup) getChildAt(0)).getChildAt(0);
		}
		LinearLayout.LayoutParams localLayoutParams = new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT, mHeaderHeightMin);
		mHeaderView.setLayoutParams(localLayoutParams);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			mHeadY = ev.getY();
		}
		return super.onInterceptTouchEvent(ev);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {

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

			if ((getScrollY() != 0) || (layoutParams.height < mHeaderHeightMin)
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

			// TODO Listener

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

		return super.onTouchEvent(ev);
	}

	@Override
	public void pull(int progress) {
		// TODO Listener
	}

}
