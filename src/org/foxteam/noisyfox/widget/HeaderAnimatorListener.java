package org.foxteam.noisyfox.widget;

import com.actionbarsherlock.internal.nineoldandroids.animation.Animator;
import com.actionbarsherlock.internal.nineoldandroids.animation.Animator.AnimatorListener;

import android.view.ViewGroup;

public class HeaderAnimatorListener implements AnimatorListener {

	private IHeaderPull mPullHeaderView;
	private ViewGroup.LayoutParams mLayoutParams;
	private boolean mRefresh;

	public HeaderAnimatorListener(IHeaderPull pullHeaderView,
			ViewGroup.LayoutParams layoutParams) {
		mPullHeaderView = pullHeaderView;
		mLayoutParams = layoutParams;
	}

	@Override
	public void onAnimationStart(Animator animation) {
		mRefresh = mLayoutParams.height >= mPullHeaderView.getHeaderMaxHeight();
	}

	@Override
	public void onAnimationEnd(Animator animation) {
		if (mRefresh) {
			OnPullListener l = mPullHeaderView.getOnPullListener();
			if (l != null) {
				l.onRelease();
			}
		}
	}

	@Override
	public void onAnimationCancel(Animator animation) {
	}

	@Override
	public void onAnimationRepeat(Animator animation) {
	}

}
