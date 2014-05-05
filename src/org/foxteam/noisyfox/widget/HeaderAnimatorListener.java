package org.foxteam.noisyfox.widget;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.view.ViewGroup;

public class HeaderAnimatorListener implements AnimatorListener {

	private PullHeaderScrollView mPullHeaderScrollView;
	private ViewGroup.LayoutParams mLayoutParams;
	private boolean mRefresh;

	public HeaderAnimatorListener(PullHeaderScrollView pullHeaderScrollView,
			ViewGroup.LayoutParams layoutParams) {
		mPullHeaderScrollView = pullHeaderScrollView;
		mLayoutParams = layoutParams;
	}

	@Override
	public void onAnimationStart(Animator animation) {
		mRefresh = mLayoutParams.height >= mPullHeaderScrollView.getMaxHeight();
	}

	@Override
	public void onAnimationEnd(Animator animation) {
		if (mRefresh) {
			// TODO Get and call listener
		}
	}

	@Override
	public void onAnimationCancel(Animator animation) {
	}

	@Override
	public void onAnimationRepeat(Animator animation) {
	}

}
