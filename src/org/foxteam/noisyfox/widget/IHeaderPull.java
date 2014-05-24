package org.foxteam.noisyfox.widget;

public interface IHeaderPull {
	public abstract void pull(int height);
	
	public void setOnPullListener(OnPullListener l);
	public OnPullListener getOnPullListener();
	
	public int getHeaderMaxHeight();
}
