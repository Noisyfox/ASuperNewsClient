package org.foxteam.asupernewsclient.contentview;

import java.io.IOException;

import org.foxteam.asupernewsclient.R;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class TextContentView implements ContentView {

	private String mText = "";

	@Override
	public View getView(Context context, XmlPullParser parser, ViewGroup parent)
			throws XmlPullParserException, IOException {
		int eventType = parser.getEventType();
		String tagName;

		if (eventType != XmlPullParser.START_TAG) {
			throw new RuntimeException("Expecting START_TAG!");
		}
		tagName = parser.getName();
		if (!TAG_TEXT.equals(tagName)) {
			throw new RuntimeException("Expecting p, got " + tagName);
		}

		// TODO read global attrs

		eventType = parser.next();
		ol: while (true) {
			switch (eventType) {
			case XmlPullParser.TEXT:
				mText = parser.getText();
				break;
			case XmlPullParser.END_TAG:
				tagName = parser.getName();
				if (!TAG_TEXT.equals(tagName)) {
					throw new RuntimeException("Expecting d, got " + tagName);
				}
				break ol;
			default:
				throw new RuntimeException("Unexpected event!");
			}
			eventType = parser.next();
		}

		return getView(context, parent);
	}

	@Override
	public View getView(Context context, ViewGroup parent) {
		View v = LayoutInflater.from(context).inflate(
				R.layout.news_content_text, parent, false);
		TextView tv = (TextView) v.findViewById(R.id.textView);
		tv.setText(mText);

		return v;
	}

}
