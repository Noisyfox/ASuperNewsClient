package org.foxteam.asupernewsclient.contentview;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.graphics.Color;
import android.graphics.Typeface;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;

public class Styles {
	/*
	 * color:#00ff00; background-color:rgb(250,0,255); text-indent:2em;
	 * font-family:Arial,Verdana,Sans-serif; font-size:100%; font-style:italic;
	 */
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
	//private Styles mBase = null;
	private HashMap<String, String> mCurrentStyles = new HashMap<String, String>();
	private HashMap<String, Styles> mChildStyles = new HashMap<String, Styles>();
	// private HashMap<String, HashMap<String, String>> mChildStyles_old = new
	// HashMap<String, HashMap<String, String>>();
	private Styles mSuper = null;

	public Styles getOverrideStyles(String tag) {
		Styles base = mChildStyles.get(tag);
		if (base == null) {
			return getChildStyles(tag);
		}

		Styles c = new Styles();
		c.mTag = tag;
		c.mSuper = base;

		return c;
	}

	public Styles getChildStyles(String tag) {
		Styles c = new Styles();
		c.mSuper = this;
		c.mTag = tag;

		return c;
	}

	/*
	 * public String findStyle(String tag, String name) { String v;
	 * 
	 * if (tag != null) { HashMap<String, String> t = mChildStyles_old.get(tag);
	 * if (t != null) { v = t.get(name);
	 * 
	 * if (v != null) { return v; } } }
	 * 
	 * v = mCurrentStyles.get(name);
	 * 
	 * if (v != null) { return v; }
	 * 
	 * if (mSuper == null) { return null; }
	 * 
	 * return mSuper.findStyle(mTag, name); }
	 * 
	 * public String findStyle(String name) { return findStyle(null, name); }
	 */

	public String findStyle(String tag, String name) {
		String v = "";

		Styles cStyles = this;
		while (cStyles != null) {
			String s = cStyles.mCurrentStyles.get(name);
			if (s != null) {
				if (s.startsWith("+")) {
					v = s.substring(1) + " " + v;
				} else {
					v = s + " " + v;
					break;
				}
			}

			//if (cStyles.mBase != null) {
			//	cStyles = cStyles.mBase;
			//} else {
				Styles c = cStyles;
				cStyles = cStyles.mSuper;
				if (cStyles != null && tag != null) {
					Styles us = cStyles.mChildStyles.get(tag);
					if (us != null && us != c) {
						s = us.mCurrentStyles.get(name);
						if (s != null) {
							if (s.startsWith("+")) {
								v = s.substring(1) + " " + v;
							} else {
								v = s + " " + v;
								break;
							}
						}
					}
				}
			//}
		}

		v = v.trim();

		return v.isEmpty() ? null : v;
	}

	public void putStyle(String name, String value) {
		mCurrentStyles.put(name, value);
	}

	public void putStyle(String tag, String name, String value) {
		if (tag == null) {
			putStyle(name, value);
			return;
		}

		String ch[] = tag.split(" ");
		Styles s = this;
		for(String str : ch){
			Styles f = s;
			s = f.mChildStyles.get(str);
			if (s == null) {
				s = f.getChildStyles(str);
				f.mChildStyles.put(str, s);
			}
		}
		s.putStyle(name, value);
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

	private void mergeStyles(HashMap<String, Queue<String>> des,
			HashMap<String, String> src) {
		Set<Entry<String, String>> ss = src.entrySet();
		for (Entry<String, String> _s : ss) {
			String k = _s.getKey();
			Queue<String> q = des.get(k);
			if (q == null) {
				q = new LinkedList<String>();
				des.put(k, q);
			}
			q.offer(_s.getValue());
		}
	}

	public List<Object> toSpans() {
		List<Object> sps = new ArrayList<Object>();

		// ArrayList<Entry<String, String>> slist = new ArrayList<Entry<String,
		// String>>();

		HashMap<String, Queue<String>> sMap = new HashMap<String, Queue<String>>();

		Styles cStyles = this;
		while (cStyles != null) {
			mergeStyles(sMap, cStyles.mCurrentStyles);

			//if (cStyles.mBase != null) {
			//	cStyles = cStyles.mBase;
			//} else {
				Styles c = cStyles;
				cStyles = cStyles.mSuper;
				if (cStyles != null && mTag != null) {
					Styles us = cStyles.mChildStyles.get(mTag);
					if (us != null && us != c) {
						mergeStyles(sMap, us.mCurrentStyles);
					}
				}
			//}
		}

		/*
		 * if (mSuper != null && mTag != null &&
		 * mSuper.mChildStyles_old.containsKey(mTag)) { Set<Entry<String,
		 * String>> _ss = mSuper.mChildStyles_old.get(mTag) .entrySet(); for
		 * (Entry<String, String> _s : _ss) { if
		 * (!mCurrentStyles.containsKey(_s.getKey())) { slist.add(_s); } } }
		 */

		String styleName, styleValue;
		for (Entry<String, Queue<String>> _s : sMap.entrySet()) {
			styleName = _s.getKey();
			styleValue = "";
			Queue<String> q = _s.getValue();
			while (!q.isEmpty()) {
				String s = q.poll();
				if (s.startsWith("+")) {
					styleValue = s.substring(1) + " " + styleValue;
				} else {
					styleValue = s + " " + styleValue;
					break;
				}
			}
			styleValue = styleValue.trim();

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

	/*
	 * currentStyle.putStyle("text-decoration", "line-through"); //
	 * spans.add(new StrikethroughSpan()); } else if
	 * (TAG_TEXT_UNDERLINE.equals(expectedTagName)) {
	 * currentStyle.putStyle("text-decoration", "underline"); // spans.add(new
	 * UnderlineSpan());
	 */

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
