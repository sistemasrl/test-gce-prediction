/*
 * Copyright (c) 2010 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.sistemaits.tdw.test.prediction;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonString;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Charsets;
import com.google.api.client.util.Key;
import com.google.api.client.util.store.DataStoreFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.prediction.Prediction;
import com.google.api.services.prediction.PredictionScopes;
import com.google.api.services.prediction.model.Input;
import com.google.api.services.prediction.model.Input.InputInput;
import com.google.common.io.Closeables;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Yaniv Inbar
 */
public class TestPredictionApi {

    /**
     * Be sure to specify the name of your application. If the application name
     * is {@code null} or blank, the application will log a warning. Suggested
     * format is "MyCompany-ProductName/1.0".
     */
    private static final String APPLICATION_NAME = "ik";
    static final String MODEL_ID = "flow_2010";
    // static final String STORAGE_DATA_LOCATION =
    // "enter_bucket/language_id.txt";

    /** Directory to store user credentials. */
    private static final java.io.File DATA_STORE_DIR = new java.io.File(System.getProperty("user.home"), ".store/prediction_sample");

    /**
     * Global instance of the {@link DataStoreFactory}. The best practice is to
     * make it a single globally shared instance across your application.
     */
    private static FileDataStoreFactory dataStoreFactory;

    /** Global instance of the HTTP transport. */
    private static HttpTransport httpTransport;

    /** Global instance of the JSON factory. */
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    /** Authorizes the installed application to access user's protected data. */
    private static Credential authorize() throws Exception {
        // load client secrets
        // GoogleClientSecrets clientSecrets =
        // GoogleClientSecrets.load(JSON_FACTORY,
        // new
        // InputStreamReader(TestPredictionApi.class.getResourceAsStream("/client_secret.json")));

        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(new FileInputStream("client_secret.json")));

        if (clientSecrets.getDetails().getClientId().startsWith("Enter") || clientSecrets.getDetails().getClientSecret().startsWith("Enter ")) {
            System.out.println("Enter Client ID and Secret from https://code.google.com/apis/console/?api=prediction "
                    + "into prediction-cmdline-sample/src/main/resources/client_secrets.json");
            System.exit(1);
        }
        // set up authorization code flow
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(httpTransport, JSON_FACTORY, clientSecrets,
                Collections.singleton(PredictionScopes.PREDICTION)).setDataStoreFactory(dataStoreFactory).build();
        // authorize
        return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
    }

    private static void run() throws Exception {

        Prediction prediction = initPrediction();
        // train(prediction);

        // 203,"S21_A13_01_115KM4_VA",1309515840000,"182,2011,7,6,1,122400",false,01
        // 60,"S21_A13_01_115KM4_VA",1309636080000,"183,
        // 2011,7,7,2,214800",true,01
        // GregorianCalendar cal1 = new GregorianCalendar();
        // cal1.setTimeInMillis(1309515840000l);
        //
        // GregorianCalendar cal2 = new GregorianCalendar();
        // cal1.setTimeInMillis(1309636080000l);

        // double val = predict(prediction, ParseCSV.buildRequest("S21_A13_01_115KM4_VA", cal1, "1"));
        // System.out.println("Expected 203, predicted"+ val);
        //
        // double val2 = predict(prediction, ParseCSV.buildRequest("S21_A13_01_115KM4_VA", cal2, "2"));
        // System.out.println("Expected 60, predicted"+ val2);

        System.out.println(predict(prediction, "S21_A13_01_115KM4_VA,SUMMER_SUNDAY,0"));
        // predict(prediction, "Â¿Es esta frase en EspaÃ±ol?");
        // predict(prediction, "Est-ce cette phrase en FranÃ§ais?");

    }

    private static Prediction initPrediction() throws Exception {

        httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        dataStoreFactory = new FileDataStoreFactory(DATA_STORE_DIR);
        // authorization
        Credential credential = authorize();
        return new Prediction.Builder(httpTransport, JSON_FACTORY, credential).setApplicationName(APPLICATION_NAME).build();
    }

    /**
     * Given a csv of request
     * 
     * @throws Exception
     */
    public static void bulkTest(String requestFileName, String model, MODEL_TYPE modelType, int limit, boolean random) throws Exception {

        File f = new File(requestFileName);
        if (!f.exists() && !f.getName().endsWith(".csv"))
            error("Input request file name for Bulk test is not a .csv file");

        FileWriter fw = new FileWriter("bulkTest.csv", false);
        Prediction prediction = initPrediction();

        System.out.println("------------- BULK TEST -------------");
        System.out.println("Model: " + model);
        System.out.println("Model Type: " + modelType);
        System.out.println("Input request file: " + f.getAbsolutePath());
        System.out.println("Running bulk test...");

        // count file row
        Path path = Paths.get(requestFileName);

        List<String> lines = Files.lines(path).collect(Collectors.toList());
        if (random) {
            System.out.println("Randomizing request...");
            Collections.shuffle(lines);
        }
        if (limit > 0) {
            int maxIndex = Math.min(limit, lines.size());
            System.out.println("Reducing request to " + maxIndex + " ...");
            lines = lines.subList(0, maxIndex);
        }

        System.out.println("Start test");

        long start=System.nanoTime();
        
        final long nRow = lines.size();
        AtomicInteger counter = new AtomicInteger(0);
        AtomicInteger errorCounter = new AtomicInteger(0);
        DecimalFormat dc = new DecimalFormat("0.00");
        lines.stream().map(line -> line.replaceAll("\"", "").split(",")).map(chunks -> {
            String request = Arrays.asList(chunks).stream().skip(1).collect(Collectors.joining(","));
            Object result = null;
            try {
                result = predict(prediction, request, model, modelType);
            }
            catch (Exception e) {
                e.printStackTrace();
                errorCounter.incrementAndGet();
                return null;
            }

            System.out.print("\r");
            int currentCounter = counter.incrementAndGet();
            System.out.print(currentCounter + "/" + nRow + " - " + dc.format((currentCounter / (double) nRow) * 100) + "%");

            try {
                Thread.sleep(1000);
            }
            catch (Exception e) {}
            
            return result.toString() + "," + chunks[0] + "," + request;

        }).filter(record -> record != null).forEach(record -> {
            Utils.writeToFile(fw, record);
        });

        fw.close();

        long end=System.nanoTime();

        System.out.println();
        System.out.println("Error number: " + errorCounter.get());
        System.out.println("Bulk test finished in " + Duration.ofNanos(end-start).toString());

    }

    private static void error(String errorMessage) {

        System.err.println();
        System.err.println(errorMessage);
        System.exit(1);
    }

    private static Object predict(Prediction prediction, String text) throws IOException {

        return predict(prediction, MODEL_ID, text, MODEL_TYPE.NUMERIC);
    }

    private static Object predict(Prediction prediction, String text, String modelId, MODEL_TYPE modelType) throws IOException {
        // System.out.println("Text: " + text);

        Input input = new Input();
        InputInput inputInput = new InputInput();
        inputInput.setCsvInstance(Arrays.asList(text.split(",")));
        input.setInput(inputInput);

        // debugLog("com.google.api.client.http");

        // BUG
        // https://groups.google.com/forum/#!topic/prediction-api-discuss/1gra6obUNig
        // Output output = prediction.trainedmodels().predict("sistema-it-01",
        // MODEL_ID, input).execute();
        // System.out.println("Predicted flow: " + output.getOutputValue());

        Object predictedValue = null;
        InputStream is = prediction.trainedmodels().predict("sistema-it-01", modelId, input).executeAsInputStream();
        try {
            JsonFactory jsonFactory = new JacksonFactory();
            switch (modelType) {
            case NUMERIC:
                OutputNumber outNumeric = jsonFactory.fromInputStream(is, Charsets.UTF_8, OutputNumber.class);
                predictedValue = outNumeric.outputValue;                
                break;

            default:
                OutputString outString = jsonFactory.fromInputStream(is, Charsets.UTF_8, OutputString.class);
                predictedValue = outString.outputLabel;                
                break;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        finally {
            Closeables.closeQuietly(is);
        }
        return predictedValue;
    }

    public static class OutputNumber {

        @Key
        @JsonString
        public java.lang.Double outputValue;
    }
    

    public static class OutputString {

        @Key
        @JsonString
        public String outputLabel;
    }

    private static void debugLog(String logger) {

        Logger googleHttpLogger = Logger.getLogger(logger);
        googleHttpLogger.setLevel(Level.ALL);
        ConsoleHandler logHandler = new ConsoleHandler();
        logHandler.setLevel(Level.ALL);
        googleHttpLogger.addHandler(logHandler);
    }

    public static void main(String[] args) {

        try {
            run();
            // bulkTest("flow_2011.csv");
            // success!
            return;
        }
        catch (IOException e) {
            System.err.println(e.getMessage());
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
        System.exit(1);
    }
}