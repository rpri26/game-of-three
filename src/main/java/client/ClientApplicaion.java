package client;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Scanner;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_10;
import org.java_websocket.handshake.ServerHandshake;

import model.PlayMode;
import util.Constants;
import util.InputUtil;

public class ClientApplicaion extends WebSocketClient {

	private static final int WAIT_TIME_IN_MILLISECONDS = 4000;
	private static final String SERVER_URI = "ws://localhost:8080";
	private static final String PLAYER_NAME = "Player-B";
	private static Scanner in;
	private static PlayMode playMode = PlayMode.AUTO;

	private static final Draft draft = new Draft_10();

	public ClientApplicaion(URI serverUri, Draft draft) {
		super(serverUri, draft, null, 999);
		in = new Scanner(System.in);
	}

	@Override
	public void onOpen(ServerHandshake handshakedata) {
		System.out.println(PLAYER_NAME + " connected to another player\n");
		playMode = InputUtil.getPlayMode(in);
	}

	@Override
	public void onClose(int code, String reason, boolean remote) {
		in.close();
	}

	@Override
	public void onMessage(String message) {
		if (message.equals(Constants.DONE_KEYWORD)) {
			System.out.println(PLAYER_NAME + " lost, Game over.");
			cleanupAndShutdown(Constants.SUCCESS_STATUS);
		}

		int receivedNumber = Integer.parseInt(message);

		System.out.println("\nReceived number: " + receivedNumber);

		if (receivedNumber == Constants.WINNER_NUMBER) {
			System.out.println("Winner!!");
			this.send(Constants.DONE_KEYWORD);
			cleanupAndShutdown(Constants.SUCCESS_STATUS);
		} else if (receivedNumber <= 0) {
			System.out.println("Error, something went wrong.");
			cleanupAndShutdown(Constants.FAILURE_STATUS);
		}

		String nextNumber = InputUtil.computeNextIntegerToSend(in, receivedNumber, playMode);
		System.out.println("Sending number: " + nextNumber);
		this.send(nextNumber);

		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {

		}
	}

	@Override
	public void onError(Exception ex) {
		if (!(ex instanceof java.net.ConnectException)) {
			this.close();
			in.close();
			System.exit(Constants.FAILURE_STATUS);
		}
	}

	private void cleanupAndShutdown(int exitStatus) {
		this.close();
		System.exit(exitStatus);
	}

	public static void main(String[] args) throws URISyntaxException, InterruptedException {
		System.out.println("Staring Client Application....");
		System.out.println(PLAYER_NAME + " waiting......");
		while (true) {
			WebSocketClient client = new ClientApplicaion(new URI(SERVER_URI), draft);
			synchronized (client) {
				if (client.connectBlocking()) {
					break;
				} else {
					System.out
							.println("Could not find an active player, waiting " + WAIT_TIME_IN_MILLISECONDS + " ms.");
					client.wait(WAIT_TIME_IN_MILLISECONDS);
				}
			}
		}
	}
}
