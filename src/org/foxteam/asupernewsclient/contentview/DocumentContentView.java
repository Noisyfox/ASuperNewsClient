package org.foxteam.asupernewsclient.contentview;

import java.io.IOException;
import java.util.LinkedList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class DocumentContentView implements ContentView {
	private LinkedList<ContentView> mContents = new LinkedList<ContentView>();

	@Override
	public View getView(Context context, XmlPullParser parser, ViewGroup parent)
			throws XmlPullParserException, IOException {
		int eventType = parser.getEventType();
		String tagName;
		boolean lookingForEndOfUnknownTag = false;
		String unknownTagName = null;

		if (eventType != XmlPullParser.START_TAG) {
			throw new RuntimeException("Expecting START_TAG!");
		}
		tagName = parser.getName();
		if (!TAG_DOCUMENT.equals(tagName)) {
			throw new RuntimeException("Expecting FoxDocument, got " + tagName);
		}

		if (parent == null) {
			parent = new LinearLayout(context);
		}

		// TODO read settings

		eventType = parser.next();
		boolean reachedEndOfNewsDocument = false;
		while (!reachedEndOfNewsDocument) {
			switch (eventType) {
			case XmlPullParser.START_TAG:
				if (lookingForEndOfUnknownTag) {
					break;
				}
				tagName = parser.getName();
				ContentView cv;
				if (TAG_TEXT.equals(tagName)) {
					cv = new TextContentView();
				} else if (TAG_IMAGE.equals(tagName)) {
					cv = new ImageContentView();
				} else {
					lookingForEndOfUnknownTag = true;
					unknownTagName = tagName;
					break;
				}
				View view = cv.getView(context, parser, parent);
				mContents.add(cv);
				parent.addView(view);
				break;
			case XmlPullParser.END_TAG:
				tagName = parser.getName();
				if (lookingForEndOfUnknownTag && tagName.equals(unknownTagName)) {
					lookingForEndOfUnknownTag = false;
					unknownTagName = null;
				}
				if (TAG_DOCUMENT.equals(tagName)) {
					reachedEndOfNewsDocument = true;
				} else {
					throw new RuntimeException("Expecting FoxDocument, got "
							+ tagName);
				}
				break;
			case XmlPullParser.END_DOCUMENT:
				throw new RuntimeException("Unexpected end of document");
			}
			eventType = parser.next();
		}

		return parent;
	}

	@Override
	public View getView(Context context, ViewGroup parent) {
		if (parent == null) {
			parent = new LinearLayout(context);
		}

		for (ContentView cv : mContents) {
			parent.addView(cv.getView(context, parent));
		}

		return parent;
	}

}
