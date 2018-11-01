package model.linefollower;

import lejos.hardware.Button;
import lejos.utility.Delay;
import utility.Lcd;
import utility.Stopwatch;

/**
 * 
 * @author Tom
 * The view class extends the general view class.
 * This class is responsible for user interaction
 */
public class View extends model.View {

	private final static String TITLE = "Kirov Tracker";
	private final static int DISPLAY_SUCCESS = 250;
	
	/**
	 * clear line 1 and print
	 * name of the application
	 */
	public static void printTitle() {
    	Lcd.clear(1);
		Lcd.print(1, TITLE);
	}

	/**
	 * clear line 2 and print
	 * boot message
	 */
	public static void printBooting() {
    	Lcd.clear(2);
		Lcd.print(2, "Kirov rising..");
	}
	
	/**
	 * clear line 2 and print
	 * message to indicate booting is finished
	 */
	public static void printBootingReady() {
    	Lcd.clear(2);
    	Lcd.print(2, "Kirov ready!");
	}
	

	/**
	 * Wrapper of method Button.waitForAnyPress:
	 * Waits for some button to be pressed. 
	 * If a button is already pressed, it must be released and pressed again.
	 */
	public static void waitAny() {
    	Button.waitForAnyPress();
	}

	/**
	 * Starts with clearing screen.
	 * Obtains the stopwatch object used during the application.
	 * The stoptime is extracted and converted to seconds and hundreds.
	 * This is printed together with finished message.
	 * @param stopwatch
	 */
	public static void printFinal(Stopwatch stopwatch) {
		Lcd.clear();
        Lcd.print(6, "Kirov finished!");
        Lcd.print(7, "Lap time: %.2f", (stopwatch.getElapsedTime()/1000.0)-1);
	}

	/**
	 * Print shutdown message and clears the screen afterwards.
	 */
	public static void printShutdown() {
		Lcd.clear();
		Lcd.print(6, "Goodbye!");
		Delay.msDelay(250);
		Lcd.clear();
	}

	/**
	 * Prints message to prompt user to start calibration.
	 */
	public static void printStartCalibration() {
		Lcd.clear(3);
		Lcd.print(3, "Press ENTER to");
		Lcd.print(4, "start calibrate");
	}
	
	/**
	 * Tell user what surface will be calibrated by printing String param.
	 * @param String
	 */
	public static void prepareUserForCalibration(String toCalibrate) {
		alertUp();
        Lcd.clear();
        Lcd.print(4, "%s:", toCalibrate);
        Lcd.print(5, "Press ENTER");
        Lcd.print(6, "to calibrate");
        waitAny();
        Lcd.print(8, "Calibrating..");
	}

	/**
	 * Print success to confirm that calibration is ready.
	 */
	public static void CalibrationSuccess() {
        Lcd.print(7, "SUCCES!");
        Delay.msDelay(DISPLAY_SUCCESS);
	}
	
	/**
	 * Prints the results of calibrating finish and background.
	 * @param finishL containing r g b values of left sensor regarding finish
	 * @param finishR containing r g b values of right sensor regarding finish
	 * @param backgroundL containing r g b values of left sensor regarding background
	 * @param backgroundR containing r g b values of right sensor regarding background
	 */
	public static void printCalibrationResults(int[] finishL, int[] finishR, int[] backgroundL, int[] backgroundR) {
        Lcd.clear();
        Lcd.print(1, "Calibration results:");
        Lcd.print(2, "FL: %d %d %d", finishL[0], finishL[1], finishL[2]);
        Lcd.print(3, "FR: %d %d %d",  finishR[0], finishR[1], finishR[2]);
        Lcd.print(6, "BL: %d %d %d", backgroundL[0], backgroundL[1], backgroundL[2]);
        Lcd.print(7, "BR: %d %d %d", backgroundR[0], backgroundR[1], backgroundR[2]);
	}

	/**
	 * 
	 * @param stopwatch
	 */
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
