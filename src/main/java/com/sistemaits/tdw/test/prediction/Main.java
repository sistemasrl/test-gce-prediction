package com.sistemaits.tdw.test.prediction;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
	
	static {
        System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "info");
        System.setProperty(org.slf4j.impl.SimpleLogger.SHOW_DATE_TIME_KEY, "true");
        System.setProperty(org.slf4j.impl.SimpleLogger.DATE_TIME_FORMAT_KEY, "HH:mm:ss.SSS");
    }

	public static final Logger log = LoggerFactory.getLogger(Main.class);
    
    public static void main(String[] args) throws Exception {
		
    	Options options = new org.apache.commons.cli.Options();
		options.addOption("build", false, "Merge all SANEF *.csv file in out.csv to train a model");
		
		options.addOption("cloc", true, "cloc idno");
		options.addOption("when", true, "Requested time, like 01/01/2010 01:18");
		options.addOption("dirx", true, "1 = from Paris, 2 = to Paris");
		options.addOption("id", true, "specific prediction ID");
		
		CommandLineParser parser = new DefaultParser();
		CommandLine cmdl = parser.parse(options, args);
		
		if(args.length == 0){
			printHelpAndQuit(options);
		}
    	
		if(cmdl.hasOption("build")) {
			log.info("Building training model and quitting");
			ParseCSV.parse();
			System.out.println("Input csv produced");
			System.exit(0);
		}
    	
		if(!cmdl.hasOption("cloc") || !cmdl.hasOption("when") || !cmdl.hasOption("dirx")){
			printHelpAndQuit(options);
		}
		
		// Rimosse da out 
		// 203,"S21_A13_01_115KM4_VA",1309515840000,"182,2011,7,6,1,122400",false,01
		// 60,"S21_A13_01_115KM4_VA",1309636080000,"183,2011,7,7,2,214800",true,01
		
		
	}
    
    
    public static void printHelpAndQuit(Options options){
    	HelpFormatter help = new HelpFormatter();
		help.printHelp("tdw-prediction", options);			
		System.exit(0);
    }
}
