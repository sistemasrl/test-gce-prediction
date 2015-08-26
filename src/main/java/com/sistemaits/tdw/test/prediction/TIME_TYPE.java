package com.sistemaits.tdw.test.prediction;

import java.time.LocalDateTime;
import java.time.temporal.ChronoField;

public enum TIME_TYPE {
	PARTY(20),			/* Dalle 20 alle 24 */
	EVENING(17),		/* Dalle 17 alle 20 */
	AFTERNOON(13),	    /* Dalle 12 alle 17 (escluse) */
	MIDDAY(10),			/* Dalle 10 alle 12(escluse) */
	MORNING(7),       /* dalle 7 alle 10 (escluse) */
	EARLY_MORNING(5), /* Dalle 5 (inclusa) alle 7 (escluse) */
	NIGHT(0); 			/* Dalle 00 alle 5 */
	
	private int fromInclusive;
//	private int toExclusive;
	private TIME_TYPE(int fromInclusive){
		this.fromInclusive = fromInclusive;
//		this.toExclusive = toExclusive;
	}
	
	public static TIME_TYPE fromLocalDate(LocalDateTime localDate){
		return fromLocalDate(localDate.get(ChronoField.HOUR_OF_DAY));
		
	}

	public static TIME_TYPE fromLocalDate(int h) {
		for(TIME_TYPE t : values()) {
			if(h >= t.fromInclusive){
				return t;
			}			
		}		
		return TIME_TYPE.values()[TIME_TYPE.values().length-1]; // Return the last one  
	}
	
}
