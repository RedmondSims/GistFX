package com.redmondsims.gistfx.networking;

import com.redmondsims.gistfx.gist.WindowManager;
import com.redmondsims.gistfx.preferences.LiveSettings;
import com.simtechdata.waifupnp.UPnP;
import javafx.application.Platform;
import javafx.scene.control.Label;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

public class Listener {

	private static final String            payload      = "payload";
	private static final String            received     = "received";
	private static Timer upnpTimer;
	private static boolean acceptGists = false;

	public static void acceptGists() {
		acceptGists = true;
	}

	public static void rejectGists() {
		acceptGists = false;
	}

	public static TimerTask mapUPnP() {
		return new TimerTask() {
			@Override public void run() {
				if (!UPnP.isMappedTCP(LiveSettings.getTcpPortNumber())) {
					UPnP.openPortTCP(LiveSettings.getTcpPortNumber());
				}
			}
		};
	}

	public static void startServer(Label lblInfo) {
		new Thread(() -> {
			upnpTimer = new Timer();
			upnpTimer.scheduleAtFixedRate(mapUPnP(), 0, 60000);
			InetAddress       localIPAddress;
			ServerSocket      serverSocket     = null;
			Socket            socket           = null;
			DataInputStream   dataInputStream  = null;
			DataOutputStream  dataOutputStream = null;
			ObjectInputStream ois              = null;
			String            msg;
			try {
				localIPAddress = InetAddress.getByName(UPnP.getLocalIP());
				serverSocket   = new ServerSocket(LiveSettings.getTcpPortNumber(), 50, localIPAddress);
				serverSocket.setSoTimeout(0);
				Platform.runLater(() -> {
					lblInfo.setText("Waiting for incoming Gist Object.");
				});
				socket = serverSocket.accept();
				if (acceptGists) {
					dataInputStream  = new DataInputStream(socket.getInputStream());
					dataOutputStream = new DataOutputStream(socket.getOutputStream());
					ois              = new ObjectInputStream(dataInputStream);
					msg              = dataInputStream.readUTF();
					if (msg.equals(payload)) {
						Payload payload = (Payload) ois.readObject();
						dataOutputStream.writeUTF(received);
						WindowManager.receiveData(payload);
					}
					dataOutputStream.close();
					dataInputStream.close();
					ois.close();
					socket.close();
					serverSocket.close();
					Platform.runLater(() -> {
						lblInfo.setText("Success");
					});
				}
				else {
					socket.close();
					serverSocket.close();
				}
			}
			catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
				Platform.runLater(() -> {
					lblInfo.setText("A connection was attempted, but failed.\n\nTry again.");
				});
				try {
					serverSocket.close();
					socket.close();
					dataInputStream.close();
					dataOutputStream.close();
					ois.close();
				}
				catch (IOException ex) {
					Platform.runLater(() -> {
						lblInfo.setText("A connection was attempted, but failed.\n\nTry again.");
					});
					ex.printStackTrace();
				}
			}
		}).start();
	}
}
