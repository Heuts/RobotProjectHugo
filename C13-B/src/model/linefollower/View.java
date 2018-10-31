package model.linefollower;

import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.utility.Delay;
import utility.Lcd;
import utility.Stopwatch;

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

	public static void printFinal(Stopwatch stopwatch) {
		Lcd.clear();
        Lcd.print(6, "Kirov finished!");
        Lcd.print(7, "Lap time: %.2f", stopwatch.getElapsedTime()/1000.0);
	}

	public static void printShutdown() {
		Lcd.clear();
		Lcd.print(6, "Goodbye!");
		Delay.msDelay(250);
		Lcd.clear();
	}

	public static void printStartCalibration() {
		Lcd.clear(3);
		Lcd.print(3, "Press ENTER to");
		Lcd.print(4, "start calibrate");
	}
	
	public static void prepareUserForCalibration(String toCalibrate) {
		alert();
        Lcd.clear();
        Lcd.print(4, "%s:", toCalibrate);
        Lcd.print(5, "Press ENTER");
        Lcd.print(6, "to calibrate");
        waitAny();
        Lcd.print(8, "Calibrating..");
	}

	public static void CalibrationSuccess() {
        Lcd.print(7, "SUCCES!");
        Delay.msDelay(DISPLAY_SUCCESS);
	}
	
	public static void printCalibrationResults(int[] finishL, int[] finishR, int[] backgroundL, int[] backgroundR) {
        Lcd.clear();
        Lcd.print(1, "Calibration results:");
        Lcd.print(2, "FL: %d %d %d", finishL[0], finishL[1], finishL[2]);
        Lcd.print(3, "FR: %d %d %d",  finishR[0], finishR[1], finishR[2]);
        Lcd.print(6, "BL: %d %d %d", backgroundL[0], backgroundL[1], backgroundL[2]);
        Lcd.print(7, "BR: %d %d %d", backgroundR[0], backgroundR[1], backgroundR[2]);
	}

	public static void printElapsedTime(Stopwatch stopwatch) {
    	Lcd.clear(8);
    	Lcd.print(8, "%d", stopwatch.getElapsedTimeSecs());		
	}

	public static void printRgbPosition(char sensor, int r, int g, int b) {
		int line;
		if(sensor == 'L')
			line = 4;
		else
			line = 6;
		
		Lcd.print(line, "%c Sensor: %d %d %d", sensor, r, g, b);
	}
	
}
