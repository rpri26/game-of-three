package server;

import java.net.InetSocketAddress;
import java.util.Scanner;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import model.PlayMode;
import util.Constants;
import util.InputUtil;

public class ServerApplication extends WebSocketServer {

	private static final int PORT = 8080;
	private static final String HOST = "localhost";
	private static final String PLAYER_NAME = "Player-A";
	private static PlayMode playMode = PlayMode.AUTO;

	private static Scanner in;

	public ServerApplication(InetSocketAddress address) {
		super(address);
		in = new Scanner(System.in);
	}

	@Override
	public void onOpen(WebSocket conn, ClientHandshake handshake) {
		System.out.println(PLAYER_NAME + " connected to another palyer\n");
		playMode = InputUtil.getPlayMode(in);

		int inputNumber = InputUtil.getStartingInputNumber(in, playMode);
		System.out.println("\nSelected number: " + inputNumber);

		if (inputNumber == Constants.WINNER_NUMBER) {
			handleWinScenario(conn);
		}

		System.out.println("Sending number: " + inputNumber);
		conn.send(Integer.toString(inputNumber));
	}

	@Override
	public void onClose(WebSocket conn, int code, String reason, boolean remote) {
		System.exit(Constants.SUCCESS_STATUS);
	}

	@Override
	public void onMessage(WebSocket conn, String message) {
		if (message.equals("" + Constants.WINNER_NUMBER)) {
			System.out.println(PLAYER_NAME + " lost, Game over.");
			cleanupAndShutdown(conn, Constants.SUCCESS_STATUS);
		}

		int receivedNumber = Integer.parseInt(message);
		System.out.println("\nReceived number: " + receivedNumber);

		if (receivedNumber == Constants.WINNER_NUMBER) {
			handleWinScenario(conn);
		} else if (receivedNumber <= 0) {
			System.out.println("Error, something went wrong.");
			cleanupAndShutdown(conn, Constants.FAILURE_STATUS);
		}

		String nextNumber = InputUtil.computeNextIntegerToSend(in, receivedNumber, playMode);
		System.out.println("Sending number: " + nextNumber);

		conn.send(nextNumber);
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {

		}
	}

	private void handleWinScenario(final WebSocket conn) {
		System.out.println("Winner!!");
		conn.send(Constants.DONE_KEYWORD);
		cleanupAndShutdown(conn, Constants.SUCCESS_STATUS);
	}

	@Override
	public void onError(WebSocket conn, Exception ex) {
		System.err.println("Error with connection " + conn.getRemoteSocketAddress() + ":" + ex);
		cleanupAndShutdown(conn, Constants.FAILURE_STATUS);
	}

	private void cleanupAndShutdown(WebSocket conn, int exitStatus) {
		conn.close();
		in.close();
		System.exit(exitStatus);
	}

	public static void main(String[] args) {
		WebSocketServer server = new ServerApplication(new InetSocketAddress(HOST, PORT));
		System.out.println("Starting Server" + "(" + HOST + ")" + "at port: " + PORT);
		System.out.println(PLAYER_NAME + " waiting......");
		server.run();

	}

}
