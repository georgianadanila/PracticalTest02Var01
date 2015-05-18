package ro.pub.cs.systems.pdsd.practicaltest02var01;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;
import android.util.Log;


public class CommunicationThread extends Thread {
	private ServerThread serverThread;
	private Socket socket;
	
	public CommunicationThread(ServerThread serverThread, Socket socket) {
		this.socket = socket;
		this.serverThread = serverThread;
		
	}
	
	@Override
	public void run() {
		if (socket != null) {
			try {
				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				PrintWriter    printWriter    = new PrintWriter(socket.getOutputStream(), true);
				if (bufferedReader != null && printWriter != null) {
					String informationType = bufferedReader.readLine();
					if (serverThread.getTemperature() == null || serverThread.getHumidity() == null) {
						HttpClient httpClient = new DefaultHttpClient();
						HttpGet httpGet = new HttpGet("http://api.openweathermap.org/data/2.5/weather?q=Bucharest,ro");
						ResponseHandler<String> responseHandler = new BasicResponseHandler();
						String pageSourceCode = httpClient.execute(httpGet, responseHandler);
						
						String temperature="", humidity="";
						if (pageSourceCode != null) {
							JSONObject content = new JSONObject(pageSourceCode);
							JSONObject infoObject = content.getJSONObject("main");
							
							temperature = infoObject.getString("temp");
							humidity = infoObject.getString("humidity");
							serverThread.setTemperature(temperature);
							serverThread.setHumidity(humidity);
	
						} else {
							Log.e("tag", "[COMMUNICATION THREAD] Error getting the information from the webservice!");
						}
					}
				
					String result = null;
					if (informationType.equals("temperature")) {
						result = serverThread.getTemperature();
					} else if (informationType.equals("humidity")) {
						result = serverThread.getHumidity();
					}
					printWriter.println(result);
					printWriter.flush();
						
				} else {
					Log.e("tag", "[COMMUNICATION THREAD] BufferedReader / PrintWriter are null!");
				}
				socket.close();
			} catch (IOException ioException) {
				ioException.printStackTrace();
			} catch (JSONException jsonException) {
				jsonException.printStackTrace();
			}
		} else {
			Log.e("tag", "[COMMUNICATION THREAD] Socket is null!");
		}
	}
}
