package org.foxteam.asupernewsclient;

import java.util.Calendar;

public class Util {

	public static long getDateStamp(long time) {
		Calendar c = Calendar.getInstance();

		c.setTimeInMillis(time);

		c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH),
				c.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
		c.set(Calendar.MILLISECOND, 0);

		return c.getTimeInMillis();
	}
}
