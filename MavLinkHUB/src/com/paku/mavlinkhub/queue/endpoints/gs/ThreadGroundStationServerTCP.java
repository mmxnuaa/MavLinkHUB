package com.paku.mavlinkhub.queue.endpoints.gs;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import com.paku.mavlinkhub.enums.SOCKET_STATE;
import com.paku.mavlinkhub.utils.ThreadSocketReader;

import android.os.Handler;
import android.util.Log;

public class ThreadGroundStationServerTCP extends Thread {

	private static final String TAG = "ThreadGroundStationServerTCP";

	Socket socket;
	ServerSocket serverSocket;
	ThreadSocketReader socketWorkerThreadTCP;
	Handler handlerServerReadMsg;
	public boolean running = true;

	public ThreadGroundStationServerTCP(Handler handler, int port) {
		try {

			serverSocket = new ServerSocket(port);
		}
		catch (IOException e) {
			e.printStackTrace();
		}

		handlerServerReadMsg = handler;

	}

	public void run() {
		String clientIP = new String();
		while (running) {
			Log.d(TAG, "Accept wait");
			try {
				socket = serverSocket.accept();

				socketWorkerThreadTCP = new ThreadSocketReader(socket, handlerServerReadMsg);
				socketWorkerThreadTCP.start();

				clientIP = socket.getInetAddress().toString() + ":" + socket.getPort();
				clientIP.replace("/", " ");

				handlerServerReadMsg.obtainMessage(SOCKET_STATE.MSG_SOCKET_TCP_SERVER_CLIENT_CONNECTED.ordinal(), clientIP.length(), -1, clientIP.getBytes()).sendToTarget();

				Log.d(TAG, "New Connection: TCP Socket Started");
			}
			catch (SocketException e) {
				running = false;
			}
			catch (IOException e) {
				// ?? should never happen if permissions set
				e.printStackTrace();
				running = false;
			}
		}
	}

	public void stopMe() {
		if (socketWorkerThreadTCP != null) {
			socketWorkerThreadTCP.stopMe();
		}
		running = false; // just in case
		try {
			serverSocket.close();
		}
		catch (IOException e) {
			// not possible on close ??
			e.printStackTrace();
		}

	}

	public void writeBytes(byte[] bytes) throws IOException {
		socketWorkerThreadTCP.writeBytes(bytes);

	}

}