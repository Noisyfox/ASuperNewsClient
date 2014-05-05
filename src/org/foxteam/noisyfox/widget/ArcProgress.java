package org.foxteam.noisyfox.widget;

import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class ArcProgress extends View {
	private final RectF mRectShadow = new RectF();
	private final RectF mRectArc = new RectF();
	private final Paint mPaintShadow = new Paint();
	private final Paint mPaintBackground = new Paint();
	private final Paint mPaintArc = new Paint();
	private int mProgress = 0;
	private float mStockWidth = 6.0F;

	public ArcProgress(Context context) {
		super(context);
		init();
	}

	public ArcProgress(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public ArcProgress(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	private void init() {
		mPaintShadow.setColor(0x80000000);
		mPaintShadow.setAntiAlias(true);
		mPaintShadow.setStyle(Paint.Style.STROKE);
		mPaintShadow.setStrokeCap(Paint.Cap.ROUND);
		mPaintShadow.setStrokeWidth(mStockWidth);
		mPaintShadow.setMaskFilter(new BlurMaskFilter(1.0F,
				BlurMaskFilter.Blur.NORMAL));

		mPaintBackground.setColor(0x19000000);
		mPaintBackground.setAntiAlias(true);
		mPaintBackground.setStyle(Paint.Style.STROKE);
		mPaintBackground.setStrokeWidth(mStockWidth);
		mPaintBackground.setMaskFilter(new BlurMaskFilter(1.0F,
				BlurMaskFilter.Blur.NORMAL));

		mPaintArc.setColor(Color.WHITE);
		mPaintArc.setAntiAlias(true);
		mPaintArc.setStyle(Paint.Style.STROKE);
		mPaintArc.setStrokeCap(Paint.Cap.ROUND);
		mPaintArc.setStrokeWidth(mStockWidth);
		mPaintArc.setMaskFilter(new BlurMaskFilter(1.0F,
				BlurMaskFilter.Blur.NORMAL));
	}

	public int getProgress() {
		return mProgress;
	}

	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		canvas.drawArc(mRectArc, 0.0F, 360.0F, false, mPaintBackground);
		canvas.drawArc(mRectShadow, 90.0F, 360.0F * (mProgress / 100.0F),
				false, mPaintShadow);
		canvas.drawArc(mRectArc, 90.0F, 360.0F * (mProgress / 100.0F), false,
				mPaintArc);
	}

	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);

		int i = (int) (mStockWidth / 2.0F);
		mRectArc.set(i, i, w - i - 1.0F, h - i - 1.0F);
		mRectShadow.set(1.0F + i, 1.0F + i, w - i, h - i);
	}

	public void setProgress(int progress) {
		mProgress = progress;

		postInvalidate();
	}

	public void setWidth(float width) {
		mStockWidth = width;
		mPaintShadow.setStrokeWidth(mStockWidth);
		mPaintBackground.setStrokeWidth(mStockWidth);
		mPaintArc.setStrokeWidth(mStockWidth);

		postInvalidate();
	}
}
