package com.sistemaits.tdw.test.prediction;

import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.StringJoiner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ParseCSV {

	/*
    private static final int YYYYMMDD = 0;
    private static final int SECONDS = 1;
    private static final int ID = 2;
    private static final int DIRECTION = 3;
    private static final int FLOW = 4;
    private static final int SPEED = 5;
    private static final int Q_TOTAL_VALIDITE = 6;
    private static final int V_TOTAL_VALIDITE = 7;
    */
	
	private static final int HORODATE = 0;
	private static final int NOM_CPTG = 1;
	private static final int SENS = 2;
	private static final int Q_TOTAL = 4;
	private static final int V_TOTAL = 6;
    private static final int Q_TOTAL_VALIDITE = 8;
    private static final int V_TOTAL_VALIDITE = 10;

    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private static final DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final Logger log = LoggerFactory.getLogger(Main.class);
    private static final int FLOW_RANGE = 140; // veh/h

    public static void parse() throws Exception {

        try {
            FileWriter fw = new FileWriter("out.csv", false);

            // Files.list(Paths.get("./")).map(path -> path.toString()).forEach(System.out::println);
            
            // add weather
            
            // if(Files.list(Paths.get("./")).anyMatch(path -> path.toFile().getName().equals("weather.csv")))
                
            
            Files.list(Paths.get("./"))
            .filter(path -> {
                return path.toFile().getName().endsWith(".csv") 
                		&& !path.toFile().getName().startsWith("out");
            })
            .map(path -> {
                ArrayList<String> csv = new ArrayList<>();
                log.info("Reading " + path);

                try {
                    Files.lines(path)
                    .skip(1)
                    .map(line ->
                        line.replaceAll("\"", "").split(","))
                    .filter(chunks -> {
                        LocalDateTime localDateTime = null;
                        try {
                            localDateTime=LocalDateTime.parse(chunks[HORODATE], dtf);
                        }
                        catch (DateTimeParseException e) {
                            localDateTime=LocalDateTime.parse(chunks[HORODATE], dtf2);
                        }
                        return //chunks[V_TOTAL_VALIDITE].equals("100000") && 
                                chunks[Q_TOTAL_VALIDITE].startsWith("1")
                                && chunks[V_TOTAL_VALIDITE].startsWith("1")
                        		&& localDateTime.getYear() == 2011
                        		// && chunks[SENS].equals("01")
                        		//&& chunks[ID].equals("S21_A13_01_115KM4_VA");
                        		;
                    })
                    .map(chunks -> {

                        LocalDateTime localDateTime = null;
                        try {
                            localDateTime=LocalDateTime.parse(chunks[HORODATE], dtf);
                        }
                        catch (DateTimeParseException e) {
                            localDateTime=LocalDateTime.parse(chunks[HORODATE], dtf2);
                        }
                        
                        DAY_TYPE dayType = DAY_TYPE.fromLocalDateTime(localDateTime);
                        // TIME_TYPE timeType = TIME_TYPE.fromLocalDateTime(localDateTIme);
                        StringJoiner sj = null;
                        try {
                            sj = new StringJoiner(",")
                                    //.add(chunks[Q_TOTAL])
                                    .add("\"" + (Integer.parseInt(chunks[Q_TOTAL]) * 60 / 6) + "\"")
                            		//.add("\"" + (chunks[SENS].equals("01") ? "fromParis" : "toParis") + "\"")
                                    .add("\"" + chunks[NOM_CPTG] + "\"")
                            		//.add("\"" + dayType.toString() + "\"")
                                    //.add("\"" + dayType.getYearPeriod() + "\"")
                                    .add("\"" + localDateTime.getMonth() + "\"")
                                    .add("\"" + localDateTime.getDayOfWeek() + "\"")
                                    .add("\"" + Utils.getHourTimeSlot(localDateTime) + "\"")
                                    .add("\"" + HolidaysManager.isHoliday(localDateTime) + "\"")
                                    //.add("\"" + localDateTime.toLocalDate() + "\"")
                                    // .add("\"" + timeType.toString() + "\"")
                                    //.add(""+localDateTime.get(ChronoField.SECOND_OF_DAY))
                                    //.add(localDateTIme.get(ChronoField.SECOND_OF_DAY)+"")
                                    // .add(chunks[V_TOTAL])
                                    ;
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }

                        return sj.toString();
                    }).forEach(record -> {
                        Utils.writeToFile(fw, record);
                    });
                }
                catch (Exception e) {
                    e.printStackTrace();
                }

                return csv;
            })
            .count()
            //.forEach(System.out::println)
            ;

            fw.close();

        }
        catch (Exception e2) {
            e2.printStackTrace();
        }

    }

    
    private static String getFlowCategory(int flow) {

        int r = flow / FLOW_RANGE;
        int min = r*FLOW_RANGE;
        return "" + min + "_" + (min + FLOW_RANGE);
    }


        
}
