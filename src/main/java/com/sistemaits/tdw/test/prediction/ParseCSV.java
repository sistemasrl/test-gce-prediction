package com.sistemaits.tdw.test.prediction;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.StringJoiner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ParseCSV {

	private static final int Q_TOTAL_VALIDITE = 8;
	private static final int HORODATE = 0;
	private static final int Q_TOTAL = 4;
	private static final int NOM_CPTG = 1;
	private static final int SENS = 2;

	private static final SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	private static final SimpleDateFormat inst = new SimpleDateFormat("HHmmss");
	private static final Logger log = LoggerFactory.getLogger(Main.class);

	public static void parse() throws Exception {
		FileWriter fw = new FileWriter("out.csv", false);

		Files.list(Paths.get("./")).filter(path -> {
			return path.toFile().getName().endsWith(".csv");
		}).map(path -> {
			ArrayList<String> csv = new ArrayList<>();
			log.info("Reading " + path);

			try {
				Files.lines(path).skip(1).map(line -> {
					return line.replaceAll("\"", "").split(",");
				}).filter(chunks -> {
					return chunks.length > Q_TOTAL_VALIDITE && Integer.parseInt(chunks[Q_TOTAL_VALIDITE]) == 100000;
				}).map(chunks -> {

					long epoch = parse(chunks[HORODATE]).getTime();
					GregorianCalendar gc = new GregorianCalendar();
					gc.setTimeInMillis(epoch);

//					StringJoiner record = new StringJoiner(",");
//				
//					record.add(chunks[Q_TOTAL]).add("\"" + chunks[NOM_CPTG] + "\"").add(epoch + "")
//							.add("\"" + gc.get(GregorianCalendar.DAY_OF_YEAR) + "")
//							.add(gc.get(GregorianCalendar.YEAR) + "").add((gc.get(GregorianCalendar.MONTH) + 1) + "")
//							.add(gc.get(GregorianCalendar.DAY_OF_WEEK) + "")
//							.add(gc.get(GregorianCalendar.DAY_OF_MONTH) + "").add(inst.format(gc.getTime()) + "\"")
//							.add(isFestivo + "").add(chunks[SENS]);
//					return record.toString();
					return chunks[Q_TOTAL] + "," + buildRequest(chunks[NOM_CPTG], gc, chunks[SENS]);
				}).forEach(record -> {
					writeToFile(fw, record);
				});
			} catch (Exception e) {
				e.printStackTrace();
			}

			return csv;
		})

		.forEach(path -> {
			System.out.println(path);
		});
	}

	public static String buildRequest(String cloc, GregorianCalendar gc, String direction){
		
		boolean isFestivo = gc.get(GregorianCalendar.DAY_OF_WEEK) == GregorianCalendar.SUNDAY
				|| gc.get(GregorianCalendar.DAY_OF_WEEK) == GregorianCalendar.SATURDAY;
		
		return  new StringJoiner(",")
				.add("\"" + cloc + "\"")
				.add(gc.getTimeInMillis() + "")
				.add("\"" + gc.get(GregorianCalendar.DAY_OF_YEAR) + "")
				.add(gc.get(GregorianCalendar.YEAR) + "")
				.add((gc.get(GregorianCalendar.MONTH) + 1) + "")
				.add(gc.get(GregorianCalendar.DAY_OF_WEEK) + "")
				.add(gc.get(GregorianCalendar.DAY_OF_MONTH) + "")
				.add(inst.format(gc.getTime()) + "\"")
				.add(isFestivo + "")
				.add(direction)
				.toString();		
	}
	
	private static void writeToFile(FileWriter fw, String s) {
		try {
			fw.write(String.format("%s%n", s));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static Date parse(String s) {
		try {
			return format.parse(s);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
