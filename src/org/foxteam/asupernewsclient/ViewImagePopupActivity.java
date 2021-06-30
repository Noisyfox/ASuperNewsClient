package org.foxteam.asupernewsclient;

import org.foxteam.noisyfox.widget.TouchImageView;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.app.Activity;
import android.graphics.Bitmap;

public class ViewImagePopupActivity extends Activity implements OnClickListener {
	private String image_cid = null;

	private TouchImageView imageView_image;
	private View view_loading;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_image_popup);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		finish();
	}

}
