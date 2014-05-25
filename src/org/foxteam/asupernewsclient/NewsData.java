package org.foxteam.asupernewsclient;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class NewsData {
	public final static SimpleDateFormat dateformat = new SimpleDateFormat(
			"yyyy.M.d E", Locale.CHINA);

	String news_id;
	String title;
	String news_abstract;
	String headerPic;
	long time;
	long time_day;
	String time_display;
	String tag[];
	int view;
	boolean allow_comment;
	String content;

}
