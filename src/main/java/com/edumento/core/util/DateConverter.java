package com.edumento.core.util;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;

/** Created by ahmad on 7/12/16. */
public class DateConverter {
	public static ZonedDateTime convertDateToZonedDateTime(Date date) {
		if (date == null) {
			return null;
		}

		return ZonedDateTime.ofInstant(new Date(date.getTime()).toInstant(), ZoneOffset.UTC);
	}

	public static Date convertZonedDateTimeToDate(ZonedDateTime zonedDateTime) {
		if (zonedDateTime == null) {
			return null;
		}
		var instant = zonedDateTime.toInstant();
		if (!ZoneOffset.UTC.equals(zonedDateTime.getOffset())) {
			instant = instant.atOffset(ZoneOffset.UTC).toInstant();
		}
		return Date.from(instant);
	}
}
