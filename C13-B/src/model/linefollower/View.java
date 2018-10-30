package model.linefollower;

import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.utility.Delay;
import utility.Lcd;

public class View {

	private final static String TITLE = "Kirov Tracker";
	private final static int DISPLAY_SUCCESS = 250;
	
	public static void printTitle() {
    	Lcd.clear(1);
		Lcd.print(1, TITLE);
	}

	public static void printBooting() {
    	Lcd.clear(2);
		Lcd.print(2, "Kirov rising..");
	}

	public static void printBootingReady() {
    	Lcd.clear(2);
    	Lcd.print(2, "Kirov ready!");
	}
	
	/*
	 * Hulpfunctie die groene led aanzet en piepend geluid maakt
	 * Indicatie dat userinput wordt verwacht
	 */
	public static void alert() {
		Button.LEDPattern(4);    // flash green led and 
        Sound.beepSequenceUp();  // make sound when ready.
	}

	public static void waitAny() {
    	Button.waitForAnyPress();
	}

	public static void printFinal() {
		Lcd.clear();
        Lcd.print(6, "Kirov finished!");
	}

	public static void printShutdown() {
		
	}

	public static void printStartCalibration() {
		Lcd.clear(3);
		Lcd.print(3, "Press ENTER to start calibrate");
	}
	
	public static void prepareUserForCalibration(String toCalibrate) {
		alert();
        Lcd.clear();
        Lcd.print(4, "%s:", toCalibrate);
        Lcd.print(5, "Press ENTER to calibrate");
        Lcd.print(6, "to calibrate");
        waitAny();
        Lcd.print(8, "Calibrating..");
	}

	public static void CalibrationSuccess() {
        Lcd.print(7, "SUCCES!");
        Delay.msDelay(DISPLAY_SUCCESS);
	}
	
}
