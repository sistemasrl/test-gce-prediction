/**
 * Utils.java
 */
package com.sistemaits.tdw.test.prediction;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

/**
 * @author simone.decristofaro
 *         27 ago 2015
 */
public class Utils {

    public static void writeToFile(FileWriter fw, String s) {

        try {
            fw.write(String.format("%s%n", s));
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static String getHourTimeSlot(LocalDateTime localDateTime){
        int from = localDateTime.getHour();
        int to = from + 1;
        return from + "_" + to;
    }

}
