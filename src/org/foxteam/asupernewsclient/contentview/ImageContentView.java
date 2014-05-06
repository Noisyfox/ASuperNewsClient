package org.foxteam.asupernewsclient.contentview;

import java.io.IOException;

import org.foxteam.asupernewsclient.R;
import org.foxteam.noisyfox.widget.Util;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

public class ImageContentView implements ContentView {

	protected static final String ATTR_ALT = "alt";
	protected static final String ATTR_GRAVITY = "gravity";
	protected static final String ATTR_MARGIN = "margin";
	protected static final String ATTR_SRC = "src";
	protected static final String ATTR_WIDTH = "width";
	protected static final String ATTR_HEIGHT = "height";

	protected static final String VALUE_GRAVITY_CENTER = "center";
	protected static final String VALUE_GRAVITY_LEFT = "left";
	protected static final String VALUE_GRAVITY_RIGHT = "right";
	protected static final String VALUE_SIZE_WRAP_CONTENT = "wrap_content";
	protected static final String VALUE_SIZE_MATCH_PARENT = "match_parent";

	private String mAttr_alt = "";
	private int mAttr_gravity = RelativeLayout.CENTER_HORIZONTAL;
	private float mAttr_margin = 0;
	private String mAttr_src = null;
	private float mAttr_width = RelativeLayout.LayoutParams.WRAP_CONTENT;
	private float mAttr_height = RelativeLayout.LayoutParams.WRAP_CONTENT;

	@Override
	public View getView(Context context, XmlPullParser parser, ViewGroup parent)
			throws XmlPullParserException, IOException {
		int eventType = parser.getEventType();
		String tagName;

		if (eventType != XmlPullParser.START_TAG) {
			throw new RuntimeException("Expecting START_TAG!");
		}
		tagName = parser.getName();
		if (!TAG_IMAGE.equals(tagName)) {
			throw new RuntimeException("Expecting img, got " + tagName);
		}

		String attrName, attrValue;
		int attrCount = parser.getAttributeCount();
		for (int i = 0; i < attrCount; i++) {
			attrName = parser.getAttributeName(i);
			attrValue = parser.getAttributeValue(i);
			if (ATTR_ALT.equals(attrName)) {
				mAttr_alt = attrValue;
			} else if (ATTR_GRAVITY.equals(attrName)) {
				if (VALUE_GRAVITY_LEFT.equals(attrValue)) {
					mAttr_gravity = RelativeLayout.ALIGN_PARENT_LEFT;
				} else if (VALUE_GRAVITY_RIGHT.equals(attrValue)) {
					mAttr_gravity = RelativeLayout.ALIGN_PARENT_RIGHT;
				} else if (VALUE_GRAVITY_CENTER.equals(attrValue)) {
					mAttr_gravity = RelativeLayout.CENTER_HORIZONTAL;
				} else {
					throw new RuntimeException("Unknown gravity " + attrValue);
				}
			} else if (ATTR_MARGIN.equals(attrName)) {
				mAttr_margin = Float.parseFloat(attrValue);
			} else if (ATTR_SRC.equals(attrName)) {
				mAttr_src = attrValue;
			} else if (ATTR_WIDTH.equals(attrName)) {
				mAttr_width = parseSizeValue(attrValue);
			} else if (ATTR_HEIGHT.equals(attrName)) {
				mAttr_height = parseSizeValue(attrValue);
			} else {
				throw new RuntimeException("Unknown attr " + attrName);
			}
		}

		eventType = parser.next();
		switch (eventType) {
		case XmlPullParser.END_TAG:
			tagName = parser.getName();
			if (!TAG_IMAGE.equals(tagName)) {
				throw new RuntimeException("Expecting img, got " + tagName);
			}
			break;
		default:
			tagName = parser.getName();
			throw new RuntimeException("Expecting END_TAG, got " + tagName);
		}

		return getView(context, parent);
	}

	private static float parseSizeValue(String value) {
		if (VALUE_SIZE_WRAP_CONTENT.equals(value)) {
			return RelativeLayout.LayoutParams.WRAP_CONTENT;
		} else if (VALUE_SIZE_MATCH_PARENT.equals(value)) {
			return RelativeLayout.LayoutParams.MATCH_PARENT;
		}

		return Float.parseFloat(value);
	}

	@Override
	public View getView(Context context, ViewGroup parent) {
		View v = LayoutInflater.from(context).inflate(
				R.layout.news_content_image, parent, false);
		ImageView iv = (ImageView) v.findViewById(R.id.imageView);
		RelativeLayout.LayoutParams layoutParams = (LayoutParams) iv
				.getLayoutParams();
		layoutParams.width = Util.dip2px(context, mAttr_width);
		layoutParams.height = Util.dip2px(context, mAttr_height);
		int margin = Util.dip2px(context, mAttr_margin);
		layoutParams.setMargins(margin, margin, margin, margin);
		layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, 0);
		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
		layoutParams.addRule(mAttr_gravity);
		iv.setLayoutParams(layoutParams);
		iv.setContentDescription(mAttr_alt);
		return v;
	}

}
