/**
 * HolidaysManager.java
 */
package com.sistemaits.tdw.test.prediction;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Collectors;

/**
 * @author simone.decristofaro
 * 08 set 2015
 */
public class HolidaysManager {
        
    private static HolidaysManager instance;
    
    private HashMap<LocalDate, Integer> holidays;
    
    private HolidaysManager() throws IOException{
        // init holidays
        holidays = new HashMap<>();
        Files.lines(Paths.get("holidays.txt"))
        .skip(1)
        .map(line ->
            line.replaceAll("\"", "").split(","))
        .forEach(v ->{
            v = Arrays.asList(v).stream().map(String::trim).toArray(String[]::new);
            holidays.put(LocalDate.of(Integer.parseInt(v[0]), Integer.parseInt(v[1]), Integer.parseInt(v[2])), holidays.size());            
        });
    }
    
    
    public static boolean isHoliday(LocalDate localDate) throws IOException{
        if(instance==null)
            instance = new HolidaysManager();
        return instance.holidays.containsKey(localDate);
    }

    public static boolean isHoliday(LocalDateTime localDateTime) throws IOException{
        return isHoliday(localDateTime.toLocalDate());
    }
    
}
