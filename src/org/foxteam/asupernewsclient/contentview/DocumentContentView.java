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
	static final String TAG_STYLE = "style";
	static final String TAG_STYLE_ATTR_TYPE = "type";

	static final String VALUE_STYLE_TYPE_CSS = "text/css";

	private LinkedList<ContentView> mContents = new LinkedList<ContentView>();

	private Styles mBaseStyle = new Styles();
	private Styles mDocumentStyle;

	public DocumentContentView() {
		mBaseStyle.readStyle("FoxDocument{}" + "FoxDocument p{}"
				+ "FoxDocument p i{font-style:italic;}"
				+ "FoxDocument p b{font-weight:bold;}"
				+ "FoxDocument p u{text-decoration:+underline;}"
				+ "FoxDocument p del{text-decoration:+line-through;}");
		mDocumentStyle = mBaseStyle.growStyles(TAG_DOCUMENT);
	}

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
					cv = new TextContentView(mDocumentStyle);
				} else if (TAG_IMAGE.equals(tagName)) {
					cv = new ImageContentView();
				} else if (TAG_STYLE.equals(tagName)) {
					parseStyle(parser);
					break;
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

	private void parseStyle(XmlPullParser parser)
			throws XmlPullParserException, IOException {
		int eventType = parser.getEventType();
		String tagName;

		if (eventType != XmlPullParser.START_TAG) {
			throw new RuntimeException("Expecting START_TAG");
		}

		tagName = parser.getName();

		if (!TAG_STYLE.equals(tagName)) {
			throw new RuntimeException("Expecting " + TAG_STYLE + ", got "
					+ tagName);
		}

		int attrCount = parser.getAttributeCount();
		String attrName, attrValue;
		for (int i = 0; i < attrCount; i++) {
			attrName = parser.getAttributeName(i);
			if (TAG_STYLE_ATTR_TYPE.equals(attrName)) {
				attrValue = parser.getAttributeValue(i);
				if (!VALUE_STYLE_TYPE_CSS.equals(attrValue)) {
					throw new RuntimeException("Style only support "
							+ VALUE_STYLE_TYPE_CSS + ", got " + attrValue);
				}
			} else {

			}
		}

		eventType = parser.next();
		if (eventType != XmlPullParser.TEXT) {
			throw new RuntimeException("Expecting TEXT");
		}

		String stylesStr = parser.getText();
		mDocumentStyle.readStyle(stylesStr);

		eventType = parser.next();
		if (eventType != XmlPullParser.END_TAG) {
			throw new RuntimeException("Expecting END_TAG");
		}

		tagName = parser.getName();

		if (!TAG_STYLE.equals(tagName)) {
			throw new RuntimeException("Expecting " + TAG_STYLE + ", got "
					+ tagName);
		}
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
