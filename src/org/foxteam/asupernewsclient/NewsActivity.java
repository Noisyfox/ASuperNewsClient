package org.foxteam.asupernewsclient;

import java.io.IOException;
import java.io.InputStream;

import org.foxteam.noisyfox.widget.ArcProgress;
import org.foxteam.noisyfox.widget.OnPullListener;
import org.foxteam.noisyfox.widget.PullHeaderScrollView;
import org.xmlpull.v1.XmlPullParserException;

import android.os.Bundle;
import android.app.Activity;
import android.widget.LinearLayout;

public class NewsActivity extends Activity {

	NewsDocument mDocument;
	PullHeaderScrollView scrollView;
	ArcProgress progressView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_news);
		scrollView = (PullHeaderScrollView) findViewById(R.id.news_scrollview);
		progressView = (ArcProgress) findViewById(R.id.news_progress);

		scrollView.setOnPullListener(new OnPullListener() {
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

		LinearLayout contentLayout = (LinearLayout) findViewById(R.id.news_content);
		mDocument = new NewsDocument(this, contentLayout);
		try {
			InputStream is = getAssets().open("TestNews.xml");
			mDocument.parseNews(is, "UTF-8");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.stay, R.anim.slide_out_right);
	}

}
