/**
 * DayType.java
 */
package com.sistemaits.tdw.test.prediction;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author simone.decristofaro
 *         25 ago 2015
 */
public enum DAY_TYPE {
    WINTER_WEEKDAY(YEAR_PERIOD.WINTER,
            WEEK_PERIOD.WEEKDAY),
    WINTER_SATURDAY(YEAR_PERIOD.WINTER,
            WEEK_PERIOD.SATURDAY),
    WINTER_SUNDAY(YEAR_PERIOD.WINTER,
            WEEK_PERIOD.SUNDAY),
    SUMMER_WEEKDAY(YEAR_PERIOD.SUMMER,
            WEEK_PERIOD.WEEKDAY),
    SUMMER_SATURDAY(YEAR_PERIOD.SUMMER,
            WEEK_PERIOD.SATURDAY),
    SUMMER_SUNDAY(YEAR_PERIOD.SUMMER,
            WEEK_PERIOD.SUNDAY);

    private YEAR_PERIOD yearPeriod;
    private WEEK_PERIOD weekPeriod;

    /**
     * @param yearPeriod
     * @param weekPeriod
     */
    private DAY_TYPE(YEAR_PERIOD yearPeriod, WEEK_PERIOD weekPeriod) {
        this.yearPeriod = yearPeriod;
        this.weekPeriod = weekPeriod;
    }

    public static DAY_TYPE fromLocalDateTime(LocalDateTime localDateTime){
        YEAR_PERIOD yp = YEAR_PERIOD.fromLocalDateTime(localDateTime);
        WEEK_PERIOD wp = WEEK_PERIOD.fromLocalDateTime(localDateTime);
        if(yp.equals(YEAR_PERIOD.WINTER)){
            if(wp.equals(WEEK_PERIOD.WEEKDAY)) return WINTER_WEEKDAY;
            else if(wp.equals(WEEK_PERIOD.SATURDAY)) return WINTER_SATURDAY;
            else return WINTER_SUNDAY;
        }else{
            if(wp.equals(WEEK_PERIOD.WEEKDAY)) return SUMMER_WEEKDAY;
            else if(wp.equals(WEEK_PERIOD.SATURDAY)) return SUMMER_SATURDAY;
            else return SUMMER_SUNDAY;            
        }
        
    }
    
    public static DAY_TYPE fromLocalDate(LocalDate localDate) {
        return fromLocalDateTime(LocalDateTime.of(localDate, LocalTime.ofSecondOfDay(0)));
    }

    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {

        return yearPeriod.toString() + " " + weekPeriod.toString();
    }

    private enum YEAR_PERIOD {
        WINTER,
        SUMMER;

        private static final List<Month> monthsSummer =
                new ArrayList<Month>(Arrays.asList(Month.APRIL, Month.MAY, Month.JUNE, Month.JULY, Month.AUGUST, Month.SEPTEMBER));

        // private static final List<Month> monthsWinter =
        // new ArrayList<Month>(Arrays.asList(Month.OCTOBER, Month.NOVEMBER, Month.DECEMBER, Month.JANUARY, Month.FEBRUARY, Month.MARCH));

        public static YEAR_PERIOD fromLocalDateTime(LocalDateTime localDateTime) {

            return monthsSummer.contains(localDateTime.getMonth()) ? SUMMER : WINTER;
        }
        
        public static YEAR_PERIOD fromLocalDate(LocalDate localDate) {
            return fromLocalDateTime(LocalDateTime.of(localDate, LocalTime.ofSecondOfDay(0)));
        }

    }

    private enum WEEK_PERIOD {
        WEEKDAY,
        SATURDAY,
        SUNDAY;

        public static WEEK_PERIOD fromLocalDateTime(LocalDateTime localDateTime) {

            switch (localDateTime.getDayOfWeek()) {
            case SATURDAY:
                return WEEK_PERIOD.SATURDAY;
            case SUNDAY:
                return WEEK_PERIOD.SUNDAY;
            default:
                return WEEK_PERIOD.WEEKDAY;
            }
        }
        
        public static WEEK_PERIOD fromLocalDate(LocalDate localDate) {
            return fromLocalDateTime(LocalDateTime.of(localDate, LocalTime.ofSecondOfDay(0)));
        }
    }

}
