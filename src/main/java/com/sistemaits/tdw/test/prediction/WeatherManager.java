/**
 * WeatherManager.java
 */
package com.sistemaits.tdw.test.prediction;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Month;
import java.util.HashMap;
import java.util.Map;

/**
 * @author simone.decristofaro
 * 28 ago 2015
 */
public class WeatherManager {

    private Map<Integer, //year 
    Map<Month,//month
    Map<Integer,//day
    Map<Integer // hour
    ,String>>>> map;
    
    /**
     * 
     */
    private WeatherManager() {}

    private static WeatherManager load() throws IOException{
        Files.lines(Paths.get("weather.csv"))
        .skip(1)
        .forEach(System.out::println);
        ;
        return null;
    }

}
