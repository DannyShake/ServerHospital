package sample.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server implements Runnable {
	private static volatile Server instance;

	private ServerSocket serverSocket;

	private boolean isStopped = false;

	public static Server getInstance() {
		if (instance == null) {
			synchronized (Server.class) {
				if (instance == null) {
					instance = new Server();
				}
			}
		}
		return instance;
	}

	@Override
	public void run() {
		openServerSocket();
		while (!isStopped()) {
			Socket clientSocket;
			try {
				clientSocket = this.serverSocket.accept();
				System.out.println("Receive new client");
				new Thread(new Worker(clientSocket)).start();
				System.out.println("Worker in progress");
			} catch (IOException e) {
			}
		}
	}

	private void openServerSocket() {
		System.out.println("Opening server socket...");
		this.isStopped = false;
		try {
			this.serverSocket = new ServerSocket(2525);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public synchronized void stop() {
		System.out.println("Closing server socket...");
		this.isStopped = true;
		try {
			this.serverSocket.close();
		} catch (IOException e) {
			throw new RuntimeException("Error closing server", e);
		}
	}

	private synchronized boolean isStopped() {
		return this.isStopped;
	}

}

