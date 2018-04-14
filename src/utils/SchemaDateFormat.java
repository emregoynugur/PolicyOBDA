package utils;

import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.IllegalFormatException;
import java.util.UnknownFormatFlagsException;


public class SchemaDateFormat {
	public SimpleDateFormat f;

	public static String getTimeStamp() {
		return new SchemaDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")).format(new Date());
	}

	public static boolean isDate(String inDate) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
		dateFormat.setLenient(false);
		try {
			dateFormat.parse(inDate.trim());
		} catch (ParseException pe) {
			return false;
		}
		return true;
	}

	public static boolean isDateTime(String inDate) {
		DateTimeFormatter dtf = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
		try {
			dtf.parse(inDate);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public SchemaDateFormat(SimpleDateFormat sdf) throws IllegalFormatException {
		if (sdf.toString().contains("z")) {
			throw new UnknownFormatFlagsException("z");
		}

		f = sdf;
	}

	public String format(Date date) throws IllegalFormatException {
		String s = f.format(date);

		if (f.toPattern().contains("Z")) {
			return new StringBuilder(s).insert(s.length() - 2, ':').toString();
		} else {
			return s;
		}
	}
}