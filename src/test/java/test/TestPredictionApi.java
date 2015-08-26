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

package test;

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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

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
	static final String MODEL_ID = "speed_noflow";
	// static final String STORAGE_DATA_LOCATION =
	// "enter_bucket/language_id.txt";

	/** Directory to store user credentials. */
	private static final java.io.File DATA_STORE_DIR = new java.io.File(System.getProperty("user.home"),
			".store/prediction_sample");

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

		GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY,
				new InputStreamReader(new FileInputStream("client_secret.json")));

		if (clientSecrets.getDetails().getClientId().startsWith("Enter")
				|| clientSecrets.getDetails().getClientSecret().startsWith("Enter ")) {
			System.out.println("Enter Client ID and Secret from https://code.google.com/apis/console/?api=prediction "
					+ "into prediction-cmdline-sample/src/main/resources/client_secrets.json");
			System.exit(1);
		}
		// set up authorization code flow
		GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(httpTransport, JSON_FACTORY,
				clientSecrets, Collections.singleton(PredictionScopes.PREDICTION)).setDataStoreFactory(dataStoreFactory)
						.build();
		// authorize
		return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
	}

	private static void run() throws Exception {
		httpTransport = GoogleNetHttpTransport.newTrustedTransport();
		dataStoreFactory = new FileDataStoreFactory(DATA_STORE_DIR);
		// authorization
		Credential credential = authorize();
		Prediction prediction = new Prediction.Builder(httpTransport, JSON_FACTORY, credential)
				.setApplicationName(APPLICATION_NAME).build();
				// train(prediction);

		// 203,"S21_A13_01_115KM4_VA",1309515840000,"182,2011,7,6,1,122400",false,01
		// 60,"S21_A13_01_115KM4_VA",1309636080000,"183,
		// 2011,7,7,2,214800",true,01
		GregorianCalendar cal1 = new GregorianCalendar();
		cal1.setTimeInMillis(1309515840000l);

		GregorianCalendar cal2 = new GregorianCalendar();
		cal1.setTimeInMillis(1309636080000l);

		
//		double val = predict(prediction, ParseCSV.buildRequest("S21_A13_01_115KM4_VA", cal1, "1"));
//		System.out.println("Expected 203, predicted"+ val);
//		
//		double val2 = predict(prediction, ParseCSV.buildRequest("S21_A13_01_115KM4_VA", cal2, "2"));
//		System.out.println("Expected 60, predicted"+ val2);
		
		 System.out.println(predict(prediction, "SUMMER SUNDAY,S21_A13_02_115KM4_VA,0"));
		// predict(prediction, "Â¿Es esta frase en EspaÃ±ol?");
		// predict(prediction, "Est-ce cette phrase en FranÃ§ais?");
	}

	// private static void train(Prediction prediction) throws IOException {
	// Training training = new Training();
	// training.setId(MODEL_ID);
	// training.setStorageDataLocation(STORAGE_DATA_LOCATION);
	// prediction.trainedmodels().insert(training).execute();
	// System.out.println("Training started.");
	// System.out.print("Waiting for training to complete");
	// System.out.flush();
	//
	// int triesCounter = 0;
	// while (triesCounter < 100) {
	// // NOTE: if model not found, it will throw an HttpResponseException with
	// a 404 error
	// try {
	// HttpResponse response =
	// prediction.trainedmodels().get(MODEL_ID).executeUnparsed();
	// if (response.getStatusCode() == 200) {
	// training = response.parseAs(Training.class);
	// String trainingStatus = training.getTrainingStatus();
	// if (trainingStatus.equals("DONE")) {
	// System.out.println();
	// System.out.println("Training completed.");
	// System.out.println(training.getModelInfo());
	// return;
	// }
	// }
	// response.ignore();
	// } catch (HttpResponseException e) {
	// }
	//
	// try {
	// // 5 seconds times the tries counter
	// Thread.sleep(5000 * (triesCounter + 1));
	// } catch (InterruptedException e) {
	// break;
	// }
	// System.out.print(".");
	// System.out.flush();
	// triesCounter++;
	// }
	// error("ERROR: training not completed.");
	// }

	private static void error(String errorMessage) {
		System.err.println();
		System.err.println(errorMessage);
		System.exit(1);
	}

	private static double predict(Prediction prediction, String text) throws IOException {
		System.out.println("Text: " + text);

		Input input = new Input();
		InputInput inputInput = new InputInput();
		inputInput.setCsvInstance(Collections.<Object> singletonList(text));
		input.setInput(inputInput);

//		debugLog("com.google.api.client.http");

		// BUG
		// https://groups.google.com/forum/#!topic/prediction-api-discuss/1gra6obUNig
		// Output output = prediction.trainedmodels().predict("sistema-it-01",
		// MODEL_ID, input).execute();
		// System.out.println("Predicted flow: " + output.getOutputValue());

		double predictedFlow = 0;
		InputStream is = prediction.trainedmodels().predict("sistema-it-01", MODEL_ID, input).executeAsInputStream();
		try {
			JsonFactory jsonFactory = new JacksonFactory();
			Output2 out2 = jsonFactory.fromInputStream(is, Charsets.UTF_8, Output2.class);
			predictedFlow = out2.outputValue;
		} finally {
			Closeables.closeQuietly(is);
		}
		return predictedFlow;
	}

	public static class Output2 {
		@Key
		@JsonString
		public java.lang.Double outputValue;
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
			// success!
			return;
		} catch (IOException e) {
			System.err.println(e.getMessage());
		} catch (Throwable t) {
			t.printStackTrace();
		}
		System.exit(1);
	}
}