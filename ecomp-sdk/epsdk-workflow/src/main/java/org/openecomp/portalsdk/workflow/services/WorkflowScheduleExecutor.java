/*-
 * ================================================================================
 * eCOMP Portal SDK
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ================================================================================
 */
package org.openecomp.portalsdk.workflow.services;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;

public class WorkflowScheduleExecutor {
	private String serverURL;
	private String workflowKey;
	private String myUrl;
	private String payload;

	//constructor
	public WorkflowScheduleExecutor(String serverURL,String workflowKey){
		this.serverURL = serverURL;
		this.workflowKey = workflowKey;
		this.myUrl = this.serverURL + "/engine-rest/process-definition/key/" + this.workflowKey + "/submit-form";;
        this.payload="{\"variables\":{}}";
	}
	
	public static void main(String [] args)  throws Exception {		

	}
	
	public void execute() {
		POST_fromURL(myUrl,payload);
	}
	
	public static String get_fromURL(String myURL) {
		System.out.println("Requeted URL:" + myURL);
		StringBuilder sb = new StringBuilder();
		URLConnection urlConn = null;
		InputStreamReader in = null;
		try {
			URL url = new URL(myURL);
			urlConn = url.openConnection();
			if (urlConn != null)
				urlConn.setReadTimeout(60 * 1000);
			if (urlConn != null && urlConn.getInputStream() != null) {
				in = new InputStreamReader(urlConn.getInputStream(),
						Charset.defaultCharset());
				BufferedReader bufferedReader = new BufferedReader(in);
				if (bufferedReader != null) {
					int cp;
					while ((cp = bufferedReader.read()) != -1) {
						sb.append((char) cp);
					}
					bufferedReader.close();
				}
			}
		in.close();
		} catch (Exception e) {
			throw new RuntimeException("Exception while calling URL:"+ myURL, e);
		} 
		return sb.toString();
	}
	
	
	public static String POST_fromURL(String myURL, String payload) {
		   	String line;
		    StringBuffer jsonString = new StringBuffer();
		    try {
		        URL url = new URL(myURL);

		        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		        connection.setDoInput(true);
		        connection.setDoOutput(true);
		        connection.setRequestMethod("POST");
		        connection.setRequestProperty("Accept", "application/json");
		        connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
		        OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
		        writer.write(payload);
		        writer.close();
		        BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		        while ((line = br.readLine()) != null) {
		                jsonString.append(line);
		        }
		        br.close();
		        connection.disconnect();
		    } catch (Exception e) {
		            throw new RuntimeException(e.getMessage());
		    }
		    return jsonString.toString();
		}					
}