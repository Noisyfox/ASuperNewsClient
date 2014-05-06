package org.foxteam.asupernewsclient;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

import org.foxteam.asupernewsclient.contentview.ContentView;
import org.foxteam.asupernewsclient.contentview.DocumentContentView;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.util.Xml;
import android.view.ViewGroup;

public class NewsDocument {
	private Context mContext;
	private ViewGroup mContentView;
	private DocumentContentView mDocumentContentView = new DocumentContentView();

	public NewsDocument(Context context, ViewGroup contentView) {
		mContext = context;
		mContentView = contentView;
	}

	public void parseNews(String xml) throws XmlPullParserException,
			IOException {
		XmlPullParser parser = Xml.newPullParser();
		StringReader reader = new StringReader(xml);
		parser.setInput(reader);

		parseNews(parser);
	}

	public void parseNews(InputStream is, String encode)
			throws XmlPullParserException, IOException {
		XmlPullParser parser = Xml.newPullParser();
		parser.setInput(is, encode);

		parseNews(parser);
	}

	public void parseNews(XmlPullParser parser) throws XmlPullParserException,
			IOException {
		int eventType = parser.getEventType();
		String tagName;

		// This loop will skip to the document start tag
		do {
			if (eventType == XmlPullParser.START_TAG) {
				tagName = parser.getName();
				if (tagName.equals(ContentView.TAG_DOCUMENT)) {
					break;
				}

				throw new RuntimeException("Expecting FoxDocument, got "
						+ tagName);
			}
			eventType = parser.next();
		} while (eventType != XmlPullParser.END_DOCUMENT);

		if (eventType != XmlPullParser.START_TAG) {
			throw new RuntimeException("Erroring parse xml");
		}

		mDocumentContentView.getView(mContext, parser, mContentView);
	}
}
