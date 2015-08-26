package test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalField;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.stream.IntStream;

import com.sistemaits.tdw.test.prediction.TIME_TYPE;

public class SimpleTests {

	public static void main(String[] args) {
		
		IntStream.range(0, 25).forEach(h -> {
			System.out.println(h + "," + TIME_TYPE.fromLocalDate(h));
		});
		
		for(TIME_TYPE type : TIME_TYPE.values()) {
			System.out.println(type);
		}
		
	}
	
	public static void mainss(String[] args) throws ParseException {
		String date = "02/02/2010 02:36:00";
		
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		SimpleDateFormat inst = new SimpleDateFormat("HHmmss");
		
		long epoch = format.parse(date).getTime();
		
		
		GregorianCalendar gc = new GregorianCalendar();
		gc.setTimeInMillis(epoch);
		
		System.out.println(gc.get(GregorianCalendar.DAY_OF_YEAR));
		System.out.println(gc.get(GregorianCalendar.YEAR));
		System.out.println(gc.get(GregorianCalendar.MONTH));
		System.out.println("-" + gc.get(GregorianCalendar.DAY_OF_WEEK));
		System.out.println(gc.get(GregorianCalendar.DAY_OF_MONTH));
		System.out.println(gc.get(GregorianCalendar.DAY_OF_WEEK) == GregorianCalendar.SUNDAY);
		System.out.println(inst.format(gc.getTime())); // Time of day
		
		boolean isFestivo = 
				gc.get(GregorianCalendar.DAY_OF_WEEK) == GregorianCalendar.SUNDAY ||
						gc.get(GregorianCalendar.DAY_OF_WEEK) == GregorianCalendar.SATURDAY;
		
		System.out.println(isFestivo);
		
	}
	
}
