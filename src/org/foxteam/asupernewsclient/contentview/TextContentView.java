package org.foxteam.asupernewsclient.contentview;

import java.io.IOException;
import java.util.List;

import org.foxteam.asupernewsclient.R;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class TextContentView implements ContentView {
	protected static final String TAG_TEXT_ATTR_STYLE = "style";

	private SpannableStringBuilder mTextBuilder;
	private Styles mContentStyles;

	public TextContentView(Styles baseStyle) {
		mContentStyles = baseStyle.growStyles(TAG_TEXT);
	}

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

		mTextBuilder = new SpannableStringBuilder();

		parseTag(parser, mContentStyles);

		return getView(context, parent);
	}

	private static class TextStyles {
		protected static final String STYLE_TEXT_INDENT = "text-indent";

		int text_indent = 0;
	}

	private void parseTag(XmlPullParser parser, Styles currentStyle)
			throws XmlPullParserException, IOException {
		int eventType = parser.getEventType();
		String expectedTagName = parser.getName(), tagName;
		boolean reachedEndOfTag = false;

		TextStyles styles = new TextStyles();

		int attrCount = parser.getAttributeCount();
		String attrName;
		for (int i = 0; i < attrCount; i++) {
			attrName = parser.getAttributeName(i);
			if (TAG_TEXT_ATTR_STYLE.equals(attrName)) {
				String stylesStr = parser.getAttributeValue(i);
				currentStyle.readInlineStyle(stylesStr);
			} else {
				// throw new RuntimeException("Unknown attr " + attrName);
			}
		}

		if (TAG_TEXT.equals(expectedTagName)) {
			String i = currentStyle
					.findStyle(Styles.STYLE_SPANABLE_TEXT_INDENT);
			if (i != null) {
				styles.text_indent = Styles.parseEm(i);
			}
		}/*
		 * else if (TAG_TEXT_ITALIC.equals(expectedTagName)) {
		 * currentStyle.putStyle(Styles.STYLE_SPANABLE_FONT_STYLE, "italic"); }
		 * else if (TAG_TEXT_BOLD.equals(expectedTagName)) {
		 * currentStyle.putStyle(Styles.STYLE_SPANABLE_FONT_WEIGHT, "bold"); }
		 * else if (TAG_TEXT_STRIKE.equals(expectedTagName)) {
		 * currentStyle.putStyle(Styles.STYLE_SPANABLE_TEXT_DECORATION,
		 * "+line-through"); } else if
		 * (TAG_TEXT_UNDERLINE.equals(expectedTagName)) {
		 * currentStyle.putStyle(Styles.STYLE_SPANABLE_TEXT_DECORATION,
		 * "+underline"); }
		 */else {

		}

		int start = mTextBuilder.length(), end;
		while (!reachedEndOfTag) {
			eventType = parser.next();
			switch (eventType) {
			case XmlPullParser.TEXT:
				int pStart = mTextBuilder.length();
				mTextBuilder.append(parser.getText().replaceAll("\\s+", " "));
				int pEnd = mTextBuilder.length();
				{
					List<Object> spans = currentStyle.toSpans();
					for (Object sp : spans) {
						mTextBuilder.setSpan(sp, pStart, pEnd,
								Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
					}
				}
				break;
			case XmlPullParser.START_TAG:
				tagName = parser.getName();
				parseTag(parser, currentStyle.growStyles(tagName));
				break;
			case XmlPullParser.END_TAG:
				tagName = parser.getName();
				if (!expectedTagName.equals(tagName)) {
					throw new RuntimeException("Expecting " + expectedTagName
							+ ", got " + tagName);
				}
				reachedEndOfTag = true;
				break;
			default:
				throw new RuntimeException("Unexpected event!");
			}
		}
		end = mTextBuilder.length();

		if (TAG_TEXT.equals(expectedTagName)) {
			end -= trim(start, end);
		}

		for (int i = 0; i < styles.text_indent; i++) {
			mTextBuilder.insert(start, "\u3000");
		}

	}

	private int trim(int from, int to) {
		int start = from;
		int end = to - 1;
		while ((start <= end)
				&& (Character.isWhitespace(mTextBuilder.charAt(start)))) {
			start++;
		}
		while ((end >= start)
				&& (Character.isWhitespace(mTextBuilder.charAt(end)))) {
			end--;
		}
		if (start == from && end == to - 1) {
			return 0;
		}
		int l = mTextBuilder.length();
		mTextBuilder = mTextBuilder.delete(end + 1, to).delete(from, start);

		return l - mTextBuilder.length();
	}

	@Override
	public View getView(Context context, ViewGroup parent) {
		View v = LayoutInflater.from(context).inflate(
				R.layout.news_content_text, parent, false);
		TextView tv = (TextView) v.findViewById(R.id.textView);
		tv.setText(mTextBuilder);

		return v;
	}

}
