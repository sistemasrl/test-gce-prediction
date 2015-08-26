package com.sistemaits.tdw.test.prediction;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void parse() throws Exception {

        try {
            FileWriter fw = new FileWriter("out.csv", false);

            // Files.list(Paths.get("./")).map(path -> path.toString()).forEach(System.out::println);
            
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
                    .map(line ->
                        line.replaceAll("\"", "").split(","))
                    .filter(chunks -> {
                        return //chunks[V_TOTAL_VALIDITE].equals("100000") && 
                        		chunks[Q_TOTAL_VALIDITE].equals("100000")
                        		//&& chunks[ID].equals("S21_A13_01_115KM4_VA");
                        		;
                    })
                    .map(chunks -> {

                        LocalDateTime localDate = LocalDateTime.parse(chunks[HORODATE], dtf);
                        DAY_TYPE dayType = DAY_TYPE.fromLocalDate(localDate);
                        TIME_TYPE timeType = TIME_TYPE.fromLocalDate(localDate);
                        StringJoiner sj = new StringJoiner(",")
                        		.add(chunks[Q_TOTAL])
                        		//.add("\"" + chunks[ID] + "\"")
                                .add("\"" + dayType.toString() + "\"")
                                .add("\"" + timeType.toString() + "\"")
                                .add(localDate.get(ChronoField.SECOND_OF_DAY)+"")
                                .add(chunks[V_TOTAL])
                                ;

                        return sj.toString();
                    }).forEach(record -> {
                        writeToFile(fw, record);
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

//    public static String buildRequest(String cloc, GregorianCalendar gc, String direction) {
//
//        boolean isFestivo = gc.get(GregorianCalendar.DAY_OF_WEEK) == GregorianCalendar.SUNDAY
//                || gc.get(GregorianCalendar.DAY_OF_WEEK) == GregorianCalendar.SATURDAY;
//
//        return new StringJoiner(",").add("\"" + cloc + "\"").add(gc.getTimeInMillis() + "").add("\"" + gc.get(GregorianCalendar.DAY_OF_YEAR) + "")
//                .add(gc.get(GregorianCalendar.YEAR) + "").add((gc.get(GregorianCalendar.MONTH) + 1) + "")
//                .add(gc.get(GregorianCalendar.DAY_OF_WEEK) + "").add(gc.get(GregorianCalendar.DAY_OF_MONTH) + "")
//                .add(inst.format(gc.getTime()) + "\"").add(isFestivo + "").add(direction).toString();
//    }

    
    
    
    private static void writeToFile(FileWriter fw, String s) {

        try {
            fw.write(String.format("%s%n", s));
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
