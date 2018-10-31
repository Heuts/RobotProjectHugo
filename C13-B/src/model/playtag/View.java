package model.playtag;

import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.utility.Delay;
import utility.Lcd;

public class View {
	private static final String TITLE = "Play Tag";

	public static void userInterface() {
		alertUp();
		Lcd.print(2, TITLE);
		Lcd.print(4, "Press any button");
		Lcd.print(5, "to start");
		Button.waitForAnyPress();
	}

	public static void alertUp() {
		Button.LEDPattern(4);
		Sound.beepSequenceUp();
	}

	public static void alertDown() {
		Sound.beepSequence();
	}

	public static void shutdown() {
		Delay.msDelay(200);
		Lcd.clear();
		Lcd.print(2, "End of Program");
		Delay.msDelay(200);
		alertDown();
		Button.waitForAnyPress(5000);
		Lcd.clear();

	}
}
