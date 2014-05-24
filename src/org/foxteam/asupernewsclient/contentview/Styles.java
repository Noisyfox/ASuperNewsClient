package org.foxteam.asupernewsclient.contentview;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.graphics.Color;
import android.graphics.Typeface;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;

public class Styles {

	protected static final String STYLE_SPANABLE_COLOR = "color";
	protected static final String STYLE_SPANABLE_BACKGROUND_COLOR = "background-color";
	protected static final String STYLE_SPANABLE_FONT_FAMILY = "font-family:";
	protected static final String STYLE_SPANABLE_FONT_SIZE = "font-size";
	protected static final String STYLE_SPANABLE_FONT_STYLE = "font-style";
	protected static final String STYLE_SPANABLE_FONT_WEIGHT = "font-weight";
	protected static final String STYLE_SPANABLE_TEXT_DECORATION = "text-decoration";
	protected static final String STYLE_SPANABLE_TEXT_INDENT = "text-indent";

	private static final Pattern mStylePattern = Pattern.compile(
			"(.+?)\\{(.*?(['\"]).*?\\3.*?|.*?)\\}", Pattern.MULTILINE
					| Pattern.DOTALL);
	private static final Pattern mStyleContentPattern = Pattern.compile(
			"\\s*(.*?)\\s*:\\s*(.*?)\\s*(;|\\Z)", Pattern.MULTILINE
					| Pattern.DOTALL);
	private static final Pattern mStyleRGBPattern = Pattern.compile(
			"rgb\\((\\d*\\.?\\d*%?),(\\d*\\.?\\d*%?),(\\d*\\.?\\d*%?)\\)",
			Pattern.MULTILINE | Pattern.DOTALL);

	private String mTag = null;

	private HashMap<String, String> mCurrentStyles = new HashMap<String, String>();
	private HashMap<String, Styles> mChildStyles = new HashMap<String, Styles>();

	public String getTag() {
		return mTag;
	}

	public Styles growStyles(String tag) {
		Styles ns = new Styles();
		ns.mTag = tag;

		// 应用父样式
		for (Entry<String, String> s : mCurrentStyles.entrySet()) {
			ns.putStyle(s.getKey(), s.getValue());
		}
		for (Entry<String, Styles> s : mChildStyles.entrySet()) {
			ns.mChildStyles.put(s.getKey(), s.getValue());
		}

		// 如果有下级样式
		Styles sc = mChildStyles.get(tag);
		if (sc != null) {
			for (Entry<String, String> s : sc.mCurrentStyles.entrySet()) {
				ns.putStyle(s.getKey(), s.getValue());
			}
			for (Entry<String, Styles> s : sc.mChildStyles.entrySet()) {
				ns.mChildStyles.put(s.getKey(), s.getValue());
			}
		}

		return ns;
	}

	public void putStyle(String tag, String name, String value) {
		if (tag == null) {
			mCurrentStyles.put(name, value);
			return;
		}

		String ch[] = tag.split(" ");
		Styles s = this;
		for (String str : ch) {
			Styles f = s;
			s = f.mChildStyles.get(str);
			if (s == null) {
				s = new Styles();
				s.mTag = str;
				f.mChildStyles.put(str, s);
			}
		}
		s.mCurrentStyles.put(name, value);
	}

	public void putStyle(String name, String value) {
		if (value.startsWith("+")) {
			value = value.substring(1);
			String oV = mCurrentStyles.get(name);
			if (oV != null) {
				value = oV + " " + value;
			}
		}
		mCurrentStyles.put(name, value);
	}

	public void readStyle(String css) {
		Matcher styleMatcher = mStylePattern.matcher(css);

		String styleTag, styleContent, styleName, styleValue;
		while (styleMatcher.find()) {
			styleTag = styleMatcher.group(1).trim();
			styleContent = styleMatcher.group(2).trim();

			Log.d(styleTag, styleContent);

			Matcher styleContentMatcher = mStyleContentPattern
					.matcher(styleContent);

			while (styleContentMatcher.find()) {
				styleName = styleContentMatcher.group(1).trim();
				styleValue = styleContentMatcher.group(2).trim();
				Log.d(styleName, styleValue);

				putStyle(styleTag, styleName, styleValue);
			}
		}
	}

	public void readInlineStyle(String style) {
		String styleName, styleValue;
		Matcher styleContentMatcher = mStyleContentPattern.matcher(style);

		while (styleContentMatcher.find()) {
			styleName = styleContentMatcher.group(1).trim();
			styleValue = styleContentMatcher.group(2).trim();
			Log.d(styleName, styleValue);

			putStyle(styleName, styleValue);
		}
	}

	public String findStyle(String name) {
		return mCurrentStyles.get(name);
	}

	public List<Object> toSpans() {
		List<Object> sps = new ArrayList<Object>();

		for (Entry<String, String> _s : mCurrentStyles.entrySet()) {
			String styleName = _s.getKey();
			String styleValue = _s.getValue();

			if (STYLE_SPANABLE_COLOR.equals(styleName)) {
				sps.add(new android.text.style.ForegroundColorSpan(
						parseColor(styleValue)));
			} else if (STYLE_SPANABLE_BACKGROUND_COLOR.equals(styleName)) {
				sps.add(new android.text.style.BackgroundColorSpan(
						parseColor(styleValue)));
			} else if (STYLE_SPANABLE_FONT_FAMILY.equals(styleName)) {
				sps.add(new android.text.style.TypefaceSpan(styleValue));
			} else if (STYLE_SPANABLE_FONT_SIZE.equals(styleName)) {
				if (styleValue.endsWith("%")) {
					sps.add(new android.text.style.RelativeSizeSpan(
							parsePersent(styleValue)));
				} else {
					sps.add(new android.text.style.AbsoluteSizeSpan(Integer
							.parseInt(styleValue)));
				}
			} else if (STYLE_SPANABLE_FONT_STYLE.equals(styleName)) {
				if ("italic".equals(styleValue)) {
					sps.add(new StyleSpan(Typeface.ITALIC));
				}
			} else if (STYLE_SPANABLE_FONT_WEIGHT.equals(styleName)) {
				if ("bold".equals(styleValue)) {
					sps.add(new StyleSpan(Typeface.BOLD));
				}
			} else if (STYLE_SPANABLE_TEXT_DECORATION.equals(styleName)) {
				String decs[] = styleValue.split(" ");
				for (String s : decs) {
					if ("line-through".equals(s)) {
						sps.add(new StrikethroughSpan());
					} else if ("underline".equals(s)) {
						sps.add(new UnderlineSpan());
					}
				}
			}
		}

		return sps;
	}

	public static float parsePersent(String persent) {
		if (persent.endsWith("%")) {
			persent = persent.substring(0, persent.length() - 1);
			float r = Float.parseFloat(persent);
			r /= 100f;
			return r;
		}
		throw new NumberFormatException();
	}

	public static int parseColor(String color) {
		if (color.startsWith("#")) {// example: #ff0000 or #f00
			if (color.length() == 4) {
				char c[] = new char[] { '#', color.charAt(1), color.charAt(1),
						color.charAt(2), color.charAt(2), color.charAt(3),
						color.charAt(3) };
				color = String.valueOf(c);
			}
			return Color.parseColor(color);
		}
		color.replace(" ", "");
		Matcher rgbMatcher = mStyleRGBPattern.matcher(color);
		if (rgbMatcher.matches()) {// example: rgb(255,255,255)
									// rgb(100%,100%,100%)
			String rs = rgbMatcher.group(1), gs = rgbMatcher.group(2), bs = rgbMatcher
					.group(3);
			float r, g, b;
			if (rs.endsWith("%")) {
				rs = rs.substring(0, rs.length() - 1);
				r = Float.parseFloat(rs);
				r /= 100f;
				r *= 255f;
			} else {
				r = Float.parseFloat(rs);
			}
			if (gs.endsWith("%")) {
				gs = gs.substring(0, gs.length() - 1);
				g = Float.parseFloat(gs);
				g /= 100f;
				g *= 255f;
			} else {
				g = Float.parseFloat(gs);
			}
			if (bs.endsWith("%")) {
				bs = bs.substring(0, bs.length() - 1);
				b = Float.parseFloat(bs);
				b /= 100f;
				b *= 255f;
			} else {
				b = Float.parseFloat(bs);
			}

			return Color.rgb((int) r, (int) g, (int) b);
		}

		return Color.parseColor(color);
	}

	public static int parseEm(String em) {
		if (!em.endsWith("em")) {
			throw new NumberFormatException();
		}

		return Integer.parseInt(em.substring(0, em.length() - 2));
	}
}
