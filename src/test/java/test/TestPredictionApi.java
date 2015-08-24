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
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.DataStoreFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.prediction.Prediction;
import com.google.api.services.prediction.PredictionScopes;
import com.google.api.services.prediction.model.Input;
import com.google.api.services.prediction.model.Input.InputInput;
import com.google.api.services.prediction.model.Output;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;

/**
 * @author Yaniv Inbar
 */
public class TestPredictionApi {

	/**
	 * Be sure to specify the name of your application. If the application name
	 * is {@code null} or blank, the application will log a warning. Suggested
	 * format is "MyCompany-ProductName/1.0".
	 */
	private static final String APPLICATION_NAME = "";
	static final String MODEL_ID = "languagetest";
//	static final String STORAGE_DATA_LOCATION = "enter_bucket/language_id.txt";

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
		GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY,
				new InputStreamReader(TestPredictionApi.class.getResourceAsStream("/client_secret.json")));
		
//		GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY,new InputStreamReader(new FileInputStream("client_secret.json")));
		
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
		predict(prediction, "Is this sentence in English?");
		predict(prediction, "Â¿Es esta frase en EspaÃ±ol?");
		predict(prediction, "Est-ce cette phrase en FranÃ§ais?");
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

	private static void predict(Prediction prediction, String text) throws IOException {
		Input input = new Input();
		InputInput inputInput = new InputInput();
		inputInput.setCsvInstance(Collections.<Object> singletonList(text));
		input.setInput(inputInput);
		Output output = prediction.trainedmodels().predict("sistema-it-01", MODEL_ID, input).execute();
		System.out.println("Text: " + text);
		System.out.println("Predicted language: " + output.getOutputLabel());
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