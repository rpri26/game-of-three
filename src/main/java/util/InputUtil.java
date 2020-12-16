package util;

import java.util.Random;
import java.util.Scanner;

import model.PlayMode;

public class InputUtil {
	private static final int MAX_INPUT = 10000;
	private static final int MAX_OPTION = 1;
	private static final int MIN_OPTION = -1;
	private static Random random = new Random();

	public synchronized static int getNextAction(final Scanner in, final PlayMode playMode) {

		if (playMode.isAuto()) {
			return random.nextInt(MAX_OPTION - MIN_OPTION + 1) + MIN_OPTION;
		} else {
			System.out.print("Enter a number from [-1, 0, 1] ");
			int selectedOption = in.nextInt();
			while (selectedOption < MIN_OPTION || selectedOption > MAX_OPTION) {
				System.out.print("Input out of range");
				System.out.print("Re-enter a number from [-1, 0, 1] ");
				selectedOption = in.nextInt();
			}
			return selectedOption;
		}
	}

	public synchronized static int getStartingInputNumber(final Scanner in, final PlayMode playMode) {
		if (playMode.isAuto()) {
			return random.nextInt(MAX_INPUT) + 1;
		} else {
			System.out.print("Enter a number between 1 - " + MAX_INPUT);
			int inputNumber = in.nextInt();
			while (inputNumber <= 0 || inputNumber > MAX_INPUT) {
				System.out.println("Input out of range");
				System.out.println("Re-enter a number between 1 - " + MAX_INPUT);
				inputNumber = in.nextInt();
			}

			return inputNumber;
		}
	}

	public synchronized static String computeNextIntegerToSend(final Scanner in, 
			int current, final PlayMode playMode) {
		int nextAction = InputUtil.getNextAction(in, playMode);
		System.out.println("Next Selected Action: " + nextAction);
		current = current + nextAction;
		return Integer.toString((current) / 3);

	}

	public synchronized static PlayMode getPlayMode(final Scanner in) {
		System.out.print("Select playing mode from [0,1]: 0- Auto 1- Manual - ");
		int mode = in.nextInt();
		while (mode != 0 && mode != 1) {
			System.out.println("Input out of range");
			System.out.println("Re-enter a number from [0,1] : ");
			mode = in.nextInt();
		}
		PlayMode playMode = PlayMode.get(String.valueOf(mode));
		System.out.print("Selected playing mode : " + playMode.toString());
		return playMode;
	}
}