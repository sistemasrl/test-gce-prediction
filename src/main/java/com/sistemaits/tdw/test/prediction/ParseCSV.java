package com.sistemaits.tdw.test.prediction;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.StringJoiner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ParseCSV {

    private static final int YYYYMMDD = 0;
    private static final int SECONDS = 1;
    private static final int ID = 2;
    private static final int DIRECTION = 3;
    private static final int FLOW = 4;
    private static final int SPEED = 5;
    private static final int Q_TOTAL_VALIDITE = 6;
    private static final int V_TOTAL_VALIDITE = 7;

    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void parse() throws Exception {

        try {
            FileWriter fw = new FileWriter("out.csv", false);

            // Files.list(Paths.get("./")).map(path -> path.toString()).forEach(System.out::println);
            
            Files.list(Paths.get("./"))
            .filter(path -> {
                return path.toFile().getName().endsWith(".csv") && !path.toFile().getName().startsWith("out");
            })
            .map(path -> {
                ArrayList<String> csv = new ArrayList<>();
                log.info("Reading " + path);

                try {
                    Files.lines(path)
                    .map(line ->
                        line.replaceAll("\"", "").split(","))
                    .filter(chunks -> {
                        return chunks[V_TOTAL_VALIDITE].equals("100000") && chunks[Q_TOTAL_VALIDITE].equals("100000");
                    })
                    .map(chunks -> {

                        LocalDate localDate = LocalDate.parse(chunks[YYYYMMDD], dtf);
                        DAY_TYPE dayType = DAY_TYPE.fromLocalDate(localDate);

                        StringJoiner sj = new StringJoiner(",").add(chunks[FLOW])
                                .add("\"" + dayType.toString() + "\"")
                                .add("\"" + chunks[ID] + "\"")
                                .add(chunks[SECONDS])
                                //.add(chunks[SPEED])
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
