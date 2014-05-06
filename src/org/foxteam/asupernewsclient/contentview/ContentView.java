package org.foxteam.asupernewsclient.contentview;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

public interface ContentView {
	static final String TAG_DOCUMENT = "FoxDocument";
	static final String TAG_TEXT = "p";
	static final String TAG_IMAGE = "img";

	public View getView(Context context, XmlPullParser parser, ViewGroup parent)
			throws XmlPullParserException, IOException;

	public View getView(Context context, ViewGroup parent);
}
