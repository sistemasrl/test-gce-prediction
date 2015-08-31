package com.sistemaits.tdw.test.prediction;

import java.util.Arrays;

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

        // init options
        Options options = new org.apache.commons.cli.Options();
        options.addOption("h", "help", false, "Print help");
        options.addOption("build", false, "Merge all SANEF *.csv file in out.csv to train a model");
        options.addOption("bulkTest", false, "Run all requests (compliant with the specified model)"
                + " contained in a csv file and returns the results in a csv file called bulkTestOutput.csv"
                + " where the first column is the result and the second is the expected value."
                + " The first column of the input csv file must represent the expected value" + " (if is not known set it as an empty value)");

        options.addOption("bulkTestInput", true, "Bulk test input file path");
        options.addOption("bulkTestLimit", true, "Max number of requests to run in the bulk test. OPTIONAL");
        options.addOption("bulkTestRandom", false, "Run request randomly in bulk test. OPTIONAL, DEFAULT = False");
        options.addOption("model", true, "Model ID");
        options.addOption("modelType", true, "Model Type for bulk test. Available values: " + Arrays.toString(MODEL_TYPE.values()));

        CommandLineParser parser = new DefaultParser();
        CommandLine cmdl = parser.parse(options, args);

        if (args.length == 0 || cmdl.hasOption("h") || cmdl.hasOption("help")) {
            printHelpAndQuit(options);
        }
        
        if (cmdl.hasOption("build")) {
            log.info("Building training model and quitting");
            ParseCSV.parse();
            log.info("Output csv produced");
            System.exit(0);
        }

        if (cmdl.hasOption("bulkTest")) {
            String inputFilePath = cmdl.getOptionValue("bulkTestInput");
            String model = cmdl.getOptionValue("model");
            String modelTypeString = cmdl.getOptionValue("modelType", MODEL_TYPE.NUMERIC.toString());
            MODEL_TYPE modelType = MODEL_TYPE.parse(modelTypeString);
            int limit = -1;
            if (cmdl.hasOption("bulkTestLimit"))
                limit = Integer.parseInt(cmdl.getOptionValue("bulkTestLimit"));
            boolean random = false;
            if (cmdl.hasOption("bulkTestRandom"))
                random = true;
            TestPredictionApi.bulkTest(inputFilePath, model, modelType, limit, random);
        }

    }

    public static void printHelpAndQuit(Options options) {

        HelpFormatter help = new HelpFormatter();
        help.printHelp("tdw-prediction", options);
        System.exit(0);
    }
}
