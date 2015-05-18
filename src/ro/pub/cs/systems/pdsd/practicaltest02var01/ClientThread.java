package ro.pub.cs.systems.pdsd.practicaltest02var01;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import android.widget.TextView;

public class ClientThread extends Thread {
	private String address;
	private int port;
	private String informationType;
	TextView weatherInformationTextView;
	private Socket socket;
	
	public ClientThread(String address, int port, String informationType, TextView weatherInformationTextView) {
		this.address = address;
		this. port = port;
		this.informationType = informationType;
		this.weatherInformationTextView = weatherInformationTextView;
	}
	
	@Override
	public void run() {
		try {
			socket = new Socket(address, port);
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintWriter    printWriter    = new PrintWriter(socket.getOutputStream(), true);
			
			printWriter.println(informationType);
			printWriter.flush();
			
			String weatherInformation;
			while ((weatherInformation = bufferedReader.readLine()) != null) {
				final String finalizedWeatherInformation = weatherInformation;
				weatherInformationTextView.post(new Runnable() {
					@Override
					public void run() {
						weatherInformationTextView.append(finalizedWeatherInformation + "\n");
					}
				});
			}
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}

}
