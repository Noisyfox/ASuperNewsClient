package org.foxteam.noisyfox.widget;

import com.actionbarsherlock.internal.nineoldandroids.animation.IntEvaluator;

import android.view.View;
import android.view.ViewGroup;

public class HeaderEvaluator extends IntEvaluator {
	private final View mHeaderView;
	private final IHeaderPull mHeaderPull;

	public HeaderEvaluator(View headerView, IHeaderPull headerPull) {
		mHeaderView = headerView;
		mHeaderPull = headerPull;
	}

	@Override
	public final Integer evaluate(float fraction, Integer startValue,
			Integer endValue) {
		int height = super.evaluate(fraction, startValue, endValue);
		ViewGroup.LayoutParams localLayoutParams = mHeaderView
				.getLayoutParams();
		localLayoutParams.height = height;
		mHeaderView.setLayoutParams(localLayoutParams);
		mHeaderPull.pull(height);
		return height;
	}
}
