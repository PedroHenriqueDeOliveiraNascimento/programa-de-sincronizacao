package br.com.phon;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public final class Ajudantes {
	public static String calendarParaString(Calendar c) {
		if(c == null)
			return null;	
		return String.format("%04d-%02d-%02d %02d:%02d:%02d", c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), c.get(Calendar.SECOND));
	}
	
	public static Calendar stringParaCalendar(String data) {
		if(data == null)
			return null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd H:m:s");
			Calendar cal = Calendar.getInstance();
			cal.setTime(sdf.parse(data));
			return cal;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
